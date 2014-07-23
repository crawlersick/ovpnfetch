/*
 * This function is for DNS qurey with a specified DNS server ip and Target Domain
 * input DNS ip and Target Domain as parameter and output a ip list in below fomat:
 * 184.154.128.246|184.154.128.245|184.154.128.244|184.154.128.243
 *  try others if the first ip connect failed.
 */
package com.crawlersick.nettool;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sick
 */
public    class  DNSQ {
    public String Getip( String DNSs,String TgDN) throws Exception{

        System.out.println("start to dnsloopup "+TgDN);

        datasize=adddataend(0,b0,datahead);strarr=TgDN.split("\\.");
        for(String tempstr:strarr)
        {
            x=tempstr.length();
            datasize++;
            b0[datasize-1]=(byte)x;
            datasize=adddataend(datasize,b0,tempstr.getBytes("ASCII"));
        }
        datasize=adddataend(datasize,b0,dataend);
        byte[] tempbyby=ByteBuffer.allocate(4).putInt(datasize-2).array();
        b0[0]=tempbyby[2];b0[1]=tempbyby[3];
        s=new Socket();
        s.connect(new InetSocketAddress(DNSs,53),3800);
        s.setSoTimeout(5000);
        //s=new Socket(DNSs,53);
  /*        
            String newfind= "00 1f 00 00 01 00 00 01 00 00 00 00 00 00 03 77 77 77 05 62 61 69 64 75 03 63 6f 6d 00 00 01 00 01";
            newfind=newfind.replaceAll(" ", "");
            System.out.println(newfind);
            b1=HexCodec.hexToBytes(newfind.toCharArray());
      */      
            /*
           for(int i=0;i<datasize;i++){                    System.out.print(Integer.toHexString(b0[i])+" ");            }
            System.out.println();
            */
        osm=s.getOutputStream();ism=s.getInputStream();
        osm.write(b0,0,datasize);
        //System.out.println("***************************************");
        int n=0;
        int maxn=3000;
        //while ((x=ism.read())!=-1)
        while (true)
        {
            x=ism.read();
            //System.out.print(Integer.toHexString(x)+"~"+x+"|");
            b3[n]=x;
            n++;

       //     System.out.println(n+":  "+x);
            if(n==maxn)
            {break;}
            if(n==2)
            {maxn=x+2;}

        }
        //System.out.println("\n***************************************");
        String returnvalue="";
        for(int i=0;i<n;i++)
        {
            if(b3[i]==192&&b3[i+2]==0&&b3[i+3]==1&&b3[i+4]==0&&b3[i+5]==1)
            {
                if(returnvalue.equals(""))
                    returnvalue=b3[i+12]+"."+b3[i+13]+"."+b3[i+14]+"."+b3[i+15];
                else
                    returnvalue=returnvalue+"|"+b3[i+12]+"."+b3[i+13]+"."+b3[i+14]+"."+b3[i+15];
                // System.out.println(b3[i+12]+"."+b3[i+13]+"."+b3[i+14]+"."+b3[i+15]);
            }
        }
        return returnvalue;
    }

    public static void main(String arg[]){
        try {
            DNSQ dq=new DNSQ();
            String a=
                    //   dq.Getip("8.8.8.8", "www.youtube.com");
                    // dq.Getip("8.8.8.8", "t66y.com");
                    //dq.Getip("114.114.114.114", "google.com");
                    dq.Getip("195.46.39.39", "google.com");
            System.out.println(a);
        } catch (Exception ex) {
            Logger.getLogger(DNSQ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int adddataend(int headnum,byte[]tb,byte[]tb_end) {
        if(tb.length-headnum<tb_end.length)
        {
            System.out.println("data head is too long!");
            return 0;
        }
        int i;
        for(i=headnum;i<headnum+tb_end.length;i++)
            tb[i]=tb_end[i-headnum];
        return i;
    }
    private int datasize;
    private byte[] b0=new byte[1024];
    private byte[] datahead={0x00,0x1f,0x00,0x00,0x01,0x00,0x00,0x01,0x00,0x00,0x00,0x00,0x00,0x00};
    private byte[]dataend={0x00,0x00,0x01,0x00,0x01};
    private String[] strarr;
    private int x;
    private byte[] b1;
    private byte[] b2;
    private int[] b3=new int[3000];
    private String fdata;
    private String DNSserver;
    private String TargetDomainName;
    private String Targetip;
    private Socket s;
    private InputStream ism;
    private OutputStream osm;
}
