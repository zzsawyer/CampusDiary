package com.campus.diary.mvp.presenter;

import com.campus.diary.model.CircleItem;
import com.campus.diary.model.ImageItem;
import com.campus.diary.model.PhotoInfo;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.PublishContract;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiPermission;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Allen Zeng on 2016/12/24.
 * Email:
 */
public class PublishPresenter implements PublishContract.Presenter {

    private PublishContract.View view;
    public PublishPresenter(PublishContract.View view){
        this.view = view;
    }


    @Override
    public void sendData(List<ImageItem> items,String content) {
        view.showLoading("正在发送...");
        if(content == null){
            content = "";
        }
        createCircleItem(items,content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DroiError>() {
                    @Override
                    public void onCompleted() {
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showToast("网络错误!" );
                    }

                    @Override
                    public void onNext(DroiError droiError) {
                        if (droiError.isOk()) {
                            view.gotoMainActivity();
                        } else {
                            view.showToast("发送失败，请检查网络！");
                        }
                    }
                });
    }


    public static List<PhotoInfo> createPhotos(List<ImageItem> items) {
            List<PhotoInfo> photos = new ArrayList<PhotoInfo>();
            for(ImageItem item:items) {
                DroiPermission permission = new DroiPermission();
                permission.setPublicReadPermission(true);
                permission.setPublicWritePermission(false);
                DroiFile file = new DroiFile(new File(item.sourcePath));
                file.setPermission(permission);
                PhotoInfo p6 = new PhotoInfo();
                p6.setIcon(file);
                p6.setPermission(permission);
                photos.add(p6);
            }
            return photos;
    }

    public static Observable<DroiError> createCircleItem(final List<ImageItem> items,final String content) {
        return Observable.create(new Observable.OnSubscribe<DroiError>() {
            @Override
            public void call(final Subscriber<? super DroiError> subscriber) {
                try {
                    CircleItem data = new CircleItem();
                    data.setContent(content);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateStr = sdf.format(Core.getTimestamp());
                    data.setCreateTime(dateStr);
                    data.setUser(User.getCurrentUser(User.class));
                    data.setType("2");
                    data.setPhotos(createPhotos(items));
                    DroiPermission permission = new DroiPermission();
                    permission.setPublicReadPermission(true);
                    permission.setPublicWritePermission(false);
                    data.setPermission(permission);
                    DroiError droiError = data.save();
                    subscriber.onNext(droiError);
                } catch (Exception e) {
                    subscriber.onError(e);
                }finally{
                    subscriber.onCompleted();
                }
            }
        });
    }
}
