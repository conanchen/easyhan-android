package org.ditto.feature.word.index;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
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
import org.ditto.feature.base.Constants;
import org.ditto.feature.base.SampleItemAnimator;
import org.ditto.feature.base.di.Injectable;
import org.ditto.feature.word.R;
import org.ditto.feature.word.R2;
import org.ditto.feature.word.di.WordViewModelFactory;
import org.ditto.lib.dbroom.index.Word;
import org.easyhan.common.grpc.HanziLevel;
import org.easyhan.word.HanZi;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a listCommandsBy of Items.
 * <p/>
 */
public class FragmentWords1000 extends BaseFragment implements Words1000Controller.AdapterCallbacks {
    private final static String TAG = FragmentWords1000.class.getSimpleName();
    private final static Gson gson = new Gson();
    private static final int SPAN_COUNT = 4;
    private final RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    private final Words1000Controller controller = new Words1000Controller(this, recycledViewPool);
    @BindView(R2.id.itemlist)
    RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentWords1000() {
    }


    public static FragmentWords1000 create(String title) {
        Preconditions.checkNotNull(title);
        FragmentWords1000 fragment = new FragmentWords1000();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupController();
    }

    private void setupController() {
        controller.setData(HanZi.LEVEL1000);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
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

        return recyclerView;
    }


    @Override
    public void onWordItemClicked(String word, int position) {
        ARouter.getInstance().build("/feature_word/WordSlideActivity")
                .withString(Constants.ROUTE_WORD, word)
                .navigation();
    }

}
