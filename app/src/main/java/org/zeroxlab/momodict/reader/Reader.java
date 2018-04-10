package org.zeroxlab.momodict.reader;

import android.content.Context;
import android.support.annotation.NonNull;

import org.zeroxlab.momodict.archive.FileSet;
import org.zeroxlab.momodict.archive.Info;
import org.zeroxlab.momodict.archive.Word;
import org.zeroxlab.momodict.db.realm.RealmStore;
import org.zeroxlab.momodict.model.Book;
import org.zeroxlab.momodict.model.Entry;
import org.zeroxlab.momodict.model.Store;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to extract compressed file, to read and to save into Database.
 */
public class Reader {

    private final String mCacheDir;
    private final String mFilePath;

    /**
     * Constructor
     *
     * @param cacheDirPath       A string as path of cache directory. Extracted files will be placed
     *                           here.
     * @param compressedFilePath A string as path of a compressed file which will be parsed.
     */
    public Reader(@NonNull String cacheDirPath, @NonNull String compressedFilePath) {
        mCacheDir = cacheDirPath;
        mFilePath = compressedFilePath;
    }

    /**
     * To read file and save into database.
     *
     * @param ctx Context instance
     */
    public void parse(@NonNull Context ctx) {
        // extract file
        final FileSet archive = CompressedFileReader.readBzip2File(mCacheDir, mFilePath);
        try {
            final Store store = new RealmStore(ctx);
            final File ifoFile = new File(archive.get(FileSet.Type.IFO));
            final File idxFile = new File(archive.get(FileSet.Type.IDX));

            // To read ifo file
            final InputStream is = new FileInputStream(ifoFile);
            final Info info = IfoReader.Companion.read(is);
            is.close();
            if (!IfoReader.Companion.isSanity(info)) {
                throw new RuntimeException("Insanity .ifo file");
            }

            // To read idx file
            if (idxFile == null || !idxFile.exists()) {
                throw new RuntimeException("Should give an existing idx file");
            }
            final InputStream idxIs = new FileInputStream(idxFile);
            final IdxReader idxReader = new IdxReader();
            idxReader.parse(idxIs);

            // To save ifo to database
            Book dict = new Book();
            dict.bookName = info.getBookName();
            dict.author = info.getAuthor();
            dict.wordCount = info.getWordCount();
            dict.date = info.getDate();
            store.addBook(dict);

            // To save each words to database
            if (idxReader.size() != 0) {
                List<Word> words = DictReader.parse(idxReader.getEntries(),
                        archive.get(FileSet.Type.DICT));
                List<Entry> entries = new ArrayList<>();
                for (Word word : words) {
                    Entry entry = new Entry();
                    entry.source = info.getBookName();
                    entry.wordStr = word.entry.getWordStr();
                    entry.data = word.data;
                    entries.add(entry);
                }
                store.addEntries(entries);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (archive != null) {
                archive.clean();
            }
        }
    }
}
