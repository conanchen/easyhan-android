/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ditto.feature.word.slide;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.apache.commons.lang3.StringUtils;
import org.ditto.feature.base.BaseFragment;
import org.ditto.feature.base.di.Injectable;
import org.ditto.feature.base.glide.GlideApp;
import org.ditto.feature.word.R;
import org.ditto.feature.word.R2;
import org.ditto.feature.word.di.WordSlideViewModelFactory;
import org.ditto.feature.word.profile.WordViewModel;
import org.ditto.lib.dbroom.index.Pinyin;
import org.ditto.lib.dbroom.index.Word;
import org.easyhan.word.HanZi;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 * <p>
 * <p>This class is used by the  {@link
 * WordSlideActivity} samples.</p>
 */
public class WordSlideFragment extends BaseFragment implements Injectable {
    public static final String MyPREFERENCES = "MyWordFragmentPrefs";
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    public static final String ARG_WORD = "word";
    @Inject
    WordSlideViewModelFactory viewModelFactory;
    SharedPreferences sharedpreferences;
    @BindView(R2.id.backdrop)
    AppCompatTextView backdrop;
    @BindView(R2.id.bishun)
    AppCompatImageView bishun;
    @BindView(R2.id.pinyin1)
    AppCompatButton pinyin1;
    @BindView(R2.id.pinyin2)
    AppCompatButton pinyin2;
    @BindView(R2.id.pinyin3)
    AppCompatButton pinyin3;
    @BindView(R2.id.bihua)
    AppCompatTextView bihua;
    @BindView(R2.id.fanyi)
    HtmlTextView fanyi;
    @BindView(R2.id.terms)
    HtmlTextView terms;
    @BindView(R2.id.basemean)
    HtmlTextView basemean;
    @BindView(R2.id.detailmean)
    HtmlTextView detailmean;
    @BindView(R2.id.riddles)
    HtmlTextView riddles;
    MediaPlayer mediaplayer;
    private WordViewModel mViewModel;
    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    private String mWord;

    public WordSlideFragment() {
    }

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static WordSlideFragment create(int pageNumber, String word) {
        WordSlideFragment fragment = new WordSlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(ARG_WORD, word);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
        }
    }

    private void setupController() {
        sharedpreferences = this.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(WordViewModel.class);
        mViewModel.getLiveWord().observe(this, word -> {
            showBishun(word);
            showPinyins(word);
            showStrokenames(word);
            showFanyi(word);
            showTerms(word);
            showBasemean(word);
            showDetailmean(word);
            showRiddles(word);
        });
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mWord = getArguments().getString(ARG_WORD);
        mViewModel.setWord(mWord);
    }

    private void showRiddles(Word word) {
        riddles.setVisibility(View.GONE);
        if (word.riddles != null) {
            riddles.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < word.riddles.size(); i++) {
                sb.append(String.format("<p>%s</p>", word.riddles.get(i)));
            }

            riddles.setHtml(sb.toString());

        }
    }

    private void showDetailmean(Word word) {
        detailmean.setVisibility(View.GONE);
        if (word.detailmean != null) {
            detailmean.setVisibility(View.VISIBLE);
            detailmean.setHtml(word.detailmean);
        }
    }

    private void showBasemean(Word word) {
        basemean.setVisibility(View.GONE);
        if (word.basemean != null) {
            basemean.setVisibility(View.VISIBLE);
            basemean.setHtml(word.basemean);
        }
    }


    private void showTerms(Word word) {
        if (word.terms != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < word.terms.size(); i++) {
                sb.append(String.format("%s   ", word.terms.get(i)));
            }
            terms.setText(sb.toString());
        }
    }

    private void showFanyi(Word word) {
        fanyi.setVisibility(View.GONE);
        if (word.fanyi != null) {
            fanyi.setVisibility(View.VISIBLE);
            fanyi.setText(word.fanyi);
        }
    }

    private void showStrokenames(Word word) {
        if (word.strokenames != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < word.strokenames.size(); i++) {
                String[] names = StringUtils.splitByWholeSeparator(word.strokenames.get(i), "/");
                String[] strokeDefinition = null;
                for (int j = 0; j < names.length; j++) {
                    strokeDefinition = HanZi.STROKE_NAMES.get(names[j]);
                    if (strokeDefinition != null) break;
                }

                sb.append(String.format("%d.%s:%s   ", i + 1, word.strokenames.get(i), strokeDefinition == null ? "" : strokeDefinition[1]));
            }
            bihua.setText(sb.toString());
        }
    }

    private void showBishun(Word word) {
        backdrop.setText(word.word);
        GlideApp
                .with(bishun)
                .load(word.bishun)
                .placeholder(R.drawable.ask28)
                .error(R.drawable.ask28)
                .fallback(new ColorDrawable(Color.GRAY))
                .centerCrop()
                .transition(withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                        backdrop.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                        backdrop.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(bishun);
    }

    private void showPinyins(Word word) {
        pinyin1.setVisibility(View.GONE);
        pinyin2.setVisibility(View.GONE);
        pinyin3.setVisibility(View.GONE);
        if (word.pinyins != null) {
            for (int i = 0; i < word.pinyins.size(); i++) {
                Pinyin pinyin = word.pinyins.get(i);
                if (i == 0) {
                    pinyin1.setVisibility(View.VISIBLE);
                    pinyin1.setText(pinyin.pinyin);
                    pinyin1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(), String.format("%s", pinyin.mp3), Toast.LENGTH_LONG).show();
                            play(pinyin.mp3);
                        }
                    });
                } else if (i == 1) {
                    pinyin2.setVisibility(View.VISIBLE);
                    pinyin2.setText(pinyin.pinyin);
                    pinyin2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(), String.format("%s", pinyin.mp3), Toast.LENGTH_LONG).show();
                            play(pinyin.mp3);
                        }
                    });
                } else if (i == 2) {
                    pinyin3.setVisibility(View.VISIBLE);
                    pinyin3.setText(pinyin.pinyin);
                    pinyin3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(), String.format("%s", pinyin.mp3), Toast.LENGTH_LONG).show();
                            play(pinyin.mp3);
                        }
                    });
                }
            }
        }
    }

    private void play(String url) {
        try {
            mediaplayer.stop();
            mediaplayer.setDataSource(url);
            mediaplayer.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaplayer.start();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaplayer = new MediaPlayer();
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_word, container, false);
        ButterKnife.bind(this, rootView);

        setupController();
//        // Set the title view to show the page number.
//        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
//                getString(R.string.title_template_step, mPageNumber + 1, mWord));
//
        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
