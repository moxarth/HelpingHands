package com.example.helpinghands.ui.profile;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.helpinghands.R;
import com.example.helpinghands.User;
import com.example.helpinghands.editprofile;
import com.example.helpinghands.emergencycontacts;
import com.example.helpinghands.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ProfileViewModel requestsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        requestsViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final LayoutInflater li = LayoutInflater.from(getContext()) ;
        final User user = new User(getActivity());
        final ListView list = root.findViewById(R.id.thelist);
        final String options[] = new String[]{"My Profile","Emergency Contacts","Change Credentials","Deactivate Account","Log out","Help & Feedback"};
        final String info[] = new String[]{"Edit profile details","Manage Emergency Contacts","Change/Forgot password","Account will be deleted permanently","Session will be destroyed","FAQs & Feedback option"};
        final Integer imageArray[] = new Integer[]{R.drawable.baseline_person_24,R.drawable.ic_contacts_24px,R.drawable.ic_security_24px,
                                           R.drawable.ic_cancel_24px,R.drawable.ic_exit_to_app_24px,R.drawable.ic_help_24px};

        CustomListAdapter adapter = new CustomListAdapter(getActivity(), options, info, imageArray);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("listview",options[position]);
                switch (position){
                    case 0:{
                        Intent intent = new Intent(getContext(), editprofile.class);
                        startActivity(intent);
                        ((Activity)getContext()).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        return;
                    }
                    case 1:{
                        Intent intent = new Intent(getContext(), emergencycontacts.class);
                        startActivity(intent);
                        ((Activity)getContext()).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        return;
                    }
                    case 2:{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(true);
                        builder.setTitle("Change Account Password");
                        final View promptsView = li.inflate(R.layout.dialog_resetpass, null);
                        builder.setView(promptsView);
                        final EditText oldpass = promptsView.findViewById(R.id.oldpass);
                        final EditText newpass = promptsView.findViewById(R.id.newpass);
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setPositiveButton("Change password", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean status = false;
                                try {
                                    final String command = "ping -c 1 google.com";
                                    status = (Runtime.getRuntime().exec(command).waitFor() == 0);
                                    Log.v("int", status + "");
                                } catch (Exception e) {
                                    Log.e("status", e.toString());
                                }
                                if (!status) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                    if(!newpass.getText().toString().matches("[a-zA-Z0-9@#$.]{5}[a-zA-Z0-9@#$.]+")){
                                        Toast.makeText(getActivity(),"New Password must be 6 characters long",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                    if(oldpass.getText().toString().equals(user.getPassword())){
                                        final ProgressDialog progressBar;
                                        progressBar = new ProgressDialog(promptsView.getContext());
                                        progressBar.setCancelable(false);
                                        progressBar.setMessage("Updating New password...");
                                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        progressBar.setProgress(0);
                                        progressBar.setMax(100);
                                        progressBar.show();
                                        db.collection("user_details").document(user.getUserid()).update("password",newpass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Log.v("status","Success");
                                                    user.setPassword(newpass.getText().toString());
                                                    Toast.makeText(getActivity(),"Password is changed successfully",Toast.LENGTH_SHORT).show();
                                                    progressBar.dismiss();
                                                }
                                                else{
                                                    Toast.makeText(getActivity(),"Error while performing action",Toast.LENGTH_SHORT).show();
                                                    progressBar.dismiss();
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(getActivity(),"Password does not match",Toast.LENGTH_SHORT).show();
                                    }}
                                }
                            }
                        });
                        builder.show();
                        return;
                    }
                    case 3:{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(true);
                        builder.setTitle("Deactivate Account");
                        builder.setMessage("This will delete your account along with all your data. This can't be undone");

                        final View promptsView = li.inflate(R.layout.dialog_format, null);
                        builder.setView(promptsView);
                        final EditText pass = promptsView.findViewById(R.id.pass);

                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setPositiveButton("Deactivate my Account", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                boolean status = false;
                                try {
                                    final String command = "ping -c 1 google.com";
                                    status = (Runtime.getRuntime().exec(command).waitFor() == 0);
                                    Log.v("int", status + "");
                                } catch (Exception e) {
                                    Log.e("status", e.toString());
                                }

                                if (!status) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                if(pass.getText().toString().equals(user.getPassword())){
                                    final ProgressDialog progressBar;
                                    progressBar = new ProgressDialog(promptsView.getContext());
                                    progressBar.setCancelable(false);
                                    progressBar.setMessage("Deleting Account Data...");
                                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progressBar.setProgress(0);
                                    progressBar.setMax(100);
                                    progressBar.show();
                                    db.collection("user_details").document(user.getUserid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Log.v("status","Success user_details");
                                                user.removeUser();
                                                Toast.makeText(getActivity(),"Account deactivated successfully",Toast.LENGTH_SHORT).show();
                                                progressBar.dismiss();
                                                Intent in = new Intent(getContext(), login.class);
                                                startActivity(in);
                                                //((Activity)getContext()).overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                                            }
                                            else{
                                                Toast.makeText(getActivity(),"Error while performing action",Toast.LENGTH_SHORT).show();
                                                progressBar.dismiss();
                                            }
                                        }
                                    });
                                    Log.v("status","id is "+user.getUserid());
                                    /*db.collection("emergency_details").document(user.getUserid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){Log.v("status","Success user_details");}
                                            else{Log.v("status","Failed user_details");}
                                        }
                                    });*/

                                }
                                else{
                                    Toast.makeText(getActivity(),"Password does not match",Toast.LENGTH_SHORT).show();
                                }}
                            }
                        });
                        builder.show();

                        return;
                    }
                    case 4:{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(true);
                        builder.setTitle("Logout");
                        builder.setMessage("Are you sure you want to log out?");
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                user.removeUser();
                                Intent in = new Intent(getContext(), login.class);
                                startActivity(in);
                                ((Activity)getContext()).overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

                            }
                        });
                        builder.show();
                        return;
                    }
                }
                //Toast.makeText(getActivity(),options[position],Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}

class CustomListAdapter extends ArrayAdapter {

    private final Activity context;
    private final Integer[] imageIDarray;
    private final String[] nameArray;
    private final String[] infoArray;

    public CustomListAdapter(Activity context,String[] nameArrayParam, String[] infoArrayParam, Integer[] imageIDArrayParam){

        super(context,R.layout.list_row , nameArrayParam);
        this.context=context;
        this.imageIDarray = imageIDArrayParam;
        this.nameArray = nameArrayParam;
        this.infoArray = infoArrayParam;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_row, null,true);

        TextView nameTextField = (TextView) rowView.findViewById(R.id.nameTextViewId);
        TextView infoTextField = (TextView) rowView.findViewById(R.id.InfoTextViewid);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1ID);

        nameTextField.setText(nameArray[position]);
        infoTextField.setText(infoArray[position]);
        imageView.setImageResource(imageIDarray[position]);

        return rowView;

    };

}
