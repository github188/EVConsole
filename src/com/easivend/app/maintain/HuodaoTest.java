/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           HuodaoTest.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        货道测试页面          
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.app.maintain;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.http.EVServerhttp;
import com.easivend.model.Tb_vmc_cabinet;
import com.easivend.view.COMService;
import com.easivend.common.HuoPictureAdapter;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_CabinetAdapter;
import com.easivend.common.Vmc_HuoAdapter;
import com.example.evconsole.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class HuodaoTest extends TabActivity 
{
	private TabHost mytabhost = null;
	private ProgressBar barhuomanager=null;
	private int[] layres=new int[]{R.id.tab_huodaomanager,R.id.tab_huodaotest,R.id.tab_huodaoset};//内嵌布局文件的id
	HuoPictureAdapter adapter=null;
	private TextView txthuosetrst=null;
	private int con=1;//查询连接次数
	private int ishuoquery=0;//是否正在查询1,正在查询,0查询完成
	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	private Button btnhuosetadd=null,btnhuosetdel=null,btnhuosetbu=null,btnhuosetclear=null,btnhuosetexit=null;
	private Spinner spinhuosetCab=null,spinhuotestCab=null,spinhuopeiCab=null;
	private String[] cabinetID=null;//用来分离出货柜编号
	private int[] cabinetType = null;//用来分离出货柜类型
	private int cabinetsetvar=0;//当前柜号
	private int cabinetTypepeivar=0;//当前柜类型
	private int devopt=0;//操作类型，出货，照明，制冷，加热	
	Map<String, Integer> huoSet= new LinkedHashMap<String,Integer>();
	private int huonum=0;//本柜货道数量
	private int huonno=0;//循环出货第几个格子了
	private boolean autohuonno=false;//true表示在进行循环出货
	private boolean chuhuopt=false;//true表示正在出货操作
	private int autohuonum=0;//循环出到第几次了
	//循环测试结果
	Map<String, Integer> huoalltest= new LinkedHashMap<String,Integer>();
	//循环测试详细结果
	Map<String,String> huoallinfo = new IdentityHashMap<String,String>();
	private boolean allinfo=false;//true表示查看详细故障信息,false表示查看统计信息
	// 定义货道列表
	Vmc_HuoAdapter huoAdapter=null;
	GridView gvhuodao=null;
	private final static int REQUEST_CODE=1;//声明请求标识
	
	private int device=0;//出货柜号		
	private int status=0;//出货结果
	private int hdid=0;//货道id
	private int cool=0;//是否支持制冷 	 	1:支持 0:不支持
	private int hot=0;//是否支持加热  		1:支持 0:不支持
	private int light=0;//是否支持照明  	1:支持 0:不支持
	private TextView txthuorst=null,txthuotestrst=null,txthuoallrst=null;
	private Button btnhuochu=null,btnhuochuall=null;// 创建Button对象“出货”
	private Button btnhuocancel=null;// 创建Button对象“重置”
	private Button btnhuoexit=null;// 创建Button对象“退出”
	private Button btnhuopre=null,btnhuonext=null;// 创建Button对象“上一道，下一道”
	private Button btnhuoall=null;//循环出货统计信息
	private EditText edtcolumn=null;
	private TextView txtlight=null,txtcold=null,txthot=null;
	private Switch switchlight = null,switcold = null,switchhot = null;	
	//货道配置页面
	private Switch btnhuosetc1=null,btnhuosetc2=null,btnhuosetc3=null,btnhuosetc4=null,
			btnhuosetc5=null,btnhuosetc6=null,btnhuosetc7=null,btnhuosetc8=null,
			btnhuoset11=null,btnhuoset12=null,btnhuoset13=null,btnhuoset14=null,btnhuoset15=null,
			btnhuoset16=null,btnhuoset17=null,btnhuoset18=null,btnhuoset19=null,btnhuoset110=null,
			btnhuoset21=null,btnhuoset22=null,btnhuoset23=null,btnhuoset24=null,btnhuoset25=null,
			btnhuoset26=null,btnhuoset27=null,btnhuoset28=null,btnhuoset29=null,btnhuoset210=null,
			btnhuoset31=null,btnhuoset32=null,btnhuoset33=null,btnhuoset34=null,btnhuoset35=null,
			btnhuoset36=null,btnhuoset37=null,btnhuoset38=null,btnhuoset39=null,btnhuoset310=null,
			btnhuoset41=null,btnhuoset42=null,btnhuoset43=null,btnhuoset44=null,btnhuoset45=null,
			btnhuoset46=null,btnhuoset47=null,btnhuoset48=null,btnhuoset49=null,btnhuoset410=null,
			btnhuoset51=null,btnhuoset52=null,btnhuoset53=null,btnhuoset54=null,btnhuoset55=null,
			btnhuoset56=null,btnhuoset57=null,btnhuoset58=null,btnhuoset59=null,btnhuoset510=null,
			btnhuoset61=null,btnhuoset62=null,btnhuoset63=null,btnhuoset64=null,btnhuoset65=null,
			btnhuoset66=null,btnhuoset67=null,btnhuoset68=null,btnhuoset69=null,btnhuoset610=null,
			btnhuoset71=null,btnhuoset72=null,btnhuoset73=null,btnhuoset74=null,btnhuoset75=null,
			btnhuoset76=null,btnhuoset77=null,btnhuoset78=null,btnhuoset79=null,btnhuoset710=null,
			btnhuoset81=null,btnhuoset82=null,btnhuoset83=null,btnhuoset84=null,btnhuoset85=null,
			btnhuoset86=null,btnhuoset87=null,btnhuoset88=null,btnhuoset89=null,btnhuoset810=null
			;
	private Button btnhuosetsethuo=null,btnhuosetautohuo=null,btnhuosetclose=null;							
	private int autophysic=57;		
	private boolean autochu=false;//true表示在进行循环自检配置
	//测试进度对话框
	ProgressDialog dialog= null;
	//COM服务相关
	LocalBroadcastManager comBroadreceiver;
	COMReceiver comreceiver;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.huodao);// 设置布局文件
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		this.mytabhost = super.getTabHost();//取得TabHost对象
        LayoutInflater.from(this).inflate(R.layout.huodao, this.mytabhost.getTabContentView(),true);
        //增加Tab的组件
        TabSpec myTabhuodaomana=this.mytabhost.newTabSpec("tab0");
        myTabhuodaomana.setIndicator("货道设置");
        myTabhuodaomana.setContent(this.layres[0]);
    	this.mytabhost.addTab(myTabhuodaomana); 
    	
    	TabSpec myTabhuodaotest=this.mytabhost.newTabSpec("tab1");
    	myTabhuodaotest.setIndicator("货道测试");
    	myTabhuodaotest.setContent(this.layres[1]);
    	this.mytabhost.addTab(myTabhuodaotest);
    	
    	TabSpec myTabhuodaoset=this.mytabhost.newTabSpec("tab2");
    	myTabhuodaoset.setIndicator("货道配置");
    	myTabhuodaoset.setContent(this.layres[2]);
    	this.mytabhost.addTab(myTabhuodaoset); 
    	
    	//4.注册接收器
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
    	
    	//===============
    	//货道设置页面
    	//===============
  	    timer.scheduleWithFixedDelay(task, 3, 3, TimeUnit.SECONDS);       // timeTask 
  	    txthuosetrst=(TextView)findViewById(R.id.txthuosetrst);
  	    barhuomanager= (ProgressBar) findViewById(R.id.barhuomanager);
  	    huoAdapter=new Vmc_HuoAdapter();
  	    this.gvhuodao=(GridView) findViewById(R.id.gvhuodao); 
    	spinhuosetCab= (Spinner) findViewById(R.id.spinhuosetCab); 
    	spinhuotestCab= (Spinner) findViewById(R.id.spinhuotestCab); 
    	spinhuopeiCab= (Spinner) findViewById(R.id.spinhuopeiCab); 
    	//显示柜信息
    	showabinet();
    	this.spinhuosetCab.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<选择柜="+cabinetID[arg2],"log.txt");
				//只有有柜号的时候，才请求加载柜内货道信息
				if(cabinetID!=null)
				{
					barhuomanager.setVisibility(View.VISIBLE); 
					cabinetsetvar=Integer.parseInt(cabinetID[arg2]);
					cabinetTypepeivar=cabinetType[arg2]; 
					spinhuotestCab.setSelection(arg2);
					spinhuopeiCab.setSelection(arg2);
					queryhuodao();					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
    	//修改或添加货道对应商品
    	gvhuodao.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub cabinetID[0],
				String huo[]=huoAdapter.getHuoID();
				String huoID = huo[arg2];// 记录收入信息               
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<货道ID="+cabinetsetvar+huoID,"log.txt");
				Intent intent = new Intent();
		    	intent.setClass(HuodaoTest.this, HuodaoSet.class);// 使用AddInaccount窗口初始化Intent
                intent.putExtra("huoID", huoID);
                intent.putExtra("cabID", String.valueOf(cabinetsetvar));
                intent.putExtra("huoStatus", "1");
		    	startActivityForResult(intent, REQUEST_CODE);// 打开AddInaccount	
			}// 为GridView设置项单击事件
    		
    	});
    	//添加柜
    	btnhuosetadd = (Button) findViewById(R.id.btnhuosetadd);
    	btnhuosetadd.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	cabinetAdd();
		    }
		});
    	//删除柜
    	btnhuosetdel = (Button) findViewById(R.id.btnhuosetdel);
    	btnhuosetdel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	cabinetDel();
		    }
		});
    	//本柜补货
    	btnhuosetbu = (Button) findViewById(R.id.btnhuosetbu);
    	btnhuosetbu.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	cabinetbuhuo();
		    }
		});
    	//清空本柜存货
    	btnhuosetclear = (Button) findViewById(R.id.btnhuosetclear);
    	btnhuosetclear.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	cabinetclearhuo();
		    }
		});
    	btnhuosetexit = (Button) findViewById(R.id.btnhuosetexit);
    	btnhuosetexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {		    	
		    	finishActivity();
		    }
		});    	
    	
    	//动态设置控件高度
    	//
    	DisplayMetrics  dm = new DisplayMetrics();  
        //取得窗口属性  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        //窗口的宽度  
        int screenWidth = dm.widthPixels;          
        //窗口高度  
        int screenHeight = dm.heightPixels;      
        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<屏幕"+screenWidth
				+"],["+screenHeight+"]","log.txt");	
	  //横屏
	  if(ToolClass.getOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
	  {
		  LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gvhuodao.getLayoutParams(); // 取控件mGrid当前的布局参数
		  linearParams.height =  screenHeight-252;// 当控件的高强制设成75象素
		  gvhuodao.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
	  }
	  //竖屏
	  else
	  {
		  LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gvhuodao.getLayoutParams(); // 取控件mGrid当前的布局参数
		  linearParams.height =  screenHeight-500;// 当控件的高强制设成75象素
		  gvhuodao.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
	  }
    	
  	   
    	//===============
    	//出货测试页面
    	//===============
		
    	//spinhuoCab= (Spinner) findViewById(R.id.spinhuoCab); 
		txthuorst=(TextView)findViewById(R.id.txthuorst);
		txthuoallrst=(TextView)findViewById(R.id.txthuoallrst);
		txthuotestrst=(TextView)findViewById(R.id.txthuotestrst);
		btnhuopre = (Button) findViewById(R.id.btnhuopre);
		btnhuonext = (Button) findViewById(R.id.btnhuonext);
		btnhuochu = (Button) findViewById(R.id.btnhuochu);
		btnhuochuall = (Button) findViewById(R.id.btnhuochuall);
		btnhuocancel = (Button) findViewById(R.id.btnhuocancel);
		btnhuoexit = (Button) findViewById(R.id.btnhuoexit);
		btnhuoall = (Button) findViewById(R.id.btnhuoall);
		edtcolumn = (EditText) findViewById(R.id.edtcolumn);
		txtlight = (TextView) findViewById(R.id.txtlight);		
		txtcold = (TextView) findViewById(R.id.txtcold);
		txthot = (TextView) findViewById(R.id.txthot);	
		switchlight = (Switch)findViewById(R.id.switchlight);
		switchlight.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					comsend(COMService.EV_LIGHTCHILD,1);
				}
				else 
				{
					comsend(COMService.EV_LIGHTCHILD,0);					
				}
			}  
			
			
		}); 
		
		switcold = (Switch)findViewById(R.id.switcold);
		switcold.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					comsend(COMService.EV_COOLCHILD,1);
				}
				else 
				{
					comsend(COMService.EV_COOLCHILD,0);					
				}
			}  
			
			
		});
		switchhot = (Switch)findViewById(R.id.switchhot);
		switchhot.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					comsend(COMService.EV_HOTCHILD,1);
				}
				else 
				{
					comsend(COMService.EV_HOTCHILD,0);					
				}
			}  
			
			
		});
		this.spinhuotestCab.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<选择柜="+cabinetID[arg2],"log.txt");
				//只有有柜号的时候，才请求加载柜内货道信息
				if(cabinetID!=null)
				{
					cabinetsetvar=Integer.parseInt(cabinetID[arg2]); 
					cabinetTypepeivar=cabinetType[arg2]; 
					spinhuosetCab.setSelection(arg2);
					spinhuopeiCab.setSelection(arg2);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnhuopre.setOnClickListener(new OnClickListener() {// 为出货按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	if(ToolClass.isEmptynull(edtcolumn.getText().toString())!=true)
		    	{
		    		int i=Integer.parseInt(edtcolumn.getText().toString());
		    		if(i>1)
		    			edtcolumn.setText(String.valueOf(i-1));
		    	}
		    }
		});
		btnhuonext.setOnClickListener(new OnClickListener() {// 为出货按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	if(ToolClass.isEmptynull(edtcolumn.getText().toString())!=true)
		    	{
		    		edtcolumn.setText(String.valueOf(Integer.parseInt(edtcolumn.getText().toString())+1));
			    }
		    }
		});
		
		btnhuochu.setOnClickListener(new OnClickListener() {// 为出货按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	if(ToolClass.isEmptynull(edtcolumn.getText().toString())!=true)
		    	{
		    		comsend(COMService.EV_CHUHUOCHILD,Integer.parseInt(edtcolumn.getText().toString()));
			    }
		    }
		});
		btnhuochuall.setOnClickListener(new OnClickListener() {// 为出货按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	//开始循环出货
		    	if(autohuonno==false)
		    	{
		    		autohuonno=true;
			    	btnhuochuall.setText("结束循环出货");
			    	autohuonum=1;
			    	huonno=1;
		    		huoalltest.clear();		
		    		huoallinfo.clear();
			    	comsend(COMService.EV_CHUHUOCHILD,huonno);
		    	}
		    	//结束循环出货
		    	else
		    	{
		    		autohuonno=false;
		    		btnhuochuall.setText("开始循环出货");
		    		autohuonum=0;
		    	}
		    }
		});
		btnhuocancel.setOnClickListener(new OnClickListener() {// 为重置按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	edtcolumn.setText("");// 设置金额文本框为空		    	      
		    }
		});
		btnhuoexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {		    	
		    	finishActivity();
		    }
		});
		btnhuoall.setOnClickListener(new OnClickListener() {// 为出货按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	if(ToolClass.isEmptynull(edtcolumn.getText().toString())!=true)
		    	{
		    		if(!allinfo)
		    		{
		    			allinfo=true;
		    			btnhuoall.setText("统计信息");
		    			txthuoallrst.setText(huoallinfo.toString());
		    		}
		    		else
		    		{
		    			allinfo=false;
		    			btnhuoall.setText("详细信息");
		    			String str="出货次数:["+autohuonum+"],故障信息"+huoalltest.toString();
		    			txthuoallrst.setText(str);
		    		}	
			    }
		    }
		});
		
		//===============
    	//货道配置页面
    	//===============		
		btnhuosetc1 = (Switch) findViewById(R.id.btnhuosetc1);
		btnhuosetc2 = (Switch) findViewById(R.id.btnhuosetc2);
		btnhuosetc3 = (Switch) findViewById(R.id.btnhuosetc3);
		btnhuosetc4 = (Switch) findViewById(R.id.btnhuosetc4);
		btnhuosetc5 = (Switch) findViewById(R.id.btnhuosetc5);
		btnhuosetc6 = (Switch) findViewById(R.id.btnhuosetc6);
		btnhuosetc7 = (Switch) findViewById(R.id.btnhuosetc7);
		btnhuosetc8 = (Switch) findViewById(R.id.btnhuosetc8);		
		btnhuoset11 = (Switch) findViewById(R.id.btnhuoset11);
		btnhuoset11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(1, isChecked);			
			} 
        });
		btnhuoset12 = (Switch) findViewById(R.id.btnhuoset12);
		btnhuoset12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(1, isChecked);			
			} 
        });
		btnhuoset13 = (Switch) findViewById(R.id.btnhuoset13);
		btnhuoset13.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(1, isChecked);			
			} 
        });
		btnhuoset14 = (Switch) findViewById(R.id.btnhuoset14);
		btnhuoset14.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(1, isChecked);			
			} 
        });
		btnhuoset15 = (Switch) findViewById(R.id.btnhuoset15);
		btnhuoset15.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(1, isChecked);			
			} 
        });
		btnhuoset16 = (Switch) findViewById(R.id.btnhuoset16);
		btnhuoset16.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(1, isChecked);			
			} 
        });
		btnhuoset17 = (Switch) findViewById(R.id.btnhuoset17);
		btnhuoset17.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(1, isChecked);			
			} 
        });
		btnhuoset18 = (Switch) findViewById(R.id.btnhuoset18);
		btnhuoset18.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(1, isChecked);			
			} 
        });
		btnhuoset19 = (Switch) findViewById(R.id.btnhuoset19);
		btnhuoset19.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(1, isChecked);			
			} 
        });
		btnhuoset110 = (Switch) findViewById(R.id.btnhuoset110);
		btnhuoset110.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(1, isChecked);			
			} 
        });
		btnhuoset21 = (Switch) findViewById(R.id.btnhuoset21);
		btnhuoset21.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(2, isChecked);			
			} 
        });
		btnhuoset22 = (Switch) findViewById(R.id.btnhuoset22);
		btnhuoset22.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(2, isChecked);			
			} 
        });
		btnhuoset23 = (Switch) findViewById(R.id.btnhuoset23);
		btnhuoset23.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(2, isChecked);			
			} 
        });
		btnhuoset24 = (Switch) findViewById(R.id.btnhuoset24);
		btnhuoset24.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(2, isChecked);			
			} 
        });
		btnhuoset25 = (Switch) findViewById(R.id.btnhuoset25);
		btnhuoset25.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(2, isChecked);			
			} 
        });
		btnhuoset26 = (Switch) findViewById(R.id.btnhuoset26);
		btnhuoset26.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(2, isChecked);			
			} 
        });
		btnhuoset27 = (Switch) findViewById(R.id.btnhuoset27);
		btnhuoset27.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(2, isChecked);			
			} 
        });
		btnhuoset28 = (Switch) findViewById(R.id.btnhuoset28);
		btnhuoset28.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(2, isChecked);			
			} 
        });
		btnhuoset29 = (Switch) findViewById(R.id.btnhuoset29);
		btnhuoset29.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(2, isChecked);			
			} 
        });
		btnhuoset210 = (Switch) findViewById(R.id.btnhuoset210);
		btnhuoset210.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(2, isChecked);			
			} 
        });
		btnhuoset31 = (Switch) findViewById(R.id.btnhuoset31);
		btnhuoset31.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(3, isChecked);			
			} 
        });
		btnhuoset32 = (Switch) findViewById(R.id.btnhuoset32);
		btnhuoset32.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(3, isChecked);			
			} 
        });
		btnhuoset33 = (Switch) findViewById(R.id.btnhuoset33);
		btnhuoset33.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(3, isChecked);			
			} 
        });
		btnhuoset34 = (Switch) findViewById(R.id.btnhuoset34);
		btnhuoset34.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(3, isChecked);			
			} 
        });
		btnhuoset35 = (Switch) findViewById(R.id.btnhuoset35);
		btnhuoset35.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(3, isChecked);			
			} 
        });
		btnhuoset36 = (Switch) findViewById(R.id.btnhuoset36);
		btnhuoset36.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(3, isChecked);			
			} 
        });
		btnhuoset37 = (Switch) findViewById(R.id.btnhuoset37);
		btnhuoset37.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(3, isChecked);			
			} 
        });
		btnhuoset38 = (Switch) findViewById(R.id.btnhuoset38);
		btnhuoset38.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(3, isChecked);			
			} 
        });
		btnhuoset39 = (Switch) findViewById(R.id.btnhuoset39);
		btnhuoset39.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(3, isChecked);			
			} 
        });
		btnhuoset310 = (Switch) findViewById(R.id.btnhuoset310);
		btnhuoset310.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(3, isChecked);			
			} 
        });
		btnhuoset41 = (Switch) findViewById(R.id.btnhuoset41);
		btnhuoset41.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(4, isChecked);			
			} 
        });
		btnhuoset42 = (Switch) findViewById(R.id.btnhuoset42);
		btnhuoset42.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(4, isChecked);			
			} 
        });
		btnhuoset43 = (Switch) findViewById(R.id.btnhuoset43);
		btnhuoset43.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(4, isChecked);			
			} 
        });
		btnhuoset44 = (Switch) findViewById(R.id.btnhuoset44);
		btnhuoset44.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(4, isChecked);			
			} 
        });
		btnhuoset45 = (Switch) findViewById(R.id.btnhuoset45);
		btnhuoset45.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(4, isChecked);			
			} 
        });
		btnhuoset46 = (Switch) findViewById(R.id.btnhuoset46);
		btnhuoset46.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(4, isChecked);			
			} 
        });
		btnhuoset47 = (Switch) findViewById(R.id.btnhuoset47);
		btnhuoset47.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(4, isChecked);			
			} 
        });
		btnhuoset48 = (Switch) findViewById(R.id.btnhuoset48);
		btnhuoset48.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(4, isChecked);			
			} 
        });
		btnhuoset49 = (Switch) findViewById(R.id.btnhuoset49);
		btnhuoset49.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(4, isChecked);			
			} 
        });
		btnhuoset410 = (Switch) findViewById(R.id.btnhuoset410);
		btnhuoset410.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(4, isChecked);			
			} 
        });
		btnhuoset51 = (Switch) findViewById(R.id.btnhuoset51);
		btnhuoset51.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(5, isChecked);			
			} 
        });
		btnhuoset52 = (Switch) findViewById(R.id.btnhuoset52);
		btnhuoset52.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(5, isChecked);			
			} 
        });
		btnhuoset53 = (Switch) findViewById(R.id.btnhuoset53);
		btnhuoset53.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(5, isChecked);			
			} 
        });
		btnhuoset54 = (Switch) findViewById(R.id.btnhuoset54);
		btnhuoset54.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(5, isChecked);			
			} 
        });
		btnhuoset55 = (Switch) findViewById(R.id.btnhuoset55);
		btnhuoset55.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(5, isChecked);			
			} 
        });
		btnhuoset56 = (Switch) findViewById(R.id.btnhuoset56);
		btnhuoset56.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(5, isChecked);			
			} 
        });
		btnhuoset57 = (Switch) findViewById(R.id.btnhuoset57);
		btnhuoset57.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(5, isChecked);			
			} 
        });
		btnhuoset58 = (Switch) findViewById(R.id.btnhuoset58);
		btnhuoset58.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(5, isChecked);			
			} 
        });
		btnhuoset59 = (Switch) findViewById(R.id.btnhuoset59);
		btnhuoset59.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(5, isChecked);			
			} 
        });
		btnhuoset510 = (Switch) findViewById(R.id.btnhuoset510);
		btnhuoset510.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(5, isChecked);			
			} 
        });
		btnhuoset61 = (Switch) findViewById(R.id.btnhuoset61);
		btnhuoset61.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(6, isChecked);			
			} 
        });
		btnhuoset62 = (Switch) findViewById(R.id.btnhuoset62);
		btnhuoset62.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(6, isChecked);			
			} 
        });
		btnhuoset63 = (Switch) findViewById(R.id.btnhuoset63);
		btnhuoset63.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(6, isChecked);			
			} 
        });
		btnhuoset64 = (Switch) findViewById(R.id.btnhuoset64);
		btnhuoset64.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(6, isChecked);			
			} 
        });
		btnhuoset65 = (Switch) findViewById(R.id.btnhuoset65);
		btnhuoset65.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(6, isChecked);			
			} 
        });
		btnhuoset66 = (Switch) findViewById(R.id.btnhuoset66);
		btnhuoset66.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(6, isChecked);			
			} 
        });
		btnhuoset67 = (Switch) findViewById(R.id.btnhuoset67);
		btnhuoset67.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(6, isChecked);			
			} 
        });
		btnhuoset68 = (Switch) findViewById(R.id.btnhuoset68);
		btnhuoset68.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(6, isChecked);			
			} 
        });
		btnhuoset69 = (Switch) findViewById(R.id.btnhuoset69);
		btnhuoset69.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(6, isChecked);			
			} 
        });
		btnhuoset610 = (Switch) findViewById(R.id.btnhuoset610);
		btnhuoset610.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(6, isChecked);			
			} 
        });
		btnhuoset71 = (Switch) findViewById(R.id.btnhuoset71);
		btnhuoset71.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(7, isChecked);			
			} 
        });
		btnhuoset72 = (Switch) findViewById(R.id.btnhuoset72);
		btnhuoset72.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(7, isChecked);			
			} 
        });
		btnhuoset73 = (Switch) findViewById(R.id.btnhuoset73);
		btnhuoset73.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(7, isChecked);			
			} 
        });
		btnhuoset74 = (Switch) findViewById(R.id.btnhuoset74);
		btnhuoset74.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(7, isChecked);			
			} 
        });
		btnhuoset75 = (Switch) findViewById(R.id.btnhuoset75);
		btnhuoset75.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(7, isChecked);			
			} 
        });
		btnhuoset76 = (Switch) findViewById(R.id.btnhuoset76);
		btnhuoset76.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(7, isChecked);			
			} 
        });
		btnhuoset77 = (Switch) findViewById(R.id.btnhuoset77);
		btnhuoset77.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(7, isChecked);			
			} 
        });
		btnhuoset78 = (Switch) findViewById(R.id.btnhuoset78);
		btnhuoset78.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(7, isChecked);			
			} 
        });
		btnhuoset79 = (Switch) findViewById(R.id.btnhuoset79);
		btnhuoset79.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(7, isChecked);			
			} 
        });
		btnhuoset710 = (Switch) findViewById(R.id.btnhuoset710);
		btnhuoset710.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(7, isChecked);			
			} 
        });
		btnhuoset81 = (Switch) findViewById(R.id.btnhuoset81);
		btnhuoset81.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(8, isChecked);			
			} 
        });
		btnhuoset82 = (Switch) findViewById(R.id.btnhuoset82);
		btnhuoset82.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(8, isChecked);			
			} 
        });
		btnhuoset83 = (Switch) findViewById(R.id.btnhuoset83);
		btnhuoset83.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(8, isChecked);			
			} 
        });
		btnhuoset84 = (Switch) findViewById(R.id.btnhuoset84);
		btnhuoset84.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(8, isChecked);			
			} 
        });
		btnhuoset85 = (Switch) findViewById(R.id.btnhuoset85);
		btnhuoset85.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(8, isChecked);			
			} 
        });
		btnhuoset86 = (Switch) findViewById(R.id.btnhuoset86);
		btnhuoset86.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(8, isChecked);			
			} 
        });
		btnhuoset87 = (Switch) findViewById(R.id.btnhuoset87);
		btnhuoset87.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(8, isChecked);			
			} 
        });
		btnhuoset88 = (Switch) findViewById(R.id.btnhuoset88);
		btnhuoset88.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(8, isChecked);			
			} 
        });
		btnhuoset89 = (Switch) findViewById(R.id.btnhuoset89);
		btnhuoset89.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(8, isChecked);			
			} 
        });
		btnhuoset810 = (Switch) findViewById(R.id.btnhuoset810);
		btnhuoset810.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				sethuoc(8, isChecked);			
			} 
        });
	
		
		btnhuosetc1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset11.setChecked(isChecked);
				btnhuoset12.setChecked(isChecked);
				btnhuoset13.setChecked(isChecked);
				btnhuoset14.setChecked(isChecked);
				btnhuoset15.setChecked(isChecked);
				btnhuoset16.setChecked(isChecked);
				btnhuoset17.setChecked(isChecked);
				btnhuoset18.setChecked(isChecked);
				btnhuoset19.setChecked(isChecked);
				btnhuoset110.setChecked(isChecked);							
			} 
        });
		btnhuosetc2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset21.setChecked(isChecked);
				btnhuoset22.setChecked(isChecked);
				btnhuoset23.setChecked(isChecked);
				btnhuoset24.setChecked(isChecked);
				btnhuoset25.setChecked(isChecked);
				btnhuoset26.setChecked(isChecked);
				btnhuoset27.setChecked(isChecked);
				btnhuoset28.setChecked(isChecked);
				btnhuoset29.setChecked(isChecked);
				btnhuoset210.setChecked(isChecked);
			} 
        });
		btnhuosetc3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset31.setChecked(isChecked);
				btnhuoset32.setChecked(isChecked);
				btnhuoset33.setChecked(isChecked);
				btnhuoset34.setChecked(isChecked);
				btnhuoset35.setChecked(isChecked);
				btnhuoset36.setChecked(isChecked);
				btnhuoset37.setChecked(isChecked);
				btnhuoset38.setChecked(isChecked);
				btnhuoset39.setChecked(isChecked);
				btnhuoset310.setChecked(isChecked);
			} 
        });
		btnhuosetc4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset41.setChecked(isChecked);
				btnhuoset42.setChecked(isChecked);
				btnhuoset43.setChecked(isChecked);
				btnhuoset44.setChecked(isChecked);
				btnhuoset45.setChecked(isChecked);
				btnhuoset46.setChecked(isChecked);
				btnhuoset47.setChecked(isChecked);
				btnhuoset48.setChecked(isChecked);
				btnhuoset49.setChecked(isChecked);
				btnhuoset410.setChecked(isChecked);
			} 
        });
		btnhuosetc5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset51.setChecked(isChecked);
				btnhuoset52.setChecked(isChecked);
				btnhuoset53.setChecked(isChecked);
				btnhuoset54.setChecked(isChecked);
				btnhuoset55.setChecked(isChecked);
				btnhuoset56.setChecked(isChecked);
				btnhuoset57.setChecked(isChecked);
				btnhuoset58.setChecked(isChecked);
				btnhuoset59.setChecked(isChecked);
				btnhuoset510.setChecked(isChecked);
			} 
        });
		btnhuosetc6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset61.setChecked(isChecked);
				btnhuoset62.setChecked(isChecked);
				btnhuoset63.setChecked(isChecked);
				btnhuoset64.setChecked(isChecked);
				btnhuoset65.setChecked(isChecked);
				btnhuoset66.setChecked(isChecked);
				btnhuoset67.setChecked(isChecked);
				btnhuoset68.setChecked(isChecked);
				btnhuoset69.setChecked(isChecked);
				btnhuoset610.setChecked(isChecked);
			} 
        });
		btnhuosetc7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset71.setChecked(isChecked);
				btnhuoset72.setChecked(isChecked);
				btnhuoset73.setChecked(isChecked);
				btnhuoset74.setChecked(isChecked);
				btnhuoset75.setChecked(isChecked);
				btnhuoset76.setChecked(isChecked);
				btnhuoset77.setChecked(isChecked);
				btnhuoset78.setChecked(isChecked);
				btnhuoset79.setChecked(isChecked);
				btnhuoset710.setChecked(isChecked);
			} 
        });
		btnhuosetc8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset81.setChecked(isChecked);
				btnhuoset82.setChecked(isChecked);
				btnhuoset83.setChecked(isChecked);
				btnhuoset84.setChecked(isChecked);
				btnhuoset85.setChecked(isChecked);
				btnhuoset86.setChecked(isChecked);
				btnhuoset87.setChecked(isChecked);
				btnhuoset88.setChecked(isChecked);
				btnhuoset89.setChecked(isChecked);
				btnhuoset810.setChecked(isChecked);
			} 
        });
		
		this.spinhuopeiCab.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<选择柜="+cabinetID[arg2],"log.txt");
				//只有有柜号的时候，才请求加载柜内货道信息
				if(cabinetID!=null)
				{
					cabinetsetvar=Integer.parseInt(cabinetID[arg2]); 
					cabinetTypepeivar=cabinetType[arg2]; 
					spinhuosetCab.setSelection(arg2);
					spinhuotestCab.setSelection(arg2);
					//弹簧货道
					if(cabinetTypepeivar==1)
					{
						gethuofile();
					}
					//升降机货道
					else if((cabinetTypepeivar==2)||(cabinetTypepeivar==3))
					{
						getelevatorfile();
					}
					else
					{
						clearhuofile();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		btnhuosetsethuo = (Button) findViewById(R.id.btnhuosetsethuo);
		btnhuosetsethuo.setOnClickListener(new OnClickListener() {
				    @Override
				    public void onClick(View arg0) {
				    	//创建警告对话框
				    	Dialog alert=new AlertDialog.Builder(HuodaoTest.this)
				    		.setTitle("对话框")//标题
				    		.setMessage("您确定要手动配置货道吗？")//表示对话框中得内容
				    		.setIcon(R.drawable.ic_launcher)//设置logo
				    		.setPositiveButton("配置", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
				    			{				
					    				@Override
					    				public void onClick(DialogInterface dialog, int which) 
					    				{
					    					// TODO Auto-generated method stub
					    					//弹簧货道
					    					if(cabinetTypepeivar==1)
					    					{
						    					sethuofile();	
										    	gethuofile();
					    					}
					    					//升降机货道
					    					else if((cabinetTypepeivar==2)||(cabinetTypepeivar==3))
					    					{
					    						setelevatorfile();
					    						getelevatorfile();
					    					}
					    				}
				    		      }
				    			)		    		        
						        .setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
						        	{			
										@Override
										public void onClick(DialogInterface dialog, int which) 
										{
											// TODO Auto-generated method stub				
										}
						        	}
						        )
						        .create();//创建一个对话框
						        alert.show();//显示对话框				    	
				    }
				});
		btnhuosetautohuo = (Button) findViewById(R.id.btnhuosetautohuo);
		btnhuosetautohuo.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
				    @Override
				    public void onClick(View arg0) {
				    	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<hello","log.txt");
				    	//弹簧货道
				    	if(cabinetTypepeivar==1)
				    	{
					    	//创建警告对话框
					    	Dialog alert=new AlertDialog.Builder(HuodaoTest.this)
					    		.setTitle("对话框")//标题
					    		.setMessage("您确定要自动配置货道吗？")//表示对话框中得内容
					    		.setIcon(R.drawable.ic_launcher)//设置logo
					    		.setPositiveButton("配置", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
					    			{				
						    				@Override
						    				public void onClick(DialogInterface dialog, int which) 
						    				{
						    					// TODO Auto-generated method stub	
						    					HuodaoTest.this.dialog= ProgressDialog.show(HuodaoTest.this,"自动配置货道","请稍候...");
										    	autochu=true;
										    	//弹簧货道
										    	if(cabinetTypepeivar==1)
										    	{
										    		autophysic=57;
										    	}
										    	//升降机货道
										    	else if((cabinetTypepeivar==2)||(cabinetTypepeivar==3))
										    	{
										    		autophysic=11;
										    	}
										    	comsend(COMService.EV_CHUHUOCHILD,autophysic);
						    				}
					    		      }
					    			)		    		        
							        .setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
							        	{			
											@Override
											public void onClick(DialogInterface dialog, int which) 
											{
												// TODO Auto-generated method stub				
											}
							        	}
							        )
							        .create();//创建一个对话框
							        alert.show();//显示对话框	
				    	}
				    	else
				    	{
				    		// 弹出信息提示
							ToolClass.failToast("本功能只适用于弹簧货道！");
				    	}
				    }
				});
		btnhuosetclose = (Button) findViewById(R.id.btnhuosetclose);
		btnhuosetclose.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
				    @Override
				    public void onClick(View arg0) {				    	
				    	finishActivity();
				    }
				});
	}
	//===============
	//串口发送和监听模块
	//===============	
	//发送模块type发送类型,opt参数
	private void comsend(int type,int opt)
	{
		Intent intent=new Intent();
		
		devopt=type;
		//7.发送指令广播给COMService	
		switch(type)
		{
			case COMService.EV_CHECKCHILD:	
				ToolClass.Log(ToolClass.INFO,"EV_JNI",
				    	"[APPsend>>]cabinet="+String.valueOf(cabinetsetvar)
				    	,"log.txt");
				intent.putExtra("EVWhat", COMService.EV_CHECKCHILD);	
				intent.putExtra("cabinet", cabinetsetvar);	
				intent.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent);
				break;
			case COMService.EV_CHUHUOCHILD:
				ToolClass.Log(ToolClass.INFO,"EV_JNI",
		    	"[APPsend>>]cabinet="+String.valueOf(cabinetsetvar)
		    	+" column="+opt		    	
		    	,"log.txt");
				chuhuopt=true;
				edtcolumn.setText(String.valueOf(opt));
				//4.发送指令广播给COMService
				if(autochu)
					intent.putExtra("EVWhat", COMService.EV_SETHUOCHILD);	
				else
					intent.putExtra("EVWhat", COMService.EV_CHUHUOCHILD);
				
				intent.putExtra("cabinet", cabinetsetvar);	
				intent.putExtra("column", opt);	
				intent.putExtra("cost", 0);
				intent.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent);
				break;
			case COMService.EV_LIGHTCHILD:
			case COMService.EV_COOLCHILD:
			case COMService.EV_HOTCHILD:
				ToolClass.Log(ToolClass.INFO,"EV_JNI",
		    	"[APPsend>>]cabinet="+String.valueOf(cabinetsetvar)
		    	+" opt="+opt		    	
		    	,"log.txt");
				//4.发送指令广播给COMService
				intent.putExtra("EVWhat", type);	
				intent.putExtra("cabinet", cabinetsetvar);	
				intent.putExtra("opt", opt);	
				intent.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent);
				break;
		}

	}
	//2.创建COMReceiver的接收器广播，用来接收服务器同步的内容
	public class COMReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			chuhuopt=false;
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			//货道查询
			case COMThread.EV_CHECKMAIN:
				SerializableMap serializableMap = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 货道查询="+Set,"com.txt");
				
				ishuoquery=0;
				String tempno=null;
				
				cool=(Integer)Set.get("cool");
				hot=(Integer)Set.get("hot");
				light=(Integer)Set.get("light");
				ToolClass.Log(ToolClass.INFO,"EV_COM","API<<货道cool:"+cool+",hot="+hot+",light="+light,"com.txt");
				if(light>0)
				{
					txtlight.setText("支持");
					switchlight.setEnabled(true);
					
				}
				else
				{
					txtlight.setText("不支持");
					switchlight.setEnabled(false);
				}
				if(cool>0)
				{
					txtcold.setText("支持");
					switcold.setEnabled(true);
				}
				else
				{
					txtcold.setText("不支持");
					switcold.setEnabled(false);
				}
				if(hot>0)
				{
					txthot.setText("支持");
					switchhot.setEnabled(true);
				}
				else
				{
					txthot.setText("不支持");
					switchhot.setEnabled(false);
				}

				huoSet.clear();
				huoalltest.clear();
				huoallinfo.clear();
				//输出内容
				Set<Entry<String, Integer>> allmap=Set.entrySet();  //实例化
				Iterator<Entry<String, Integer>> iter=allmap.iterator();
				while(iter.hasNext())
				{
					Entry<String, Integer> me=iter.next();
					if(
					   (me.getKey().equals("EV_TYPE")!=true)&&(me.getKey().equals("cool")!=true)
					   &&(me.getKey().equals("hot")!=true)&&(me.getKey().equals("light")!=true)
					)   
					{
						if(Integer.parseInt(me.getKey())<10)
							tempno="0"+me.getKey();
						else 
							tempno=me.getKey();
						
						huoSet.put(tempno, (Integer)me.getValue());
					}
				} 
				huonum=huoSet.size();
				ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<"+huonum+"货道状态:"+huoSet.toString(),"com.txt");	
				if(huonum==0)
            	{
            		ToolClass.failToast("本柜连接失败!");
            	}
            	showhuodao();//显示货道列表				
				break;
			//操作返回	
			case COMThread.EV_OPTMAIN: 
				SerializableMap serializableMap2 = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set2=serializableMap2.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 货道操作="+Set2,"com.txt");
				int EV_TYPE=Set2.get("EV_TYPE");
				if((EV_TYPE==EVprotocol.EV_BENTO_OPEN)||(EV_TYPE==EVprotocol.EV_COLUMN_OPEN))
				{
					//是出货操作
					if(devopt==COMService.EV_CHUHUOCHILD)
					{
						device=(Integer)Set2.get("addr");//出货柜号						
						hdid=(Integer)Set2.get("box");//货道id
						status=Set2.get("result");//出货结果
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<出货结果"+"device=["+device+"],hdid=["+hdid+"],status=["+status+"]","log.txt");	
								
						txthuorst.setText("device=["+device+"],hdid=["+hdid+"],status=["+status+"]");
						//循环继续做货道配置操作
						if(autochu)
						{
							//弹簧货道
							if(cabinetTypepeivar==1)
							{
								autohuofile(status);
							}
							//升降机货道
							else if((cabinetTypepeivar==2)||(cabinetTypepeivar==3))
							{
								autoelevatorfile(status);
							}
						}
						
						sethuorst(status);
						//循环继续做出货操作
						if(autohuonno)
						{
							setallhuorst();
							//延时
						    new Handler().postDelayed(new Runnable() 
							{
					            @Override
					            public void run() 
					            {            	
					            	//出货下一个货道
									if((huonno>0)&&(huonno<huonum))
									{							
										huonno++;
										comsend(COMService.EV_CHUHUOCHILD,huonno);
									}
									else if(huonno>=huonum)
									{							
										huonno=1;
										autohuonum++;
										comsend(COMService.EV_CHUHUOCHILD,huonno);
									}
					            }
	
							}, 500);						
						}
					}
				}
				break;
			}			
		}

	}
	//===============
	//货道设置页面
	//===============
		
	//调用倒计时定时器
	Runnable task = new Runnable() { 
        @Override 
        public void run() { 
            if(ishuoquery==1)
            {
            	queryhuodao();
            }
//                    //查询已经完成
//                    else if(ishuoquery==0)
//                    {
//                    	timer.cancel(); //取消掉定时器，就不会再继续查了
//                    }
        }             
    };
	private void queryhuodao()
	{
		txthuosetrst.setText("查询次数:"+(con++));		
		ishuoquery=1;		
		comsend(COMService.EV_CHECKCHILD,0);
	}
	//接收GoodsProSet返回信息
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==REQUEST_CODE)
		{
			if(resultCode==HuodaoTest.RESULT_OK)
			{
				barhuomanager.setVisibility(View.VISIBLE);  
				showhuodao();
			}			
		}
	}
	
	private void finishActivity()
	{
		if(chuhuopt)
		{
			// 弹出信息提示
			ToolClass.failToast("请在[本次出货完成]之后，再退出页面！");
		}
		else
		{
			finish();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onDestroy() {
		timer.shutdown();
		//=============
		//COM服务相关
		//=============
		//5.解除注册接收器
		comBroadreceiver.unregisterReceiver(comreceiver);		
    	//退出时，返回intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}
	
	//添加柜号
	private void cabinetAdd()
	{
		View myview=null;    
		String [] mStringArray; 
		ArrayAdapter<String> mAdapter ;
		
		// TODO Auto-generated method stub
		LayoutInflater factory = LayoutInflater.from(HuodaoTest.this);
		myview=factory.inflate(R.layout.selectcabinet, null);
		final EditText dialogcab=(EditText) myview.findViewById(R.id.dialogcab);
		final Spinner dialogspincabtype=(Spinner) myview.findViewById(R.id.dialogspincabtype);
		mStringArray=getResources().getStringArray(R.array.cabinet_Type);
  	    //使用自定义的ArrayAdapter
	  	mAdapter = new ArrayAdapter<String>(this,R.layout.viewspinner,mStringArray);
	  	dialogspincabtype.setAdapter(mAdapter);// 为ListView列表设置数据源
		
		Dialog dialog = new AlertDialog.Builder(HuodaoTest.this)
		.setTitle("设置")
		.setPositiveButton("保存", new DialogInterface.OnClickListener() 	
		{
				
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub	
				
				//Toast.makeText(HuodaoTest.this, "数值="+dialogspincabtype.getSelectedItemId(), Toast.LENGTH_LONG).show();
				String no = dialogcab.getText().toString();
		    	int type = (int)dialogspincabtype.getSelectedItemId()+1;
		    	if((ToolClass.isEmptynull(no)!=true)&&(type!=0))
		    	{
		    		addabinet(no,type);
		    	}
		    	else
		        {
		    		ToolClass.failToast("请输入货柜编号和类型！");	
		        }
			}
		})
		.setNegativeButton("取消",  new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
    	{			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// TODO Auto-generated method stub				
			}
    	})
		.setView(myview)//这里将对话框布局文件加入到对话框中
		.create();
		dialog.show();	
	}
	//往数据库添加柜类型表的值
	private void addabinet(String no,int type)
	{
		try 
		{
			// 创建InaccountDAO对象
			vmc_cabinetDAO cabinetDAO = new vmc_cabinetDAO(HuodaoTest.this);
            // 创建Tb_inaccount对象
        	Tb_vmc_cabinet tb_vmc_cabinet = new Tb_vmc_cabinet(no,type);
        	cabinetDAO.add(tb_vmc_cabinet);// 添加收入信息
        	showabinet();//显示柜信息
        	ToolClass.setExtraComType(HuodaoTest.this);	
        	ToolClass.addOptLog(HuodaoTest.this,0,"添加柜:"+no);
        	// 弹出信息提示
            Toast.makeText(HuodaoTest.this, "〖货柜〗数据添加成功！", Toast.LENGTH_SHORT).show();
            
		} catch (Exception e)
		{
			// TODO: handle exception
			ToolClass.failToast("货柜添加失败！");	
		}
	}
	//显示全部柜信息
	private void showabinet()
	{
		ArrayAdapter<String> arrayAdapter = null;// 创建ArrayAdapter对象 
		Vmc_CabinetAdapter vmc_cabAdapter=new Vmc_CabinetAdapter();
	    String[] strInfos = vmc_cabAdapter.showSpinInfo(HuodaoTest.this);	    
	    // 使用字符串数组初始化ArrayAdapter对象
	    arrayAdapter = new ArrayAdapter<String>(this,R.layout.viewspinner, strInfos);
	    spinhuosetCab.setAdapter(arrayAdapter);// 为spin列表设置数据源
	    spinhuotestCab.setAdapter(arrayAdapter);// 为spin列表设置数据源
	    spinhuopeiCab.setAdapter(arrayAdapter);// 为spin列表设置数据源
	    cabinetID=vmc_cabAdapter.getCabinetID();    
	    cabinetType=vmc_cabAdapter.getCabinetType(); 	    
	    //只有有柜号的时候，才请求加载柜内货道信息
		if(cabinetID.length>0)
		{
			cabinetsetvar=Integer.parseInt(cabinetID[0]); 
			cabinetTypepeivar=cabinetType[0]; 
		}
	}
	//导入本柜全部货道信息
	private void showhuodao()
	{		 
		VmcHuoThread vmcHuoThread=new VmcHuoThread();
		vmcHuoThread.execute(); 
	}
	//****************
	//异步线程，用于查询记录
	//****************
	private class VmcHuoThread extends AsyncTask<Void,Void,Vmc_HuoAdapter>
	{

		@Override
		protected Vmc_HuoAdapter doInBackground(Void... params) {
			// TODO Auto-generated method stub
			huoAdapter.showProInfo(HuodaoTest.this, "", huoSet,String.valueOf(cabinetsetvar));
			return huoAdapter;
		}

		@Override
		protected void onPostExecute(Vmc_HuoAdapter huoAdapter) {
			// TODO Auto-generated method stub
			adapter = new HuoPictureAdapter(String.valueOf(cabinetsetvar),huoAdapter.getHuoID(),huoAdapter.getHuoproID(),huoAdapter.getHuoRemain(),huoAdapter.getHuoname(), huoAdapter.getProImage(),HuodaoTest.this);// 创建pictureAdapter对象
			gvhuodao.setAdapter(adapter);// 为GridView设置数据源		 
			barhuomanager.setVisibility(View.GONE);
		}
				
	}
	//删除柜号以及柜内货道全部信息
	private void cabinetDel()
	{
		//创建警告对话框
    	Dialog alert=new AlertDialog.Builder(HuodaoTest.this)
    		.setTitle("对话框")//标题
    		.setMessage("您确定要删除该柜吗？")//表示对话框中得内容
    		.setIcon(R.drawable.ic_launcher)//设置logo
    		.setPositiveButton("删除", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
    			{				
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) 
	    				{
	    					// TODO Auto-generated method stub	
	    					// 创建InaccountDAO对象
	    					vmc_columnDAO columnDAO = new vmc_columnDAO(HuodaoTest.this);
				            columnDAO.deteleCab(String.valueOf(cabinetsetvar));// 删除该柜货道信息
				            
				            vmc_cabinetDAO cabinetDAO = new vmc_cabinetDAO(HuodaoTest.this);
				            cabinetDAO.detele(String.valueOf(cabinetsetvar));// 删除该柜信息
				            ToolClass.setExtraComType(HuodaoTest.this);	
				            ToolClass.addOptLog(HuodaoTest.this,2,"删除柜:"+cabinetsetvar);				            
	    					// 弹出信息提示
				            Toast.makeText(HuodaoTest.this, "柜删除成功！", Toast.LENGTH_SHORT).show();						            
				            finish();
	    				}
    		      }
    			)		    		        
		        .setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
		        	{			
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							// TODO Auto-generated method stub				
						}
		        	}
		        )
		        .create();//创建一个对话框
		        alert.show();//显示对话框
	}
	
	//补满本柜货道
	private void cabinetbuhuo()
	{
		//创建警告对话框
    	Dialog alert=new AlertDialog.Builder(HuodaoTest.this)
    		.setTitle("对话框")//标题
    		.setMessage("您确定要补满本柜货道吗？")//表示对话框中得内容
    		.setIcon(R.drawable.ic_launcher)//设置logo
    		.setPositiveButton("补货", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
    			{				
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) 
	    				{
	    					// TODO Auto-generated method stub	
	    					barhuomanager.setVisibility(View.VISIBLE);
	    					// 创建InaccountDAO对象
	    					vmc_columnDAO columnDAO = new vmc_columnDAO(HuodaoTest.this);
				            columnDAO.buhuoCab(String.valueOf(cabinetsetvar));
				            showhuodao();
				            //=============
			    			//Server服务相关
			    			//=============
			    			//7.发送指令广播给EVServerService
			    			Intent intent2=new Intent();
		    				intent2.putExtra("EVWhat", EVServerhttp.SETHUODAOSTATUCHILD);
		    				intent2.setAction("android.intent.action.vmserversend");//action与接收器相同
		    				LocalBroadcastManager localBroadreceiver = LocalBroadcastManager.getInstance(HuodaoTest.this);
		    				localBroadreceiver.sendBroadcast(intent2);
	    					//=========
		    				//COM串口相关
		    				//=========
		    				ToolClass.Log(ToolClass.INFO,"EV_JNI",
    				    	"[APPsend>>]全部补货cabinet="+String.valueOf(cabinetsetvar)
    				    	,"log.txt");
    						//4.发送指令广播给COMService
    						Intent intent=new Intent();
    						intent.putExtra("EVWhat", COMThread.VBOX_HUODAO_SET_INDALLCHILD);
    						intent.putExtra("cabinet", cabinetsetvar);	
    						intent.setAction("android.intent.action.comsend");//action与接收器相同
    						comBroadreceiver.sendBroadcast(intent);
		    				// 弹出信息提示
				            Toast.makeText(HuodaoTest.this, "补货成功！", Toast.LENGTH_SHORT).show();	
	    				}
    		      }
    			)		    		        
		        .setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
		        	{			
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							// TODO Auto-generated method stub				
						}
		        	}
		        )
		        .create();//创建一个对话框
		        alert.show();//显示对话框
	}
	
	//清空本柜存货
	private void cabinetclearhuo()
	{
		//创建警告对话框
    	Dialog alert=new AlertDialog.Builder(HuodaoTest.this)
    		.setTitle("对话框")//标题
    		.setMessage("您确定要清空本柜存货吗？")//表示对话框中得内容
    		.setIcon(R.drawable.ic_launcher)//设置logo
    		.setPositiveButton("清空", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
    			{				
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) 
	    				{
	    					// TODO Auto-generated method stub	
	    					barhuomanager.setVisibility(View.VISIBLE);
	    					// 创建InaccountDAO对象
	    					vmc_columnDAO columnDAO = new vmc_columnDAO(HuodaoTest.this);
				            columnDAO.clearhuoCab(String.valueOf(cabinetsetvar));
				            showhuodao();
				            //=============
			    			//Server服务相关
			    			//=============
			    			//7.发送指令广播给EVServerService
			    			Intent intent2=new Intent();
		    				intent2.putExtra("EVWhat", EVServerhttp.SETHUODAOSTATUCHILD);
		    				intent2.setAction("android.intent.action.vmserversend");//action与接收器相同
		    				LocalBroadcastManager localBroadreceiver = LocalBroadcastManager.getInstance(HuodaoTest.this);
		    				localBroadreceiver.sendBroadcast(intent2);
	    					//=========
		    				//COM串口相关
		    				//=========
		    				ToolClass.Log(ToolClass.INFO,"EV_JNI",
    				    	"[APPsend>>]全部补货cabinet="+String.valueOf(cabinetsetvar)
    				    	,"log.txt");
    						//4.发送指令广播给COMService
    						Intent intent=new Intent();
    						intent.putExtra("EVWhat", COMThread.VBOX_HUODAO_SET_INDALLCHILD);
    						intent.putExtra("cabinet", cabinetsetvar);	
    						intent.setAction("android.intent.action.comsend");//action与接收器相同
    						comBroadreceiver.sendBroadcast(intent);
		    				// 弹出信息提示
				            Toast.makeText(HuodaoTest.this, "清空成功！", Toast.LENGTH_SHORT).show();	
	    				}
    		      }
    			)		    		        
		        .setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
		        	{			
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							// TODO Auto-generated method stub				
						}
		        	}
		        )
		        .create();//创建一个对话框
		        alert.show();//显示对话框
	}
	
	//===============
	//货道测试页面
	//===============
	private void sethuorst(int status)
	{
		boolean result=false;//true出货成功,false出货失败
		StringBuilder str=new StringBuilder();
		str.append("货道");
		str.append(edtcolumn.getText().toString());
		String tempno="";
		if(Integer.parseInt(edtcolumn.getText().toString())<10)
			tempno="0"+edtcolumn.getText().toString();
		else 
			tempno=edtcolumn.getText().toString();
		
		//******************************************
		//所有货道返回值，只有1代表成功，4代表已售完，其他值都是失败
		//******************************************
		
		//弹簧货道或者格子柜或者冰山柜
		if((cabinetTypepeivar==1)||(cabinetTypepeivar==5)||(cabinetTypepeivar==4))
		{
			switch(status)
			{
				case 1:
					str.append("出货成功");
					result=true;
					break;
				case 0:
					str.append("出货失败");
					break;	
				case 2:
					str.append("货道不存在");
					break;
				case 3:
					str.append("电机未到位");
					break;	
				case 4:
					str.append("货物已售完");
					result=true;
					break;
				case 5:
					str.append("通信故障");
					break;	
			}
		}
		//升降机货道
		else if((cabinetTypepeivar==2)||(cabinetTypepeivar==3))
		{
			switch(status)
			{
				case 1:
					str.append("出货成功");
					result=true;
					break;
				case 4:
					str.append("货物已售完");
					result=true;
					break;
				case 8:
					str.append("卡货");
					break;
				case 0x1F:
					str.append("通信故障");
					break;
				case 0x12:
					str.append("升降机故障");
					break;
				case 0x11:
					str.append("升降机忙");
					break;
				case 0:
					str.append("出货失败 通信失败");
					break;
				case 0x2:
					str.append("数据错误");
					break;
				case 5:
					str.append("取货门未开启");
					break;
				case 6:
					str.append("货物未取走");
					break;	
				case 0x7:
					str.append("其他故障");
					break;
				case 0x88:
					str.append("正在出货");
					break;	
			}
		}	
		txthuotestrst.setText(str);
		//打印出货结果
		if(result)
		{
			txthuotestrst.setTextColor(android.graphics.Color.BLACK);
		}
		else
		{
			txthuotestrst.setTextColor(android.graphics.Color.RED);
			if(huoalltest.containsKey(tempno))
			{
				int value=huoalltest.get(tempno);
				value++;
				huoalltest.put(tempno, value);
			}
			else
			{
				huoalltest.put(tempno, 1);
			}
			huoallinfo.put(tempno, str.toString());
		}
		ToolClass.Log(ToolClass.INFO,"EV_JNI","huoalltest="+huoalltest.toString(),"log.txt");	
	}
	//循环出货返回结果
	private void setallhuorst()
	{
		String str="出货次数:["+autohuonum+"],故障信息"+huoalltest.toString();
		txthuoallrst.setText(str);
	}
	
	//===============
	//货道配置页面
	//===============
	//货道使能禁能操作
	private void sethuoc(int ceng,boolean isChecked)
	{
		switch(ceng)
		{
			case 1:
				if(isChecked)
				{
					
				}
				else
				{
					if(
						(btnhuoset11.isChecked()==false)&&
						(btnhuoset12.isChecked()==false)&&
						(btnhuoset13.isChecked()==false)&&
						(btnhuoset14.isChecked()==false)&&
						(btnhuoset15.isChecked()==false)&&
						(btnhuoset16.isChecked()==false)&&
						(btnhuoset17.isChecked()==false)&&
						(btnhuoset18.isChecked()==false)&&
						(btnhuoset19.isChecked()==false)&&
						(btnhuoset110.isChecked()==false)
					  )
					{
						btnhuosetc1.setChecked(isChecked);
					}	
				}
				break;
			case 2:
				if(isChecked)
				{
					
				}
				else
				{
					if(
						(btnhuoset21.isChecked()==false)&&
						(btnhuoset22.isChecked()==false)&&
						(btnhuoset23.isChecked()==false)&&
						(btnhuoset24.isChecked()==false)&&
						(btnhuoset25.isChecked()==false)&&
						(btnhuoset26.isChecked()==false)&&
						(btnhuoset27.isChecked()==false)&&
						(btnhuoset28.isChecked()==false)&&
						(btnhuoset29.isChecked()==false)&&
						(btnhuoset210.isChecked()==false)
					  )
					{
						btnhuosetc2.setChecked(isChecked);
					}	
				}
				break;
			case 3:
				if(isChecked)
				{
					
				}
				else
				{
					if(
						(btnhuoset31.isChecked()==false)&&
						(btnhuoset32.isChecked()==false)&&
						(btnhuoset33.isChecked()==false)&&
						(btnhuoset34.isChecked()==false)&&
						(btnhuoset35.isChecked()==false)&&
						(btnhuoset36.isChecked()==false)&&
						(btnhuoset37.isChecked()==false)&&
						(btnhuoset38.isChecked()==false)&&
						(btnhuoset39.isChecked()==false)&&
						(btnhuoset310.isChecked()==false)
					  )
					{
						btnhuosetc3.setChecked(isChecked);
					}	
				}
				break;
			case 4:
				if(isChecked)
				{
					
				}
				else
				{
					if(
						(btnhuoset41.isChecked()==false)&&
						(btnhuoset42.isChecked()==false)&&
						(btnhuoset43.isChecked()==false)&&
						(btnhuoset44.isChecked()==false)&&
						(btnhuoset45.isChecked()==false)&&
						(btnhuoset46.isChecked()==false)&&
						(btnhuoset47.isChecked()==false)&&
						(btnhuoset48.isChecked()==false)&&
						(btnhuoset49.isChecked()==false)&&
						(btnhuoset410.isChecked()==false)
					  )
					{
						btnhuosetc4.setChecked(isChecked);
					}	
				}
				break;
			case 5:
				if(isChecked)
				{
					
				}
				else
				{
					if(
						(btnhuoset51.isChecked()==false)&&
						(btnhuoset52.isChecked()==false)&&
						(btnhuoset53.isChecked()==false)&&
						(btnhuoset54.isChecked()==false)&&
						(btnhuoset55.isChecked()==false)&&
						(btnhuoset56.isChecked()==false)&&
						(btnhuoset57.isChecked()==false)&&
						(btnhuoset58.isChecked()==false)&&
						(btnhuoset59.isChecked()==false)&&
						(btnhuoset510.isChecked()==false)
					  )
					{
						btnhuosetc5.setChecked(isChecked);
					}	
				}
				break;
			case 6:
				if(isChecked)
				{
					
				}
				else
				{
					if(
						(btnhuoset61.isChecked()==false)&&
						(btnhuoset62.isChecked()==false)&&
						(btnhuoset63.isChecked()==false)&&
						(btnhuoset64.isChecked()==false)&&
						(btnhuoset65.isChecked()==false)&&
						(btnhuoset66.isChecked()==false)&&
						(btnhuoset67.isChecked()==false)&&
						(btnhuoset68.isChecked()==false)&&
						(btnhuoset69.isChecked()==false)&&
						(btnhuoset610.isChecked()==false)
					  )
					{
						btnhuosetc6.setChecked(isChecked);
					}	
				}
				break;
			case 7:
				if(isChecked)
				{
					
				}
				else
				{
					if(
						(btnhuoset71.isChecked()==false)&&
						(btnhuoset72.isChecked()==false)&&
						(btnhuoset73.isChecked()==false)&&
						(btnhuoset74.isChecked()==false)&&
						(btnhuoset75.isChecked()==false)&&
						(btnhuoset76.isChecked()==false)&&
						(btnhuoset77.isChecked()==false)&&
						(btnhuoset78.isChecked()==false)&&
						(btnhuoset79.isChecked()==false)&&
						(btnhuoset710.isChecked()==false)
					  )
					{
						btnhuosetc7.setChecked(isChecked);
					}	
				}
				break;
			case 8:
				if(isChecked)
				{
					
				}
				else
				{
					if(
						(btnhuoset81.isChecked()==false)&&
						(btnhuoset82.isChecked()==false)&&
						(btnhuoset83.isChecked()==false)&&
						(btnhuoset84.isChecked()==false)&&
						(btnhuoset85.isChecked()==false)&&
						(btnhuoset86.isChecked()==false)&&
						(btnhuoset87.isChecked()==false)&&
						(btnhuoset88.isChecked()==false)&&
						(btnhuoset89.isChecked()==false)&&
						(btnhuoset810.isChecked()==false)
					  )
					{
						btnhuosetc8.setChecked(isChecked);
					}	
				}
				break;	
		}
	}
	//自检逻辑货道开关性能,result==1,或者4货道存在，其他值货道不存在
	private void autohuofile(int result)
	{
		switch(autophysic)
		{
			//第一层
			case 57:
				if((result==1)||(result==4))
				{
					btnhuoset11.setChecked(true);
				}
				else 
				{
					btnhuoset11.setChecked(false);	
				}
				autophysic++;				
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 58:
				if((result==1)||(result==4))
				{
					btnhuoset12.setChecked(true);
				}
				else 
				{
					btnhuoset12.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 59:
				if((result==1)||(result==4))
				{
					btnhuoset13.setChecked(true);
				}
				else 
				{
					btnhuoset13.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 60:
				if((result==1)||(result==4))
				{
					btnhuoset14.setChecked(true);
				}
				else 
				{
					btnhuoset14.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 61:
				if((result==1)||(result==4))
				{
					btnhuoset15.setChecked(true);
				}
				else 
				{
					btnhuoset15.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 62:
				if((result==1)||(result==4))
				{
					btnhuoset16.setChecked(true);
				}
				else 
				{
					btnhuoset16.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 63:
				if((result==1)||(result==4))
				{
					btnhuoset17.setChecked(true);
				}
				else 
				{
					btnhuoset17.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 64:
				if((result==1)||(result==4))
				{
					btnhuoset18.setChecked(true);
				}
				else 
				{
					btnhuoset18.setChecked(false);	
				}
				autophysic=49;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;	
			//第二层	
			case 49:
				if((result==1)||(result==4))
				{
					btnhuoset21.setChecked(true);
				}
				else 
				{
					btnhuoset21.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 50:
				if((result==1)||(result==4))
				{
					btnhuoset22.setChecked(true);
				}
				else 
				{
					btnhuoset22.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 51:
				if((result==1)||(result==4))
				{
					btnhuoset23.setChecked(true);
				}
				else 
				{
					btnhuoset23.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 52:
				if((result==1)||(result==4))
				{
					btnhuoset24.setChecked(true);
				}
				else 
				{
					btnhuoset24.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 53:
				if((result==1)||(result==4))
				{
					btnhuoset25.setChecked(true);
				}
				else 
				{
					btnhuoset25.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 54:
				if((result==1)||(result==4))
				{
					btnhuoset26.setChecked(true);
				}
				else 
				{
					btnhuoset26.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 55:
				if((result==1)||(result==4))
				{
					btnhuoset27.setChecked(true);
				}
				else 
				{
					btnhuoset27.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 56:
				if((result==1)||(result==4))
				{
					btnhuoset28.setChecked(true);
				}
				else 
				{
					btnhuoset28.setChecked(false);	
				}
				autophysic=41;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			//第三层	
			case 41:
				if((result==1)||(result==4))
				{
					btnhuoset31.setChecked(true);
				}
				else 
				{
					btnhuoset31.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 42:
				if((result==1)||(result==4))
				{
					btnhuoset32.setChecked(true);
				}
				else 
				{
					btnhuoset32.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 43:
				if((result==1)||(result==4))
				{
					btnhuoset33.setChecked(true);
				}
				else 
				{
					btnhuoset33.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 44:
				if((result==1)||(result==4))
				{
					btnhuoset34.setChecked(true);
				}
				else 
				{
					btnhuoset34.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 45:
				if((result==1)||(result==4))
				{
					btnhuoset35.setChecked(true);
				}
				else 
				{
					btnhuoset35.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 46:
				if((result==1)||(result==4))
				{
					btnhuoset36.setChecked(true);
				}
				else 
				{
					btnhuoset36.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 47:
				if((result==1)||(result==4))
				{
					btnhuoset37.setChecked(true);
				}
				else 
				{
					btnhuoset37.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 48:
				if((result==1)||(result==4))
				{
					btnhuoset38.setChecked(true);
				}
				else 
				{
					btnhuoset38.setChecked(false);	
				}
				autophysic=33;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;	
			//第四层	
			case 33:
				if((result==1)||(result==4))
				{
					btnhuoset41.setChecked(true);
				}
				else 
				{
					btnhuoset41.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 34:
				if((result==1)||(result==4))
				{
					btnhuoset42.setChecked(true);
				}
				else 
				{
					btnhuoset42.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 35:
				if((result==1)||(result==4))
				{
					btnhuoset43.setChecked(true);
				}
				else 
				{
					btnhuoset43.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 36:
				if((result==1)||(result==4))
				{
					btnhuoset44.setChecked(true);
				}
				else 
				{
					btnhuoset44.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 37:
				if((result==1)||(result==4))
				{
					btnhuoset45.setChecked(true);
				}
				else 
				{
					btnhuoset45.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 38:
				if((result==1)||(result==4))
				{
					btnhuoset46.setChecked(true);
				}
				else 
				{
					btnhuoset46.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 39:
				if((result==1)||(result==4))
				{
					btnhuoset47.setChecked(true);
				}
				else 
				{
					btnhuoset47.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 40:
				if((result==1)||(result==4))
				{
					btnhuoset48.setChecked(true);
				}
				else 
				{
					btnhuoset48.setChecked(false);	
				}
				autophysic=25;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			//第五层	
			case 25:
				if((result==1)||(result==4))
				{
					btnhuoset51.setChecked(true);
				}
				else 
				{
					btnhuoset51.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 26:
				if((result==1)||(result==4))
				{
					btnhuoset52.setChecked(true);
				}
				else 
				{
					btnhuoset52.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 27:
				if((result==1)||(result==4))
				{
					btnhuoset53.setChecked(true);
				}
				else 
				{
					btnhuoset53.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 28:
				if((result==1)||(result==4))
				{
					btnhuoset54.setChecked(true);
				}
				else 
				{
					btnhuoset54.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 29:
				if((result==1)||(result==4))
				{
					btnhuoset55.setChecked(true);
				}
				else 
				{
					btnhuoset55.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 30:
				if((result==1)||(result==4))
				{
					btnhuoset56.setChecked(true);
				}
				else 
				{
					btnhuoset56.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 31:
				if((result==1)||(result==4))
				{
					btnhuoset57.setChecked(true);
				}
				else 
				{
					btnhuoset57.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 32:
				if((result==1)||(result==4))
				{
					btnhuoset58.setChecked(true);
				}
				else 
				{
					btnhuoset58.setChecked(false);	
				}
				autophysic=17;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;	
			//第六层	
			case 17:
				if((result==1)||(result==4))
				{
					btnhuoset61.setChecked(true);
				}
				else 
				{
					btnhuoset61.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 18:
				if((result==1)||(result==4))
				{
					btnhuoset62.setChecked(true);
				}
				else 
				{
					btnhuoset62.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 19:
				if((result==1)||(result==4))
				{
					btnhuoset63.setChecked(true);
				}
				else 
				{
					btnhuoset63.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 20:
				if((result==1)||(result==4))
				{
					btnhuoset64.setChecked(true);
				}
				else 
				{
					btnhuoset64.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 21:
				if((result==1)||(result==4))
				{
					btnhuoset65.setChecked(true);
				}
				else 
				{
					btnhuoset65.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 22:
				if((result==1)||(result==4))
				{
					btnhuoset66.setChecked(true);
				}
				else 
				{
					btnhuoset66.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 23:
				if((result==1)||(result==4))
				{
					btnhuoset67.setChecked(true);
				}
				else 
				{
					btnhuoset67.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 24:
				if((result==1)||(result==4))
				{
					btnhuoset68.setChecked(true);
				}
				else 
				{
					btnhuoset68.setChecked(false);	
				}
				autophysic=9;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;	
			//第七层
			case 9:
				if((result==1)||(result==4))
				{
					btnhuoset71.setChecked(true);
				}
				else 
				{
					btnhuoset71.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 10:
				if((result==1)||(result==4))
				{
					btnhuoset72.setChecked(true);
				}
				else 
				{
					btnhuoset72.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 11:
				if((result==1)||(result==4))
				{
					btnhuoset73.setChecked(true);
				}
				else 
				{
					btnhuoset73.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 12:
				if((result==1)||(result==4))
				{
					btnhuoset74.setChecked(true);
				}
				else 
				{
					btnhuoset74.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 13:
				if((result==1)||(result==4))
				{
					btnhuoset75.setChecked(true);
				}
				else 
				{
					btnhuoset75.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 14:
				if((result==1)||(result==4))
				{
					btnhuoset76.setChecked(true);
				}
				else 
				{
					btnhuoset76.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 15:
				if((result==1)||(result==4))
				{
					btnhuoset77.setChecked(true);
				}
				else 
				{
					btnhuoset77.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 16:
				if((result==1)||(result==4))
				{
					btnhuoset78.setChecked(true);
				}
				else 
				{
					btnhuoset78.setChecked(false);	
				}
				autophysic=1;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			//第八层
			case 1:
				if((result==1)||(result==4))
				{
					btnhuoset81.setChecked(true);
				}
				else 
				{
					btnhuoset81.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 2:
				if((result==1)||(result==4))
				{
					btnhuoset82.setChecked(true);
				}
				else 
				{
					btnhuoset82.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 3:
				if((result==1)||(result==4))
				{
					btnhuoset83.setChecked(true);
				}
				else 
				{
					btnhuoset83.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 4:
				if((result==1)||(result==4))
				{
					btnhuoset84.setChecked(true);
				}
				else 
				{
					btnhuoset84.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 5:
				if((result==1)||(result==4))
				{
					btnhuoset85.setChecked(true);
				}
				else 
				{
					btnhuoset85.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 6:
				if((result==1)||(result==4))
				{
					btnhuoset86.setChecked(true);
				}
				else 
				{
					btnhuoset86.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 7:
				if((result==1)||(result==4))
				{
					btnhuoset87.setChecked(true);
				}
				else 
				{
					btnhuoset87.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 8:
				if((result==1)||(result==4))
				{
					btnhuoset88.setChecked(true);
				}
				else 
				{
					btnhuoset88.setChecked(false);	
				}
				dialog.dismiss();
				autochu=false;
				sethuofile();
				gethuofile();
				break;	
		}
	}
	
	//保存逻辑货道实际对应物理货道的文件
	private void sethuofile()
	{
		int logic=1,physic;
		JSONObject allSet=new JSONObject();
		try {
			//第一层
			physic=57;
			if(btnhuoset11.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset12.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset13.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset14.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset15.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset16.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset17.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset18.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第二层
			physic=49;
			if(btnhuoset21.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset22.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset23.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset24.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset25.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset26.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset27.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset28.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第三层
			physic=41;
			if(btnhuoset31.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset32.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset33.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset34.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset35.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset36.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset37.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset38.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第四层
			physic=33;
			if(btnhuoset41.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset42.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset43.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset44.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset45.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset46.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset47.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset48.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第五层
			physic=25;
			if(btnhuoset51.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset52.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset53.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset54.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset55.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset56.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset57.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset58.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第六层
			physic=17;
			if(btnhuoset61.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset62.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset63.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset64.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset65.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset66.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset67.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset68.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第七层
			physic=9;
			if(btnhuoset71.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset72.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset73.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset74.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset75.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset76.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset77.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset78.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第八层
			physic=1;
			if(btnhuoset81.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset82.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset83.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset84.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset85.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset86.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset87.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset88.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(cabinetsetvar==2)
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+allSet.length()+"副柜货道状态:"+allSet.toString(),"log.txt");	
			ToolClass.WriteColumnFile2(allSet.toString());
			ToolClass.addOptLog(HuodaoTest.this,1,"修改副柜物理货道对应表");
	        // 弹出信息提示
	        Toast.makeText(HuodaoTest.this, "〖修改副柜物理货道对应表〗成功！", Toast.LENGTH_SHORT).show();
		}
		else
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+allSet.length()+"货道状态:"+allSet.toString(),"log.txt");	
			ToolClass.WriteColumnFile(allSet.toString());
			ToolClass.addOptLog(HuodaoTest.this,1,"修改物理货道对应表");
	        // 弹出信息提示
	        Toast.makeText(HuodaoTest.this, "〖修改物理货道对应表〗成功！", Toast.LENGTH_SHORT).show();
		}
	}
	
	//读取逻辑货道实际对应物理货道的文件
	private void gethuofile()
	{
		int physic;		
		Map<String, Integer> allset=null; 
		if(cabinetsetvar==2)
			allset=ToolClass.ReadColumnFile2(); 
		else
			allset=ToolClass.ReadColumnFile(); 
		if(allset!=null)
		{
			//第一层
			physic=57;
			//如果判断有这个Value值
			if(allset.containsValue(physic++))
			{
				btnhuoset11.setChecked(true);
			}
			else
			{
				btnhuoset11.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset12.setChecked(true);
			}
			else
			{
				btnhuoset12.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset13.setChecked(true);
			}
			else
			{
				btnhuoset13.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset14.setChecked(true);
			}
			else
			{
				btnhuoset14.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset15.setChecked(true);
			}
			else
			{
				btnhuoset15.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset16.setChecked(true);
			}
			else
			{
				btnhuoset16.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset17.setChecked(true);
			}
			else
			{
				btnhuoset17.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset18.setChecked(true);
			}
			else
			{
				btnhuoset18.setChecked(false);
			}
			
			//第二层
			physic=49;
			if(allset.containsValue(physic++))
			{
				btnhuoset21.setChecked(true);
			}
			else
			{
				btnhuoset21.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset22.setChecked(true);
			}
			else
			{
				btnhuoset22.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset23.setChecked(true);
			}
			else
			{
				btnhuoset23.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset24.setChecked(true);
			}
			else
			{
				btnhuoset24.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset25.setChecked(true);
			}
			else
			{
				btnhuoset25.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset26.setChecked(true);
			}
			else
			{
				btnhuoset26.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset27.setChecked(true);
			}
			else
			{
				btnhuoset27.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset28.setChecked(true);
			}
			else
			{
				btnhuoset28.setChecked(false);
			}
			
			//第三层
			physic=41;
			if(allset.containsValue(physic++))
			{
				btnhuoset31.setChecked(true);
			}
			else
			{
				btnhuoset31.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset32.setChecked(true);
			}
			else
			{
				btnhuoset32.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset33.setChecked(true);
			}
			else
			{
				btnhuoset33.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset34.setChecked(true);
			}
			else
			{
				btnhuoset34.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset35.setChecked(true);
			}
			else
			{
				btnhuoset35.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset36.setChecked(true);
			}
			else
			{
				btnhuoset36.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset37.setChecked(true);
			}
			else
			{
				btnhuoset37.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset38.setChecked(true);
			}
			else
			{
				btnhuoset38.setChecked(false);
			}
			
			//第四层
			physic=33;
			if(allset.containsValue(physic++))
			{
				btnhuoset41.setChecked(true);
			}
			else
			{
				btnhuoset41.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset42.setChecked(true);
			}
			else
			{
				btnhuoset42.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset43.setChecked(true);
			}
			else
			{
				btnhuoset43.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset44.setChecked(true);
			}
			else
			{
				btnhuoset44.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset45.setChecked(true);
			}
			else
			{
				btnhuoset45.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset46.setChecked(true);
			}
			else
			{
				btnhuoset46.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset47.setChecked(true);
			}
			else
			{
				btnhuoset47.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset48.setChecked(true);
			}
			else
			{
				btnhuoset48.setChecked(false);
			}
			
			//第五层
			physic=25;
			if(allset.containsValue(physic++))
			{
				btnhuoset51.setChecked(true);
			}
			else
			{
				btnhuoset51.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset52.setChecked(true);
			}
			else
			{
				btnhuoset52.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset53.setChecked(true);
			}
			else
			{
				btnhuoset53.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset54.setChecked(true);
			}
			else
			{
				btnhuoset54.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset55.setChecked(true);
			}
			else
			{
				btnhuoset55.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset56.setChecked(true);
			}
			else
			{
				btnhuoset56.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset57.setChecked(true);
			}
			else
			{
				btnhuoset57.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset58.setChecked(true);
			}
			else
			{
				btnhuoset58.setChecked(false);
			}
			
			//第六层
			physic=17;
			if(allset.containsValue(physic++))
			{
				btnhuoset61.setChecked(true);
			}
			else
			{
				btnhuoset61.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset62.setChecked(true);
			}
			else
			{
				btnhuoset62.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset63.setChecked(true);
			}
			else
			{
				btnhuoset63.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset64.setChecked(true);
			}
			else
			{
				btnhuoset64.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset65.setChecked(true);
			}
			else
			{
				btnhuoset65.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset66.setChecked(true);
			}
			else
			{
				btnhuoset66.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset67.setChecked(true);
			}
			else
			{
				btnhuoset67.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset68.setChecked(true);
			}
			else
			{
				btnhuoset68.setChecked(false);
			}
			
			//第七层
			physic=9;
			if(allset.containsValue(physic++))
			{
				btnhuoset71.setChecked(true);
			}
			else
			{
				btnhuoset71.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset72.setChecked(true);
			}
			else
			{
				btnhuoset72.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset73.setChecked(true);
			}
			else
			{
				btnhuoset73.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset74.setChecked(true);
			}
			else
			{
				btnhuoset74.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset75.setChecked(true);
			}
			else
			{
				btnhuoset75.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset76.setChecked(true);
			}
			else
			{
				btnhuoset76.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset77.setChecked(true);
			}
			else
			{
				btnhuoset77.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset78.setChecked(true);
			}
			else
			{
				btnhuoset78.setChecked(false);
			}
			
			//第八层
			physic=1;
			if(allset.containsValue(physic++))
			{
				btnhuoset81.setChecked(true);
			}
			else
			{
				btnhuoset81.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset82.setChecked(true);
			}
			else
			{
				btnhuoset82.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset83.setChecked(true);
			}
			else
			{
				btnhuoset83.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset84.setChecked(true);
			}
			else
			{
				btnhuoset84.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset85.setChecked(true);
			}
			else
			{
				btnhuoset85.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset86.setChecked(true);
			}
			else
			{
				btnhuoset86.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset87.setChecked(true);
			}
			else
			{
				btnhuoset87.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset88.setChecked(true);
			}
			else
			{
				btnhuoset88.setChecked(false);
			}
		}
	}
	
	//清空逻辑货道实际对应物理货道的文件
	private void clearhuofile()
	{
		btnhuoset11.setChecked(false);
		btnhuoset12.setChecked(false);
		btnhuoset13.setChecked(false);
		btnhuoset14.setChecked(false);
		btnhuoset15.setChecked(false);
		btnhuoset16.setChecked(false);
		btnhuoset17.setChecked(false);
		btnhuoset18.setChecked(false);
		btnhuoset21.setChecked(false);
		btnhuoset22.setChecked(false);
		btnhuoset23.setChecked(false);
		btnhuoset24.setChecked(false);
		btnhuoset25.setChecked(false);
		btnhuoset26.setChecked(false);
		btnhuoset27.setChecked(false);
		btnhuoset28.setChecked(false);
		btnhuoset31.setChecked(false);
		btnhuoset32.setChecked(false);
		btnhuoset33.setChecked(false);
		btnhuoset34.setChecked(false);
		btnhuoset35.setChecked(false);
		btnhuoset36.setChecked(false);
		btnhuoset37.setChecked(false);
		btnhuoset38.setChecked(false);
		btnhuoset41.setChecked(false);
		btnhuoset42.setChecked(false);
		btnhuoset43.setChecked(false);
		btnhuoset44.setChecked(false);
		btnhuoset45.setChecked(false);
		btnhuoset46.setChecked(false);
		btnhuoset47.setChecked(false);
		btnhuoset48.setChecked(false);
		btnhuoset51.setChecked(false);
		btnhuoset52.setChecked(false);
		btnhuoset53.setChecked(false);
		btnhuoset54.setChecked(false);
		btnhuoset55.setChecked(false);
		btnhuoset56.setChecked(false);
		btnhuoset57.setChecked(false);
		btnhuoset58.setChecked(false);
		btnhuoset61.setChecked(false);
		btnhuoset62.setChecked(false);
		btnhuoset63.setChecked(false);
		btnhuoset64.setChecked(false);
		btnhuoset65.setChecked(false);
		btnhuoset66.setChecked(false);
		btnhuoset67.setChecked(false);
		btnhuoset68.setChecked(false);
		btnhuoset71.setChecked(false);
		btnhuoset72.setChecked(false);
		btnhuoset73.setChecked(false);
		btnhuoset74.setChecked(false);
		btnhuoset75.setChecked(false);
		btnhuoset76.setChecked(false);
		btnhuoset77.setChecked(false);
		btnhuoset78.setChecked(false);
		btnhuoset81.setChecked(false);
		btnhuoset82.setChecked(false);
		btnhuoset83.setChecked(false);
		btnhuoset84.setChecked(false);
		btnhuoset85.setChecked(false);
		btnhuoset86.setChecked(false);
		btnhuoset87.setChecked(false);
		btnhuoset88.setChecked(false);
	}
	
	//========
	//升降机模块
	//========
	//自检逻辑货道开关性能,result==1,或者4货道存在，其他值货道不存在
	private void autoelevatorfile(int result)
	{
		switch(autophysic)
		{
			//第一层
			case 11:
				if((result==1)||(result==4))
				{
					btnhuoset11.setChecked(true);
				}
				else 
				{
					btnhuoset11.setChecked(false);	
				}
				autophysic++;				
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 12:
				if((result==1)||(result==4))
				{
					btnhuoset12.setChecked(true);
				}
				else 
				{
					btnhuoset12.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 13:
				if((result==1)||(result==4))
				{
					btnhuoset13.setChecked(true);
				}
				else 
				{
					btnhuoset13.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 14:
				if((result==1)||(result==4))
				{
					btnhuoset14.setChecked(true);
				}
				else 
				{
					btnhuoset14.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 15:
				if((result==1)||(result==4))
				{
					btnhuoset15.setChecked(true);
				}
				else 
				{
					btnhuoset15.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 16:
				if((result==1)||(result==4))
				{
					btnhuoset16.setChecked(true);
				}
				else 
				{
					btnhuoset16.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 17:
				if((result==1)||(result==4))
				{
					btnhuoset17.setChecked(true);
				}
				else 
				{
					btnhuoset17.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 18:
				if((result==1)||(result==4))
				{
					btnhuoset18.setChecked(true);
				}
				else 
				{
					btnhuoset18.setChecked(false);	
				}
				autophysic=21;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;	
			//第二层	
			case 21:
				if((result==1)||(result==4))
				{
					btnhuoset21.setChecked(true);
				}
				else 
				{
					btnhuoset21.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 22:
				if((result==1)||(result==4))
				{
					btnhuoset22.setChecked(true);
				}
				else 
				{
					btnhuoset22.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 23:
				if((result==1)||(result==4))
				{
					btnhuoset23.setChecked(true);
				}
				else 
				{
					btnhuoset23.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 24:
				if((result==1)||(result==4))
				{
					btnhuoset24.setChecked(true);
				}
				else 
				{
					btnhuoset24.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 25:
				if((result==1)||(result==4))
				{
					btnhuoset25.setChecked(true);
				}
				else 
				{
					btnhuoset25.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 26:
				if((result==1)||(result==4))
				{
					btnhuoset26.setChecked(true);
				}
				else 
				{
					btnhuoset26.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 27:
				if((result==1)||(result==4))
				{
					btnhuoset27.setChecked(true);
				}
				else 
				{
					btnhuoset27.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 28:
				if((result==1)||(result==4))
				{
					btnhuoset28.setChecked(true);
				}
				else 
				{
					btnhuoset28.setChecked(false);	
				}
				autophysic=31;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			//第三层	
			case 31:
				if((result==1)||(result==4))
				{
					btnhuoset31.setChecked(true);
				}
				else 
				{
					btnhuoset31.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 32:
				if((result==1)||(result==4))
				{
					btnhuoset32.setChecked(true);
				}
				else 
				{
					btnhuoset32.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 33:
				if((result==1)||(result==4))
				{
					btnhuoset33.setChecked(true);
				}
				else 
				{
					btnhuoset33.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 34:
				if((result==1)||(result==4))
				{
					btnhuoset34.setChecked(true);
				}
				else 
				{
					btnhuoset34.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 35:
				if((result==1)||(result==4))
				{
					btnhuoset35.setChecked(true);
				}
				else 
				{
					btnhuoset35.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 36:
				if((result==1)||(result==4))
				{
					btnhuoset36.setChecked(true);
				}
				else 
				{
					btnhuoset36.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 37:
				if((result==1)||(result==4))
				{
					btnhuoset37.setChecked(true);
				}
				else 
				{
					btnhuoset37.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 38:
				if((result==1)||(result==4))
				{
					btnhuoset38.setChecked(true);
				}
				else 
				{
					btnhuoset38.setChecked(false);	
				}
				autophysic=41;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;	
			//第四层	
			case 41:
				if((result==1)||(result==4))
				{
					btnhuoset41.setChecked(true);
				}
				else 
				{
					btnhuoset41.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 42:
				if((result==1)||(result==4))
				{
					btnhuoset42.setChecked(true);
				}
				else 
				{
					btnhuoset42.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 43:
				if((result==1)||(result==4))
				{
					btnhuoset43.setChecked(true);
				}
				else 
				{
					btnhuoset43.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 44:
				if((result==1)||(result==4))
				{
					btnhuoset44.setChecked(true);
				}
				else 
				{
					btnhuoset44.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 45:
				if((result==1)||(result==4))
				{
					btnhuoset45.setChecked(true);
				}
				else 
				{
					btnhuoset45.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 46:
				if((result==1)||(result==4))
				{
					btnhuoset46.setChecked(true);
				}
				else 
				{
					btnhuoset46.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 47:
				if((result==1)||(result==4))
				{
					btnhuoset47.setChecked(true);
				}
				else 
				{
					btnhuoset47.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 48:
				if((result==1)||(result==4))
				{
					btnhuoset48.setChecked(true);
				}
				else 
				{
					btnhuoset48.setChecked(false);	
				}
				autophysic=51;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			//第五层	
			case 51:
				if((result==1)||(result==4))
				{
					btnhuoset51.setChecked(true);
				}
				else 
				{
					btnhuoset51.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 52:
				if((result==1)||(result==4))
				{
					btnhuoset52.setChecked(true);
				}
				else 
				{
					btnhuoset52.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 53:
				if((result==1)||(result==4))
				{
					btnhuoset53.setChecked(true);
				}
				else 
				{
					btnhuoset53.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 54:
				if((result==1)||(result==4))
				{
					btnhuoset54.setChecked(true);
				}
				else 
				{
					btnhuoset54.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 55:
				if((result==1)||(result==4))
				{
					btnhuoset55.setChecked(true);
				}
				else 
				{
					btnhuoset55.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 56:
				if((result==1)||(result==4))
				{
					btnhuoset56.setChecked(true);
				}
				else 
				{
					btnhuoset56.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 57:
				if((result==1)||(result==4))
				{
					btnhuoset57.setChecked(true);
				}
				else 
				{
					btnhuoset57.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 58:
				if((result==1)||(result==4))
				{
					btnhuoset58.setChecked(true);
				}
				else 
				{
					btnhuoset58.setChecked(false);	
				}
				autophysic=61;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;	
			//第六层	
			case 61:
				if((result==1)||(result==4))
				{
					btnhuoset61.setChecked(true);
				}
				else 
				{
					btnhuoset61.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 62:
				if((result==1)||(result==4))
				{
					btnhuoset62.setChecked(true);
				}
				else 
				{
					btnhuoset62.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 63:
				if((result==1)||(result==4))
				{
					btnhuoset63.setChecked(true);
				}
				else 
				{
					btnhuoset63.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 64:
				if((result==1)||(result==4))
				{
					btnhuoset64.setChecked(true);
				}
				else 
				{
					btnhuoset64.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 65:
				if((result==1)||(result==4))
				{
					btnhuoset65.setChecked(true);
				}
				else 
				{
					btnhuoset65.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 66:
				if((result==1)||(result==4))
				{
					btnhuoset66.setChecked(true);
				}
				else 
				{
					btnhuoset66.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 67:
				if((result==1)||(result==4))
				{
					btnhuoset67.setChecked(true);
				}
				else 
				{
					btnhuoset67.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 68:
				if((result==1)||(result==4))
				{
					btnhuoset68.setChecked(true);
				}
				else 
				{
					btnhuoset68.setChecked(false);	
				}
				autophysic=71;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;	
			//第七层
			case 71:
				if((result==1)||(result==4))
				{
					btnhuoset71.setChecked(true);
				}
				else 
				{
					btnhuoset71.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 72:
				if((result==1)||(result==4))
				{
					btnhuoset72.setChecked(true);
				}
				else 
				{
					btnhuoset72.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 73:
				if((result==1)||(result==4))
				{
					btnhuoset73.setChecked(true);
				}
				else 
				{
					btnhuoset73.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 74:
				if((result==1)||(result==4))
				{
					btnhuoset74.setChecked(true);
				}
				else 
				{
					btnhuoset74.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 75:
				if((result==1)||(result==4))
				{
					btnhuoset75.setChecked(true);
				}
				else 
				{
					btnhuoset75.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 76:
				if((result==1)||(result==4))
				{
					btnhuoset76.setChecked(true);
				}
				else 
				{
					btnhuoset76.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 77:
				if((result==1)||(result==4))
				{
					btnhuoset77.setChecked(true);
				}
				else 
				{
					btnhuoset77.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 78:
				if((result==1)||(result==4))
				{
					btnhuoset78.setChecked(true);
				}
				else 
				{
					btnhuoset78.setChecked(false);	
				}
				autophysic=81;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			//第八层
			case 81:
				if((result==1)||(result==4))
				{
					btnhuoset81.setChecked(true);
				}
				else 
				{
					btnhuoset81.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 82:
				if((result==1)||(result==4))
				{
					btnhuoset82.setChecked(true);
				}
				else 
				{
					btnhuoset82.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 83:
				if((result==1)||(result==4))
				{
					btnhuoset83.setChecked(true);
				}
				else 
				{
					btnhuoset83.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 84:
				if((result==1)||(result==4))
				{
					btnhuoset84.setChecked(true);
				}
				else 
				{
					btnhuoset84.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 85:
				if((result==1)||(result==4))
				{
					btnhuoset85.setChecked(true);
				}
				else 
				{
					btnhuoset85.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 86:
				if((result==1)||(result==4))
				{
					btnhuoset86.setChecked(true);
				}
				else 
				{
					btnhuoset86.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 87:
				if((result==1)||(result==4))
				{
					btnhuoset87.setChecked(true);
				}
				else 
				{
					btnhuoset87.setChecked(false);	
				}
				autophysic++;
				comsend(COMService.EV_CHUHUOCHILD,autophysic);
				break;
			case 88:
				if((result==1)||(result==4))
				{
					btnhuoset88.setChecked(true);
				}
				else 
				{
					btnhuoset88.setChecked(false);	
				}
				dialog.dismiss();
				autochu=false;
				sethuofile();
				gethuofile();
				break;	
		}
	}
	
	//保存逻辑货道实际对应物理货道的升降机文件
	private void setelevatorfile()
	{
		int logic=1,physic;
		JSONObject allSet=new JSONObject();
		try {
			//第一层
			physic=11;
			if(btnhuoset11.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset12.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset13.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset14.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset15.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset16.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset17.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset18.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第二层
			physic=21;
			if(btnhuoset21.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset22.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset23.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset24.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset25.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset26.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset27.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset28.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第三层
			physic=31;
			if(btnhuoset31.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset32.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset33.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset34.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset35.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset36.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset37.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset38.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第四层
			physic=41;
			if(btnhuoset41.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset42.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset43.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset44.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset45.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset46.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset47.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset48.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第五层
			physic=51;
			if(btnhuoset51.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset52.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset53.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset54.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset55.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset56.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset57.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset58.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第六层
			physic=61;
			if(btnhuoset61.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset62.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset63.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset64.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset65.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset66.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset67.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset68.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第七层
			physic=71;
			if(btnhuoset71.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset72.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset73.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset74.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset75.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset76.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset77.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset78.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			
			//第八层
			physic=81;
			if(btnhuoset81.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset82.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset83.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset84.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset85.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset86.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset87.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
			if(btnhuoset88.isChecked())
			{
				allSet.put(String.valueOf(logic++), physic++);
			}
			else
			{
				physic++;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
        if(cabinetsetvar==2)
        {
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+allSet.length()+"升降机状态:"+allSet.toString(),"log.txt");	
			ToolClass.WriteElevatorFile2(allSet.toString());
			ToolClass.addOptLog(HuodaoTest.this,1,"修改物理副柜货道对应表");
	        // 弹出信息提示
	        Toast.makeText(HuodaoTest.this, "〖修改物理副柜货道对应表〗成功！", Toast.LENGTH_SHORT).show();
        }
        else
        {
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+allSet.length()+"升降机状态:"+allSet.toString(),"log.txt");	
			ToolClass.WriteElevatorFile(allSet.toString());
			ToolClass.addOptLog(HuodaoTest.this,1,"修改物理货道对应表");
	        // 弹出信息提示
	        Toast.makeText(HuodaoTest.this, "〖修改物理货道对应表〗成功！", Toast.LENGTH_SHORT).show();
        }
    }
	
	//读取逻辑货道实际对应物理货道的文件
	private void getelevatorfile()
	{
		int physic;
		Map<String, Integer> allset=null; 
		if(cabinetsetvar==2)
			allset=ToolClass.ReadElevatorFile2(); 
		else
			allset=ToolClass.ReadElevatorFile(); 
		if(allset!=null)
		{
			//第一层
			physic=11;
			//如果判断有这个Value值
			if(allset.containsValue(physic++))
			{
				btnhuoset11.setChecked(true);
			}
			else
			{
				btnhuoset11.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset12.setChecked(true);
			}
			else
			{
				btnhuoset12.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset13.setChecked(true);
			}
			else
			{
				btnhuoset13.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset14.setChecked(true);
			}
			else
			{
				btnhuoset14.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset15.setChecked(true);
			}
			else
			{
				btnhuoset15.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset16.setChecked(true);
			}
			else
			{
				btnhuoset16.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset17.setChecked(true);
			}
			else
			{
				btnhuoset17.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset18.setChecked(true);
			}
			else
			{
				btnhuoset18.setChecked(false);
			}
			
			//第二层
			physic=21;
			if(allset.containsValue(physic++))
			{
				btnhuoset21.setChecked(true);
			}
			else
			{
				btnhuoset21.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset22.setChecked(true);
			}
			else
			{
				btnhuoset22.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset23.setChecked(true);
			}
			else
			{
				btnhuoset23.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset24.setChecked(true);
			}
			else
			{
				btnhuoset24.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset25.setChecked(true);
			}
			else
			{
				btnhuoset25.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset26.setChecked(true);
			}
			else
			{
				btnhuoset26.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset27.setChecked(true);
			}
			else
			{
				btnhuoset27.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset28.setChecked(true);
			}
			else
			{
				btnhuoset28.setChecked(false);
			}
			
			//第三层
			physic=31;
			if(allset.containsValue(physic++))
			{
				btnhuoset31.setChecked(true);
			}
			else
			{
				btnhuoset31.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset32.setChecked(true);
			}
			else
			{
				btnhuoset32.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset33.setChecked(true);
			}
			else
			{
				btnhuoset33.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset34.setChecked(true);
			}
			else
			{
				btnhuoset34.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset35.setChecked(true);
			}
			else
			{
				btnhuoset35.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset36.setChecked(true);
			}
			else
			{
				btnhuoset36.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset37.setChecked(true);
			}
			else
			{
				btnhuoset37.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset38.setChecked(true);
			}
			else
			{
				btnhuoset38.setChecked(false);
			}
			
			//第四层
			physic=41;
			if(allset.containsValue(physic++))
			{
				btnhuoset41.setChecked(true);
			}
			else
			{
				btnhuoset41.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset42.setChecked(true);
			}
			else
			{
				btnhuoset42.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset43.setChecked(true);
			}
			else
			{
				btnhuoset43.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset44.setChecked(true);
			}
			else
			{
				btnhuoset44.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset45.setChecked(true);
			}
			else
			{
				btnhuoset45.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset46.setChecked(true);
			}
			else
			{
				btnhuoset46.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset47.setChecked(true);
			}
			else
			{
				btnhuoset47.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset48.setChecked(true);
			}
			else
			{
				btnhuoset48.setChecked(false);
			}
			
			//第五层
			physic=51;
			if(allset.containsValue(physic++))
			{
				btnhuoset51.setChecked(true);
			}
			else
			{
				btnhuoset51.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset52.setChecked(true);
			}
			else
			{
				btnhuoset52.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset53.setChecked(true);
			}
			else
			{
				btnhuoset53.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset54.setChecked(true);
			}
			else
			{
				btnhuoset54.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset55.setChecked(true);
			}
			else
			{
				btnhuoset55.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset56.setChecked(true);
			}
			else
			{
				btnhuoset56.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset57.setChecked(true);
			}
			else
			{
				btnhuoset57.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset58.setChecked(true);
			}
			else
			{
				btnhuoset58.setChecked(false);
			}
			
			//第六层
			physic=61;
			if(allset.containsValue(physic++))
			{
				btnhuoset61.setChecked(true);
			}
			else
			{
				btnhuoset61.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset62.setChecked(true);
			}
			else
			{
				btnhuoset62.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset63.setChecked(true);
			}
			else
			{
				btnhuoset63.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset64.setChecked(true);
			}
			else
			{
				btnhuoset64.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset65.setChecked(true);
			}
			else
			{
				btnhuoset65.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset66.setChecked(true);
			}
			else
			{
				btnhuoset66.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset67.setChecked(true);
			}
			else
			{
				btnhuoset67.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset68.setChecked(true);
			}
			else
			{
				btnhuoset68.setChecked(false);
			}
			
			//第七层
			physic=71;
			if(allset.containsValue(physic++))
			{
				btnhuoset71.setChecked(true);
			}
			else
			{
				btnhuoset71.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset72.setChecked(true);
			}
			else
			{
				btnhuoset72.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset73.setChecked(true);
			}
			else
			{
				btnhuoset73.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset74.setChecked(true);
			}
			else
			{
				btnhuoset74.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset75.setChecked(true);
			}
			else
			{
				btnhuoset75.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset76.setChecked(true);
			}
			else
			{
				btnhuoset76.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset77.setChecked(true);
			}
			else
			{
				btnhuoset77.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset78.setChecked(true);
			}
			else
			{
				btnhuoset78.setChecked(false);
			}
			
			//第八层
			physic=81;
			if(allset.containsValue(physic++))
			{
				btnhuoset81.setChecked(true);
			}
			else
			{
				btnhuoset81.setChecked(false);
			}	
			if(allset.containsValue(physic++))
			{
				btnhuoset82.setChecked(true);
			}
			else
			{
				btnhuoset82.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset83.setChecked(true);
			}
			else
			{
				btnhuoset83.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset84.setChecked(true);
			}
			else
			{
				btnhuoset84.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset85.setChecked(true);
			}
			else
			{
				btnhuoset85.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset86.setChecked(true);
			}
			else
			{
				btnhuoset86.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset87.setChecked(true);
			}
			else
			{
				btnhuoset87.setChecked(false);
			}
			if(allset.containsValue(physic++))
			{
				btnhuoset88.setChecked(true);
			}
			else
			{
				btnhuoset88.setChecked(false);
			}
		}
	}
	
		
	
}
