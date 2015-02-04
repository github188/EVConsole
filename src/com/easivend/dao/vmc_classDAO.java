/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_classDAO �����Ͳ����ļ�  
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.easivend.model.Tb_vmc_class;



import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;



public class vmc_classDAO 
{
	private DBOpenHelper helper;// ����DBOpenHelper����
    private SQLiteDatabase db;// ����SQLiteDatabase����
    //SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    // ���幹�캯��
	public vmc_classDAO(Context context) 
	{
		helper=new DBOpenHelper(context);// ��ʼ��DBOpenHelper����
	}
	//����
	public void add(Tb_vmc_class tb_vmc_class)throws SQLException
	{
		
		db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
		// ִ������		
		db.execSQL("insert into vmc_class (classID,className,classTime) values (?,?,(datetime('now', 'localtime')))",
		        new Object[] { tb_vmc_class.getClassID(), tb_vmc_class.getClassName() });
		db.close(); 
	}
    //�޸�
	public void update(Tb_vmc_class tb_vmc_class) {
        db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        // ִ���޸�
        db.execSQL("update vmc_class set className = ?,classTime=(datetime('now', 'localtime')) where classID = ?", 
        		new Object[] { tb_vmc_class.getClassName(),tb_vmc_class.getClassID()});
        db.close(); 
	}
	//ɾ������
	public void detele(Tb_vmc_class tb_vmc_class) 
	{       
        db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        // ִ��ɾ��������Ϣ����
        db.execSQL("delete from vmc_class where classID = ?", 
        		new Object[] { tb_vmc_class.getClassID()});
        db.close(); 
    }
	/**
     * ͨ��ID�h��������Ϣ
     * 
     * @param ids
     */
    public void detele(StringBuffer... ids) 
    {
    	// �ж��Ƿ����Ҫɾ����id
        if (ids.length > 0) 
        {
            StringBuffer sb = new StringBuffer();// ����StringBuffer����
            for (int i = 0; i < ids.length; i++) 
            {// ����Ҫɾ����id����

                sb.append('?').append(',');// ��ɾ���������ӵ�StringBuffer������
            }
            sb.deleteCharAt(sb.length() - 1);// ȥ�����һ����,���ַ�
            db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
            // ִ��ɾ��������Ϣ����
            db.execSQL("delete from vmc_class where classID in (" + sb + ")", (Object[]) ids);
            db.close(); 
        }
    }

    /**
     * ��ȡָ��������Ʒ�����Ϣ
     * 
     * @param start
     *            ��ʼλ��
     * @param count
     *            ÿҳ��ʾ����
     * @return
     */
    public List<Tb_vmc_class> getScrollData(int start, int count) 
    {
        List<Tb_vmc_class> tb_inaccount = new ArrayList<Tb_vmc_class>();// �������϶���
        db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        // ��ȡ����������Ϣ
        Cursor cursor = db.rawQuery("select * from vmc_class limit ?,?", new String[] { String.valueOf(start), String.valueOf(count) });
        //�������е�������Ϣ
        while (cursor.moveToNext()) 
        {	
            // ����������������Ϣ���ӵ�������
            tb_inaccount.add(new Tb_vmc_class(cursor.getString(cursor.getColumnIndex("classID")), cursor.getString(cursor.getColumnIndex("className")),
            		cursor.getString(cursor.getColumnIndex("classTime"))));
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close(); 
        return tb_inaccount;// ���ؼ���
    }

    /**
     * ��ȡ�ܼ�¼��
     * 
     * @return
     */
    public long getCount() {
        db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        Cursor cursor = db.rawQuery("select count(classID) from vmc_class", null);// ��ȡ������Ϣ�ļ�¼��
        if (cursor.moveToNext()) {// �ж�Cursor���Ƿ�������

            return cursor.getLong(0);// �����ܼ�¼��
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close(); 
        return 0;// ���û�����ݣ��򷵻�0
    }

    /**
     * ��ȡ���������
     * 
     * @return
     */
    public int getMaxId() {
        db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
        Cursor cursor = db.rawQuery("select max(classID) from vmc_class", null);// ��ȡ������Ϣ���е������
        while (cursor.moveToLast()) {// ����Cursor�е����һ������
            return cursor.getInt(0);// ��ȡ���ʵ������ݣ��������
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close(); 
        return 0;// ���û�����ݣ��򷵻�0
    }
	
}