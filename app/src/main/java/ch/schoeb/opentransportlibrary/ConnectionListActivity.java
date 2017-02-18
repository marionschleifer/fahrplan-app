package ch.schoeb.opentransportlibrary;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
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

    private String from;
    private String to;
    private String [] stations;
    private Boolean isArrivalTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab=getSupportActionBar();
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
            arrayAdapter.add(c.toString());
        }
    }

    private void LoadConnections() {
        new LoaderTask(stations[0], stations[1], stations[2], stations[3], isArrivalTime).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_connections, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_about:
                Intent intent = new Intent(ConnectionListActivity.this, AboutActivity.class);
                startActivity(intent);
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
            this.date=date;
            this.time=time;
            this.isArrivalTime=isArrivalTime;
        }

        @Override
        protected List<Connection> doInBackground(Void... params) {
            // Get Repository
            IOpenTransportRepository repo = OpenTransportRepositoryFactory.CreateOnlineOpenTransportRepository();
            List<Connection> connectionList = null;
            try {
                connectionList = repo.searchConnections( from,  to, null,  date,  time,  isArrivalTime).getConnections();
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
}



