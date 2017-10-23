package org.ditto.feature.base.epoxymodels;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import org.ditto.feature.base.R;
import org.ditto.feature.base.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass
public abstract class ItemStatusNetworkModel extends EpoxyModelWithHolder<ItemStatusNetworkModel.Holder> {
    @EpoxyAttribute
    String url;  //广告url

    @EpoxyAttribute
    String ads;  //广告文字内容

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener adsClickListener;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener settingClickListener;

    @Override
    public void bind(Holder holder) {
        holder.text_ads_network.setOnClickListener(adsClickListener);
        holder.antenna_button.setOnClickListener(settingClickListener);
    }

    @Override
    public void unbind(Holder holder) {
    }

    public static class Holder extends EpoxyHolder {

        @BindView(R2.id.antenna_button)
        AppCompatImageButton antenna_button;

        @BindView(R2.id.text_ads_network)
        AppCompatTextView text_ads_network;

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
        return R.layout.item_statusnetwork_model;
    }
}
