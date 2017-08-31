package vikram.findbyf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.ContactsContract;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class SendDataToServer extends BroadcastReceiver {
    private static String caller_name = null;
    private static String caller_number = null;
    private static String userId;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    public void onReceive(Context context, Intent intent) {

        mAuth = FirebaseAuth.getInstance();
        userId = getUid();
        int set=0;
        if (userId != null) {

            mDatabase = FirebaseDatabase.getInstance().getReference();

            final Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            phones.moveToFirst();
            //Log.e("exception", "caught you");
            if (isNetworkAvailable(context)) {
                String data = readFromFile();
                if (data != null) {
                    final String[] caller_detail = data.split(";>&>;");
                    do {
                        caller_name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        caller_number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        caller_number=caller_number.replaceAll("\\s+","");
                        if (caller_number.equals(caller_detail[0])) {
                            set=1;
                            FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // Get user information
                                            User profile = dataSnapshot.getValue(User.class);
                                            String myMobileno = profile.myContactNo;
                                            BusyDetail busyDetail;

                                            if (caller_name == null) {
                                                busyDetail = new BusyDetail(caller_number, "***", caller_detail[1]);
                                            } else {
                                                busyDetail = new BusyDetail(caller_number, caller_name, caller_detail[1]);
                                            }
                                            //MyCallLogDetail detail=new MyCallLogDetail(callName,phNumber);
                                            mDatabase.child("MobileNo").child(myMobileno).setValue(busyDetail);
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            break;
                        }

                    } while (phones.moveToNext());
                    phones.close();
                    if(set==0)
                    {
                        FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User profile = dataSnapshot.getValue(User.class);
                                        String myMobileno = profile.myContactNo;
                                        BusyDetail busyDetail;
                                        busyDetail = new BusyDetail(caller_detail[0], "Unknown Name", caller_detail[1]);
                                        mDatabase.child("MobileNo").child(myMobileno).setValue(busyDetail);
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }
        }
    }

    private String readFromFile() {

        String response = null;
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                Environment.getDataDirectory().getPath() + "/CallBusyName"
                        );
        final File file = new File(path, "mydemo1.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                response = line;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
        return response;

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
