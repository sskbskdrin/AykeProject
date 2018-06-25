package com.ayke.library.abstracts;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

/**
 * @author ex-keayuan001
 * @date 17/10/19
 */
public class BaseActivity extends FragmentActivity implements IController {

    protected Context mContext;
    protected int screenWidth;
    protected int screenHeight;
    private boolean isStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ViewGroup contentView = getView(android.R.id.content);
        View childAt = contentView.getChildAt(0);
        if (childAt != null) {
            childAt.setFitsSystemWindows(true);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.KITKAT) {
            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusHeight()));
            view.setBackgroundColor(Color.CYAN);
            contentView.addView(view);
        }
    }

    public int getStatusHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return (int) getResources().getDimension(resourceId);
    }

    private View.OnClickListener onBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickBack();
        }
    };

    @Override
    protected void onStart() {
        isStop = false;
        super.onStart();
        View view = getBackView();
        if (view != null) {
            view.setOnClickListener(onBackClickListener);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        setText(getTitleView(), title);
    }

    protected TextView getTitleView() {
        return null;
    }

    protected View getBackView() {
        return null;
    }

    protected void onClickBack() {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        isStop = false;
        super.onResume();
    }

    @Override
    protected void onStop() {
        isStop = true;
        super.onStop();
    }

    public boolean isStop() {
        return isStop;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean isFinish() {
        return isFinishing();
    }

    protected void openActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    protected boolean checkPermission(String[] permission, int requestCode) {
        boolean flag = true;
        for (String str : permission) {
            if (!checkPermission(str, requestCode)) {
                flag = false;
            }
        }
        if (!flag && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, requestCode);
        }
        return flag;
    }

    protected boolean checkPermission(String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission) || Manifest
                            .permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                        showToast("请开启存储权限，否则无法使用该功能！", true);
                    } else if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
                        showToast("请开启录音权限，否则无法使用该功能！", true);
                    } else if (Manifest.permission.CAMERA.equals(permission)) {
                        showToast("请开启相机权限，否则无法使用拍照功能！", true);
                    }
                } else {
                    requestPermissions(new String[]{permission}, requestCode);
                }
                return false;
            }
        }
        return true;
    }
}
