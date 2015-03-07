package com.linxinzhe.android.xinzhesecurity.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linxinzhe.android.xinzhesecurity.R;

/**
 * 自定义的组合控件，仿谷歌系统setting布局控件
 * Created by linxinzhe on 2015/3/5.
 */
public class SettingItemView extends RelativeLayout {

    private CheckBox mStatusCB;
    private TextView mDescTV;
    private TextView mTitleTV;

    private String title;
    private String desc_on;
    private String desc_off;


    private void initView(Context context) {
        View.inflate(context, R.layout.setting_item_view, this);
        mStatusCB = (CheckBox) this.findViewById(R.id.cb_status);
        mDescTV = (TextView) findViewById(R.id.tv_desc);
        mTitleTV = (TextView) findViewById(R.id.tv_title);
    }

    public SettingItemView(Context context) {
        super(context);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

        title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "title");
        desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc_on");
        desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc_off");
        mTitleTV.setText(title);
        setDesc(desc_off);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 检查组合控件是否被选中
     *
     * @return
     */
    public boolean isChecked() {
        return mStatusCB.isChecked();
    }

    /**
     * 设置组合控件状态
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        if (checked) {
            setDesc(desc_on);
        } else {
            setDesc(desc_off);
        }
        mStatusCB.setChecked(checked);
    }

    /**
     * 设置组合控件描述信息
     *
     * @param text
     */
    public void setDesc(String text) {
        mDescTV.setText(text);
    }
}
