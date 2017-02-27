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
    public FileSet extract(File outputDir, InputStream inputStream) throws Exception {
        FileSet archive = new FileSet();
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
            throw new Exception("RealmBook is malformed");
        }
        return archive;
    }

    private void onEntryFound(File parent,
                              InputStream is,
                              TarArchiveEntry entry,
                              FileSet archive)
            throws Exception {

        final String fileName = entry.getName();
        if (entry.isDirectory()) {
            File dir = new File(parent, fileName);
            if (!dir.mkdirs()) {
                throw new Exception("Create dir fail");
            }
        } else {
            // TODO: support .dz file. issue #2
            if (fileName.endsWith(".dz")) {
                StringBuilder msg = new StringBuilder();
                msg.append("Haven't support .dz (dictzip) file so far.\n");
                msg.append("please provide '.dict' to instead of '.dict.dz'");
                throw new RuntimeException(msg.toString());
            }
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
                archive.set(FileSet.Type.IDX, out.getAbsolutePath());
            } else if (fileName.endsWith(".ifo")) {
                archive.set(FileSet.Type.IFO, out.getAbsolutePath());
            } else if (fileName.endsWith(".dict.dz") || fileName.endsWith(".dict")) {
                archive.set(FileSet.Type.DICT, out.getAbsolutePath());
            }
        }
        System.out.println(entry.getName());
    }
}
