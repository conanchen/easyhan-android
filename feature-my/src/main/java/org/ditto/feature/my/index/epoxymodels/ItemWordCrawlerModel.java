package org.ditto.feature.my.index.epoxymodels;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import org.ditto.feature.my.R;
import org.ditto.feature.my.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass
public abstract class ItemWordCrawlerModel extends EpoxyModelWithHolder<ItemWordCrawlerModel.Holder> {
    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;


    @Override
    public void bind(Holder holder) {
        holder.crawler.setOnClickListener(clickListener);
    }

    @Override
    public void unbind(Holder holder) {
        holder.crawler.setOnClickListener(null);
    }

    public static class Holder extends EpoxyHolder {

        @BindView(R2.id.crawler)
        AppCompatButton crawler;


        View view;

        @Override
        protected void bindView(View itemView) {
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return totalSpanCount;
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.item_wordcrawler_model;
    }
}
