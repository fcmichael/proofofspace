package file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class FileGenerator {

    public static File ofSizeMBs(String path, int sizeMBs) {

        File file = new File(path);

        try {
            FileWriter writer = new FileWriter(file);
            long size = sizeMBs * 1024 * 1024;

            while (FileUtils.sizeOf(file) < size) {
                writer.write(generateRandomStringOfSize(1024));
            }

            writer.close();
        } catch (IOException e) {
            log.error("Error while generating random file of size = " + sizeMBs + " MBs");
            log.error(e.getMessage());
        }

        return file;
    }

    static String generateRandomStringOfSize(int size) {
        return RandomStringUtils.randomAlphanumeric(size);
    }

    static String getFileMd5Hash(String path) {
        String md5 = "";
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            md5 = DigestUtils.md5Hex(fis);
            fis.close();
        } catch (IOException e) {
            log.error("Error while generating file md5 hash, filePath = " + path);
            log.error(e.getMessage());
        }
        return md5;
    }
}
