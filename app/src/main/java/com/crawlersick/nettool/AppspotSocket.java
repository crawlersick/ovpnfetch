package com.crawlersick.nettool;

/**
 * Created by sick on 7/19/14.
 */

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import com.crawlersick.ovpnfetcher.MyIntentService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author sick
 */
public class AppspotSocket {
    Logger logger = Logger.getLogger(AppspotSocket.class.getName());
    FileHandler fh;
    String webhost;
    String appid;
    private int hostport;
    SSLSocketFactory sslsocketfactory;
    private SSLSocket sock;
    String headerstr;
    String ipadrs[];
    private String Errormsg;
    private OutputStream ost;
    private InputStream ist;
    private byte b[]=new byte[1024];
    ByteBuffer bbuf = ByteBuffer.allocate(2000000);
    private int progress=0;

    public int getProgress(){return progress;}

    public AppspotSocket(String appid,String logfilefolder,Intent localIntent,MyIntentService myis) throws Exception
    {
        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler(logfilefolder+"LogFile.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            logger.info("log:");

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("--------------------");



        this.appid=appid;
        this.webhost=appid+".appspot.com";
        this.hostport=443;

        sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        DNSQ dq=new DNSQ();

        String googlelist[]={
                //   "www.google.com.hk","www.google.com.tw","www.google.com.sg","www.google.co.jp","www.google.sg","www.google.cat","www.google.jp",
                //"google.io","google.com.my","google.com.pr","google.sk","google.st",
                "google.de","google.cz","google.ee","google.gf","google.gp","google.hn"

                // "www.google.sg","www.google.cat","www.google.co.jp","google.st",  "google.io","google.com.my","www.google.com.tw"
        };

    /*
     *
173.194.127.18
74.125.128.94
173.194.127.55
74.125.128.94
173.194.127.255
173.194.127.31
173.194.127.215
74.125.128.94
173.194.127.243
173.194.127.55
74.125.128.94
     *
     */

        int effecnt=0;
        String effelist[]=new String[googlelist.length];
        String dnsserverips[]={"114.114.114.114","114.114.115.115","8.8.8.8","8.8.4.4"};
        int ipsidx=0;
        while(effecnt==0){
            for(int i=0;i<googlelist.length;i++)
            {
                try{

                    String tempresult=dq.Getip(dnsserverips[(ipsidx % (dnsserverips.length-1))], googlelist[i]);
                    String tempresultlist[] =tempresult.split("\\|");
                    //effelist[effecnt]=tempresultlist[tempresultlist.length-1];
                    effelist[effecnt]=tempresultlist[0];
                    effecnt++;
                }catch(Exception e)
                {
                    System.out.println(e);
                }
            }
            ipsidx++;
        }

        progress=10;
        localIntent.putExtra("213123", "Got server address. processing....");
        LocalBroadcastManager.getInstance(myis).sendBroadcast(localIntent);


        // System.out.println("------------");
        for(int i=0;i<effecnt;i++)
        {
            logger.info(effelist[i]);
            //System.out.println(effelist[i]);
        }
        //  System.out.println("------------");
    /*
    String tempresult=dq.Getip("114.114.114.114", "www.google.com.cat");
    ipadrs= tempresult.split("\\|");
    if (ipadrs==null )
    {
        Errormsg= "Error: not DNS IP found!";
    }

          */



//System.out.println(effelist[3]);
//System.out.println(webhost);
        //sock = (SSLSocket) sslsocketfactory.createSocket();
        // sock = (SSLSocket) sslsocketfactory.createSocket(ipadrs[0],hostport);
        SocketAddress socketAddress ;//= new InetSocketAddress(effelist[3],hostport);
        int rip=0;
        boolean loopflag = true;
        int loopcnt=0;
        while(loopflag){

            try{
                rip=(int) (Math.random()*effecnt);
                socketAddress = new InetSocketAddress(effelist[rip],hostport);
                sock = (SSLSocket) sslsocketfactory.createSocket();
                sock.connect(socketAddress,30000);

                loopflag=false;
            }catch (SocketTimeoutException se){
                loopcnt++;
                logger.info("" + rip+" Time out Retry connect : "+ loopcnt);

                localIntent.putExtra("213123", "Server connecting.. "+loopcnt);
               // logger.info(" Time out send before");
                LocalBroadcastManager.getInstance(myis).sendBroadcast(localIntent);
               // logger.info(" Time out send after");
                //System.out.println("" + rip+" Time out Retry connect : "+ loopcnt);

            }
        }
        logger.info("connected!!"+rip +" :: "+effelist[rip]);
        //System.out.println("connected!!"+rip +" :: "+effelist[rip]);
        localIntent.putExtra("213123", "connected!!"+rip +" :: "+effelist[rip] + " , downloading....");
        LocalBroadcastManager.getInstance(myis).sendBroadcast(localIntent);

        progress=30;
        sock.setSoTimeout(100000);
        //System.out.println("2222");
        ost=sock.getOutputStream();
        ist=sock.getInputStream();

        setheader();

    }

    public String GteErrormsg(){return Errormsg;}

    public void setheader()
    {
        headerstr="GET /"+" HTTP/1.1"+"\n"
                +"Host: "+webhost+"\n"
                +"Connection: close"+"\n\n";
    }

    public void setheader(String URLparameter)
    {
        headerstr="GET /"+URLparameter+" HTTP/1.1"+"\n"
                +"Host: "+webhost+"\n"
                +"accept-encoding:gzip,deflate,sdch"+"\n"
                +"user-agent:Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/34.0.1847.116 Chrome/34.0.1847.116 Safari/537.36"+"\n"
                +"Connection: close"+"\n\n";
    }

    public String URLConmunicate(String URLparameter,Intent localIntent,MyIntentService myis) throws Exception
    {
        if (URLparameter==null)
        {setheader();}
        else
        {setheader(URLparameter);}

        ost.write(headerstr.getBytes());
        ost.flush();
        int n = 1;

        boolean firstflag=true;



        byte header[] = new byte[1024];
        int headrcnt=0;
        byte x=0;
        byte x_pre=0;
        byte x_prepre=0;
        byte x_preprepre=0;
        boolean dataloopflag=false;
        int datasize=100000000;
        int datacounter=0;
       // while(x!=-1)
        while(datacounter!=datasize)
        {
            //n = ist.read();
            x = (byte) ist.read();

            if(!dataloopflag)
            {
                header[headrcnt]=x;
                headrcnt++;

                if (x_preprepre == 0X0D&&x_prepre == 0X0A&&x_pre == 0X0D&&x == 0X0A) {
                    Log.i("date header end found! ","!!!!!");

                    dataloopflag=true;

                    //x = (byte) ist.read();
                    //Log.i("next byte! ","!!!!!"+x);

                    String headerstr=new String(header,0,headrcnt);
                    Log.i("header string! ",headerstr);

                    logger.info(headerstr);

                    String strslist[]=headerstr.split("Content-Length: ");
                    String strslist2[]=strslist[1].split("\r\n");
                    datasize=Integer.parseInt(strslist2[0].trim());
                    Log.i("get the number! ",": "+datasize);


                }
                x_preprepre=x_prepre;
                x_prepre = x_pre;
                x_pre = x;
                continue;
            }

            datacounter++;
/*
    if(firstflag)
    {
        if(n==-1)
        {
            n = ist.read(b);
            firstflag=false;
        }
    }
 */
//
//    ist.wait(100);
//    bbuf.put(b);
            //break;

            //System.out.print(new String(b));
            // System.out.println("read bytes: "+bbuf.position());
            bbuf.put(x);


        }
        //  System.out.println("read bytes: "+bbuf.position());

        progress=70;


        bbuf.flip();
        byte bb[]=new byte[bbuf.limit()];
        //bbuf.position(0);
        bbuf.get(bb,0,bb.length);

        Log.i("data size ! ", ".. " + bbuf.position() );
        logger.info("data size :" + bbuf.position() +"bytes");
        localIntent.putExtra("213123", "Downloaded data size: " + bbuf.position() +"bytes");
        LocalBroadcastManager.getInstance(myis).sendBroadcast(localIntent);

        return decompress(bb);
        //return new String(bb);


    }

    public void closeappsocket() throws IOException{
        ist.close();
        ost.close();
        sock.close();

    }

    public void resultAnalyst(String restr,int delaynum,int speednum,String targetoutputfolder) throws IOException
    {

        String []tempgetudplist=restr.split("sickjohnsisick1122356l112355iaaaoss");

        //     System.out.println(tempgetudplist[1]);

        String []tempstrs=tempgetudplist[0].split("\\r\\n");





        for(int i=10;i<tempstrs.length;i++)
        {

            String []tempstrsxxxx=tempstrs[i].split(",");
            //vpn539246233|182.216.181.220|508611|35|41230804|Korea Republic of|KR|13|
            //#HostName|IP|Score|Ping|Speed|CountryLong|CountryShort|NumVpnSessions|Uptime|TotalUsers|TotalTraffic|LogType|Operator|Message|OpenVPN_ConfigData_Base64|\

            if(tempstrsxxxx.length>14)
            {
                //Base64 decoder = new Base64();

                byte[] decodedBytes = Base64.decode(tempstrsxxxx[14],Base64.DEFAULT);
                tempstrsxxxx[14]=new String(decodedBytes,"UTF-8");
                tempstrsxxxx[14]=tempstrsxxxx[14].replaceAll("#.+?\r\n", "");

                // System.out.println
                logger.info(tempstrsxxxx[0]+"|"+tempstrsxxxx[1]+"|"+tempstrsxxxx[2]+
                                "|"+tempstrsxxxx[3]+"|"+tempstrsxxxx[4]+"|"+tempstrsxxxx[5]+"|"+tempstrsxxxx[6]
                                +"|"+tempstrsxxxx[7]+"|"+tempstrsxxxx[8]+"|"+tempstrsxxxx[9]+"|"+tempstrsxxxx[10]+"|"
                                +tempstrsxxxx[11]+"|"+tempstrsxxxx[12]+"|"+tempstrsxxxx[13]+"|"
                        // +udplist.get(tempudpportnum+1)+"|" //+tempstrsxxxx[14]
                );


                if ( isNumericInt(tempstrsxxxx[7])&&isNumericInt(tempstrsxxxx[3])&&isNumericInt(tempstrsxxxx[4])&&
                        //tempstrsxxxx[14].indexOf("proto udp")!=-1 &&
                        // Integer.valueOf(tempstrsxxxx[7])>0        &&
                        Integer.valueOf(tempstrsxxxx[3])<delaynum
                        &&  Integer.valueOf(tempstrsxxxx[4])>speednum
                        )
                {

                    //  tempgetudplist[1].split(",");
                    List<String> udplist = Arrays.asList(tempgetudplist[1].split(","));

                    int tempudpportnum=udplist.indexOf(tempstrsxxxx[0]);

                    if(tempudpportnum!=-1)
                    {

                        tempstrsxxxx[14]=tempstrsxxxx[14].replace("proto tcp", "proto udp");
                        tempstrsxxxx[14]=tempstrsxxxx[14].replaceFirst("remote [0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+ [0-9]+",
                                "remote "+tempstrsxxxx[1]+" "+udplist.get(tempudpportnum+1));


                        int performrank=Integer.valueOf(tempstrsxxxx[2])/10000;

                        File tempfile=new File(targetoutputfolder+tempstrsxxxx[1]+"_"+tempstrsxxxx[6]+"_udp_"+"Rank"+performrank+".ovpn");
                        FileOutputStream osss =new FileOutputStream(tempfile);
                        osss.write(tempstrsxxxx[14].getBytes("UTF-8"));
                        osss.close();

                        //System.out.println
                        logger.info(tempfile.getAbsoluteFile().toString());
                        tempfile=null;




                    }
                    //+tempstrsxxxx[14]+"|");
                }

            }


        }
        progress=100;
    }


    public static boolean isNumericInt(String str)
    {
        try
        {
            Integer d = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static byte[] compress(String string) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }

    public static String decompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }


}
