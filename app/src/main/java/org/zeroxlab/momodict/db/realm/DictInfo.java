package org.zeroxlab.momodict.db.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DictInfo extends RealmObject {
    @PrimaryKey
    public String bookName;

    public String mVersion;
    public int mWordCount;
    public int mSyncWordCount;
    public int mIdxFileSize;
    public int mIdxOffsetBits;
    public String mAuthor;
    public String mEmail;
    public String mWebsite;
    public String mDescription;
    public String mDate;
}

