package action.spc;

import jdo.spc.SPCSysFeeTool;
import jdo.sys.SystemTool;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 药品编码转换
 * </p>
 *
 * <p>
 * Description: 药品编码转换
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: ProperSoft
 * </p>
 *
 * @author fuwj 2012.12.19
 * @version 1.0
 */
public class SPCSysFeeAction extends TAction {
	
	
	/**
	 * 国药药品编码转换
	 * @param parm
	 * @return
	 */
	public TParm updateSysFeeAll(TParm parm) {
		TConnection connection = getConnection();
		TParm result =null;
		int count = parm.getCount();
		for(int i=0;i<count;i++) { 
			String regionCode = parm.getValue("REGION_CODE",i);
			String orderCode = parm.getValue("ORDER_CODE",i);
			String hisOrderCode = parm.getValue("HIS_ORDER_CODE",i);
			String hisOrderDesc = parm.getValue("HIS_ORDER_DESC",i);
			String hisSpecification = parm.getValue("HIS_SPECIFICATION",i);
			String hisGoodDesc = parm.getValue("HIS_GOODS_DESC",i);
			TParm inserTParm = new TParm();
			inserTParm.setData("REGION_CODE","H01");
			inserTParm.setData("ORDER_CODE",orderCode);
			inserTParm.setData("HIS_ORDER_CODE",hisOrderCode);
			inserTParm.setData("HIS_ORDER_DESC",hisOrderDesc);
			inserTParm.setData("HIS_SPECIFICATION",hisSpecification);			
			inserTParm.setData("HIS_GOODS_DESC",hisGoodDesc);
			inserTParm.setData("OPT_USER",parm.getValue("OPT_USER",i));
			inserTParm.setData("OPT_DATE",SystemTool.getInstance().getDate());
			inserTParm.setData("OPT_TERM",parm.getValue("OPT_TERM",i));
			inserTParm.setData("IS_UPDATE",parm.getValue("IS_UPDATE",i));
			result = SPCSysFeeTool.getInstance().updateSysFee(inserTParm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}   
			result = SPCSysFeeTool.getInstance().updatePhaBase(inserTParm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}    
			result = SPCSysFeeTool.getInstance().updatePhaTransunit(inserTParm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}                                        
			result = SPCSysFeeTool.getInstance().insertSPCSysFee(inserTParm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}     
			result = SPCSysFeeTool.getInstance().deleteSysFee(inserTParm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}   
			result = SPCSysFeeTool.getInstance().deletePhaBase(inserTParm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}   
			result = SPCSysFeeTool.getInstance().deletePhaTransunit(inserTParm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}  
			
			result = SPCSysFeeTool.getInstance().updateIndStockM(inserTParm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			} 
			result = SPCSysFeeTool.getInstance().updateIndStock(inserTParm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			} 
			  
			
		}
		connection.commit();
		connection.close();
		return result;
	}
	
}
