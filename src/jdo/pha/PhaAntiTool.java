package jdo.pha;

import jdo.udd.UddChnCheckTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>
* Title: 抗菌药品控制表
* </p>
*
* <p>
* Description:住院抗菌药品控制
* </p>
*
* <p>
* Copyright: Copyright (c) pangben 2013
* </p>
*
* <p>
* Company: ProperSoft
* </p>
*
* @author pangben 2013-9-4
* @version 4.0
*/
public class PhaAntiTool extends TJDOTool {
	 /**
     * 实例
     */
    public static PhaAntiTool instanceObject;
    /**
     * 得到实例
     * @return UddChnCheckTool
     */
    public static PhaAntiTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new PhaAntiTool();
        }
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PhaAntiTool() {
        setModuleName("pha\\phaAntiModule.x");
        onInit();
    }
    /**
     * 添加数据
     * @param parm
     * @param connection
     * @return
     */
    public TParm insertPhaAnti(TParm parm, TConnection connection){
    	TParm result = update("insertPhaAnti", parm, connection);
    	return result;
    }
    /**
     * 查询数据
     * @param parm
     * @return
     */
    public TParm queryPhaAnti(TParm parm){
    	TParm result = query("queryPhaAnti", parm);
    	return result;
    }
    /**
     * 条件查询数据 ,住院医生站、电子病例使用
     * @param parm
     * @return
     */
    public TParm queryPhaAntiStatus(TParm parm){
    	TParm result = query("queryPhaAntiStatus", parm);
    	return result;
    }
    /**
     * 修改医生使用注记
     * @param parm
     * @param connection
     * @return
     */
    public TParm updatePhaAntiUseFlg(TParm parm, TConnection connection){
    	TParm result = update("updatePhaAntiUseFlg", parm, connection);
    	return result;
    }
    /**
     * 修改会诊注记
     * @param parm
     * @param connection
     * @return
     * ===========pangben 2014-2-25
     */
    public TParm updatePhaAntiApproveFlg(TParm parm, TConnection connection){
    	TParm result = update("updatePhaAntiApproveFlg", parm, connection);
    	return result;
    }
    /**
     * 修改医生使用注记和审批标记
     * @param parm
     * @param connection
     * @return
     */
    public TParm updatePhaAnti(TParm parm, TConnection connection){
    	TParm result = update("updatePhaAntiCheckUseFlg", parm, connection);
    	return result;
    }
}
