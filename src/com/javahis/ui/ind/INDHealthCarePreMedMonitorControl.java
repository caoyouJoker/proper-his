package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

import jdo.odi.OdiObject;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 非医保贵重药品重点监测品种
 * </p>
 *
 * <p>
 * Description: 门诊用药与住院用药
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: BlueCore
 * </p>
 *
 * @author chenhong 2013.03.25
 * @version 1.0
 */


public class INDHealthCarePreMedMonitorControl extends TControl {
	
	//医嘱实现那块需要个计数
	private int flag=0;
	
	
	public INDHealthCarePreMedMonitorControl(){
		
	}
	
	public void onInit(){
		super.init();
		initPage();
	}
	
	// initPage
	public void initPage() {
		// 初始化统计区间
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// 结束时间
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (本月25)
		setValue("END_DATE", dateTime);

		// 起始时间(上个月26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");
		
		//初始化科室
		setValue("Kind", "O");
		
		//设置区域
		setValue("REGION_CODE", "H01");
		
		
		//定义一个容器，设置参数类型为“PHA”（即药品）
		TParm parmIn = new TParm();
        parmIn.setData("CAT1_TYPE","PHA");
        
		this.getTextField("ORDER_CODE").setPopupMenuParameter(
                "UD",
                getConfigParm().newConfig(
                    "%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);
		//定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        
        
        //删除医嘱名称所在的table一行记录
        TTable  table = (TTable)this.getComponent("Table_Order") ;
        callFunction("UI|" + table + "|addEventListener", table + "->"
				+ TTableEvent.CLICKED, this, "onDelete");


	}
	
	
	
	
	 /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }
	
	
	
	/*
	 * 向Table_Order中添加医嘱
	 * 
	 */
    
	public void addOrder(){
		String ORDER_CODE=getValueString("ORDER_CODE");
		String ORDER_DESC=getValueString("ORDER_DESC");
		
		TParm tParm=new TParm();
		TTable  table = (TTable)this.getComponent("Table_Order") ;
		
		if(null==ORDER_CODE||"".equals(ORDER_CODE)){
			this.messageBox("未输入医嘱！");
			return;
		}
		
		if(flag==0){
			//先新建数据，然后可以利用table.setData(tParm);添加到table中
			tParm.setData("ORDER_CODE", ORDER_CODE);
			tParm.setData("ORDER_DESC", ORDER_DESC);
			table.addRow(tParm);
			table.update();
			flag++;
		}else {
				for(int j=0;j<flag;j++){
					if(table.getItemString(j, "ORDER_CODE").equals(ORDER_CODE)){
						this.messageBox("该医嘱已添加！");
						return;
					}
				}
					
				//先新建数据，然后可以利用table.setData(tParm);添加到table中
				tParm.setData("ORDER_CODE", ORDER_CODE);
				tParm.setData("ORDER_DESC", ORDER_DESC);
				table.addRow(tParm);
				table.update();
				flag++;
		}
		
	}
	
	
	/*
	 * 医嘱名称所在的table中点删除就会删除一条记录
	 */
	public void onDelete() {
		TTable  table = (TTable)this.getComponent("Table_Order") ;
        int delrow = table.getSelectedRow();
        table.removeRow(delrow);
        flag--;
        
    }
	
	
	/*public void onCheckBoxValueST(Object obj){
		TTable table=(TTable)obj;
		table.acceptText();
		int column=table.getSelectedColumn();
		String columnName=this.getTable("Table_Order").getDataStoreColumnName(column);
		int row=table.getSelectedRow();
		TParm linkParm=table.getDataStore().getRowParm(row);
		if("LINKMAIN_FLG".equals(columnName)){
			if("Y".equals(linkParm.getValue("LINKMAIN_FLG"))){
				if(linkParm.getValue("ORDER_CODE").length()==0){
					//请开立遗嘱
					this.messageBox("E0152");
					return;
				}
				
				//查询最大连结号
				TParm linkP=new TParm();
				linkP.setData("OPERATE", linkParm.getValue("OPERATE"));
				linkP.setData("ORDER_CODE", linkParm.getValue("ORDER_CODE"));
				linkP.setData("ORDER_DESC", linkParm.getValue("ORDER_DESC"));
				
			}
		}
		
		
	}*/
	
	
	
	 /**
     * 根据医嘱名称以及记账日期进行查询
     */
    public void onQuery(){
    	TTable  table1 = (TTable)this.getComponent("TTable") ;
    	TTable  table = (TTable)this.getComponent("Table_Order") ;
    	TParm newdata=null;
    	String order_code="";
    	//获取order_code
    	if(flag==1){
    		order_code=" ='"+table.getItemString(0, "ORDER_CODE")+"' ";
    	}else{
    		for(int i=0;i<flag-1;i++){
       		 	order_code="'"+(String)table.getItemString(i, "ORDER_CODE")+"','";
    			}
    			order_code+=table.getItemString(flag-1, "ORDER_CODE")+"'";
    			order_code="in ( "+order_code+")";
    	}
    	
    	 
    	//获取科室类别
    	String kind=this.getValueString("Kind");
        
    	//如果可是类别没有选择的话，默认为门诊
    	if(null==kind||"".equals(kind)){
    		kind="O";
    	}
    	
    	
    	String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	
    	String bill_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND A.BILL_DATE BETWEEN TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
    	
    	/* //判断从前台是否选择了医嘱，如果为空，选择一下默认
    	if("=''".equals(order_code)){
    		
    		order_code=" in	('2C010009',"
    						+"'2C010007',"
    						+"'2G041030',"
    						+"'2C020013',"
    						+"'2A020009',"
    						+"'2W020040',"
    						+"'2N020015',"
    						+"'2S040013',"
    						+"'1N080013',"
    						+"'2A020007')";
    		
    		
    	}*/
    	
    		
    	//非医保贵重药品重点监测品种（住院用药情况）
    	String sql_I= "SELECT DISTINCT E.PAT_NAME,B.REGION_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, A.DOSAGE_QTY,"
    			  +"F.UNIT_CHN_DESC, A.OWN_PRICE,A.TOT_AMT,C.USER_NAME, D.DEPT_CHN_DESC,A.BILL_DATE "
    			  +"FROM (SELECT A.ORDER_CODE,A.DOSAGE_QTY,A.OWN_PRICE,A.TOT_AMT,A.BILL_DATE,A.DR_CODE,"
    			  +"A.DEPT_CODE,A.CASE_NO,A.DOSAGE_UNIT "
    			  +"FROM IBS_ORDD A "
    			  +"WHERE A.CAT1_TYPE = 'PHA' "
    			  +bill_date
    			  +") A,"
    			  +"SYS_FEE B,SYS_OPERATOR C,SYS_DEPT D,MRO_RECORD E,SYS_UNIT F "
    			  +"WHERE A.ORDER_CODE = B.ORDER_CODE AND A.DR_CODE = C.USER_ID AND A.DEPT_CODE = D.DEPT_CODE "
    			  +"AND A.CASE_NO = E.CASE_NO AND A.DOSAGE_UNIT = F.UNIT_CODE "
    			  +"AND B.ORDER_CODE "
    			  +order_code
    			  +" ORDER BY A.ORDER_CODE";
        
    	//非医保贵重药品重点监测品种（门诊用药情况）
    	String sql_O= "SELECT DISTINCT E.PAT_NAME,B.REGION_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, A.DOSAGE_QTY,"
	  			  +"F.UNIT_CHN_DESC, A.OWN_PRICE,A.AR_AMT,C.USER_NAME, D.DEPT_CHN_DESC,A.BILL_DATE "
	  			  +"FROM (SELECT A.ORDER_CODE,A.DOSAGE_QTY,A.OWN_PRICE,A.AR_AMT,A.BILL_DATE,A.DR_CODE,"
	  			  +"A.DEPT_CODE,A.MR_NO,A.DOSAGE_UNIT "
	  			  +"FROM OPD_ORDER A "
	  			  +"WHERE A.CAT1_TYPE = 'PHA' "
	  			  +bill_date
	  			  +") A,"
	  			  +"SYS_FEE B,SYS_OPERATOR C,SYS_DEPT D,MRO_RECORD E,SYS_UNIT F "
	  			  +"WHERE A.ORDER_CODE = B.ORDER_CODE AND A.DR_CODE = C.USER_ID AND A.DEPT_CODE = D.DEPT_CODE "
	  			  +"AND A.MR_NO = E.MR_NO AND A.DOSAGE_UNIT = F.UNIT_CODE "
	  			  +"AND B.ORDER_CODE "
	  			  +order_code
	  			  +" ORDER BY A.ORDER_CODE";
    	
    	
    	if(kind.equals("I")){
    	     newdata = new TParm(TJDODBTool.getInstance().select(sql_I));
    	}else {
    		newdata = new TParm(TJDODBTool.getInstance().select(sql_O));
    	}
    	
    	
    	
    	if(newdata.getErrCode() < 0 ){
    		this.messageBox(newdata.getErrText());
    		return;
    	}
        if(newdata.getCount() <= 0)
        {
        	this.messageBox("查无数据");
        }
    	
        table1.setParmValue(newdata);
    }
    
	
	
	
    /**
     * 汇出Excel
     */
    public void onExport() {
        TTable table = this.getTable("TTable");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "非医保贵重药品重点监测品种");
    }
	
	
    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "ORDER_CODE;ORDER_DESC;START_DATE;END_DATE";
        this.clearValue(clearStr);

		// 初始化统计区间
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// 结束时间
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (本月25)
		setValue("END_DATE", dateTime);

		// 起始时间(上个月26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");
     		
        TTable  table1 = this.getTable("TTable");
        TTable  table = this.getTable("Table_Order");

        table1.removeRowAll();
        table.removeRowAll();
    }
    
	
   
    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
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
}
