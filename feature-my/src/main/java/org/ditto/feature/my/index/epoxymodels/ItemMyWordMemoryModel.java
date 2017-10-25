package org.ditto.feature.my.index.epoxymodels;

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

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass
public abstract class ItemMyWordMemoryModel extends EpoxyModelWithHolder<ItemMyWordMemoryModel.Holder> {
    @EpoxyAttribute
    String title;
    @EpoxyAttribute
    int number;
    @EpoxyAttribute
    String text;

    @Override
    public void bind(Holder holder) {
        holder.title.setText(title);
        holder.number.setText(String.format("%04d",number));
        holder.text.setText(text);
    }

    @Override
    public void unbind(Holder holder) {
    }

    public static class Holder extends EpoxyHolder {

        @BindView(R2.id.title)
        AppCompatTextView title;

        @BindView(R2.id.number)
        AppCompatTextView number;

        @BindView(R2.id.text)
        AppCompatTextView text;

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
        return R.layout.item_mywordmemory_model;
    }
}
