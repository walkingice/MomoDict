package org.zeroxlab.momodict.reader;

import org.zeroxlab.momodict.archive.Info;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.functions.Func1;
import rx.functions.Func2;

/**
 * A reader to parse .ifo file
 */
public class IfoReader {

    private final Info mInfo;
    private final File mFile;
    private final Map<String, Func2<Info, String, Info>> mMap;

    public IfoReader(File ifoFile) {
        mFile = ifoFile;
        assert mFile != null : "Should give a existing file";
        assert mFile.exists() : "IFO file does not exists:" + ifoFile.getAbsolutePath();

        mInfo = new Info();
        mMap = makeMatchers();
    }

    public Info parse() {
        parseLine("");
        try {
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            String line;
            while ((line = br.readLine()) != null) {
                parseLine(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mInfo;
    }

    public static boolean isSanity(Info info) {
        // Todo: really Info file sanity check
        return true;
    }

    private void parseLine(String line) {
        Pattern pattern = Pattern.compile("(.+=).*");
        Matcher m = pattern.matcher(line);
        if (m.find()) {
            String prefix = m.group(1);
            if (mMap.containsKey(prefix)) {
                mMap.get(prefix).call(mInfo, line);
            }
        }
    }

    private Map<String, Func2<Info, String, Info>> makeMatchers() {
        Map<String, Func2<Info, String, Info>> map = new HashMap<>();
        map.put("bookname=", bookNameMatcher());
        map.put("version=", versionMatcher());
        map.put("author=", authorMatcher());
        map.put("website=", webSiteMatcher());
        map.put("email=", emailMatcher());
        map.put("description=", descriptionMatcher());
        map.put("sametypesequence=", sameTypeSequenceMatcher());
        map.put("wordcount=", wordCountMatcher());
        map.put("syncwordcount=", syncWordCountMatcher());
        map.put("idxfilesize=", idxFileSizeMatcher());
        map.put("idxoffsets=", idxOffsetBitsMatcher());
        return map;
    }

    private Func1<String, String> createMatcher(final String key) {
        return new Func1<String, String>() {
            // ie. build "bookName=XXXX" if key is "bookName"
            final Pattern pattern = Pattern.compile(key + "\\s?=\\s?(.*)");

            public String call(String str) {
                String tailingSpaceRemoved = str.replaceAll("\\s$", "");
                Matcher matcher = pattern.matcher(tailingSpaceRemoved);
                return matcher.find() ? matcher.group(1) : null;
            }
        };
    }

    private Func2<Info, String, Info> bookNameMatcher() {
        final Func1<String, String> matchFunc = createMatcher("bookname");
        return new Func2<Info, String, Info>() {
            @Override
            public Info call(Info info, String str) {
                info.bookName = matchFunc.call(str);
                return info;
            }
        };
    }

    private Func2<Info, String, Info> versionMatcher() {
        final Func1<String, String> matchFunc = createMatcher("version");
        return new Func2<Info, String, Info>() {
            @Override
            public Info call(Info info, String str) {
                info.version = matchFunc.call(str);
                return info;
            }
        };
    }

    private Func2<Info, String, Info> authorMatcher() {
        final Func1<String, String> matchFunc = createMatcher("author");
        return new Func2<Info, String, Info>() {
            @Override
            public Info call(Info info, String str) {
                info.author = matchFunc.call(str);
                return info;
            }
        };
    }

    private Func2<Info, String, Info> webSiteMatcher() {
        final Func1<String, String> matchFunc = createMatcher("website");
        return new Func2<Info, String, Info>() {
            @Override
            public Info call(Info info, String str) {
                info.webSite = matchFunc.call(str);
                return info;
            }
        };
    }

    private Func2<Info, String, Info> emailMatcher() {
        final Func1<String, String> matchFunc = createMatcher("email");
        return new Func2<Info, String, Info>() {
            @Override
            public Info call(Info info, String str) {
                info.email = matchFunc.call(str);
                return info;
            }
        };
    }


    private Func2<Info, String, Info> descriptionMatcher() {
        final Func1<String, String> matchFunc = createMatcher("description");
        return new Func2<Info, String, Info>() {
            @Override
            public Info call(Info info, String str) {
                info.description = matchFunc.call(str);
                return info;
            }
        };
    }

    private Func2<Info, String, Info> sameTypeSequenceMatcher() {
        final Func1<String, String> matchFunc = createMatcher("sametypesequence");
        return new Func2<Info, String, Info>() {
            @Override
            public Info call(Info info, String str) {
                info.sameTypeSequence = matchFunc.call(str);
                return info;
            }
        };
    }

    private Func2<Info, String, Info> wordCountMatcher() {
        final Func1<String, String> matchFunc = createMatcher("wordcount");
        return new Func2<Info, String, Info>() {
            @Override
            public Info call(Info info, String str) {
                String cnt = matchFunc.call(str);
                if (cnt != null) {
                    info.wordCount = Integer.parseInt(cnt);
                }
                return info;
            }
        };
    }

    private Func2<Info, String, Info> syncWordCountMatcher() {
        final Func1<String, String> matchFunc = createMatcher("syncwordcount");
        return new Func2<Info, String, Info>() {
            @Override
            public Info call(Info info, String str) {
                String cnt = matchFunc.call(str);
                if (cnt != null) {
                    info.syncWordCount = Integer.parseInt(cnt);
                }
                return info;
            }
        };
    }

    private Func2<Info, String, Info> idxFileSizeMatcher() {
        final Func1<String, String> matchFunc = createMatcher("idxfilesize");
        return new Func2<Info, String, Info>() {
            @Override
            public Info call(Info info, String str) {
                String cnt = matchFunc.call(str);
                if (cnt != null) {
                    info.idxFileSize = Integer.parseInt(cnt);
                }
                return info;
            }
        };
    }

    private Func2<Info, String, Info> idxOffsetBitsMatcher() {
        final Func1<String, String> matchFunc = createMatcher("idxoffsetbits");
        return new Func2<Info, String, Info>() {
            @Override
            public Info call(Info info, String str) {
                String cnt = matchFunc.call(str);
                if (cnt != null) {
                    info.idxOffsetBits = Integer.parseInt(cnt);
                }
                return info;
            }
        };
    }
}
