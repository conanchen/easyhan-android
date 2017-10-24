package org.ditto.feature.my.index.epoxymodels;

import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.ditto.feature.my.R;
import org.ditto.feature.my.R2;
import org.ditto.feature.my.index.MyController;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass
public abstract class ItemWordSortTypeModel extends EpoxyModelWithHolder<ItemWordSortTypeModel.Holder> {
    private final static String TAG=ItemWordSortTypeModel.class.getSimpleName();
    @EpoxyAttribute
    VoWordSortType.WordSortType sortType;

    @EpoxyAttribute(DoNotHash)
    MyController.AdapterCallbacks callbacks;


    @Override
    public void bind(Holder holder) {
        switch (sortType) {
            case MEMORY:
                holder.radio_memory.setChecked(true);
                break;
            case USAGE:
                holder.radio_usage.setChecked(true);
                break;
            case SEQUENCE:
            default:
                holder.radio_seq.setChecked(true);
                break;
        }
        holder.radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int rid) {
                Log.i(TAG,String.format("onCheckedChanged(RadioGroup radioGroup, @IdRes int rid=%x)",rid));
               if(rid == R.id.radio_seq) {
                   callbacks.onWordSortTypeChangedTo(VoWordSortType.WordSortType.SEQUENCE);
               }else if(rid == R.id.radio_memory) {
                   callbacks.onWordSortTypeChangedTo(VoWordSortType.WordSortType.MEMORY);
               }else if(rid == R.id.radio_usage) {
                   callbacks.onWordSortTypeChangedTo(VoWordSortType.WordSortType.USAGE);
               }
            }
        });
    }

    @Override
    public void unbind(Holder holder) {
    }

    public static class Holder extends EpoxyHolder {

        @BindView(R2.id.radio_group)
        RadioGroup radio_group;

        @BindView(R2.id.radio_seq)
        RadioButton radio_seq;

        @BindView(R2.id.radio_memory)
        RadioButton radio_memory;

        @BindView(R2.id.radio_usage)
        RadioButton radio_usage;

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
        return R.layout.item_wordsorttype_model;
    }
}
