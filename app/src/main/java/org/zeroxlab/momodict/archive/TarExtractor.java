package org.zeroxlab.momodict.archive;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TarExtractor implements Extractor {

    @Override
    public DictionaryArchive extract(File outputDir, InputStream inputStream) throws Exception {
        DictionaryArchive archive = new DictionaryArchive();
        archive.setCleanCallback(() -> {
            try {
                if (outputDir.isDirectory()) {
                    FileUtils.deleteDirectory(outputDir);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        TarArchiveInputStream tis = new TarArchiveInputStream(inputStream);
        while (tis.getNextEntry() != null) {
            onEntryFound(outputDir, tis, tis.getCurrentEntry(), archive);
        }

        if (!archive.isSane()) {
            throw new Exception("RealmDictionary is malformed");
        }
        return archive;
    }

    private void onEntryFound(File parent,
                              InputStream is,
                              TarArchiveEntry entry,
                              DictionaryArchive archive)
            throws Exception {

        final String fileName = entry.getName();
        if (entry.isDirectory()) {
            File dir = new File(parent, fileName);
            if (!dir.mkdirs()) {
                throw new Exception("Create dir fail");
            }
        } else {
            // write file
            File out = new File(parent, fileName);
            byte[] buf = new byte[65535];
            FileOutputStream fos = new FileOutputStream(out);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int count;
            while ((count = is.read(buf)) != -1) {
                bos.write(buf, 0, count);
            }
            bos.close();

            // cache file path
            if (fileName.endsWith(".idx")) {
                archive.set(DictionaryArchive.Type.IDX, out.getAbsolutePath());
            } else if (fileName.endsWith(".ifo")) {
                archive.set(DictionaryArchive.Type.IFO, out.getAbsolutePath());
            } else if (fileName.endsWith(".dict.dz") || fileName.endsWith(".dict")) {
                archive.set(DictionaryArchive.Type.DICT, out.getAbsolutePath());
            }
        }
        System.out.println(entry.getName());
    }
}
