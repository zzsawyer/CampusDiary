package com.campus.diary.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.campus.diary.R;
import com.campus.diary.mvp.contract.SignInContract;
import com.campus.diary.mvp.presenter.SignInPresenter;

import static com.droi.sdk.core.Core.getActivity;


/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class SignInActivity extends BaseActivity implements SignInContract.View {
    private SignInContract.Presenter signInLogic;
    private EditText username;
    private EditText password;
    private TextView gotoRegister;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signInLogic=new SignInPresenter(SignInActivity.this);
        username=(EditText)findViewById(R.id.login_name);
        password=(EditText)findViewById(R.id.login_password);
        gotoRegister=(TextView)findViewById(R.id.login_register);

        addTitle(R.string.login_account);
        setrightButton(R.string.login_login,new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInLogic.authority(username.getText().toString(),password.getText().toString());
            }
        });

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
        progressDialog = new ProgressDialog(SignInActivity.this);
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

    @Override
    public void showToast(String error) {
        Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void gotoMainActivity() {
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }
}

