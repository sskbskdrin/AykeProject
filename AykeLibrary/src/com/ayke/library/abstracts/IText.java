package com.ayke.library.abstracts;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.util.TypedValue;
import android.widget.TextView;

public interface IText extends IView {

    default void setText(@IdRes int viewId, @StringRes int resId) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setText(resId);
        }
    }

    default void setText(TextView view, CharSequence text) {
        if (view != null) {
            view.setText(text != null ? text : "");
        }
    }

    default void setTextColor(TextView view, @ColorInt int color) {
        if (view != null) {
            view.setTextColor(color);
        }
    }

    default void setTextSize(TextView view, float sp) {
        if (view != null) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        }
    }

    default void setTextSize(TextView view, @DimenRes int dimenId) {
        if (view != null) {
            Context c = view.getContext();
            Resources r;

            if (c == null) {
                r = Resources.getSystem();
            } else {
                r = c.getResources();
            }
            view.setTextSize(r.getDimension(dimenId));
        }
    }

}
