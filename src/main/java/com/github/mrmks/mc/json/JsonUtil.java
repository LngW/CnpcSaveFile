package com.github.mrmks.mc.json;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.nbt.*;
import noppes.npcs.LogWriter;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.NBTJsonUtil.JsonException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonUtil {
    public static JsonException createException(String msg, JsonToken token) {
        throw new UnsupportedOperationException(msg + ": " + token.curPosMessage());
    }

    private static String toChar(int c) {
        return c == -1 ? "EOF" : new String(new char[]{'\'', (char) c, '\''});
    }

    public static NBTTagCompound fillCompound(JsonToken token, int depth) throws IOException, JsonException {
        NBTTagCompound tag = new NBTTagCompound();
        int c = token.nextW();
        if (c != '}') {
            token.back();
            do {
                String key = readStr(token);
                c = token.nextW();
                if (c != ':') throw createException("Expected ':', but found ".concat(toChar(c)), token);

                NBTBase value = readValue(token, depth + 1);
                tag.setTag(key, value);
                c = token.nextW();
                if (c != '}' && c != ',') throw createException("Expected ',' or '}', but found ".concat(toChar(c)), token);
            } while (c == ',');
        }
        return tag;
    }

    private static char[] keyWorker = new char[32];
    private static int step = 32;
    private static void ensureStrWorker(int index) {
        if (keyWorker.length <= index) {
            if (keyWorker.length / step > 3) step *= 2;
            char[] swap = new char[keyWorker.length + step];
            System.arraycopy(keyWorker, 0, swap, 0, keyWorker.length);
            keyWorker = swap;
        }
    }

    private static String readStr(JsonToken token) throws IOException, JsonException {
        int c = token.nextW();
        if (c == '\"') {
            int index = 0;
            while ((c = token.nextV()) != '\"') {
                if (c == -1) throw createException("Expected '\"', but found EOF", token);
                if (c == '\\') {
                    int c1 = token.nextV();
                    if (c1 == '\"' || c1 == '\\') {
                        c = c1;
                    } else {
                        throw createException("Expected '\\' or '\"', but found ".concat(toChar(c1)), token);
                    }
                }
                ensureStrWorker(index);
                keyWorker[index++] = (char) c;
            }
            return new String(keyWorker, 0, index);
        }
        throw createException("Can't parse a string, expected '\"', but found ".concat(toChar(c)), token);
    }

    private static NBTBase readValue(JsonToken token, int depth) throws IOException, JsonException {
        int c = token.nextW();
        if (c == -1) throw createException("Found EOF while parsing value", token);
        else if (c == '{') {
            // compound
            if (depth > 512) throw createException("Tried to read NBT tag with too high complexity, depth > 512", token);
            return fillCompound(token, depth + 1);
        } else if (c == '[') {
            // list, byteArray, intArray, longArray
            c = token.nextW();
            if (c == 'B' || c == 'L' || c == 'I') {
                int c1 = token.nextV();
                if (c1 == ';') {
                    if (c == 'B') {
                        // byteArray
                        ByteArrayList list = new ByteArrayList();
                        parseNumberArray(token, ((l, tk) -> {
                            if (l > Byte.MAX_VALUE || l < Byte.MIN_VALUE)
                                throw createException("'" + l + "' can't be parsed to byte", token);
                            list.add((byte) l);
                        }));
                        return new NBTTagByteArray(list.toByteArray());
                    } else if (c == 'L') {
                        // longArray
                        LongArrayList list = new LongArrayList();
                        parseNumberArray(token, (l,tk)->list.add(l));
                        return new NBTTagLongArray(list.toLongArray());
                    } else {
                        // intArray
                        IntArrayList list = new IntArrayList();
                        parseNumberArray(token, (l,tk) -> {
                            if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE)
                                throw createException("'" + l + "' can't be parsed to int", tk);
                            list.add((int) l);
                        });
                        return new NBTTagIntArray(list.toIntArray());
                    }
                } else throw createException("Expected ';' but found ".concat(toChar(c1)), token);
            } else {
                NBTTagList list = new NBTTagList();
                if (depth > 512) throw createException("Tried to read NBT tag with too high complexity, depth > 512", token);
                if (c != ']') {
                    token.back();
                    for (;;) {
                        NBTBase base = readValue(token, depth + 1);
                        if (list.getTagType() == 0 || list.getTagType() == base.getId()) list.appendTag(base);
                        else LogWriter.warn("Adding mismatching tag types to tag list : " + token.curPosMessage());
                        c = token.nextW();
                        if (c == ']') break;
                        else if (c != ',') throw createException("Expected ',', but found ".concat(toChar(c)), token);
                    }
                }
                return list;
            }
        } else if (c == '\"') {
            // string
            return new NBTTagString(readStr(token.back()));
        } else {
            // byte, short, int, float, long, double
            long l = parseInteger(token.back());
            c = token.nextV();
            if (c == '.') {
                double d = l + parseFloat(token);
                c = token.nextV();
                switch (c) {
                    case 'f': return new NBTTagFloat((float) d);
                    case 'd': return new NBTTagDouble(d);
                    case -1: throw createException("Found EOF while parsing float number", token);
                    default: throw createException("Unexpected suffix ".concat(toChar(c)), token);
                }
            } else {
                switch (c) {
                    case 'b':
                        return new NBTTagByte((byte) l);
                    case 's':
                        return new NBTTagShort((short) l);
                    case 'L':
                        return new NBTTagLong(l);
                    default:
                        token.back();
                        return new NBTTagInt((int) l);
                }
            }
        }
    }

    private static long parseInteger(JsonToken tk) throws IOException, JsonException {
        int c = tk.nextW();
        if (c == -1) throw createException("Found EOF while parsing a number", tk);
        long prefix = 0;
        boolean minus = false;

        if (c == '-') minus = true; else if (c >= '0' && c <= '9') prefix = c - '0';
        else throw createException("Unexpected char " + toChar(c) + " while parsing a number", tk);

        while ((c = tk.nextV()) >= '0' && c <= '9') prefix = prefix * 10 + (c - '0');

        tk.back();
        return minus ? -prefix : prefix;
    }

    private static double parseFloat(JsonToken tk) throws IOException, JsonException {
        int c = tk.nextV();
        if (c == -1) throw createException("Found EOF while parsing a number", tk);
        double prefix = 0;
        long bt = 1;

        while ((c = tk.nextV()) >= '0' && c <= '9') {
            prefix = prefix * 10 + c - '0';
            bt *= 10;
        }
        tk.back();
        return prefix / bt;
    }

    private static void parseNumberArray(JsonToken tk, LongFunc func) throws IOException, JsonException {
        boolean minus = false;
        boolean eon = false;
        long prefix = 0;
        int c;
        while (true) {
            c = tk.nextW();
            if (c == -1) throw createException("Found EOF while parsing list", tk);
            else if (c == ',' || c ==']') {
                func.apply(minus ? -prefix : prefix, tk);
                minus = eon = false;
                prefix = 0;
                if (c == ']') break;
            } else if (c == '-' || (c >= '0' && c <= '9')) {
                if (eon) throw createException("Expected ',' or ']', but found ".concat(toChar(c)), tk);
                if (c == '-') minus = true; else prefix = c - '0';
                while ((c = tk.nextV()) >= '0' && c <= '9') prefix = prefix * 10 + (c - '0');
                if (c != 'B' && c != 'L') tk.back();
                eon = true;
            } else throw createException("Illegal char " + toChar(c) + " in array", tk);
        }
    }

    public static NBTTagCompound LoadFile(File file) throws IOException, JsonException {
        JsonToken token = new JsonToken(file);
        NBTTagCompound tag;
        try {
            int c = token.nextW();
            if (c == '{') {
                tag = JsonUtil.fillCompound(token, 0);
                token.close();
            } else {
                token.close();
                throw JsonUtil.createException("Expected '}' but found ".concat(toChar(c)), token);
            }
        } catch (IOException | JsonException e) {
            token.close();
            throw e;
        }
        return tag;
    }

    public static NBTTagCompound Convert(String json) throws JsonException {
        JsonToken token = new JsonToken(json);
        NBTTagCompound tag;
        try {
            int c = token.nextW();
            if (c == '{') {
                tag = JsonUtil.fillCompound(token, 0);
                token.close();
                return tag;
            } else {
                token.close();
                throw JsonUtil.createException("Expected '}' but found ".concat(toChar(c)), token);
            }
        } catch (IOException e) {
            // this should never happen
            return null;
        }
    }

    private static final char[] space4 = new char[]{' ',' ',' ',' '};
    private static Writer appendPrefix(Writer w, int level) throws IOException {
        for (int i = 0; i < level; i++) w.write(space4);
        return w;
    }

    private static void writeTag(Writer w, NBTBase tag, int level) throws IOException {
        int id = tag.getId();
        boolean first = true;
        if (id == 10) {
            if (tag.hasNoTags()) w.write("{}");
            else {
                NBTTagCompound tagC = (NBTTagCompound) tag;
                w.write("{\n");
                for (String k : tagC.getKeySet()) {
                    if (!first) w.write(",\n");
                    writeTag(appendPrefix(w, level + 1).append('\"').append(k).append("\": "), tagC.getTag(k), level + 1);
                    first = false;
                }
                appendPrefix(w.append('\n'), level).write('}');
            }
        } else if (id == 9) {
            if (tag.hasNoTags()) w.write("[]");
            else {
                NBTTagList tagL = (NBTTagList) tag;
                w.append("[\n");
                for (NBTBase b : tagL) {
                    if (!first) w.write(",\n");
                    writeTag(appendPrefix(w, level + 1), b, level + 1);
                    first = false;
                }
                appendPrefix(w.append('\n'), level).write(']');
            }
        } else {
            w.write(tag.toString());
        }
    }

    public static void SaveFile(File file, NBTTagCompound compound) throws IOException, NBTJsonUtil.JsonException {
        try (Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), StandardCharsets.UTF_8)) {
            writeTag(writer, compound, 0);
        }
    }

    private interface LongFunc {
        void apply(long l, JsonToken tk) throws JsonException;
    }
}
