package com.ayke.library.abstracts;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.widget.ImageView;

public interface IImage extends IView {

    default void setImageDrawable(@IdRes int viewId, Drawable drawable) {
        setImageDrawable(getView(viewId), drawable);
    }

    default void setImageResource(@IdRes int viewId, @DrawableRes int resId) {
        setImageResource(getView(viewId), resId);
    }

    default void setImageBitmap(@IdRes int viewId, Bitmap bitmap) {
        setImageBitmap(getView(viewId), bitmap);
    }

    default void setImageDrawable(ImageView view, Drawable drawable) {
        if (view != null) {
            view.setImageDrawable(drawable);
        }
    }

    default void setImageResource(ImageView view, @DrawableRes int resId) {
        if (view != null) {
            view.setImageResource(resId);
        }
    }

    default void setImageBitmap(ImageView view, Bitmap bitmap) {
        if (view != null) {
            view.setImageBitmap(bitmap);
        }
    }

}
