package ecig.app.RatioPie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ecig.app.R;

public class RatioPieView extends View {

    int width;
    int height; // of the view
    static final float[] initialData = new float[] {0.25f, 0.25f, 0.2f, 0.1f, 0.1f, 0.1f};

    static final float BOUNDARY_LENGTH = 350.0f;
    static final float PIE_OFFSET = 5.0f;
    final int[] PIE_COLORS = getColorStrings();

    Paint bgndP;
    // TODO onchange event

    int _count = 0;
    private TextView tv;

    public void setTextView (TextView t) {
        tv = t;
    }

    final BoundarySet bs;

    public RatioPieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bs = new BoundarySet(initialData, PIE_COLORS);
        initPaints();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RatioPieView rpv = (RatioPieView) v;
                rpv._count ++;

                String text = "";


                int action = event.getAction();
                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        text += "down";
                        break;
                    case MotionEvent.ACTION_UP:
                        text += "up";
                        break;
                    case MotionEvent.ACTION_MOVE:
                        text += "move";
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                int[] loc = new int[]{0,0};
                rpv.getLocationOnScreen(loc);

                text += " (" +  Integer.toString(Math.round(event.getRawX()-loc[0])) + ","  + Integer.toString(Math.round(event.getRawY()-loc[1])) + ") ";
                text += Integer.toString(rpv._count);
                rpv.tv.setText(text);


                return true;
            }
        });
    }

    // TODO create a slice, change slice size, delete a slice.


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        for (BoundarySet.Slice b : bs.slices) {
            b.computeRectf(width/2, height/2, BOUNDARY_LENGTH, PIE_OFFSET);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(700, 700);
    }

    protected void initPaints() {
        bgndP = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgndP.setARGB(255, 255, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //canvas.drawRect(new Rect(0, 0, width, height), bgndP);
        drawWedges(canvas);
    }

    // Logic to draws from the BoundarySet
    protected void drawWedges(Canvas canvas) {
        if (bs.slices.size() == 0) {
            return;
        } else if (bs.slices.size() == 1) {
            throw new RuntimeException();
        } else {
            for (BoundarySet.Slice b : bs.slices) {
                canvas.drawArc(b.rectf, b.startAngle, b.sweepAngle, true, b.p);
            }

        }
    }

    public int[] getColorStrings() {

        String[] colorStrs;
        if (isInEditMode()) {
            colorStrs = new String[] {"#F2D06B", "#F15441"};
        } else {
            colorStrs = getResources().getStringArray(R.array.colors);
        }

        int[] colors = new int[colorStrs.length];
        for (int i = 0; i < colorStrs.length; i ++) {
            colors[i] = Color.parseColor(colorStrs[i]);
        }
        return colors;


    }

}