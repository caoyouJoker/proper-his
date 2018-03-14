package action.spc;

import jdo.spc.SPCCodeMapTool;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;


/**
 * <p>
 * Title: 物联网供应商药品编码比对
 * </p>
 *
 * <p>
 * Description: 物联网供应商药品编码比对
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: BLUECORE
 * </p>
 *
 * @author liuzhen 2013.1.17
 * @version 1.0
 */
public class SPCCodeMapAction extends TAction {
	
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
			String supCode = inParm.getValue("SUP_CODE");
			String supOrderCode = inParm.getValue("SUP_ORDER_CODE");
			
			if(tempCode == null || "".equals(tempCode.trim())){
				rowNo = i+1;
				break;
			}
			
			if(supCode == null || "".equals(supCode.trim())){
				rowNo = i+1;
				break;
			}
			
			if(supOrderCode == null || "".equals(supOrderCode.trim())){
				rowNo = i+1;
				break;
			}
			
			queryflg = SPCCodeMapTool.getInstance().queryBase(inParm);
			if(!queryflg){
				break;
			}
			
			flg = SPCCodeMapTool.getInstance().importSave(inParm);
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
