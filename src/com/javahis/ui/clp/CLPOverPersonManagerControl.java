package com.javahis.ui.clp;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTable;
import com.dongyang.ui.base.TComboBoxModel;
import com.dongyang.ui.util.Compare;

import jdo.sys.SYSRegionTool;
import java.util.Date;
import java.util.Vector;

import com.dongyang.util.StringTool;
import jdo.sys.Operator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.javahis.util.ExportExcelUtil;
import jdo.clp.CLPOverPersonManagerTool;

/**
 * <p>Title: 科室溢出人员管理 </p>
 *
 * <p>Description: 科室溢出人员管理</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110708
 * @version 1.0
 */
public class CLPOverPersonManagerControl extends TControl {
	int selectRow = -1;
    private int sortColumn = -1;
    private boolean ascending = false;
    private Compare compare = new Compare();
    /**
     * 初始化方法
     */
    public void onInit() {
        initPage();
        //权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
    }

    private TTable table;
    public CLPOverPersonManagerControl() {
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        String date_s = getValueString("DATE_S");
        String date_e = getValueString("DATE_E");
        if (null == date_s || date_s.length() <= 0 || null == date_e ||
            date_e.length() <= 0) {
            this.messageBox("请输入需要查询的时间范围");
            return;
        }
        date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "").
                 replace("-", "").replace(" ", "");
        date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "").
                 replace("-", "").replace(" ", "");
        if (null != this.getValueString("REGION_CODE") && this.getValueString("REGION_CODE").length() > 0)
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        parm.setData("DATE_S", date_s);
        parm.setData("DATE_E", date_e);
        //System.out.println("parm::::"+parm);
        TParm result = CLPOverPersonManagerTool.getInstance().selectData("selectManagemHistory",parm);
        //System.out.println("resulDFDFDFt::::"+result);
        if(result.getCount()<=0){
            this.messageBox("查无数据");
        }
        table.setParmValue(result);
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        Timestamp date = StringTool.getTimestamp(new Date());
        table = (TTable) getComponent("TABLE");
        addListener(table);
        this.setValue("REGION_CODE", Operator.getRegion());
        // 初始化查询区间
        this.setValue("DATE_E",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("DATE_S",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");

    }

    /**
     * 打印方法
     */
    public void onPrint() {

        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("没有需要打印的数据");
            return;
        }
        TParm parmValue=new TParm();
        TComboBox com = (TComboBox) this.getComponent("REGION_CODE");// wangzhilei 20120724 添加
        for(int i=0;i<parm.getCount();i++){
        	// wangzhilei 20120724 添加
			String rn = parm.getValue("REGION_CODE", i);
        	TComboBoxModel tbm = com.getModel();
        	Vector v = tbm.getItems();
			for (int j = 0; j < v.size(); j++) {
				TComboNode tn = (TComboNode) v.get(j);
				if (rn.equals(tn.getID())) {
					rn = tn.getName();
					break;
				}
			}
			// wangzhilei 20120724 添加
        	parmValue.addData("REGION_CODE",rn);// wangzhilei 20120724 添加
            parmValue.addData("DEPT_CHN_DESC",parm.getValue("DEPT_CHN_DESC",i));
            parmValue.addData("STATION_DESC",parm.getValue("STATION_DESC",i));
            parmValue.addData("CLNCPATH_CHN_DESC",parm.getValue("CLNCPATH_CHN_DESC",i));
            parmValue.addData("MR_NO",parm.getValue("MR_NO",i));
            parmValue.addData("IPD_NO",parm.getValue("IPD_NO",i));
            parmValue.addData("PAT_NAME",parm.getValue("PAT_NAME",i));
            parmValue.addData("CHN_DESC",parm.getValue("CHN_DESC",i));
            parmValue.addData("DESCRIPTION",null!=parm.getValue("DESCRIPTION",i)?parm.getValue("DESCRIPTION",i):"");
        }
        parmValue.setCount(parm.getCount());
        TParm result = new TParm();
        parmValue.addData("SYSTEM", "COLUMNS", "REGION_CODE");// wangzhilei 20120724 添加
        parmValue.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        parmValue.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        parmValue.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
        parmValue.addData("SYSTEM", "COLUMNS", "MR_NO");
        parmValue.addData("SYSTEM", "COLUMNS", "IPD_NO");
        parmValue.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        parmValue.addData("SYSTEM", "COLUMNS", "CHN_DESC");
        parmValue.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        result.setData("S_DATE", "TEXT",
                       getValueString("DATE_S").substring(0,
                getValueString("DATE_S").lastIndexOf(".")));
        result.setData("E_DATE", "TEXT",
                       getValueString("DATE_E").substring(0,
                getValueString("DATE_S").lastIndexOf(".")));
        result.setData("OPT_USER", Operator.getName());
        result.setData("T1", parmValue.getData());
        result.setData("TITLE", "TEXT",
                       (null != Operator.getHospitalCHNShortName() ?
                        Operator.getHospitalCHNShortName() : "所有院区") +
                       "科室溢出人员管理");
        //卢海加入制表人
        //表尾
        result.setData("CREATEUSER", "TEXT", Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\CLP\\CLPNewOverPersonManager.jhw",
                             result);
    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
        table.removeRowAll();
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        //得到UI对应控件对象的方法
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("没有需要导出的数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "科室溢出人员管理");
    }
    
    public void addListener(final TTable table) {
		
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
				TParm tableData = table.getParmValue();
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
				String tblColumnName = table.getParmMap(sortColumn);
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
		table.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
	
}
