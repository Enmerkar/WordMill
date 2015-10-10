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
import java.util.Collections;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.res.Resources;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import android.database.sqlite.SQLiteQueryBuilder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String GLOBAL_PREFERENCES = "GLOBAL_PREFERENCES";
    private static final String ACTIVE_LANGUAGE = "ACTIVE_LANGUAGE";
    private static final String FIBONACCI_MODE = "FIBONACCI_MODE";
    private static final String PRIME_MODE = "PRIME_MODE";
    private static final String ODD_MODE = "ODD_MODE";

    private static final String GERMAN_LANGUAGE = "GERMAN";
    private static final String GERMAN_PREFERENCES = "GERMAN_PREFERENCES";
    private static final String GERMAN_DIRECTION = "GERMAN_DIRECTION";
    private static final String GERMAN_BUCKET_SIZE = "GERMAN_BUCKET_SIZE";
    private static final String GERMAN_LAPSE_MODE = "GERMAN_LAPSE_MODE";

    private static final String LATIN_LANGUAGE = "LATIN";
    private static final String LATIN_PREFERENCES = "LATIN_PREFERENCES";
    private static final String LATIN_DIRECTION = "LATIN_DIRECTION";
    private static final String LATIN_BUCKET_SIZE = "LATIN_BUCKET_SIZE";
    private static final String LATIN_LAPSE_MODE = "LATIN_LAPSE_MODE";

    private static SharedPreferences global_preferences;
    private static SharedPreferences german_preferences;
    private static SharedPreferences latin_preferences;

    private static String active_language;
    private static String table_name;

    private static int language_mode;
    private static int bucket_size;
    private static String lapse_mode;

    private static LanguageSQLiteOpenHelper langHelper;
    private static SQLiteDatabase writableDatabase;
    private static SQLiteDatabase readableDatabase;

    private static ViewStub language_stub;
    private static ViewStub practice_stub;

    private static boolean newGermanFile;
    private static boolean newLatinFile;

    private static TextView main_message;
    private static TextView main_passed;
    private static TextView main_failed;
    private static TextView word_view;
    private static TextView full_view;
    private static TextView blank_view;
    private static TextView trans_view;
    private static LinearLayout check_view;
    private static LinearLayout respond_view;

    private static ArrayList<Integer> round_id;
    private static ArrayList<Integer> round_freq;
    private static ArrayList<String> round_word;
    private static ArrayList<String> round_full;
    private static ArrayList<String> round_trans;
    private static ArrayList<Integer> round_flevel;
    private static ArrayList<Integer> round_flapse;
    private static ArrayList<Integer> round_blevel;
    private static ArrayList<Integer> round_blapse;

    private static ArrayList<Integer> round_shuffled;
    private static int yes_count;
    private static int no_count;

    private static int current_id;
    private static int current_flevel;
    private static int current_blevel;

    private static int[] fibonacci = {1,2,3,5,8,13,21,34,55,89,144,233};
    private static int[] prime = {1,2,3,5,7,11,13,17,19,29,23,31};
    private static int[] odd = {1,3,5,7,9,11,13,15,17,19,21,23};

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

        // Set preference variables
        global_preferences = getSharedPreferences(GLOBAL_PREFERENCES, MODE_PRIVATE);
        german_preferences = getSharedPreferences(GERMAN_PREFERENCES, MODE_PRIVATE);
        latin_preferences = getSharedPreferences(LATIN_PREFERENCES, MODE_PRIVATE);

        // Set view variables
        main_message = (TextView) findViewById(R.id.main_message);
        main_passed = (TextView) findViewById(R.id.main_passed);
        main_failed = (TextView) findViewById(R.id.main_failed);
        word_view = (TextView) findViewById(R.id.view_word);
        full_view = (TextView) findViewById(R.id.view_full);
        blank_view = (TextView) findViewById(R.id.view_blank);
        trans_view = (TextView) findViewById(R.id.view_trans);
        check_view = (LinearLayout) findViewById(R.id.view_check);
        respond_view = (LinearLayout) findViewById(R.id.view_respond);

        // Ensure that the most up-to-date data files exist both locally and in cloud.
        updateDataFiles();

        // Create database from data files if a) it doesn't exist, or
        // b) new data files have been downloaded from the cloud.
        if (!databaseExists()) {
            langHelper = new LanguageSQLiteOpenHelper(getBaseContext());
            populateDatabase(GERMAN_LANGUAGE);
            populateDatabase(LATIN_LANGUAGE);
        } else {
            langHelper = new LanguageSQLiteOpenHelper(getBaseContext());
            if (newGermanFile) populateDatabase(GERMAN_LANGUAGE);
            if (newLatinFile) populateDatabase(LATIN_LANGUAGE);
        }

        // Restore previously active language
        active_language = global_preferences.getString(ACTIVE_LANGUAGE, null);
        changeLanguage(active_language);

        // Click listener for begin new round button
        Button begin_round = (Button) findViewById(R.id.begin_round);
        begin_round.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                beginRound();
            }
        });

        // Click listener for reverse direction button
        Button reverse_button = (Button) findViewById(R.id.reverse_button);
        reverse_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reverseDirection();
            }
        });

        // Click listener for check button
        Button check_button = (Button) findViewById(R.id.button_check);
        check_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkWord();
            }
        });

        // Click listener for no button
        Button no_button = (Button) findViewById(R.id.button_no);
        no_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onUserRespond(false);
            }
        });

        // Click listener for yes button
        Button yes_button = (Button) findViewById(R.id.button_yes);
        yes_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onUserRespond(true);
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

        switch (id) {
            case R.id.nav_german: changeLanguage(GERMAN_LANGUAGE); break;
            case R.id.nav_latin: changeLanguage(LATIN_LANGUAGE); break;
            case R.id.nav_add: break;
            case R.id.nav_profile: break;
            case R.id.nav_share: break;
            case R.id.nav_settings: break;
            default: break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeLanguage(String language) {

        switch (active_language) {
            case GERMAN_LANGUAGE:
                active_language = GERMAN_LANGUAGE;
                table_name = LanguageSQLiteOpenHelper.TABLE_NAME_GERMAN;
                language_mode = german_preferences.getInt(GERMAN_DIRECTION, 0);
                bucket_size = german_preferences.getInt(GERMAN_BUCKET_SIZE, 12);
                lapse_mode = german_preferences.getString(GERMAN_LAPSE_MODE, FIBONACCI_MODE);
                break;
            case LATIN_LANGUAGE:
                active_language = LATIN_LANGUAGE;
                table_name = LanguageSQLiteOpenHelper.TABLE_NAME_LATIN;
                language_mode = latin_preferences.getInt(LATIN_DIRECTION, 0);
                bucket_size = latin_preferences.getInt(LATIN_BUCKET_SIZE, 12);
                lapse_mode = latin_preferences.getString(LATIN_LAPSE_MODE, FIBONACCI_MODE);
                break;
            default: break;
        }

        SharedPreferences.Editor editor = global_preferences.edit();
        editor.putString(ACTIVE_LANGUAGE, active_language);
        editor.apply();

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
    public void populateDatabase(String language) {

        int res_id_data = 0;
        int res_id_user = 0;
        String table_name = "NONE";

        switch (language) {
            case "GERMAN":
                res_id_data = R.raw.german_data;
                res_id_user = R.raw.german_user;
                table_name = LanguageSQLiteOpenHelper.TABLE_NAME_GERMAN;
                break;
            case "LATIN":
                res_id_data = R.raw.latin_data;
                res_id_user = R.raw.latin_user;
                table_name = LanguageSQLiteOpenHelper.TABLE_NAME_LATIN;
                break;
            default: break;
        }

        Resources res = getResources();
        writableDatabase = langHelper.getWritableDatabase();

        // Clear table of old data
        writableDatabase.execSQL("DELETE * FROM " + table_name);

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
                            row.put(LanguageSQLiteOpenHelper.COLUMN_FLEVEL, flevel);
                            row.put(LanguageSQLiteOpenHelper.COLUMN_FLAPSE, flapse);
                            row.put(LanguageSQLiteOpenHelper.COLUMN_BLEVEL, blevel);
                            row.put(LanguageSQLiteOpenHelper.COLUMN_BLAPSE, blapse);
                        } else {
                            row.put(LanguageSQLiteOpenHelper.COLUMN_FLEVEL, 0);
                            row.put(LanguageSQLiteOpenHelper.COLUMN_FLAPSE, 0);
                            row.put(LanguageSQLiteOpenHelper.COLUMN_BLEVEL, 0);
                            row.put(LanguageSQLiteOpenHelper.COLUMN_BLAPSE, 0);
                        }

                        row.put(LanguageSQLiteOpenHelper.COLUMN_ID, id_lang);
                        row.put(LanguageSQLiteOpenHelper.COLUMN_FREQ, freq);
                        row.put(LanguageSQLiteOpenHelper.COLUMN_WORD, word);
                        row.put(LanguageSQLiteOpenHelper.COLUMN_FULL, full);
                        row.put(LanguageSQLiteOpenHelper.COLUMN_TRANS, trans);

                        rowID = writableDatabase.insert(table_name, null, row);

                    }

                } catch (IOException e) {

                }
            }

            // Copy in any trailing unattempted words into the database
            while ((inputLineLang = brLang.readLine()) != null) {

                inputArrayLang = inputLineUser.split("\t");

                row.put(LanguageSQLiteOpenHelper.COLUMN_ID, id_lang);
                row.put(LanguageSQLiteOpenHelper.COLUMN_FREQ, freq);
                row.put(LanguageSQLiteOpenHelper.COLUMN_WORD, word);
                row.put(LanguageSQLiteOpenHelper.COLUMN_FULL, full);
                row.put(LanguageSQLiteOpenHelper.COLUMN_TRANS, trans);
                row.put(LanguageSQLiteOpenHelper.COLUMN_FLEVEL, 0);
                row.put(LanguageSQLiteOpenHelper.COLUMN_FLAPSE, 0);
                row.put(LanguageSQLiteOpenHelper.COLUMN_BLEVEL, 0);
                row.put(LanguageSQLiteOpenHelper.COLUMN_BLAPSE, 0);

                rowID = writableDatabase.insert(table_name, null, row);

            }

        } catch (IOException e) {

        }

        try {
            brLang.close();
            brUser.close();
        } catch (IOException e) {

        }

        writableDatabase.close();

    }

    public void beginRound() {

        yes_count = no_count = 0;

        // Get words by most lapsed
        writableDatabase = langHelper.getWritableDatabase();
        String lapseWordQuery = null;

        if (language_mode == 0) {
            lapseWordQuery = "SELECT * FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_FLEVEL + " > 0 ORDER BY " + LanguageSQLiteOpenHelper.COLUMN_FLAPSE + " ASC LIMIT " + bucket_size;
        } else {
            lapseWordQuery = "SELECT * FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_BLEVEL + " > 0 ORDER BY " + LanguageSQLiteOpenHelper.COLUMN_BLAPSE + " ASC LIMIT " + bucket_size;
        }

        Cursor lapseCursor = writableDatabase.rawQuery(lapseWordQuery, null);

        round_id = new ArrayList<Integer>();
        round_freq = new ArrayList<Integer>();
        round_word = new ArrayList<String>();
        round_full = new ArrayList<String>();
        round_trans = new ArrayList<String>();
        round_flevel = new ArrayList<Integer>();
        round_flapse = new ArrayList<Integer>();
        round_blevel = new ArrayList<Integer>();
        round_blapse = new ArrayList<Integer>();

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

        lapseCursor.close();

        int additional = bucket_size - round_id.size();
        if (additional > 0) {

            String newWordQuery = null;

            if (language_mode == 0) {
                newWordQuery = "SELECT * FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_FLEVEL +
                        " = 0 ORDER BY " + LanguageSQLiteOpenHelper.COLUMN_FREQ + " ASC LIMIT " + additional;
            } else {
                newWordQuery = "SELECT * FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_BLEVEL +
                        " = 0 ORDER BY " + LanguageSQLiteOpenHelper.COLUMN_FREQ + " ASC LIMIT " + additional;
            }

            Cursor newCursor = writableDatabase.rawQuery(newWordQuery, null);

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

            newCursor.close();

        }

        round_shuffled = new ArrayList<Integer>();
        for (int i = 0; i < round_id.size(); i++) {
            round_shuffled.add(i);
        }
        Collections.shuffle(round_shuffled);

        // Inflate practice view
        language_stub.setVisibility(View.GONE);
        View inflated = practice_stub.inflate();

        if (round_shuffled.size() > 0) {
            askWord();
        } else {
            writableDatabase.close();
        }

    }

    public static void askWord () {

        check_view.setVisibility(View.VISIBLE);
        respond_view.setVisibility(View.GONE);
        blank_view.setVisibility(View.VISIBLE);
        trans_view.setVisibility(View.GONE);

        int i = round_shuffled.get(0);
        round_shuffled.remove(0);

        word_view.setText(round_word.get(i));
        full_view.setText(round_full.get(i));
        trans_view.setText(round_trans.get(i));

        current_id = round_id.get(i);
        current_flevel = round_flevel.get(i);
        current_blevel = round_blevel.get(i);

    }

    public static void checkWord () {

        check_view.setVisibility(View.GONE);
        respond_view.setVisibility(View.VISIBLE);
        blank_view.setVisibility(View.GONE);
        trans_view.setVisibility(View.VISIBLE);

    }

    public static void onUserRespond (boolean known) {

        int new_flevel, new_flapse, new_blevel, new_blapse;
        String updateQuery = null;

        if (language_mode == 0) {
            if (known) {
                yes_count++;
                new_flevel = current_flevel - 1;
                new_flapse = getLapse(new_flevel);
                updateQuery = "UDPATE SET " + LanguageSQLiteOpenHelper.COLUMN_FLEVEL + " = " + new_flevel + ", " + LanguageSQLiteOpenHelper.COLUMN_FLAPSE + " = " + new_flapse
                        + " FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_ID + " = " + current_id;
            } else {
                no_count++;
                new_flevel = current_flevel + 1;
                new_flapse = getLapse(new_flevel);
                updateQuery = "UDPATE SET " + LanguageSQLiteOpenHelper.COLUMN_FLEVEL + " = " + new_flevel + ", " + LanguageSQLiteOpenHelper.COLUMN_FLAPSE + " = " + new_flapse
                        + " FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_ID + " = " + current_id;
            }
        } else {
            if (known) {
                yes_count++;
                new_blevel = current_blevel - 1;
                new_blapse = getLapse(new_blevel);
                updateQuery = "UDPATE SET " + LanguageSQLiteOpenHelper.COLUMN_BLEVEL + " = " + new_blevel + ", " + LanguageSQLiteOpenHelper.COLUMN_BLAPSE + " = " + new_blapse
                        + " FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_ID + " = " + current_id;
            } else {
                no_count++;
                new_blevel = current_blevel + 1;
                new_blapse = getLapse(new_blevel);
                updateQuery = "UDPATE SET " + LanguageSQLiteOpenHelper.COLUMN_BLEVEL + " = " + new_blevel + ", " + LanguageSQLiteOpenHelper.COLUMN_BLAPSE + " = " + new_blapse
                        + " FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_ID + " = " + current_id;
            }
        }

        writableDatabase.execSQL(updateQuery);

        if (round_shuffled.size() > 0) {
            askWord();
        } else {
            endRound();
        }

    }

    public static void endRound() {

        main_message.setText(R.string.round_complete);
        main_passed.setText(R.string.words_passed + yes_count);
        main_failed.setText(R.string.words_failed + no_count);



        writableDatabase.close();

        // Inflate language view
        practice_stub.setVisibility(View.GONE);
        View inflated = language_stub.inflate();

    }

    public static int getLapse (int level) {

        switch (lapse_mode) {
            case PRIME_MODE: return prime[level-1];
            case ODD_MODE: return odd[level-1];
            default: return fibonacci[level-1];
        }

    }

    public static boolean languageBackupText () {

        readableDatabase = langHelper.getReadableDatabase();
        String createViewQuery = "CREATE VIEW ordered_table AS SELECT * FROM " +
                LanguageSQLiteOpenHelper.TABLE_NAME_GERMAN + " ORDER BY " + LanguageSQLiteOpenHelper.COLUMN_ID;

        return true;
    }

}

