package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import java.util.regex.Pattern;
import com.dongyang.data.TNull;
import com.dongyang.ui.TTable;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.util.TMessage;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import jdo.clp.CLPORDTypeTool;

/**
 * <p>Title: 临床路径项目字典</p>
 *
 * <p>Description:临床路径项目字典 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPORDTypeControl extends TControl {
    //记录选择行数
    int selectRow = -1;
    TTable masterTbl;
    private int sortColumn = -1;
    private boolean ascending = false;
    private Compare compare = new Compare();
    public CLPORDTypeControl() {

    }

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        initPage();
        this.onQuery();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
    }

    /**
     * 初始化页面
     */
    private void initPage() {
    	masterTbl=(TTable) this.getComponent("Table");
    	addListener(masterTbl);
    }

    /**
     * 页面查询方法
     */
    public void onQuery() {
        setPrimaryKeyEnabled(true);
//        //在顺序号存在时验证数字
//        if (this.checkNullAndEmpty(this.getValueString("SEQ")) &&
//            !this.validNumber(this.getValueString("SEQ"))) {
//            this.messageBox("顺序号请输入数字");
//            return;
//        }
        TParm selectParm = getSelectedCondition();
        TParm result = CLPORDTypeTool.getInstance().selectData(selectParm);
        this.callFunction("UI|Table|setParmValue", result);
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        selectRow = this.getSelectedRow("Table");
        if (selectRow == -1) {
            this.messageBox("请选择需要删除的数据");
            return;
        }
        if (this.messageBox("询问", "是否删除", 2) == 0) {
            TTable table = (TTable)this.getComponent("Table");
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String REGION_CODE = tableParm.getValue("REGION_CODE", selRow);
            String TYPE_CODE = tableParm.getValue("TYPE_CODE", selRow);
            TParm parm = new TParm();
            parm.setData("REGION_CODE", REGION_CODE);
            parm.setData("TYPE_CODE", TYPE_CODE);
            TParm result = CLPORDTypeTool.getInstance().deleteData(parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //删除单行显示
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0) {
                return;
            }
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);
            this.messageBox("P0003");
        } else {
            return;
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        this.setValue("TYPE_CODE", "");
        this.setValue("PY2", "");
        this.setValue("TYPE_CHN_DESC", "");
        this.setValue("PY1", "");
        this.setValue("TYPE_ENG_DESC", "");
        this.setValue("DESCRIPTION", "");
        this.setValue("SEQ", "");
        setPrimaryKeyEnabled(true);
    }

    /**
     * 保存方法
     */
    public void onSave() {
        //验证数据
        if (!validBasicData()) {
            return;
        }
        //得到插入数据
        //处理生成拼音问题
        //处理拼音不能自动生成问题
        //设置拼音1
        setValue("PY1", TMessage.getPy(getValueString("TYPE_CHN_DESC")));
        TParm parm = new TParm();
        this.putParamWithObjName("TYPE_CODE", parm);
        //处理typeCode 采用取号原则
        if(!this.checkNullAndEmpty(parm.getValue("TYPE_CODE"))){
            parm.setData("TYPE_CODE",SystemTool.getInstance().getNo("ALL", "CLP", "CLP",
                    "TYPE_CODE"));
        }
        this.putParamWithObjName("PY2", parm);
        this.putParamWithObjName("TYPE_CHN_DESC", parm);
        this.putParamWithObjName("PY1", parm);
        this.putParamWithObjName("TYPE_ENG_DESC", parm);
        this.putParamWithObjName("DESCRIPTION", parm);
        this.putParamWithObjName("SEQ", parm);
        this.putBasicSysInfoIntoParm(parm);
        //判断数据是否存在
        TParm result = CLPORDTypeTool.getInstance().checkDataExist(parm);
        boolean isdataExist = Integer.parseInt(result.getValue("DATACOUNT", 0)) >
                              0 ? true : false;
        TParm resultopt = null;
        //处理seq
        if ("".equals(parm.getValue("SEQ"))) {
            TNull tnull = new TNull(String.class);
            parm.setData("SEQ", tnull);
        }
        if (isdataExist) {
            resultopt = CLPORDTypeTool.getInstance().updateData(parm);
            if (resultopt.getErrCode() >= 0) {
                this.messageBox("P0001");
            } else {
                this.messageBox("E0001");
            }
        } else {
            resultopt = CLPORDTypeTool.getInstance().insertData(parm);
            if (resultopt.getErrCode() >= 0) {
                this.messageBox("P0002");
            } else {
                this.messageBox("E0002");
            }
        }
        this.onQuery();
    }

    /**
     * 验证信息
     * @return TParm
     */
    private boolean validBasicData() {
        if (!this.emptyTextCheck("TYPE_CHN_DESC")) {
            return false;
        }
        String seqstr = this.getValueString("SEQ");
        if (this.checkNullAndEmpty(seqstr)) {
            if (!this.validNumber(seqstr)) {
                this.messageBox("顺序号请输入数字");
                return false;
            }
        }
        return true;
    }

    /**
     * 设置控件是否可用方法
     * @param flag boolean
     */
    private void setPrimaryKeyEnabled(boolean flag) {
        TTextField tTextField = (TTextField)this.getComponent("TYPE_CODE");
        tTextField.setEnabled(flag);
    }

    /**
     * 从页面得到查询条件方法
     */
    private TParm getSelectedCondition() {
        TParm selectedCondition = new TParm();
        putParamLikeWithObjName("TYPE_CODE", selectedCondition);
//        putParamLikeWithObjName("PY2", selectedCondition);
        putParamLikeWithObjName("TYPE_CHN_DESC", selectedCondition);
//        putParamLikeWithObjName("TYPE_ENG_DESC", selectedCondition);
//        putParamWithObjNameForQuery("CLASS_FLG", selectedCondition);
//        putParamLikeWithObjName("DESCRIPTION", selectedCondition);
//        putParamLikeWithObjName("SEQ", selectedCondition);
        selectedCondition.setData("REGION_CODE", Operator.getRegion());
        return selectedCondition;
    }

    /**
     * 将表格的对应单元格设置成可写，其他的设置成不可写
     * @param tableName String
     * @param rowNum int
     * @param columnNum int
     */
    private void setTableEnabled(String tableName, int rowNum, int columnNum) {
        TTable table = (TTable)this.getComponent(tableName);
        int totalColumnMaxLength = table.getColumnCount();
        int totalRowMaxLength = table.getRowCount();
        //锁列
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
        table.setLockColumns(lockColumnStr);
        //锁行
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
        if (lockRowStr.length() > 0) {
            table.setLockRows(lockRowStr);
        }

    }

    /**
     * 将控件值放入TParam方法(可以传入放置参数值)
     * @param objName String
     */
    private void putParamWithObjName(String objName, TParm parm,
                                     String paramName) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        parm.setData(paramName, objstr);
    }


    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        setValueForParm(
                "TYPE_CODE;TYPE_CHN_DESC;PY1;PY2;TYPE_ENG_DESC;CLASS_FLG;DESCRIPTION;SEQ",
                data, row);
        String str = data.getValue("SEQ", row);
        this.setValue("SEQ", str);
        setPrimaryKeyEnabled(false);
        selectRow = row;
    }

    /**
     * 将控件值放入TParam方法(放置参数值与控件名相同)
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        //SYSTEM.OUT.PRINTln(objstr);
        objstr = objstr;
        //参数值与控件名相同
        parm.setData(objName, objstr);
    }

    /**
     * 将控件值放入TParam方法(可以传入放置参数值)
     * @param objName String
     */
    private void putParamLikeWithObjName(String objName, TParm parm,
                                         String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr + "%";
            parm.setData(paramName, objstr);
        }

    }

    /**
     * 将控件值放入TParam方法(放置参数值与控件名相同)
     * @param objName String
     * @param parm TParm
     */
    private void putParamLikeWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr.trim() + "%";
            //参数值与控件名相同
            parm.setData(objName, objstr);
        }
    }

    /**
     * 用于放置用于完全匹配进行查询的控件
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm) {
        putParamWithObjNameForQuery(objName, parm, objName);
    }

    /**
     * 用于放置用于完全匹配进行查询的控件
     * @param objName String
     * @param parm TParm
     * @param paramName String
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm,
                                             String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            //参数值与控件名相同
            parm.setData(paramName, objstr.trim());
        }
    }

    /**
     * 检查控件是否为空
     * @param componentName String
     * @return boolean
     */
    private boolean checkComponentNullOrEmpty(String componentName) {
        if (componentName == null || "".equals(componentName)) {
            return false;
        }
        String valueStr = this.getValueString(componentName);
        if (valueStr == null || "".equals(valueStr)) {
            return false;
        }
        return true;
    }

    /**
     * 得到指定table的选中行
     * @param tableName String
     * @return int
     */
    private int getSelectedRow(String tableName) {
        int selectedIndex = -1;
        if (tableName == null || tableName.length() <= 0) {
            return -1;
        }
        Object componentObj = this.getComponent(tableName);
        if (!(componentObj instanceof TTable)) {
            return -1;
        }
        TTable table = (TTable) componentObj;
        selectedIndex = table.getSelectedRow();
        return selectedIndex;
    }

    /**
     * 数字验证方法
     * @param validData String
     * @return boolean
     */
    private boolean validNumber(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,}");
        Matcher match = p.matcher(validData);
        if (!match.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 向TParm中加入系统默认信息
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
        //SYSTEM.OUT.PRINTln("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM", Operator.getIP());
    }

    /**
     * 根据Operator得到map
     * @return Map
     */
    private Map getBasicOperatorMap() {
        Map map = new HashMap();
        map.put("REGION_CODE", Operator.getRegion());
        map.put("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        map.put("OPT_DATE", datestr);
        map.put("OPT_TERM", Operator.getIP());
        return map;
    }

    /**
     * 得到当前时间字符串方法
     * @param dataFormatStr String
     * @return String
     */
    private String getCurrentDateStr(String dataFormatStr) {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, dataFormatStr);
        return datestr;
    }

    /**
     * 得到当前时间字符串方法
     * @return String
     */
    private String getCurrentDateStr() {
        return getCurrentDateStr("yyyyMMdd");
    }

    /**
     * 检查是否为空或空串
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }

    /**
     * 拷贝TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from, TParm to, int row) {
        for (int i = 0; i < from.getNames().length; i++) {
            to.addData(from.getNames()[i],
                       from.getValue(from.getNames()[i], row));
        }
    }

    /**
     * 克隆对象
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getValue(from.getNames()[i]));
        }
        return returnTParm;
    }

    /**
     * 处理TParm 里的null的方法
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNullVector(TParm parm, String keyStr, Class type) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData(keyStr, i) == null) {
                //SYSTEM.OUT.PRINTln("处理为空情况");
                TNull tnull = new TNull(type);
                parm.setData(keyStr, i, tnull);
            }
        }
    }

    /**
     * 处理TParm 里null值方法
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNull(TParm parm, String keyStr, Class type) {
        if (parm.getData(keyStr) == null) {
            //SYSTEM.OUT.PRINTln("处理为空情况");
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }

    /**
     * 数字验证方法
     * @param validData String
     * @return boolean
     */
    private boolean validDouble(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,2}([.][0-9]{1,2}){0,1}");
        Matcher match = p.matcher(validData);
        if (!match.matches()) {
            return false;
        }
        return true;
    }

    /**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
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
				TParm tableData = masterTbl.getParmValue();
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
				String tblColumnName = masterTbl.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
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
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
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
		masterTbl.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
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
}
