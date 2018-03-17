package com.liuh.learn.imitateele.utils;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by huan on 2018/3/12.
 * 汉语转拼音的一个工具类
 */

public class PinyinUtil {

    public static String getPinyin(String str) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);//大写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);//不需要音调

        StringBuilder sb = new StringBuilder();

        char[] charArray = str.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (Character.isWhitespace(c)) {
                continue;
            }

            if (c > 128 || c < -127) {
                //可能是汉字
                try {
                    String s = PinyinHelper.toHanyuPinyinStringArray(c, format)[0];
                    sb.append(s);
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }

            } else {
                sb.append(c);
            }

        }
        return sb.toString();
    }
}
