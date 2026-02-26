package com.example.vpntest;

import android.util.Log;

import java.io.IOException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import fi.iki.elonen.NanoHTTPD;

/* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
public class HttpServer extends NanoHTTPD {
   static final int FR1ENDS_fbkcpncvfuod = 0;
   static final int FR1ENDS_xkqwohqvysll = 0;

   public HttpServer(int port) throws Exception {
      super(port);
   }

   private Response handleGetRequest(String str) {
      if("/".equals(str)){
         return NanoHTTPD.newFixedLengthResponse("<html><body><h1>Hello HTTP!</h1></body></html>");
      }else if("/api/data".equals(str)){
         return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "text/plain", "This is data from API.");
      }else{
         return NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found");
      }
   }

   private Response handleGetjson(String str, String str2) {
      HashMap hashMap = new HashMap();
      hashMap.put(str, str2);
      return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", jsonEncode(hashMap));
   }

   private Response handlePostRequest(String str, IHTTPSession iHTTPSession, Map<String, String> map, Map<String, String> map2) {
      if("/submit".equals(str)){
         return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "text/plain", String.format("Received POST request with name: %s and email: %s", map.get("name"), map.get("email")));
      }else{
         return NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found");
      }
   }

   private String jsonEncode(Map<String, String> map) {
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      boolean z = true;
      for (Map.Entry<String, String> entry : map.entrySet()) {
         if (!z) {
            sb.append(", ");
         }
         sb.append("\"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\"");
         z = false;
      }
      sb.append("}");
      return sb.toString();
   }

   public Response serve(IHTTPSession iHTTPSession) {
      Method method = iHTTPSession.getMethod();
      String uri = iHTTPSession.getUri();
      Log.e("uri","http"+method+" uri="+uri);
      if (Method.GET.equals(method)) {
         StringBuffer stringBuffer = new StringBuffer();
         "x".length();
         FileUtils.saveStringToFile(stringBuffer.append("访问路径:").append(uri).toString(), "log.txt");
         return NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
      } else if (Method.POST.equals(method)) {
         if("/api/v8/security/validation2".equals(uri) ){
            Log.e("qxdebug","/api/v8/security/validation2");
            String data = "uNl8AK0WM6mIAQAAM9bHGgAAAACaX4kztI8jdDdMKBwYbba4oNAKCHba0nRgN7zXoP0IzjEyM2NjZjgzMmFiZTg5OGYAAQAAAAAAAAAyWsXfnWpHYVlJ4ZPT/u3n+ZH3NLvubrTRJnas08r0ijocgKnKqCxTFvJgeZnWx2omp6CzeSFWEG8aEaarJ4XMkp9+F8sdy2yFkqkOrp41KmCfShbIQX4hCYeD0mVOOwfOVLpQLJjg18FvFvHm9TKYzK5ysfv9UHuHn8+dexgnLM28j5BDrIFv9B9XS+UW1x/lLAwe+QzBEAWzsYFKPkVJ9Mc0L5lG/i8Eh7bxcGHIg1L+VbC4t9+CZXcF6DOoy75I40omuQs/gtbLCsMEr7fdsiDQ76iukr1SwLHVIEaXrNutrvvqKp+UBcq4WGQEM+aMj46S3pd7+h17J8vKdTVknI2IOJPZM2mVjGCQ3MBriG5HQqghbFE3y/VEPWpmtkgjDXqc09vuYA4PLxnV1AbvoAEvy8FgqxY00MXANK2MMixzZorUIC2Jk1hBLgPYHd1lMPlAMt8Deab3KZ0sJNLMo/7tAzk50DrPse3onAg5oA5QTSDfKBI2AtZP+DmPYrtsa96iUFK9iz8/18Pnhw/GBd+ceDR00dpQRVGjqTFxftAtZFr9kFYXTfz94+uq/fnVlH4eDGQiNAvuPg/4nQLXlde3lDYp5loaN2MkjL4uK9m8uQjH68217L195jsXANSo8IKjJYqWzcA1oCF/Smnmwc03k0Uk5OcfunIF/AGJ1g==";
//            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", "{\"data\": \"" + data + "\"}");
            return NanoHTTPD.newFixedLengthResponse(Response.Status.GONE, "text/plain", "Not Found2");
         }else {
            try {
               iHTTPSession.getHeaders();
               Map<String, String> parms = iHTTPSession.getParms();
               HashMap hashMap = new HashMap();
               iHTTPSession.parseBody(hashMap);
               return handlePostRequest(uri, iHTTPSession, parms, hashMap);
            } catch (IOException | ResponseException e) {
               e.printStackTrace();
               return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Internal Error");
            }
         }
      }else{
         return NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found2");
      }
   }
}