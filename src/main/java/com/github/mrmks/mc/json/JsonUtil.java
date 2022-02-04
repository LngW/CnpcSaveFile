package com.github.mrmks.mc.json;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.nbt.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonUtil {

    private static String toChar(int c) {
        return c == -1 ? "EOF" : new String(new char[]{'\'', (char) c, '\''});
    }

    public static NBTTagCompound fillCompound(JsonToken token, int depth) throws IOException, JsonException {
        if (depth > 512) throw new JsonException("Tried to read NBT tag with too high complexity, depth > 512", token);
        NBTTagCompound tag = new NBTTagCompound();
        int c = token.nextW();
        if (c != '}') {
            token.back();
            for (;;) {
                String key = readStr(token, false);
                c = token.nextW();
                if (c != ':') throw new JsonException("Expected ':', but found ".concat(toChar(c)), token);

                NBTBase value = readValue(token, depth + 1);
                tag.setTag(key, value);
                c = token.nextW();
                if (c == ',') continue;
                if (c == '}') break;
                else throw new JsonException("Expected ',' or '}', but found ".concat(toChar(c)), token);
            }
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

    private static String readStr(JsonToken token, boolean complex) throws IOException, JsonException {
        int c = token.nextW();
        if (c == '\"') {
            int index = 0, c1;
            while ((c = token.next()) != '\"') {
                if (c == -1) throw new JsonException("Expected '\"', but found EOF", token);
                if (complex && c == '\\') {
                    c1 = token.next();
                    if (c1 == '\"' || c1 == '\\') {
                        c = c1;
                    } else {
                        throw new JsonException("Expected '\\' or '\"', but found ".concat(toChar(c1)), token);
                    }
                }
                ensureStrWorker(index);
                keyWorker[index++] = (char) c;
            }
            return new String(keyWorker, 0, index);
        }
        throw new JsonException("Can't parse a string, expected '\"', but found ".concat(toChar(c)), token);
    }

    private static NBTBase readValue(JsonToken token, int depth) throws IOException, JsonException {
        int c = token.nextW();
        if (c == -1) throw new JsonException("Found EOF while parsing value", token);
        else if (c == '{') {
            // compound
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
                        parseNumberArray(token, 'B', ((l, tk) -> {
                            if (l > Byte.MAX_VALUE || l < Byte.MIN_VALUE)
                                throw new JsonException("'" + l + "' can't be parsed to byte", token);
                            list.add((byte) l);
                        }));
                        return new NBTTagByteArray(list.toByteArray());
                    } else if (c == 'L') {
                        // longArray
                        LongArrayList list = new LongArrayList();
                        parseNumberArray(token, 'L', (l,tk)->list.add(l));
                        return new NBTTagLongArray(list.toLongArray());
                    } else {
                        // intArray
                        IntArrayList list = new IntArrayList();
                        parseNumberArray(token, 'I', (l,tk) -> {
                            if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE)
                                throw new JsonException("'" + l + "' can't be parsed to int", tk);
                            list.add((int) l);
                        });
                        return new NBTTagIntArray(list.toIntArray());
                    }
                } else throw new JsonException("Expected ';' but found ".concat(toChar(c1)), token);
            } else {
                if (depth > 512) throw new JsonException("Tried to read NBT tag with too high complexity, depth > 512", token);
                NBTTagList list = new NBTTagList();
                if (c != ']') {
                    token.back();
                    for (;;) {
                        NBTBase base = readValue(token, depth + 1);
                        if (list.getTagType() == 0 || list.getTagType() == base.getId()) list.appendTag(base);
                        c = token.nextW();
                        if (c == ',') continue;
                        if (c == ']') break;
                        else throw new JsonException("Expected ',' or ']', but found ".concat(toChar(c)), token);
                    }
                }
                return list;
            }
        } else if (c == '\"') {
            // string
            return new NBTTagString(readStr(token.back(), true));
        } else if (c == '-' || c >= '0' && c <= '9') {
            // byte, short, int, float, long, double
            boolean minus = c == '-';
            long l = parseInteger(minus ? token : token.back(), false);
            c = token.nextV();
            if (c == '.') {
                double d = l + parseFloat(token);
                d = minus ? -d : d;
                c = token.nextV();
                switch (c) {
                    case 'f':
                        if (d > Float.MAX_VALUE || d < Float.MIN_NORMAL)
                        return new NBTTagFloat((float) d);
                    case 'd': return new NBTTagDouble(d);
                    case -1: throw new JsonException("Found EOF while parsing float number", token);
                    default: throw new JsonException("Unexpected suffix ".concat(toChar(c)), token);
                }
            } else {
                l = minus ? -l : l;
                switch (c) {
                    case 'b':
                        if (l < Byte.MIN_VALUE || l > Byte.MAX_VALUE) throw new JsonException("Can't parse " + l + " to byte", token);
                        return new NBTTagByte((byte) l);
                    case 's':
                        if (l < Short.MIN_VALUE || l > Short.MAX_VALUE) throw new JsonException("Can't parse " + l + " to short", token);
                        return new NBTTagShort((short) l);
                    case 'L':
                        return new NBTTagLong(l);
                    default:
                        token.back();
                        if (l <= Integer.MIN_VALUE || l >= Integer.MAX_VALUE) throw new JsonException("Can't parse " + l + " to int", token);
                        return new NBTTagInt((int) l);
                }
            }
        } else throw new JsonException("Unexpected content", token);
    }

    private static long parseInteger(JsonToken tk, boolean readMinus) throws IOException, JsonException {
        int c = tk.nextW();
        long prefix;
        boolean minus = false;

        if (readMinus && c == '-') {
            minus = true;
            c = tk.nextV();
        }
        if (c >= '0' && c <= '9') prefix = c - '0';
        else throw new JsonException("Unexpected char " + toChar(c) + " while parsing a number", tk);

        while ((c = tk.nextV()) >= '0' && c <= '9') prefix = (prefix << 3) + (prefix << 1) + (c - '0');

        tk.back();
        return minus ? -prefix : prefix;
    }

    private static double parseFloat(JsonToken tk) throws IOException, JsonException {
        int c = tk.nextV();
        if (c >= '0' && c <= '9') {
            long prefix = c - '0';
            long bt = 10;
            while ((c = tk.nextV()) >= '0' && c <= '9') {
                prefix = (prefix << 3) + (prefix << 1) + c - '0';
                bt = (bt << 3) + (bt << 1);
            }

            tk.back();
            return (double) prefix / bt;
        } else throw new JsonException("Found EOF while parsing a number", tk);
    }

    private static void parseNumberArray(JsonToken tk, char suffix, LongFunc func) throws IOException, JsonException {
        for (;;) {
            long n = parseInteger(tk, true);
            int c = tk.nextV();
            if (suffix == 'B' || suffix == 'L') {
                if (c != suffix) throw new JsonException("Unexpected suffix ".concat(toChar(c)), tk);
                c = tk.nextV();
            }
            if (c == ',') {
                func.apply(n, tk);
            } else if (c == ']') {
                func.apply(n, tk);
                break;
            } else throw new JsonException("Expected ',' or ']', but found ".concat(toChar(c)), tk);
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
                throw new JsonException("Expected '}' but found ".concat(toChar(c)), token);
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
                throw new JsonException("Expected '}' but found ".concat(toChar(c)), token);
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

    public static void SaveFile(File file, NBTTagCompound compound) throws IOException {
        try (Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), StandardCharsets.UTF_8)) {
            writeTag(writer, compound, 0);
        }
    }

    /**
     *  This method should only be used in test environment.
     */
    public static NBTBase convertFrom(String str) throws JsonException, IOException {
        JsonToken tk = new JsonToken(str);
        NBTBase tag = readValue(tk, 0);
        tk.close();
        return tag;
    }

    private interface LongFunc {
        void apply(long l, JsonToken tk) throws JsonException;
    }

    public static class JsonException extends Exception {
        JsonException(String msg, JsonToken tk) {
            super(msg + ": " + tk.curPosMessage());
        }
    }
}
