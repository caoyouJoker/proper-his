package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.poi.util.SystemOutLogger;
import org.mortbay.jetty.security.SSORealm;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 物联网耗用记录查询</p>
 *
 * <p>Description: 物联网耗用记录查询</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2013</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author caowl
 * @version 1.0
 */
public class BILSPCINVRecordSelControl extends TControl{

	
	TTable Table;
	
	//action的路径
	private static final String actionName = "action.bil.SPCINVRecordAction";
	
	/**
	 * 初始化
	 * */
	 public void onInit() {
	        super.onInit();	       
	        //获得全部控件
	        getAllComponent(); 
	      
	 }
	 
	 //获得全部控件
	 public void getAllComponent(){
		 Table = (TTable)this.getComponent("Table");
		 //初始化时间段  昨天到今天
//		 Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
//                    getDate(), -1);
////		 setValue("S_TIME", yesterday.toString().substring(0,10)+"00:00:00");
////		 setValue("E_TIME", SystemTool.getInstance().getDate().toString().substring(0,10)+"23:59:59");
//		 setValue("S_TIME", yesterday);
//		 setValue("E_TIME", SystemTool.getInstance().getDate());
		 Timestamp now = StringTool.getTimestamp(new Date());
		 this.setValue("S_TIME",
		 		now.toString().substring(0, 10).replace('-', '/') + " 00:00:00");// 开始时间
		 this.setValue("E_TIME",
		 		now.toString().substring(0, 10).replace('-', '/') + " 23:59:59");// 结束时间
		 	
		 setValue("DEPT_CODE",Operator.getDept());
		 setValue("OPT_USER","");
		 
		 TParm parm = new TParm();
		 // 设置弹出菜单
	     getTextField("INV_CODE").setPopupMenuParameter("UD",
	         getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),parm);
		 // 定义接受返回值方法
	     getTextField("INV_CODE").addEventListener(
	         TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	 }
		/**
		 * 得到TextField对象
		 */
		private TTextField getTextField(String tagName) {
			return (TTextField) getComponent(tagName);
		}
		/**
		 * 接受返回值方法
		 *
		 * @param tag
		 * @param obj
		 */
		public void popReturn(String tag, Object obj) {
		    TParm parm = (TParm) obj;
		    if (parm == null) {
		            return;
		    }
		    String order_code = parm.getValue("INV_CODE");
		      if (!StringUtil.isNullString(order_code))
		          getTextField("INV_CODE").setValue(order_code);
		      String order_desc = parm.getValue("INV_CHN_DESC");
		      if (!StringUtil.isNullString(order_desc))
		            getTextField("INV_DESC").setValue(order_desc);
		}
	 /**
	  * 补齐病案号
	  * */
	 public void onMrNo(){
		 String mrNo =PatTool.getInstance().checkMrno(
					TypeTool.getString(getValue("MR_NO")));
		 this.setValue("MR_NO", mrNo);
		 
		   //modify by huangtt 20160928 EMPI患者查重提示  start     
         Pat pat = Pat.onQueryByMrNo(mrNo);       
 		 if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
 	            this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
 	          setValue("MR_NO", pat.getMrNo());
 	         mrNo = pat.getMrNo();
 	     }
        //modify by huangtt 20160928 EMPI患者查重提示  end
		 

		 TParm parm = new TParm();
		 parm.setData("MR_NO",mrNo);
		 parm.setData("ADM_TYPE","I");
		 
		 TParm selParm = TIOM_AppServer.executeAction(actionName, "onMrNo", parm);
		 System.out.println("查询结果："+selParm);
		 if(selParm.getCount("CASE_NO")<0){
			 this.messageBox("查无此病人！");
			 return;
		 }
		 Timestamp sysDate = SystemTool.getInstance().getDate();
		
//		 Date date = new Date();
//		 SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		 String time = df1.format(date);
//		 Timestamp CreateDate = Timestamp.valueOf(time);
		 String birthDate = selParm.getData("BIRTH_DATE",0).toString().substring(0,19).replace("-", "/");
		 //System.out.println("birthDate--->"+birthDate);
		 Timestamp birth_date = new Timestamp(Date.parse(birthDate));
		 
		 
		 Timestamp temp = birth_date == null ? sysDate: birth_date;
		
		// 计算年龄
		String age = "0";
		if (birth_date != null)
			age = OdiUtil.getInstance().showAge(temp,sysDate);
		
		else
			age = "";
		selParm.addData("AGE", age);
		
		//this.setValue("IPD_NO", selParm.getValue("IPD_NO", 0));
		this.setValue("PAT_NAME", selParm.getValue("PAT_NAME", 0));
		this.setValue("AGE", selParm.getValue("AGE", 0));
		this.setValue("SEX_CODE", selParm.getValue("SEX_CODE", 0));
		//this.setValue("DEPT_CODE", selParm.getValue("DEPT_CODE",0));
		//this.setValue("STATION_CODE", selParm.getValue("STATION_CODE",0));
		//this.setValue("DR_CODE", selParm.getValue("DR_CODE",0));
		//this.setValue("CASE_NO", selParm.getValue("CASE_NO", 0));
	 }
		
	 /**
	  * 查询
	  * */
	 public void onQuery()
		{
			String sTime = getValueString("S_TIME");
			String eTime = getValueString("E_TIME");
			String deptCode = getValueString("DEPT_CODE");
			String optUser = getValueString("OPT_USER");
			String class_code = getValueString("CLASS_CODE");
			String inv_code = getValueString("INV_CODE");
			String billFlg = getValueString("BILL_FLG");
			String mr_no = getValueString("MR_NO");
			String sValidDate = getValueString("S_VALID_DATE");
			String eValidDate = getValueString("E_VALID_DATE");
			String batchSeq = getValueString("BATCH_SEQ");
			String opRoom = getValueString("OP_ROOM");//手术间
			String sqlWhere = "";
			if (sTime == null || sTime.equals("") || eTime == null || eTime.equals(""))
			{
				messageBox("请选择开始和结束时间！");
				return;
			}
			if (deptCode != null && !deptCode.equals(""))
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND A.EXE_DEPT_CODE = '").append(deptCode).append("'").toString();
			if (optUser != null && !optUser.equals(""))
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND A.OPT_USER = '").append(optUser).append("'").toString();
			if (mr_no != null && !mr_no.equals(""))
			{
				String mrNo = PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
				setValue("MR_NO", mrNo);
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND A.MR_NO = '").append(mrNo).append("'").toString();
			}
			if (billFlg != null && !billFlg.equals(""))
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND A.BILL_FLG = '").append(billFlg).append("'").toString();
			if (class_code != null && !class_code.equals(""))
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND A.CLASS_CODE = '").append(class_code).append("'").toString();
			if (inv_code != null && !inv_code.equals(""))
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND A.INV_CODE = '").append(inv_code).append("'").toString();
			if(sValidDate != null && !sValidDate.equals("") && eValidDate != null && !eValidDate.equals("")){
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND A.VALID_DATE BETWEEN TO_DATE('").append(sValidDate.substring(0, 19)).append("','yyyy-MM-dd HH24:Mi:ss') ").append(" AND TO_DATE('").append(eValidDate.substring(0, 19)).append("','yyyy-MM-dd HH24:Mi:ss')").append(sqlWhere).toString();
			}
			if(batchSeq != null && !batchSeq.equals("")){
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND A.BATCH_SEQ = '").append(batchSeq).append("'").toString();
			}
			if(opRoom != null && !opRoom.equals("")){
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND A.OP_ROOM = '").append(opRoom).append("'").toString();
			}
			String sql = (new StringBuilder("SELECT A.*, B.DESCRIPTION, C.PAT_NAME AS PAT FROM SPC_INV_RECORD A,INV_BASE B, SYS_PATINFO C  WHERE A.INV_CODE=B.INV_CODE AND A.MR_NO=C.MR_NO AND A.OPT_DATE BETWEEN TO_DATE('")).append(sTime.substring(0, 19)).append("','yyyy-MM-dd HH24:Mi:ss') ").append("        AND TO_DATE('").append(eTime.substring(0, 19)).append("','yyyy-MM-dd HH24:Mi:ss')")
				.append(sqlWhere).append(" ORDER BY A.INV_DESC,A.INV_CODE ").toString();
			
			//System.out.println("sql---->"+sql);
			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
			TParm tableParm = new TParm();
			int count = selParm.getCount();
			if (count < 0)
			{
				messageBox("没有要查询的数据");
				Table.removeAll();
				return;
			}
			double qtySum = 0.0D;
			double arAmtSum = 0.0D;
			for (int i = 0; i < count; i++)
			{
				tableParm.addData("MR_NO", selParm.getValue("MR_NO", i));
				tableParm.addData("PAT_NAME", selParm.getValue("PAT", i));
				tableParm.addData("INV_CODE", selParm.getValue("INV_CODE", i));
				if (selParm.getValue("INV_DESC", i).equals(""))
					tableParm.addData("DESC", selParm.getValue("ORDER_DESC", i));
				else
					tableParm.addData("DESC", selParm.getValue("INV_DESC", i));
				tableParm.addData("DESCRIPTION", selParm.getValue("DESCRIPTION", i));
				tableParm.addData("QTY", selParm.getValue("QTY", i));
				qtySum += Double.parseDouble(selParm.getValue("QTY", i).toString());
				tableParm.addData("UNIT_CODE", selParm.getValue("UNIT_CODE", i));
				tableParm.addData("BILL_FLG", selParm.getValue("BILL_FLG", i));
				tableParm.addData("OWN_PRICE", selParm.getValue("OWN_PRICE", i));
				tableParm.addData("AR_AMT", selParm.getValue("AR_AMT", i));
				arAmtSum += Double.parseDouble(selParm.getValue("AR_AMT", i).toString());
				tableParm.addData("HEXP_CODE", selParm.getValue("ORDER_CODE", i));
				tableParm.addData("HEXP_DESC", selParm.getValue("ORDER_DESC", i));
				tableParm.addData("OP_ROOM", selParm.getValue("OP_ROOM", i));
				tableParm.addData("DEPT_CODE", selParm.getValue("DEPT_CODE", i));
				tableParm.addData("VALID_DATE", selParm.getValue("VALID_DATE",i).substring(0, 19));
				tableParm.addData("BATCH_SEQ", selParm.getValue("BATCH_SEQ",i));
				tableParm.addData("BILL_USER", selParm.getValue("OPT_USER",i));
			}

			tableParm.addData("MR_NO", "总计：");
			tableParm.addData("PAT_NAME", "");
			tableParm.addData("INV_CODE", "");
			tableParm.addData("INV_DESC", "");
			tableParm.addData("DESCRIPTION", "");
			tableParm.addData("QTY", Double.valueOf(qtySum));
			tableParm.addData("UNIT_CODE", "");
			tableParm.addData("BILL_FLG", "");
			tableParm.addData("OWN_PRICE", "");
			tableParm.addData("AR_AMT", Double.valueOf(arAmtSum));
			tableParm.addData("HEXP_CODE", "");
			tableParm.addData("HEXP_DESC", "");
			tableParm.addData("OP_ROOM", "");
			tableParm.addData("DEPT_CODE", "");
			tableParm.addData("VALID_DATE", "");
			tableParm.addData("BATCH_SEQ", "");
			tableParm.addData("BILL_USER", "");
			callFunction("UI|Table|setParmValue", tableParm);
		}
//	 public void onQuery(){
//		 System.out.println("------------查询开始 --------------");
//		 String sTime = this.getValueString("S_TIME");
//		 String eTime = this.getValueString("E_TIME");
//		 String deptCode = this.getValueString("DEPT_CODE");
//		 String optUser =this.getValueString("OPT_USER");
//		 String class_code = this.getValueString("CLASS_CODE");
//		 String bar_code = this.getValueString("BAR_CODE");
//		// String mrNo = this.getValueString("MR_NO");
//		 String billFlg = this.getValueString("BILL_FLG");
//		 String mr_no = this.getValueString("MR_NO");
//		 String sqlWhere = "";
//		 if(sTime == null || sTime.equals("") || eTime == null || eTime.equals("") ){
//			this.messageBox("请选择开始和结束时间！");
//			return;
//		 }
//		 if(deptCode != null && !deptCode.equals("")){
//			 sqlWhere += " AND EXE_DEPT_CODE = '"+deptCode+"'";
//		 }
//		 if(optUser != null && !optUser.equals("")){
//			 sqlWhere += " AND OPT_USER = '"+optUser+"'";
//		 }
//		 if(mr_no != null && !mr_no.equals("")){
//			 String mrNo =PatTool.getInstance().checkMrno(
//						TypeTool.getString(getValue("MR_NO")));
//			 this.setValue("MR_NO", mrNo);
//			 sqlWhere += " AND MR_NO = '"+mrNo+"'";
//		 }
//		 if(billFlg != null && !billFlg .equals("")){
//			 sqlWhere += " AND BILL_FLG = '"+billFlg+"'";
//		 }
//		 if(null != class_code && !class_code.equals("")){
//			 sqlWhere += " ADN CLASS_CODE = '"+class_code+"'";
//		 }
//		 
//		 if(null != bar_code && !bar_code.equals("")){
//			 sqlWhere += " AND BAR_CODE = '"+bar_code+"'";
//		 }
//		 
//		 String sql = "SELECT * " +
//		 		" FROM SPC_INV_RECORD A " +
//		 		" WHERE OPT_DATE BETWEEN TO_DATE('"+sTime.substring(0, 19)+"','yyyy-MM-dd HH24:Mi:ss') " +
//		 				"        AND TO_DATE('"+eTime.substring(0,19)+"','yyyy-MM-dd HH24:Mi:ss')"+sqlWhere;
//		 System.out.println("sql------->"+sql);
//		 TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
//		 TParm tableParm = new TParm();
//		 int count = selParm.getCount();
//		 if(count<0){
//			 this.messageBox("没有要查询的数据");
//			 return;
//		 }
//		 double qtySum = 0;
//		 double arAmtSum = 0;
// 		 //BAR_CODE;DESC;QTY;UNIT_CODE;BILL_FLG;OWN_PRICE;AR_AMT;HEXP_CODE;HEXP_DESC;OP_ROOM;DEPT_CODE
//		 for(int i=0;i<count;i++){
//			 tableParm.addData("BAR_CODE", selParm.getValue("BAR_CODE", i));
//			 if(selParm.getValue("INV_DESC", i).equals("")){
//				 tableParm.addData("DESC", selParm.getValue("ORDER_DESC", i));
//			 }else{
//				 tableParm.addData("DESC", selParm.getValue("INV_DESC", i));
//			 }
////			 if(!(selParm.getValue("INV_CODE",i)== null || selParm.getValue("INV_CODE",i).equals(""))){
////				 String invCode = selParm.getValue("INV_CODE",i);
////				 String sqlDescr = "SELECT DISTINCT DESCRIPTION FROM INV_BASE WHERE INV_CODE = '"+invCode+"'"; 
////				 System.out.println("sqlDescr----------->"+sqlDescr);
////				 TParm descrParm = new TParm(TJDODBTool.getInstance().select(sqlDescr));
////				 tableParm.addData("DESCRIPTION", descrParm.getValue("DESCRIPTION",0));
////			 }else{
////				 tableParm.addData("DESCRIPTION", "");
////			 }
//			
//			 tableParm.addData("QTY", selParm.getValue("QTY", i));
//			 qtySum += Double.parseDouble(selParm.getValue("QTY",i).toString());
//			 tableParm.addData("UNIT_CODE", selParm.getValue("UNIT_CODE", i));
//			 tableParm.addData("BILL_FLG", selParm.getValue("BILL_FLG", i));
//			 tableParm.addData("OWN_PRICE", selParm.getValue("OWN_PRICE", i));
//			 tableParm.addData("AR_AMT", selParm.getValue("AR_AMT", i));
//			 arAmtSum += Double.parseDouble(selParm.getValue("AR_AMT",i).toString());
//			 tableParm.addData("HEXP_CODE", selParm.getValue("ORDER_CODE", i));
//			 tableParm.addData("HEXP_DESC", selParm.getValue("ORDER_DESC", i));
//			 tableParm.addData("OP_ROOM", selParm.getValue("OP_ROOM", i));
//			 tableParm.addData("DEPT_CODE", selParm.getValue("DEPT_CODE", i));
//		 }
//		 System.out.println("---------总计之前----------");
//		 //添加总计
//		 tableParm.addData("BAR_CODE", "总计：");
//		 tableParm.addData("INV_DESC", "");
//		 tableParm.addData("DESCRIPTION", "");
//		 tableParm.addData("QTY",qtySum);
//		 tableParm.addData("UNIT_CODE", "");
//		 tableParm.addData("BILL_FLG", "");
//		 tableParm.addData("OWN_PRICE", "");
//		 tableParm.addData("AR_AMT",arAmtSum);
//		 tableParm.addData("HEXP_CODE", "");
//		 tableParm.addData("HEXP_DESC", "");
//		 tableParm.addData("OP_ROOM", "");		
//		 tableParm.addData("DEPT_CODE", "");
//		 System.out.println("------------总计之后-----------");
//		 System.out.println("tableParm--->"+tableParm);
//		 TTable table = (TTable)this.getComponent("Table"); 
//		 table.setParmValue(tableParm);
//		// this.callFunction("UI|Table|setParmValue", tableParm);
//			
//	 }

	 /**
	  * 清空
	  * */
	 public void onClear(){
//		 Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
//                 getDate(), -1);
//		 setValue("S_TIME", yesterday);
//		 setValue("E_TIME", SystemTool.getInstance().getDate());
		//初始化时间
		Timestamp now = StringTool.getTimestamp(new Date());
		this.setValue("S_TIME",
			 	now.toString().substring(0, 10).replace('-', '/') + " 00:00:00");// 开始时间
		this.setValue("E_TIME",
			 	now.toString().substring(0, 10).replace('-', '/') + " 23:59:59");// 结束时间
			 	
		 setValue("S_VALID_DATE", "");
		 setValue("E_VALID_DATE", "");
		
		 this.setValue("OPT_USER", "");
		 this.setValue("CLASS_CODE", "");
		 this.setValue("INV_CODE", "");
		 this.setValue("INV_DESC", "");
		 this.setValue("BILL_FLG", "");
		 this.setValue("MR_NO", "");
		 this.setValue("BATCH_SEQ", "");
		 this.setValue("PAT_NAME", "");
		 this.setValue("AGE", "");
		 this.setValue("SEX_CODE", "");
		 this.setValue("DEPT_CODE", "");
		 Table.removeAll();
		 
		 
	 }
	 /**
	  * 导出excel
	  * */
	 public void onExcel(){
		 //得到UI对应控件对象的方法（UI|XXTag|getThis）
	        TTable table = (TTable) this.getComponent("Table");
	        if (table.getRowCount() > 0)
	            ExportExcelUtil.getInstance().exportExcel(table, "物联网耗用记录查询");
	 }
} 
	