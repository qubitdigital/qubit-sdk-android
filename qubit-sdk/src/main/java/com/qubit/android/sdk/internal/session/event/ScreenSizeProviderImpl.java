package com.qubit.android.sdk.internal.session.event;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import static android.os.Build.VERSION_CODES.*;

public class ScreenSizeProviderImpl implements ScreenSizeProvider {

  private Context appContext;
  private boolean isInitialized;
  private Point sizePx;
  private double sizeIn;

  public ScreenSizeProviderImpl(Context appContext) {
    this.appContext = appContext;
  }

  @Override
  public int getWidthPx() {
    init();
    return sizePx.x;
  }

  @Override
  public int getHeightPx() {
    init();
    return sizePx.y;
  }

  @Override
  public double getSizeInches() {
    init();
    return sizeIn;
  }

  private void init() {
    if (isInitialized) {
      return;
    }

    WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
    Display defaultDisplay = windowManager.getDefaultDisplay();

    sizePx = getSizePx(defaultDisplay);
    sizeIn = evaluateSizeIn(defaultDisplay, sizePx);

    isInitialized = true;
  }

  @SuppressWarnings("checkstyle:illegalcatch")
  private static Point getSizePx(Display display) {
    DisplayMetrics metrics = new DisplayMetrics();
    display.getMetrics(metrics);

    // includes window decorations (statusbar bar/menu bar)
    if (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT < JELLY_BEAN_MR1) {
      try {
        int w = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
        int h = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
        return new Point(w, h);
      } catch (Exception ignored) {
      }
    }
    // includes window decorations (statusbar bar/menu bar)
    if (Build.VERSION.SDK_INT >= JELLY_BEAN_MR1) {
      try {
        Point realSize = new Point();
        Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
        return realSize;
      } catch (Exception ignored) {
      }
    }

    // since SDK_INT = 1;
    return new Point(metrics.widthPixels, metrics.heightPixels);
  }

  private static double evaluateSizeIn(Display display, Point sizePx) {
    DisplayMetrics metrics = new DisplayMetrics();
    display.getMetrics(metrics);

    double x = Math.pow(sizePx.x / metrics.xdpi, 2);
    double y = Math.pow(sizePx.y / metrics.ydpi, 2);
    return Math.sqrt(x + y);
  }

}
