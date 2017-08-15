/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_columnDAO ����������ļ�  
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.dao;

import java.util.ArrayList;
import java.util.List;

import com.easivend.common.ToolClass;
import com.easivend.model.Tb_vmc_column;
import com.easivend.model.Tb_vmc_product;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class vmc_columnDAO 
{
	private DBOpenHelper helper;// ����DBOpenHelper����
    private SQLiteDatabase db;// ����SQLiteDatabase����
    //SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    // ���幹�캯��
	public vmc_columnDAO(Context context) 
	{
		helper=new DBOpenHelper(context);// ��ʼ��DBOpenHelper����
	}
	
	//�޸Ļ��������
	public void updatetihuo(Tb_vmc_column tb_vmc_column)throws SQLException
	{
		db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
		//�Ƿ��Ѿ����ڱ���Ʒ
        Cursor cursor = db.rawQuery("select columnID from vmc_column where cabID = ? and columnID=?", 
        		new String[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID() });// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
        // ����һ������
	    db.beginTransaction();
	    try {
	        if (cursor.moveToNext()) // �������ҵ���֧����Ϣ
	        {
	        	//ִ���޸Ļ�������
		 		db.execSQL(
					"update vmc_column set " +
					"tihuoPwd=?, " +
					"lasttime=(datetime('now', 'localtime')) " +
					"where cabID=? and columnID=?",
			        new Object[] { tb_vmc_column.getTihuoPwd(),
							tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID()});
			
			}	    
	        // ��������ı�־Ϊ�ɹ������������setTransactionSuccessful() ������Ĭ�ϻ�ع�����
		    db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // ��������ı�־�Ƿ�Ϊ�ɹ������Ϊ�ɹ����ύ���񣬷���ع�����
	        db.endTransaction();
	        if (!cursor.isClosed()) 
	 		{  
	 			cursor.close();  
	 		}  
	 		db.close(); 
	    }
        
 		 
	}
	//��ӻ��޸Ļ�������
	public void addorupdate(Tb_vmc_column tb_vmc_column)throws SQLException
	{
		db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
		//�Ƿ��Ѿ����ڱ���Ʒ
        Cursor cursor = db.rawQuery("select columnID from vmc_column where cabID = ? and columnID=?", 
        		new String[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID() });// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
        // ����һ������
	    db.beginTransaction();
	    try {
	        if (cursor.moveToNext()) // �������ҵ���֧����Ϣ
	        {
	        	//ִ���޸Ļ�������
		 		db.execSQL(
					"update vmc_column set " +
					"tihuoPwd=?,productID=?,pathCount=?,pathRemain=?,columnStatus=?," +
					"lasttime=(datetime('now', 'localtime')),isupload=0 " +
					"where cabID=? and columnID=?",
			        new Object[] { tb_vmc_column.getTihuoPwd(),tb_vmc_column.getProductID(),tb_vmc_column.getPathCount(),
							tb_vmc_column.getPathRemain(),tb_vmc_column.getColumnStatus(),
							tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID()});
			
			}	      
	        else
	        {	
	        	// ִ����ӻ���		
	     		db.execSQL(
	     				"insert into vmc_column" +
	     				"(" +
	     				"cabID,columnID,productID,pathCount,pathRemain," +
	     				"columnStatus,lasttime,isupload,tihuoPwd" +
	     				") " +
	     				"values" +
	     				"(" +
	     				"?,?,?,?,?,?,(datetime('now', 'localtime')),?,?" +
	     				")",
	     		        new Object[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID(),tb_vmc_column.getProductID(),
	     						tb_vmc_column.getPathCount(),tb_vmc_column.getPathRemain(),tb_vmc_column.getColumnStatus(),
	     						tb_vmc_column.getIsupload(),tb_vmc_column.getTihuoPwd()});
	     		
	        }
        
	        // ��������ı�־Ϊ�ɹ������������setTransactionSuccessful() ������Ĭ�ϻ�ع�����
		    db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // ��������ı�־�Ƿ�Ϊ�ɹ������Ϊ�ɹ����ύ���񣬷���ع�����
	        db.endTransaction();
	        if (!cursor.isClosed()) 
	 		{  
	 			cursor.close();  
	 		}  
	 		db.close(); 
	    }
        
 		 
	}
	//Ϊ�������豸������ӻ��޸Ļ�������
	public void addorupdateforserver(Tb_vmc_column tb_vmc_column)throws SQLException
	{
		db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
		//�Ƿ��Ѿ����ڱ���Ʒ
        Cursor cursor = db.rawQuery("select columnID from vmc_column where cabID = ? and columnID=?", 
        		new String[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID() });// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
        // ����һ������
	    db.beginTransaction();
	    try {
	        if (cursor.moveToNext()) // �������ҵ���֧����Ϣ
	        {
	        	//ִ���޸Ļ�������
		 		db.execSQL(
					"update vmc_column set " +
					"productID=?,pathCount=?,path_id=?," +
					"isupload=0 " +
					"where cabID=? and columnID=?",
			        new Object[] { tb_vmc_column.getProductID(),tb_vmc_column.getPathCount(),
							tb_vmc_column.getPath_id(),
							tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID()});
			
			}	      
	        else
	        {	
	        	// ִ����ӻ���		
	     		db.execSQL(
	     				"insert into vmc_column" +
	     				"(" +
	     				"cabID,columnID,productID,pathCount,pathRemain," +
	     				"columnStatus,lasttime,path_id,isupload" +
	     				") " +
	     				"values" +
	     				"(" +
	     				"?,?,?,?,?,?,(datetime('now', 'localtime')),?,?" +
	     				")",
	     		        new Object[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID(),tb_vmc_column.getProductID(),
	     						tb_vmc_column.getPathCount(),0,3,tb_vmc_column.getPath_id(),tb_vmc_column.getIsupload()});
	     		
	        }
        
	        // ��������ı�־Ϊ�ɹ������������setTransactionSuccessful() ������Ĭ�ϻ�ع�����
		    db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // ��������ı�־�Ƿ�Ϊ�ɹ������Ϊ�ɹ����ύ���񣬷���ع�����
	        db.endTransaction();
	        if (!cursor.isClosed()) 
	 		{  
	 			cursor.close();  
	 		}  
	 		db.close(); 
	    }
	}
	//ɾ������
	public void detele(Tb_vmc_column tb_vmc_column) 
	{       
        db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        // ����һ������
	    db.beginTransaction();
	    try {
	        // ִ��ɾ����Ʒ��
	        db.execSQL("delete from vmc_column where cabID=? and columnID=?", 
	        		new Object[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID()});        

	        // ��������ı�־Ϊ�ɹ������������setTransactionSuccessful() ������Ĭ�ϻ�ع�����
	        db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // ��������ı�־�Ƿ�Ϊ�ɹ������Ϊ�ɹ����ύ���񣬷���ع�����
	        db.endTransaction();
	        db.close(); 
	    }
	}
	//ɾ���ù�ȫ��������Ϣ
  	public void deteleCab(String cabID) 
  	{       
          db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        // ����һ������
  	    db.beginTransaction();
  	    try {
          // ִ��ɾ���ù���Ʒ��
          db.execSQL("delete from vmc_column where cabID=?", 
          		new Object[] { cabID});    
          
          // ��������ı�־Ϊ�ɹ������������setTransactionSuccessful() ������Ĭ�ϻ�ع�����
	      db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // ��������ı�־�Ƿ�Ϊ�ɹ������Ϊ�ɹ����ύ���񣬷���ع�����
	        db.endTransaction();
	        db.close(); 
	    }
  	}	
    //�����ù�ȫ�������������
  	public void buhuoCab(String cabID) 
  	{       
            db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
            // ����һ������
    	    db.beginTransaction();
    	    try {
	          // ִ�в����ù������
	          db.execSQL("update vmc_column set pathRemain=pathCount,columnStatus=1,isupload=0,lasttime=(datetime('now', 'localtime')) where cabID=? and columnStatus<>2 ", 
	          		new Object[] { cabID});    
          
	          // ��������ı�־Ϊ�ɹ������������setTransactionSuccessful() ������Ĭ�ϻ�ع�����
		      db.setTransactionSuccessful();
		    } catch (Exception e) {
		        // process it
		        e.printStackTrace();
		    } finally {
		        // ��������ı�־�Ƿ�Ϊ�ɹ������Ϊ�ɹ����ύ���񣬷���ع�����
		        db.endTransaction();
		        db.close(); 
		    }
  	}
    //��ոù�ȫ�������������
  	public void clearhuoCab(String cabID) 
  	{       
            db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
            // ����һ������
    	    db.beginTransaction();
    	    try {
	          // ִ�в����ù������
	          db.execSQL("update vmc_column set pathRemain=0,columnStatus=3,isupload=0 where cabID=? and columnStatus<>2 ", 
	          		new Object[] { cabID});    
          
	          // ��������ı�־Ϊ�ɹ������������setTransactionSuccessful() ������Ĭ�ϻ�ع�����
		      db.setTransactionSuccessful();
		    } catch (Exception e) {
		        // process it
		        e.printStackTrace();
		    } finally {
		        // ��������ı�־�Ƿ�Ϊ�ɹ������Ϊ�ɹ����ύ���񣬷���ع�����
		        db.endTransaction();
		        db.close(); 
		    }
  	}
	/**
     * ����һ��������Ϣ
     * 
     * @param id
     * @return
     */
    public Tb_vmc_column find(String cabID,String columnID) 
    {
    	Tb_vmc_column tb_vmc_column=null;
        db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        Cursor cursor = db.rawQuery("select cabID,columnID,productID,pathCount,pathRemain," +
            		"columnStatus,lasttime,path_id,isupload,tihuoPwd from vmc_column where cabID=? and columnID=?", 
            		new String[] { cabID,columnID });// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
        if (cursor.moveToNext()) {// �������ҵ���֧����Ϣ

            // ����������֧����Ϣ�洢��Tb_outaccount����
        	tb_vmc_column=new Tb_vmc_column(
    				cursor.getString(cursor.getColumnIndex("cabID")), cursor.getString(cursor.getColumnIndex("columnID")),
    				cursor.getString(cursor.getColumnIndex("tihuoPwd")),
    				cursor.getString(cursor.getColumnIndex("productID")),cursor.getInt(cursor.getColumnIndex("pathCount")),
    				cursor.getInt(cursor.getColumnIndex("pathRemain")),cursor.getInt(cursor.getColumnIndex("columnStatus")),
    				cursor.getString(cursor.getColumnIndex("lasttime")),cursor.getInt(cursor.getColumnIndex("path_id")),
    				cursor.getInt(cursor.getColumnIndex("isupload"))
    		);
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close();
        return tb_vmc_column;// ���û����Ϣ���򷵻�null
    }
    
    /**
     * ��������δ�ϱ��Ļ�����Ϣ
     * 
     * @param id
     * @return
     */
    public List<Tb_vmc_column> getScrollPay() 
    {
    	List<Tb_vmc_column> tb_vmc_column = new ArrayList<Tb_vmc_column>();// �������϶���
        db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        Cursor cursor = db.rawQuery("select cabID,columnID,productID,pathCount,pathRemain," +
            		"columnStatus,lasttime,path_id,isupload,tihuoPwd from vmc_column where isupload <> 1 ", null);// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
        while (cursor.moveToNext()) {// �������ҵ���֧����Ϣ

            // ����������֧����Ϣ�洢��Tb_outaccount����
        	tb_vmc_column.add(new Tb_vmc_column
        			(
    				cursor.getString(cursor.getColumnIndex("cabID")), cursor.getString(cursor.getColumnIndex("columnID")),
    				cursor.getString(cursor.getColumnIndex("tihuoPwd")),
    				cursor.getString(cursor.getColumnIndex("productID")),cursor.getInt(cursor.getColumnIndex("pathCount")),
    				cursor.getInt(cursor.getColumnIndex("pathRemain")),cursor.getInt(cursor.getColumnIndex("columnStatus")),
    				cursor.getString(cursor.getColumnIndex("lasttime")),cursor.getInt(cursor.getColumnIndex("path_id")),
    				cursor.getInt(cursor.getColumnIndex("isupload"))
    				)
    		);
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close();
        return tb_vmc_column;// ���û����Ϣ���򷵻�null
    }
    /**
     * �ϱ��ɹ����޸����ϱ�״̬
     * 
     * @return
     */
  	public void updatecol(String cabID,String columnID) 
  	{       
          db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����     
       // ����һ������
  	    db.beginTransaction();
  	    try {
          // ִ��ɾ����Ʒ��
          db.execSQL("update [vmc_column] set isupload=1 where cabID=? and columnID=?", 
            		new String[] { cabID,columnID });        

          // ��������ı�־Ϊ�ɹ������������setTransactionSuccessful() ������Ĭ�ϻ�ع�����
	      db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // ��������ı�־�Ƿ�Ϊ�ɹ������Ϊ�ɹ����ύ���񣬷���ع�����
	        db.endTransaction();
	        db.close(); 
	    }
  	}  
    
    /**
     * ��ȡ����������Ʒ��Ϣ
     * 
     * @param cabID
     *            ���    
     * @return
     */
    public List<Tb_vmc_column> getScrollData(String cabID) 
    {
        List<Tb_vmc_column> tb_inaccount = new ArrayList<Tb_vmc_column>();// �������϶���
        db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        Cursor cursor = db.rawQuery("select cabID,columnID,productID,pathCount,pathRemain," +
        		"columnStatus,lasttime,path_id,isupload,tihuoPwd from vmc_column where cabID=? ", 
        		new String[] { cabID});// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
    
        //�������е�������Ϣ
        while (cursor.moveToNext()) 
        {	
            // ����������������Ϣ��ӵ�������
            tb_inaccount.add(new Tb_vmc_column
        		(
        				cursor.getString(cursor.getColumnIndex("cabID")), cursor.getString(cursor.getColumnIndex("columnID")),
        				cursor.getString(cursor.getColumnIndex("tihuoPwd")),
        				cursor.getString(cursor.getColumnIndex("productID")),cursor.getInt(cursor.getColumnIndex("pathCount")),
        				cursor.getInt(cursor.getColumnIndex("pathRemain")),cursor.getInt(cursor.getColumnIndex("columnStatus")),
        				cursor.getString(cursor.getColumnIndex("lasttime")),cursor.getInt(cursor.getColumnIndex("path_id")),
        				cursor.getInt(cursor.getColumnIndex("isupload"))
        		)
           );
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close();
        return tb_inaccount;// ���ؼ���
    }
    
    /**
     * ��ȡ�������
     * 
     * @return
     */
    public int getproductCount(String productID) { 
    	int count=0;
        db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        Cursor cursor = db.rawQuery("select pathRemain from vmc_column where  productID=?", 
        		new String[] { productID});// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
        //�������е�������Ϣ
        while (cursor.moveToNext()) 
        {	
            // ����������������Ϣ��ӵ�������
        	count+=cursor.getInt(cursor.getColumnIndex("pathRemain"));           
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close();
        return count;// ���û�����ݣ��򷵻�0
    }
    
    /**
     * ��ȡ��������
     * 
     * @return
     */
    public List<String> getproductColumn(String productID) {
    	List<String> alllist=new ArrayList<String>();
    	
    	db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        Cursor cursor = db.rawQuery("select cabID,columnID from vmc_column where pathRemain>0 and productID=? order by lasttime asc", 
        		new String[] { productID});// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
        //�������е�������Ϣ
        if (cursor.moveToNext()) 
        {	
            // ����������������Ϣ��ӵ�������
        	alllist.add(cursor.getString(cursor.getColumnIndex("cabID")));//���
        	alllist.add(cursor.getString(cursor.getColumnIndex("columnID")));//������
        	Cursor cursor2 = db.rawQuery("select cabType from vmc_cabinet where cabID=?", 
            		new String[] { cursor.getString(cursor.getColumnIndex("cabID"))});// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
        	if (cursor2.moveToNext()) 
        	{
        		alllist.add(String.valueOf(cursor2.getInt(cursor2.getColumnIndex("cabType"))));//������        		
        	}
        	if (!cursor2.isClosed()) 
     		{  
     			cursor2.close();  
     		} 
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}         
 		db.close();
        return alllist;// ���û�����ݣ��򷵻�0
    }
    
    /**
     * ��ȡָ��������������Ʒ��Ϣ
     * 
     * @return
     */
    public Tb_vmc_product getColumnproduct(String cabID,String columnID) {
    	String productID=null;
    	db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        Cursor cursor = db.rawQuery("select productID from vmc_column where pathRemain>0 and cabID=? and columnID=?", 
        		new String[] { cabID,columnID});// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����    	
		
        //�������е�������Ϣ
        if (cursor.moveToNext()) 
        {	
        	productID=cursor.getString(cursor.getColumnIndex("productID"));//��ƷID
        	Cursor cursor2 = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productID = ?", new String[] { String.valueOf(productID) });// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
	        if (cursor2.moveToNext()) 
	        {// �������ҵ���֧����Ϣ
	
	            // ����������֧����Ϣ�洢��Tb_outaccount����
	        	return new Tb_vmc_product(
	    				cursor2.getString(cursor2.getColumnIndex("productID")), cursor2.getString(cursor2.getColumnIndex("productName")),
	    				cursor2.getString(cursor2.getColumnIndex("productDesc")),cursor2.getFloat(cursor2.getColumnIndex("marketPrice")),
	    				cursor2.getFloat(cursor2.getColumnIndex("salesPrice")),cursor2.getInt(cursor2.getColumnIndex("shelfLife")),
	    				cursor2.getString(cursor2.getColumnIndex("downloadTime")),cursor2.getString(cursor2.getColumnIndex("onloadTime")),
	    				cursor2.getString(cursor2.getColumnIndex("attBatch1")), cursor2.getString(cursor2.getColumnIndex("attBatch2")),
	    				cursor2.getString(cursor2.getColumnIndex("attBatch3")),cursor2.getInt(cursor2.getColumnIndex("paixu")),
	    				cursor2.getInt(cursor2.getColumnIndex("isdelete"))
	    		);
	        }
	        if (!cursor2.isClosed()) 
     		{  
     			cursor2.close();  
     		} 
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}         
 		db.close();
 		return null;// ���û�����ݣ��򷵻�0
    }
    /**
     * ��ȡָ��������������Ʒ��Ϣ,�����������Ϊ0��
     * 
     * @return
     */
    public Tb_vmc_product getColumnproductforzero(String cabID,String columnID) {
    	String productID=null;
    	db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
    	Cursor cursor = db.rawQuery("select productID from vmc_column where pathRemain=0 and cabID=? and columnID=?", 
        		new String[] { cabID,columnID});// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����    	
		
        //�������е�������Ϣ
        if (cursor.moveToNext())  
        {	
        	productID=cursor.getString(cursor.getColumnIndex("productID"));//��ƷID
        	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��ƷproductID="+productID+"����=0","log.txt");
        	
        	Cursor cursor2 = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productID = ?", new String[] { String.valueOf(productID) });// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
	        if (cursor2.moveToNext()) 
	        {// �������ҵ���֧����Ϣ
	
	            // ����������֧����Ϣ�洢��Tb_outaccount����
	        	return new Tb_vmc_product(
	    				cursor2.getString(cursor2.getColumnIndex("productID")), cursor2.getString(cursor2.getColumnIndex("productName")),
	    				cursor2.getString(cursor2.getColumnIndex("productDesc")),cursor2.getFloat(cursor2.getColumnIndex("marketPrice")),
	    				cursor2.getFloat(cursor2.getColumnIndex("salesPrice")),cursor2.getInt(cursor2.getColumnIndex("shelfLife")),
	    				cursor2.getString(cursor2.getColumnIndex("downloadTime")),cursor2.getString(cursor2.getColumnIndex("onloadTime")),
	    				cursor2.getString(cursor2.getColumnIndex("attBatch1")), cursor2.getString(cursor2.getColumnIndex("attBatch2")),
	    				cursor2.getString(cursor2.getColumnIndex("attBatch3")),cursor2.getInt(cursor2.getColumnIndex("paixu")),
	    				cursor2.getInt(cursor2.getColumnIndex("isdelete"))
	    		);
	        }
	        if (!cursor2.isClosed()) 
     		{  
     			cursor2.close();  
     		} 
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}         
 		db.close();
 		return null;// ���û�����ݣ��򷵻�0
    }
    
    /**
     * ��ȡָ�����������Ļ�������
     * 
     * @return
     */
    public String getcolumnType(String cabID) {
    	String alllist=null;    	
    	db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
    	Cursor cursor2 = db.rawQuery("select cabType from vmc_cabinet where cabID=?", 
        		new String[] { String.valueOf(cabID)});// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
    	if (cursor2.moveToNext()) 
    	{
    		alllist=String.valueOf(cursor2.getInt(cursor2.getColumnIndex("cabType")));//������        		
    	}
    	if (!cursor2.isClosed()) 
 		{  
 			cursor2.close();  
 		}                 
 		db.close();
        return alllist;// ���û�����ݣ��򷵻�0
    }
    
    /**
     * �������޸ĳ��������������
     * 
     * @return
     */
  	public void update(String cabID,String columnID) 
  	{       
          db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����          
	       // ����һ������
	  	  db.beginTransaction();
	  	  try {
	  		  Cursor cur = db.rawQuery("select pathRemain from vmc_column where cabID=? and columnID=?", 
	  				new String[] { cabID,columnID});// ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
	  		  //�������е�������Ϣ
	  		  if (cur.moveToNext()) 
	  		  {
	  			  int curpathRemain=cur.getInt(cur.getColumnIndex("pathRemain"));
	  			  if(curpathRemain>0)
	  			  {
			          // ִ��ɾ����Ʒ��
			          db.execSQL("update vmc_column set pathRemain=(pathRemain-1),isupload=0,lasttime=(datetime('now', 'localtime')) where cabID=? and columnID=?", 
			          		new Object[] { cabID,columnID});    
			          Cursor cursor = db.rawQuery("select pathRemain from vmc_column where cabID=? and columnID=?", 
			          		new String[] { cabID,columnID}); // ���ݱ�Ų���֧����Ϣ�����洢��Cursor����
			          //�������е�������Ϣ
			          if (cursor.moveToNext()) 
			          {
			        	  int pathRemain=cursor.getInt(cursor.getColumnIndex("pathRemain"));
			        	  if(pathRemain==0)
			        	  {
			        		  db.execSQL("update vmc_column set columnStatus=3 where cabID=? and columnID=?", 
			        	          		new Object[] { cabID,columnID}); 
			        	  }
			          }
			          if(!cursor.isClosed()) 
			   		  {  
			        	cursor.close();  
			   		  } 
	  			  }
	  		  }
	  		  if(!cur.isClosed()) 
	   		  {  
	  			cur.close();  
	   		  } 
	          // ��������ı�־Ϊ�ɹ������������setTransactionSuccessful() ������Ĭ�ϻ�ع�����
		      db.setTransactionSuccessful();
		    } catch (Exception e) {
		        // process it
		        e.printStackTrace();
		    } finally {
		        // ��������ı�־�Ƿ�Ϊ�ɹ������Ϊ�ɹ����ύ���񣬷���ع�����
		        db.endTransaction();
		        db.close(); 
		    }
  	}
	
}
