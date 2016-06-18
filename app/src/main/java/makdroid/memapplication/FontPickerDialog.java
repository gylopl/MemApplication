package makdroid.memapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Grzecho on 17.06.2016.
 */
public class FontPickerDialog extends DialogFragment {
    public static final String SELECTED_FONT_SIZE = "SELECTED_FONT_SIZE";

    FontPickerDialogListener mListener;
    private int mSelectedFontSize = 0;


    public static FontPickerDialog newInstance(int selectedFontSize) {
        FontPickerDialog fragment = new FontPickerDialog();

        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_FONT_SIZE, selectedFontSize);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mSelectedFontSize = getArguments().getInt(SELECTED_FONT_SIZE, 0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_change)
                .setSingleChoiceItems(R.array.font_size, mSelectedFontSize,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v("which", "" + which);
                                mSelectedFontSize = which;
                            }
                        })
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(mSelectedFontSize);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }
                );

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FontPickerDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FontPickerDialogListener");
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @NonNull
    public void show(AppCompatActivity context) {
        String tag = "TagFontPickerDialog";
        show(context.getSupportFragmentManager(), tag);
    }


    public interface FontPickerDialogListener {
        void onDialogPositiveClick(int selectedFontSize);
    }


}
