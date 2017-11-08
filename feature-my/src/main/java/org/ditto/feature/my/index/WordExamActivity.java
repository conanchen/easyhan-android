package org.ditto.feature.my.index;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.view.WindowManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;

import org.ditto.feature.base.BaseActivity;
import org.ditto.feature.base.Constants;
import org.ditto.feature.my.R;
import org.ditto.feature.my.R2;
import org.ditto.feature.my.di.MyViewModelFactory;
import org.ditto.lib.dbroom.index.Word;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import nl.fampennings.keyboard.PinyinKeyboard;
import nl.fampennings.keyboard.StrokeKeyboard;


@Route(path = "/feature_my/WordExamActivity")
public class WordExamActivity extends BaseActivity {
    private final static String TAG = WordExamActivity.class.getSimpleName();
    private final static Gson gson = new Gson();


    @Autowired(name = Constants.ROUTE_WORD)
    String mWord;

    private String mImageTitle = "";
    private Word mCurrentExamWord;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    private CollapsingToolbarLayoutState mCollapsingToolbarLayoutState;

    @Inject
    MyViewModelFactory mViewModelFactory;

    private MyWordsViewModel mViewModel;

    @BindView(R2.id.app_bar)
    AppBarLayout app_bar;

    @BindView(R2.id.backdrop)
    AppCompatTextView image;

    @BindView(R2.id.toolbar_button_layout)
    ButtonBarLayout buttonBarLayout;

    @BindView(R2.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R2.id.pinyin1)
    TextInputEditText pinyin1;

    @BindView(R2.id.pinyin2)
    TextInputEditText pinyin2;

    @BindView(R2.id.strokes)
    TextInputEditText strokes;

    @BindView(R2.id.broken)
    TextInputEditText broken;

    @BindView(R2.id.ok)
    AppCompatButton ookButton;

    PinyinKeyboard mPinyinKeyboard;
    StrokeKeyboard mStrokeKeyboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_exam);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ARouter.getInstance().inject(this);

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


        broken.setOnFocusChangeListener((view, b) -> {
            if (b) {
                Observable
                        .just(true)
                        .delay(500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                            app_bar.setExpanded(false, true);
                        });
            }
        });

        ookButton.setFocusable(true);

        setupExamKeyboard();
    }

    private void setupExamKeyboard() {
        mPinyinKeyboard = new PinyinKeyboard(this, R.id.keyboardview, R.xml.pinyinkbd);
        mStrokeKeyboard = new StrokeKeyboard(this, R.id.keyboardview, R.xml.strokekbd);

        mPinyinKeyboard.registerEditText(R.id.pinyin1, () -> Observable
                .just(true)
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    app_bar.setExpanded(false, true);
                }));
        mPinyinKeyboard.registerEditText(R.id.pinyin2, () -> Observable
                .just(true)
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    app_bar.setExpanded(false, true);
                }));
        mStrokeKeyboard.registerEditText(R.id.strokes, () -> Observable
                .just(true)
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    app_bar.setExpanded(false, true);
                })
        );

    }

    @Override
    public void onBackPressed() {
        // NOTE Trap the back key: when the PinyinKeyboard is still visible hide it, only when it is invisible, finish activity
        if (mPinyinKeyboard.isCustomKeyboardVisible() || mStrokeKeyboard.isCustomKeyboardVisible()) {
            mPinyinKeyboard.hideCustomKeyboard();
            mStrokeKeyboard.hideCustomKeyboard();
        } else {
            this.finish();
        }
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MyWordsViewModel.class);

        nextExamWord();
    }

    private void nextExamWord() {
        mViewModel.getLiveExamWords().observe(this, words -> {
            if (words != null && words.size() > 0) {
                Random random = new Random();
                mCurrentExamWord = words.get(random.nextInt(words.size()));
                mViewModel.removeObserver(WordExamActivity.this);

                image.setText(mCurrentExamWord.word);
            }
        });

        mViewModel.nextExamWord();
    }

    @OnClick(R2.id.ok)
    void onOKButtonClicked() {
        nextExamWord();
    }
}
