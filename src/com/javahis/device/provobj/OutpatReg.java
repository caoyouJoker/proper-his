package com.javahis.device.provobj;
/**
 * 门诊挂号返回值;
 * @author lixiang
 *
 */
public class OutpatReg {
		float pcPubPay;//统筹支付[N10, 2]
		float pcAccPay;//帐户支付[N10, 2]
		float pcCashPay;//现金支付[N10, 2]
		public float getPcPubPay() {
			return pcPubPay;
		}
		public void setPcPubPay(float pcPubPay) {
			this.pcPubPay = pcPubPay;
		}
		public float getPcAccPay() {
			return pcAccPay;
		}
		public void setPcAccPay(float pcAccPay) {
			this.pcAccPay = pcAccPay;
		}
		public float getPcCashPay() {
			return pcCashPay;
		}
		public void setPcCashPay(float pcCashPay) {
			this.pcCashPay = pcCashPay;
		}		
		
}
