package ecig.app.RatioPie;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

/**
 * Created by kssworld93 on 12/24/14.
 *
 * If you have 1 slice, you have 0 boundaries.
 * If you have n > 1 slices, you have n boundaries.
 */
public class BoundarySet {
    static int D360 = 360;

    static class Slice {
        float startAngle;
        float sweepAngle;
        int color;
        RectF rectf; // oval bounds of circle of the slice starting at this boundary
        Paint p;
        public Slice(float startAngle, float sweepAngle, int color) {
            this.startAngle = startAngle;
            this.sweepAngle = sweepAngle;
            this.color = color;
            rectf = new RectF();
            p = new Paint();
            p.setColor(color);

        }
        /* The boundary would normally start at the center of some Pie,
            but it's offset from center in direction of this slice */
        void computeRectf(float pieCenterX, float pieCenterY, float radius, float pieOffset) {
            float degMidSlice = (startAngle + sweepAngle/2) % 360;

            float xProjector = (float)Math.cos(Math.toRadians(degMidSlice));
            float yProjector = (float)Math.sin(Math.toRadians(degMidSlice));

            float centerX = pieCenterX + xProjector * pieOffset;
            float centerY = pieCenterY + yProjector * pieOffset;
            this.rectf.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            this.rectf.sort();
        }

    }

    protected final ArrayList<Slice> boundaries = new ArrayList<Slice>();
    public final int[] PIE_COLORS;
    int nextColorI; // helps us choose the next color


    public BoundarySet(float[] percents, int[] PIE_COLORS) {
        if(boundaries.size() != 0) // just testing my knowledge of final instance variables.
            throw new RuntimeException("wt: " + boundaries.size());
        this.PIE_COLORS = PIE_COLORS;
        fromPercents(percents);
    }

    void fromPercents(float[] pcts) {
        // assume the pcts are from 0 to 1 and add to 1

        // do nothing if only one slice
        if (pcts.length <= 1) {
            return;
        }

        float cumDegrees = 0.0f;
        // make the starting boundary for each slice.
        for (int i = 0; i < pcts.length; i ++) {
            float degrees = pcts[i] * D360;
            Slice b = new Slice(cumDegrees, degrees, nextColor());
            cumDegrees += degrees;
            boundaries.add(b);

        }
    }

    protected int nextColor() {
        return PIE_COLORS[(nextColorI ++) % PIE_COLORS.length];
    }

}
