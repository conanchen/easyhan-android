package org.ditto.feature.my.crawler;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ButtonBarLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.ditto.feature.base.BaseActivity;
import org.ditto.feature.base.WordUtils;
import org.ditto.feature.my.R;
import org.ditto.feature.my.R2;
import org.ditto.feature.my.di.MyViewModelFactory;
import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.repository.model.Status;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


@Route(path = "/feature_my/WordCrawlerActivity")
public class WordCrawlerActivity extends BaseActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    private final static String TAG = WordCrawlerActivity.class.getSimpleName();
    private final static Gson gson = new Gson();
    private final static ObjectMapper mapper = new ObjectMapper();

    @Inject
    MyViewModelFactory mViewModelFactory;
    @BindView(R2.id.auto)
    AppCompatCheckBox auto;
    @BindView(R2.id.word_idx)
    AppCompatEditText word_idx;
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
    SharedPreferences sharedpreferences;
    private String mImageTitle = "";
    private Word mWord;
    private CollapsingToolbarLayoutState mCollapsingToolbarLayoutState;
    private WordCrawlerViewModel mViewModel;
    private String wordIdxKey = WordCrawlerActivity.TAG + "WordIdx";
    private Integer wordIdx = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word_crawler);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ARouter.getInstance().inject(this);
        setupViewModel();

        setupX5WebView();

        setupAppBar();
    }

    private void setupAppBar() {
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
        x5webView.addJavascriptInterface(new MyJavaScriptInterface(), "INTERFACE");

        x5webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].outerHTML);");
            }
        });
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
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(WordCrawlerViewModel.class);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        wordIdx = sharedpreferences.getInt(wordIdxKey, 1);
        mViewModel.setWordIdx(wordIdx);
        mViewModel.getLiveWord().observe(this, word -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(wordIdxKey, word.levelIdx);
            editor.commit();

            mWord = word;
            word_idx.setText(String.valueOf(word.levelIdx));
            x5webView.loadUrl("http://hanyu.baidu.com/zici/s?wd=" + word.word);

            toolbar_title.setText(String.format("%s %s ：%s", WordUtils.getTitleByMemIdx(word.memIdx), WordUtils.getDescByMemIdx(word.memIdx), word.word));


        });

        mViewModel.getLiveSaveStatus().observe(this, status -> {
            if (Status.Code.END_SUCCESS.equals(status.code) && auto.isChecked()) {
                Observable.just(true).delay(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                aBoolean -> {
                                    mViewModel.setWordIdx(Integer.valueOf(word_idx.getText().toString()) + 1);
                                }
                        );
            }
        });
    }

    @OnClick(R2.id.auto)
    public void onAutoCheckBoxClicked() {
        if (auto.isChecked()) {
            mViewModel.setWordIdx(Integer.valueOf(word_idx.getText().toString()) + 1);
        }
    }

    @OnClick(R2.id.next)
    public void onNextButtonClicked() {
        int old = Integer.valueOf(word_idx.getText().toString());
        old = old > 0 ? old : 0;
        mViewModel.setWordIdx(old + 1);
    }

    @OnClick(R2.id.prev)
    public void onPrevButtonClicked() {
        int old = Integer.valueOf(word_idx.getText().toString());
        old = old > 0 ? old : 0;
        mViewModel.setWordIdx(old - 1);
    }

    @OnClick(R2.id.back)
    public void onBackButtonClicked() {
        this.finish();
    }

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    /* An instance of this class will be registered as a JavaScript interface */
    class MyJavaScriptInterface {

        public MyJavaScriptInterface() {
        }

        @SuppressWarnings("unused")

        @JavascriptInterface
        public void processContent(String results) {
            mViewModel.saveParsedWord(results);
        }

    }
}
