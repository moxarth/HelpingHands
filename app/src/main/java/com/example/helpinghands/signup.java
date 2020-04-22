package com.example.helpinghands;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

public class signup extends AppCompatActivity {

    private EditText username;
    private EditText username2;
    private EditText password;
    private EditText phone;
    private EditText email;
    private EditText age;
    private EditText addressl1;
    private EditText addressl2;
    private EditText city;
    private EditText state;
    private EditText country;
    private Button button;
    private CheckBox type;
    private RadioButton gender;
    private RadioButton gender2;
    ProgressDialog progressBar;
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

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.v("Main4","Inside");
        username = (EditText)findViewById(R.id.editText);
        username2 = (EditText)findViewById(R.id.lastname);
        password = (EditText)findViewById(R.id.editText2);
        button = (Button)findViewById(R.id.button);
        email = (EditText)findViewById(R.id.email);
        age = (EditText)findViewById(R.id.age);
        addressl1 = (EditText)findViewById(R.id.addressl1);
        addressl2 = (EditText)findViewById(R.id.addressl2);
        city = (EditText)findViewById(R.id.city);
        country = (EditText)findViewById(R.id.country);
        state = (EditText)findViewById(R.id.state);
        gender = (RadioButton) findViewById(R.id.Male);
        gender2 = (RadioButton) findViewById(R.id.Female);
        phone = (EditText)findViewById(R.id.editText3);
        type=(CheckBox)findViewById(R.id.checkBox);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                if(!password.getText().toString().matches("[a-zA-Z0-9]{5}[a-zA-Z0-9]+")){
                    password.requestFocus();
                    password.setError("Password must be 6 characters long");
                    flag=0;
                }
                if(!phone.getText().toString().matches("[0-9]{10}")){
                    phone.requestFocus();
                    phone.setError("Please provide 10 digit Mobile number");
                    flag=0;
                }
                if(addressl1.getText().toString().length() == 0){
                    addressl1.requestFocus();
                    addressl1.setError("Field is required");
                    flag=0;
                }

                if(addressl2.getText().toString().length() == 0){
                    addressl2.requestFocus();
                    addressl2.setError("Field is required");
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

                if(!email.getText().toString().matches("^\\w+@[a-zA-Z0-9_]+?\\.[a-zA-Z]{2,3}$")){
                    email.requestFocus();
                    email.setError("Please provide appropriate email-id");
                    flag=0;
                }

                if(!age.getText().toString().matches("[1-9][0-9]")){
                    age.requestFocus();
                    age.setError("Please provide appropriate age");
                    flag=0;
                }
                if(flag == 1){

                    boolean status = false;
                    try{
                        final String command = "ping -c 1 google.com";
                        status = (Runtime.getRuntime().exec(command).waitFor() == 0);
                        Log.v("int",status+"");
                    }
                    catch (Exception e){Log.e("status",e.toString());}

                    if(!status){
                        AlertDialog.Builder builder = new AlertDialog.Builder(signup.this);
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
                progressBar = new ProgressDialog(v.getContext());
                progressBar.setCancelable(false);
                progressBar.setMessage("Creating a new account...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.setProgress(0);
                progressBar.setMax(100);
                progressBar.show();

                        db.collection("user_details").whereEqualTo("Contactno", Long.parseLong(phone.getText().toString()))
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if(!task.getResult().isEmpty()){
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(signup.this);
                                        builder.setCancelable(true);
                                        builder.setTitle("Account Already Exist");
                                        builder.setMessage("An account is already associated with given contact number.");
                                        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(signup.this,login.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                            }
                                        });
                                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                progressBar.dismiss();
                                            }
                                        });
                                        builder.show();
                                    }
                                    else{
                                        String usergender;
                                        if(gender2.isChecked()){usergender = "Female" ;}
                                        else{usergender = "Male";}
                                        final login_details my_details = new login_details();
                                        my_details.setPassword(password.getText().toString());
                                        my_details.setFirstname(username.getText().toString());
                                        my_details.setLastname(username2.getText().toString());
                                        my_details.setContactno(Long.parseLong(phone.getText().toString()));
                                        my_details.setEmail(email.getText().toString());
                                        my_details.setAddress(addressl1.getText().toString()+" "+addressl2.getText().toString());
                                        my_details.setAge(age.getText().toString());
                                        my_details.setGender(usergender);
                                        my_details.setCity(city.getText().toString());
                                        my_details.setCountry(country.getText().toString());
                                        my_details.setState(state.getText().toString());
                                        String acc_type="not_initialized";
                                        if(type.isChecked()){acc_type="1";}
                                        else{acc_type="0";}
                                        my_details.setType(acc_type);

                                        Map<String, Object> user = new HashMap<>();
                                        user.put("firstname", my_details.getFirstname());
                                        user.put("lastname", my_details.getLastname());
                                        user.put("Contactno",my_details.getContactno());
                                        user.put("email", my_details.getEmail());
                                        user.put("password", my_details.getPassword());
                                        user.put("gender", usergender);
                                        user.put("address", my_details.getAddress());
                                        user.put("age",my_details.getAge());
                                        user.put("city",my_details.getCity());
                                        user.put("state", my_details.getState());
                                        user.put("country", my_details.getCountry());
                                        user.put("latitude",0);
                                        user.put("longitude",0);
                                        user.put("type", my_details.getType());
                                        user.put("lcity","");
                                        db.collection("user_details")
                                                .add(user)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.d("msg1", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                        Map<String, Object> econtacts = new HashMap<>();
                                                        econtacts.put("name","");
                                                        econtacts.put("relation","Parent");
                                                        econtacts.put("contactno","");
                                                        db.collection("emergency_details").document(documentReference.getId()).collection("contacts").document("econtact1").set(econtacts);
                                                        db.collection("emergency_details").document(documentReference.getId()).collection("contacts").document("econtact2").set(econtacts);
                                                        db.collection("emergency_details").document(documentReference.getId()).collection("contacts").document("econtact3").set(econtacts);
                                                        Toast.makeText(getApplicationContext(),"ACCOUNT CREATED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                                                        User user = new User(signup.this);
                                                        user.setFName(my_details.getFirstname());
                                                        user.setLName(my_details.getLastname());
                                                        user.setContactnumber(my_details.getContactno());
                                                        user.setUserid(documentReference.getId());
                                                        user.setAge(my_details.getAge());
                                                        user.setAddress(my_details.getAddress());
                                                        user.setCity(my_details.getCity());
                                                        user.setCountry(my_details.getCountry());
                                                        user.setState(my_details.getState());
                                                        user.setGender(my_details.getGender());
                                                        user.setEmail(my_details.getEmail());
                                                        user.setType(Integer.parseInt(my_details.getType()));
                                                        user.setPassword(my_details.getPassword());
                                                        user.setEcon1(Long.parseLong("0"));
                                                        user.setEcon1name("");
                                                        user.setRel1("Parent");
                                                        user.setEcon2(Long.parseLong("0"));
                                                        user.setEcon2name("");
                                                        user.setRel2("Parent");
                                                        user.setEcon3(Long.parseLong("0"));
                                                        user.setEcon3name("");
                                                        user.setRel3("Parent");
                                                        Log.v("username",user.getFName()+user.getLName());
                                                        progressBar.dismiss();
                                                        Intent in = new Intent(signup.this,Splash.class);
                                                        startActivity(in);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressBar.dismiss();
                                                        Log.w("msg2", "Error adding document", e);
                                                        Toast.makeText(getApplicationContext(),"FAIL TO CREATE ACCOUNT, TRY AGAIN AFTER SOME TIME",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        //here


                                    }
                                }
                                else{Log.v("Error","Error connecting database");}
                            }
                        });

            }}
        }
        });
    }
}
