package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class LoginReg extends AppCompatActivity {
    private SQLiteDatabase db; // Holds the database instance
    private DatabaseHelper dbhelper; // Holds the DatabaseHelper instance
    private EditText editTextUname, editTextPwd, editTextUname1, editTextPwd1,editTextEmail1;

    int cid;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

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
        // register click listener
        findViewById(R.id.reg).setOnClickListener(v -> reg());
    }

    public void login()
    {
        // Get the user input
        String unameInput = editTextUname.getText().toString();
        String pwdInput = editTextPwd.getText().toString();

        // Query the database for the username
        String query = "select p.cid, p.fname, p.lname, a.password from authenticate a " +
                "inner join person p on a.cid = p.cid " +
                "where a.username = ?";

        Cursor cursor = null;

        cursor = db.rawQuery(query, new String[]{unameInput});

        try
        {
            if (cursor.moveToFirst())
            {

                // Get the column indices
                int cidColumn = cursor.getColumnIndex("cid");
                int fnameColumn = cursor.getColumnIndex("fname");
                int lnameColumn = cursor.getColumnIndex("lname");
                int passwordColumn = cursor.getColumnIndex("password");

                // Get the values from the cursor
                cid = cursor.getInt(cidColumn);
                String fname = cursor.getString(fnameColumn);
                String lname = cursor.getString(lnameColumn);
                String storedPassword = cursor.getString(passwordColumn);

                // Check if the password matches
                if (pwdInput.equals(storedPassword))
                {
                    Toast.makeText(this, "Welcome, " + fname + " " + lname + "!", Toast.LENGTH_SHORT).show();
                    //go to home page here
                    //showUserScore(String.valueOf(cid)); // Pass the logged-in user ID
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



    public void showUserScore(String cid) //avg score
    {
        String query = "select AVG(score) AS avg_score from drive where cid = ?"; // Fetch the latest score
        Cursor cursor = db.rawQuery(query, new String[]{cid});

        if (cursor != null && cursor.moveToFirst()) {
            int scoreColumn =cursor.getColumnIndex("avg_score");
            int score = cursor.getInt(scoreColumn); // Get the score column value
            TextView textScore = findViewById(R.id.text_score); // Get the TextView
            textScore.setText(String.valueOf(score)); // Set the score as text
            cursor.close();
        } else {
            // Handle if no scores are found
            TextView textScore = findViewById(R.id.text_score);
            textScore.setText("No Score");
        }

        db.close(); // Close the database connection
    }

    @SuppressLint("MissingInflatedId")
    public void reg() {
        setContentView(R.layout.registration);

        findViewById(R.id.btnRegister).setOnClickListener(v -> register());

    }

    public void register() {
        // Initialize UI components
        editTextUname1 = findViewById(R.id.editTextUname1);
        editTextPwd1 = findViewById(R.id.editTextPwd1);

        String unameInput = editTextUname1.getText().toString();
        String pwdInput = editTextPwd1.getText().toString();

        if (unameInput.isEmpty() || pwdInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        //String token = generateToken();
        //sendConfirmationEmail(emailInput, token);

        db.beginTransaction();
        try {
            ContentValues personValues = new ContentValues();
            personValues.put("fname", "FirstName");
            personValues.put("lname", "LastName");
            personValues.put("dob", "1990-01-01");
            long lastCid = db.insert("person", null, personValues);

            if (lastCid == -1) throw new Exception("Failed to insert into person table");

            ContentValues authValues = new ContentValues();
            authValues.put("username", unameInput);
            authValues.put("password", pwdInput);
            authValues.put("cid", lastCid);
            long authResult = db.insert("authenticate", null, authValues);

            if (authResult == -1) throw new Exception("Failed to insert into authenticate table");


            db.setTransactionSuccessful();
            Toast.makeText(this, "Registration successful! Check your email.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Register", "Registration error: ", e);
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }
    }

    public void toHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
