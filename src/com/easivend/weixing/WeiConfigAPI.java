package com.easivend.weixing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;
import com.easivend.common.ToolClass;

public class WeiConfigAPI 
{
	//����΢���˺�
    public static void SetWeiConfig(Map<String, String> list) 
    {
    	String str=null;
    	//weixing
    	str=list.get("weiappid");    	
    	WeiConfig.setWeiappid(str);
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<weiappid="+WeiConfig.getWeiappid(),"log.txt");
    	
    	str=list.get("weimch_id");
    	WeiConfig.setWeimch_id(str);
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<weimch_id="+WeiConfig.getWeimch_id(),"log.txt");
    	
    	str=list.get("weikey");
    	WeiConfig.setWeikey(str);
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<weikey="+WeiConfig.getWeikey(),"log.txt");

    	str=list.get("weisubmch_id");
    	WeiConfig.setWeisubmch_id(str);
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<weisubmch_id="+WeiConfig.getWeisubmch_id(),"log.txt");
    	
    	str=list.get("isweisub");
        if(str.equals("")==false) {
            WeiConfig.setIsweisub(Float.parseFloat(str));
            ToolClass.Log(ToolClass.INFO, "EV_JNI", "APP<<isweisub=" + WeiConfig.getIsweisub(), "log.txt");
        }

//    	str=list.get("weicert_pwd");
//    	WeiConfig.setWeicert_pwd(str);
//    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<weicert_pwd="+WeiConfig.getWeicert_pwd(),"log.txt");
    }
    //������Ϣ
    public static String sendPost(String urlString,Map<String, String> list) 
    		throws Exception
    {
    	String content=null;
    	//�����ϳɷ�����Ϣ
    	StringBuilder xml = new StringBuilder();
	     xml.append("<xml>");
//	     xml.append("<appid>"+list.get("appid")+"</appid>");
//	     xml.append("<mch_id>"+list.get("mch_id")+"</mch_id>");
//	     xml.append("<nonce_str>"+list.get("nonce_str")+"</nonce_str>");
//	     xml.append("<body><![CDATA["+list.get("body")+"]]></body>");
//	     xml.append("<out_trade_no>"+list.get("out_trade_no")+"</out_trade_no>");
//	     xml.append("<total_fee>"+list.get("total_fee")+"</total_fee>");
//	     xml.append("<spbill_create_ip>"+list.get("spbill_create_ip")+"</spbill_create_ip>");	     
//	     xml.append("<time_start>"+list.get("time_start")+"</time_start>");
//	     xml.append("<time_expire>"+list.get("time_expire")+"</time_expire>");
//	     xml.append("<notify_url>"+list.get("notify_url")+"</notify_url>");	     
//	     xml.append("<trade_type>"+list.get("trade_type")+"</trade_type>");
//	     xml.append("<product_id>"+list.get("product_id")+"</product_id>");
//	     xml.append("<sign><![CDATA["+list.get("sign")+"]]></sign>");
	    //��������
	 	Set<Map.Entry<String,String>> allset=list.entrySet();  //ʵ����
	     Iterator<Map.Entry<String,String>> iter=allset.iterator();
	     while(iter.hasNext())
	     {
	         Map.Entry<String,String> me=iter.next();
	         xml.append("<"+me.getKey()+"><![CDATA["+me.getValue()+"]]></"+me.getKey()+">");
	     }  
	     xml.append("</xml>");	
	     Log.i("EV_JNI","Send2="+xml.toString());
	     
	     //4.������Ϣ		
        byte[] xmlbyte = xml.toString().getBytes("UTF-8");        
        //Log.i("EV_JNI","Send5="+xml);
        URL url = new URL(urlString);    
        HttpsURLConnection  conn = (HttpsURLConnection) url.openConnection();
		if(ToolClass.getSsl()!=null)
		{
			conn.setSSLSocketFactory(ToolClass.getSsl());
		}
        conn.setConnectTimeout(9000);//��������������ʱ����λ�����룩
        conn.setReadTimeout(3000);//���ô�������ȡ���ݳ�ʱ����λ�����룩
        conn.setDoOutput(true);// �������
        conn.setDoInput(true);// ��������
        conn.setUseCaches(false);// ��ʹ�û���
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");// ά�ֳ�����
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Content-Length",String.valueOf(xmlbyte.length));
        conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        conn.setRequestProperty("X-ClientType", "2");//�����Զ����ͷ��Ϣ

        conn.getOutputStream().write(xmlbyte);
        conn.getOutputStream().flush();
        conn.getOutputStream().close();


        if (conn.getResponseCode() != 200)
            throw new RuntimeException("����urlʧ��");

        InputStream in = conn.getInputStream();// ��ȡ��������
          
        BufferedReader bufferedReader = new BufferedReader(  
                new InputStreamReader(in));  
        StringBuffer temp = new StringBuffer();  
        String line = bufferedReader.readLine();  
        while (line != null) {  
            temp.append(line).append("\r\n");  
            line = bufferedReader.readLine();  
        }  
        bufferedReader.close(); 
        content = new String(temp.toString().getBytes(), "UTF-8"); 				            
        Log.i("EV_JNI","rec1="+content);
		   
	     return content;
    }
    
    //����֧��������Ϣ
    public static Map<String, String> PostWeiBuy(Map<String, String> list) 
    {
    	ToolClass.CheckAliWeiFile();
    	//��΢�ŷ��������ͽ�����Ϣ
		Map<String, String> sPara = new HashMap<String, String>();
		 sPara.put("appid",WeiConfig.getWeiappid());//appid
		 sPara.put("mch_id",WeiConfig.getWeimch_id());//mch_id
		 //��ӷ����˺�
		 if(WeiConfig.getIsweisub()>0)
		 {
			 sPara.put("sub_mch_id",WeiConfig.getWeisubmch_id());//sub_mch_id
		 }
		 sPara.put("device_info",ToolClass.getVmc_no());//�豸��
		 sPara.put("fee_type","CNY");//��������
		 sPara.put("nonce_str","960f228109051b9969f76c82bde183ac");//����ַ���
		 sPara.put("body","��ά�뽻��");//��������
		 sPara.put("out_trade_no", list.get("out_trade_no"));//�������
		 sPara.put("total_fee",list.get("total_fee"));//�����ܽ���λΪ�֣����ܴ�С����	
		 sPara.put("spbill_create_ip", "127.0.0.1");//�ն�IP
		 //��������ʱ��yyyyMMddHHmmss
		 SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmss"); //��ȷ������ 
		 Date now = new Date();
		 String starttime = tempDate.format(now).toString(); 					
	     sPara.put("time_start", starttime);   
		 //����ʧЧʱ��yyyyMMddHHmmss
	     Calendar cal = Calendar.getInstance();   
         cal.setTime(now);   
         cal.add(Calendar.MINUTE, 10);// 24Сʱ��  
         now = cal.getTime(); 
         String endtime = tempDate.format(now).toString(); 					
	     sPara.put("time_expire", endtime);   
	     
		 sPara.put("notify_url", "127.0.0.1");//֪ͨ��ַ
		 sPara.put("trade_type","NATIVE");//��ά�뽻��
		 sPara.put("product_id", list.get("out_trade_no"));//��ƷID
		 String key=WeiConfig.getWeikey();
		 String sign=WeixingSubmit.buildRequestPara(sPara,key);
		 sPara.put("sign",sign);
		 Log.i("EV_JNI","Send1="+sPara);
    	return sPara;
    }
    
    //���֧������ķ�����Ϣ
    public static Map<String, String> PendWeiBuy(InputStream is)
    {
    	Map<String, String>list=new HashMap<String, String>();
    	
    	XmlPullParser parser = Xml.newPullParser(); 
        try {
            //parser.setInput(new ByteArrayInputStream(string.substring(1)
            //        .getBytes("UTF-8")), "UTF-8");
            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
			{
                if (eventType == XmlPullParser.START_TAG) 
				{
                    if ("return_code".equals(parser.getName())) 
					{
                    	list.put("return_code", parser.nextText());
                    } 
					else if ("return_msg".equals(parser.getName())) 
					{
						list.put("return_msg", parser.nextText());
                    } 
					else if ("result_code".equals(parser.getName())) 
					{
						list.put("result_code", parser.nextText());
                    }
					else if ("err_code".equals(parser.getName())) 
					{
						list.put("err_code", parser.nextText());
                    }
					else if ("err_code_des".equals(parser.getName())) 
					{
						list.put("err_code_des", parser.nextText());
                    }
					else if ("prepay_id".equals(parser.getName())) 
					{
						list.put("prepay_id", parser.nextText());
                    }
					else if ("code_url".equals(parser.getName())) 
					{
						list.put("code_url", parser.nextText());
                    }					
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return list;
    }
    
    //���ɲ�ѯ��Ϣ
    public static Map<String, String> PostWeiQuery(Map<String, String> list) 
    {
    	//��΢�ŷ��������ͽ�����Ϣ
		Map<String, String> sPara = new HashMap<String, String>();
		 sPara.put("appid",WeiConfig.getWeiappid());//appid
		 sPara.put("mch_id",WeiConfig.getWeimch_id());//mch_id
		 //��ӷ����˺�
		 if(WeiConfig.getIsweisub()>0)
		 {
			 sPara.put("sub_mch_id",WeiConfig.getWeisubmch_id());//sub_mch_id
		 }
		 sPara.put("nonce_str","960f228109051b9969f76c82bde183ac");//����ַ���
		 sPara.put("out_trade_no", list.get("out_trade_no"));//�������
		 String key=WeiConfig.getWeikey();
		 String sign=WeixingSubmit.buildRequestPara(sPara,key);
		 sPara.put("sign",sign);
		 Log.i("EV_JNI","Send1="+sPara);
    	return sPara;
    }
    //�����ѯ����ķ�����Ϣ
    public static Map<String, String> PendWeiQuery(InputStream is)
    {
    	Map<String, String>list=new HashMap<String, String>();
    	
    	XmlPullParser parser = Xml.newPullParser();
        try {
            //parser.setInput(new ByteArrayInputStream(string.substring(1)
            //        .getBytes("UTF-8")), "UTF-8");
            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
			{
                if (eventType == XmlPullParser.START_TAG) 
				{
                    if ("return_code".equals(parser.getName())) 
					{
                    	list.put("return_code", parser.nextText());
                    } 
					else if ("return_msg".equals(parser.getName())) 
					{
						list.put("return_msg", parser.nextText());
                    } 
					else if ("result_code".equals(parser.getName())) 
					{
						list.put("result_code", parser.nextText());
                    }
					else if ("err_code".equals(parser.getName())) 
					{
						list.put("err_code", parser.nextText());
                    }
					else if ("err_code_des".equals(parser.getName())) 
					{
						list.put("err_code_des", parser.nextText());
                    }
					else if ("trade_state".equals(parser.getName())) 
					{
						list.put("trade_state", parser.nextText());
                    }										
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return list;
    }
    
    //�����˿���Ϣ
    public static Map<String, String> PostWeiPayout(Map<String, String> list)
    {
    	//��΢�ŷ��������ͽ�����Ϣ
		Map<String, String> sPara = new HashMap<String, String>();
		 sPara.put("appid",WeiConfig.getWeiappid());//appid
		 sPara.put("mch_id",WeiConfig.getWeimch_id());//mch_id
		//��ӷ����˺�
		 if(WeiConfig.getIsweisub()>0)
		 {
			 sPara.put("sub_mch_id",WeiConfig.getWeisubmch_id());//sub_mch_id
		 }
		 sPara.put("device_info",ToolClass.getVmc_no());//�豸��
		 sPara.put("nonce_str","960f228109051b9969f76c82bde183ac");//����ַ���
		 sPara.put("out_trade_no", list.get("out_trade_no"));//�������
		 sPara.put("out_refund_no", list.get("out_refund_no"));//�˿���
		 sPara.put("total_fee",list.get("total_fee"));//�����ܽ���λΪ�֣����ܴ�С����	
		 sPara.put("refund_fee",list.get("refund_fee"));//�˿���
		 sPara.put("refund_fee_type","CNY");//��������
		 sPara.put("op_user_id", WeiConfig.getWeimch_id());//�̻���		 
		 String key=WeiConfig.getWeikey();
		 String sign=WeixingSubmit.buildRequestPara(sPara,key);
		 sPara.put("sign",sign);
		 Log.i("EV_JNI","Send1="+sPara);
    	return sPara;
    }
    
    //����˿�����ķ�����Ϣ
    public static Map<String, String> PendWeiPayout(InputStream is)
    {
    	Map<String, String>list=new HashMap<String, String>();
    	
    	XmlPullParser parser = Xml.newPullParser();
        try {
            //parser.setInput(new ByteArrayInputStream(string.substring(1)
            //        .getBytes("UTF-8")), "UTF-8");
            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
			{
                if (eventType == XmlPullParser.START_TAG) 
				{
                    if ("return_code".equals(parser.getName())) 
					{
                    	list.put("return_code", parser.nextText());
                    } 
					else if ("return_msg".equals(parser.getName())) 
					{
						list.put("return_msg", parser.nextText());
                    } 
					else if ("result_code".equals(parser.getName())) 
					{
						list.put("result_code", parser.nextText());
                    }
					else if ("err_code".equals(parser.getName())) 
					{
						list.put("err_code", parser.nextText());
                    }
					else if ("err_code_des".equals(parser.getName())) 
					{
						list.put("err_code_des", parser.nextText());
                    }	
					else if ("trade_state".equals(parser.getName())) 
					{
						list.put("trade_state", parser.nextText());
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return list;
    }
    
    //���ɳ�����Ϣ
    public static Map<String, String> PostWeiDelete(Map<String, String> list) 
    {
    	//��΢�ŷ��������ͽ�����Ϣ
		Map<String, String> sPara = new HashMap<String, String>();
		 sPara.put("appid",WeiConfig.getWeiappid());//appid
		 sPara.put("mch_id",WeiConfig.getWeimch_id());//mch_id
		 //��ӷ����˺�
		 if(WeiConfig.getIsweisub()>0)
		 {
			 sPara.put("sub_mch_id",WeiConfig.getWeisubmch_id());//sub_mch_id
		 }
		 sPara.put("nonce_str","960f228109051b9969f76c82bde183ac");//����ַ���
		 sPara.put("out_trade_no", list.get("out_trade_no"));//�������
		 String key=WeiConfig.getWeikey();
		 String sign=WeixingSubmit.buildRequestPara(sPara,key);
		 sPara.put("sign",sign);
		 Log.i("EV_JNI","Send1="+sPara);
    	return sPara;
    }
    
    //�����������ķ�����Ϣ
    public static Map<String, String> PendWeiDelete(InputStream is)
    {
    	Map<String, String>list=new HashMap<String, String>();
    	
    	XmlPullParser parser = Xml.newPullParser();
        try {
            //parser.setInput(new ByteArrayInputStream(string.substring(1)
            //        .getBytes("UTF-8")), "UTF-8");
            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
			{
                if (eventType == XmlPullParser.START_TAG) 
				{
                    if ("return_code".equals(parser.getName())) 
					{
                    	list.put("return_code", parser.nextText());
                    } 
					else if ("return_msg".equals(parser.getName())) 
					{
						list.put("return_msg", parser.nextText());
                    } 
					else if ("result_code".equals(parser.getName())) 
					{
						list.put("result_code", parser.nextText());
                    }
					else if ("err_code".equals(parser.getName())) 
					{
						list.put("err_code", parser.nextText());
                    }
					else if ("err_code_des".equals(parser.getName())) 
					{
						list.put("err_code_des", parser.nextText());
                    }															
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return list;
    }
}
