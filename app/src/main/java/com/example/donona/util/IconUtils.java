package com.example.donona.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import vn.vietmap.vietmapsdk.annotations.Icon;
import vn.vietmap.vietmapsdk.annotations.IconFactory;

public class IconUtils {
    public static Icon drawableToIcon(Context context, @DrawableRes int id, @ColorInt int colorRes) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
        if (vectorDrawable == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, colorRes);
        vectorDrawable.draw(canvas);

        return IconFactory.getInstance(context).fromBitmap(bitmap);
    }
}
