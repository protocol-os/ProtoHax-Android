package dev.sora.protohax;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.WindowManager;
import androidx.core.app.NotificationCompat;
import com.github.megatronking.netbare.NetBare;
import com.github.megatronking.netbare.NetBareService;
import dev.sora.protohax.ContextUtils.toast;
import dev.sora.protohax.forwarder.R;
import kotlin.random.Random;

class AppService extends NetBareService {

    private WindowManager windowManager;

    @Override
    public void onCreate() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW));
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int notificationId() {
        return Random.Default.nextInt();
    }

    @Override
    public Notification createNotification() {
        int flag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;

        Intent intent = new Intent(this, MainActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flag);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        builder
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.proxy_notification, getString(R.string.app_name), NetBare.get().config.allowedApplications.firstOrNull() ?: "unknown"))
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        return builder.build();
    }

    public static final String CHANNEL_ID = "dev.sora.protohax.NOTIFICATION_CHANNEL_ID";
}
