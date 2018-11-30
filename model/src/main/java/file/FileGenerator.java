package file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class FileGenerator {

    public static File ofSizeMBs(String path, int sizeMBs) {
        File file = new File(path);
        try {
            FileUtils.touch(file);
            FileWriter writer = new FileWriter(file);
            long size = sizeMBs * 1000 * 1000;

            while (FileUtils.sizeOf(file) < size) {
                writer.write(generateRandomStringOfSize(99));
                writer.write("\n");
            }

            writer.close();
        } catch (IOException e) {
            log.error("Błąd podczas generowania pliku o rozmiarze = " + sizeMBs + " MBs");
            log.error(e.getMessage());
        }

        return file;
    }

    static String generateRandomStringOfSize(int size) {
        return RandomStringUtils.randomAlphanumeric(size);
    }
}
