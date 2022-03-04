package io;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFileFinder {
    private final String dir = "./src/test/resources/";
    @Test
    void directoryContains_shouldReturnTrue_whenDirectoryContainsFile() throws IOException {
        String dir = this.dir;
        String pth = "simulation_data.csv";
        assertTrue(FileFinder.directoryContains(dir, pth));
    }
    @Test
    void directoryContains_shouldReturnFalse_whenDirectoryDoesNotContainFile() throws IOException {
        String dir = this.dir;
        String pth = "simulation_fakeData.csv";
        assertFalse(FileFinder.directoryContains(dir, pth));
    }
}
