package project.android.kaverikkr.profileview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class CircleImageView extends AppCompatImageView {

    private static final String TAG ="CircleImageView" ;

    private Paint paint,borderPaint;
    private float DEFAUT_SHADOW_RADIUS = 2;
    private int canvasSize ;

    private static final int INVALID_POINTER_ID = -1;

    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;


    private float borderWidth;
    private Bitmap image,bb;
    private Drawable drawable;

    private float mPosX =0,mPosY=0;

    private boolean setCroped = false;
    private float mLastTouchX;
    private float mLastTouchY;
    private Canvas canvas1;
    private int width, height;

    private ScaleGestureDetector mScaleDetector;
    private int left,top,right,bottom;
    private GestureDetector gestureDetector;
    private GestureListener gestureListener;

    private float mScaleFactor = 1.1f;


    public CircleImageView(Context context) {
        this(context,null);

    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attributeSet, int defStyleAttr){
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.parseColor("#40000000"));
        this.setDrawingCacheEnabled(true);


        mScaleDetector = new ScaleGestureDetector(context,new ScaleListner());
        gestureListener = new GestureListener();
        gestureDetector = new GestureDetector(context,gestureListener);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width  = w;
        height = h;

        canvasSize = Math.max(width,height);
        int radius = (int) ((canvasSize-(borderWidth*2))/2);
        mPosX = radius/2;
         mPosY = radius/2;

         left = this.getLeft();
         right = this.getRight();
         top = this.getTop();
         bottom = this.getBottom();

    }



    @Override
    protected void onDraw(Canvas canvas) {

        loadBitmap();

        if(image==null)
            return;

        canvas.save();
        canvas.scale(mScaleFactor,mScaleFactor);
        canvas.drawBitmap(image,mPosX,mPosY,null);
        canvas.restore();
    }







    private void loadBitmap() {
        if (drawable == getDrawable())
            return;

        drawable = getDrawable();

        image = drawableToBitmap(drawable);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            // Create Bitmap object out of the drawable
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);

        if(!setCroped) {
            mScaleDetector.onTouchEvent(event);
            final int action = event.getAction();
            switch (action) {

                case MotionEvent.ACTION_DOWN: {

                    final float x = event.getX();
                    final float y = event.getY();

                    mLastTouchX = x;
                    mLastTouchY = y;

                    mActivePointerId = event.getPointerId(0);
                    break;

                }
                case MotionEvent.ACTION_MOVE: {
                    final int pointerIndex = event.findPointerIndex(mActivePointerId);
                     float x = event.getX(pointerIndex);
                     float y = event.getY(pointerIndex);

                    if(x>right - 40|| x<left + 40|| y>bottom - 40|| y <top + 50){
                        Log.e(TAG, "onTouchEvent: " );
                        x = mLastTouchX;
                        y = mLastTouchY;
                    }

                    if (!mScaleDetector.isInProgress()) {
                        final float dx = x - mLastTouchX;
                        final float dy = y - mLastTouchY;

                        mPosX += dx;
                        mPosY += dy;

                        invalidate();
                    }

                    mLastTouchX = x;
                    mLastTouchY = y;

                    break;
                }
                case MotionEvent.ACTION_UP: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {
                    // Extract the index of the pointer that left the touch sensor
                    final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                            >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mLastTouchX = event.getX(newPointerIndex);
                        mLastTouchY = event.getY(newPointerIndex);
                        mActivePointerId = event.getPointerId(newPointerIndex);
                    }
                    break;
                }
            }
        }
            return true;

    }


    private class ScaleListner extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d("scale", "onScale: " );
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,Math.min(mScaleFactor,5.0f));
            invalidate();
            return true;
        }


    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            setCroped = !setCroped;
            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
            return true;
        }
    }


}
