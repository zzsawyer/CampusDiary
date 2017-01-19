package com.campus.diary.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.campus.diary.R;
import com.droi.sdk.analytics.DroiAnalytics;


/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public abstract class BaseActivity extends FragmentActivity {
    ImageView back_button;
    TextView title_tv;
    Button right_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        DroiAnalytics.onResume(BaseActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DroiAnalytics.onPause(BaseActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void addTitle(int titleId) {
        back_button = (ImageView) findViewById(R.id.back_botton);
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText(titleId);
        right_button = (Button) findViewById(R.id.right_botton);
    }

    public void addTitle(String titleStr) {
        back_button = (ImageView)findViewById(R.id.back_botton);
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText(titleStr);
        right_button = (Button) findViewById(R.id.right_botton);
    }

    public void setBackButton() {
        if (back_button != null) {
            back_button.setVisibility(View.VISIBLE);
            back_button.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }


    public void setrightButton(int buttonId,View.OnClickListener listener) {
        if (right_button != null) {
            right_button.setVisibility(View.VISIBLE);
            right_button.setText(buttonId);
            right_button.setOnClickListener(listener);
        }
    }

    public void setrightButton(String buttonStr,View.OnClickListener listener) {
        if (right_button != null) {
            right_button.setVisibility(View.VISIBLE);
            right_button.setText(buttonStr);
            right_button.setOnClickListener(listener);
        }
    }
}
