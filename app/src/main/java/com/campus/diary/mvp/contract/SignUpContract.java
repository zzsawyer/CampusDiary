package com.campus.diary.mvp.contract;

import java.util.Map;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public interface SignUpContract {

    interface View extends BaseView{
        Map<String, Object> getSignUpInfo();
        void gotoSignInView();
    }

    interface Presenter{
        void authority(Map<String, Object> userInfoMap);
    }
}
