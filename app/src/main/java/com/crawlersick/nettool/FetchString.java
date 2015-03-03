package com.crawlersick.nettool;

/**
 * Created by sadpanda on 1/31/15.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sadpanda
 */



        import java.io.IOException;




        import java.util.ArrayList;


        import java.util.logging.Logger;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

public class FetchString {
    private static final Logger log = Logger.getLogger(FetchString.class.getName());


    String inputstr;

    StringBuilder s;

    Pattern pgp;
    String conencode;
    String regxstr;
    int returncode=0;
    public int getReturncode() {
        return returncode;
    }

    public FetchString(String str,String regxstr) throws IOException{
        this.inputstr=str;
        this.regxstr=regxstr;
        this.s=new StringBuilder(str);
    }

    public String getRegxstr() {
        return regxstr;
    }

    public void setRegxstr(String regxstr) {
        this.regxstr = regxstr;
    }

    public String getconencode(){
        return conencode;
    }




    public String GetContent()
    {
        return s.toString();
    }


    //regex
    public static Pattern setPt(String p)
    {
        Pattern pt=null;
        try{
            pt=Pattern.compile(p,Pattern.CASE_INSENSITIVE);
            return pt;
        }catch(Exception e){
            return null;
        }
    }

    public static String[] matchPt(String s,Pattern pt)
    {
        if(pt==null) return null;
        String[] result=null;
        Matcher m=pt.matcher(s);
        boolean b=m.find();
        if(b)
        {
            int lgt=m.groupCount();
            result=new String[lgt+1];
            for(int i=0;i<=lgt;i++)
            {
                result[i]=m.group(i);
            }
        }
        return result;
    }

    public static ArrayList<String> matchPtloop(String s,Pattern pt)
    {
        ArrayList<String> indexarr=new ArrayList<String>();
        if(pt==null) return null;
        Matcher m=pt.matcher(s);
        boolean b=m.find();
        while(b)
        {

            indexarr.add(m.group(1));
            b=m.find();
        }

        return indexarr;
    }




    public static ArrayList<String>  getmatchstr(String str,String reg)
    {
        Pattern pgps=setPt(reg);
        ArrayList<String> res= matchPtloop(str,pgps);
        return res;
    }




}

