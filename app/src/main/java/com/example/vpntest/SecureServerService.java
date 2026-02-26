package com.example.vpntest;

import android.annotation.SuppressLint;
import android.app.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import com.example.vpntest.config.Config;

import org.autojs.autojsserver.R;

/* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
public class SecureServerService extends Service {
   static final int FR1ENDS_ecqdxbujycwd = 0;
   static final int FR1ENDS_mqbeiacvgkrw = 0;
   public static HttpsServer httpsServer;
   public static HttpServer httpServer;

   private void createNotificationChannel() {
      if (Build.VERSION.SDK_INT >= 26) {
         NotificationChannel notificationChannel = new NotificationChannel("ForegroundServiceChannelID", "MyForegroundServiceChannel", NotificationManager.IMPORTANCE_LOW);
         notificationChannel.setDescription("Channel for Foreground Service");
         try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Class.forName("android.app.NotificationManager"));
            if (notificationManager != null) {
               notificationManager.createNotificationChannel(notificationChannel);
            }
         } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
         }
      }
   }

   public void closeserver() {
      httpsServer.closeAllConnections();
      httpServer.closeAllConnections();
   }

   @Override // android.app.Service
   public IBinder onBind(Intent intent) {
      return null;
   }

   @SuppressLint("ForegroundServiceType")
   @Override // android.app.Service
   public void onCreate() {
      super.onCreate();
      createNotificationChannel();
      try {
         HttpsServer httpsServer = new HttpsServer(Config.HTTPS_PROXY_PORT, "sxc.bks", "123456789");
         SecureServerService.httpsServer = httpsServer;
         httpsServer.start(5000, true);
         HttpServer httpServer = new HttpServer(Config.HTTP_PROXY_PORT);
         SecureServerService.httpServer = httpServer;
         httpServer.start(5000, true);

      } catch (Exception e) {
         e.printStackTrace();
         FileUtils.saveStringToFile(new StringBuffer().append("服务问题").append(e).toString(), "z.txt");
      }
      startForeground(11584, new NotificationCompat.Builder(this, "ForegroundServiceChannelID").setContentTitle("AotoJsPro自建服务器运行中").setContentText("AotoJsPro自建服务器正在运行, 监听端口:"+Config.HTTPS_PROXY_PORT+"|"+Config.HTTP_PROXY_PORT+", 请勿占用该端口.").setSmallIcon(R.drawable.c).setSilent(true).build());
   }

   @Override // android.app.Service
   public void onDestroy() {
      super.onDestroy();
      FileUtils.saveStringToFile("服务销毁", "z.txt");
      if (SecureServerService.httpsServer != null) {
         SecureServerService.httpsServer.stop();
      }
      if(SecureServerService.httpServer != null){
         SecureServerService.httpServer.stop();
      }
   }

   @Override // android.app.Service
   public int onStartCommand(Intent intent, int i, int i2) {
      return Service.START_STICKY;
   }
}