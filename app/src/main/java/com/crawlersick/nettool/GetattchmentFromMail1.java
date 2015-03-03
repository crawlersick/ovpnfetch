/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crawlersick.nettool;



import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;

import com.crawlersick.ovpnfetcher.MyIntentService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

/**
 *
 * @author sadpanda
 */
public class GetattchmentFromMail1 {
    private static final Logger log = Logger.getLogger(GetattchmentFromMail1.class.getName());
    private String IMapHost;
    private String MailId;
    private String MailPassword;
    private String ValidVGHost=null;
    int totalmailcount=0;
    String fromadd="";
    String sentdate;
    String Content;
    String subject;
    String logfilefolder;
    byte validvgbinary[];
    Intent localIntent;
    MyIntentService myis;
    FileHandler fh;
    public GetattchmentFromMail1(String imap,String mid,String mpd, String logfilefolder,Intent localIntent,MyIntentService myis) throws IOException {
        this.logfilefolder=logfilefolder;
        this.localIntent=localIntent;
        this.myis=myis;
        this.IMapHost=imap;
        this.MailId=mid;
        this.MailPassword=mpd;
        fh = new FileHandler(logfilefolder+"LogFile.log");
        log.addHandler(fh);
    }

    public byte[]  getValidVGattch(){
        return validvgbinary;
    }

    public boolean fetchmailforattch() throws IOException, MessagingException
    {
        boolean fetchtest=false;

        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();
            store.connect(IMapHost, MailId, MailPassword);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            totalmailcount=inbox.getMessageCount();

            Message msg =null;
            for(int i = totalmailcount;i>0;i--)
            {
                fromadd="";
                msg = inbox.getMessage(i);
                Address[] in = msg.getFrom();
                for (Address address : in) {
                    fromadd=address.toString()+fromadd;
                    //System.out.println("FROM:" + address.toString());
                }
                if(fromadd.matches("admin@cronmailservice.appspotmail.com")&&
                        msg.getSubject().matches
                                ("ThanksToTsukuba_World-on-my-shoulders-as-I-run-back-to-this-8-Mile-Road_cronmailservice"))
                    break;
            }

            if(fromadd.equals("'")){
                log.log(Level.SEVERE,"Error: no related mail found!" + this.MailId);
                return fetchtest;
            }




            //    Multipart mp = (Multipart) msg.getContent();
            //  BodyPart bp = mp.getBodyPart(0);
            sentdate=msg.getSentDate().toString();

            subject=msg.getSubject();

            Content=msg.getContent().toString();


            log.log(Level.INFO,Content);
            log.log(Level.INFO,sentdate);
            localIntent.putExtra("213123", "Got Server latest update at : "+sentdate+" , Reading the Data...");
            LocalBroadcastManager.getInstance(myis).sendBroadcast(localIntent);


            Multipart multipart = (Multipart) msg.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
                        (bodyPart.getFileName()==null||!bodyPart.getFileName().equals("dataforvgendwithudp.gzip"))
                        ) {
                    continue; // dealing with attachments only
                }
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                InputStream is = bodyPart.getInputStream();
                //validvgbinary = IOUtils.toByteArray(is);
                int nRead;
                byte[] data = new byte[5000000];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();

                validvgbinary= buffer.toByteArray();
                break;
            }













            fetchtest=true;
        } catch (Exception mex) {
            mex.printStackTrace();

        }

        return fetchtest;
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

                byte[] decodedBytes = Base64.decode(tempstrsxxxx[14], Base64.DEFAULT);
                tempstrsxxxx[14]=new String(decodedBytes,"UTF-8");
                tempstrsxxxx[14]=tempstrsxxxx[14].replaceAll("#.+?\r\n", "");

                // System.out.println
                log.info(tempstrsxxxx[0]+"|"+tempstrsxxxx[1]+"|"+tempstrsxxxx[2]+
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
                        log.info(tempfile.getAbsoluteFile().toString());
                        tempfile=null;




                    }
                    //+tempstrsxxxx[14]+"|");
                }

            }


        }

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
}
