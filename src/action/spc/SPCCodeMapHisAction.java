package action.spc;

import jdo.spc.SPCCodeMapHisTool;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;


/**
 * <p>
 * Title: 物联网医院商药品编码比对
 * </p>
 *
 * <p>
 * Description: 物联网医院商药品编码比对
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
 * @author liuzhen 2013.1.17
 * @version 1.0
 */
public class SPCCodeMapHisAction extends TAction {
	
	
	/**
	 * 国药药品编码转换
	 * @param parm
	 * @return
	 */
	public TParm importMap(TParm parm) {
		TConnection connection = getConnection();
		
		int count = parm.getCount();
		
		boolean queryflg = true;		
		boolean flg = true;
		
		String tempCode ="";
		int rowNo = 0;
		
		for(int i = 0; i < count; i++) {
			TParm inParm = parm.getRow(i);
			tempCode = inParm.getValue("ORDER_CODE");
			
			String regionCode = inParm.getValue("REGION_CODE");
			String hisOrderCode = inParm.getValue("HIS_ORDER_CODE");
			
			if(tempCode == null || "".equals(tempCode.trim())){
				rowNo = i+1;
				break;
			}
			
			if(regionCode == null || "".equals(regionCode.trim())){
				rowNo = i+1;
				break;
			}
			
			if(hisOrderCode == null || "".equals(hisOrderCode.trim())){
				rowNo = i+1;
				break;
			}
			
			queryflg = SPCCodeMapHisTool.getInstance().queryBase(inParm);
			if(!queryflg){
				break;
			}
			
			flg = SPCCodeMapHisTool.getInstance().importSave(inParm);
			if(!flg){
				break;
			}
		}		
		connection.commit();
		connection.close();
		
		TParm result = new TParm();
			
		if(rowNo != 0){
			result.setData("FLG", "第 " + rowNo + " 行数据不全，请检查！");
		}else if(!queryflg){
			result.setData("FLG", "系统中没有该药品，药品编码："+tempCode);
		}else if(!flg){
			result.setData("FLG", "N");
		}else{
			result.setData("FLG", "Y");
		}
		
		return result;
	}
	
}
