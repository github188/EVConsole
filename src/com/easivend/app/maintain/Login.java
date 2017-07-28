/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           Login.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        ��½ѡ�񴮿ں�ҳ��          
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
	private EditText txtlogin,txtbent,txtcolumn,txtcardcom,txtprintcom,txtextracom,txtposip,txtposipport,txtcolumn2;// ����EditText����
	private TextView tvVersion=null,tvprintcom=null,tvextracom=null,tvposip=null,tvposipport=null,tvcolumn2=null;
    private Button btnlogin,btnclose,btnGaoji,btnDel,btnDelImg,btnDelads;// ��������Button����
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
		setContentView(R.layout.login);// ���ò����ļ�
		//���ú������������Ĳ��ֲ���
		this.setRequestedOrientation(ToolClass.getOrientation());		
		txtlogin = (EditText) findViewById(R.id.txtLogin);// ��ȡ�ֽ��豸���ں��ı���
        txtbent = (EditText) findViewById(R.id.txtbent);// ��ȡ���ӹ񴮿ں��ı���
        txtcolumn = (EditText) findViewById(R.id.txtcolumn);// ��ȡ���񴮿ں��ı���
        txtcardcom = (EditText) findViewById(R.id.txtserver);// ��ȡ���������ں��ı���
        tvprintcom = (TextView) findViewById(R.id.tvprintcom);
        txtprintcom = (EditText) findViewById(R.id.txtprintcom);//��ȡ��ӡ�����ں��ı���
        tvextracom = (TextView) findViewById(R.id.tvextracom);        
        txtextracom = (EditText) findViewById(R.id.txtextracom);//��ȡ��Э�豸���ں��ı���
        tvposip = (TextView) findViewById(R.id.tvposip);        
        txtposip = (EditText) findViewById(R.id.txtposip);//��ȡ������ip�ı���
        tvposipport = (TextView) findViewById(R.id.tvposipport);        
        txtposipport = (EditText) findViewById(R.id.txtposipport);//��ȡ�������˿��ı���
        switchisssl= (Switch) findViewById(R.id.switchisssl);//�Ƿ��ssl����
        tvcolumn2 = (TextView) findViewById(R.id.tvcolumn2);        
        txtcolumn2 = (EditText) findViewById(R.id.txtcolumn2);//��ȡ������/�������񴮿ں�
        tvVersion = (TextView) findViewById(R.id.tvVersion);//�����汾��
        btnDel = (Button) findViewById(R.id.btnDel);// ��ȡ���ȫ����Ʒ������Ϣ
        btnDelImg = (Button) findViewById(R.id.btnDelImg);// ��ȡ�����ƷͼƬ������Ϣ
        btnDelads = (Button) findViewById(R.id.btnDelads);// ��ȡ�����滺����Ϣ
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
        tvVersion.setText("�汾�ţ�"+ToolClass.getVersion());
        Map<String, String> list=ToolClass.ReadConfigFile();
        if(list!=null)
        {	        
	        if(list.containsKey("com"))//�����ֽ𴮿ں�
	        {
	        	com = list.get("com");
	        }
			if(list.containsKey("bentcom"))//���ø��ӹ񴮿ں�
	        {
				bentcom = list.get("bentcom");
	        }
			if(list.containsKey("columncom"))//�������񴮿ں�
	        {
				columncom = list.get("columncom");
	        }			
			if(list.containsKey("cardcom"))//���ö��������ں�
	        {
	        	cardcom = list.get("cardcom");
	        }	
			if(list.containsKey("printcom"))//���ô�ӡ�����ں�
	        {
	        	printcom = list.get("printcom");
	        }
			if(list.containsKey("extracom"))//������Э���ں�
	        {
	        	extracom = list.get("extracom");	
	        }
			if(list.containsKey("posip"))//���ö�����ip
	        {
	        	posip = list.get("posip");
	        }
	        if(list.containsKey("posipport"))//���ö������˿�
	        {
	        	posipport = list.get("posipport");
	        }
	        if(list.containsKey("isallopen"))//���ø��񴮿ں�
	        {
				columncom2 = list.get("isallopen");	
	        }
	        if(list.containsKey("posisssl"))//����ssl����
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
        btnlogin = (Button) findViewById(R.id.btnLogin);// ��ȡ�޸İ�ť
        btnclose = (Button) findViewById(R.id.btnClose);// ��ȡȡ����ť
        btnGaoji = (Button) findViewById(R.id.btnGaoji);// ��ȡ�߼���ť
        btnclose.setOnClickListener(new OnClickListener() {// Ϊȡ����ť���ü����¼�
            @Override
            public void onClick(View arg0) {
                finish();// �˳���ǰ����
            }
        });
        btnlogin.setOnClickListener(new OnClickListener() {// Ϊ��¼��ť���ü����¼�
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
            	ToolClass.addOptLog(Login.this,1,"�޸Ĵ���:");
	            // ������Ϣ��ʾ
	            Toast.makeText(Login.this, "���޸Ĵ��ڡ��ɹ���", Toast.LENGTH_SHORT).show();
	            //�˳�ʱ������intent
	            Intent intent=new Intent();
	            setResult(MaintainActivity.RESULT_OK,intent);
            	finish();// �˳���ǰ����           
            }
        });
        btnGaoji.setOnClickListener(new OnClickListener() {// Ϊȡ����ť���ü����¼�
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
        btnDel.setOnClickListener(new OnClickListener() {// Ϊȡ����ť���ü����¼�
            @Override
            public void onClick(View arg0) {
            	//��������Ի���
            	Dialog alert=new AlertDialog.Builder(Login.this)
            		.setTitle("�Ի���")//����
            		.setMessage("��ȷ��Ҫɾ��ȫ����Ʒ������Ϣ��")//��ʾ�Ի����е�����
            		.setIcon(R.drawable.ic_launcher)//����logo
            		.setPositiveButton("ɾ��", new DialogInterface.OnClickListener()//�˳���ť���������ü����¼�
            			{				
        	    				@Override
        	    				public void onClick(DialogInterface dialog, int which) 
        	    				{
        	    					// TODO Auto-generated method stub	
        	    					// ����InaccountDAO����
        	    					vmc_cabinetDAO cabinetDAO = new vmc_cabinetDAO(Login.this);
        				            cabinetDAO.deteleall();// ɾ��
        				            //ɾ�����׼�¼
        				            vmc_orderDAO orderDAO = new vmc_orderDAO(Login.this);
        				            orderDAO.deteleall();
        				            //ɾ��������ƷͼƬ
        				            ToolClass.deleteproductImageFile();
        				            ToolClass.addOptLog(Login.this,2,"ɾ��ȫ����Ʒ������Ϣ");
        	    					// ������Ϣ��ʾ
        				            Toast.makeText(Login.this, "ɾ���ɹ���", Toast.LENGTH_SHORT).show();						            
        				            finish();
        	    				}
            		      }
            			)		    		        
        		        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener()//ȡ����ť���������ü����¼�
        		        	{			
        						@Override
        						public void onClick(DialogInterface dialog, int which) 
        						{
        							// TODO Auto-generated method stub				
        						}
        		        	}
        		        )
        		        .create();//����һ���Ի���
        		        alert.show();//��ʾ�Ի���
            }
        });
        btnDelImg.setOnClickListener(new OnClickListener() {// Ϊȡ����ť���ü����¼�
            @Override
            public void onClick(View arg0) {
            	//��������Ի���
            	Dialog alert=new AlertDialog.Builder(Login.this)
            		.setTitle("�Ի���")//����
            		.setMessage("��ȷ��Ҫ���ͼƬ������")//��ʾ�Ի����е�����
            		.setIcon(R.drawable.ic_launcher)//����logo
            		.setPositiveButton("���", new DialogInterface.OnClickListener()//�˳���ť���������ü����¼�
            			{				
        	    				@Override
        	    				public void onClick(DialogInterface dialog, int which) 
        	    				{
        	    					// TODO Auto-generated method stub	        	    					
        				            //ɾ��������ƷͼƬ
        				            ToolClass.deleteproductImageFile();
        				            ToolClass.addOptLog(Login.this,2,"���ȫ����ƷͼƬ����");
        	    					// ������Ϣ��ʾ
        				            Toast.makeText(Login.this, "����ɹ���", Toast.LENGTH_SHORT).show();						            
        				            finish();
        	    				}
            		      }
            			)		    		        
        		        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener()//ȡ����ť���������ü����¼�
        		        	{			
        						@Override
        						public void onClick(DialogInterface dialog, int which) 
        						{
        							// TODO Auto-generated method stub				
        						}
        		        	}
        		        )
        		        .create();//����һ���Ի���
        		        alert.show();//��ʾ�Ի���
            }
        });
        btnDelads.setOnClickListener(new OnClickListener() {// Ϊȡ����ť���ü����¼�
            @Override
            public void onClick(View arg0) {
            	//��������Ի���
            	Dialog alert=new AlertDialog.Builder(Login.this)
            		.setTitle("�Ի���")//����
            		.setMessage("��ȷ��Ҫɾ����滺����")//��ʾ�Ի����е�����
            		.setIcon(R.drawable.ic_launcher)//����logo
            		.setPositiveButton("ɾ��", new DialogInterface.OnClickListener()//�˳���ť���������ü����¼�
            			{				
        	    				@Override
        	    				public void onClick(DialogInterface dialog, int which) 
        	    				{
        	    					// TODO Auto-generated method stub	        	    					
        				            //ɾ��������ƷͼƬ
        				            ToolClass.deleteadsImageFile();
        				            ToolClass.addOptLog(Login.this,2,"ɾ����滺��");
        	    					// ������Ϣ��ʾ
        				            Toast.makeText(Login.this, "ɾ���ɹ���", Toast.LENGTH_SHORT).show();						            
        				            finish();
        	    				}
            		      }
            			)		    		        
        		        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener()//ȡ����ť���������ü����¼�
        		        	{			
        						@Override
        						public void onClick(DialogInterface dialog, int which) 
        						{
        							// TODO Auto-generated method stub				
        						}
        		        	}
        		        )
        		        .create();//����һ���Ի���
        		        alert.show();//��ʾ�Ի���
            }
        });
	}
	
	@Override
	protected void onDestroy() {
    	//�˳�ʱ������intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}
	
}
