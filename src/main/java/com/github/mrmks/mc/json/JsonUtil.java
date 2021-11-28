package com.github.mrmks.mc.json;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.nbt.*;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.NBTJsonUtil.JsonException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JsonUtil {
    public static JsonException createException(String msg, JsonToken token) {
        System.out.println(msg + ": " + token.curPosMessage());
        throw new UnsupportedOperationException("This method should be replaced via asm");
    }

    public static NBTTagCompound fillCompound(JsonToken token) throws IOException, JsonException {
        NBTTagCompound tag = new NBTTagCompound();
        int c = token.nextCleanSpace();
        if (c != '}') {
            token.moveLast();
            while (true) {
                String key = readKey(token);
                c = token.nextCleanSpace();
                if (c != ':') throw createException("Expected ':', but found '" + (char)c + "'", token);

                NBTBase value = readValue(token);
                tag.setTag(key, value);
                c = token.nextCleanSpace();
                if (c == '}') break;
                else if (c != ',') throw createException("Expected ',' or '}', but found '" + (char) c + "'", token);
            }
        }
        return tag;
    }

    private static char[] keyWorker = new char[32];
    private static String readKey(JsonToken token) throws IOException, JsonException {
        int c = token.nextCleanSpace();
        if (c == '\"') {
            int index = 0;
            c = token.nextClean();
            while (c != '\"') {
                if (c == -1) throw createException("Expected '\"', but found EOF", token);
                if (index >= keyWorker.length) {
                    char[] swap = new char[keyWorker.length + 32];
                    System.arraycopy(keyWorker, 0, swap, 0, keyWorker.length);
                    keyWorker = swap;
                }
                keyWorker[index++] = (char) c;
                c = token.nextClean();
            }
            return new String(Arrays.copyOf(keyWorker, index));
        }
        throw createException("Can't parse a key, expected '\"', but found '" + (char)c + "'", token);
    }

    private static NBTBase readValue(JsonToken token) throws IOException, JsonException {
        int c = token.nextCleanSpace();
        if (c == -1) throw createException("Found EOF while parsing value", token);
        else if (c == '{') {
            // compound
            return fillCompound(token);
        } else if (c == '[') {
            // list, byteArray, intArray, longArray
            c = token.nextCleanSpace();
            if (c == 'B' || c == 'L' || c == 'I') {
                int c1 = token.nextClean();
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
                } else throw createException("Expected ';' but found '" + (char)c1 + "'", token);
            } else {
                NBTTagList list = new NBTTagList();
                if (c != ']') {
                    token.moveLast();
                    while (true) {
                        list.appendTag(readValue(token));
                        c = token.nextCleanSpace();
                        if (c == ']') break;
                        else if (c == -1) throw createException("Expected ',', but found EOF", token);
                        else if (c != ',') throw createException("Expected ',', but found '" + (char)c + "'", token);
                    }
                }
                return list;
            }
        } else if (c == '\"') {
            // string
            StringBuilder sb = new StringBuilder();
            boolean last = false;
            while ((c = token.next()) != '\"' || last) {
                if (c == -1) throw createException("Found EOF before found \"", token);
                else if (c == '\\') {
                    if (last) sb.append('\\');
                    last = !last;
                } else if (c == '\"') {
                    sb.append('\"');
                    last = false;
                } else {
                    if (last) throw createException("Expected '\\' but found '" + (char)c + "'", token);
                    else sb.append((char) c);
                }
            }
            return new NBTTagString(sb.toString());
        } else {
            // byte, short, int, float, long, double
            token.moveLast();
            long l = parseInteger(token);
            c = token.nextClean();
            if (c == '.') {
                double d = l + parseFloat(token);
                c = token.nextClean();
                switch (c) {
                    case 'f': return new NBTTagFloat((float) d);
                    case 'd': return new NBTTagDouble(d);
                    default: throw createException("Unexpected subfix '" + (char)c + "'", token);
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
                        token.moveLast();
                        return new NBTTagInt((int) l);
                }
            }
        }
    }

    private static long parseInteger(JsonToken tk) throws IOException, JsonException {
        int c = tk.nextCleanSpace();
        if (c == -1) throw createException("Found EOF while parsing a number", tk);
        long prefix = 0;
        boolean minus = false;

        if (c == '-') minus = true; else if (c >= '0' && c <= '9') prefix = c - '0';
        else throw createException("Unexpected char '" + (char) c + "'", tk);

        c = tk.nextClean();
        while (c >= '0' && c <= '9') {
            prefix = prefix * 10 + (c - '0');
            c = tk.nextClean();
        }
        tk.moveLast();
        return minus ? -prefix : prefix;
    }

    private static double parseFloat(JsonToken tk) throws IOException, JsonException {
        int c = tk.nextClean();
        if (c == -1) throw createException("Found EOF while parsing a number", tk);
        double prefix = 0;
        long bt = 1;

        while ((c = tk.nextClean()) >= '0' && c <= '9') {
            prefix = prefix * 10 + c - '0';
            bt *= 10;
        }
        tk.moveLast();
        return prefix / bt;
    }

    private static void parseNumberArray(JsonToken tk, LongFunc func) throws IOException, JsonException {
        boolean minus = false;
        boolean eon = false;
        long prefix = 0;
        int c;
        while (true) {
            c = tk.nextCleanSpace();
            if (c == -1) throw createException("Found EOF while parsing list", tk);
            else if (c == ',' || c ==']') {
                func.apply(minus ? -prefix : prefix, tk);
                minus = eon = false;
                prefix = 0;
                if (c == ']') break;
            } else if (c == '-' || (c >= '0' && c <= '9')) {
                if (eon) throw createException("Expected ',' or ']', but found '" + (char) c + "'", tk);
                if (c == '-') minus = true; else prefix = c - '0';
                while ((c = tk.nextClean()) >= '0' && c <= '9') prefix = prefix * 10 + (c - '0');
                if (c != 'B' && c != 'L') tk.moveLast();
                eon = true;
            } else throw createException("Illegal char '" + (char)c + "' in array", tk);
        }
    }

    public static NBTTagCompound LoadFile(File file) throws IOException, JsonException {
        JsonToken token = new JsonToken(file);
        NBTTagCompound tag;
        try {
            int c = token.nextCleanSpace();
            if (c == '{') {
                tag = JsonUtil.fillCompound(token);
                token.close();
            } else {
                token.close();
                throw JsonUtil.createException("Expected '}' but found '" + (char)c + "'", token);
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
            int c = token.nextCleanSpace();
            if (c == '{') {
                tag = JsonUtil.fillCompound(token);
                token.close();
                return tag;
            } else {
                token.close();
                throw JsonUtil.createException("Expected '}' but found '\" + (char)c + \"'", token);
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
        Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), StandardCharsets.UTF_8);
        try {
            writeTag(writer, compound, 0);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            writer.flush();
            writer.close();
        }
    }

    private interface LongFunc {
        void apply(long l, JsonToken tk) throws JsonException;
    }
}
