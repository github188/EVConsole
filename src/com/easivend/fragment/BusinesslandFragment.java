package com.easivend.fragment;

import java.util.HashMap;
import java.util.Map;

import com.easivend.common.AudioSound;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_classDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_cabinet;
import com.easivend.model.Tb_vmc_product;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
public class BusinesslandFragment extends Fragment 
{	
	final static int REQUEST_CODE=1; 	
	EditText txtadsTip=null;
	ImageButton btnads1=null, btnads2=null,btnads3=null,btnads4=null,btnads5=null,btnads6=null,
			btnads7=null,btnads8=null,btnads9=null,btnads0=null,btnadscancel=null,btnadsenter=null;
	ImageView ivquhuo=null,ivgmys=null,ivczjx=null,btnadsclass=null;
	Intent intent=null;
	private boolean quhuo=false;//true使用取货码功能
	private static int count=0;
	private static String huo="";
	private int pscount=0;
	//定时器清除调出密码框的功能
	Dialog psdialog=null;
	Dialog quhuodialog=null;
	//发送出货指令
    private String proID = null;
	private String productID = null;
	private String proImage = null;
	private String cabID = null;
	private String huoID = null;
    private String prosales = null; 
    private Context context;   
    //=========================
    //fragment与activity回调相关
    //=========================
    /**
     * 用来与外部activity交互的
     */
    private BusFragInteraction listterner;
    /**
     * 步骤四、当ContentFragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof BusFragInteraction)
        {
            listterner = (BusFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements BusFragInteraction");
        }

    }
    /**
     * 步骤一、定义了所有activity必须实现的接口
     */
    public interface BusFragInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        void finishBusiness();//关闭activity页面
        void gotoBusiness(int buslevel,Map<String, String>str);  //跳转到商品页面     
        void stoptimer();//关闭定时器
        void restarttimer();//重新打开定时器
        void quhuoBusiness(String PICKUP_CODE);//传递取货码
        void tishiInfo(int infotype);//传递提示信息
    }
    @Override
    public void onDetach() {
        super.onDetach();

        listterner = null;
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//设置横屏还是竖屏的布局策略
		View view;
		//竖屏
		if(ToolClass.getOrientation()==1)
			view = inflater.inflate(R.layout.fragment_businessport, container, false);  
		//横屏
		else
			view = inflater.inflate(R.layout.fragment_businessland, container, false);  
		context=this.getActivity();//获取activity的context	
		//定时器返回广告页面
//		timer.schedule(new TimerTask() { 
//	        @Override 
//	        public void run() { 	        	  
//        		  if(pwdcount > 0)
//	              { 
//        			  pwdcount=0;
//	              }		        	  
//	        } 
//	    }, 1000, 10000);       // timeTask  
		//=======
		//操作模块
		//=======
		txtadsTip = (EditText) view.findViewById(R.id.txtadsTip);
		txtadsTip.setFocusable(false);//不让该edittext获得焦点
		txtadsTip.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// 关闭软键盘，这样当点击该edittext的时候，不会弹出系统自带的输入法
				txtadsTip.setInputType(InputType.TYPE_NULL);
				if((pscount<10)&&(pscount>=5))
				{
					pscount++;
				}
				return false;
			}
		});
		btnads1 = (ImageButton) view.findViewById(R.id.btnads1);		
		btnads1.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("1",1);
		    	
		    }
		});
		btnads2 = (ImageButton) view.findViewById(R.id.btnads2);
		btnads2.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("2",1);
		    }
		});
		btnads3 = (ImageButton) view.findViewById(R.id.btnads3);
		btnads3.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("3",1);
		    }
		});
		btnads4 = (ImageButton) view.findViewById(R.id.btnads4);
		btnads4.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("4",1);
		    }
		});
		btnads5 = (ImageButton) view.findViewById(R.id.btnads5);
		btnads5.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("5",1);
		    }
		});
		btnads6 = (ImageButton) view.findViewById(R.id.btnads6);
		btnads6.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("6",1);
		    }
		});
		btnads7 = (ImageButton) view.findViewById(R.id.btnads7);
		btnads7.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("7",1);
		    }
		});
		btnads8 = (ImageButton) view.findViewById(R.id.btnads8);
		btnads8.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("8",1);
		    }
		});
		btnads9 = (ImageButton) view.findViewById(R.id.btnads9);
		btnads9.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("9",1);
		    }
		});
		btnads0 = (ImageButton) view.findViewById(R.id.btnads0);
		btnads0.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("0",1);
		    }
		});
		btnadscancel = (ImageButton) view.findViewById(R.id.btnadscancel);
		btnadscancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("0",0);
		    	if(pscount>=10)
				{
					pscount=0;
					passdialog();
				}
		    	else
		    	{
		    		pscount=0;
		    	}
		    }
		});
		btnadsenter = (ImageButton) view.findViewById(R.id.btnadsenter);
		btnadsenter.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(pscount<5)
		    	{
			    	pscount++;
		    	}
		    }
		});
		btnadsclass = (ImageView) view.findViewById(R.id.btnadsclass);
		btnadsclass.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	vmc_classDAO classdao = new vmc_classDAO(context);// 创建InaccountDAO对象
		    	long count=classdao.getCount();
		    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品类型数量="+count,"log.txt");
		    	if(count>0)
		    	{
			    	//intent = new Intent(context, BusgoodsClass.class);// 使用Accountflag窗口初始化Intent
			    	//startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
		    		listterner.gotoBusiness(1,null);
		    	}
		    	else
		    	{
//		    		intent = new Intent(context, Busgoods.class);// 使用Accountflag窗口初始化Intent
//                	intent.putExtra("proclassID", "");
//                	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag		    		
                	listterner.gotoBusiness(2,null);
		    	}
		    	AudioSound.playbusiness();
		    	
		    }
		});
		ivgmys = (ImageView) view.findViewById(R.id.ivgmys);
		ivgmys.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	listterner.tishiInfo(1);		    	
		    }
		});
		ivczjx = (ImageView) view.findViewById(R.id.ivczjx);
		ivczjx.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	listterner.tishiInfo(2);		    	
		    }
		});
		ivquhuo = (ImageView) view.findViewById(R.id.ivquhuo);
		ivquhuo.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) 
		    {
		    	if(quhuo)
		    		quhuodialog();		    	
		    }
		});
		//*********************
		//搜索是否可以使用取货码
		//*********************
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if((tb_inaccount!=null)&&(tb_inaccount.getCard()==1))
    	{
			quhuo=true;//打开
		}
		else
		{
			quhuo=false;//关闭
		}
    	
		return view;  
	}
	//num出货柜号,type=1输入数字，type=0回退数字
    private void chuhuo(String num,int type)
    {    	
		if(type==1)
		{
			pscount=0;
			if(count<3)
	    	{
	    		count++;
	    		huo=huo+num;
	    		txtadsTip.setText(huo);
	    	}
		}
		else if(type==0)
		{
			if(count>0)
			{
				count--;
				huo=huo.substring(0,huo.length()-1);
				if(count==0)
					txtadsTip.setText("");
				else
					txtadsTip.setText(huo);
			}
		}  
		if(count==3)
		{
			cabID=huo.substring(0,1);
		    huoID=huo.substring(1,huo.length());
		    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品huoID="+huoID,"log.txt");
		    vmc_columnDAO columnDAO = new vmc_columnDAO(context);// 创建InaccountDAO对象		    
		    Tb_vmc_product tb_inaccount = columnDAO.getColumnproduct(cabID,huoID);
		    if(tb_inaccount!=null)
		    {
			    productID=tb_inaccount.getProductID().toString();
			    prosales=String.valueOf(tb_inaccount.getSalesPrice());
			    proImage=tb_inaccount.getAttBatch1();
			    proID=productID+"-"+tb_inaccount.getProductName().toString();
			    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="
						+productID+" proType="
						+"2"+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
						+"1","log.txt");
			    count=0;
			    huo="";
			    txtadsTip.setText("");
//				Intent intent = null;// 创建Intent对象                
//	        	intent = new Intent(context, BusgoodsSelect.class);// 使用Accountflag窗口初始化Intent
//	        	intent.putExtra("proID", proID);
//	        	intent.putExtra("productID", productID);
//	        	intent.putExtra("proImage", proImage);
//	        	intent.putExtra("prosales", prosales);
//	        	intent.putExtra("procount", "1");
//	        	intent.putExtra("proType", "2");//1代表通过商品ID出货,2代表通过货道出货
//	        	intent.putExtra("cabID", cabID);//出货柜号,proType=1时无效
//	        	intent.putExtra("huoID", huoID);//出货货道号,proType=1时无效
//
//
////	        	OrderDetail.setProID(proID);
////            	OrderDetail.setProductID(productID);
////            	OrderDetail.setProType("2");
////            	OrderDetail.setCabID(cabID);
////            	OrderDetail.setColumnID(huoID);
////            	OrderDetail.setShouldPay(Float.parseFloat(prosales));
////            	OrderDetail.setShouldNo(1);
//	        	
//	        	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
	        	Map<String, String>str=new HashMap<String, String>();
	        	str.put("proID", proID);
	        	str.put("productID", productID);
	        	str.put("proImage", proImage);
	        	str.put("prosales", prosales);
	        	str.put("procount", "1");
	        	str.put("proType", "2");//1代表通过商品ID出货,2代表通过货道出货
	        	str.put("cabID", cabID);//出货柜号,proType=1时无效
	        	str.put("huoID", huoID);//出货货道号,proType=1时无效
	        	listterner.gotoBusiness(3,str);
		    }
		    else
		    {
		    	count=0;
			    huo="";
			    txtadsTip.setText("");
                ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品huoID="+huoID+"isLAST_CHUHUO="+ToolClass.isLAST_CHUHUO(),"log.txt");
                //开格子柜门
		    	if(ToolClass.isLAST_CHUHUO())	
		    	{
		    		vmc_columnDAO columnDAO2 = new vmc_columnDAO(context);// 创建InaccountDAO对象		    
				    Tb_vmc_product tb_inaccount2 = columnDAO2.getColumnproductforzero(cabID,huoID);
				    if(tb_inaccount2!=null)
				    {
				    	//查找货道类型
		        		vmc_cabinetDAO cabinetDAO3 = new vmc_cabinetDAO(context);// 创建InaccountDAO对象
		        	    // 获取所有收入信息，并存储到List泛型集合中
		        	    Tb_vmc_cabinet listinfos3 = cabinetDAO3.findScrollData(String.valueOf(Integer.parseInt(cabID)));
		        	    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productID="+tb_inaccount2.getProductID()+"柜类型="+listinfos3.getCabType(),"log.txt");
		        	    if(listinfos3.getCabType()==5)
		        	    {
		        	    	productID=tb_inaccount2.getProductID().toString();
		    			    prosales=String.valueOf(tb_inaccount2.getSalesPrice());
		    			    proImage=tb_inaccount2.getAttBatch1();
		    			    proID=productID+"-"+tb_inaccount2.getProductName().toString();
		    			    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="
		    						+productID+" proType="
		    						+"2"+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
		    						+"1","log.txt");
							//免费开门
		    			    Map<String, String>str=new HashMap<String, String>();
		    	        	str.put("proID", proID);
		    	        	str.put("productID", productID);
		    	        	str.put("proImage", proImage);
		    	        	str.put("prosales", prosales);
		    	        	str.put("procount", "1");
		    	        	str.put("proType", "2");//1代表通过商品ID出货,2代表通过货道出货
		    	        	str.put("cabID", cabID);//出货柜号,proType=1时无效
		    	        	str.put("huoID", huoID);//出货货道号,proType=1时无效
		    	        	str.put("payType", "5");//
		    	        	
		    	        	OrderDetail.setProID(str.get("proID"));
		    		    	OrderDetail.setProductID(str.get("productID"));
		    		    	OrderDetail.setProType(str.get("proType"));
		    		    	OrderDetail.setShouldPay(Float.parseFloat(str.get("prosales")));
		    		    	OrderDetail.setShouldNo(1);
		    		    	OrderDetail.setCabID(str.get("cabID"));
		    		    	OrderDetail.setColumnID(str.get("huoID"));
		    		    	OrderDetail.setPayType(5);
		    	        	listterner.gotoBusiness(4,str);						    
		        	    }
		        	    else
				    	{
					    	// 弹出信息提示
						    ToolClass.failToast("抱歉，本商品已售完！");	
				    	}
				    }
				    else
			    	{
				    	// 弹出信息提示
					    ToolClass.failToast("抱歉，本商品已售完！");	
			    	}
		    	}
		    	//货道无法售卖
		    	else
		    	{
			    	// 弹出信息提示
				    ToolClass.failToast("抱歉，本商品已售完！");	
		    	}				
		    }
		    
		}
    } 
    
        
    //密码框
    private void passdialog()
    {    	
    	View myview=null;
		// TODO Auto-generated method stub
		LayoutInflater factory = LayoutInflater.from(context);
		myview=factory.inflate(R.layout.selectinteger, null);
		final EditText dialoginte=(EditText) myview.findViewById(R.id.dialoginte);
		
		psdialog = new AlertDialog.Builder(context)
		.setTitle("请输入管理员密码")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() 	
		{
				
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				boolean istrue=false;
				// TODO Auto-generated method stub
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<数值="+dialoginte.getText().toString(),"log.txt");
				//调出维护页面密码
				vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
			    // 获取所有收入信息，并存储到List泛型集合中
		    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
		    	if(tb_inaccount!=null)
                {
                    String Pwd=tb_inaccount.getMainPwd().toString();
                    if(ToolClass.isEmptynull(Pwd))
                    {
                        //ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<=null","log.txt");
                        istrue="83718557".equals(dialoginte.getText().toString());
                    }
                    else
                    {
                        istrue=Pwd.equals(dialoginte.getText().toString());                        
                    }
                }
                else
                {
                    istrue="83718557".equals(dialoginte.getText().toString());
                }
		    	
		    	if(istrue)
		    	{
		    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<确定退出","log.txt");
		    		//步骤二、fragment向activity发送回调信息
		        	listterner.finishBusiness();
		    	}
		    	else
		    	{
		    		listterner.restarttimer();//重新打开定时器
		    		// 弹出信息提示
		    		ToolClass.failToast("〖管理员密码〗错误！");	
				}
			}
		})
		.setNegativeButton("取消",  new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
    	{			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// TODO Auto-generated method stub	
				listterner.restarttimer();//重新打开定时器
				
			}
    	})
		.setView(myview)//这里将对话框布局文件加入到对话框中
		.create();
		psdialog.show();
		
    	listterner.stoptimer();//关闭定时器
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<打开密码框","log.txt");
    	//延时0.5s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {      
            	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<超时取消密码框","log.txt");
            	if(psdialog!=null)
            	{
	            	if(psdialog.isShowing())
	            	{
	            		psdialog.dismiss();
	            		listterner.restarttimer();//重新打开定时器
	            	}
            	}
            }

		}, 30*1000);
    }
        
    
    //取货码框
    private void quhuodialog()
    {
    	View myview=null;  
		// TODO Auto-generated method stub
		LayoutInflater factory = LayoutInflater.from(context);
		myview=factory.inflate(R.layout.selectinteger, null);
		final EditText dialoginte=(EditText) myview.findViewById(R.id.dialoginte);
		quhuodialog = new AlertDialog.Builder(context)
		.setTitle("请输入取货码")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() 	
		{
				
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				String PICKUP_CODE=dialoginte.getText().toString();
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<取货码="+PICKUP_CODE,"log.txt");
				if(ToolClass.isEmptynull(PICKUP_CODE)!=true)
				{
					//步骤二、fragment向activity发送回调信息
		        	listterner.quhuoBusiness(PICKUP_CODE);
				}
				else
				{
					listterner.restarttimer();//重新打开定时器
				}
			}
		})
		.setNegativeButton("取消",  new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
    	{			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// TODO Auto-generated method stub	
				listterner.restarttimer();//重新打开定时器
			}
    	})
		.setView(myview)//这里将对话框布局文件加入到对话框中
		.create();
		quhuodialog.show();  
		
		listterner.stoptimer();//关闭定时器
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<打开取货码框","log.txt");
		//延时0.5s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {      
            	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<超时取消取货码框","log.txt");
            	if(quhuodialog!=null)
            	{
	            	if(quhuodialog.isShowing())
	            	{
	            		quhuodialog.dismiss();
	            		listterner.restarttimer();//重新打开定时器
	            	}
            	}
            }

		}, 2*60*1000);	
    }
      
}
