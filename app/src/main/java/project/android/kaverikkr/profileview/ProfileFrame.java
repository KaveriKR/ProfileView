package project.android.kaverikkr.profileview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

public class ProfileFrame extends FrameLayout {

    private Paint paint,borderPaint;
    private float DEFAUT_SHADOW_RADIUS = 2;
    private int canvasSize ;
    private float borderWidth;
    private int width, height;
    private int count = 0;
    private Path path;

    int[] colors = new int []{Color.GREEN,Color.RED,Color.BLUE,Color.MAGENTA,Color.CYAN};


    public ProfileFrame(@NonNull Context context) {
        this(context,null);
    }

    public ProfileFrame(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProfileFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }




    private void init(Context context, AttributeSet attributeSet, int defStyleAttr){

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);

        this.setWillNotDraw(false);
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.DKGRAY);
        borderPaint.setStrokeWidth(20);
        borderPaint.setStyle(Paint.Style.STROKE);


        TypedArray array = context.obtainStyledAttributes(attributeSet,R.styleable.ProfileView,defStyleAttr,0);
        if(array.getBoolean(R.styleable.ProfileView_border,true)){

            float defaultBorderWidth = DEFAUT_SHADOW_RADIUS * getContext().getResources().getDisplayMetrics().density;
            setBorderWidth(array.getDimension(R.styleable.ProfileView_borderRadius,defaultBorderWidth));
            setBorderColor(array.getColor(R.styleable.ProfileView_borderColor,Color.BLACK));
        }

    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width  =  w - ((int)borderWidth*2);
        height = h -((int)borderWidth*2);

        canvasSize = Math.max(width,height);
    }


    private void setBorderColor(int borderColor) {
        if(borderPaint!= null)
            borderPaint.setColor(borderColor);

        requestFocus();
        invalidate();
    }

    public void setBorderWidth(float dimension) {
        this.borderWidth = dimension;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int radius = (int) ((canvasSize-(borderWidth*2))/2);
        path = new Path();
        RectF rectF = new RectF(0,0,canvasSize,canvasSize);
        path.addRoundRect(rectF,radius,radius,Path.Direction.CW);

        super.onDraw(canvas);

        canvas.drawPath(path,borderPaint);

    }






}
