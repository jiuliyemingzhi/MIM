package net.jiuli.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import net.jiuli.common.R;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;


/**
 * //Created by r1907 on 2018/3/11.
 */

public class DragBubbleView extends View {

    private interface State {
        int STATE_DEFAULT = 0x00; //默认

        int STATE_DRAG = 0x01; // 未脱落 拖拽

        int STATE_MOVE = 0x02; // 移动

        int STATE_DISMISS = 0x03; //消失 Explosion

        int STATE_SHED = 0x04; //脱落 拖拽

    }


    private int mState; //气泡状态

    private float textWidth;

    private float mTextSize;

    private float dragCircleX;

    private float dragCircleY;

    private float oCircleX;

    private float oCircleY;

    private float oCircleRadius;

    private float dragRadius;

    private float dragCircleRadius;

    private float oRadius;

    private float maxD;

    private String mText;

    private Paint mBubblePaint;

    private Paint mTextPaint;

    private Paint mExplosionPaint;

    private Path mPath;

    private Rect mExplosionRect;

    private int index = 0;

    private Thread threadBoom;


    @IdRes
    private int[] mExplosionDrawables = {
            R.drawable.explosion_one,
            R.drawable.explosion_two,
            R.drawable.explosion_three,
            R.drawable.explosion_four,
            R.drawable.explosion_five,
    };

    private Bitmap[] mExplosionBitmaps;


    public DragBubbleView(Context context) {
        super(context);
        init(context, null);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mState = State.STATE_DEFAULT;
        init(context, attrs);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = null;
        if (attrs != null) {
            a = context.obtainStyledAttributes(attrs, R.styleable.DragBubbleView);
        }

        Resources resources = getResources();
        int mTextColor;
        int mBubbleColor;
        if (a != null) {
            mTextColor = a.getColor(R.styleable.DragBubbleView_textColor, Color.WHITE);
            mBubbleColor = a.getColor(R.styleable.DragBubbleView_bubbleColor, 0xc51162);
            oCircleRadius = a.getDimension(R.styleable.DragBubbleView_oCircleRadius, dipToPx(resources, 6f));
            dragCircleRadius = a.getDimension(R.styleable.DragBubbleView_dragRadius, dipToPx(resources, 14f));
            maxD = a.getDimension(R.styleable.DragBubbleView_maxD, dipToPx(resources, 150f));
            mTextSize = a.getDimension(R.styleable.DragBubbleView_textSize, dipToPx(resources, 20f));
            String text = a.getString(R.styleable.DragBubbleView_text);
            mText = TextUtils.isEmpty(text) ? "0" : text;

        } else {
            mBubbleColor = 0xc51162;
            oCircleRadius = dipToPx(resources, 6f);
            dragCircleRadius = dipToPx(resources, 14f);
            mText = "0";
            mTextColor = Color.WHITE;
            mTextSize = dipToPx(resources, 20f);
            maxD = 8 * dragCircleRadius;
        }

        if (a != null) a.recycle();


        mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBubblePaint.setColor(mBubbleColor);
        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setStyle(Paint.Style.FILL);

        mPath = new Path();

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        textWidth = mTextPaint.measureText(mText);
        dragRadius = textWidth / 2 > dragCircleRadius / 3 ? dragCircleRadius : oCircleRadius;

        mExplosionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mExplosionPaint.setFilterBitmap(true);
        mExplosionRect = new Rect();

        mExplosionBitmaps = new Bitmap[mExplosionDrawables.length];
        for (int i = 0; i < mExplosionDrawables.length; i++) {
            mExplosionBitmaps[i] = BitmapFactory.decodeResource(resources, mExplosionDrawables[i]);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        ViewGroup parent = (ViewGroup) getParent();
        do {
            parent.setClipChildren(false);
        }
        while (parent.getParent() instanceof ViewGroup && (parent = (ViewGroup) parent.getParent()) != null);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;


        switch (hMode) {
            case MeasureSpec.EXACTLY:
                height = hSize;
                break;
            case MeasureSpec.AT_MOST:
                height = (int) (mTextSize * 1.3);
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                height = getSuggestedMinimumHeight();
                break;
        }

        switch (wMode) {
            case MeasureSpec.EXACTLY:
                width = wSize;
                break;
            case MeasureSpec.AT_MOST:
                width = (int) (mTextPaint.measureText(mText) + height);
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                width = getSuggestedMinimumWidth();
                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        oCircleX = getWidth() / 2;
        oCircleY = getHeight() / 2;
        dragCircleX = oCircleX;
        dragCircleY = oCircleY;
    }

    @SuppressLint("DefaultLocale")
    public void setText(final int count) {
        if (count < 1) return;

        if ((threadBoom != null && threadBoom.isAlive())) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    mText = count > 99 ? "99+" : String.valueOf(count);
                    dragCircleX = oCircleX;
                    dragCircleY = oCircleY;
                    textWidth = mTextPaint.measureText(mText);
                    mState = State.STATE_DEFAULT;
                    postInvalidate();
                }
            };
        } else {
            mText = count > 99 ? "99+" : String.valueOf(count);
            dragCircleX = oCircleX;
            dragCircleY = oCircleY;
            textWidth = mTextPaint.measureText(mText);
            mState = State.STATE_DEFAULT;
            invalidate();
        }
    }

    private Runnable runnable;

    private void isDisappear() {
        if (onExplosionListener != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    onExplosionListener.onExplosion();
                }
            });
        }

        if (runnable != null) {
            new Thread(runnable).start();
        }
    }

    public void setOnExplosionListener(OnExplosionListener onExplosionListener) {
        this.onExplosionListener = onExplosionListener;
    }

    private OnExplosionListener onExplosionListener;

    public interface OnExplosionListener {
        void onExplosion();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mState == State.STATE_DISMISS) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                dragCircleX = event.getX();
                dragCircleY = event.getY();
                index = 0;
                mState = State.STATE_DRAG;
                oRadius = oCircleRadius;
                break;
            case MotionEvent.ACTION_MOVE:
                dragCircleX = event.getX();
                dragCircleY = event.getY();

                if (Math.hypot(dragCircleX - oCircleX, dragCircleY - oCircleX) > maxD) {
                    mState = State.STATE_SHED;
                } else if (Math.hypot(dragCircleX - oCircleX, dragCircleY - oCircleX) < maxD * .6f) {
                    mState = State.STATE_DEFAULT;
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                double distance = Math.hypot(dragCircleX - oCircleX, dragCircleY - oCircleY);
                if (distance > maxD || (distance > .66f * distance && mState == State.STATE_SHED)) {
                    mState = State.STATE_DISMISS;
                    runnable = null;
                    float explosionRadius = dragCircleRadius * 2;

                    mExplosionRect.set(
                            (int) (dragCircleX - explosionRadius),
                            (int) (dragCircleY - explosionRadius),
                            (int) (dragCircleX + explosionRadius),
                            (int) (dragCircleY + explosionRadius));

                    if (threadBoom == null || !threadBoom.isAlive()) {
                        threadBoom = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (index < 5) {
                                    if (mState == State.STATE_DEFAULT) {
                                        break;
                                    }
                                    postInvalidate();
                                    index++;
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                postInvalidate();
                                isDisappear();
                            }
                        });
                        threadBoom.start();
                    }

                } else {
                    mState = State.STATE_MOVE;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            final float dragFinalX = dragCircleX;
                            final float dragFinalY = dragCircleY;

                            for (int i = 0; i <= 20; i++) {
                                if (mState != State.STATE_MOVE) {
                                    break;
                                }
                                float scale = (20 - i) / 20f * (float) Math.cos(Math.PI / 10 * i);
                                dragCircleX = oCircleX + scale * (dragFinalX - oCircleX);
                                dragCircleY = oCircleY + scale * (dragFinalY - oCircleY);
                                postInvalidate();
                                try {
                                    Thread.sleep(16);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            mState = State.STATE_DEFAULT;
                        }

                    }).start();
                }
                break;
        }
        return true;

    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mState != State.STATE_SHED && mState != State.STATE_DISMISS) {
            drawElasticPath(canvas);
            canvas.drawCircle(oCircleX, oCircleY, oRadius, mBubblePaint);
        }

        if (mState != State.STATE_DISMISS) {
            if (textWidth / 2 > dragCircleRadius / 3) {
                drawDragPath(canvas);
            } else {
                canvas.drawCircle(dragCircleX, dragCircleY, dragCircleRadius, mBubblePaint);
            }
            drawText(canvas);
        } else if (index < 5) {
            canvas.drawBitmap(mExplosionBitmaps[index], null, mExplosionRect, mExplosionPaint);
        }
        super.onDraw(canvas);
    }


    private void drawDragPath(Canvas canvas) {
        mPath.reset();
        mPath.addCircle(
                dragCircleX - textWidth / 2 + dragCircleRadius / 3,
                dragCircleY, dragCircleRadius,
                Path.Direction.CCW);
        mPath.addCircle(
                dragCircleX + textWidth / 2 - dragCircleRadius / 3,
                dragCircleY, dragCircleRadius,
                Path.Direction.CCW);
        mPath.addRect(
                dragCircleX - textWidth / 2 + dragCircleRadius / 2,
                dragCircleY - dragCircleRadius,
                dragCircleX + textWidth / 2 - dragCircleRadius / 2,
                dragCircleY + dragCircleRadius, Path.Direction.CCW);
        canvas.drawPath(mPath, mBubblePaint);
    }

    private void drawElasticPath(Canvas canvas) {
        float dx = dragCircleX - oCircleX;
        float dy = dragCircleY - oCircleX;

        float distance = (float) Math.hypot(dx, dy);

        oRadius = oCircleRadius * (1 - 2 / 3f * distance / maxD);

        float cos = dx / distance;
        float sin = dy / distance;

        float scale = oRadius / (oRadius + dragCircleRadius);

        float x = oCircleX + scale * (dragCircleX - oCircleX);
        float y = oCircleY + scale * (dragCircleY - oCircleY);


        mPath.reset();

        mPath.moveTo(oCircleX - oRadius * sin, oCircleY + oRadius * cos);
        mPath.quadTo(x, y, dragCircleX - dragRadius * sin, dragCircleY + dragRadius * cos);

        mPath.lineTo(dragCircleX + dragRadius * sin, dragCircleY - dragRadius * cos);
        mPath.quadTo(x, y, oCircleX + oRadius * sin, oCircleY - oRadius * cos);

        mPath.close();

        canvas.drawPath(mPath, mBubblePaint);
    }


    protected void drawText(Canvas canvas) {
        float textX = dragCircleX - textWidth / 2;
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float dy = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
        float textY = dragCircleY + dy;
        canvas.drawText(mText, textX, textY, mTextPaint);
    }


    public static float dipToPx(Resources resources, float dp) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
