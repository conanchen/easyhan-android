package org.ditto.feature.my.index;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.ditto.feature.my.R;
import org.ditto.lib.dbroom.index.Word;

/**
 * Created by mellychen on 2017/12/16.
 */

public class BrokenStrokesDialogFragment extends AppCompatDialogFragment {
    // Use this instance of the interface to deliver action events
    BrokenStrokesDialogListener mListener;

    private Word word;

    public BrokenStrokesDialogFragment setWord(Word word) {
        this.word = word;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_brokenstrokes, null);
        AppCompatTextView broken_message_title = view.findViewById(R.id.broken_message_title);
        broken_message_title.setText(String.format("拆字记忆法：%s",word.word));
        AppCompatEditText appCompatEditText = view.findViewById(R.id.broken_message);
        appCompatEditText.setText(StringUtils.isEmpty(word.memBrokenStrokes) ? "" : word.memBrokenStrokes);
        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onBrokenStrokesMessageChanged(appCompatEditText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (BrokenStrokesDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement BrokenStrokesDialogListener");
        }
    }


    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface BrokenStrokesDialogListener {
        public void onBrokenStrokesMessageChanged(String brokenStrokesMessage);

    }
}
