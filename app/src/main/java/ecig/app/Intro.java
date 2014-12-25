package ecig.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;


public class Intro extends ActionBarActivity {

    RatioPieView pie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        initPie();
    }

    private void initPie() {
        pie = (RatioPieView) findViewById(R.id.ratiopieview);
        ArrayList<PieChartData> alPercentage = new ArrayList<PieChartData>();
        alPercentage.add(new PieChartData(25.0f, "1"));
        alPercentage.add(new PieChartData(25.0f, "2"));
        alPercentage.add(new PieChartData(20.0f, "3"));
        alPercentage.add(new PieChartData(10.0f, "4"));
        alPercentage.add(new PieChartData(10.0f, "5"));
        alPercentage.add(new PieChartData(10.0f, "6"));

        try {
            // setting data
            pie.setAdapter(alPercentage);

            /*
            // setting a listener
            pie.setOnSelectedListener(new OnSelectedLisenter() {
                @Override
                public void onSelected(int iSelectedIndex) {
                    // Toast.makeText(ChartView.this, "Select index:" + iSelectedIndex, Toast.LENGTH_SHORT).show();
                }
            });*/
        } catch (Exception e) {
            if (e.getMessage().equals(RatioPieView.ERROR_NOT_EQUAL_TO_100)){
                throw new RuntimeException("yo");
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
