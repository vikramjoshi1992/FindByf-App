package vikram.findbyf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SendContactListToServer extends BroadcastReceiver {

    private static String caller_name = null;
    private static String caller_number = null;
    private static String userId;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "5 seconds have passed", Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        userId =getUid();

        if(userId!=null) {


            mDatabase = FirebaseDatabase.getInstance().getReference();

            final Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            phones.moveToFirst();
            //Log.e("exception", "caught you");
            final HashMap<String, Object> dataMap = new HashMap<String, Object>();
            final Context context_copy=context;
            if (isNetworkAvailable(context)) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // Get user information
                                            User profile = dataSnapshot.getValue(User.class);
                                            String myMobileno = profile.myContactNo;
                                            String countryCode = myMobileno.substring(0, myMobileno.length() - 10);

                                            //MyCallLogDetail detail=new MyCallLogDetail(callName,phNumber);
                                                do {
                                                    caller_name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                                    caller_number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                                    caller_number = caller_number.replaceAll("\\s+", "");
                                                    caller_number=caller_number.replaceAll("-","");
                                                    if (caller_name == null) {
                                                        caller_name = "NameNotFound";
                                                    }

                                                    if(caller_number.length()==10)
                                                    {
                                                        caller_number=countryCode+caller_number;
                                                    }
                                                    else if (caller_number.length()==11)
                                                    {
                                                        char demo=caller_number.charAt(0);
                                                        if (demo=='0')
                                                        {
                                                            caller_number=countryCode+caller_number.substring(1,caller_number.length());
                                                        }
                                                    }

                                                    dataMap.put(caller_number, caller_name);

                                                } while (phones.moveToNext());
                                                phones.close();
                                            if (isNetworkAvailable(context_copy)) {
                                                Log.e("contact list", "Sent");
                                                mDatabase.child("ContactList").updateChildren(dataMap);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

            }
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

