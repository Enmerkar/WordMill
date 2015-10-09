package com.findelworks.wordmill;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Justin on 7/10/2015.
 */

public class LanguageSQLiteOpenHelper extends SQLiteOpenHelper {

    // Database variables
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Language.db";

    // Language table name variables
    public static final String TABLE_NAME_GERMAN = "german";
    public static final String TABLE_NAME_LATIN = "latin";

    // Language information variables
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FREQ = "freq";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_FULL = "full";
    public static final String COLUMN_TRANS = "trans";

    // User variables
    public static final String COLUMN_FLEVEL = "flevel";
    public static final String COLUMN_FLAPSE = "flapse";
    public static final String COLUMN_BLEVEL = "blevel";
    public static final String COLUMN_BLAPSE = "blapse";

    private static final String CREATE_TABLE_VARIABLES = " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_FREQ + " INTEGER NOT NULL, " +
                    COLUMN_WORD + " TEXT NOT NULL, " +
                    COLUMN_FULL + " TEXT NOT NULL, " +
                    COLUMN_TRANS + " TEXT NOT NULL, " +
                    COLUMN_FLEVEL + " INTEGER, " +
                    COLUMN_FLAPSE + " INTEGER, " +
                    COLUMN_BLEVEL + " INTEGER, " +
                    COLUMN_BLAPSE + " INTEGER);";

    public LanguageSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_GERMAN + CREATE_TABLE_VARIABLES);
        db.execSQL("CREATE TABLE " + TABLE_NAME_LATIN + CREATE_TABLE_VARIABLES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GERMAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LATIN);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
