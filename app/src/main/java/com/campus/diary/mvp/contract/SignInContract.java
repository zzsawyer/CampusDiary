package com.campus.diary.mvp.contract;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public interface SignInContract {

    interface View extends BaseView {
        void gotoMainActivity();
    }

    interface Presenter{
        void authority(String userName, String password);
    }
}
