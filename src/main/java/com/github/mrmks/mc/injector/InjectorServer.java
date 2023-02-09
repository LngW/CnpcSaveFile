package com.github.mrmks.mc.injector;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class InjectorServer {

    static ThreadLocal<Deflater> local = new ThreadLocal<>();

    public static OutputStream warpGZipOutputStream(OutputStream stream) throws IOException {
        Deflater deflater = local.get();
        if (deflater == null) {
            local.set(deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true));
        }

        return new ReuseDeflaterOutputStream(stream, deflater);
    }

    // nearly a copy of GZIPOutputStream
    private static class ReuseDeflaterOutputStream extends DeflaterOutputStream {

        private final static int GZIP_MAGIC = 0x8b1f;
        private final static int TRAILER_SIZE = 8;

        CRC32 crc = new CRC32();

        ReuseDeflaterOutputStream(OutputStream stream, Deflater deflater) throws IOException {
            super(stream, deflater);
            deflater.reset();
            crc.reset();
            writeHeader();
        }

        private void writeHeader() throws IOException {
            out.write(new byte[] {
                    (byte) GZIP_MAGIC,
                    (byte) (GZIP_MAGIC >> 8),
                    Deflater.DEFLATED,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
            });
        }

        @Override
        public synchronized void write(byte[] buf, int off, int len)
                throws IOException
        {
            super.write(buf, off, len);
            crc.update(buf, off, len);
        }

        public void finish() throws IOException {
            if (!def.finished()) {
                def.finish();
                while (!def.finished()) {
                    int len = def.deflate(buf, 0, buf.length);
                    if (def.finished() && len <= buf.length - TRAILER_SIZE) {
                        // last deflater buffer. Fit trailer at the end
                        writeTrailer(buf, len);
                        len = len + TRAILER_SIZE;
                        out.write(buf, 0, len);
                        return;
                    }
                    if (len > 0)
                        out.write(buf, 0, len);
                }
                // if we can't fit the trailer at the end of the last
                // deflater buffer, we write it separately
                byte[] trailer = new byte[TRAILER_SIZE];
                writeTrailer(trailer, 0);
                out.write(trailer);
            }
        }

        private void writeTrailer(byte[] buf, int offset) throws IOException {
            writeInt((int)crc.getValue(), buf, offset); // CRC-32 of uncompr. data
            writeInt(def.getTotalIn(), buf, offset + 4); // Number of uncompr. bytes
        }

        private void writeInt(int i, byte[] buf, int offset) throws IOException {
            writeShort(i & 0xffff, buf, offset);
            writeShort((i >> 16) & 0xffff, buf, offset + 2);
        }

        private void writeShort(int s, byte[] buf, int offset) throws IOException {
            buf[offset] = (byte)(s & 0xff);
            buf[offset + 1] = (byte)((s >> 8) & 0xff);
        }

    }

}
