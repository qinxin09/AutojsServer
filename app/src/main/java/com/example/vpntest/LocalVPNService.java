package com.example.vpntest;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.example.vpntest.bio.BioUdpHandler;
import com.example.vpntest.bio.NioSingleThreadTcpHandler;
import com.example.vpntest.config.Config;
import com.example.vpntest.protocol.tcpip.Packet;
import com.example.vpntest.util.ByteBufferPool;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
public class LocalVPNService extends VpnService {
   private static final String TAG;
   private BlockingQueue<Packet> deviceToNetworkTCPQueue;
   private BlockingQueue<Packet> deviceToNetworkUDPQueue;
   private ExecutorService executorService;
   private BlockingQueue<ByteBuffer> networkToDeviceQueue;
   private ParcelFileDescriptor vpnInterface = null;
   private static byte[] httpProxyPortBytes = Config.getProxyPortBytes(Config.HTTP_PROXY_PORT);
   private static byte[] httpsProxyPortBytes = Config.getProxyPortBytes(Config.HTTPS_PROXY_PORT);
   private static byte[] httpPortBytes = Config.getProxyPortBytes(Config.HTTP_PORT);
   private static byte[] httpsPortBytes = Config.getProxyPortBytes(Config.HTTPS_PORT);
   private static int[] vpnAddressIpv4IntArray = Config.stringToIpv4IntArray(Config.VPN_ADDRESS);
   private static int[] virtualIpv4IntArray = Config.stringToIpv4IntArray(Config.VIRTUAL_IPV4);
   private static byte[] dnsBytes = Config.stringIpv4ToBytes(Config.dns);
   private static byte[] VPN_ADDRESS_Bytes = Config.stringIpv4ToBytes(Config.VPN_ADDRESS);
   private static byte[] distProxyIpv4Bytes = Config.stringIpv4ToBytes(Config.VIRTUAL_IPV4);


   private PendingIntent pendingIntent;

   /* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
   private static class VPNRunnable implements Runnable {
      static final int FR1ENDS_qktjvrotctef = 0;
      private static final String TAG;
      private BlockingQueue<Packet> deviceToNetworkTCPQueue;
      private BlockingQueue<Packet> deviceToNetworkUDPQueue;
      private BlockingQueue<ByteBuffer> networkToDeviceQueue;
      private FileDescriptor vpnFileDescriptor;

      /* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
      static class WriteVpnThread implements Runnable {
         static final int FR1ENDS_bhisbjfmslvm = 0;
         private BlockingQueue<ByteBuffer> networkToDeviceQueue;
         FileChannel vpnOutput;

         WriteVpnThread(FileChannel vpnOutput, BlockingQueue<ByteBuffer> blockingQueue) {
            this.vpnOutput = vpnOutput;
            this.networkToDeviceQueue = blockingQueue;
         }
         /* JADX WARN: Multi-variable type inference failed */
         @Override // java.lang.Runnable
         public void run() {
            while (true) {
               try {
                  ByteBuffer take = this.networkToDeviceQueue.take();
                  take.flip();
                  int position = 0;
                  //拦截自建服务器返回的数据包
                  if ((take.get(12) & 255) == vpnAddressIpv4IntArray[0] && (take.get(13) & 255) == vpnAddressIpv4IntArray[1] && take.get(14) == vpnAddressIpv4IntArray[2] && take.get(15) == vpnAddressIpv4IntArray[3]) {
                     byte[] portBytes = new byte[0];

                     if(take.get(20) == httpsProxyPortBytes[0] && take.get(21) == httpsProxyPortBytes[1]){
                        portBytes = httpsPortBytes;
                     }else if(take.get(20) == httpProxyPortBytes[0] && take.get(21) == httpProxyPortBytes[1]){
                        portBytes = httpPortBytes;
                     }
                     FileUtils.saveStringToFile(new StringBuffer().append("拦截到自建服务器返回的数据包").append(String.format("0x%02x  0x%02x", new Byte(take.get(36)), new Byte(take.get(37)))).toString(), "pp.txt");
                     position = take.position();
                     // 修改ip
                     take.position(12);
                     take.put((byte) virtualIpv4IntArray[0]);
                     take.position(13);
                     take.put((byte) virtualIpv4IntArray[1]);
                     take.position(14);
                     take.put((byte) virtualIpv4IntArray[2]);
                     take.position(15);
                     take.put((byte) virtualIpv4IntArray[3]);

                     // 修改端口号
                     take.position(20);
                     take.put(portBytes[0]);
                     take.position(21);
                     take.put(portBytes[1]);
                     take.position(position);

                  }
                  Packet packet = new Packet(take);
                  packet.updateIP4Checksum();
                  if (packet.isTCP() ) {
                     packet.updateTCPChecksum(packet.getpayloadSize());
                  }else if(packet.isUDP()){
                     int a = 0;
                  }
                  FileUtils.saveStringToFile(packet.toString(), "pp.txt");
                  take.rewind();
                  while (((take.hasRemaining() ? 1 : 0) << 5) / 32 != 0) {
                     int write = this.vpnOutput.write(take);
                     if (write > 0) {
                        MainActivity.downByte.addAndGet((long) write);
                     }
                     if ((Config.logRW ? 1 : 0) % 22721 != 0) {
                        Log.d(VPNRunnable.TAG, new StringBuffer().append("vpn write ").append(write).toString());
                     }
                  }
               } catch (Exception e) {
                  Log.i(VPNRunnable.TAG, "WriteVpnThread fail", e);
               }
            }
         }
      }

      static {
         try {
            TAG = Class.forName("com.example.vpntest.LocalVPNService$VPNRunnable").getSimpleName();
         } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
         }
      }

      public VPNRunnable(FileDescriptor fileDescriptor, BlockingQueue<Packet> blockingQueue, BlockingQueue<Packet> blockingQueue2, BlockingQueue<ByteBuffer> blockingQueue3) {
         this.vpnFileDescriptor = fileDescriptor;
         this.deviceToNetworkUDPQueue = blockingQueue;
         this.deviceToNetworkTCPQueue = blockingQueue2;
         this.networkToDeviceQueue = blockingQueue3;
      }


      ByteBuffer replaceAddress_BOYS_INTCL_TOP(ByteBuffer byteBuffer) {
         byte[] bytes = new byte[]{
                 //IP头部（20字节）
                 69,   0, // Transaction ID
                 0, -123, // Flags
                 0,   1,  //Questions
                 64,   0, //Answer RRs
                 64,  17,  75, -127,  // Authority RRS & Additional RRs
                 dnsBytes[0], dnsBytes[1],  dnsBytes[2], dnsBytes[3], //Config.dns
                 VPN_ADDRESS_Bytes[0], VPN_ADDRESS_Bytes[1],  VPN_ADDRESS_Bytes[2], VPN_ADDRESS_Bytes[3],  // Config.VPN_ADDRESS
                 // UDP头部（8字节）
                 0,  53, // Source Port
                 -1, -99, // Dest Port:
                 0, 113,  // Length
                 0,   0, // Checksum

                 // DNS头部（12字节）
                 -2,  33,  // Transaction ID: 0xFE21
                 -127, Byte.MIN_VALUE, // Flags: 0x8180
                 // QR=1(Response), OPCODE=0, AA=1(Authoritative), TC=0, RD=1
                 // RA=1, Z=0, RCODE=0(No error)
                 0,   1, // Questions
                 0,   3, // Answer RRs
                 0,   0, // Authority RRs
                 0,   0, //Additional RRs

                 // Question Section
                 4,  98, 111,  121, 115,   //boys
                 5,  105, 110, 116, 99, 108,    //intcl
                 3, 116, 111,  112,   // top
                 0,//End of name
                 0,   1, //Type: A
                 0,   1, // Class: IN

                 // Answer Section（回答部分）
                 // 第一条记录
                 -64,  12, // Name: pointer to offset 12 ("boys.intcl.top")
                 0,   5,   // Type: CNAME (5)
                 0,   1,   // Class: IN (1)
                 0,    0,   2,  94,   // TTL: 606秒
                 0,  29,  // Data Length: 29字节
                 // CNAME数据：CNAME值： boys.intcl.top.eo.dnse2.com
                 4,  98, 111,  121, 115,   // "boys"
                 5,  105, 110, 116, 99, 108,  // "intcl"
                 3, 116,  111, 112,  // "top"
                 2, 101, 111,   // "eo"
                 5, 100,  110,  115, 101,  50, // "dnse2"
                 3,   99, 111, 109,  // "com"
                 0,// End
                 // 第二条记录
                 -64,  44, //Name: pointer to offset 44
                 0, 1,  // Type: A (1)
                 0, 1,  // Class: IN (1)
                 0, 0, 2, 94, // TTL: 606秒
                 0, 4,  // Data Length: 4字节
                 Byte.MAX_VALUE, 0, 0, 1, // IP Address: 127.0.0.1 ⭐⭐⭐
                 // 第三条记录
                 -64, 44, // Name: same pointer
                 0, 1, // Type: A (1)
                 0, 1, // Class: IN (1)
                 0, 0, 2, 94, // TTL: 606秒
                 0, 4, // Data Length: 4字节
                 distProxyIpv4Bytes[0],distProxyIpv4Bytes[1],distProxyIpv4Bytes[2],distProxyIpv4Bytes[3]        // IP Address
         };
         ByteBuffer wrap = ByteBuffer.wrap(bytes);
         String v = new String(wrap.array());
//         Log.e("qxdebug", "getn 替换域名: ");
         wrap.position(28);
         wrap.put(byteBuffer.get(28));
         wrap.position(29);
         wrap.put(byteBuffer.get(29));
         wrap.position(22);
         wrap.put(byteBuffer.get(20));
         wrap.position(23);
         wrap.put(byteBuffer.get(21));
         wrap.position(0);
         return wrap;
      }

      // aj.joysboy.com
      ByteBuffer replaceAddress_AJ_JOYSBOY_COM_bak(ByteBuffer byteBuffer) {
//         一个完整的DNS响应包，包含了：
//
//         IP头部（20字节）
//         UDP头部（8字节）
//         DNS数据部分
         ByteBuffer wrap = ByteBuffer.wrap(new byte[]{
                 //IP头部（20字节）
                 69, 0, // Transaction ID
                 0, -117, // Flags
                 0, 2, //Questions
                 64, 0, //Answer RRs
                 64, 17, 75, 122, // Authority RRS & Additional RRs
                 dnsBytes[0], dnsBytes[1],  dnsBytes[2], dnsBytes[3], //Config.dns
                 VPN_ADDRESS_Bytes[0], VPN_ADDRESS_Bytes[1],  VPN_ADDRESS_Bytes[2], VPN_ADDRESS_Bytes[3], // Config.VPN_ADDRESS

                 // UDP头部（8字节）
                 // Source Port
                 0, 53,
                 -93, -45, // Dest Port: (-93&0xFF)×256 + (-45&0xFF) = 163×256 + 211 = 41939
                 0, 119, // Length
                 0, 0, // Checksum


                 // DNS 头部
                 24, 50, // Transaction ID
                 -127, -125, // Flags
                 0, 1, // Questions
                 0, 0, // Answer RRs
                 0, 1, // Authority RRs
                 0, 0, //Additional RRs

                 // Question Section
                 2, 97, 106, // aj
                 7, 106, 111, 121, 115, 98, 111, 121, // joysboy
                 3, 99, 111, 109, // com
                 0, //End of name
                 0, 1, //Type: A
                 0, 1, // Class: IN
                 // Authority Section
                 3, 99, 111, 109, 0, // Name: "com." （根域）
                 0, 6, // Type：SOA
                 0, 1, // Class: IN
                 0, 0, 3, 119, // TTL:887 seconds
                 0, 64, // Data Length:64 bytes
                 // SOA 记录详细信息
                 1, 97, //a
                 12, 103, 116, 108, 100, 45, 115, 101, 114, 118, 101, 114, 115, // gtld-servers
                 3, 110, 101, 116, 0,// net.
                 5, 110, 115, 116, 108, 100, // nstld
                 12, 118, 101, 114, 105, 115, 105, 103, 110, 45, 103, 114, 115, // verisign-grs
                 3, 99, 111, 109, 0, //com
                 103, -97, -121, 111, // Serial: 1736333167
                 0, 0, 7, 8,// Refresh: 1800 seconds
                 0, 0, 3, -124,  // Retry: 900 seconds
                 0, 9, 58, Byte.MIN_VALUE, // Expire: 604800 seconds
                 0, 0, 3, -124 // Minimum TTL: 900 seconds
         });
         String v = new String(wrap.array());
         Log.e("qxdebug ", "replaceAddress_AJ_JOYSBOY_COM buffer="+v);
         wrap.position(28);
         wrap.put(byteBuffer.get(28));
         wrap.position(29);
         wrap.put(byteBuffer.get(29));
         wrap.position(22);
         wrap.put(byteBuffer.get(20));
         wrap.position(23);
         wrap.put(byteBuffer.get(21));
         wrap.position(0);
         return wrap;
      }

      ByteBuffer replaceAddress_AJ_JOYSBOY_COM_TEST(ByteBuffer byteBuffer){
         // 来自 放行这个域名后，从 WriteVpnThread 调试找出来的
         byte[] bytes = new byte[]{
                 //IP头部（20字节）
                 69, 0,  // Transaction ID
                 0,  105,// Flags
                 0,  1,  //Questions
                 64, 0,  //Answer RRs
                 64, 17, 75, -99,// Authority RRS & Additional RRs
                 dnsBytes[0], dnsBytes[1],  dnsBytes[2], dnsBytes[3], //Config.dns
                 VPN_ADDRESS_Bytes[0], VPN_ADDRESS_Bytes[1],  VPN_ADDRESS_Bytes[2], VPN_ADDRESS_Bytes[3],  // Config.VPN_ADDRESS
                 // UDP头部（8字节）
                 0, 53, // Source Port
                 -26, -46, // Dest Port:
                 0, 85, // Length
                 0, 0, // Checksum

                 // DNS头部（12字节）
                 -80, -13, // Transaction ID: 0xFE21
                 -127, -128, // Flags
                 0, 1, // Questions
                 0, 2, // Answer RRs
                 0, 0, // Authority RRs
                 0, 0, //Additional RRs
                 // Question Section
                 2, 97, 106, // aj
                 7, 106, 111, 121, 115, 98, 111, 121,  // joysboy
                 3, 99, 111, 109, // com
                 0, //End of name
                 0, 1, //Type: A
                 0, 1, // Class: IN

                 // Answer Section（回答部分）
                 // 第一条记录
                 -64, 12, //Name: pointer to offset 12
                 0, 5, // Type: CNAME (5)
                 0, 1, // Class: IN (1)
                 0, 0, 2, 91, // TTL: 606秒
                 0, 17, // Data Length: 29字节
                 // CNAME数据：
                 3, 119, 119, 119, // www
                 7, 119, 117, 121, 117, 110, 97, 105, // wuyunai
                 3, 99, 111, 109, // com
                 0,// End
                 // 第二条记录
                 -64, 44, //Name: pointer to offset 44
                 0, 1, // Type: A (1)
                 0, 1, // Class: IN (1)
                 0, 0, 2, 91, // TTL: 606秒
                 0, 4, // Data Length: 4字节
//                 1,12,-13,-91
                 distProxyIpv4Bytes[0],distProxyIpv4Bytes[1],distProxyIpv4Bytes[2],distProxyIpv4Bytes[3]        // IP Address
         };
         ByteBuffer wrap = ByteBuffer.wrap(bytes);
         String v = new String(wrap.array());
//         Log.e("qxdebug", "getn 替换域名: ");
         wrap.position(28);
         wrap.put(byteBuffer.get(28));
         wrap.position(29);
         wrap.put(byteBuffer.get(29));
         wrap.position(22);
         wrap.put(byteBuffer.get(20));
         wrap.position(23);
         wrap.put(byteBuffer.get(21));
         wrap.position(0);
         return wrap;
      }
      ByteBuffer getn2(ByteBuffer byteBuffer) {
         ByteBuffer wrap = ByteBuffer.wrap(new byte[]{69, 0, 0, 117, 0, 1, 64, 0, 64, 17, 75, -111,
                 dnsBytes[0], dnsBytes[1],  dnsBytes[2], dnsBytes[3], //Config.dns
                 VPN_ADDRESS_Bytes[0], VPN_ADDRESS_Bytes[1],  VPN_ADDRESS_Bytes[2], VPN_ADDRESS_Bytes[3],
                 0, 53, 63, 10, 0, 97, 0, 0, -44, 103, -123, -125, 0, 1, 0, 0, 0, 1, 0, 0, 1, 50, 1, 48, 1, 48, 2, 49, 48, 7, 105, 110, 45, 97, 100, 100, 114, 4, 97, 114, 112, 97, 0, 0, 12, 0, 1, 2, 49, 48, 7, 73, 78, 45, 65, 68, 68, 82, 4, 65, 82, 80, 65, 0, 0, 6, 0, 1, 0, 1, 79, -16, 0, 23, -64, 39, 0, 0, 0, 0, 0, 0, 0, 112, Byte.MIN_VALUE, 0, 0, 28, 32, 0, 9, 58, Byte.MIN_VALUE, 0, 1, 81, Byte.MIN_VALUE});
         String v = new String(wrap.array());
         Log.e("qxdebug ", "getn2");
         wrap.position(28);
         wrap.put(byteBuffer.get(28));
         wrap.position(29);
         wrap.put(byteBuffer.get(29));
         wrap.position(22);
         wrap.put(byteBuffer.get(20));
         wrap.position(23);
         wrap.put(byteBuffer.get(21));
         wrap.position(0);
         return wrap;
      }

      /* JADX WARN: Finally extract failed */
      /* JADX WARN: Multi-variable type inference failed */
      @Override // java.lang.Runnable
      public void run() {
         FileChannel inChannel = null;
         FileChannel outChannel = null;
         int i;
         try {
            Log.i(TAG, "Started");
            inChannel = new FileInputStream(this.vpnFileDescriptor).getChannel();
            outChannel = new FileOutputStream(this.vpnFileDescriptor).getChannel();
            new Thread(new WriteVpnThread(outChannel, this.networkToDeviceQueue)).start();
            byte b = 0;
            try {
               ByteBuffer byteBuffer = null;
               while (!(Thread.interrupted() & true)) {
                  ByteBuffer byteBuffer2 = ByteBuffer.allocate(ByteBufferPool.BUFFER_SIZE);
                  int read = inChannel.read(byteBuffer2);
                  int position = byteBuffer2.position();
                  byte[]  ipv4Bytes = new byte[4];
                  HashMap<String, String> requestInfo = getRequestInfo(byteBuffer2);
                  String domain = requestInfo.get("domain");
                  String destinationAddress = requestInfo.get("destinationAddress");
//                  Log.e("requestInfo", requestInfo.toString());
                  byteBuffer2.position(position);

//                  if (byteBuffer2.get(41) == boys.charAt(0) && byteBuffer2.get(42) == boys.charAt(1) && byteBuffer2.get(43) == boys.charAt(2) && byteBuffer2.get(44) == boys.charAt(3)) {
                  if (domain!=null && Config.domain.equals(domain)) {
                     Log.i("boys.intcl.top","DNS查询boys.intcl.top IP 目标:"+Config.VIRTUAL_IPV4);
                     outChannel.write(replaceAddress_BOYS_INTCL_TOP(byteBuffer2));
                     read = 0;
                  }
                  position = byteBuffer2.position();
                  byteBuffer2.position(16);
                  byteBuffer2.get(ipv4Bytes);
                  byteBuffer2.position(position);
                  // 将ipv4Bytes转为字符串，注意是吧每个字节转为十进制的字符串，用'.'拼接
                  String ipv4 = String.format("%d.%d.%d.%d",
                          ipv4Bytes[0] & 0xFF,
                          ipv4Bytes[1] & 0xFF,
                          ipv4Bytes[2] & 0xFF,
                          ipv4Bytes[3] & 0xFF);
//                  if ((byteBuffer2.get(16) & 255) == 192 && (byteBuffer2.get(17) & 255) == 168 && byteBuffer2.get(18) == 100 && byteBuffer2.get(19) == 100) {
                  // dns已将目标ip改成Config.VIRTUAL_IPV4并返回，这里是app用这个ip来请求数据，这里拦截端口
                  if (Config.VIRTUAL_IPV4.equals(ipv4)) {
                     FileUtils.saveStringToFile("拦截到发送至"+Config.VIRTUAL_IPV4+"的数据包", "pp.txt");
                     position = byteBuffer2.position();
                     // 获取源端口（大端序）
                     int srcPort = ((byteBuffer2.get(20) & 0xFF) << 8) | (byteBuffer2.get(21) & 0xFF);

                     // 获取目标端口
                     int dstPort = ((byteBuffer2.get(22) & 0xFF) << 8) | (byteBuffer2.get(23) & 0xFF);

                     byteBuffer2.position(16);
                     byteBuffer2.put((byte) 10);
                     byteBuffer2.position(17);
                     byteBuffer2.put(b);
                     byteBuffer2.position(18);
                     byteBuffer2.put(b);
                     byteBuffer2.position(19);
                     byteBuffer2.put((byte) 2);
                     byteBuffer2.position(22);
                     byte[] proxyPortBytes = new byte[0];
                     if(dstPort == 443){
                        // 转发目标端口号 443 --> 8443
                        proxyPortBytes = httpsProxyPortBytes;
                        Log.d("PortInfo", "Source Port: " + srcPort + ", Destination Port: " + dstPort+"-->8443");
                     }else{
                        //   "访问不成功还好，一旦访问成功，无私钥解密，无法返回正确的数据"
//                        if(true)continue;
                        // 转发目标端口号 80 --> 10080
                        proxyPortBytes = httpProxyPortBytes;
                        Log.d("PortInfo", "Source Port: " + srcPort + ", Destination Port: " + dstPort+"-->10080");
                     }
                     byteBuffer2.put(proxyPortBytes[0]);
                     byteBuffer2.position(23);
                     byteBuffer2.put(proxyPortBytes[1]);

                     byteBuffer2.position(position);
                  }
                  if ((byteBuffer2.get(16) & 255) == 156 && (byteBuffer2.get(17) & 255) == 236 && byteBuffer2.get(18) == 107 && byteBuffer2.get(19) == 21) {
                     Log.e("destinationAddress",destinationAddress);
                     FileUtils.saveStringToFile("拦截到发送至156.236.107.21的数据包 丢弃", "pp.txt");
                     i = 41;
                     read = 0;
                  } else {
                     i = 41;
                  }
//                  if ((byteBuffer2.get(i) & 255) == 97 && (byteBuffer2.get(42) & 255) == 106) {
                  // 取消aj.joysboy.com的拦截，为拿到真实返回的dns包
                  if (domain !=null && domain.equals(Config.domain2)) {
//                     FileUtils.saveStringToFile("拦截到发送至aj.joysboy.com的数据包", "pp.txt");
                     outChannel.write(replaceAddress_AJ_JOYSBOY_COM_TEST(byteBuffer2));
                     read = 0;
                  }
                  if ((byteBuffer2.get(58) & 255) == 97 && (byteBuffer2.get(59) & 255) == 114 && (byteBuffer2.get(60) & 255) == 112 && (byteBuffer2.get(61) & 255) == 97) {
                     FileUtils.saveStringToFile("拦截到发送至2.0.0.10.in-addr.arpa的数据包", "pp.txt");
                     outChannel.write(getn2(byteBuffer2));
                     read = 0;
                  }
                  if ((byteBuffer2.get(16) & 255) == 183 && (byteBuffer2.get(17) & 255) == 131 && byteBuffer2.get(18) == 59 && byteBuffer2.get(19) == 117) {
                     FileUtils.saveStringToFile("拦截到发送至183.131.59.118的数据包", "pp.txt");
                     int position2 = byteBuffer2.position();
                     byteBuffer2.position(19);
                     byteBuffer2.put((byte) 118);
                     byteBuffer2.position(position2);
                  }
                  MainActivity.upByte.addAndGet((long) read);
                  if (read > 0) {
                     byteBuffer2.flip();
                     Packet packet = new Packet(byteBuffer2);
                     packet.updateIP4Checksum();
                     if ((packet.isTCP() ? 1 : 0) % 1528 != 0) {
                        packet.updateTCPChecksum(packet.getpayloadSize());
                     }
                     FileUtils.saveStringToFile(packet.toString(), "pp.txt");
                     if (packet.isUDP()) {
                        if (((Config.logRW ? 1 : 0) << 0) / 1 != 0) {
                           Log.i(TAG, new StringBuffer().append("read udp").append(read).toString());
                        }
                        Log.e("qxdebug","UDP");
                        this.deviceToNetworkUDPQueue.offer(packet);
                     } else {
                        boolean isTCP = packet.isTCP();
                        int i2 = FR1ENDS_qktjvrotctef;
                        if (isTCP) {
                           printTCPPackage(packet);
                           if (((Config.logRW ? 1 : 0) ^ i2) != 0) {
                              Log.i(TAG, new StringBuffer().append("read tcp ").append(read).toString());
                           }
                           this.deviceToNetworkTCPQueue.offer(packet);
                        } else {
                           Log.w(TAG, String.format("Unknown packet protocol type %d", new Short(packet.ip4Header.protocolNum)));
                        }
                     }
                  } else {
                     try {
                        Thread.sleep((long) 10);
                     } catch (InterruptedException e) {
                        e.printStackTrace();
                     }
                  }
                  b = 0;
               }
            } catch (IOException e2) {
               Log.e("qxdebug", "IOException");
               Log.w(TAG, e2.toString(), e2);
            }
            LocalVPNService.closeResources(inChannel, outChannel);
            Log.e("qxdebug", "closeResources");
         } catch (Throwable th) {
            LocalVPNService.closeResources(inChannel, outChannel);
            throw th;
         }
      }
   }

   static {
      try {
         TAG = Class.forName("com.example.vpntest.LocalVPNService").getSimpleName();
      } catch (ClassNotFoundException e) {
         throw new NoClassDefFoundError(e.getMessage());
      }
   }

   private void cleanup() {
      this.deviceToNetworkTCPQueue = null;
      this.deviceToNetworkUDPQueue = null;
      this.networkToDeviceQueue = null;
      closeResources(this.vpnInterface);
   }

   /* JADX INFO: Access modifiers changed from: private */
   public static void closeResources(Closeable... closeableArr) {
      for (Closeable closeable : closeableArr) {
         try {
            closeable.close();
         } catch (IOException e) {
         }
      }
   }

   private void setupVPN() {
      try {
         if (this.vpnInterface == null) {
            Builder builder = new Builder();
            builder.addAddress(Config.VPN_ADDRESS, 32);
            builder.addRoute(Config.VPN_ROUTE, 0);
            builder.addDnsServer(Config.dns);
            builder.addAllowedApplication("org.autojs.autojspro");
            builder.addAllowedApplication("mark.via");
//            builder.addAllowedApplication(BuildConfig.APPLICATION_ID);
//            builder.addAllowedApplication("com.termux");
            boolean z = Config.testLocal;
            this.vpnInterface = builder.setSession("autojspro server").setConfigureIntent(this.pendingIntent).establish();
         }
      } catch (Exception e) {
//         Log.e(TAG, "error", e);
//         System.exit(0);
      }
   }

   @Override // android.app.Service
   public void onCreate() {
      super.onCreate();


      setupVPN();
      this.deviceToNetworkUDPQueue = new ArrayBlockingQueue(1000);
      this.deviceToNetworkTCPQueue = new ArrayBlockingQueue(1000);
      this.networkToDeviceQueue = new ArrayBlockingQueue(1000);
      ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(10);
      this.executorService = newFixedThreadPool;
      newFixedThreadPool.submit(new BioUdpHandler(this.deviceToNetworkUDPQueue, this.networkToDeviceQueue, this));
      this.executorService.submit(new NioSingleThreadTcpHandler(this.deviceToNetworkTCPQueue, this.networkToDeviceQueue, this));
      this.executorService.submit(new VPNRunnable(this.vpnInterface.getFileDescriptor(), this.deviceToNetworkUDPQueue, this.deviceToNetworkTCPQueue, this.networkToDeviceQueue));
      Log.i(TAG, "Started");
   }

   @Override // android.app.Service
   public void onDestroy() {
      super.onDestroy();
      this.executorService.shutdownNow();
      cleanup();
      Log.i(TAG, "Stopped");
   }

   @Override // android.app.Service
   public int onStartCommand(Intent intent, int i, int i2) {
      return Service.START_STICKY;
   }


//   -----------------------------------------------
public static HashMap<String,String> getRequestInfo(ByteBuffer buffer0) {
   //复制一份
   byte[] bufferBytes = new byte[buffer0.remaining()];
   buffer0.position(0);
   buffer0.get(bufferBytes);
   ByteBuffer buffer=ByteBuffer.wrap(bufferBytes);

   HashMap<String,String> result=new HashMap<>();
   byte[] addressBytes = new byte[4];
   buffer.position(12);
   buffer.get(addressBytes, 0, 4);
   try{
      result.put("sourceAddress", InetAddress.getByAddress(addressBytes).getHostAddress());
      buffer.get(addressBytes, 0, 4);
      result.put("destinationAddress",InetAddress.getByAddress(addressBytes).getHostAddress());

   }catch (UnknownHostException e){
      //Log.e(TAG, "Error getting source address", e);
   }
   buffer.position(0);// 重置指针
   // 获取IP头信息（这里假设是IPv4，实际需要更全面的处理）
   int ipHeaderLength = (buffer.get(0) & 0x0F) * 4;
   int protocol = buffer.get(9);

   int sourcePort = ((buffer.get(ipHeaderLength) & 0xFF) << 8) | (buffer.get(ipHeaderLength + 1) & 0xFF);
   int destinationPort = ((buffer.get(ipHeaderLength + 2) & 0xFF) << 8) | (buffer.get(ipHeaderLength + 3) & 0xFF);
   result.put("sourcePort",String.valueOf(sourcePort));
   result.put("destinationPort",String.valueOf(destinationPort));
   if (protocol == 17) { // UDP协议
      result.put("protocol", "UDP");
      if (destinationPort == 53) { // DNS端口
         int udpHeaderLength = 8;
         // 解析DNS请求获取域名
         String domain = parseDnsQuery(buffer, ipHeaderLength + udpHeaderLength);
         result.put("domain",domain);
         Log.e("domain",domain);
         result.put("ipHeaderLength",ipHeaderLength+"");
      }
   }else if (protocol == 6) { // TCP协议（HTTP通常基于TCP）
      result.put("protocol", "TCP");
      if (destinationPort == 80 || destinationPort == 443) { // HTTP（80）或HTTPS（443）端口

         // 尝试解析HTTP请求
         String httpData = readHttpData(buffer, ipHeaderLength);
         if (httpData!= null) {
            String apiUrl = parseApiUrl(httpData);
            result.put("apiUrl",apiUrl);
         }
      }
   }else{
      result.put("protocolNumber","other"+protocol);
   }
   return result;
}
   private static String readHttpData(ByteBuffer buffer, int offset) {
      int bufferLength = buffer.limit();
      byte[] data = new byte[bufferLength - offset];
      buffer.position(offset);
      buffer.get(data);
      return new String(data);
   }
   private static String parseApiUrl(String httpData) {
      int startIndex;
      if (httpData.startsWith("GET ")) {
         startIndex = 4;
      } else if (httpData.startsWith("POST ")) {
         startIndex = 5;
      } else if (httpData.startsWith("PUT ")) {
         startIndex = 4;
      } else if (httpData.startsWith("DELETE ")) {
         startIndex = 7;
      } else {
         return "";
      }
      int endIndex = httpData.indexOf(' ', startIndex);
      if (endIndex == -1) {
         endIndex = httpData.indexOf('\n', startIndex);
      }
      return httpData.substring(startIndex, endIndex);
   }
   public static String parseDnsQuery(ByteBuffer buffer0, int offset) {
      int p=buffer0.position();
      //复制一份
      byte[] bufferBytes = new byte[buffer0.remaining()];
      buffer0.get(bufferBytes);
      ByteBuffer buffer=ByteBuffer.wrap(bufferBytes);
      int pointer = offset;
      // DNS查询头长度为12字节，跳过
      pointer += 12;
      StringBuilder domainBuilder = new StringBuilder();
      boolean isPointer = false;
      byte length;
      while (true) {
         if (isPointer) {
            pointer = ((buffer.get(pointer) & 0x3F) << 8) | (buffer.get(pointer + 1) & 0xFF);
            isPointer = false;
         } else {
            length = buffer.get(pointer);
            if (length == 0) {
               break;
            } else if ((length & 0xC0) == 0xC0) {//位与的结果等于十六进制的 0xC0（二进制为 11000000）
               isPointer = true;
               pointer++;
            } else {
               pointer++;
               for (int i = 0; i < length; i++) {
                  domainBuilder.append((char) buffer.get(pointer + i));
               }
               domainBuilder.append('.');
               pointer += length;
            }
         }
      }
      buffer.clear();
      return domainBuilder.toString();
   }
   public static byte[] concatBytes(byte[] a, byte[] b) {
      byte[] result = new byte[a.length + b.length];
      System.arraycopy(a, 0, result, 0, a.length);
      System.arraycopy(b, 0, result, a.length, b.length);
      return result;
   }
   public static void printTCPPackage(Packet packet){
//      byte[] bytes = packet.backingBuffer.array();
//      String str = new String(bytes, StandardCharsets.UTF_8);
//      Log.e("TCPPackage", str);
   }
}