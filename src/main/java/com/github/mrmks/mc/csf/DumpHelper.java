package com.github.mrmks.mc.csf;

import net.minecraftforge.fml.common.FMLLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DumpHelper {
    public static byte[] saveDump(String name, byte[] bytes) {
        String fileName = System.getProperty("mrmks.csf.saveDump", "").trim();
        if (!fileName.equals("")) {
            fileName = fileName.replace('/', '\\');
            if (fileName.endsWith(".class")) {
                fileName = fileName.substring(0, fileName.lastIndexOf('\\') + 1);
            }
            if (fileName.endsWith("\\")) {
                fileName = fileName.concat(name.replace('.','\\')).concat(".class");
            }
            else {
                fileName = fileName.concat("\\").concat(name.replace('.','\\')).concat(".class");
            }
            try {
                FMLLog.log.warn("Saving transformed {} to \"{}\"", name.substring(name.lastIndexOf('.') + 1), fileName);
                File file = new File(fileName.substring(0, fileName.lastIndexOf('\\')));
                if (file.exists() || file.mkdirs()) {
                    FileOutputStream fos = new FileOutputStream(fileName);
                    fos.write(bytes);
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }
}
