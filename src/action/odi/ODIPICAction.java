package action.odi;

import org.apache.commons.lang.StringUtils;

import jdo.odi.ODIPICTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: һ���ٴ�Action
 * </p>
 * 
 * <p>
 * Description: һ���ٴ�Action
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
     * һ���ٴ������޸�ҽ������ʱ��
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
			
			// ����ҽ���������ʱ��
			result = ODIPICTool.getInstance().updateOrderEffDate(updateParm, conn);
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
			// ����MedApply��ҽ��ʱ��
			result = ODIPICTool.getInstance().updateMedApplyDate(updateParm, conn);
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
			// �ж��Ƿ��Ѿ�չ����ҽ��
			if (StringUtils.isNotEmpty(updateParm.getValue("LAST_DSPN_DATE"))) {
				// ���»�ʿִ�������Ӧִ��ʱ��
				result = ODIPICTool.getInstance().updateOdiDspnMDate(updateParm, conn);
				
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
				
				// ���»�ʿִ��ϸ���Ӧִ��ʱ��
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
