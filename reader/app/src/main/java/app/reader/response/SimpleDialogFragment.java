package app.reader.response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class SimpleDialogFragment extends DialogFragment {
    private AlertDialog.Builder dialog;
    private Context actionContext;

    public void setDialog(AlertDialog.Builder dialog) {
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return dialog.create();
    }
}
