package ch.schoeb.opentransportlibrary;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
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

public class ConnectionListActivity extends Activity{

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



