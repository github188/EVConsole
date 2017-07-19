package com.easivend.fragment;

import java.util.List;

import com.easivend.app.business.BusPort;
import com.easivend.app.business.BusPort.BusPortFragInteraction;
import com.easivend.common.AudioSound;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_columnDAO;
import com.example.evconsole.R;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class BushuoFragment extends Fragment
{
	private String proID = null;
	private String productID = null;
	private String proType = null;
	private String cabID = null;
	private String huoID = null;
    private float prosales = 0;
    private int count = 0;
    private int zhifutype = 0;//0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��
    private TextView txtbushuoname = null;
    private ImageView ivbushuoquhuo=null,imgbtnbusgoodsback=null;
    private int tempx=0;
    private int cabinetvar=0,huodaoNo=0,cabinetTypevar=0;
    private vmc_columnDAO columnDAO =null; 
    //�������
    private int status=0;//�������	
    private Context context;
    //=========================
    //fragment��activity�ص����
    //=========================
    /**
     * �������ⲿactivity������
     */
    private BushuoFragInteraction listterner;
    /**
     * �����ġ���ContentFragment�����ص�activity��ʱ������ע��ص���Ϣ
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof BushuoFragInteraction)
        {
            listterner = (BushuoFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements BushuoFragInteraction");
        }

    }
    /**
     * ����һ������������activity����ʵ�ֵĽӿ�
     */
    public interface BushuoFragInteraction
    {
        /**
         * Fragment ��Activity����ָ�����������Ը�������������
         * @param str
         */
        void BushuoChuhuoOpt(int cabinetvar,int huodaoNo,int cabinetTypevar);      //���ͳ���ָ��
        void BushuoFinish(int status);      //��������ҳ��
        void BushuoNow();      //���Ͻ���ҳ��
    }
    @Override
    public void onDetach() {
        super.onDetach();

        listterner = null;
    }
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bushuo, container, false);  
		context=this.getActivity();//��ȡactivity��context
		
		//����Ʒҳ����ȡ����ѡ�е���Ʒ
//		Intent intent=getIntent();
//		Bundle bundle=intent.getExtras();
		proID=OrderDetail.getProID();
		productID=OrderDetail.getProductID();
		proType=OrderDetail.getProType();
		cabID=OrderDetail.getCabID();
		huoID=OrderDetail.getColumnID();
		prosales=OrderDetail.getShouldPay();//��Ʒ����
		count=OrderDetail.getShouldNo();//����
		zhifutype=OrderDetail.getPayType();
		txtbushuoname=(TextView)view.findViewById(R.id.txtbushuoname);
		
  	    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��ƷorderID="+OrderDetail.getOrdereID()+"proID="+proID+" productID="
				+productID+" proType="
				+proType+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
				+count+" zhifutype="+zhifutype,"log.txt");		
		this.ivbushuoquhuo =(ImageView) view.findViewById(R.id.ivbushuoquhuo);
		Bitmap bitmap=ToolClass.ReadAdshuoFile();
	    if(bitmap!=null)
	    {
	    	this.ivbushuoquhuo.setImageBitmap(bitmap);// ����ͼ��Ķ�����ֵ
	    }
	    else
	    {
	    	ivbushuoquhuo.setImageResource(R.drawable.chuwait);
	    }
		
		//****
		//����
		//****
		chuhuoopt(tempx);
		/**
	     * ����������fragment������,
	     * �����塢��Fragment�����ص�activity��ʱ��ע��ص���Ϣ
	     * @param activity
	     */
		BusPort.setCallBack(new buportInterfaceImp());
		this.imgbtnbusgoodsback =(ImageView) view.findViewById(R.id.imgbtnbusgoodsback);
		imgbtnbusgoodsback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//�����ɹ�
				if(status==1)
				{
					listterner.BushuoNow();//�˳�ҳ��
				}
			}
		});
		return view;
	}
    
    //����,����ֵ0ʧ��,1����ָ��ɹ����ȴ����ؽ��,2�������
  	private void chuhuoopt(int huox)
  	{
  		// ����InaccountDAO�������ڴ����ݿ�����ȡ���ݵ�Tb_vmc_column����
   	    columnDAO = new vmc_columnDAO(context);
   	    //txtbushuoname.setText(proID+"["+prosales+"]"+"->���ڳ���,���Ժ�...");
  		//1.�������������
  		//����Ʒid����
  		if(proType.equals("1")==true)
  		{
  	 	    // ��ȡ����������Ϣ�����洢��Map������
  			List<String> alllist = columnDAO.getproductColumn(productID);
  			cabinetvar=Integer.parseInt(alllist.get(0));
  			huodaoNo=Integer.parseInt(alllist.get(1));
  			cabinetTypevar=Integer.parseInt(alllist.get(2));
  			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<[1]��ƷcabID="+cabinetvar+"huoID="+huodaoNo+"cabType="+cabinetTypevar,"log.txt"); 
  		}
  		//������id����
  		else if(proType.equals("2")==true)
  		{
  	 	    // ��ȡ����������Ϣ�����洢��Map������
  			String alllist = columnDAO.getcolumnType(cabID);
  			cabinetvar=Integer.parseInt(cabID);
  			huodaoNo=Integer.parseInt(huoID);
  			cabinetTypevar=Integer.parseInt(alllist);
  			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<[2]��ƷcabID="+cabinetvar+"huoID="+huodaoNo+"cabType="+cabinetTypevar,"log.txt"); 
  		}
  		listterner.BushuoChuhuoOpt(cabinetvar,huodaoNo,cabinetTypevar);//�������fragment��activity���ͻص���Ϣ  		
  	}
  	//�޸Ĵ������
  	private void chuhuoupdate(int cabinetvar,int huodaoNo)
  	{
  		String cab=null,huo=null;
  		cab=String.valueOf(cabinetvar);
  		//����id=1��9����Ϊ01��09
          if(huodaoNo<10)
          {
          	huo="0"+String.valueOf(huodaoNo);
          }
          else
          {
          	huo=String.valueOf(huodaoNo);
          }	
          //�۳��������
  		columnDAO.update(cab,huo);		
  	}
  	
  	//��¼��־�������type=1�����ɹ���0����ʧ��
  	private void chuhuoLog(int type)
  	{
  		OrderDetail.setYujiHuo(1);
  		OrderDetail.setCabID(String.valueOf(cabinetvar));
  		OrderDetail.setColumnID(String.valueOf(huodaoNo));
  		if(type==1)//�����ɹ�
  		{
  			OrderDetail.setPayStatus(0);
  			OrderDetail.setRealHuo(1);
  			OrderDetail.setHuoStatus(0);
  		}
  		else//����ʧ��
  		{
  			OrderDetail.setPayStatus(1);
  			OrderDetail.setRealHuo(0);
  			OrderDetail.setHuoStatus(1);
  		}
  	}
  	
  	private class buportInterfaceImp implements BusPortFragInteraction//���ؽӿ�
	{
		/**
	     * ����������fragment������,
	     * ��������ʵ��BusPortFragInteraction�ӿ�
	     * @param activity
	     */
		@Override
		public void BusportTsxx(String str) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportTbje(String str) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportChjg(int sta) {
			// TODO Auto-generated method stub
			status=sta;//�������	
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<Fragment�������"+"device=["+cabinetvar+"],hdid=["+huodaoNo+"],status=["+status+"]","log.txt");	
			//1.���³������
			//������������ģ��ſ۳��������
			if(OrderDetail.getPayType()!=5)
			{
				//�۳��������
				chuhuoupdate(cabinetvar,huodaoNo);
			}
			//�����ɹ�
			if(status==1)
			{
				txtbushuoname.setText(proID+"["+prosales+"]"+"->������ɣ��뵽"+cabinetvar+"��"+huodaoNo+"����ȡ��Ʒ");
				txtbushuoname.setTextColor(android.graphics.Color.BLUE);
				chuhuoLog(1);//��¼��־
				ivbushuoquhuo.setImageResource(R.drawable.chusuccess);
				//���ӹ�
				if(cabinetTypevar==5)
				{
					AudioSound.playbushuogezi();
				}
				//��ͨ��
				else
				{
					AudioSound.playbushuotang();
				}
			}
			else
			{
				txtbushuoname.setText(proID+"["+prosales+"]"+"->"+cabinetvar+"��"+huodaoNo+"��������ʧ�ܣ���û�б��ۿ�!");
				txtbushuoname.setTextColor(android.graphics.Color.RED);
				chuhuoLog(0);//��¼��־
				ivbushuoquhuo.setImageResource(R.drawable.chufail);
			}					
			//3.�˻�����ҳ��
			listterner.BushuoFinish(status);//�������fragment��activity���ͻص���Ϣ 
// 	    	new Handler().postDelayed(new Runnable() 
//			{
//                @Override
//                public void run() 
//                {	   
//                	//�˳�ʱ������intent
//    	            Intent intent=new Intent();
//    	            intent.putExtra("status", status);//�������
//                	if(zhifutype==0)//�ֽ�֧��
//                	{                        			
//        	            setResult(BusZhiAmount.RESULT_CANCELED,intent);                    	            
//            		}
//                	else if(zhifutype==3)//֧������ά��
//                	{
//        	            setResult(BusZhier.RESULT_CANCELED,intent);                    	            
//            		}
//                	else if(zhifutype==4)//΢��ɨ��
//                	{                        			
//        	            setResult(BusZhiwei.RESULT_CANCELED,intent);                    	            
//            		}
//                	finish();
////                	//�������,�ѷ��ֽ�ģ��ȥ��
////                	if(status==0)
////                	{
////                		if(BusZhier.BusZhierAct!=null)
////                			BusZhier.BusZhierAct.finish(); 
////                		if(BusZhiwei.BusZhiweiAct!=null)
////                			BusZhiwei.BusZhiweiAct.finish(); 
////                		OrderDetail.addLog(BusHuo.this);
////                	}
////                	//����ʧ�ܣ��˵����ֽ�ģ������˱Ҳ���
////                	else
////                	{
////                		if(BusZhier.BusZhierAct!=null)
////                		{
////                			//�˳�ʱ������intent
////            	            Intent intent=new Intent();
////            	            setResult(BusZhier.RESULT_CANCELED,intent);
////                		}
////                		if(BusZhiwei.BusZhiweiAct!=null)
////                		{
////                			//�˳�ʱ������intent
////            	            Intent intent=new Intent();
////            	            setResult(BusZhiwei.RESULT_CANCELED,intent);
////                		}
////					}		                        	
//                    
//                }
//
//			}, SPLASH_DISPLAY_LENGHT);
		}

		@Override
		public void BusportSend(String str) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportSendWei(String str) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void BusportSendYinlian(String str) {
			// TODO Auto-generated method stub
		}
	}
}
