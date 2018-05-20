package net.jiuli.common.face;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;
import java.util.HashSet;


public class AnimatedGifDrawable extends AnimationDrawable {

    private int mCurrentIndex = 0;


    public final HashSet<View> views = new HashSet<>();


    public AnimatedGifDrawable(InputStream is, final TextView target, int size) {

        views.add(target);
        GifDecoder decoder = new GifDecoder();
        decoder.read(is);

        for (int i = 0; i < decoder.getFrameCount(); i++) {
            Bitmap bitmap = decoder.getFrame(i);
            BitmapDrawable drawable = new BitmapDrawable(target.getResources(), bitmap);
            // Explicitly set the bounds in order for the frames to display
            drawable.setBounds(0, 0, size <= 0 ? bitmap.getWidth() : size, size <= 0 ? bitmap.getHeight() : size);
            addFrame(drawable, decoder.getDelay(i));
            if (i == 0) {
                // Also set the bounds for this container drawable
                setBounds(0, 0, size <= 0 ? bitmap.getWidth() : size, size <= 0 ? bitmap.getHeight() : size);
            }
        }
    }

    /**
     * Naive method to proceed to next frame. Also notifies listener.
     */
    public void nextFrame() {
        mCurrentIndex = (mCurrentIndex + 1) % getNumberOfFrames();
        update();
    }

    private void update() {
        for (View view : views) {
            view.postInvalidate();
        }
    }

    /**
     * Return display duration for current frame
     */
    public int getFrameDuration() {
        return getDuration(mCurrentIndex);
    }

    /**
     * Return drawable for current frame
     */
    public Drawable getDrawable() {
        return getFrame(mCurrentIndex);
    }


}