package com.example.vpntest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

//import androidx.annotation.NonNull;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

// $FF: synthetic class
public class GlobalApplication extends Application {
   static final int FR1ENDS_dknkkbfmcetd = 0;
   static final int FR1ENDS_oxgvlthmwdic = 0;

   // $FF: synthetic class
   public static final class CrashActivity extends Activity {
      static final int FR1ENDS_deedyjsqojec = 0;
      static final int FR1ENDS_gbtnbgqjpqrl = 0;
      private String mLog;
   }

   // $FF: synthetic class
   public static class CrashHandler {
      public static final Thread.UncaughtExceptionHandler DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = Thread.getDefaultUncaughtExceptionHandler();
      static final int FR1ENDS_hwlvywiwwyfe = 0;
      static final int FR1ENDS_ybtlyifjdukm = 0;
      private static CrashHandler sInstance;

      // $FF: synthetic class
      private static class UncaughtExceptionHandlerImpl implements Thread.UncaughtExceptionHandler {
         private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
         static final int FR1ENDS_briypbtojvby = 0;
         static final int FR1ENDS_uqukobozkcyc = 0;
         private final Context mContext;
         private final File mCrashDir;

         // $FF: synthetic method
         public UncaughtExceptionHandlerImpl(Context var1, String var2) {
            this.mContext = var1;
            File var3;
            if (TextUtils.isEmpty(var2) != Boolean.valueOf(String.valueOf(FR1ENDS_briypbtojvby))) {
               var3 = new File(var1.getExternalCacheDir(), "crash");
            } else {
               var3 = new File(var2);
            }

            this.mCrashDir = var3;
         }

         @Override
         public void uncaughtException(Thread t, Throwable e) {

         }
      }
   }
}
