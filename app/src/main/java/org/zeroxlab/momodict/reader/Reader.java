package org.zeroxlab.momodict.reader;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;

import org.zeroxlab.momodict.archive.FileSet;
import org.zeroxlab.momodict.archive.Idx;
import org.zeroxlab.momodict.archive.Info;
import org.zeroxlab.momodict.archive.Word;
import org.zeroxlab.momodict.db.room.RoomStore;
import org.zeroxlab.momodict.db.room.RoomStore_Impl;
import org.zeroxlab.momodict.model.Book;
import org.zeroxlab.momodict.model.Entry;
import org.zeroxlab.momodict.model.Store;

import java.io.BufferedInputStream;
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
        final File cachedir = new File(mCacheDir);
        final File outputDir = CompressedFileReader.makeTempDir(cachedir);
        FileSet archive = null;
        try {
            final InputStream fis = new FileInputStream(new File(mFilePath));
            archive = CompressedFileReader.readBzip2File(outputDir, fis);
            // FIXME: should avoid main thread
            final Store store = Room.databaseBuilder(ctx.getApplicationContext(),
                    RoomStore.class,
                    RoomStore_Impl.Companion.getDB_NAME())
                    .allowMainThreadQueries()
                    .build();
            final File ifoFile = new File(archive.get(FileSet.Type.IFO));
            final File idxFile = new File(archive.get(FileSet.Type.IDX));

            // To read ifo file
            final InputStream is = new FileInputStream(ifoFile);
            final Info info = IfoReader.readIfo(is);
            is.close();
            if (!IfoReader.isSanity(info)) {
                throw new RuntimeException("Insanity .ifo file");
            }

            // To read idx file
            if (idxFile == null || !idxFile.exists()) {
                throw new RuntimeException("Should give an existing idx file");
            }
            final InputStream idxIs = new FileInputStream(idxFile);
            final Idx idx = IdxReader.parseIdx(idxIs);
            idxIs.close();

            // To save ifo to database
            Book dict = new Book();
            dict.setBookName(info.getBookName());
            dict.setAuthor(info.getAuthor());
            dict.setWordCount(info.getWordCount());
            dict.setDate(info.getDate());
            store.addBook(dict);

            // To save each words to database
            if (idx.size() != 0) {
                final String dictPath = archive.get(FileSet.Type.DICT);
                final boolean isDict = dictPath.endsWith(".dz");
                InputStream ips = new FileInputStream(dictPath);
                BufferedInputStream bis = new BufferedInputStream(ips);
                InputStream dictIs = DictReader.wrapInputStream(isDict, bis);
                List<Word> words = DictReader.parseDict(idx.getEntries(), dictIs);
                List<Entry> entries = new ArrayList<>();
                for (Word word : words) {
                    Entry entry = new Entry();
                    entry.setSource(info.getBookName());
                    entry.setWordStr(word.getEntry().getWordStr());
                    entry.setData(word.getData());
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
