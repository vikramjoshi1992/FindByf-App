package vikram.findbyf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class myNotification extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static String userId;
    private ProgressDialog mProgressDialog;
    Context context;
    TextView result;
    TextView notification_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        context=getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        result=(TextView)findViewById(R.id.textview_notification_result);
        userId = getUid();
        Button back=(Button)findViewById(R.id.button_Back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(myNotification.this, MainActivity.class));
                finish();
            }
        });
        notification_status=(TextView)findViewById(R.id.notification_empty);
        mDatabase = FirebaseDatabase.getInstance().getReference();

                if (isNetworkAvailable(getApplicationContext())) {
                    Log.e("Notify","connected");
                    mDatabase.child("ContactUs").child(userId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get user information
                                    if (dataSnapshot.getValue() != null) {
                                    notification_status.setVisibility(View.GONE);
                                    addSearchResult((Map<String,Object>)dataSnapshot.getValue());

                                    }
                                }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    result.setText(databaseError.toString());

                                    }
                                });
                }
                else
                    {
                    Toast.makeText(myNotification.this, "Need Network Connection", Toast.LENGTH_SHORT).show();
                }
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
            mProgressDialog = new ProgressDialog(context);
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
    public void addSearchResult(Map<String,Object> message_detail_reply)
    {

        LinearLayout main_layout = (LinearLayout) findViewById(R.id.list_content1);

        for (Map.Entry<String,Object>entry : message_detail_reply.entrySet())
        {
            //String subject_name=entry.getKey();
            Map message_detail=(Map) entry.getValue();
            String subject_name=entry.getKey();
            String message=(String) message_detail.get("message");
            String reply=(String) message_detail.get("reply");
            String date=(String)message_detail.get("date");

            LinearLayout parent = new LinearLayout(context);
            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            parent.setOrientation(LinearLayout.VERTICAL);
            parent.setBackgroundResource(R.drawable.layout_textview_bg);
            main_layout.addView(parent);

            TextView MobileNo = new TextView(context);
            MobileNo.setText("Date: "+date);
            MobileNo.setTextColor(Color.parseColor("#0b84aa"));
            MobileNo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            parent.addView(MobileNo);


            LinearLayout layout = new LinearLayout(context);

            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);

            parent.addView(layout);

            TextView detail = new TextView(this);
            //result1=result1+"///"+message_detail.message+"///"+message_detail.reply;
            //result.setText(result1);
            if (reply==null || reply== "")
            {
                detail.setText("Subject: "+subject_name+"\nYour Message: " + message);
            }
            else
            {
                detail.setText("Subject: "+subject_name+"\nYour Message: " + message + "\n\nReply from FindByf's Team: \n" + reply);
            }

            detail.setTextColor(Color.parseColor("#3B444B"));
            detail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);


            layout.addView(detail);
            LinearLayout parent1 = new LinearLayout(context);
            parent1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            parent1.setOrientation(LinearLayout.VERTICAL);
            parent1.setBackgroundResource(R.drawable.layout_dummy_bg);
            TextView demo1 = new TextView(context);
            demo1.setText("");
            parent1.addView(demo1);
            main_layout.addView(parent1);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            startActivity(new Intent(myNotification.this, MainActivity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}