package ch.schoeb.opentransportlibrary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ch.schoeb.opentransportlibrary.ch.schoeb.opentransportlibrary.contracts.DataBaseContract;
import ch.schoeb.opentransportlibrary.ch.schoeb.opentransportlibrary.contracts.FavoritesDbHelper;

/**
 * Created by helga on 19.02.17.
 */

public class FavoritesActivity extends AppCompatActivity {

    ListView listView;
    FavoritesDbHelper mDbHelper;

    List<Favorit> favoritList = new ArrayList<>();
    private ArrayAdapter<Favorit> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("FavoritesActivionCreate", "enter");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoritList);
        listView = (ListView) findViewById(R.id.favoriteslist);
        listView.setAdapter(arrayAdapter);

        loadFavorites();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parentView, View childView,
                                    int position, long id) {
                Favorit favorit = arrayAdapter.getItem(position);
                showSearch(favorit);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(final AdapterView parentView, View childView, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
                builder.setTitle("Löschen");
                builder.setMessage("Möchten Sie diesen Favoriten löschen?");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int ii) {
                        if (mDbHelper == null) {
                            mDbHelper = new FavoritesDbHelper(FavoritesActivity.this);
                        }
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();
                        Favorit favorite = (Favorit) parentView.getItemAtPosition(position);
                        long ID = favorite.getId();
                        String selection = DataBaseContract.FavoritEntry._ID + " LIKE ?";
                        String[] selectionArgs = {ID + ""};
                        db.delete(DataBaseContract.FavoritEntry.TABLE_NAME, selection, selectionArgs);
                        favoritList=getDbData();
                        arrayAdapter.clear();
                        arrayAdapter.addAll(favoritList);
                        arrayAdapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int ii) {
                                dialog.dismiss();
                            }
                        }
                );
                builder.show();
                return true;
            }
        });
    }

    private void showSearch(Favorit favorit) {
        Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
        intent.putExtra("stationKey", new String[]{favorit.getFrom(), favorit.getTo(), favorit.getDate(), favorit.getTime()});
        intent.putExtra("isArrivalTime", favorit.getArrivalTime());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_connections, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about_favorites:
                Intent intent_about = new Intent(FavoritesActivity.this, AboutActivity.class);
                startActivity(intent_about);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public List<Favorit> getDbData() {
        if (mDbHelper == null) {
            mDbHelper = new FavoritesDbHelper(FavoritesActivity.this);
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                DataBaseContract.FavoritEntry._ID,
                DataBaseContract.FavoritEntry.COLUMN_NAME_FROM,
                DataBaseContract.FavoritEntry.COLUMN_NAME_TO,
                DataBaseContract.FavoritEntry.COLUMN_NAME_DATE,
                DataBaseContract.FavoritEntry.COLUMN_NAME_TIME,
                DataBaseContract.FavoritEntry.COLUMN_NAME_IS_ARRIVAL_TIME
        };


        Cursor cursor = db.query(
                DataBaseContract.FavoritEntry.TABLE_NAME, projection, null, null, null, null, null);
        List favorites = new ArrayList<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseContract.FavoritEntry._ID));
            String from = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.FavoritEntry.COLUMN_NAME_FROM));
            String to = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.FavoritEntry.COLUMN_NAME_TO));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.FavoritEntry.COLUMN_NAME_DATE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.FavoritEntry.COLUMN_NAME_TIME));
            boolean isArrivalTime = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseContract.FavoritEntry.COLUMN_NAME_IS_ARRIVAL_TIME)) == 1 ;
            favorites.add(new Favorit(id, from, to, date, time, isArrivalTime));
        }
        cursor.close();

        return favorites;
    }

    private void setFavorites(List<Favorit> favorites) {
        arrayAdapter.addAll(favorites);
    }

    private void loadFavorites() {
        new FavoritesTask().execute();
    }

    private class FavoritesTask extends AsyncTask<Void, Void, List<Favorit>> {

        @Override
        protected List<Favorit> doInBackground(Void... params) {
            return getDbData();
        }


        @Override
        protected void onPostExecute(List<Favorit> favoritsList) {
            Log.d("FavoritesList", favoritsList.toString());
            setFavorites(favoritsList);
        }
    }

    private class Favorit {
        private long id;
        private String from;
        private String to;
        private String date;
        private String time;
        private Boolean isArrivalTime;

        public Favorit(long id, String from, String to, String date, String time, Boolean isArrivalTime) {
            this.id = id;
            this.from = from;
            this.to = to;
            this.date = date;
            this.time = time;
            this.isArrivalTime = isArrivalTime;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public Boolean getArrivalTime() {
            return isArrivalTime;
        }

        public void setArrivalTime(Boolean isArrivalTime) {
            this.isArrivalTime = isArrivalTime;
        }

        @Override
        public String toString() {
            return from + " -> " + to + " am " + date + " " + time + " (" + (isArrivalTime ? "Ankunfts" : "Abfahrts") + "zeit)";
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Override
    protected void onDestroy() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
        super.onDestroy();
    }
}



