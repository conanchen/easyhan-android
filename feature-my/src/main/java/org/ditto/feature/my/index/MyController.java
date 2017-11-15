package org.ditto.feature.my.index;

import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.view.View;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.OnModelClickListener;
import com.airbnb.epoxy.TypedEpoxyController;
import com.google.gson.Gson;

import org.ditto.feature.base.WordUtils;
import org.ditto.feature.base.epoxymodels.ItemRefreshHeaderModel_;
import org.ditto.feature.base.epoxymodels.ItemStatusNetworkModel_;
import org.ditto.feature.my.index.epoxymodels.ItemMyAccountModel_;
import org.ditto.feature.my.index.epoxymodels.ItemMyWordMemoryHeaderModel_;
import org.ditto.feature.my.index.epoxymodels.ItemMyWordMemoryModel_;
import org.ditto.feature.my.index.epoxymodels.ItemPagetipModel_;
import org.ditto.feature.my.index.epoxymodels.ItemWordCrawlerModel;
import org.ditto.feature.my.index.epoxymodels.ItemWordCrawlerModel_;
import org.ditto.feature.my.index.epoxymodels.ItemWordSortTypeModel_;
import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.ditto.lib.dbroom.kv.VoWordSummary;

public class MyController extends TypedEpoxyController<MyLiveDataHolder> {
    private final static String TAG = MyController.class.getSimpleName();
    private final static Gson gson = new Gson();

    public interface AdapterCallbacks {
        void onWordSortTypeChangedTo(VoWordSortType.WordSortType sortType);

        void onWordCrawlerClicked();
    }


    private final AdapterCallbacks callbacks;
    private final RecycledViewPool recycledViewPool;

    public MyController(AdapterCallbacks callbacks, RecycledViewPool recycledViewPool) {
        this.callbacks = callbacks;
        this.recycledViewPool = recycledViewPool;
        setDebugLoggingEnabled(true);
    }

    @AutoModel
    ItemRefreshHeaderModel_ headerModel_;

    @AutoModel
    ItemMyAccountModel_ accountModel_;

    @AutoModel
    ItemStatusNetworkModel_ itemStatusNetworkModel_;

    @AutoModel
    ItemWordSortTypeModel_ itemWordSortTypeModel_;


    @AutoModel
    ItemWordCrawlerModel_ itemWordCrawlerModel_;

    @AutoModel
    ItemPagetipModel_ footerModel_;

    @Override
    protected void buildModels(MyLiveDataHolder myLiveDataHolder) {


//        headerModel_.addTo(this);
        setupMyAccount(myLiveDataHolder);
        setupMyWordStats(myLiveDataHolder);
        if (myLiveDataHolder.wordSortType != null) {
            itemWordSortTypeModel_
                    .sortType(myLiveDataHolder.wordSortType)
                    .callbacks(callbacks)
                    .addTo(this);
        }

//        itemStatusNetworkModel_.addIf(status != null && Status.Code.END_ERROR.equals(status.code), this);
        itemWordCrawlerModel_
                .clickListener(new OnModelClickListener<ItemWordCrawlerModel_, ItemWordCrawlerModel.Holder>() {
                    @Override
                    public void onClick(ItemWordCrawlerModel_ itemWordCrawlerModel_, ItemWordCrawlerModel.Holder holder, View view, int i) {
                        callbacks.onWordCrawlerClicked();
                    }
                })
                .addTo(this);
    }

    private void setupMyAccount(MyLiveDataHolder myLiveDataHolder) {
        if (myLiveDataHolder.myProfile != null) {
            accountModel_
                    .name(myLiveDataHolder.myProfile.name)
                    .idno(myLiveDataHolder.myProfile.userNo)
                    .addTo(this);
        } else {
            accountModel_.addTo(this);
        }
    }

    private void setupMyWordStats(MyLiveDataHolder myLiveDataHolder) {
        if (myLiveDataHolder.myWordLevel1Stats != null
                && myLiveDataHolder.myWordLevel1Stats.value != null
                && myLiveDataHolder.myWordLevel1Stats.value.voWordSummary != null) {
            add(new ItemMyWordMemoryHeaderModel_().id("memoryheader_one").title("一级字表3500识字进度"));
            VoWordSummary voWordSummary = myLiveDataHolder.myWordLevel1Stats.value.voWordSummary;
            add(new ItemMyWordMemoryModel_().id("level_1_memory_7").title(WordUtils.getTitleByMemIdx(7)).number(voWordSummary.memory7).text(WordUtils.getDescByMemIdx(7)));
            add(new ItemMyWordMemoryModel_().id("level_1_memory_6").title(WordUtils.getTitleByMemIdx(6)).number(voWordSummary.memory6).text(WordUtils.getDescByMemIdx(6)));
            add(new ItemMyWordMemoryModel_().id("level_1_memory_5").title(WordUtils.getTitleByMemIdx(5)).number(voWordSummary.memory5).text(WordUtils.getDescByMemIdx(5)));
            add(new ItemMyWordMemoryModel_().id("level_1_memory_4").title(WordUtils.getTitleByMemIdx(4)).number(voWordSummary.memory4).text(WordUtils.getDescByMemIdx(4)));
            add(new ItemMyWordMemoryModel_().id("level_1_memory_3").title(WordUtils.getTitleByMemIdx(3)).number(voWordSummary.memory3).text(WordUtils.getDescByMemIdx(3)));
            add(new ItemMyWordMemoryModel_().id("level_1_memory_2").title(WordUtils.getTitleByMemIdx(2)).number(voWordSummary.memory2).text(WordUtils.getDescByMemIdx(2)));
            add(new ItemMyWordMemoryModel_().id("level_1_memory_1").title(WordUtils.getTitleByMemIdx(1)).number(voWordSummary.memory1).text(WordUtils.getDescByMemIdx(1)));
            add(new ItemMyWordMemoryModel_().id("level_1_memory_0").title(WordUtils.getTitleByMemIdx(0)).number(voWordSummary.memory0).text(WordUtils.getDescByMemIdx(0)));
        }
        if (myLiveDataHolder.myWordLevel2Stats != null
                && myLiveDataHolder.myWordLevel2Stats.value != null
                && myLiveDataHolder.myWordLevel2Stats.value.voWordSummary != null) {
            add(new ItemMyWordMemoryHeaderModel_().id("memoryheader_two").title("二级字表3000识字进度"));
            VoWordSummary voWordSummary = myLiveDataHolder.myWordLevel2Stats.value.voWordSummary;
            add(new ItemMyWordMemoryModel_().id("level_2_memory_7").title(WordUtils.getTitleByMemIdx(7)).number(voWordSummary.memory7).text(WordUtils.getDescByMemIdx(7)));
            add(new ItemMyWordMemoryModel_().id("level_2_memory_6").title(WordUtils.getTitleByMemIdx(6)).number(voWordSummary.memory6).text(WordUtils.getDescByMemIdx(6)));
            add(new ItemMyWordMemoryModel_().id("level_2_memory_5").title(WordUtils.getTitleByMemIdx(5)).number(voWordSummary.memory5).text(WordUtils.getDescByMemIdx(5)));
            add(new ItemMyWordMemoryModel_().id("level_2_memory_4").title(WordUtils.getTitleByMemIdx(4)).number(voWordSummary.memory4).text(WordUtils.getDescByMemIdx(4)));
            add(new ItemMyWordMemoryModel_().id("level_2_memory_3").title(WordUtils.getTitleByMemIdx(3)).number(voWordSummary.memory3).text(WordUtils.getDescByMemIdx(3)));
            add(new ItemMyWordMemoryModel_().id("level_2_memory_2").title(WordUtils.getTitleByMemIdx(2)).number(voWordSummary.memory2).text(WordUtils.getDescByMemIdx(2)));
            add(new ItemMyWordMemoryModel_().id("level_2_memory_1").title(WordUtils.getTitleByMemIdx(1)).number(voWordSummary.memory1).text(WordUtils.getDescByMemIdx(1)));
            add(new ItemMyWordMemoryModel_().id("level_2_memory_0").title(WordUtils.getTitleByMemIdx(0)).number(voWordSummary.memory0).text(WordUtils.getDescByMemIdx(0)));
        }
        if (myLiveDataHolder.myWordLevel3Stats != null
                && myLiveDataHolder.myWordLevel3Stats.value != null
                && myLiveDataHolder.myWordLevel3Stats.value.voWordSummary != null) {
            add(new ItemMyWordMemoryHeaderModel_().id("memoryheader_three").title("三级字表1605识字进度"));
            VoWordSummary voWordSummary = myLiveDataHolder.myWordLevel3Stats.value.voWordSummary;
            add(new ItemMyWordMemoryModel_().id("level_3_memory_7").title(WordUtils.getTitleByMemIdx(7)).number(voWordSummary.memory7).text(WordUtils.getDescByMemIdx(7)));
            add(new ItemMyWordMemoryModel_().id("level_3_memory_6").title(WordUtils.getTitleByMemIdx(6)).number(voWordSummary.memory6).text(WordUtils.getDescByMemIdx(6)));
            add(new ItemMyWordMemoryModel_().id("level_3_memory_5").title(WordUtils.getTitleByMemIdx(5)).number(voWordSummary.memory5).text(WordUtils.getDescByMemIdx(5)));
            add(new ItemMyWordMemoryModel_().id("level_3_memory_4").title(WordUtils.getTitleByMemIdx(4)).number(voWordSummary.memory4).text(WordUtils.getDescByMemIdx(4)));
            add(new ItemMyWordMemoryModel_().id("level_3_memory_3").title(WordUtils.getTitleByMemIdx(3)).number(voWordSummary.memory3).text(WordUtils.getDescByMemIdx(3)));
            add(new ItemMyWordMemoryModel_().id("level_3_memory_2").title(WordUtils.getTitleByMemIdx(2)).number(voWordSummary.memory2).text(WordUtils.getDescByMemIdx(2)));
            add(new ItemMyWordMemoryModel_().id("level_3_memory_1").title(WordUtils.getTitleByMemIdx(1)).number(voWordSummary.memory1).text(WordUtils.getDescByMemIdx(1)));
            add(new ItemMyWordMemoryModel_().id("level_3_memory_0").title(WordUtils.getTitleByMemIdx(0)).number(voWordSummary.memory0).text(WordUtils.getDescByMemIdx(0)));
        }
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }
}
