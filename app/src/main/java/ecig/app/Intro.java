package ecig.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import ecig.app.RatioPie.RatioPieView;
import ecig.app.EmbreAgent.CData;
import ecig.app.EmbreAgent.CDataError;


public class Intro extends Activity {
    public final static String TAG = "ecig.app.Intro";

    TextView textView;
    Button connectingButton;

    EmbreAgent embre;

    RatioPieView pie;
    TabHost tabHost;
    TableLayout cartTable;
    Button editDoneButton;
    Button reconfButton;

    boolean editState = false;

    CData[] data = new CData[]{
            new CData("C1",100),
            new CData("C2", 0),
            new CData("C3", 0),
            new CData("C4", 0),
            new CData("C5", 0),
            new CData("C6", 0)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_intro);
        initConnectingFragment();
        // TODO fix the pie.
        MyApplication app = (MyApplication) getApplication();
        embre = app.embre;
        embre.initialize(this);
        //initPie();
        //initTabs();
        initNumericalTable();
    }

    @Override
    protected void onStop() {
        embre.cancelTask();
        super.onStop();
    }

    private void initConnectingFragment() {
        textView = (TextView) findViewById(R.id.textView);
        connectingButton = (Button) findViewById(R.id.connectingButton);

    }

    /* </connecting fragment> */

    /*
    private void initPie() {
        pie = (RatioPieView) findViewById(R.id.ratiopieview);
        pie.setTextView(textView);

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
    }*/

    /* Numerical table:
       Edit or View state
       When in View state, you can redraw the percentage numbers.
       When in Edit state, you can edit the percentage numbers.
       Done button validates the percentage numbers and makes edit -> view
       Edit button makes view -> edit
       */
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

        reconfButton = (Button) findViewById(R.id.reconfButton);
        reconfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String macAddr = ((MyApplication) Intro.this.getApplicationContext()).embreMac;
                if (macAddr == null) {
                    Intent i = new Intent(Intro.this, FindEmbre.class);
                    startActivity(i);
                    return;
                }

                editDoneButton.setEnabled(false);
                reconfButton.setEnabled(false);
                setProgressBarIndeterminateVisibility(true);


                // Reconfigure ecig or display ecig not connected.
                embre.writeData(Intro.this.data, macAddr, new EmbreAgent.WriteCB() {
                    @Override
                    public void call(final boolean success) {
                        Intro.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reconfButton.setEnabled(true);
                                editDoneButton.setEnabled(true);
                                setProgressBarIndeterminateVisibility(false);

                                if (success) {
                                    Toast t = Toast.makeText(Intro.this, "We seem to have finished writing", Toast.LENGTH_SHORT);
                                    t.show();
                                } else {
                                    Toast t = Toast.makeText(Intro.this, "Error", Toast.LENGTH_SHORT);
                                    t.show();
                                }
                            }
                        });

                    }
                });
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

            int dataValue;
            try {
                dataValue = Integer.parseInt(dataView.getText().toString());
            } catch (NumberFormatException e) {
                dataValue = 0;
            }
            newData[dataI] = new CData(labelView.getText().toString(), dataValue);
        }
        return newData;
    }

    void editNumTable() {
        reconfButton.setEnabled(false);

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
            editText.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
            editText.setEnabled(true);

        }
        editState = true;
    }

    private void updateNumericalTable() {
        reconfButton.setEnabled(true);
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

            // undo any error left over
            dataView.setError(null);


            dataView.setEnabled(false);

            labelView.setText(rowData.label);
            dataView.setText(rowData.valueString());
            dataView.clearFocus();
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

        if (id == R.id.action_findembre) {
            Intent i = new Intent(this, FindEmbre.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
