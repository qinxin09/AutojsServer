package com.example.vpntest.config;

public class Config {
   public static final String VPN_ROUTE = "0.0.0.0";
   //只代理本app的流量
   public static boolean testLocal = false;
   //配置dns
   public static String dns = "114.114.114.114";
   public static String VPN_ADDRESS = "10.0.0.2"; //虚拟的本地ip,给vpn用的
   public static final int HTTPS_PROXY_PORT = 8443;
   public static final int HTTP_PROXY_PORT = 10080;
   public static final int HTTPS_PORT = 443;
   public static final int HTTP_PORT = 80;
   public static final String VIRTUAL_IPV4 = "192.168.100.100"; // 虚拟的目标ip地址,给vpn用的，用于替换dns响应包的目标地址
   public static final String domain = "boys.intcl.top.";
   public static final String domain2 = "aj.joysboy.com.";

   public static byte[] getProxyPortBytes(int port){
      byte[] proxyPortBytes = new byte[2];
      proxyPortBytes[0] = (byte) ((port >> 8) & 0xFF); // [8448-->高8位 -> 32]
      proxyPortBytes[1] = (byte) (port & 0xFF);        // [8448-->低8位 -> 251]
      return proxyPortBytes;
   }
   public static int[] stringToIpv4IntArray(String ipv4){
      String[] ip = ipv4.split("\\.");
      if(ip.length != 4){
         throw new IllegalArgumentException("ipv4 is not valid");
      }
      int[] result = new int[4];
      for (int i = 0; i < 4; i++) {
         result[i] = Integer.parseInt(ip[i]);
      }
      return result;
   }
   public static byte[] stringIpv4ToBytes(String str) {
      if (str == null || str.isEmpty()) {
         throw new IllegalArgumentException("Input string cannot be null or empty");
      }

      String[] parts = str.split("\\.");

      // Validate IP structure structure: must have exactly 4 parts
      if (parts.length != 4) {
         throw new IllegalArgumentException("Invalid IPv4 address format");
      }

      byte[] ipBytes = new byte[4];

      try {
         for (int i = 0; i < 4; i++) {
            int octet = Integer.parseInt(parts[i]);

            // Validate octet range
            if (octet < 0 || octet > 255) {
               throw new IllegalArgumentException("Octet value out of range [0,255]");
            }

            ipBytes[i] = (byte) octet;
         }
      } catch (NumberFormatException e) {
         throw new IllegalArgumentException("Invalid octet value - must be numeric", e);
      }

      return ipBytes;
   }
   //io日志
   @Deprecated
   public static boolean logRW = false;
   //ack日志
   @Deprecated
   public static boolean logAck = false;
}