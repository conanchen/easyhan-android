package org.ditto.feature.login;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.arouter.utils.TextUtils;
import com.google.gson.Gson;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.ditto.feature.base.BaseActivity;
import org.ditto.feature.login.di.LoginViewModelFactory;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;

@Route(path = "/feature_login/LoginActivity")
public class LoginActivity extends BaseActivity implements QQLogin.Callbacks {
    private final static String TAG = "LoginActivity";
    Gson gson = new Gson();

    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Inject
    LoginViewModelFactory mViewModelFactory;

    private LoginViewModel mViewModel;


    @Autowired
    String username; //maybe passed from RegisterActivity、ForgetpasswordActivity

    @BindView(R2.id.toolbar)
    Toolbar toolbar;

    @BindView(R2.id.username_layout)
    TextInputLayout usernameLayout;

    @BindView(R2.id.username)
    EditText usernameEditText;

    @BindView(R2.id.password)
    EditText passwordEditText;

    @BindView(R2.id.login_button)
    Button loginButton;
    @BindView(R2.id.login_qq_button)
    AppCompatImageButton loginQQButton;

    @BindView(R2.id.login_weibo_button)
    AppCompatImageButton loginWeiboButton;

    @BindView(R2.id.login_wechat_button)
    AppCompatImageButton loginWechatButton;

    private QQLogin mQqLogin;
//    private WeiboLogin mWeiboLogin;
//    private WechatLogin mWechatLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ARouter.getInstance().inject(this);

        mQqLogin = new QQLogin(this);
//        mWeiboLogin = new WeiboLogin(this);
//        mWechatLogin = new WechatLogin(this);

        if (!TextUtils.isEmpty(username)) {
            usernameEditText.setText(username);
            passwordEditText.requestFocus();
        }
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usernameLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (usernameEditText.getText() != null && usernameEditText.getText().length() < 11) {
                        usernameLayout.setErrorEnabled(true);
                        usernameLayout.setError("请输入正确的手机号");
                    }
                }
            }
        });

        usernameEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        loginButton.setEnabled(false);
        loginWechatButton.setEnabled(false);
        loginWeiboButton.setEnabled(false);

        setupViewModel();
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel.class);
        mViewModel.getLiveLoginedUser().observe(this, user -> {
            if (user != null) {
                Toast.makeText(this,
                        String.format("Login OK code=%s", user.code),
                        Toast.LENGTH_LONG)
                        .show();
                Observable.just(true)
                        .delay(500, TimeUnit.MILLISECONDS)
                        .subscribe(aBoolean -> {
                            LoginActivity.this.finish();
                        });
            } else {
                Toast.makeText(this,
                        String.format("CAN NOT LOGIN TO TT, TRY LATER!"),
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

    }


    @OnClick(R2.id.login_button)
    public void loginButtonOnClick(Button button) {
        button.setEnabled(false);
    }

    @OnClick(R2.id.login_wechat_button)
    public void loginWechatOnClick(ImageButton button) {
        Log.e(TAG, "call wxapi ");
//        mWechatLogin.login();
    }

    @OnClick(R2.id.login_qq_button)
    public void loginQQOnClick(ImageButton button) {
        Log.e(TAG, "call loginQQOnClick ");
        mQqLogin.login(this);
    }

    @OnClick(R2.id.login_weibo_button)
    public void loginWeiboOnClick(ImageView button) {
        Log.d(TAG, "call loginWeiboOnClick ");
//        mWeiboLogin.login();
    }


    /***
     * QQ平台返回返回数据个体 loginListener的values
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //QQ login result
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_LOGIN ||
                requestCode == com.tencent.connect.common.Constants.REQUEST_APPBAR) {
            Log.i(TAG, "onActivityResult Constants.REQUEST_LOGIN || Constants.REQUEST_APPBAR");
            Tencent.onActivityResultData(requestCode, resultCode, data, new IUiListener() {

                @Override
                public void onComplete(Object response) {
                    //TODO: here won't be called ?
                    JSONObject jsonResponse = (JSONObject) response;

                    Log.i(TAG, String.format("Tencent.onActivityResultData onComplete jsonResponse=%s", jsonResponse.toString()));
                }

                @Override
                public void onError(UiError uiError) {
                    Log.i(TAG, String.format("Tencent.onActivityResultData onError uiError=%s", uiError.toString()));
                }

                @Override
                public void onCancel() {
                    Log.i(TAG, String.format("Tencent.onActivityResultData onCancel"));
                }
            });
        }

//        // Weibo Login Result, SSO 授权回调
//        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
//        if (mWeiboLogin != null) {
//            mWeiboLogin.authorizeCallBack(requestCode, resultCode, data);
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onQQLogined(String accessToken) {
        mViewModel.loginWithQQAccessToken(accessToken);
    }
}
