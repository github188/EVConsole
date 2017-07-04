package com.easivend.app.business;

import java.util.Map;

import com.easivend.common.AudioSound;
import com.easivend.common.OrderDetail;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.model.Tb_vmc_product;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class BusgoodsSelect extends Activity 
{
	private final int SPLASH_DISPLAY_LENGHT = 5*60*1000; // �ӳ�5����	
	public static BusgoodsSelect BusgoodsSelectAct=null;
	ImageView ivbusgoodselProduct=null,imgbtnbusgoodsback=null;
	ImageView ivbuszhiselamount=null,ivbuszhiselzhier=null,ivbuszhiselweixing=null,
			ivbuszhiselpos=null,ivbuszhiseltihuo=null;
	TextView txtbusgoodselName=null,txtbusgoodselAmount=null;
	WebView webproductDesc;
	private String proID = null;
	private String productID = null;
	private String proImage = null;	
    private String prosales = null;
    private String procount = null;
    private String proType=null;
    private String cabID = null;
	private String huoID = null;
	//=================
    //COM�������
    //=================
  	LocalBroadcastManager comBroadreceiver;
  	COMReceiver comreceiver;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ��title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // ȫ��
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.busgoodsselect);
		BusgoodsSelectAct = this;
		AudioSound.playbusselect();
		//=============
  		//COM�������
  		//=============
		//4.ע�������
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		
		//����Ʒҳ����ȡ����ѡ�е���Ʒ
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		proID=bundle.getString("proID");
		productID=bundle.getString("productID");
		proImage=bundle.getString("proImage");
		prosales=bundle.getString("prosales");
		procount=bundle.getString("procount");
		proType=bundle.getString("proType");
		cabID=bundle.getString("cabID");
		huoID=bundle.getString("huoID");
		
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��ƷproID="+proID+" productID="+productID+" proImage="
					+proImage+" prosales="+prosales+" procount="
					+procount+" proType="+proType+" cabID="+cabID+" huoID="+huoID,"log.txt");
		ivbusgoodselProduct = (ImageView) findViewById(R.id.ivbusgoodselProduct);
		/*ΪʲôͼƬһ��Ҫת��Ϊ Bitmap��ʽ�ģ��� */
        Bitmap bitmap = ToolClass.getLoacalBitmap(proImage); //�ӱ���ȡͼƬ(��cdcard�л�ȡ)  //
        ivbusgoodselProduct.setImageBitmap(bitmap);// ����ͼ��Ķ�����ֵ
		txtbusgoodselName = (TextView) findViewById(R.id.txtbusgoodselName);
		txtbusgoodselName.setText(proID);
		txtbusgoodselAmount = (TextView) findViewById(R.id.txtbusgoodselAmount);
		if(Integer.parseInt(procount)>0)
		{
			txtbusgoodselAmount.setText(prosales);
		}
		else
		{
			txtbusgoodselAmount.setText("������");
		}
		//�õ���Ʒ����
		if(ToolClass.getOrientation()==1)
		{
			webproductDesc = (WebView) findViewById(R.id.webproductDesc); 
			vmc_productDAO productDAO = new vmc_productDAO(this);// ����InaccountDAO����
		    Tb_vmc_product tb_vmc_product = productDAO.find(productID);
		    if(ToolClass.isEmptynull(tb_vmc_product.getProductDesc())!=true)
		    {
		    	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��ƷDesc="+tb_vmc_product.getProductDesc().toString(),"log.txt");
			    WebSettings settings = webproductDesc.getSettings();
			    settings.setSupportZoom(true);
			    settings.setTextSize(WebSettings.TextSize.LARGEST);
			    webproductDesc.getSettings().setSupportMultipleWindows(true);
			    webproductDesc.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //���ù�������ʽ
			    webproductDesc.getSettings().setDefaultTextEncodingName("UTF -8");//����Ĭ��Ϊutf-8
			    webproductDesc.loadDataWithBaseURL(null,tb_vmc_product.getProductDesc().toString(), "text/html; charset=UTF-8","utf-8", null);//����д��������ȷ���Ľ���
		    }
		    else
		    {
		    	webproductDesc.setVisibility(View.GONE);
		    }
		}
		ivbuszhiselamount = (ImageView) findViewById(R.id.ivbuszhiselamount);
		ivbuszhiselamount.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
			    	Intent intent = null;// ����Intent����                
	            	intent = new Intent(BusgoodsSelect.this, BusZhiAmount.class);// ʹ��Accountflag���ڳ�ʼ��Intent
	            	startActivity(intent);// ��Accountflag
		    	}
		    }
		});
		ivbuszhiselzhier = (ImageView) findViewById(R.id.ivbuszhiselzhier);
		ivbuszhiselzhier.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
			    	Intent intent = null;// ����Intent����                
	            	intent = new Intent(BusgoodsSelect.this, BusZhier.class);// ʹ��Accountflag���ڳ�ʼ��Intent
	            	startActivity(intent);// ��Accountflag
		    	}
		    }
		});
		ivbuszhiselweixing = (ImageView) findViewById(R.id.ivbuszhiselweixing);	
		ivbuszhiselweixing.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
			    	Intent intent = null;// ����Intent����                
	            	intent = new Intent(BusgoodsSelect.this, BusZhiwei.class);// ʹ��Accountflag���ڳ�ʼ��Intent
	            	startActivity(intent);// ��Accountflag
		    	}
		    }
		});
		ivbuszhiselpos = (ImageView) findViewById(R.id.ivbuszhiselpos);	
		ivbuszhiselpos.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
			    	Intent intent = null;// ����Intent����                
	            	intent = new Intent(BusgoodsSelect.this, BusZhipos.class);// ʹ��Accountflag���ڳ�ʼ��Intent
	            	startActivity(intent);// ��Accountflag
		    	}
		    }
		});
		ivbuszhiseltihuo = (ImageView) findViewById(R.id.ivbuszhiseltihuo);	
		ivbuszhiseltihuo.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
			    	Intent intent = null;// ����Intent����                
	            	intent = new Intent(BusgoodsSelect.this, BusZhitihuo.class);// ʹ��Accountflag���ڳ�ʼ��Intent
	            	startActivity(intent);// ��Accountflag
		    	}
		    }
		});
		//*********************
		//�������Եõ���֧����ʽ
		//*********************
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(BusgoodsSelect.this);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		int zhifucount=0;
    		ivbuszhiseltihuo.setVisibility(View.GONE);//�ر�
			if(tb_inaccount.getAmount()==0)
    		{
    			ivbuszhiselamount.setVisibility(View.GONE);//�ر�
    		}
    		else
    		{
    			ivbuszhiselamount.setVisibility(View.VISIBLE);//��
    			zhifucount++;
    		}	
    		if(tb_inaccount.getZhifubaoer()==0)
    		{
    			ivbuszhiselzhier.setVisibility(View.GONE);//�ر�
    		}
    		else
    		{
    			ivbuszhiselzhier.setVisibility(View.VISIBLE);//��
    			zhifucount++;
    		}
    		if(tb_inaccount.getWeixing()==0)
    		{
    			ivbuszhiselweixing.setVisibility(View.GONE);//�ر�
    		}
    		else
    		{
    			ivbuszhiselweixing.setVisibility(View.VISIBLE);//��
    			zhifucount++;
    		}
    		if(tb_inaccount.getZhifubaofaca()==0)
    		{
    			ivbuszhiselpos.setVisibility(View.GONE);//�ر�
    		}
    		else
    		{
    			ivbuszhiselpos.setVisibility(View.VISIBLE);//��
    			zhifucount++;
    		}
    		switch(zhifucount)
    		{
    			case 3:
    			case 4:		
    				ivbuszhiselamount.setImageResource(R.drawable.amountnormalland);
    				ivbuszhiselzhier.setImageResource(R.drawable.zhiernormalland);
    				ivbuszhiselweixing.setImageResource(R.drawable.weixingnormalland);
    				ivbuszhiselpos.setImageResource(R.drawable.zhiposnormal);
    				break;
    			case 2:
    			case 1:	
    				ivbuszhiselamount.setImageResource(R.drawable.amountlargeland);
    				ivbuszhiselzhier.setImageResource(R.drawable.zhierlargeland);
    				ivbuszhiselweixing.setImageResource(R.drawable.weixinglargeland);
    				ivbuszhiselpos.setImageResource(R.drawable.zhiposlarge);
    				break;	
    		}   		    			
    	}		
    	imgbtnbusgoodsback=(ImageView)findViewById(R.id.imgbtnbusgoodsback);
    	imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});		
		//5������֮���˳�ҳ��
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {	
            	finish();
            }

		}, SPLASH_DISPLAY_LENGHT);
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
			case COMThread.EV_BUTTONMAIN: 
					SerializableMap serializableMap2 = (SerializableMap) bundle.get("result");
					Map<String, Integer> Set2=serializableMap2.getMap();
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusSelect ��������="+Set2,"com.txt");
					int EV_TYPE=Set2.get("EV_TYPE");
					//�ϱ���������
					if(EV_TYPE==COMThread.EV_BUTTONRPT_HUODAO)
					{
						if(ToolClass.checkCLIENT_STATUS_SERVICE())
						{
							//��ת��Ʒ
							
							cabID="1";
						    int huono=Set2.get("btnvalue");
						    huoID=(huono<=9)?("0"+huono):String.valueOf(huono);
						    vmc_columnDAO columnDAO = new vmc_columnDAO(context);// ����InaccountDAO����		    
						    Tb_vmc_product tb_inaccount = columnDAO.getColumnproduct(cabID,huoID);
						    if(tb_inaccount!=null)
						    {	
							    productID=tb_inaccount.getProductID().toString();
							    prosales=String.valueOf(tb_inaccount.getSalesPrice());
							    proImage=tb_inaccount.getAttBatch1();
							    proID=productID+"-"+tb_inaccount.getProductName().toString();
							    procount="1";
							    proType="2";
							    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��ƷproID="+proID+" productID="+productID+" proImage="
										+proImage+" prosales="+prosales+" procount="
										+procount+" proType="+proType+" cabID="+cabID+" huoID="+huoID,"log.txt");						    
	
							    /*ΪʲôͼƬһ��Ҫת��Ϊ Bitmap��ʽ�ģ��� */
						        Bitmap bitmap = ToolClass.getLoacalBitmap(proImage); //�ӱ���ȡͼƬ(��cdcard�л�ȡ)  //
						        ivbusgoodselProduct.setImageBitmap(bitmap);// ����ͼ��Ķ�����ֵ
								txtbusgoodselName = (TextView) findViewById(R.id.txtbusgoodselName);
								txtbusgoodselName.setText(proID);
								txtbusgoodselAmount = (TextView) findViewById(R.id.txtbusgoodselAmount);
								if(Integer.parseInt(procount)>0)
								{
									txtbusgoodselAmount.setText(prosales);
								}
								else
								{
									txtbusgoodselAmount.setText("������");
								}
								//�õ���Ʒ����
								if(ToolClass.getOrientation()==1)
								{
									vmc_productDAO productDAO = new vmc_productDAO(context);// ����InaccountDAO����
								    Tb_vmc_product tb_vmc_product = productDAO.find(productID);
								    if(ToolClass.isEmptynull(tb_vmc_product.getProductDesc())!=true)
								    {
								    	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��ƷDesc="+tb_vmc_product.getProductDesc().toString(),"log.txt");
									    WebSettings settings = webproductDesc.getSettings();
									    settings.setSupportZoom(true);
									    settings.setTextSize(WebSettings.TextSize.LARGEST);
									    webproductDesc.getSettings().setSupportMultipleWindows(true);
									    webproductDesc.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //���ù�������ʽ
									    webproductDesc.getSettings().setDefaultTextEncodingName("UTF -8");//����Ĭ��Ϊutf-8
									    webproductDesc.loadDataWithBaseURL(null,tb_vmc_product.getProductDesc().toString(), "text/html; charset=UTF-8","utf-8", null);//����д��������ȷ���Ľ���
								    }
								    else
								    {
								    	webproductDesc.setVisibility(View.GONE);
								    }
								}
						    }
						    else
						    {
						    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��ƷproID="+proID+" productID="
										+productID+" proType="
										+"2"+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
										+"1","log.txt");						    
							    // ������Ϣ��ʾ
							    ToolClass.failToast("��Ǹ������Ʒ�����꣡");					
						    }
						}
					}	
					break;
				
			}			
		}

	}
	
	private void sendzhifu()
	{
		OrderDetail.setProID(proID);
    	OrderDetail.setProductID(productID);
    	OrderDetail.setProType(proType);
    	OrderDetail.setShouldPay(Float.parseFloat(prosales));
    	OrderDetail.setShouldNo(1);
    	OrderDetail.setCabID(cabID);
    	OrderDetail.setColumnID(huoID);
    	//=============
  		//COM�������
  		//=============
  		//5.���ע�������
  		comBroadreceiver.unregisterReceiver(comreceiver);
	}
	
	//����BusHuo������Ϣ
  	@Override
  	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
  	{
  		//4.ע�������
		//comBroadreceiver = LocalBroadcastManager.getInstance(this);
		//comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
  	}
	
	@Override
	protected void onDestroy() {
		//=============
		//COM�������
		//=============
		//5.���ע�������
		comBroadreceiver.unregisterReceiver(comreceiver);
		super.onDestroy();	
	}
	
}
