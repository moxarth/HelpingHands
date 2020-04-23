package com.example.helpinghands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class editprofile extends AppCompatActivity {


    private EditText username;
    private EditText username2;
    private EditText email;
    private EditText age;
    private EditText addressl1;
    private EditText city;
    private EditText state;
    private EditText country;
    private Button button;
    private CheckBox type;
    private RadioButton gender;
    private RadioButton gender2;
    private EditText contactno;
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        final User user = new User(this);
        contactno = (EditText)findViewById(R.id.editText3);
        button = (Button)findViewById(R.id.button);
        username = (EditText)findViewById(R.id.editText);
        username2 = (EditText)findViewById(R.id.lastname);
        button = (Button)findViewById(R.id.button);
        email = (EditText)findViewById(R.id.email);
        age = (EditText)findViewById(R.id.age);
        addressl1 = (EditText)findViewById(R.id.addressl1);
        city = (EditText)findViewById(R.id.city);
        country = (EditText)findViewById(R.id.country);
        state = (EditText)findViewById(R.id.state);
        gender = (RadioButton) findViewById(R.id.Male);
        gender2 = (RadioButton) findViewById(R.id.Female);
        type=(CheckBox)findViewById(R.id.checkBox);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        contactno.setText(Long.toString(user.getContactnumber()));
        username.setText(user.getFName());
        username2.setText(user.getLName());
        email.setText(user.getEmail());
        age.setText(user.getAge());
        addressl1.setText(user.getAddress());
        city.setText(user.getCity());
        state.setText(user.getState());
        country.setText(user.getCountry());
        if(user.getGender().equals("Female")){gender2.setChecked(true);}else{gender.setChecked(true);}
        if(user.getType() == 1){type.setChecked(true);}else{}

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 1;
                if(!username.getText().toString().matches("[a-z A-Z]+")){
                    username.requestFocus();
                    username.setError("Please provide appropriate name");
                    flag=0;
                }
                if(!username2.getText().toString().matches("[a-z A-Z]+")){
                    username2.requestFocus();
                    username2.setError("Please provide appropriate name");
                    flag=0;
                }
                if(addressl1.getText().toString().length() == 0){
                    addressl1.requestFocus();
                    addressl1.setError("Field is required");
                    flag=0;
                }

                if(city.getText().toString().length() == 0){
                    city.requestFocus();
                    city.setError("Field is required");
                    flag=0;
                }
                if(state.getText().toString().length() == 0){
                    state.requestFocus();
                    state.setError("Field is required");
                    flag=0;
                }
                if(country.getText().toString().length() == 0){
                    country.requestFocus();
                    country.setError("Field is required");
                    flag=0;
                }

                if(!email.getText().toString().matches("^[a-zA-Z0-9._]+@[a-zA-Z]+?\\.[a-zA-Z]{2,5}$")){
                    email.requestFocus();
                    email.setError("Please provide appropriate email-id");
                    flag=0;
                }

                if(!age.getText().toString().matches("[1-9][0-9]")){
                    age.requestFocus();
                    age.setError("Please provide appropriate age");
                    flag=0;
                }
                if(flag == 1) {

                    boolean status = false;
                    try {
                        final String command = "ping -c 1 google.com";
                        status = (Runtime.getRuntime().exec(command).waitFor() == 0);
                        Log.v("int", status + "");
                    } catch (Exception e) {
                        Log.e("status", e.toString());
                    }

                    if (!status) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(editprofile.this);
                        builder.setCancelable(true);
                        builder.setTitle("No Internet Connection");
                        builder.setMessage("Internet Connection is required to perform the following task.");
                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        });
                        builder.show();
                    }
                    else{
                    String usertype, usergender;
                    if (gender2.isChecked()) {
                        usergender = "Female";
                    } else {
                        usergender = "Male";
                    }
                    if (type.isChecked()) {
                        usertype = "1";
                    } else {
                        usertype = "0";
                    }
                    db.collection("user_details").document(user.getUserid()).update("email", email.getText().toString(),
                            "age", age.getText().toString(),
                            "address", addressl1.getText().toString(),
                            "city", city.getText().toString(),
                            "country", country.getText().toString(),
                            "firstname", username.getText().toString(),
                            "lastname", username2.getText().toString(),
                            "gender", usergender,
                            "state", state.getText().toString(),
                            "type", usertype
                    );
                    user.setFName(username.getText().toString());
                    user.setLName(username2.getText().toString());
                    user.setAge(age.getText().toString());
                    user.setAddress(addressl1.getText().toString());
                    user.setCity(city.getText().toString());
                    user.setCountry(country.getText().toString());
                    user.setState(state.getText().toString());
                    user.setGender(usergender);
                    user.setEmail(email.getText().toString());
                    user.setType(Integer.parseInt(usertype));
                    Toast.makeText(editprofile.this, "PROFILE UPDATED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



    }
}
