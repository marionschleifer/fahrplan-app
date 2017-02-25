package ch.schoeb.opentransportlibrary;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.schoeb.opendatatransport.IOpenTransportRepository;
import ch.schoeb.opendatatransport.OpenDataTransportException;
import ch.schoeb.opendatatransport.OpenTransportRepositoryFactory;
import ch.schoeb.opendatatransport.model.Station;
import ch.schoeb.opentransportlibrary.ch.schoeb.opentransportlibrary.contracts.DataBaseContract;
import ch.schoeb.opentransportlibrary.ch.schoeb.opentransportlibrary.contracts.FavoritesDbHelper;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getName();
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    FavoritesDbHelper mDbHelper;

    Button btnDatePicker, btnTimePicker;
    ImageButton btnSwitch, btnFromLocation, btnToLocation;
    ToggleButton toggle;
    AutoCompleteTextView etFrom, etTo;
    EditText etDate, etTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private boolean isArrivalTime=false;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etFrom = (AutoCompleteTextView) findViewById(R.id.from);
        etTo = (AutoCompleteTextView) findViewById(R.id.to);

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

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
        btnFromLocation = (ImageButton) findViewById(R.id.buttonLocationFrom);
        btnFromLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStationByLocation(etFrom);
            }
        });

        btnToLocation = (ImageButton) findViewById(R.id.buttonLocationTo);
        btnToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStationByLocation(etTo);
            }
        });


        Intent intent = getIntent();
        String[] stations = intent.getStringArrayExtra("stationKey");
        isArrivalTime = intent.getBooleanExtra("isArrivalTime", false);
        if (stations != null) {
            etFrom.setText(stations[0]);
            etTo.setText(stations[1]);
            etDate.setText(stations[2]);
            etTime.setText(stations[3]);
            toggle.setChecked(isArrivalTime);
        }

        ArrayAdapter<String> fromAutocompleteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        etFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loadStations(s.toString(), etFrom);
            }
        });
        etFrom.setAdapter(fromAutocompleteAdapter);

        ArrayAdapter<String> toAutocompleteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        etTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loadStations(s.toString(), etTo);
            }
        });
        etTo.setAdapter(toAutocompleteAdapter);
    }

    private String getStationByLocation(final AutoCompleteTextView autoComplete) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            return "";
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            Log.d("latiude", latitude + "");
            Log.d("longitude", longitude + "");
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoaderTaskLocation loader = new LoaderTaskLocation(latitude + "", longitude + "", autoComplete);
                    loader.execute();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(),
                    "Suche nach nächster Location wird nicht unterstützt.", Toast.LENGTH_LONG)
                    .show();
        }
        return "";
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(MainActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //TODO
    }

    private void loadStations(final String query, final AutoCompleteTextView autoComplete) {
        if (query == null || query.isEmpty()) {
            return;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoaderTask loader = new LoaderTask(query, autoComplete);
                loader.execute();
            }
        });
    }

    private class LoaderTask extends AsyncTask<Void, Void, List<Station>> {
        private final AutoCompleteTextView autoComplete;
        private String query;

        public LoaderTask(String query, AutoCompleteTextView autoComplete) {
            this.query = query;
            this.autoComplete = autoComplete;
        }

        @Override
        protected List<Station> doInBackground(Void... params) {
            Log.i(TAG, "Query: " + query);

            // Get Repository
            IOpenTransportRepository repo = OpenTransportRepositoryFactory.CreateOnlineOpenTransportRepository();
            List<Station> stationList = new ArrayList<>();

            try {
                stationList = repo.findStations(query).getStations();
            } catch (OpenDataTransportException e) {
                e.printStackTrace();
            }

            return stationList;
        }

        @Override
        protected void onPostExecute(List<Station> stationList) {
            super.onPostExecute(stationList);
            final ArrayAdapter<String> elementAdapter =
                    (ArrayAdapter<String>) autoComplete.getAdapter();

            elementAdapter.clear();

            Log.i(TAG, "Results: " + stationList.size());
            for (Station station : stationList) {
                Log.i(TAG, station.getName());
                elementAdapter.add(station.getName());
            }

            elementAdapter.getFilter().filter(autoComplete.getText(), null);
        }
    }

    private class LoaderTaskLocation extends AsyncTask<Void, Void, List<Station>> {
        private final AutoCompleteTextView autoComplete;
        private String latitude;
        private String longitude;

        public LoaderTaskLocation(String latitude, String longitude, final AutoCompleteTextView autoComplete) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.autoComplete = autoComplete;
        }

        @Override
        protected List<Station> doInBackground(Void... params) {
            Log.i(TAG, "latitude: " + latitude);
            Log.i(TAG, "longitude: " + longitude);

            // Get Repository
            IOpenTransportRepository repo = OpenTransportRepositoryFactory.CreateOnlineOpenTransportRepository();
            List<Station> stationList = new ArrayList<>();

            try {
                stationList = repo.findStationsByLocation(longitude, latitude).getStations();
            } catch (OpenDataTransportException e) {
                e.printStackTrace();
            }
            return stationList;
        }

        @Override
        protected void onPostExecute(List<Station> stationList) {
            super.onPostExecute(stationList);
            Station station = stationList.get(0);
            autoComplete.setText(station.getName());
        }
    }

    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
        if(etFrom.getText().toString().equals(etTo.getText().toString())){
            Toast.makeText(MainActivity.this, "Bitte unterschiedliche Start- und Zielorte wählen", Toast.LENGTH_LONG).show();
            return;
        }
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
        db.insert(DataBaseContract.FavoritEntry.TABLE_NAME, null, values);

        Toast.makeText(MainActivity.this, "Verbindung zu Favoriten hinzugefügt", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
}


