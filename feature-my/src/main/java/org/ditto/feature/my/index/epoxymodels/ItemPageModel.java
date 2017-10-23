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
public abstract class ItemPageModel extends EpoxyModelWithHolder<ItemPageModel.Holder> {
    @EpoxyAttribute
    int pageNo;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(Holder holder) {
        holder.idx.setOnClickListener(clickListener);
        holder.idx.setText(String.format("%02d",pageNo));
    }

    @Override
    public void unbind(Holder holder) {
        holder.idx.setOnClickListener(null);
    }

    public static class Holder extends EpoxyHolder {


        @BindView(R2.id.idx)
        AppCompatTextView idx;

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
        return R.layout.item_page_model;
    }
}
