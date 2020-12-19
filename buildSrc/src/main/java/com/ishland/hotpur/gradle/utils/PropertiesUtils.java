package com.ishland.hotpur.gradle.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class PropertiesUtils {

    public static void saveProperties(Properties prop, Path file, String comments){
        System.out.println("Saving properties file to " + file);
        file.toFile().getParentFile().mkdirs();
        file.toFile().delete();
        try(final OutputStream out = new FileOutputStream(file.toFile())) {
            prop.store(out, comments);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
