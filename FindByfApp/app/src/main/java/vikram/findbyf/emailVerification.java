package vikram.findbyf;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class emailVerification extends AppCompatActivity implements View.OnClickListener{
    Button refresh;
    Button resent_mail;
    TextView verifiedMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        TextView text=(TextView)findViewById(R.id.field_email_verification);
        refresh=(Button)findViewById(R.id.button_refresh);
        refresh.setOnClickListener(this);
        verifiedMail=(TextView)findViewById(R.id.email_verified_link);
        resent_mail=(Button)findViewById(R.id.button_resend_verification_mail);
        resent_mail.setOnClickListener(this);
        verifiedMail.setOnClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (isNetworkAvailable(getApplicationContext())){

            boolean emailVerified = user.isEmailVerified();
            if (emailVerified) {

                Intent myIntent = new Intent(emailVerification.this, MainActivity.class);
                startActivity(myIntent);
            }
            else {
                text.setText("Please Verify Your Email-Id.\nA mail Sent to \"" + user.getEmail() + "\" for verification !");
                resent_mail.setVisibility(View.VISIBLE);
                verifiedMail.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            text.setText("Need Network Connection");

        }

    }
    private boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onStart() {
        super.onStart();

    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_refresh) {
            Intent myIntent = new Intent(emailVerification.this, SignInActivity.class);
            finish();
            startActivity(myIntent);
        } else if (i == R.id.button_resend_verification_mail)
        {
            if (isNetworkAvailable(getApplicationContext())) {
                resendEmailVerification();
            }
            else
            {
                Intent myIntent = new Intent(emailVerification.this, SignInActivity.class);
                finish();
                startActivity(myIntent);
            }
        } else if (i == R.id.email_verified_link)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }

    }
    public void resendEmailVerification()
    {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(emailVerification.this, "An Verification-Email sent!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(emailVerification.this, "Cant Send Verification-Email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
