package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db; // Holds the database instance
    private DatabaseHelper dbhelper; // Holds the DatabaseHelper instance
    private EditText editTextUname, editTextPwd, editTextUname1, editTextPwd1,editTextEmail1;

    int cid;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate (Bundle savedInstanceState)
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
        // register click listener
        findViewById(R.id.reg).setOnClickListener(v -> reg());



        //onDestroy();
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
                    //...
                    //showUserScore(unameInput);
                    setContentView(R.layout.activity_main1);
                    findViewById(R.id.btn_menu).setOnClickListener(v -> openCarInfo());
                    findViewById(R.id.btn_card).setOnClickListener(v -> openPolicy());
                    findViewById(R.id.btn_drive).setOnClickListener(v -> openMap());
                    findViewById(R.id.btn_home).setOnClickListener(v -> openAverage());

                    showUserScore(String.valueOf(cid)); // Pass the logged-in user ID


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



    //go to personal homepage
    private void showUserScore(String cid) //avg score
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

    int code;
    private void sendConfirmationEmail(String email, String token) {
        new Thread(() -> {
            try {
//                String confirmationLink = "http://yourserver.com/confirm?token=" + token;
//                String subject = "Email Confirmation";
//                String messageText = "Please confirm your email by clicking the link: " + confirmationLink;
//
//                Properties props = new Properties();
//                props.put("mail.smtp.host", "smtp.gmail.com");
//                props.put("mail.smtp.port", "587");
//                props.put("mail.smtp.auth", "true");
//                props.put("mail.smtp.starttls.enable", "true");
//
//                javax.mail.Session session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
//                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
//                        return new javax.mail.PasswordAuthentication("your_email@gmail.com", "your_password");
//                    }
//                });
//
//                MimeMessage message = new MimeMessage(session);
//                message.setFrom(new InternetAddress("your_email@gmail.com"));
//                message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
//                message.setSubject(subject);
//                message.setText(messageText);
//
//                Transport.send(message);
//                Log.i("Email", "Email sent successfully to " + email);

                Random random=new Random();
                code=random.nextInt(8999)+1000;
                String url="https://rahilacademy.com/otp/sendEmail.php";
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                } ){
                    @Nullable
                    @Override

                    protected Map<String, String>getParams()throws AuthFailureError{
                        Map<String, String>params=new HashMap<>();
                        params.put("email",editTextEmail1.getText().toString());
                        params.put("code",String.valueOf(code));
                        return params;
                    }
                };

                requestQueue.add(stringRequest);


            } catch (Exception e) {
                Log.e("EmailError", "Error sending email", e);
            }
        }).start();
    }


    private String generateToken() {
        return java.util.UUID.randomUUID().toString();
    }

    public void register() {
        // Initialize UI components
        editTextUname1 = findViewById(R.id.editTextUname1);
        editTextPwd1 = findViewById(R.id.editTextPwd1);
        editTextEmail1 = findViewById(R.id.editTextEmail1);

        String unameInput = editTextUname1.getText().toString();
        String pwdInput = editTextPwd1.getText().toString();
        String emailInput = editTextEmail1.getText().toString();

        if (unameInput.isEmpty() || pwdInput.isEmpty() || emailInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = generateToken();
        sendConfirmationEmail(emailInput, token);

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



    public void openAverage() {

    }

    public void openPolicy() {
    }

    public void openMap() {
    }

    //show bluetooth thingy
    public void openCarInfo() {
        setContentView(R.layout.item_vehicle);
        showCarInfo(String.valueOf(cid));


    }
    public void showCarInfo(String cid) {
        // Fetch car details for the given cid
        List<Map<String, String>> carDetails = getCarDetails(cid);

        // Check if data exists
        if (!carDetails.isEmpty()) {
            // Assume only one car per cid for simplicity (use loop for multiple cars)
            Map<String, String> car = carDetails.get(0);

            // Get UI elements from the layout
            TextView tvMake = findViewById(R.id.tv_make);
            TextView tvModel = findViewById(R.id.tv_model);
            TextView tvYear = findViewById(R.id.tv_year);
            TextView tvVin = findViewById(R.id.tv_vin);
            TextView tvPolicyNum = findViewById(R.id.tv_policynum);
            TextView tvAddress = findViewById(R.id.tv_btaddress);

            // Populate the UI elements with car details
            tvMake.setText("Make: " + car.get("make"));
            tvModel.setText("Model: " + car.get("model"));
            tvYear.setText("Year: " + car.get("year"));
            tvVin.setText("VIN: " + car.get("vin"));
            tvPolicyNum.setText("Policy: " + car.get("policynum"));
            tvAddress.setText("Address: " + getAddress(car.get("policynum")));
        } else {
            // Show a message if no car details are found
            Toast.makeText(this, "No car details found for the customer ID: " + cid, Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("Range")
    public List<Map<String, String>> getCarDetails(String cid) {
        List<Map<String, String>> carList = new ArrayList<>();
        String query = "SELECT c.vin, c.model, c.make, c.year, c.policynum " +
                "FROM car c " +
                "INNER JOIN custaccount ca ON c.policynum = ca.policynum " +
                "WHERE ca.cid = ?";
        Cursor cursor = db.rawQuery(query, new String[]{cid});

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> car = new HashMap<>();
                car.put("vin", cursor.getString(cursor.getColumnIndex("vin")));
                car.put("model", cursor.getString(cursor.getColumnIndex("model")));
                car.put("make", cursor.getString(cursor.getColumnIndex("make")));
                car.put("year", cursor.getString(cursor.getColumnIndex("year")));
                car.put("policynum", cursor.getString(cursor.getColumnIndex("policynum")));
                carList.add(car);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return carList;
    }

    @SuppressLint("Range")
    private String getAddress(String policyNum) {
        String address = "";
        String query = "SELECT streetnum || ' ' || streetname || ', ' || city || ', ' || province || ' ' || postalcode AS address " +
                "FROM policy " +
                "WHERE policynum = ?";
        Cursor cursor = db.rawQuery(query, new String[]{policyNum});

        if (cursor.moveToFirst()) {
            address = cursor.getString(cursor.getColumnIndex("address"));
        }
        cursor.close();
        return address;
    }

    public void openProfile() {


    }
}
