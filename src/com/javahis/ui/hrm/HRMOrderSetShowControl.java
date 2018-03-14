package com.javahis.ui.hrm;

import java.util.HashMap;
import java.util.Map;
import jdo.hrm.HRMPackageD;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TButton;

/**
 * <p>Title: 健康检查套餐设定修改医嘱细相</p>
 *
 * <p>Description: 健康检查套餐设定修改医嘱细相</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMOrderSetShowControl extends TControl {

    private TTable table;
    private HRMPackageD packD;// 套餐细项

    public void onInit() {
        super.onInit();
        initParam();
    }
    
	/**
	 * 初始化参数
	 */
	public void initParam() {
		TParm parm=(TParm)this.getParameter();
		// System.out.println("parm="+parm);
		table=(TTable)this.getComponent("TABLE");
		table.addEventListener("TABLE->"+ TTableEvent.CHANGE_VALUE,this, "onValueChange");

		String packCode=parm.getValue("PACKAGE_CODE");
		String orderSetCode=parm.getValue("ORDERSET_CODE");
		int groupNo=parm.getInt("ORDERSET_GROUP_NO");
		String sql="SELECT * FROM HRM_PACKAGED " +
				"	WHERE PACKAGE_CODE='" +packCode+
				"' AND ORDERSET_CODE='" +orderSetCode+
				"' AND ORDERSET_GROUP_NO="+groupNo+" AND SETMAIN_FLG='N'";
		packD=new HRMPackageD();
		packD.setSQL(sql);
		packD.retrieve();
                int rowCountD = packD.rowCount();
                // System.out.println("行数=="+rowCountD);
                if(rowCountD>0){
                    table.setDataStore(packD);
                    table.setDSValue();
                }else{
                    Object obj = parm.getData("DATASTORE");
                    if (obj != null) {
                        TParm result = new TParm();
                        TParm parmOrder = (TParm)obj;
                        int count = parmOrder.getCount();
                        if (count < 0) {
                            return;
                        }
                        String tempCode;
                        int tempNo;
                        for (int i = 0; i < count; i++) {
                            tempCode = parmOrder.getValue("ORDERSET_CODE", i);
                            tempNo = parmOrder.getInt("ORDERSET_GROUP_NO", i);
                            if (tempCode.equalsIgnoreCase(orderSetCode) &&
                                tempNo == groupNo &&
                                !parmOrder.getBoolean("SETMAIN_FLG", i)) {
                                // System.out.println("行数据=="+parmOrder.getRow(i));
                                result.addData("ORDER_DESC",parmOrder.getValue("ORDER_DESC",i));
                                result.addData("DISPENSE_QTY",parmOrder.getValue("DISPENSE_QTY", i));
                                result.addData("DISPENSE_UNIT",parmOrder.getValue("DISPENSE_UNIT", i));
                                result.addData("ORIGINAL_PRICE",parmOrder.getValue("OWN_PRICE", i));
                                result.addData("PACKAGE_PRICE",parmOrder.getDouble("OWN_AMT",i));
                                result.addData("AR_AMT",parmOrder.getDouble("OWN_AMT",i));
                            }
                        }
                        result.setCount(result.getCount("ORDER_DESC"));
                        table.setParmValue(result);
                    }
                }
                if("Y".equals(parm.getValue("FLG"))){
                    ((TButton) this.getComponent("SAVE")).setEnabled(false);
                    ((TButton) this.getComponent("DEL")).setEnabled(false);
                    int rowCount = table.getRowCount();
                    for(int i=0;i<rowCount;i++){
                        table.setLockCellRow(i,true);
                    }
                }
	}

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

	/**
	 * TABLE值改变事件
	 * @param tNode
	 * @return
	 */
    public boolean onValueChange(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = tNode.getTable().getParmMap(column);
        // 修改数量，并且修改总价
        if ("DISPENSE_QTY".equalsIgnoreCase(colName)) {
            double qty = TypeTool.getDouble(tNode.getValue());
            double oldQty = TypeTool.getDouble(tNode.getOldValue());
            if (qty == oldQty) {
                return true;
            }
            if (qty <= 0) {
                return true;
            }
            table.setDSValue();
            return false;
        }
        // 修改套餐价，并且修改总价
        if ("PACKAGE_PRICE".equalsIgnoreCase(colName)) {
            double price = TypeTool.getDouble(tNode.getValue());
            double oldPrice = TypeTool.getDouble(tNode.getOldValue());
            if (price == oldPrice) {
                return true;
            }
            if (price <= 0.0) {
                return true;
            }
            table.setDSValue();
            return false;
        }
        return false;
    }
    
	/**
	 * 删除事件
	 */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox_("没有数据可删除");
            return;
        }
        packD.deleteRow(row);
        onSave();
    }
    
	/**
	 * 保存
	 */
    public void onSave() {
        table.acceptText();
        if (!packD.isModified()) {
            return;
        }
        String[] sql = packD.getUpdateSQL();
        if (sql == null) {
            return;
        }
        if (sql.length < 1) {
            return;
        }
        TParm inParm = new TParm();
        Map inMap = new HashMap();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);// 更新套餐细项
        if (result.getErrCode() != 0) {
            this.messageBox("E0001");
            this.closeWindow();
        }
        String updatePackageTotAmtSql =
                "UPDATE HRM_PACKAGEM B SET B.TC_TOT_AMT = ROUND((SELECT SUM(A.DISPENSE_QTY*A.ORIGINAL_PRICE) "
                        + " FROM HRM_PACKAGED A WHERE A.PACKAGE_CODE = B.PACKAGE_CODE GROUP BY A.PACKAGE_CODE),2) ";// 原价总额
        String updatePackageArAmtSql =
                "UPDATE HRM_PACKAGEM A SET A.TC_AR_AMT = ROUND((SELECT SUM(B.DISPENSE_QTY*B.PACKAGE_PRICE) "
                        + " FROM HRM_PACKAGED B WHERE A.PACKAGE_CODE = B.PACKAGE_CODE),2) ";// 套餐价总额
        inParm = new TParm();
        inMap = new HashMap();
        inMap.put("SQL", new String[]{updatePackageTotAmtSql, updatePackageArAmtSql });
        inParm.setData("IN_MAP", inMap);
        result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);// 更新套餐金额
        if (result.getErrCode() != 0) {
            this.messageBox("更新套餐总价失败");
            this.closeWindow();
        }
        this.setReturnValue(result);
        this.closeWindow();
        this.messageBox("P0001");
        packD.resetModify();
        table.setDSValue();
    }
    
}
