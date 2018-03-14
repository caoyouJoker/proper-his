package com.javahis.ui.ins;

//import java.sql.Timestamp;
//import java.util.Date;

import jdo.ins.INSTJTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
//import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.DateTool;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
//import com.dongyang.ui.TTextFormat;
//import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
//import com.javahis.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * <p>
 * Title: 备案管理
 * </p>
 *
 * <p>
 * Description:备案管理
 * </p>
 *
 * <p>
 * Copyright: Copyright (c)
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangs 20140820
 * @version 1.0
 */
public class INS_Item_RegControl extends TControl {
	private static final String TParm = null;
	private static TTable InsItemRegTable;
	private static TComboBox INSTYPE;
	private TParm regionParm; // 医保区域代码
	private TTextField START;
	private TTextField END;
	private TComboBox REG_TYPE;
	private TComboBox APPROVE_TYPE;
	private TComboBox ORDER_TYPE;
	private TTextFormat REG_CLASS;
	private TComboBox DUL_FLG;

	public void onInit() {
		InsItemRegTable = (TTable)getComponent("INSITEMREG");
		INSTYPE=(TComboBox)getComponent("INS_TYPE");
		INSTYPE.setSelectedIndex(0);
		START=(TTextField)getComponent("START");
		END=(TTextField)getComponent("END");
		REG_TYPE=(TComboBox)getComponent("REG_TYPE");
		APPROVE_TYPE=(TComboBox)getComponent("APPROVE_TYPE");
		ORDER_TYPE=(TComboBox)getComponent("ORDER_TYPE");
		REG_CLASS = (TTextFormat) getComponent("REG_CLASS");
		REG_CLASS.setValue("1");
		DUL_FLG=(TComboBox)getComponent("DUL_FLG");
		DUL_FLG.setSelectedIndex(0);
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码
	}

	public void onQuery() {
		if(!onIsNull()){
			return;
		}
		onInsItemRegTable_Q(this.getValueString("INS_TYPE"),this.getValueString("REG_TYPE"),
				            this.getValueString("APPROVE_TYPE"),this.getValueString("ORDER_TYPE"),
				            "");
	}
	public void onClear() {
		InsItemRegTable.removeRowAll();
		INSTYPE.setSelectedIndex(0);
		///////////////////////////////
		START.setValue("");
		END.setValue("");
		REG_TYPE.setValue("");
		APPROVE_TYPE.setValue("");
		ORDER_TYPE.setValue("");
		REG_CLASS.setValue("1");
		DUL_FLG.setSelectedIndex(0);
	}

	/**
	 * 导出Excel
	 * */
	public void onExport() {
		TTable table = (TTable) callFunction("UI|INSITEMREG|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "备案信息");
	}
	/**
	 * 导出再用医保码txt
	 * */
	public void onExportTxt() throws Exception{
		String Sql =
			" SELECT Z.NHI_CODE FROM ( "+
			"   SELECT A.NHI_CODE_O NHI_CODE,A.ORDER_CAT1_CODE,A.ORDER_CODE "+
			"   FROM SYS_FEE_HISTORY A "+
			"   WHERE TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') BETWEEN A.START_DATE AND A.END_DATE "+
			"   AND A.ACTIVE_FLG='Y' "+
			"   AND A.CAT1_TYPE='PHA' "+
			"   UNION "+
			"   SELECT A.NHI_CODE_E NHI_CODE,A.ORDER_CAT1_CODE,A.ORDER_CODE "+
			"   FROM SYS_FEE_HISTORY A "+
			"   WHERE TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') BETWEEN A.START_DATE AND A.END_DATE "+
			"   AND A.ACTIVE_FLG='Y' "+
			"   AND A.CAT1_TYPE='PHA' "+
			"   UNION "+
			" 	SELECT A.NHI_CODE_I NHI_CODE,A.ORDER_CAT1_CODE,A.ORDER_CODE "+
			" 	FROM SYS_FEE_HISTORY A "+
			" 	WHERE TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') BETWEEN A.START_DATE AND A.END_DATE "+
			" 	AND A.ACTIVE_FLG='Y' "+
			"   AND A.CAT1_TYPE='PHA' ) Z "+
			" WHERE Z.NHI_CODE IS NOT NULL "+
			" GROUP BY Z.NHI_CODE "+
			" ORDER BY Z.NHI_CODE ";

//			 System.out.println("regSql==="+Sql);
			TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

			if (tabParm.getCount("NHI_CODE") < 0) {
				this.messageBox("没有要查询的数据！");
				return;
			}
			this.writeTxtFile(tabParm);
	}
	// 取得备案信息
	public void onInsItemRegTable_Q(String ins_type,String reg_type,
			                        String approve_type,String order_type,
			                        String nhi_code) {
	String Sql =
//		" SELECT 'N' CHOOSE,NHI_CODE,NHI_ORDER_DESC,ORDER_CODE,ORDER_DESC, "+
//		" CASE WHEN INS_TYPE='01' THEN '城职' "+
//		" WHEN INS_TYPE='02' THEN '城乡' END AS INS_TYPE_DESC, "+
//		" CHANGE_DATE,  "+
//		" CASE WHEN REG_TYPE='1' THEN '新增' "+
//		" WHEN REG_TYPE='2' THEN '变更' "+
//		" WHEN REG_TYPE='3' THEN '终止' "+
//		" WHEN REG_TYPE='4' THEN '删除' END AS REG_TYPE, "+
//		" CASE WHEN APPROVE_TYPE='0' THEN '未审批' "+
//		" WHEN APPROVE_TYPE='1' THEN '分中心初审' "+
//		" WHEN APPROVE_TYPE='2' THEN '市中心复审' "+
//		" WHEN APPROVE_TYPE='3' THEN '初审不通过' "+
//		" WHEN APPROVE_TYPE='8' THEN '新录入' "+
//		" WHEN APPROVE_TYPE='9' THEN '已上传' END AS APPROVE_TYPE_DESC, "+
//		" PRICE,OWN_PRICE,TOT_QTY,START_DATE,END_DATE, "+
//		" CASE WHEN ORDER_TYPE='PHA' THEN '药品' "+
//		" WHEN ORDER_TYPE='MAT' THEN '材料' "+
//		" WHEN ORDER_TYPE='TRT' THEN '处置' "+
//		" WHEN ORDER_TYPE='OTH' THEN '其他' END AS ORDER_TYPE,APPROVE_TYPE,INS_TYPE,B.CHN_DESC REG_CLASS,C.CHN_DESC DISEASE_CODE "+
//		" FROM INS_ITEM_REG A,SYS_DICTIONARY B,SYS_DICTIONARY C "+
//		" WHERE INS_TYPE='"+ins_type+"' "+
//		" AND B.ID=A.REG_CLASS(+) "+
//		" AND (B.GROUP_ID = 'INS_REG_CLASS' OR B.GROUP_ID IS NULL) "+
//		" AND C.ID=A.DISEASE_CODE(+) "+
//		" AND C.GROUP_ID = 'SIN_DISEASE'  OR B.GROUP_ID IS NULL ";
		" SELECT Z.CHOOSE,Z.NHI_CODE,Z.NHI_ORDER_DESC,Z.ORDER_CODE,Z.ORDER_DESC,Z.INS_TYPE_DESC,  Z.CHANGE_DATE,   Z.REG_TYPE, Z.APPROVE_TYPE_DESC,  Z.PRICE,Z.OWN_PRICE,Z.TOT_QTY,Z.START_DATE,Z.END_DATE, Z.ORDER_TYPE,Z.APPROVE_TYPE,Z.INS_TYPE,Z.REG_CLASS,C.CHN_DESC DISEASE_CODE "+
		" FROM (SELECT 'N' CHOOSE,NHI_CODE,NHI_ORDER_DESC,ORDER_CODE,ORDER_DESC,  CASE WHEN INS_TYPE='310' THEN '城职'  WHEN INS_TYPE='390' THEN '城乡' END AS INS_TYPE_DESC,  CHANGE_DATE,   CASE WHEN REG_TYPE='1' THEN '新增'  WHEN REG_TYPE='2' THEN '变更'  WHEN REG_TYPE='3' THEN '终止'  WHEN REG_TYPE='4' THEN '删除' END AS REG_TYPE,  CASE WHEN APPROVE_TYPE='0' THEN '未审批'  WHEN APPROVE_TYPE='1' THEN '分中心初审'  WHEN APPROVE_TYPE='2' THEN '市中心复审'  WHEN APPROVE_TYPE='3' THEN '初审不通过'  WHEN APPROVE_TYPE='8' THEN '新录入'  WHEN APPROVE_TYPE='9' THEN '已上传' END AS APPROVE_TYPE_DESC,  PRICE,OWN_PRICE,TOT_QTY,START_DATE,END_DATE,  CASE WHEN ORDER_TYPE='PHA' THEN '药品'  WHEN ORDER_TYPE='MAT' THEN '材料'  WHEN ORDER_TYPE='TRT' THEN '处置'  WHEN ORDER_TYPE='OTH' THEN '其他' END AS ORDER_TYPE,APPROVE_TYPE,INS_TYPE,B.CHN_DESC REG_CLASS ,A.DISEASE_CODE "+
		" FROM INS_ITEM_REG A ,SYS_DICTIONARY B  "+
		" WHERE A.INS_TYPE='"+ins_type+"' ";
	if(!reg_type.equals("")){
		Sql = Sql+"AND A.REG_TYPE='"+reg_type+"' ";
	}
	if(!approve_type.equals("")){
		Sql = Sql+"AND A.APPROVE_TYPE='"+approve_type+"' ";
	}
	if(!order_type.equals("")){
		Sql = Sql+"AND A.ORDER_TYPE='"+order_type+"' ";
	}
	if(!nhi_code.equals("")){
		Sql = Sql+"AND A.NHI_CODE='"+nhi_code+"' ";
	}
	Sql = Sql+" AND B.ID(+)=A.REG_CLASS AND (B.GROUP_ID = 'INS_REG_CLASS' OR B.GROUP_ID IS NULL)) Z ,SYS_DICTIONARY C  "+
		" WHERE C.ID(+)=Z.DISEASE_CODE AND (C.GROUP_ID = 'SIN_DISEASE' OR C.GROUP_ID IS NULL)  "+
		" ORDER BY Z.ORDER_CODE ";
//	if(!reg_type.equals("")){
//		Sql = Sql+"AND A.REG_TYPE='"+reg_type+"' ";
//	}
//	if(!approve_type.equals("")){
//		Sql = Sql+"AND A.APPROVE_TYPE='"+approve_type+"' ";
//	}
//	if(!order_type.equals("")){
//		Sql = Sql+"AND A.ORDER_TYPE='"+order_type+"' ";
//	}
//	if(!nhi_code.equals("")){
//		Sql = Sql+"AND A.NHI_CODE='"+nhi_code+"' ";
//	}
//	if(!RegClass.equals("")){
//		Sql = Sql+"AND REG_CLASS='"+RegClass+"' ";
//	}
//		 System.out.println("regSql==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("NHI_CODE") < 0) {
			this.messageBox("没有要查询的数据！");
			onClear();
			return;
		}
//		System.out.println("tabParm==="+tabParm);
		InsItemRegTable.setParmValue(tabParm);
	}
	// 取得备案信息
	public void onRegisterItemTable_DQ() {
		if(!onIsNull()){
			return;
		}
		this.onInsItemRegTable_Q(this.getValueString("INS_TYPE"),"","","",
				                 this.getValueString("NHI_CODE"));
	}

	/**
	 * 备案信息下载
	 */
	public void onInsItemRegDown() {
		TParm tableParm = null;
		TParm newParm = new TParm(); // 累计数据
		InsItemRegTable.acceptText();
		boolean flg=true;
		TParm parmValue = InsItemRegTable.getParmValue();
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			if (tableParm.getValue("CHOOSE").equals("N")) {
				continue;
			}
			TParm parm = onGetDown(onQuery(tableParm.getValue("NHI_CODE"),getValueString("INS_TYPE"),tableParm.getValue("START_DATE")));
			TParm splitParm = INSTJTool.getInstance().DataDown_zjkd_M(parm);
//			System.out.println("DataDown_zjkd_M:"+splitParm);
			if (!INSTJTool.getInstance().getErrParm(splitParm)) {
				this.messageBox(parmValue.getValue("NHI_DESC", i) +"\n"+splitParm.getErrText()+ "\n下载失败");
				flg=false;
				continue;
			}
			String sql=" UPDATE INS_ITEM_REG SET "+
			   " NHI_ORDER_DESC='"+splitParm.getValue("NHI_ORDER_DESC",0)+
			   "',APPROVE_TYPE='"+splitParm.getValue("APPROVE_TYPE",0)+
//			   "',TYPE='"+splitParm.getValue("TYPE",0)+
			   "',END_DATE='"+splitParm.getValue("END_DATE",0)+"',PRICE="+splitParm.getValue("PRICE",0)+", "+
               " OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' "+
		       " WHERE INS_TYPE='"+getValueString("INS_TYPE")+"' AND NHI_CODE='"+tableParm.getValue("NHI_CODE")+"' AND START_DATE='"+tableParm.getValue("START_DATE")+"' ";
//			System.out.println("onInsItemRegDown_sql:"+sql);
			TParm result = new TParm(TJDODBTool.getInstance().update(sql));

			if (result.getErrCode() < 0) {
//				this.messageBox("E0005");
				flg=false;
			}
		}


			if (flg) {
				this.messageBox("下载成功");
			}else{
				this.messageBox("请重新下载");
			}
	}

	/**
	 * 备案信息新增
	 */
	public void onInsItemRegInsert() {
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INS_ItemRegItem.x", null);
	}
	/**
	 * 备案信息修改
	 */
	public void onInsItemRegUpdate() {
		TParm parm=new TParm();
		int row=InsItemRegTable.getSelectedRow();
		TParm tableParm=InsItemRegTable.getParmValue();
//		parm.setData("NHI_CODE", tableParm.getRow(row).getValue("NHI_CODE"));
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INS_ItemRegItem.x", tableParm.getRow(row));
	}
	/**
	 * 空值检查
	 */
	public boolean onIsNull() {
		if(getValueString("INS_TYPE").equals("")){
			this.messageBox("请选择医保类型");
			return false;
		}
		return true;
	}

	/**
	 * 备案信息上传
	 */
	public void onInsItemRegUp() {

		TParm tableParm = null;
//		TParm newParm = new TParm(); // 累计数据
		InsItemRegTable.acceptText();
		boolean flg=true;
		TParm parmValue = InsItemRegTable.getParmValue();
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			if (tableParm.getValue("CHOOSE").equals("N")
//					||(!tableParm.getValue("APPROVE_TYPE").equals("2"))
					) {
				continue;
			}

			if(tableParm.getValue("REG_TYPE").equals("2")){
				this.messageBox("变更备案信息请双击项目,在打开的窗口内操作");
				flg=false;
				continue;
			}
			TParm parmOrder =onQuery(tableParm.getValue("NHI_CODE"),getValueString("INS_TYPE"),tableParm.getValue("START_DATE"));
			TParm parm = onGetUpload(parmOrder);
//			System.out.println("DataDown_zjks_X:"+parm);
			TParm splitParm = INSTJTool.getInstance().DataDown_zjks_X(parm);
//			System.out.println("onInsItemRegUp:"+splitParm);
			if (!INSTJTool.getInstance().getErrParm(splitParm)) {
				this.messageBox(parmValue.getValue("NHI_DESC", i) +"\n"+splitParm.getErrText()+ "\n上传失败");
				flg=false;
				continue;
			}
//			newParm.addData("NHI_CODE", tableParm.getValue("NHI_CODE"));
//			newParm.addData("INS_TYPE", tableParm.getValue("INS_TYPE"));
//			newParm.addData("START_DATE", tableParm.getValue("START_DATE"));
			String sql=" UPDATE INS_ITEM_REG SET "+
	           " APPROVE_TYPE='3', "+
               " OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' "+
               " WHERE INS_TYPE='"+tableParm.getValue("INS_TYPE")+"' AND NHI_CODE='"+tableParm.getValue("NHI_CODE")+"' AND START_DATE='"+tableParm.getValue("START_DATE")+"' ";
//	        System.out.println("onInsItemRegDown_sql:"+sql);
	        TParm result = new TParm(TJDODBTool.getInstance().update(sql));

	        if (result.getErrCode() < 0) {
        		flg=false;
	        }
//	        if(parmOrder.getValue("REG_TYPE").equals("2")&&
//					(!parmOrder.getValue("APPROVE_TYPE").equals("2"))){
//	        	parmOrder.setData("START_DATE", parmOrder.getValue("CHANGE_DATE"));
//	        	parmOrder.setData("REG_TYPE", "1");
//	        	parmOrder.setData("OPT_USER", Operator.getID());
//	        	parmOrder.setData("OPT_TERM", Operator.getIP());
//				this.insert(parm);
//
//			}
		}

			if (flg) {
				this.messageBox("上传成功");
			}else{
				this.messageBox("上传完成,请查看报错的医嘱信息");
			}
	}

	/**
	 * 查询备案信息
	 *
	 * @return TParm
	 */
	private TParm onQuery(String nhi_code,String ins_type,String change_date) {
		String Sql = " SELECT NHI_CODE,NHI_ORDER_DESC,ORDER_CODE,ORDER_DESC, "+
	    " INS_TYPE,CHANGE_DATE,REG_TYPE,APPROVE_TYPE, "+
	    " PRICE,OWN_PRICE,TOT_QTY,START_DATE,END_DATE,ORDER_TYPE,REG_CLASS,DISEASE_CODE "+
	    " FROM INS_ITEM_REG "+
	    " WHERE NHI_CODE='"+ nhi_code + "' " +
		" AND INS_TYPE='"+ins_type+"' "+
		" AND START_DATE='"+change_date+"' ";
//		" AND REG_CLASS='"+RegClass+"' ";
//		 System.out.println("regSql==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("NHI_CODE") < 0) {
			this.messageBox("没有查询到相应记录");
			return null;
		}
		return tabParm;
	}

	private TParm onGetUpload(TParm tabParm) {
		TParm parm = new TParm();
		parm.addData("INS_TYPE", tabParm.getValue("INS_TYPE", 0)); // 险种
		parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
		parm.addData("NHI_CODE", tabParm.getValue("NHI_CODE", 0)); // 收费项目编码
		parm.addData("START_DATE", tabParm.getValue("START_DATE", 0)); // 开始时间
		parm.addData("REG_TYPE", tabParm.getValue("REG_TYPE", 0)); // 备案状态
		parm.addData("CHANGE_DATE", tabParm.getValue("CHANGE_DATE", 0)); //变更/终止时间
		parm.addData("PRICE", tabParm.getValue("PRICE", 0)); // 实际价格
		parm.addData("OWN_PRICE", tabParm.getValue("OWN_PRICE", 0)); // 采购价格
		parm.addData("TOT_QTY", tabParm.getValue("TOT_QTY", 0)); // 采购数量
		parm.addData("OPT_USER", Operator.getName()); // 操作员
		parm.addData("REG_CLASS", tabParm.getValue("REG_CLASS",0));//备案类别
		parm.addData("DISEASE_CODE", tabParm.getValue("DISEASE_CODE",0));//病种编码
		parm.addData("DUL_FLG", "0");//双轨制标志
		parm.addData("PARM_COUNT", 13);
		return parm;
	}
	private TParm onGetDown(TParm tabParm) {
		TParm parm = new TParm();
		parm.addData("INS_TYPE", tabParm.getValue("INS_TYPE", 0)); // 险种
		parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
		parm.addData("NHI_CODE", tabParm.getValue("NHI_CODE", 0)); // 收费项目编码
		parm.addData("START_DATE", tabParm.getValue("START_DATE", 0)); // 开始时间
		parm.addData("PARM_COUNT", 4);
		return parm;
	}
//	private String getUpDateFromat(String str){
//		return str.substring(0, 4)+"/"+str.substring(4, 6)+"/"+str.substring(6, 8);
//	}
	public void setSelectCheck(){
		int start=Integer.parseInt(START.getValue());
		int end=Integer.parseInt(END.getValue());
		this.onQuery();
		TParm parmValue = InsItemRegTable.getParmValue();
		int count=parmValue.getCount("CHOOSE");
		if(end>count){
		   end=count;
		}
		for (int i = start-1; i < end; i++) {
			parmValue.setData("CHOOSE", i, "Y");
		}
		InsItemRegTable.setParmValue(parmValue);
	}
	public void setAllSelect(){
		TParm parmValue = InsItemRegTable.getParmValue();
		int count=parmValue.getCount("CHOOSE");
		for (int i = 0; i < count; i++) {
			parmValue.setData("CHOOSE", i, "Y");
		}
		InsItemRegTable.setParmValue(parmValue);
	}
	/**
	 * 备案信息删除
	 */
	public void onDelete() {
		if (this.messageBox("信息", "删除被选中的备案记录,是否继续", 2) == 1) {
			return;
		}
		TParm tableParm = null;
		TParm newParm = new TParm(); // 累计数据
		InsItemRegTable.acceptText();
		boolean flg=true;
		TParm parmValue = InsItemRegTable.getParmValue();
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			if (tableParm.getValue("CHOOSE").equals("N")){
				continue;
			}
			if((!tableParm.getValue("APPROVE_TYPE").equals("8"))&&
					(!tableParm.getValue("APPROVE_TYPE").equals("3"))){
			    this.messageBox(tableParm.getValue("ORDER_CODE")+"已上传项目不得删除");
			    flg=false;
			    continue;
			}

			String sql=" DELETE FROM INS_ITEM_REG "+
                       " WHERE INS_TYPE='"+tableParm.getValue("INS_TYPE")+
                       "' AND ORDER_CODE='"+tableParm.getValue("ORDER_CODE")+
                       "' AND NHI_CODE='"+tableParm.getValue("NHI_CODE")+
                       "' AND START_DATE='"+tableParm.getValue("START_DATE")+"' ";
//                       "' AND REG_CLASS='"+tableParm.getValue("REG_CLASS")+"' ";

	     TParm result = new TParm(TJDODBTool.getInstance().update(sql));

	     if (result.getErrCode() < 0) {
		     flg=false;
	     }
		}
          if(flg){
        	  this.messageBox("删除成功");
        	  this.onQuery();
          }

	}
	/**
	 * 取得插入数据
     * @param fileName
	 * @return
	 */
//	private TParm onGetSaveDate() {
//		TParm parm = new TParm();
//		parm.setData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
//		parm.setData("INS_TYPE", this.getValueString("INS_TYPE")); // 医保类型
//		parm.setData("NHI_CODE", NHI_CODE.getValue()); // 收费项目编码
//		parm.setData("NHI_ORDER_DESC", NHI_ORDER_DESC.getValue()); // 收费项目名称
//		parm.setData("ORDER_CODE", ORDER_CODE.getValue()); // 院内医嘱编码
//		parm.setData("ORDER_DESC", ORDER_DESC.getValue()); // 院内医嘱名称
//		parm.setData("START_DATE", START_DATE.getValue().toString().substring(0, 10).replace("-", "")); // 开始时间
//		parm.setData("REG_TYPE", this.getValueString("REG_TYPE")); //备案状态
//		parm.setData("PRICE", PRICE.getValue()); // 实际价格
//		parm.setData("ORDER_TYPE", this.getValueString("ORDER_TYPE")); //医嘱类别
//		parm.setData("OPT_USER", Operator.getID());
//		parm.setData("OPT_TERM", Operator.getIP());
//		parm.setData("APPROVE_TYPE", "2");
//		return parm;
//	}
	/**
	 * 插入新记录
     * @param fileName
	 * @return
	 */
//	private boolean insert(TParm saveData) {
//		TParm result = TIOM_AppServer.executeAction(
//				"action.ins.INS_Item_RegAction", "onInsertInsItemReg",
//				saveData);
//		if (result.getErrCode() < 0) {
//			this.messageBox("E0005");
//			return false;
//		}
//		return true;
//	}
	/**
	 * 创建文件
     * @param fileName
	 * @return
	 */
//	public static boolean createFile(File fileName)throws Exception{
//	  boolean flag=false;
//	  try{
//	   if(!fileName.exists()){
//	    fileName.createNewFile();
//	    flag=true;
//	   }
//	  }catch(Exception e){
//	   e.printStackTrace();
//	  }
//	  return true;
//	 }
	/**
	 * 写文件
     * @param fileName
	 * @return
	 */
	public boolean writeTxtFile(TParm content)
			throws Exception {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt",
				"txt");
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(filter);
		fc.setMultiSelectionEnabled(false);
		int result = fc.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (!file.getPath().endsWith(".txt")) {
				file = new File(file.getPath() + ".txt");
			}
//			System.out.println("file path=" + file.getPath());
			FileOutputStream fos = null;
			try {
				if (!file.exists()) {// 文件不存在 则创建一个
					file.createNewFile();
				}
				fos = new FileOutputStream(file);
				int count=content.getCount("NHI_CODE");
				for(int i=0;i<count;i++){
				  fos.write((content.getValue("NHI_CODE",i)+"\r\n").getBytes());
				}
				fos.flush();
			} catch (IOException e) {
				System.err.println("文件创建失败：");
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;

	}


		//////////////////////////////////////////////////////////
	   /**
     * 通过excel备案信息
     * excel第一行为表头，应包含：医嘱类别，医保项目编码，医保项目名称，院内医嘱编码，院内医嘱名称，
     *                           开始时间，备案状态，实际价格，医嘱类别
     * 各列的顺序可变
     * 所有信息必须存在excel的第一个sheet页中
     */
	public void onInsertPatByExl() {// refactor by wanglong 20130116
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileFilter() {// 过滤xls文件 add by
																// wanglong20130116

					public boolean accept(File f) {
						if (f.isDirectory()) {// 忽略文件夹
							return true;
						}
						return f.getName().endsWith(".xls");
					}

					public String getDescription() {
						return ".xls";
					}
				});
		int option = fileChooser.showOpenDialog(null);
		TParm parm = new TParm();
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				Workbook wb = Workbook.getWorkbook(file);
				Sheet st = wb.getSheet(0);
				int row = st.getRows();
				int column = st.getColumns();
				if (row <= 1 || column <= 0) {
					this.messageBox("excel中没有数据");
					return;
				}
				StringBuffer wrongMsg = new StringBuffer();
				wrongMsg.append("excel文件名为：" + file.getName() + "\r\n");
				String[] title = new String[column];
				for (int j = 0; j < column; j++) {
					String cell = st.getCell(j, 0).getContents();
					if (cell.indexOf("医保类型") != -1) {
						title[j] = "INS_TYPE";
						continue;
					}
					if (cell.indexOf("医保项目编码") != -1) {
						title[j] = "NHI_CODE";
						continue;
					}
					if (cell.indexOf("医保项目名称") != -1) {
						title[j] = "NHI_ORDER_DESC";
						continue;
					}
					if (cell.indexOf("院内医嘱编码") != -1) {
						title[j] = "ORDER_CODE";
						continue;
					}
					if (cell.indexOf("院内医嘱名称") != -1) {
						title[j] = "ORDER_DESC";
						continue;
					}
					if (cell.indexOf("开始时间") != -1) {
						title[j] = "START_DATE";
						continue;
					}
					if (cell.indexOf("备案状态") != -1) {
						title[j] = "REG_TYPE";
						continue;
					}
					if (cell.indexOf("实际价格") != -1) {
						title[j] = "PRICE";
						continue;
					}
					if (cell.indexOf("医嘱类别") != -1) {
						title[j] = "ORDER_TYPE";
						continue;
					}
				}
				List<String> titleList = Arrays.asList(title);
				if (!titleList.contains("INS_TYPE")) {// 医保类型
					this.messageBox("缺少“医保类型(01 城职医保;02 城乡医保)”列！");
					return;
				}
				if (!titleList.contains("NHI_CODE")) {// 医保项目编码
					this.messageBox("缺少“ 医保项目编码”列！");
					return;
				}
				if (!titleList.contains("NHI_ORDER_DESC")) {// 医保项目名称
					this.messageBox("缺少“医保项目名称”列！");
					return;
				}
				if (!titleList.contains("ORDER_CODE")) {// 院内医嘱编码
					this.messageBox("缺少“院内医嘱编码”列！");
					return;
				}
				if (!titleList.contains("ORDER_DESC")) {// 院内医嘱名称
					this.messageBox("缺少“院内医嘱名称”列！");
					return;
				}
				if (!titleList.contains("START_DATE")) {// 开始时间
					this.messageBox("缺少“开始时间(例:20140901)”列！");
					return;
				}
				if (!titleList.contains("REG_TYPE")) {// 备案状态
					this.messageBox("缺少“备案状态(1 新增;2 变更;3 终止;4 删除)”列！");
					return;
				}
				if (!titleList.contains("PRICE")) {// 实际价格
					this.messageBox("缺少“实际价格”列！");
					return;
				}
				if (!titleList.contains("ORDER_TYPE")) {// 医嘱类别
					this.messageBox("缺少“医嘱类别(PHA 药品;MAT 材料;TRT 处置;OTH 其他)”列！");
					return;
				}
//				int count = 0;
				for (int i = 1; i < row; i++) {// 一行一行加入excel中的数据
//					for (int j = 0; j < column; j++) {// 每次导入一行的所有列
//						String cell = st.getCell(j, i).getContents();
//						parm.addData(title[j], cell.trim());
//					}
					if(st.getCell(1, i).getContents().equals("")){
						continue;
					}
					String sql= "INSERT INTO INS_ITEM_REG A "+
					" (INS_TYPE, NHI_CODE, NHI_ORDER_DESC, ORDER_CODE, ORDER_DESC, START_DATE, REG_TYPE, PRICE, OPT_USER, OPT_TERM, OPT_DATE, APPROVE_TYPE, ORDER_TYPE) VALUES "+
					" ('"+st.getCell(0, i).getContents()+"', '"+st.getCell(1, i).getContents()+"','"+st.getCell(2, i).getContents()+"','"+st.getCell(3, i).getContents()+"','"+
					st.getCell(4, i).getContents()+"','"+st.getCell(5, i).getContents()+"','"+st.getCell(6, i).getContents()+"',"+st.getCell(7, i).getContents()+", '"+
					Operator.getID()+"', '"+Operator.getIP()+"', SYSDATE, '2', '"+st.getCell(8, i).getContents()+"') ";

					TParm result = new TParm(TJDODBTool.getInstance().update(sql));

				     if (result.getErrCode() < 0) {
				    	 this.messageBox_(st.getCell(1, i).getContents()+
				    			 st.getCell(2, i).getContents()+"导入失败");
				    	 continue;
				     }
				}
//				if (count < 1) {
//					this.messageBox_("有效数据少于一行，导入操作终止");
//					return;
//				}
			} catch (BiffException e) {
				this.messageBox_("excel文件操作出错");
				e.printStackTrace();
				return;
			} catch (IOException e) {
				this.messageBox_("打开文件出错");
				e.printStackTrace();
				return;
			}
		}
		this.messageBox_("导入完成");
	}

}
