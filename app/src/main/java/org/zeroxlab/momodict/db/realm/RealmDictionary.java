package org.zeroxlab.momodict.db.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmDictionary extends RealmObject {
    @PrimaryKey
    public String bookName;

    public String version;
    public int wordCount;
    public int syncWordCount;
    public String author;
    public String email;
    public String website;
    public String description;
    public String date;

    public RealmList<RealmEntry> words = new RealmList<>();
}
