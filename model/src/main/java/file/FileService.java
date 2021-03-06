package file;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileService {

    public static String getFileMd5Hash(String path) {
        return getFileMd5Hash(new File(path));
    }

    public static String getFileMd5Hash(File file) {
        String md5 = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            md5 = DigestUtils.md5Hex(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            md5 = "";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return md5;
    }

    public static String getSpecificFileLine(String path, long lineNumber) {
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            for (int i = 1; i < lineNumber; i++) br.readLine();
            line = br.readLine();
        } catch (FileNotFoundException e) {
            line = "";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return line;
    }

    public static long countFileLines(String path) {
        try {
            return Files.lines(Paths.get(path)).count();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%d %sB", (int) (bytes / Math.pow(unit, exp)), pre);
    }
}
