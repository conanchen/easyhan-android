package org.ditto.feature.login;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.ditto.feature.login.beans.UserQQInfo;
import org.json.JSONObject;

public class QQLogin {
    private final String TAG = QQLogin.class.getSimpleName();
    private Activity mActivity;
    private Tencent mTencentQQ;
    private UserInfo mUserInfo;
    private final String mHiaskAppidInQQ = "1106139510";

    public interface Callbacks{
        void onQQLogined(String accessToken);
    }

    public QQLogin(Activity loginActivity) {
        this.mActivity = loginActivity;
        if (mTencentQQ == null) {
            mTencentQQ = Tencent.createInstance(mHiaskAppidInQQ, mActivity);
        }
    }

    public void login(QQLogin.Callbacks callbacks) {
//        if (!mTencentQQ.isSessionValid()) {
            mTencentQQ.login(mActivity, "all", new IUiListener() {
                @Override
                public void onComplete(Object response) {
                    JSONObject jsonResponse = (JSONObject) response;
                    Log.i(TAG, String.format("login callback jsonResponse=%s", jsonResponse.toString()));
                    try {
                        String accessToken = jsonResponse.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
                        String expires = jsonResponse.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
                        String openId = jsonResponse.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
                        if (!android.text.TextUtils.isEmpty(accessToken)
                                && !android.text.TextUtils.isEmpty(expires)
                                && !android.text.TextUtils.isEmpty(openId)) {
                            mTencentQQ.setAccessToken(accessToken, expires);
                            mTencentQQ.setOpenId(openId);

                            callbacks.onQQLogined(accessToken);
                        }
                    } catch (Exception e) {
                    }

                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            });
//        }
    }

    public void logout(Context context) {
        Tencent.createInstance(mHiaskAppidInQQ, context).logout(context);
    }

    /**
     * 获取登录QQ腾讯平台的权限信息(用于访问QQ用户信息)
     *
     * @param jsonObject
     */
    private void initOpenidAndToken(JSONObject jsonObject) {
//        public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
            if (!android.text.TextUtils.isEmpty(token) && !android.text.TextUtils.isEmpty(expires)
                    && !android.text.TextUtils.isEmpty(openId)) {
                mTencentQQ.setAccessToken(token, expires);
                mTencentQQ.setOpenId(openId);
            }
            Log.i(TAG, "token:" + token);
            Log.i(TAG, "expires:" + expires);
            Log.i(TAG, "openId:" + openId);

        } catch (Exception e) {
        }
    }

    private void updateUserInfo() {
        if (mTencentQQ != null && mTencentQQ.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                }

                @Override
                public void onComplete(final Object response1) {
                    JSONObject response = (JSONObject) response1;
                    if (response.has("nickname")) {
                        Gson gson = new Gson();
                        UserQQInfo userQQInfo = gson.fromJson(response.toString(), UserQQInfo.class);
                        if (userQQInfo != null) {
                            Log.i("enyu", "昵称：" + userQQInfo.getNickname() + "  性别:" + userQQInfo.getGender() + "  地址：" + userQQInfo.getProvince() + userQQInfo.getCity());
                            Log.i("enyu", "头像路径：" + userQQInfo.getFigureurl_qq_2());
                        }
                    }
                }

                @Override
                public void onCancel() {
                }
            };
            mUserInfo = new UserInfo(mActivity, mTencentQQ.getQQToken());
            mUserInfo.getUserInfo(listener);
        }
    }

    /**
     * 继承的到BaseUiListener得到doComplete()的方法信息
     */
    IUiListener mQQloginListener = new QQBaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {//得到用户的ID  和签名等信息  用来得到用户信息
            Log.i(TAG, values.toString());
            initOpenidAndToken(values);
            updateUserInfo();
        }
    };

    private class QQBaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Toast.makeText(mActivity, "QQBaseUiListener登录失败", Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Toast.makeText(mActivity, "QQBaseUiListener登录失败", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(mActivity, "QQBaseUiListener登录成功", Toast.LENGTH_LONG).show();
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {
            //// TODO: 2017/5/25
        }

        @Override
        public void onError(UiError e) {
            //登录错误
            Log.i("enyu", "onError");
        }

        @Override
        public void onCancel() {
            // 运行完成
            Log.i("enyu", "onCancel");
        }
    }

}
