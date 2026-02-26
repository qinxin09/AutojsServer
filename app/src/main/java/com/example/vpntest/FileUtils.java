package com.example.vpntest;

import android.content.Context;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

/* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
public class FileUtils {
   static final int FR1ENDS_ivykqiigdefo = 0;
   private static final String TAG = "";

   public static String byteArrayToChar(ByteBuffer byteBuffer, int i) {
      StringBuilder sb = new StringBuilder();
      for (int i2 = 0; i2 < i; i2++) {
         sb.append(String.format("%c", new Byte(byteBuffer.get(i2))));
      }
      return sb.toString();
   }

   public static String byteArrayToHex(ByteBuffer byteBuffer, int i) {
      StringBuilder sb = new StringBuilder();
      for (int i2 = 0; i2 < i; i2++) {
         sb.append(String.format("%02X ", new Byte(byteBuffer.get(i2))));
      }
      return sb.toString();
   }

   static String deal(ByteBuffer byteBuffer, String str) {
      if (byteBuffer.position() == 0) {
         return "";
      }
      String stringBuffer = new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("").append("****************************\n").toString()).append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("ca:").append(byteBuffer.capacity()).toString()).append(" po:").toString()).append(byteBuffer.position()).toString()).append(" lim:").toString()).append(byteBuffer.limit()).toString()).append("\n").toString()).toString()).append(new StringBuffer().append(new StringBuffer().append("des:").append(str).toString()).append("\n").toString()).toString();
      byte b = byteBuffer.get(9);
      String str2 = b == 6 ? "TCP" : b == 17 ? "UDP" : "Other";
      String stringBuffer2 = new StringBuffer().append(new StringBuffer().append(stringBuffer).append(new StringBuffer().append(new StringBuffer().append("len: ").append(byteBuffer.position()).toString()).append("\n").toString()).toString()).append(new StringBuffer().append(new StringBuffer().append("protocolType: ").append(str2).toString()).append("\n").toString()).toString();
      if (byteBuffer.position() <= 20) {
         return stringBuffer2;
      }
      String stringBuffer3 = new StringBuffer().append(new StringBuffer().append(stringBuffer2).append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("srcip:").append(String.format("%d.%d.%d.%d", new Integer(byteBuffer.get(12) & 255), new Integer(byteBuffer.get(13) & 255), new Integer(byteBuffer.get(14) & 255), new Integer(byteBuffer.get(15) & 255))).toString()).append(":").toString()).append(((byteBuffer.get(20) & 255) << 8) | (byteBuffer.get(21) & 255)).toString()).append("\n").toString()).toString()).append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("destip:").append(String.format("%d.%d.%d.%d", new Integer(byteBuffer.get(16) & 255), new Integer(byteBuffer.get(17) & 255), new Integer(byteBuffer.get(18) & 255), new Integer(byteBuffer.get(19) & 255))).toString()).append(":").toString()).append((byteBuffer.get(23) & 255) | ((byteBuffer.get(22) & 255) << 8)).toString()).append("\n").toString()).toString();
      if (str2 != "UDP") {
         return stringBuffer3;
      }
      String stringBuffer4 = new StringBuffer().append(stringBuffer3).append(new StringBuffer().append(new StringBuffer().append("udp:").append(IpPacketParser.parseUdpHeader(byteBuffer).destPort).toString()).append("\n").toString()).toString();
      if (byteBuffer.get(22) == 0 && byteBuffer.get(23) == 53) {
         StringBuilder sb = new StringBuilder();
         int i = 40;
         byte b2 = byteBuffer.get(40);
         while (b2 != 0) {
            for (int i2 = 0; i2 < b2; i2++) {
               sb.append(String.format("%c", new Byte(byteBuffer.get(i + i2 + 1))));
            }
            i += b2 + 1;
            b2 = byteBuffer.get(i);
            if (b2 != 0) {
               sb.append(".");
            }
         }
         stringBuffer4 = new StringBuffer().append(new StringBuffer().append(stringBuffer4).append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("======> DNS:").append(sb.toString()).toString()).append(" id:").toString()).append((byteBuffer.get(28) & 65280) | (byteBuffer.get(29) & 255)).toString()).append("\n").toString()).toString()).append(new StringBuffer().append(new StringBuffer().append("HEX: ").append(byteArrayToHex(byteBuffer, byteBuffer.position())).toString()).append("\n").toString()).toString();
      }
      return (byteBuffer.get(20) == 0 && byteBuffer.get(21) == 53) ? new StringBuffer().append(stringBuffer4).append(new StringBuffer().append(new StringBuffer().append("HEX: ").append(byteArrayToHex(byteBuffer, byteBuffer.position())).toString()).append("\n").toString()).toString() : stringBuffer4;
   }

   public static String readFile(Context context, String str) {
      StringBuilder sb = new StringBuilder();
      try {
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(str)));
         while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
               break;
            }
            sb.append(readLine).append("\n");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return sb.toString().trim();
   }

   public static void saveStringToFile(String str, String str2) {
   }

   public static void writeFile(String str, String str2) {
      try {
         saveStringToFile("OK", "e.txt");
         FileOutputStream openFileOutput = MainActivity.instance.openFileOutput(str, 0);
         openFileOutput.write(str2.getBytes());
         openFileOutput.flush();
      } catch (IOException e) {
         e.printStackTrace();
         saveStringToFile(new StringBuffer().append(e).append("").toString(), "e.txt");
      }
   }
}