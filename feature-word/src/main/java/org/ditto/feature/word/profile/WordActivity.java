package org.ditto.feature.word.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;

import org.ditto.feature.base.BaseActivity;
import org.ditto.feature.word.R;
import org.ditto.feature.word.R2;
import org.ditto.feature.word.di.WordViewModelFactory;
import org.ditto.feature.base.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


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

    @BindView(R2.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
        ARouter.getInstance().inject(this);
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

        x5webView.loadUrl("http://hanyu.baidu.com/zici/s?wd=" + mWord);
        x5webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

    }


    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(WordViewModel.class);
        mViewModel.getLiveUpsertStatus().observe(this, status -> {
            Log.i(TAG, String.format("getLiveUpsertStatus status.code=%s \nstatus.message=%s", status.code, status.message));
            Toast.makeText(WordActivity.this,
                    String.format("getLiveUpsertStatus status.code=%s \nstatus.message=%s", status.code, status.message),
                    Toast.LENGTH_LONG)
                    .show();
        });

    }

    @OnClick(R2.id.fab)
    public void onFabupsertButtonClicked() {
        if (true) {
            ARouter.getInstance().build("/feature_login/LoginActivity")
                    .navigation();
        } else {
            mViewModel.upsertMyWord(mWord);
        }
    }

}
