package com.campus.diary.activity;

import android.os.Bundle;

import com.campus.diary.R;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class AboutActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        addTitle("关于我们");
        setBackButton();
    }
}
