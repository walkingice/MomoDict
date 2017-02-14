package org.zeroxlab.momodict.model;

public class Info {

    public String version;
    public String bookName;
    public int wordCount;
    public int syncWordCount;
    public int idxFileSize;
    public int idxOffsetBits;
    public String author;
    public String email;
    public String webSite;
    public String description;
    public String date;
    public String sameTypeSequence;

    public Info() {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("version:" + this.version + "\n");
        sb.append("bookName:" + this.bookName + "\n");
        sb.append("wordCount:" + this.wordCount + "\n");
        sb.append("syncWordCount:" + this.syncWordCount + "\n");
        sb.append("idxFileSize:" + this.idxFileSize + "\n");
        sb.append("idxOffsetBits:" + this.idxOffsetBits + "\n");
        sb.append("author:" + this.author + "\n");
        sb.append("email:" + this.email + "\n");
        sb.append("webSite:" + this.webSite + "\n");
        sb.append("description:" + this.description + "\n");
        sb.append("date:" + this.date + "\n");
        sb.append("sameTypeSequence:" + this.sameTypeSequence + "\n");
        return sb.toString();
    }
}
