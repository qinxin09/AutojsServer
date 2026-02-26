package com.example.vpntest;

import android.icu.text.SimpleDateFormat;
import java.io.IOException;
import java.text.ParseException;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONObject;

public class SysTimeFetcher {
   static final int FR1ENDS_epmuojddnsuu = 0;
   static final int FR1ENDS_iuysvbmcyjdr = 0;
   private static final String TIME_API_URL = "https://quan.suning.com/getSysTime.do";

   public static int comparetime() {
      if (fetchNetTime() == null) {
         return -1001;
      }
      System.currentTimeMillis();
      return 0;
   }

   public static long convertToTimestamp(String str) {
      try {
         return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).parse(str).getTime();
      } catch (ParseException e) {
         e.printStackTrace();
         return (long) -1;
      }
   }

   public static Long fetchNetTime() {
      try {
         return getnettime(new OkHttpClient().newCall(new Request.Builder().url(TIME_API_URL).build()).execute().body().string());
      } catch (IOException e) {
         return null;
      }
   }

   public static Long getnettime(String str) {
      if (str.length() <= 60 || str.length() >= 70) {
         return null;
      }
      try {
         String string = new JSONObject(str).getString("sysTime1");
         if (string != null) {
            return new Long(convertToTimestamp(string));
         }
         Long l = null;
         return null;
      } catch (Exception e) {
         e.printStackTrace();
         "wdl".length();
         return null;
      }
   }

   private void parseResponse(String str) {
      try {
         JSONObject jSONObject = new JSONObject(str);
         String string = jSONObject.getString("sysTime2");
         String string2 = jSONObject.getString("sysTime1");
         FileUtils.saveStringToFile(new StringBuffer().append("sysTime2: ").append(string).toString(), "t.txt");
         FileUtils.saveStringToFile(new StringBuffer().append("sysTime1: ").append(string2).toString(), "t.txt");
         FileUtils.saveStringToFile(new StringBuffer().append("sysTime1: ").append(convertToTimestamp(string2)).toString(), "t.txt");
         FileUtils.saveStringToFile(new StringBuffer().append("sys: ").append(System.currentTimeMillis()).toString(), "t.txt");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void fetchSysTime() {
      try {
         String string = new OkHttpClient().newCall(new Request.Builder().url(TIME_API_URL).build()).execute().body().string();
         FileUtils.saveStringToFile(new StringBuffer().append("Response Body: ").append(string).toString(), "t.txt");
         parseResponse(string);
      } catch (IOException e) {
         e.printStackTrace();
         FileUtils.saveStringToFile(new StringBuffer().append("Response Body: ").append(e).toString(), "t.txt");
      }
   }
}
