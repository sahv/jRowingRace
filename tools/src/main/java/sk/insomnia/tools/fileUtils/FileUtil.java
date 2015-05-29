package sk.insomnia.tools.fileUtils;

import org.apache.log4j.Logger;
import sk.insomnia.tools.exceptionUtils.ExceptionUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;


/**
 * Helper class for reading and writing files.
 */
public class FileUtil {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final Logger logger = Logger.getLogger(FileUtil.class.toString());

    public static boolean fileExists(String filename) {
        File f = new File(filename);
        return f.exists();
    }

    /**
     * Reads the specified file and returns the content as a String.
     *
     * @param file
     * @return
     * @throws IOException thrown if an I/O error occurs opening the file
     */
    public static String readFile(File file) throws IOException {

        StringBuffer stringBuffer = new StringBuffer();

        BufferedReader reader = Files.newBufferedReader(file.toPath(), CHARSET);

        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
        }

        reader.close();
        return stringBuffer.toString();
    }

    public static boolean renameFile(String filename, String newFileName) {
        try {

            File file = new File(filename);
            File fileTo = new File(newFileName);
            return file.renameTo(fileTo);
        } catch (Exception e) {

            logger.debug(ExceptionUtils.exceptionAsString(e));

        }
        return false;
    }

    public static boolean deleteFile(String filename) {
        try {

            File file = new File(filename);

            if (file.delete()) {
                return true;
            }

        } catch (Exception e) {

            logger.debug(ExceptionUtils.exceptionAsString(e));

        }
        return false;
    }

    /**
     * Saves the content String to the specified file.
     *
     * @param content
     * @param file
     * @throws IOException thrown if an I/O error occurs opening or creating the file
     */
    public static void saveFile(String content, File file) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(file.toPath(), CHARSET);
        writer.write(content, 0, content.length());
        writer.close();
    }
}