package ecig.app;

/**
 * Created by kssworld93 on 12/24/14.
 */
public class PieChartData {
    public float fPercentage;
    public String strTitle;

    /**
     *
     * @param fPercentage value in pie chart
     * @param strTitle the legend title
     */
    public PieChartData(float fPercentage,String strTitle) {
        this.fPercentage = fPercentage;
        this.strTitle    = strTitle;
    }
}
