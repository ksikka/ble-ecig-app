package ecig.app;
// Adapted from
//  https://github.com/Ken-Yang/AndroidPieChart/blob/master/AndroidPieChart/src/net/kenyang/piechart/PieChart.java

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class RatioPieView extends View {

    public interface OnSelectedListener {
        public abstract void onSelected(int iSelectedIndex);
    }

    private OnSelectedListener onSelectedListener = null;

    private static final String TAG = RatioPieView.class.getName();
    public static final String ERROR_NOT_EQUAL_TO_100 = "NOT_EQUAL_TO_100";
    public static final String ERROR_RADIUS_VALUE = "Radius must be percentage";
    private static final int DEGREE_360 = 360;
    private static String[] PIE_COLORS 	= null;
    private static int iColorListSize 	= 0;
    private final DecimalFormat   decimalFormat =   new   DecimalFormat("##0");


    private Paint paintPieFill;
    private Paint paintPieBorder;
    private Paint paintText;
    private ArrayList<PieChartData> alPieCharData = new ArrayList<PieChartData>();

    private int iDisplayWidth, iDisplayHeight;
    private int iSelectedIndex 	= -1;
    private int iCenterPoint 	= 0;
    private int iShift			= 0;
    private int iDataSize		= 0;
    private int iPaddingLeft    = 0;
    private int iPaddingTop     = 100;
    private int iR              = 0;

    private RectF rectPie 			= null;
    private RectF rectLegendIcon[] 			= null;

    private float fDensity 		= 0.0f;
    private float fLegendLeft         = 0.0f;
    private float fLegendIconSize     = 0.0f;
    private float fMargin   = 0.0f;
    private boolean bIsAlignCenter = true;
    private boolean bIsShowLegend = false;

    public RatioPieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        PIE_COLORS = getResources().getStringArray(R.array.colors);
        iColorListSize = PIE_COLORS.length;

        fnGetDisplayMetrics(context);
        iShift 	      = (int) fnGetRealPxFromDp(20);
        iPaddingLeft  = (int) fnGetRealPxFromDp(5);
        iPaddingTop   = (int) fnGetRealPxFromDp(15);
        fMargin       = fnGetRealPxFromDp(15);
        fLegendIconSize     = fnGetRealPxFromDp(10);




        paintText = new Paint();
        paintText.setTextSize(getResources().getDimension(R.dimen.legend_font_size));
        paintText.setColor(Color.WHITE);

        // used for paint circle
        paintPieFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPieFill.setStyle(Paint.Style.FILL);

        // used for paint border
        paintPieBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPieBorder.setStyle(Paint.Style.STROKE);
        paintPieBorder.setStrokeWidth(fnGetRealPxFromDp(3));
        paintPieBorder.setColor(Color.WHITE);
        Log.i(TAG, "PieChart init");

    }

    // set listener
    public void setOnSelectedListener(OnSelectedListener listener){
        this.onSelectedListener = listener;
    }

    public void setColor(int index) {
        // check whether the data size larger than color list size
        if (index>=iColorListSize){
            paintPieFill.setColor(Color.parseColor(PIE_COLORS[index%iColorListSize]));
        } else {
            paintPieFill.setColor(Color.parseColor(PIE_COLORS[index]));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
        System.out.println("look ma, no hands");
        Log.i(TAG, "onDraw");

        // draw legend
        if (bIsShowLegend) {
            for (int i = 0; i < iDataSize; i++) {
                setColor(i);
                final PieChartData tmpData = alPieCharData.get(i);
                final float fEndAngle = alPieCharData.get(i).fPercentage;
                canvas.drawRect(rectLegendIcon[i], paintPieFill);

                // draw text
                canvas.drawText(String.format("%1s %2$.2f%%", tmpData.strTitle, fEndAngle),
                        fLegendLeft+fnGetRealPxFromDp(15),
                        iDisplayHeight-fLegendIconSize*(6-i)- fMargin*(6-i),
                        paintText);
            }
        }

        float fStartAngle    = 0.0f;
        for (int i = 0; i < iDataSize; i++) {

            final PieChartData tmpData = alPieCharData.get(i);
            // convert percentage to angle
            final float fEndAngle = tmpData.fPercentage / 100 * DEGREE_360;
            // if the part of pie was selected then change the coordinate

            if (iSelectedIndex == i) {
                canvas.save(Canvas.MATRIX_SAVE_FLAG);
                float fAngle = fStartAngle + fEndAngle / 2;
                double dxRadius = Math.toRadians((fAngle + DEGREE_360) % DEGREE_360);
                float fY = (float) Math.sin(dxRadius);
                float fX = (float) Math.cos(dxRadius);
                canvas.translate(fX * iShift, fY * iShift);
            }

            setColor(i);
            canvas.drawArc(rectPie, fStartAngle, fEndAngle, true, paintPieFill);

            // if the part of pie was selected then draw a border
            if (iSelectedIndex == i) {
                canvas.drawArc(rectPie, fStartAngle, fEndAngle, true, paintPieBorder);
                canvas.restore();
            }
            fStartAngle = fStartAngle + fEndAngle;
        }*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(300, 600);
        return;
/*
        if (iDisplayWidth == 0) {
            // get screen size
            iDisplayWidth = MeasureSpec.getSize(widthMeasureSpec);
            iDisplayHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            // do not get size again
            setMeasuredDimension(getMeasuredWidth(), iDisplayHeight);
            return;
        }

        if (iDisplayWidth>iDisplayHeight) {
            iDisplayWidth = iDisplayHeight;
        }

		/*
		 *  determine the rectangle size
        */ /*
        iCenterPoint = iDisplayWidth / 2;
        this.iR = iCenterPoint * this.iR / 100;
        iDisplayHeight = iR * 2 + iPaddingTop+iPaddingTop;
        if (rectPie == null) {
            int iLeft = iCenterPoint - iR;
            int iTop  = iPaddingTop;
            int iRight = iCenterPoint + iR;
            int iBottom = iR * 2 +iPaddingTop;

            if (!bIsAlignCenter) {
                iLeft = iPaddingLeft;
                iRight = iR * 2 + iPaddingLeft;
                iCenterPoint = iR;
            }

            rectPie = new RectF(iLeft,
                    iTop,
                    iRight,
                    iBottom);

        }

        fLegendLeft  = iCenterPoint + iR + fLegendIconSize;
        float fRight    = iCenterPoint + iR + fLegendIconSize + fLegendIconSize;
        for (int i = iDataSize-1; i >= 0; i--) {
            rectLegendIcon[i].set(fLegendLeft,
                    iDisplayHeight-fLegendIconSize-fLegendIconSize*(6-i)- fMargin*(6-i),
                    fRight,
                    iDisplayHeight-fLegendIconSize*(6-i)- fMargin*(6-i));
        }
        //setMeasuredDimension(iDisplayWidth, iDisplayHeight);
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = 100;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            System.out.println("CHOICE 1");

            // height = heightSize;
            height = desiredHeight;
            System.out.println(height);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            System.out.println("CHOICE 2");
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            System.out.println("CHOICE 3");
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("yoman dont touch");
        // get degree of the touch point
        double dx = Math.atan2(event.getY() - iR, event.getX() - iCenterPoint);
        float fDegree = (float) (dx / (2 * Math.PI) * DEGREE_360);
        fDegree = (fDegree + DEGREE_360) % DEGREE_360;

        // get the percent of the selected degree
        float fSelectedPercent = fDegree * 100 / DEGREE_360;

        // check which pie was selected
        float fTotalPercent = 0;
        for (int i = 0; i < iDataSize; i++) {
            fTotalPercent += alPieCharData.get(i).fPercentage;
            if (fTotalPercent > fSelectedPercent) {
                iSelectedIndex = i;
                break;
            }
        }
        if (onSelectedListener != null){
            onSelectedListener.onSelected(iSelectedIndex);
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    private void fnGetDisplayMetrics(Context cxt){
        final DisplayMetrics dm = cxt.getResources().getDisplayMetrics();
        fDensity = dm.density;
    }

    private float fnGetRealPxFromDp(float fDp){
        return (fDensity!=1.0f) ? fDensity*fDp : fDp;
    }

    public void setSelectedIndex(int iSelectedIndex) {
        this.iSelectedIndex = iSelectedIndex;
        this.invalidate();
    }

    public void setAdapter(ArrayList<PieChartData> alPercentage) throws Exception {
        this.alPieCharData = alPercentage;
        iDataSize         = alPercentage.size();

        if (rectLegendIcon == null) {
            rectLegendIcon    = new RectF[iDataSize];
            for (int i = 0; i < iDataSize; i++) {
                if (rectLegendIcon[i]==null) {
                    rectLegendIcon[i] = new RectF();
                }
            }
        }

        float fSum = 0;
        for (int i = 0; i < iDataSize; i++) {
            fSum+=alPercentage.get(i).fPercentage;
        }

        if (!decimalFormat.format(fSum).equals("100")){
            Log.e(TAG,ERROR_NOT_EQUAL_TO_100);
            iDataSize = 0;
            throw new Exception(ERROR_NOT_EQUAL_TO_100);
        }
        invalidate();

    }

    public void setRadius(int iR) throws Exception{
        if (iR>100 || iR<0){
            Log.e(TAG,ERROR_RADIUS_VALUE);
            throw new Exception(ERROR_RADIUS_VALUE);
        }

        this.iR = iR;
    }

    public void setIsCenter(boolean bIsAlignCenter) {
        this.bIsAlignCenter = bIsAlignCenter;
    }

    public void setShowLegend(boolean bIsShowLegend) {
        this.bIsShowLegend = bIsShowLegend;
    }

}