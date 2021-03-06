package org.ditto.feature.my.index;

import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.util.Log;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.TypedEpoxyController;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import org.ditto.feature.base.epoxymodels.ItemRefreshHeaderModel_;
import org.ditto.feature.base.epoxymodels.ItemStatusNetworkModel_;
import org.ditto.feature.my.index.epoxymodels.ItemMyWordDetailModel_;
import org.ditto.feature.my.index.epoxymodels.ItemPageModel_;
import org.ditto.feature.my.index.epoxymodels.ItemPagetipModel_;
import org.ditto.lib.dbroom.index.Word;

public class MyWordsController extends TypedEpoxyController<MyLiveWordsHolder> {
    private final static String TAG = MyWordsController.class.getSimpleName();
    private final static Gson gson = new Gson();
    private final AdapterCallbacks callbacks;
    private final RecycledViewPool recycledViewPool;
    @AutoModel
    ItemRefreshHeaderModel_ headerModel_;
    @AutoModel
    ItemStatusNetworkModel_ itemStatusNetworkModel_;
    @AutoModel
    ItemPagetipModel_ footerModel_;

    public MyWordsController(AdapterCallbacks callbacks, RecycledViewPool recycledViewPool) {
        this.callbacks = callbacks;
        this.recycledViewPool = recycledViewPool;
        setDebugLoggingEnabled(true);
    }

    @Override
    protected void buildModels(MyLiveWordsHolder myLiveDataHolder) {


//        headerModel_.addTo(this);

        if (myLiveDataHolder.words != null) {
            Log.i(TAG, String.format(" build %d Word", myLiveDataHolder.words.size()));
            for (Word word : myLiveDataHolder.words) {
                if (word != null && !Strings.isNullOrEmpty(word.word)) {
                    String pinyin = "";
                    if (word.pinyins != null && word.pinyins.size() > 1) {
                        pinyin = String.format("%s - %s", word.pinyins.get(0).pinyin, word.pinyins.get(1).pinyin);
                    } else if (word.pinyins != null && word.pinyins.size() > 0) {
                        pinyin = String.format("%s", word.pinyins.get(0).pinyin);
                    }
                    add(new ItemMyWordDetailModel_()
                            .id(word.word)
                            .word(word.word)
                            .pinyin(pinyin)
                            .memIdx(word.memIdx)
                            .memBrokenStrokes(word.memBrokenStrokes)
                            .clickListener((model, parentView, clickedView, position) -> {
                                // A model click listener is used instead of a normal click listener so that we can get
                                // the current position of the view. Since the view may have been moved when the colors
                                // were shuffled we can't rely on the position of the model when it was added here to
                                // be correct, since the model won't have been rebound when shuffled.
                                callbacks.onWordItemClicked(word, position);
                            })
                    );
                }
            }
        }

        footerModel_.addTo(this);

        for (int i = 0; i < 18; i++) {
            final int pageNo = i;
            add(new ItemPageModel_().id("pagenumber" + i).pageNo(i + 1)
                    .clickListener((model, parentView, clickedView, position) -> {
                        // A model click listener is used instead of a normal click listener so that we can get
                        // the current position of the view. Since the view may have been moved when the colors
                        // were shuffled we can't rely on the position of the model when it was added here to
                        // be correct, since the model won't have been rebound when shuffled.
                        callbacks.onPageClicked(pageNo);
                    })
            );
        }

    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }


    public interface AdapterCallbacks {
        void onWordItemClicked(Word word, int position);

        void onPageClicked(int pageno);
    }
}
