package com.liuh.learn.imitateele.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liuh.learn.imitateele.R;
import com.liuh.learn.imitateele.model.ATestGoodMan;

import java.util.List;

/**
 * Created by huan on 2018/3/12.
 */

public class CityListAdapter extends BaseQuickAdapter<ATestGoodMan, BaseViewHolder> {

    public CityListAdapter(@Nullable List<ATestGoodMan> data) {
        super(R.layout.item_city, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ATestGoodMan item) {
        //比较当前项的拼音首字母和上一项的拼音首字母，如果不相同则显示索引
        String preFirstLetter = null;
        String curFirstLetter = item.getPinyin().charAt(0) + "";
        if (helper.getAdapterPosition() != 0) {
            preFirstLetter = getItem(helper.getAdapterPosition() - 1).getPinyin().charAt(0) + "";
        }

        if (!TextUtils.equals(curFirstLetter, preFirstLetter)) {
            helper.setGone(R.id.tv_city_index, true);
            helper.setText(R.id.tv_city_index, curFirstLetter);
        } else {
            helper.setGone(R.id.tv_city_index, false);
        }
        helper.setText(R.id.tv_city, item.getName());

    }
}
