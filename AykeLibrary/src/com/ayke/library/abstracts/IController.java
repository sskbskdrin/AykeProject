package com.ayke.library.abstracts;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public interface IController extends IText, IImage {
    String DIALOG_KEY = "loading_dialog";
    String TOAST_KEY = "toast_key";

    Map<String, Object> MAP = new HashMap<>();

    Context getContext();

    default boolean isFinish() {
        return getContext() == null;
    }

    default void showLoadingDialog(int resId) {
        showLoadingDialog(getContext().getString(resId));
    }

    default void showLoadingDialog(String content) {
        Dialog dialog;
        if (MAP.containsKey(DIALOG_KEY)) {
            dialog = (Dialog) MAP.get(DIALOG_KEY);
            if (dialog != null) {
                dialog.dismiss();
            }
        }
        if (!isFinish()) {
            dialog = generateLoadingDialog(content);
            MAP.put(DIALOG_KEY, dialog);
            dialog.show();
        }
    }

    default void hideLoadingDialog() {
        if (MAP.containsKey(DIALOG_KEY)) {
            Dialog dialog = (Dialog) MAP.get(DIALOG_KEY);
            if (dialog != null) {
                dialog.dismiss();
            }
            MAP.remove(DIALOG_KEY);
        }
    }

    default Dialog generateLoadingDialog(String content) {
        return ProgressDialog.show(getContext(), "", content, true, false);
    }

    default void showToast(String text) {
        showToast(text, false);
    }

    default void showToast(String text, boolean isLong) {
        Toast toast;
        if (MAP.containsKey(TOAST_KEY)) {
            toast = (Toast) MAP.get(TOAST_KEY);
            if (toast != null) {
                toast.cancel();
            }
        }
        toast = Toast.makeText(getContext(), text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        MAP.put(TOAST_KEY, toast);
        toast.show();
    }
}
