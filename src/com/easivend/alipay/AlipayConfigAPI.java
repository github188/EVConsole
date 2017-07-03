package com.easivend.alipay;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import com.easivend.alipay.AlipayConfig;
import com.easivend.common.ToolClass;

public class AlipayConfigAPI {
	//����֧�����˺�
    public static void SetAliConfig(Map<String, String> list) 
    {
    	String str=null;
    	//alipay
    	str=list.get("alipartner");    	
    	AlipayConfig.setPartner(str);
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<partner="+AlipayConfig.getPartner(),"log.txt");
    	
    	str=list.get("aliseller_email");
    	AlipayConfig.setSeller_email(str);
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<seller_email="+AlipayConfig.getSeller_email(),"log.txt");
    	
    	str=list.get("alikey");
    	AlipayConfig.setKey(str);
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<alikey="+AlipayConfig.getKey(),"log.txt");

    	str=list.get("alisubpartner");    	
    	AlipayConfig.setSubpartner(str);
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<subpartner="+AlipayConfig.getSubpartner(),"log.txt");
    	
        str=list.get("isalisub");
        if(str.equals("")==false) {
            AlipayConfig.setIsalisub(Float.parseFloat(str));
            ToolClass.Log(ToolClass.INFO, "EV_JNI", "APP<<isalisub=" + AlipayConfig.getIsalisub(), "log.txt");
        }
    	 
    	if(list.containsKey("aliprivateKey"))//����˽Կ
        {
    		str=list.get("aliprivateKey");
    		AlipayConfig.setAliprivateKey(str);
    		//˽Կ��ÿ���̻��Ŷ���ӦΨһ�ģ��ڱ���������������RSAǩ������
        	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<aliprivateKey="+AlipayConfig.getAliprivateKey(),"log.txt");
        }
    }
    
    //����֧��������Ϣ
    public static Map<String, String> PostAliBuy(Map<String, String> list) 
    {
    	ToolClass.CheckAliWeiFile();
    	//��֧�������������ͽ�����Ϣ
		Map<String, String> sPara = new HashMap<String, String>();
		 sPara.put("service","alipay.acquire.precreate");//�ӿ�����
		 sPara.put("partner",AlipayConfig.getPartner());//֧���� PID��
		 sPara.put("_input_charset",AlipayConfig.getInput_charset());//����		
		 sPara.put("out_trade_no", list.get("out_trade_no"));//�������
		 sPara.put("subject",list.get("subject"));//��������
		 sPara.put("product_code","QR_CODE_OFFLINE");//��ά��
		 sPara.put("total_fee",list.get("total_fee"));//�����ܽ��	
		 sPara.put("seller_email", AlipayConfig.getSeller_email());//����֧�����ʻ�		 
		 sPara.put("goods_detail",list.get("goods_detail"));//��Ʒ��ϸ
		 sPara.put("it_b_pay",list.get("it_b_pay"));//���׹ر�ʱ��	
		 //��ӷ����˺�
		 if(AlipayConfig.getIsalisub()>0)
		 {
			 JSONArray array=new JSONArray();
			 JSONObject json=new JSONObject();
			 try {
				json.put("serialNo", 1);
				json.put("transOut", AlipayConfig.getPartner());
				json.put("transIn", AlipayConfig.getSubpartner());
				float total_fee=Float.parseFloat(list.get("total_fee"));
				total_fee=total_fee*AlipayConfig.getIsalisub();
				BigDecimal b=new BigDecimal(total_fee);  
				double amount=b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
				json.put("amount", amount);
				json.put("desc", "���˽���");
				array.put(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("EV_JNI","Send0.3="+array.toString());
			sPara.put("royalty_type","ROYALTY");//��������
			sPara.put("royalty_parameters",array.toString());//������Ϣ
		 }
		 String vmc_no=ToolClass.getVmc_no();
		 JSONObject extend=new JSONObject();
		 try {
			extend.put("STORE_TYPE", "1");
			extend.put("STORE_ID", vmc_no);
			extend.put("TERMINAL_ID", vmc_no);
			Log.i("EV_JNI","Send0.4="+extend.toString());
			sPara.put("extend_params",extend.toString());//������Ϣ
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 Map<String, String> map1 = AlipaySubmit.buildRequestPara(sPara);
		 Log.i("EV_JNI","Send1="+map1);
    	return map1;
    }
    //���֧������ķ�����Ϣ
    public static Map<String, String> PendAliBuy(InputStream is)
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
                    if ("is_success".equals(parser.getName())) 
					{
                    	list.put("is_success", parser.nextText());
                    } 
					else if ("error".equals(parser.getName())) 
					{
						list.put("error", parser.nextText());
                    } 
					else if ("result_code".equals(parser.getName())) 
					{
						list.put("result_code", parser.nextText());
                    }
					else if ("detail_error_code".equals(parser.getName())) 
					{
						list.put("detail_error_code", parser.nextText());
                    }
					else if ("detail_error_des".equals(parser.getName())) 
					{
						list.put("detail_error_des", parser.nextText());
                    }
					else if ("qr_code".equals(parser.getName())) 
					{
						list.put("qr_code", parser.nextText());
                    }
					else if ("pic_url".equals(parser.getName())) 
					{
						list.put("pic_url", parser.nextText());
                    }
					else if ("small_pic_url".equals(parser.getName())) 
					{
						list.put("small_pic_url", parser.nextText());
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
    
    //���ɲ�ѯ������Ϣ
    public static Map<String, String> PostAliQuery(Map<String, String> list) 
    {
    	//��֧�������������ͽ�����Ϣ
		Map<String, String> sPara = new HashMap<String, String>();
		 sPara.put("service","alipay.acquire.query");//�ӿ�����
		 sPara.put("partner",AlipayConfig.getPartner());//֧���� PID��
		 sPara.put("_input_charset",AlipayConfig.getInput_charset());//����		
		 sPara.put("out_trade_no", list.get("out_trade_no"));//�������
		 
		 Map<String, String> map1 = AlipaySubmit.buildRequestPara(sPara);
		 Log.i("EV_JNI","Send1="+map1);
    	return map1;
    }
    
    //�����ѯ����ķ�����Ϣ
    public static Map<String, String> PendAliQuery(InputStream is)
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
                    if ("is_success".equals(parser.getName())) 
					{
                    	list.put("is_success", parser.nextText());
                    } 
					else if ("error".equals(parser.getName())) 
					{
						list.put("error", parser.nextText());
                    } 
					else if ("result_code".equals(parser.getName())) 
					{
						list.put("result_code", parser.nextText());
                    }
					else if ("detail_error_code".equals(parser.getName())) 
					{
						list.put("detail_error_code", parser.nextText());
                    }
					else if ("detail_error_des".equals(parser.getName())) 
					{
						list.put("detail_error_des", parser.nextText());
                    }
					else if ("trade_status".equals(parser.getName())) 
					{
						list.put("trade_status", parser.nextText());
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
    public static Map<String, String> PostAliPayout(Map<String, String> list) 
    {
    	//��֧�������������ͽ�����Ϣ
		Map<String, String> sPara = new HashMap<String, String>();
		 sPara.put("service","alipay.acquire.refund");//�ӿ�����
		 sPara.put("partner",AlipayConfig.getPartner());//֧���� PID��
		 sPara.put("_input_charset",AlipayConfig.getInput_charset());//����		
		 sPara.put("out_trade_no", list.get("out_trade_no"));//�������
		 sPara.put("refund_amount", list.get("refund_amount"));//�˿���
		 //�̻��˿����󵥺ţ����Ա�ʶ���ν��׵��˿�����
		 //��������뱾����������out_trade_no��䱾������ֵ��ͬʱ����Ϊ��������Ϊȫ���˿Ҫ���˿���ͽ���֧�����һ�¡�
		 sPara.put("out_request_no", list.get("out_request_no"));//�˿�����
		 
		 Map<String, String> map1 = AlipaySubmit.buildRequestPara(sPara);
		 Log.i("EV_JNI","Send1="+map1);
    	return map1;
    }
    
    //����˿�����ķ�����Ϣ
    public static Map<String, String> PendAliPayout(InputStream is)
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
                    if ("is_success".equals(parser.getName())) 
					{
                    	list.put("is_success", parser.nextText());
                    } 
					else if ("error".equals(parser.getName())) 
					{
						list.put("error", parser.nextText());
                    } 
					else if ("result_code".equals(parser.getName())) 
					{
						list.put("result_code", parser.nextText());
                    }
					else if ("detail_error_code".equals(parser.getName())) 
					{
						list.put("detail_error_code", parser.nextText());
                    }
					else if ("detail_error_des".equals(parser.getName())) 
					{
						list.put("detail_error_des", parser.nextText());
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
    
    //����������Ϣ
    public static Map<String, String> PostAliDelete(Map<String, String> list) 
    {
    	//��֧�������������ͽ�����Ϣ
		Map<String, String> sPara = new HashMap<String, String>();
		 sPara.put("service","alipay.acquire.cancel");//�ӿ�����
		 sPara.put("partner",AlipayConfig.getPartner());//֧���� PID��
		 sPara.put("_input_charset",AlipayConfig.getInput_charset());//����		
		 sPara.put("out_trade_no", list.get("out_trade_no"));//�������
		 
		 Map<String, String> map1 = AlipaySubmit.buildRequestPara(sPara);
		 Log.i("EV_JNI","Send1="+map1);
    	return map1;
    }
    
    //���������������ķ�����Ϣ
    public static Map<String, String> PendAliDelete(InputStream is)
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
                    if ("is_success".equals(parser.getName())) 
					{
                    	list.put("is_success", parser.nextText());
                    } 
					else if ("error".equals(parser.getName())) 
					{
						list.put("error", parser.nextText());
                    } 
					else if ("result_code".equals(parser.getName())) 
					{
						list.put("result_code", parser.nextText());
                    }
					else if ("detail_error_code".equals(parser.getName())) 
					{
						list.put("detail_error_code", parser.nextText());
                    }
					else if ("detail_error_des".equals(parser.getName())) 
					{
						list.put("detail_error_des", parser.nextText());
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
