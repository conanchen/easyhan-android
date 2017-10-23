package org.ditto.feature.visitor.models;

import android.view.View;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;


import org.ditto.feature.visitor.R;
import org.ditto.feature.visitor.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass
public abstract class ItemVisitorModel extends EpoxyModelWithHolder<ItemVisitorModel.Holder> {
    @EpoxyAttribute
    String title;
    @EpoxyAttribute
    String detaill;
    @EpoxyAttribute
    String require;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;


    @Override
    public void bind(Holder holder) {
//        holder.title.setText(title);
//        holder.detail.setText(detaill);
//        holder.view.setOnClickListener(clickListener);
    }

    @Override
    public void unbind(Holder holder) {
        // Release resources and don't leak listeners as this view goes back to the view pool
        holder.view.setOnClickListener(null);
    }


    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        // We want the header to take up all spans so it fills the screen width
        return totalSpanCount;
    }


    public static class Holder extends EpoxyHolder {
        @BindView(R2.id.ipaddr) TextView title;
        @BindView(R2.id.lastUpdated) TextView detail;
        @BindView(R2.id.detail) TextView require;

        View view;

        @Override
        protected void bindView(View itemView) {
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }


    @Override
    protected int getDefaultLayout() {
        return R.layout.item_visitor_model;
    }

}
