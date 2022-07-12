package com.eneskacan.bankingsystem.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class FileReaderUtil {

    private FileReaderUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static List<String> readFile(String path) {
        List<String> data = new ArrayList<>();

        try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr)) {
            String line = br.readLine();
            while (line != null) {
                data.add(line);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
