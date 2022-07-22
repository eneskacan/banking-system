package com.eneskacan.bankingsystem.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public final class FileWriterUtil {

    private FileWriterUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean writeToFile(String msg, String path, boolean append) {
        File file = new File(path);

        // Check if parent dir exists
        if(!file.getParentFile().exists()) {
            boolean isParentDirCreated = file.getParentFile().mkdirs();

            // Return here if fails to create the parent folder
            if(!isParentDirCreated) return false;
        }

        try (FileWriter fw = new FileWriter(path, append); BufferedWriter bw = new BufferedWriter(fw)) {
            // Check if file exists
            if(!file.exists()) {
                boolean isFileCreated = file.createNewFile();

                // Return here if fails to create the file
                if(!isFileCreated) return false;
            }

            // Write data to the file
            bw.write(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
