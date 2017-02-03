package ch.schoeb.opentransportlibrary;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import ch.schoeb.opendatatransport.model.Station;
import ch.schoeb.opendatatransport.model.StationList;


public class MainActivity extends ActionBarActivity {

    EditText etFrom;
    EditText etTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etFrom = (EditText) findViewById(R.id.from);
        etTo = (EditText) findViewById(R.id.to);

        final Button button = (Button) findViewById(R.id.buttonSwitch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String toFrom = etTo.getText().toString();
                String fromTo = etFrom.getText().toString();

                etFrom.setText(toFrom);
                etTo.setText(fromTo);
            }
        });

    }

    public void about(View view)
    {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void connectionList(View view)
    {
        Intent intent = new Intent(MainActivity.this, ConnectionListActivity.class);
        intent.putExtra("stationKey", new String[]{ etFrom.getText().toString(), etTo.getText().toString() });
        startActivity(intent);
    }

}


