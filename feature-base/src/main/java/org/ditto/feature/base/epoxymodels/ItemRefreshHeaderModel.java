package org.ditto.feature.base.epoxymodels;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import org.ditto.feature.base.R;
import org.ditto.feature.base.R2;
import org.ditto.lib.repository.model.Status;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass
public abstract class ItemRefreshHeaderModel extends EpoxyModelWithHolder<ItemRefreshHeaderModel.Holder> {
    @EpoxyAttribute
    Status status;  //广告url

    @EpoxyAttribute
    String ads;  //广告文字内容


    @Override
    public void bind(Holder holder) {
        if (Status.Code.START.equals(status.code)) {
            holder.text.setText("Start");
        } else if (Status.Code.LOADING.equals(status.code)) {
            holder.text.setText("Loading");
        } else if (Status.Code.END_DISCONNECTED.equals(status.code)) {
            holder.text.setText("END_DISCONNECTED");
        }else if (Status.Code.END_ERROR.equals(status.code)) {
            holder.text.setText("END_ERROR");
        }
    }

    @Override
    public void unbind(Holder holder) {
    }

    public static class Holder extends EpoxyHolder {

        @BindView(R2.id.image)
        AppCompatImageView image;

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
        return R.layout.item_refreshheader_model;
    }
}
