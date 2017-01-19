package com.campus.diary.mvp.contract;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public interface ChangePwdContract {

    interface View extends BaseView {
    }

    interface Presenter{
        void changePassword(String oldPassword, String newPassword, String newPasswordAgain);

    }
}
