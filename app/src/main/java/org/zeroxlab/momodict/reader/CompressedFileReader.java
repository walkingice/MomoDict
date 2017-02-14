package org.zeroxlab.momodict.reader;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2Utils;
import org.zeroxlab.momodict.archive.DictionaryArchive;
import org.zeroxlab.momodict.archive.Extractor;
import org.zeroxlab.momodict.archive.TarExtractor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

public class CompressedFileReader {
    private static final String TMP_PATH = "/tmp";
    private static final String DIR_PREFIX = "DICT.";
    private static final int RANDOM_BITS = 32;

    public static DictionaryArchive readBzip2File(String path) {
        System.out.println(BZip2Utils.isCompressedFilename(path));
        System.out.println(BZip2Utils.getUncompressedFilename(path));

        // to make a random path such as /tmp/DICT.ru9527
        SecureRandom random = new SecureRandom();
        BigInteger integer = new BigInteger(RANDOM_BITS, random);
        String randomText = integer.toString(RANDOM_BITS);
        String dirPath = TMP_PATH + "/" + DIR_PREFIX + randomText;
        File tmpDir = new File(dirPath);
        tmpDir.mkdirs();

        try {
            FileInputStream fis = new FileInputStream(new File(path));
            BufferedInputStream bis = new BufferedInputStream(fis);
            BZip2CompressorInputStream b2is = new BZip2CompressorInputStream(bis);

            Extractor extractor = new TarExtractor();
            DictionaryArchive archive = extractor.extract(tmpDir, b2is);
            if (!archive.isSane()) {
                throw new Exception("Necessary files missing: " + archive.toString());
            }
            fis.close();
            return archive;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
