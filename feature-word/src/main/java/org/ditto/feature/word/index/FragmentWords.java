package org.ditto.feature.word.index;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

import org.ditto.feature.base.BaseFragment;
import org.ditto.feature.base.SampleItemAnimator;
import org.ditto.feature.base.di.Injectable;
import org.ditto.feature.word.R;
import org.ditto.feature.word.R2;
import org.ditto.feature.word.di.WordViewModelFactory;
import org.ditto.lib.Constants;
import org.ditto.lib.dbroom.index.Word;
import org.easyhan.common.grpc.HanziLevel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a listCommandsBy of Items.
 * <p/>
 */
public class FragmentWords extends BaseFragment implements Injectable, WordsController.AdapterCallbacks {
    private final static String TAG = FragmentWords.class.getSimpleName();
    private final static Gson gson = new Gson();

    @Inject
    WordViewModelFactory viewModelFactory;

    private WordsViewModel viewModel;

    private static final int SPAN_COUNT = 6;

    private final RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    private final WordsController controller = new WordsController(this, recycledViewPool);

    @BindView(R2.id.itemlist)
    RecyclerView recyclerView;

    int currentPageNo = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentWords() {
    }


    public static FragmentWords create(String title, HanziLevel level) {
        Preconditions.checkNotNull(title);
        FragmentWords fragment = new FragmentWords();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TITLE, title);
        bundle.putString(Constants.HANZILEVEL, level.name());
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentPageNo = savedInstanceState.getInt(getNameCurrentPageNo());
            Log.i(TAG, String.format("onActivityCreated savedInstanceState.getInt(%s)=%d", getNameCurrentPageNo(), currentPageNo));
        }
        setupController();
    }

    private String getNameCurrentPageNo() {
        Preconditions.checkNotNull(this.getArguments().getString(Constants.HANZILEVEL));
        HanziLevel level = HanziLevel.valueOf(this.getArguments().getString(Constants.HANZILEVEL));
        return level.name() + "_CURRENT_PGNO";
    }

    @Override
    public void onResume() {
        super.onResume();
        Preconditions.checkNotNull(this.getArguments().getString(Constants.HANZILEVEL));
        HanziLevel level = HanziLevel.valueOf(this.getArguments().getString(Constants.HANZILEVEL));
        viewModel.refresh(level);
        viewModel.loadPage(level, currentPageNo);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getNameCurrentPageNo(), currentPageNo);
        Log.i(TAG, String.format("onSaveInstanceState outState.putInt(%s, %d)", getNameCurrentPageNo(), currentPageNo));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
    }


    private void setupController() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WordsViewModel.class);

        viewModel.getLiveWords().observe(this, data -> {
            controller.setData(data);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_item_list, container, false);
        ButterKnife.bind(this, view);

        // Many carousels and color models are shown on screen at once. The default recycled view
        // pool size is only 5, so we manually set the pool size to avoid constantly creating new views
        // We also use a shared view pool so that carousels can recycle items between themselves.
//        recycledViewPool.setMaxRecycledViews(R.layout.model_color, Integer.MAX_VALUE);
//        recycledViewPool.setMaxRecycledViews(R.layout.model_carousel_group, Integer.MAX_VALUE);
        recyclerView.setRecycledViewPool(recycledViewPool);

        // We are using a multi span grid to allow two columns of buttons. To set this up we need
        // to set our span count on the controller and then get the span size lookup object from
        // the controller. This look up object will delegate span size lookups to each model.
        controller.setSpanCount(SPAN_COUNT);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), SPAN_COUNT);
        gridLayoutManager.setSpanSizeLookup(controller.getSpanSizeLookup());
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(false);
        recyclerView.setItemAnimator(new SampleItemAnimator());

        recyclerView.setAdapter(controller.getAdapter());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollVertically(-1)) {
                        Log.i(TAG, "callback.onScrollToTop() -1,viewModel.refresh()");
                        HanziLevel level = HanziLevel.valueOf(getArguments().getString(Constants.HANZILEVEL));
                        viewModel.refresh(level);
                    }
                }
            }
        });


        return recyclerView;
    }


    @Override
    public void onWordItemClicked(Word word, int position) {
        ARouter.getInstance().build("/feature_word/WordActivity")
                .withString(Constants.ROUTE_WORD, word.word)
                .navigation();
    }

    @Override
    public void onPageClicked(int pageno) {
        Preconditions.checkNotNull(this.getArguments().getString(Constants.HANZILEVEL));
        HanziLevel level = HanziLevel.valueOf(this.getArguments().getString(Constants.HANZILEVEL));
        viewModel.refresh(level);
        viewModel.loadPage(level, pageno);
        currentPageNo = pageno;
    }
}
