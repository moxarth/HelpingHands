package com.example.helpinghands;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class login extends AppCompatActivity {
    private Button button;
    private Button button1;
    private EditText username;
    private EditText password;
    ProgressDialog progressBar;
    @Override
    public void onBackPressed(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText)findViewById(R.id.editText);
        password = (EditText)findViewById(R.id.editText2);
        button = (Button)findViewById(R.id.button);
        button1 = (Button)findViewById(R.id.button3);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int flag=1;
                if(!username.getText().toString().matches("[0-9]{10}")){
                    username.requestFocus();
                    username.setError("Please provide 10 digit Mobile number");
                    flag=0;
                }
                if(username.getText().toString().length() == 0){
                    username.requestFocus();
                    username.setError("Field can not be empty");
                    flag=0;
                }
                if(password.getText().toString().length() == 0){
                    password.requestFocus();
                    password.setError("Field can not be empty");
                    flag=0;
                }
                if(flag==1) {

                    boolean status = false;
                    try {
                        final String command = "ping -c 1 google.com";
                        status = (Runtime.getRuntime().exec(command).waitFor() == 0);
                        Log.v("int", status + "");
                    } catch (Exception e) {
                        Log.e("status", e.toString());
                    }

                    if (!status) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                        builder.setCancelable(true);
                        builder.setTitle("No Internet Connection");
                        builder.setMessage("Internet Connection is required to perform the following task.");
                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                    }
                    else{
                    progressBar = new ProgressDialog(v.getContext());
                    progressBar.setCancelable(true);
                    progressBar.setMessage("Verifying Login Credentials...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    Query q = db.collection("user_details").whereEqualTo("Contactno", Long.parseLong(username.getText().toString()));
                    q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getDocuments().toString() == "[]") {
                                    Toast.makeText(getApplicationContext(), "Account does not exist", Toast.LENGTH_SHORT).show();
                                    Log.v("status", "not exist");
                                    progressBar.dismiss();
                                } else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.v("status", document.getId() + " => " + document.getData());

                                        if (password.getText().toString().equals(document.get("password"))) {
                                            Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                                            final User user = new User(login.this);
                                            user.setFName(document.get("firstname").toString());
                                            user.setLName(document.get("lastname").toString());
                                            user.setContactnumber(Long.parseLong(document.get("Contactno").toString()));
                                            user.setUserid(document.getId());
                                            user.setAge(document.get("age").toString());
                                            user.setAddress(document.get("address").toString());
                                            user.setCity(document.get("city").toString());
                                            user.setCountry(document.get("country").toString());
                                            user.setState(document.get("state").toString());
                                            user.setGender(document.get("gender").toString());
                                            user.setEmail(document.get("email").toString());
                                            user.setType(Integer.parseInt(document.get("type").toString()));
                                            user.setPassword(password.getText().toString());
                                            Log.v("userid", "" + user.getUserid());
                                            Task<DocumentSnapshot> sp = db.collection("emergency_details").document(user.getUserid())
                                                    .collection("contacts").document("econtact1").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            DocumentSnapshot spd = task.getResult();
                                                            if (spd.get("contactno").toString().length() == 0) {
                                                                user.setEcon1(Long.parseLong("0"));
                                                            } else {
                                                                user.setEcon1(Long.parseLong(spd.get("contactno").toString()));
                                                            }
                                                            user.setEcon1name(spd.get("name").toString());
                                                            user.setRel1(spd.get("relation").toString());
                                                        }
                                                    });
                                            Task<DocumentSnapshot> sp1 = db.collection("emergency_details").document(user.getUserid())
                                                    .collection("contacts").document("econtact2").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            DocumentSnapshot spd = task.getResult();
                                                            if (spd.get("contactno").toString().length() == 0) {
                                                                user.setEcon2(Long.parseLong("0"));
                                                            } else {
                                                                user.setEcon2(Long.parseLong(spd.get("contactno").toString()));
                                                            }
                                                            user.setEcon2name(spd.get("name").toString());
                                                            user.setRel2(spd.get("relation").toString());
                                                        }
                                                    });
                                            Task<DocumentSnapshot> sp2 = db.collection("emergency_details").document(user.getUserid())
                                                    .collection("contacts").document("econtact3").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            DocumentSnapshot spd = task.getResult();
                                                            if (spd.get("contactno").toString().length() == 0) {
                                                                user.setEcon3(Long.parseLong("0"));
                                                            } else {
                                                                user.setEcon3(Long.parseLong(spd.get("contactno").toString()));
                                                            }
                                                            user.setEcon3name(spd.get("name").toString());
                                                            user.setRel3(spd.get("relation").toString());
                                                        }
                                                    });
                                            progressBar.dismiss();
                                            Intent in = new Intent(login.this, MainActivity.class);
                                            startActivity(in);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        } else {
                                            progressBar.dismiss();
                                            Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
                                            Log.v("log1", "Invalid Login Credentials" + document.get("password"));
                                        }
                                    }
                                }
                            } else {
                                Log.v("msg2", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                }

                }

            }
        });


    }

}
