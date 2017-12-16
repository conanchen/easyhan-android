package org.ditto.feature.word.index;

import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.util.Log;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.TypedEpoxyController;
import com.google.gson.Gson;

import org.ditto.feature.word.index.epoxymodels.ItemPagetipModel_;
import org.ditto.feature.word.index.epoxymodels.ItemWord1000Model_;

public class Words1000Controller extends TypedEpoxyController<String[]> {
    private final static String TAG = Words1000Controller.class.getSimpleName();
    private final static Gson gson = new Gson();
    private final AdapterCallbacks callbacks;
    private final RecycledViewPool recycledViewPool;
    @AutoModel
    ItemPagetipModel_ footerModel_;

    public Words1000Controller(AdapterCallbacks callbacks, RecycledViewPool recycledViewPool) {
        this.callbacks = callbacks;
        this.recycledViewPool = recycledViewPool;
        setDebugLoggingEnabled(true);
    }

    @Override
    protected void buildModels(String[] words) {

        if (words != null) {
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                add(new ItemWord1000Model_()
                        .id(word+i)
                        .word(word)
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

        footerModel_.addTo(this);
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }

    public interface AdapterCallbacks {
        void onWordItemClicked(String word, int position);
    }
}
