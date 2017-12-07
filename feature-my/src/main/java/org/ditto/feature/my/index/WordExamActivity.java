package org.ditto.feature.my.index;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ButtonBarLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;

import org.ditto.feature.base.BaseActivity;
import org.ditto.feature.base.WordUtils;
import org.ditto.feature.my.R;
import org.ditto.feature.my.R2;
import org.ditto.feature.my.di.MyViewModelFactory;
import org.ditto.lib.repository.model.Status;
import org.easyhan.common.grpc.HanziLevel;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nl.fampennings.keyboard.PinyinKeyboard;
import nl.fampennings.keyboard.StrokeKeyboard;
import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;


@Route(path = "/feature_my/WordExamActivity")
public class WordExamActivity extends BaseActivity {
    private final static String TAG = WordExamActivity.class.getSimpleName();
    private final static Gson gson = new Gson();

    private String mImageTitle = "";
    private org.ditto.feature.base.CollapsingToolbarLayoutState state;
    private ChainTourGuide mTourGuideHandler;

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

    @BindView(R2.id.broken_button)
    AppCompatButton broken;

    @BindView(R2.id.ok)
    AppCompatButton ookButton;

    @BindView(R2.id.keyboardview)
    KeyboardView keyboardView;

    @BindView(R2.id.fab)
    FloatingActionButton fabButton;

    PinyinKeyboard mPinyinKeyboard;
    StrokeKeyboard mStrokeKeyboard;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    private String canPopupTourGuideKey = WordExamActivity.TAG + "canPopupTourGuide";
    private Boolean canPopupTourGuide = Boolean.TRUE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        canPopupTourGuide = sharedpreferences.getBoolean(canPopupTourGuideKey, Boolean.TRUE);

        Log.i(TAG, String.format("canPopupTourGuide=%b", canPopupTourGuide));

        setContentView(R.layout.activity_word_exam);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ARouter.getInstance().inject(this);

        setupViewModel();

        setupAppBar();

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
                    if (canPopupTourGuide) {
                        runOverlayListener_ContinueMethod();
                    }
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
                if (HanziLevel.ONE.name().equals(examWordHolder.examWord.level) && examWordHolder.examWord.memIdx > 1
                        || HanziLevel.TWO.name().equals(examWordHolder.examWord.level) && examWordHolder.examWord.memIdx > 3
                        || HanziLevel.THREE.name().equals(examWordHolder.examWord.level) && examWordHolder.examWord.memIdx > 5) {
                    fabButton.setVisibility(View.VISIBLE);
                } else {
                    fabButton.setVisibility(View.GONE);
                }
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
                Toast.makeText(WordExamActivity.this, "识字进度升级成功，准备下一个字", Toast.LENGTH_SHORT).show();
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
        mViewModel.updateMyWordProgress(Boolean.FALSE);
    }

    AlertDialog flightProgressDialog = null;

    @OnClick(R2.id.fab)
    void onFabButtonClicked() {
        if (flightProgressDialog == null) {
            flightProgressDialog = new AlertDialog.Builder(this)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            Observable
                                    .just(true)
                                    .delay(500, TimeUnit.MILLISECONDS)
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(aBoolean -> {
                                        mViewModel.updateMyWordProgress(Boolean.TRUE);
                                    });
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the flightProgressDialog
                        }
                    })
                    .setIcon(R.drawable.ic_flight_black_24dp)
                    .setTitle(R.string.flight_progress_title)
                    .setMessage(R.string.flight_progress_message)
                    .create();
        }
        flightProgressDialog.show();
    }

    private void runOverlayListener_ContinueMethod() {
                /* setup enter and exit animation */
        AlphaAnimation mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        AlphaAnimation mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);


        // the return handler is used to manipulate the cleanup of all the tutorial elements
        ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("拼音正确吗？")
                        .setDescription("注意右边提示✔表示输入正确️")
                        .setGravity(Gravity.BOTTOM | Gravity.LEFT)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(pinyin2_indicator);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("笔顺正确吗？")
                        .setDescription("注意右边提示✔表示输入正确️")
                        .setGravity(Gravity.BOTTOM | Gravity.LEFT)
                        .setBackgroundColor(Color.parseColor("#c0392b"))
                )
                .setOverlay(new Overlay()
                        .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })
                )
                .playLater(strokes_indicator);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("识字进度升级")
                        .setDescription("只有正确输入拼音1、拼音2、笔顺后，才可点击按钮[...识字进度升级...]")
                        .setGravity(Gravity.TOP)
                )
                .setOverlay(new Overlay()
                        .setBackgroundColor(Color.parseColor("#AAFF0000"))
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setStyle(Overlay.Style.Rectangle)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })
                )
                .playLater(ookButton);

        ChainTourGuide tourGuide4 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("拼៎音៎笔៎画៎键៎盘៎")
                        .setDescription("长按或连续点击有³标号的键钮可以切换音标或笔画")
                        .setGravity(Gravity.TOP)
                )
                .setOverlay(new Overlay()
                        .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setStyle(Overlay.Style.Rectangle)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                                new AlertDialog.Builder(WordExamActivity.this)
                                        .setPositiveButton(R.string.yes,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                    }
                                                })
                                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putBoolean(canPopupTourGuideKey, Boolean.FALSE);
                                                editor.commit();
                                                canPopupTourGuide = sharedpreferences.getBoolean(canPopupTourGuideKey, Boolean.TRUE);

                                            }
                                        })
                                        .setTitle("提示")
                                        .setMessage("下次继续提示测验界面新手导航吗？")
                                        .create()
                                        .show();

                            }
                        })
                )
                .playLater(keyboardView);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3, tourGuide4)
                .setDefaultOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.OverlayListener)
                .build();


        mTourGuideHandler = ChainTourGuide.init(this).playInSequence(sequence);
    }


}
