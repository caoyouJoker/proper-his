package action.reg;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.StringTool;

import jdo.reg.REGClinicQueTool;
import jdo.reg.REGQueUnfoldTool;
import jdo.reg.SchDayTool;
import jdo.reg.REGQueMethodTool;
import jdo.reg.REGTool;
import jdo.reg.PatAdmTool;
import jdo.util.XmlUtil;

/**
 *
 * <p>Title:挂号动作类 </p>
 *
 * <p>Description:挂号动作类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.20
 * @version 1.0
 */
public class REGAction
    extends TAction {
    /**
     * 周班转日班
     * @param parm TParm
     * @return TParm
     */
    public TParm schWeekForDay(TParm parm) {
        TConnection connection = getConnection();
        TParm result = SchDayTool.getInstance().schWeekForDay(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 周班转日班
     * @param parm TParm
     * @return TParm
     */
    public TParm schWeekForDayNew(TParm parm) {
        TConnection connection = getConnection();
        TParm result = SchDayTool.getInstance().schWeekForDayNew(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 新增挂号
     * @param parm TParm
     * @return TParm
     */
    public TParm onNewReg(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = REGTool.getInstance().onSaveREGPatAdm(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
    /**
     * 解决重号问题
     * @param regParm
     * @return
     * ==============pangben 2012-6-18
     */
    public TParm onSaveQueNo(TParm regParm){
    	TConnection connection = getConnection();
    	TParm result = SchDayTool.getInstance().updatequeno(regParm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		//TParm regParm1 = regParm;
		//TCM_Transform.getTimestamp(regParm1.getData("ADM_DATE"));
		//regParm1.setData("ADM_DATE",regParm1.getValue("ADM_DATE"));
		// VIP诊
		result = REGClinicQueTool.getInstance().updatequeno(regParm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
    }
    /**
     * 报道保存
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveRegister(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = REGTool.getInstance().onSaveRegister(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }

    /**
     * 退挂
     * @param parm TParm
     * @return TParm
     */
    public TParm onUnReg(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = REGTool.getInstance().onUnREGPatAdm(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 挂号日结操作
     * @param parm TParm
     * @return TParm
     */
    public TParm onREGAccount(TParm parm) {
        TConnection connection = this.getConnection();
        TParm result = new TParm();
        result = REGTool.getInstance().executeREGAccount(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 设定VIP班表就诊号
     * @param parm TParm
     * @return TParm
     */
    public TParm setQueNoForQueMethod(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        int sNo = parm.getInt("S_NO");
        int eNo = parm.getInt("E_NO");
        int iNo = parm.getInt("I_NO") == 0 ? 1 : parm.getInt("I_NO");
        for (int i = sNo; i <= eNo; i = i + iNo) {
            TParm parmNew = new TParm();
            parmNew.setRowData(parm);
            parmNew.setData("QUE_NO", i);
            result = REGQueMethodTool.getInstance().updatQueNo(parmNew,
                connection);
        }
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 设定到院时间
     * @param parm TParm
     * @return TParm
     */
    public TParm setArriveTime(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        int count = parm.getInt("MAX_NO");
        String t = "";
        for (int i = 1; i <= count; i++) {
            TParm parmNew = new TParm();
            parmNew.setRowData(parm);
            parmNew.setData("QUE_NO", i);
            if (t.length() == 0)
                t = parmNew.getValue("START_TIME");
            String interveenTime = parmNew.getValue("I_TIME");
            parmNew.setData("START_TIME", t);
            result = REGQueMethodTool.getInstance().setArriveTime(parmNew,
                connection);
            t = REGQueMethodTool.getInstance().addTime(t, interveenTime);
        }
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 医疗卡退挂
     * @param parm TParm
     * @return TParm
     */
    public TParm updateUnAdmReg(TParm parm){
        TConnection connection = getConnection();
        TParm result = PatAdmTool.getInstance().updateUnAdmReg(parm,connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 新增挂号(医疗卡计费使用)
     * @param parm TParm
     * @return TParm
     */
    public TParm onNewRegForEKT(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = REGTool.getInstance().onSaveREGPatAdmForEKT(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
    /**
     * 退挂(医疗卡使用)
     * @param parm TParm
     * @return TParm
     */
    public TParm onUnRegForEKT(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = REGTool.getInstance().onUnREGPatAdmForEKT(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 退挂(医疗卡使用):记账未完成的数据操作
     * @param parm TParm
     * @return TParm
     * ==============pangben 20110821
     */
    public TParm onUnRegForStatusEKT(TParm parm) {

        TConnection connection = getConnection();
        TParm result = new TParm();
        result = REGTool.getInstance().onUnREGStatusForEKT(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
    /**
     * 挂号条补印
     * @param parm TParm
     * @return TParm
     */
    public TParm onREGReprint(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = REGTool.getInstance().onREGReprint(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
    /**
     * 直接添加、修改操作BIL_REG_RECP
     * @param parm TParm 记账退挂操作:BIL_STATUS=1 判断是否产生费用，如果没有产出费用直接添加、修改操作BIL_REG_RECP
     * @return TParm
     */
    public TParm onUnRegStatus(TParm parm ){
         TConnection connection = getConnection();
         TParm  result =REGTool.getInstance().onUnRegStatus(parm,connection);
         if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 代诊
     * ===zhangp 20120621
     * @param parm
     * @return
     */
    public TParm updateRegSchDay(TParm parm){
    	 TConnection connection = getConnection();
         TParm  result =SchDayTool.getInstance().updateRegSchDay(parm,connection);
         if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * 预约发短信
     * ==huangtt 20131101
     */
    public static void orderMessage(TParm parmAll){
//    	TParm parm = parmAll.getParm("parm");
//    	TParm telParm = parmAll.getParm("telParm");
    	TParm parm = new TParm();
    	TParm telParm = new TParm();
    	for(int i=0;i<parmAll.getCount("MrNo");i++){
    		parm = new TParm();
        	telParm = new TParm();
        	parm.setData("Title", "预约挂号通知");
        	parm.setData("SysNo", "REG");
    		parm.setData("MrNo", parmAll.getValue("MrNo", i));
    		parm.setData("Name", parmAll.getValue("Name", i));
    		parm.setData("Content", parmAll.getValue("Content", i));
    		telParm.setData("TEL1", 0 ,parmAll.getValue("TEL1", i));
    		telParm.setCount(telParm.getCount("TEL1"));
    		XmlUtil.createSmsFile(parm, telParm);
    	}    	
    }
    /**
     * 退挂(垫付延迟使用)
     * @param parm TParm
     * @return TParm
     */
    public TParm onUnRegForAdvance(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = REGTool.getInstance().onUnREGForAdvance(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    
    /**
     * 保存检伤评估信息
     * @param parm
     * @return
     */
    public TParm onSaveErdEvalution(TParm parm){
    	 TConnection connection = getConnection();
         TParm result = new TParm();
         result = PatAdmTool.getInstance().insertErdEvalution(parm, connection);
         if (result.getErrCode() < 0) {
             err(result.getErrName() + " " + result.getErrText());
             connection.close();
             return result;
         }
         if(parm.getValue("CASE_NO").length() > 0){
        	 result = PatAdmTool.getInstance().updateRegPatadmErd(parm, connection);
        	 if (result.getErrCode() < 0) {
                 err(result.getErrName() + " " + result.getErrText());
                 connection.close();
                 return result;
             }
         }
         if(parm.getValue("BEFORE_LEVEL_CODE").length() > 0){
        	 TParm parmLog = parm.getParm("erdLevelLog");
        	 result = PatAdmTool.getInstance().insertErdLevelLog(parmLog, connection);
        	 if (result.getErrCode() < 0) {
                 err(result.getErrName() + " " + result.getErrText());
                 connection.close();
                 return result;
             }
         }
         connection.commit();
         connection.close();
         return result;
    }
    
    /**
     * 修改检伤评估等级
     * @param parm
     * @return
     */
    public TParm onSaveErdLevelLog(TParm parm){
    	 TConnection connection = getConnection();
         TParm result = new TParm();
         
         if(parm.getValue("CASE_NO").length() > 0){
        	 result = PatAdmTool.getInstance().updateRegErdLevel(parm, connection);
             if (result.getErrCode() < 0) {
                 err(result.getErrName() + " " + result.getErrText());
                 connection.close();
                 return result;
             }
         }
         
         result = PatAdmTool.getInstance().updateErdLevelCode(parm, connection);
         if (result.getErrCode() < 0) {
             err(result.getErrName() + " " + result.getErrText());
             connection.close();
             return result;
         }
         
         
         
         result = PatAdmTool.getInstance().insertErdLevelLog(parm, connection);
         if (result.getErrCode() < 0) {
             err(result.getErrName() + " " + result.getErrText());
             connection.close();
             return result;
         }
         connection.commit();
         connection.close();
         return result;
    }
    
    public TParm updateErdEvalution(TParm parm){
    	TConnection connection = getConnection();
        TParm result = new TParm();
        result = PatAdmTool.getInstance().updateErdLevelCode(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        
        // add by wangqing 20170626 start
        if(parm.getValue("CASE_NO").length() > 0){
        	result = PatAdmTool.getInstance().updateRegPatadmErd(parm, connection);
        	if (result.getErrCode() < 0) {
        		err(result.getErrName() + " " + result.getErrText());
        		connection.close();
        		return result;
        	}
        }
        // add by wangqing 20170626 end
        String sql = "UPDATE ERD_EVALUTION SET TRIAGE_ZONE='"+parm.getValue("TRIAGE_ZONE")+"' "
        		+ "WHERE TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"'";
        result = new TParm(TJDODBTool.getInstance().update(sql, connection));
        if (result.getErrCode() < 0) {
    		err(result.getErrName() + " " + result.getErrText());
    		connection.close();
    		return result;
    	}       
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * 医师加号
     * ==huangtt 20131206
     * @param parm
     * @return
     */
    public TParm addRegVipQue(TParm parm){
    	TParm result = new TParm();
    	TConnection conn = getConnection();
    	for(int i=0;i<parm.getCount("ADM_TYPE");i++){
    		result = REGQueUnfoldTool.getInstance().addRegQue(parm.getRow(i), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
    	}
    	conn.commit();
		conn.close();
		return result;
    }
    
    /**
     * Q医挂号条打印 
     * @param parm TParm
     * @return TParm
     */
    public TParm onQeREGReprint(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = REGTool.getInstance().onQeREGReprint(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
    
  
}
