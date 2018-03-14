package com.javahis.ui.ekt.testEkt.impl;

import psyg.graphic.SysObj;
import jdo.opb.OPB;
import jdo.reg.Reg;
import jdo.sys.Pat;

import com.dongyang.data.TParm;
import com.javahis.ui.ekt.testEkt.EktParam;
import com.javahis.ui.ekt.testEkt.EktRegOrOpbClient;
import com.javahis.ui.ekt.testEkt.IEktTradeStrategy;
import com.javahis.ui.opb.OPBChargesMControl;

public class EktTradeStrategyOpbImpl implements IEktTradeStrategy {
	
	private EktParam ektParam;
	private Reg reg;
	private Pat pat;
	private OPB opb;
	public OPBChargesMControl opbChargesMControl;
	private EktRegOrOpbClient ektRegOrOpbClient;
	
	public EktTradeStrategyOpbImpl(EktParam ektParam){
		this.ektParam = ektParam;
		opbChargesMControl = ektParam.getOpbChargesMControl();
		ektParam.settControl(opbChargesMControl);
		ektRegOrOpbClient = new EktRegOrOpbClient();
	}
	

	@Override
	public <T> EktParam creatParam(T t) {
		
		opb = (OPB) t;
		opb.setEktSql(null);
		
		TParm modifyOrderParm = ektParam.getOrderParm();
		pat = ektParam.getPat();
		reg = ektParam.getReg();
		
		TParm orderParm = opb.getEKTParm(opbChargesMControl.ektTCharge.isSelected(), opbChargesMControl
				.getValueString("CAT1_TYPE"));
		opbChargesMControl.setEktExeParm(orderParm, modifyOrderParm, null);
		if (orderParm.getParm("newParm").getCount("RX_NO") <= 0) {
			opbChargesMControl.messageBox("没有要执行的数据");
			return null;
		}
		
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("IDNO", pat.getIdNo());
		orderParm.setData("CASE_NO", reg.caseNo());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "男" : "女");
		orderParm.setData("ektParm", opbChargesMControl.parmEKT.getData());
		ektParam.setOrderParm(orderParm);
		ektParam.setOpType("");
		return ektParam;
		

	}

	@Override
	public EktParam openClient(EktParam ektParam) {
//		ektParam = EktRegOrOpbClient.getInstance().openClient(ektParam);
		ektParam = ektRegOrOpbClient.openClient(ektParam);
		System.out.println("出来----------------");
		if(ektParam != null){
			System.out.println("出来-------1111---------");
			String type = ektParam.getOpType();
			// System.out.println("type===" + type);
			if (type.equals("3") || type.equals("-1")) {
				opbChargesMControl.messageBox("E0115");
				return null;
			}
			if (type.equals("2")) {
				return null;
			}
			
			
			
		}
		opb.setEktSql(ektParam.getSqls());
		System.out.println("出来---------222222-------");

		TParm parm =ektParam.getOrderParm().getParm("result");
		System.out.println("出来---------33333-------");
		
		System.out.println("parm----"+parm);
		
		if(parm.getData() == null){
			parm = new TParm();
		}

		opbChargesMControl.tredeNo = parm.getValue("TREDE_NO");
		
		System.out.println("ektParam.getOrderParm()----"+ektParam.getOrderParm());

		parm.setData("orderParm", ektParam.getOrderParm().getData());
		
		System.out.println("出来---------44444-------");
	
		opbChargesMControl.ektParmSave = parm;
		System.out.println("出来---------55555-------");

		
		return ektParam;
	}

	@Override
	public EktParam openClientR(EktParam ektParam) {
		// TODO Auto-generated method stub
		return null;
	}

}
