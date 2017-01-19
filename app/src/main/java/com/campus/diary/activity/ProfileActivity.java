package com.campus.diary.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.campus.diary.R;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.ProfileContract;
import com.campus.diary.mvp.presenter.ProfilePresenter;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiUser;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class ProfileActivity extends BaseActivity implements ProfileContract.View,View.OnClickListener {
    private TextView userNameText,changeNickTv;
    private ImageView headImageView;
    private View selectPic;
    private ProgressDialog progressDialog;
    private ProfilePresenter profileLogic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileLogic = new ProfilePresenter(this);
        initUI();
        profileLogic.getHeadIcon();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initUI() {
        addTitle(R.string.profile);
        setBackButton();
        progressDialog=new ProgressDialog(this);
        userNameText = (TextView) findViewById(R.id.user_id);
        changeNickTv = (TextView) findViewById(R.id.changenick);
        User user = DroiUser.getCurrentUser(User.class);
        if (user != null && user.isAuthorized() && !user.isAnonymous()) {
            if(user.getNickName() != null)
            changeNickTv.setText(user.getNickName());
        }

        headImageView = (ImageView) findViewById(R.id.head_pic);
        findViewById(R.id.head).setOnClickListener(this);
        findViewById(R.id.profile_logout).setOnClickListener(this);
        findViewById(R.id.change_password).setOnClickListener(this);
        findViewById(R.id.change_nickname).setOnClickListener(this);
        Button btn_take_photo = (Button) findViewById(R.id.btn_take_photo);
        Button btn_pick_photo = (Button) findViewById(R.id.btn_pick_photo);
        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        selectPic = findViewById(R.id.select_pic);
        btn_cancel.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head:
                selectPic.setVisibility(View.VISIBLE);
                break;
            case R.id.profile_logout:
                DroiAnalytics.onEvent(this, "logout");
                profileLogic.logout();
                break;
            case R.id.change_password:
                Intent changePasswordIntent = new Intent(this, ChangePasswordActivity.class);
                startActivity(changePasswordIntent);
                break;
            case R.id.btn_take_photo:
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_pick_photo:
                try {
                    Intent intent;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                    } else {
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                    }
                    intent.setType("image/*");
                    startActivityForResult(intent, 2);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.change_nickname:
                FragmentManager manager = getSupportFragmentManager();
                CustomDialogFragment dialogFragment = new CustomDialogFragment();
                dialogFragment.show(manager, "custom");
                break;
            case R.id.btn_cancel:
                hidePopMenu();
                break;
            default:
                break;
        }
    }

    public void hidePopMenu() {
        selectPic.setVisibility(View.GONE);
    }

    @Override
    public void refreshProfile(Bitmap bitmap,String userName) {
        headImageView.setImageBitmap(bitmap);
        userNameText.setText(userName);
    }

    @Override
    public void refreshNickname(String nickname) {
        changeNickTv.setText(nickname);
    }

    @Override
    public void finishActivity(){
        finish();
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        hidePopMenu();
        profileLogic.uploadHeadIcon(this,resultCode,data);
    }

    @Override
    public void showToast(String result) {
        Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToastByResID(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void showLoading(String msg) {
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.dismiss();
    }

    @SuppressLint("ValidFragment")
    class CustomDialogFragment extends DialogFragment {

        private ImageView iv_quxiao_popup;//取消按钮
        private ImageView iv_write_popup; //确认按钮
        private EditText et_comment_popup;//评论内容
        private LinearLayout ll_background_dialog;//容器


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //这句代码的意思是让dialogFragment弹出时沾满全屏
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_DialogWhenLarge_NoActionBar);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.pop_nickname, null);
            //让DialogFragment的背景为透明
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            initView(view);
            initEvent();
            return view;
        }

        //初始化view
        private void initView(View view) {
            iv_quxiao_popup = (ImageView) view.findViewById(R.id.cancel_popup);
            iv_write_popup = (ImageView) view.findViewById(R.id.commit_popup);
            et_comment_popup = (EditText) view.findViewById(R.id.nickname_popup);
            ll_background_dialog = (LinearLayout) view.findViewById(R.id.ll_background_dialog);
        }

        private void initEvent(){
            //取消
            iv_quxiao_popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            //确认
            iv_write_popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = et_comment_popup.getText().toString();
                    if(TextUtils.isEmpty(name)){
                        showToast("昵称不能为空");
                        return;
                    }
                    profileLogic.updateNickname(name);
                    dismiss();
                }
            });
            ll_background_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }
}
