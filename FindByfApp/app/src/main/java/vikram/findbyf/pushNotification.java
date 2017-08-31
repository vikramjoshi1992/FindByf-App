package vikram.findbyf;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

public class pushNotification extends BroadcastReceiver {
    private static String userId;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Context context_copy;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        mAuth = FirebaseAuth.getInstance();
        userId =getUid();

        if(userId!=null) {
            context_copy=context;

            mDatabase = FirebaseDatabase.getInstance().getReference();
            final HashMap<String, Object> dataMap = new HashMap<String, Object>();
            final Context context_copy=context;
            if (isNetworkAvailable(context)) {
                FirebaseDatabase.getInstance().getReference().child("Notification")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get user information
                                Map <String,Object>data=(Map<String,Object>)dataSnapshot.getValue();
                                for (Map.Entry<String,Object>entry : data.entrySet())
                                {
                                    Map message_detail=(Map) entry.getValue();
                                    String title=entry.getKey();
                                    String subject =(String) message_detail.get("subject");
                                    String body=(String) message_detail.get("body");
                                    String link=(String) message_detail.get("link");

                                    Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(link));
                                    PendingIntent pIntent = PendingIntent.getActivity(context_copy, 0, intent, 0);

                                    NotificationManager notif=(NotificationManager)context_copy.getSystemService(NOTIFICATION_SERVICE);
                                    Notification notify;
                                    if (Build.VERSION.SDK_INT < 16) {
                                        notify=new Notification.Builder
                                                (context_copy).setContentTitle(title).setContentText(body).
                                                setContentTitle(subject).setContentIntent(pIntent).setSmallIcon(R.drawable.notification_logo).getNotification();
                                    } else {
                                         notify=new Notification.Builder
                                                (context_copy).setContentTitle(title).setContentText(body).
                                                setContentTitle(subject).setContentIntent(pIntent).setSmallIcon(R.drawable.notification_logo).build();
                                    }
                                    notify.flags |= Notification.FLAG_AUTO_CANCEL;
                                    notify.defaults |= Notification.DEFAULT_LIGHTS;
                                    notify.defaults |= Notification.DEFAULT_VIBRATE;
                                    notify.defaults |= Notification.DEFAULT_SOUND;
                                    notif.notify(0, notify);
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
