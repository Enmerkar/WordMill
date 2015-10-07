package com.findelworks.wordmill;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import java.io.File;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!databaseExists()) {
            LanguageSQLiteOpenHelper langHelper = new LanguageSQLiteOpenHelper(getBaseContext());
            try {
                populateDatabase(langHelper);
            } catch (Exception e) {
                // catch
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_german) {
            // Handle the action
        } else if (id == R.id.nav_latin) {

        } else if (id == R.id.nav_add) {

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean databaseExists() {
        File database = getApplicationContext().getDatabasePath("Language.db");
        return database.exists();
    }

    public void populateDatabase(LanguageSQLiteOpenHelper helper) {

        AssetManager assetManager = this.getAssets();
        InputStream isLang = assetManager.open("german_data.txt");
        InputStream isUser = assetManager.open("german_user.txt");
        BufferedReader brLang = new BufferedReader(new InputStreamReader(isLang));
        BufferedReader brUser = new BufferedReader(new InputStreamReader(isUser));

        // final String vocabTextFilePath = this.getResource("vocabulary.txt");

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();

        String inputLine;
        String[] inputDataArray = new String[7];
        int id, level, score;
        String english, german, plural, genitive;
        @SuppressWarnings("unused")
        long rowID;

        while ((inputLine = br.readLine()) != null) {

            inputDataArray = inputLine.split("\t");

            id = Integer.parseInt(inputDataArray[0]);
            level = Integer.parseInt(inputDataArray[1]);
            score = Integer.parseInt(inputDataArray[2]);
            english = inputDataArray[3];
            german = inputDataArray[4];
            plural = inputDataArray[5];
            genitive = inputDataArray[6];

            row.put(VocabDbHelper.COLUMN_NAME_ID, id);
            row.put(VocabDbHelper.COLUMN_NAME_LEVEL, level);
            row.put(VocabDbHelper.COLUMN_NAME_SCORE, score);
            row.put(VocabDbHelper.COLUMN_NAME_ENGLISH, english);
            row.put(VocabDbHelper.COLUMN_NAME_GERMAN, german);
            row.put(VocabDbHelper.COLUMN_NAME_PLURAL, plural);
            row.put(VocabDbHelper.COLUMN_NAME_GENITIVE, genitive);

            rowID = db.insert(VocabDbHelper.TABLE_NAME, null, row);
        }

        br.close();
    }

}
