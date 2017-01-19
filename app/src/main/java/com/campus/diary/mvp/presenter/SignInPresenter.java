package com.campus.diary.mvp.presenter;

import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiUser;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.SignInContract;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class SignInPresenter implements SignInContract.Presenter {
    private SignInContract.View view;

    public SignInPresenter(SignInContract.View view){
        this.view = view;
    }

    @Override
    public void authority(String username, String password) {
        if (checkInput(username, password)==false){
            return ;
        }
        //上传进度提示
        view.showLoading("正在登陆...");

       signIn(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<User>() {
                @Override
                public void onCompleted() {
                    view.hideLoading();
                }

                @Override
                public void onError(Throwable e) {
                    view.showToast("网络错误!" );
                }

                @Override
                public void onNext(User droiUser) {
                    if (droiUser != null && droiUser.isAuthorized() && !droiUser.isAnonymous()) {
                        view.showToast("登录成功！");
                        view.gotoMainActivity();
                    } else {
                        view.showToast("账号或密码错误，请检查！");
                    }
                }
            });

    }

    public static Observable<User> signIn(final String username, final String password) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(final Subscriber<? super User> subscriber) {
                try {
                    DroiError droiError = new DroiError();
                    User user = DroiUser.login(username, password, User.class, droiError);
                    subscriber.onNext(user);
                } catch (Exception e) {
                    subscriber.onError(e);
                }finally{
                    subscriber.onCompleted();
                }
            }
        });
    }

    protected boolean checkInput(String username, String password){
        if (username.length()<6){
            view.showToast("用户名不能小于6个字符");
            return false;
        }else if (username.contains(" ")){
            view.showToast("用户名不能含有空格");
            return false;
        }else if (password.length()<6){
            view.showToast("密码不能小于6个字符");
            return false;
        }else if (password.contains(" ")){
            view.showToast("密码不能含有空格");
            return false;
        }
        return true;
    }
}
