package com.findelworks.wordmill;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import java.lang.Long;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Collections;
import android.text.TextUtils;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.res.Resources;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String GERMAN_LANGUAGE = "GERMAN";
    private static final String LATIN_LANGUAGE = "LATIN";
    private static final String ATTIC_LANGUAGE = "ATTIC";

    private static final String GERMAN_USER_FILE = "german_user.txt";
    private static final String LATIN_USER_FILE = "latin_user.txt";
    private static final String ATTIC_USER_FILE = "attic_user.txt";

    private static final String GLOBAL_PREFERENCES = "GLOBAL_PREFERENCES";
    private static final String LAST_DECREMENT_TIME = "LAST_DECREMENT_TIME";
    private static final String LAPSE_DECREMENT_HOUR_PREFERENCE = "LAPSE_DECREMENT_HOUR_PREFERENCE";

    private static final String GERMAN_PREFERENCES = "GERMAN_PREFERENCES";
    private static final String LATIN_PREFERENCES = "LATIN_PREFERENCES";
    private static final String ATTIC_PREFERENCES = "ATTIC_PREFERENCES";

    private static final String DIRECTION_PREFERENCE = "DIRECTION_PREFERENCE";
    private static final String BUCKET_SIZE_PREFERENCE = "BUCKET_SIZE_PREFERENCE";
    private static final String DAILY_LIMIT_PREFERENCE = "DAILY_LIMIT_PREFERENCE";
    private static final String LAPSE_MODE_PREFERENCE = "LAPSE_MODE_PREFERENCE";
    private static final String FULL_MODE_PREFERENCE = "FULL_MODE_PREFERENCE";

    private static final String ACTIVE_LANGUAGE = "ACTIVE_LANGUAGE";
    private static final String FIBONACCI_MODE = "FIBONACCI_MODE";
    private static final String PRIME_MODE = "PRIME_MODE";
    private static final String ODD_MODE = "ODD_MODE";
    private static final String WORDS_ENCOUNTERED = "WORDS_ENCOUNTERED";
    private static final String WORDS_LAPSED = "WORDS_LAPSED";
    private static final String WORDS_TOTAL = "WORDS_TOTAL";
    private static final String NEW_WORDS_TODAY = "NEW_WORDS_TODAY";

    private static final String DEFAULT_LANGUAGE = "LATIN";
    private static final int DEFAULT_LAPSE_DECREMENT_HOUR = 3;

    private static final int DEFAULT_BUCKET_SIZE = 12;
    private static final int DEFAULT_DAILY_LIMIT = 20;
    private static final boolean DEFAULT_DIRECTION = true;
    private static final boolean DEFAULT_FULL_MODE = true;
    private static final int DEFAULT_WORDS_ENCOUNTERED = 0;
    private static final int DEFAULT_WORDS_LAPSED = 0;
    private static final int DEFAULT_WORDS_TOTAL = 2000;
    private static final int DEFAULT_NEW_WORDS_TODAY = 0;

    private static SharedPreferences global_preferences;
    private static SharedPreferences german_preferences;
    private static SharedPreferences latin_preferences;
    private static SharedPreferences attic_preferences;

    private static String active_language;
    private static int word_total;
    private static String table_name;
    private static int lapse_decrement_hour;
    private static long last_decrement_time;
    private static boolean forward_mode;
    private static int bucket_size;
    private static int daily_limit;
    private static String lapse_mode;
    private static boolean full_mode;
    private static int words_learned;
    private static int words_lapsed;
    private static int new_words;

    private static LanguageSQLiteOpenHelper langHelper;
    private static SQLiteDatabase writableDatabase;

    private static boolean newGermanFile;
    private static boolean newLatinFile;
    private static boolean newAtticFile;

    private static TextView main_message;
    private static TextView main_total;
    private static TextView main_learned;
    private static TextView main_lapsed;
    private static TextView main_passed;
    private static TextView main_failed;
    private static TextView word_view;
    private static TextView full_view;
    private static TextView blank_view;
    private static TextView trans_view;
    private static LinearLayout check_view;
    private static LinearLayout respond_view;
    private static LinearLayout next_view;

    private static Bitmap b;
    private static Canvas c;
    private static Paint p;
    private static float learnedArcStart;
    private static float learnedArcSweep;
    private static float lapsedArcStart;
    private static float lapsedArcSweep;

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

    private static int[] fibonacci = {0,0,1,2,3,5,8,13,21,34,55,89,144,233};
    private static int[] middle = {};
    private static int[] prime = {0,0,1,2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101};
    private static int[] odd = {0,0,1,3,5,7,9,11,13,15,17,19,21,23};

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

        // Set preference variables
        global_preferences = getSharedPreferences(GLOBAL_PREFERENCES, MODE_PRIVATE);
        german_preferences = getSharedPreferences(GERMAN_PREFERENCES, MODE_PRIVATE);
        latin_preferences = getSharedPreferences(LATIN_PREFERENCES, MODE_PRIVATE);
        attic_preferences = getSharedPreferences(ATTIC_PREFERENCES, MODE_PRIVATE);

        // Set view variables
        main_message = (TextView) findViewById(R.id.main_message);
        main_total = (TextView) findViewById(R.id.main_total);
        main_learned = (TextView) findViewById(R.id.main_learned);
        main_lapsed = (TextView) findViewById(R.id.main_lapsed);
        main_passed = (TextView) findViewById(R.id.main_passed);
        main_failed = (TextView) findViewById(R.id.main_failed);
        word_view = (TextView) findViewById(R.id.view_word);
        full_view = (TextView) findViewById(R.id.view_full);
        blank_view = (TextView) findViewById(R.id.view_blank);
        trans_view = (TextView) findViewById(R.id.view_trans);
        check_view = (LinearLayout) findViewById(R.id.view_check);
        respond_view = (LinearLayout) findViewById(R.id.view_respond);
        next_view = (LinearLayout) findViewById(R.id.view_next);

        // Create button image components
        b = Bitmap.createBitmap(2000, 2000, Bitmap.Config.ARGB_8888);
        c = new Canvas(b);
        p = new Paint();

        // Ensure that the most up-to-date data files exist both locally and in cloud.
        updateDataFiles();

        // Create database from data files if a) it doesn't exist, or
        // b) new data files have been downloaded from the cloud.
        if (!databaseExists()) {
            langHelper = new LanguageSQLiteOpenHelper(getBaseContext());
            writableDatabase = langHelper.getWritableDatabase();
            populateDatabase(GERMAN_LANGUAGE);
            populateDatabase(LATIN_LANGUAGE);
            populateDatabase(ATTIC_LANGUAGE);
        } else {
            langHelper = new LanguageSQLiteOpenHelper(getBaseContext());
            writableDatabase = langHelper.getWritableDatabase();
            if (newGermanFile) populateDatabase(GERMAN_LANGUAGE);
            if (newLatinFile) populateDatabase(LATIN_LANGUAGE);
            if (newAtticFile) populateDatabase(ATTIC_LANGUAGE);
        }

        // Restore previously active language
        try {
            active_language = global_preferences.getString(ACTIVE_LANGUAGE, ATTIC_LANGUAGE);
            lapse_decrement_hour = global_preferences.getInt(LAPSE_DECREMENT_HOUR_PREFERENCE, DEFAULT_LAPSE_DECREMENT_HOUR);
            daily_limit = global_preferences.getInt(DAILY_LIMIT_PREFERENCE, DEFAULT_DAILY_LIMIT);
        } catch (ClassCastException e) {
            SharedPreferences.Editor editor = global_preferences.edit();
            editor.putString(ACTIVE_LANGUAGE, DEFAULT_LANGUAGE);
            editor.putInt(LAPSE_DECREMENT_HOUR_PREFERENCE, DEFAULT_LAPSE_DECREMENT_HOUR);
            editor.putInt(DAILY_LIMIT_PREFERENCE, DEFAULT_DAILY_LIMIT);
            editor.apply();
            active_language = DEFAULT_LANGUAGE;
        }

        // Set/get the language specific preferences
        setLanguage(active_language);

        // Click listener for begin new round button
        View begin_round = findViewById(R.id.begin_round_view);
        begin_round.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                beginRound();
            }
        });

        // Click listener for reverse direction button
        View reverse_button = findViewById(R.id.reverse_button_view);
        reverse_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reverseDirection();
            }
        });

        // Click listener for check button
        Button check_button = (Button) findViewById(R.id.button_check);
        check_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkWord(true);
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

        // Click listener for next button
        Button next_button = (Button) findViewById(R.id.button_next);
        next_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        languageBackupText(active_language);

        writableDatabase.close();

    }

    @Override
    protected void onResume() {
        super.onResume();

        writableDatabase = langHelper.getWritableDatabase();

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
            case R.id.nav_german: setLanguage(GERMAN_LANGUAGE); break;
            case R.id.nav_latin: setLanguage(LATIN_LANGUAGE); break;
            case R.id.nav_attic: setLanguage(ATTIC_LANGUAGE); break;
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

    public void setLanguage(String language) {

        Date now = new Date();
        Long now_time = now.getTime();

        switch (language) {
            case GERMAN_LANGUAGE:
                active_language = GERMAN_LANGUAGE;
                table_name = LanguageSQLiteOpenHelper.TABLE_NAME_GERMAN;
                try {
                    last_decrement_time = german_preferences.getLong(LAST_DECREMENT_TIME, now_time);
                    forward_mode = german_preferences.getBoolean(DIRECTION_PREFERENCE, DEFAULT_DIRECTION);
                    bucket_size = german_preferences.getInt(BUCKET_SIZE_PREFERENCE, DEFAULT_BUCKET_SIZE);
                    lapse_mode = german_preferences.getString(LAPSE_MODE_PREFERENCE, FIBONACCI_MODE);
                    full_mode = german_preferences.getBoolean(FULL_MODE_PREFERENCE, DEFAULT_FULL_MODE);
                    words_learned = german_preferences.getInt(WORDS_ENCOUNTERED, DEFAULT_WORDS_ENCOUNTERED);
                    words_lapsed = german_preferences.getInt(WORDS_LAPSED, DEFAULT_WORDS_LAPSED);
                    word_total = german_preferences.getInt(WORDS_TOTAL, DEFAULT_WORDS_TOTAL);
                    decrementLapseDays(now_time);
                    new_words = german_preferences.getInt(NEW_WORDS_TODAY, DEFAULT_NEW_WORDS_TODAY);
                } catch (ClassCastException e) {
                    SharedPreferences.Editor german_editor = german_preferences.edit();
                    german_editor.putLong(LAST_DECREMENT_TIME, now_time);
                    german_editor.putBoolean(DIRECTION_PREFERENCE, DEFAULT_DIRECTION);
                    german_editor.putInt(BUCKET_SIZE_PREFERENCE, DEFAULT_BUCKET_SIZE);
                    german_editor.putString(LAPSE_MODE_PREFERENCE, FIBONACCI_MODE);
                    german_editor.putBoolean(FULL_MODE_PREFERENCE, DEFAULT_FULL_MODE);
                    german_editor.putInt(WORDS_ENCOUNTERED, DEFAULT_WORDS_ENCOUNTERED);
                    german_editor.putInt(WORDS_LAPSED, DEFAULT_WORDS_LAPSED);
                    german_editor.putInt(WORDS_TOTAL, DEFAULT_WORDS_TOTAL);
                    german_editor.putInt(NEW_WORDS_TODAY, DEFAULT_NEW_WORDS_TODAY);
                    german_editor.apply();
                }
                break;
            case LATIN_LANGUAGE:
                active_language = LATIN_LANGUAGE;
                table_name = LanguageSQLiteOpenHelper.TABLE_NAME_LATIN;
                try {
                    last_decrement_time = latin_preferences.getLong(LAST_DECREMENT_TIME, now_time);
                    forward_mode = latin_preferences.getBoolean(DIRECTION_PREFERENCE, DEFAULT_DIRECTION);
                    bucket_size = latin_preferences.getInt(BUCKET_SIZE_PREFERENCE, DEFAULT_BUCKET_SIZE);
                    lapse_mode = latin_preferences.getString(LAPSE_MODE_PREFERENCE, FIBONACCI_MODE);
                    full_mode = latin_preferences.getBoolean(FULL_MODE_PREFERENCE, DEFAULT_FULL_MODE);
                    words_learned = latin_preferences.getInt(WORDS_ENCOUNTERED, DEFAULT_WORDS_ENCOUNTERED);
                    words_lapsed = latin_preferences.getInt(WORDS_LAPSED, DEFAULT_WORDS_LAPSED);
                    word_total = latin_preferences.getInt(WORDS_TOTAL, DEFAULT_WORDS_TOTAL);
                    decrementLapseDays(now_time);
                    new_words = latin_preferences.getInt(NEW_WORDS_TODAY, DEFAULT_NEW_WORDS_TODAY);
                } catch (ClassCastException e) {
                    SharedPreferences.Editor latin_editor = latin_preferences.edit();
                    latin_editor.putLong(LAST_DECREMENT_TIME, now_time);
                    latin_editor.putBoolean(DIRECTION_PREFERENCE, DEFAULT_DIRECTION);
                    latin_editor.putInt(BUCKET_SIZE_PREFERENCE, DEFAULT_BUCKET_SIZE);
                    latin_editor.putString(LAPSE_MODE_PREFERENCE, FIBONACCI_MODE);
                    latin_editor.putBoolean(FULL_MODE_PREFERENCE, DEFAULT_FULL_MODE);
                    latin_editor.putInt(WORDS_ENCOUNTERED, DEFAULT_WORDS_ENCOUNTERED);
                    latin_editor.putInt(WORDS_LAPSED, DEFAULT_WORDS_LAPSED);
                    latin_editor.putInt(WORDS_TOTAL, DEFAULT_WORDS_TOTAL);
                    latin_editor.putInt(NEW_WORDS_TODAY, DEFAULT_NEW_WORDS_TODAY);
                    latin_editor.apply();
                }
                break;
            case ATTIC_LANGUAGE:
                active_language = ATTIC_LANGUAGE;
                table_name = LanguageSQLiteOpenHelper.TABLE_NAME_ATTIC;
                try {
                    last_decrement_time = german_preferences.getLong(LAST_DECREMENT_TIME, now_time);
                    forward_mode = attic_preferences.getBoolean(DIRECTION_PREFERENCE, DEFAULT_DIRECTION);
                    bucket_size = attic_preferences.getInt(BUCKET_SIZE_PREFERENCE, DEFAULT_BUCKET_SIZE);
                    lapse_mode = attic_preferences.getString(LAPSE_MODE_PREFERENCE, FIBONACCI_MODE);
                    full_mode = attic_preferences.getBoolean(FULL_MODE_PREFERENCE, DEFAULT_FULL_MODE);
                    words_learned = attic_preferences.getInt(WORDS_ENCOUNTERED, DEFAULT_WORDS_ENCOUNTERED);
                    words_lapsed = attic_preferences.getInt(WORDS_LAPSED, DEFAULT_WORDS_LAPSED);
                    word_total = attic_preferences.getInt(WORDS_TOTAL, DEFAULT_WORDS_TOTAL);
                    decrementLapseDays(now_time);
                    new_words = attic_preferences.getInt(NEW_WORDS_TODAY, DEFAULT_NEW_WORDS_TODAY);
                } catch (ClassCastException e) {
                    SharedPreferences.Editor attic_editor = attic_preferences.edit();
                    attic_editor.putLong(LAST_DECREMENT_TIME, now_time);
                    attic_editor.putBoolean(DIRECTION_PREFERENCE, DEFAULT_DIRECTION);
                    attic_editor.putInt(BUCKET_SIZE_PREFERENCE, DEFAULT_BUCKET_SIZE);
                    attic_editor.putString(LAPSE_MODE_PREFERENCE, FIBONACCI_MODE);
                    attic_editor.putBoolean(FULL_MODE_PREFERENCE, DEFAULT_FULL_MODE);
                    attic_editor.putInt(WORDS_ENCOUNTERED, DEFAULT_WORDS_ENCOUNTERED);
                    attic_editor.putInt(WORDS_LAPSED, DEFAULT_WORDS_LAPSED);
                    attic_editor.putInt(WORDS_TOTAL, DEFAULT_WORDS_TOTAL);
                    attic_editor.putInt(NEW_WORDS_TODAY, DEFAULT_NEW_WORDS_TODAY);
                    attic_editor.apply();
                }
                break;
        }

        SharedPreferences.Editor editor = global_preferences.edit();
        editor.putString(ACTIVE_LANGUAGE, active_language);
        editor.apply();

        setMainScreen();

    }

    public static void decrementLapseDays (long now) {

        // This needs to be updated to account for lapse_time_preference
        long difference = now - last_decrement_time;
        long days_long = difference / 86400000;
        int days = (int) days_long;

        if (days > 1) {

            String decrementFlapseQuery = "UDPATE " + table_name +
                    " SET " + LanguageSQLiteOpenHelper.COLUMN_FLAPSE + " = " + LanguageSQLiteOpenHelper.COLUMN_FLAPSE + " - " + days +
                    " WHERE" + LanguageSQLiteOpenHelper.COLUMN_FLEVEL + " > 1";

            String decrementBlapseQuery = "UDPATE " + table_name +
                    " SET " + LanguageSQLiteOpenHelper.COLUMN_BLAPSE + " = " + LanguageSQLiteOpenHelper.COLUMN_BLAPSE + " - " + days +
                    " WHERE" + LanguageSQLiteOpenHelper.COLUMN_BLEVEL + " > 1";

            writableDatabase.execSQL(decrementFlapseQuery);
            writableDatabase.execSQL(decrementBlapseQuery);

            switch (active_language) {
                case GERMAN_LANGUAGE:
                    SharedPreferences.Editor german_editor = german_preferences.edit();
                    german_editor.putLong(LAST_DECREMENT_TIME, now);
                    german_editor.putInt(NEW_WORDS_TODAY, 0);
                    german_editor.apply();
                    break;
                case LATIN_LANGUAGE:
                    SharedPreferences.Editor latin_editor = latin_preferences.edit();
                    latin_editor.putLong(LAST_DECREMENT_TIME, now);
                    latin_editor.putInt(NEW_WORDS_TODAY, 0);
                    latin_editor.apply();
                    break;
                case ATTIC_LANGUAGE:
                    SharedPreferences.Editor attic_editor = attic_preferences.edit();
                    attic_editor.putLong(LAST_DECREMENT_TIME, now);
                    attic_editor.putInt(NEW_WORDS_TODAY, 0);
                    attic_editor.apply();
                    break;
            }

        }

    }

    public void reverseDirection() {

        forward_mode = !forward_mode;

        switch (active_language) {
            case GERMAN_LANGUAGE:
                SharedPreferences.Editor german_editor = german_preferences.edit();
                german_editor.putBoolean(DIRECTION_PREFERENCE, forward_mode);
                german_editor.apply();
                break;
            case LATIN_LANGUAGE:
                SharedPreferences.Editor latin_editor = latin_preferences.edit();
                latin_editor.putBoolean(DIRECTION_PREFERENCE, forward_mode);
                latin_editor.apply();
                break;
            case ATTIC_LANGUAGE:
                SharedPreferences.Editor attic_editor = attic_preferences.edit();
                attic_editor.putBoolean(DIRECTION_PREFERENCE, forward_mode);
                attic_editor.apply();
                break;
            default: break;
        }

    }

    public void setMainScreen () {

        String learnedWordQuery, lapsedWordQuery;

        if (forward_mode) {
            learnedWordQuery = "SELECT COUNT(*) FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_FLEVEL + " > 0";
            lapsedWordQuery = "SELECT COUNT(*) FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_FLEVEL + " > 0 AND " + LanguageSQLiteOpenHelper.COLUMN_FLAPSE + " < 1";
        } else {
            learnedWordQuery = "SELECT COUNT(*) FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_BLEVEL + " > 0";
            lapsedWordQuery = "SELECT COUNT(*) FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_BLEVEL + " > 0 AND " + LanguageSQLiteOpenHelper.COLUMN_BLAPSE + " < 1";
        }

        Cursor learnedWordCursor = writableDatabase.rawQuery(learnedWordQuery, null);
        Cursor lapsedWordCursor = writableDatabase.rawQuery(lapsedWordQuery, null);

        learnedWordCursor.moveToFirst();
        lapsedWordCursor.moveToFirst();

        words_learned = learnedWordCursor.getInt(0);
        words_lapsed = lapsedWordCursor.getInt(0);

        learnedWordCursor.close();
        lapsedWordCursor.close();

        String total_message = getString(R.string.main_total) + " " + Integer.toString(word_total);
        String learned_message = getString(R.string.words_learned) + " " + Integer.toString(words_learned);
        String lapsed_message = getString(R.string.words_lapsed) + " " + Integer.toString(words_lapsed);
        String yes_message = getString(R.string.words_passed) + " " + Integer.toString(yes_count);
        String no_message = getString(R.string.words_failed) + " " + Integer.toString(no_count);

        main_message.setText(active_language);
        main_total.setText(total_message);
        main_learned.setText(learned_message);
        main_lapsed.setText(lapsed_message);
        main_passed.setText(yes_message);
        main_failed.setText(no_message);

        drawButton(words_learned, words_lapsed);

    }

    public void drawButton(double learned, double lapsed) {

        // Clear the canvas
        //c.drawColor(ContextCompat.getColor(this, R.color.colorBackground));

        learnedArcStart = 270;
        learnedArcSweep = Math.round(360*((learned-lapsed)/learned));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            p.setStyle(Paint.Style.STROKE);
            p.setAntiAlias(true);
            p.setStrokeWidth(250);
            p.setColor(ContextCompat.getColor(this, R.color.colorBlack));
            c.drawArc(400, 400, 1600, 1600, 0, 360, false, p);
            p.setStrokeWidth(220);
            p.setColor(ContextCompat.getColor(this, R.color.colorLapsed));
            c.drawArc(400, 400, 1600, 1600, 0, 360, false, p);
            p.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            c.drawArc(400, 400, 1600, 1600, learnedArcStart, learnedArcSweep, false, p);
        }

        ImageView begin_round = (ImageView) findViewById(R.id.begin_round_view);
        begin_round.setImageBitmap(b);

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
        String file_user = null;
        String table_name = null;

        switch (language) {
            case GERMAN_LANGUAGE:
                res_id_data = R.raw.german_data;
                file_user = GERMAN_USER_FILE;
                table_name = LanguageSQLiteOpenHelper.TABLE_NAME_GERMAN;
                break;
            case LATIN_LANGUAGE:
                res_id_data = R.raw.latin_data;
                file_user = LATIN_USER_FILE;
                table_name = LanguageSQLiteOpenHelper.TABLE_NAME_LATIN;
                break;
            case ATTIC_LANGUAGE:
                res_id_data = R.raw.attic_data;
                file_user = ATTIC_USER_FILE;
                table_name = LanguageSQLiteOpenHelper.TABLE_NAME_ATTIC;
                break;
            default: break;
        }

        // Clear table of old data
        writableDatabase.execSQL("DELETE FROM " + table_name);

        boolean match;
        String inputLineLang, inputLineUser;
        String[] inputArrayLang = new String[5];
        String[] inputArrayUser = new String[5];
        int id_lang, id_user, freq, flevel, flapse, blevel, blapse;
        String word, full, trans;
        long rowID;

        InputStream is = getResources().openRawResource(res_id_data);
        BufferedReader brLang = new BufferedReader(new InputStreamReader(is));

        ContentValues row = new ContentValues();

        match = false;
        id_lang = id_user = freq = flevel = flapse = blevel = blapse = 0;
        word = full = trans = null;

        try {

            FileInputStream fis = openFileInput(file_user);
            BufferedReader brUser = new BufferedReader(new InputStreamReader(fis));

            while ((inputLineUser = brUser.readLine()) != null) {

                inputArrayUser = inputLineUser.split("\\|");

                id_user = Integer.parseInt(inputArrayUser[0]);
                flevel = Integer.parseInt(inputArrayUser[1]);
                flapse = Integer.parseInt(inputArrayUser[2]);
                blevel = Integer.parseInt(inputArrayUser[3]);
                blapse = Integer.parseInt(inputArrayUser[4]);

                match = false;

                while (!match) {

                    inputLineLang = brLang.readLine();
                    inputArrayLang = inputLineLang.split("\\|");

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

                    brUser.close();

                }

            }

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

        try {

            // Copy in any trailing unattempted words into the database
            // Limited by daily new word preference
            while ((inputLineLang = brLang.readLine()) != null && new_words < daily_limit) {

                new_words = new_words++;

                inputArrayLang = inputLineLang.split("\\|");

                id_lang = Integer.parseInt(inputArrayLang[0]);
                freq = Integer.parseInt(inputArrayLang[1]);
                word = inputArrayLang[2];
                full = inputArrayLang[3];
                trans = inputArrayLang[4];

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
        } catch (IOException e){

        }

        // Get total word count
        Cursor c = writableDatabase.rawQuery("SELECT COUNT(*) FROM " + table_name, null);
        c.moveToFirst();
        word_total = c.getInt(0);
        c.close();

        // Add to shared_preferences of language
        switch (language) {
            case GERMAN_LANGUAGE:
                SharedPreferences.Editor german_editor = german_preferences.edit();
                german_editor.putInt(WORDS_TOTAL, word_total);
                german_editor.apply();
                break;
            case LATIN_LANGUAGE:
                SharedPreferences.Editor latin_editor = latin_preferences.edit();
                latin_editor.putInt(WORDS_TOTAL, word_total);
                latin_editor.apply();
                break;
            case ATTIC_LANGUAGE:
                SharedPreferences.Editor attic_editor = attic_preferences.edit();
                attic_editor.putInt(WORDS_TOTAL, word_total);
                attic_editor.apply();
                break;
            default: break;
        }
    }

    public void beginRound() {

        yes_count = no_count = 0;

        String lapseWordQuery = null;

        if (forward_mode) {
            lapseWordQuery = "SELECT * FROM " + table_name +
                    " WHERE " + LanguageSQLiteOpenHelper.COLUMN_FLEVEL + " > 0" +
                    " AND " + LanguageSQLiteOpenHelper.COLUMN_FLAPSE + " < 1" +
                    " ORDER BY " + LanguageSQLiteOpenHelper.COLUMN_FLAPSE + " ASC" +
                    " LIMIT " + bucket_size;
        } else {
            lapseWordQuery = "SELECT * FROM " + table_name +
                    " WHERE " + LanguageSQLiteOpenHelper.COLUMN_BLEVEL + " > 0" +
                    " AND " + LanguageSQLiteOpenHelper.COLUMN_BLAPSE + " < 1" +
                    " ORDER BY " + LanguageSQLiteOpenHelper.COLUMN_BLAPSE + " ASC" +
                    " LIMIT " + bucket_size;
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

        String newWordQuery = "";

        int additional = bucket_size - round_id.size();
        if (additional > 0) {

            if (forward_mode) {
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

        if (round_shuffled.size() > 0) {
            askWord();
        } else {

            // Popup message explaining that no more words need to be revised
            CharSequence text;
            if (additional > 0) {
                text = getString(R.string.fully_revised_toast);
            } else {
                text = getString(R.string.fully_learned_toast);
            }
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
            toast.show();

        }


    }

    public void askWord () {

        int i = round_shuffled.get(0);
        round_shuffled.remove(0);

        word_view.setText(round_word.get(i));
        full_view.setText(round_full.get(i));
        trans_view.setText(round_trans.get(i));

        current_id = round_id.get(i);
        current_flevel = round_flevel.get(i);
        current_blevel = round_blevel.get(i);

        check_view.setVisibility(View.VISIBLE);
        respond_view.setVisibility(View.GONE);
        blank_view.setVisibility(View.VISIBLE);
        trans_view.setVisibility(View.GONE);
        next_view.setVisibility(View.GONE);

        findViewById(R.id.language_page).setVisibility(View.GONE);
        findViewById(R.id.practice_page).setVisibility(View.VISIBLE);

    }

    public void checkWord (boolean score) {

        check_view.setVisibility(View.GONE);
        respond_view.setVisibility(View.VISIBLE);
        blank_view.setVisibility(View.GONE);
        trans_view.setVisibility(View.VISIBLE);
        next_view.setVisibility(View.GONE);

        //
        if (!score) {
            respond_view.setVisibility(View.GONE);
            next_view.setVisibility(View.VISIBLE);
        }

    }

    public void nextWord () {

        if (round_shuffled.size() > 0) {
            askWord();
        } else {
            endPracticeRound();
        }

    }

    public void onUserRespond (boolean known) {

        int new_flevel, new_flapse, new_blevel, new_blapse;
        new_flevel = new_flapse = new_blevel = new_blapse = 0;
        String updateQuery = null;

        if (forward_mode) {
            if (known) {
                yes_count++;
                new_flevel = (current_flevel < 2) ? 2 : current_flevel++;
                new_flapse = getLapse(new_flevel);
                updateQuery = "UPDATE " + table_name + " SET " + LanguageSQLiteOpenHelper.COLUMN_FLEVEL + " = " + new_flevel +
                        ", " + LanguageSQLiteOpenHelper.COLUMN_FLAPSE + " = " + new_flapse +
                        " WHERE " + LanguageSQLiteOpenHelper.COLUMN_ID + " = " + current_id;
            } else {
                no_count++;
                new_flevel = (current_flevel < 2) ? 1 : current_flevel--;
                new_flapse = getLapse(new_flevel);
                updateQuery = "UPDATE " + table_name + " SET " + LanguageSQLiteOpenHelper.COLUMN_FLEVEL + " = " + new_flevel +
                        ", " + LanguageSQLiteOpenHelper.COLUMN_FLAPSE + " = " + new_flapse +
                        " WHERE " + LanguageSQLiteOpenHelper.COLUMN_ID + " = " + current_id;
            }
        } else {
            if (known) {
                yes_count++;
                new_blevel = (current_blevel < 2) ? 2 : current_blevel++;
                new_blapse = getLapse(new_blevel);
                updateQuery = "UPDATE " + table_name + " SET " + LanguageSQLiteOpenHelper.COLUMN_BLEVEL + " = " + new_blevel +
                        ", " + LanguageSQLiteOpenHelper.COLUMN_BLAPSE + " = " + new_blapse +
                        " WHERE " + LanguageSQLiteOpenHelper.COLUMN_ID + " = " + current_id;
            } else {
                no_count++;
                new_blevel = (current_blevel < 2) ? 1 : current_blevel--;
                new_blapse = getLapse(new_blevel);
                updateQuery = "UPDATE " + table_name + " SET " + LanguageSQLiteOpenHelper.COLUMN_BLEVEL + " = " + new_blevel +
                        ", " + LanguageSQLiteOpenHelper.COLUMN_BLAPSE + " = " + new_blapse +
                        " WHERE " + LanguageSQLiteOpenHelper.COLUMN_ID + " = " + current_id;
            }
        }

        writableDatabase.execSQL(updateQuery);

        if (round_shuffled.size() > 0) {
            askWord();
        } else {
            endRound();
        }

    }

    public void endRound() {

        setMainScreen();

        // Show practice.xml layout
        findViewById(R.id.practice_page).setVisibility(View.GONE);
        findViewById(R.id.language_page).setVisibility(View.VISIBLE);

    }

    // DELETE> DO NOT USE.
    public void endPracticeRound() {

        main_message.setText(R.string.practice_complete);
        main_failed.setText(R.string.practice_message);

        setMainScreen();

        // Show practice.xml layout
        findViewById(R.id.practice_page).setVisibility(View.GONE);
        findViewById(R.id.language_page).setVisibility(View.VISIBLE);

    }

    public int getLapse (int level) {

        switch (lapse_mode) {
            case PRIME_MODE: return prime[level];
            case ODD_MODE: return odd[level];
            default: return fibonacci[level];
        }

    }

    public void languageBackupText (String language) {

        String file = null;

        switch (language) {
            case GERMAN_LANGUAGE: file = GERMAN_USER_FILE; break;
            case LATIN_LANGUAGE: file = LATIN_USER_FILE; break;
            case ATTIC_LANGUAGE: file = ATTIC_USER_FILE; break;
        }

        String createViewQuery = "CREATE VIEW ordered AS SELECT " +
                LanguageSQLiteOpenHelper.COLUMN_ID + ", " +
                LanguageSQLiteOpenHelper.COLUMN_FLEVEL + ", " +
                LanguageSQLiteOpenHelper.COLUMN_FLAPSE + ", " +
                LanguageSQLiteOpenHelper.COLUMN_BLEVEL + ", " +
                LanguageSQLiteOpenHelper.COLUMN_BLAPSE +
                " FROM " + table_name + " ORDER BY " + LanguageSQLiteOpenHelper.COLUMN_ID;
        writableDatabase.execSQL(createViewQuery);

        String viewRowsQuery = "SELECT * FROM ordered";
        Cursor viewCursor = writableDatabase.rawQuery(viewRowsQuery, null);

        try {

            FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);

            ArrayList<Integer> row = new ArrayList<>();

            while (viewCursor.moveToNext()) {

                row.add(viewCursor.getInt(0));
                row.add(viewCursor.getInt(1));
                row.add(viewCursor.getInt(2));
                row.add(viewCursor.getInt(3));
                row.add(viewCursor.getInt(4));

                CharSequence delimiter = "|";
                String joined = TextUtils.join(delimiter, row) + "\n";
                fos.write(joined.getBytes());
                row.clear();

            }

            viewCursor.close();
            fos.close();

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

        String dropViewQuery = "DROP VIEW ordered";
        writableDatabase.execSQL(dropViewQuery);

    }

    // DELETE THIS. DO NOT USE. DEFEATS THE PHILOSOPHY OF THE APP.
    // Only needed if the entire database has been learned.
    public void practiceRound() {

        String practiceQuery;

        if (forward_mode) {
            practiceQuery = "SELECT * FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_FLEVEL + " > 0 LIMIT + " + bucket_size;
        } else {
            practiceQuery = "SELECT * FROM " + table_name + " WHERE " + LanguageSQLiteOpenHelper.COLUMN_BLEVEL + " > 0 LIMIT + " + bucket_size;
        }

        Cursor newCursor = writableDatabase.rawQuery(practiceQuery, null);

        round_id = new ArrayList<Integer>();
        round_freq = new ArrayList<Integer>();
        round_word = new ArrayList<String>();
        round_full = new ArrayList<String>();
        round_trans = new ArrayList<String>();

        newCursor.moveToFirst();
        while (!newCursor.isAfterLast()) {
            round_id.add(newCursor.getInt(0));
            round_freq.add(newCursor.getInt(1));
            round_word.add(newCursor.getString(2));
            round_full.add(newCursor.getString(3));
            round_trans.add(newCursor.getString(4));
            newCursor.moveToNext();
        }
        newCursor.close();

        round_shuffled = new ArrayList<Integer>();
        for (int i = 0; i < round_id.size(); i++) {
            round_shuffled.add(i);
        }
        Collections.shuffle(round_shuffled);

        if (round_shuffled.size() > 0) {
            askWord();
        } else {

        }
    }

}

