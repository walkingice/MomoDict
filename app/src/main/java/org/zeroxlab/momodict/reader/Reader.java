package org.zeroxlab.momodict.reader;

import org.zeroxlab.momodict.archive.DictionaryArchive;
import org.zeroxlab.momodict.model.Info;
import org.zeroxlab.momodict.model.Word;

import java.io.File;
import java.util.List;

public class Reader {

    private DictionaryArchive mArchive;

    public Reader(String cachePath, String path) {
        mArchive = CompressedFileReader.readBzip2File(cachePath, path);
    }

    public void parse() {
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

            if (idxReader.size() != 0) {
                List<Word> words = DictReader.parse(idxReader.getEntries(),
                        mArchive.get(DictionaryArchive.Type.DICT));
                for (int i = 0; i < 5 && i < words.size(); i++) {
                    System.out.println(String.format("Read %d th entry", i));
                    System.out.println(words.get(i));
                    System.out.println("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mArchive.clean();
        }

    }
}
