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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.res.Resources;

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

        Resources res = getResources();
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();

        boolean match;
        String inputLineLang, inputLineUser;
        String[] inputArrayLang = new String[5];
        String[] inputArrayUser = new String[5];
        int id_lang, id_user, freq, flevel, flapse, blevel, blapse;
        String word, full, trans;
        long rowID;

        // Repeat for each language

            InputStream isLang = res.openRawResource(R.raw.german_data);
            InputStream isUser = res.openRawResource(R.raw.german_user);
            BufferedReader brLang = new BufferedReader(new InputStreamReader(isLang));
            BufferedReader brUser = new BufferedReader(new InputStreamReader(isUser));

            match = false;
            id_lang = id_user = freq = flevel = flapse = blevel = blapse = 0;
            word = full = trans = null;

            try {
                while ((inputLineUser = brUser.readLine()) != null) {

                    inputArrayUser = inputLineUser.split("\t");

                    id_user = Integer.parseInt(inputArrayUser[0]);
                    flevel = Integer.parseInt(inputArrayUser[1]);
                    flapse = Integer.parseInt(inputArrayUser[2]);
                    blevel = Integer.parseInt(inputArrayUser[3]);
                    blapse = Integer.parseInt(inputArrayUser[4]);

                    match = false;

                    try {
                        while (!match) {

                            inputLineLang = brLang.readLine();
                            inputArrayLang = inputLineUser.split("\t");

                            id_lang = Integer.parseInt(inputArrayLang[0]);
                            freq = Integer.parseInt(inputArrayLang[1]);
                            word = inputArrayLang[2];
                            full = inputArrayLang[3];
                            trans = inputArrayLang[4];

                            if (id_user == id_lang) {
                                match = true;
                                row.put(helper.COLUMN_FLEVEL, flevel);
                                row.put(helper.COLUMN_FLAPSE, flapse);
                                row.put(helper.COLUMN_BLEVEL, blevel);
                                row.put(helper.COLUMN_BLAPSE, blapse);
                            } else {
                                row.put(helper.COLUMN_FLEVEL, 0);
                                row.put(helper.COLUMN_FLAPSE, 0);
                                row.put(helper.COLUMN_BLEVEL, 0);
                                row.put(helper.COLUMN_BLAPSE, 0);
                            }

                            row.put(helper.COLUMN_ID, id_lang);
                            row.put(helper.COLUMN_FREQ, freq);
                            row.put(helper.COLUMN_WORD, word);
                            row.put(helper.COLUMN_FULL, full);
                            row.put(helper.COLUMN_TRANS, trans);

                            rowID = db.insert(helper.TABLE_NAME_GERMAN, null, row);

                        }

                    } catch (IOException e) {

                    }
                }

                // Copy in any trailing unattempted words into the database
                while ((inputLineLang = brLang.readLine()) != null) {

                    inputArrayLang = inputLineUser.split("\t");

                    row.put(helper.COLUMN_ID, id_lang);
                    row.put(helper.COLUMN_FREQ, freq);
                    row.put(helper.COLUMN_WORD, word);
                    row.put(helper.COLUMN_FULL, full);
                    row.put(helper.COLUMN_TRANS, trans);
                    row.put(helper.COLUMN_FLEVEL, 0);
                    row.put(helper.COLUMN_FLAPSE, 0);
                    row.put(helper.COLUMN_BLEVEL, 0);
                    row.put(helper.COLUMN_BLAPSE, 0);

                    rowID = db.insert(helper.TABLE_NAME_GERMAN, null, row);

                }

            } catch (IOException e) {

            }

            try {
                brLang.close();
                brUser.close();
            } catch (IOException e) {

            }

    }

}

