package org.zeroxlab.momodict.db.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Dictionary extends RealmObject {
    public String name;
    public RealmList<WordEntry> words = new RealmList<>();
}
