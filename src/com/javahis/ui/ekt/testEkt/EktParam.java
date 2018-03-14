package com.javahis.ui.ekt.testEkt;

import jdo.reg.Reg;
import jdo.sys.Pat;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.javahis.ui.opb.OPBChargesMControl;
import com.javahis.ui.opd.OdoMainControl;
import com.javahis.ui.reg.REGPatAdmControl;

public class EktParam {
	
	private String type; //reg  挂号  odo 医生站 opd 门诊收费
	private OdoMainControl odoMainControl;
	private OPBChargesMControl opbChargesMControl;
	private REGPatAdmControl regPatAdmControl;
	private TControl tControl;
	private Reg reg;
	private Pat pat;
	
	private TParm orderOldParm;
	private TParm orderParm;
//	private TParm unParm;
	
	private boolean isNull;
	
	private String opType;
	
	private String[] sqls;
	
	private String confirmNo="";

	public String getConfirmNo() {
		return confirmNo;
	}

	public void setConfirmNo(String confirmNo) {
		this.confirmNo = confirmNo;
	}

	public String[] getSqls() {
		return sqls;
	}

	public void setSqls(String[] sqls) {
		this.sqls = sqls;
	}

	public String getOpType() {
		return opType;
	}

	public void setOpType(String opType) {
		this.opType = opType;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}


	public Reg getReg() {
		return reg;
	}

	public void setReg(Reg reg) {
		this.reg = reg;
	}

	public Pat getPat() {
		return pat;
	}

	public void setPat(Pat pat) {
		this.pat = pat;
	}

//	public TParm getUnParm() {
//		return unParm;
//	}
//
//	public void setUnParm(TParm unParm) {
//		this.unParm = unParm;
//	}

	public TParm getOrderOldParm() {
		return orderOldParm;
	}

	public void setOrderOldParm(TParm orderOldParm) {
		this.orderOldParm = orderOldParm;
	}

	public TParm getOrderParm() {
		return orderParm;
	}

	public void setOrderParm(TParm orderParm) {
		this.orderParm = orderParm;
	}

	public OdoMainControl getOdoMainControl() {
		return odoMainControl;
	}

	public void setOdoMainControl(OdoMainControl odoMainControl) {
		this.odoMainControl = odoMainControl;
		
	}

	public OPBChargesMControl getOpbChargesMControl() {
		return opbChargesMControl;
	}

	public void setOpbChargesMControl(OPBChargesMControl opbChargesMControl) {
		this.opbChargesMControl = opbChargesMControl;
	}

	public REGPatAdmControl getRegPatAdmControl() {
		return regPatAdmControl;
	}

	public void setRegPatAdmControl(REGPatAdmControl regPatAdmControl) {
		this.regPatAdmControl = regPatAdmControl;
	}

	public String getType() {
		return type;
	}

	public TControl gettControl() {
		return tControl;
	}

	public void settControl(TControl tControl) {
		this.tControl = tControl;
	}

	public void setType(String type) {
		this.type = type;
	}

}
