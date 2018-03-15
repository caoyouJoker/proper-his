package com.javahis.ui.inv;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import jdo.clp.intoPathStatisticsTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.system.textFormat.TextFormatINVMaterialloc;

/**
 * <p>Title: 物资盘点</p>
 *
 * <p>Description: 物资盘点</p>
 *
 * <p>Copyright: Copyright (c) ProperSoft 2013</p>
 *
 * <p>Company: ProperSoft</p>                          
 *  
 * @author fux
 * @version 1.0
 */
public class INVQtyCheckControl extends TControl{  

	//action的路径
	private static final String actionName = "action.inv.INVQtyCheckAction";
	
	private TParm SaveMain = new TParm();
	private boolean update=false;
	private TParm caseNoParm=new TParm();
	 
	
	/**
	 * 初始化
	 * */
	 public void onInit() {
	        super.onInit();	       
	        //获得全部控件
	        getTextFormat("ORG_CODE").setValue(Operator.getDept());
	        getTextFormat("ORG_CODE").setEnabled(false);
	        this.setValue("USE_DATE", new Date());
	        Timestamp date = StringTool.getTimestamp(new Date());
	        this.setValue("END_DATE",
                    date.toString().substring(0, 10).replace('-', '/') +
                    " 23:59:59");
	        this.setValue("START_DATE",
                    StringTool.rollDate(date, -7).toString().substring(0, 10).
                    replace('-', '/') + " 00:00:00");
	        
	        //callFunction("UI|INV_CODE|addEventListener",TTextFieldEvent.KEY_PRESSED, this, "getBarCode");
	        // TABLE_D值改变事件
	        getTable("TableD").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
			"onCheckBoxClicked");
	        
	        String org_code = this.getValueString("ORG_CODE");
	        if (!"".equals(org_code)) {
	            // 设定料位
	            ( (TextFormatINVMaterialloc) getComponent("LW")).
	                setOrgCode(org_code);
	            ( (TextFormatINVMaterialloc) getComponent("LW")).
	                onQuery();
	        }
	        this.initPage();
	        
	 }
	 
	 
	 
	 public void onCheckBoxClicked(Object obj){
			TTable table = (TTable) obj;
			table.acceptText();
			int row = table.getSelectedRow();
			int col=table.getSelectedColumn();
			//初始化    退货日期查询区间
	        Timestamp date = SystemTool.getInstance().getDate();
	        String startDate = date.toString().substring(0, 10).replace('-', '/') + " 00:00:00";
	        String endDate = date.toString().substring(0, 19).replace('-', '/');
			if (col==1) {
				String tag = table.getItemString(row, "RFLG");
				if ("Y".equals(tag)) {
					
					

					
					String sql="select sum(qty) qty from SPC_INV_RECORD where inv_code='"+table.getItemString(row, "INV_CODE")+"' and EXE_DEPT_CODE='"+Operator.getDept()+"' " +
							" and BILL_DATE BETWEEN TO_DATE('" + startDate.substring(0, 19) + "','yyyy/mm/dd hh24:mi:ss') AND TO_DATE('" + endDate.substring(0, 19) + "','yyyy/mm/dd hh24:mi:ss') ";
					TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
					table.setItem(row, "RE_QTY",parm.getData("QTY", 0));
					table.setItem(row, "MODI_QTY",table.getItemDouble(row, "STOCK_QTY")-parm.getDouble("QTY", 0)-table.getItemDouble(row, "USE_QTY"));
					
				}else {
//					String sql="select stock_qty from inv_stockm where inv_code='"+table.getItemString(row, "INV_CODE")+"' and org_code='"+Operator.getDept()+"'";
//					TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
//					parm.getData("STOCK_QTY", 0);
					table.setItem(row, "RE_QTY", 0);
					table.setItem(row, "MODI_QTY", table.getItemDouble(row, "STOCK_QTY")-table.getItemDouble(row, "USE_QTY"));
				}
			}
			if (col==2) {
				String tag = table.getItemString(row, "UFLG");
				if ("Y".equals(tag)) {
					String sql="select sum(qty) qty from inv_use where inv_code='"+table.getItemString(row, "INV_CODE")+"'" +
							" and use_dept='"+Operator.getDept()+"'";
					TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
					table.setItem(row, "USE_QTY",parm.getDouble("QTY", 0));
					table.setItem(row, "MODI_QTY",table.getItemDouble(row, "STOCK_QTY")-parm.getDouble("QTY", 0)-table.getItemDouble(row, "RE_QTY"));
				}else {
					table.setItem(row, "USE_QTY",0);
					table.setItem(row, "MODI_QTY",table.getItemDouble(row, "STOCK_QTY")-table.getItemDouble(row, "RE_QTY"));
				}
				
			}
			
		}

	 private void initPage() {
//		 String sql="select b.inv_code,b.inv_chn_desc,b.DESCRIPTION,d.sup_chn_desc, e.order_code,e.order_desc, ";
//			 	sql+="C.BASE_QTY,(C.BASE_QTY-C.STOCK_QTY) as OLD_QTY ,m.CONTRACT_PRICE  " ;
//			    sql+="from  inv_base b ";
//			    sql+="left join INV_STOCKM C on C.INV_CODE=b.INV_CODE ";
//				sql+="left join SYS_SUPPLIER d on D.SUP_CODE=b.up_sup_code ";
//				sql+="left join INV_AGENT m on m.INV_CODE=b.INV_CODE and m.SUP_CODE=b.SUP_CODE ";
//				sql+="left join SYS_FEE e on b.order_code =e.order_code ";
//				sql+="where  b.inv_code in (select f.inv_code from inv_stockm f where f.org_code='"+Operator.getDept()+"')";
		 
		 //SFLG;RFLG;UFLG;INV_CODE;INV_CHN_DESC;DESCRIPTION;UNIT_DESC;SUP_DESC;SUP_CHN_DESC;BASE_QTY;RE_QTY;USE_QTY;STOCK_QTY
				String sqlString="select 'Y' SFLG,'N' RFLG,'N' UFLG, " +
						" f.inv_code,b.inv_chn_desc,b.DESCRIPTION,d.sup_chn_desc SUP_DESC,e.sup_chn_desc sup_chn_desc,u.UNIT_CHN_DESC UNIT_DESC," +
						"  f.BASE_QTY, '0' RE_QTY,'0' USE_QTY, f.STOCK_QTY  ,'0' MODI_QTY" +
						"  from inv_stockm f" +
						" left join inv_base b on b.inv_code=f.inv_code " +
						" left join SYS_SUPPLIER d on D.SUP_CODE=b.sup_code " +
						" left join SYS_SUPPLIER e on e.SUP_CODE=b.up_sup_code " +
						" left join SYS_UNIT u on u.UNIT_CODE=b.stock_unit " +
						
						" where f.org_code='"+Operator.getDept()+"'";
				 TParm parm1 = new TParm(TJDODBTool.getInstance().select(sqlString));
				 System.out.println(sqlString);
				 TParm mainParm=new TParm();
//		    	 for (int i = 0; i < parm1.getCount("INV_CODE"); i++) {
//		    		 mainParm.setData("INV_CODE",i, parm1.getValue("INV_CODE", i));
//		    		 mainParm.setData("INV_CHN_DESC",i, parm1.getValue("INV_CHN_DESC", i));
//		    		 mainParm.setData("DESCRIPTION", i,parm1.getValue("DESCRIPTION", i));
//		    		 mainParm.setData("SUP_CHN_DESC",i, parm1.getValue("SUP_CHN_DESC", i));
//		    		 mainParm.setData("BASE_QTY",i, parm1.getDouble("BASE_QTY", i));
//		    		 mainParm.setData("MODI_QTY",i, 0);
//		    		 mainParm.setData("STOCK_QTY",i, 0);
////		    		 mainParm.setData("OLD_QTY",i, parm1.getDouble("OLD_QTY", i));
////		    		 mainParm.setData("CONTRACT_PRICE",i, parm1.getDouble("CONTRACT_PRICE", i));
////		    		 mainParm.setData("QTY",i, 0);
////		    		 mainParm.setData("FLG", i,'Y');
////		    		 mainParm.setData("TRUE_QTY",i, 0);
////		    		 mainParm.setData("ORDER_CODE",i, parm1.getValue("ORDER_CODE", i));
////		    		 mainParm.setData("ORDER_DESC",i, parm1.getValue("ORDER_DESC", i));
////		    		 mainParm.setData("INV_CODE",i, parm1.getValue("INV_CODE", i));
////		    		 mainParm.setData("ORG_CODE", i,Operator.getDept());
//				}
		    	 getTable("TableD").setParmValue(parm1);
		
	}

	    /**
	     * 表格值改变事件
	     *
	     * @param obj  
	     *            Object
	     */
	    public boolean onTableDChangeValue(Object obj) {
	    	getTable("TableD").acceptText();            
	        // 值改变的单元格
	        TTableNode node = (TTableNode) obj;
	        if (node == null)               
	            return false;
	        // 判断数据改变
	        if (node.getValue().equals(node.getOldValue()))
	            return true;
	        // Table的列名
	        String columnName = node.getTable().getDataStoreColumnName(
	            node.getColumn());
	        int row = node.getRow();
	        if ("STOCK_QTY".equals(columnName)) {
	            double qty = TypeTool.getDouble(node.getValue());
	            if (qty>0) {
	               double c=getTable("TableD").getItemDouble(row, "BASE_QTY")- qty ;
//                    Table.setItem(row, "AMT", c *
//                    		Table.getItemDouble(row, "CONTRACT_PRICE"));
	               getTable("TableD").setItem(row, "MODI_QTY",c);
	               
                    return false;
					
				}
	         
	            else {
	            	messageBox("错误的数值");
	               
	                    return false;
	                }
	        }  
	        return false;
	    }
     
	/**
	 * 保存
	 * */
	 public void onSave(){
//		 if (true) {
//			messageBox("保存成功");
//			//Table.removeAll();
//			Table.setParmValue(new TParm() );
//			
//			return;
//		}
		    getTable("TableD").acceptText();       
			TParm invRegressParm = new TParm();// 物资退货参数
			TParm tableParm = getTable("TableD").getShowParmValue();
			System.out.println(tableParm);
			int count = tableParm.getCount("INV_CODE");
			if(count <= 0){
				this.messageBox("无保存数据！");
				return;  
			}
			//调用密码验证？  fux 20140603
//			String c=checkPW();
//			if ("".equals(c)) {
//				this.messageBox("请重新保存！");
//				return;
//			}
			String returnNo = SystemTool.getInstance().getNo("ALL",
					"INV", "CHECK_NO", "No");
//			HashMap<String, Double> invMap = new HashMap<String, Double>();
			for (int i = 0; i < count; i++) {
				TParm parmV = tableParm.getRow(i);
				//盘点量改变
				if (new BigDecimal(parmV.getData("MODI_QTY").toString()).intValue()==0) {
					continue;
				}
				 Timestamp date = SystemTool.getInstance().getDate();
				
				invRegressParm.addData("CHECK_NO", returnNo);
				invRegressParm.addData("SEQ", i + 1);
				invRegressParm.addData("INV_CODE", parmV.getData("INV_CODE"));
				invRegressParm.addData("ORG_CODE", getTextFormat("ORG_CODE").getValue());
				invRegressParm.addData("STOCK_QTY", parmV.getData("STOCK_QTY"));
				invRegressParm.addData("MODI_QTY", parmV.getData("MODI_QTY"));   
				invRegressParm.addData("BASE_QTY", parmV.getData("BASE_QTY"));
				invRegressParm.addData("CHECK_USER", Operator.getID());
				invRegressParm.addData("CHECK_DATE", date);
				//
				//invRegressParm.addData("AGCHECK_USER", c);
				invRegressParm.addData("AGCHECK_USER", Operator.getID());
				invRegressParm.addData("AGCHECK_DATE", date);  
				invRegressParm.addData("OPT_USER", Operator.getID());
				invRegressParm.addData("OPT_TERM", Operator.getIP());
				invRegressParm.addData("OPT_DATE", date);
				invRegressParm.addData("LW", getTextFormat("LW").getValue()==null?"":getTextFormat("LW").getValue());
				invRegressParm.addData("CHECK_FLG", "Y");  
			}	
			//action.inv.INVVerifyinAction
			TParm result = TIOM_AppServer.executeAction(
					"action.inv.INVQtyCheckAction", "onSave222", invRegressParm);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
			this.messageBox("保存成功！"); 
			onClear();
		}
	  
		/**
		 * 调用密码验证
		 * 
		 * @return boolean
		 */
		public String checkPW() {                      
			String inwExe = "singleExe";
			TParm value = (TParm) this.openDialog(
					"%ROOT%\\config\\inv\\passWordCheck.x", inwExe);
			if (value == null) {  
				return "";
			}
			return value.getData("USER_ID").toString();
		}

	 
	 /**
	  * 计算总价
	  */
//	 public void getAr_Amt() {
//			Table.acceptText();
//			int row = Table.getSelectedRow();
//			double qty =  Table.getParmValue().getDouble("QTY", row);
//			double price = Table.getParmValue().getDouble("OWN_PRICE", row);
//			Table.setItem(row, "AR_AMT", qty*price);
//			SaveMain.setData("QTY", row, qty);
//       	    SaveMain.setData("AR_AMT", row, qty*price);
//			sum_arAmt();
//		}
	 
	 /**
	  * 计算总金额
	  */
//	 public void sum_arAmt(){
//		 double sum=0.0;
//		 Table.acceptText();
//		 int count = Table.getRowCount();
//		 for(int i=0;i<count;i++){
//			sum += Table.getParmValue().getDouble("AR_AMT", i);
//		 }
//
//		 this.setValue("AR_AMT", String.valueOf(sum));
//
//	 }
	 

	 /**
	  * 查询
	  * */
	 public void onQuery(){
		 

		 
		 String sql = "SELECT CHECK_NO,CHECK_DATE FROM INV_QTYCHECK A WHERE 1=1";
		 // 单号
	        if (!"".equals(this.getValueString("CHECK_NO").trim())) {
	            sql+=" and A.CHECK_NO='"+this.getValueString("CHECK_NO")+"'";
	        }
	        // 查询时间
	        if (!"".equals(this.getValueString("START_DATE")) &&
	            !"".equals(this.getValueString("END_DATE"))) {
	        	 String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
		         "START_DATE")), "yyyyMMdd")+" 00:00:00";
		         String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
		         "END_DATE")), "yyyyMMdd")+" 23:59:59";
		         
//		         System.out.println(startTime);
		         
		         sql += " AND A.CHECK_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
					+ "','yyyymmdd hh24:mi:ss')";
	        }
	        // 部门
	        if (!"".equals(this.getValueString("ORG_Q"))) {
	            sql+=" and A.ORG_CODE='"+this.getValueString("ORG_Q")+"'";
	        }
	        sql+=" group by CHECK_NO,CHECK_DATE";  
		 TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		 TParm tableParm = new TParm();
		 int count = selParm.getCount();
		 if(count<0){
			 this.messageBox("没有要查询的数据");
			 return;
		 }
		 for(int i=0;i<count;i++){
			 tableParm.addData("CHECK_NO", selParm.getValue("CHECK_NO", i));
			 tableParm.addData("CHECK_DATE", selParm.getValue("CHECK_DATE", i));
		 }
		 this.callFunction("UI|TableM|setParmValue", tableParm);
		 
		 
	


	
	 }
	 
	 
	 /**
	     * 主项表格(TABLE_M)单击事件
	     */
	    public void onTableMClicked() {
	   	 ( (TMenuItem) getComponent("delete")).setEnabled(false);
         ( (TMenuItem) getComponent("save")).setEnabled(false); 
        
         TTable table_m= getTable("TableM");
         TTable table_d= getTable("TableD");
	        int row = table_m.getSelectedRow();
	        if (row != -1) {
	     
	            table_d.removeRowAll();
	        	String sqlString="select f.inv_code,b.inv_chn_desc,b.DESCRIPTION,f.BASE_QTY,f.modi_qty,f.stock_qty,d.sup_chn_desc " +
				" from inv_qtycheck f" +
				" left join inv_base b on b.inv_code=f.inv_code " +
				" left join SYS_SUPPLIER d on D.SUP_CODE=b.sup_code " +
				
				" where f.check_no='"+table_m.getItemString(row, "CHECK_NO")+"'";
	        	System.out.println("sqlString"+sqlString);  
		 TParm parm1 = new TParm(TJDODBTool.getInstance().select(sqlString));
		 if (parm1 == null || parm1.getCount() <= 0) {
             this.messageBox("没有验收明细");
             return;
         }
		 TParm mainParm=new TParm();
    	 for (int i = 0; i < parm1.getCount("INV_CODE"); i++) {
    		 //SFLG;RFLG;UFLG;INV_CODE;INV_CHN_DESC;DESCRIPTION;UNIT_DESC;SUP_DESC;SUP_CHN_DESC;BASE_QTY;RE_QTY;USE_QTY;STOCK_QTY;MODI_QTY
    		 mainParm.setData("SFLG",i, "Y");
    		 mainParm.setData("RFLG",i, "N");
    		 mainParm.setData("UFLG",i, "N");
    		 mainParm.setData("INV_CODE",i, parm1.getValue("INV_CODE", i));
    		 mainParm.setData("INV_CHN_DESC",i, parm1.getValue("INV_CHN_DESC", i));
    		 mainParm.setData("DESCRIPTION", i,parm1.getValue("DESCRIPTION", i));
    		 mainParm.setData("SUP_DESC",i, parm1.getValue("SUP_CHN_DESC", i));
    		 mainParm.setData("SUP_CHN_DESC",i, parm1.getValue("SUP_CHN_DESC", i));
    		 mainParm.setData("BASE_QTY",i, parm1.getDouble("BASE_QTY", i));
    		 mainParm.setData("MODI_QTY",i,  parm1.getDouble("MODI_QTY", i));   
    		 mainParm.setData("STOCK_QTY",i,  parm1.getDouble("STOCK_QTY", i));  
//    		 mainParm.setData("OLD_QTY",i, parm1.getDouble("OLD_QTY", i));
//    		 mainParm.setData("CONTRACT_PRICE",i, parm1.getDouble("CONTRACT_PRICE", i));
//    		 mainParm.setData("QTY",i, 0);
//    		 mainParm.setData("FLG", i,'Y');
//    		 mainParm.setData("TRUE_QTY",i, 0);
//    		 mainParm.setData("ORDER_CODE",i, parm1.getValue("ORDER_CODE", i));
//    		 mainParm.setData("ORDER_DESC",i, parm1.getValue("ORDER_DESC", i));
//    		 mainParm.setData("INV_CODE",i, parm1.getValue("INV_CODE", i));
//    		 mainParm.setData("ORG_CODE", i,Operator.getDept());
		}
    	 getTable("TableD").setParmValue(mainParm);
	        }
	    }

	 
	 /**
	  * 删除
	  * */
	 public void onDelete(){ 				 		 		 
		 getTable("TableD").acceptText();
		 int selRow = getTable("TableD").getSelectedRow();
		 if(selRow <=0){
			 this.messageBox("没有要删除的数据，请选中要删除的行！");
		 }
		 getTable("TableD").removeRow(selRow);
		 
	 }
	 
	 /**
	  * 清空
	  * */
	 public void onClear(){   
		 ( (TMenuItem) getComponent("delete")).setEnabled(true);
         ( (TMenuItem) getComponent("save")).setEnabled(true); 
		 getTable("TableD").removeRowAll();
		 getTable("TableM").removeRowAll();
		 getTextField("NO").setValue("");
		 getTextField("NO_Q").setValue("");
		 getTextFormat("USE_DATE").setValue(new Date());
		 getTextFormat("ORG_CODE").setValue(Operator.getDept());
		 getTextFormat("ORG_Q").setValue(Operator.getDept());
		   
		 String org_code =Operator.getDept();
	        if (!"".equals(org_code)) {
	            // 设定料位
	            ( (TextFormatINVMaterialloc) getComponent("LW")).
	                setOrgCode(org_code);
	            ( (TextFormatINVMaterialloc) getComponent("LW")).
	                onQuery();
	        }
		 
	 }
	  
		/** 
	     * 得到TTextField对象
	     *
	     * @param tagName
	     *            元素TAG名称
	     * @return
	     */
	    private TTextField getTextField(String tagName) {
	        return (TTextField) getComponent(tagName);
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
	     * 得到TextFormat对象
	     *
	     * @param tagName
	     *            元素TAG名称
	     * @return
	     */
	    private TTextFormat getTextFormat(String tagName) {
	        return (TTextFormat) getComponent(tagName);
	    }

	    /**
	     * 得到TCheckBox对象
	     * @param tagName String
	     * @return TCheckBox
	     */
	    private TCheckBox getCheckBox(String tagName) {
	        return (TCheckBox) getComponent(tagName);
	    }
	
	
	
}
