package com.javahis.ui.odi;

import java.util.List;
import java.util.Map;

import jdo.adm.ADMInpTool;
import jdo.opd.TotQtyTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 抗菌药物医生越级审核
 * </p>
 * 
 * <p>
 * Description: 抗菌药物医生越级审核
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c) 2014年2月
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author Miracle
 * @version JavaHis 1.0
 */
public class ODIOverRideCheckControl extends TControl {
	private String caseNo = "";
	private String mrNo = "";
	private String ipdNo = "";
	private String bedNo = "";
	private String stationNo = "";
	private String deptCode = "";
	private String vsDcCode = "";
	private TTable table;
	private String orgCode = "";

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		table = getTable("TABLE1");
		// //接受传入的参数
		TParm outsideParm = (TParm) this.getParameter();
		mrNo = outsideParm.getValue("INW", "MR_NO");// 患者姓名
		caseNo = outsideParm.getValue("INW", "CASE_NO");// 就诊号
		this.onQuery();
		ipdNo = outsideParm.getValue("INW", "IPD_NO");// 住院号
		bedNo = outsideParm.getValue("INW", "BED_NO");// 床号
		stationNo = outsideParm.getValue("INW", "STATION_CODE");// 病区
		deptCode = outsideParm.getValue("INW", "DEPT_CODE");// 科室
		vsDcCode = outsideParm.getValue("INW", "VS_DR_CODE");// 经治医生
		orgCode = (((TParm) this.getParameter()).getData("ODI", "ORG_CODE")
				.toString());
		addEventListener("TABLE1" + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValue");
		// 长期医嘱CHECK_BOX监听事件
		getTable("TABLE1").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
				this, "onCheckBoxValue");

	}

	/**
	 * 修改事件
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onChangeTableValue(Object obj) {
		TTableNode tNode = (TTableNode) obj;
		if (tNode == null)
			return true;
		TParm tableParm = table.getParmValue();
		// 判断当前列是否有医嘱
		int row = tNode.getRow();
		// 拿到table上的parmmap的列名
		String columnName = tNode.getTable().getDataStoreColumnName(
				tNode.getColumn());
		if (columnName.equals("CHOOSE_FLG")) {
			int rowCount = tableParm.getCount("ORDER_DESC");
			String linkNo = tableParm.getValue("LINK_NO",row);
			for (int i = 0; i < rowCount; i++) {
				if (!"".equals(linkNo)&&!linkNo.equals(null)&&
						linkNo.equals(tableParm.getValue("LINK_NO", i))&&i!= row) {
					tableParm.setData("CHOOSE_FLG", i, tableParm.getBoolean("CHOOSE_FLG", row) ? "N"
							: "Y");
				}
			}
			tableParm.setData("CHOOSE_FLG", row, tableParm.getBoolean("CHOOSE_FLG", row) ? "N"
					: "Y");
			table.setParmValue(tableParm);
			String flg = tableParm.getValue(columnName, row);
			int newRow = row;
			if (flg.equals("Y")) {
				if(!tableParm.getValue("LCS_CLASS_CODE", row).equals(null)&&
						!"".equals(tableParm.getValue("LCS_CLASS_CODE", row))){//抗菌药物时
					newRow = row;
				}else{
					//非抗菌药物时取主项抗菌药物的停用时间
					//取出link_no找出对应的主项
					String fLinkNo = tableParm.getValue("LINK_NO", row);
					for(int m = 0;m<tableParm.getCount();m++){
						String kLinkNo = tableParm.getValue("LINK_NO", m);
						if(fLinkNo.equals(kLinkNo)){
						String lcsClassCode  = tableParm.getValue("LCS_CLASS_CODE", m);
						if(!"".equals(lcsClassCode)&&!lcsClassCode.equals(null)){//找出主项，取出停用时间
							newRow = m;
						}
							 
						}
						
					}
					
				}
					TParm lcsParm = new TParm(
							getDBTool()
									.select(
											"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
													+ Operator.getID()
													+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
					String lcsClassCode = table.getParmValue().getValue(
							"LCS_CLASS_CODE", newRow);
					if (!OrderUtil.getInstance().checkLcsClassCode(lcsParm,
							Operator.getID(), "" + lcsClassCode)) {
						lcsParm.setErrCode(-1);
						// 您没有此医嘱的证照！
						this.messageBox("E0166");
						for (int i = 0; i < rowCount; i++) {
							if (!"".equals(linkNo)&&!linkNo.equals(null)&&
									linkNo.equals(tableParm.getValue("LINK_NO", i))&&i!=row) {
								tableParm.setData("CHOOSE_FLG", i, tableParm.getBoolean("CHOOSE_FLG", row) ? "N"
										: "Y");
							}
						}
						tableParm.setData("CHOOSE_FLG", row, "N");
						table.setParmValue(tableParm);
						return true;
					}	
				tableParm.setData("DC_DATE", row, StringTool.rollDate(
						SystemTool.getInstance().getDate(), tableParm.getInt(
								"ANTI_TAKE_DAYS", newRow)));// 停用时间
				tableParm.setData("DC_DR_CODE", row, Operator.getID());// 停用医生
				tableParm.setData("ANTIBIOTIC_WAY", row, "02");// 抗菌标识
				tableParm.setData("ORDER_DR_CODE", row, Operator.getID());
//				tableParm.setData("CHOOSE_FLG", newRow, "Y");
				for (int i = 0; i < rowCount; i++) {
					if (!"".equals(linkNo)&&!linkNo.equals(null)&&
							linkNo.equals(tableParm.getValue("LINK_NO", i))) {
//						tableParm.setData("CHOOSE_FLG", row, "Y");
						tableParm.setData("DC_DATE", i, StringTool.rollDate(
								SystemTool.getInstance().getDate(), tableParm.getInt(
										"ANTI_TAKE_DAYS", newRow)));// 停用时间
						tableParm.setData("DC_DR_CODE", i, Operator.getID());// 停用医生
						tableParm.setData("ANTIBIOTIC_WAY", i, "02");// 抗菌标识
						tableParm.setData("ORDER_DR_CODE", i, Operator.getID());
						tableParm.setData("CHOOSE_FLG", i, "Y");
					}
				}
			} else {
				tableParm.setData("DC_DATE", row, "");// 停用时间
				tableParm.setData("DC_DR_CODE", row, "");// 停用医生
				tableParm.setData("ANTIBIOTIC_WAY", row, "");// 抗菌标识
				tableParm.setData("ORDER_DR_CODE", row, vsDcCode);
				tableParm.setData("CHOOSE_FLG", newRow, "N");
				for (int i = 0; i < rowCount; i++) {
					if (!"".equals(linkNo)&&!linkNo.equals(null)&&
							linkNo.equals(tableParm.getValue("LINK_NO", i))) {
						tableParm.setData("DC_DATE", i, "");// 停用时间
						tableParm.setData("DC_DR_CODE", i, "");// 停用医生
						tableParm.setData("ANTIBIOTIC_WAY", i, "");// 抗菌标识
						tableParm.setData("ORDER_DR_CODE", i, vsDcCode);
						tableParm.setData("CHOOSE_FLG", i, "N");
					}
				}
			}
			table.setParmValue(tableParm);
		}
		if (columnName.equals("MEDI_QTY") || columnName.equals("FREQ_CODE")) {
			if(setValueTableParm(tableParm, row, columnName, tNode))
				return true;
			table.setParmValue(tableParm);
		}
		// this.messageBox("===============888888888===================");
		// EXEC_DEPT_CODE
		return false;
	}
	
	
	private boolean setValueTableParm(TParm tableParm,int row,String columnName,TTableNode tNode){
		if (tableParm.getValue("ORDER_CODE", row).length() == 0) {
			// 请录入医嘱！
			// ============xueyf modify 20120217 start
			if (Float.valueOf(tableParm.getValue("MEDI_QTY", row)) > 0) {
				this.messageBox("E0157");
			}
			// ============xueyf modify 20120217 stop
			this.messageBox("000");
			return true;
		}
		if (tableParm.getValue("CAT1_TYPE", row).equals("PHA")) {
			// 首日量计算
			if (columnName.equals("MEDI_QTY")) {
				tableParm.setData("MEDI_QTY", row, tNode.getValue());
			}
			if (columnName.equals("FREQ_CODE")) {
				// 判断频次是否可以在临时使用
				tableParm.setData("FREQ_CODE", row, tNode.getValue());
			}
			TParm action = this.getTempStartQty(tableParm.getRow(row));
			if (action.getErrCode() < 0) {
				// 抗生素设置
				if (action.getErrCode() == -2) {
					if (messageBox("提示信息 Tips",
							"管制药品用量超过标准是否按照此用量设置? \n Qty Overproof",
							this.YES_NO_OPTION) != 0)
						this.messageBox("111");
						return true;
				} else {// shibl 20130123 modify 总用量未回传
					this.messageBox(action.getErrText());
					this.messageBox("222");
					return true;
				}
			}
			tableParm.setData("MEDI_UNIT",row,action.getValue("MEDI_UNIT"));
			tableParm.setData("DISPENSE_QTY",row,action.getDouble("DISPENSE_QTY"));
			tableParm.setData("DOSAGE_UNIT",row,action.getValue("DOSAGE_UNIT"));
			tableParm.setData("DISPENSE_UNIT",row,action.getValue("DISPENSE_UNIT"));
			tableParm.setData("ACUMMEDI_QTY",row,action.getDouble("ACUMMEDI_QTY"));
			tableParm.setData("ACUMDSPN_QTY",row,action.getDouble("ACUMDSPN_QTY"));
			tableParm.setData("DOSAGE_QTY",row,action.getDouble("DOSAGE_QTY"));
			tableParm.setData("LASTDSPN_QTY",row,action.getDouble("LASTDSPN_QTY"));
			tableParm.setData("START_DTTM",row,action.getTimestamp("START_DTTM"));
		}
		this.messageBox("3333");
		return false;
	}
	/**
	 * 查询方法
	 */
	public void onQuery() {
		
		String selSql = "SELECT 'N' AS MODIFY_FLG,'N' AS CHOOSE_FLG,A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,"
				+ "A.FREQ_CODE,A.ROUTE_CODE,'N' AS URGENT_FLG,'N' AS DISPENSE_FLG,"
				+ "'' AS DR_NOTE,SYSDATE AS EFF_DATEDAY,'' AS DC_DATE,'' AS NS_NOTE,"
				+ "B.INSPAY_TYPE,A.ORDER_DATE,C.DOSAGE_QTY AS DISPENSE_QTY,C.DOSAGE_UNIT,"
				+ "C.DOSAGE_UNIT AS DISPENSE_UNIT,"
				+ "ANTI_MAX_DAYS AS ANTI_TAKE_DAYS,ANTI_MAX_DAYS AS TAKE_DAYS,A.PHA_SEQ," 
				+ "A.SEQ_NO,A.ORDER_CODE,B.LCS_CLASS_CODE,C.DOSAGE_QTY,'PHA' CAT1_TYPE,'' START_DTTM "
				+ " FROM PHA_ANTI A ,SYS_FEE B ,PHA_TRANSUNIT C " 
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDER_CODE=C.ORDER_CODE "
				+ "AND A.CASE_NO = '"
				+ caseNo
				+ "' AND A.OVERRIDE_FLG = 'Y' AND A.CHECK_FLG = 'N' ORDER BY LINK_NO,LINKMAIN_FLG DESC ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(selSql));

		for (int i = 0; i < parm.getCount(); i++) {
			// 取出抗菌药物的使用天数
			parm.setData("ANTIBIOTIC_WAY", i, "");// 抗菌标识
			parm.setData("EXEC_DEPT_CODE", i, orgCode);//执行科室
			parm.setData("ORDER_DR_CODE", i, Operator.getID());// 开立医生
			parm.setData("NS_CHECK_CODE", i, "");//确认护士
			parm.setData("NS_CHECK_DATE", i, "");// 确认时间
			parm.setData("DC_RSN_CODE", i, "");// 停用原因
			parm.setData("DC_NS_CHECK_CODE", i, "");// 停用确认护士
			parm.setData("DC_NS_CHECK_DATE", i, "");// 停用确认时间
			parm.setData("ORDER_STATE", i, "N");// 医嘱状态（N）
			parm.setData("ACUMMEDI_QTY", i, 0);// 累积用量
			parm.setData("ACUMDSPN_QTY", i, 0);// 累积配药
			parm.setData("DC_DATE", i, "");// 停用时间
			parm.setData("DC_DR_CODE", i, "");// 停用医生
			parm.setData("ANTIBIOTIC_WAY", i, "");// 抗菌标识
		}
		// 为表格赋值
		table.setParmValue(parm);
	}

	/**
	 * 得到TABLE对象
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		// 取得表格的值
		TTable table1 = (TTable) getComponent("TABLE1");
		table1.acceptText();
		TParm tableParm = table1.getParmValue();
		TParm saveParm = new TParm();
		boolean flg = false;
		// 调用存储过程拿到ORDER_NO
		String orderNo = SystemTool.getInstance().getNo("ALL", "ODI",
				"ORDER_NO", "ORDER_NO");
		int orderSeq = 0;
		//=====连嘱号的设置   yanjing 20140619
		String linkNo = "9999";
		String linkNoSql = "SELECT LINK_NO FROM ODI_ORDER "
			+ "WHERE CASE_NO = '" +caseNo + "' AND RX_KIND = 'UD' AND LINK_NO IS NOT NULL "
			+ " ORDER BY LINK_NO DESC  ";
//		System.out.println("action sql is :"+linkNoSql);
	    TParm linkNoResult = new TParm(TJDODBTool.getInstance().select(linkNoSql));
	    if (linkNoResult.getErrCode() < 0) {
		    return ;
	    }
	    if(linkNoResult.getCount()<=0){
	    	linkNo = "0";
	    	
	    }else{
//	    	 linkNo =  String.valueOf(Integer.parseInt(linkNoResult.getValue("LINK_NO",0))+1);
	    	linkNo = linkNoResult.getValue("LINK_NO",0);
	    }
	    String tempLinkNo = linkNo;
	    //=====连嘱号的设置   yanjing 20140619  END
	    for(int m = 0;m < tableParm.getCount();m++){//整理保存的参数
	    	String rLinkNo = tableParm.getValue("LINK_NO", m);
	    	if("".equals(rLinkNo)||rLinkNo.equals(null)){
				tempLinkNo = linkNo;
				linkNo = "";
			}else if(!tableParm.getValue("MODIFY_FLG",m).equals("N")){
				linkNo = tempLinkNo;
			}
	    	if(tableParm.getValue("CHOOSE_FLG", m).equals("Y")&&
	    			tableParm.getValue("LINKMAIN_FLG", m).equals("Y")){//连嘱的主项修改其及其细项的连嘱号
				//=====取出其连嘱号
	    		
				
				linkNo =  String.valueOf(Integer.parseInt(linkNo)+1);
				tempLinkNo = linkNo;
				//=====循环遍历找出连嘱号相同的医嘱，并修改连嘱号
				for(int j = 0;j<tableParm.getCount();j++){
					String nLinkNo = tableParm.getValue("LINK_NO", j);
					if(rLinkNo.equals(nLinkNo)&&tableParm.getValue("MODIFY_FLG", j).equals("N")&&j!=m){//修改连嘱号
						tableParm.setData("LINK_NO", j,linkNo);
						tableParm.setData("MODIFY_FLG", j,"Y");//修改过的标记
					}
				}
				tableParm.setData("LINK_NO", m,linkNo);
				tableParm.setData("MODIFY_FLG", m,"Y");//修改过的标记
				
			}
	    	
	    }
	    
		for (int i = 0; i < tableParm.getCount(); i++) {
			// 取出是否勾选的状态
			String isSave = tableParm.getValue("CHOOSE_FLG", i);
			if (isSave.equals("Y")) {
				String bedSql = "SELECT BED_NO FROM SYS_BED WHERE BED_NO_DESC = '"
						+ bedNo + "'";
				TParm bedNoParm = new TParm(TJDODBTool.getInstance().select(
						bedSql));
				String catSql = "SELECT A.CAT1_TYPE,A.ORDER_CAT1_CODE,B.ANTIBIOTIC_CODE " +
						"FROM SYS_FEE A ,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.ORDER_CODE = '"
						+ tableParm.getValue("ORDER_CODE", i) + "'";
				TParm catTypeParm = new TParm(TJDODBTool.getInstance().select(
						catSql));
				flg = true;
				// 取出该行的数据
				saveParm = tableParm.getRow(i);
				saveParm.setData("REGION_CODE", Operator.getRegion());
				saveParm.setData("ORDER_NO", orderNo);
				saveParm.setData("ANTIBIOTIC_CODE", catTypeParm.getValue(
						"ANTIBIOTIC_CODE", 0));
				saveParm.setData("ORDER_SEQ", ++orderSeq);
				saveParm.setData("CASE_NO", caseNo);//
				saveParm.setData("MR_NO", mrNo);//
				saveParm.setData("IPD_NO", ipdNo);// 住院号
				saveParm.setData("BED_NO", bedNoParm.getValue("BED_NO", 0));// 床号
				saveParm.setData("STATION_CODE", stationNo);// 病区
				saveParm.setData("RX_KIND", "UD");//
				saveParm.setData("DEPT_CODE", deptCode);//
				saveParm.setData("VS_DR_CODE", vsDcCode);//
				saveParm.setData("ORDER_DEPT_CODE", deptCode);//
				saveParm.setData("INDV_FLG", "Y");//
				saveParm.setData("HIDE_FLG", "N");//
				saveParm.setData("ORDER_CAT1_CODE", catTypeParm.getValue(
						"ORDER_CAT1_CODE", 0));//
				saveParm.setData("CAT1_TYPE", catTypeParm.getValue("CAT1_TYPE",
						0));//
				
				saveParm.setData("EXEC_DEPT_CODE", orgCode);// 执行科室
				saveParm.setData("SETMAIN_FLG", "N");//
				saveParm.setData("CHECK_FLG", "Y");//
				saveParm.setData("USE_FLG", "Y");//
				saveParm.setData("OPT_DATE", SystemTool.getInstance().getDate());//
				saveParm.setData("OPT_TERM", Operator.getIP());//
				saveParm.setData("OPT_USER", Operator.getID());

				// 向odi_order表中写数据，修改pha_anti表中的状态位action中
//				System.out.println("入参 入参saveParm saveParm  is：："+saveParm);
				TParm result = TIOM_AppServer.executeAction(
						"action.odi.ODIAction", "onSaveToOdi", saveParm);
				if (result.getErrCode() < 0) {
					this.messageBox("保存失败");
					return;
				}
			}
		}
		if (!flg) {
			this.messageBox("请选择要保存的数据。");
			return;
		} else {
			this.messageBox("保存成功。");
			onQuery() ;
			return;
		}

	}

	/**
	 * checkBox值改变事件
	 * 
	 * @param obj
	 */
	public boolean onCheckBoxValue(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		return true;
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		this.onQuery();
	}

	/**
	 * 计算临时首日量
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getTempStartQty(TParm parm) {

		TParm result = new TParm();
		// 累用量
		parm.setData("ACUMDSPN_QTY", 0);
		// 累计开药量
		parm.setData("ACUMMEDI_QTY", 0);
		String effDate = StringTool.getString(parm.getTimestamp("ORDER_DATE"),
				"yyyyMMddHHmmss");
		// 拿到配药起日和迄日
		// List dispenseDttm =
		// TotQtyTool.getInstance().getDispenseDttmArrange(effDate);
		List dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(
				parm.getTimestamp("ORDER_DATE"));
		// System.out.println("===dispenseDttm==="+dispenseDttm);
		if (StringUtil.isNullList(dispenseDttm)) {
			result.setErrCode(-1);
			// 参数有错误！
			result.setErrText("E0024");
			return result;
		}
		// this.messageBox("配药时间起日:"+dispenseDttm.get(0)+"配药时间迄日:"+dispenseDttm.get(1));
		// 计算首日量
		// this.messageBox_(parm);
		TParm selLevelParm = new TParm();
		selLevelParm.setData("CASE_NO", this.caseNo);
		// System.out.println(""+this.caseNo);
		// =============pangben modify 20110512 start 添加参数
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			selLevelParm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop

		// System.out.println("==selLevelParm=="+selLevelParm);
		TParm selLevel = ADMInpTool.getInstance().selectall(selLevelParm);
		// System.out.println("selLevel==="+selLevel+"====="+selLevel.getValue("SERVICE_LEVEL",
		// 0));
		String level = selLevel.getValue("SERVICE_LEVEL", 0);
		parm.setData("RX_KIND", "UD");
		List startQty = TotQtyTool.getInstance().getOdiStQty(effDate,
				parm.getValue("DC_DATE"), dispenseDttm.get(0).toString(),
				dispenseDttm.get(1).toString(), parm, level);
		// this.messageBox_(startQty);
		// System.out.println(""+startQty);
		// 首餐时间 START_DTTM
		List startDate = (List) startQty.get(0);
		// System.out.println("======startDate====="+startDate);
		// 其他必要参数//order表的LASTDSPN_QTY ORDER_LASTDSPN_QTY
		// order表的ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
		// order表的ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
		// M表的dispenseQty M_DISPENSE_QTY
		// M表的dispenseUnit M_DISPENSE_UNIT
		// M表的dosageQty M_DOSAGE_QTY
		// M表的dosageUnit M_DOSAGE_UNIT
		// D表的MediQty D_MEDI_QTY
		// D表的MediUnit D_MEDI_UNIT
		// D表的dosageQty D_DOSAGE_QTY
		// D表的dosageUnit D_DOSAGE_UNIT
		Map otherData = (Map) startQty.get(1);
		// System.out.println("===otherData==="+otherData);
		if (StringUtil.isNullList(startDate)
				&& (otherData == null || otherData.isEmpty())) {
			result.setErrCode(-1);
			result.setErrText("E0024");
			return result;
		}

		// 首餐时间表
		result.setData("START_DTTM_LIST", startDate);
		// 首餐时间
		// this.messageBox_(startDate.get(0).toString()+":"+startDate.get(0).getClass());
		result.setData("START_DTTM", StringTool.getTimestamp(startDate.get(0)
				.toString(), "yyyyMMddHHmm"));
		// 首日量
		result.setData("FRST_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
		// 最近配药量
		result.setData("LASTDSPN_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
		// 累用量
		result.setData("ACUMDSPN_QTY", otherData.get("ORDER_ACUMDSPN_QTY"));
		// 累计开药量
		result.setData("ACUMMEDI_QTY", otherData.get("ORDER_ACUMMEDI_QTY"));
		// 发药数量 / 实际退药入库量《盒或是片》
		result.setData("DISPENSE_QTY", otherData.get("M_DISPENSE_QTY"));
		// 总量单位
		result.setData("DISPENSE_UNIT", otherData.get("M_DISPENSE_UNIT"));
		// 配药数量、实际扣库数量
		result.setData("DOSAGE_QTY", otherData.get("M_DOSAGE_QTY"));
		// 配药单位 < 实际扣库量 >
		result.setData("DOSAGE_UNIT", otherData.get("M_DOSAGE_UNIT"));
		// 开药数量
		result.setData("MEDI_QTY", otherData.get("D_MEDI_QTY"));
		// 开药单位
		result.setData("MEDI_UNIT", otherData.get("D_MEDI_UNIT"));
		// 发药数量
		result.setData("DOSAGE_QTY", otherData.get("D_DOSAGE_QTY"));
		// 发药单位、调配单位
		result.setData("DOSAGE_UNIT", otherData.get("D_DOSAGE_UNIT"));
		// 毒麻药是否超量
		if (!OrderUtil.getInstance().checkKssPhaQty(parm)) { // shibl 20130123
			// modify 总用量未回传
			result.setErrCode(-2);
			return result;
		}
		return result;
	}
}
