package org.ditto.feature.my.index;

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
import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.ditto.feature.base.di.Injectable;
import org.ditto.feature.my.R;
import org.ditto.feature.my.R2;
import org.ditto.feature.my.di.MyViewModelFactory;
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
public class FragmentMy extends BaseFragment implements Injectable, MyController.AdapterCallbacks {
    private final static String TAG = FragmentMy.class.getSimpleName();
    private final static Gson gson = new Gson();

    @Inject
    MyViewModelFactory viewModelFactory;

    private MyViewModel viewModel;

    private static final int SPAN_COUNT = 6;

    private final RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    private final MyController controller = new MyController(this, recycledViewPool);

    @BindView(R2.id.itemlist)
    RecyclerView recyclerView;

    int currentPageNo = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentMy() {
    }


    public static FragmentMy create(String title, HanziLevel level) {
        Preconditions.checkNotNull(title);
        FragmentMy fragment = new FragmentMy();
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
        return  "MYWORD_CURRENT_PGNO";
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
        viewModel.loadPage(currentPageNo);
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MyViewModel.class);

        viewModel.getLiveMyWords().observe(this, data -> {
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
                        viewModel.refresh();
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
        viewModel.refresh();
        viewModel.loadPage(pageno);
        currentPageNo = pageno;
    }

    @Override
    public void onWordSortTypeChangedTo(VoWordSortType.WordSortType sortType) {
        Log.i(TAG, String.format("onWordSortTypeChangedTo sortType=%s",sortType.name()));
        viewModel.changeWordSortType(sortType);
    }
}
