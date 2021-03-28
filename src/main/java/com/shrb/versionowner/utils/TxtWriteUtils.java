package com.shrb.versionowner.utils;

/**
 * txt文件写工具类
 */
public class TxtWriteUtils {
    /**
     * 固定输出内容的长度，不足的部分填充
     * @param str
     * @param length
     * @return
     */
    public static String appentStr4Length(String str, int length) throws Exception {
        if (str == null) {
            str = "";
        }
        int strLen = 0;//计算原字符串所占长度,规定中文占两个,其他占一个
        for (int i = 0; i < str.length(); i++) {
            if (isChinese(str.charAt(i))) {
                strLen = strLen + 2;
            } else {
                strLen = strLen + 1;
            }
        }
        if (strLen >= length) {
            return str;
        }
        int remain = length - strLen;//计算所需补充空格长度
        for (int i = 0; i < remain; i++) {
            str = str + " ";
        }

        return str;
    }

    /**
     * 判断中文和其他符号
     * @param c
     * @return
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

}
