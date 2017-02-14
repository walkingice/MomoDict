package org.zeroxlab.momodict.model;

public class Word {
    public IdxEntry entry;
    public String data;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(entry);
        sb.append("\n");
        sb.append(data);
        return sb.toString();
    }
}
