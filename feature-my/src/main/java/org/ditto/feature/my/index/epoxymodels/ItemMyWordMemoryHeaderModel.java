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
public abstract class ItemMyWordMemoryHeaderModel extends EpoxyModelWithHolder<ItemMyWordMemoryHeaderModel.Holder> {
    @EpoxyAttribute
    String title;

    @Override
    public void bind(Holder holder) {
        holder.text.setText(title);
    }

    @Override
    public void unbind(Holder holder) {
    }

    public static class Holder extends EpoxyHolder {
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
        return R.layout.item_mywordmemoryheader_model;
    }
}
