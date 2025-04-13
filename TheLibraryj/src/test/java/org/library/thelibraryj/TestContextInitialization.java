package org.library.thelibraryj;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public abstract class TestContextInitialization {
    @TempDir
    static Path fileStorage;

    @DynamicPropertySource
    static void fileStoringEnv (DynamicPropertyRegistry registry) throws IOException {
        registry.add("library.user.image_source", fileStorage::toString);
        registry.add("library.book.image_source", fileStorage::toString);
        Path filePath = fileStorage.resolve("default.jpg");
        byte[] content = new byte[5000];
        Arrays.fill(content, (byte) 1);
        Files.write(filePath, content, StandardOpenOption.CREATE_NEW);
        Assertions.assertTrue(Files.exists(filePath));
    }

    @RegisterExtension
    protected static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("username", "password"))
            .withPerMethodLifecycle(false);
}
