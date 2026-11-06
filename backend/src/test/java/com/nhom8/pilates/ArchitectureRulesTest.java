package com.nhom8.pilates;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchitectureRulesTest {
    @Test
    void sourceMustNotUseLombok() throws IOException {
        Path sourceRoot = Path.of("src", "main", "java");
        List<Path> violations;
        try (var paths = Files.walk(sourceRoot)) {
            violations = paths.filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> read(path).contains("lombok"))
                    .toList();
        }
        assertTrue(violations.isEmpty(), "Không được sử dụng Lombok: " + violations);
    }

    private String read(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
