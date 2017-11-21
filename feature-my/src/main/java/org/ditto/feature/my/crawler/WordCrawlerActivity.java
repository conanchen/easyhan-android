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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.ditto.feature.base.BaseActivity;
import org.ditto.feature.base.WordUtils;
import org.ditto.feature.my.R;
import org.ditto.feature.my.R2;
import org.ditto.feature.my.di.MyViewModelFactory;
import org.ditto.lib.dbroom.index.Word;

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
    @BindView(R2.id.word_value)
    AppCompatEditText word_value;
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
                view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
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
            editor.putInt(wordIdxKey, word.idx);
            editor.commit();

            mWord = word;
            word_idx.setText(String.valueOf(word.idx));
            x5webView.loadUrl("http://hanyu.baidu.com/zici/s?wd=" + word.word);
            toolbar_title.setText(String.format("%s %s ：%s", WordUtils.getTitleByMemIdx(word.memIdx), WordUtils.getDescByMemIdx(word.memIdx), word.word));

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
        mViewModel.setWordIdx(Integer.valueOf(word_idx.getText().toString()) + 1);
    }

    @OnClick(R2.id.ok)
    public void onOkButtonClicked() {
        mViewModel.setWordIdx(Integer.valueOf(word_idx.getText().toString()) + 1);
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
        public void processContent(String aContent) {
            final String content = aContent.trim();
            Log.i(TAG, content);
            final String[] lines = content.split("\n");
            String pinyin = "";
            String radical ="";
            String fiveElem ="";
            String strokesNum = "";
            String strokes = "";
            String basis = "";
            String detail = "";
            String wordGroups = "";
            String riddles = "";
            String english = "";
            boolean pinyinFound = false;
            boolean strokesNumFound = false;
            boolean strokesFound = false;
            WordBaidu wordBaidu = new WordBaidu();
            for (int i = 0; i < lines.length; i++) {
                if (!pinyinFound && StringUtils.contains(lines[i], "部 首")) {
                    pinyin = StringUtils.substringBefore(lines[i], "部 首");
                    radical = StringUtils.trim(StringUtils.substringBetween(lines[i],"部 首","笔 画"));
                    fiveElem = StringUtils.trim(StringUtils.substringBetween(lines[i],"五 行","部首"));
                    Log.i(TAG, String.format("found %s pinyin=%s", lines[i], pinyin));
                    pinyinFound = true;
                }

                if (!strokesNumFound && StringUtils.contains(lines[i], "笔画 ：")) {
                    strokesNum = StringUtils.substringAfter(lines[i], "笔画 ：").trim();
                    Log.i(TAG, String.format("found %s strokesNum=%s", lines[i], strokesNum));
                    strokesNumFound = true;
                    wordBaidu.strokesNum = Integer.valueOf(strokesNum);
                }

                String theStokes = StringUtils.substringAfter(lines[i], "名称 ：");
                if (!strokesFound && StringUtils.contains(lines[i], "名称 ：")) {
                    strokes = theStokes;
                    Log.i(TAG, String.format("found %s strokes=%s", lines[i], strokes));
                    strokesFound = true;

                }
            }
            String[] pinyinArr = StringUtils.splitByWholeSeparator(StringUtils.remove(pinyin, '\u00a0').trim(), null);
            String pinyins = "";
            for (int j = 0; j < pinyinArr.length; j++) {
                pinyins += StringUtils.isEmpty(pinyins) ? pinyinArr[j] : "|" + pinyinArr[j];
                wordBaidu.pinyins.add(pinyinArr[j]);
            }

            String[] strokeArr = StringUtils.splitByWholeSeparator(StringUtils.remove(strokes, '\u00a0').trim(), "、");
            String strokes1 = "";
            for (int j = 0; j < strokeArr.length; j++) {
                if (StringUtils.isNotEmpty(strokeArr[j])) {
                    strokes1 += StringUtils.isEmpty(strokes1) ? strokeArr[j] : "|" + strokeArr[j];
                    wordBaidu.strokes.add(StringUtils.trim(strokeArr[j]));
                }
            }

            boolean startBasis = false;
            for (int i = 0; i < lines.length; i++) {
                if ("详细释义".compareToIgnoreCase(lines[i].trim()) == 0) {
                    break;
                }
                if (startBasis) {
                    basis += lines[i] + "\n";
                    wordBaidu.basicMeanings.add(lines[i]);
                }
                if ("基本释义".compareToIgnoreCase(lines[i].trim()) == 0) {
                    startBasis = true;
                }
            }
            Log.i(TAG, String.format("基本释义=%s", basis));


            boolean startDetail = false;
            for (int i = 0; i < lines.length; i++) {
                if ("组词".compareToIgnoreCase(lines[i].trim()) == 0) {
                    break;
                }
                if (startDetail) {
                    detail += lines[i] + "\n";
                    wordBaidu.detailMeanings.add(lines[i]);
                }
                if ("详细释义".compareToIgnoreCase(lines[i].trim()) == 0) {
                    startDetail = true;
                }
            }
            Log.i(TAG, String.format("详细释义=%s", detail));


            boolean startGroups = false;
            for (int i = 0; i < lines.length; i++) {
                if ("百科释义".compareToIgnoreCase(lines[i].trim()) == 0) {
                    break;
                }
                if (startGroups) {
                    wordGroups += lines[i];
                    String[] arr = StringUtils.splitByWholeSeparator(lines[i], null);
                    for (int j = 0; j < arr.length; j++) {
                        wordBaidu.wordGroups.add(StringUtils.trim(arr[j]));
                    }
                }
                if ("组词".compareToIgnoreCase(lines[i].trim()) == 0) {
                    startGroups = true;
                }
            }
            Log.i(TAG, String.format("组词=%s", wordGroups));


            boolean startRiddles = false;
            for (int i = 0; i < lines.length; i++) {
                if ("百科释义".compareToIgnoreCase(lines[i].trim()) == 0) {
                    break;
                }
                if (startRiddles) {
                    riddles += lines[i] + "\n";
                    wordBaidu.riddles.add(lines[i]);
                }
                if ("相关谜语".compareToIgnoreCase(lines[i].trim()) == 0) {
                    startRiddles = true;
                }
            }
            Log.i(TAG, String.format("相关谜语=%s", riddles));


            boolean startEnglish = false;
            for (int i = 0; i < lines.length; i++) {
                if ("新产品体验".compareToIgnoreCase(lines[i].trim()) == 0) {
                    break;
                }
                if (startEnglish) {
                    english += lines[i];
                    wordBaidu.englishMeanings.add(lines[i]);
                }
                if ("英文翻译".compareToIgnoreCase(lines[i].trim()) == 0) {
                    startEnglish = true;
                }
            }
            Log.i(TAG, String.format("英文翻译=%s", english));

            wordBaidu.word = mWord.word;
            wordBaidu.radical = radical;
            wordBaidu.fiveElem = fiveElem;
            // Convert object to JSON string
            try {
                String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(wordBaidu);
                Log.e(TAG, jsonInString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            Log.i(TAG, String.format("%s,pinyin,strokesNum,strokes=%s,%s(%s),%s,%s(%s)", mWord, mWord,
                    pinyins,
                    WordUtils.toUNICODE(pinyins).trim(),
                    strokesNum.trim(),
                    StringUtils.remove(strokes1.trim(), '\u0020'),
                    WordUtils.toUNICODE(strokes1).trim()
            ));

            final String wordValue = String.format("%s,%s,%s,%s", mWord.word,
                    pinyins,
                    strokesNum.trim(), StringUtils.remove(strokes1.trim(), '\u0020'));

            Observable
                    .just(wordValue)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(value -> {
                        word_value.setText(value);
                        if (auto.isChecked()) {
                            Observable.just(true)
                                    .delay(500, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(aBoolean -> {
                                        mViewModel.setWordIdx(Integer.valueOf(word_idx.getText().toString()) + 1);
                                    });
                        }
                    });
        }
    }
}
