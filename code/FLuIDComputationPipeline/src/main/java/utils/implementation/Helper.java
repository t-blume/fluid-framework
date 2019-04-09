package utils.implementation;

import common.IResource;

import java.io.File;

public class Helper {

    public static boolean isLiteral(IResource resource){
        return resource.toN3().matches("\".*\"(@[a-z]+)?");
    }

    public static File createFile(String filepath){
        File targetFile = new File(filepath);
        File parent = targetFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
        return targetFile;
    }
}
