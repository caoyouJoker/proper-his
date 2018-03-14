package com.javahis.device.provobj;
/**
 * 费用明细
 * @author lixiang
 *
 */
public class FeeDetail {
	float pcRate;   //O个人支付比例[N(5,4)]
	float pcSelfFee; //O自付费用[N(10, 4)] 
	float pcDeduct;  //O自费费用[N(10, 4)]
	public float getPcRate() {
		return pcRate;
	}
	public void setPcRate(float pcRate) {
		this.pcRate = pcRate;
	}
	public float getPcSelfFee() {
		return pcSelfFee;
	}
	public void setPcSelfFee(float pcSelfFee) {
		this.pcSelfFee = pcSelfFee;
	}
	public float getPcDeduct() {
		return pcDeduct;
	}
	public void setPcDeduct(float pcDeduct) {
		this.pcDeduct = pcDeduct;
	}
	
	
}
