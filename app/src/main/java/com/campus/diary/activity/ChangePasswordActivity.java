package com.campus.diary.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.campus.diary.R;
import com.campus.diary.mvp.contract.ChangePwdContract;
import com.campus.diary.mvp.presenter.ChangePwdPresenter;

import static com.droi.sdk.core.Core.getActivity;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class ChangePasswordActivity extends BaseActivity implements ChangePwdContract.View{
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText retypeNewPasswordEditText;
    private Button changePasswordButton;
    private Context mContext;
    private ProgressDialog progressDialog;
    private ChangePwdContract.Presenter pwdLogic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mContext = this;
        pwdLogic = new ChangePwdPresenter(this);
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initUI() {
        addTitle(R.string.change_password);
        setBackButton();
        progressDialog=new ProgressDialog(ChangePasswordActivity.this);
        oldPasswordEditText = (EditText) findViewById(R.id.old_password);
        newPasswordEditText = (EditText) findViewById(R.id.new_password);
        retypeNewPasswordEditText = (EditText) findViewById(R.id.retype_new_password);
        changePasswordButton = (Button) findViewById(R.id.change_password);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();
            }
        });
    }

    private void attemptChangePassword() {
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String retypeNewPassword = retypeNewPasswordEditText.getText().toString();
        pwdLogic.changePassword(oldPassword,newPassword,retypeNewPassword);
    }



    @Override
    public void showLoading(String msg) {
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @Override
    public void showToast(String result) {
        Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoading() {
        progressDialog.dismiss();
    }

}
