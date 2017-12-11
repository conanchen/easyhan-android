package org.ditto.feature.word.slide;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;

import org.ditto.feature.base.BaseFragmentActivity;
import org.ditto.feature.base.Constants;
import org.ditto.feature.word.R;
import org.ditto.feature.word.di.WordSlideViewModelFactory;

import javax.inject.Inject;


@Route(path = "/feature_word/WordSlideActivity")
public class WordSlideActivity extends BaseFragmentActivity {
    private final static String TAG = WordSlideActivity.class.getSimpleName();
    private final static Gson gson = new Gson();


    @Autowired(name = Constants.ROUTE_WORD)
    String mWord;

    @Inject
    WordSlideViewModelFactory mViewModelFactory;

    private WordSlideViewModel mViewModel;


    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word);

        ARouter.getInstance().inject(this);

        setupViewModel();

    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(WordSlideViewModel.class);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mViewModel.setDefaultWord(mWord);
        mViewModel.getLiveWordSlides().observe(this, wordSlideHolder -> {
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), wordSlideHolder);
            mPager.setAdapter(mPagerAdapter);

//            toolbar_title.setText(String.format("%s %s ：%s", WordUtils.getTitleByMemIdx(word.memIdx), WordUtils.getDescByMemIdx(word.memIdx),word.word));
        });
    }

    /**
     * A simple pager adapter that represents 5 {@link WordSlideFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        WordSlideHolder holder;

        public ScreenSlidePagerAdapter(FragmentManager fm, WordSlideHolder holder) {
            super(fm);
            this.holder = holder;
        }

        @Override
        public Fragment getItem(int position) {
//            return WordSlideFragment.create(position, holder.defaultWord.word);
            if (position == 0) {
                return WordSlideFragment.create(position, holder.defaultWord.word);
            } else {
                return WordSlideFragment.create(position, holder.words.get(position-1).word);
            }
        }

        @Override
        public int getCount() {
            return holder.words == null ? 1 : holder.words.size() + 1;
        }
    }

}
