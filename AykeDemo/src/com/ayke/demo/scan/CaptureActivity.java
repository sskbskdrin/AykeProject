/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ayke.demo.scan;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayke.demo.R;
import com.ayke.library.util.SysUtils;
import com.open.zxing.tesseract.DictionaryEntity;
import com.open.zxing.tesseract.UnzipFromAssets;
import com.open.zxing.tools.IScanResultListener;
import com.open.zxing.tools.ScanManager;
import com.open.zxing.tools.ViewfinderView;

import java.io.IOException;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback, IScanResultListener {
    private static final String TAG = CaptureActivity.class.getSimpleName();

    private ViewfinderView mFinderView;
    private SurfaceView surfaceView;
    private boolean change;
    private boolean hasSurface;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SysUtils.init(getApplicationContext());
        ScanManager.init(this, this);
        setContentView(R.layout.activity_capture);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UnzipFromAssets.unZip(getApplicationContext(), "dicts.zip", getFilesDir().getAbsolutePath(), false);
                    UnzipFromAssets.unZip(getApplicationContext(), "tessdata.zip", getFilesDir().getAbsolutePath(),
							false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        hasSurface = false;
        mFinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);

        surfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("ayke", "onGlobalLayout surfaceView=" + surfaceView.getHolder().getSurfaceFrame().toString());
                if (change) {
                    change = false;
                    changeSurfaceView();
                    ScanManager.getInstance().changeOrientation();
                }
            }
        });

        findViewById(R.id.scan_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanManager.getInstance().changeMode(ScanManager.ScanMode.CODE);
            }
        });
        findViewById(R.id.scan_word).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanManager.getInstance().changeMode(ScanManager.ScanMode.WORD);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ayke", "onResume ");
        ScanManager.getInstance().onResume();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            ScanManager.getInstance().initCamera(surfaceHolder); // The activity was paused but
            changeSurfaceView();
            // not stopped, so the surface
            // still exists. Therefore surfaceCreated() won't be called, so init the camera here.
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onPause() {
        Log.d("ayke", "onPause ");
        ScanManager.getInstance().onPause();
        if (!hasSurface) {
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                // Handle these events so they don't launch the Camera app
                return true;
            // Use volume up/down to turn on light
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                ScanManager.getInstance().getCameraManager().setTorch(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                ScanManager.getInstance().getCameraManager().setTorch(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            Log.d("ayke", "SCREEN_ORIENTATION_PORTRAIT");
        } else if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            Log.d("ayke", "SCREEN_ORIENTATION_LANDSCAPE");
        } else if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            Log.d("ayke", "SCREEN_ORIENTATION_REVERSE_PORTRAIT");
        } else if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            Log.d("ayke", "SCREEN_ORIENTATION_REVERSE_LANDSCAPE");
        } else {
            Log.d("ayke", "SCREEN_OTHER " + newConfig.orientation);
        }
        Log.d("ayke", "SCREEN_ " + getWindowManager().getDefaultDisplay().getRotation());
        change = true;
        super.onConfigurationChanged(newConfig);
    }

    private void changeSurfaceView() {
        int width = ((ViewGroup) surfaceView.getParent()).getWidth();
        int height = ((ViewGroup) surfaceView.getParent()).getHeight();
        Point point = new Point(width, height);
        ScanManager.getInstance().getCameraManager().findBestSurfacePoint(point);
        point = ScanManager.getInstance().getCameraManager().getSurfacePoint();
        if (point == null) return;
        surfaceView.getLayoutParams().width = point.x;
        surfaceView.getLayoutParams().height = point.y;
        surfaceView.requestLayout();
        mFinderView.getLayoutParams().width = point.x;
        mFinderView.getLayoutParams().height = point.y;
        mFinderView.requestLayout();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            ScanManager.getInstance().initCamera(holder);
            changeSurfaceView();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    private DictionaryEntity mDictionaryEntity;

    public void handleWord(final String word, final Bitmap bitmap) {
        if (mDictionaryEntity != null) {
            if (mDictionaryEntity.getName() != null && word.compareToIgnoreCase(mDictionaryEntity.getName()) == 0)
                return;
        }
        String url = "http://apis.baidu.com/apistore/tranlateservice/dictionary?from=en&to=zh" + "&query=";
        //		VolleyJsonObjectRequest<DictionaryEntity> request = new
        //				VolleyJsonObjectRequest<DictionaryEntity>(url + word, new DictionaryEntity(), new
        //				VolleyJsonObjectRequest.IVolleyRequestListener() {
        //					@Override
        //					public void onVolleySuccess(IParseResponse result, boolean isSuccess, int id) {
        //						mDictionaryEntity = (DictionaryEntity) result;
        //						if (mDictionaryEntity == null || TextUtils.isEmpty(mDictionaryEntity.getName())) {
        //							((TextView) findViewById(R.id.text)).setText(word);
        //						} else {
        //							((TextView) findViewById(R.id.text)).setText(mDictionaryEntity.toString());
        //						}
        ////						((ImageView) findViewById(R.id.img)).setImageBitmap(bitmap);
        //					}
        //
        //					@Override
        //					public void onVolleyError(VolleyError error, int id) {
        //						((TextView) findViewById(R.id.text)).setText(word);
        //					}
        //				});
        //		Map<String, String> map = new HashMap<String, String>();
        //		map.put("apikey", "85bcbaf63a7b08e52614cc6771005a7f");
        //		request.setHeaders(map);
        //		VolleyEncapsulation.startRequest(request);
    }

    @Override
    public void onScanResult(String result, Bitmap bitmap) {
        if (ScanManager.getInstance().getScanMode() == ScanManager.ScanMode.WORD) handleWord(result, bitmap);
        else {
            ((TextView) findViewById(R.id.text)).setText(result);
            ((ImageView) findViewById(R.id.img)).setImageBitmap(bitmap);
        }
    }

    @Override
    public ViewfinderView getFinderView() {
        return mFinderView;
    }
}
