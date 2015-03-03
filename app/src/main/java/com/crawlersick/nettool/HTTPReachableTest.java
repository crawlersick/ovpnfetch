package com.crawlersick.nettool;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.net.InetAddress;
        import java.net.Socket;
        import java.net.UnknownHostException;
        import java.util.logging.Level;
        import java.util.logging.Logger;

/**
 *
 Jan 19, 2015 10:54:34 PM mail2ovpn.GetHostFromMail fetchmailforhost
 INFO: got url: http://77.78.12.91:40961/
 Jan 19, 2015 10:54:34 PM mail2ovpn.GetHostFromMail fetchmailforhost
 INFO: got url: http://218.185.130.65.eo.eaccess.ne.jp:5288/
 Jan 19, 2015 10:54:34 PM mail2ovpn.GetHostFromMail fetchmailforhost
 INFO: got url: http://222.97.89.198:48877/
 Jan 19, 2015 10:54:34 PM mail2ovpn.GetHostFromMail fetchmailforhost
 INFO: got url: http://121.161.229.77:56121/
 Jan 19, 2015 10:54:34 PM mail2ovpn.GetHostFromMail fetchmailforhost
 INFO: got url: http://ZP229038.ppp.dion.ne.jp:42097/
 */
public class HTTPReachableTest {
    private static final Logger log = Logger.getLogger(HTTPReachableTest.class.getName());
    String     headerstr="";
    String host;
    int port;
    public HTTPReachableTest(String host,int port){this.host=host;this.port=port;
        headerstr="GET /"+" HTTP/1.1"+"\n"
                +"Host: "+host+"\n"
                +"Connection: close"+"\n\n";
    }
    public boolean doTest()
    {
        boolean result=false;
        try {
            Socket ss=new Socket(host,port);
            ss.setSoTimeout(9000);
            OutputStream os=ss.getOutputStream();
            InputStream is=ss.getInputStream();
            os.write(headerstr.getBytes());
            is=ss.getInputStream();
            int x;
            byte[] header=new byte[1024];
            int pos=0;
            while((x=is.read())!=-1&&pos<1024)
            {
                header[pos]=(byte) x;

                if(pos>3&&header[pos-3] == 0X0D&&header[pos-2] == 0X0A
                        &&header[pos-1] == 0X0D&&header[pos] == 0X0A)
                    break;

                pos++;
            }
            String headerstring=new String(header,0,pos);
            log.log(Level.INFO,"'header got: "+headerstring);

            os.close();
            is.close();
            ss.close();
            result=true;
        } catch (IOException ex) {
            Logger.getLogger(HTTPReachableTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;

    }

    public static void main(String args[]) {
        HTTPReachableTest rt=new HTTPReachableTest("77.78.12.91",40961);
        rt.doTest();
    }
}
