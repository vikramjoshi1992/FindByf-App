package vikram.findbyf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetectOutgoingCall extends BroadcastReceiver {

    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference mDatabase;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent. getAction (). equals (Intent. ACTION_NEW_OUTGOING_CALL)) {

            Log.e("Exception Outgoing","hello");
            final HashMap<String, String> contact_detail = new HashMap<String, String>();
            String caller_name,caller_number;
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            userId = getUid();
            DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm");
            final String date = df.format(Calendar.getInstance().getTime());
            final String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            phones.moveToFirst();
            do {
                caller_name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                caller_number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                caller_number=caller_number.replaceAll("\\s+","");
                contact_detail.put(caller_number,caller_name);
            }while(phones.moveToNext());
            phones.close();
            if (isNetworkAvailable(context))
            {
                FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.e("detectOutgoingcall","hey");
                                // Get user information
                                User profile = dataSnapshot.getValue(User.class);
                                String myMobileno = profile.myContactNo;
                                BusyDetail busyDetail;
                                String caller_name=contact_detail.get(phoneNumber);
                                if (caller_name == null) {
                                    busyDetail = new BusyDetail(phoneNumber, "***", date);
                                } else {
                                    busyDetail = new BusyDetail(phoneNumber, caller_name, date);
                                }
                                //MyCallLogDetail detail=new MyCallLogDetail(callName,phNumber);
                                Log.e("detectOutgoingCall","hey");
                                mDatabase.child("MobileNo").child(myMobileno).setValue(busyDetail);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }
            writeToFile(phoneNumber+";>&>;"+date);
        }
    }
    public void writeToFile(String data) {
        final File path =

                Environment.getExternalStoragePublicDirectory
                        (
                                Environment.getDataDirectory().getPath()+"/CallBusyName"
                        );

        if (!path.exists()) {
            path.mkdirs();
        }

        final File file = new File(path, "mydemo1.txt");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());

        }
    }

    private boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public String getUid() {
        if(mAuth.getCurrentUser() == null)
        {
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
