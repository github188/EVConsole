/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           Login.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        登陆选择串口号页面          
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/


package com.easivend.app.maintain;


import java.util.Map;

import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_orderDAO;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity 
{
	private EditText txtlogin,txtbent,txtcolumn,txtcardcom,txtprintcom,txtextracom,txtposip,txtposipport,txtcolumn2;// 创建EditText对象
	private TextView tvVersion=null,tvprintcom=null,tvextracom=null,tvposip=null,tvposipport=null,tvcolumn2=null;
    private Button btnlogin,btnclose,btnGaoji,btnDel,btnDelImg,btnDelads;// 创建两个Button对象
    private Switch switchisssl;
    String com =null;
    String bentcom =null;
    String columncom =null;
    String extracom =null;
    String cardcom =null;
    String printcom =null;
    String posip=null;
    String posipport=null;
    int posisssl=0;
    String columncom2 =null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);// 设置布局文件
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());		
		txtlogin = (EditText) findViewById(R.id.txtLogin);// 获取现金设备串口号文本框
        txtbent = (EditText) findViewById(R.id.txtbent);// 获取格子柜串口号文本框
        txtcolumn = (EditText) findViewById(R.id.txtcolumn);// 获取主柜串口号文本框
        txtcardcom = (EditText) findViewById(R.id.txtserver);// 获取读卡器串口号文本框
        tvprintcom = (TextView) findViewById(R.id.tvprintcom);
        txtprintcom = (EditText) findViewById(R.id.txtprintcom);//获取打印机串口号文本框
        tvextracom = (TextView) findViewById(R.id.tvextracom);        
        txtextracom = (EditText) findViewById(R.id.txtextracom);//获取外协设备串口号文本框
        tvposip = (TextView) findViewById(R.id.tvposip);        
        txtposip = (EditText) findViewById(R.id.txtposip);//获取读卡器ip文本框
        tvposipport = (TextView) findViewById(R.id.tvposipport);        
        txtposipport = (EditText) findViewById(R.id.txtposipport);//获取读卡器端口文本框
        switchisssl= (Switch) findViewById(R.id.switchisssl);//是否打开ssl加密
        tvcolumn2 = (TextView) findViewById(R.id.tvcolumn2);        
        txtcolumn2 = (EditText) findViewById(R.id.txtcolumn2);//获取副弹簧/升降机柜串口号
        tvVersion = (TextView) findViewById(R.id.tvVersion);//本机版本号
        btnDel = (Button) findViewById(R.id.btnDel);// 获取清除全部商品货道信息
        btnDelImg = (Button) findViewById(R.id.btnDelImg);// 获取清除商品图片缓存信息
        btnDelads = (Button) findViewById(R.id.btnDelads);// 获取清除广告缓存信息
        tvprintcom.setVisibility(View.GONE); 
        txtprintcom.setVisibility(View.GONE); 
        tvextracom.setVisibility(View.GONE); 
        txtextracom.setVisibility(View.GONE); 
        tvposip.setVisibility(View.GONE); 
        txtposip.setVisibility(View.GONE);
        tvposipport.setVisibility(View.GONE); 
        txtposipport.setVisibility(View.GONE);
        switchisssl.setVisibility(View.GONE);
        tvcolumn2.setVisibility(View.GONE); 
        txtcolumn2.setVisibility(View.GONE);
        btnDel.setVisibility(View.GONE);
        btnDelImg.setVisibility(View.GONE);
        btnDelads.setVisibility(View.GONE);
        tvVersion.setText("版本号："+ToolClass.getVersion());
        Map<String, String> list=ToolClass.ReadConfigFile();
        if(list!=null)
        {	        
	        if(list.containsKey("com"))//设置现金串口号
	        {
	        	com = list.get("com");
	        }
			if(list.containsKey("bentcom"))//设置格子柜串口号
	        {
				bentcom = list.get("bentcom");
	        }
			if(list.containsKey("columncom"))//设置主柜串口号
	        {
				columncom = list.get("columncom");
	        }			
			if(list.containsKey("cardcom"))//设置读卡器串口号
	        {
	        	cardcom = list.get("cardcom");
	        }	
			if(list.containsKey("printcom"))//设置打印机串口号
	        {
	        	printcom = list.get("printcom");
	        }
			if(list.containsKey("extracom"))//设置外协串口号
	        {
	        	extracom = list.get("extracom");	
	        }
			if(list.containsKey("posip"))//设置读卡器ip
	        {
	        	posip = list.get("posip");
	        }
	        if(list.containsKey("posipport"))//设置读卡器端口
	        {
	        	posipport = list.get("posipport");
	        }
	        if(list.containsKey("isallopen"))//设置副柜串口号
	        {
				columncom2 = list.get("isallopen");	
	        }
	        if(list.containsKey("posisssl"))//设置ssl加密
	        {
                posisssl = (list.get("posisssl").equals(""))?0:Integer.parseInt(list.get("posisssl"));    
	        }
        }
        txtlogin.setText(com);
        txtbent.setText(bentcom);
        txtcolumn.setText(columncom);        
        txtcardcom.setText(cardcom); 
        txtprintcom.setText(printcom);
        txtextracom.setText(extracom);
        txtposip.setText(posip);
        txtposipport.setText(posipport);
        switchisssl.setChecked((posisssl==1)?true:false);
        txtcolumn2.setText(columncom2); 
        btnlogin = (Button) findViewById(R.id.btnLogin);// 获取修改按钮
        btnclose = (Button) findViewById(R.id.btnClose);// 获取取消按钮
        btnGaoji = (Button) findViewById(R.id.btnGaoji);// 获取高级按钮
        btnclose.setOnClickListener(new OnClickListener() {// 为取消按钮设置监听事件
            @Override
            public void onClick(View arg0) {
                finish();// 退出当前程序
            }
        });
        btnlogin.setOnClickListener(new OnClickListener() {// 为登录按钮设置监听事件
            @Override
            public void onClick(View arg0)
            {
            	com = ToolClass.replaceBlank(txtlogin.getText().toString());
    	        bentcom = ToolClass.replaceBlank(txtbent.getText().toString()); 
    	        columncom = ToolClass.replaceBlank(txtcolumn.getText().toString()); 
    	        printcom = ToolClass.replaceBlank(txtprintcom.getText().toString());
    	        extracom = ToolClass.replaceBlank(txtextracom.getText().toString()); 
    	        posip = ToolClass.replaceBlank(txtposip.getText().toString()); 
    	        posipport = ToolClass.replaceBlank(txtposipport.getText().toString()); 
    	        posisssl=(switchisssl.isChecked()==true)?1:0;
    	        columncom2 = ToolClass.replaceBlank(txtcolumn2.getText().toString());
    	        cardcom = ToolClass.replaceBlank(txtcardcom.getText().toString()); 
            	ToolClass.WriteConfigFile(com, bentcom,columncom,extracom,cardcom,printcom,columncom2,posip,posipport,String.valueOf(posisssl));            	
            	ToolClass.addOptLog(Login.this,1,"修改串口:");
	            // 弹出信息提示
	            Toast.makeText(Login.this, "〖修改串口〗成功！", Toast.LENGTH_SHORT).show();
	            //退出时，返回intent
	            Intent intent=new Intent();
	            setResult(MaintainActivity.RESULT_OK,intent);
            	finish();// 退出当前程序           
            }
        });
        btnGaoji.setOnClickListener(new OnClickListener() {// 为取消按钮设置监听事件
            @Override
            public void onClick(View arg0) {
                if(tvextracom.getVisibility()==View.GONE)
                {
                	tvprintcom.setVisibility(View.VISIBLE); 
                	txtprintcom.setVisibility(View.VISIBLE);
                	tvextracom.setVisibility(View.VISIBLE); 
                    txtextracom.setVisibility(View.VISIBLE); 
                    tvposip.setVisibility(View.VISIBLE); 
                    txtposip.setVisibility(View.VISIBLE);
                    tvposipport.setVisibility(View.VISIBLE); 
                    txtposipport.setVisibility(View.VISIBLE);
                    switchisssl.setVisibility(View.VISIBLE);
                    tvcolumn2.setVisibility(View.VISIBLE); 
                    txtcolumn2.setVisibility(View.VISIBLE);
                    btnDel.setVisibility(View.VISIBLE);
                    btnDelImg.setVisibility(View.VISIBLE);
                    btnDelads.setVisibility(View.VISIBLE);
                }
                else
                {
                	tvprintcom.setVisibility(View.GONE);
                	txtprintcom.setVisibility(View.GONE); 
                	tvextracom.setVisibility(View.GONE);
                    txtextracom.setVisibility(View.GONE); 
                    tvposip.setVisibility(View.GONE); 
                    txtposip.setVisibility(View.GONE);
                    tvposipport.setVisibility(View.GONE); 
                    txtposipport.setVisibility(View.GONE);
                    switchisssl.setVisibility(View.GONE);
                    tvcolumn2.setVisibility(View.GONE); 
                    txtcolumn2.setVisibility(View.GONE);
                    btnDel.setVisibility(View.GONE);
                    btnDelImg.setVisibility(View.GONE);
                    btnDelads.setVisibility(View.GONE);
				}
            }
        });
        btnDel.setOnClickListener(new OnClickListener() {// 为取消按钮设置监听事件
            @Override
            public void onClick(View arg0) {
            	//创建警告对话框
            	Dialog alert=new AlertDialog.Builder(Login.this)
            		.setTitle("对话框")//标题
            		.setMessage("您确定要删除全部商品货道信息吗？")//表示对话框中得内容
            		.setIcon(R.drawable.ic_launcher)//设置logo
            		.setPositiveButton("删除", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
            			{				
        	    				@Override
        	    				public void onClick(DialogInterface dialog, int which) 
        	    				{
        	    					// TODO Auto-generated method stub	
        	    					// 创建InaccountDAO对象
        	    					vmc_cabinetDAO cabinetDAO = new vmc_cabinetDAO(Login.this);
        				            cabinetDAO.deteleall();// 删除
        				            //删除交易记录
        				            vmc_orderDAO orderDAO = new vmc_orderDAO(Login.this);
        				            orderDAO.deteleall();
        				            //删除所有商品图片
        				            ToolClass.deleteproductImageFile();
        				            ToolClass.addOptLog(Login.this,2,"删除全部商品货道信息");
        	    					// 弹出信息提示
        				            Toast.makeText(Login.this, "删除成功！", Toast.LENGTH_SHORT).show();						            
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
        });
        btnDelImg.setOnClickListener(new OnClickListener() {// 为取消按钮设置监听事件
            @Override
            public void onClick(View arg0) {
            	//创建警告对话框
            	Dialog alert=new AlertDialog.Builder(Login.this)
            		.setTitle("对话框")//标题
            		.setMessage("您确定要清除图片缓存吗？")//表示对话框中得内容
            		.setIcon(R.drawable.ic_launcher)//设置logo
            		.setPositiveButton("清除", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
            			{				
        	    				@Override
        	    				public void onClick(DialogInterface dialog, int which) 
        	    				{
        	    					// TODO Auto-generated method stub	        	    					
        				            //删除所有商品图片
        				            ToolClass.deleteproductImageFile();
        				            ToolClass.addOptLog(Login.this,2,"清除全部商品图片缓存");
        	    					// 弹出信息提示
        				            Toast.makeText(Login.this, "清除成功！", Toast.LENGTH_SHORT).show();						            
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
        });
        btnDelads.setOnClickListener(new OnClickListener() {// 为取消按钮设置监听事件
            @Override
            public void onClick(View arg0) {
            	//创建警告对话框
            	Dialog alert=new AlertDialog.Builder(Login.this)
            		.setTitle("对话框")//标题
            		.setMessage("您确定要删除广告缓存吗？")//表示对话框中得内容
            		.setIcon(R.drawable.ic_launcher)//设置logo
            		.setPositiveButton("删除", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
            			{				
        	    				@Override
        	    				public void onClick(DialogInterface dialog, int which) 
        	    				{
        	    					// TODO Auto-generated method stub	        	    					
        				            //删除所有商品图片
        				            ToolClass.deleteadsImageFile();
        				            ToolClass.addOptLog(Login.this,2,"删除广告缓存");
        	    					// 弹出信息提示
        				            Toast.makeText(Login.this, "删除成功！", Toast.LENGTH_SHORT).show();						            
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
        });
	}
	
	@Override
	protected void onDestroy() {
    	//退出时，返回intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}
	
}
