package com.github.mrmks.mc.csf;

import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TransformHelper {

    static Logger log;

    private static File file = null;
    private static boolean willSave = false;
    private static boolean loaded = false;

    public static byte[] saveDump(String name, byte[] bytes) {
        if (!loaded) {
            loaded = true;
            String fileName = System.getProperty("mrmks.csf.saveDump", "").trim();
            willSave = !fileName.equals("");
            if (willSave) {
                fileName = fileName.replace('/','\\');
                if (fileName.endsWith(".class")) fileName = fileName.substring(0, fileName.lastIndexOf('\\'));
                file = new File(fileName);
            }
        }
        if (willSave) {
            String full = name.replace('.', '\\');
            String path = full.substring(0, full.lastIndexOf('\\'));
            name = full.substring(full.lastIndexOf('\\') + 1);

            File dic = new File(file, path);
            if (dic.exists() || dic.mkdirs()) {
                dic = new File(dic, name.concat(".class"));
                log.debug("Saving transformed {} to \"{}\"", name, dic);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(dic);
                } catch (IOException e) {
                    log.debug("Can't dump {} to \"{}\"", name, dic);
                    log.debug(e);
                }
                if (fos != null) {
                    try {
                        fos.write(bytes);
                    } catch (IOException e) {
                        log.debug("Can't dump {} to \"{}\"", name, dic);
                        log.debug(e);
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            log.debug(e);
                        }
                    }
                }
            }
        }
        return bytes;
    }

    static void transformed(String name) {
        log.warn("Transformed: ".concat(name));
    }
}
