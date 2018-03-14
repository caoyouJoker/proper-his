package com.javahis.device;

public class STDFAuth {	
	byte dfaID[]=new byte[3];//Desfire aid
	byte keyNo;//DesfireKeyNo
	byte psamKeyType;//psam Key用途
	byte psamKeyIndex;//psam Key索引
	byte psamAPP;//psam 应用类型
	public byte[] getDfaID() {
		return dfaID;
	}
	public void setDfaID(byte[] dfaID) {
		this.dfaID = dfaID;
	}
	public byte getKeyNo() {
		return keyNo;
	}
	public void setKeyNo(byte keyNo) {
		this.keyNo = keyNo;
	}
	public byte getPsamKeyType() {
		return psamKeyType;
	}
	public void setPsamKeyType(byte psamKeyType) {
		this.psamKeyType = psamKeyType;
	}
	public byte getPsamKeyIndex() {
		return psamKeyIndex;
	}
	public void setPsamKeyIndex(byte psamKeyIndex) {
		this.psamKeyIndex = psamKeyIndex;
	}
	public byte getPsamAPP() {
		return psamAPP;
	}
	public void setPsamAPP(byte psamAPP) {
		this.psamAPP = psamAPP;
	}
	
	

}
