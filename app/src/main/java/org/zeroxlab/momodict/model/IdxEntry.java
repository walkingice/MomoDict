package org.zeroxlab.momodict.model;

public class IdxEntry {
    public String wordStr;
    public int wordDataOffset;
    public int wordDataSize;

    public IdxEntry(String str, int offset, int size) {
        wordStr = str;
        wordDataOffset = offset;
        wordDataSize = size;
    }

    public String toString() {
        return String.format("%s / %d / %d", wordStr, wordDataOffset, wordDataSize);
    }
}
