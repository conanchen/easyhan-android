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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ditto.feature.base.BaseFragment;
import org.ditto.feature.base.di.Injectable;
import org.ditto.feature.word.R;
import org.ditto.feature.word.di.WordSlideViewModelFactory;
import org.ditto.feature.word.profile.WordViewModel;

import javax.inject.Inject;


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
    private WordViewModel mViewModel;

    SharedPreferences sharedpreferences;
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
        setupController();
    }

    private void setupController() {
        sharedpreferences = this.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(WordViewModel.class);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mWord = getArguments().getString(ARG_WORD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_word, container, false);

        // Set the title view to show the page number.
        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                getString(R.string.title_template_step, mPageNumber + 1, mWord));

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
