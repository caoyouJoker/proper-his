package com.javahis.ui.bms;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.dongyang.control.TControl;
import jdo.bms.BMSBloodTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboBMSBldsubcat;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.SelectResult;

/**
 * <p>Title: 血品库存查询</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.10.8
 * @version 1.0
 */
public class BMSStockQtyQueryControl extends TControl {

	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;	

	private static String TABLE = "TABLE";	

	private String print_bld_type;  //血型

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 排序监听
		Timestamp date = StringTool.getTimestamp(new Date());
		Timestamp timeEnd =StringTool.getTimestamp(date.toString().substring(0, 10).replaceAll("-", "/") + " 23:59:59", "yyyy/MM/dd HH:mm:ss") ;
		this.setValue("END_DATE", timeEnd);
		Timestamp timeStart =StringTool.getTimestamp( StringTool.rollDate(date, -7).toString().substring(0, 10).replaceAll("-", "/") + " 00:00:00", "yyyy/MM/dd HH:mm:ss");
		this.setValue("START_DATE", timeStart);
		addListener(getTTable(TABLE));        
	}	

	public String getPrint_bld_type() {
		return print_bld_type;
	}

	public void setPrint_bld_type(String print_bld_type) {
		this.print_bld_type = print_bld_type;
	}
	/**
	 * 报废点击时设定时间区间
	 */
	public void onChoose() {
		if(getTCheckBox("CHK_2").isSelected()) {
			getTTextFormat("START_DATE").setEnabled(true);
			getTTextFormat("END_DATE").setEnabled(true);
		} else {
			getTTextFormat("START_DATE").setEnabled(false);
			getTTextFormat("END_DATE").setEnabled(false);
		}
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		//        TParm parm = new TParm();
		//        String bood_no = this.getValueString("BLOOD_NO");
		//        if (bood_no != null && bood_no.length() > 0) {
		//            parm.setData("BLOOD_NO", bood_no);
		//        }
		//        String bld_code = this.getValueString("BLD_CODE");
		//        if (bld_code != null && bld_code.length() > 0) {
		//            parm.setData("BLD_CODE", bld_code);
		//        }
		//        String bld_type = this.getValueString("BLD_TYPE");
		//        if (bld_type != null && bld_type.length() > 0) {
		//            parm.setData("BLD_TYPE", bld_type);
		//        }
		//        String subcat_code = this.getValueString("SUBCAT_CODE");
		//        if (subcat_code != null && subcat_code.length() > 0) {
		//            parm.setData("SUBCAT_CODE", subcat_code);
		//        }
		//        String end_date = this.getValueString("END_DATE");
		//        if (end_date != null && end_date.length() > 0) {
		//            parm.setData("END_DATE", end_date);
		//        }
		//        String state_code = this.getValueString("STATE_CODE");
		//        if (state_code != null && state_code.length() > 0) {
		//            parm.setData("STATE_CODE", state_code);
		//        }

		//        TParm result = BMSBloodTool.getInstance().onQueryBloodQtyStock(parm);
		this.setValue("BLO_VOL", "0");
		String sql1 = "WITH M AS (  SELECT A.BLD_CODE,"
				+ "          (CASE A.BLD_CODE"
				+ "           WHEN '01' THEN 7"
				+ "                 WHEN '02' THEN 7"
				+ "                   WHEN '05' THEN 30"
				+ "                    WHEN '06' THEN 1"
				+ "		                      WHEN '07' THEN 7"
				+ "		                      WHEN '08' THEN 7 / 24"
				+ "			                      WHEN '09' THEN 30"
				+ " WHEN '11' THEN 7"
				+ "   WHEN '12' THEN 30"
				+ "        WHEN '13' THEN 7 / 24"
				+ "         ELSE 0"
				+ "            END)"
				+ "                   AS DATE_1"
				+ "        FROM BMS_BLDCODE A"
				+ "    ORDER BY BLD_CODE) ";




		/*String sql=sql1+" SELECT A.BLOOD_NO, A.RH_FLG, A.BLD_CODE, A.SUBCAT_CODE,"+
				"        A.IN_DATE, A.BLD_TYPE, A.SHIT_FLG, A.END_DATE, A.IN_PRICE, A.BLOOD_VOL,"+ 
				"        A.ORG_BARCODE, A.STATE_CODE, A.APPLY_NO, A.MR_NO, A.IPD_NO,"+ 
				"        A.CASE_NO, A.ID_NO, A.USE_DATE, A.CROSS_MATCH_L, A.CROSS_MATCH_S,"+ 
				"        A.ANTI_A, A.ANTI_B, A.RESULT, A.TEST_DATE, A.TEST_USER,"+ 
				"        A.PRE_U, A.PRE_D, A.T, A.P, A.R,"+ 
				"        A.WORK_USER, A.OUT_NO, A.OUT_DATE, A.OUT_USER,"+ 
				"        A.TRAN_RESN,A.TRAN_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.DEPT_CODE ,B.PAT_NAME,D.UNIT_CHN_DESC,C.UNIT_CODE "+

	            // add by wangqing 20180118 start 新增血液到期提醒,颜色标注。到期红色，提醒黄色
                " ,(CASE WHEN A.END_DATE <= SYSDATE + (SELECT DISTINCT M.DATE_1 FROM M WHERE M.BLD_CODE=A.BLD_CODE) THEN 'Y' ELSE 'N' END) AS YELLOW_FLG, "

                + "(CASE WHEN A.END_DATE<=SYSDATE THEN 'Y' ELSE 'N' END) AS RED_FLG "+
                //add by wangqing 20180118 end

	       " FROM BMS_BLOOD A ,SYS_PATINFO B ,BMS_BLDSUBCAT C,SYS_UNIT D "+ 
	       " WHERE A.MR_NO = B.MR_NO(+) " +
	       
	       
	       "   AND A.BLD_CODE = C.BLD_CODE " +
	       "   AND A.SUBCAT_CODE = C.SUBCAT_CODE " +
         "   AND C.UNIT_CODE = D.UNIT_CODE " ;*/


           /*"   AND A.BLD_CODE = C.BLD_CODE(+) " +

           "   AND A.SUBCAT_CODE = C.SUBCAT_CODE(+) " +
 
           "   AND C.UNIT_CODE = D.UNIT_CODE(+) "*/ ;
           
           
           String sql=sql1+" SELECT A.BLOOD_NO, A.RH_FLG, A.BLD_CODE, A.SUBCAT_CODE,"+
   				"        A.IN_DATE, A.BLD_TYPE, A.SHIT_FLG, A.END_DATE, A.IN_PRICE, A.BLOOD_VOL,"+ 
   				"        A.ORG_BARCODE, A.STATE_CODE, A.APPLY_NO, A.MR_NO, A.IPD_NO,"+ 
   				"        A.CASE_NO, A.ID_NO, A.USE_DATE, A.CROSS_MATCH_L, A.CROSS_MATCH_S,"+ 
   				"        A.ANTI_A, A.ANTI_B, A.RESULT, A.TEST_DATE, A.TEST_USER,"+ 
   				"        A.PRE_U, A.PRE_D, A.T, A.P, A.R,"+ 
   				"        A.WORK_USER, A.OUT_NO, A.OUT_DATE, A.OUT_USER,"+ 
   				"        A.TRAN_RESN,A.TRAN_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.DEPT_CODE ,B.PAT_NAME,C.UNIT_CHN_DESC,C.UNIT_CODE "+

   	            // add by wangqing 20180118 start 新增血液到期提醒,颜色标注。到期红色，提醒黄色
                   " ,(CASE WHEN A.END_DATE <= SYSDATE + (SELECT DISTINCT M.DATE_1 FROM M WHERE M.BLD_CODE=A.BLD_CODE) AND A.END_DATE>=SYSDATE  THEN 'Y' ELSE 'N' END) AS YELLOW_FLG, "

                   + "(CASE WHEN A.END_DATE < SYSDATE THEN 'Y' ELSE 'N' END) AS RED_FLG "+
                   //add by wangqing 20180118 end

   	       " FROM BMS_BLOOD A ,SYS_PATINFO B , "
   	       
   	       + "(SELECT A.BLD_CODE, A.SUBCAT_CODE, A.UNIT_CODE, B.UNIT_CHN_DESC "
   	       + "FROM BMS_BLDSUBCAT A, SYS_UNIT B "
   	       + "WHERE A.UNIT_CODE=B.UNIT_CODE(+)) C "+ 
   	       
   	       
   	       " WHERE A.MR_NO = B.MR_NO(+) " +
   	       "   AND A.BLD_CODE = C.BLD_CODE(+) " +
   	       "   AND A.SUBCAT_CODE = C.SUBCAT_CODE(+) ";
		
		
		   
		StringBuilder sbuilder = new StringBuilder(sql) ;

		String bood_no = this.getValueString("BLOOD_NO");
		if (bood_no != null && bood_no.length() > 0) {
			sbuilder.append(" AND A.BLOOD_NO= '"+bood_no+"'") ;
		}
		String bld_code = this.getValueString("BLD_CODE");
		if (bld_code != null && bld_code.length() > 0) {
			sbuilder.append(" AND A.BLD_CODE= '"+bld_code+"'") ;
		}
		String bld_type = this.getValueString("BLD_TYPE");
		if (bld_type != null && bld_type.length() > 0) {
			sbuilder.append(" AND A.BLD_TYPE= '"+bld_type+"'") ;
			this.setPrint_bld_type(this.getText("BLD_TYPE"));
		}else {
			this.setPrint_bld_type("A AB B O 不确定");
		}
		String subcat_code = this.getValueString("SUBCAT_CODE");
		if (subcat_code != null && subcat_code.length() > 0) {
			sbuilder.append(" AND A.SUBCAT_CODE= '"+subcat_code+"'") ;
		}
		
		
		// test by wangqing 20180119 start
		String rhFlg = this.getValueString("RH_FLG");
		if (rhFlg != null && rhFlg.length() > 0) {
			sbuilder.append(" AND A.RH_FLG= '"+rhFlg+"'") ;
		}
		
		
		// test by wangqing 20180119 end
		

		//删除效期查询条件
		/*  String end_date = this.getValueString("END_DATE");
        if (end_date != null && end_date.length() > 0) {
    		String sDate = end_date.substring(0, 19) ;
    		sbuilder.append(" AND TO_CHAR(END_DATE,'YYYY-MM-DD HH:mm:ss') <= '"+sDate+"'") ;            
        }*/

		//如果报废选中，添加时间区间  add by wukai 20160714
		if(this.getTCheckBox("CHK_2").isSelected()) {
			String start_date = this.getValueString("START_DATE");
			if(start_date != null && start_date.length() > 0) {
				String sDate = start_date.substring(0, 19) ;
				sbuilder.append(" AND TO_CHAR(END_DATE,'YYYY-MM-DD HH:mm:ss') >= '"+sDate+"'");
			}
			String end_date = this.getValueString("END_DATE");
			if (end_date != null && end_date.length() > 0) {
				String sDate = end_date.substring(0, 19) ;
				sbuilder.append(" AND TO_CHAR(END_DATE,'YYYY-MM-DD HH:mm:ss') <= '"+sDate+"'") ;            
			}
			//System.out.println("start_data :::::: " + start_date + "    ?????   end_date :::: " + end_date);
		}

		//修改状态信息
		String state_code ="";
		for (int i = 0; i < 3; i++) {
			if(((TCheckBox)this.getComponent("CHK_"+i)).isSelected()){
				if(i == 2) {
					state_code+="'" + (i+1) + "',";
				} else {
					state_code+="'"+i+"',";
				}
			}
		}
		if (state_code != null && state_code.length() > 0) {
			sbuilder.append(" AND A.STATE_CODE IN( "+state_code.substring(0,state_code.lastIndexOf(","))+")") ;
		}  else {
			sbuilder.append(" AND A.STATE_CODE IN( '0', '1', '3' ) ");
		}
		sbuilder.append(" ORDER BY SUBCAT_CODE,BLD_TYPE,RH_FLG,END_DATE,STATE_CODE"); //add by wanglong 20121212
		//System.out.println("sbuilder::::"+sbuilder);
		TParm result = new TParm(TJDODBTool.getInstance().select(sbuilder.toString()));
		//   获得错误信息消息
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;      
		}    	
		if (result == null || result.getCount() <= 0) {
			this.messageBox("没有查询数据");
			this.callFunction("UI|TABLE|setParmValue", new TParm());
			return;
		}
		
		
		
		Set<String>  unitSet = new HashSet<String>() ;
		double bldVol = 0.0 ;
		/*for (int i = 0; i < result.getCount(); i++) {
			unitSet.add(result.getValue("UNIT_CHN_DESC", i)) ;
			//modify by wukai 添加对血品单位为1.5时  的判断
			String subcatcode = result.getValue("SUBCAT_CODE", i);
			double bld_vol = result.getDouble("BLOOD_VOL", i) ;
			//System.out.println( "bld_vol   :::::::   " + bld_vol);
			//System.out.println( "bld_vol equals 1   :::::::   " + (bld_vol == 1));
			if(bld_vol == 1 && "0203".equals(subcatcode) || "0803".equals(subcatcode) || "0702".equals(subcatcode)) {
				bldVol += bld_vol * 1.5;
			} else {
				bldVol += bld_vol;
			}
		}
		//System.out.println("BLO_VOL:::" + bldVol);
		//System.out.println("unitSet::::" + unitSet);
		if(unitSet.size()==1){
			this.setValue("BLO_VOL", String.valueOf(bldVol)) ;
			this.setValue("UNIT_LABEL", unitSet.iterator().next() ) ;
		}    	
		this.getTable("TABLE").setParmValue(result);*/
		
		// test by wangqing 20180119 start
		TCheckBox c = (TCheckBox) this.getComponent("YELLOW_FLG");
		TCheckBox cc = (TCheckBox) this.getComponent("RED_FLG");
		if(c.isSelected() && cc.isSelected()){
			for (int i = result.getCount()-1; i >= 0; i--) {
				if(result.getValue("YELLOW_FLG", i).equals("Y") || result.getValue("RED_FLG", i).equals("Y")){
					
				}else{	
					result.removeRow(i);
				}
			}
		}else if(!c.isSelected() && !cc.isSelected()){
			
		}else if(c.isSelected() && !cc.isSelected()){
			for (int i = result.getCount()-1; i >= 0; i--) {
				if(result.getValue("YELLOW_FLG", i).equals("Y")){
					
				}else{	
					result.removeRow(i);
				}
			}
		}else if(!c.isSelected() && cc.isSelected()){
			for (int i = result.getCount()-1; i >= 0; i--) {
				if(result.getValue("RED_FLG", i).equals("Y")){
					
				}else{	
					result.removeRow(i);
				}
			}
		}else{
			
		}
		
		// test by wangqing 20180119 end
		
		this.getTable("TABLE").setParmValue(result);
		for (int i = 0; i < result.getCount(); i++) {
			double bld_vol = result.getDouble("BLOOD_VOL", i) ;
			bldVol += bld_vol;
			unitSet.add(result.getValue("UNIT_CHN_DESC", i)) ;
			
		}
		this.setValue("BLO_VOL", String.valueOf(bldVol)) ;
		this.setValue("UNIT_LABEL", unitSet.iterator().next() ) ;
		
		
		
		
		
		
		
		
		// add by wangqing 20180118 start 新增血液到期提醒,颜色标注。到期红色，提醒黄色
		Color red = new Color(255, 0, 0);
		Color yellow = new Color(255, 255, 0);
		HashMap map = new HashMap();
		for(int i=0; i<result.getCount(); i++){
			if(result.getValue("RED_FLG", i).equals("Y")){
				map.put(i, red);
			}else if(result.getValue("YELLOW_FLG", i).equals("Y")){
				map.put(i, yellow);
			}			
		}
		if (map.size() > 0) {
			this.getTable("TABLE").setRowColorMap(map);
		}		
		// add by wangqing 20180118 end
		
	}

	/**
	 * add by wukai on 20160927
	 * 手动报废血品功能
	 */
	public void onScrap() {
		TTable table = this.getTable(TABLE);
		int row = table.getSelectedRow();
		if(row < 0) {
			this.messageBox("请选择一条报废");
			return;
		}
		TParm tableParm = table.getParmValue();
		String stateCode = String.valueOf(tableParm.getData("STATE_CODE", row));
		if("3".equals(stateCode)) {
			this.messageBox("此血品已报废！");
			return;
		}
		int res = this.messageBox("提示", "确定报废此血品？", OK_CANCEL_OPTION);
		if(res == 0) {
			String bloodNo = String.valueOf(tableParm.getData("BLOOD_NO", row));
			//this.messageBox( "bloodNo" + bloodNo);
			if(!StringUtils.isEmpty(bloodNo)) {
				String sql = "UPDATE BMS_BLOOD SET STATE_CODE = '3' WHERE BLOOD_NO = '" + bloodNo + "'" ;
				TJDODBTool.getInstance().update(sql);
				this.messageBox("报废成功!");
				onQuery();
			}
		}

	}



	/**
	 * 清空方法
	 */
	public void onClear() {
		String clearStr = "BLOOD_NO;BLD_CODE;BLD_TYPE;SUBCAT_CODE;START_DATE;END_DATE";
		this.clearValue(clearStr);
		getTable("TABLE").removeRowAll();
		for (int i = 0; i < 3; i++) {
			((TCheckBox)this.getComponent("CHK_"+i)).setSelected(false);
		}
		Timestamp date = StringTool.getTimestamp(new Date());
		Timestamp timeEnd =StringTool.getTimestamp(date.toString().substring(0, 10).replaceAll("-", "/") + " 23:59:59", "yyyy/MM/dd HH:mm:ss") ;
		this.setValue("END_DATE", timeEnd);
		Timestamp timeStart =StringTool.getTimestamp( StringTool.rollDate(date, -7).toString().substring(0, 10).replaceAll("-", "/") + " 00:00:00", "yyyy/MM/dd HH:mm:ss");
		this.setValue("START_DATE", timeStart);
		getTTextFormat("START_DATE").setEnabled(false);
		getTTextFormat("END_DATE").setEnabled(false);
		this.setValue("BLO_VOL", "0");
	}

	/**
	 * 变更血品
	 */
	public void onChangeBld() {
		String bld_code = getComboBox("BLD_CODE").getSelectedID();
		((TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).setBldCode(bld_code);
		((TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).onQuery();
	}

	/**
	 * 得到ComboBox对象
	 * @param tagName 元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * 得到Table对象
	 * @param tagName 元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 加入表格排序监听方法
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		//加入表格行选择功能
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = getTTable(TABLE).getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				//getTMenuItem("save").setEnabled(false);
				
				
				// add by wangqing 20180118 start 新增血液到期提醒,颜色标注。到期红色，提醒黄色
				TParm result = getTTable(TABLE).getParmValue();
				Color red = new Color(255, 0, 0);
				Color yellow = new Color(255, 255, 0);
				HashMap map = new HashMap();
				for(int i1=0; i1<result.getCount(); i1++){
					if(result.getValue("RED_FLG", i1).equals("Y")){
						map.put(i1, red);
					}else if(result.getValue("YELLOW_FLG", i1).equals("Y")){
						map.put(i1, yellow);
					}			
				}
				if (map.size() > 0) {
					getTable("TABLE").setRowColorMap(map);
				}		
				// add by wangqing 20180118 end
				
				
				
			}
		});
	}

	/**
	 * 得到 Vector 值
	 * @param group String 组名
	 * @param names String "ID;NAME"
	 * @param size int 最大行数
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable(TABLE).setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}	

	/**
	 * 拿到TABLE
	 * @param tag String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}	  

	/**
	 * 拿到TTextFormat
	 * @param tag String
	 * @return TTextFormat
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) this.getComponent(tag);
	}

	/**
	 * 
	 * @param tag
	 * @return
	 */
	public TCheckBox getTCheckBox (String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * 打印报表  add by wukai on 20160805
	 */
	public void onPrint(){
		TTable table = this.getTable(TABLE);
		if(table.getRowCount() <= 0) {
			this.messageBox("无可打印数据");
			return;
		}
		//表头数据
		TParm data = new TParm();
		data.setData("TITLE", "TEXT", "血品库存统计");
		data.setData("BLD_TYPE", "TEXT", getPrint_bld_type());
		data.setData("BLD_VOL", "TEXT", this.getValueString("BLO_VOL"));
		data.setData("BLD_UNIT", "TEXT", this.getValueString("UNIT_LABEL"));

		//表格数据"BLOOD_NO;BLD_CODE;SUBCAT_CODE;BLD_TYPE;RH_FLG;END_DATE;STATE_CODE";
		TParm parm = new TParm();
		TParm tableParm = table.getShowParmValue();
		for( int i = 0; i < table.getRowCount() ; i ++) {
			parm.addData("BLOOD_NO", tableParm.getData("BLOOD_NO", i));
			parm.addData("BLD_CODE", tableParm.getData("BLD_CODE", i));
			parm.addData("SUBCAT_CODE", tableParm.getData("SUBCAT_CODE", i));
			parm.addData("BLD_TYPE", tableParm.getData("BLD_TYPE", i));
			parm.addData("RH_FLG", tableParm.getData("RH_FLG", i));
			parm.addData("END_DATE", tableParm.getData("END_DATE", i));
			parm.addData("STATE_CODE", tableParm.getData("STATE_CODE", i));
		}
		parm.setCount(parm.getCount("BLOOD_NO"));
		parm.addData("SYSTEM","COLUMNS","BLOOD_NO");
		parm.addData("SYSTEM","COLUMNS","BLD_CODE");
		parm.addData("SYSTEM","COLUMNS","SUBCAT_CODE");
		parm.addData("SYSTEM","COLUMNS","BLD_TYPE");
		parm.addData("SYSTEM","COLUMNS","RH_FLG");
		parm.addData("SYSTEM","COLUMNS","END_DATE");
		parm.addData("SYSTEM","COLUMNS","STATE_CODE");
		data.setData("TABLE", parm.getData());

		//设置表尾数据
		data.setData("OPT_USER", "TEXT", Operator.getName());
		data.setData("OPT_TIME", "TEXT", SystemTool.getInstance().getDate().toString().substring(0, 10));
		//System.out.println( "print data :::::::   " + data);
		this.openPrintDialog("%ROOT%\\config\\prt\\bms\\BMSStockQtyQuery.jhw", data);
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		TTable table = this.getTable(TABLE);
		if(table.getRowCount() <= 0) {
			this.messageBox("无可导出数据！");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "血品库存统计表");
	}

}
