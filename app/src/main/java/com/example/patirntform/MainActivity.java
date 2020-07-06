package com.example.patirntform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout Rollno, Name, Age, Phone, Email;
    private Button Insert, ViewAll, View;
    private RadioGroup radioGender, radioName;
    private RadioButton radioButton, genderButton;
    private CheckBox cbiot, cbrobo, cbai, cbml;

    private SQLiteDatabase db;
    int getSelected;
    int getGetSelected1;
    String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Rollno = findViewById(R.id.main_roll);
        Name = findViewById(R.id.main_name);
        Age = findViewById(R.id.main_age);
        Phone = findViewById(R.id.main_cell);
        Email = findViewById(R.id.main_email);
        Insert = findViewById(R.id.main_insert);
        ViewAll = findViewById(R.id.main_viewAll);
        View = findViewById(R.id.main_view);

        radioName = findViewById(R.id.radioName);
        radioGender = findViewById(R.id.radioGender);
        radioName.clearCheck();
        radioGender.clearCheck();

        cbiot = findViewById(R.id.cbiot);
        cbrobo = findViewById(R.id.cbrobo);
        cbai = findViewById(R.id.cbai);
        cbml = findViewById(R.id.cbml);

        db = openOrCreateDatabase("StudentDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS student(rollno VARCHAR, name VARCHAR,age VARCHAR, phone VARCHAR, email VARCHAR, fullname VARCHAR, gender VARCHAR, subject VARCHAR);");

        Insert.setOnClickListener(this);
        ViewAll.setOnClickListener(this);
        View.setOnClickListener(this);
    }

    public void onClick(View view) {

        if (cbiot.isChecked())
            text = text + " IoT ";
        if (cbrobo.isChecked())
            text = text + " Robotics ";
        if (cbai.isChecked())
            text = text + " AI ";
        if (cbml.isChecked())
            text = text + " ML ";

        // Inserting a record to the Student table
        if (view == Insert) {

            getSelected = radioName.getCheckedRadioButtonId();
            getGetSelected1 = radioGender.getCheckedRadioButtonId();

            radioButton = findViewById(getSelected);
            genderButton = findViewById(getGetSelected1);
            // Checking for empty fields
            if (Rollno.getEditText().getText().toString().trim().length() == 0 ||
                    Name.getEditText().getText().toString().trim().length() == 0 ||
                    Age.getEditText().getText().toString().trim().length() == 0 ||
                    Phone.getEditText().getText().toString().trim().length() == 0 ||
                    Email.getEditText().getText().toString().trim().length() == 0) {
                showMessage("Error", "Please enter all values");
                return;
            }
            db.execSQL("INSERT INTO student VALUES('" + Rollno.getEditText().getText() + "','" + Name.getEditText().getText() +
                    "','" + Age.getEditText().getText() + "','" + Phone.getEditText().getText() + "','" + Email.getEditText().getText() +
                    "','" + radioButton.getText() + "','" + genderButton.getText() + "','" + text + "');");
            showMessage("Success", "Record added");
            clearText();
        }

        // Display a record from the Student table
        if (view == View) {
            // Checking for empty roll number
            if (Rollno.getEditText().getText().toString().trim().length() == 0) {
                showMessage("Error", "Please enter Rollno");
                return;
            }
            Cursor c = db.rawQuery("SELECT * FROM student WHERE rollno='" + Rollno.getEditText().getText() + "'", null);
            if (c.moveToFirst()) {
                Name.getEditText().setText(c.getString(5) + c.getString(1));
                Age.getEditText().setText(c.getString(2));
                Phone.getEditText().setText(c.getString(3));
                Email.getEditText().setText(c.getString(4));
            } else {
                showMessage("Error", "Invalid Rollno");
                clearText();
            }
        }

        // Displaying all the records
        if (view == ViewAll) {
            Cursor c = db.rawQuery("SELECT * FROM student", null);
            if (c.getCount() == 0) {
                showMessage("Error", "No records found");
                return;
            }
            StringBuffer buffer = new StringBuffer();
            while (c.moveToNext()) {
                buffer.append("Rollno: " + c.getString(0) + "\n");
                buffer.append("Name: " + c.getString(5) + " " + c.getString(1) + "\n");
                buffer.append("Age: " + c.getString(2) + "\n");
                buffer.append("Phone: " + c.getString(3) + "\n");
                buffer.append("Email: " + c.getString(4) + "\n");
                buffer.append("Gender: " + c.getString(6) + "\n");
                buffer.append("Subjects: " + c.getString(7) + "\n\n\n");
            }
            showMessage("Student Details", buffer.toString());
        }
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void clearText() {
        Rollno.getEditText().setText("");
        Name.getEditText().setText("");
        Age.getEditText().setText("");
        Phone.getEditText().setText("");
        Email.getEditText().setText("");
        radioName.clearCheck();
        radioGender.clearCheck();
        cbiot.setChecked(false);
        cbrobo.setChecked(false);
        cbai.setChecked(false);
        cbml.setChecked(false);
        Phone.clearFocus();
    }

    public void export(View view) {
        //generate data
        StringBuilder data = new StringBuilder();
        Cursor c = db.rawQuery("SELECT * FROM student", null);

        data.append("Rollno,Name,Age,Phone,Email,Gender,Subjects");
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            data.append("\n" + c.getString(0) + "," + c.getString(5) + " " + c.getString(1) + "," + c.getString(2) + "," + c.getString(3) + ","
                    + c.getString(4) + "," + c.getString(6) + "," + c.getString(7));

            try {
                //saving the file into device
                FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
                out.write((data.toString()).getBytes());
                out.close();

                //exporting
                Context context = getApplicationContext();
                File filelocation = new File(getFilesDir(), "data.csv");
                Uri path = FileProvider.getUriForFile(context, "com.example.exportcsv.fileprovider", filelocation);
                Intent fileIntent = new Intent(Intent.ACTION_SEND);
                fileIntent.setType("text/csv");
                fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
                fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                startActivity(Intent.createChooser(fileIntent, "Send mail"));
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}