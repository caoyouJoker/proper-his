package com.javahis.ui.adm;

import jdo.adm.ADMInpTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 床位管理主窗口
 * </p>
 * table
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author YangJj 2015-6-18
 * 
 * @version 1.0
 */

public class ADMBedManageControl extends TControl{

	//预计时间
	private TTextFormat PRETREAT_DATE;
	
	//病区
	private TTextFormat STATION_CODE;
	
	//主表
	private TTable TABLE_M;
	
	//细表
	private TTable TABLE_D;
	
	public void onInit(){
		super.onInit();
		
		PRETREAT_DATE = (TTextFormat) this.getComponent("PRETREAT_DATE");
		STATION_CODE = (TTextFormat) this.getComponent("STATION_CODE");
		TABLE_M = (TTable) this.getComponent("TABLE_M");
		TABLE_D = (TTable) this.getComponent("TABLE_D");
		
		callFunction("UI|TABLE_M|addEventListener",
                "TABLE_M->" + TTableEvent.CLICKED, this, "onTableMClicked");
		
		//String date = SystemTool.getInstance().getDate().toString().replace("-", "/").substring(0, 10)+" 23:59:59";
		
		this.setValue("PRETREAT_DATE", StringTool.rollDate(SystemTool.getInstance().getDate(), 1).toString().substring(0,10).replaceAll("-", "/")+" 23:59:59");
	}
	
	
	//主表查询
	public void onQuery(){
		if("".equals(getValueString("PRETREAT_DATE"))){
			this.messageBox("请输入预计时间！");
			return ;
		}
		
		TParm parm = new TParm();
		parm.setData("START_TIME", SystemTool.getInstance().getDate().toString().replace("-", "/").substring(0, 19));
		parm.setData("END_TIME", getValueString("PRETREAT_DATE").replace("-", "/").substring(0, 19));
		parm.setData("STATION_CODE", getValueString("STATION_CODE"));
		
		String sql = getTableMSql(parm);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount() <= 0){
			this.messageBox("查无数据！");
			TABLE_M.setParmValue(new TParm());
			TABLE_D.setParmValue(new TParm());
			return;
		}
		
		
		//合计
		int fillNum = 0;
		int nullNum = 0;
		int preInNum = 0;
		int preOutNum = 0;
		int containNum = 0;
		for(int i = 0 ; i < result.getCount() ; i++){
			fillNum += Integer.parseInt(result.getValue("FILL_NUM", i));
			nullNum += Integer.parseInt(result.getValue("NULL_NUM", i));
			preInNum += Integer.parseInt(result.getValue("PRE_IN_NUM", i));
			preOutNum += Integer.parseInt(result.getValue("PRE_OUT_NUM", i));
			containNum += Integer.parseInt(result.getValue("CONTAIN_NUM", i));
		}
		
		TParm total = new TParm();
		total.setData("STATION_CODE",0, "合计：");
		total.setData("FILL_NUM",0, fillNum);
		total.setData("NULL_NUM",0, nullNum);
		total.setData("PRE_IN_NUM",0, preInNum);
		total.setData("PRE_OUT_NUM",0, preOutNum);
		total.setData("CONTAIN_NUM",0, containNum);
		
		result.addRowData(total, 0);
		
		TABLE_M.setParmValue(result);
		TABLE_D.setParmValue(new TParm());
	}
	
	//细表单击事件
	public void onTableMClicked(int row){
		TParm parm = new TParm();
		
		TABLE_M.acceptText();
		TParm tableParm = TABLE_M.getParmValue();
		
		if(row < 0){
			this.messageBox("请选择一行内容！");
			return;
		}
		
		parm.setData("STATION_CODE", tableParm.getValue("STATION_CODE", row));
		queryTableD(parm);
	}
	
	//细表查询
	public void queryTableD(TParm parm){
		TParm admInfo = new TParm();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		admInfo = ADMInpTool.getInstance().queryInStation(parm);
		if (admInfo.getErrCode() < 0) {
			this.messageBox_(admInfo.getErrText());
			return;
		}
		// 如果病床没有住人 把年龄的0去掉
		for (int i = 0; i < admInfo.getCount(); i++) {
			int status = Integer.parseInt(admInfo.getValue("BED_STATUS", i));
			String bedStatus = "";
			switch (status) {
			case 0:
				bedStatus = "空床";
				break;
				
			case 1:
				bedStatus = "占床";
				break;
			case 2:
				bedStatus = "整理中";
				break;
			case 3:
				bedStatus = "包床";
				break;
			default:
				break;
			}
			
			admInfo.setData("BED_STATUS", i, bedStatus);
			

			if (admInfo.getInt("AGE", i) == 0) {
				// 如果住有病患并且此病床不是包床 年龄为0岁 那么自动加一
				if (admInfo.getValue("MR_NO", i).length() > 0
						&& !admInfo.getValue("BED_STATUS", i).equals("3"))
					admInfo.setData("AGE", i, "1");
				else
					// 没有病患在床将0改为空
					admInfo.setData("AGE", i, "");
			} else {
				// 得到病患年龄========pangb 2011-11-18 获得年龄一致
				String[] AGE = StringTool.CountAgeByTimestamp(
						admInfo.getTimestamp("BIRTH_DATE", i),
						admInfo.getTimestamp("IN_DATE", i));
				admInfo.setData("AGE", i, AGE[0]);
			}
			if (admInfo.getData("IN_DATE", i) != null
					&& admInfo.getValue("MR_NO", i).length() > 0
					&& !admInfo.getValue("BED_STATUS", i).equals("3")) {
				int days = StringTool.getDateDiffer(SystemTool.getInstance()
						.getDate(), admInfo.getTimestamp("IN_DATE", i));
				if (days > 0) {
					admInfo.setData("DAYNUM", i, days);
				} else {
					admInfo.setData("DAYNUM", i, "1");
				}
			} else
				admInfo.setData("DAYNUM", i, "");

		}
		
		System.out.println("BED:"+admInfo+"");
		TABLE_D.setParmValue(admInfo);
	}
	
	public String getTableMSql(TParm parm){
		String startTime = parm.getValue("START_TIME");
		String endTime = parm.getValue("END_TIME");
		String stationCode = parm.getValue("STATION_CODE");
		
		String sql = "";
		
		sql += " SELECT  " +
					" A.STATION_CODE, " + //病区编码
			   		" A.STATION_DESC, " + //病区
			   		" DECODE (B.C1, NULL, 0, B.C1) AS FILL_NUM, " + //占床数
			   		" DECODE (C.C2, NULL, 0, C.C2) AS NULL_NUM, " + //空床数
			   		" DECODE (D.C3, NULL, 0, D.C3) AS PRE_IN_NUM, " + //预计转入人数
			   		" DECODE (E.C4, NULL, 0, E.C4) AS PRE_OUT_NUM, " + //预计转出人数
			   		" (DECODE (C.C2, NULL, 0, C.C2)+DECODE (E.C4, NULL, 0, E.C4)-DECODE (D.C3, NULL, 0, D.C3)) AS CONTAIN_NUM " + //可接收人数
			   	" FROM " +
			   	
			   		//病区
			   		"(SELECT STATION_CODE,STATION_DESC FROM SYS_STATION) A, " +
			   	
			   		//占床数
			   		" ( SELECT STATION_CODE, COUNT (STATION_CODE) C1 " +
			   			" FROM SYS_BED " +
			   			" WHERE BED_STATUS <> '0' AND ACTIVE_FLG='Y' " +
			   		" GROUP BY STATION_CODE) B, " +
			   		
			   		//空床数
			   		" ( SELECT STATION_CODE, COUNT (STATION_CODE) C2 " +
			   			" FROM SYS_BED " +
			   			" WHERE BED_STATUS = '0' AND ACTIVE_FLG='Y' " +
			   		" GROUP BY STATION_CODE) C, " +

			   		//预计转入人数
			   		" ( SELECT STATION_CODE, SUM (C3) AS C3 " +
			   			" FROM ( SELECT PRETREAT_IN_STATION AS STATION_CODE, " +
			   						" COUNT (PRETREAT_IN_STATION) AS C3 " +
			   					" FROM ADM_PRETREAT " +
			   					" WHERE PRETREAT_DATE BETWEEN TO_DATE('"+startTime+"','yyyy/MM/dd HH24:mi:ss') AND TO_DATE('"+endTime+"','yyyy/MM/dd HH24:mi:ss') " +
			   					" GROUP BY PRETREAT_IN_STATION " +
			   					" UNION ALL " +
			   					" SELECT IN_STATION_CODE AS STATION_CODE, " +
			   						" COUNT (IN_STATION_CODE) AS C3 " +
			   					" FROM ADM_WAIT_TRANS " +
			   					" GROUP BY IN_STATION_CODE) D_1 " +
			   		" GROUP BY STATION_CODE) D, " +
			   		
			   		//预计转出人数
			   		" ( SELECT PRETREAT_OUT_STATION AS STATION_CODE, " +
			   			" COUNT (PRETREAT_OUT_STATION) AS C4 " +
			   		" FROM ADM_PRETREAT " +
			   		" WHERE PRETREAT_DATE BETWEEN TO_DATE('"+startTime+"','yyyy/MM/dd HH24:mi:ss') AND TO_DATE('"+endTime+"','yyyy/MM/dd HH24:mi:ss') " +
			   		" GROUP BY PRETREAT_OUT_STATION) E " +
			   	" WHERE A.STATION_CODE = B.STATION_CODE(+) " +
			   	" AND A.STATION_CODE = C.STATION_CODE(+) " +
			   	" AND A.STATION_CODE = D.STATION_CODE(+) " +
			   	" AND A.STATION_CODE = E.STATION_CODE(+) ";
		
		if(!"".equals(stationCode)){
			sql += " AND A.STATION_CODE = '"+stationCode+"'";
		}
		
		sql += " ORDER BY A.STATION_CODE";
System.out.println(":::"+sql);
		return sql;
	}
	
}
