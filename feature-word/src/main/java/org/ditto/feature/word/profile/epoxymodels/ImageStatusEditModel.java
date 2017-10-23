package org.ditto.feature.word.profile.epoxymodels;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.CheckBox;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import org.ditto.feature.word.R;
import org.ditto.feature.word.R2;
import org.ditto.feature.word.profile.ImageProfileEditController;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass
public abstract class ImageStatusEditModel extends EpoxyModelWithHolder<ImageStatusEditModel.Holder> {
    @EpoxyAttribute
    Boolean active;

    @EpoxyAttribute
    Boolean toprank;

    @EpoxyAttribute
    Long visitCount;

    @EpoxyAttribute
    Long created;

    @EpoxyAttribute
    Long lastUpdated;

    @EpoxyAttribute(DoNotHash)
    ImageProfileEditController.AdapterCallbacks callbacks;

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void bind(Holder holder) {
        holder.checkActive.setChecked(active);
        holder.checkActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbacks.onStatusChanged(holder.checkActive.isChecked(), holder.checkToprank.isChecked());
            }
        });
        holder.checkToprank.setChecked(toprank);
        holder.checkToprank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbacks.onStatusChanged(holder.checkActive.isChecked(), holder.checkToprank.isChecked());
            }
        });

        holder.visitCountValue.setText(String.valueOf(visitCount));

        holder.createdValue.setText(sdf.format(new Date(created)));

        holder.lastUpdatedValue.setText(sdf.format(new Date(lastUpdated)));
    }

    @Override
    public void unbind(Holder holder) {

    }

    public static class Holder extends EpoxyHolder {
        @BindView(R2.id.checkActive)
        CheckBox checkActive;

        @BindView(R2.id.checkToprank)
        CheckBox checkToprank;

        @BindView(R2.id.visitCountValue)
        AppCompatTextView visitCountValue;


        @BindView(R2.id.createdValue)
        AppCompatTextView createdValue;

        @BindView(R2.id.lastUpdatedValue)
        AppCompatTextView lastUpdatedValue;


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
        return R.layout.image_statusedit_model;
    }
}
