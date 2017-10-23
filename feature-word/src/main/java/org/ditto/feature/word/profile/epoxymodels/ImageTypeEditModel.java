package org.ditto.feature.word.profile.epoxymodels;

import android.view.View;
import android.widget.RadioButton;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import org.ditto.feature.word.R;
import org.ditto.feature.word.R2;
import org.ditto.feature.word.profile.ImageProfileEditController;
import org.easyhan.common.grpc.HanziLevel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass
public abstract class ImageTypeEditModel extends EpoxyModelWithHolder<ImageTypeEditModel.Holder> {
    @EpoxyAttribute
    String type;


    @EpoxyAttribute(DoNotHash)
    ImageProfileEditController.AdapterCallbacks callbacks;


    @Override
    public void bind(Holder holder) {
        holder.radioNormal.setChecked(HanziLevel.ONE.name().equals(type));
        holder.radioPoster.setChecked(HanziLevel.TWO.name().equals(type));
        holder.radioSexy.setChecked(HanziLevel.THREE.name().equals(type));

        holder.radioNormal.setOnClickListener(v -> {
            holder.clearRadioButtons();
            holder.radioNormal.setChecked(true);
            callbacks.onTypeChanged(HanziLevel.ONE);
        });
        holder.radioPoster.setOnClickListener(v -> {
            holder.clearRadioButtons();
            holder.radioPoster.setChecked(true);
            callbacks.onTypeChanged(HanziLevel.TWO);
        });
        holder.radioSexy.setOnClickListener(v -> {
            holder.clearRadioButtons();
            holder.radioSexy.setChecked(true);
            callbacks.onTypeChanged(HanziLevel.THREE);
        });
    }

    @Override
    public void unbind(Holder holder) {

    }

    public static class Holder extends EpoxyHolder {

        @BindView(R2.id.radioNormal)
        RadioButton radioNormal;

        @BindView(R2.id.radioPoster)
        RadioButton radioPoster;

        @BindView(R2.id.radioSexy)
        RadioButton radioSexy;

        @BindView(R2.id.radioPorn)
        RadioButton radioPorn;

        @BindView(R2.id.radioSecret)
        RadioButton radioSecret;


        View view;

        @Override
        protected void bindView(View itemView) {
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }

        void clearRadioButtons() {
            radioNormal.setChecked(false);
            radioPoster.setChecked(false);
            radioSexy.setChecked(false);
            radioPorn.setChecked(false);
            radioSecret.setChecked(false);
        }


    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return 1;
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.image_typeedit_model;
    }
}
