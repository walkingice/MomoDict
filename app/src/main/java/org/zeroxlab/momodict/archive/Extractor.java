package org.zeroxlab.momodict.archive;

import java.io.File;
import java.io.InputStream;

public interface Extractor {
    // TODO: proper exception helps user understands what happened
    FileSet extract(File outputDir,
                    InputStream inputStream) throws Exception;
}
