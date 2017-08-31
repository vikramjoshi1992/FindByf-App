package vikram.findbyf;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.io.File;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText mNamefield;
    private EditText mMobileNoField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mSignInButton;
    private Button mSignUpButton;
    CountryCodePicker ccp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ccp1=(CountryCodePicker) findViewById(R.id.register_country_code);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mEmailField = (EditText) findViewById(R.id.field_email1);
        mPasswordField = (EditText) findViewById(R.id.field_password1);
        mNamefield=(EditText)findViewById(R.id.field_fullName);
        mMobileNoField=(EditText)findViewById(R.id.field_ContactNo1);


        mSignInButton = (TextView) findViewById(R.id.link_to_login);
        mSignUpButton = (Button) findViewById(R.id.button_sign_up1);

        // Click listeners
        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser(),0);
        }
    }

    public void signUp()
    {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        final String mobileNo = ccp1.getFullNumberWithPlus()+mMobileNoField.getText().toString();
        showProgressDialog();
        mAuth = FirebaseAuth.getInstance();
        if(isNetworkAvailable(getApplication())) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                final FirebaseUser user = task.getResult().getUser();
                                FirebaseDatabase.getInstance().getReference().child("MobileNo").child(mobileNo)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                // Get user information
                                                if (dataSnapshot.getValue() != null) {
                                                    hideProgressDialog();
                                                    Toast.makeText(SignUpActivity.this, "Mobile No. already Exist", Toast.LENGTH_SHORT).show();
                                                    user.delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        mMobileNoField.setError("Change Mobile No.");
                                                                        Log.d(TAG, "User account deleted.");
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    hideProgressDialog();
                                                    onAuthSuccess(user, 1);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                                hideProgressDialog();
                                                user.delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                }
                                                            }
                                                        });
                                                Toast.makeText(SignUpActivity.this, databaseError.toException().toString(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                hideProgressDialog();
                                if(task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                    mEmailField.setError("Email is already registered");
                                }
                                else {
                                    Toast.makeText(SignUpActivity.this, "Sign Up failed!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Need Network Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onAuthSuccess(FirebaseUser user,int type) {
        showProgressDialog();
        final File path =

                Environment.getExternalStoragePublicDirectory
                        (

                                Environment.getDataDirectory().getPath()+"/CallBusyName"
                        );

        if (!path.exists()) {
            if(!path.mkdirs())
            {
                Log.e("Exception", "Cant create directory");
            }
        }

        final File file = new File(path, "mydemo1.txt");
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
        }
        catch(Exception e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }


        // Write new user
        if(type==1) {
            writeNewUser(user.getUid(), user.getEmail());
        }
        // Go to MainActivity
        startService(new Intent(this, DetectIncomingCall.class));
        startService(new Intent(this, ActiveDataSendingService.class));
        hideProgressDialog();
        finish();
        startActivity(new Intent(SignUpActivity.this, emailVerification.class));
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }


    private boolean validateForm() {
        boolean valid = true,valid1=true,valid2=true,valid3=true;

        String name = mNamefield.getText().toString();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String contact_no=mMobileNoField.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            mNamefield.setError("at least 3 characters");
            valid = false;
        } else {
            mNamefield.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailField.setError("enter a valid email address");
            valid1 = false;
        } else {
            mEmailField.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 14) {
            mPasswordField.setError("between 4 and 10 alphanumeric characters");
            valid2 = false;
        } else {
            mPasswordField.setError(null);
        }

        if (contact_no.isEmpty() || contact_no.length() !=10) {
            mMobileNoField.setError("Mobile No is not Valid");
            valid3 = false;
        } else {
            mMobileNoField.setError(null);
        }
        return (valid && valid1 && valid2 && valid3);
    }


    // [START basic_write]
    private void writeNewUser(String userId, String email) {
        if (isNetworkAvailable(getApplication())) {
            String name = mNamefield.getText().toString();
            String contact_no =  ccp1.getFullNumberWithPlus()+ mMobileNoField.getText().toString();

            User user = new User(name, email, contact_no);
            BusyDetail busydetail = new BusyDetail("***", "***", "***");

            mDatabase.child("MobileNo").child(contact_no).setValue(busydetail);
            mDatabase.child("users").child(userId).setValue(user);
            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

            user1.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "An Email Sent for verification !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
        else
        {
            Toast.makeText(getBaseContext(), "Need Netwok Connection!", Toast.LENGTH_SHORT).show();
        }
    }
    // [END basic_write]
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.link_to_login) {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        } else if (i == R.id.button_sign_up1) {
            if(isNetworkAvailable(getApplication())) {
                signUp();
            }
            else
            {
                Toast.makeText(this, "Need Network Connection !", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
