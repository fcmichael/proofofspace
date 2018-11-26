package file;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class FileGeneratorTest {

    private final String filePath = "src/test/test.txt";

    @Test
    public void should_generate_random_string_of_specific_size() {
        String string = FileGenerator.generateRandomStringOfSize(10);
        Assert.assertEquals(10, string.length());
    }

    @Test
    public void should_generate_random_file_of_3MBs_size() {
        File file = FileGenerator.ofSizeMBs(filePath, 3);
        Assert.assertEquals("3 MB", FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file)));
    }

    @Test
    public void should_generate_random_file_of_5MBs_size() {
        File file = FileGenerator.ofSizeMBs(filePath, 5);
        Assert.assertEquals("5 MB", FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file)));
    }

    @Test
    public void should_generate_random_file_of_70MBs_size() {
        File file = FileGenerator.ofSizeMBs(filePath, 70);
        Assert.assertEquals("70 MB", FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file)));
    }

    @Test
    public void should_generate_same_hash_for_same_file() {
        FileGenerator.ofSizeMBs(filePath, 3);
        Assert.assertEquals(FileGenerator.getFileMd5Hash(filePath), FileGenerator.getFileMd5Hash(filePath));
    }

    @Test
    public void should_likely_generate_different_hashes_of_different_files() {
        FileGenerator.ofSizeMBs(filePath, 3);
        FileGenerator.ofSizeMBs(filePath + "1", 3);

        String hash = FileGenerator.getFileMd5Hash(filePath);
        String hash1 = FileGenerator.getFileMd5Hash(filePath + "1");

        Assert.assertNotEquals(hash, hash1);
        FileUtils.deleteQuietly(new File(filePath + "1"));
    }

    @After
    public void remove_created_file_if_exists() {
        FileUtils.deleteQuietly(new File(filePath));
    }
}
