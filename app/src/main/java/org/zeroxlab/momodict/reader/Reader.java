package org.zeroxlab.momodict.reader;

import org.zeroxlab.momodict.archive.DictionaryArchive;
import org.zeroxlab.momodict.db.realm.Dictionary;
import org.zeroxlab.momodict.db.realm.WordEntry;
import org.zeroxlab.momodict.model.Info;
import org.zeroxlab.momodict.model.Word;

import java.io.File;
import java.util.List;

import io.realm.Realm;

public class Reader {

    private DictionaryArchive mArchive;

    public Reader(String cachePath, String path) {
        mArchive = CompressedFileReader.readBzip2File(cachePath, path);
    }

    public void parse() {
        Realm realm = Realm.getDefaultInstance();
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

            realm.beginTransaction();
            Dictionary dictionary = realm.createObject(Dictionary.class);
            dictionary.name = info.bookName;
            if (idxReader.size() != 0) {
                List<Word> words = DictReader.parse(idxReader.getEntries(),
                        mArchive.get(DictionaryArchive.Type.DICT));
                int max = 1000;
                for (int i = 0; i < max && i < words.size(); i++) {
                    System.out.println(String.format("Read %d th entry", i));
                    System.out.println(words.get(i));
                    System.out.println("");
                    WordEntry entry = realm.createObject(WordEntry.class);
                    entry.wordStr = words.get(i).entry.wordStr;
                    entry.data = words.get(i).data;
                    dictionary.words.add(entry);
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
            mArchive.clean();
        }

    }
}
