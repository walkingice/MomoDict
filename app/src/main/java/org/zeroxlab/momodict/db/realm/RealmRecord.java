package org.zeroxlab.momodict.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRecord extends RealmObject {
    @PrimaryKey
    public String wordStr;
    public int count;
    public Date time;
}
