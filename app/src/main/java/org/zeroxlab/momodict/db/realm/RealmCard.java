package org.zeroxlab.momodict.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmCard extends RealmObject {
    @PrimaryKey
    public String wordStr;
    public Date time;
    public String note;
}
