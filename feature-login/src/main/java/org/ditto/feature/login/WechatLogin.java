package org.ditto.feature.login;

import android.app.Activity;

public class WechatLogin {
        private final Activity mActivity;
//        private IWXAPI mWeixinAPI;

        public WechatLogin(Activity loginActivity) {
            this.mActivity = loginActivity;

//            mWeixinAPI = WXAPIFactory.createWXAPI(mActivity, Constants.APP_ID);
//            mWeixinAPI.registerApp(Constants.APP_ID);
        }

        public void login() {
            // send oauth request
//            final SendAuth.Req req = new SendAuth.Req();
//            req.scope = "snsapi_userinfo";
//            req.state = "none";
//            mWeixinAPI.sendReq(req);
        }


//        IWXAPIEventHandler mWeixinapiEventHandler = new IWXAPIEventHandler() {
//
//            // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
//            @Override
//            public void onResp(BaseResp resp) {
//                int result = 0;
//
//                Log.e(TAG, "baseresp.getType = " + resp.getType());
////            Toast.makeText(this, "baseresp.getType = " + resp.getType(), Toast.LENGTH_SHORT).show();
//
//                switch (resp.errCode) {
//                    case BaseResp.ErrCode.ERR_OK:
//                        result = R.string.errcode_success;
//                        break;
//                    case BaseResp.ErrCode.ERR_USER_CANCEL:
//                        result = R.string.errcode_cancel;
//                        break;
//                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                        result = R.string.errcode_deny;
//                        break;
//                    case BaseResp.ErrCode.ERR_UNSUPPORT:
//                        result = R.string.errcode_unsupported;
//                        break;
//                    default:
//                        result = R.string.errcode_unknown;
//                        break;
//                }
//
////            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onReq(BaseReq baseReq) {
//
//            }
//        };

    }
