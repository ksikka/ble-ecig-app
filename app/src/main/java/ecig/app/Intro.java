package ecig.app;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import ecig.app.RatioPie.RatioPieView;
import ecig.app.RatioPie.Slice;


public class Intro extends ActionBarActivity {

    RatioPieView pie;
    TabHost tabHost;
    TableLayout cartTable;
    Button editDoneButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        initPie();
        initTabs();
        initNumericalTable();
    }

    private void initPie() {
        pie = (RatioPieView) findViewById(R.id.ratiopieview);
        pie.setTextView((TextView) findViewById(R.id.textView));

    }

    private void initTabs() {
        tabHost = (TabHost)findViewById(R.id.tabHost);
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

    /* Numerical table:
       Edit or View state
       When in View state, you can redraw the percentage numbers.
       When in Edit state, you can edit the percentage numbers.
       Done button validates the percentage numbers and makes edit -> view
       Edit button makes view -> edit
       */
    static class CDataError {
        String message;
        int index;
        public CDataError(String message, int index) {
            this.message = message; this.index = index;
        }
    }

    static class CData {
        String label;
        int value;

        public CData(String label, int value) {
            this.label = label; this.value = value;
        }
        public String valueString() {
            String dataValue = value == 0 ? "-" :Integer.toString(value) + "%";
            return dataValue;
        }
        public static CDataError validate(CData[] cDatas) {
            int sum = 0;
            for (int i = 0; i < cDatas.length; i ++) {
                if (cDatas[i].value < 0) {
                    return new CDataError("Percent can't be negative", i);
                }
                sum += cDatas[i].value;
                if (sum > 100) {
                    return new CDataError("This adds up to over 100%\nPlease fix and try again.", i);
                }

            }
            if (sum < 100) {
                return new CDataError("This doesn't add up to 100%.\nPlease fix and try again.", cDatas.length - 1);
            }

            return null;
        }

    }

    boolean editState = false;

    CData[] data = new CData[]{
            new CData("C1",100),
            new CData("C2", 0),
            new CData("C3", 0),
            new CData("C4", 0),
            new CData("C5", 0),
            new CData("C6", 0)
    };

    private void initNumericalTable() {
        cartTable = (TableLayout) findViewById(R.id.numericalTable);


        // bind the edit button listener.
        editDoneButton = (Button) findViewById(R.id.editDoneButton);
        editDoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editState) {
                    CData[] newData = readTableData();
                    CDataError cde = null;
                    if ((cde = CData.validate(newData)) == null) {
                        data = newData;
                        updateNumericalTable();
                    } else {
                        TableRow row = (TableRow) cartTable.getChildAt(cde.index + 1);
                        EditText editView = (EditText) row.getChildAt(1);
                        editView.setError(cde.message);
                        editView.requestFocus();
                    }
                } else {
                    editNumTable();
                }
            }
        });

        updateNumericalTable();

    }
    CData[] readTableData() {
        CData[] newData = new CData[6];
        for (int rowI = 1; rowI < 7; rowI ++) {
            int dataI = rowI - 1;
            TableRow row = (TableRow) cartTable.getChildAt(rowI);
            if (row.getChildCount() != 2) {
                throw new RuntimeException("Should only be two columns in a data row");
            }

            TextView labelView = (TextView) row.getChildAt(0);
            TextView dataView = (TextView) row.getChildAt(1);

            newData[dataI] = new CData(
                    labelView.getText().toString(),
                    Integer.parseInt(dataView.getText().toString())
            );
        }
        return newData;
    }

    void editNumTable() {
        editDoneButton.setText("Done");
        for (int rowI = 1; rowI < 7; rowI ++) {
            int dataI = rowI - 1;
            TableRow row = (TableRow) cartTable.getChildAt(rowI);
            if (row.getChildCount() != 2) {
                throw new RuntimeException("Should only be two columns in a data row");
            }

            CData rowData = data[dataI];
            EditText editText = (EditText) row.getChildAt(1);
            editText.setText(Integer.toString(rowData.value));
            editText.setEnabled(true);
        }
        editState = true;
    }

    private void updateNumericalTable() {
        editDoneButton.setText("Edit");
        // One header, 6 data rows.
        if (cartTable.getChildCount() != 7) {
            throw new RuntimeException("Should be 7 rows.");
        }

        // Update the label and data.
        for (int rowI = 1; rowI < 7; rowI ++) {
            int dataI = rowI - 1;
            TableRow row = (TableRow) cartTable.getChildAt(rowI);
            if (row.getChildCount() != 2) {
                throw new RuntimeException("Should only be two columns in a data row");
            }

            CData rowData = data[dataI];

            TextView labelView = (TextView) row.getChildAt(0);
            EditText dataView = (EditText) row.getChildAt(1);

            dataView.setEnabled(false);

            labelView.setText(rowData.label);
            dataView.setText(rowData.valueString());
        }

        editState = false;

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
