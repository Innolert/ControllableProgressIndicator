package toyberman.controllableprogressindicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import toyberman.controllableprogressindicator.Utils.GeneralUtils;

/**
 * Created by maximtoyberman on 12/28/16.
 */

public class ControllableProgressIndicator extends View {
    /**
     * minimal dots gap
     */
    private static final int DEFAULT_GAP = 12;
    /**
     * minimal dot size
     */
    private final int DEFAULT_DOT_SIZE = 8;
    private final int DEFAULT_NUMBER_FONT_SIZE = 6;
    /**
     * Number of drawn dots on the screen
     */
    private int mDotsNumber;
    /**
     * Dot diameter
     */
    private int mDotDiameter;
    /**
     * Screen density
     */
    private int mDensity;
    /**
     * Gap between the circles
     */
    private int mDotGap;
    /**
     * Dot radius
     */
    private int mDotRadius;
    /**
     * Dot centers
     */
    private float[] dotCenterX;
    /**
     * The size of the font of the number.
     */
    private int mNumberFontSize;

    private Paint mUnselectedPaint;
    private Paint mNumbersPaint;

    private Bitmap mUnSelectedItemBitmap;
    private int mDotTopY;
    private int mDotCenterY;
    private int mDotBottomY;

    public ControllableProgressIndicator(Context context) {
        this(context, null, 0);
    }

    public ControllableProgressIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControllableProgressIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
        initPainters();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawUnselected(canvas, mUnselectedPaint);
    }

    private void drawUnselected(Canvas canvas, Paint mUnselectedPaint) {

        for (int page = 0; page < mDotsNumber; page++) {
            Paint test = new Paint();
            test.setColor(Color.BLACK);
            test.setStrokeWidth(30);
            canvas.drawBitmap(mUnSelectedItemBitmap, dotCenterX[page], mDotTopY, mUnselectedPaint);
            float dist = ((mNumbersPaint.descent() + mNumbersPaint.ascent()) / 2);
            canvas.drawText(""+page,dotCenterX[page]+mDotRadius +dist,mDotTopY +mDotRadius - dist,mNumbersPaint);
            if(page == mDotsNumber -1){

            }
            else{
                canvas.drawLine(dotCenterX[page] +mDotDiameter, mDotRadius, dotCenterX[page] + mDotDiameter + mDotGap, mDotRadius, mUnselectedPaint);
            }
        }
    }

    private void initPainters() {
        mUnselectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnselectedPaint.setColor(Color.parseColor("#c2c2c2"));
        mUnselectedPaint.setStrokeWidth(25);
        //Numbers paint
        mNumbersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int alpha = (int) (0.31 * 255.0f);
        mNumbersPaint.setColor(Color.argb(alpha, 0, 0, 0));
        mNumbersPaint.setTextSize(mNumberFontSize);
        Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/Rubik-Regular.ttf");
        mNumbersPaint.setTypeface(typeFace);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mDensity = (int) context.getResources().getDisplayMetrics().density;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ControllableProgressIndicator);

        mDotsNumber = typedArray.getInt(R.styleable.ControllableProgressIndicator_dotsNumber, 1);
        mDotGap = typedArray.getDimensionPixelSize(R.styleable.ControllableProgressIndicator_dotGap, DEFAULT_GAP * mDensity);
        mDotDiameter = typedArray.getDimensionPixelSize(R.styleable.ControllableProgressIndicator_dotDiameter, DEFAULT_DOT_SIZE * mDensity);
        mNumberFontSize = typedArray.getDimensionPixelSize(R.styleable.ControllableProgressIndicator_numberFontSize, DEFAULT_NUMBER_FONT_SIZE * mDensity);
        mDotRadius = mDotDiameter / 2;
        typedArray.recycle();

        mUnSelectedItemBitmap = GeneralUtils.drawableToBitmap(ContextCompat.getDrawable(getContext(), R.drawable.nonselecteditem_dot));

    }

    private int getRequiredWidth() {
        return mDotsNumber * mDotDiameter + (mDotsNumber - 1) * mDotGap;
    }

    private int getDesiredHeight() {
        return getPaddingTop() + mDotDiameter + getPaddingBottom();
    }

    private int getDesiredWidth() {
        return getPaddingLeft() + getRequiredWidth() + getPaddingRight();
    }

    @SuppressLint("SwitchIntDef")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredHeight = getDesiredHeight();
        int height;
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                height = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(desiredHeight, MeasureSpec.getSize(heightMeasureSpec));
                break;
            default:
                height = desiredHeight;
                break;
        }

        int desiredWidth = getDesiredWidth();
        int width;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                width = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(desiredWidth, MeasureSpec.getSize(widthMeasureSpec));
                break;
            default:
                width = desiredWidth;
                break;
        }
        setMeasuredDimension(width, height);
        calculateDotPositions(width);
    }

    private void calculateDotPositions(int width) {
        int density = (int) getResources().getDisplayMetrics().density;
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = width - getPaddingRight();

        int requiredWidth = getRequiredWidth();
        float startLeft = left + ((right - left - requiredWidth) / 2); //ltr

        if (mDotsNumber > 1) {
            int new_gap = (width - (mDotsNumber * mDotDiameter)) / (mDotsNumber - 1);
            mDotGap = requiredWidth < width ? mDotGap : new_gap / density;
        }
        dotCenterX = new float[mDotsNumber];
        for (int i = 0; i < mDotsNumber; i++) {
            dotCenterX[i] = startLeft + i * (mDotDiameter + mDotGap);
        }

        mDotTopY = top;
        mDotCenterY = top + mDotRadius;
        mDotBottomY = top + mDotDiameter;

    }

}
