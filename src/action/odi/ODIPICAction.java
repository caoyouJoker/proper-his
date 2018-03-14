package action.odi;

import org.apache.commons.lang.StringUtils;

import jdo.odi.ODIPICTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 一期临床Action
 * </p>
 * 
 * <p>
 * Description: 一期临床Action
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangbin 2016.6.15
 * @version 1.0
 */
public class ODIPICAction extends TAction {
	
	/**
     * 一期临床批量修改医嘱启用时间
     *
     * @param parm
     *            TParm
     * @return TParm
     */
	public TParm onSaveByBatchModOrderDate(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		int count = parm.getCount();
		TParm updateParm = new TParm();
		int seqCount = 0;
		
		for (int i = 0; i < count; i++) {
			updateParm = parm.getRow(i);
			
			updateParm.setData("EFF_DATE", updateParm.getValue("EFF_DATE").substring(0, 19).replace("-", "/"));
			updateParm.setData("APPLICATION_NO", updateParm.getValue("MED_APPLY_NO"));
			if (i < count - 1) {
				updateParm.setData("ORDER_SEQ_L", updateParm.getInt("ORDER_SEQ"));
				updateParm.setData("ORDER_SEQ_H", parm.getInt("ORDER_SEQ", i + 1));
				if (seqCount == 0) {
					seqCount = parm.getInt("ORDER_SEQ", i + 1) - parm.getInt("ORDER_SEQ", i);
				}
			} else {
				updateParm.setData("ORDER_SEQ_L", updateParm.getInt("ORDER_SEQ"));
				updateParm.setData("ORDER_SEQ_H", updateParm.getInt("ORDER_SEQ") + seqCount);
			}
			
			// 更新医嘱表的启用时间
			result = ODIPICTool.getInstance().updateOrderEffDate(updateParm, conn);
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
			// 更新MedApply表医嘱时间
			result = ODIPICTool.getInstance().updateMedApplyDate(updateParm, conn);
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
			// 判断是否已经展开过医嘱
			if (StringUtils.isNotEmpty(updateParm.getValue("LAST_DSPN_DATE"))) {
				// 更新护士执行主表的应执行时间
				result = ODIPICTool.getInstance().updateOdiDspnMDate(updateParm, conn);
				
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
				
				// 更新护士执行细表的应执行时间
				result = ODIPICTool.getInstance().updateOdiDspnDDate(updateParm, conn);
				
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
			}
		}
		
		conn.commit();
		conn.close();
		return result;
	}

}
