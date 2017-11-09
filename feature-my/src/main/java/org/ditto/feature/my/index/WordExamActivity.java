package org.ditto.feature.my.index;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ButtonBarLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;

import org.ditto.feature.base.BaseActivity;
import org.ditto.feature.my.R;
import org.ditto.feature.my.R2;
import org.ditto.feature.my.di.MyViewModelFactory;
import org.ditto.lib.repository.model.Status;

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

    private String mImageTitle = "";

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    private CollapsingToolbarLayoutState mCollapsingToolbarLayoutState;

    @Inject
    MyViewModelFactory mViewModelFactory;

    private MyWordViewModel mViewModel;

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

    @BindView(R2.id.pinyin1_indicator)
    AppCompatImageView pinyin1_indicator;

    @BindView(R2.id.pinyin2)
    TextInputEditText pinyin2;

    @BindView(R2.id.pinyin2_indicator)
    AppCompatImageView pinyin2_indicator;

    @BindView(R2.id.strokes)
    TextInputEditText strokes;

    @BindView(R2.id.strokes_indicator)
    AppCompatImageView strokes_indicator;

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
        pinyin1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mViewModel.checkPinyin1(editable.toString());
            }
        });
        pinyin2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mViewModel.checkPinyin2(editable.toString());
            }
        });
        strokes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mViewModel.checkStrokes(editable.toString());
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
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MyWordViewModel.class);
        mViewModel.getLiveExamWordHolder().observe(this, examWordHolder -> {
            boolean pinyin1Passed = false, pinyin2Passed = false, strokesPassed = false;
            if (examWordHolder.examWord != null) {
                image.setText(examWordHolder.examWord.word);
            }
            if (examWordHolder.checkPinyin1Status != null && Status.Code.END_SUCCESS.equals(examWordHolder.checkPinyin1Status.code)) {
                pinyin1Passed = true;
                pinyin1_indicator.setImageResource(R.drawable.ic_check_circle_black_24dp);
            } else {
                pinyin1_indicator.setImageResource(R.drawable.ic_error_black_24dp);
            }
            if (examWordHolder.checkPinyin2Status != null && Status.Code.END_SUCCESS.equals(examWordHolder.checkPinyin2Status.code)) {
                pinyin2Passed = true;
                pinyin2_indicator.setImageResource(R.drawable.ic_check_circle_black_24dp);
            } else {
                pinyin2_indicator.setImageResource(R.drawable.ic_error_black_24dp);
            }
            if (examWordHolder.checkStrokesStatus != null && Status.Code.END_SUCCESS.equals(examWordHolder.checkStrokesStatus.code)) {
                strokesPassed = true;
                strokes_indicator.setImageResource(R.drawable.ic_check_circle_black_24dp);
            } else {
                strokes_indicator.setImageResource(R.drawable.ic_error_black_24dp);
            }

            if (pinyin1Passed && pinyin2Passed && strokesPassed) {
                ookButton.setEnabled(true);
                ookButton.setText(String.format("[%s]识字进度升级到%s", examWordHolder.examWord.word, WordUtils.getTitleByMemIdx(examWordHolder.examWord.memIdx + 1)));
            } else {
                ookButton.setText(String.format("[%s]识字进度升级到%s", "?", WordUtils.getTitleByMemIdx(examWordHolder.examWord.memIdx + 1)));
                ookButton.setEnabled(false);
            }
        });

        nextExamWord();

        mViewModel.getLiveUpsertStatus().observe(this, status -> {
            if (Status.Code.END_NOT_LOGIN.equals(status.code)) {
                ARouter.getInstance().build("/feature_login/LoginActivity").navigation();
//                mViewModel.getLiveUpsertStatus().removeObservers(WordActivity.this);
            } else if (Status.Code.END_SUCCESS.equals(status.code)) {
                nextExamWord();
            } else {
                Toast.makeText(WordExamActivity.this,
                        String.format("升级识字进度服务器返回错误，请稍后再试。 错误代码=%s \n错误信息=%s", status.code, status.message),
                        Toast.LENGTH_LONG)
                        .show();
            }

        });

    }

    private void nextExamWord() {
        mViewModel.nextExamWord();
        pinyin1.clearFocus();
        pinyin1.setText("");
        pinyin2.clearFocus();
        pinyin2.setText("");
        strokes.clearFocus();
        strokes.setText("");
        app_bar.setExpanded(true, true);
    }

    @OnClick(R2.id.ok)
    void onOKButtonClicked() {
        mViewModel.updateMyWordProgress();
    }
}
