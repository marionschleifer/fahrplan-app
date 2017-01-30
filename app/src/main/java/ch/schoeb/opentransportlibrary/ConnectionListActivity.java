package ch.schoeb.opentransportlibrary;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.schoeb.opendatatransport.IOpenTransportRepository;
import ch.schoeb.opendatatransport.OpenDataTransportException;
import ch.schoeb.opendatatransport.OpenTransportRepositoryFactory;
import ch.schoeb.opendatatransport.model.Connection;
import ch.schoeb.opendatatransport.model.ConnectionList;

/**
 * Created by marion on 28.01.17.
 */

public class ConnectionListActivity extends Activity{

    ListView listView;

    List<String> stringList = new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapter;
//    private boolean loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

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
        new LoaderTask().execute();
    }

//    public void setLoading(boolean loading) {
//        if (loading) {
//            // show loading view...
//        } else {
//            // hide loading view...
//        }
//    }

    private class LoaderTask extends AsyncTask<Void, Void, List<Connection>> {
        @Override
        protected List<Connection> doInBackground(Void... params) {
            // Get Repository
            IOpenTransportRepository repo = OpenTransportRepositoryFactory.CreateOnlineOpenTransportRepository();
            List<Connection> connectionList = null;
            try {
                connectionList = repo.searchConnections("Buchs SG", "Zürich HB").getConnections();
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



