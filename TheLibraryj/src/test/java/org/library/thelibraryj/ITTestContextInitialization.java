package org.library.thelibraryj;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import lombok.Getter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public abstract class ITTestContextInitialization {

    @RegisterExtension
    protected static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("username", "password"))
            .withPerMethodLifecycle(false);

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected DataSource dataSource;

    @Getter
    private static Path fileStorage;

    private static byte[] content = null;

    private static final List<Path> handlerDirs = new ArrayList<>();

    private static final String HANDLER_NAME = "someHandlerName";

    @DynamicPropertySource
    static void addFileStoringEnv(DynamicPropertyRegistry registry) throws IOException {
        registry.add("library.image.base", fileStorage::toString);
        content = new byte[100];
        Arrays.fill(content, (byte) 1);
        String[] properties_to_fill = {"library.user.image_source", "library.book.image_source"};
        for (String property : properties_to_fill) {
            String currentHandlerName = HANDLER_NAME + '_' + (handlerDirs.size() + 1);
            handlerDirs.add(fileStorage.resolve(currentHandlerName));
            registry.add(property, () -> currentHandlerName);
        }
        seedFileStorage();
    }

    @BeforeAll
    static void setupFileStorage() throws IOException {
        if (fileStorage == null) {
            Path systemTempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            fileStorage = systemTempDir.resolve("library_IT_file_storage");
        }
        Files.createDirectory(fileStorage);
        Assertions.assertTrue(Files.exists(fileStorage));
        seedFileStorage();
    }

    @AfterAll
    static void cleanupFileStorage() {
        if (fileStorage != null) {
            try (Stream<Path> files = Files.walk(fileStorage)) {
                //noinspection RedundantStreamOptionalCall
                Assertions.assertTrue(files.sorted(Comparator.reverseOrder()).allMatch(p -> p.toFile().delete()));
            } catch (Exception e) {
                throw new RuntimeException("Error cleaning up file storage", e);
            }
            Assertions.assertFalse(Files.exists(fileStorage));
        }
    }

    private static void seedFileStorage() throws IOException {
        for (Path handlerDir : handlerDirs) {
            if (!Files.exists(handlerDir)) {
                Files.createDirectories(handlerDir);
                Assertions.assertTrue(Files.exists(handlerDir));
                Path filePath = handlerDir.resolve("default.jpg");
                Files.write(filePath, content, StandardOpenOption.CREATE_NEW);
                Assertions.assertTrue(Files.exists(filePath));
            }
        }
    }

    protected static byte[] getDefaultImage() {
        Assertions.assertNotNull(content);
        return content;
    }

    protected static String getHandlerName(int index) {
        if (index < 0 || index > handlerDirs.size()) throw new IllegalArgumentException("Invalid index");
        return HANDLER_NAME + '_' + (index + 1);
    }

    protected static byte[] storeFile(String filename, int handlerIndex, byte fillByte) throws IOException {
        if (handlerIndex < 0 || handlerIndex > handlerDirs.size()) throw new IllegalArgumentException("Invalid index");
        Path filePath = handlerDirs.get(handlerIndex).resolve(filename);
        Assertions.assertFalse(Files.exists(filePath));
        byte[] file = new byte[100];
        Arrays.fill(file, fillByte);
        Files.write(filePath, file, StandardOpenOption.CREATE_NEW);
        Assertions.assertTrue(Files.exists(filePath));
        return file;
    }

    protected static void fillAuthHeadersForUser1() {
        TestProperties.fillHeadersForUser1();
    }

    protected void seedDB() {
        ResourceDatabasePopulator scriptExecutor = new ResourceDatabasePopulator();
        scriptExecutor.addScript(new ClassPathResource(TestProperties.SCHEMA_FILE_NAME));
        scriptExecutor.addScript(new ClassPathResource(TestProperties.DATA_FILE_NAME));
        scriptExecutor.setSeparator("@@");
        scriptExecutor.execute(this.dataSource);
    }

}
