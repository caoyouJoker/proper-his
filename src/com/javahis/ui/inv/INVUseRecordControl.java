package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.util.SystemOutLogger;
import org.hibernate.mapping.Map;
import org.mortbay.jetty.security.SSORealm;

import jdo.inv.INVRegressGoodTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdiUtil;

/**
 * <p>Title: 物联网耗用记录</p>
 *
 * <p>Description: 物联网耗用记录</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2013</p>
 *
 * <p>Company: BlueCore</p> 
 *
 * @author caowl
 * @version 1.0
 */
public class INVUseRecordControl extends TControl{

	
	TTable Table;
	public static final String TABLE = "Table";
	//action的路径
	private static final String actionName = "action.inv.INVUseRecordAction";
	
	private int SaveDataI=1; //保存数据的编号
	private TParm SaveMain = new TParm();
	private boolean update=false;
	private TParm caseNoParm=new TParm();
	
	
	/**
	 * 初始化
	 * */
	 public void onInit() {
	        super.onInit();	       
	        //获得全部控件
	        getAllComponent(); 
	        getTextFormat("ORG_CODE").setValue(Operator.getDept());
	        getTextFormat("ORG_CODE").setEnabled(false);
	        this.setValue("USE_DATE", new Date());
	        
	        //callFunction("UI|INV_CODE|addEventListener",TTextFieldEvent.KEY_PRESSED, this, "getBarCode");
	        // TABLE_D值改变事件
//	        addEventListener("Table->" + TTableEvent.CHANGE_VALUE,
//	                         "onTableDChangeValue"); 
	        this.initPage();
	        
	 }
	 private void initPage() {
		 String sql="select b.inv_code,b.inv_chn_desc,b.DESCRIPTION,d.sup_chn_desc, e.order_code,e.order_desc, ";
			 	sql+="C.BASE_QTY,(C.BASE_QTY-C.STOCK_QTY) as OLD_QTY ,m.CONTRACT_PRICE  " ;
			    sql+="from  inv_base b ";
			    sql+="left join INV_STOCKM C on C.INV_CODE=b.INV_CODE ";
				sql+="left join SYS_SUPPLIER d on D.SUP_CODE=b.up_sup_code ";
				sql+="left join INV_AGENT m on m.INV_CODE=b.INV_CODE and m.SUP_CODE=b.SUP_CODE ";
				sql+="left join SYS_FEE e on b.order_code =e.order_code ";
				sql+="where b.inv_code like '08%' and b.inv_code in (select f.inv_code from inv_stockm f where f.org_code='"+Operator.getDept()+"')";
				
				 TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
				 TParm mainParm=new TParm();
				 
		    	 for (int i = 0; i < parm1.getCount("INV_CODE"); i++) {
		    		 mainParm.setData("INV_CODE",i, parm1.getValue("INV_CODE", i));
		    		 mainParm.setData("INV_CHN_DESC",i, parm1.getValue("INV_CHN_DESC", i));
		    		 mainParm.setData("INV_CODE",i, parm1.getValue("INV_CODE", i));
		    		 mainParm.setData("DESCRIPTION", i,parm1.getValue("DESCRIPTION", i));
		    		 mainParm.setData("SUP_CHN_DESC",i, parm1.getValue("SUP_CHN_DESC", i));
		    		 mainParm.setData("BASE_QTY",i, parm1.getDouble("BASE_QTY", i));
		    		 mainParm.setData("OLD_QTY",i, parm1.getDouble("OLD_QTY", i));
		    		 mainParm.setData("CONTRACT_PRICE",i, parm1.getDouble("CONTRACT_PRICE", i));
		    		 mainParm.setData("QTY",i, 0);
		    		 mainParm.setData("FLG", i,'Y');
		    		 mainParm.setData("TRUE_QTY",i, 0);
		    		 mainParm.setData("ORDER_CODE",i, parm1.getValue("ORDER_CODE", i));
		    		 mainParm.setData("ORDER_DESC",i, parm1.getValue("ORDER_DESC", i));
		    		 mainParm.setData("INV_CODE",i, parm1.getValue("INV_CODE", i));
		    		 mainParm.setData("ORG_CODE", i,Operator.getDept());
				}
		    	 Table.setParmValue(mainParm);
		
	}

	    /**
	     * 表格值改变事件
	     *
	     * @param obj  
	     *            Object
	     */
	    public boolean onTableDChangeValue(Object obj) {
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
	        if ("QTY".equals(columnName)) {
	            double qty = TypeTool.getDouble(node.getValue());
	            if (qty>0) {
	            	messageBox("qty"+qty);
	               
                    Table.setItem(row, "AMT", qty *
                    		Table.getItemDouble(row, "CONTRACT_PRICE"));
                    Table.setItem(row, "TRUE_QTY",Table.getItemDouble(row, "BASE_QTY")-Table.getItemDouble(row, "OLD_QTY")- qty );
                    return true;
					
				}
	         
	            else {
	            	messageBox("错误的数值");
	               
	                    return false;
	                }
	        }  
	        if ("TRUE_QTY".equals(columnName)) {
	            double qty = TypeTool.getDouble(node.getValue());
	            if (qty>0) {
	            	messageBox("trueqty"+qty);
	               double c=Table.getItemDouble(row, "BASE_QTY")-Table.getItemDouble(row, "OLD_QTY")- qty ;
                    Table.setItem(row, "AMT", c *
                    		Table.getItemDouble(row, "CONTRACT_PRICE"));
                    Table.setItem(row, "QTY",c);
                    return true;
					
				}
	         
	            else {
	            	messageBox("错误的数值");
	               
	                    return false;
	                }
	        }  
	        return false;
	    }
	 
	 //获得全部控件
	 public void getAllComponent(){
		 Table = (TTable)this.getComponent("Table");
	 }
	 


     /**
      * 得到条码号
      * */
     public void getBarCode(){
    	 if (getValueString("ORG_CODE")==null||"".equals(getValueString("ORG_CODE"))) {
    		 messageBox("部门科室有误！");
    		 return;
			
		}
    	 
    	 if (getValueString("INV_CODE")!=null&&getValueString("INV_CODE").trim()!=null&&getValueString("INV_CODE").trim().length()>0) {
     		//getTextField("BAR_CODE").setEditable(false);     		
         	getTextField("INV_CODE").grabFocus();
 		 }
    	 else {
			return;
		}
    	 messageBox("11");
    	
    	 //-----------------如果是高值  只能扫一遍 ----如果是药品或者低值，条码号相同时，数量加1，详细在onTableValue方法里 ----
    	 String barCode = this.getValueString("INV_CODE");
    	 String orgcode = this.getValueString("ORG_CODE");
    	 String sql1 = "SELECT A.INV_CODE,A.INV_CHN_DESC,A.DESCRIPTION,B.SUP_CHN_DESC,C.BASE_QTY,(C.BASE_QTY-C.STOCK_QTY) as OLD_QTY ," +
    	 		"D.CONTRACT_PRICE,A.ORDER_CODE,E.ORDER_DESC FROM INV_BASE A " +
    	 		"left join SYS_SUPPLIER B on A.UP_SUP_CODE=B.SUP_CODE " +
    	 		"left join INV_STOCKM C on C.INV_CODE=A.INV_CODE " +
    	 		"left join INV_AGENT D on D.INV_CODE=A.INV_CODE and D.SUP_CODE=A.SUP_CODE " +
    	 		"left join SYS_FEE E on E.ORDER_CODE=A.ORDER_CODE" +
    	 		" WHERE  A.INV_CODE='"+barCode+"' and C.ORG_CODE='"+orgcode+"'";
    	 TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
    	 if (parm1.getCount("INV_CODE")<=0) {
			messageBox("错误的编号，重新扫描");
			return;
    	 }
    	 //----------------------end-----------------
//    	 
//    	 if(update){
//    		 update=false;
//    		 Table.setLockRows("");
//    		 Table.removeRowAll();
//    		 this.addDefaultRowForTable(TABLE);
//    		 callFunction("UI|delete|setEnabled", false);
//    		 SaveMain = new TParm();
//    		 
//    	 }
    	 TParm mainParm=new TParm();
    	 for (int i = 0; i < parm1.getCount("INV_CODE"); i++) {
    		 mainParm.setData("INV_CODE", parm1.getValue("INV_CODE", i));
    		 mainParm.setData("INV_CHN_DESC", parm1.getValue("INV_CHN_DESC", i));
    		 mainParm.setData("INV_CODE", parm1.getValue("INV_CODE", i));
    		 mainParm.setData("DESCRIPTION", parm1.getValue("DESCRIPTION", i));
    		 mainParm.setData("SUP_CHN_DESC", parm1.getValue("SUP_CHN_DESC", i));
    		 mainParm.setData("BASE_QTY", parm1.getDouble("BASE_QTY", i));
    		 mainParm.setData("OLD_QTY", parm1.getDouble("OLD_QTY", i));
    		 mainParm.setData("CONTRACT_PRICE", parm1.getDouble("CONTRACT_PRICE", i));
    		 mainParm.setData("QTY", 0);
    		 mainParm.setData("FLG", 'Y');
    		 mainParm.setData("TRUE_QTY", 0);
    		 mainParm.setData("ORDER_CODE", parm1.getValue("ORDER_CODE", i));
    		 mainParm.setData("ORDER_DESC", parm1.getValue("ORDER_DESC", i));
    		 mainParm.setData("INV_CODE", parm1.getValue("INV_CODE", i));
    		 mainParm.setData("ORG_CODE", Operator.getDept());
		}
    	 Table.addRow(mainParm);
    	 this.setValue("INV_CODE", "");
    	
     }
  
     
	/**
	 * 保存
	 * */
	 public void onSave(){
		 if (true) {
			messageBox("保存成功");
			//Table.removeAll();
			Table.setParmValue(new TParm() );
			
			return;
		}
		 Table.acceptText();
//			TParm parm = new TParm();// 得到录入数量为负数的数据
			TParm invRegressParm = new TParm();// 物资退货参数
			TParm invStockParm = new TParm();// 扣库参数
			TParm tableParm = Table.getShowParmValue();
			int count = tableParm.getCount("INV_CODE");
			if(count <= 0){
				this.messageBox("无保存数据！");
				return;
			}
//			HashMap<String, Double> invMap = new HashMap<String, Double>();
			for (int i = 0; i < count; i++) {
				TParm parmV = tableParm.getRow(i);
				
				TParm returnTparm = new TParm();
				//根据取号原则得到退货编号
				String returnNo = SystemTool.getInstance().getNo("ALL",
								"INV", "INV_USERECORD", "No");
				invRegressParm.addData("RETURN_NO", returnNo);
				invRegressParm.addData("SEQ", returnTparm.getInt("SEQ", 0) + 1);
				invRegressParm.addData("INV_CODE", parmV.getData("INV_CODE"));
				invRegressParm.addData("RFID", parmV.getData("RFID"));
				invRegressParm.addData("RETURN_USER", Operator.getID());
				invRegressParm.addData("RETURN_DEPT", Operator.getDept());
				invRegressParm.addData("SUP_CODE", "");
				invRegressParm.addData("REASON", "");
				invRegressParm.addData("OPT_USER", Operator.getID());
				invRegressParm.addData("OPT_TERM", Operator.getIP());
				invRegressParm.addData("QTY", parmV.getData("QTY"));
				
				// 高值
				if (parmV.getData("INV_CODE") != null
						&& (!parmV.getData("INV_CODE").toString().equals(""))
						&& parmV.getData("INV_CODE").toString().substring(0, 2).equals("08")) {

//					// 扣库参数组合
//					if (invMap.containsKey(parmV.getData("INV_CODE").toString())) {
//						System.out.println("走IF");
//						Double double1 = invMap.get(parmV.getData("INV_CODE")
//								.toString());
//						invMap.put(parmV.getData("INV_CODE").toString(), double1
//								+ parmV.getDouble("QTY"));
//					} else {
//						System.out.println("走ELSE");
//						invMap.put(parmV.getData("INV_CODE").toString(), parmV
//								.getDouble("QTY"));
//					}
					
					invStockParm.addData("INV_CODE", parmV.getData("INV_CODE"));
					invStockParm.addData("QTY", parmV.getData("QTY"));
					invStockParm.addData("ORG_CODE", Operator.getDept());
					invStockParm.addData("RFID", parmV.getData("RFID"));
					invStockParm.addData("OPT_USER", Operator.getID());
					invStockParm.addData("OPT_TERM", Operator.getIP());
					invStockParm.addData("FLG", "HIGH");
					// 低值
				} else if (parmV.getData("INV_CODE") == null
						|| parmV.getData("INV_CODE").toString().equals("")) {
					// 扣库参数组合
					// INV_STOCKM
					invStockParm.addData("INV_CODE", parmV.getData("INV_CODE"));
					invStockParm.addData("QTY", parmV.getData("QTY"));
					invStockParm.addData("ORG_CODE", Operator.getDept());
					invStockParm.addData("RFID", "");
					invStockParm.addData("OPT_USER", Operator.getID());
					invStockParm.addData("OPT_TERM", Operator.getIP());
					invStockParm.addData("FLG", "LOW");
				}
			}
//			invStockParm.setData("MERGE", invMap);
			TParm Main = new TParm();
			Main.setData("INVRegressGood", invRegressParm.getData());// 退货明细表参数
			Main.setData("INVStock", invStockParm.getData());// 扣库参数
			TParm result = TIOM_AppServer.executeAction(
					"action.inv.INVRegressGoodAction", "onSave", Main);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
			this.messageBox("保存成功！");
			onClear();
		}
	 
	
	 
	 /**
	  * 根据条码（BAR_CODE）查询
	  */
	
	 /**
	  * 按条件锁住部分行
	  */
	 public void getLockRows(){
		 Table.acceptText();
		 String row="";
		 int count = Table.getRowCount();
		 for(int i=0;i<count;i++){
			 if(Table.getParmValue().getValue("BILL_FLG",i).equals("N")){
				 row = row + String.valueOf(i)+",";
			 } 
		 }
		 Table.setLockRows(row.substring(0, row.length()-1));
	 }
	 
	 /**
	  * 计算总价
	  */
	 public void getAr_Amt() {
			Table.acceptText();
			int row = Table.getSelectedRow();
			double qty =  Table.getParmValue().getDouble("QTY", row);
			double price = Table.getParmValue().getDouble("OWN_PRICE", row);
			Table.setItem(row, "AR_AMT", qty*price);
			SaveMain.setData("QTY", row, qty);
       	    SaveMain.setData("AR_AMT", row, qty*price);
			sum_arAmt();
		}
	 
	 /**
	  * 计算总金额
	  */
	 public void sum_arAmt(){
		 double sum=0.0;
		 Table.acceptText();
		 int count = Table.getRowCount();
		 for(int i=0;i<count;i++){
			sum += Table.getParmValue().getDouble("AR_AMT", i);
		 }

		 this.setValue("AR_AMT", String.valueOf(sum));

	 }
	 

	 /**
	  * 查询
	  * */
	 public void onQuery(){

		 if(this.getValue("MR_NO") == null || this.getValue("MR_NO").equals("")){
			 this.messageBox("请输入病案号！");
			 return;
		 }
		 
		 String mr_no =PatTool.getInstance().checkMrno(
					TypeTool.getString(getValue("MR_NO")));
		 String sql = "SELECT * FROM SPC_INV_RECORD WHERE MR_NO='"+mr_no+"'";
		 TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		 TParm tableParm = new TParm();
		 int count = selParm.getCount();
		 if(count<0){
			 this.messageBox("没有要查询的数据");
			 return;
		 }
		 for(int i=0;i<count;i++){
			 tableParm.addData("BAR_CODE", selParm.getValue("BAR_CODE", i));
			 if(selParm.getValue("INV_DESC", i).equals("")){
				 tableParm.addData("DESC", selParm.getValue("ORDER_DESC", i));
			 }else{
				 tableParm.addData("DESC", selParm.getValue("INV_DESC", i));
			 }
			 tableParm.addData("QTY", selParm.getValue("QTY", i));
			 tableParm.addData("UNIT_CODE", selParm.getValue("UNIT_CODE", i));
			 tableParm.addData("BILL_FLG", selParm.getValue("BILL_FLG", i));
			 tableParm.addData("OWN_PRICE", selParm.getValue("OWN_PRICE", i));
			 tableParm.addData("AR_AMT", selParm.getValue("AR_AMT", i));
			 tableParm.addData("HEXP_CODE", selParm.getValue("ORDER_CODE", i));
			 tableParm.addData("HEXP_DESC", selParm.getValue("ORDER_DESC", i));
			 tableParm.addData("OP_ROOM", selParm.getValue("OP_ROOM", i));
			 tableParm.addData("DEPT_CODE", selParm.getValue("DEPT_CODE", i));
		 }
		 SaveDataI=count+1;
		 this.callFunction("UI|Table|setParmValue", tableParm);
		 sum_arAmt();
		 update=true;


	
	 }
	 
	 /**
	  * 删除
	  * */
	 public void onDelete(){ 				 		 		 
		 Table.acceptText();
		 int selRow = Table.getSelectedRow();
		 if(selRow <=0){
			 this.messageBox("没有要删除的数据，请选中要删除的行！");
		 }
		 Table.removeRow(selRow);
		 
	 }
	 
	 /**
	  * 清空
	  * */
	 public void onClear(){
		 Table.removeRowAll();
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
