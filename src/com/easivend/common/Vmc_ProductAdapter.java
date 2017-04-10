/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           Vmc_ProductAdapter.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        ��ȡ��Ʒ���е�������Ʒ��Ϣ�����䵽��Ʒ���ݽṹ������   
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.model.Tb_vmc_product;

public class Vmc_ProductAdapter
{	 
	 private String[] proID = null;
	 private String[] productID = null;
	 private String[] productName = null;
     private String[] proImage = null;
     private String[] promarket = null;
     private String[] prosales = null;
     private String[] procount = null;
     List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
     
     // ��Ʒ���е�������Ʒ��Ϣ���䵽��Ʒ���ݽṹ������
     //param��������,sort��������,classID������Ϣ
    public void showProInfo(Context context,String param,String sort,String classID) 
 	{
 	    //ArrayAdapter<String> arrayAdapter = null;// ����ArrayAdapter����
 	    List<Tb_vmc_product> listinfos=null;//���ݱ�list�༯
 	    // ����InaccountDAO�������ڴ����ݿ�����ȡ���ݵ�Tb_vmc_product����
 	    vmc_productDAO productdao = new vmc_productDAO(context);
 	    //�޹���������ѯ
 	    if(ToolClass.isEmptynull(param)==true)
 	    {
 	    	//�����ѯ
 	    	if(ToolClass.isEmptynull(classID)!=true)
 	    	{
 			    // ��ȡ����������Ϣ�����洢��List���ͼ�����
 			    listinfos = productdao.getScrollData(classID);
 		    }	
 	    	else
 	    	{
	 		    // ��ȡ����������Ϣ�����洢��List���ͼ�����
	 		    listinfos = productdao.getScrollData(0, (int) productdao.getCount(),sort);
 	    	}
 	    }
 	    //�й���������ѯ
 	    else 
 	    {
 		    // ��ȡ����������Ϣ�����洢��List���ͼ�����
 		    listinfos = productdao.getScrollData(param,sort);
 	    }
 	    proID = new String[listinfos.size()];// �����ַ�������ĳ���
 	    productID = new String[listinfos.size()];// �����ַ�������ĳ���
 	    productName = new String[listinfos.size()];// �����ַ�������ĳ���
 	    proImage = new String[listinfos.size()];// �����ַ�������ĳ���
 	    promarket = new String[listinfos.size()];// �����ַ�������ĳ���
 	    prosales = new String[listinfos.size()];// �����ַ�������ĳ���
 	    procount = new String[listinfos.size()];// �����ַ�������ĳ���
 	    int m = 0;// ����һ����ʼ��ʶ 	
 	    list.clear();
 	    // ����List���ͼ���
 	    for (Tb_vmc_product tb_inaccount : listinfos) 
 	    {
 	    	Map<String,Object> map=new HashMap<String, Object>();
 	        // �����������Ϣ��ϳ�һ���ַ������洢���ַ����������Ӧλ��
 	    	map.put("proID", tb_inaccount.getProductID()+"-"+tb_inaccount.getProductName());
 	    	map.put("productID", tb_inaccount.getProductID());
 	    	map.put("productName", tb_inaccount.getProductName());
 	    	map.put("proImage", tb_inaccount.getAttBatch1());
 	    	map.put("promarket", String.valueOf(tb_inaccount.getMarketPrice()));
 	    	map.put("prosales", String.valueOf(tb_inaccount.getSalesPrice()));
 	    	//�õ������Ʒid��Ӧ�Ĵ������
	    	if(tb_inaccount.getProductID()!=null)
	    	{
	    		vmc_columnDAO columnDAO = new vmc_columnDAO(context);// ����InaccountDAO����
    		    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    		    map.put("procount", String.valueOf(columnDAO.getproductCount(tb_inaccount.getProductID())));
	    	}	    		    	
	    	list.add(map); 	    	    	
 	    }
 	    try {
			list=ToolClass.listSort(list);
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��Ʒsort="+list.toString(),"log.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	    for(Map<String,Object> map:list)  
        {  
 	        // �����������Ϣ��ϳ�һ���ַ������洢���ַ����������Ӧλ��
 	    	proID[m] = map.get("proID").toString();
 	    	productID[m] = map.get("productID").toString();
 	    	productName[m] = map.get("productName").toString();
 	    	proImage[m] = map.get("proImage").toString();
 	    	promarket[m] = map.get("promarket").toString();
 	    	prosales[m] = map.get("prosales").toString();
 	    	procount[m] = map.get("procount").toString();
 	    	m++;// ��ʶ��1 	 	    	
        } 
 	    
 	}
	public String[] getProID() {
		return proID;
	}
	public void setProID(String[] proID) {
		this.proID = proID;
	}
	public String[] getProductID() {
		return productID;
	}
	public void setProductID(String[] productID) {
		this.productID = productID;
	}
	public String[] getProImage() {
		return proImage;
	}
	public void setProImage(String[] proImage) {
		this.proImage = proImage;
	}
	public String[] getPromarket() {
		return promarket;
	}
	public void setPromarket(String[] promarket) {
		this.promarket = promarket;
	}
	public String[] getProsales() {
		return prosales;
	}
	public void setProsales(String[] prosales) {
		this.prosales = prosales;
	}
	public String[] getProcount() {
		return procount;
	}
	public void setProcount(String[] procount) {
		this.procount = procount;
	}
	public String[] getProductName() {
		return productName;
	}
	public void setProductName(String[] productName) {
		this.productName = productName;
	}
    
}
