package com.liuh.learn.imitateele.adapter;

import android.support.annotation.Nullable;

import com.baidu.mapapi.search.core.PoiInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liuh.learn.imitateele.R;

import java.util.List;

/**
 * Date: 2018/3/6 16:50
 * Description:我的设备的添加的位置选择的Fragment的list的适配器
 */

public class MyDeviceAddSiteListAdapter extends BaseQuickAdapter<PoiInfo, BaseViewHolder> {

    public MyDeviceAddSiteListAdapter(int layoutResId, @Nullable List<PoiInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PoiInfo item) {

        if (helper.getAdapterPosition() == 0) {
            helper.setBackgroundRes(R.id.iv_site_icon, R.drawable.icon_site_current);
        } else {
            helper.setBackgroundRes(R.id.iv_site_icon, R.drawable.icon_site_left);
        }

        helper.setText(R.id.tv_site_name, item.name);
        helper.setText(R.id.tv_site_address_desc, item.address);
    }
}
