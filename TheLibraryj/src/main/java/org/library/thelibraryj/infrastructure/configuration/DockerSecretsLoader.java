package org.library.thelibraryj.infrastructure.configuration;

import org.library.thelibraryj.infrastructure.exception.DockerSecretParsingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DockerSecretsLoader implements EnvironmentPostProcessor, Ordered {
    private static final Path DOCKER_SECRET_BIND_PATH = Path.of("/run/secrets");

    private record SecretPair(String key, String value) {}

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (Files.exists(DOCKER_SECRET_BIND_PATH) && Files.isDirectory(DOCKER_SECRET_BIND_PATH)) {
            try (Stream<Path> paths = Files.list(DOCKER_SECRET_BIND_PATH)) {
                Map<String, Object> extractedSecrets = paths
                        .filter(path -> path.getFileName().toString().startsWith("library_"))
                        .map(DockerSecretsLoader::getSecret)
                        .collect(Collectors.toMap(sp -> sp.key, sp -> sp.value));
                if(extractedSecrets.isEmpty()) return;
                environment.getPropertySources().addLast(new MapPropertySource("docker-secrets", extractedSecrets));
            } catch (IOException e) {
                throw new DockerSecretParsingException("Error reading docker secrets. Cause: " + e.getMessage());
            }
        }
    }

    private static SecretPair getSecret(Path pathToSecret){
        String content;
        try {
            content = Files.readString(pathToSecret).trim();
        } catch (IOException e) {
            throw new DockerSecretParsingException("Error reading a docker secret file at: " + pathToSecret);
        }
        String[] KV = content.split("=");
        if (KV.length != 2) throw new DockerSecretParsingException("Invalid docker secret file format. Must be [key]=[value]");
        return new SecretPair(KV[0], KV[1]);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
