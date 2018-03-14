package action.bms;

import jdo.bms.BMSBloodTool;
import jdo.bms.BMSFeeTool;
import jdo.bms.BMSTool;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
/**
 * <p>
 * Title: 血液信息
 * </p>
 *
 * <p>
 * Description: 血液信息
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class BMSBloodAction
    extends TAction {
    public BMSBloodAction() {
    }

    public TParm onQuery(TParm parm) {
        TParm result = new TParm();
        result = BMSBloodTool.getInstance().onQuery(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 新增
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onBMSBloodInInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSBloodTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 删除
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onBMSBloodInDelete(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新备血单信息,更新病患血型
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdatePatCheckInfo(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onUpdatePatCheckInfo(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新备血单交叉配血信息
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateBloodCross(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onUpdateBloodCross(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 更新备血单交叉配血审核信息
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateBloodCrossRecheck(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSBloodTool.getInstance().onUpdateBloodCrossRecheck(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新备血单血品出库信息
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateBloodOut(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onUpdateBloodOut(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback() ;
            conn.close();
            return result;
        }
        result =BMSFeeTool.getInstance().getIbsData(parm, conn) ;  
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback() ;
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新血品并且新增血品规格
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveBldSubcat(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onSaveBldSubcat(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 
     * 方法描述 更新血品规格.
     * @param parm
     * @return
     */
    public TParm onUpdateBldSubcat(TParm parm){
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onUpdateBldSubcat(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;    	
    }
   

    /**
     * 删除血品
     * @param parm TParm
     * @return TParm
     */
    public TParm onDeleteBldSubcat(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onDeleteBldSubcat(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    
    /*
     * 记录输血信息 开始
     * */
    public TParm onTranBlood(TParm parm){
    	TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onTranBlood(parm, conn);
//        System.out.println("yangjj:"+result+"");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        result = BMSTool.getInstance().onInsertOrderForBL(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /*
     * 记录输血信息 结束
     * */
    public TParm onTranBlood2(TParm parm){
    	TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onTranBlood2(parm, conn);
//        System.out.println("yangjj:"+result+"");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        result = BMSTool.getInstance().onUpdateOrderForBL(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 交叉配血界面，删除交叉配血信息时，同时更新配血审核信息
     * @author wangqing 20180108
     * @param parm
     * @return
     */
    public TParm onUpdateBmsBlood(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onUpdateBloodCross(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        String sql = "";
        for (int i = 0; i < parm.getCount("BLOOD_NO"); i++) {
        	sql = "UPDATE BMS_BLOOD "
        			+ "SET RECHECK_USER ='', RECHECK_TIME='' "
        			+ "WHERE BLOOD_NO='"+parm.getValue("BLOOD_NO", i)+"' ";
        	result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        	if (result.getErrCode() < 0) {
        		err("ERR:" + result.getErrCode() + result.getErrText()
        		+ result.getErrName());
        		conn.close();
        		return result;
        	}
        }      
        conn.commit();
        conn.close();
        return result;
    }


}
