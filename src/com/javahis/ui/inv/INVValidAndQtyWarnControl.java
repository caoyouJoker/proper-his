package com.javahis.ui.inv;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.inv.INVValidAndQtyWarnTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 近效期及库存量提示</p>
 *
 * <p>Description: 近效期及库存量提示</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangh 2013.7.12
 * @version 1.0
 */
public class INVValidAndQtyWarnControl
    extends TControl {

    private TPanel panel_0; 

    private TTable tableValid;

    private TTable tableQty;
    
  //===========排序功能==================add by wanglong 20121212
    private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;

    public INVValidAndQtyWarnControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        panel_0 = getPanel("TPanel_0");
        tableValid = (TTable) this.getComponent("TABLE_VALID");
        tableQty = (TTable) this.getComponent("TABLE_QTY");
        addSortListener(tableValid);//===表格加排序监听=====add by wanglong 20121212
        addSortListener(tableQty);//===表格加排序监听=====add by wanglong 20121212
		TParm parmValid = new TParm();
		parmValid.setData("CAT1_TYPE", "OTH");
		// 设置弹出菜单
        getTextField("INV_CODE_VALID").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parmValid);
		// 定义接受返回值方法
        getTextField("INV_CODE_VALID").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturnValid");
        TParm parmQty = new TParm();
        parmQty.setData("CAT1_TYPE", "OTH");
		// 设置弹出菜单
        getTextField("INV_CODE_QTY").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parmQty);
		// 定义接受返回值方法
        getTextField("INV_CODE_QTY").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturnQTY");
    }
    
    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturnValid(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE_VALID").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC_VALID").setValue(order_desc);
    }
    
    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturnQTY(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE_QTY").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC_QTY").setValue(order_desc);
    }
    

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        TParm result = new TParm();
        String inv_code = "";

        if (panel_0.isShowing()) {
            String date = StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyyMMdd");
            //期限限制
            if (getRadioButton("VALID_DATE_A").isSelected()) {
                parm.setData("VALID_DATE",
                             rollMonth(date.substring(0, 6), date.substring(6, 8), 1));
            }
            else if (getRadioButton("VALID_DATE_B").isSelected()) {
                parm.setData("VALID_DATE",
                             rollMonth(date.substring(0, 6), date.substring(6, 8), 3));
            }
            else {
                String valid_date = getValueString("VALID_DATE");
                parm.setData("VALID_DATE", valid_date.substring(0, 4) +
                             valid_date.substring(5, 7) + valid_date.substring(8, 10));
            }
            //物资代码
            inv_code = getValueString("INV_CODE_VALID");
            if (inv_code != null && inv_code.length() > 0) {
                parm.setData("INV_CODE", inv_code);
            }
            //近效期查询
            result = INVValidAndQtyWarnTool.getInstance().onQueryValid(parm);
            if (result == null || result.getCount("INV_CODE") <= 0) {
                this.messageBox("没有查询数据");
                tableValid.removeRowAll();
                return;
            }
            tableValid.setParmValue(result);
        }
        else {
            //药品代码
            inv_code = getValueString("INV_CODE_QTY");
            if (inv_code != null && inv_code.length() > 0) {
                parm.setData("INV_CODE", inv_code);
            }
            //库存量
            if (getRadioButton("STOCK_QTY_A").isSelected()) {
                parm.setData("STOCK_QTY_A", "STOCK_QTY_A");
            }
            else if (getRadioButton("STOCK_QTY_B").isSelected()) {
                parm.setData("STOCK_QTY_B", "STOCK_QTY_B");
            }
            else {
                parm.setData("STOCK_QTY_C", getValue("STOCK_QTY_C"));
            }
            //库存量查询
            result = INVValidAndQtyWarnTool.getInstance().onQueryQty(parm);
            if (result == null || result.getCount("INV_CODE") <= 0) {
                this.messageBox("没有查询数据");
                tableQty.removeRowAll();
                return;
            }
            tableQty.setParmValue(result);
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        if (panel_0.isShowing()) {
            getRadioButton("VALID_DATE_A").setSelected(true);
            getTextFormat("VALID_DATE").setEnabled(false);
            this.clearValue("VALID_DATE;INV_CODE_VALID;INV_DESC_VALID");
            tableValid.removeRowAll();
        }
        else {
            getRadioButton("STOCK_QTY_C").setSelected(true);
            this.clearValue("INV_CODE_QTY;INV_DESC_QTY");
            tableQty.removeRowAll();
        }
    }

    /**
     * 变更单选框
     */
    public void onChangeRadioButton() {
        if (getRadioButton("VALID_DATE_C").isSelected()) {
            getTextFormat("VALID_DATE").setEnabled(true);
        }
        else {
            getTextFormat("VALID_DATE").setEnabled(false);
            this.clearValue("VALID_DATE");
        }
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }


    /**
     * 得到TPanel对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TPanel getPanel(String tagName) {
        return (TPanel) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 根据指定的月份和天数加减计算需要的月份和天数
     * @param Month String 制定月份 格式:yyyyMM
     * @param Day String 制定月份 格式:dd
     * @param num String 加减的数量 以月为单位
     * @return String
     */
    public String rollMonth(String Month, String Day,int num){
        if(Month.trim().length()<=0){
            return "";
        }
        Timestamp time = StringTool.getTimestamp(Month,"yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // 当前月＋num
        cal.add(cal.MONTH, num);
        // 将下个月1号作为日期初始值
        cal.set(cal.DATE, 1);
        Timestamp month = new Timestamp(cal.getTimeInMillis());
        String result = StringTool.getString(month, "yyyyMM");
        String lastDayOfMonth = getLastDayOfMonth(result);
        if (TypeTool.getInt(Day) > TypeTool.getInt(lastDayOfMonth)) {
            result += lastDayOfMonth;
        }
        else {
            result += Day;
        }
        return result;
    }

    /**
     * 获取指定月份的最后一天的日期
     * @param date String 格式 YYYYMM
     * @return Timestamp
     */
    public String getLastDayOfMonth(String date) {
        if (date.trim().length() <= 0) {
            return "";
        }
        Timestamp time = StringTool.getTimestamp(date, "yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // 当前月＋1，即下个月
        cal.add(cal.MONTH, 1);
        // 将下个月1号作为日期初始值
        cal.set(cal.DATE, 1);
        // 下个月1号减去一天，即得到当前月最后一天
        cal.add(cal.DATE, -1);
        Timestamp result = new Timestamp(cal.getTimeInMillis());
        return StringTool.getString(result, "dd");
    }
    
    /*
     * 导出excel方法
     */
    public void onExcel(){
    	if (panel_0.isShowing()) {
    		if(tableValid.getRowCount() <= 0){
    			this.messageBox("没有数据！");
    			return;
    		}
    		ExportExcelUtil.getInstance().exportExcel(tableValid, "物资近效期统计");
    	}else {
    		if(tableQty.getRowCount() <= 0){
    			this.messageBox("没有数据！");
    			return;
    		}
    		ExportExcelUtil.getInstance().exportExcel(tableQty, "物资安全库存量统计");
    	}
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

}
