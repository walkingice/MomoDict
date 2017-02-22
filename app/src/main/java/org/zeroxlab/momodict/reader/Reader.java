package org.zeroxlab.momodict.reader;

import android.content.Context;

import org.zeroxlab.momodict.archive.DictionaryArchive;
import org.zeroxlab.momodict.archive.Info;
import org.zeroxlab.momodict.archive.Word;
import org.zeroxlab.momodict.db.realm.RealmStore;
import org.zeroxlab.momodict.model.Book;
import org.zeroxlab.momodict.model.Entry;
import org.zeroxlab.momodict.model.Store;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
            Book dict = new Book();
            dict.bookName = info.bookName;
            dict.author = info.author;
            dict.wordCount = info.wordCount;
            dict.date = info.date;
            store.addBook(dict);

            if (idxReader.size() != 0) {
                List<Word> words = DictReader.parse(idxReader.getEntries(),
                        mArchive.get(DictionaryArchive.Type.DICT));
                List<Entry> entries = new ArrayList<>();
                for (Word word : words) {
                    Entry entry = new Entry();
                    entry.source = info.bookName;
                    entry.wordStr = word.entry.wordStr;
                    entry.data = word.data;
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
