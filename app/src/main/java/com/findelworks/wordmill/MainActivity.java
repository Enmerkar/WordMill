package com.findelworks.wordmill;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.res.Resources;
import android.widget.Button;
import android.widget.ImageView;
import java.util.ArrayList;
import android.database.sqlite.SQLiteQueryBuilder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String GLOBAL_PREFERENCES = "GLOBAL_PREFERENCES";
    private static final String ACTIVE_LANGUAGE = "ACTIVE_LANGUAGE";

    private static SharedPreferences global_preferences;
    private static String active_language;

    private static final String GERMAN_PREFERENCES = "GERMAN_PREFERENCES";
    private static final String GERMAN_BUCKET_SIZE = "GERMAN_BUCKET_SIZE";
    private static final String GERMAN_MODE = "GERMAN_MODE";

    private static SharedPreferences german_preferences;
    private static int german_bucket_size;
    private static int german_mode;

    private static final String LATIN_PREFERENCES = "LATIN_PREFERENCES";
    private static final String LATIN_BUCKET_SIZE = "LATIN_BUCKET_SIZE";
    private static final String LATIN_MODE = "LATIN_MODE";

    private static SharedPreferences latin_preferences;
    private static int latin_bucket_size;
    private static int latin_mode;

    private static String table_name;
    private static int bucket_size;
    private static int round_mode;

    private static ViewStub language_stub;
    private static ViewStub practice_stub;

    private static boolean newGermanFile;
    private static boolean newLatinFile;

    private static LanguageSQLiteOpenHelper langHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        language_stub = (ViewStub) findViewById(R.id.language_page);
        practice_stub = (ViewStub) findViewById(R.id.practice_page);
        practice_stub.setVisibility(View.INVISIBLE);
        View inflated = language_stub.inflate();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Get the language currently or last studied
        global_preferences = getSharedPreferences(GLOBAL_PREFERENCES, MODE_PRIVATE);
        active_language = global_preferences.getString(ACTIVE_LANGUAGE, null);

        german_preferences = getSharedPreferences(GERMAN_PREFERENCES, MODE_PRIVATE);
        german_bucket_size = german_preferences.getInt(GERMAN_BUCKET_SIZE, 12);
        german_mode = german_preferences.getInt(GERMAN_MODE, 0);


        // Ensure that the most up-to-date data files exist both locally and in cloud.
        updateDataFiles();

        // Create database from data files if a) it doesn't exist, or
        // b) new data files have been downloaded from the cloud.
        if (!databaseExists()) {
            langHelper = new LanguageSQLiteOpenHelper(getBaseContext());
            populateDatabase(langHelper, "GERMAN");
            populateDatabase(langHelper, "LATIN");
        } else {
            langHelper = new LanguageSQLiteOpenHelper(getBaseContext());
            if (newGermanFile) populateDatabase(langHelper, "GERMAN");
            if (newLatinFile) populateDatabase(langHelper, "LATIN");
        }

        // Button to begin a practice round
        Button practice_button = (Button) findViewById(R.id.begin_practice);
        practice_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newRound();
            }
        });

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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_german) {
            active_language = "GERMAN";
            changeLanguage(active_language);
        } else if (id == R.id.nav_latin) {
            active_language = "LATIN";
            changeLanguage(active_language);
        } else if (id == R.id.nav_add) {

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_settings) {

        }

        SharedPreferences.Editor editor = global_preferences.edit();
        editor.putString(ACTIVE_LANGUAGE, active_language);
        editor.apply();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeLanguage(String language) {

        switch (active_language) {
            case "GERMAN":
                table_name = langHelper.TABLE_NAME_GERMAN;
                bucket_size = german_bucket_size;
                round_mode = german_mode;
                break;
            case "LATIN":
                table_name = langHelper.TABLE_NAME_LATIN;
                bucket_size = latin_bucket_size;
                round_mode = latin_mode;
                break;
            default: break;
        }

        // Set heading

        // Set variables which define the status bar drawable

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = global_preferences.edit();
        editor.putString(ACTIVE_LANGUAGE, active_language);
        editor.apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public boolean databaseExists() {
        File database = getApplicationContext().getDatabasePath("Language.db");
        return database.exists();
    }

    public void updateDataFiles() {
        // Compare local and cloud data files.

        // Set relevant variables if changes made locally.

        newGermanFile = true;
        newLatinFile = false;
    }

    // Populates the database table for the specified language
    public void populateDatabase(LanguageSQLiteOpenHelper helper, String language) {

        int res_id_data = 0;
        int res_id_user = 0;
        String table_name = "NONE";

        switch (language) {
            case "GERMAN":
                res_id_data = R.raw.german_data;
                res_id_user = R.raw.german_user;
                table_name = helper.TABLE_NAME_GERMAN;
                break;
            case "LATIN":
                res_id_data = R.raw.latin_data;
                res_id_user = R.raw.latin_user;
                table_name = helper.TABLE_NAME_LATIN;
                break;
            default: break;
        }

        Resources res = getResources();
        SQLiteDatabase db = helper.getWritableDatabase();

        // Clear table of old data
        db.execSQL("DELETE * FROM " + table_name);

        boolean match;
        String inputLineLang, inputLineUser;
        String[] inputArrayLang = new String[5];
        String[] inputArrayUser = new String[5];
        int id_lang, id_user, freq, flevel, flapse, blevel, blapse;
        String word, full, trans;
        long rowID;

        ContentValues row = new ContentValues();
        InputStream isLang = res.openRawResource(res_id_data);
        InputStream isUser = res.openRawResource(res_id_user);
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

                        rowID = db.insert(table_name, null, row);

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

                rowID = db.insert(table_name, null, row);

            }

        } catch (IOException e) {

        }

        try {
            brLang.close();
            brUser.close();
        } catch (IOException e) {

        }

    }

    public void newRound() {
        // Get words by most lapsed
        SQLiteDatabase langDatabase = langHelper.getReadableDatabase();
        String lapseWordQuery = "SELECT * FROM " + table_name + " WHERE " + langHelper.COLUMN_FLEVEL + " > 0 ORDER BY " + langHelper.COLUMN_FLAPSE + " ASC LIMIT " + bucket_size;
        Cursor lapseCursor = langDatabase.rawQuery(lapseWordQuery, null);

        ArrayList<Integer> round_id = new ArrayList<Integer>();
        ArrayList<Integer> round_freq = new ArrayList<Integer>();
        ArrayList<String> round_word = new ArrayList<String>();
        ArrayList<String> round_full = new ArrayList<String>();
        ArrayList<String> round_trans = new ArrayList<String>();
        ArrayList<Integer> round_flevel = new ArrayList<Integer>();
        ArrayList<Integer> round_flapse = new ArrayList<Integer>();
        ArrayList<Integer> round_blevel = new ArrayList<Integer>();
        ArrayList<Integer> round_blapse = new ArrayList<Integer>();

        lapseCursor.moveToFirst();
        while (!lapseCursor.isAfterLast()) {
            round_id.add(lapseCursor.getInt(0));
            round_freq.add(lapseCursor.getInt(1));
            round_word.add(lapseCursor.getString(2));
            round_full.add(lapseCursor.getString(3));
            round_trans.add(lapseCursor.getString(4));
            round_flevel.add(lapseCursor.getInt(5));
            round_flapse.add(lapseCursor.getInt(6));
            round_blevel.add(lapseCursor.getInt(7));
            round_blapse.add(lapseCursor.getInt(8));
            lapseCursor.moveToNext();
        }

        int additional = bucket_size - round_id.size();
        if (additional > 0) {
            String newWordQuery = "SELECT * FROM " + table_name + " WHERE " + langHelper.COLUMN_FLEVEL + " = 0 ORDER BY " + langHelper.COLUMN_FREQ + " ASC LIMIT " + additional;
            Cursor newCursor = langDatabase.rawQuery(newWordQuery, null);

            newCursor.moveToFirst();
            while (!newCursor.isAfterLast()) {
                round_id.add(newCursor.getInt(0));
                round_freq.add(newCursor.getInt(1));
                round_word.add(newCursor.getString(2));
                round_full.add(newCursor.getString(3));
                round_trans.add(newCursor.getString(4));
                round_flevel.add(newCursor.getInt(5));
                round_flapse.add(newCursor.getInt(6));
                round_blevel.add(newCursor.getInt(7));
                round_blapse.add(newCursor.getInt(8));
                newCursor.moveToNext();
            }
        }

        // Set text views in practice layout

        // Inflate practice view
        language_stub.setVisibility(View.INVISIBLE);
        View inflated = practice_stub.inflate();
    }

}

