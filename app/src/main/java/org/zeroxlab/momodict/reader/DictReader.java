package org.zeroxlab.momodict.reader;

import org.zeroxlab.momodict.archive.IdxEntry;
import org.zeroxlab.momodict.archive.Word;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class DictReader {
    public static List<Word> parse(List<IdxEntry> entries, String dictPath) {
        List<Word> words = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(dictPath);
            BufferedInputStream bis = new BufferedInputStream(is);
            InputStream stream = dictPath.endsWith(".dz")
                    ? new GZIPInputStream(bis)
                    : bis;
            for (IdxEntry entry : entries) {
                Word word = new Word();
                byte[] data = new byte[entry.getWordDataSize()];
                stream.read(data);
                word.data = new String(data);
                word.entry = entry;
                words.add(word);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
    }

    public static void read(IdxEntry entry, File dict) {
        try {
            RandomAccessFile rf = new RandomAccessFile(dict, "r");
            byte[] data = new byte[entry.getWordDataSize()];
            rf.seek(entry.getWordDataOffset());
            rf.read(data, 0, entry.getWordDataSize());
            System.out.println(new String(data, "UTF-8"));
            rf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
