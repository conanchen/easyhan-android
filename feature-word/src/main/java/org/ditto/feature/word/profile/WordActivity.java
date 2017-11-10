package org.ditto.feature.word.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;

import org.ditto.feature.base.BaseActivity;
import org.ditto.feature.base.Constants;
import org.ditto.feature.base.WordUtils;
import org.ditto.feature.word.R;
import org.ditto.feature.word.R2;
import org.ditto.feature.word.di.WordViewModelFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


@Route(path = "/feature_word/WordActivity")
public class WordActivity extends BaseActivity {
    private final static String TAG = WordActivity.class.getSimpleName();
    private final static Gson gson = new Gson();


    @Autowired(name = Constants.ROUTE_WORD)
    String mWord;

    private String mImageTitle = "";

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    private CollapsingToolbarLayoutState mCollapsingToolbarLayoutState;

    @Inject
    WordViewModelFactory mViewModelFactory;

    private WordViewModel mViewModel;

    @BindView(R2.id.x5webView)
    WebView x5webView;

    @BindView(R2.id.app_bar)
    AppBarLayout app_bar;

    @BindView(R2.id.backdrop)
    AppCompatImageView image;

    @BindView(R2.id.toolbar_button_layout)
    ButtonBarLayout buttonBarLayout;

    @BindView(R2.id.toolbar_title)
    AppCompatTextView toolbar_title;

    @BindView(R2.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ARouter.getInstance().inject(this);
        setupX5WebView();

        setupViewModel();
        app_bar.addOnOffsetChangedListener((AppBarLayout appBarLayout, int verticalOffset) -> {

            if (verticalOffset == 0) {
                if (mCollapsingToolbarLayoutState != CollapsingToolbarLayoutState.EXPANDED) {
                    mCollapsingToolbarLayoutState = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                    collapsingToolbarLayout.setTitle("EXPANDED");//设置title为EXPANDED

                    collapsingToolbarLayout.setTitle(mImageTitle);
                }
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (mCollapsingToolbarLayoutState != CollapsingToolbarLayoutState.COLLAPSED) {
                    collapsingToolbarLayout.setTitle("");//设置title不显示
                    buttonBarLayout.setVisibility(View.VISIBLE);//隐藏播放按钮
                    mCollapsingToolbarLayoutState = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                }
            } else {
                if (mCollapsingToolbarLayoutState != CollapsingToolbarLayoutState.INTERNEDIATE) {
                    if (mCollapsingToolbarLayoutState == CollapsingToolbarLayoutState.COLLAPSED) {
                        buttonBarLayout.setVisibility(View.GONE);//由折叠变为中间状态时隐藏播放按钮
                    }
                    collapsingToolbarLayout.setTitle("粉红字帖");//设置title为INTERNEDIATE
                    mCollapsingToolbarLayoutState = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                }
            }

        });

    }

    private void setupX5WebView() {
        //TODO: 使用http://x5.tencent.com/tbs/sdk.html代替android自带的webview
        WebSettings webSettings = x5webView.getSettings();
        webSettings.setAppCacheEnabled(true);

        //适配手机大小
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        x5webView.addJavascriptInterface(new MyJavaScriptInterface( ), "INTERFACE");

        x5webView.loadUrl("http://hanyu.baidu.com/zici/s?wd=" + mWord);
        x5webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url)
            {
                view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
            }
        });
    }

    /* An instance of this class will be registered as a JavaScript interface */
    class MyJavaScriptInterface {

        public MyJavaScriptInterface() {
        }

        @SuppressWarnings("unused")

        @JavascriptInterface
        public void processContent(String aContent) {
            final String content = aContent;
            Log.i(TAG, content);
            Log.i(TAG, toUNICODE(content));
        }
    }
    public static String toUNICODE(String s)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<s.length();i++)
        {
            if(s.charAt(i) == '\n'){
                sb.append("\n");
            }else {
                sb.append(" U+" + Integer.toHexString(s.charAt(i)));
            }
        }
        return sb.toString();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && x5webView.canGoBack()) {
            // 返回上一页面
            x5webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            x5webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(WordViewModel.class);
        mViewModel.setWord(mWord);
        mViewModel.getLiveWord().observe(this, word -> {
            toolbar_title.setText(String.format("%s %s", WordUtils.getTitleByMemIdx(word.memIdx), WordUtils.getDescByMemIdx(word.memIdx)));
        });
    }

}
