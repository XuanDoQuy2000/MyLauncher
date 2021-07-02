package com.xuandq.mylauncher.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.xuandq.mylauncher.model.App;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Tool {
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) return;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) return;
        inputMethodManager.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static void visibleViews(long duration, View... views) {
        if (views == null) return;
        for (final View view : views) {
            if (view == null) continue;
            view.animate().alpha(1).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator()).withStartAction(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public static void invisibleViews(long duration, View... views) {
        if (views == null) return;
        for (final View view : views) {
            if (view == null) continue;
            view.animate().alpha(0).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public static void goneViews(long duration, View... views) {
        if (views == null) return;
        for (final View view : views) {
            if (view == null) continue;
            view.animate().alpha(0).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.GONE);
                }
            });
        }
    }


    public static void toast(Context context, int str) {
        Toast.makeText(context, context.getResources().getString(str), Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static boolean isPackageInstalled(String packageName,PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static int dp2px(float dp) {
        Resources resources = Resources.getSystem();
        float px = dp * resources.getDisplayMetrics().density;
        return (int) Math.ceil(px);
    }

    public static int sp2px(float sp) {
        Resources resources = Resources.getSystem();
        float px = sp * resources.getDisplayMetrics().scaledDensity;
        return (int) Math.ceil(px);
    }

    public static int clampInt(int target, int min, int max) {
        return Math.max(min, Math.min(max, target));
    }

    public static float clampFloat(float target, float min, float max) {
        return Math.max(min, Math.min(max, target));
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // single color bitmap will be created
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Point convertPoint(Point fromPoint, View fromView, View toView) {
        int[] fromCoordinate = new int[2];
        int[] toCoordinate = new int[2];
        fromView.getLocationOnScreen(fromCoordinate);
        toView.getLocationOnScreen(toCoordinate);

        Point toPoint = new Point(fromCoordinate[0] - toCoordinate[0] + fromPoint.x, fromCoordinate[1] - toCoordinate[1] + fromPoint.y);
        return toPoint;
    }

    public static boolean isIntentActionAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.size() > 0;
    }

    public static String getIntentAsString(Intent intent) {
        if (intent == null) {
            return "";
        } else {
            return intent.toUri(0);
        }
    }

    public static Intent getIntentFromString(String string) {
        try {
            return Intent.parseUri(string, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Intent getIntentFromApp(App app) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(app.getPackageName(), app.getClassName());
        return intent;
    }


    public static void removeIcon(Context context, String filename) {
        File file = new File(context.getFilesDir() + "/icons/" + filename + ".png");
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap captureViewAndBlur(Context context, View view) {
        //Create a Bitmap with the same dimensions as the View
        view.setDrawingCacheEnabled(true);
        Bitmap image = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        //Draw the view inside the Bitmap
        Canvas canvas = new Canvas(image);
        view.draw(canvas);

        blurBitmapWithRenderscript(RenderScript.create(context),image);

        //Make it frosty
        Paint paint = new Paint();
        paint.setXfermode(
                new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        ColorFilter filter =
                new LightingColorFilter(0xFFCCCCCC, 0x00444444); // lighten
//        ColorFilter filter =
//           new LightingColorFilter(0xFF7F7F7F, 0x00000000); // darken
        paint.setColorFilter(filter);
        canvas.drawBitmap(image, 0, 0, paint);
        return image;
    }

    public static void blurBitmapWithRenderscript(
            RenderScript rs, Bitmap bitmap2) {
        // this will blur the bitmapOriginal with a radius of 25
        // and save it in bitmapOriginal
        // use this constructor for best performance, because it uses
        // USAGE_SHARED mode which reuses memory
        final Allocation input =
                Allocation.createFromBitmap(rs, bitmap2);
        final Allocation output = Allocation.createTyped(rs,
                input.getType());
        final ScriptIntrinsicBlur script =
                ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        // must be >0 and <= 25
        script.setRadius(25f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap2);
    }

    public static Bitmap createBackGroundView(Context context, Bitmap wallpaper, View targetView){
        Bitmap temp = wallpaper.copy(Bitmap.Config.ARGB_8888,true);
        blurBitmapWithRenderscript(RenderScript.create(context), temp);
        Canvas canvas = new Canvas(temp);
        Paint paint = new Paint();
        paint.setXfermode(
                new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        ColorFilter filter =
                new LightingColorFilter(0xFFCCCCCC, 0x00444444); // lighten
//        ColorFilter filter =
//           new LightingColorFilter(0xFF7F7F7F, 0x00000000); // darken
        paint.setColorFilter(filter);
        canvas.drawBitmap(temp, 0, 0, paint);
        Matrix matrix = new Matrix();
        //half the size of the cropped bitmap
        //to increase performance, it will also
        //increase the blur effect.
        matrix.setScale(0.5f, 0.5f);
        Bitmap bitmap = Bitmap.createBitmap(temp,
                (int) targetView.getX(),
                (int) targetView.getY(),
                targetView.getMeasuredWidth(),
                targetView.getMeasuredHeight(),
                matrix,
                true);
        return bitmap;
    }

    public static Bitmap createBackGroundView(Context context, View backgroundView, View targetView) {
        Bitmap blurredBitmap = captureViewAndBlur(context,backgroundView);

        Matrix matrix = new Matrix();
        //half the size of the cropped bitmap
        //to increase performance, it will also
        //increase the blur effect.
        matrix.setScale(0.5f, 0.5f);
        Bitmap bitmap = Bitmap.createBitmap(blurredBitmap,
                (int) targetView.getX(),
                (int) targetView.getY(),
                targetView.getMeasuredWidth(),
                targetView.getMeasuredHeight(),
                matrix,
                true);


        return bitmap;
    }


}
