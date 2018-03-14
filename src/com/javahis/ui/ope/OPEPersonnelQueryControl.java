package com.javahis.ui.ope;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

import jdo.bil.BILComparator;
import jdo.ope.OPEPersonnelQueryTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;

import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;


/**
 * <p>Title: 手术排程查询</p>
 *
 * <p>Description: 手术排程查询</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-12-09
 * @version 4.0
 */
public class OPEPersonnelQueryControl extends TControl {
    private TParm data;//记录查询结果
    private TTable TABLE;
    TDataStore dataStore_Dept = new TDataStore();//部门
    TDataStore dataStore_User = new TDataStore();//人员
    TDataStore DICTIONARY = new TDataStore();//大字典
    private String BOOK_DEPT = "";//预约科室
    
	//===========排序功能==================add by wanglong 20121212
    private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	
    /**
     * 初始化
     */
    public void onInit(){
        super.onInit();
        TABLE = (TTable)this.getComponent("Table");
        addSortListener(TABLE);//===表格加排序监听=====add by wanglong 20121212
        dataStore_Dept.setSQL("SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT");
        dataStore_Dept.retrieve();
        dataStore_User.setSQL("SELECT USER_NAME,USER_ID FROM SYS_OPERATOR");
        dataStore_User.retrieve();
        DICTIONARY = TIOM_Database.getLocalTable("SYS_DICTIONARY");
        //获取当前时间
        this.setValue("DATE_S",SystemTool.getInstance().getDate());
        this.setValue("DATE_E",SystemTool.getInstance().getDate());
        callFunction("UI|Table|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this, "onClickBox");
    }
    
    /**
     * 查询
     */
	public void onQuery() {
		TParm parm = new TParm();
		// 时间段
		if (this.getValue("DATE_S") != null && this.getValue("DATE_E") != null) {
			parm.setData("DATE_S", this.getText("DATE_S").replace("/", ""));
			parm.setData("DATE_E", this.getText("DATE_E").replace("/", "") + "235959");
		}
		// 病案号
		if (this.getValueString("MR_NO").length() > 0) {
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		}
		// 手术类型
		if (this.getValueString("TYPE_CODE").length() > 0) {
			parm.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
		}
		// 门急住别
		if (this.getValueString("ADM_TYPE").length() > 0) {
			parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
		}
		// 就诊科室
		if (this.getValueString("REALDEPT_CODE").length() > 0) {
			parm.setData("REALDEPT_CODE", this.getValueString("REALDEPT_CODE"));
		}
		if (this.getValueString("REALDR_CODE").length() > 0) {
			parm.setData("REALDR_CODE", this.getValueString("REALDR_CODE"));
		}
		// 预约 科室
		if (this.getValueString("BOOK_DEPT_CODE").length() > 0) {
			parm.setData("BOOK_DEPT_CODE", this.getValueString("BOOK_DEPT_CODE"));
			BOOK_DEPT = this.getText("BOOK_DEPT_CODE");
		} else {
			BOOK_DEPT = "全院";
		}
		// 手术安排状态
		if ("Y".equals(this.getValueString("STATE2"))) {
			parm.setData("STATE", "0");
		} else if ("Y".equals(this.getValueString("STATE3"))) {
//			parm.setData("STATE", "1");
			parm.setData("REV_STATE", "0");
		}
		// ===============pangben modify 20110630 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		// =============pangben modify 20110630 stop
		data = OPEPersonnelQueryTool.getInstance().selectData(parm);
		if (data.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
			return;
		}
		if (data.getCount() <= 0) {
			this.messageBox("E0008");// 查无资料
			TABLE.removeRowAll();
			this.setValue("TOTAL", "0");
			return;
		}
		this.setValue("selectALL", false);
//		for (int i = 0; i < data.getCount(); i++) {//wanglong delete 20150330
//			if ("1".equals(data.getValue("STATE", i))
//					|| "2".equals(data.getValue("STATE", i))) {
//				data.setData("STATE", i, "Y");
//			} else {
//				data.setData("STATE", i, "N");
//			}
//		}
		TABLE.setParmValue(data);
		
		this.setValue("TOTAL", String.valueOf(data.getCount()));
	}
	
    /**
     * 显示手术详细信息
     */
    public void onInfo(){
        int row = TABLE.getSelectedRow();
        if(row<0){
            this.messageBox_("请选择一条信息！");
            return;
        }
        data = TABLE.getParmValue();//add by wanglong 20121212
        String OPBOOK_SEQ = data.getValue("OPBOOK_SEQ",row);
        TParm parm = new TParm();
        parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
        parm.setData("MR_NO",data.getValue("MR_NO",row));
        parm.setData("ADM_TYPE",data.getValue("ADM_TYPE",row));//add by wanglong 20121219
        parm.setData("EDITABLE","FALSE");//add by wanglong 20121219
        this.openDialog("%ROOT%/config/ope/OPEOpDetail.x",parm);//modify by wanglong 20121219
    }
    
    /**
     * 手术记录
     * ============pangben modify 20110701
     */
    public void onOpRecord(){
        TTable table = (TTable)this.getComponent("Table");
        int index = table.getSelectedRow();//选中行
        if(index<0){
            return;
        }
        TParm parm = new TParm();
        data = table.getParmValue();//add by wanglong 20121212
        String OPBOOK_SEQ = data.getValue("OPBOOK_SEQ",index);
        parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
        parm.setData("MR_NO",data.getValue("MR_NO",index));
        parm.setData("ADM_TYPE",data.getValue("ADM_TYPE",index));
        this.openDialog("%ROOT%/config/ope/OPEOpDetail.x",parm);
    }
    
	/**
     * 打印
     */
    public void onPrint(){
        TABLE.acceptText();
        Map rows = new HashMap();
        int key = 0;
        for(int i=0;i<TABLE.getRowCount();i++){
            if(TABLE.getItemString(i,0).equals("Y")){
                rows.put(key,i);
                key++;
            }
        }
        if(rows.size()<1){
            this.messageBox("请勾选一行");
            return;
        }
        TParm T1 = new TParm();//报表表格数据
        String str1 = "";
        String str2 = "";
        for(int i=0;i<rows.size();i++){
            int row =Integer.valueOf(rows.get(i).toString());
            T1.addData("OP_ROOM",data.getValue("OP_ROOM",row));
            if(data.getBoolean("URGBLADE_FLG",row)){
                T1.addData("URGBLADE_FLG", "√");
            }else{
                T1.addData("URGBLADE_FLG", "");
            }
            T1.addData("OP_DATE",StringTool.getString(data.getTimestamp("OP_DATE",row),"yyyy/MM/dd HH:mm:ss"));
            T1.addData("PAT_NAME",data.getValue("PAT_NAME",row));
            T1.addData("DEPT_CHN_DESC",data.getValue("DEPT_CHN_DESC",row));
            //如果是住院手术 那么显示病区  否则显示诊区
            if("I".equals(data.getValue("ADM_TYPE",row)))
                T1.addData("STATION_DESC",data.getValue("STATION_DESC",row));
            else
                T1.addData("STATION_DESC",data.getValue("CLINIC_DESC",row));
            T1.addData("BED_NO_DESC",data.getValue("BED_NO_DESC",row));
            T1.addData("SEX",data.getValue("SEX",row));
            T1.addData("AGE",StringUtil.showAge(data.getTimestamp("BIRTH_DATE",row),data.getTimestamp("OP_DATE",row)));
            T1.addData("HEIGHT",data.getValue("HEIGHT",row));
            T1.addData("WEIGHT",data.getValue("WEIGHT",row));
            T1.addData("MR_NO",data.getValue("MR_NO",row));
            //术前诊断
            str1 = data.getValue("ICD_CHN_DESC",row)+";"+data.getValue("ICD_CHN_DESC2",row)+";"+data.getValue("ICD_CHN_DESC3",row);
            T1.addData("ICD_CHN_DESC",str1);
            //拟行手术
            str2 = data.getValue("OPT_CHN_DESC",row)+";"+data.getValue("OPT_CHN_DESC2",row);
            T1.addData("OPT_CHN_DESC",str2);
            
            T1.addData("MAIN_SURGEON",this.getUserName(data.getValue("MAIN_SURGEON",row)));
            T1.addData("BOOK_AST_1",this.getUserName(data.getValue("BOOK_AST_1",row)));
            T1.addData("BOOK_AST_2",this.getUserName(data.getValue("BOOK_AST_2",row)));
            T1.addData("BOOK_AST_3",this.getUserName(data.getValue("BOOK_AST_3",row)));
            T1.addData("BOOK_AST_4",this.getUserName(data.getValue("BOOK_AST_4",row)));
            T1.addData("CIRCULE_USER1",this.getUserName(data.getValue("CIRCULE_USER1",row)));
            T1.addData("ANA_CODE",getDICTIONARY("OPE_ANAMETHOD",data.getValue("ANA_CODE",row)));
            T1.addData("ANA_USER1",this.getUserName(data.getValue("ANA_USER1",row)));
            T1.addData("EXTRA_USER1",this.getUserName(data.getValue("EXTRA_USER1",row)));
            T1.addData("REMARK",data.getValue("REMARK",row));
        }
        T1.setCount(rows.size());
        T1.addData("SYSTEM", "COLUMNS", "OP_ROOM");
        T1.addData("SYSTEM", "COLUMNS", "URGBLADE_FLG");
        T1.addData("SYSTEM", "COLUMNS", "OP_DATE");
        T1.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        T1.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        T1.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        T1.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        T1.addData("SYSTEM", "COLUMNS", "SEX");
        T1.addData("SYSTEM", "COLUMNS", "AGE");
        T1.addData("SYSTEM", "COLUMNS", "HEIGHT");
        T1.addData("SYSTEM", "COLUMNS", "WEIGHT");
        T1.addData("SYSTEM", "COLUMNS", "MR_NO");
        T1.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
        T1.addData("SYSTEM", "COLUMNS", "OPT_CHN_DESC");
        T1.addData("SYSTEM", "COLUMNS", "MAIN_SURGEON");
        T1.addData("SYSTEM", "COLUMNS", "BOOK_AST_1");
        T1.addData("SYSTEM", "COLUMNS", "BOOK_AST_2");
        T1.addData("SYSTEM", "COLUMNS", "BOOK_AST_3");
        T1.addData("SYSTEM", "COLUMNS", "BOOK_AST_4");
        T1.addData("SYSTEM", "COLUMNS", "CIRCULE_USER1");
        T1.addData("SYSTEM", "COLUMNS", "ANA_CODE");
        T1.addData("SYSTEM", "COLUMNS", "ANA_USER1");
        T1.addData("SYSTEM", "COLUMNS", "EXTRA_USER1");
        T1.addData("SYSTEM", "COLUMNS", "REMARK");
        //报表总体数据
        TParm printData = new TParm();
        printData.setData("T1",T1.getData());
        printData.setData("title","TEXT","手术安排清单");
        printData.setData("BOOK_DEPT","TEXT",BOOK_DEPT);
        printData.setData("OP_DATE","TEXT",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日"));
        this.openPrintWindow("%ROOT%\\config\\prt\\OPE\\OPEPersonnelPrint.jhw", printData);
    }
       
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("DATE_S;DATE_E;MR_NO;TYPE_CODE;ADM_TYPE;REALDEPT_CODE;REALDR_CODE;BOOK_DEPT_CODE;TOTAL");
        this.setValue("STATE1",true);
        TABLE.removeRowAll();
    }
        
    /**
     * 替换科室中午
     * @param s String
     * @return String
     */
    public String getDeptDesc(String s) {
        if (dataStore_Dept == null)
            return s;
        String bufferString = dataStore_Dept.isFilter() ? dataStore_Dept.FILTER : dataStore_Dept.PRIMARY;
        TParm parm = dataStore_Dept.getBuffer(bufferString);
        Vector v = (Vector) parm.getData("DEPT_CODE");
        Vector d = (Vector) parm.getData("DEPT_CHN_DESC");
        int count = v.size();
        for (int i = 0; i < count; i++) {
            if (s.equals(v.get(i)))
                return "" + d.get(i);
        }
        return s;
    }
    
    /**
     * 替换人员姓名
     * @param s String
     * @return String
     */
    public String getUserName(String s) {
        if (dataStore_User == null)
            return s;
        String bufferString = dataStore_User.isFilter() ? dataStore_Dept.FILTER : dataStore_Dept.PRIMARY;
        TParm parm = dataStore_User.getBuffer(bufferString);
        Vector v = (Vector) parm.getData("USER_ID");
        Vector d = (Vector) parm.getData("USER_NAME");
        int count = v.size();
        for (int i = 0; i < count; i++) {
            if (s.equals(v.get(i)))
                return "" + d.get(i);
        }
        return s;
    }
    
    /**
     * 替换大字典中的中文
     * @param group_ID String
     * @param ID String
     * @return String
     */
    private String getDICTIONARY(String group_ID,String ID){
        DICTIONARY.setFilter(" GROUP_ID='"+group_ID+"' AND ID='"+ID+"'");
        DICTIONARY.filter();
        return DICTIONARY.getItemString(0,"CHN_DESC");
    }
    
    /**
     * 全选事件
     */
    public void selectALL(){
        String flg = "N";
        if("Y".equals(this.getValueString("selectALL"))){
            flg = "Y";
        }else
            flg = "N";
        for (int i = 0; i < TABLE.getRowCount(); i++) {
            //如果是全选状态 要判断 选中的信息是不是已经排程的信息
            if("Y".equals(flg)){
                if("Y".equals(TABLE.getItemString(i,1))){
                    TABLE.setItem(i, 0, flg);
                }
            }else{
                TABLE.setItem(i, 0, flg);
            }
        }
    }
    
    /**
     * table 中 checkbox 事件
     * @param object Object
     */
    public boolean onClickBox(Object object) {
        TTable obj = (TTable) object;
        obj.acceptText();
        int row = obj.getSelectedRow();
        if(obj.getItemString(row,"FLG").equals("Y")){
            //判断选中行信息是否已经完成了 排程
            if(obj.getItemString(row,1).equals("N")){
                this.messageBox_("此条手术申请还没有进行排程，不可打印");
                obj.setItem(row,"FLG","N");
                return true;
            }
        }
        return false;
    }
    
    /**
     * 导出excel
     */
    public void onExport() {//add by wanglong 20130718
        if (TABLE.getRowCount() <= 0) {
            this.messageBox("没有数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(TABLE, "手术排程报表");
    }
    
	// ====================排序功能begin======================add by wanglong 20121212
	/**
	 * 加入表格排序监听方法
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// 点击相同列，翻转排序
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// 取得表单中的数据
				String columnName[] = tableData.getNames("Data");// 获得列名
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
				int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * 根据列名数据，将TParm转为Vector
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
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
	 * 返回指定列在列名数组中的index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
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
	 * 根据列名数据，将Vector转成Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================排序功能end======================
	
	public void onMrNo(){
		String mrNo = this.getValueString("MR_NO");
		if(mrNo.length() == 0){
			return;
		}
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		this.setValue("MR_NO", mrNo);
		
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if(pat == null){
			this.messageBox("无此病案号!");
			return;
		}
		
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
		}
		
		onQuery();
		
	}
	
   
}
