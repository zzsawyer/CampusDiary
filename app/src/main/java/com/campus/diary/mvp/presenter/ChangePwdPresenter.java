package com.campus.diary.mvp.presenter;

import com.campus.diary.mvp.contract.ChangePwdContract;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiUser;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class ChangePwdPresenter implements ChangePwdContract.Presenter {
    private ChangePwdContract.View view;

    public ChangePwdPresenter(ChangePwdContract.View view){
        this.view = view;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String newPasswordAgain) {
        if (checkInput(oldPassword, newPassword,newPasswordAgain)==false){
            return ;
        }
        //上传进度提示
        view.showLoading("修改中...");

        changeUserPassword(oldPassword, newPassword)
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
                        view.showToast("修改成功!");
                    } else {
                        String errString;
                        if (droiError.getCode() == DroiError.USER_PASSWORD_INCORRECT) {
                            errString = "原密码不正确！";
                        } else {
                            errString = "修改失败！";
                        }
                        view.showToast(errString);
                    }
                }
            });

    }

    public static Observable<DroiError> changeUserPassword(final String oldPassword, final String newPassword) {
        return Observable.create(new Observable.OnSubscribe<DroiError>() {
            @Override
            public void call(final Subscriber<? super DroiError> subscriber) {
                try {
                    DroiUser myUser = DroiUser.getCurrentUser();
                    if (myUser != null && myUser.isAuthorized() && !myUser.isAnonymous()) {
                        DroiError droiError = myUser.changePassword(oldPassword, newPassword);
                        subscriber.onNext(droiError);
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }finally{
                    subscriber.onCompleted();
                }
            }
        });
    }

    protected boolean checkInput(String oldPassword, String newPassword, String newPasswordAgain){
        if (oldPassword.length()<6){
            view.showToast("原密码不能小于6个字符");
            return false;
        }else if (newPassword.length()<6){
            view.showToast("设置密码不能小于6个字符");
            return false;
        }else if (newPassword.contains(" ") || oldPassword.contains(" ")){
            view.showToast("密码不能含有空格");
            return false;
        } else if (newPassword.equals(oldPassword)) {
            view.showToast("设置密码不能与原密码相同");
            return false;
        }else if (!newPassword.equals(newPasswordAgain)) {
            view.showToast("两次输入的新密码不一致");
            return false;
        }
        return true;
    }
}
