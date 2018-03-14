package action.inv;

import jdo.inv.InvDispenseDTool;
import jdo.inv.InvDispenseMTool;
import jdo.inv.InvStockDDTool;
import jdo.inv.InvVerifyinDDTool;
import jdo.inv.InvVerifyinDTool;
import jdo.inv.InvVerifyinMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

public class INVCostAction extends TAction {

	/**
	 * 保存
	 * */
	public TParm onCreateCost(TParm parm) {
		TParm result = new TParm();
		//TParm parm = parms.getParm("INVRegressGood");
		if (parm == null)
			return result.newErrParm(-1, "参数为空");
		TConnection conn = getConnection();
	      // 验收单主项
        TParm ver_M = parm.getParm("VER_M");
        result = InvVerifyinMTool.getInstance().onInsert(ver_M, conn);
        if (result.getErrCode() < 0) {
        	conn.close();
            return result;
        }
        

        // 验收单细项
        TParm dD = parm.getParm("DD");
        for (int i = 0; i < dD.getCount("RFID"); i++) {
            result = InvStockDDTool.getInstance().updateCost(dD.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
            	conn.close();
                return result;
            }
        }
        

        // 验收单细项
        TParm ver_D = parm.getParm("VER_D");
        for (int i = 0; i < ver_D.getCount("VERIFYIN_NO"); i++) {
            result = InvVerifyinDTool.getInstance().onInsert(ver_D.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
            	conn.close();
                return result;
            }
        }

        // 判断是否审核入库
       
            // 验收序号管理细项数据
            TParm ver_DD = parm.getParm("VER_DD");
            for (int i = 0; i < ver_DD.getCount("VERIFYIN_NO"); i++) {
                result = InvVerifyinDDTool.getInstance().onInsert(ver_DD.getRow(
                    i), conn);
                if (result.getErrCode() < 0) {
                	conn.close();
                    return result;
                }
            }
            
            
            
            
            System.out.println(" 出库单主项信息");
            // 出库单主项信息(不管标记位)  &&&&
            TParm dispenseM = parm.getParm("DISPENSE_M");
            result = InvDispenseMTool.getInstance().onInsertOutIn(dispenseM, conn);
            if (result.getErrCode() < 0) {
            	conn.close();
                return result; 
            }
            System.out.println(" 出库单细项信息");
            // 出库单细项信息    &&&&
            TParm dispenseD = parm.getParm("DISPENSE_D");
            for (int i = 0; i < dispenseD.getCount("DISPENSE_NO"); i++) {
                result = InvDispenseDTool.getInstance().onInsert(dispenseD.getRow(
                    i), conn);
                if (result.getErrCode() < 0) {
                	conn.close();
                    return result;
                }
            }  
		conn.commit();
		conn.close();
		return result;
	}
	
	
	
}
