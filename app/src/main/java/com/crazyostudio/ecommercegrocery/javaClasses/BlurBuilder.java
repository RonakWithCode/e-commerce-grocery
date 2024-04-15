package com.crazyostudio.ecommercegrocery.javaClasses;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

public class BlurBuilder {

    private static final float BLUR_RADIUS = 25f;

    public static Bitmap blur(Context context, View view) {
        Bitmap bitmap = getScreenshot(view);
        return blur(context, bitmap);
    }

    public static Bitmap blur(Context context, Bitmap image) {
        // Create a blurred bitmap
        Bitmap outputBitmap = Bitmap.createBitmap(image);
        RenderScript rs = RenderScript.create(context);

        // Create allocations for input and output bitmaps
        Allocation input = Allocation.createFromBitmap(rs, image);
        Allocation output = Allocation.createTyped(rs, input.getType());

        // Create a script for intrinsic blur
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(BLUR_RADIUS);

        // Perform the blur operation
        script.setInput(input);
        script.forEach(output);

        // Copy the output allocation to the blurred bitmap
        output.copyTo(outputBitmap);

        // Destroy RenderScript resources
        rs.destroy();

        return outputBitmap;
    }

    private static Bitmap getScreenshot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
