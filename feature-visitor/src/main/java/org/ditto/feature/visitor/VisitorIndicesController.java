package org.ditto.feature.visitor;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.RecycledViewPool;

import com.airbnb.epoxy.TypedEpoxyController;

import org.ditto.feature.visitor.models.ItemVisitorModel_;
import org.ditto.lib.dbroom.index.IndexVisitor;

import java.util.List;

public class VisitorIndicesController extends TypedEpoxyController<List<IndexVisitor>> {
    @Override
    protected void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface AdapterCallbacks {

        void onItemUserClicked(IndexVisitor carousel, int colorPosition);

        void onItemUgroupClicked(IndexVisitor carousel, int colorPosition);
    }


    private final AdapterCallbacks callbacks;
    private final RecycledViewPool recycledViewPool;

    public VisitorIndicesController(AdapterCallbacks callbacks, RecycledViewPool recycledViewPool) {
        this.callbacks = callbacks;
        this.recycledViewPool = recycledViewPool;
        setDebugLoggingEnabled(true);
    }

    @Override
    protected void buildModels(List<IndexVisitor> carousels) {

        if (carousels != null) {
            for (IndexVisitor user : carousels) {
                add(new ItemVisitorModel_()
                        .id(user.uuid)
                        .title(user.title)
                        .detaill(user.detail)
                        .clickListener((model, parentView, clickedView, position) -> {
                            // A model click listener is used instead of a normal click listener so that we can get
                            // the current position of the view. Since the view may have been moved when the colors
                            // were shuffled we can't rely on the position of the model when it was added here to
                            // be correct, since the model won't have been rebound when shuffled.
                            callbacks.onItemUserClicked(user, position);
                        }));
            }
        }
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }


}