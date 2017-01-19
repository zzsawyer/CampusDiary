package com.campus.diary.mvp.contract;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public interface ProfileContract {

    interface View extends BaseView{
        void showToastByResID(int strId);
        void refreshProfile(Bitmap bitmap, String userName);
        void refreshNickname(String nickname);
        void finishActivity();
    }

    interface Presenter{
        void uploadHeadIcon(Context context,int resultCode, Intent data);
        void logout();
        void getHeadIcon();
        void updateNickname(String name);
    }
}
