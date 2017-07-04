package cn.yoc.unionPay.po;

/**
 * @author Yocer
 * @version 2017/6/15
 * @description
 */
public class UnionOrder {

    private String txnAmt; //���ѽ�� ��λ������ 20 ��ʾ 20��
    private String orderId; //������
    private String txnTime; //�µ�ʱ��
    private String termId; //�ն˺� �����豸��
    private String refundAmt; //�˿���

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(String refundAmt) {
        this.refundAmt = refundAmt;
    }

	@Override
	public String toString() {
		return "UnionOrder [txnAmt=" + txnAmt + ", orderId=" + orderId
				+ ", txnTime=" + txnTime + ", termId=" + termId
				+ ", refundAmt=" + refundAmt + "]";
	}
    
}
