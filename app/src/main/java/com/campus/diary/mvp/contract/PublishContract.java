package com.campus.diary.mvp.contract;

import com.campus.diary.model.ImageItem;

import java.util.List;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public interface PublishContract {

    interface View extends BaseView{
        void gotoMainActivity();
    }

    interface Presenter{
        void sendData(List<ImageItem> items,String content);
    }
}
