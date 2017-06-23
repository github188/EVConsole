/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 *
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 *
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28       ��־��ӡ������
 * =============================================================================
 */
package cn.yoc.unionPay.sdk;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @ClassName LogUtil
 * @Description acpsdk��־������
 * @date 2016-7-22 ����4:04:35
 *
 */
public class LogUtil {

	private final static LogUtils GATELOG = LogUtils.getLogger("ACP_SDK_LOG");
	private final static LogUtils GATELOG_ERROR = LogUtils.getLogger("SDK_ERR_LOG");
	private final static LogUtils GATELOG_MESSAGE = LogUtils.getLogger("SDK_MSG_LOG");

	final static String LOG_STRING_REQ_MSG_BEGIN = "============================== SDK REQ MSG BEGIN ==============================";
	final static String LOG_STRING_REQ_MSG_END = "==============================  SDK REQ MSG END  ==============================";
	final static String LOG_STRING_RSP_MSG_BEGIN = "============================== SDK RSP MSG BEGIN ==============================";
	final static String LOG_STRING_RSP_MSG_END = "==============================  SDK RSP MSG END  ==============================";

	/**
	 * ��¼��ͨ��־
	 * 
	 * @param cont
	 */
	public static void writeLog(String cont) {
		GATELOG.info(cont);
	}

	/**
	 * ��¼ERORR��־
	 * 
	 * @param cont
	 */
	public static void writeErrorLog(String cont) {
		GATELOG_ERROR.error(cont);
	}

	/**
	 * ��¼ERROR��־
	 * 
	 * @param cont
	 * @param ex
	 */
	public static void writeErrorLog(String cont, Throwable ex) {
		GATELOG_ERROR.error(cont + "\tex=" +  ex.getMessage());
	}

	/**
	 * ��¼ͨ�ű���
	 * 
	 * @param msg
	 */
	public static void writeMessage(String msg) {
		GATELOG_MESSAGE.info(msg);
	}

	/**
	 * ��ӡ������
	 * 
	 * @param reqParam
	 */
	public static void printRequestLog(Map<String, String> reqParam) {
		writeMessage(LOG_STRING_REQ_MSG_BEGIN);
		Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			writeMessage("[" + en.getKey() + "] = [" + en.getValue() + "]");
		}
		writeMessage(LOG_STRING_REQ_MSG_END);
	}

	/**
	 * ��ӡ��Ӧ����.
	 * 
	 * @param res
	 */
	public static void printResponseLog(String res) {
		writeMessage(LOG_STRING_RSP_MSG_BEGIN);
		writeMessage(res);
		writeMessage(LOG_STRING_RSP_MSG_END);
	}

	/**
	 * debug����
	 * 
	 * @param cont
	 */
	public static void debug(String cont) {
		if (GATELOG.isDebugEnabled()) {
			GATELOG.debug(cont);
		}
	}
}
