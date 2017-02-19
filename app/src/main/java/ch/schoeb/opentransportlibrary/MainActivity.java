package ch.schoeb.opentransportlibrary;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ch.schoeb.opentransportlibrary.ch.schoeb.opentransportlibrary.contracts.DataBaseContract;
import ch.schoeb.opentransportlibrary.ch.schoeb.opentransportlibrary.contracts.FavoritesDbHelper;

import static android.R.attr.format;


public class MainActivity extends AppCompatActivity {

    FavoritesDbHelper mDbHelper;

    Button btnDatePicker, btnTimePicker;
    ImageButton btnSwitch;
    ToggleButton toggle;
    EditText etFrom, etTo, etDate, etTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private boolean isArrivalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etFrom = (EditText) findViewById(R.id.from);
        etTo = (EditText) findViewById(R.id.to);

        btnDatePicker = (Button) findViewById(R.id.btn_date);
        btnTimePicker = (Button) findViewById(R.id.btn_time);
        etDate = (EditText) findViewById(R.id.date);
        etTime = (EditText) findViewById(R.id.time);
        toggle = (ToggleButton) findViewById(R.id.toggle);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isArrivalTime = isChecked;
            }
        });

        Calendar c = Calendar.getInstance();
        DecimalFormat format = new DecimalFormat("00");
        etDate.setText(formatDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)));
        etTime.setText(formatTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                etDate.setText(formatDate(year, monthOfYear, dayOfMonth));
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                etTime.setText(formatTime(hourOfDay, minute));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });


        btnSwitch = (ImageButton) findViewById(R.id.buttonSwitch);
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String toFrom = etTo.getText().toString();
                String fromTo = etFrom.getText().toString();

                etFrom.setText(toFrom);
                etTo.setText(fromTo);

            }
        });

        Intent intent = getIntent();
        String[] stations = intent.getStringArrayExtra("stationKey");
        isArrivalTime = intent.getBooleanExtra("isArrivalTime", true);
        if(stations!=null){
            etFrom.setText(stations[0]);
            etTo.setText(stations[1]);
            etDate.setText(stations[2]);
            etTime.setText(stations[3]);
            toggle.setChecked(isArrivalTime);
        }
    }

    public String formatDate(int year, int month, int day) {
        DecimalFormat format = new DecimalFormat("00");
        return year + "-" + format.format(month + 1) + "-" + format.format(day);
    }

    public String formatTime(int hour, int minute) {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(hour) + ":" + format.format(minute);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent intent_action = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent_action);
                return true;
            case R.id.action_favorites:
                Intent intent_favorites = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intent_favorites);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void about(View view) {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void connectionList(View view) {
        Intent intent = new Intent(MainActivity.this, ConnectionListActivity.class);
        intent.putExtra("stationKey", new String[]{etFrom.getText().toString(), etTo.getText().toString(), etDate.getText().toString(), etTime.getText().toString()});
        intent.putExtra("isArrivalTime", isArrivalTime);
        startActivity(intent);
    }

    public void addToFavorites(View view) {
        if (mDbHelper == null) {
            mDbHelper = new FavoritesDbHelper(MainActivity.this);
        }
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.FavoritEntry.COLUMN_NAME_FROM, etFrom.getText().toString());
        values.put(DataBaseContract.FavoritEntry.COLUMN_NAME_TO, etTo.getText().toString());
        values.put(DataBaseContract.FavoritEntry.COLUMN_NAME_DATE, etDate.getText().toString());
        values.put(DataBaseContract.FavoritEntry.COLUMN_NAME_TIME, etTime.getText().toString());
        values.put(DataBaseContract.FavoritEntry.COLUMN_NAME_IS_ARRIVAL_TIME, isArrivalTime);


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DataBaseContract.FavoritEntry.TABLE_NAME, null, values);

        Toast.makeText(MainActivity.this, "Verbindung zu Favoriten hinzugefügt", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
        super.onDestroy();
    }

}


