package com.javahis.ui.ekt.testEkt.impl;

import java.awt.Color;

import jdo.ekt.EKTIO;
import jdo.odo.ODO;
import jdo.odo.OpdOrder;
import jdo.opd.OrderTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.javahis.ui.ekt.testEkt.EktClient;
import com.javahis.ui.ekt.testEkt.EktOpdClient;
import com.javahis.ui.ekt.testEkt.EktParam;
import com.javahis.ui.ekt.testEkt.IEktTradeStrategy;
import com.javahis.ui.opd.OdoMainControl;

public class EktTradeStrategyOpdImpl implements IEktTradeStrategy {
	
	private ODO odo;
	public OdoMainControl odoMainControl;
	private EktOpdClient ektOpdClient;
	private EktParam ektParam;
	public Color red = new Color(255, 0, 0);
	public Color green = new Color(0,125, 0);
	private boolean isDelOrder;
	
	
	public EktTradeStrategyOpdImpl(EktParam ektParam){
		this.ektParam = ektParam;
		odoMainControl = ektParam.getOdoMainControl();
		ektParam.settControl(odoMainControl);
		ektOpdClient = new EktOpdClient();
	}

	@Override
	public <T> EktParam creatParam(T t) {
		// TODO Auto-generated method stub
		odo = (ODO) t;
		odo.setEktSql(null);
		if (EKTIO.getInstance().ektSwitch()) {
			
			Reg reg = ektParam.getReg();
			Pat pat = ektParam.getPat();
			
			TParm orderOldParm = ektParam.getOrderOldParm();
			TParm orderParm = ektParam.getOrderParm();

			if (odo == null) {
				odoMainControl.messageBox("E0115");
				return null;
			}
			
			ektParam.setOpType("");
			
			TParm unParm = new TParm();
			if (orderOldParm == null) {
				odoMainControl.messageBox("没有需要操作的医嘱");
				ektParam.setOpType("5");
				return ektParam;
			}
			if(orderParm.getValue("OP_FLG").length()>0 && orderParm.getInt("OP_FLG")==5){
				odoMainControl.messageBox("没有需要操作的医嘱");
				ektParam.setOpType("5");
				return ektParam;
			}
			
			TParm updateParm=orderParm.getParm("updateParm");
			//TODO
			boolean unFlg = odoMainControl.updateOrderParm(updateParm, orderOldParm, unParm);
			TParm parm = new TParm();
			 isDelOrder = false;// 执行删除医嘱
			//boolean exeDelOrder = false;// 执行删除医嘱
			String delFlg=orderParm.getValue("DEL_FLG");
			// 如果出现所有医嘱删除也会出现IS_NEW = false 状态 所有需要在执行方法时先查询当前所有医嘱
			// 校验是否发送删除检验检查接口
			if(delFlg.equals("Y")){
				isDelOrder = true;
			}
			
			orderParm.setData("BUSINESS_TYPE", "ODO");
			parm.setData("CASE_NO",reg.caseNo());
			orderParm.setData("REGION_CODE", Operator.getRegion());
			orderParm.setData("MR_NO", pat.getMrNo());
			orderParm.setData("NAME", pat.getName());
			orderParm.setData("IDNO", pat.getIdNo());
			orderParm.setData("SEX", pat.getSexCode() != null
					&& pat.getSexCode().equals("1") ? "男" : "女");
			// 送医疗卡，返回医疗卡的回传值
			orderParm.setData("INS_FLG", "N");// 医保卡操作
			orderParm.setData("UN_FLG", unFlg ? "Y" : "N");// 医生修改的医嘱超过医疗卡金额执行的操作
			orderParm.setData("unParm", unParm.getData());// 获得执行修改的医嘱
			if (null != orderOldParm.getValue("OPBEKTFEE_FLG")
					&& orderOldParm.getValue("OPBEKTFEE_FLG").equals("Y")) {
				orderParm.setData("OPBEKTFEE_FLG", "Y");
			}
			//直接收费操作如果有修改的收费医嘱 不能执行取消操作
			if(null == orderOldParm.getValue("OPBEKTFEE_FLG")
					|| orderOldParm.getValue("OPBEKTFEE_FLG").length()<=0){
				if(unFlg)
					orderParm.setData("OPBEKTFEE_FLG", "Y");
			}
			if (odoMainControl.resultData.getCount("CASE_NO")>0) {//====pangben 2014-1-20 建行卡校验
				odoMainControl.messageBox("P0001");
//				parm.setData("OP_TYPE", 5);
				ektParam.setOpType("5");
				return ektParam;
			}else{
				odoMainControl.ektReadParm = EKTIO.getInstance().TXreadEKT();
				if (null == odoMainControl.ektReadParm || odoMainControl.ektReadParm.getErrCode() < 0
						|| null == odoMainControl.ektReadParm.getValue("MR_NO")) {
					odoMainControl.messageBox("医疗卡读卡有误。");
					odoMainControl.setValue("LBL_EKT_MESSAGE", "未读卡");//====pangben 2013-5-3添加读卡
					odoMainControl.ekt_lable.setForeground(red);//======yanjing 2013-06-14设置读卡颜色
					ektParam.setOpType("5");
					return ektParam;
				}else{
					odoMainControl.setValue("LBL_EKT_MESSAGE", "已读卡");//====pangben 2013-5-3添加读卡
					odoMainControl.ekt_lable.setForeground(green);//======yanjing 2013-06-14设置读卡颜色
				}
			}
			if (!odoMainControl.ektReadParm.getValue("MR_NO").equals(odoMainControl.getValue("MR_NO"))) {
				odoMainControl.messageBox("病患信息不符,此医疗卡病患名称为:"
						+ odoMainControl.ektReadParm.getValue("PAT_NAME"));
				odoMainControl.ektReadParm = null;
				ektParam.setOpType("5");
				return ektParam;
			}
			int type=0;
			//parm.setData("BILL_FLG", "Y");
			orderParm.setData("ektParm", odoMainControl.ektReadParm.getData()); // 医疗卡数据
			
				boolean isNull = true;
				OpdOrder opdOrder = odo.getOpdOrder();
				String lastFilter = opdOrder.getFilter();
				opdOrder.setFilter("");
				opdOrder.filter();
				for (int i = 0; i < opdOrder.rowCount(); i++) {
					if(opdOrder.getItemString(i, "ORDER_CODE").length() > 0){
						isNull = false;
						break;
					}
				}
				opdOrder.setFilter(lastFilter);
				opdOrder.filter();
			
			ektParam.setNull(isNull);
			
			
		
		} else {
			odoMainControl.messageBox_("医疗卡接口未开启");
			return null;
			

		}
	
		
		return ektParam;
		
		
		
	}

	@Override
	public EktParam openClient(EktParam ektParam) {

		
		ektParam = ektOpdClient.openClient(ektParam);
		if(ektParam != null){
			odo.setEktSql(ektParam.getSqls());
			odo.setTredeNo(ektParam.getOrderParm().getParm("result").getValue("TRADE_NO"));
			odo.setHistoryNo(ektParam.getOrderParm().getParm("result").getValue("HISTORY_NO"));
			odoMainControl.tredeNo=ektParam.getOrderParm().getParm("result").getValue("TRADE_NO");
			odoMainControl.opdUnFlg=ektParam.getOrderParm().getParm("result").getValue("OPD_UN_FLG");
			System.out.println("ektParam.getSqls()----"+ektParam.getSqls());
			if(ektParam.getSqls() != null){
				odoMainControl.sendHL7Parm = ektParam.getOrderParm().getParm("hl7Parm");
			}
			
		}

		return ektParam;
	}

	@Override
	public EktParam openClientR(EktParam ektParam) {
		// TODO Auto-generated method stub
		return null;
	}

}
