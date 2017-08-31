package vikram.findbyf;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
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

public class DetectIncomingCall extends Service {

    private int laststate=TelephonyManager.CALL_STATE_IDLE;
    private int currentstate=TelephonyManager.CALL_STATE_IDLE;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userId;
    public DetectIncomingCall() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        final HashMap<String, String> contact_detail = new HashMap<String, String>();
        String caller_name,caller_number;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userId = getUid();
        Cursor phones = getBaseContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        phones.moveToFirst();
        do {
            caller_name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            caller_number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            caller_number=caller_number.replaceAll("\\s+","");
            contact_detail.put(caller_number,caller_name);
        }while(phones.moveToNext());
        phones.close();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, final String incomingNumber)
            {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    laststate=state;
                    DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm");
                    final String date = df.format(Calendar.getInstance().getTime());
                    if (isNetworkAvailable(getBaseContext()))
                    {
                        FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.e("detectincmoingcall","hey");
                                        // Get user information
                                        User profile = dataSnapshot.getValue(User.class);
                                        String myMobileno = profile.myContactNo;
                                        BusyDetail busyDetail;
                                        String caller_name=contact_detail.get(incomingNumber);
                                        if (caller_name == null) {
                                            busyDetail = new BusyDetail(incomingNumber, "***", date);
                                        } else {
                                            busyDetail = new BusyDetail(incomingNumber, caller_name, date);
                                        }
                                        //MyCallLogDetail detail=new MyCallLogDetail(callName,phNumber);
                                        Log.e("5","hey");
                                        mDatabase.child("MobileNo").child(myMobileno).setValue(busyDetail);
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }
                    Log.e("detectIncomingcall","hello");
                    writeToFile(incomingNumber+";>&>;"+date);
                    //phones.close();
                }
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    laststate=state;

                }

                if (state == TelephonyManager.CALL_STATE_IDLE) {
                    if(laststate==TelephonyManager.CALL_STATE_RINGING)
                    {
                        laststate=TelephonyManager.CALL_STATE_IDLE;
                        Cursor phones = getBaseContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                        phones.moveToFirst();
                        String caller_name;
                        String caller_number;
                        do {
                            caller_name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            caller_number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contact_detail.put(caller_number,caller_name);
                        }while(phones.moveToNext());
                        phones.close();

                    }

                }

            }
        };
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
