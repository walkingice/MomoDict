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
    private static final String DIR_PREFIX = "DICT.";
    private static final int RANDOM_BITS = 32;

    public static DictionaryArchive readBzip2File(String cachePath, String path) {
        System.out.println(BZip2Utils.isCompressedFilename(path));
        System.out.println(BZip2Utils.getUncompressedFilename(path));

        // TODO: support .dz file
        if (path.endsWith(".dz")) {
            StringBuilder msg = new StringBuilder();
            msg.append("Haven't support .dz (dictzip) file so far.\n");
            msg.append("please provide '.dict' to instead of '.dict.dz'");
            throw new RuntimeException(msg.toString());
        }

        // to make a random path such as /tmp/DICT.ru9527
        SecureRandom random = new SecureRandom();
        BigInteger integer = new BigInteger(RANDOM_BITS, random);
        String randomText = integer.toString(RANDOM_BITS);
        String dirPath = cachePath + "/" + DIR_PREFIX + randomText;
        File tmpDir = new File(dirPath);
        boolean made = tmpDir.mkdirs();
        if (!made) {
            return null;
        }

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
