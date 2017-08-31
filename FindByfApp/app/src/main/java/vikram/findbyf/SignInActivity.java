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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignInButton;
    private Button resetPassword;
    private TextView register_link;
    private TextView forgotPassword;
    private TextView register_link1;
    private TextView login_link;
    private LinearLayout login_layout;
    private LinearLayout reset_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login_layout=(LinearLayout)findViewById(R.id.layout_loginForm);
        reset_layout=(LinearLayout)findViewById(R.id.layout_reset);
        reset_layout.setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);


        mSignInButton = (Button) findViewById(R.id.button_sign_in);
        resetPassword = (Button) findViewById(R.id.button_reset) ;
        register_link = (TextView) findViewById(R.id.link_to_register);
        forgotPassword=(TextView)findViewById(R.id.forgot_password);
        register_link1=(TextView)findViewById(R.id.link_to_register1);
        login_link=(TextView)findViewById(R.id.link_to_login1);
        // Click listeners
        mSignInButton.setOnClickListener(this);
        register_link.setOnClickListener(this);
        register_link1.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        login_link.setOnClickListener(this);

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
                Intent myIntent = new Intent(SignInActivity.this, emailVerification.class);
                startActivity(myIntent);

        }

    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }
        if (isNetworkAvailable(getBaseContext())) {
            showProgressDialog();
            String email = mEmailField.getText().toString();
            String password = mPasswordField.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                            hideProgressDialog();
                            if (task.isSuccessful()) {
                                onAuthSuccess(task.getResult().getUser());
                            } else {
                                Toast.makeText(SignInActivity.this, "AuthenticationFailed:"+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(getBaseContext(), "Need Network Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());
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

        startService(new Intent(this, DetectIncomingCall.class));
        startService(new Intent(this, ActiveDataSendingService.class));
        hideProgressDialog();
        finish();
        startActivity(new Intent(SignInActivity.this, emailVerification.class));
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true,result1=true;
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailField.setError("enter a valid email address");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordField.setError("between 4 and 10 alphanumeric characters");
            result1 = false;
        } else {
            mPasswordField.setError(null);
        }

        return (result&&result1);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_in) {
            if(isNetworkAvailable(getApplicationContext())) {
                signIn();
            }
            else
            {
                Toast.makeText(this, "Need Network connection !", Toast.LENGTH_SHORT).show();
            }
        } else if (i == R.id.link_to_register || i==R.id.link_to_register1) {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        }
        else if (i==R.id.forgot_password)
        {
            login_layout.setVisibility(View.GONE);
            reset_layout.setVisibility(View.VISIBLE);

        }else if(i==R.id.link_to_login1)
        {
            reset_layout.setVisibility(View.GONE);
            login_layout.setVisibility(View.VISIBLE);
        }

        else if (i==R.id.button_reset)
        {
            if(isNetworkAvailable(getApplicationContext())) {
                resetPassword();
            }
            else
            {
                Toast.makeText(this, "Need Network connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void resetPassword()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final EditText mEmail=(EditText)findViewById(R.id.reset_field_email);
        final String email = mEmail.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("enter a valid email address");

        } else {
            if (isNetworkAvailable(getApplicationContext())) {
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignInActivity.this, "Email Sent to : \"" + email+"\"", Toast.LENGTH_SHORT).show();
                                    mEmail.setText("");

                                } else {
                                    Toast.makeText(SignInActivity.this, "Error :Can't Send !", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
            else
            {
                Toast.makeText(SignInActivity.this, "Need Network Connection", Toast.LENGTH_SHORT).show();
            }
        }
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
