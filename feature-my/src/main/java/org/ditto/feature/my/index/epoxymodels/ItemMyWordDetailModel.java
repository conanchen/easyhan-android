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
public abstract class ItemMyWordDetailModel extends EpoxyModelWithHolder<ItemMyWordDetailModel.Holder> {
    @EpoxyAttribute
    String word;

    @EpoxyAttribute
    String pinyin;


    @EpoxyAttribute
    int memIdx;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;


    @Override
    public void bind(Holder holder) {
        holder.word.setText(word);
        holder.pinyin.setText(pinyin);
        holder.title.setText(getTitleByMemIdx(memIdx));
//        holder.detail.setText(detaill);
        holder.view.setOnClickListener(clickListener);
    }

    private static String[] ms = new String[]{"〇", "①", "②", "③", "④", "⑤", "⑥", "⑦"};

    private String getTitleByMemIdx(int memIdx) {
        if (memIdx < 7) {
            return ms[memIdx];
        } else {
            return ms[7];
        }
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
        @BindView(R2.id.word)
        AppCompatTextView word;
        @BindView(R2.id.pinyin)
        AppCompatTextView pinyin;
        @BindView(R2.id.title)
        AppCompatTextView title;
        @BindView(R2.id.detail)
        AppCompatTextView detail;
        @BindView(R2.id.time)
        AppCompatTextView time;

        View view;

        @Override
        protected void bindView(View itemView) {
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }


    @Override
    protected int getDefaultLayout() {
        return R.layout.item_myword_detail_model;
    }

}
