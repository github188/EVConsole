/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           ToolClass.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        �����࣬�������ŵ���Ҫ��static������static��Ա��ͳһ��Ϊȫ�ֱ�����ȫ�ֺ�����       
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/


package com.easivend.common;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.conn.ssl.SSLContexts;
import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.alipay.AlipayConfig;
import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_logDAO;
import com.easivend.dao.vmc_orderDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.model.Tb_vmc_column;
import com.easivend.model.Tb_vmc_log;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.view.XZip;
import com.easivend.weixing.WeiConfig;
import com.easivend.weixing.WeiConfigAPI;
import com.example.evconsole.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ToolClass 
{
	public final static int VERBOSE=0;
	public final static int DEBUG=1;
	public final static int INFO=2;
	public final static int WARN=3;
	public final static int ERROR=4;
	public static String EV_DIR=null;//ev���ĵ�ַ
	private static int bentcom_id=-1,com_id=-1,columncom_id=-1,extracom_id=-1,columncom2_id=-1;//����id��
	private static String bentcom="",com="",columncom="",extracom="",cardcom="",printcom="",posip="",posipport="",columncom2="";//����������
	private static int posisssl=0;
	private static boolean possign=false;
	private static int bill_err=0,coin_err=0;//ֽ������Ӳ��������״̬
	public static String vmc_no="";//�������
	public static Bitmap mark=null;//����ͼƬ
	public static int goc=0;//�Ƿ�ʹ�ó���ȷ�ϰ�1��
	public static int extraComType=0;//1ʹ�ñ�ɽ���ͣ�2ʹ��չʾλ
	public static Map<Integer, Integer> huodaolist=null;//�����߼���������������Ķ�Ӧ��ϵ
	public static Map<Integer, Integer> elevatorlist=null;//�����������߼���������������Ķ�Ӧ��ϵ
	public static Map<Integer, Integer> huodaolist2=null;//���渱���߼���������������Ķ�Ӧ��ϵ
	public static Map<Integer, Integer> elevatorlist2=null;//���渱���������߼���������������Ķ�Ӧ��ϵ
	public static Map<String, String> selectlist=null;//����ѡ������id����Ʒid�Ķ�Ӧ��ϵ
	public static int orientation=0;//ʹ�ú�����������ģʽ0����,1����
	public static SSLSocketFactory ssl=null;//ssl�������
	public static Context context=null;//��Ӧ��context
	private static int ServerVer=1;//0�ɵĺ�̨��1һ�ڵĺ�̨
	public static String version="";//�����汾��
	public static boolean CLIENT_STATUS_SERVICE=true;//true��������ʹ��,false������ͣ���� 
	public static boolean LAST_CHUHUO=false;//true��һ�ʳ����ɹ�,false��һ�ʳ���ʧ��
	public static int netType=0;//��������1.����,2.wifi,3.4gmobile,4������
	public static String netStr="";//�����ź���Ϣ
	
	public static int getNetType() {
		return netType;
	}
	public static void setNetType(int netType) {
		ToolClass.netType = netType;
	}
	
	public static String getNetStr() {
		return netStr;
	}
	public static void setNetStr(String netStr) {
		ToolClass.netStr = netStr;
	}
	public static boolean isLAST_CHUHUO() {
		return LAST_CHUHUO;
	}
	public static void setLAST_CHUHUO(boolean lAST_CHUHUO) {
		LAST_CHUHUO = lAST_CHUHUO;
	}
	//��ȡ�ļ���Ϣ
	public static boolean  ReadSharedPreferencesAccess()
	{
		boolean isaccess=true;
		//�ļ���˽�е�
		SharedPreferences  user = context.getSharedPreferences("access_info",0);
		//��ȡ
		isaccess=user.getBoolean("access",true);
		return isaccess;
	}
	//д���ļ���Ϣ
	public static void  WriteSharedPreferences(boolean value)
	{
		//�ļ���˽�е�
		SharedPreferences  user = context.getSharedPreferences("access_info",0);
		//��Ҫ�ӿڽ��б༭
		SharedPreferences.Editor edit=user.edit();
		//����
		edit.putBoolean("access", value);
		//�ύ����
		edit.commit();
	}
		
	public static boolean isCLIENT_STATUS_SERVICE() {
		return CLIENT_STATUS_SERVICE;
	}

	public static void setCLIENT_STATUS_SERVICE(boolean cLIENT_STATUS_SERVICE) {
		CLIENT_STATUS_SERVICE = cLIENT_STATUS_SERVICE;
		WriteSharedPreferences(CLIENT_STATUS_SERVICE);
	}
	
	//�ж����������ͣ���񣬲��������۲���ʾ
	public static boolean checkCLIENT_STATUS_SERVICE()
	{
		boolean check=isCLIENT_STATUS_SERVICE();
		if(check==false)
		{
			failToast("��Ǹ��������ͣ����,����ϵ����Ա��");
		}
		return check;
	}

	public static String getVersion() {
		String curVersion=null;
		int curVersionCode=0;
		 try {
	            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
	            		context.getPackageName(), 0);
	            curVersion = pInfo.versionName;
	            curVersionCode = pInfo.versionCode;
	        } catch (NameNotFoundException e) {
	            Log.e("update", e.getMessage());
	            curVersion = "1.1.1000";
	            curVersionCode = 111000;
	        }
		 version=(curVersion+curVersionCode).toString();
		return version;
	}

	public static int getServerVer() {
		return ServerVer;
	}

	public static void setServerVer(int serverVer) {
		ServerVer = serverVer;
	}

	public static String getBentcom() {
		return bentcom;
	}

	public static void setBentcom(String bentcom) {
		ToolClass.bentcom = bentcom;
	}

	public static String getCom() {
		return com;
	}

	public static void setCom(String com) {
		ToolClass.com = com;
	}

	public static String getColumncom() {
		return columncom;
	}

	public static void setColumncom(String columncom) {
		ToolClass.columncom = columncom;
	}
	
		
	public static String getColumncom2() {
		return columncom2;
	}
	public static void setColumncom2(String columncom2) {
		ToolClass.columncom2 = columncom2;
	}
	public static String getExtracom() {
		return extracom;
	}

	public static void setExtracom(String extracom) {
		ToolClass.extracom = extracom;
	}
		
	public static String getCardcom() {
		return cardcom;
	}

	public static void setCardcom(String cardcom) {
		ToolClass.cardcom = cardcom;
	}
	
	public static String getPrintcom() {
		return printcom;
	}
	public static void setPrintcom(String printcom) {
		ToolClass.printcom = printcom;
	}
	
	public static String getPosip() {
		return posip;
	}
	public static void setPosip(String posip) {
		ToolClass.posip = posip;
	}
	public static String getPosipport() {
		return posipport;
	}
	public static void setPosipport(String posipport) {
		ToolClass.posipport = posipport;
	}
	
	public static int getPosisssl() {
		return posisssl;
	}
	public static void setPosisssl(int posisssl) {
		ToolClass.posisssl = posisssl;
	}
	
	public static boolean isPossign() {
		return possign;
	}
	public static void setPossign(boolean possign) {
		ToolClass.possign = possign;
	}
	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		ToolClass.context = context;
	}

	public static String getEV_DIR() {
		return EV_DIR;
	}

	public static void setEV_DIR(String eV_DIR) {
		EV_DIR = eV_DIR;
	}

	public static SSLSocketFactory getSsl() {
		return ssl;
	}

	public static void setSsl(SSLSocketFactory ssl) {
		ToolClass.ssl = ssl;
	}

	public static int getOrientation() {
		return orientation;
	}

	public static void setOrientation(int orientation) {
		ToolClass.orientation = orientation;
	}

	public static Bitmap getMark() {
		return mark;
	}

	public static void setMark(Bitmap mark) {
		ToolClass.mark = mark;
	}
	
	public static void setExtraComType(Context context) 
	{
		//���һ�������
		vmc_cabinetDAO cabinetDAO3 = new vmc_cabinetDAO(context);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
	    if(cabinetDAO3.findUBoxData())
	    {
	    	extraComType=1;
	    }	
	}
			
	public static int getExtraComType() {
		return extraComType;
	}

	public static int getGoc() {
		return goc;
	}
	
	public static void setGoc(Context context) 
	{
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		ToolClass.goc = tb_inaccount.getIsbuyCar();
    	}
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<goc="+ToolClass.goc,"log.txt");	
	}

	public static String getVmc_no() {
		return vmc_no;
	}

	public static void setVmc_no(String vmc_no) {
		ToolClass.vmc_no = vmc_no;
	}

	public static int getBentcom_id() {
		return bentcom_id;
	}

	public static void setBentcom_id(int bentcom_id) {
		ToolClass.bentcom_id = bentcom_id;
	}
	
	public static int getColumncom_id() {
		return columncom_id;
	}

	public static void setColumncom_id(int columncom_id) {
		ToolClass.columncom_id = columncom_id;
	}
	
	
	public static int getColumncom2_id() {
		return columncom2_id;
	}
	public static void setColumncom2_id(int columncom2_id) {
		ToolClass.columncom2_id = columncom2_id;
	}
	public static int getCom_id() {
		return com_id;
	}

	public static void setCom_id(int com_id) {
		ToolClass.com_id = com_id;
	}
	
	public static int getExtracom_id() {
		return extracom_id;
	}

	public static void setExtracom_id(int extracom_id) {
		ToolClass.extracom_id = extracom_id;
	}

	//����Map����<String,Object>�����ݼ���
	public static Map<String, Object> getMapListgson(String jsonStr)
	{
		Map<String, Object> list=new HashMap<String,Object>();
		try {
			JSONObject object=new JSONObject(jsonStr);//{"EV_json":{"EV_type":"EV_STATE_RPT","state":2}}
			JSONObject perobj=object.getJSONObject("EV_json");//{"EV_type":"EV_STATE_RPT","state":2}
			Gson gson=new Gson();
			list=gson.fromJson(perobj.toString(), new TypeToken<Map<String, Object>>(){}.getType());
			//Log.i("EV_JNI",perobj.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	
	/**
     * ���ø�Ŀ¼�ļ�
     */
    public static void SetDir() 
    {
    	final String SDCARD_DIR=File.separator+"sdcard"+File.separator+"ev";
    	final String NOSDCARD_DIR=File.separator+"ev";
    	String  sDir =null;
    	Map<String, String> list=null;
    	    	
        try {
        	  //�����ж�sdcard�Ƿ����
        	  String status = Environment.getExternalStorageState();
        	  if (status.equals(Environment.MEDIA_MOUNTED)) 
        	  {
        		 sDir = SDCARD_DIR;;
        	  } 
        	  else
        	  {
        		  sDir = NOSDCARD_DIR;
        	  }
        	  ToolClass.setEV_DIR(sDir); 
        	             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	//Log���������ڴ�ӡlog�������ı��ļ��д�ӡ������־
	public static void Log(int info,String tag,String str,String filename)
	{
		String infotype="";
		switch(info)
		{
			case VERBOSE:
				infotype="VERBOSE";
				Log.v(tag,str);
				break;
			case DEBUG:
				infotype="DEBUG";
				Log.d(tag,str);
				break;
			case INFO:
				infotype="INFO";
				Log.i(tag,str);
				break;
			case WARN:
				infotype="WARN";
				Log.w(tag,str);
				break;
			case ERROR:
				infotype="ERROR";
				Log.e(tag,str);
				break;	
		}	
		infotype="("+infotype+"),["+tag+"] "+str;
		AppendLogFile(infotype,filename);
	}
	
	/**
     * ׷���ļ���ʹ��FileWriter
     */
    public static synchronized void AppendLogFile(String content,String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd" + " "  
                + "HH:mm:ss:SSS"); //��ȷ������ 
        String datetime = tempDate.format(new java.util.Date()).toString();  
        String cont=datetime+content+"\r\n";
    	
        try {
        	 sDir = ToolClass.getEV_DIR()+File.separator+"logs";
        	
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename);         	
        	//��������ڣ��򴴽��ļ�
        	if(!fileName.exists())
        	{  
    	      fileName.createNewFile(); 
    	    }  
            //��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
//            FileWriter writer = new FileWriter(fileName, true);
//            writer.write(cont);
//            writer.close();
        	Writer writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(fileName, true),Charset.forName("gbk")));
            writer.write(cont);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ����������־����һ����������ļ�������¾��������ļ�
     */
    public static void optLogFile() 
    {
    	String  sDir =null;
    	File fileName=null;
    	SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd" + " "  
                + "HH:mm:ss"); //��ȷ������ 
    	
    	//��ǰʱ��
//        String datetime = tempDate.format(new java.util.Date()).toString();  
//        ParsePosition pos = new ParsePosition(0);  
//    	Date d1 = (Date) tempDate.parse(datetime, pos); 
//    	ToolClass.Log(ToolClass.INFO,"EV_DOG","��ǰʱ��="+datetime+",="+d1.getTime(),"dog.txt");
    	 
    	//��ʼʱ��
        Calendar todayStart = Calendar.getInstance();  
        todayStart.set(Calendar.HOUR_OF_DAY, 0);  
        todayStart.set(Calendar.MINUTE, 0);  
        todayStart.set(Calendar.SECOND, 0);  
        todayStart.set(Calendar.MILLISECOND, 0); 
        Date date = todayStart.getTime(); 
        String starttime=tempDate.format(date);
        ParsePosition posstart = new ParsePosition(0);  
    	Date dstart = (Date) tempDate.parse(starttime, posstart);
    	ToolClass.Log(ToolClass.INFO,"EV_DOG","��ʼʱ��="+starttime+",="+dstart.getTime(),"dog.txt");
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"logs";
        	  
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	        	
        	//2.�������log�ļ������ж�
        	fileName=new File(sDir+File.separator+"log.txt"); 
        	if(fileName.exists())
        	{  
        		String logdatetime = getFileCreated(fileName);
        		ParsePosition poslog = new ParsePosition(0);  
				Date dlog = (Date) tempDate.parse(logdatetime, poslog);
				ToolClass.Log(ToolClass.INFO,"EV_DOG","�ж��������ļ�log.txtʱ��="+logdatetime+",="+dlog.getTime(),"dog.txt");
        		//�ж��Ƿ��ļ����ڽ���
            	if(dlog.getTime()<=dstart.getTime())
            	{
            		ToolClass.Log(ToolClass.INFO,"EV_DOG"," �ļ�log�طָ�","dog.txt"); 
            		updatefile(fileName,sDir);
            	}
            	else
            	{
            		ToolClass.Log(ToolClass.INFO,"EV_DOG"," �ļ�log�ų�","dog.txt"); 
            	}
    	    }
        	//3.�������dog�ļ������ж�
        	fileName=new File(sDir+File.separator+"dog.txt"); 
        	if(fileName.exists())
        	{  
        		String logdatetime = getFileCreated(fileName);
        		ParsePosition poslog = new ParsePosition(0);  
        		Date dlog = (Date) tempDate.parse(logdatetime, poslog);
        		ToolClass.Log(ToolClass.INFO,"EV_DOG","�ж��������ļ�dog.txtʱ��="+logdatetime+",="+dlog.getTime(),"dog.txt");
        		
        		//�ж��Ƿ��ļ����ڽ���
        		if(dlog.getTime()<=dstart.getTime())
            	{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," �ļ�dog�طָ�","dog.txt"); 
            		updatefile(fileName,sDir);
            	}
        		else
        		{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," �ļ�dog�ų�","dog.txt"); 
        		}
    	    } 
        	//4.�������server�ļ������ж�
        	fileName=new File(sDir+File.separator+"server.txt"); 
        	if(fileName.exists())
        	{  
        		String logdatetime = getFileCreated(fileName);
        		ParsePosition poslog = new ParsePosition(0);  
        		Date dlog = (Date) tempDate.parse(logdatetime, poslog);
        		ToolClass.Log(ToolClass.INFO,"EV_DOG","�ж��������ļ�server.txtʱ��="+logdatetime+",="+dlog.getTime(),"dog.txt");
        		//�ж��Ƿ��ļ����ڽ���
        		if(dlog.getTime()<=dstart.getTime())
            	{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," �ļ�server�طָ�","dog.txt"); 
            		updatefile(fileName,sDir);
            	}
        		else
        		{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," �ļ�server�ų�","dog.txt"); 
        		}
    	    }
        	//5.�������com�ļ������ж�
        	fileName=new File(sDir+File.separator+"com.txt"); 
        	if(fileName.exists())
        	{  
        		String logdatetime = getFileCreated(fileName);
        		ParsePosition poslog = new ParsePosition(0);  
        		Date dlog = (Date) tempDate.parse(logdatetime, poslog);
        		ToolClass.Log(ToolClass.INFO,"EV_DOG","�ж��������ļ�com.txtʱ��="+logdatetime+",="+dlog.getTime(),"dog.txt");
        		//�ж��Ƿ��ļ����ڽ���
        		if(dlog.getTime()<=dstart.getTime())
            	{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," �ļ�com�طָ�","dog.txt"); 
            		updatefile(fileName,sDir);
            	}
        		else
        		{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," �ļ�com�ų�","dog.txt"); 
        		}
    	    }
        	//6.��Ŀ¼�µ������ļ�������г�������µģ�ȫ��ɾ��
        	delFiles(dirName);
        	//7.ɾ������г�������µģ��Ѿ��ϴ��˵Ľ��׼�¼
        	delOrders();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	//��ȡ�ļ�����ʱ��
    public static String getFileCreated(final File file)  
    {  
		 String res=null;
		 Scanner scan = null ;
		 try{
		 	scan = new Scanner(file) ;	// ���ļ���������
		 	if(scan.hasNext())
		 	{
		 		res=scan.next()+" "+scan.next();	//	ȡ����		 		
		 	}
		 	
		 }catch(Exception e)
		 {
			
		 }
         if(res.indexOf("(INFO)")!=-1)
             res=res.substring(0, res.indexOf("(INFO)"));// ��������Ϣ�н�ȡ������
         else if(res.indexOf("(WARN)")!=-1)
            res=res.substring(0, res.indexOf("(WARN)"));// ��������Ϣ�н�ȡ������
		 System.out.println(" �ļ�����ʱ��1="+res);
         return res;
    }
	
	 
	 
	//�������ļ���fileNameԭ�ļ���,sDir��Ŀ¼
    public static void updatefile(File fileName,String  sDir)
	{
		SimpleDateFormat tempDate2 = new SimpleDateFormat("yyyy-MM-dd-HHmmss"); //��ȷ������ 
        String datetime2 = tempDate2.format(new java.util.Date()).toString();
        String oldname=fileName.getName();
		String newname=datetime2+oldname;
		System.out.println(fileName+" �޸��ļ�����="+newname);            		
		fileName.renameTo(new File(sDir+File.separator+newname));
	}
    
    //java�趨һ������ʱ�䣬�Ӽ����ӣ�Сʱ�����죩��õ��µ�����
    //���ص����ַ����͵�ʱ�䣬
    //�������String day��׼ʱ��, int x����
    public static String addDateMinut(String day, int x)
    {   
    	// 24Сʱ��  
    	//�����������ʽҲ������ HH:mm:ss����HH:mm�ȵȣ�������ģ�����������������ʱ��Ҫ������ı�
    	//��day��ʽһ��
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;   
        try {   
            date = format.parse(day);   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        if (date == null)   
            return "";   
        System.out.println("front:" + format.format(date)); //��ʾ���������  
        Calendar cal = Calendar.getInstance();   
        cal.setTime(date);   //�õ���׼ʱ��
        
        cal.add(Calendar.DATE, x);// �� 
        date = cal.getTime();   
        System.out.println("after:" + format.format(date));  //��ʾ���º������ 
        cal = null;   
        return format.format(date);   
  
    } 
    /**
     * �ݹ�ɾ��productImage�ļ����ļ���
     * @param file    Ҫɾ���ĸ�Ŀ¼
     */
    public static void deleteproductImageFile()
    {
    	String  sDir =null;
    	 try {
    		  sDir = ToolClass.getEV_DIR()+File.separator+"productImage";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllZIPFile(dirName);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * �ݹ�ɾ��ads,adshuo�ļ����ļ���
     * @param file    Ҫɾ���ĸ�Ŀ¼
     */
    public static void deleteadsImageFile()
    {
    	String  sDir =null;
    	 try {
    		 //ɾ�����Ŀ¼
    		  sDir = ToolClass.getEV_DIR()+File.separator+"ads";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllZIPFile(dirName);
        	 
        	//ɾ���������Ŀ¼
	   		sDir = ToolClass.getEV_DIR()+File.separator+"adshuo";
	       	dirName = new File(sDir);
	       	//���Ŀ¼�����ڣ��򴴽�Ŀ¼
	       	if (!dirName.exists()) 
	       	{  
	            //����ָ����·�������ļ���  
	       		dirName.mkdirs(); 
	        }
	       	 
	       	deleteAllZIPFile(dirName);
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * �ݹ�ɾ��ZIP�ļ����ļ���
     * @param file    Ҫɾ���ĸ�Ŀ¼
     */
    private static void deleteZIPFile()
    {
    	String  sDir =null;
    	 try {
        	  sDir = ToolClass.ReadLogFile()+"ZIPFile";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllZIPFile(dirName);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void deleteAllZIPFile(File file)
    {        
        if(file.isDirectory())
        {
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0)
            {
                return;
            }
            for(File f : childFile)
            {
            	ToolClass.Log(ToolClass.INFO,"EV_SERVER","ɾ��log="+f.toString(),"server.txt");										
                f.delete();
            }
        }
    }
     /**  
     * ���Ƶ����ļ�  
     * @param oldPath String ԭ�ļ�·�� �磺c:/fqf.txt  
     * @param newPath String ���ƺ�·�� �磺f:/fqf.txt  
     * @return boolean  
     */   
	   private static void copyFile(String oldPath, String newPath) 
	   {   
	       try {   
	           int bytesum = 0;   
	           int byteread = 0;   
	           File oldfile = new File(oldPath);   
	           if (oldfile.exists()) { //�ļ�����ʱ   
	               InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ�   
	               FileOutputStream fs = new FileOutputStream(newPath);   
	               byte[] buffer = new byte[1444];   
	               while ( (byteread = inStream.read(buffer)) != -1) {   
	                   bytesum += byteread; //�ֽ��� �ļ���С   
	                   System.out.println(bytesum);   
	                   fs.write(buffer, 0, byteread);   
	               }   
	               inStream.close();   
	               fs.close();
	           }   
	       }   
	       catch (Exception e) {   
	           System.out.println("���Ƶ����ļ���������");   
	           e.printStackTrace();   
	  
	       }   
	  
	   }  
	   /**
	     * zipLogFilesѹ����Ҫ�ϴ�����־��
	     * @param 
	     */
	    private static String zipLogFiles(String srcFileString) 
	  	{  
	  		//��������ļ�����������ļ�
	  		String zipFileString=ToolClass.getEV_DIR()+File.separator+"logzip.zip";
	  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<srcFileString="+srcFileString+" zipFileString="+zipFileString,"log.txt"); 
	  		try {
	  			XZip.ZipFolder(srcFileString, zipFileString);
	  		} catch (Exception e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		return zipFileString;
	  	}   
    /**
     * �ж��뵱ǰʱ������,createtime���ļ�����ʱ��,datetime�ǵ�ǰʱ��
     * �����ʱ���ʽ����������2012-8-21 17:53:20�����ĸ�ʽ  
     * ����ֵ��1�룬2�֣�3ʱ��4�죬5�����
     */
    public static String logFileInterval(String starttime,String endtime)
    {
    	boolean inter=false;//true��ʾ��Ҫѹ�����ݰ�
        String  sDir =null,zipDir=null,zipFileString=null;
    	File fileName=null;
    	
        //1.������ʼʱ��ͽ���ʱ��
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        
        ParsePosition pos = new ParsePosition(0);  
        Date d1 = (Date) sd.parse(starttime, pos); 
        ToolClass.Log(ToolClass.INFO,"EV_SERVER","��ʼʱ��="+starttime+",="+d1.getTime(),"server.txt");
        
        endtime=addDateMinut(endtime,1);
        ParsePosition posnow = new ParsePosition(0);  
        Date dnow = (Date) sd.parse(endtime, posnow);
        ToolClass.Log(ToolClass.INFO,"EV_SERVER","����ʱ��="+endtime+",="+dnow.getTime(),"server.txt");
        //2.����ѹ��Ŀ��Ŀ¼ 
        deleteZIPFile(); 	
        zipDir=ToolClass.ReadLogFile()+"ZIPFile"+File.separator;
        //3.����log�ļ����ж��Ƿ��������ʱ��֮��
        try {
        	 sDir = ToolClass.getEV_DIR()+File.separator+"logs";
  		  
	  		 File dirName = new File(sDir);
	  		 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
	  		 if (!dirName.exists()) 
	  		 {  
	  			//����ָ����·�������ļ���  
	  			dirName.mkdirs(); 
	  		 }   
	  		//��������ļ�����������ļ�
	   		File[] files = dirName.listFiles();
	   		if (files.length > 0) 
	   		{  
	   			for (int i = 0; i < files.length; i++) 
	   			{
	   			  if(!files[i].isDirectory())
	   			  {		
	   				    String filestr=files[i].toString();
	   				  	ToolClass.Log(ToolClass.INFO,"EV_SERVER"," �ж���־Ŀ¼���ļ�="+filestr,"server.txt"); 
	   				    //�ų�dog���ļ�
	   				  	int attimg2=filestr.lastIndexOf("dog.txt");
		   		        if(attimg2==-1)
		   		        {
		   		        	fileName=new File(files[i].toString()); 
		   		        	if(fileName.exists())
		   		        	{ 
		   		        		try
				        		{
			   		        		String logdatetime = getFileCreated(fileName);
			   		        		ParsePosition poslog = new ParsePosition(0);  
			   		        		Date dlog = (Date) sd.parse(logdatetime, poslog);
			   		        		ToolClass.Log(ToolClass.INFO,"EV_SERVER","�ļ�ʱ��="+logdatetime+",="+dlog.getTime(),"server.txt");
			   		            	if((d1.getTime()<=dlog.getTime())&&(dlog.getTime()<=dnow.getTime()))
			   		            	{
			   		            		//4.�����ļ���ѹ��Ŀ¼��
			   		            		String a[] = files[i].toString().split("/");  
			   		            		String ATT_ID=a[a.length-1];  
			   		            		String ZIPFile=zipDir+ATT_ID;
			   		            		ToolClass.Log(ToolClass.INFO,"EV_SERVER"," �ļ�"+files[i].toString()+"ѡ��,zip="+ZIPFile,"server.txt"); 
			   		            		copyFile(files[i].toString(),ZIPFile);
			   		            		inter=true;
			   		            	}
			   		            	else
			   		            	{
			   		            		ToolClass.Log(ToolClass.INFO,"EV_SERVER"," �ļ�"+files[i].toString()+"�ų�","server.txt"); 
			   		            	}
				        		}
				        		catch(Exception e)
				        		{
				        			ToolClass.Log(ToolClass.INFO,"EV_SERVER","�ļ�="+files[i].toString()+"�쳣���޷��ж�","server.txt");
				        		}	
		   		    	    } 
		   		        }
	   			  }
	   			}
	   		}
	   		//5.ѹ�����ݰ�
	   		if(inter)
	   		{
	   			zipFileString=zipLogFiles(zipDir);
	   		}
	     } catch (Exception e) {
			e.printStackTrace();
		 } 
        return zipFileString;
    }
	
	 /* ����Ŀ¼���ļ��б� file��Ŀ¼����datetime�ǵ�ǰʱ�䣬�����������£���ɾ��������ļ�
	  * */  
    public static void delFiles(File file) 
    {  
    	//1.������ʼʱ��ͽ���ʱ��
    	SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    	
    	//������ʼʱ��
        Calendar todayStart = Calendar.getInstance(); 
        todayStart.setFirstDayOfWeek(Calendar.MONDAY);  
        todayStart.set(Calendar.HOUR_OF_DAY, 0);  
        todayStart.set(Calendar.MINUTE, 0);  
        todayStart.set(Calendar.SECOND, 0);  
        todayStart.set(Calendar.MILLISECOND, 0); 
        todayStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
        //����ǰ����
        todayStart.add(Calendar.WEEK_OF_YEAR, -2);
        Date date = todayStart.getTime(); 
        String starttime=tempDate.format(date);
        ParsePosition posstart = new ParsePosition(0);  
    	Date dstart = (Date) tempDate.parse(starttime, posstart);
    	ToolClass.Log(ToolClass.INFO,"EV_DOG","������־����ʼʱ��="+starttime+",="+dstart.getTime(),"dog.txt");
    	ToolClass.Log(ToolClass.INFO,"EV_DOG","Ŀ¼="+file.toString(),"dog.txt");
    	//��������ļ�����������ļ�
		File[] files = file.listFiles();
		if (files.length > 0) 
		{  
			for (int i = 0; i < files.length; i++) 
			{
			  if(!files[i].isDirectory())
			  {		
				    //3.��������ļ������ж�
		        	File fileName=new File(files[i].toString()); 
		        	if(fileName.exists())
		        	{  
		        		try
		        		{
			        		String logdatetime = getFileCreated(fileName);
			        		ParsePosition poslog = new ParsePosition(0);  
			        		Date dlog = (Date) tempDate.parse(logdatetime, poslog);
			        		ToolClass.Log(ToolClass.INFO,"EV_DOG","�ж���־Ŀ¼���ļ�="+files[i].toString()+"ʱ��="+logdatetime+",="+dlog.getTime(),"dog.txt");
			        		//�ж��Ƿ��ļ����ڱ���
			        		if(dlog.getTime()<=dstart.getTime())
			            	{
			        			ToolClass.Log(ToolClass.INFO,"EV_DOG","�ļ�="+files[i].toString()+"ɾ��","dog.txt");
			            		fileName.delete();		            		
			            	}
			        		else
			        		{
			        			ToolClass.Log(ToolClass.INFO,"EV_DOG","�ļ�="+files[i].toString()+"�ų�","dog.txt");
			        		}
		        		}
		        		catch(Exception e)
		        		{
		        			ToolClass.Log(ToolClass.INFO,"EV_DOG","�ļ�="+files[i].toString()+"�쳣���޷��ж�","dog.txt");
		        		}
		    	    } 
			  }
			}
		}    
    }
    
    /* �����������£��Ѿ��ϴ����Ľ��׼�¼����ɾ��
	  * */  
   public static void delOrders() 
   {  
	   	//1.������ʼʱ��ͽ���ʱ��
	   	SimpleDateFormat df;  
	   	String mYear,mMon,mDay;
	   	
   	   //������ʼʱ��
       Calendar todayStart = Calendar.getInstance(); 
       todayStart.setFirstDayOfWeek(Calendar.MONDAY);  
       todayStart.set(Calendar.HOUR_OF_DAY, 0);  
       todayStart.set(Calendar.MINUTE, 0);  
       todayStart.set(Calendar.SECOND, 0);  
       todayStart.set(Calendar.MILLISECOND, 0); 
       todayStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
       //����ǰ����
       todayStart.add(Calendar.WEEK_OF_YEAR, -2);
       Date date = todayStart.getTime(); 
       
       //��ȡ��
       df = new SimpleDateFormat("yyyy");//�������ڸ�ʽ
       mYear=df.format(date);
       //��ȡ��
       df = new SimpleDateFormat("MM");//�������ڸ�ʽ
       mMon=df.format(date);
       //��ȡ��
       df = new SimpleDateFormat("dd");//�������ڸ�ʽ
       mDay=df.format(date);
       String start=mYear+"-"+mMon+"-"+mDay;
       ToolClass.Log(ToolClass.INFO,"EV_DOG","���潻�׼�¼����ʼʱ��="+start,"dog.txt");
       // ����InaccountDAO����
       vmc_orderDAO orderDAO = new vmc_orderDAO(context); 
       orderDAO.deteleforserver(start);
   }
    
    /**
     * �ݹ�ɾ��certzip�ļ����ļ���
     * @param file    Ҫɾ���ĸ�Ŀ¼
     */
    public static void deleteCertFile()
    {
    	String  sDir =null;
    	 try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"CertFile";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllFile(dirName);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * �ݹ�ɾ��cert�ļ����ļ���
     * @param file    Ҫɾ���ĸ�Ŀ¼
     */
    public static void deleteCertFolder()
    {
    	String  sDir =null;
    	 try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"cert";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllFile(dirName);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ʹ��setCertFile,�����������Ŀ¼��
     */
    public static File setCertFile(String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"CertFile";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName; 
    }
    
    /**
     * ʹ��isAPKFile,�ж�����������Ѿ�����Ŀ¼��,true����,false������
     */
    public static boolean isAPKFile(String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"APKFile";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename);         	
        	//��������ڣ��򴴽��ļ�
        	if(!fileName.exists())
        	{  
        		fileext=false; 
    	    }  
        	else
        		fileext=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileext; 
    }
    /**
     * ʹ��isAPKFile,�����������Ŀ¼��
     */
    public static File setAPKFile(String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"APKFile";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName; 
    }
    /**
     * �ݹ�ɾ��APK�ļ����ļ���
     * @param file    Ҫɾ���ĸ�Ŀ¼
     */
    public static void deleteAPKFile()
    {
    	String  sDir =null;
    	 try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"APKFile";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllFile(dirName);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void deleteAllFile(File file)
    {        
        if(file.isDirectory())
        {
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0)
            {
                return;
            }
            for(File f : childFile)
            {
            	ToolClass.Log(ToolClass.INFO,"EV_SERVER","ɾ������="+f.toString(),"server.txt");										
                f.delete();
            }
        }
    }
    
    /**
     * zipFilesѹ����־��
     * @param 
     */
    public static String zipFiles() 
  	{  
  		//��������ļ�����������ļ�
  		String srcFileString=ToolClass.ReadLogFile();
  		String zipFileString=ToolClass.getEV_DIR()+File.separator+"logzip.zip";
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<srcFileString="+srcFileString+" zipFileString="+zipFileString,"log.txt"); 
  		try {
  			XZip.ZipFolder(srcFileString, zipFileString);
  		} catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		return zipFileString;
  	}
    
     
    /**
     * ʹ��isImgFile,�ж������ƷͼƬ���Ѿ�����Ŀ¼��,true����,false������
     */
    public static boolean isImgFile(String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"productImage";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+".jpg");         	
        	//��������ڣ��򴴽��ļ�
        	if(!fileName.exists())
        	{  
        		fileext=false; 
    	    }  
        	else
        		fileext=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileext; 
    }
            
    //��BitmapͼƬ�����ڱ���
    public static boolean  saveBitmaptofile(Bitmap bmp,String filename)
    {      	
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"productImage";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+".jpg");         	
        	//��������ڣ���ʼ����ͼƬ
        	if(!fileName.exists())
        	{  
        		//1.����ԭ��ͼƬ
        		CompressFormat format= Bitmap.CompressFormat.JPEG;  
    	        int quality = 100;  
    	        OutputStream stream = null;  
    	        stream = new FileOutputStream(fileName);     	        
    	        fileext=bmp.compress(format, quality, stream); 
    	        //2.ѹ���ü�ͼƬ
 	    	   //������ǰ�����Ϊtrue����ôBitmapFactory.decodeFile(String path, Options opt)��������ķ���һ��Bitmap���㣬
 	    	   //������������Ŀ���ȡ�������㣬�����Ͳ���ռ��̫����ڴ�
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                //��δ���֮��options.outWidth �� options.outHeight����������Ҫ�Ŀ�͸���
                Bitmap bmptmp = BitmapFactory.decodeFile(fileName.toString(), options);
                //������������ѹ������ֵ���������Լ����ڴ�ʹ��
                // ���ŵı����������Ǻ��Ѱ�׼���ı����������ŵģ���ֵ�������ŵı�����
                //SDK�н�����ֵ��2��ָ��ֵ,ֵԽ��ᵼ��ͼƬ������ 
                int inSampleSize = options.outWidth / 350;
                options.inSampleSize = inSampleSize; 
                //��ͼƬ�����ε�����»�ȡ��ͼƬָ����С������ͼ��
                //��ô������Ҫ�ȼ���һ������֮��ͼƬ�ĸ߶��Ƕ���,������ʾ��ô��ĳ��Ϳ��ͼƬ
                int height = options.outHeight * 350 / options.outWidth;
                options.outWidth = 350;
                options.outHeight = height;              
                //Ϊ�˽�Լ�ڴ����ǻ�����ʹ������ļ����ֶ�
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;// Ĭ����Bitmap.Config.ARGB_8888
                /* ���������ֶ���Ҫ���ʹ�� */
                options.inPurgeable = true;
                options.inInputShareable = true;
                /* �������������ķ���һ��Bitmap���� */
                options.inJustDecodeBounds = false;
                Bitmap bmp2 = BitmapFactory.decodeFile(fileName.toString(), options);
                //3.����ü���ͼƬ
        		stream = new FileOutputStream(fileName);     	        
    	        fileext=bmp2.compress(format, quality, stream); 
    	        stream.close();
    	    }  
        	else
        		fileext=false;
        } catch (Exception e) {
            e.printStackTrace();
        }   
       return fileext; 
     } 
    
    //����Ʒ��ϸ��ϢͼƬ�����ڱ���
    public static boolean  saveBitproductmaptofile(Bitmap bmp,String filename)
    {      	
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"productImage";
      	  File dirName = new File(sDir);
      	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
      	 if (!dirName.exists()) 
      	 {  
              //����ָ����·�������ļ���  
      		dirName.mkdirs(); 
           }
      	 
      	 fileName=new File(sDir+File.separator+filename+".jpg");         	
        	//��������ڣ���ʼ����ͼƬ
        	if(!fileName.exists())
        	{  
        		CompressFormat format= Bitmap.CompressFormat.JPEG;  
    	        int quality = 100;  
    	        OutputStream stream = null;  
    	        stream = new FileOutputStream(fileName);      	         
    	        fileext=bmp.compress(format, quality, stream); 
    	    }  
        	else
        		fileext=false;
        } catch (Exception e) {
            e.printStackTrace();
        }   
       return fileext; 
     } 
    
    /**
     * ʹ��getImgFile,�õ������ƷͼƬ������Ŀ¼
     */
    public static String getImgFile(String filename) 
    {
    	String  sDir =null;
    	String fileName=null;
    	try {
    		  sDir = ToolClass.getEV_DIR()+File.separator+"productImage";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	fileName=sDir+File.separator+filename+".jpg";  
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName; 
    }
    
  //��ȡһ���ַ�����ͼƬ���ӵ�ַ�б���һ���ַ�����ȡ����ȫ��ͼƬ���ӵ�ַadd��Vector<String>�з���
    public static Vector<String> parseImageURL(String text)
  	{
  		  text = text.replaceAll("< *(?i)img ", "\n<img ");
  		  String[] list = text.split("\n");
  		  if (list == null || list.length == 0)
  			  return null;
  		  Vector<String> imageList = new java.util.Vector<String>();
  		  String image;
  		  for (String line : list) 
  		  {
  			   line = line.trim();
  			   if (!line.startsWith("<img "))
  				   continue;
  			   image = line.replaceFirst("<img .*?(?i)src=\"(.*?)\".*", "$1");
  			   if (image.equals(line))
  				   continue;
  			   imageList.add(image);
  		  }
  		  return imageList.size() == 0 ? null : imageList;
  	 }
  	
  	//��ȡͼƬ����(�磺����http://xxx/xx/photo.jpg �򷵻�photo.jpg)
    public static String getImageName(String text) 
  	{
  		String[] urlArray = text.split("/");
  		String ATT_ID=urlArray[urlArray.length - 1];
  		ATT_ID=ATT_ID.substring(0,ATT_ID.lastIndexOf("."));
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","ͼƬATT_ID="+ATT_ID,"server.txt");	
  		return ATT_ID;
  	}
  	
  	//ͨ��ǰ���2������ ���԰�һ���ַ����е�����ͼƬ���ֻ�õ� ֮���������ķ�����ϣ���ӱ��ؼ��ص�ͼƬ��ַ�޸ĳ�ָ��Ŀ¼
  	//�����򷽷� ����һ���ַ��� ����ͼƬ���ƣ���photo.jpg) �˷�������������������Ϊphoto.jpg��ͼƬ���ص�ַ�������ϵ�ַ�滻�ɱ��ص�ַ(SD����Ŀ¼)��֮�󷵻��滻����ַ���
  	//�˸�ʽΪ�����ϼ���ͼƬ�ĸ�ʽ
  	//��<img src = http://xxx/xxx/xxx/a.jpg/>��ʽ
  	//�˸�ʽΪ�ӱ��ؼ���ͼƬ�ĸ�ʽ��Ŀ¼ΪSD����Ŀ¼(����ʹ��Environment.getExternalStorageDirectory()������ȡSD����Ŀ¼)
  	//�ĳ�<img src = file:///sdcard/a.jpg/>��ʽ
    public static String repImageURL(String text, String filename) {
  		String pattern = "<img src=\"" + filename + "\"";
  		String files=getImageName(filename);
  		files=ToolClass.getImgFile(files);
  	  return text.replaceAll(pattern, "<img src=\"file://"+files+"\"");
  	 }
    
    /**
     * ��ȡ����ʱ����ļ�
     */
    public static Bitmap ReadAdshuoFile() 
    {
    	String  sDir =null;
    	Bitmap bitmap=null;
    	sDir = ToolClass.getEV_DIR()+File.separator+"adshuo"+File.separator;
    	File dirName = new File(sDir);
	   	//���Ŀ¼�����ڣ��򴴽�Ŀ¼
	   	if (!dirName.exists()) 
	   	{  
          //����ָ����·�������ļ���  
   		  dirName.mkdirs(); 
        }
	    //��������ļ�����������ļ�
  		File file = new File(sDir);
  		File[] files = file.listFiles();
  		if (files.length > 0)
  		{
  			//��ʼ������б�
  			List<String> mHuoList =  new ArrayList<String>(); //�������ҳ�����б�
  			for (int i = 0; i < files.length; i++) 
			{
			  if(!files[i].isDirectory())
			  {	
				    try
	        		{
					  //�Ƿ�ͼƬ�ļ�
					  if(MediaFileAdapter.isImgFileType(files[i].toString())==true)
					  {
						  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<�������ID="+files[i].toString(),"log.txt");
						  mHuoList.add(files[i].toString());
					  }
	        		}
				    catch(Exception e)
	        		{
	        			ToolClass.Log(ToolClass.INFO,"EV_JNI","�ļ�="+files[i].toString()+"�쳣���޷��ж�","log.txt");
	        		}
			  }
			}  
  			//ѡ����Ҫ��ʾ�Ĺ��
  			if(mHuoList.size()>0)
  			{
  				Random r=new Random(); 
  				int curIndex = r.nextInt(mHuoList.size()); 
  				/*ΪʲôͼƬһ��Ҫת��Ϊ Bitmap��ʽ�ģ��� */
		        bitmap = ToolClass.getLoacalBitmap(mHuoList.get(curIndex)); //�ӱ���ȡͼƬ(��cdcard�л�ȡ)  //
		        
  			}
  		}
    	return bitmap;
    }
    
    /**
     * ��ȡ����ļ�
     */
    public static String ReadAdsFile() 
    {
    	String  sDir =null;
    	sDir = ToolClass.getEV_DIR()+File.separator+"ads"+File.separator;
    	File dirName = new File(sDir);
	   	//���Ŀ¼�����ڣ��򴴽�Ŀ¼
	   	if (!dirName.exists()) 
	   	{  
          //����ָ����·�������ļ���  
   		  dirName.mkdirs(); 
        }
    	return sDir;
    }
    
    /**
     * ʹ��isAdsFile,�ж����������Ѿ�����Ŀ¼��,true����,false������
     */
    public static boolean isAdsFile(String filename,String TypeStr,String ads) 
    {
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+ads;
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+"."+TypeStr);         	
        	//��������ڣ��򴴽��ļ�
        	if(!fileName.exists())
        	{  
        		fileext=false; 
    	    }  
        	else
        		fileext=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileext; 
    }
    
    //��BitmapͼƬ�����ڱ���
    public static boolean  saveBitmaptoads(Bitmap bmp,String TypeStr,String filename,String ads)
    {      	
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+ads;
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+"."+TypeStr);         	
        	//��������ڣ���ʼ����ͼƬ
        	if(!fileName.exists())
        	{  
        		CompressFormat format= Bitmap.CompressFormat.JPEG;  
    	        int quality = 100;  
    	        OutputStream stream = null;  
    	        stream = new FileOutputStream(fileName);      	         
    	        fileext=bmp.compress(format, quality, stream); 
    	    }  
        	else
        		fileext=false;
        } catch (Exception e) {
            e.printStackTrace();
        }   
       return fileext; 
     } 
    
    /**
     * ʹ��savetoCert,�������cert��Ŀ¼��
     */
    public static File savetoCert(String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"CertFile";
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName; 
    }
    
    /**
     * ʹ��saveAvitoads,���������Ƶ��浽Ŀ¼��
     */
    public static File saveAvitoads(String filename,String TypeStr,String ads) 
    {
    	String  sDir =null;
    	File fileName=null;
    	try {
        	sDir = ToolClass.getEV_DIR()+File.separator+ads;
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+"."+TypeStr);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName; 
    }
    
    //������ļ�ɾ��
    public static boolean  delAds(String filename,String TypeStr,String ads)
    {      	
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+ads;
        	  File dirName = new File(sDir);
        	 //���Ŀ¼�����ڣ��򴴽�Ŀ¼
        	 if (!dirName.exists()) 
        	 {  
                //����ָ����·�������ļ���  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+"."+TypeStr);   
        	 //������ڣ�ɾ��
        	 if(fileName.exists())
        	{  
        		 fileName.delete();	
    	    } 
        } catch (Exception e) {
            e.printStackTrace();
        }   
       return fileext; 
     }
    
    /**
     * ��ȡ��־�ļ�
     */
    public static String ReadLogFile() 
    {
    	String  sDir =null;
    	sDir = ToolClass.getEV_DIR()+File.separator+"logs"+File.separator;
    	return sDir;
    }
    
    /**
     * �������û�е���ɹ�֧��������΢�ŵ��˺���Ϣ�����µ���һ��
     */
    public static void CheckAliWeiFile()
    {
    	if(
    	  (AlipayConfig.getPartner()==null)||(AlipayConfig.getSeller_email()==null)
    	  ||(AlipayConfig.getKey()==null)
    	  ||(WeiConfig.getWeiappid()==null)||(WeiConfig.getWeimch_id()==null)
    	  ||(WeiConfig.getWeikey()==null)
    	  )
    	{
	    	//�������ļ���ȡ����
			Map<String, String> list=ReadConfigFile();
			if(list!=null)
			{
		        AlipayConfigAPI.SetAliConfig(list);//���ð����˺�
		        WeiConfigAPI.SetWeiConfig(list);//����΢���˺�	        
			}
			//����΢��֤��
			setWeiCertFile();
    	}
    }
    
    /**
     * ��ȡ�����ļ�
     */
    public static Map<String, String> ReadConfigFile() 
    {
    	File fileName=null;
    	String  sDir =null,str="";
		boolean isaccess=true;//true�����ļ���û������,false������

		//1.��ȡ�����ļ�,�ļ���˽�е�
		SharedPreferences user = context.getSharedPreferences("easivendconfig",0);
		Map<String,String>  list=new HashMap<String,String>();
		//��ȡ��ͨ�����ļ�
		list.put("com",user.getString("com",""));//�����ֽ𴮿ں�
		list.put("bentcom",user.getString("bentcom",""));//���ø��ӹ񴮿ں�
		list.put("columncom",user.getString("columncom",""));//�������񴮿ں�
		list.put("cardcom",user.getString("cardcom",""));//���ö��������ں�
		list.put("printcom",user.getString("printcom",""));//���ô�ӡ�����ں�
		list.put("extracom",user.getString("extracom",""));//������Э���ں�
		list.put("posip",user.getString("posip",""));//���ö�����ip
		list.put("posipport",user.getString("posipport",""));//���ö������˿�
		list.put("isallopen",user.getString("isallopen",""));//���ø��񴮿ں�
		list.put("posisssl",user.getString("posisssl",""));//�������֤���ں�
		//�������̳�
		list.put("onecakeshort",user.getString("onecakeshort",""));//���ö����ӵ�ַ
		list.put("isOnecakeshort",user.getString("isOnecakeshort",""));//�����Ƿ�ʹ�ö�����
		list.put("onecakews",user.getString("onecakews",""));//���ó����ӵ�ַ
		list.put("onecakephone",user.getString("onecakephone",""));//�����ֻ������ַ
		//֧�����˺�
		list.put("alikey",user.getString("alikey",""));
		list.put("alipartner",user.getString("alipartner",""));
		list.put("aliseller_email",user.getString("aliseller_email",""));
		list.put("alisubpartner",user.getString("alisubpartner",""));
		list.put("isalisub",user.getString("isalisub",""));
		list.put("server",user.getString("server",""));
		//΢���˺�
		list.put("weiappid",user.getString("weiappid",""));
		list.put("weikey",user.getString("weikey",""));
		list.put("weimch_id",user.getString("weimch_id",""));
		list.put("weisubmch_id",user.getString("weisubmch_id",""));
		list.put("isweisub",user.getString("isweisub",""));
		Set<Entry<String, String>> allmap8=list.entrySet();  //ʵ����
		Iterator<Entry<String, String>> iter8=allmap8.iterator();
		while(iter8.hasNext())
		{
			Entry<String, String> me=iter8.next();
			if(
					(me.getValue().equals("")!=true)
					)
			{
				isaccess=false;
			}
		}
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<configSharedPreferences="+list.toString(),"log.txt");

		//2.��ȡev�ļ�
		if(isaccess)
		{
			try {
				  sDir = ToolClass.getEV_DIR()+File.separator+"easivendconfig.txt";
				  fileName=new File(sDir);
				  //������ڣ��Ŷ��ļ�
				  if(fileName.exists())
				  {
					 //���ļ�
					  FileInputStream input = new FileInputStream(sDir);
					 //�����Ϣ
					  Scanner scan=new Scanner(input);
					  while(scan.hasNext())
					  {
						str+=scan.next()+"\n";
					  }
					  input.close();
					  scan.close();
					 ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config="+str,"log.txt");

					 //��json��ʽ���
					 list=new HashMap<String,String>();
					JSONObject object=new JSONObject(str);
					Gson gson=new Gson();
					list=gson.fromJson(object.toString(), new TypeToken<Map<String, Object>>(){}.getType());
					//Log.i("EV_JNI",perobj.toString());
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config2="+list.toString(),"log.txt");
					WriteSharedPreferencesEV(list);
				  }

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return list;
    }

    
    /**
     * д�������ļ�
     */
    public static void WriteConfigFile(String com,String bentcom,String columncom,String extracom,String cardcom,String printcom,String columncom2,
    		String posip,String posipport,String posisssl) 
    {
		//�ļ���˽�е�
		SharedPreferences  user = context.getSharedPreferences("easivendconfig",0);
		//��Ҫ�ӿڽ��б༭
		SharedPreferences.Editor list=user.edit();
		//����
		list.putString("com",com);//�����ֽ𴮿ں�
		list.putString("bentcom",bentcom);//���ø��ӹ񴮿ں�
		list.putString("columncom",columncom);//�������񴮿ں�
		list.putString("cardcom",cardcom);//���ö��������ں�
		list.putString("printcom",printcom);//���ô�ӡ�����ں�
		list.putString("extracom",extracom);//������Э���ں�
		list.putString("posip",posip);//���ö�����ip
		list.putString("posipport",posipport);//���ö������˿�
		list.putString("isallopen",columncom2);//���ø��񴮿ں�
		list.putString("posisssl",posisssl);
		//�ύ����
		list.commit();
	}
    
    //д��Ϣ�������ļ���
    private static void WriteSharedPreferencesEV(Map<String, String> list)
    {
        //�ļ���˽�е�
        SharedPreferences  user = context.getSharedPreferences("easivendconfig",0);
        //��Ҫ�ӿڽ��б༭
        SharedPreferences.Editor edit=user.edit();
        Set<Entry<String, String>> allmap8=list.entrySet();  //ʵ����
        Iterator<Entry<String, String>> iter8=allmap8.iterator();
        while(iter8.hasNext())
        {
            Entry<String, String> me=iter8.next();
            //����
            edit.putString(me.getKey(), me.getValue());
        }
        //�ύ����
        edit.commit();
    }

    
    /**
     * ����΢��֤���ļ�
     */
    public static void setWeiCertFile() 
    {
    	File fileName=null;
    	String  sDir =null,str=null,mch_id=null;
    	
    	mch_id=WeiConfig.getWeimch_id();
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"cert"+File.separator+"apiclient_cert.p12";
        	 
        	 
        	  fileName=new File(sDir);
        	  //������ڣ��Ŷ��ļ�
        	  if(fileName.exists())
        	  {
        		//ָ����ȡ֤���ʽΪPKCS12
    	    	KeyStore keyStore = KeyStore.getInstance("PKCS12");
    	    	//��ȡ������ŵ�PKCS12֤���ļ�
    	    	FileInputStream instream = new FileInputStream(sDir);
    	    	try 
    	    	{
    	    		//ָ��PKCS12������(�̻�ID)
    	    		keyStore.load(instream, mch_id.toCharArray());
	    		} 
    	    	finally 
    	    	{
    	    		instream.close();
	    		}	
    	    	SSLContext sslcontext = SSLContexts.custom()
    	        		.loadKeyMaterial(keyStore, mch_id.toCharArray()).build();
    	    	ToolClass.ssl=sslcontext.getSocketFactory(); 
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<havessl,mch_id="+mch_id,"log.txt");
        	  }
        	  else 
        	  {
        		  ToolClass.ssl=null;
        		  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<nossl","log.txt");
			  }
        	             
        } catch (Exception e) {
            e.printStackTrace();
            ToolClass.ssl=null;
            ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<sslerror","log.txt");
        }
        
    }
    
    /**
     * ���¸���֧����΢���ļ�,��̨�������·���
     */
    public static void ResetConfigFileServer(JSONObject object2,String VMC_NO)
	{
		try {
			Map<String,String> list2=new HashMap<String,String>();
			//֧����
			int ALIPAYMODE=1;
			if((ToolClass.isEmptynull(object2.get("ALIPAYMODE").toString())==false)
					&&(object2.get("ALIPAYMODE").toString().equals("null")==false)
					)
				ALIPAYMODE=Integer.parseInt(object2.get("ALIPAYMODE").toString());

			//����֧�����Ƿ��Ѿ���
			int getZhifubaoer=0;
            vmc_system_parameterDAO parameterDAOrd = new vmc_system_parameterDAO(context);// ����InaccountDAO����
            // ��ȡ����������Ϣ�����洢��List���ͼ�����
			Tb_vmc_system_parameter tb_inaccount = parameterDAOrd.find();
			if(tb_inaccount!=null)
			{
				getZhifubaoer=tb_inaccount.getZhifubaoer();
			}

			//2.0
			if(ALIPAYMODE==2)
			{
				ALIPAYMODE=(getZhifubaoer>0)?ALIPAYMODE:getZhifubaoer;
                vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// ����InaccountDAO����
                //����Tb_inaccount���� 
                Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(VMC_NO, "", 0,0, 
                      0,0,"",0,0,0,ALIPAYMODE,0,0,0,"",0,
                      0,0, 0,0,0,"","");
                ToolClass.Log(ToolClass.INFO,"EV_SERVER","����֧����VMC_NO="+tb_vmc_system_parameter.getDevID()+",zhifubaoer="+tb_vmc_system_parameter.getZhifubaoer(),"server.txt");    
                parameterDAO.updatezhifubao(tb_vmc_system_parameter);

				list2.put("alipartner", object2.get("ALIPAYTWO_PID").toString());
				list2.put("aliseller_email", "");
				list2.put("alikey", "");
				list2.put("alisubpartner", object2.get("ALIPAYTWO_ALIOTHERPARTNER").toString());
				if((ToolClass.isEmptynull(object2.get("ALIPAYTWO_MERCHANT_PRIVATE_ANDROID_KEY").toString())==false)
						&&(object2.get("ALIPAYTWO_MERCHANT_PRIVATE_ANDROID_KEY").toString().equals("null")==false)
						)
				{
					list2.put("aliprivateKey", object2.get("ALIPAYTWO_MERCHANT_PRIVATE_ANDROID_KEY").toString());
				}
				else
				{
					list2.put("aliprivateKey", object2.get("ALIPAYTWO_MERCHANT_PRIVATE_KEY").toString());
				}

				if(ToolClass.isEmptynull(object2.get("ALIPAYTWO_ALIOTHERPARTNER").toString()))
				{
					list2.put("isalisub", "0");
				}
				else
				{
					list2.put("isalisub", "0.995");
				}
			}
			//1.0
			else
			{
				ALIPAYMODE=(getZhifubaoer>0)?ALIPAYMODE:getZhifubaoer;
                vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// ����InaccountDAO����
                //����Tb_inaccount���� 
                Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(VMC_NO, "", 0,0, 
                      0,0,"",0,0,0,ALIPAYMODE,0,0,0,"",0,
                      0,0, 0,0,0,"","");
                ToolClass.Log(ToolClass.INFO,"EV_SERVER","����֧����VMC_NO="+tb_vmc_system_parameter.getDevID()+",zhifubaoer="+tb_vmc_system_parameter.getZhifubaoer(),"server.txt");    
                parameterDAO.updatezhifubao(tb_vmc_system_parameter);

				list2.put("alipartner", object2.get("ALI_PARTNER").toString());
				list2.put("aliseller_email", object2.get("ALI_SELLER_EMAIL").toString());
				list2.put("alikey", object2.get("ALI_SECURITY_KEY").toString());
				list2.put("alisubpartner", object2.get("ALI_OTHER_PARTNER").toString());
				list2.put("aliprivateKey", "");
				if(ToolClass.isEmptynull(object2.get("ALI_OTHER_PARTNER").toString()))
				{
					list2.put("isalisub", "0");
				}
				else
				{
					list2.put("isalisub", "0.995");
				}
			}

			//΢��
			list2.put("weiappid", object2.get("WX_APP_ID").toString());
			list2.put("weimch_id", object2.get("WX_MCHID").toString());
			list2.put("weikey", object2.get("WX_KEY").toString());
			list2.put("weisubmch_id", object2.get("WX_OTHER_MCHID").toString());
			if(ToolClass.isEmptynull(object2.get("WX_OTHER_MCHID").toString()))
			{
				list2.put("isweisub", "0");
			}
			else
			{
				list2.put("isweisub", "1");
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","APP<<config3="+list2.toString(),"server.txt");
			WriteSharedPreferencesEV(list2);

			//2.����֧����΢���˺�
			AlipayConfigAPI.SetAliConfig(list2);//���ð����˺�
			WeiConfigAPI.SetWeiConfig(list2);//����΢���˺�
			//����΢��֤��
			ToolClass.setWeiCertFile();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    
    /**
     * ��ȡ���������ļ�
     */
    public static Map<String, Integer> ReadColumnFile() 
    {
    	File fileName=null;
    	String  sDir =null,str="";
    	huodaolist=new HashMap<Integer,Integer>(); 
    	//1.��ȡ�����ļ�,�ļ���˽�е�
		SharedPreferences user = context.getSharedPreferences("evColumnconfig",0);
		Map<String,Integer>  list=new HashMap<String,Integer>();
		str=user.getString("column","");
		//2.��ȡevColumnconfig�ļ�
    	if(str.equals(""))  
    	{
	        try {
	        	  sDir = ToolClass.getEV_DIR()+File.separator+"evColumnconfig.txt";
	        	  fileName=new File(sDir);
	        	  //������ڣ��Ŷ��ļ�
	        	  if(fileName.exists())
	        	  {
		    	  	 //���ļ�
		    		  FileInputStream input = new FileInputStream(sDir);
		    		 //�����Ϣ
		  	          Scanner scan=new Scanner(input);
		  	          while(scan.hasNext())
		  	          {
		  	           	str+=scan.next()+"\n";
		  	          }
		  	         ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config="+str,"com.txt");
		  	         input.close();
		  	         scan.close();		  	         
	        	  }
	        	  //���浽�����ļ���
	        	  WriteColumnFile(str);	        	 
	        	             
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
    	}
    	//3.���뵽list�б�
    	try {
	    	//��json��ʽ��� 
			JSONObject object=new JSONObject(str);      				
			Gson gson=new Gson();
			list=gson.fromJson(object.toString(), new TypeToken<Map<String, Integer>>(){}.getType());
			//�������
	        Set<Entry<String, Integer>> allmap=list.entrySet();  //ʵ����
	        Iterator<Entry<String, Integer>> iter=allmap.iterator();
	        while(iter.hasNext())
	        {
	            Entry<String, Integer> me=iter.next();	
	        	huodaolist.put(Integer.parseInt(me.getKey()),(Integer)me.getValue());		            
	        } 			
			//Log.i("EV_JNI",perobj.toString());
			ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config2="+huodaolist.toString(),"com.txt");
		} catch (Exception e) {
            e.printStackTrace();
        }
		return list;
    }
    
    /**
     * ��ȡ�߼�����������ñ�
     */
    public static Map<Integer, Integer> getHuodaolist() {
		return huodaolist;
	}
    
    /**
     * ���ɳ���
     */
    public static int columnChuhuo(Integer logic)
    {
    	int val=0;
    	if(huodaolist!=null)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]huodaolist="+huodaolist,"com.txt");
    		if(huodaolist.containsKey(logic))
    		{
    			//����keyȡ������
    		    val=huodaolist.get(logic); 
    		    ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]logic="+logic+"physic="+val,"com.txt");
    		}
    	}
    	return val;
    }
    
    
    /**
     * ��ȡ������������ļ�
     */
    public static Map<String, Integer> ReadColumnFile2() 
    {
    	File fileName=null;
    	String  sDir =null,str="";
    	huodaolist2=new HashMap<Integer,Integer>(); 
    	//1.��ȡ�����ļ�,�ļ���˽�е�
		SharedPreferences user = context.getSharedPreferences("evColumnconfig2",0);
		Map<String,Integer>  list=new HashMap<String,Integer>();
		str=user.getString("column","");
		//2.��ȡevColumnconfig�ļ�
    	if(str.equals(""))  
    	{
	        try {
	        	  sDir = ToolClass.getEV_DIR()+File.separator+"evColumnconfig2.txt";
	        	  fileName=new File(sDir);
	        	  //������ڣ��Ŷ��ļ�
	        	  if(fileName.exists())
	        	  {
		    	  	 //���ļ�
		    		  FileInputStream input = new FileInputStream(sDir);
		    		 //�����Ϣ
		  	          Scanner scan=new Scanner(input);
		  	          while(scan.hasNext())
		  	          {
		  	           	str+=scan.next()+"\n";
		  	          }
		  	         ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config="+str,"com.txt");
		  	         input.close();
		  	         scan.close();		  	         
	        	  }
	        	  //���浽�����ļ���
	        	  WriteColumnFile2(str);	        	 
	        	             
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
    	}
    	//3.���뵽list�б�
    	try {
	    	//��json��ʽ��� 
			JSONObject object=new JSONObject(str);      				
			Gson gson=new Gson();
			list=gson.fromJson(object.toString(), new TypeToken<Map<String, Integer>>(){}.getType());
			//�������
	        Set<Entry<String, Integer>> allmap=list.entrySet();  //ʵ����
	        Iterator<Entry<String, Integer>> iter=allmap.iterator();
	        while(iter.hasNext())
	        {
	            Entry<String, Integer> me=iter.next();	
	        	huodaolist2.put(Integer.parseInt(me.getKey()),(Integer)me.getValue());		            
	        } 			
			//Log.i("EV_JNI",perobj.toString());
			ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config2="+huodaolist2.toString(),"com.txt");
		} catch (Exception e) {
            e.printStackTrace();
        }
		return list;
    }
    
    /**
     * ��ȡ�����߼�����������ñ�
     */
    public static Map<Integer, Integer> getHuodaolist2() {
		return huodaolist2;
	}
    
    /**
     * ���񵯻ɳ���
     */
    public static int columnChuhuo2(Integer logic)
    {
    	int val=0;
    	if(huodaolist2!=null)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]huodaolist2="+huodaolist2,"com.txt");
    		if(huodaolist2.containsKey(logic))
    		{
    			//����keyȡ������
    		    val=huodaolist2.get(logic); 
    		    ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]logic="+logic+"physic="+val,"com.txt");
    		}
    	}
    	return val;
    }
    
    /**
     * ����д����������ļ�
     */
    public static void WriteColumnFile2(String str) 
    {
    	//�ļ���˽�е�
		SharedPreferences  user = context.getSharedPreferences("evColumnconfig2",0);
		//��Ҫ�ӿڽ��б༭
		SharedPreferences.Editor list=user.edit();
		list.putString("column",str);
		//�ύ����
		list.commit();     	  
    }
    
    
    /**
     * ���ɷ��س������
     *   GOODS_SOLD_ERR          (1 << 0)   //bit0�ܹ���λ
	* 	 GOODS_SOLDOUT_BIT       (1 << 1)   //bit1�������
	* 	 MOTO_MISPLACE_BIT       (1 << 2)   //bit2�����ת֮ǰ�Ͳ�����ȷ��λ����(Ҳ��������)
	* 	 MOTO_NOTMOVE_BIT        (1 << 3)   //bit3�������ת(Ҳ��������)
	* 	 MOTO_NOTRETURN_BIT      (1 << 4)   //bit4���ûת����ȷλ��(Ҳ��������)
	* 	 GOODS_NOTPASS_BIT       (1 << 5)   //bit5��Ʒû��(����ȷ��û��⵽)
	* 	 DRV_CMDERR_BIT          (1 << 6)   //bit6�������(ֻ�з�������Ͳ�ѯ������������������������������ͱ���)
	* 	 DRV_GOCERR_BIT          (1 << 7)   //bit7�������ģ��״̬(GOC����)
	 * 1:�ɹ�;0:����;2:����������;3:���δ��λ;4:�޻� 5:ͨ�Ź���
     */
    public static int colChuhuorst(int Rst)
    {
    	int GOODS_SOLD_ERR=(1 << 0);   //bit0�ܹ���λ
    	int GOODS_SOLDOUT_BIT=(1 << 1);   //bit1�������
    	int MOTO_MISPLACE_BIT=(1 << 2);  //bit2�����ת֮ǰ�Ͳ�����ȷ��λ����(Ҳ��������)
    	int MOTO_NOTMOVE_BIT=(1 << 3); //bit3�������ת(Ҳ��������)
    	int MOTO_NOTRETURN_BIT=(1 << 4);   //bit4���ûת����ȷλ��(Ҳ��������)
    	int GOODS_NOTPASS_BIT=(1 << 5);   //bit5��Ʒû��(����ȷ��û��⵽)
//    	int DRV_CMDERR_BIT   =(1 << 6);   //bit6�������(ֻ�з�������Ͳ�ѯ������������������������������ͱ���)
    	int DRV_GOCERR_BIT   =(1 << 7);   //bit7�������ģ��״̬(GOC����)
    	if(Rst == 0x00)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_OK","com.txt");
    		return 1;	
    	}
    	else if(Rst == 0xff)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_COMERR","com.txt");
    		return 5;
    	}	
    	//1.�ж�GOC�Ƿ����bit7:  GOC����->��Ǯ
    	else if((Rst&DRV_GOCERR_BIT)>0)
    	{
    		if((Rst&GOODS_SOLD_ERR)>0)
    		{
    			//2.���жϵ����
    			//ûת��λ������״��bit1,bit2,bit3,bit4->�����ù���  
    			if( (Rst & (GOODS_SOLDOUT_BIT|MOTO_MISPLACE_BIT|MOTO_NOTMOVE_BIT|MOTO_NOTRETURN_BIT))>0) 
    			{
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_3","com.txt");
    				return 3;								
    			}
    			else
    			{
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_4","com.txt");
    				return 4;	
    			}
    		}
    		else
    		{
    			ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_1","com.txt");
    			return 1;
    		}
    	}
    	else if((Rst&GOODS_SOLD_ERR)>0)
    	{
    		/*
    		//���δת��
    		if((GocType==1)&&(Rst&0x08))
    			return 1;
    		//����ȷ�Ͽ���������ȷ��δ��⵽��Ʒ����
    		if((GocType==1)&&(Rst&0x20))
    			return 4;	
    		else
    		//����ȷ�Ͽ��������δת��λ��������ȷ�ϼ�⵽��Ʒ����
    		if((GocType==1)&&(Rst==0x11))
    			return 0;
    		else
    		//����ȷ��δ���������δת��λ
    		if((GocType==0)&&(Rst&0x10))
    			return 3;
    		else
    		//����ȷ��δ�������������ת��
    		if((GocType==0)&&(Rst&0x08))
    			return 1;
    		*/
    		/*********GOC�򿪵������********************/
    		if(ToolClass.getGoc()==1)
    		{			
    			//1.���δת��,����Ǯ
    			if((Rst&MOTO_NOTMOVE_BIT)>0)
    			{
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_0","com.txt");
    				return 0;
    			}			
    			//2.���ж�GOC�Ƿ��⵽bit5,  ==0˵������ȷ�ϼ�⵽��Ʒ���� 			
    			else if((Rst & GOODS_NOTPASS_BIT) == 0) 
    			{				
    				//�м�⵽->�����ɹ���Ǯ  
    				/*************************************************************************/
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_1","com.txt");
    				return 1;		
    			}
    			else
    			{ 
    				   //û��⵽->����ʧ�ܲ���Ǯ
    				   //3.���жϵ����
    				   //ûת��λ������״��bit1,bit2,bit3,bit4->�����ù���	
    				   if( (Rst & (GOODS_SOLDOUT_BIT|MOTO_MISPLACE_BIT|MOTO_NOTMOVE_BIT|MOTO_NOTRETURN_BIT))>0) 
    				   {
    						ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_3","com.txt");
    						return 3;
    				   }
    				   else
    				   {
    						ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_4","com.txt");
    						return 4;
    				   }				   
    			}
    			
    		}	
    		/*********GOC�رյ������********************/
    		else
    		{
    			//2.���жϵ����
    		   //ûת��λ������״��bit1,bit2,bit3,bit4->�����ù���	
    		   if( (Rst & (GOODS_SOLDOUT_BIT|MOTO_MISPLACE_BIT|MOTO_NOTMOVE_BIT|MOTO_NOTRETURN_BIT))>0) 
    		   {
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_3","com.txt");
    				return 3;								
    		   }
    		   else
    		   {
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_4","com.txt");
    				return 4;	
    		   }
    		}
    	}
    	return 0;
    }
    
    
    
    /**
     * д����������ļ�
     */
    public static void WriteColumnFile(String str) 
    {
    	//�ļ���˽�е�
		SharedPreferences  user = context.getSharedPreferences("evColumnconfig",0);
		//��Ҫ�ӿڽ��б༭
		SharedPreferences.Editor list=user.edit();
		list.putString("column",str);
		//�ύ����
		list.commit(); 
    }
    
    /*=============
     * ������ģ��
     =============*/
    /**
     * ��ȡ���������ļ�
     */
    public static Map<String, Integer> ReadElevatorFile() 
    {
    	File fileName=null;
    	String  sDir =null,str="";
    	elevatorlist=new HashMap<Integer,Integer>(); 
    	//1.��ȡ�����ļ�,�ļ���˽�е�
		SharedPreferences user = context.getSharedPreferences("evElevatorconfig",0);
		Map<String,Integer>  list=new HashMap<String,Integer>();
		str=user.getString("column","");
		//2.��ȡevColumnconfig�ļ�
    	if(str.equals(""))  
    	{
	        try {
	        	  sDir = ToolClass.getEV_DIR()+File.separator+"evElevatorconfig.txt";
	        	  fileName=new File(sDir);
	        	  //������ڣ��Ŷ��ļ�
	        	  if(fileName.exists())
	        	  {
		    	  	 //���ļ�
		    		  FileInputStream input = new FileInputStream(sDir);
		    		 //�����Ϣ
		  	          Scanner scan=new Scanner(input);
		  	          while(scan.hasNext())
		  	          {
		  	           	str+=scan.next()+"\n";
		  	          }
		  	         ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config="+str,"com.txt");
		  	         input.close();
		  	         scan.close();		  	         
	        	  }
	        	  //���浽�����ļ���
	        	  WriteElevatorFile(str);	        	 
	        	             
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
    	}
    	//3.���뵽list�б�
    	try {
	    	//��json��ʽ��� 
			JSONObject object=new JSONObject(str);      				
			Gson gson=new Gson();
			list=gson.fromJson(object.toString(), new TypeToken<Map<String, Integer>>(){}.getType());
			//�������
	        Set<Entry<String, Integer>> allmap=list.entrySet();  //ʵ����
	        Iterator<Entry<String, Integer>> iter=allmap.iterator();
	        while(iter.hasNext())
	        {
	            Entry<String, Integer> me=iter.next();	
	            elevatorlist.put(Integer.parseInt(me.getKey()),(Integer)me.getValue());		            
	        } 			
			//Log.i("EV_JNI",perobj.toString());
			ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config2="+elevatorlist.toString(),"com.txt");
		} catch (Exception e) {
            e.printStackTrace();
        }
		return list;		
    }
    
    /**
     * ����������
     */
    public static int elevatorChuhuo(Integer logic)
    {
    	int val=0;
    	if(elevatorlist!=null)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]elevatorlist="+elevatorlist,"com.txt");
    		if(elevatorlist.containsKey(logic))
    		{
    			//����keyȡ������
    		    val=elevatorlist.get(logic); 
    		    ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]logic="+logic+"physic="+val,"com.txt");
    		}
    	}
    	return val;
    }
    /**
     * д�����������������ļ�
     */
    public static void WriteElevatorFile(String str) 
    {
    	//�ļ���˽�е�
		SharedPreferences  user = context.getSharedPreferences("evElevatorconfig",0);
		//��Ҫ�ӿڽ��б༭
		SharedPreferences.Editor list=user.edit();
		list.putString("column",str);
		//�ύ����
		list.commit();     	     
    }
    
    /**
     * ��ȡ���������������ļ�
     */
    public static Map<String, Integer> ReadElevatorFile2() 
    {
    	File fileName=null;
    	String  sDir =null,str="";
    	elevatorlist2=new HashMap<Integer,Integer>(); 
    	//1.��ȡ�����ļ�,�ļ���˽�е�
		SharedPreferences user = context.getSharedPreferences("evElevatorconfig2",0);
		Map<String,Integer>  list=new HashMap<String,Integer>();
		str=user.getString("column","");
		//2.��ȡevColumnconfig�ļ�
    	if(str.equals(""))  
    	{
	        try {
	        	  sDir = ToolClass.getEV_DIR()+File.separator+"evElevatorconfig2.txt";
	        	  fileName=new File(sDir);
	        	  //������ڣ��Ŷ��ļ�
	        	  if(fileName.exists())
	        	  {
		    	  	 //���ļ�
		    		  FileInputStream input = new FileInputStream(sDir);
		    		 //�����Ϣ
		  	          Scanner scan=new Scanner(input);
		  	          while(scan.hasNext())
		  	          {
		  	           	str+=scan.next()+"\n";
		  	          }
		  	         ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config="+str,"com.txt");
		  	         input.close();
		  	         scan.close();		  	         
	        	  }
	        	  //���浽�����ļ���
	        	  WriteElevatorFile2(str);	        	 
	        	             
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
    	}
    	//3.���뵽list�б�
    	try {
	    	//��json��ʽ��� 
			JSONObject object=new JSONObject(str);      				
			Gson gson=new Gson();
			list=gson.fromJson(object.toString(), new TypeToken<Map<String, Integer>>(){}.getType());
			//�������
	        Set<Entry<String, Integer>> allmap=list.entrySet();  //ʵ����
	        Iterator<Entry<String, Integer>> iter=allmap.iterator();
	        while(iter.hasNext())
	        {
	            Entry<String, Integer> me=iter.next();	
	            elevatorlist2.put(Integer.parseInt(me.getKey()),(Integer)me.getValue());		            
	        } 			
			//Log.i("EV_JNI",perobj.toString());
			ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config2="+elevatorlist2.toString(),"com.txt");
		} catch (Exception e) {
            e.printStackTrace();
        }
		return list;
    }
    
    /**
     * �������������
     */
    public static int elevatorChuhuo2(Integer logic)
    {
    	int val=0;
    	if(elevatorlist2!=null)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]elevatorlist2="+elevatorlist2,"com.txt");
    		if(elevatorlist2.containsKey(logic))
    		{
    			//����keyȡ������
    		    val=elevatorlist2.get(logic); 
    		    ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]logic="+logic+"physic="+val,"com.txt");
    		}
    	}
    	return val;
    }
    /**
     * д��������������������ļ�
     */
    public static void WriteElevatorFile2(String str) 
    {
    	//�ļ���˽�е�
		SharedPreferences  user = context.getSharedPreferences("evElevatorconfig2",0);
		//��Ҫ�ӿڽ��б༭
		SharedPreferences.Editor list=user.edit();
		list.putString("column",str);
		//�ύ����
		list.commit();     	  
    }
    
    /**
     * ���������س������
    private static final int LIFT_VENDOUT_COM_ERR	=		0x1F;		//ͨ�Ź���
	private static final int LIFT_VENDOUT_FAULT		=		0x12;		//����������
	private static final int LIFT_VENDOUT_BUSY		=		0x11;		//������æ
	private static final int LIFT_VENDOUT_FAIL		=		0;			//����ʧ�� ͨ��ʧ��
	private static final int LIFT_VENDOUT_SUC		=		1;			//�����ɹ�
	private static final int LIFT_VENDOUT_DATAERR	=		2;			//���ݴ���
	private static final int LIFT_VENDOUT_EMPTY		=		3;			//�޻�
	private static final int LIFT_VENDOUT_STUCK		=		4 ; 			//����
	private static final int LIFT_VNEDOUT_DOOR_NOT_OPEN	=	5;			//ȡ����δ����
	private static final int LIFT_VENDOUT_GOODS_NOT_TAKE=	6;			//����δȡ��
	private static final int LIFT_VENDOUT_OTHER_FAULT	=	7	;		//��������
	private static final int LIFT_VENDOUT_VENDING		=	0x88;		//���ڳ���
	 *  1:�ɹ�;4:�޻�8:����;�����������һ��
     */    
    public static int elevatorChuhuorst(int Rst)
    {    
//    	int LIFT_VENDOUT_COM_ERR	=		0x1F;		//ͨ�Ź���
//    	int LIFT_VENDOUT_FAULT		=		0x12;		//����������
//    	int LIFT_VENDOUT_BUSY		=		0x11;		//������æ
//    	int LIFT_VENDOUT_FAIL		=		0;			//����ʧ�� ͨ��ʧ��
//    	int LIFT_VENDOUT_SUC		=		1;			//�����ɹ�
//    	int LIFT_VENDOUT_DATAERR	=		2;			//���ݴ���
    	int LIFT_VENDOUT_EMPTY		=		3;			//�޻�
    	int LIFT_VENDOUT_STUCK		=		4 ; 			//����
//    	int LIFT_VNEDOUT_DOOR_NOT_OPEN	=	5;			//ȡ����δ����
//    	int LIFT_VENDOUT_GOODS_NOT_TAKE=	6;			//����δȡ��
//    	int LIFT_VENDOUT_OTHER_FAULT	=	7	;		//��������
//    	int LIFT_VENDOUT_VENDING		=	0x88;		//���ڳ���
    	if(Rst==LIFT_VENDOUT_EMPTY)
    	{
    		return 4;
    	}
    	else if(Rst==LIFT_VENDOUT_STUCK)
    	{
    		return 8;
    	}
    	else
    	{
    		return Rst;
    	}
    }
    
    /*=============
     * ѡ������ģ��
     =============*/
    /**
     * ��ȡѡ�������ļ�
     */
    public static Map<String,String> ReadSelectFile() 
    {
    	File fileName=null;
    	String  sDir =null,str="";
    	Map<String,String> list=null;
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evSelectconfig.txt";
        	  fileName=new File(sDir);
        	  //������ڣ��Ŷ��ļ�
        	  if(fileName.exists())
        	  {
	    	  	 //���ļ�
	    		  FileInputStream input = new FileInputStream(sDir);
	    		 //�����Ϣ
	  	          Scanner scan=new Scanner(input);
	  	          while(scan.hasNext())
	  	          {
	  	           	str+=scan.next()+"\n";
	  	          }
	  	         ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config="+str,"com.txt");
	  	         input.close();
	  	         scan.close();
	  	         //��json��ʽ���
	  	         list=new HashMap<String,String>();   
	  	         selectlist=new HashMap<String,String>(); 
				JSONObject object=new JSONObject(str);      				
				Gson gson=new Gson();
				list=gson.fromJson(object.toString(), new TypeToken<Map<String,String>>(){}.getType());
				//�������
		        Set<Entry<String,String>> allmap=list.entrySet();  //ʵ����
		        Iterator<Entry<String,String>> iter=allmap.iterator();
		        while(iter.hasNext())
		        {
		            Entry<String,String> me=iter.next();	
		            selectlist.put(me.getKey(),me.getValue());		            
		        } 			
				//Log.i("EV_JNI",perobj.toString());
				ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config2="+selectlist.toString(),"com.txt");
        	  }
        	             
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * д�밴�������ļ�
     */
    public static void WriteSelectFile(String selectKey,String productID) 
    {
    	File fileName=null;
    	String  sDir =null;
    	
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evSelectconfig.txt";
        	 
        	  fileName=new File(sDir);
        	  //��������ڣ��򴴽��ļ�
          	  if(!fileName.exists())
          	  {  
      	        fileName.createNewFile(); 
      	      }  
  	          
          	  ReadSelectFile();
          	  selectlist.put(selectKey,productID);
          	  ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<selectlist="
                	  +selectlist.toString(),"com.txt");
          	  Map<String,String> list=new HashMap<String,String>();
              //ɾ�������ͬ��Valueֵ
			  for(Map.Entry entry:selectlist.entrySet())
			  {
				 if(entry.getValue().equals(productID)!=true)
				 {
					 list.put(entry.getKey().toString(), entry.getValue().toString());
				 }				 
			  }
			  ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<list="
                	  +list.toString(),"com.txt");
          	  
			  JSONObject obj=new JSONObject();	
          	  //������Ĳ������
			  for(Map.Entry entry:list.entrySet())
			  {
			      obj.put(entry.getKey().toString(), entry.getValue().toString());
			  }
			  //��������ӽ���
			  obj.put(selectKey, productID);
			  
          	  ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<JSONObject="+obj.toString(),"com.txt");
  	          //��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
  	          FileWriter writer = new FileWriter(fileName, false);  	          
  	          writer.write(obj.toString());
  	          writer.close();	
  	          
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /**
     * ɾ�����������ļ�
     */
    public static void DelSelectFile() 
    {
    	File fileName=null;
    	String  sDir =null;
    	
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evSelectconfig.txt";
        	 
        	  fileName=new File(sDir);
        	  //��������ڣ��򴴽��ļ�
          	  if(!fileName.exists())
          	  {  
      	        fileName.createNewFile(); 
      	      }  
          	  fileName.delete();
  	          
          	  ReadSelectFile();	
  	          
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    //���������־
    public static void addOptLog(Context context, int logType, String logDesc)
	{
    	String id="";
 	    vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// ����InaccountDAO����
	    // �õ��豸ID��
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		id=tb_inaccount.getDevhCode().toString();
    	}
    	Log.i("EV_JNI","Send0.0="+id);
    	SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //��ȷ������ 
        String datetime = tempDate.format(new java.util.Date()).toString(); 					
        String logID="log"+id+datetime;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//�������ڸ�ʽ
    	String date=df.format(new Date());
    	       
    	vmc_logDAO logDAO = new vmc_logDAO(context);// ����InaccountDAO����		
    	Tb_vmc_log tb_vmc_log=new Tb_vmc_log(logID, logType, logDesc,
    			date);
		logDAO.add(tb_vmc_log);		
	}
	
	//���ͽ����������Ԫ,תΪ�Է�Ϊ��λ���͵�����
	public static int MoneySend(float sendMoney)
	{
		BigDecimal x1 = new BigDecimal(Float.toString(sendMoney));
		BigDecimal mulpar= new BigDecimal(100);
		x1=x1.multiply(mulpar);
		int values=x1.intValue();
		return values; 
	}
	
	//���ս��ת�����������յķ�,תΪ�����Ԫ
	public static float MoneyRec(long Money)
	{
		BigDecimal x1 = new BigDecimal(Money);
		BigDecimal divpar= new BigDecimal(100);		
		x1=x1.divide(divpar);
		float amount = x1.floatValue();
		return amount;
	}
	
	/**
	 * �������Uri��������ļ�ϵͳ�е�·��
	 *
	 * @param context
	 * @param uri
	 * @return the file path or null
	 */
	public static String getRealFilePath( final Context context, final Uri uri )
	{
	    if ( null == uri ) 
	    	return "";
	    final String scheme = uri.getScheme();
	    String data = null;
	    if ( scheme == null )
	        data = uri.getPath();
	    else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
	        data = uri.getPath();
	    } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
	        Cursor cursor = context.getContentResolver().query( uri, new String[] { ImageColumns.DATA }, null, null, null );
	        if ( null != cursor ) {
	            if ( cursor.moveToFirst() ) {
	                int index = cursor.getColumnIndex( ImageColumns.DATA );
	                if ( index > -1 ) {
	                    data = cursor.getString( index );
	                }
	            }
	            cursor.close();
	        }
	    }
	    return data;
	}
	
	/**
     * ���ر���ͼƬ
     * @param url
     * @return
     */
	public static Bitmap getLoacalBitmap(String url) {
        try {
        	 BitmapFactory.Options opt = new BitmapFactory.Options();  
        	 opt.inPreferredConfig = Bitmap.Config.RGB_565;   
        	 opt.inPurgeable = true;  
        	 opt.inInputShareable = true;  
             FileInputStream fis = new FileInputStream(url);
             return BitmapFactory.decodeStream(fis,null,opt);  ///����ת��ΪBitmapͼƬ        

          } catch (FileNotFoundException e) {
             e.printStackTrace();
             return null;
        }
   }
     
     /**
      * @��������˵��: ���ɶ�ά��ͼƬ,ʵ��ʹ��ʱҪ��ʼ��sweepIV,��Ȼ�ᱨ��ָ�����
      * @����:������
      * @ʱ��:2013-4-18����11:14:16
      * @����: @param url Ҫת���ĵ�ַ���ַ���,����������
      * @return void
      * @throws
      */
     //Ҫת���ĵ�ַ���ַ���,����������
     public static Bitmap createQRImage(String url)
     {
    	int QR_WIDTH = 230;
		int QR_HEIGHT = 230; 
     	try
     	{
     		//�ж�URL�Ϸ���
     		if (url == null || "".equals(url) || url.length() < 1)
     		{
     			return null;
     		}
     		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
     		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
     		//ͼ������ת����ʹ���˾���ת��
     		BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
     		int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
     		//�������ﰴ�ն�ά����㷨��������ɶ�ά���ͼƬ��
     		//����forѭ����ͼƬ����ɨ��Ľ��
     		for (int y = 0; y < QR_HEIGHT; y++)
     		{
     			for (int x = 0; x < QR_WIDTH; x++)
     			{
     				if (bitMatrix.get(x, y))
     				{
     					pixels[y * QR_WIDTH + x] = 0xff000000;
     				}
     				else
     				{
     					pixels[y * QR_WIDTH + x] = 0xffffffff;
     				}
     			}
     		}
     		//���ɶ�ά��ͼƬ�ĸ�ʽ��ʹ��ARGB_8888
     		Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
     		bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
     		return bitmap;
     	}
     	catch (WriterException e)
     	{
     		e.printStackTrace();
     	}
		return null;
     }    
     
    //out_trade_no���������ɶ�����
 	public static String out_trade_no(Context context)
 	{
 		String id="";
 	    String out_trade_no=null;
 		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// ����InaccountDAO����
	    // �õ��豸ID��
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		id=tb_inaccount.getDevID().toString();
    	}
    	Log.i("EV_JNI","Send0.0="+id);
    	SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //��ȷ������ 
        String datetime = tempDate.format(new java.util.Date()).toString(); 					
        out_trade_no=id+datetime;
        return out_trade_no;
 	}
 	
 	//vmc_no�������õ��豸id,ǩ����id��
 	public static Map<String, String> getvmc_no(Context context)
 	{
 		Map<String,String> allSet = new HashMap<String,String>() ;
 	    vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// ����InaccountDAO����
	    // �õ��豸ID��
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		allSet.put("vmc_no",tb_inaccount.getDevID().toString());
    		allSet.put("vmc_auth_code",tb_inaccount.getDevhCode().toString());
    		ToolClass.setVmc_no(tb_inaccount.getDevID().toString());
    	}
    	return allSet;
 	}
 	
 	/*********************************************************************************************************
	** Function name:     	typestr
	** Descriptions:	       ����������״̬ת���ַ�����ʾ
	** input parameters:    type=0֧����ʽ,1����״̬,2�˿�״̬
	** payType;// ֧����ʽ0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��
	** payStatus;// ����״̬0�����ɹ���1����ʧ�ܣ�2֧��ʧ�ܣ�3δ֧��
	** RealStatus;// �˿�״̬��0����ʾδ�����˿����1�˿���ɣ�2�����˿3�˿�ʧ��
	** logType    ��������0���,1�޸�,2ɾ��
	** output parameters:   ��
	** Returned value:      ��
	*********************************************************************************************************/
	public static String typestr(int type,int value)
	{
		switch(type)
		{
			case 0:// ֧����ʽ
				// ֧����ʽ0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��
				switch(value)
				{
					case 0:
						return "�ֽ�";						
					case 1:
						return "����";	
					case 2:
						return "֧��������";
					case 3:
						return "֧������ά��";
					case 4:
						return "΢֧��";		
				}
				break;
			case 1:// ����״̬
				// ����״̬0�����ɹ���1����ʧ�ܣ�2֧��ʧ�ܣ�3δ֧��
				switch(value)
				{
					case 0:
						return "�����ɹ�";						
					case 1:
						return "����ʧ��";	
					case 2:
						return "֧��ʧ��";
					case 3:
						return "δ֧��";					
				}
				break;
			case 2:// �˿�״̬
				// �˿�״̬��0����ʾδ�����˿����1�˿���ɣ�2�����˿3�˿�ʧ��
				switch(value)
				{
					case 0:
						return "";						
					case 1:
						return "�˿����";	
					case 2:
						return "�����˿�";
					case 3:
						return "�˿�ʧ��";					
				}
				break;
			case 3:// ��������
				// ��������0���,1�޸�,2ɾ��
				switch(value)
				{
					case 0:
						return "���";						
					case 1:
						return "�޸�";	
					case 2:
						return "ɾ��";									
				}
				break;	
		}
		return "";
	}
	
	/**
	 * ��ʽ��ʱ��
	 * @Title:getLastDayOfMonth
	 * @Description:
	 * @param:@param year
	 * @param:@param month
	 * @param:@param type=0�³�,1����Ѯ,2����Ѯ
	 * @param:@return
	 * @return:String
	 * @throws
	 */
	public static String getDayOfMonth(int year,int month,int day)
	{
		Calendar cal = Calendar.getInstance();
		//�������
		cal.set(Calendar.YEAR,year);
		//�����·�
		cal.set(Calendar.MONTH, month-1);
		//������
		cal.set(Calendar.DATE, day);
		
		//��ʽ������
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastDayOfMonth = sdf.format(cal.getTime());
		
		return lastDayOfMonth;
	}
	/**
	 * ��ȡĳ�µ����һ�������Ѯ
	 * @Title:getLastDayOfMonth
	 * @Description:
	 * @param:@param year
	 * @param:@param month
	 * @param:@param type=0�³�,1����Ѯ,2����Ѯ
	 * @param:@return
	 * @return:String
	 * @throws
	 */
	public static String getLastDayOfMonth(int year,int month,int type)
	{
		Calendar cal = Calendar.getInstance();
		//�������
		cal.set(Calendar.YEAR,year);
		//�����·�
		cal.set(Calendar.MONTH, month-1);
		if(type==0)
		{
			//�����������·ݵ���Ѯ
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		else if(type==1)
		{
			//�����������·ݵ���Ѯ
			cal.set(Calendar.DAY_OF_MONTH, 15);
		}
		else if(type==2)
		{
			//��ȡĳ���������
			int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			//�����������·ݵ��������
			cal.set(Calendar.DAY_OF_MONTH, lastDay);
		}
		//��ʽ������
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastDayOfMonth = sdf.format(cal.getTime());
		
		return lastDayOfMonth;
	}
	//�Ƿ���ʱ��������,s����Ҫ�Ƚϵ�ʱ��,begin,end��ʱ������
	//�Ƿ���true
	public static boolean isdatein(String begin,String end,String s)
	{
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s+"��"+begin+"="+dateCompare(s,begin));
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s+"��"+end+"="+dateCompare(end,s));
		if((dateCompare(s,begin)>=0)&&(dateCompare(end,s)>=0))
		{
			return true;
		}
		return false;
	}
	//ʱ��Ƚ�,����ֵresult==0s1���s2,result<0s1С��s2,result:>0s1����s2,
	public static int dateCompare(String s1,String s2)
	{
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s1);
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s2);
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Calendar c1=java.util.Calendar.getInstance();
		java.util.Calendar c2=java.util.Calendar.getInstance();
		try
		{
			c1.setTime(df.parse(s1));
			c2.setTime(df.parse(s2));
		}catch(java.text.ParseException e){
			System.err.println("��ʽ����ȷ");
		}
		return c1.compareTo(c2);
	}
	
	//�õ�hopper�豸�ĵ�ǰ״̬
	public static String gethopperstats(int hopper)
	{
		String res=null;
		//"hopper":8��hopper��״̬,0����,1ȱ��,2����,3ͨѶ����
		if(hopper==0)
		{
			res="����";
		}
		else if(hopper==1)
		{
			res="ȱ��";
		}
		else if(hopper==2)
		{
			res="����";
		}
		else if(hopper==3)
		{
			res="ͨѶ����";
		}
		return res;
	}
	
	//Ϊ�ϴ���serverʹ�ã�����ǰʱ��ת��Ϊ��server�ϱ���ʱ��
	public static String getLasttime()
	{
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd"); //��ȷ������ 
		SimpleDateFormat tempTime = new SimpleDateFormat("HH:mm:ss"); //��ȷ������ 
        String datetime = tempDate.format(new java.util.Date()).toString()+"T"
        		+tempTime.format(new java.util.Date()).toString(); 
		return datetime;
	}
	
	//Ϊ�ϴ���serverʹ�ã�����һ��ʱ�䣬ת��Ϊ��server�ϱ���ʱ��
	public static String getStrtime(String orderTime)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date =null;
		try {
			date = df.parse(orderTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd"); //��ȷ������ 
		SimpleDateFormat tempTime = new SimpleDateFormat("HH:mm:ss"); //��ȷ������ 
        String datetime = tempDate.format(date).toString()+"T"
        		+tempTime.format(date).toString(); 
		return datetime;
	}
	
	//��ȡ�豸״̬���ϴ���������
	//dev�豸��1ֽ����,2Ӳ����,3hopper1,4hopper2,5hopper3,6hopper4,7hopper5,8hopper6,9hopper7,10hopper8
	//���أ�2���ϣ�0����
	public static int getvmcStatus(Map<String, Object> Set,int dev)
	{
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<�ֽ��豸״̬="+rst,"log.txt");	
		int rst=0;
		switch (dev) 
		{
			//ֽ����
			case 1:
				int bill_err=(Integer)Set.get("bill_err");
				int bill_enable=(Integer)Set.get("bill_enable");
				if(bill_err>0)
				{
					rst=2;
				}
				else
				{
					rst=0;
				}
				break;
			//Ӳ����	
			case 2:
				int coin_err=(Integer)Set.get("coin_err");
				int coin_enable=(Integer)Set.get("coin_enable");
				if(coin_err>0)
				{
					rst=2;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper1	
			case 3:
				int hopper1=(Integer)Set.get("hopper1");
				if((hopper1==3)||(hopper1==2))
				{
					rst=2;
				}
				else if(hopper1==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper2	
			case 4:
				int hopper2=(Integer)Set.get("hopper2");
				if((hopper2==3)||(hopper2==2))
				{
					rst=2;
				}
				else if(hopper2==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper3	
			case 5:
				int hopper3=(Integer)Set.get("hopper3");
				if((hopper3==3)||(hopper3==2))
				{
					rst=2;
				}
				else if(hopper3==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper4		
			case 6:
				int hopper4=(Integer)Set.get("hopper4");
				if((hopper4==3)||(hopper4==2))
				{
					rst=2;
				}
				else if(hopper4==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper5	
			case 7:
				int hopper5=(Integer)Set.get("hopper5");
				if((hopper5==3)||(hopper5==2))
				{
					rst=2;
				}
				else if(hopper5==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper6
			case 8:
				int hopper6=(Integer)Set.get("hopper6");
				if((hopper6==3)||(hopper6==2))
				{
					rst=2;
				}
				else if(hopper6==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper7
			case 9:
				int hopper7=(Integer)Set.get("hopper7");
				if((hopper7==3)||(hopper7==2))
				{
					rst=2;
				}
				else if(hopper7==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper8
			case 10:
				int hopper8=(Integer)Set.get("hopper8");
				if((hopper8==3)||(hopper8==2))
				{
					rst=2;
				}
				else if(hopper8==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;	
			default:
				break;
		}		
		return rst;
	}
	
	public static int getBill_err() {
		return bill_err;
	}

	public static void setBill_err(int bill_err) {
		ToolClass.bill_err = bill_err;
	}

	public static int getCoin_err() {
		return coin_err;
	}

	public static void setCoin_err(int coin_err) {
		ToolClass.coin_err = coin_err;
	}
	
	//�����ȶ�
	private static boolean passcmp(String pwd,String value)
    {
    	boolean istrue=false;
    	if((pwd==null)||(pwd.equals("")==true))
    	{
    		if(value.equals("83718557"))
    		{
    			istrue=true;
    		}
    	}
    	else
    	{
    		if(value.equals(pwd))
    		{
    			istrue=true;
    		}
    	}
    	return istrue;
    }
	//��ȡ�Ƿ�������ȷ
	//���أ�true��ȷ,false����
	public static boolean getpwdStatus(Context context,String passwd)
	{
		boolean istrue=false;
		//����ά��ҳ������
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		String Pwd=tb_inaccount.getMainPwd().toString();
    		if(Pwd==null)
    		{
    			//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��ֵ=null","log.txt");
    			istrue=passcmp(null,passwd);
    		}
    		else
    		{
    			//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��ֵ="+Pwd,"log.txt");
    			istrue=passcmp(Pwd,passwd);
    		}
    	}
    	else
    	{
    		istrue=passcmp(null,passwd);
		}
		return istrue;
	}
	
	//�����ȶ�
	//���أ�true��ȷ,false����
	public static boolean getzhitihuo(Context context,String cabid,String columnID,String pwd)
	{
		boolean istrue=false;
		if((pwd==null)||(pwd.equals("")==true))
		{
			istrue=false;
		}
		else if((cabid==null)||(cabid.equals("")==true))
		{
			istrue=false;
		}	
		else if((columnID==null)||(columnID.equals("")==true))
		{
			istrue=false;
		}
		//���������������
		else
		{
			// ����InaccountDAO����
			vmc_columnDAO columnDAO = new vmc_columnDAO(context);
			Tb_vmc_column tb_vmc_column = columnDAO.find(cabid,columnID);// �����Ʒ��Ϣ
			if(tb_vmc_column!=null)
			{
				String str=tb_vmc_column.getTihuoPwd();
				if((str==null)||(str.equals("")==true))
				{
					istrue=false;
				}
				else
				{
					if(pwd.equals(str)==true)
					{
						istrue=true;
					}
				}
			}	
		}
		return istrue;
	}
	//�Ƿ����ʹ����������
	//���أ�true��ȷ,false����
	public static boolean getzhitihuotype(Context context,String cabid,String columnID)
	{
		boolean istrue=false;
		if((cabid==null)||(cabid.equals("")==true))
		{
			istrue=false;
		}	
		else if((columnID==null)||(columnID.equals("")==true))
		{
			istrue=false;
		}
		//���������������
		else
		{
			// ����InaccountDAO����
			vmc_columnDAO columnDAO = new vmc_columnDAO(context);
			Tb_vmc_column tb_vmc_column = columnDAO.find(cabid,columnID);// �����Ʒ��Ϣ
			if(tb_vmc_column!=null)
			{
				String str=tb_vmc_column.getTihuoPwd();
				if((str==null)||(str.equals("")==true))
				{
					istrue=false;
				}
				else
				{
					istrue=true;
				}
			}	
		}
		return istrue;
	}
	/*********************************************************************************************************
	** Function name:     	CrcCheck
	** Descriptions:	    CRCУ���
	** input parameters:    msg��Ҫ���������;len���ݳ���
	** output parameters:   ��
	** Returned value:      CRC������
	*********************************************************************************************************/
	public static int crcCheck(byte msg[], int len){
        int i, j, crc = 0, current;
        for(i = 0;i < len;i++)
        {
            current = ((msg[i] & 0x000000FF) << 8);
            for (j = 0;j < 8;j++)
            {
                if((short)(crc ^ current) < 0)
                {
                    crc =  ((crc << 1) ^ 0x1021) & 0x0000FFFF;
                }
                else
                {
                    crc = (crc << 1) & 0x0000FFFF;
                }
                current = (current << 1) & 0x0000FFFF;
            }
        }

        return crc;
    }

	/*********************************************************************************************************
	** Function name:     	printHex
	** Descriptions:	    ��ӡʮ��������Ϣ
	** input parameters:    bentrecv��Ҫ��ӡ������;bentindex���ݳ���
	** output parameters:   ��
	** Returned value:      ��ӡ��ʮ��������Ϣ
	*********************************************************************************************************/
	public static StringBuilder printHex(byte[] bentrecv,int bentindex)
	{
		StringBuilder res=new StringBuilder();
		for(int i=0;i<bentindex;i++)
	    {
	    	String hex = Integer.toHexString(bentrecv[i] & 0xFF);
            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }	
	    	res.append("[").append(hex.toUpperCase()).append("]");				    	
	    }
		return res;
	}
	
	//��ȡ��portid
	public static int Resetportid(String bentcom)
	{
		int bentcom_id=0;
		//2.�������
		try {
			JSONObject jsonObject = new JSONObject(bentcom); 
			//����keyȡ������
			JSONObject ev_head = (JSONObject) jsonObject.getJSONObject("EV_json");
			int str_evType =  ev_head.getInt("EV_type");
			if(str_evType==EVprotocol.EV_REGISTER)
			{
				bentcom_id=ev_head.getInt("port_id");				
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return bentcom_id;
	}
	
	//type=1���ֽ�,2�Ǹ��ӹ�3�ǵ���/��������,4��Э�豸����,5�Ǹ��񵯻�/��������
	public static void ResstartPort(int type)
	{
		if(type==1)//�ֽ�
		{
			//���ֽ��豸����
			if(ToolClass.getCom().equals("")==false) 
			{
				ToolClass.Log(ToolClass.INFO,"EV_COM","comRelease=port="+ToolClass.getCom()+"    port_id="+ToolClass.getCom_id(),"com.txt");
				String com2 = EVprotocol.EVPortRelease(ToolClass.getCom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","comRelease="+com2,"com.txt");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String com = EVprotocol.EVPortRegister(ToolClass.getCom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","comRegister="+com,"com.txt");
				ToolClass.setCom_id(ToolClass.Resetportid(com));
			}			
		}
		else if(type==2)
		{
			//�򿪸��ӹ񴮿�
			if(ToolClass.getBentcom().equals("")==false)
			{
				ToolClass.Log(ToolClass.INFO,"EV_COM","bentcomRelease=port="+ToolClass.getBentcom()+"    port_id="+ToolClass.getBentcom_id(),"com.txt");
				String com2 = EVprotocol.EVPortRelease(ToolClass.getBentcom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","bentcomRelease="+com2,"com.txt");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String bentcom = EVprotocol.EVPortRegister(ToolClass.getBentcom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","bentcomRegister="+bentcom,"com.txt");
				ToolClass.setBentcom_id(ToolClass.Resetportid(bentcom));
			}
		}
		else if(type==3)
		{
			//�򿪵��ɹ񴮿�
			if(ToolClass.getColumncom().equals("")==false)
			{
				ToolClass.Log(ToolClass.INFO,"EV_COM","columncomRelease=port="+ToolClass.getColumncom()+"    port_id="+ToolClass.getColumncom_id(),"com.txt");
				String com2 = EVprotocol.EVPortRelease(ToolClass.getColumncom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","columncomRelease="+com2,"com.txt");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String columncom = EVprotocol.EVPortRegister(ToolClass.getColumncom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","columncomRegister="+columncom,"com.txt");
				ToolClass.setColumncom_id(ToolClass.Resetportid(columncom));
			}
		}
		else if(type==4)
		{
			//����Э�豸����
			if(ToolClass.getExtracom().equals("")==false)
			{
				ToolClass.Log(ToolClass.INFO,"EV_COM","extracomcomRelease=port="+ToolClass.getExtracom()+"    port_id="+ToolClass.getExtracom_id(),"com.txt");
				String com2 = EVprotocol.EVPortRelease(ToolClass.getExtracom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","extracomRelease="+com2,"com.txt");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String extracom = EVprotocol.EVPortRegister(ToolClass.getExtracom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","extracomRegister="+extracom,"com.txt");
				ToolClass.setExtracom_id(ToolClass.Resetportid(extracom));
			}
		}
		else if(type==5)
		{
			//�򿪵��ɹ񴮿�
			if(ToolClass.getColumncom2().equals("")==false)
			{
				ToolClass.Log(ToolClass.INFO,"EV_COM","columncom2Release=port="+ToolClass.getColumncom2()+"    port_id="+ToolClass.getColumncom2_id(),"com.txt");
				String com2 = EVprotocol.EVPortRelease(ToolClass.getColumncom2());
				ToolClass.Log(ToolClass.INFO,"EV_COM","columncom2Release="+com2,"com.txt");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String columncom = EVprotocol.EVPortRegister(ToolClass.getColumncom2());
				ToolClass.Log(ToolClass.INFO,"EV_COM","columncom2Register="+columncom,"com.txt");
				ToolClass.setColumncom2_id(ToolClass.Resetportid(columncom));
			}
		}
	}
	
	//����ͼ�ν���ʧ����ʾ
	public static void failToast(String str)
	{
		// ������Ϣ��ʾ
		Toast myToast=Toast.makeText(context, str, Toast.LENGTH_LONG);
		myToast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) myToast.getView();
		ImageView imageCodeProject = new ImageView(context);
		imageCodeProject.setImageResource(R.drawable.search);
		toastView.addView(imageCodeProject, 0);
		myToast.show();
	}
	
	//�ж��ַ����Ƿ�Ϊ��:true�գ�false�ǿ�
	public static boolean isEmptynull(String str)
	{
		boolean result=true;
		if(str!=null)
		{
			if((str.isEmpty()==false)&&(str.equals("")==false))
			{
				result=false;
			}
		}
		return result;
	}
	
	//���Service�Ƿ�������
	 public static boolean isServiceRunning(String serviceClassName)
	 { 
        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
        final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE); 

        for (RunningServiceInfo runningServiceInfo : services) 
        { 
        	//ToolClass.Log(ToolClass.INFO,"EV_DOG","service appName:"+runningServiceInfo.service.getClassName()+"-->pack:"+runningServiceInfo.service.getPackageName(),"dog.txt");
            if (runningServiceInfo.service.getClassName().equals(serviceClassName))
            { 
                return true; 
            } 
        } 
        return false; 
	 }
	 
	 //ȥ���ַ����еĿո񡢻س������з����Ʊ��
	 public static String replaceBlank(String str) {
	        String dest = "";
	        if (str!=null) {
	            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	            Matcher m = p.matcher(str);
	            dest = m.replaceAll("");
	        }
	        return dest;
	    }
	
	 public static List<Map<String,Object>> listSort(List<Map<String,Object>> resultList) throws Exception{  
         // resultList����Ҫ�����list�����ڷŵ���Map  
         // ���صĽ����  
         Collections.sort(resultList,new Comparator<Map<String,Object>>() {  
 
          public int compare(Map<String, Object> o1,Map<String, Object> o2) {  
 
           //o1��o2��list�е�Map������������ȡ��ֵ���������򣬴���Ϊ����s1��s2�������ֶ�ֵ  
              int s1 = Integer.parseInt(o1.get("procount").toString());  
              int s2 = Integer.parseInt(o2.get("procount").toString());    
 
           if(s1<s2) {  
            return 1;  
           }else {  
            return -1;  
           }  
          }  
         });  
       return resultList;   
     }  
}
