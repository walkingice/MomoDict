package org.zeroxlab.momodict.reader;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.zeroxlab.momodict.archive.FileSet;
import org.zeroxlab.momodict.archive.Extractor;
import org.zeroxlab.momodict.archive.TarExtractor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * A class to deal with compressed file.
 */
public class CompressedFileReader {

    // Prefix for directory which contains extracted files
    private static final String DIR_PREFIX = "DICT.";

    // to generate random string for directory which contains extracted files
    private static final int RANDOM_BITS = 32;

    /**
     * To extract tar.bz2 file to cache directory.
     * @param outputDir a parent directory. Any cache directory will be under this.
     * @param inputFile the compressed file to be extracted.
     * @return
     */
    public static FileSet readBzip2File(String outputDir, String inputFile) {

        // to make a random path such as /tmp/DICT.ru9527
        SecureRandom random = new SecureRandom();
        BigInteger integer = new BigInteger(RANDOM_BITS, random);
        String randomText = integer.toString(RANDOM_BITS);
        String dirPath = outputDir + "/" + DIR_PREFIX + randomText;
        File tmpDir = new File(dirPath);
        boolean made = tmpDir.mkdirs();
        if (!made) {
            throw new RuntimeException("Cannot create directory: " + tmpDir);
        }

        try {
            // extract bz2 file
            FileInputStream fis = new FileInputStream(new File(inputFile));
            BufferedInputStream bis = new BufferedInputStream(fis);
            BZip2CompressorInputStream b2is = new BZip2CompressorInputStream(bis);

            // extract tar file
            Extractor extractor = new TarExtractor();
            FileSet archive = extractor.extract(tmpDir, b2is);
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
