package app.reader.control.fileOpen;

import java.io.File;

public abstract class DirectoryUtil {
    public static boolean finished=false;

    public static File[] entreIntoDirectory(String dirPath) {
        File[] files=(new File(dirPath)).listFiles();
        finished=true;
        return files;
    }

}
