package file;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileServiceTest {

    private final String filePath = "src/test/test.txt";

    @Test
    public void should_generate_same_hash_for_same_file() {
        FileGenerator.ofSizeMBs(filePath, 3);
        Assert.assertEquals(FileService.getFileMd5Hash(filePath), FileService.getFileMd5Hash(filePath));
    }

    @Test
    public void should_likely_generate_different_hashes_of_different_files() {
        FileGenerator.ofSizeMBs(filePath, 3);
        FileGenerator.ofSizeMBs(filePath + "1", 3);

        String hash = FileService.getFileMd5Hash(filePath);
        String hash1 = FileService.getFileMd5Hash(filePath + "1");

        Assert.assertNotEquals(hash, hash1);
        FileUtils.deleteQuietly(new File(filePath + "1"));
    }

    @Test
    public void should_return_nth_file_line() throws IOException {
        File file = new File(filePath);
        FileWriter writer = new FileWriter(file);
        writer.write("AAA");
        writer.write("\n");
        writer.write("BBB");
        writer.write("\n");
        writer.write("CCC");
        writer.close();

        Assert.assertEquals("AAA", FileService.getSpecificFileLine(filePath, 1));
        Assert.assertEquals("BBB", FileService.getSpecificFileLine(filePath, 2));
        Assert.assertEquals("CCC", FileService.getSpecificFileLine(filePath, 3));
    }

    @Test
    public void should_return_empty_string_when_asking_of_specific_line_of_not_existing_file() {
        Assert.assertEquals("", FileService.getSpecificFileLine("aa", 1));
    }

    @Test
    public void should_return_empty_string_when_asking_of_hash_of_not_existing_file() {
        Assert.assertEquals("", FileService.getFileMd5Hash("aa"));
    }

    @After
    public void remove_created_file_if_exists() {
        FileUtils.deleteQuietly(new File(filePath));
    }
}
