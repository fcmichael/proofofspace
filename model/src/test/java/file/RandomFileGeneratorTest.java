package file;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class RandomFileGeneratorTest {

    private final String filePath = "src/test/test.txt";

    @Test
    public void should_generate_random_string_of_specific_size(){
        String string = RandomFileGenerator.generateRandomStringOfSize(10);
        Assert.assertEquals(10, string.length());
    }

    @Test
    public void should_generate_random_file_of_3MBs_size(){
        File file = RandomFileGenerator.ofSizeMBs(filePath, 3);
        Assert.assertEquals("3 MB", FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file)));
    }

    @Test
    public void should_generate_random_file_of_5MBs_size(){
        File file = RandomFileGenerator.ofSizeMBs(filePath, 5);
        Assert.assertEquals("5 MB", FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file)));
    }

    @Test
    public void should_generate_random_file_of_70MBs_size(){
        File file = RandomFileGenerator.ofSizeMBs(filePath, 70);
        Assert.assertEquals("70 MB", FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file)));
    }

    @After
    public void remove_created_file_if_exists(){
        FileUtils.deleteQuietly(new File(filePath));
    }
}
