package com.javahis.ui.ins;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Vector;

import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;

import jdo.sys.SystemTool;

/**
 * Title: 医保总额指标记录
 * Description:医保总额指标记录
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2014
 * @version 1.0
 */
public class INSTotalQuotaControl extends TControl {
    TParm data;
    int selectRow = -1;
	private TTable tableTotal;
	// 排序
	private Compare compare = new Compare();
	private int sortColumn = -1;
	private boolean ascending = false;
    public void onInit() {
        super.onInit();
        ( (TTable) getComponent("TABLE")).addEventListener("TABLE->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        tableTotal = (TTable) this.getComponent("TABLE");
        onClear();
        addListener(tableTotal);//数据排序
    }
    /**
     * 初始化数据
     */
    public void getData() {
     	//年度
    	String sysdate =StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
    	String year = sysdate.substring(0,4);  
    	this.setValue("YEAR",year);   	
    	//指标开始日期
    	String startdate =year+"-04-01 00:00:00";
    	Timestamp date = StringTool.getTimestamp(startdate, "yyyy-MM-dd HH:mm:ss");
    	this.setValue("START_DATE",date);
    }
    /**
     * 增加对Table的监听
     *
     * @param row
     */
    public void onTableClicked(int row) {
        // 选中行
        if (row < 0)
            return;
        setValueForParm(
            "YEAR;YEAR_QUOTA_AMT;YEAR_PAT_COUNT;MON_QUOTA_AMT;" +
            "MON_PAT_COUNT;AVERAGE_PAY_AMT;OWN_PAY_PERCENT",data, row);
        //显示类别
        if(data.getValue("INS_TYPE_DESC", row).equals("城职门诊")){
            this.setValue("INS_TYPE","01");
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //次均统筹支付不可编辑
        }
        if(data.getValue("INS_TYPE_DESC", row).equals("城职门特")){
            this.setValue("INS_TYPE","02");
            callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //次均统筹支付不可编辑
        }
        if(data.getValue("INS_TYPE_DESC", row).equals("城乡门特")){
            this.setValue("INS_TYPE","03");
            callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //次均统筹支付不可编辑
        }
        if(data.getValue("INS_TYPE_DESC", row).equals("城职住院")){
            this.setValue("INS_TYPE","04");
            callFunction("UI|AVERAGE_PAY_AMT|setEnabled", true); //次均统筹支付可编辑
        }
        if(data.getValue("INS_TYPE_DESC", row).equals("城乡住院")){
            this.setValue("INS_TYPE","05");
            callFunction("UI|AVERAGE_PAY_AMT|setEnabled", true); //次均统筹支付可编辑
        }
        //显示指标开始日期
//        String sysdate =StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
    	String year = data.getValue("YEAR", row);
    	String startdate = data.getValue("START_DATE", row);
    	String date =year+"-"+startdate.substring(0,2)+"-"+startdate.substring(2,4)+" 00:00:00";
    	Timestamp datejm = StringTool.getTimestamp(date, "yyyy-MM-dd HH:mm:ss");
    	this.setValue("START_DATE",datejm);
        selectRow = row;
        callFunction("UI|YEAR|setEnabled", false); //年度不可编辑
        callFunction("UI|INS_TYPE|setEnabled", false); //类别不可编辑
    }
    /**
     * 查询
     */
    public void onQuery() {
    	String sql =" SELECT YEAR,CASE  WHEN INS_TYPE='01' THEN '城职门诊'  " +
    			    " WHEN INS_TYPE='02' THEN '城职门特' " +   			    
    			    " WHEN INS_TYPE='03' THEN '城乡门特' " +
    			    " WHEN INS_TYPE='04' THEN '城职住院' " +
    			    " WHEN INS_TYPE='05' THEN '城乡住院' END AS INS_TYPE_DESC," +
    			    " YEAR_QUOTA_AMT,YEAR_PAT_COUNT,MON_QUOTA_AMT,"+
                    " MON_PAT_COUNT,AVERAGE_PAY_AMT,OWN_PAY_PERCENT," +
                    " START_DATE,OPT_USER,OPT_TERM,OPT_DATE,INS_TYPE"+
                    " FROM INS_TOTAL_QUOTA" +
                    " WHERE YEAR ='"+this.getValue("YEAR")+"'" +
                    " ORDER BY INS_TYPE";
    	data = new TParm(TJDODBTool.getInstance().select(sql));
		if (data.getErrCode() < 0) {
			this.messageBox("E0116");//没有数据
			return;
		}
        ((TTable) getComponent("TABLE")).setParmValue(data);
    }
    /**
     * 查询数据(初始化)
     */
    public void onQuerydata() {
    	//年度
    	String sysdate =StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
    	String year = sysdate.substring(0,4); 
    	String sql =" SELECT YEAR,CASE  WHEN INS_TYPE='01' THEN '城职门诊'  " +
    	            " WHEN INS_TYPE='02' THEN '城职门特' " +   			    
		            " WHEN INS_TYPE='03' THEN '城乡门特' " +
		            " WHEN INS_TYPE='04' THEN '城职住院' " +
		            " WHEN INS_TYPE='05' THEN '城乡住院' END AS INS_TYPE_DESC," +
    			    " YEAR_QUOTA_AMT,YEAR_PAT_COUNT,MON_QUOTA_AMT,"+
                    " MON_PAT_COUNT,AVERAGE_PAY_AMT,OWN_PAY_PERCENT," +
                    " START_DATE,OPT_USER,OPT_TERM,OPT_DATE,INS_TYPE"+
                    " FROM INS_TOTAL_QUOTA" +
                    " WHERE YEAR ='"+year+"'" +
                    " ORDER BY INS_TYPE";
    	data = new TParm(TJDODBTool.getInstance().select(sql));
		if (data.getErrCode() < 0) {
			this.messageBox("E0116");//没有数据
			return;
		}
        ((TTable) getComponent("TABLE")).setParmValue(data);
    }
    /**
     * 保存
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }

    /**
     * 新增
     */
    public void onInsert() {
        if (this.getValue("YEAR").equals("")) {
        	this.messageBox("年度不能为空");
            return;
        }
        if (this.getValue("INS_TYPE").equals("")) {
        	this.messageBox("类别不能为空");
            return;
        }
        //判断是否存在已有数据
        TParm parmQ = new TParm();
        parmQ.setData("YEAR", this.getValue("YEAR"));
        parmQ.setData("INS_TYPE", this.getValue("INS_TYPE"));
        String sqlQ= " SELECT * FROM INS_TOTAL_QUOTA B"+
        " WHERE  B.YEAR ='"+ parmQ.getValue("YEAR")+ "'"+
        " AND B.INS_TYPE ='"+ parmQ.getValue("INS_TYPE")+ "'";
        TParm resultQ = new TParm(TJDODBTool.getInstance().select(sqlQ)); 
//        System.out.println("resultQ=========="+resultQ.getData("YEAR"));
        if(resultQ.getData("YEAR")!=null){
            this.messageBox("数据已存在,不能保存");
        	return;   
        }        	
        TParm parm = getParmForTag("YEAR;INS_TYPE;YEAR_QUOTA_AMT;YEAR_PAT_COUNT;MON_QUOTA_AMT;" +
                                   "MON_PAT_COUNT;AVERAGE_PAY_AMT;OWN_PAY_PERCENT");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "START_DATE")), "yyyyMMdd"); //拿到界面的查询时间
        String startdate = date.substring(4,8);
        String sql= " INSERT INTO INS_TOTAL_QUOTA"+
		            " (YEAR,INS_TYPE,YEAR_QUOTA_AMT,YEAR_PAT_COUNT,MON_QUOTA_AMT,"+
		            " MON_PAT_COUNT,AVERAGE_PAY_AMT,OWN_PAY_PERCENT," +
		            " START_DATE,OPT_USER,OPT_TERM,OPT_DATE)"+ 
	                " VALUES ('"+ parm.getValue("YEAR")+ "','"+ parm.getValue("INS_TYPE")+ "'," +
	                 parm.getValue("YEAR_QUOTA_AMT")+ ","+ parm.getDouble("YEAR_PAT_COUNT")+ "," +
	                 parm.getValue("MON_QUOTA_AMT")+ ","+ parm.getDouble("MON_PAT_COUNT")+ "," +
	                 parm.getDouble("AVERAGE_PAY_AMT")+ ","+ parm.getDouble("OWN_PAY_PERCENT")+ ",'" +
	                 startdate + "','"+ parm.getValue("OPT_USER")+ "','" +
	                 parm.getValue("OPT_TERM")+ "',SYSDATE)";
//     System.out.println("sql=========="+sql);   
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//        System.out.println("result=========="+result);  
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        onQuery();
        this.messageBox("保存成功");
    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag("YEAR;INS_TYPE;YEAR_QUOTA_AMT;YEAR_PAT_COUNT;MON_QUOTA_AMT;" +
                                   "MON_PAT_COUNT;AVERAGE_PAY_AMT;OWN_PAY_PERCENT");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
                      "START_DATE")), "yyyyMMdd"); //拿到界面的查询时间
        String startdate = date.substring(4,8);
        String sql = "UPDATE INS_TOTAL_QUOTA SET YEAR ='"
		+ parm.getValue("YEAR") + "',INS_TYPE ='"
		+ parm.getValue("INS_TYPE") + "',YEAR_QUOTA_AMT ="
		+ parm.getValue("YEAR_QUOTA_AMT") + ",YEAR_PAT_COUNT ="
		+ parm.getDouble("YEAR_PAT_COUNT") + ",MON_QUOTA_AMT ="
		+ parm.getValue("MON_QUOTA_AMT") + ",MON_PAT_COUNT ="
		+ parm.getDouble("MON_PAT_COUNT") + ",AVERAGE_PAY_AMT="
		+ parm.getDouble("AVERAGE_PAY_AMT") + ",OWN_PAY_PERCENT="
		+ parm.getDouble("OWN_PAY_PERCENT") + ",START_DATE ='"
		+ startdate + "',OPT_USER ='"
		+ parm.getValue("OPT_USER") + "',OPT_TERM ='"
		+ parm.getValue("OPT_TERM") + "',OPT_DATE = SYSDATE" +
		" WHERE YEAR = '"
		+ parm.getValue("YEAR") + "'" +
		" AND INS_TYPE='"
		+ parm.getValue("INS_TYPE") + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        onQuery();
        this.messageBox("修改成功");
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("INS_TYPE;YEAR_QUOTA_AMT;YEAR_PAT_COUNT;MON_QUOTA_AMT;"+
                   "MON_PAT_COUNT;AVERAGE_PAY_AMT;OWN_PAY_PERCENT;START_DATE");
        ((TTable) getComponent("TABLE")).removeRowAll();
        selectRow = -1;
        callFunction("UI|YEAR|setEnabled", true); //年度可编辑
        callFunction("UI|INS_TYPE|setEnabled", true); //类别可编辑
        callFunction("UI|AVERAGE_PAY_AMT|setEnabled", true); //次均统筹支付可编辑
		//初始化数据
		onQuerydata();
		//年度,日期
        getData();
    }
    /**
     * 年总额指标回车事件
     */
    public void yearAmt(){
    	//显示月总额指标   	
        double amt=this.getValueDouble("YEAR_QUOTA_AMT");
        double monthamt = (amt*10000)/12;
        this.setValue("MON_QUOTA_AMT", monthamt);
        this.grabFocus("YEAR_PAT_COUNT");
    }
    /**
     * 年人次指标回车事件
     */
    public void yearCount(){
        //显示月人次指标
        double count=this.getValueDouble("YEAR_PAT_COUNT");
        this.setValue("MON_PAT_COUNT", count/12);
        if(this.getValue("INS_TYPE").equals("01"))//城职门诊
        this.grabFocus("OWN_PAY_PERCENT");
        if(this.getValue("INS_TYPE").equals("02"))//城职门特
        this.grabFocus("OWN_PAY_PERCENT");
        if(this.getValue("INS_TYPE").equals("03"))//城乡门特
        this.grabFocus("OWN_PAY_PERCENT");
        if(this.getValue("INS_TYPE").equals("04"))//城职住院
        this.grabFocus("AVERAGE_PAY_AMT");
        if(this.getValue("INS_TYPE").equals("05"))//城乡住院
        this.grabFocus("AVERAGE_PAY_AMT");
    }   
    /**
     * 次均统筹支付回车事件
     */
    public void averagepayAmt(){
    	this.grabFocus("OWN_PAY_PERCENT");
    }
    /**
     * 类别选择事件
     */
    public void instype(){
    	if(this.getValue("INS_TYPE").equals("01")){//城职门诊
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //次均统筹支付不可编辑
    		this.grabFocus("YEAR_QUOTA_AMT");
    	}else if(this.getValue("INS_TYPE").equals("02")){//城职门特
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //次均统筹支付不可编辑
    		this.grabFocus("YEAR_QUOTA_AMT");
    	}else if(this.getValue("INS_TYPE").equals("03")){//城乡门特
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //次均统筹支付不可编辑
    		this.grabFocus("YEAR_QUOTA_AMT");
    	}else if(this.getValue("INS_TYPE").equals("04")){//城职住院
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", true); //次均统筹支付可编辑
    		this.grabFocus("YEAR_QUOTA_AMT");
    	}else if(this.getValue("INS_TYPE").equals("05")){//城乡住院
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", true); //次均统筹支付可编辑
    		this.grabFocus("YEAR_QUOTA_AMT");
    	}
    	clearValue("YEAR_QUOTA_AMT;YEAR_PAT_COUNT;MON_QUOTA_AMT;MON_PAT_COUNT;"+
                   "AVERAGE_PAY_AMT;OWN_PAY_PERCENT");
    }
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}

				// 表格中parm值一致
				// 1.取paramw值;
				TParm tableData = tableTotal.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// 3.根据点击的列,对vector排序
				// 表格排序的列名;
				String tblColumnName = tableTotal.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
			}
		});
	}
	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
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
		tableTotal.setParmValue(parmTable);
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
	
}
