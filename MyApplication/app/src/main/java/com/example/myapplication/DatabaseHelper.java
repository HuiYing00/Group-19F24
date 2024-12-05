package com.example.myapplication;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper{

    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private static final String DATABASE_NAME = "Database.db";
    //public final static String DATABASE_PATH = "assets/";
    public static final int DATABASE_VERSION = 1;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;

    }




    //Checks if the database exists using checkDataBase.
    //If it doesn't exist, creates an empty database and copies the pre-populated database from the assets folder.
    public void createDatabase() throws IOException
    {
        boolean dbExist1 = checkDataBase();
        if(!dbExist1)
        {
            this.getReadableDatabase();
            try
            {
                this.close();
                copyDataBase();
            }
            catch (IOException e)
            {
                throw new Error("Error copying database");
            }
        }

    }

    private boolean checkDataBase() {
        File dbFile = myContext.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }


    private void copyDataBase() throws IOException {
        // Open the database from the assets folder as an InputStream
        InputStream mInput = myContext.getAssets().open(DATABASE_NAME);

        // Path to the just-created empty database in the internal storage
        String outFileName = myContext.getDatabasePath(DATABASE_NAME).getPath();

        // Open the empty database as the OutputStream
        OutputStream mOutput = new FileOutputStream(outFileName);

        // Transfer bytes from the InputStream to the OutputStream
        byte[] mBuffer = new byte[1024]; // Adjust buffer size as needed
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }

        // Close the streams
        mOutput.flush();
        mOutput.close();
        mInput.close();

        Log.d("DatabaseHelper", "Database copied to: " + outFileName);
    }

    //delete database
    public void db_delete() {
        // Get the path to the internal database
        File file = myContext.getDatabasePath(DATABASE_NAME);

        // Delete the file if it exists
        if (file.exists()) {
            if (file.delete()) {
                Log.d("DatabaseHelper", "Database deleted at: " + file.getPath());
            } else {
                Log.e("DatabaseHelper", "Failed to delete database at: " + file.getPath());
            }
        } else {
            Log.d("DatabaseHelper", "Database does not exist at: " + file.getPath());
        }
    }

    //Open database
    public void openDatabase() throws SQLException {
        // Get the path to the internal database
        String myPath = myContext.getDatabasePath(DATABASE_NAME).getPath();

        // Check if the database file exists
        File dbFile = new File(myPath);
        if (dbFile.exists()) {
            Log.d("DatabaseHelper", "Database exists at: " + myPath);
        } else {
            Log.d("DatabaseHelper", "Database does not exist at: " + myPath);
        }

        // Open the database
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        Log.d("DatabaseHelper", "Database opened successfully.");
    }


    public synchronized void closeDataBase()throws SQLException
    {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
        {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }

    }
}


