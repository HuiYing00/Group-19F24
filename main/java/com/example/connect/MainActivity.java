package com.example.connect;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db; // Holds the database instance
    private DatabaseHelper dbhelper; // Holds the DatabaseHelper instance
    private EditText editTextUname, editTextPwd;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        editTextUname = findViewById(R.id.editTextUname);
        editTextPwd = findViewById(R.id.editTextPwd);

        // Initialize the DatabaseHelper and open the database
        dbhelper = new DatabaseHelper(getApplicationContext());
        try
        {
            dbhelper.createDatabase();
        } catch (IOException e)
        {
            Toast.makeText(this, "Database initialization failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        dbhelper.openDatabase(); // Open the database
        db = dbhelper.getWritableDatabase();
        // Set login button click listener
        findViewById(R.id.btnLogin).setOnClickListener(v -> login());

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Close the database when the activity is destroyed
        if (db != null && db.isOpen())
        {
            db.close();
        }
        if (dbhelper != null)
        {
            dbhelper.closeDataBase();
        }
    }

    public void login()
    {
        // Get the user input
        String unameInput = editTextUname.getText().toString();
        String pwdInput = editTextPwd.getText().toString();

        // Query the database for the username
        String query = "select p.fname, p.lname, a.password from authenticate a " +
                "inner join person p on a.cid = p.cid " +
                "where a.username = ?";

        Cursor cursor = null;

        cursor = db.rawQuery(query, new String[]{unameInput});

        try
        {
            if (cursor.moveToFirst())
            {

                // Get the column indices
                int fnameColumn = cursor.getColumnIndex("fname");
                int lnameColumn = cursor.getColumnIndex("lname");
                int passwordColumn = cursor.getColumnIndex("password");

                // Get the values from the cursor
                String fname = cursor.getString(fnameColumn);
                String lname = cursor.getString(lnameColumn);
                String storedPassword = cursor.getString(passwordColumn);

                // Check if the password matches
                if (pwdInput.equals(storedPassword))
                {
                    Toast.makeText(this, "Welcome, " + fname + " " + lname + "!", Toast.LENGTH_SHORT).show();
                    //go to personal page here
                    //...
                }
                else
                {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e)
        {
            Toast.makeText(this, "Error during login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }
}
