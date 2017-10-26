package org.ditto.feature.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import org.ditto.lib.Constants;

public class WeiboLogin {
        private Activity mActivity;

        /**
         * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
         */
        private SsoHandler mWeiboSsoHandler;
        /**
         * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
         */
        private Oauth2AccessToken mAccessToken;

        public WeiboLogin(Activity loginActivity) {
            this.mActivity = loginActivity;
            WbSdk.install(mActivity, new AuthInfo(mActivity, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE));
            mWeiboSsoHandler = new SsoHandler(mActivity);

        }

        public void login() {
            mWeiboSsoHandler.authorize(new SelfWeiboAuthListener());
        }

        public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
            mWeiboSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        private class SelfWeiboAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {

            @Override
            public void onSuccess(final Oauth2AccessToken token) {
                mAccessToken = token;
                if (mAccessToken.isSessionValid()) {
                    // 显示 Token
//                    updateTokenView(false);
                    // 保存 Token 到 SharedPreferences
                    AccessTokenKeeper.writeAccessToken(mActivity, mAccessToken);
                    String token1 = mAccessToken.getToken();
                    String refreshtoken = mAccessToken.getRefreshToken();
                    String uid = mAccessToken.getUid();
                    String phoneNum = mAccessToken.getPhoneNum();
                    Log.i("enyu", "token1:" + token1);
                    Log.i("enyu", "refreshtoken:" + refreshtoken);
                    Log.i("enyu", "uid:" + uid);
                    Log.i("enyu", "phoneNum:" + phoneNum);
                }
            }

            @Override
            public void cancel() {
                Log.i("enyu", "cancel");
            }

            @Override
            public void onFailure(WbConnectErrorMessage errorMessage) {
                Log.i("enyu", "onFailure");
            }
        }

    }
