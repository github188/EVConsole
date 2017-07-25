package com.easivend.app.business;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.bean.ComBean;
import com.easivend.app.maintain.CahslessTest;
import com.easivend.app.maintain.MaintainActivity;
import com.easivend.app.maintain.PrintTest;
import com.easivend.common.AudioSound;
import com.easivend.common.OrderDetail;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_classDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.fragment.BusgoodsFragment;
import com.easivend.fragment.BusgoodsFragment.BusgoodsFragInteraction;
import com.easivend.fragment.BusgoodsclassFragment;
import com.easivend.fragment.BusgoodsclassFragment.BusgoodsclassFragInteraction;
import com.easivend.fragment.BusgoodsselectFragment;
import com.easivend.fragment.BusgoodsselectFragment.BusgoodsselectFragInteraction;
import com.easivend.fragment.BushuoFragment;
import com.easivend.fragment.BushuoFragment.BushuoFragInteraction;
import com.easivend.fragment.BusinessportFragment;
import com.easivend.fragment.BusinessportFragment.BusportFragInteraction;
import com.easivend.fragment.BuszhiamountFragment;
import com.easivend.fragment.BuszhiamountFragment.BuszhiamountFragInteraction;
import com.easivend.fragment.MoviewlandFragment.MovieFragInteraction;
import com.easivend.http.EVServerhttp;
import com.easivend.http.Weixinghttp;
import com.easivend.http.Yinlianhttp;
import com.easivend.http.Zhifubaohttp;
import com.easivend.model.Tb_vmc_product;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.view.COMService;
import com.example.evconsole.R;
import com.example.printdemo.MyFunc;
import com.example.printdemo.SerialHelper;
import com.landfone.common.utils.IUserCallback;
import com.landfoneapi.mispos.Display;
import com.landfoneapi.mispos.DisplayType;
import com.landfoneapi.mispos.ErrCode;
import com.landfoneapi.mispos.LfMISPOSApi;
import com.landfoneapi.protocol.pkg.REPLY;
import com.landfoneapi.protocol.pkg._04_GetRecordReply;
import com.landfoneapi.protocol.pkg._04_QueryReply;
import com.printsdk.cmd.PrintCmd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class BusPort extends Activity implements 
//businessҳ��ӿ�
MovieFragInteraction,BusportFragInteraction,
//busgoodsclassҳ��ӿ�
BusgoodsclassFragInteraction,
//busgoodsҳ��ӿ�
BusgoodsFragInteraction,
//Busgoodsselectҳ��ӿ�
BusgoodsselectFragInteraction,
//Buszhiamountҳ��ӿ�
BuszhiamountFragInteraction,
//Bushuoҳ��ӿ�
BushuoFragInteraction
{
	private BusinessportFragment businessportFragment;
	private BusgoodsclassFragment busgoodsclassFragment;
	private BusgoodsFragment busgoodsFragment;
	private BusgoodsselectFragment busgoodsselectFragment;
	private BuszhiamountFragment buszhiamountFragment;
	private BushuoFragment bushuoFragment;
	//����ҳ��
    Intent intent=null;
    //final static int REQUEST_CODE=1; 
    final static int PWD_CODE=2; 
    public static final int BUSPORT=1;//��ҳ��
    public static final int BUSGOODSCLASS=2;//��Ʒ���ҳ��
	public static final int BUSGOODS=3;//��Ʒ����ҳ��
	public static final int BUSGOODSSELECT=4;//��Ʒ��ϸҳ��
	public static final int BUSZHIAMOUNT=5;//�ֽ�֧��ҳ��
	public static final int BUSZHIER=6;//֧����֧��ҳ��
	public static final int BUSZHIWEI=7;//΢��֧��ҳ��
	public static final int BUSZHIPOS=9;//POS֧��ҳ��
	public static final int BUSHUO=8;//����ҳ��
	private int gotoswitch=0;//��ǰ��ת���ĸ�ҳ��
	private int con=0;
	private Map<String, String> proclassID=new HashMap<String, String>();//��Ʒ����id�����浱ǰ��������ĸ���Ʒ����
	//���ȶԻ���
	ProgressDialog dialog= null;
	private String zhifutype = "0";//֧����ʽ0=�ֽ�1=pos 3=��ά��4=΢֧�� -1=ȡ����,5��������
	private String out_trade_no=null;
	//Server�������
	LocalBroadcastManager localBroadreceiver;
	EVServerReceiver receiver;
	//=================
	//==�ֽ�֧��ҳ�����
	//=================
	private int queryLen = 0; 
	private int queryamountLen = 0,queryzhierLen = 0,queryzhiweiLen = 0,queryzhiposLen = 0,
			queryzhiyinlianLen = 0; 
	private int billdev=1;//�Ƿ���Ҫ��ֽ����,1��Ҫ
    private int iszhienable=0;//1���ʹ�ָ��,0��û���ʹ�ָ�2����Ͷ���Ѿ�����
    private boolean isempcoin=false;//false��δ���͹�ֽ����ָ�true��Ϊȱ�ң��Ѿ����͹�ֽ����ָ��
    private int dispenser=0;//0��,1hopper,2mdb
	float billmoney=0,coinmoney=0,money=0;//Ͷ�ҽ��
	float amount=0;//��Ʒ��Ҫ֧�����
	private int iszhiamount=0;//1�ɹ�Ͷ��Ǯ,0û�гɹ�Ͷ��Ǯ
    private boolean ischuhuo=false;//true�Ѿ��������ˣ������ϱ���־
	float RealNote=0,RealCoin=0,RealAmount=0;//�˱ҽ��	
	private int iszhiamountsel=0;//0û�������ֽ�1�������ֽ�
	//=================
	//==֧����֧��ҳ�����
	//=================
	//�߳̽���֧������ά�����
    private ExecutorService zhifubaothread=null;
    private Handler zhifubaomainhand=null,zhifubaochildhand=null;
    Zhifubaohttp zhifubaohttp=null;
    private int iszhier=0;//1�ɹ������˶�ά��,0û�гɹ����ɶ�ά�룬2���ν����Ѿ�����
    private boolean ercheck=false;//true���ڶ�ά����̲߳����У����Ժ�falseû�ж�ά����̲߳���
    private int ispayoutopt=0;//1���ڽ����˱Ҳ���,0δ�����˱Ҳ���
    private int iszhiersel=0;//0û������֧������1������֧����
    //=================
  	//==΢��֧��ҳ�����
  	//=================    
    //�߳̽���΢�Ŷ�ά�����
    private ExecutorService weixingthread=null;
    private Handler weixingmainhand=null,weixingchildhand=null;   
    Weixinghttp weixinghttp=null;
    private int iszhiwei=0;//1�ɹ������˶�ά��,0û�гɹ����ɶ�ά�룬2����Ͷ���Ѿ�����
    private int iszhiweisel=0;//0û������΢�ţ�1������΢��
    private boolean weicheck=false;//true���ڶ�ά����̲߳����У����Ժ�falseû�ж�ά����̲߳���
    private JSONObject weipayout=new JSONObject();
    //=================
  	//==����֧��ҳ�����
  	//=================    
    //�߳̽���΢�Ŷ�ά�����
    private ExecutorService yinlianthread=null;
    private Handler yinlianmainhand=null,yinlianchildhand=null;   
    Yinlianhttp yinlianhttp=null;
    private int iszhiyinlian=0;//1�ɹ������˶�ά��,0û�гɹ����ɶ�ά�룬2����Ͷ���Ѿ�����
    private int iszhiyinliansel=0;//0û������΢�ţ�1������΢��
    private boolean yinliancheck=false;//true���ڶ�ά����̲߳����У����Ժ�falseû�ж�ά����̲߳���
    //=================
  	//==pos֧��ҳ�����
  	//=================
    private int isPossel=0;//0û������Pos��1������Pos�в�ѯ����,����ֵ������posû�в�ѯ����
    private LfMISPOSApi mMyApi = new LfMISPOSApi();
    private Handler posmainhand=null;
    private boolean zhiposcheck=false;//1����������̲߳�����,0û��������̲߳���
    private int iszhipos=0;//0δˢ��,1��Ҫˢ������,2ˢ���ۿ��Ѿ���ɲ��ҽ���㹻
    //��ѯ����
  	private String cashbalance = "";
    //�˿����
   	private String rfd_card_no = "";
   	private String rfd_spec_tmp_serial = "";
    //=================
	//==����ҳ�����
	//=================
	private int status=0;//�������	
    Timer timer = new Timer();
    private final int SPLASH_DISPLAY_LENGHT = 5*60; //  5*60�ӳ�5����	
    private int recLen = SPLASH_DISPLAY_LENGHT; 
    private boolean isbus=true;//true��ʾ�ڹ��ҳ�棬false������ҳ��
    private int chuhuoLen = 0;//��ѳ���һ�����ӹ� 
    //=================
    //COM�������
    //=================
  	LocalBroadcastManager comBroadreceiver;
  	COMReceiver comreceiver;
    //=================
    //��ӡ�����
    //=================
  	boolean istitle1,istitle2,isno,issum,isthank,iser,isdate,isdocter;
	int serialno=0;//��ˮ��
	String title1str,title2str,thankstr,erstr;
	SerialControl ComA;                  // ���ڿ���
	static DispQueueThread DispQueue;    // ˢ����ʾ�߳�
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // ���ʻ���־ʱ���ʽ��
	SimpleDateFormat sdfdoc = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss"); // ҩ��СƱ��
	private Handler printmainhand=null;
	private int isPrinter=0;//0û�����ô�ӡ����1�����ô�ӡ��
    
    //=========================
    //activity��fragment�ص����
    //=========================
    /**
     * ����������fragment������,���þ�̬����
     */
    public static BusPortFragInteraction listterner;
    public static BusPortMovieFragInteraction listternermovie;
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        if(activity instanceof BuszhiamountFragInteraction)
//        {
//            listterner = (BuszhiamountFragInteraction)activity;
//        }
//        else{
//            throw new IllegalArgumentException("activity must implements BuszhiamountFragInteraction");
//        }
//
//    }
    /**
     * ����������fragment������,
     * ����һ������������fragment����ʵ�ֵĽӿ�
     */
    public interface BusPortFragInteraction
    {
        /**
         * Activity ��Fragment����ָ�����������Ը�������������
         * @param str
         */
        //�ֽ�ҳ��
        void BusportTsxx(String str);      //��ʾ��Ϣ
        void BusportTbje(String str);      //Ͷ�ҽ��
        //����ҳ��
        void BusportChjg(int sta);      //�������
        //���ֽ�ҳ��
        void BusportSend(String str);      //��ά��
        void BusportSendWei(String str);      //΢��
        void BusportSendYinlian(String str);//������ά��
    }
    public interface BusPortMovieFragInteraction
    {
    	//��ʾ������ʾ��Ϣ
        void BusportMovie(int infotype);      //��ʾ������ʾ��Ϣ
        //ˢ�¹��ҳ��
        void BusportAds();      //ˢ�¹���б�
        void BusportCashless(String cashbalance);//�����
        void BusportVideoStop(int type);//type=0��ͣ��Ƶ����,1�ָ���Ƶ����
    }
        
    /**
     * ����������fragment������,
     * �����ġ���Fragment�����ص�activity��ʱ������ע��ص���Ϣ
     * @param activity
     */
  	public static void setCallBack(BusPortFragInteraction call){ 
  		listterner = call;
      }
  	public static void setMovieCallBack(BusPortMovieFragInteraction call){ 
  		listternermovie = call;
      }
  	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ��title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // ȫ��
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.busport);		
		//���ú������������Ĳ��ֲ���
		this.setRequestedOrientation(ToolClass.getOrientation());		 
		//=============
		//Server�������
		//=============
		//4.ע�������
		localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new EVServerReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserverrec");
		localBroadreceiver.registerReceiver(receiver,filter);
		//=============
		//COM�������
		//=============
		//4.ע�������
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
        timer.schedule(new TimerTask() { 
	        @Override 
	        public void run() { 
	        	  //ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<portthread="+Thread.currentThread().getId(),"log.txt");
	        	  if(isbus==false)
	        	  {
		        	  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<recLen="+recLen,"log.txt");
		        	  recLen--; 
		        	  //�ص���ҳ
		        	  if(recLen == 0)
		              { 
		                  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<recclose=BusinessportFragment","log.txt");
		                  //=================
			        	  //==�ֽ�֧��ҳ�����
			        	  //=================
				          if(gotoswitch==BUSZHIAMOUNT)
				          {
				        	  BuszhiamountFinish();
				          }
				          else
				          {
				        	  viewSwitch(BUSPORT,null);	  
						  }				          
		                  	                   
		              }	
		        	//=================
	        		//==��һ֧��ҳ�����
	        		//=================
		        	if(gotoswitch==BUSZHIAMOUNT)
		        	{
		        		//=================
		        		//==�ֽ�֧��ҳ�����
		        		//=================
		        		if(iszhiamountsel>0)
		        		{
				        	//���Ͳ�ѯ����ָ��
		                    if(iszhienable==1)
		                    {
		                    	queryamountLen++;
			                    if(queryamountLen>=5)
			                    {
			                    	queryamountLen=0;
			                    	//EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
			                    	//Heart����
								    Intent intent2=new Intent();
							    	intent2.putExtra("EVWhat", EVprotocol.EV_MDB_HEART);
									intent2.setAction("android.intent.action.comsend");//action���������ͬ
									comBroadreceiver.sendBroadcast(intent2);
			                    }
		                    }
		                    //���ʹ�ֽ��Ӳ����ָ��
		                    else if(iszhienable==0)
		                    {
		                    	queryamountLen++;
			                    if(queryamountLen>=10)
			                    {
			                    	queryamountLen=0;
			                    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,1);
			                    	BillEnable(1);
			                    }
		                    }
		        		}
	                    
	                    //=================
		        		//==֧����֧��ҳ�����
		        		//=================
		        		if(iszhiersel>0)
		        		{
		                    //���Ͳ�ѯ����ָ��
		                    if(iszhier==1)
		                    {
		                    	queryzhierLen++;
			                    if(queryzhierLen>=2)
			                    {
			                    	queryzhierLen=0;
			                    	queryzhier();
			                    }
		                    }
		                    //���Ͷ�������ָ��
		                    else if(iszhier==0)
		                    {
		                    	queryzhierLen++;
			                    if(queryzhierLen>=5)
			                    {
			                    	queryzhierLen=0;
			                    	//���Ͷ���
			                		sendzhier();
			                    }
		                    }
		        		}
	                    
	                    //=================
		        		//==΢��֧��ҳ�����
		        		//=================
		        		if(iszhiweisel>0)
		        		{
		                    //���Ͳ�ѯ����ָ��
		                    if(iszhiwei==1)
		                    {
		                    	queryzhiweiLen++;
			                    if(queryzhiweiLen>=2)
			                    {
			                    	queryzhiweiLen=0;
			                    	queryzhiwei();
			                    }
		                    }
		                    //���Ͷ�������ָ��
		                    else if(iszhiwei==0)
		                    {
		                    	queryzhiweiLen++;
			                    if(queryzhiweiLen>=5)
			                    {
			                    	queryzhiweiLen=0;
			                    	//���Ͷ���
			                		sendzhiwei();
			                    }
		                    }
		        		}
		        		
		        		//=================
		        		//==����֧��ҳ�����
		        		//=================
		        		if(iszhiyinliansel>0)
		        		{
		                    //���Ͳ�ѯ����ָ��
		                    if(iszhiyinlian==1)
		                    {
		                    	queryzhiyinlianLen++;
			                    if(queryzhiyinlianLen>=2)
			                    {
			                    	queryzhiyinlianLen=0;
			                    	queryzhiyinlian();
			                    }
		                    }
		                    //���Ͷ�������ָ��
		                    else if(iszhiyinlian==0)
		                    {
		                    	queryzhiyinlianLen++;
			                    if(queryzhiyinlianLen>=5)
			                    {
			                    	queryzhiyinlianLen=0;
			                    	//���Ͷ���
			                		sendzhiyinlian();
			                    }
		                    }
		        		}
	                    	                    
		        	}
		        	
		        	
	        	  }
	        	  
	        	  //=================
	        	  //==pos���
	        	  //=================
	        	  if(isPossel>0)
	        	  {
	        		  queryzhiposLen++;
	                    if(queryzhiposLen>=4)
	                    {
	                    	queryzhiposLen=0;
			        		//�в�ѯ����                         û��������̲߳���                 û�н�ˢ������ҳ��
			      			if((isPossel==1)&&(zhiposcheck==false)&&(iszhipos==0))
			      			{
			      				ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<======>��һ����ѯ","com.txt");
			      				mMyApi.pos_query(mIUserCallback);
			      				zhiposcheck=true;
			      			}
	                    }
	        	  }
	        	  //�ж�3�����ڣ�������¿յĻ����ſ���ֱ�ӳ���
	        	  if(ToolClass.isLAST_CHUHUO())
	        	  {
		        		chuhuoLen++;
		        		if(chuhuoLen>=3*60)
		        		{
		        			chuhuoLen=0;
		        			ToolClass.setLAST_CHUHUO(false);
		        		}
	        	  }
	        } 
	    }, 1000, 1000);       // timeTask 
		//��ʼ��Ĭ��fragment
		initView();
		//***********************
		//�߳̽���֧������ά�����
		//***********************
		zhifubaomainhand=new Handler()
		{			
			@Override
			public void handleMessage(Message msg) {
				//barzhifubaotest.setVisibility(View.GONE);
				ercheck=false;
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case Zhifubaohttp.SETMAIN://���߳̽������߳���Ϣ
						try {
							JSONObject zhuhe=new JSONObject(msg.obj.toString());
							String zhuheout_trade_no=zhuhe.getString("out_trade_no");
							String qr_code=zhuhe.getString("qr_code");
							ToolClass.Log(ToolClass.INFO,"EV_JNI","����֧����=out_trade_no="+out_trade_no+">>zhuheout_trade_no="+zhuheout_trade_no,"log.txt");
							if(zhuheout_trade_no.equals(out_trade_no))
							{
								listterner.BusportSend(qr_code);
								iszhier=1;
							}							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
						break;
					case Zhifubaohttp.SETFAILNETCHILD://���߳̽������߳���Ϣ
						listterner.BusportTsxx("���׽��:����"+msg.obj.toString()+con);
						con++;
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ʧ��");
							dialog.dismiss();
							zhierDestroy(1);
						}
						break;		
					case Zhifubaohttp.SETPAYOUTMAIN://���߳̽������߳���Ϣ
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(1);//��¼�˱ҳɹ�
							OrderDetail.setRealCard(amount);//��¼�˱ҽ��
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ɹ�");
							dialog.dismiss();
							zhierDestroy(1);
						}
						break;
					case Zhifubaohttp.SETDELETEMAIN://���߳̽������߳���Ϣ
//						listterner.BusportTsxx("���׽��:�����ɹ�");
//						clearamount();
//				    	viewSwitch(BUSPORT, null);
						break;	
					case Zhifubaohttp.SETQUERYMAINSUCC://���׳ɹ�
						listterner.BusportTsxx("���׽��:���׳ɹ�");
						if(iszhier==1)
						{
							iszhier=2;
	//						//reamin_amount=String.valueOf(amount);
							zhifutype="3";						
							tochuhuo();
						}
						break;
					case Zhifubaohttp.SETQUERYMAIN://���߳̽������߳���Ϣ
					case Zhifubaohttp.SETFAILPROCHILD://���߳̽������߳���Ϣ
					case Zhifubaohttp.SETFAILBUSCHILD://���߳̽������߳���Ϣ	
					case Zhifubaohttp.SETFAILQUERYPROCHILD://���߳̽������߳���Ϣ
					case Zhifubaohttp.SETFAILQUERYBUSCHILD://���߳̽������߳���Ϣ	
					case Zhifubaohttp.SETFAILPAYOUTPROCHILD://���߳̽������߳���Ϣ		
					case Zhifubaohttp.SETFAILPAYOUTBUSCHILD://���߳̽������߳���Ϣ
					case Zhifubaohttp.SETFAILDELETEPROCHILD://���߳̽������߳���Ϣ		
					case Zhifubaohttp.SETFAILDELETEBUSCHILD://���߳̽������߳���Ϣ						
						//listterner.BusportTsxx("���׽��:"+msg.obj.toString());
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ʧ��");
							dialog.dismiss();
							zhierDestroy(1);
						}
						break;	
				}				
			}
			
		};
		//�����û��Լ��������
		zhifubaohttp=new Zhifubaohttp(zhifubaomainhand);
		zhifubaothread=Executors.newCachedThreadPool();
		
		
		//***********************
		//�߳̽���΢�Ŷ�ά�����
		//***********************
		weixingmainhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				//barweixingtest.setVisibility(View.GONE);
				weicheck=false;
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case Weixinghttp.SETMAIN://���߳̽������߳���Ϣ
						try {
							JSONObject zhuhe=new JSONObject(msg.obj.toString());
							String zhuheout_trade_no=zhuhe.getString("out_trade_no");
							String code_url=zhuhe.getString("code_url");
							ToolClass.Log(ToolClass.INFO,"EV_JNI","����΢��=out_trade_no="+out_trade_no+">>zhuheout_trade_no="+zhuheout_trade_no,"log.txt");						       
							if(zhuheout_trade_no.equals(out_trade_no))
							{
								listterner.BusportSendWei(code_url);
								iszhiwei=1;
							}							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						break;
					case Weixinghttp.SETFAILNETCHILD://���߳̽������߳���Ϣ
						listterner.BusportTsxx("���׽��:����"+msg.obj.toString()+con);
						con++;	
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ʧ��");
							dialog.dismiss();
							zhiweiDestroy(1);
						}
						break;	
					case Weixinghttp.SETPAYOUTMAIN://���߳̽������߳���Ϣ
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(1);//��¼�˱ҳɹ�
							OrderDetail.setRealCard(amount);//��¼�˱ҽ��
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ɹ�");
							dialog.dismiss();
							zhiweiDestroy(1);
						}
						break;
					case Weixinghttp.SETDELETEMAIN://���߳̽������߳���Ϣ
//						listterner.BusportTsxx("���׽��:�����ɹ�");
//						clearamount();
//				    	viewSwitch(BUSPORT, null);
						break;	
					case Weixinghttp.SETQUERYMAINSUCC://���߳̽������߳���Ϣ		
						listterner.BusportTsxx("���׽��:���׳ɹ�");
						//reamin_amount=String.valueOf(amount);
						if(iszhiwei==1)
						{
							iszhiwei=2;
	                        zhifutype="4";                        
							tochuhuo();
						}
						break;
					case Weixinghttp.SETFAILPAYOUTPROCHILD://���߳̽������߳���Ϣ		
					case Weixinghttp.SETFAILPAYOUTBUSCHILD://���߳̽������߳���Ϣ
						ToolClass.WriteSharedPreferencesWeiPayout(weipayout);
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ʧ��");
							dialog.dismiss();
							zhiweiDestroy(1);
						}
						break;
					case Weixinghttp.SETFAILPROCHILD://���߳̽������߳���Ϣ
					case Weixinghttp.SETFAILBUSCHILD://���߳̽������߳���Ϣ	
					case Weixinghttp.SETFAILQUERYPROCHILD://���߳̽������߳���Ϣ
					case Weixinghttp.SETFAILQUERYBUSCHILD://���߳̽������߳���Ϣ		
					case Weixinghttp.SETQUERYMAIN://���߳̽������߳���Ϣ						
					case Weixinghttp.SETFAILDELETEPROCHILD://���߳̽������߳���Ϣ		
					case Weixinghttp.SETFAILDELETEBUSCHILD://���߳̽������߳���Ϣ
						//listterner.BusportTsxx("���׽��:"+msg.obj.toString());
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ʧ��");
							dialog.dismiss();
							zhiweiDestroy(1);
						}
						break;		
				}				
			}
			
		};
		//�����û��Լ��������
		weixinghttp=new Weixinghttp(weixingmainhand);
		weixingthread=Executors.newCachedThreadPool();
		
		//***********************
		//�߳̽���������ά�����
		//***********************
		yinlianmainhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				//barweixingtest.setVisibility(View.GONE);
				yinliancheck=false;
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case Yinlianhttp.SETMAIN://���߳̽������߳���Ϣ
						try {
							JSONObject zhuhe=new JSONObject(msg.obj.toString());
							String zhuheout_trade_no=zhuhe.getString("out_trade_no");
							String code_url=zhuhe.getString("code_url");
							ToolClass.Log(ToolClass.INFO,"EV_JNI","��������=out_trade_no="+out_trade_no+">>zhuheout_trade_no="+zhuheout_trade_no,"log.txt");						       
							if(zhuheout_trade_no.equals(out_trade_no))
							{
								listterner.BusportSendYinlian(code_url);
								iszhiyinlian=1;
							}							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						break;
					case Yinlianhttp.SETFAILNETCHILD://���߳̽������߳���Ϣ
						listterner.BusportTsxx("���׽��:����"+msg.obj.toString()+con);
						con++;	
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ʧ��");
							dialog.dismiss();
							zhiweiDestroy(1);
						}
						break;	
					case Yinlianhttp.SETPAYOUTMAIN://���߳̽������߳���Ϣ
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(1);//��¼�˱ҳɹ�
							OrderDetail.setRealCard(amount);//��¼�˱ҽ��
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ɹ�");
							dialog.dismiss();
							zhiweiDestroy(1);
						}
						break;
					case Yinlianhttp.SETDELETEMAIN://���߳̽������߳���Ϣ
//								listterner.BusportTsxx("���׽��:�����ɹ�");
//								clearamount();
//						    	viewSwitch(BUSPORT, null);
						break;	
					case Yinlianhttp.SETQUERYMAINSUCC://���߳̽������߳���Ϣ		
						listterner.BusportTsxx("���׽��:���׳ɹ�");
						//reamin_amount=String.valueOf(amount);
						if(iszhiyinlian==1)
						{
							iszhiyinlian=2;
	                        zhifutype="7";                        
							tochuhuo();
						}
						break;
					case Yinlianhttp.SETFAILPROCHILD://���߳̽������߳���Ϣ
					case Yinlianhttp.SETFAILBUSCHILD://���߳̽������߳���Ϣ	
					case Yinlianhttp.SETFAILQUERYPROCHILD://���߳̽������߳���Ϣ
					case Yinlianhttp.SETFAILQUERYBUSCHILD://���߳̽������߳���Ϣ		
					case Yinlianhttp.SETQUERYMAIN://���߳̽������߳���Ϣ	
					case Yinlianhttp.SETFAILPAYOUTPROCHILD://���߳̽������߳���Ϣ		
					case Yinlianhttp.SETFAILPAYOUTBUSCHILD://���߳̽������߳���Ϣ
					case Yinlianhttp.SETFAILDELETEPROCHILD://���߳̽������߳���Ϣ		
					case Yinlianhttp.SETFAILDELETEBUSCHILD://���߳̽������߳���Ϣ
						//listterner.BusportTsxx("���׽��:"+msg.obj.toString());
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ʧ��");
							dialog.dismiss();
							zhiweiDestroy(1);
						}
						break;		
				}				
			}
			
		};
		//�����û��Լ��������
		yinlianhttp=new Yinlianhttp(yinlianmainhand);
		yinlianthread=Executors.newCachedThreadPool();
		
		
		//***********************
		//����pos����
		//***********************
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(BusPort.this);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		if(tb_inaccount.getZhifubaofaca()>0)  
    		{
    			if(
                        (ToolClass.isEmptynull(ToolClass.getCardcom())==false)
                        &&(ToolClass.getCardcom().equals("null")==false)
                        )			    
    			{
    				isPossel=tb_inaccount.getZhifubaofaca();
    	    		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �򿪶�����"+ToolClass.getCardcom(),"com.txt");
	    	        //�򿪴���
    	    		int posipport=(ToolClass.getPosipport().equals(""))?0:Integer.parseInt(ToolClass.getPosipport());
    		        //ip���˿ڡ����ڡ������ʱ���׼ȷ"121.40.30.62", 18080
    				mMyApi.pos_init(ToolClass.getPosip(), posipport
    						,ToolClass.getCardcom(), "9600", mIUserCallback); 
	    			if(ToolClass.getPosisssl()==1)
	    	    	{
	    				ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ssl����","com.txt");
	    				mMyApi.pos_setKeyCert(ToolClass.getContext(), true, "CUP_cacert.pem");
	    				if(ToolClass.isPossign()==false)
	    				{
	    					ToolClass.setPossign(true);
	    					ToolClass.Log(ToolClass.INFO,"EV_COM","busport��sslǩ��","com.txt");
		    				mMyApi.pos_signin(mIUserCallback);
	    				}
	    	    	}
    			}
    		}
    	}				
		posmainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub	
				zhiposcheck=false;
				switch (msg.what) 
				{
					case CahslessTest.OPENSUCCESS:
						break;
					case CahslessTest.OPENFAIL:	
						break;
					case CahslessTest.CLOSESUCCESS:
						break;
					case CahslessTest.CLOSEFAIL:	
						break;
					case CahslessTest.READCARD:
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMAPI <<�Ѷ�����","com.txt");
	  					iszhipos=2;
						break;
					case CahslessTest.COSTSUCCESS:
						listterner.BusportTsxx("��ʾ��Ϣ���������");
						iszhipos=2;
						//��ʱ
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {         
				            	//��������ȡ������Ϣ
								mMyApi.pos_getrecord("000000000000000", "00000000","000000", mIUserCallback);
								zhiposcheck=true;
				            }

						}, 300);
						break;
					case CahslessTest.COSTFAIL:	
						listterner.BusportTsxx("��ʾ��Ϣ���ۿ�ʧ��"+cashbalance);
						iszhipos=0;
						break;
					case CahslessTest.QUERYSUCCESS:
					case CahslessTest.QUERYFAIL:	
						listterner.BusportTsxx("��ʾ��Ϣ�������"+cashbalance);
						//��ʱ
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {   
		                        zhifutype="1";
		                        tochuhuo();	
							}

						}, 3000);
						break;
					case CahslessTest.DELETESUCCESS:
					case CahslessTest.DELETEFAIL:	
						if(iszhipos==1)
						{
							//��ʱ
						    new Handler().postDelayed(new Runnable() 
							{
					            @Override
					            public void run() 
					            {   
					            	listterner.BusportTsxx("��ʾ��Ϣ����ˢ��");
					            	if(isPossel==1)//��Ա��
					                {
					            		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �������ۿ�="+amount+"[pos_purchase]<<amount_fen="+ToolClass.MoneySend(amount)
					            				+"type="+0,"com.txt");
						            	mMyApi.pos_purchase(ToolClass.MoneySend(amount), 0,mIUserCallback);
					                }
					                else if(isPossel>1)//���п�
					                {
					                	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �������ۿ�="+amount+"[pos_purchase]<<amount_fen="+ToolClass.MoneySend(amount)
					            				+"type="+1,"com.txt");
					                    mMyApi.pos_purchase(ToolClass.MoneySend(amount), 1,mIUserCallback);
					                }
							    	zhiposcheck=true;
								}

							}, 500);							
						}				
						break;						
					case CahslessTest.PAYOUTSUCCESS:
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(1);//��¼�˱ҳɹ�
							OrderDetail.setRealCard(amount);//��¼�˱ҽ��
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ɹ�");
							//��ʱ
						    new Handler().postDelayed(new Runnable() 
							{
					            @Override
					            public void run() 
					            {         
					            	dialog.dismiss();
					            	zhiposDestroy(1);
								}

							}, 300);							
						}
						break;
					case CahslessTest.PAYOUTFAIL:	
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusPort.this);
							//��������ҳ��
							listterner.BusportTsxx("���׽��:�˿�ʧ��");
							//��ʱ
						    new Handler().postDelayed(new Runnable() 
							{
					            @Override
					            public void run() 
					            {         
					            	dialog.dismiss();
					            	zhiposDestroy(1);
								}

							}, 300);							
						}
						break;	
					case CahslessTest.FINDSUCCESS:
						listternermovie.BusportCashless(cashbalance);
						break;
					case CahslessTest.FINDFAIL:						
						break;
				}
			}
		};	
		//=================
	    //��ӡ�����
	    //=================
		printmainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what) 
				{
					case PrintTest.NORMAL:
						ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ӡ������","com.txt");
						if(isPrinter==1)
							isPrinter=2;
						break;
					case PrintTest.NOPOWER:
						ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ӡ��δ���ӻ�δ�ϵ�","com.txt");
						break;
					case PrintTest.NOMATCH:
						ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ӡ���쳣[��ӡ���͵��ÿⲻƥ��]","com.txt");
						if(isPrinter==1)
							isPrinter=2;
						break;
					case PrintTest.HEADOPEN:	
						ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ӡ����ӡ��ͷ��","com.txt");
						break;
					case PrintTest.CUTTERERR:
						ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ӡ���е�δ��λ","com.txt");
						break;
					case PrintTest.HEADHEAT:
						ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ӡ��ͷ����","com.txt");
						break;
					case PrintTest.BLACKMARKERR:
						ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ӡ���ڱ����","com.txt");
						break;
					case PrintTest.PAPEREXH:	
						ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ӡ��ֽ��","com.txt");
						break;
					case PrintTest.PAPERWILLEXH://���Ҳ���Ե�����״̬ʹ��	
						ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ӡ��ֽ����","com.txt");
						if(isPrinter==1)
							isPrinter=2;
						break;
					case PrintTest.UNKNOWERR: 
						ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ӡ�������쳣="+msg.obj,"com.txt");
						break;
				}				
			}
		};
			
				
	}
	
	//��ʼ��Ĭ��fragment
	public void initView() {        
        // ����Ĭ�ϵ�Fragment
        setDefaultFragment();
    }
	
	// ����Ĭ�ϵ�Fragment
	@SuppressLint("NewApi")
    private void setDefaultFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        businessportFragment = new BusinessportFragment();
        transaction.replace(R.id.id_content, businessportFragment);
        transaction.commit();
    }
	
	//=======================
	//ʵ��Businessҳ����ؽӿ�
	//=======================
	@Override
	public void switchBusiness() {
		// TODO Auto-generated method stub		
	}

	//��������ʵ��Business�ӿ�,�������
	@Override
	public void finishBusiness() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<busland=�˳�����ҳ��","log.txt");
//    	Intent intent = new Intent();
//    	intent.setClass(BusPort.this, PassWord.class);// ʹ��AddInaccount���ڳ�ʼ��Intent
//        startActivityForResult(intent, PWD_CODE);
		//��ʱ0.5s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {      
            	finish(); 
            }

		}, 500);
	}
	//��������ʵ��Business�ӿ�,ת����Ʒ����ҳ��
	//buslevel��ת����ҳ��
	@Override
	public void gotoBusiness(int buslevel, Map<String, String> str)
	{
		if(ToolClass.checkCLIENT_STATUS_SERVICE())
		{
			listternermovie.BusportVideoStop(0);
			viewSwitch(buslevel, str);
		}
	}
	//��������ʵ��Business�ӿ�,����ȡ����
	@Override
	public void quhuoBusiness(String PICKUP_CODE)
	{
		if(ToolClass.checkCLIENT_STATUS_SERVICE())
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<portȡ����="+PICKUP_CODE,"log.txt");
			Intent intent2=new Intent(); 
			intent2.putExtra("EVWhat", EVServerhttp.SETPICKUPCHILD);
			intent2.putExtra("PICKUP_CODE", PICKUP_CODE);
			intent2.setAction("android.intent.action.vmserversend");//action���������ͬ
			localBroadreceiver.sendBroadcast(intent2);
		}
	}
	//��������ʵ��Business�ӿ�,������ʾ��Ϣ
	@Override
	public void tishiInfo(int infotype)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<port��ʾ����="+infotype,"log.txt");
		listternermovie.BusportMovie(infotype);
	}
	
	//=============
	//Server�������
	//=============	
	//2.����EVServerReceiver�Ľ������㲥���������շ�����ͬ��������
	public class EVServerReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			case EVServerhttp.SETPICKUPMAIN:
				String PRODUCT_NO=bundle.getString("PRODUCT_NO");
				out_trade_no=bundle.getString("out_trade_no");
				ToolClass.Log(ToolClass.INFO,"EV_JNI","BusPort=ȡ����ɹ�PRODUCT_NO="+PRODUCT_NO+"out_trade_no="+out_trade_no,"log.txt");					
				// ����InaccountDAO�������ڴ����ݿ�����ȡ���ݵ�Tb_vmc_product����
		 	    vmc_productDAO productdao = new vmc_productDAO(context);
		 	    Tb_vmc_product tb_vmc_product=productdao.find(PRODUCT_NO);
		 	    //���浽���������
		 	    //��������Ϣ
		 	    OrderDetail.setProID(tb_vmc_product.getProductID()+"-"+tb_vmc_product.getProductName());		 	    
		 	    OrderDetail.setProType("1");
		 	    //����֧���� 
		 	    zhifutype="-1";
		 	    OrderDetail.setShouldPay(tb_vmc_product.getSalesPrice());
		 	    OrderDetail.setShouldNo(1);
		 	    OrderDetail.setCabID("");
		 		OrderDetail.setColumnID("");
		 	    //������ϸ��Ϣ��   
		 	    OrderDetail.setProductID(PRODUCT_NO);
		 	    tochuhuo();
				break;
			case EVServerhttp.SETERRFAILPICKUPMAIN:
				ToolClass.Log(ToolClass.INFO,"EV_JNI","BusPort=ȡ����ʧ��","log.txt");
				// ������Ϣ��ʾ
				ToolClass.failToast("��Ǹ��ȡ������Ч,����ϵ����Ա��");
	    		break;	
			case EVServerhttp.SETADVRESETMAIN:
				ToolClass.Log(ToolClass.INFO,"EV_JNI","BusPort=ˢ�¹��","log.txt");
				listternermovie.BusportAds();
				break;
			}			
		}

	}
	
	//=======================
	//ʵ��BusgoodsClassҳ����ؽӿ�
	//=======================
	//��������ʵ��Busgoodsclass�ӿ�,ת����Ʒ����ҳ��
	@Override
	public void BusgoodsclassSwitch(Map<String, String> str) {
		// TODO Auto-generated method stub
		viewSwitch(BUSGOODS, str);
	}

	//��������ʵ��Busgoodsclass�ӿ�,ת����ҳ��
	@Override
	public void BusgoodsclassFinish() {
		// TODO Auto-generated method stub
		viewSwitch(BUSPORT, null);
	}
	
	
	//=======================
	//ʵ��Busgoodsҳ����ؽӿ�
	//=======================
	//��������ʵ��Busgoods�ӿ�,ת����Ʒ��ϸҳ��
	@Override
	public void BusgoodsSwitch(Map<String, String> str) {
		// TODO Auto-generated method stub
		viewSwitch(BUSGOODSSELECT, str);
	}
	//��������ʵ��Busgoodsclass�ӿ�,ת�����������ҳ��
	@Override
	public void gotoBusClass() {
		// TODO Auto-generated method stub		
		vmc_classDAO classdao = new vmc_classDAO(BusPort.this);// ����InaccountDAO����
    	long count=classdao.getCount();
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��Ʒ��������="+count,"log.txt");
    	if(count>0)
    	{
    		viewSwitch(BUSGOODSCLASS, null);
    	}
    	else
    	{
    		viewSwitch(BUSPORT, null);
    	}
	}
	//��������ʵ��Busgoodsclass�ӿ�,ת����ҳ��
	@Override
	public void BusgoodsFinish() {
		// TODO Auto-generated method stub
		viewSwitch(BUSPORT, null);
	}
	
	//=======================
	//ʵ��Busgoodsselectҳ����ؽӿ�
	//=======================
	//��������ʵ��Busgoodsselect�ӿ�,ת����Ʒ��ϸҳ��
	@Override
	public void BusgoodsselectSwitch(int buslevel) {
		// TODO Auto-generated method stub		
		viewSwitch(buslevel, null);
	}
	//��������ʵ��Busgoodsselect�ӿ�,ת����ҳ��
	@Override
	public void BusgoodsselectFinish() {
		// TODO Auto-generated method stub
		viewSwitch(BUSPORT, null);
	}
	//��������ʵ��Busgoodsselect�ӿ�,ת����ҳ��
	@Override
	public void BusgoodsSwitch() {
		// TODO Auto-generated method stub
		viewSwitch(BUSGOODS, proclassID);
	}
	
	//=======================
	//ʵ��Buszhiamountҳ����ؽӿ�
	//=======================
	//��������ʵ��Buszhiamount�ӿ�,ת����ҳ��
	@Override
	public void BuszhiamountFinish() {
		// TODO Auto-generated method stub
		//�ֽ�ģ���������Ͷ���Ѿ����������Թ����򲻽����˱Ҳ���
		if(iszhienable==2)
		{
			ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusPort �˱Ұ�ť��Ч","com.txt");
		}
		//֧����ģ���������ɨ���Ѿ����������Թ����򲻽����˿����
		else if(iszhier==2)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<zhier�˱Ұ�ť��Ч","log.txt");
    	}
		//΢��ģ���������ɨ���Ѿ����������Թ����򲻽����˿����
		else if(iszhiwei==2)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<zhiwei�˱Ұ�ť��Ч","log.txt");
    	}
		//΢��ģ���������ɨ���Ѿ����������Թ����򲻽����˿����
		else if(iszhiyinlian==2)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<zhiyinlian�˱Ұ�ť��Ч","log.txt");
    	}
		//posģ����������Ѿ�ˢ�������Թ����򲻽����˿����
    	if(iszhipos==2)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<zhipos�˱Ұ�ť��Ч","log.txt");
    	}
		else
		{
			//֧����ģ��:���ɶ�ά����Ҫ����
            if(iszhier==1)
			{
				deletezhier();
			}
			//΢��ģ��:���ɶ�ά����Ҫ����
            if(iszhiwei==1)
			{
				deletezhiwei();
			}
            //����ģ��:���ɶ�ά����Ҫ����
            if(iszhiyinlian==1)
			{
				
			}
			//posģ��:���Ϳۿ��ˣ���Ҫ����
			if(zhiposcheck)
			{
				deletezhipos();
			}
			
			
			if(iszhiamount==1)
	  		{
	  			dialog= ProgressDialog.show(BusPort.this,"�����˱���","���Ժ�...");
	  			OrderDetail.setPayStatus(2);//֧��ʧ��
	  			//�˱�
	  	    	//EVprotocolAPI.EV_mdbPayback(ToolClass.getCom_id(),1,1);
	  			Intent intent=new Intent();
		    	intent.putExtra("EVWhat", EVprotocol.EV_MDB_PAYBACK);	
				intent.putExtra("bill", 1);	
				intent.putExtra("coin", 1);	
				intent.setAction("android.intent.action.comsend");//action���������ͬ
				comBroadreceiver.sendBroadcast(intent);
	  		} 
			
			//�����˳�
	  		amountDestroy(0);			
		}
	    
	}
	
	//�ر�ҳ��:type=1��ʱ10s�ر�,0�����ر�
	private void amountDestroy(int type)
	{
		//��ʱ�ر�
		if(type==1)
		{
			//������
	    	clearamount();
  	    	recLen=10;
  	    	AudioSound.playbusfinish();  	    	
		}
		//�����ر�
		else
		{
			clearamount();
			viewSwitch(BUSPORT, null);
		}
		
		//�ر�ֽ��Ӳ����
		if(iszhiamountsel>0)
		{
			BillEnable(0);
		}
	}
	
	
    
    //=======================
  	//ʵ��Buszhierҳ����ؽӿ�
  	//=======================
   
    
    //���Ͷ���
  	private void sendzhier()
  	{	
  		if(ercheckopt())
  		{
	      	// ����Ϣ���͵����߳���
	      	zhifubaochildhand=zhifubaohttp.obtainHandler();
	  		Message childmsg=zhifubaochildhand.obtainMessage();
	  		childmsg.what=Zhifubaohttp.SETCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();	  			
	  	        ev.put("out_trade_no", out_trade_no);
	  			ev.put("total_fee", String.valueOf(amount));
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		zhifubaochildhand.sendMessage(childmsg);
  		}
  	}
    //��ѯ����
  	private void queryzhier()
  	{
  		if(ercheckopt())
  		{
	  		// ����Ϣ���͵����߳���
	  		zhifubaochildhand=zhifubaohttp.obtainHandler();
	  		Message childmsg=zhifubaochildhand.obtainMessage();
	  		childmsg.what=Zhifubaohttp.SETQUERYCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
	  			//ev.put("out_trade_no", "000120150301113215800");	
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		zhifubaochildhand.sendMessage(childmsg);
  		}
  	}
  	//��������
  	private void deletezhier()
  	{
  		//if(ercheckopt())
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<viewSwitch=��������","log.txt");
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
  		{
	  		// ����Ϣ���͵����߳���
	  		zhifubaochildhand=zhifubaohttp.obtainHandler();
	  		Message childmsg=zhifubaochildhand.obtainMessage();
	  		childmsg.what=Zhifubaohttp.SETDELETECHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
                ev.put("total_fee", String.valueOf(amount));
                ev.put("refund_fee", String.valueOf(amount));
                ev.put("out_refund_no", ToolClass.out_trade_no(BusPort.this));
	  			//ev.put("out_trade_no", "000120150301092857698");	
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		zhifubaochildhand.sendMessage(childmsg);
  		}
  	}
    //�˿�
  	private void payoutzhier()
  	{
  		//if(ercheckopt())
  		AudioSound.playbuspayout();
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
  		{
	  		// ����Ϣ���͵����߳���
	  		zhifubaochildhand=zhifubaohttp.obtainHandler();
	  		Message childmsg=zhifubaochildhand.obtainMessage();
	  		childmsg.what=Zhifubaohttp.SETPAYOUTCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
	  			ev.put("refund_amount", String.valueOf(amount));
	  			ev.put("out_request_no", ToolClass.out_trade_no(BusPort.this));
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		zhifubaochildhand.sendMessage(childmsg);
  		}  		
  	}
  	
    //�ر�ҳ��:type=1��ʱ10s�ر�,0�����ر�
  	private void zhierDestroy(int type)
  	{
  		//��ʱ�ر�
  		if(type==1)
  		{
  			clearamount();
			recLen=5;			
  		}
  		//�����ر�
  		else
  		{
  			clearamount();
	    	viewSwitch(BUSPORT, null);
  		}
  	}
    
    //=======================
  	//ʵ��Buszhiweiҳ����ؽӿ�
  	//=======================
    //�ж��Ƿ��ڶ�ά����̲߳�����,true��ʾ���Բ�����,false���ܲ���
  	private boolean weicheckopt()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<weicheck="+weicheck,"log.txt");
  		if(weicheck==false)
  		{
  			weicheck=true;
  			return true;
  		}
  		else
  		{
  			return false;
		}
  	}  
    //���Ͷ���
  	private void sendzhiwei()
  	{	
  		if(weicheckopt())
  		{
	      	// ����Ϣ���͵����߳���
	      	weixingchildhand=weixinghttp.obtainHandler();
	  		Message childmsg=weixingchildhand.obtainMessage();
	  		childmsg.what=Weixinghttp.SETCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();	  			
	  	        ev.put("out_trade_no", out_trade_no);
	  			ev.put("total_fee", String.valueOf(amount));
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		weixingchildhand.sendMessage(childmsg);
  		}
  	}
  	//��ѯ����
  	private void queryzhiwei()
  	{
  		if(weicheckopt())
  		{
	  		// ����Ϣ���͵����߳���
	  		weixingchildhand=weixinghttp.obtainHandler();
	  		Message childmsg=weixingchildhand.obtainMessage();
	  		childmsg.what=Weixinghttp.SETQUERYCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
	  			//ev.put("out_trade_no", "000120150301113215800");	
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		weixingchildhand.sendMessage(childmsg);
  		}
  	}
  	//�˿��
  	private void payoutzhiwei()
  	{
  		//if(weicheckopt())
  		AudioSound.playbuspayout();
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<weicheckopt="+weicheck,"log.txt");
  		{
	  		// ����Ϣ���͵����߳���
	  		weixingchildhand=weixinghttp.obtainHandler();
	  		Message childmsg=weixingchildhand.obtainMessage();
	  		childmsg.what=Weixinghttp.SETPAYOUTCHILD;
	  		try {
				weipayout.put("out_trade_no", out_trade_no);	
				weipayout.put("total_fee", String.valueOf(amount));
				weipayout.put("refund_fee", String.valueOf(amount));
				weipayout.put("out_refund_no", ToolClass.out_trade_no(BusPort.this));
				Log.i("EV_JNI","Send0.1="+weipayout.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  		childmsg.obj=weipayout;
	  		weixingchildhand.sendMessage(childmsg);
  		}  		
  	}
  	
	//��������
  	private void deletezhiwei()
  	{
  		//if(weicheckoptopt())
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<viewSwitch=��������","log.txt");
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<weicheckopt="+weicheck,"log.txt");
  		{
	  		// ����Ϣ���͵����߳���
	  		weixingchildhand=weixinghttp.obtainHandler();
	  		Message childmsg=weixingchildhand.obtainMessage();
	  		childmsg.what=Weixinghttp.SETDELETECHILD;
	  		try {
				weipayout.put("out_trade_no", out_trade_no);		
				weipayout.put("total_fee", String.valueOf(amount));
				weipayout.put("refund_fee", String.valueOf(amount));
                weipayout.put("out_refund_no", ToolClass.out_trade_no(BusPort.this));
				Log.i("EV_JNI","Send0.1="+weipayout.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  		childmsg.obj=weipayout;
	  		weixingchildhand.sendMessage(childmsg);
  		}
  	}
  	
    //�ر�ҳ��:type=1��ʱ10s�ر�,0�����ر�
  	private void zhiweiDestroy(int type)
  	{
  		//��ʱ�ر�
  		if(type==1)
  		{
  			clearamount();
			recLen=5;	
  		}
  		//�����ر�
  		else
  		{
  			clearamount();
	    	viewSwitch(BUSPORT, null);
  		}
  	}
 
  	//=======================
  	//ʵ��Buszhiyinlianҳ����ؽӿ�
  	//=======================
    //�ж��Ƿ��ڶ�ά����̲߳�����,true��ʾ���Բ�����,false���ܲ���
  	private boolean yinliancheckopt()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<yinliancheck="+yinliancheck,"log.txt");
  		if(yinliancheck==false)
  		{
  			yinliancheck=true;
  			return true;
  		}
  		else
  		{
  			return false;
		}
  	}  
    //���Ͷ���
  	private void sendzhiyinlian()
  	{	
  		if(yinliancheckopt())
  		{
	      	// ����Ϣ���͵����߳���
	      	yinlianchildhand=yinlianhttp.obtainHandler();
	  		Message childmsg=yinlianchildhand.obtainMessage();
	  		childmsg.what=Yinlianhttp.SETCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();	  			
	  	        ev.put("out_trade_no", out_trade_no);
	  			ev.put("total_fee", String.valueOf(amount));
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		yinlianchildhand.sendMessage(childmsg);
  		}
  	}
  	//��ѯ����
  	private void queryzhiyinlian()
  	{
  		if(yinliancheckopt())
  		{
	  		// ����Ϣ���͵����߳���
	  		yinlianchildhand=yinlianhttp.obtainHandler();
	  		Message childmsg=yinlianchildhand.obtainMessage();
	  		childmsg.what=Yinlianhttp.SETQUERYCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
	  			//ev.put("out_trade_no", "000120150301113215800");	
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		yinlianchildhand.sendMessage(childmsg);
  		}
  	}
  	//�˿��
  	private void payoutzhiyinlian()
  	{
  		//if(yinliancheckopt())
  		AudioSound.playbuspayout();
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<yinliancheckopt="+yinliancheck,"log.txt");
  		{
	  		// ����Ϣ���͵����߳���
	  		yinlianchildhand=yinlianhttp.obtainHandler();
	  		Message childmsg=yinlianchildhand.obtainMessage();
	  		childmsg.what=Yinlianhttp.SETPAYOUTCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
	  			ev.put("total_fee", String.valueOf(amount));
	  			ev.put("refund_fee", String.valueOf(amount));
	  			ev.put("out_refund_no", ToolClass.out_trade_no(BusPort.this));
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		yinlianchildhand.sendMessage(childmsg);
  		}  		
  	}
  		  	
    //�ر�ҳ��:type=1��ʱ10s�ر�,0�����ر�
  	private void zhiyinlianDestroy(int type)
  	{
  		//��ʱ�ر�
  		if(type==1)
  		{
  			clearamount();
			recLen=5;	
  		}
  		//�����ر�
  		else
  		{
  			clearamount();
	    	viewSwitch(BUSPORT, null);
  		}
  	}
  	
    //=======================
  	//ʵ��Buszhiposҳ����ؽӿ�
  	//=======================
      
  	
    //��������
  	private void deletezhipos()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<viewSwitch=��������","log.txt");
  		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ����������ˢ��ǰ��..","com.txt");
    	mMyApi.pos_cancel();
    	zhiposcheck=true;
  	}
  	
    //�˿��
  	private void payoutzhipos()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �������˿�="+amount,"com.txt");
  		AudioSound.playbuspayout();
  	    if(isPossel==1)//��Ա��
  		{
  			mMyApi.pos_refund("000000000000000", "00000000",rfd_card_no,ToolClass.MoneySend(amount),rfd_spec_tmp_serial,0, mIUserCallback);
  		}
  		else if(isPossel>1)//���п�
  		{
            mMyApi.pos_refund("000000000000000", "00000000",rfd_card_no,1,"      ",1, mIUserCallback);
        }
  		zhiposcheck=true;
  	}
  
  	//�ӿڷ���
  	private IUserCallback mIUserCallback = new IUserCallback(){
  		@Override
  		public void onResult(REPLY rst) 
  		{
  			if(rst!=null) 
  			{
  				Message childmsg=posmainhand.obtainMessage();
  				//info(rst.op + ":" + rst.code + "," + rst.code_info);
  				//��������ʶ����LfMISPOSApi�¡�OP_����ͷ�ľ�̬�������磺LfMISPOSApi.OP_INIT��LfMISPOSApi.OP_PURCHASE�ȵ�
  				//�򿪴���
				if(rst.op.equals(LfMISPOSApi.OP_INIT))
				{
					//�����������Ϣ��code��code_info�ķ���/˵������com.landfoneapi.mispos.ErrCode
					if(rst.code.equals(ErrCode._00.getCode())){//����00������ɹ�
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �򿪳ɹ�"+ToolClass.getCardcom(),"com.txt");
						childmsg.what=CahslessTest.OPENSUCCESS;
						childmsg.obj="�򿪳ɹ�"+ToolClass.getCardcom();
					}else{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ��ʧ��"+ToolClass.getCardcom()+",code:"+rst.code+",info:"+rst.code_info,"com.txt");						
						childmsg.what=CahslessTest.OPENFAIL;
						childmsg.obj="��ʧ��"+ToolClass.getCardcom()+",code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//�رմ���
				else if(rst.op.equals(LfMISPOSApi.OP_RELEASE))
				{
					if(rst.code.equals(ErrCode._00.getCode())){//����00������ɹ�
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �رճɹ�","com.txt");
						childmsg.what=CahslessTest.CLOSESUCCESS;
						childmsg.obj="�رճɹ�";
					}else{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �ر�ʧ��,code:"+rst.code+",info:"+rst.code_info,"com.txt");						
						childmsg.what=CahslessTest.CLOSEFAIL;
						childmsg.obj="�ر�ʧ��,code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//ǩ��
				else if (rst.op.equals(LfMISPOSApi.OP_SIGNIN)) 
				{
                    if (rst.code.equals(ErrCode._00.getCode())) {//����00������ɹ�
                    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ǩ���ɹ�","com.txt");
                    } else {
                        ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ǩ��ʧ��,code111:"+rst.code+",info:"+rst.code_info,"com.txt");						
                    }
                }
				//���㡾����ʱ����ܻ�ǳ�����
				else if (rst.op.equals(LfMISPOSApi.OP_SETTLE)) 
				{
                    if (rst.code.equals(ErrCode._00.getCode())) {//����00������ɹ�
                    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ����ɹ�","com.txt");                        
                    } else {
                    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ����ʧ��,code:"+rst.code+",info:"+rst.code_info,"com.txt");		
                    }
                } 
				//�ۿ�
				else if(rst.op.equals(LfMISPOSApi.OP_PURCHASE))
				{
					if(rst.code.equals(ErrCode._00.getCode())){//����00������ɹ�
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �ۿ�ɹ�","com.txt");
						childmsg.what=CahslessTest.COSTSUCCESS;
						childmsg.obj="�ۿ�ɹ�";
					}
					else if(rst.code.equals(ErrCode._XY.getCode())){
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �����ɹ�","com.txt");
  						childmsg.what=CahslessTest.DELETESUCCESS;
  						childmsg.obj="�����ɹ�";
  					}
					else{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �ۿ�ʧ��,code:"+rst.code+",info:"+rst.code_info,"com.txt");
						if(rst.code.equals("01"))
						{
							cashbalance=rst.code_info;
						}
						childmsg.what=CahslessTest.COSTFAIL;
						childmsg.obj="�ۿ�ʧ��,code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//��ѯ
				else if (rst.op.equals(LfMISPOSApi.OP_QUERY)) 
				{
                    if (rst.code.equals(ErrCode._00.getCode()))
                    {
                        String tmp = "��ѯ���:";
                        tmp += "\n������룺" + ((_04_QueryReply) (rst)).getCode();
                        tmp += ",\n��ʾ��Ϣ��" + ((_04_QueryReply) (rst)).getCode_info();
                        tmp += ",\n����:" + ((_04_QueryReply) (rst)).getCardNo();//����
                        tmp += ",\n�ֽ����:" + ((_04_QueryReply) (rst)).getCashBalance();//�ֽ����
                        tmp += ",\n�������:" + ((_04_QueryReply) (rst)).getPointBalance();//�������
                        ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ��ѯ�ɹ�="+tmp,"com.txt");
                        //��ѯ������ȡ
                        cashbalance = ((_04_QueryReply) (rst)).getCashBalance();
                        childmsg.what=CahslessTest.FINDSUCCESS;
						childmsg.obj="��ѯ�ɹ�="+tmp;
                        
                    } 
                    else if(rst.code.equals(ErrCode._XY.getCode())){
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �����ɹ�","com.txt");
  						childmsg.what=CahslessTest.DELETESUCCESS;
  						childmsg.obj="�����ɹ�";
  					}
                    else 
                    {
                    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ��ѯʧ��,code:"+rst.code+",info:"+rst.code_info,"com.txt");
                        childmsg.what=CahslessTest.FINDFAIL;
						childmsg.obj="��ѯʧ��,code:"+rst.code+",info:"+rst.code_info;
                    }
                } 
				//�˿�
  				else if(rst.op.equals(LfMISPOSApi.OP_REFUND))
  				{
  					//����00������ɹ�
					if(rst.code.equals(ErrCode._00.getCode()))
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �˿�ɹ�","com.txt");
						childmsg.what=CahslessTest.PAYOUTSUCCESS;
  						childmsg.obj="�˿�ɹ�";
					}else
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �˿�ʧ��,code:"+rst.code+",info:"+rst.code_info,"com.txt");
						childmsg.what=CahslessTest.PAYOUTFAIL;
						childmsg.obj="�˿�ʧ��,code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//���ؽ��
				else if(rst.op.equals(LfMISPOSApi.OP_GETRECORD))
				{
					//����00������ɹ�
					if(rst.code.equals(ErrCode._00.getCode()))
					{
						String tmp = "����:�ض���Ϣ=";
						tmp += "[" + ((_04_GetRecordReply) (rst)).getSpecInfoField();//�ض���Ϣ����Ա����Ҫ������
						/*�ض���Ϣ˵��
						+��ֵ����(19)
						+�ն���ˮ��(6)
						+�ն˱��(8)
						+���κ�(6)
						+�̻���(15)
						+�̻�����(60)
						+��Ա����(60)
						+����ʱ��(6)
						+��������(8)
						+���׵���(14)
						+���ѽ��(12)
						+�˻����(12)
						+��ʱ������ˮ�ţ�26��
						���϶��Ƕ��������Ƕ���12λ��ǰ��0����������λ���󲹿ո�

						* */
						tmp += "],�̻�����=[" + ((_04_GetRecordReply) (rst)).getMer();//�̻�����
						tmp += "],�ն˺�=[" + ((_04_GetRecordReply) (rst)).getTmn();//�ն˺�
						tmp += "],����=[" + ((_04_GetRecordReply) (rst)).getCardNo();//����
						tmp += "],�������κ�=[" + ((_04_GetRecordReply) (rst)).getTransacionBatchNo();//�������κ�
						tmp += "],ԭ��������=[" + ((_04_GetRecordReply) (rst)).getTransacionVoucherNo();//ԭ��������
						tmp += "],�������ں�ʱ��=[" + ((_04_GetRecordReply) (rst)).getTransacionDatetime();//�������ں�ʱ��
						tmp += "],���׽��=[" + ((_04_GetRecordReply) (rst)).getTransacionAmount();//���׽��
						tmp +="]";
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ���سɹ�="+tmp,"com.txt");

  						//�˿������ȡ
						String tmp_spec = ((_04_GetRecordReply) (rst)).getSpecInfoField();
						int tmp_spec_len = tmp_spec!=null?tmp_spec.length():0;
						//����
						//rfd_amt_fen = amount;//ʹ���ϴ�ȫ����Խ���1��
						//���˿�š�
						if(tmp_spec!=null && tmp_spec_len>(2+19)){
							rfd_card_no = (((_04_GetRecordReply) (rst)).getSpecInfoField()).substring(0+2,2+19).trim();
						}
						//��ʣ���
						if(tmp_spec!=null && tmp_spec_len>(2+19)){
							cashbalance = (((_04_GetRecordReply) (rst)).getSpecInfoField()).substring((tmp_spec_len-26-12),(tmp_spec_len-26)).trim();
						}
						//����ʱ������ˮ�š�
						if(tmp_spec!=null && tmp_spec_len>26){
							rfd_spec_tmp_serial = (((_04_GetRecordReply) (rst)).getSpecInfoField()).substring((tmp_spec_len-26),tmp_spec_len);
						}else{//ʹ�ÿո�ʱ����ʾ��һ�εġ���ʱ������ˮ�š�
							rfd_spec_tmp_serial = String.format("%1$-26s","");
						}
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �˿����=���"+amount+"����="+rfd_card_no+"��ˮ��="+rfd_spec_tmp_serial+"ʣ����="+cashbalance,"com.txt");
						childmsg.what=CahslessTest.QUERYSUCCESS;
						childmsg.obj="���سɹ�="+tmp;
					}
					else
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ����ʧ��,code:"+rst.code+",info:"+rst.code_info,"com.txt");
						cashbalance="";
						childmsg.what=CahslessTest.QUERYFAIL;
						childmsg.obj="����ʧ��";
					}
				}
				posmainhand.sendMessage(childmsg);
  			}
  		}

  		@Override
  		public void onProcess(Display dpl) {//���̺���ʾ��Ϣ
  			if(dpl!=null) {
  				//lcd(dpl.getType() + "\n" + dpl.getMsg());

  				//����ʾ��Ϣ���͡�type��˵������com.landfoneapi.mispos.DisplayType
  				if(dpl.getType().equals(DisplayType._4.getType())){
  					ToolClass.Log(ToolClass.INFO,"EV_COM","COMAPI ͨѶ��ʾ<<"+dpl.getMsg(),"com.txt");
  				}  				
  				else if(dpl.getType().equals(DisplayType._h.getType()))
  				{
  					Message childmsg=posmainhand.obtainMessage();
  					childmsg.what=CahslessTest.READCARD;
					childmsg.obj="�Ѷ�����";  	
					posmainhand.sendMessage(childmsg);
  				}
  			}
  		}
  	};
  	
  	
  	private void zhiposDestroy(int type)
  	{
  		//��ʱ�ر�
  		if(type==1)
  		{
  			clearamount();
			recLen=5;	
  		}
  		//�����ر�
  		else
  		{
  			clearamount();
	    	viewSwitch(BUSPORT, null);		    	
  		}  	    
  	}
  	
  	
    //=======================
  	//ʵ��Bushuoҳ����ؽӿ�
  	//=======================
    //��������ʵ��Bushuo�ӿ�,����
    @Override
	public void BushuoChuhuoOpt(int cabinetvar, int huodaoNo,int cabinetTypevar) {
		// TODO Auto-generated method stub
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<busport��ƷcabID="+cabinetvar+"huoID="+huodaoNo+"cabType="+cabinetTypevar,"log.txt");
    	//2.���������
		float cost=0;
		if(Integer.parseInt(zhifutype)==0)
		{
			cost=amount;
		}
    	ToolClass.Log(ToolClass.INFO,"EV_JNI",
		    	"[APPsend>>]cabinet="+String.valueOf(cabinetvar)
		    	+" column="+huodaoNo
		    	+" cost="+cost
		    	,"log.txt");
		Intent intent = new Intent();
		//4.����ָ��㲥��COMService
		intent.putExtra("EVWhat", COMService.EV_CHUHUOCHILD);	
		intent.putExtra("cabinet", cabinetvar);	
		intent.putExtra("column", huodaoNo);	
		intent.putExtra("cost", ToolClass.MoneySend(cost));
		intent.setAction("android.intent.action.comsend");//action���������ͬ
		comBroadreceiver.sendBroadcast(intent);
    }
    
    //��������ʵ��Bushuo�ӿ�,��������ҳ��
    @Override
	public void BushuoFinish(final int status) {
    	// TODO Auto-generated method stub
    	recLen=SPLASH_DISPLAY_LENGHT;
    	//=======
		//��ӡ�����
		//=======
		// ��ӡ
		if(isPrinter>0)
        {
			//�����ɹ�,��ӡƾ֤
			if((status==1)&&(OrderDetail.getPayType()!=5))
			{
				new Handler().postDelayed(new Runnable() 
				{
		            @Override
		            public void run() 
		            {	  
		            	ToolClass.Log(ToolClass.INFO,"EV_COM","busport��ӡƾ֤...","com.txt");
						if(isdocter)
				    	{
				    		PrintDocter();                             // ��ӡСƱ
				    	}
				    	else
				    	{
				    		PrintBankQueue();                             // ��ӡСƱ
				    	}
		            }

				}, 300);
			}
        }
		
		//=============
		//��ӡ�����
		//=============
		if(isPrinter>0)
		{        			
			CloseComPort(ComA);// 2.1 �رմ���
		}
    	switch(OrderDetail.getPayType())
    	{
    		//�ֽ�ҳ��
    		case 0:            			
    			//viewSwitch(BUSZHIAMOUNT, null);
    			//1.
  				//�����ɹ�,��Ǯ
				if(status==1)
				{
					//��Ǯ
		  	    	//EVprotocolAPI.EV_mdbCost(ToolClass.getCom_id(),ToolClass.MoneySend(amount));
					ToolClass.setLAST_CHUHUO(true);
					Intent intent=new Intent();
			    	intent.putExtra("EVWhat", EVprotocol.EV_MDB_COST);	
					intent.putExtra("cost", ToolClass.MoneySend((float)amount));	
					intent.setAction("android.intent.action.comsend");//action���������ͬ
					comBroadreceiver.sendBroadcast(intent);					
				}
				//����ʧ��,����Ǯ
				else
				{	
					payback();
				}				
    			break;
    		//posҳ��	
    		case 1:
    			//�����ɹ�,��������
				if(status==1)
				{
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<pos���˿�","log.txt");
					ToolClass.setLAST_CHUHUO(true);
					OrderDetail.addLog(BusPort.this);
			        AudioSound.playbusfinish();
					zhiposDestroy(1);
				}
				//����ʧ��,��Ǯ
				else
				{	
					ispayoutopt=1;
					ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<pos�˿�amount="+amount,"com.txt");
					dialog= ProgressDialog.show(BusPort.this,"�����˿���","���Ժ�...");
					payoutzhipos();//�˿����									
				}
    			break;	
    		//֧����ҳ��	
    		case 3:            			
    			//�����ɹ�,��������
				if(status==1)
				{
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ali���˿�","log.txt");
					ToolClass.setLAST_CHUHUO(true);
					OrderDetail.addLog(BusPort.this);	
					AudioSound.playbusfinish();
					zhierDestroy(1);
				}
				//����ʧ��,��Ǯ
				else
				{	
					ispayoutopt=1;
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ali�˿�amount="+amount,"log.txt");					
					dialog= ProgressDialog.show(BusPort.this,"�����˿���","���Ժ�...");
					payoutzhier();//�˿����	
				}
    			break;
    		//΢��ҳ��	
    		case 4:            			
    			//�����ɹ�,��������
				if(status==1)
				{
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<wei���˿�","log.txt");
					ToolClass.setLAST_CHUHUO(true);
					OrderDetail.addLog(BusPort.this);
					AudioSound.playbusfinish();
					zhiweiDestroy(1);
				}
				//����ʧ��,��Ǯ
				else
				{	
					ispayoutopt=1;
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<wei�˿�amount="+amount,"log.txt");
					dialog= ProgressDialog.show(BusPort.this,"�����˿���","���Ժ�...");
					payoutzhiwei();//�˿����									
				}
    			break; 
    		//����ҳ��	
    		case 7:            			
    			//�����ɹ�,��������
				if(status==1)
				{
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<yinlian���˿�","log.txt");
					ToolClass.setLAST_CHUHUO(true);
					OrderDetail.addLog(BusPort.this);
					AudioSound.playbusfinish();
					zhiweiDestroy(1);
				}
				//����ʧ��,��Ǯ
				else
				{	
					ispayoutopt=1;
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<yinlian�˿�amount="+amount,"log.txt");
					dialog= ProgressDialog.show(BusPort.this,"�����˿���","���Ժ�...");
					payoutzhiyinlian();//�˿����									
				}
    			break;	
    		//��������ҳ��		
    		case 5:
    			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��������ҳ��","log.txt");
    			clearamount();
				recLen=5;
    			break;	
    		//ȡ����ҳ��		
    		case -1:
    			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ȡ����ҳ��","log.txt");
    			ToolClass.setLAST_CHUHUO(true);
    			OrderDetail.addLog(BusPort.this);					
				clearamount();
				recLen=5;
    			break;
    	}
    	
    	
	}
    
    //�����˳�����ҳ��
    @Override
	public void BushuoNow() {
		// TODO Auto-generated method stub
		recLen=1;
	}
    
    //������ɺ󣬽����˱�����
  	private void payback()
  	{
  		//2.����Ͷ�ҽ��
		ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<�˿�money="+money,"com.txt");
		//��������˱�
		if(money>0)
		{
			dialog= ProgressDialog.show(BusPort.this,"�����˱���,���"+money,"���Ժ�...");
			AudioSound.playbuspayout();
			new Handler().postDelayed(new Runnable() 
			{
	            @Override
	            public void run() 
	            {            	
	            	//�˱�
	    	    	//EVprotocolAPI.EV_mdbPayback(ToolClass.getCom_id(),1,1);
  					Intent intent=new Intent();
  			    	intent.putExtra("EVWhat", EVprotocol.EV_MDB_PAYBACK);	
  					intent.putExtra("bill", 1);	
  					intent.putExtra("coin", 1);	
  					intent.setAction("android.intent.action.comsend");//action���������ͬ
  					comBroadreceiver.sendBroadcast(intent);				    	
	            }

			}, 500);   					
		} 
	    //ûʣ������ˣ����˱�
		else
		{  					
	    	OrderDetail.addLog(BusPort.this);
	    	amountDestroy(1);
		}
  	}
  	
    //=======================
  	//ʵ�ִ�ӡ����ؽӿ�
  	//=======================
  //=================
    //��ӡ�����
    //=================
	//��ȡ��ӡ��Ϣ
    private void ReadSharedPreferencesPrinter()
    {
    	//�ļ���˽�е�
    	SharedPreferences  user = getSharedPreferences("print_info",0);
    	//��ȡ
    	//����һ
    	istitle1=user.getBoolean("istitle1",true);
    	if(istitle1==false)
    	{
    		title1str="";
    	}
    	else
    	{
    		title1str=user.getString("title1str", "�����ۻ���");
    	}
		//�����
    	istitle2=user.getBoolean("istitle2",true);
    	if(istitle2==false)
    	{
    		title2str="";
    	}
    	else
    	{
    		title2str=user.getString("title2str", "����ƾ֤");
    	}
		//�������
		isno=user.getBoolean("isno",true);
		serialno=user.getInt("serialno", 1);
		//���ͳ��
		issum=user.getBoolean("issum",true);
		//��л��ʾ
    	isthank=user.getBoolean("isthank",true);
    	if(isthank==false)
    	{
    		thankstr="";
    	}
    	else
    	{
    		thankstr=user.getString("thankstr", "ллʹ�������ۻ���,���ǽ��߳�Ϊ������!");
    	}
		//��ά��
    	iser=user.getBoolean("iser",true);
    	if(iser==false)
    	{
    		erstr="";
    	}
    	else
    	{
    		erstr=user.getString("erstr", "http://www.easivend.com.cn/");
    	}
		//��ǰʱ��
		isdate=user.getBoolean("isdate",true);
		//ҩ��СƱ
		isdocter=user.getBoolean("isdocter",true);
    }
    
  
    //д����ˮ����Ϣ
    private void SaveSharedPreferencesSerialno()
    {
    	//�ļ���˽�е�
		SharedPreferences  user = getSharedPreferences("print_info",0);
		//��Ҫ�ӿڽ��б༭
		SharedPreferences.Editor edit=user.edit();
		//д��
		//�������
		edit.putInt("serialno", serialno);
		//�ύ����
		edit.commit();
    }
    
    // ------------------�򿪴���--------------------
    private void OpenComPort(SerialHelper ComPort) {
		try {
			ComPort.open();
		} catch (SecurityException e) {
			ToolClass.Log(ToolClass.ERROR,"EV_COM","û�ж�/дȨ��","com.txt");
		} catch (IOException e) {
			ToolClass.Log(ToolClass.ERROR,"EV_COM","δ֪����","com.txt");
		} catch (InvalidParameterException e) {
			ToolClass.Log(ToolClass.ERROR,"EV_COM","��������","com.txt");
		}
	}
    
    // ------------------�رմ���--------------------
 	private void CloseComPort(SerialHelper ComPort) {
 		if (ComPort != null) {
 			ComPort.stopSend();
 			ComPort.close();
 		}
 	}
 	
    // -------------------------��ѯ״̬---------------------------
 	private void GetPrinterStates(SerialHelper ComPort, byte[] sOut) {
  		try { 			
 			if (ComPort != null && ComPort.isOpen()) {
 				ComPort.send(sOut);
 				ercheck = true; 				
 				ToolClass.Log(ToolClass.INFO,"EV_COM","��ӡ��״̬��ѯ...","com.txt");
 			} else
 			{
 				ToolClass.Log(ToolClass.ERROR,"EV_COM","��ӡ������δ��","com.txt"); 				
 			}
 		} catch (Exception ex) {
 			ToolClass.Log(ToolClass.ERROR,"EV_COM","��ӡ�����ڴ��쳣="+ex.getMessage(),"com.txt"); 			
 		}
  		
 	}
 	/**
	 * ��ӡ���۵���
	 */
	private void PrintBankQueue() {
		try {
			// СƱ����
//			byte[] bValue = new byte[100];
			ComA.send(PrintCmd.SetBold(0));
			ComA.send(PrintCmd.SetAlignment(1));
			ComA.send(PrintCmd.SetSizetext(1, 1));
			//����һ
			if(istitle1)
			{
				ComA.send(PrintCmd.PrintString(title1str+"\n\n", 0));
			}
			//�����
			if(istitle2)
			{
				ComA.send(PrintCmd.PrintString(title2str+"\n\n", 0));
			}
			//�������
			if(isno)
			{
				ComA.send(PrintCmd.SetBold(1));
				ComA.send(PrintCmd.PrintString(String.format("%04d", serialno++)+"\n\n", 0));
			}
			// СƱ��Ҫ����
			CleanPrinter(); // �����棬ȱʡģʽ
			ComA.send(PrintCmd.PrintString(OrderDetail.getProID()+"  ����"+OrderDetail.getShouldPay()+"Ԫ\n", 0));
			ComA.send(PrintCmd.PrintString("_________________________________________\n\n", 0));
			//���ͳ��
			if(issum)
			{
				ComA.send(PrintCmd.PrintString("�ܼ�:"+OrderDetail.getShouldPay()*OrderDetail.getShouldNo()+"Ԫ\n", 0));
			}
			//��л��ʾ
			if(isthank)
			{
				ComA.send(PrintCmd.PrintString(thankstr+"\n", 0));
			}
			// ��ά��
			if(iser)
			{
				ComA.send(PrintCmd.PrintFeedline(2));    
				PrintQRCode();  // ��ά���ӡ                          
				ComA.send(PrintCmd.PrintFeedline(2));   
			}
			//��ǰʱ��
			if(isdate)
			{
				ComA.send(PrintCmd.SetAlignment(2));
				ComA.send(PrintCmd.PrintString(sdf.format(new Date()).toString() + "\n\n", 1));
			}
			// ��ֽ4��,����ֽ,������
			PrintFeedCutpaper(4);  
			SaveSharedPreferencesSerialno();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ��ӡҩ��СƱ
	 */
	private void PrintDocter() {
		try {
			// СƱ����
			ComA.send(PrintCmd.SetAlignment(1));
			ComA.send(PrintCmd.PrintString("ҩ��СƱ\n", 0));
			CleanPrinter(); // �����棬ȱʡģʽ	
			//�������
			if(isno)
			{
				ComA.send(PrintCmd.PrintString("�վݺ�:                                  "+String.format("%06d", 385017+(serialno++))+"\n", 0));
			}					
			//��ǰʱ��
			if(isdate)
			{
				ComA.send(PrintCmd.PrintString(sdfdoc.format(new Date()).toString() + "\n\n", 1));
			}
			ComA.send(PrintCmd.PrintString("����      Ʒ�����     �۸�*����     С��\n", 0));
			ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
			//ComA.send(PrintCmd.PrintString("160705  ��̵��߽���0.32g*10��*3��  57*4  228\n", 0));
			ComA.send(PrintCmd.PrintString(OrderDetail.getProID()+"   "+OrderDetail.getShouldPay()+"*"+OrderDetail.getShouldNo()+"   "+OrderDetail.getShouldPay()+"\n", 0));
			ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
			ComA.send(PrintCmd.PrintString("�ϼ�(�����):                         "+OrderDetail.getShouldPay()*OrderDetail.getShouldNo()+"\n", 0));
			ComA.send(PrintCmd.PrintString("Ӧ�տ�:                               "+OrderDetail.getShouldPay()*OrderDetail.getShouldNo()+"\n", 0));
			//0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��
			if(OrderDetail.getPayType()==0)
			{
				ComA.send(PrintCmd.PrintString("ʵ�տ�:                              "+OrderDetail.getSmallAmount()+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("����:                                        \n", 0));
				ComA.send(PrintCmd.PrintString(" ������֧��:                      0.00\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("�һ�(�����):                      "+(OrderDetail.getSmallAmount()-OrderDetail.getShouldPay())+"\n\n", 0));
			}
			else if(OrderDetail.getPayType()==1)
			{
				ComA.send(PrintCmd.PrintString("ʵ�տ�:                              "+OrderDetail.getShouldPay()*OrderDetail.getShouldNo()+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("����:                                        \n", 0));
				ComA.send(PrintCmd.PrintString(" ������֧��:                      "+OrderDetail.getShouldPay()*OrderDetail.getShouldNo()+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("�һ�(�����):                      0.00\n\n", 0));
			}
			else if(OrderDetail.getPayType()==3)
			{
				ComA.send(PrintCmd.PrintString("ʵ�տ�:                              "+OrderDetail.getShouldPay()*OrderDetail.getShouldNo()+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("����:                                        \n", 0));
				ComA.send(PrintCmd.PrintString(" ֧����֧��:                      "+OrderDetail.getShouldPay()*OrderDetail.getShouldNo()+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("�һ�(�����):                      0.00\n\n", 0));
			}
			else if(OrderDetail.getPayType()==4)
			{
				ComA.send(PrintCmd.PrintString("ʵ�տ�:                              "+OrderDetail.getShouldPay()*OrderDetail.getShouldNo()+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("����:                                        \n", 0));
				ComA.send(PrintCmd.PrintString("  ΢��֧��:                      "+OrderDetail.getShouldPay()*OrderDetail.getShouldNo()+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("�һ�(�����):                      0.00\n\n", 0));
			}
			ComA.send(PrintCmd.PrintString("��������СƱ�Ա��ۺ����лл����ҩƷ��������Ʒ������������ˡ���˻�\n", 0));			
			// ��ֽ4��,����ֽ,������
			PrintFeedCutpaper(4);  
			SaveSharedPreferencesSerialno();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// ��ӡ��ά��
	private void PrintQRCode() throws IOException {
		byte[] bValue = new byte[50];
		bValue = PrintCmd.PrintQrcode(erstr, 25, 7, 1);
		ComA.send(bValue);
	}
	
	// ��ֽ���У�����ֽ��������
	private void PrintFeedCutpaper(int iLine) throws IOException{
		ComA.send(PrintCmd.PrintFeedline(iLine));
		ComA.send(PrintCmd.PrintCutpaper(0));
		ComA.send(PrintCmd.SetClean());
	}
	// �����棬ȱʡģʽ
	private void CleanPrinter(){
		ComA.send(PrintCmd.SetClean());
	}
    
	// -------------------------ˢ����ʾ�߳�---------------------------
	private class DispQueueThread extends Thread {
		private Queue<ComBean> QueueList = new LinkedList<ComBean>();
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				final ComBean ComData;
				while ((ComData = QueueList.poll()) != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							DispRecData(ComData);
						}
					});
					try {
						Thread.sleep(200);// ��ʾ���ܸߵĻ������԰Ѵ���ֵ��С��
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		public synchronized void AddQueue(ComBean ComData) {
			QueueList.add(ComData);
		}
	}
	
	// ------------------------��ʾ��������----------------------------
	 /*
	  * 0 ��ӡ������ ��1 ��ӡ��δ���ӻ�δ�ϵ硢2 ��ӡ���͵��ÿⲻƥ�� 
	  * 3 ��ӡͷ�� ��4 �е�δ��λ ��5 ��ӡͷ���� ��6 �ڱ���� ��7 ֽ�� ��8 ֽ����
	  */
	private void DispRecData(ComBean ComRecData) {
		Message childmsg=printmainhand.obtainMessage();
		StringBuilder sMsg = new StringBuilder();
		try {
			sMsg.append(MyFunc.ByteArrToHex(ComRecData.bRec));
			int iState = PrintCmd.CheckStatus(ComRecData.bRec); // ���״̬
			ToolClass.Log(ToolClass.INFO,"EV_COM","busport����״̬��" + iState + "======="
					+ ComRecData.bRec[0],"com.txt");
			switch (iState) 
			{
			case 0:
				ToolClass.Log(ToolClass.INFO,"EV_COM","busport>>����","com.txt");
				sMsg.append("����");                 // ����
				ercheck = true;
				childmsg.what=PrintTest.NORMAL;
				break;
			case 1:
				ToolClass.Log(ToolClass.INFO,"EV_COM","busport>>δ���ӻ�δ�ϵ�","com.txt");
				sMsg.append("δ���ӻ�δ�ϵ�");//δ���ӻ�δ�ϵ�
				ercheck = true;
				childmsg.what=PrintTest.NOPOWER;
				break;
			case 2:
				ToolClass.Log(ToolClass.INFO,"EV_COM","busport>>�쳣[��ӡ���͵��ÿⲻƥ��]","com.txt");
				sMsg.append("�쳣[��ӡ���͵��ÿⲻƥ��]");               //�쳣[��ӡ���͵��ÿⲻƥ��]
				ercheck = false;
				childmsg.what=PrintTest.NOMATCH;
				break;
			case 3:
				ToolClass.Log(ToolClass.INFO,"EV_COM","busport>>��ӡ��ͷ��","com.txt");
				sMsg.append("��ӡ��ͷ��");        //��ӡ��ͷ��
				ercheck = true;
				childmsg.what=PrintTest.HEADOPEN;
				break;
			case 4:
				ToolClass.Log(ToolClass.INFO,"EV_COM","busport>>�е�δ��λ","com.txt");
				sMsg.append("�е�δ��λ");         //�е�δ��λ
				ercheck = true;
				childmsg.what=PrintTest.CUTTERERR;
				break;
			case 5:
				ToolClass.Log(ToolClass.INFO,"EV_COM","busport>>��ӡͷ����","com.txt");
				sMsg.append("��ӡͷ����");    // ��ӡͷ����
				ercheck = true;
				childmsg.what=PrintTest.HEADHEAT;
				break;
			case 6:
				ToolClass.Log(ToolClass.INFO,"EV_COM","busport>>�ڱ����","com.txt");
				sMsg.append("�ڱ����");         // �ڱ����
				ercheck = true;
				childmsg.what=PrintTest.BLACKMARKERR;
				break;
			case 7:
				ToolClass.Log(ToolClass.INFO,"EV_COM","busport>>ֽ��","com.txt");
				sMsg.append("ֽ��");               //ֽ��
				ercheck = true;
				childmsg.what=PrintTest.PAPEREXH;
				break;
			case 8:
				ToolClass.Log(ToolClass.INFO,"EV_COM","busport>>ֽ����","com.txt");
				sMsg.append("ֽ����");           //ֽ����
				ercheck = true;
				childmsg.what=PrintTest.PAPERWILLEXH;
				break;
			default:
				break;
			}
			childmsg.obj=sMsg.toString();
		} catch (Exception ex) {
			childmsg.what=PrintTest.UNKNOWERR;
			childmsg.obj=ex.getMessage();
		}
		printmainhand.sendMessage(childmsg);
	}
    // -------------------------�ײ㴮�ڿ�����---------------------------
    private static class SerialControl extends SerialHelper {
		public SerialControl() {
		}
		private static SerialControl single = null;
		// ��̬��������
		public static SerialControl getInstance() {
			if (single == null) {
				single = new SerialControl();
			}
			return single;
		}
		@Override
		protected void onDataReceived(final ComBean ComRecData) {
			DispQueue.AddQueue(ComRecData);// �̶߳�ʱˢ����ʾ(�Ƽ�)
		}
	}
    
    //=======================
  	//ʵ����ػ��������ӿ�
  	//=======================
    //��������
    private void tochuhuo()
    {        
    	ischuhuo=true;
//    	Intent intent = null;// ����Intent����                
//    	intent = new Intent(BusZhiAmount.this, BusHuo.class);// ʹ��Accountflag���ڳ�ʼ��Intent
////    	intent.putExtra("out_trade_no", out_trade_no);
////    	intent.putExtra("proID", proID);
////    	intent.putExtra("productID", productID);
////    	intent.putExtra("proType", proType);
////    	intent.putExtra("cabID", cabID);
////    	intent.putExtra("huoID", huoID);
////    	intent.putExtra("prosales", prosales);
////    	intent.putExtra("count", count);
////    	intent.putExtra("reamin_amount", reamin_amount);
////    	intent.putExtra("zhifutype", zhifutype);    	
//    	startActivityForResult(intent, REQUEST_CODE);// ��Accountflag
    	OrderDetail.setOrdereID(out_trade_no);
    	OrderDetail.setPayType(Integer.parseInt(zhifutype));
    	OrderDetail.setRfd_card_no(rfd_card_no);
    	
    	//�ֽ�ģ��
    	if(iszhiamountsel>0)
    	{
    		BillEnable(0);	//�ر��ֽ��豸
    	}
    	//posģ��
    	iszhipos=2;
    	if(zhiposcheck==true)//����������̲߳����У���Ҫ����
		{
			deletezhipos();
		}
    	//֧����΢��ģ��
    	//֧����ģ��:���ɶ�ά����Ҫ����
        if(iszhier==1)
		{
			deletezhier();
		}
		//΢��ģ��:���ɶ�ά����Ҫ����
        if(iszhiwei==1)
		{
			deletezhiwei();
		}
        //����ģ��:���ɶ�ά����Ҫ����
        if(iszhiyinlian==1)
		{
		}
    	
    	//֧����΢��ģ��,�Լ�posģ����ֽ���
    	if(zhifutype.equals("0")!=true)
    	{
    		OrderDetail.setSmallCard(amount);
    	}
    	//=================
	    //��ӡ�����
	    //=================
    	vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(BusPort.this);// ����InaccountDAO����
	    // �õ��豸ID��
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		isPrinter=tb_inaccount.getIsNet();
    	}
    	ToolClass.Log(ToolClass.INFO,"EV_COM","isPrinter=" + isPrinter,"com.txt");
        if(isPrinter>0)
        {
        	ToolClass.Log(ToolClass.INFO,"EV_COM","�򿪴�ӡ��","com.txt");
        	ReadSharedPreferencesPrinter();
	    	ComA = SerialControl.getInstance();
	        ComA.setbLoopData(PrintCmd.GetStatus());
	        DispQueue = new DispQueueThread();
			DispQueue.start();
			//�򿪴���
			ComA.setPort(ToolClass.getPrintcom());            // 1.1 �趨����
			ComA.setBaudRate("9600");// 1.2 �趨������
			OpenComPort(ComA); // 1.3 �򿪴���			
        }
        viewSwitch(BUSHUO, null);   	
    }
    //�����
  	public void clearamount()
  	{
  		//�ֽ�ҳ��
  	    con = 0;
  	    queryLen = 0; 
  	    iszhienable=0;//1���ʹ�ָ��,0��û���ʹ�ָ��
  	    isempcoin=false;//false��δ���͹�ֽ����ָ�true��Ϊȱ�ң��Ѿ����͹�ֽ����ָ��
  	    billdev=1;//�Ƿ���Ҫ��ֽ����,1��Ҫ
  	    dispenser=0;//0��,1hopper,2mdb
  		billmoney=0;coinmoney=0;money=0;//Ͷ�ҽ��
  		amount=0;//��Ʒ��Ҫ֧�����
  		iszhiamount=0;//1�ɹ�Ͷ��Ǯ,0û�гɹ�Ͷ��Ǯ
  		RealNote=0;
  		RealCoin=0;
  		RealAmount=0;//�˱ҽ��
  		ischuhuo=false;//true�Ѿ��������ˣ������ϱ���־
  		//֧����ҳ��
  		iszhier=0;//1�ɹ������˶�ά��,0û�гɹ����ɶ�ά��
  		ispayoutopt=0;//1���ڽ����˱Ҳ���,0δ�����˱Ҳ���
  		ercheck=false;//true���ڶ�ά����̲߳����У����Ժ�falseû�ж�ά����̲߳���
  	    //΢��ҳ��
  		iszhiwei=0;//1�ɹ������˶�ά��,0û�гɹ����ɶ�ά��
  		weicheck=false;//true���ڶ�ά����̲߳����У����Ժ�falseû�ж�ά����̲߳���
  		//����ҳ��
  		iszhiyinlian=0;//1�ɹ������˶�ά��,0û�гɹ����ɶ�ά��
  		yinliancheck=false;//true���ڶ�ά����̲߳����У����Ժ�falseû�ж�ά����̲߳���
  		//posҳ��
  		iszhipos=0;//0δˢ��,2ˢ���ۿ��Ѿ���ɲ��ҽ���㹻  	
  	  	cashbalance = "";//��ѯ����  	    
  	   	rfd_card_no = "";//�˿����
  	   	rfd_spec_tmp_serial = "";
  		  		
  	    //��ӡ��ҳ��
  		isPrinter=0;//0û�����ô�ӡ����1�����ô�ӡ����2��ӡ���Լ�ɹ������Դ�ӡ
  		//����ҳ��
  		status=0;//�������	
  	}
  	
  	//�ж��Ƿ��ڶ�ά����̲߳�����,true��ʾ���Բ�����,false���ܲ���
  	private boolean ercheckopt()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
  		if(ercheck==false)
  		{
  			ercheck=true;
  			return true;
  		}
  		else
  		{
  			return false;
		}
  	}
	
	//ȫ�������л�view�ĺ���
	public void viewSwitch(int buslevel, Map<String, String> str)
	{
		recLen=SPLASH_DISPLAY_LENGHT;
		gotoswitch=buslevel;
		Bundle data = new Bundle();
		FragmentManager fm = getFragmentManager();
        // ����Fragment����
        FragmentTransaction transaction = fm.beginTransaction();
		// TODO Auto-generated method stub
		switch(buslevel)
		{
			case BUSPORT://��ҳ��
				isbus=true;
				if (businessportFragment == null) {
					businessportFragment = new BusinessportFragment();
	            }
	            // ʹ�õ�ǰFragment�Ĳ������id_content�Ŀؼ�
	            transaction.replace(R.id.id_content, businessportFragment);	
	            listternermovie.BusportVideoStop(1);
				break;
			case BUSGOODSCLASS://��Ʒ���
				isbus=false;
				if (busgoodsclassFragment == null) {
					busgoodsclassFragment = new BusgoodsclassFragment();
	            }
	            // ʹ�õ�ǰFragment�Ĳ������id_content�Ŀؼ�
	            transaction.replace(R.id.id_content, busgoodsclassFragment);
				break;
			case BUSGOODS:
				isbus=false;
//				intent = new Intent(BusPort.this, Busgoods.class);// ʹ��Accountflag���ڳ�ʼ��Intent
//            	intent.putExtra("proclassID", "");
//            	startActivityForResult(intent,REQUEST_CODE);// ��Accountflag
				if (busgoodsFragment == null) {
					busgoodsFragment = new BusgoodsFragment();
	            }
				proclassID=str;
				//�����塢����������ݸ�friendfragment
				//��������
		        data.clear();
		        data.putString("proclassID", str.get("proclassID"));
		        busgoodsFragment.setArguments(data);
	            // ʹ�õ�ǰFragment�Ĳ������id_content�Ŀؼ�
	            transaction.replace(R.id.id_content, busgoodsFragment);
				break;
			case BUSGOODSSELECT:
				isbus=false;
//				intent = new Intent(BusPort.this, BusgoodsSelect.class);// ʹ��Accountflag���ڳ�ʼ��Intent
//	        	intent.putExtra("proID", str.get("proID"));
//	        	intent.putExtra("productID", str.get("productID"));
//	        	intent.putExtra("proImage", str.get("proImage"));
//	        	intent.putExtra("prosales", str.get("prosales"));
//	        	intent.putExtra("procount", str.get("procount"));
//	        	intent.putExtra("proType", str.get("proType"));//1����ͨ����ƷID����,2����ͨ����������
//	        	intent.putExtra("cabID", str.get("cabID"));//�������,proType=1ʱ��Ч
//	        	intent.putExtra("huoID", str.get("huoID"));//����������,proType=1ʱ��Ч


//	        	OrderDetail.setProID(proID);
//            	OrderDetail.setProductID(productID);
//            	OrderDetail.setProType("2");
//            	OrderDetail.setCabID(cabID);
//            	OrderDetail.setColumnID(huoID);
//            	OrderDetail.setShouldPay(Float.parseFloat(prosales));
//            	OrderDetail.setShouldNo(1);
	        	
//	        	startActivityForResult(intent,REQUEST_CODE);// ��Accountflag
				if (busgoodsselectFragment == null) {
					busgoodsselectFragment = new BusgoodsselectFragment();
	            }
				//�����塢����������ݸ�friendfragment
				//��������
				data.clear();
		        data.putString("proID", str.get("proID"));
		        data.putString("productID", str.get("productID"));
		        data.putString("proImage", str.get("proImage"));
		        data.putString("prosales", str.get("prosales"));
		        data.putString("procount", str.get("procount"));
		        data.putString("proType", str.get("proType"));//1����ͨ����ƷID����,2����ͨ����������
		        data.putString("cabID", str.get("cabID"));//�������,proType=1ʱ��Ч
		        data.putString("huoID", str.get("huoID"));//����������,proType=1ʱ��Ч
	        	busgoodsselectFragment.setArguments(data);
	            // ʹ�õ�ǰFragment�Ĳ������id_content�Ŀؼ�
	            transaction.replace(R.id.id_content, busgoodsselectFragment);
	            AudioSound.playbusselect();
				break;
			case BUSZHIAMOUNT://�ֽ�֧��
				isbus=false;			
				
				amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo(); 
				out_trade_no=ToolClass.out_trade_no(BusPort.this);// ����InaccountDAO����;
				//�л�ҳ��
				if (buszhiamountFragment == null) {
					buszhiamountFragment = new BuszhiamountFragment();
	            }
	            // ʹ�õ�ǰFragment�Ĳ������id_content�Ŀؼ�
	            transaction.replace(R.id.id_content, buszhiamountFragment);
	           
			    
			    
			    
			    //*********************
				//�������Եõ���֧����ʽ
				//*********************
				vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(BusPort.this);// ����InaccountDAO����
			    // ��ȡ����������Ϣ�����洢��List���ͼ�����
		    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
		    	if(tb_inaccount!=null)
		    	{
		    		if(tb_inaccount.getAmount()>0)
		    		{
		    			//��
		    			iszhiamountsel=1;
		    			//Heart����
					    Intent intent2=new Intent();
				    	intent2.putExtra("EVWhat", EVprotocol.EV_MDB_HEART);
						intent2.setAction("android.intent.action.comsend");//action���������ͬ
						comBroadreceiver.sendBroadcast(intent2);
						 //��ʱ
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {   
				            	//��ֽ����
								BillEnable(1);
								queryamountLen=0;
				            }

						}, 30);
		    		}	
		    		//֧����
		    		if(tb_inaccount.getZhifubaoer()>0)
		    		{
		    			iszhiersel=1;
		    			//��
		    			//�½�һ���̲߳�����
						zhifubaothread.execute(zhifubaohttp);
						//��ʱ
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {   
					            //���Ͷ���
				        		sendzhier();
				        		queryzhierLen=0;
				            }

						}, 10);
		    		}
		    		//΢��
		    		if(tb_inaccount.getWeixing()>0)
		    		{
		    			iszhiweisel=1;
		    			//��
		    			//�½�һ���̲߳�����
			            weixingthread.execute(weixinghttp);
						//��ʱ
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {   
				            	//���Ͷ���
				        		sendzhiwei();
				        		queryzhiweiLen=0;
				            }

						}, 20);
		    		}
		    		//������ά��
		    		if(tb_inaccount.getPrinter()>0)
		    		{
		    			iszhiyinliansel=1;
		    			//��
		    			//�½�һ���̲߳�����
			            yinlianthread.execute(yinlianhttp);
						//��ʱ
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {   
				            	//���Ͷ���
				        		sendzhiyinlian();
				        		queryzhiyinlianLen=0;
				            }

						}, 20);
		    		}
		    		//pos��
		    		if(tb_inaccount.getZhifubaofaca()>0)
		    		{
		    			//��
		    			//***********************
			    		//����pos����
			    		//***********************
			            if(isPossel>0)
			            {
			            	iszhipos=1;
				    		if(zhiposcheck==true)//����������̲߳����У���Ҫ����
				    		{
				    			deletezhipos();
				    		}	
				    		else
				    		{
				    			//��ʱ
							    new Handler().postDelayed(new Runnable() 
								{
						            @Override
						            public void run() 
						            {   
						            	listterner.BusportTsxx("��ʾ��Ϣ����ˢ��");
						            	if(isPossel==1)//��Ա��
						                {
						            		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �������ۿ�="+amount+"[pos_purchase]<<amount_fen="+ToolClass.MoneySend(amount)
						            				+"type="+0,"com.txt");
						                    mMyApi.pos_purchase(ToolClass.MoneySend(amount), 0,mIUserCallback);
						                }
						                else if(isPossel>1)//���п�
						                {
						                	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �������ۿ�="+amount+"[pos_purchase]<<amount_fen="+ToolClass.MoneySend(amount)
						                			+"type="+1,"com.txt");
						                    mMyApi.pos_purchase(ToolClass.MoneySend(amount), 1,mIUserCallback);
						                }	
								    	zhiposcheck=true;
									}
			
								}, 40);
				    		}
			            }
		    		}    		
		    	}
				break;				
			case BUSHUO://����ҳ��	
				recLen=10*60;
				isbus=false;
				if (bushuoFragment == null) {
					bushuoFragment = new BushuoFragment();
	            }
	            // ʹ�õ�ǰFragment�Ĳ������id_content�Ŀؼ�
	            transaction.replace(R.id.id_content, bushuoFragment);
				break;
		}
		// transaction.addToBackStack();
        // �����ύ
        transaction.commit();
	}
		
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{		
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<businessJNI","log.txt");		
	}
	//2.����COMReceiver�Ľ������㲥���������շ�����ͬ��������
	public class COMReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			//��������	
			case COMThread.EV_OPTMAIN: 
				SerializableMap serializableMap = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusPort �ۺ��豸����="+Set,"com.txt");
				int EV_TYPE=Set.get("EV_TYPE");
				switch(EV_TYPE)
				{
					case EVprotocol.EV_MDB_ENABLE:	
						//��
						if((Integer)Set.get("opt")==1)
						{
							//��ʧ��,�ȴ����´�
							if( ((Integer)Set.get("bill_result")==0)&&((Integer)Set.get("coin_result")==0) )
							{
								listterner.BusportTsxx("��ʾ��Ϣ������"+con);
								if((Integer)Set.get("bill_result")==0)
									ToolClass.setBill_err(2);
								if((Integer)Set.get("coin_result")==0)
									ToolClass.setCoin_err(2);
								con++;
								billdev=0;
							}
							//ֽ��������
							else if( ((Integer)Set.get("bill_result")==0)&&((Integer)Set.get("coin_result")==1) )
							{
								listterner.BusportTsxx("��ʾ��Ϣ��[ֽ����]�޷�ʹ��");
								//��һ�δ򿪲ŷ���coninfo���Ժ�Ͳ��ٲ��������
								if(iszhienable==0)
								{
									//EVprotocolAPI.EV_mdbCoinInfoCheck(ToolClass.getCom_id());
									//Ӳ������ѯ�ӿ�
									Intent intent3=new Intent();
							    	intent3.putExtra("EVWhat", EVprotocol.EV_MDB_C_INFO);	
									intent3.setAction("android.intent.action.comsend");//action���������ͬ
									comBroadreceiver.sendBroadcast(intent3);
								}
								ToolClass.setBill_err(2);
								ToolClass.setCoin_err(0);
							}	
							//�򿪳ɹ�
							else 
							{
								//��һ�δ򿪲ŷ���coninfo���Ժ�Ͳ��ٲ��������
								if(iszhienable==0)
								{
									//EVprotocolAPI.EV_mdbCoinInfoCheck(ToolClass.getCom_id());
									//Ӳ������ѯ�ӿ�
									Intent intent3=new Intent();
							    	intent3.putExtra("EVWhat", EVprotocol.EV_MDB_C_INFO);	
									intent3.setAction("android.intent.action.comsend");//action���������ͬ
									comBroadreceiver.sendBroadcast(intent3);
								}
								ToolClass.setBill_err(0);
								ToolClass.setCoin_err(0);
							}		
						}												
						break;
					case EVprotocol.EV_MDB_B_INFO:
						break;
					case EVprotocol.EV_MDB_C_INFO:
						dispenser=(Integer)Set.get("dispenser");						
					    //Heart����
					    Intent intent4=new Intent();
				    	intent4.putExtra("EVWhat", EVprotocol.EV_MDB_HEART);
						intent4.setAction("android.intent.action.comsend");//action���������ͬ
						comBroadreceiver.sendBroadcast(intent4);
						iszhienable=1;
						break;	
					case EVprotocol.EV_MDB_HEART://������ѯ
						Map<String,Object> obj=new LinkedHashMap<String, Object>();
						obj.putAll(Set);
						String bill_enable="";
						String coin_enable="";
						String hopperString="";
						int bill_err=ToolClass.getvmcStatus(obj,1);
						int coin_err=ToolClass.getvmcStatus(obj,2);	
						//����ֽ��������ʱ��������ֽ������
						if(bill_err>0)
						{
							billdev=0;
						}
						//ֽ����ҳ��
						if(iszhienable==1)
						{								
							ToolClass.setBill_err(bill_err);
							ToolClass.setCoin_err(coin_err);
							if(bill_err>0)
								bill_enable="[ֽ����]�޷�ʹ��";
							if(coin_err>0)
								coin_enable="[Ӳ����]�޷�ʹ��";	
							int hopper1=0;
							if(dispenser==1)//hopper
						  	{
								hopper1=ToolClass.getvmcStatus(obj,3);
								if(hopper1>0)
									hopperString="[������]:"+ToolClass.gethopperstats(hopper1);
						  	}
							else if(dispenser==2)//mdb
						  	{
						  		//��ǰ��ҽ��С��5Ԫ
						  		if(ToolClass.MoneyRec((Integer)Set.get("coin_remain"))<5)
						  		{
						  			hopperString="[������]:ȱ��";
						  		}
						  	}
							listterner.BusportTsxx("��ʾ��Ϣ��"+bill_enable+coin_enable+hopperString);
							billmoney=ToolClass.MoneyRec((Integer)Set.get("bill_recv"));	
						  	coinmoney=ToolClass.MoneyRec((Integer)Set.get("coin_recv"));
						  	money=billmoney+coinmoney;
						  	//���ȱ��,�Ͱ�ֽ��Ӳ�����ر�
						  	if(dispenser==1)//hopper
						  	{
						  		if(hopper1>0)//hopperȱ��
						  		{
							  		if(isempcoin==false)//��һ�ιر�ֽ��Ӳ����
							  		{
							  			//�ر�ֽ��Ӳ����
							  	    	BillEnable(0);
							  			isempcoin=true;
							  		}
						  		}
						  	}
						  	else if(dispenser==2)//mdb
						  	{
						  		//��ǰ��ҽ��С��5Ԫ
						  		if(ToolClass.MoneyRec((Integer)Set.get("coin_remain"))<5)
						  		{
						  			if(isempcoin==false)//��һ�ιر�ֽ��Ӳ����
							  		{
							  			//�ر�ֽ��Ӳ����
							  	    	BillEnable(0);
							  			isempcoin=true;
							  		}
						  		}
						  	}
						  	
						  	if(money>0)
						  	{						  		
						  		iszhiamount=1;
						  		recLen = 180;
						  		listterner.BusportTbje(String.valueOf(money));
						  		OrderDetail.setSmallNote(billmoney);
						  		OrderDetail.setSmallConi(coinmoney);
						  		OrderDetail.setSmallAmount(money);
						  		if(money>=amount)
						  		{
						  			iszhienable=2;
						  			zhifutype="0";						  			
						  			tochuhuo();
						  		}
						  	}
						}						
						break;
					case EVprotocol.EV_MDB_COST://�ۿ�����
						float cost=ToolClass.MoneyRec((Integer)Set.get("cost"));	
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusPort �ۿ�="+cost,"com.txt");
						money-=amount;//�ۿ�
						payback();								
					case EVprotocol.EV_MDB_PAYOUT://����			
						break;	
					case EVprotocol.EV_MDB_PAYBACK://�˱�
						RealNote=ToolClass.MoneyRec((Integer)Set.get("bill_changed"));	
						RealCoin=ToolClass.MoneyRec((Integer)Set.get("coin_changed"));	
						RealAmount=RealNote+RealCoin;						
						OrderDetail.setRealNote(RealNote);
				    	OrderDetail.setRealCoin(RealCoin);
				    	OrderDetail.setRealAmount(RealAmount);
				    	//�˱����
				    	if(RealAmount==money)
				    	{
				    		OrderDetail.setRealStatus(1);//�˿����				    		
				    	}
				    	//Ƿ��
				    	else
				    	{
				    		OrderDetail.setRealStatus(3);//�˿�ʧ��
				    		OrderDetail.setDebtAmount(money-RealAmount);//Ƿ����
				    	}
				    	if(ischuhuo==true)
				    	{
				    		OrderDetail.addLog(BusPort.this);
				    	}
				    	else
				    	{
				    		OrderDetail.cleardata();
						}
				    	dialog.dismiss();
						if(gotoswitch==BUSZHIAMOUNT)
			  	    	{			  	    		
			  	    	}
			  	    	else
			  	    	{
			  	    		amountDestroy(1);
			  	    	}
						break; 
					//�ǳ�������	
					case EVprotocol.EV_BENTO_OPEN://���ӹ���� 					
					case EVprotocol.EV_COLUMN_OPEN://�������
						status=Set.get("result");//�������
						listterner.BusportChjg(status);
					default:break;	
				}
				break;				
			}
		}

	}
	//1��,0�رչر�ֽ��Ӳ����   
  	private void BillEnable(int opt)
  	{  		 	
		Intent intent=new Intent();
		intent.putExtra("EVWhat", EVprotocol.EV_MDB_ENABLE);	
		intent.putExtra("bill", billdev);	
		intent.putExtra("coin", 1);	
		intent.putExtra("opt", opt);	
		intent.setAction("android.intent.action.comsend");//action���������ͬ
		comBroadreceiver.sendBroadcast(intent);	
  	}
	@Override
	protected void onDestroy() {
		timer.cancel(); 
		//=============
		//Server�������
		//=============
		//5.���ע�������
		localBroadreceiver.unregisterReceiver(receiver);
		//=============
		//COM�������
		//=============
		//5.���ע�������
		comBroadreceiver.unregisterReceiver(comreceiver);
		//***********************
		//����pos����
		//***********************
		if(isPossel>0)
		{
			ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ����������","com.txt");
//	    	mMyApi.pos_release();//���ڲ��ùر���
			gotoswitch=0;
			iszhipos=0;
            mMyApi.pos_cancel();  
//			mMyApi=null;
		}
		//�˳�ʱ������intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}
	

	
	

	

	

	

	

	

	

	

	

}
