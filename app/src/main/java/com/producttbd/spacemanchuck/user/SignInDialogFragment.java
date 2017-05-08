package com.producttbd.spacemanchuck.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.producttbd.spacemanchuck.R;


/**
 * Class that shows a dialog to encourage the user to sign in and calls a listener appropriately.
 */

public class SignInDialogFragment extends DialogFragment {

    private SignInOutRequestListener mListener;

    public SignInDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignInOutRequestListener) {
            mListener = (SignInOutRequestListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignInOutRequestListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setMessage(R.string.sign_in_why)
                .setPositiveButton(R.string.sign_in_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onSignInRequested();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onSignOutRequested();
                    }
                })
                .create();
    }
}
