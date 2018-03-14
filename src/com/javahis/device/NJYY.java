package com.javahis.device;
/**
 * 医疗信息
 * @author lixiang
 *
 */
public class NJYY {
	//:病人姓名
	byte sickName[]=new byte[20];
	//电子病历号
	byte erecordNo[]=new byte[30];
	//识别号
	byte flag[]=new byte[3];
	//医保号
	byte hspNo[]=new byte[9];
	//保留位
	byte rFu[]=new byte[2];
	
	public byte[] getSickName() {
		return sickName;
	}
	public void setSickName(byte[] sickName) {
		this.sickName = sickName;
	}
	public byte[] getErecordNo() {
		return erecordNo;
	}
	public void setErecordNo(byte[] erecordNo) {
		this.erecordNo = erecordNo;
	}
	public byte[] getFlag() {
		return flag;
	}
	public void setFlag(byte[] flag) {
		this.flag = flag;
	}
	public byte[] getHspNo() {
		return hspNo;
	}
	public void setHspNo(byte[] hspNo) {
		this.hspNo = hspNo;
	}
	public byte[] getrFu() {
		return rFu;
	}
	public void setrFu(byte[] rFu) {
		this.rFu = rFu;
	}
	
	
	
	
}
