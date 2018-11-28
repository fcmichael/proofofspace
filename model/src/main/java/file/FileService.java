package file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;

@Slf4j
public class FileService {

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

    public static String getSpecificFileLine(String path, int lineNumber) {
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            for (int i = 1; i < lineNumber; i++) br.readLine();
            line = br.readLine();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return line;
    }

    static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
