package vikram.findbyf;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.GregorianCalendar;

public class ActiveDataSendingService extends Service {
    public ActiveDataSendingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent alertIntent = new Intent(this, SendDataToServer.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, GregorianCalendar.getInstance().getTimeInMillis(), repeatTime(), pendingIntent);



        Intent alertIntent1 = new Intent(this, SendContactListToServer.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 0, alertIntent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager1.setInexactRepeating(AlarmManager.RTC_WAKEUP, GregorianCalendar.getInstance().getTimeInMillis(), repeatTime1(), pendingIntent1);

        Intent alertIntent2 = new Intent(this, pushNotification.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 0, alertIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager2.setInexactRepeating(AlarmManager.RTC_WAKEUP, GregorianCalendar.getInstance().getTimeInMillis(), repeatTime2(), pendingIntent2);

        return START_STICKY;
    }

    public long repeatTime() {
        return 6000;

    }
    public long repeatTime1() {
        return 24*60*60*1000;

    }
    public long repeatTime2() {
        return 24*60*60*000;

    }
}
