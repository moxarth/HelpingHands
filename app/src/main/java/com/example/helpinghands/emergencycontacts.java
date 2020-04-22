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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.helpinghands.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class emergencycontacts extends AppCompatActivity {

    private Spinner choice1;
    private EditText name1;
    private EditText contact1;
    private Spinner choice2;
    private EditText name2;
    private EditText contact2;
    private Spinner choice3;
    private EditText name3;
    private EditText contact3;
    private Button button;

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
        setContentView(R.layout.activity_emergencycontacts);

        choice1 = (Spinner) findViewById(R.id.spinner);
        name1 = (EditText) findViewById(R.id.editText6);
        contact1 = (EditText) findViewById(R.id.editText4);
        choice2 = (Spinner) findViewById(R.id.spinner2);
        name2 = (EditText) findViewById(R.id.editText60);
        contact2 = (EditText) findViewById(R.id.editText40);
        choice3 = (Spinner) findViewById(R.id.spinner3);
        name3 = (EditText) findViewById(R.id.editText06);
        contact3 = (EditText) findViewById(R.id.editText04);
        button = (Button) findViewById(R.id.button2);
        final String options[] = new String[]{"Parent", "Child", "Sibling", "Relative", "Neighbour", "Friend", "Other"};
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choice1.setAdapter(adapter);
        choice2.setAdapter(adapter);
        choice3.setAdapter(adapter);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final User user = new User(emergencycontacts.this);

        name1.setText(user.getEcon1name());
        if(user.getEcon1() == 0 || user.getEcon3() == 1){contact1.setText("");}
        else{
            contact1.setText(user.getEcon1().toString());
        }
        choice1.setSelection(Arrays.asList(options).indexOf(user.getRel1()));

        name2.setText(user.getEcon2name());
        if(user.getEcon2() == 0 || user.getEcon3() == 1){contact2.setText("");}
        else {
            contact2.setText(user.getEcon2().toString());
        }
        choice2.setSelection(Arrays.asList(options).indexOf(user.getRel2()));

        name3.setText(user.getEcon3name());
        if(user.getEcon3() == 0 || user.getEcon3() == 1){contact3.setText("");}
        else {
            contact3.setText(user.getEcon3().toString());
        }
        choice3.setSelection(Arrays.asList(options).indexOf(user.getRel3()));

        button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int flag=1;
            if(name1.getText().toString().length() != 0 && !contact1.getText().toString().matches("[0-9]{10}")){
                contact1.requestFocus();
                contact1.setError("Contact number is required");
                flag=0;
            }

            if(name2.getText().toString().length() != 0 && !contact2.getText().toString().matches("[0-9]{10}")){
                contact2.requestFocus();
                contact2.setError("Contact number is required");
                flag=0;
            }

            if(name3.getText().toString().length() != 0 && !contact3.getText().toString().matches("[0-9]{10}")){
                contact3.requestFocus();
                contact3.setError("Contact number is required");
                flag=0;
            }

            if(name1.getText().toString().length() == 0 && contact1.getText().toString().length() != 0 ){
                name1.requestFocus();
                name1.setError("Name is required");
                flag=0;
            }
            if(name2.getText().toString().length() == 0 && contact2.getText().toString().length() != 0 ){
                name2.requestFocus();
                name2.setError("Name is required");
                flag=0;
            }
            if(name3.getText().toString().length() == 0 && contact3.getText().toString().length() != 0 ){
                name3.requestFocus();
                name3.setError("Name is required");
                flag=0;
            }
            if(flag==1) {

                boolean status = false;
                try{
                    final String command = "ping -c 1 google.com";
                    status = (Runtime.getRuntime().exec(command).waitFor() == 0);
                    Log.v("int",status+"");
                }
                catch (Exception e){Log.e("status",e.toString());}

                if(!status){
                    AlertDialog.Builder builder = new AlertDialog.Builder(emergencycontacts.this);
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

                else {
                    //Toast.makeText(getApplicationContext(),choice1.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    Map<String, Object> econtacts = new HashMap<>();
                    Map<String, Object> econtacts2 = new HashMap<>();
                    Map<String, Object> econtacts3 = new HashMap<>();
                    econtacts.put("name", name1.getText().toString());
                    econtacts.put("contactno", contact1.getText().toString());
                    econtacts.put("relation", choice1.getSelectedItem().toString());
                    db.collection("emergency_details").document(user.getUserid().toString()).collection("contacts").document("econtact1").update(econtacts);
                    econtacts2.put("name", name2.getText().toString());
                    econtacts2.put("contactno", contact2.getText().toString());
                    econtacts2.put("relation", choice2.getSelectedItem().toString());
                    db.collection("emergency_details").document(user.getUserid().toString()).collection("contacts").document("econtact2").update(econtacts2);
                    econtacts3.put("name", name3.getText().toString());
                    econtacts3.put("contactno", contact3.getText().toString());
                    econtacts3.put("relation", choice3.getSelectedItem().toString());
                    db.collection("emergency_details").document(user.getUserid().toString()).collection("contacts").document("econtact3").update(econtacts3);

                    if (contact1.getText().toString().length() == 0) {
                        user.setEcon1(Long.parseLong("0"));
                    } else {
                        user.setEcon1(Long.parseLong(contact1.getText().toString()));
                    }
                    user.setEcon1name(name1.getText().toString());
                    user.setRel1(choice1.getSelectedItem().toString());
                    if (contact2.getText().toString().length() == 0) {
                        user.setEcon2(Long.parseLong("0"));
                    } else {
                        user.setEcon2(Long.parseLong(contact2.getText().toString()));
                    }
                    user.setEcon2name(name2.getText().toString());
                    user.setRel2(choice2.getSelectedItem().toString());
                    Log.v("status", "");
                    if (contact3.getText().toString().length() == 0) {
                        user.setEcon3(Long.parseLong("0"));
                    } else {
                        user.setEcon3(Long.parseLong(contact3.getText().toString()));
                    }
                    user.setEcon3name(name3.getText().toString());
                    user.setRel3(choice3.getSelectedItem().toString());
                    Toast.makeText(emergencycontacts.this, "EMERGENCY CONTACTS UPDATED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });
    Log.v("values",user.getEcon1name()+" "+user.getEcon1()+" "+user.getRel1());
    }
}
