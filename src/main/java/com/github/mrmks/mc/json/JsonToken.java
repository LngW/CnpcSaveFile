package com.github.mrmks.mc.json;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonToken implements Closeable {
    private final Reader reader;
    private int cache = -1;
    private int line = 0;
    private int index = 0;
    private boolean lastFlag = false;

    public JsonToken(File file) throws IOException {
        this.reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), StandardCharsets.UTF_8);
    }

    public JsonToken(String json) {
        this.reader = new StringReader(json);
    }

    private int read() throws IOException {
        return reader.read();
    }

    public int next() throws IOException {
        if (lastFlag) {
            lastFlag = false;
            return cache;
        }
        if (cache == -2) return -1;
        else {
            int c = cache;
            cache = read();
            index ++;
            if (cache == -1) {
                cache = -2;
            } else if (c == '\n' || c == '\r' && (cache == '\r' || cache != '\n')) {
                line += 1;
                index = 0;
            }
            return cache;
        }
    }

    public int nextClean() throws IOException {
        if (lastFlag) {
            lastFlag = false;
            return last();
        } else {
            int c = next();
            while (c > -1 && (c < 32 || c == 127)) c = next();
            return c;
        }
    }

    public int nextCleanSpace() throws IOException {
        if (lastFlag) {
            lastFlag = false;
            int c = last();
            while (c > -1 && (c < 33 || c == 127)) c = next();
            return c;
        } else {
            int c = next();
            while (c > -1 && (c < 33 || c == 127)) c = next();
            return c;
        }
    }

    private int last() {
        return cache == -2 ? -1: cache;
    }

    public void moveLast() {
        lastFlag = true;
    }

    public void close() throws IOException {
        reader.close();
    }

    public String curPosMessage() {
        return "At line " + (line + 1) + ", index " + (index + 1);
    }

}
