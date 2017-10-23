package org.ditto.feature.my.index.epoxymodels;

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
public abstract class ItemMyWordModel extends EpoxyModelWithHolder<ItemMyWordModel.Holder> {
    @EpoxyAttribute
    String word;


    @EpoxyAttribute
    int memIdx;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(Holder holder) {
        holder.word.setOnClickListener(clickListener);
        holder.word.setText(word);
        holder.memIdx.setText(String.format("%01d",memIdx));
    }

    @Override
    public void unbind(Holder holder) {
        holder.word.setOnClickListener(null);

    }

    public static class Holder extends EpoxyHolder {

        @BindView(R2.id.title)
        AppCompatTextView word;


        @BindView(R2.id.memory)
        AppCompatTextView memIdx;

        View view;

        @Override
        protected void bindView(View itemView) {
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return 1;
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.item_myword_model;
    }
}
