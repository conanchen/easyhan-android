package org.ditto.feature.word.profile;

import android.support.v7.widget.RecyclerView.RecycledViewPool;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Typed2EpoxyController;

import org.ditto.feature.word.profile.epoxymodels.ImageInfoUrlEditModel_;
import org.ditto.feature.word.profile.epoxymodels.ImageStatusEditModel_;
import org.ditto.feature.word.profile.epoxymodels.ImageTitleEditModel_;
import org.ditto.feature.word.profile.epoxymodels.ImageTypeEditModel_;
import org.ditto.feature.word.profile.epoxymodels.ImageUrlEditModel_;
import org.ditto.lib.dbroom.index.Word;
import org.easyhan.common.grpc.HanziLevel;

public class ImageProfileEditController extends Typed2EpoxyController<Word, Boolean> {
    public interface AdapterCallbacks {
        void onUrlChanged(String url);

        void onStatusChanged(boolean active, boolean toprank);

        void onTitleChanged(String title);

        void onTypeChanged(HanziLevel type);

        void onInfoUrlChanged(String url);

    }


    private final AdapterCallbacks callbacks;
    private final RecycledViewPool recycledViewPool;


    @AutoModel
    ImageUrlEditModel_ imageUrlEditModel_;

    @AutoModel
    ImageInfoUrlEditModel_ imageInfoUrlEditModel_;

    @AutoModel
    ImageTitleEditModel_ imageTitleEditModel_;

    @AutoModel
    ImageTypeEditModel_ imageTypeEditModel_;

    @AutoModel
    ImageStatusEditModel_ imageStatusEditModel_;


    public ImageProfileEditController(AdapterCallbacks callbacks, RecycledViewPool recycledViewPool) {
        this.callbacks = callbacks;
        this.recycledViewPool = recycledViewPool;
        setDebugLoggingEnabled(true);
    }

    @Override
    protected void buildModels(Word word, Boolean isUpdate) {
        imageUrlEditModel_.url(word.word)
                .isUpdate(isUpdate)
                .callbacks(callbacks)
                .addTo(this);

        imageInfoUrlEditModel_.url(word.word)
                .callbacks(callbacks)
                .addTo(this);

        imageTitleEditModel_.title(word.word)
                .callbacks(callbacks)
                .addTo(this);

        imageTypeEditModel_.type(word.word)
                .callbacks(callbacks)
                .addTo(this);
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }
}
