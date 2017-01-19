package com.campus.diary.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.feedback.DroiFeedback;
import com.droi.sdk.selfupdate.DroiUpdate;
import com.campus.diary.R;
import com.campus.diary.model.User;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.droi.sdk.core.Core.getActivity;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class SelfActivity extends BaseActivity implements View.OnClickListener {
    public static final int REQUEST_LOGIN = 1001;

    private ImageView mUserAvatar;
    private TextView mUserName;

    private View mUserInfoItem;
    private View mUpdateItem;
    private View mFeedBackItem;
    private View mAboutItem;

    private String mAvatarPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self);
        addTitle("我的");
        setBackButton();
        mUserAvatar = (ImageView) findViewById(R.id.avatar);
        mUserName = (TextView) findViewById(R.id.username);

        mUserInfoItem = findViewById(R.id.userinfo_item);
        mUserInfoItem.setOnClickListener(this);

        mUpdateItem = findViewById(R.id.update_item);
        mUpdateItem.setOnClickListener(this);

        mFeedBackItem = findViewById(R.id.feedback_item);
        mFeedBackItem.setOnClickListener(this);

        mAboutItem = findViewById(R.id.about_item);
        mAboutItem.setOnClickListener(this);

        mUserAvatar.setOnClickListener(this);
        mUserName.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        onShowView();
    }

    private void onShowView() {
        User user = DroiUser.getCurrentUser(User.class);
        if (user != null && !user.isAnonymous()) {
            String userName = user.getNickName();

            if (user.getHeadIcon() != null) {
                user.getHeadIcon().getInBackground(new DroiCallback<byte[]>() {
                    @Override
                    public void result(byte[] bytes, DroiError error) {
                        if (error.isOk()) {
                            if (bytes == null) {
                                Log.i(TAG, "bytes == null");
                            } else {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                mUserAvatar.setImageBitmap(bitmap);
                            }
                        }
                    }
                }, null);
            } else{
                mUserAvatar.setImageResource(R.drawable.default_account_icon);
            }

            if (!TextUtils.isEmpty(userName)) {
                mUserName.setText(userName);
            }

        } else {
            mUserName.setText("点击登录");
            mUserAvatar.setImageResource(R.drawable.default_account_icon);
        }
    }

    // login or show userinfo
    private void showAccountDetailOrLogin() {
        User user = DroiUser.getCurrentUser(User.class);
        if (user != null && user.isAuthorized() && !user.isAnonymous()) {
            showAccountDetail();
        } else {
            showLogin();
        }
    }

    private void showAccountDetail() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }

    private void showLogin() {
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }


    private void showAboutUs() {
        Intent intent = new Intent(SelfActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    private void showFeedBack() {
        User user = DroiUser.getCurrentUser(User.class);
        if (user == null || user.isAnonymous()) {
            showLogin();
        } else {
            DroiFeedback.setUserId(user.getObjectId());
            DroiFeedback.callFeedback(SelfActivity.this);
        }
    }

    private void manualUpdate() {
        DroiUpdate.manualUpdate(SelfActivity.this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                showAccountDetailOrLogin();
                break;

            case R.id.update_item:
                manualUpdate();
                break;

            case R.id.feedback_item:
                showFeedBack();
                break;

            case R.id.about_item:
                showAboutUs();
                break;
        }
    }
}
