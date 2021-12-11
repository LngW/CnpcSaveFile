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

    public int next() throws IOException {
        return lastFlag ? last() : next0();
    }

    public int nextV() throws IOException {
        int c = next();
        while (c > -1 && (c < 32 || c == 127)) c = next0();
        return c;
    }

    public int nextW() throws IOException {
        int c = next();
        while (c > -1 && (c < 33 || c == 127)) c = next0();
        return c;
    }

    private int next0() throws IOException {
        if (cache == -2) return -1;
        else {
            int c = cache;
            cache = reader.read();
            ++index;
            if (cache == -1) cache = -2;
            else if (c == '\n' || c == '\r' && (cache == '\r' || cache != '\n')) {
                line += 1;
                index = 0;
            }
            return cache;
        }
    }

    private int last() {
        lastFlag = false;
        return cache == -2 ? -1: cache;
    }

    public JsonToken back() {
        lastFlag = true;
        return this;
    }

    public void close() throws IOException {
        reader.close();
    }

    public String curPosMessage() {
        return "At line " + (line + 1) + ", index " + (index + 1);
    }

}
