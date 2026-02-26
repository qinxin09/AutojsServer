package com.example.vpntest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;
import org.autojs.autojsserver.R;

public class MainActivity extends Activity {
   static final int FR1ENDS_faxpfcwbdbdr = 0;
   static final int FR1ENDS_glqyurehomfy = 0;
   private static final int VPN_REQUEST_CODE = 15;
   static MainActivity instance;
   int click = 0;
   private Handler handler;
   TextView showtext;
   Button vpnBtn;
   Intent vpnIntent;
   private long clickTime = 0;
   public static AtomicLong downByte = new AtomicLong((long) 0);
   public static AtomicLong upByte = new AtomicLong((long) 0);
   public Toast lastToast = null;

   /* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
   static class UpdateText implements Runnable {
      static final int FR1ENDS_adevqlbsqmua = 0;
      static final int FR1ENDS_szirdzqcwhxf = 0;
      TextView textView;

      UpdateText(TextView textView) {
         this.textView = textView;
      }

      @Override // java.lang.Runnable
      public void run() {
         while (true) {
            try {
               Thread.sleep((long) 100);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            this.textView.post(new Runnable() { // from class: com.example.vpntest.MainActivity.UpdateText.100000002
               static final int FR1ENDS_fbjwvnjwxzsw = 0;
               static final int FR1ENDS_oletqenksbmi = 0;

               @Override // java.lang.Runnable
               public void run() {
                  UpdateText.this.textView.setText(String.format("up %dKB down %dKB", new Long(MainActivity.upByte.get() / ((long) 1024)), new Long(MainActivity.downByte.get() / ((long) 1024))));
               }
            });
         }
      }
   }

   /* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
   static class UpdateTime implements Runnable {
      static final int FR1ENDS_mqexstmbzzvy = 0;
      static final int FR1ENDS_ubndhkhesffm = 0;
      TextView textView;

      UpdateTime(TextView textView) {
         this.textView = textView;
      }

      @Override // java.lang.Runnable
      public void run() {
         new SysTimeFetcher().fetchSysTime();
      }
   }

   /* JADX INFO: Access modifiers changed from: private */
   public void startVpn() {
      onActivityResult(VPN_REQUEST_CODE, -1, null);
      try {
         startService(new Intent(this, Class.forName("com.example.vpntest.LocalVPNService")));
      } catch (ClassNotFoundException e) {
         throw new NoClassDefFoundError(e.getMessage());
      }
   }

   public void stopVpn() {
       try {
           stopService(new Intent(this, Class.forName("com.example.vpntest.LocalVPNService")));
       } catch (ClassNotFoundException e) {
           throw new RuntimeException(e);
       }
   }
   

   /* JADX INFO: Access modifiers changed from: private */
   public void startserve() {
      try {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, Class.forName("com.example.vpntest.SecureServerService")));
         }else{
            throw new NoClassDefFoundError("version not support");
         }
      } catch (ClassNotFoundException e) {
         throw new NoClassDefFoundError(e.getMessage());
      }
   }
   public void stopserver(){
       try {
           stopService(new Intent(this, Class.forName("com.example.vpntest.SecureServerService")));
       } catch (ClassNotFoundException e) {
           throw new RuntimeException(e);
       }
   }

   void get_permissions_storage() {
      String[] strArr = new String[4];
      if (Build.VERSION.SDK_INT < 30 || (((Environment.isExternalStorageManager() ? 1 : 0) & 38803) ^ FR1ENDS_glqyurehomfy) == 0) {
         Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
         intent.setData(Uri.fromParts("package", getApplicationContext().getPackageName(), null));
         try {
            startActivity(intent);
            checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public boolean checkExpireTime(){
       try {
          InputStream is = this.getAssets().open("expire.txt");
          byte[] bArr = new byte[is.available()];
          is.read(bArr);
          String expireDate = new String(bArr);
          // 到期则弹窗提示
          SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
          long time = spf.parse(expireDate).getTime();
          if(new Date().getTime() > time){
             // Dialog弹窗
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setTitle("到期提示");
             builder.setCancelable(false);
             builder.setMessage("您的软件已到期，请联系开发者！");
             builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   joinQQGroup();
                }
             });
             builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
                   finish();
                }
             });
             builder.show();
             return true;
          }
       } catch (IOException e) {
           throw new RuntimeException(e);
       } catch (ParseException e) {
           throw new RuntimeException(e);
       }
       return false;
   }
   @SuppressLint("MissingInflatedId")
   @Override // android.app.Activity
   protected void onCreate(Bundle bundle) {
      String stringBuffer;
      super.onCreate(bundle);
      this.handler = new Handler(getMainLooper());
      setContentView(R.layout.a);
      // statusbarcolor
      getWindow().setStatusBarColor(getResources().getColor(R.color.black));
      instance = this;
      this.vpnBtn = findViewById(R.id.vpnbtn);
      this.vpnBtn.setOnClickListener(new View.OnClickListener() { // from class: com.example.vpntest.MainActivity.100000000
         static final int FR1ENDS_cspdcyuyruhx = 0;
         static final int FR1ENDS_yjslhteouglv = 0;

         @SuppressLint("ResourceAsColor")
         @Override // android.view.View.OnClickListener
         public void onClick(View view) {
            if(checkExpireTime()) return;
            if(new Date().getTime()-clickTime<2000){
               // 先关闭正在显示的所有toast
               if(lastToast != null){
                  lastToast.cancel(); // 先取消上一个 Toast
               }
               lastToast = Toast.makeText(MainActivity.instance, "请勿频繁点击", Toast.LENGTH_SHORT);
               lastToast.show();
               return;
            }
            clickTime = new Date().getTime();
            if (MainActivity.this.click == 0) {
               Toast.makeText(MainActivity.instance, "开启autojspro转接服务器", Toast.LENGTH_SHORT).show();
               MainActivity.this.startserve();
               MainActivity.this.startVpn();
               MainActivity.this.click++;
               MainActivity.this.vpnBtn.setBackgroundResource(R.drawable.b);
               MainActivity.this.vpnBtn.setText("已开启本地服务器");
               MainActivity.this.vpnBtn.setTextColor(R.color.d);
            }else{
               onClickStop();
            }
         }
      });
      Intent prepare = VpnService.prepare(this);
      this.vpnIntent = prepare;
      if (prepare != null) {
         startActivityForResult(prepare, VPN_REQUEST_CODE);
      }
      String readFile = FileUtils.readFile(this, "mm.txt");
      if (readFile.length() == 0) {
         stringBuffer = new StringBuffer().append(System.currentTimeMillis()).append("").toString();
      } else if (System.currentTimeMillis() > Long.parseLong(readFile)) {
         stringBuffer = new StringBuffer().append(System.currentTimeMillis()).append("").toString();
      } else {
         return;
      }
      FileUtils.writeFile("mm.txt", stringBuffer);
      findViewById(R.id.launchAutojsPro).setOnClickListener((v)->launchApp("org.autojs.autojspro", "org.autojs.autojs.ui.splash.SplashActivity"));
      findViewById(R.id.launchAutojsPro).setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View v) {
            Toast.makeText(MainActivity.this, "长按", Toast.LENGTH_SHORT).show();
            return true;
         }
      });
      findViewById(R.id.offlineVersion).setOnClickListener((v)->{
         joinQQGroup();
      });
   }
   public void joinQQGroup(){
      String url = "http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=toPLG7ZO3YkAg4-n6X_ibL07apAXUFOe&authKey=DTx6%2F0EIICaOclLuJFqDdQ6ordjFTRzKTCF4oCOaUoQkAiU9SdpcFQ1ny2e03OUM&noverify=0&group_code=975044417";
      url ="mqqopensdkapi://bizAgent/qm/qr?url="+ URLEncoder.encode( url);
      // 创建Intent
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(url));
      try{
         startActivity(intent);
      }catch (Exception e){}
   }
   @SuppressLint("ResourceAsColor")
   public void onClickStop(){
      MainActivity.this.stopserver();
      MainActivity.this.stopVpn();
      MainActivity.this.vpnBtn.setBackgroundResource(R.drawable.a);
      MainActivity.this.click = 0;
      Toast.makeText(MainActivity.instance, "服务器已关闭", Toast.LENGTH_SHORT).show();
      MainActivity.this.vpnBtn.setText("已关闭本地服务器");
      MainActivity.this.vpnBtn.setTextColor(R.color.c);
   }
   private void launchApp(String packageName, String activityName){
      PackageManager packageManager = getPackageManager();
      try {
         PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
         String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
         // 应用已安装，继续启动Intent
         Intent intent1 = new Intent();
         intent1.setClassName(packageName, activityName);
         intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         if (intent1.resolveActivity(getPackageManager())!= null) {
            startActivity(intent1);
//                Toast.makeText(HelloWorldActivity.this, "启动VPN成功", Toast.LENGTH_SHORT).show();
         }
      } catch (PackageManager.NameNotFoundException e) {
         // 应用未安装，提示用户安装
         Toast.makeText(MainActivity.this, "未安装"+packageName, Toast.LENGTH_SHORT).show();
         e.printStackTrace();
      }
   }

   public void updateLog(String str) {
      int i = ((this.handler.post(new Runnable() { // from class: com.example.vpntest.MainActivity.100000001
         static final int FR1ENDS_pguzbzmvrygf = 0;
         static final int FR1ENDS_vfhujauobjxc = 0;

         @Override // java.lang.Runnable
         public void run() {
            MainActivity.this.showtext.setText(String.format("up %dKB down %dKB", new Long(MainActivity.upByte.get() / ((long) 1024)), new Long(MainActivity.downByte.get() / ((long) 1024))));
         }
      }) ? 1 : 0) << 1) / 2;
   }


}