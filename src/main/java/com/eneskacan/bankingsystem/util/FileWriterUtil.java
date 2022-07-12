package com.eneskacan.bankingsystem.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public final class FileWriterUtil {

    private FileWriterUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean writeToFile(String msg, String path, boolean append) {
        try (FileWriter fw = new FileWriter(path, append); BufferedWriter bw = new BufferedWriter(fw)) {
            File file = new File(path);
            if(!file.exists()) file.createNewFile();

            bw.write(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
