package ecig.app.RatioPie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import ecig.app.R;

public class RatioPieView extends View {

    int width;
    int height; // of the view
    static final float[] initialData = new float[] {0.25f, 0.25f, 0.2f, 0.1f, 0.1f, 0.1f};

    static final float BOUNDARY_LENGTH = 350.0f;
    static final float PIE_OFFSET = 5.0f;
    final int[] PIE_COLORS = RatioPieView.parseColorStrings(getResources().getStringArray(R.array.colors));

    Paint bgndP;
    // TODO onchange event


    final BoundarySet bs;

    public RatioPieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bs = new BoundarySet(initialData, PIE_COLORS);
        initPaints();
    }

    // TODO create a slice, change slice size, delete a slice.


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        for (BoundarySet.Slice b : bs.boundaries) {
            b.computeRectf(width/2, height/2, BOUNDARY_LENGTH, PIE_OFFSET);
        }
    }

    protected void initPaints() {
        bgndP = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgndP.setARGB(255,255,0,0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //canvas.drawRect(new Rect(0, 0, width, height), bgndP);
        drawWedges(canvas);
    }

    // Logic to draws from the BoundarySet
    protected void drawWedges(Canvas canvas) {
        if (bs.boundaries.size() == 0) {
            return;
        } else if (bs.boundaries.size() == 1) {
            throw new RuntimeException();
        } else {
            for (BoundarySet.Slice b : bs.boundaries) {
                canvas.drawArc(b.rectf, b.startAngle, b.sweepAngle, true, b.p);
            }

        }

    }

    public static int[] parseColorStrings(String[] colorStrs) {
        int[] colors = new int[colorStrs.length];
        for (int i = 0; i < colorStrs.length; i ++) {
            colors[i] = Color.parseColor(colorStrs[i]);
        }
        return colors;
    }

}