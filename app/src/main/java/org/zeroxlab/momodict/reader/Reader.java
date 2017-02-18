package org.zeroxlab.momodict.reader;

import android.content.Context;

import org.zeroxlab.momodict.archive.DictionaryArchive;
import org.zeroxlab.momodict.db.Store;
import org.zeroxlab.momodict.db.realm.RealmDictionary;
import org.zeroxlab.momodict.db.realm.RealmEntry;
import org.zeroxlab.momodict.archive.Info;
import org.zeroxlab.momodict.archive.Word;
import org.zeroxlab.momodict.db.realm.RealmStore;
import org.zeroxlab.momodict.model.Dictionary;
import org.zeroxlab.momodict.model.Entry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class Reader {

    private DictionaryArchive mArchive;

    public Reader(String cachePath, String path) {
        mArchive = CompressedFileReader.readBzip2File(cachePath, path);
    }

    public void parse(Context ctx) {
        try {
            File ifoFile = new File(mArchive.get(DictionaryArchive.Type.IFO));
            File idxFile = new File(mArchive.get(DictionaryArchive.Type.IDX));
            IfoReader ifoReader = new IfoReader(ifoFile);
            Info info = ifoReader.parse();
            if (!IfoReader.isSanity(info)) {
                throw new RuntimeException("Insanity .ifo file");
            }

            IdxReader idxReader = new IdxReader(idxFile);
            idxReader.parse();
            System.out.println("");
            if (idxReader.size() > 0) {
                System.out.println("First:" + idxReader.get(0));
                System.out.println("Last:" + idxReader.get(idxReader.size() - 1));
            }

            Store store = new RealmStore(ctx);
            Dictionary dict = new Dictionary();
            dict.bookName = info.bookName;
            dict.author = info.author;
            dict.wordCount = info.wordCount;
            dict.date = info.date;
            store.addDictionary(dict);

            if (idxReader.size() != 0) {
                List<Word> words = DictReader.parse(idxReader.getEntries(),
                        mArchive.get(DictionaryArchive.Type.DICT));
                List<Entry> entries = new ArrayList<>();
                // FIXME
                int max = 1000;
                for (int i = 0; i < max && i < words.size(); i++) {
                    System.out.println(String.format("Read %d th entry", i));
                    System.out.println(words.get(i));
                    System.out.println("");

                    Entry entry = new Entry();
                    entry.source = info.bookName;
                    entry.wordStr = words.get(i).entry.wordStr;
                    entry.data = words.get(i).data;
                    entries.add(entry);
                }
                store.addEntries(entries);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mArchive.clean();
        }

    }
}
