package com.legendmohe.methoddiff;

import java.util.regex.Pattern;

/**
 * Created by hexinyu on 2018/7/27.
 */
public class IInterface { //from https://github.com/twitter/twitter-text

    // Generated from unicode_regex/unicode_regex_groups.scala, more inclusive than Java's \p{L}\p{M}
    private static final String HASHTAG_LETTERS_AND_MARKS = "\\p{L}\\p{M}" +
            "\\u037f\\u0528-\\u052f\\u08a0-\\u08b2\\u08e4-\\u08ff\\u0978\\u0980\\u0c00\\u0c34\\u0c81\\u0d01" +
            "\\u0ede\\u0edf\\u10c7\\u10cd\\u10fd-\\u10ff\\u16f1-\\u16f8\\u17b4\\u17b5\\u191d\\u191e\\u1ab0-" +
            "\\u1abe\\u1bab-\\u1bad\\u1bba-\\u1bbf\\u1cf3-\\u1cf6\\u1cf8\\u1cf9\\u1de7-\\u1df5\\u2cf2\\u2cf3" +
            "\\u2d27\\u2d2d\\u2d66\\u2d67\\u9fcc\\ua674-\\ua67b\\ua698-\\ua69d\\ua69f\\ua792-\\ua79f\\ua7aa-" +
            "\\ua7ad\\ua7b0\\ua7b1\\ua7f7-\\ua7f9\\ua9e0-\\ua9ef\\ua9fa-\\ua9fe\\uaa7c-\\uaa7f\\uaae0-\\uaaef" +
            "\\uaaf2-\\uaaf6\\uab30-\\uab5a\\uab5c-\\uab5f\\uab64\\uab65\\uf870-\\uf87f\\uf882\\uf884-\\uf89f" +
            "\\uf8b8\\uf8c1-\\uf8d6\\ufa2e\\ufa2f\\ufe27-\\ufe2d\\ud800\\udee0\\ud800\\udf1f\\ud800\\udf50-" +
            "\\ud800\\udf7a\\ud801\\udd00-\\ud801\\udd27\\ud801\\udd30-\\ud801\\udd63\\ud801\\ude00-\\ud801" +
            "\\udf36\\ud801\\udf40-\\ud801\\udf55\\ud801\\udf60-\\ud801\\udf67\\ud802\\udc60-\\ud802\\udc76" +
            "\\ud802\\udc80-\\ud802\\udc9e\\ud802\\udd80-\\ud802\\uddb7\\ud802\\uddbe\\ud802\\uddbf\\ud802" +
            "\\ude80-\\ud802\\ude9c\\ud802\\udec0-\\ud802\\udec7\\ud802\\udec9-\\ud802\\udee6\\ud802\\udf80-" +
            "\\ud802\\udf91\\ud804\\udc7f\\ud804\\udcd0-\\ud804\\udce8\\ud804\\udd00-\\ud804\\udd34\\ud804" +
            "\\udd50-\\ud804\\udd73\\ud804\\udd76\\ud804\\udd80-\\ud804\\uddc4\\ud804\\uddda\\ud804\\ude00-" +
            "\\ud804\\ude11\\ud804\\ude13-\\ud804\\ude37\\ud804\\udeb0-\\ud804\\udeea\\ud804\\udf01-\\ud804" +
            "\\udf03\\ud804\\udf05-\\ud804\\udf0c\\ud804\\udf0f\\ud804\\udf10\\ud804\\udf13-\\ud804\\udf28" +
            "\\ud804\\udf2a-\\ud804\\udf30\\ud804\\udf32\\ud804\\udf33\\ud804\\udf35-\\ud804\\udf39\\ud804" +
            "\\udf3c-\\ud804\\udf44\\ud804\\udf47\\ud804\\udf48\\ud804\\udf4b-\\ud804\\udf4d\\ud804\\udf57" +
            "\\ud804\\udf5d-\\ud804\\udf63\\ud804\\udf66-\\ud804\\udf6c\\ud804\\udf70-\\ud804\\udf74\\ud805" +
            "\\udc80-\\ud805\\udcc5\\ud805\\udcc7\\ud805\\udd80-\\ud805\\uddb5\\ud805\\uddb8-\\ud805\\uddc0" +
            "\\ud805\\ude00-\\ud805\\ude40\\ud805\\ude44\\ud805\\ude80-\\ud805\\udeb7\\ud806\\udca0-\\ud806" +
            "\\udcdf\\ud806\\udcff\\ud806\\udec0-\\ud806\\udef8\\ud808\\udf6f-\\ud808\\udf98\\ud81a\\ude40-" +
            "\\ud81a\\ude5e\\ud81a\\uded0-\\ud81a\\udeed\\ud81a\\udef0-\\ud81a\\udef4\\ud81a\\udf00-\\ud81a" +
            "\\udf36\\ud81a\\udf40-\\ud81a\\udf43\\ud81a\\udf63-\\ud81a\\udf77\\ud81a\\udf7d-\\ud81a\\udf8f" +
            "\\ud81b\\udf00-\\ud81b\\udf44\\ud81b\\udf50-\\ud81b\\udf7e\\ud81b\\udf8f-\\ud81b\\udf9f\\ud82f" +
            "\\udc00-\\ud82f\\udc6a\\ud82f\\udc70-\\ud82f\\udc7c\\ud82f\\udc80-\\ud82f\\udc88\\ud82f\\udc90-" +
            "\\ud82f\\udc99\\ud82f\\udc9d\\ud82f\\udc9e\\ud83a\\udc00-\\ud83a\\udcc4\\ud83a\\udcd0-\\ud83a" +
            "\\udcd6\\ud83b\\ude00-\\ud83b\\ude03\\ud83b\\ude05-\\ud83b\\ude1f\\ud83b\\ude21\\ud83b\\ude22" +
            "\\ud83b\\ude24\\ud83b\\ude27\\ud83b\\ude29-\\ud83b\\ude32\\ud83b\\ude34-\\ud83b\\ude37\\ud83b" +
            "\\ude39\\ud83b\\ude3b\\ud83b\\ude42\\ud83b\\ude47\\ud83b\\ude49\\ud83b\\ude4b\\ud83b\\ude4d-\\ud83b" +
            "\\ude4f\\ud83b\\ude51\\ud83b\\ude52\\ud83b\\ude54\\ud83b\\ude57\\ud83b\\ude59\\ud83b\\ude5b\\ud83b" +
            "\\ude5d\\ud83b\\ude5f\\ud83b\\ude61\\ud83b\\ude62\\ud83b\\ude64\\ud83b\\ude67-\\ud83b\\ude6a\\ud83b" +
            "\\ude6c-\\ud83b\\ude72\\ud83b\\ude74-\\ud83b\\ude77\\ud83b\\ude79-\\ud83b\\ude7c\\ud83b\\ude7e" +
            "\\ud83b\\ude80-\\ud83b\\ude89\\ud83b\\ude8b-\\ud83b\\ude9b\\ud83b\\udea1-\\ud83b\\udea3\\ud83b" +
            "\\udea5-\\ud83b\\udea9\\ud83b\\udeab-\\ud83b\\udebb";

    // Generated from unicode_regex/unicode_regex_groups.scala, more inclusive than Java's \p{Nd}
    private static final String HASHTAG_NUMERALS = "\\p{Nd}" +
            "\\u0de6-\\u0def\\ua9f0-\\ua9f9\\ud804\\udcf0-\\ud804\\udcf9\\ud804\\udd36-\\ud804\\udd3f\\ud804" +
            "\\uddd0-\\ud804\\uddd9\\ud804\\udef0-\\ud804\\udef9\\ud805\\udcd0-\\ud805\\udcd9\\ud805\\ude50-" +
            "\\ud805\\ude59\\ud805\\udec0-\\ud805\\udec9\\ud806\\udce0-\\ud806\\udce9\\ud81a\\ude60-\\ud81a" +
            "\\ude69\\ud81a\\udf50-\\ud81a\\udf59";

    private static final String HASHTAG_SPECIAL_CHARS = "_" + //underscore
            "\\u200c" + // ZERO WIDTH NON-JOINER (ZWNJ)
            "\\u200d" + // ZERO WIDTH JOINER (ZWJ)
            "\\ua67e" + // CYRILLIC KAVYKA
/*            "\\u05be" + // HEBREW PUNCTUATION MAQAF
            "\\u05f3" + // HEBREW PUNCTUATION GERESH
            "\\u05f4" + // HEBREW PUNCTUATION GERSHAYIM
            "\\uff5e" + // FULLWIDTH TILDE
            "\\u301c" + // WAVE DASH*/
            "\\u309b" + // KATAKANA-HIRAGANA VOICED SOUND MARK
            "\\u309c" + // KATAKANA-HIRAGANA SEMI-VOICED SOUND MARK
            "\\u30a0" + // KATAKANA-HIRAGANA DOUBLE HYPHEN
            "\\u30fb";  // KATAKANA MIDDLE DOT
    /*            "\\u3003" + // DITTO MARK
                "\\u0f0b" + // TIBETAN MARK INTERSYLLABIC TSHEG
                "\\u0f0c" + // TIBETAN MARK DELIMITER TSHEG BSTAR
                "\\u00b7";  // MIDDLE DOT*/
//
    private static final String HASHTAG_LETTERS_NUMERALS = HASHTAG_LETTERS_AND_MARKS + HASHTAG_NUMERALS + HASHTAG_SPECIAL_CHARS;
    private static final String HASHTAG_LETTERS_SET = "[" + HASHTAG_LETTERS_AND_MARKS + "]";
    private static final String HASHTAG_LETTERS_NUMERALS_SET_WITH_NO_SPECIAL = "[" + HASHTAG_LETTERS_AND_MARKS + HASHTAG_NUMERALS + "]";
    private static final String HASHTAG_LETTERS_NUMERALS_SET = "[" + HASHTAG_LETTERS_NUMERALS + "]";


    /* Begin public constants */

    public static final Pattern VALID_HASHTAG;
    public static final int VALID_HASHTAG_GROUP_HASH = 1;
    public static final int VALID_HASHTAG_GROUP_TAG = 2;
//    public static final Pattern INVALID_HASHTAG_MATCH_END;

    //     initializing in a static synchronized block, there appears to be thread safety issues with Pattern.compile in android
    static {
        synchronized (IInterface.class) {
//            VALID_HASHTAG = Pattern.compile("(^|[^&" + HASHTAG_LETTERS_NUMERALS + "])(#|\uFF03)(?!\uFE0F|\u20E3)(" + HASHTAG_LETTERS_NUMERALS_SET + "*" + HASHTAG_LETTERS_SET + HASHTAG_LETTERS_NUMERALS_SET + "*)", Pattern.CASE_INSENSITIVE);
            VALID_HASHTAG = Pattern.compile("(#|\uFF03)(?!\uFE0F|\u20E3)(" + HASHTAG_LETTERS_NUMERALS_SET + "*" + HASHTAG_LETTERS_NUMERALS_SET_WITH_NO_SPECIAL + HASHTAG_LETTERS_NUMERALS_SET + "*)", Pattern.CASE_INSENSITIVE);
//            INVALID_HASHTAG_MATCH_END = Pattern.compile("^(?:[#＃]|://)");
        }
    }

    public void run() {
        System.out.print("sdsd");
    }
}
