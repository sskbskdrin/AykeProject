package com.ayke.library.abstracts;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.view.View;

public interface IView {

    @SuppressWarnings("unchecked")
    default <T extends View> T getView(View parent, @IdRes int id) {
        return (T) parent.findViewById(id);
    }

    <T extends View> T getView(@IdRes int id);

    default void setBackground(@IdRes int viewId, Drawable drawable) {
        setBackground(getView(viewId), drawable);
    }

    default void setBackground(View view, Drawable drawable) {
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    }

    default void setBackgroundResource(View view, @DrawableRes int resId) {
        if (view != null) {
            view.setBackgroundResource(resId);
        }
    }

    default void setBackgroundColor(View view, @ColorInt int color) {
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }

    default void showView(boolean show, @IdRes int... ids) {
        if (ids != null) {
            int visible = show ? View.VISIBLE : View.GONE;
            for (int id : ids) {
                View view = getView(id);
                if (view != null) {
                    view.setVisibility(visible);
                }
            }
        }
    }

    default void showView(boolean show, View... views) {
        if (views != null) {
            int visible = show ? View.VISIBLE : View.GONE;
            for (View view : views) {
                view.setVisibility(visible);
            }
        }
    }
}
