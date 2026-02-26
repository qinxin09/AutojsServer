package com.example.vpntest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
public class IpPacketParser {

   /* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
   public static class DnsPacket {
      public int answerCount;
      public List<String> answers = new ArrayList();
      public boolean isResponse;
      public int transactionId;
   }

   /* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
   public static class IpHeader {
      public String destIp;
      public int headerLength;
      public int protocol;
      public String sourceIp;
      public int totalLength;
      public int version;
   }

   /* loaded from: C:\Users\qx\Documents\AnLink\Files\classes.dex */
   public static class UdpHeader {
      public int destPort;
      public int length;
      public int sourcePort;
   }

   private static String bytesToIp(byte[] bArr) {
      return String.format("%d.%d.%d.%d", new Integer(bArr[0] & 255), new Integer(bArr[1] & 255), new Integer(bArr[2] & 255), new Integer(bArr[3] & 255));
   }

   public static int getipHeaderLength(ByteBuffer byteBuffer) {
      return (byteBuffer.get(0) & 255 & 15) * 4;
   }

   public static int getudpHeaderLength(ByteBuffer byteBuffer) {
      int i = getipHeaderLength(byteBuffer);
      return (byteBuffer.get(i + 5) & 255) | ((byteBuffer.get(i + 4) & 255) << 8);
   }

   public static DnsPacket parseDnsPacket(ByteBuffer byteBuffer) {
      DnsPacket dnsPacket = new DnsPacket();
      int i = getipHeaderLength(byteBuffer) + getudpHeaderLength(byteBuffer);
      dnsPacket.transactionId = ((byteBuffer.get(i) & 255) << 8) | (byteBuffer.get(i + 1) & 255);
      dnsPacket.isResponse = (((((byteBuffer.get(i + 2) & 255) << 8) | (byteBuffer.get(i + 3) & 255)) & 32768) != 0) & true;
      dnsPacket.answerCount = ((byteBuffer.get(i + 6) & 255) << 8) | (byteBuffer.get(i + 7) & 255);
      int skipDnsName = skipDnsName(byteBuffer, i + 12) + 4;
      for (int i2 = 0; i2 < dnsPacket.answerCount; i2++) {
         int skipDnsName2 = skipDnsName(byteBuffer, skipDnsName);
         int i3 = ((byteBuffer.get(skipDnsName2) & 255) << 8) | (byteBuffer.get(skipDnsName2 + 1) & 255);
         int i4 = skipDnsName2 + 8;
         int i5 = ((byteBuffer.get(i4) & 255) << 8) | (byteBuffer.get(i4 + 1) & 255);
         int i6 = i4 + 2;
         if (i3 == 1) {
            byte[] bArr = new byte[4];
            for (int i7 = 0; i7 < 4; i7++) {
               bArr[i7] = byteBuffer.get(i6 + i7);
            }
            dnsPacket.answers.add(bytesToIp(bArr));
            skipDnsName = i6 + 4;
         } else {
            if (i3 == 5) {
               dnsPacket.answers.add(readDnsName(byteBuffer, i6));
            }
            skipDnsName = i6 + i5;
         }
      }
      return dnsPacket;
   }

   public static IpHeader parseIpHeader(ByteBuffer byteBuffer) {
      IpHeader ipHeader = new IpHeader();
      byteBuffer.order(ByteOrder.BIG_ENDIAN);
      int i = byteBuffer.get(0) & 255;
      ipHeader.version = (i >> 4) & 15;
      ipHeader.headerLength = (i & 15) * 4;
      ipHeader.totalLength = ((byteBuffer.get(2) & 255) << 8) | (byteBuffer.get(3) & 255);
      ipHeader.protocol = byteBuffer.get(9) & 255;
      byte[] bArr = new byte[4];
      for (int i2 = 0; i2 < 4; i2++) {
         bArr[i2] = byteBuffer.get(12 + i2);
      }
      ipHeader.sourceIp = bytesToIp(bArr);
      byte[] bArr2 = new byte[4];
      for (int i3 = 0; i3 < 4; i3++) {
         bArr2[i3] = byteBuffer.get(16 + i3);
      }
      ipHeader.destIp = bytesToIp(bArr2);
      return ipHeader;
   }

   public static UdpHeader parseUdpHeader(ByteBuffer byteBuffer) {
      int i = getipHeaderLength(byteBuffer);
      UdpHeader udpHeader = new UdpHeader();
      udpHeader.sourcePort = ((byteBuffer.get(i) & 255) << 8) | (byteBuffer.get(i + 1) & 255);
      udpHeader.destPort = ((byteBuffer.get(i + 2) & 255) << 8) | (byteBuffer.get(i + 3) & 255);
      udpHeader.length = (byteBuffer.get(i + 5) & 255) | ((byteBuffer.get(i + 4) & 255) << 8);
      return udpHeader;
   }

   private static String readDnsName(ByteBuffer byteBuffer, int i) {
      StringBuilder sb = new StringBuilder();
      while (true) {
         int i2 = byteBuffer.get(i) & 255;
         if (i2 == 0) {
            break;
         } else if ((i2 & 192) == 192) {
            sb.append(readDnsName(byteBuffer, (byteBuffer.get(i + 1) & 255) | ((i2 & 63) << 8)));
            break;
         } else {
            byte[] bArr = new byte[i2];
            for (int i3 = 0; i3 < i2; i3++) {
               bArr[i3] = byteBuffer.get(i + 1 + i3);
            }
            sb.append(new String(bArr)).append('.');
            i += 1 + i2;
         }
      }
      return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
   }

   private static int skipDnsName(ByteBuffer byteBuffer, int i) {
      while (true) {
         int i2 = byteBuffer.get(i) & 255;
         if (i2 == 0) {
            return i;
         }
         if ((i2 & 192) == 192) {
            return i + 2;
         }
         i += 1 + i2;
      }
   }
}