package vikram.findbyf;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MyProfile extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static String userId;
    private EditText nameET;
    private EditText emailET;
    private EditText phoneNoET;
    private Button editProfileBtn;
    private Button profileSaveBtn;
    private ProgressDialog mProgressDialog;
    public MyProfile() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);



        nameET = (EditText) view.findViewById(R.id.field_profile_name);
        emailET = (EditText) view.findViewById(R.id.field_profile_email);
        phoneNoET = (EditText) view.findViewById(R.id.field_profile_phoneNumber);

        editProfileBtn = (Button) view.findViewById(R.id.profile_editbtn);
        profileSaveBtn = (Button) view.findViewById(R.id.profile_saveBtn);

        mAuth = FirebaseAuth.getInstance();
        userId = getUid();
        if (isNetworkAvailable(getContext())) {
            showProgressDialog();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user information
                            User profile = dataSnapshot.getValue(User.class);
                            nameET.setText(profile.fullname);
                            nameET.setFocusable(false);

                            emailET.setText(profile.email);
                            emailET.setFocusable(false);

                            phoneNoET.setText(profile.myContactNo);
                            phoneNoET.setFocusable(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            hideProgressDialog();
        }
        else
        {
            Toast.makeText(getContext(), "Need Network Connection", Toast.LENGTH_SHORT).show();
        }
                editProfileBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        if (isNetworkAvailable(getContext())) {
                            nameET.setFocusableInTouchMode(true);
                            editProfileBtn.setVisibility(View.GONE);
                            profileSaveBtn.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getContext(), "Need Network Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                profileSaveBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        if (isNetworkAvailable(getContext())) {
                            if (nameET.getText().toString().isEmpty() || nameET.getText().toString().length() < 3) {
                                nameET.setError("at least 3 characters");
                            } else {

                                showProgressDialog();
                                User user = new User(nameET.getText().toString(), emailET.getText().toString(), phoneNoET.getText().toString());

                                mDatabase.child("users").child(userId).setValue(user);
                                nameET.setFocusable(false);
                                editProfileBtn.setVisibility(View.VISIBLE);
                                profileSaveBtn.setVisibility(View.GONE);
                                hideProgressDialog();
                            }
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Need Network Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            return view;

    }
    public String getUid() {

        if(mAuth.getCurrentUser() == null)
        {
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    private boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
