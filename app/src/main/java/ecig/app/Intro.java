package ecig.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;

import ecig.app.RatioPie.RatioPieView;
import ecig.app.RatioPie.Slice;


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
        pie.setTextView((TextView) findViewById(R.id.textView));


        TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2");
        tab1.setIndicator("Numeric");
        tab2.setIndicator("Graphical");
        tab1.setContent(R.id.tab1);
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
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
