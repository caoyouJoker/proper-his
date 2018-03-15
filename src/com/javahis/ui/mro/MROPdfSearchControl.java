package com.javahis.ui.mro;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 未生成PDF文件查询功能 </p>
 *
 * <p>Description: 主要针对检验、检查的值 </p>
 *
 * <p>Copyright: Copyright (c) 2013 </p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @version 1.0
 */
public class MROPdfSearchControl
        extends TControl {

	private TTable table;
    private BILComparator compare = new BILComparator();//add by wanglong 20130909
    private boolean ascending = false;
    private int sortColumn =-1;
	/**
	 * 初始化
	 */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE"); // 病患基本信息列表
        addListener(table);
        initUI();
    }

    /**
     * 界面初始化
     */
    private void initUI(){//add by wanglong 20130909
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("START_DATE", StringTool.rollDate(date,-7));
        this.setValue("END_DATE", date);
        this.setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance()
                .getRegionIsEnabled(this.getValueString("REGION_CODE")));
    }
    
    /**
	 * 查询方法
	 */
    public void onQuery() {
        // 检验
        if (StringUtil.isNullString(this.getValueString("START_DATE"))) {
            this.messageBox("请输入日期");
            this.grabFocus("START_DATE");
            return;
        }
        if (StringUtil.isNullString(this.getValueString("END_DATE"))) {
            this.messageBox("请输入日期");
            this.grabFocus("END_DATE");
            return;
        }
        String sql =//modify by wanglong 20130909
                "SELECT A.APPLICATION_NO,A.MR_NO,A.PAT_NAME,A.ORDER_CODE,A.ORDER_DESC,A.OPTITEM_CHN_DESC,"
                        + "       A.ORDER_DATE,A.ORDER_DR_CODE,A.STATUS,A.REPORT_DATE,A.REPORT_DR,A.RESERVED_DATE,"
                        + "       A.REGISTER_DATE,INSPECT_DATE,EXEC_DR_CODE,A.EXAMINE_DATE,A.EXAMINE_DR "
                        + "  FROM MED_APPLY A, ADM_INP B "
                        + " WHERE A.CASE_NO = B.CASE_NO "
                        + "   AND A.STATUS NOT IN ('0', '1', '9') "
                        + "   AND A.PDFRE_FLG IS NULL "
                        + "   AND B.DS_DATE IS NOT NULL "
                        + "   AND B.DS_DATE BETWEEN TO_DATE('#','YYYYMMDD') AND TO_DATE('#'||'235959','YYYYMMDDHH24MISS') ";
        sql = sql.replaceFirst("#", this.getText("START_DATE").replaceAll("/", ""));
        sql = sql.replaceFirst("#", this.getText("END_DATE").replaceAll("/", ""));
        if (!this.getValueString("REGION_CODE").equals("")) {
            sql += " AND A.REGION_CODE='" + this.getValue("REGION_CODE") + "' ";
        }
        if (!this.getValueString("MR_NO").equals("")) {
            sql += " AND A.MR_NO='" + this.getValue("MR_NO") + "' ";
        }
        sql += " ORDER BY A.CASE_NO,A.APPLICATION_NO,A.ORDER_CODE ";
        // System.out.println("-------SQL---------"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("查询失败");
            return;
        }
        if (result.getCount() <= 0) {
            this.messageBox("没有需要查询的数据");
            table.setParmValue(new TParm());
            this.setValue("COUNT", 0);
            return;
        }
        table.setParmValue(result);
        ((TTextField) getComponent("COUNT")).setValue(result.getCount() + "");
    }
	
    /**
     * 病案号回车事件
     */
    public void onMrNo() {
        String mr_no = this.getValueString("MR_NO");
        Pat pat = Pat.onQueryByMrNo(mr_no);
        if (pat == null) {
            this.messageBox("没有查询数据");
            this.clearValue("MR_NO;PAT_NAME");
            return;
        }
       // modify by huangtt 20160929 EMPI患者查重提示 start
        mr_no=PatTool.getInstance().checkMrno(getValueString("mr_no")); 
        this.setValue("MR_NO",mr_no);
        if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
			
		}	
        // modify by huangtt 20160929 EMPI患者查重提示 end 
        this.setValue("MR_NO", pat.getMrNo());
        this.setValue("PAT_NAME", pat.getName());
    }
	
	/**
	 * 汇出Excel
	 */
    public void onExport() {
        if (table.getRowCount() > 0) {
            ExportExcelUtil.getInstance().exportExcel(table,  "未生成PDF病历查询");
        }
    }
    
	/**
	 * 清空 
	 */
    public void onClear() {
        this.clearValue("DEPT_CODE;STATION_CODE;MR_NO;PAT_NAME;COUNT");
        initUI();
        table.setParmValue(new TParm());
    }
    
    // ====================排序功能begin======================//add by wanglong 20130909
    /**
     * 加入表格排序监听方法
     * @param table
     */
    public void addListener(final TTable table) {
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
