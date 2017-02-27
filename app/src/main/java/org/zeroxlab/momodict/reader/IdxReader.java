package org.zeroxlab.momodict.reader;

import org.zeroxlab.momodict.archive.IdxEntry;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *  A reader to parse .idx file
 */
public class IdxReader {
    private static final int NULL_TERMINATED_LENGTH = 1; // length of '\0'
    private static final int OFFSET_LENGTH = 4;
    private static final int SIZE_LENGTH = 4;
    private static final int BUFFER_SIZE = 1024;

    private File mFile;
    private long mMaxSize;

    private List<IdxEntry> mEntries = new ArrayList<>();

    public IdxReader(File idxFile) {
        mFile = idxFile;
        assert mFile != null : "Should give a existing file";
        assert mFile.exists() : "Idx file does not exists:" + idxFile.getAbsolutePath();

        mMaxSize = mFile.length();
    }

    public IdxEntry get(int idx) {
        return mEntries.get(idx);
    }

    public List<IdxEntry> getEntries() {
        return mEntries;
    }

    public int size() {
        return mEntries.size();
    }

    public void parse() {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = new BufferedInputStream(new FileInputStream(mFile));
            int readCnt = 0;
            while ((readCnt = is.read(buffer)) != -1) {
                baos.write(buffer, 0, readCnt);
                byte[] data = baos.toByteArray();
                int fresh = analysis(data);
                baos.reset();
                baos.write(data, fresh, data.length - fresh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Total:" + mEntries.size());
    }

    private int analysis(byte[] data) {
        // an index to indicate the last-parsed-byte + 1
        int idxFresh = 0;

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final int max = data.length - OFFSET_LENGTH - SIZE_LENGTH;
        for (int i = 0; i < max; i++) {
            if (data[i] == '\0') {
                baos.write(data, idxFresh, i - idxFresh);
                String str = baos.toString();

                i += NULL_TERMINATED_LENGTH; // skip '\0'
                baos.reset();
                baos.write(data, i, OFFSET_LENGTH);
                byte[] offsetArray = baos.toByteArray();
                int offset = ByteBuffer.wrap(offsetArray).getInt();
                i += OFFSET_LENGTH; // 64 bits = 8 bytes
                baos.reset();

                baos.write(data, i, SIZE_LENGTH);
                byte[] sizeArray = baos.toByteArray();
                int size = ByteBuffer.wrap(sizeArray).getInt();
                baos.reset();

                i += SIZE_LENGTH;
                idxFresh = i;

                mEntries.add(new IdxEntry(str, offset, size));
            }
        }

        return idxFresh;
    }
}
