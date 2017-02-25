package ch.schoeb.opentransportlibrary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.schoeb.opendatatransport.IOpenTransportRepository;
import ch.schoeb.opendatatransport.OpenDataTransportException;
import ch.schoeb.opendatatransport.OpenTransportRepositoryFactory;
import ch.schoeb.opendatatransport.model.Connection;

/**
 * Created by marion on 28.01.17.
 */

public class ConnectionListActivity extends AppCompatActivity {

    ListView listView;

    List<String> stringList = new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapter;
//    private boolean loading;

    private String[] stations;
    private Boolean isArrivalTime;

    List<Connection> connectionList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        stations = intent.getStringArrayExtra("stationKey");
        isArrivalTime = intent.getBooleanExtra("isArrivalTime", true);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringList);
        ListView listView = (ListView) findViewById(R.id.connectionlist);
        listView.setAdapter(arrayAdapter);

        LoadConnections();
//        setLoading(true);
    }

    private void setConnections(List<Connection> connections) {
        for (Connection c : connections) {
            arrayAdapter.add(formatConnection(c));
        }
    }

    private String formatConnection(Connection c) {
        DateFormat formatBefore = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS");
        DateFormat formatAfter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String dateTime = null;
        try {
            dateTime=formatAfter.format(formatBefore.parse(c.getFrom().getDeparture()));
        } catch (ParseException e) {
            Log.e("ParseError", e.getMessage());
            dateTime="?";
        }
        return c.getFrom().getStation().getName() + " -> " + c.getTo().getStation().getName() + " um " + dateTime;
    }

    private void LoadConnections() {
        new LoaderTask(stations[0], stations[1], stations[2], stations[3], isArrivalTime).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_connections, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about_connections:
                Intent intent_about = new Intent(ConnectionListActivity.this, AboutActivity.class);
                startActivity(intent_about);
                return true;
            case R.id.action_favorite_connections:
                Intent intent_favorites = new Intent(ConnectionListActivity.this, FavoritesActivity.class);
                startActivity(intent_favorites);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    public void setLoading(boolean loading) {
//        if (loading) {
//            // show loading view...
//        } else {
//            // hide loading view...
//        }
//    }

    private class LoaderTask extends AsyncTask<Void, Void, List<Connection>> {

        private Boolean isArrivalTime;
        private String time;
        private String date;
        private String from;
        private String to;

        public LoaderTask(String from, String to, String date, String time, Boolean isArrivalTime) {
            this.from = from;
            this.to = to;
            this.date = date;
            this.time = time;
            this.isArrivalTime = isArrivalTime;
        }

        @Override
        protected List<Connection> doInBackground(Void... params) {
            // Get Repository
            IOpenTransportRepository repo = OpenTransportRepositoryFactory.CreateOnlineOpenTransportRepository();
            try {
                connectionList = repo.searchConnections(from, to, null, date, time, isArrivalTime).getConnections();
            } catch (OpenDataTransportException e) {
                e.printStackTrace();
            }
            return connectionList;
        }


        @Override
        protected void onPostExecute(List<Connection> connectionList) {
            Log.d("ConnectionList", connectionList.toString());
            setConnections(connectionList);
//            setLoading(false);
        }
    }

    public void later(View view) {
        final long ONE_MINUTE_IN_MILLIS = 60000;
        if(connectionList.size()<1){
            Toast.makeText(ConnectionListActivity.this, "Keine Verbindung vorhanden", Toast.LENGTH_LONG).show();
            return;
        }
        Connection lastConnection = connectionList.get(connectionList.size() - 1);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS");
        String arrivalTimeString = lastConnection.getFrom().getDeparture();
        Date oldDate = null;
        try {
            oldDate = format.parse(arrivalTimeString);
        } catch (ParseException e) {
            Log.e("Parse Exception", e.getMessage());
        }
        Date newDate = new Date(oldDate.getTime() + ONE_MINUTE_IN_MILLIS);
        String getConnectionTime = format.format(newDate);
        String[] arrivalTime = getConnectionTime.split("T");
        String date = arrivalTime[0];
        String time = arrivalTime[1].substring(0, 5);
        new LoaderTask(stations[0], stations[1], date, time, false).execute();
    }
}



