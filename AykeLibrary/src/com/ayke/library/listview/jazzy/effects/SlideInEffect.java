package com.ayke.library.listview.jazzy.effects;

import android.view.View;

import com.ayke.library.listview.jazzy.JazzyEffect;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class SlideInEffect implements JazzyEffect {
    @Override
    public void initView(View item, int position, int scrollDirection) {
        ViewHelper.setTranslationY(item, item.getHeight() / 2 * scrollDirection);
    }

    @Override
    public void setupAnimation(View item, int position, int scrollDirection, ViewPropertyAnimator animator) {
        animator.translationYBy(-item.getHeight() / 2 * scrollDirection);
    }
}
