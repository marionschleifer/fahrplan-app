package ch.schoeb.opentransportlibrary;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import ch.schoeb.opendatatransport.IOpenTransportRepository;
import ch.schoeb.opendatatransport.OpenDataTransportException;
import ch.schoeb.opendatatransport.OpenTransportRepositoryFactory;
import ch.schoeb.opendatatransport.model.ConnectionList;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.buttonSearch);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadConnections();
            }
        });
    }

    public void about(View view)
    {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    private void LoadConnections() {
        new LoaderTask().execute();
    }

    private class LoaderTask extends AsyncTask<Void, Void, ConnectionList> {
        @Override
        protected ConnectionList doInBackground(Void... params) {
            // Get Repository
            IOpenTransportRepository repo = OpenTransportRepositoryFactory.CreateOnlineOpenTransportRepository();
            ConnectionList connectionList = null;
            try {
                connectionList = repo.searchConnections("Buchs SG", "Zürich HB");
            } catch (OpenDataTransportException e) {
                    e.printStackTrace();
            }

            return connectionList;
        }


        @Override
        protected void onPostExecute(ConnectionList connectionList) {
            Log.d("ConnectionList", connectionList.toString());
        }
    }
}


