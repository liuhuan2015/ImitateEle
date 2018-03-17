package com.liuh.learn.imitateele.model;

import android.support.annotation.NonNull;

import com.liuh.learn.imitateele.utils.PinyinUtil;

/**
 * Created by huan on 2018/3/12.
 */

public class ATestGoodMan implements Comparable<ATestGoodMan> {

    private String name;
    private String pinyin;

    public ATestGoodMan(String name) {
        this.name = name;
        this.pinyin = PinyinUtil.getPinyin(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public int compareTo(@NonNull ATestGoodMan aTestGoodMan) {
        return this.pinyin.compareTo(aTestGoodMan.getPinyin());
    }
}
