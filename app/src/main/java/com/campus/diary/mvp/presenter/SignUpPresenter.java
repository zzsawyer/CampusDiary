package com.campus.diary.mvp.presenter;

import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiPermission;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.SignUpContract;

import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class SignUpPresenter implements SignUpContract.Presenter {

    private SignUpContract.View view;

    public SignUpPresenter(SignUpContract.View view){
        this.view = view;
    }

    protected boolean checkInput(Map<String, Object> userInfoMap) {
        String inputInfo = userInfoMap.get("mUserName").toString();
        String checkResult = null;
        if (inputInfo.trim().length() == 0) {
            checkResult = "用户名不能为空";
        } else if (inputInfo.length() < 6) {
            checkResult = "用户名太短";
        } else if (inputInfo.length() > 30) {
            checkResult = "用户名太长";
        }  else if (userInfoMap.get("mUserPassword1").toString().length() < 6) {
            checkResult = "密码太短";
        } else if (userInfoMap.get("mUserPassword1").toString().length() > 30) {
            checkResult = "密码太长";
        } else if (!userInfoMap.get("mUserPassword1").toString().equals(userInfoMap.get("mUserPassword2").toString())) {
            checkResult = "两次输入的密码不一致，请重新输入";
        }else if (userInfoMap.get("mNickName").toString().trim().length() == 0) {
            checkResult = "昵称不能为空";
        }
        if (checkResult != null) {
            view.showToast(checkResult);
            return false;
        }
        return true;
    }

    @Override
    public void authority(Map<String, Object> userInfoMap) {
        if (!checkInput(userInfoMap)) {
            return ;
        }
        view.showLoading("正在注册....");
        signUp(userInfoMap)
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
                            view.showToast("注册成功!");
                            view.gotoSignInView();
                        } else {
                            String errString;
                            if (droiError.getCode() == DroiError.USER_ALREADY_EXISTS) {
                                errString = "账户已经存在！";
                            } else {
                                errString = "账户注册失败！";
                            }
                            view.showToast(errString);
                        }
                    }
                });

    }

    public static Observable<DroiError> signUp(final Map<String, Object> userInfoMap) {
        return Observable.create(new Observable.OnSubscribe<DroiError>() {
            @Override
            public void call(final Subscriber<? super DroiError> subscriber) {
                try {
                    User user = new User();
                    user.setUserId(userInfoMap.get("mUserName").toString());
                    user.setPassword(userInfoMap.get("mUserPassword1").toString());
                    user.setNickName(userInfoMap.get("mNickName").toString());
                    DroiPermission permission = new DroiPermission();
                    permission.setPublicReadPermission(true);
                    user.setPermission(permission);
                    DroiError droiError = user.signUp();
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
