package com.example.vpntest;

import android.util.Log;

import org.json.JSONArray;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
public class HttpsServer extends NanoHTTPD {
   static final int FR1ENDS_fbkcpncvfuod = 0;
   static final int FR1ENDS_xkqwohqvysll = 0;

   public HttpsServer(int i, String str, String str2) throws Exception {
      super(i);
      KeyStore instance = KeyStore.getInstance("BKS");
      instance.load(MainActivity.instance.getAssets().open(str), str2.toCharArray());
      SSLContext instance2 = SSLContext.getInstance("TLS");
      TrustManagerFactory instance3 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      instance3.init(instance);
      KeyManagerFactory instance4 = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      instance4.init(instance, str2.toCharArray());
      instance2.init(instance4.getKeyManagers(), instance3.getTrustManagers(), null);
      makeSecure(NanoHTTPD.makeSSLSocketFactory(instance, instance4), null);
   }

   private Response handleGetRequest(String str) {
      if("/".equals(str)){
         return NanoHTTPD.newFixedLengthResponse("<html><body><h1>Hello HTTPS!</h1></body></html>");
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

   @Override
   public Response serve(IHTTPSession iHTTPSession) {
      Method method = iHTTPSession.getMethod();
      String uri = iHTTPSession.getUri();
      Log.e("uri","https "+method+" uri="+uri);
      if (Method.CONNECT.equals(iHTTPSession.getMethod())) {
         Log.e("connect","出现了connect请求");
         return null;
      } else if (Method.GET.equals(method)) {
         StringBuffer stringBuffer = new StringBuffer();
         FileUtils.saveStringToFile(stringBuffer.append("访问路径:").append(uri).toString(), "log.txt");
         if ("/csrfToken".equals(uri)) {
            FileUtils.saveStringToFile("令牌查询", "log.txt");
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", "{\"_csrf\": \"Tbs6hIVo--Ngb_G9VJ3lnoMR1EYRnQli5bEY\"}");
         } else if(uri.indexOf("docs/documentation.json")!=-1){
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", "{\"documentation_version\":20221024}");
         } else if(uri.indexOf("/api/v1/project/categories")!=-1){
            String[] categories = new String[] {"官方示例", "模块", "系统工具", "实用工具", "学习教育", "软件辅助", "游戏辅助", "游戏", "其他"};
            String res =  "[\"" + String.join("\",\"", categories) + "\"]";
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", res);
         } else if(uri.indexOf("/api/v1/project")!=-1){
            ArrayList<HashMap<String, Object>> data = new ArrayList<>();
            data.add(new HashMap<String, Object>() {{
               put("packageName", "com.ninedays.a.b");
               put("file", "http://pcdn.autojs.org/projects/migrated/97beef80-d1d7-4812-8000-c91bd4e03007.zip");
               put("name", "下拉框高度更改");
               put("permissions", new ArrayList<String>());
               put("version", "2.69");
               put("versionCode", 1);
               put("minSdkVersion", 0);
               put("contacts", new HashMap<String, Object>());
               put("summary", "花了两天研究改下拉框高度");
               put("details", "有更好的更改方法告诉我一下2307136635");
               put("images", new ArrayList<String>());
               put("releaseNotes", new HashMap<String, Object>());
               put("maxAutoJsVersion", 0);
               put("minAutoJsVersion", 0);
               put("minProVersion", 0);
               put("maxProVersion", 0);
               put("compileVersion", "Pro 9.1.20-0");
               put("category", "模块");
               put("tags", new ArrayList<String>());
               put("status", 0);
               put("fileSize", 1556);
               put("user", new HashMap<String, Object>() {{
                  put("id", "62a57ee9879b9e3dbb07a9b0");
                  put("emailAddress", "wm_v@qq.com");
                  put("fullName", "九天");
               }});
               put("upvoted", false);
               put("upvotedCount", 0);
               put("id", "6309b505f9e3cc1848d963bb");
            }});
            JSONArray sendData = new JSONArray(data);
            String res = sendData.toString();
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", res);
         } else if(uri.indexOf("/docs/v8")!=-1 || uri.indexOf("/docs")!=-1){
            Log.e("/docs/v8","还未处理 /docs/v8");
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "text/html", "html");
         } else if ("/api/v1/config".equals(uri)) {
            FileUtils.saveStringToFile("config查询", "log.txt");
            return handleGetjson("wl", "0a4fd5d5accf385b8d5f382d7abcfea7");
         } else if ("/api/v1/announcements".equals(uri)) {
            FileUtils.saveStringToFile("公告获取", "log.txt");
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", "{}");
         } else {
            int i = FR1ENDS_xkqwohqvysll;
            if ("/api/v1/account".equals(uri)) {
               FileUtils.saveStringToFile("账户查询", "log.txt");
               long currentTimeMillis = System.currentTimeMillis();
               return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("{\"id\": \"6131f76468e4553fba39ae4c\",\"now\": ").append(currentTimeMillis).toString()).append(",\"emailAddress\": \"QQ交流群：975044417\",\"fullName\": \"AutojsPro9.3.11 host版\",\"paidServices\": {\"v8\": {\"expires\":").toString()).append(((long) 1000000) + currentTimeMillis).toString()).append(" }},\"permissions\": {}}").toString());
            }
            int i2 = FR1ENDS_fbkcpncvfuod;
            if ("/static/legal/version.json".equals(uri)) {
               FileUtils.saveStringToFile("版本查询", "log.txt");
               return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", "{\"version\": 20240211,\"wording\": \"AutojsPro\n阅读%s和%s全文了解详细信\n请点击“同意”继续接受我们的服务。\"}");
            } else if ("/api/v1/plugins".equals(uri)) {
               FileUtils.saveStringToFile("插件查询", "log.txt");
               return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", "[{\"package_name\":\"org.autojs.plugin.ffmpeg\",\"name\":\" 官方FFMpeg插件\",\"version\":\"1.1\",\"version_code\":1,\"summary\":\"FFmpeg是一套可以用来记录、转换数字音频、视频，并能将其转化为流的开源计算机程序。本插件用于利用ffmpeg处理音视频文件，比如从格式转换等。\",\"icon\":\"https://www.wuyunai.com/docs/assets/image/ffmpeg-plugin.png\",\"url\":\"https://www.wuyunai.com/docs/blog/ffmpeg-plugin.html\",\"installed\":false,\"update_timestamp\":0},{\"package_name\":\"org.autojs.plugin.mlkit\",\"name\":\"官方MLKitOCR插件\",\"version\":\"1.1\",\"version_code\":1,\"summary\":\"FFmpeg是一套可以用来记录、转换数字音频、视频，并能将其转化为流的开源计算机程序。本插件用于利用ffmpeg处理 音视频文件，比如从格式转换等。\",\"icon\":\"https://www.wuyunai.com/docs/assets/image/mlkit-ocr-plugin.png\",\"url\":\"https://www.wuyunai.com/docs/blog/mlkit-ocr-plugin.html\",\"installed\":false,\"update_timestamp\":0},{\"package_name\":\"cn.lzx284.p7zip\",\"name\":\"7Zip通用压缩插 件\",\"version\":\"1.2.1\",\"version_code\":4,\"summary\":\"本 插件基于p7zip 16.02制作，支持多种格式文件的压缩与解压。7-Zip是一款完全免费而且开源的压缩软件，相比其他软件有更高的压缩比但同时耗费的资源也相对更多，能提供比使 用 PKZip 及 WinZip 高2~10%的压缩比率。\",\"icon\":\"https://www.wuyunai.com/docs/assets/image/7zip-plugin.png\",\"url\":\"https://www.wuyunai.com/docs/blog/7zip-plugin.html\",\"documentation_url\":\"https://www.wuyunai.com/docs/blog/7zip-plugin.html\",\"installed\":false,\"update_timestamp\":0},{\"package_name\":\"com.hraps.pytorch\",\"name\":\"Pytorch插件\",\"version\":\"1.0.0\",\"version_code\":1,\"summary\":\"Pytorch模块提供了已完成的深度学习神经网络 模型在安卓设备上执行的功能，可以实现常规程序难以实现 的功能，如：图像识别，语言翻译，语言问答等。\",\"icon\":\"https://www.wuyunai.com/docs/assets/image/pytorch-logo.png\",\"url\":\"https://www.wuyunai.com/docs/v8/thirdPartyPlugins.html\",\"documentation_url\":\"https://www.wuyunai.com/docs/v8/thirdPartyPlugins.html#pytorch插件\",\"installed\":false,\"update_timestamp\":0}]");
            } else if ("/api/v1/project/categories".equals(uri)) {
               FileUtils.saveStringToFile("查询project/categories", "log.txt");
               return NanoHTTPD.newFixedLengthResponse("<!DOCTYPE html><html lang=\"en\"><head> <meta charset=\"utf-8\"> <title>Error</title> </head> <body> <pre>Cannot GET /api/v1/project/categories</pre> </body> </html>");
            } else if ("/api/22c08f8c483227ec5b0b8b93c5f9f841".equals(uri)) {
               return handleGetRequest(uri);
            } else {
               FileUtils.saveStringToFile("登录成功反馈", "log.txt");
               return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "text/plain", "登录成功|103547|11701766190");
            }
         }
      } else if (Method.POST.equals(method)) {
         return NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
      } else {
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
   }

}