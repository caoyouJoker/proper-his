package com.javahis.ui.sta;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.sta.STAIn_15Tool;
import jdo.sys.SystemTool;
import jdo.bil.BILComparator;


/**
 * <p>Title: 付款方式查询报表 </p>
 *
 * <p>Description: 付款方式查询报表 </p>
 *
 * <p>Copyright: Copyright (c) 2013 </p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author WangLong 20131008
 * @version 1.0
 */
public class STAIn_15Control extends TControl {
	
    private String admType;
    private TextFormatDept dept;
    private TTable table;
    // =================排序辅助==============
    private BILComparator compare = new BILComparator();
    private int sortColumn = -1;
    private boolean ascending = false;
	
    /**
     * 初始化方法
     */
    public void onInit() {
        dept = (TextFormatDept) this.getComponent("DEPT_CODE");
        table = (TTable) this.getComponent("TABLE");
        addSortListener(table);// 加排序
        onClear();
    }
    
    /**
     * 查询
     */
    public void onQuery() {
        String startDate = this.getText("START_DATE");// 开始时间
        String endDate = this.getText("END_DATE");// 结束日期
        String deptCode = this.getValueString("DEPT_CODE");// 科室
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("START_DATE", startDate);
        parm.setData("END_DATE", endDate);
        if (!StringUtil.isNullString(deptCode)) {
            parm.setData("DEPT_CODE", deptCode);
        }
        TParm dataParm = new TParm();
        TParm deptParm = new TParm();
        TParm ctzParm = new TParm();
        String header = "";
        String parmMap = "";
        String alignment = "";
        TParm result = new TParm();
        if (admType.equals("O")) {
            dataParm = STAIn_15Tool.getInstance().selectOPD(parm);
            if (dataParm.getErrCode() < 0) {
                messageBox(dataParm.getErrText());
                return;
            }
            if (dataParm.getCount() > 0) {
                Set<String> deptSet = new HashSet<String>();
                Set<String> ctzSet = new HashSet<String>();
                for (int i = 0; i < dataParm.getCount(); i++) {
                    deptSet.add(dataParm.getValue("DEPT_CODE", i));
                    ctzSet.add(dataParm.getValue("CTZ_CODE", i) + "#" + dataParm.getValue("CTZ_DESC", i)+ "#" + dataParm.getValue("PY", i));
                }
                for (String dept : deptSet) {
                    deptParm.addData("DEPT_CODE", dept);
                }
                deptParm.setCount(deptSet.size());
                for (String ctz : ctzSet) {
                    ctzParm.addData("CTZ_CODE", ctz.split("#")[0]);
                    ctzParm.addData("CTZ_DESC", ctz.split("#")[1]);
                    ctzParm.addData("PY", ctz.split("#")[2]);
                }
                ctzParm.setCount(ctzSet.size());
                header = "科室,100,DEPT_CODE_SQL;";
                parmMap = "DEPT_CODE;";
                alignment = "0,left;";
                for (int i = 0; i < ctzParm.getCount(); i++) {
                    header += ctzParm.getValue("CTZ_DESC",i) + countTitleItemLength(ctzParm.getValue("CTZ_DESC",i));
                    alignment += (i + 1) + ",right;";
                    parmMap += ctzParm.getValue("PY",i) + ";";
                }
                for (int i = 0; i < ctzParm.getCount(); i++) {
                    for (int j = 0; j < deptParm.getCount(); j++) {
                        result.addData(ctzParm.getValue("PY", i), 0);// 空值占位
                    }
                }
                for (int i = 0; i < deptParm.getCount(); i++) {
                    for (int j = 0; j < ctzParm.getCount(); j++) {
                        for (int k = 0; k < dataParm.getCount(); k++) {
                            if (dataParm.getValue("CTZ_CODE", k).equals(ctzParm.getValue("CTZ_CODE", j))
                                    && dataParm.getValue("DEPT_CODE", k).equals(deptParm.getValue("DEPT_CODE", i))) {
                                result.setData(ctzParm.getValue("PY", j), i,
                                               dataParm.getInt("COUNT", k));
                            }
                        }
                    }
                }
                result.setCount(deptParm.getCount());
                result = addSumRowCol(result);// 加总计行和列
                for (int i = 0; i < deptParm.getCount(); i++) {
                    result.addData("DEPT_CODE", deptParm.getValue("DEPT_CODE", i));
                }
                result.addData("DEPT_CODE", "合计");
                result.setCount(result.getCount("DEPT_CODE"));
                header += "合计,60";
                parmMap += "SUM";
                alignment += ctzParm.getCount("CTZ_CODE") + 1 + ",right";
            }
            table.setHeader(header);
            table.setColumnHorizontalAlignmentData(alignment);
            table.setParmMap(parmMap);
        } else if (admType.equals("I")) {
            dataParm = STAIn_15Tool.getInstance().selectIPD(parm);
            if (dataParm.getErrCode() < 0) {
                messageBox(dataParm.getErrText());
                return;
            }
            if (dataParm.getCount() > 0) {
                Set<String> deptSet = new HashSet<String>();
                Set<String> ctzSet = new HashSet<String>();
                for (int i = 0; i < dataParm.getCount(); i++) {
                    deptSet.add(dataParm.getValue("DEPT_CODE", i));
                    ctzSet.add(dataParm.getValue("CTZ_CODE", i) + "#" + dataParm.getValue("CTZ_DESC", i)+ "#" + dataParm.getValue("PY", i));
                }
                for (String dept : deptSet) {
                    deptParm.addData("DEPT_CODE", dept);
                }
                deptParm.setCount(deptSet.size());
                for (String ctz : ctzSet) {
                    ctzParm.addData("CTZ_CODE", ctz.split("#")[0]);
                    ctzParm.addData("CTZ_DESC", ctz.split("#")[1]);
                    ctzParm.addData("PY", ctz.split("#")[2]);
                }
                ctzParm.setCount(ctzSet.size());
                header = "科室,100,DEPT_CODE_SQL;";
                parmMap = "DEPT_CODE;";
                alignment = "0,left;";
                for (int i = 0; i < ctzParm.getCount(); i++) {
                    header += ctzParm.getValue("CTZ_DESC",i) + countTitleItemLength(ctzParm.getValue("CTZ_DESC",i));
                    alignment += (i + 1) + ",right;";
                    parmMap += ctzParm.getValue("PY",i) + ";";
                }
                for (int i = 0; i < ctzParm.getCount(); i++) {
                    for (int j = 0; j < deptParm.getCount(); j++) {
                        result.addData(ctzParm.getValue("PY", i), 0);// 空值占位
                    }
                }
                for (int i = 0; i < deptParm.getCount(); i++) {
                    for (int j = 0; j < ctzParm.getCount(); j++) {
                        for (int k = 0; k < dataParm.getCount(); k++) {
                            if (dataParm.getValue("CTZ_CODE", k).equals(ctzParm.getValue("CTZ_CODE", j))
                                    && dataParm.getValue("DEPT_CODE", k).equals(deptParm.getValue("DEPT_CODE", i))) {
                                result.setData(ctzParm.getValue("PY", j), i,
                                               dataParm.getInt("COUNT", k));
                            }
                        }
                    }
                }
                result.setCount(deptParm.getCount());
                result = addSumRowCol(result);// 加总计行和列
                for (int i = 0; i < deptParm.getCount(); i++) {
                    result.addData("DEPT_CODE", deptParm.getValue("DEPT_CODE", i));
                }
                result.addData("DEPT_CODE", "合计");
                result.setCount(result.getCount("DEPT_CODE"));
                header += "合计,60";
                parmMap += "SUM";
                alignment += ctzParm.getCount("CTZ_CODE") + 1 + ",right";
            }
            table.setHeader(header);
            table.setColumnHorizontalAlignmentData(alignment);
            table.setParmMap(parmMap);
        }
		if (dataParm.getCount() <= 0) {
			messageBox("E0008");//查无资料
			this.callFunction("UI|TABLE|setParmValue", new TParm());
			this.clearValue("COUNT");
			return;
        }
        this.clearValue("TABLE");
        ((TTextField) getComponent("COUNT")).setValue(result.getValue("SUM", result.getCount()-1) + "");
        this.callFunction("UI|TABLE|setParmValue", result);
    }

    /**
     * 科室选择事件
     */
    public void onChooseDept() {
        this.clearValue("DEPT_CODE");
        admType = this.getValueString("ADM_TYPE");// 门急住别
        if (admType.equals("O")) {
            dept.setOpdFitFlg("Y");
            dept.setEmgFitFlg("Y");
            dept.setIpdFitFlg("");
            dept.onQuery();
        } else if (admType.equals("I")) {
            dept.setOpdFitFlg("");
            dept.setEmgFitFlg("");
            dept.setIpdFitFlg("Y");
            dept.onQuery();
        }
    }
    
    /**
     * 导出
     */
    public void onExport(){
        if (admType.equals("O")) {
            ExportExcelUtil.getInstance().exportExcel(table,"门急诊患者按付款方式查询");
        } else if (admType.equals("I")) {
            ExportExcelUtil.getInstance().exportExcel(table,"出院患者按付款方式查询");
        }
       
    }

    /**
     * 得到TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    
    /**
     * 清空
     */
    public void onClear() {
        admType = "O";
        this.setValue("ADM_TYPE", admType);
        onChooseDept();
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(sysDate, "yyyy/MM/dd");
        this.setValue("END_DATE",
                      StringTool.getTimestamp(dateStr + " 23:59:59", "yyyy/MM/dd HH:mm:ss"));
        sysDate = StringTool.rollDate(sysDate, -7);
        dateStr = StringTool.getString(sysDate, "yyyy/MM/dd");
        this.setValue("START_DATE",
                      StringTool.getTimestamp(dateStr + " 00:00:00", "yyyy/MM/dd HH:mm:ss"));
        this.clearValue("DEPT_CODE");
        ((TTextField) getComponent("COUNT")).setValue("");
        this.getTTable("TABLE").setDSValue();
    }
    
    /**
     * 增加总计行和总计列
     * @param parm
     * @return
     */
    public TParm addSumRowCol(TParm parm) {
        int count = parm.getCount();
        int sum = 0;
        for (int i = 0; i < parm.getCount(); i++) {
            for (int j = 0; j < parm.getNames().length; j++) {
                sum += parm.getInt(parm.getNames()[j], i);
            }
            parm.addData("SUM", sum);
            sum = 0;
        }
        for (int i = 0; i < parm.getNames().length; i++) {
            for (int j = 0; j < parm.getCount(); j++) {
                sum += parm.getInt(parm.getNames()[i], j);
            }
            parm.addData(parm.getNames()[i], sum);
            sum = 0;
        }
        parm.setCount(count + 1);
        return parm;
    }
    
    /**
     * 根据标题字数,生成其长度的字符串 
     * 
     * @param titleItem
     * @return
     */
    public String countTitleItemLength(String titleItem) {
        try {
            titleItem = new String(titleItem.getBytes("GBK"), "ISO8859_1");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int length = (titleItem.length() * 8) < 50 ? 50 : (titleItem.length() * 8);//宽度不小于50
        return "," + length + ",int;";
    }
    
	// ====================排序功能begin======================
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
	 * 
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
}
