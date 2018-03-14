/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

import java.util.Map;

/**
 *
 * <p>
 * Title: 用户管理
 * </p>
 *
 * <p>
 * Description:用户管理
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 200800901
 * @version 1.0
 */
public class SYSOperatorTool
    extends TJDOTool {
    public static final String GET_LCS_SQL =
        "SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL";
    /**
     * 实例
     */
    public static SYSOperatorTool instanceObject;

    /**
     * 得到实例
     *
     * @return SYSOperatorTool
     */
    public static SYSOperatorTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSOperatorTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SYSOperatorTool() {
        setModuleName("sys\\SYSOperatorModule.x");
        onInit();
    }

    /**
     * 初始化界面，查询所有的数据
     *
     * @return TParm
     */
    public TParm selectall() {
        TParm parm = new TParm();
        // parm.setData("CODE",CODE);
        TParm result = query("selectall", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据 USER_ID 代码查询数据
     *
     * @param USER_ID
     *            String 手术ICD代码
     * @return TParm
     */
    public TParm selectdata(String USER_ID) {
        TParm parm = new TParm();
        parm.setData("USER_ID", USER_ID);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据变动参数查询数据，检核user_name是否含有‘%’，如无则加上
     *
     * @param parm
     *            String user_id,String REGION_CODE,String DEPT_CODE,String
     *            USER_NAME(右匹配模糊查询),String POS_CODE,String ROLE_ID
     * @return TParm result
     */
    public TParm selecedataByParameters(TParm parm) {
        // 检核user_name是否含有‘%’，如无则加上
        String userName = parm.getValue("USER_NAME");
        if (userName != null && !userName.endsWith("%")) {
            userName = userName + "%";
        }
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        //System.out.println("after operator tool==================");
        //System.out.println("data====================" + result);
        return result;
    }

    /**
     * 判断是否存在数据
     *
     * @param USER_ID
     *            String
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsOperator(String USER_ID) {
        TParm parm = new TParm();
        parm.setData("USER_ID", USER_ID);
        //System.out.println("existsOperator" + USER_ID);
        return getResultInt(query("USER_ID", parm), "COUNT") > 0;
    }

    /**
     * 新增指定 USER_ID 得到数据
     *
     * @param USER_ID
     *            String
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        String USER_ID = parm.getValue("USER_ID");
        //System.out.println("USER_ID" + USER_ID);
        TParm result = new TParm();
        if (!existsOperator(USER_ID)) {
            result = update("insertdata", parm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        else {
            result.setErr( -1, "资料 " + USER_ID + " 已经存在!");
            return result;
        }

        return result;
    }

    /**
     * 更新指定USER_ID数据
     *
     * @param USER_ID
     *            String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String USER_ID = parm.getValue("USER_ID");
        //System.out.println("true or false" + existsOperator(USER_ID));
        if (existsOperator(USER_ID)) {
            result = update("updatedata", parm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        else {
            result.setErr( -1, "资料 " + USER_ID + " 不存在！");
            return result;
        }

        return result;
    }

    /**
     * 删除指定编号职别数据
     *
     * @param USER_ID
     *            String
     * @return boolean
     */
    public TParm deletedata(String USER_ID) {
        TParm parm = new TParm();
        parm.setData("USER_ID", USER_ID);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增指定 USER_ID 得到数据
     *
     * @param USER_ID
     *            String
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
        String USER_ID = parm.getValue("USER_ID");
        //System.out.println("USER_ID" + USER_ID);
        TParm result = new TParm();
        if (!existsOperator(USER_ID)) {
            result = update("insertdata", parm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        else {
            result.setErr( -1, "资料 " + USER_ID + " 已经存在!");
            return result;
        }

        return result;
    }

    /**
     * 更新指定USER_ID数据
     *
     * @param USER_ID
     *            String
     * @return TParm
     */
    public TParm updatedata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String USER_ID = parm.getValue("USER_ID");
        //System.out.println("true or false" + existsOperator(USER_ID));
        if (existsOperator(USER_ID)) {
            result = update("updatedata", parm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        else {
            result.setErr( -1, "资料 " + USER_ID + " 不存在！");
            return result;
        }

        return result;
    }

    /**
     * 删除指定编号职别数据
     *
     * @param USER_ID
     *            String
     * @return boolean
     */
    public TParm deletedata(String USER_ID, TConnection connection) {
        TParm parm = new TParm();
        parm.setData("USER_ID", USER_ID);
        TParm result = update("deletedata", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * odo异动主入口
     *
     * @param parm
     * @param connection
     * @return result 保存结果
     */
    public TParm onSave(TParm parm, TConnection connection) {
        TParm result = deletedata(parm.getParm(JDOOperatorList.DELETED)
                                  .getValue("USER_ID"), connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = insertdata(parm.getParm(JDOOperatorList.NEW), connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = updatedata(parm.getParm(JDOOperatorList.MODIFIED), connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据用户ID得到证照类别
     *
     * @param userId
     *            String 用户ID
     * @return result TParm 证照类别
     */
    public TParm getLcsCode(String userId) {
        TParm result;
        StringBuffer sb = new StringBuffer(GET_LCS_SQL);
        sb.append(" WHERE USER_ID='"
                  + userId
                  + "' AND TO_DATE('"
                  + StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                         "yyyyMMdd")
                  + "','YYYYMMDD') BETWEEN EFF_LCS_DATE AND END_LCS_DATE");
        result = new TParm(TJDODBTool.getInstance().select(sb.toString()));
        return result;
    }

    /**
     * 更新异常登陆次数
     *
     * @param userId
     * @return
     */
    public boolean updateAbnormaiTimes(String userId) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("USER_ID", userId);
        result = update("updateAbnormaiTimes", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return false;
        }
        return true;
    }

    /**
     * 重置异常登陆次数
     *
     * @param userId
     * @return
     */
    public boolean resetAbnormaiTimes(String userId) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("USER_ID", userId);
        result = update("resetAbnormaiTimes", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return false;
        }
        return true;
    }

    /**
     * 根据 USER_ID 代码查询数据
     *
     * @param USER_ID
     *
     * @return TParm
     */
    public TParm getOperator(String USER_ID,String regionCode) {
        String sql = SYSSQL.getSYSOperator(regionCode) + " WHERE USER_ID='" + USER_ID
            + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 保存用户信息
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onSaveOperator(TParm parm, TConnection conn) {
        TParm result = new TParm();
        // 数据检核
        if (parm == null)
            return null;
        // 使用者数据
        if (parm.existData("OPERATOR")) {
            Object update = parm.getData("OPERATOR");
            if (update == null) {
                return null;
            }
            if (update instanceof String[]) {
                String[] updateSql = (String[]) update;
                for (int i = 0; i < updateSql.length; i++) {
                    result = new TParm(TJDODBTool.getInstance().update(
                        updateSql[i], conn));
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
            else {
                return null;
            }
        }
        // 科室信息
        if (parm.existData("DEPT")) {
            Object update = parm.getData("DEPT");
            if (update == null) {
                return null;
            }
            if (update instanceof String[]) {
                String[] updateSql = (String[]) update;
                for (int i = 0; i < updateSql.length; i++) {
                    result = new TParm(TJDODBTool.getInstance().update(
                        updateSql[i], conn));
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
            else {
                return null;
            }
        }
        // 证照信息
        if (parm.existData("LISCENSE")) {
            Object update = parm.getData("LISCENSE");
            if (update == null) {
                return null;
            }
            if (update instanceof String[]) {
                String[] updateSql = (String[]) update;
                for (int i = 0; i < updateSql.length; i++) {
                    result = new TParm(TJDODBTool.getInstance().update(
                        updateSql[i], conn));
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
            else {
                return null;
            }
        }
        // 诊区信息
        if (parm.existData("CLINICAREA")) {
            Object update = parm.getData("CLINICAREA");
            if (update == null) {
                return null;
            }
            if (update instanceof String[]) {
                String[] updateSql = (String[]) update;
                for (int i = 0; i < updateSql.length; i++) {
                    result = new TParm(TJDODBTool.getInstance().update(
                        updateSql[i], conn));
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
            else {
                return null;
            }
        }
        // 病区信息
        if (parm.existData("STATION")) {
            Object update = parm.getData("STATION");
            if (update == null) {
                return null;
            }
            if (update instanceof String[]) {
                String[] updateSql = (String[]) update;
                for (int i = 0; i < updateSql.length; i++) {
                    result = new TParm(TJDODBTool.getInstance().update(
                        updateSql[i], conn));
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
            else {
                return null;
            }
        }
        //记录更改用户的角色及变更日志,当用户权限，权限组，角色发生变化时,写变更信息至SYS_OPERATORLOG表
        if (parm.existData("userInfoChange")) {
            TParm userInfoChange = new TParm((Map)parm.getData("userInfoChange"));
            /*
             * 构造dataParm,一次修改的不只是角色的改变,坑内包含其他
             */System.out.println(userInfoChange.getCount("MODI_ITEM"));
            for (int i = 0; i < userInfoChange.getCount("MODI_ITEM"); i++) {
                TParm dataParm = new TParm();
                dataParm.setData("MODI_ITEM",
                                 userInfoChange.getData("MODI_ITEM", i));
                dataParm.setData("MODI_ITEM_CHN",
                                 userInfoChange.getData("MODI_ITEM_CHN", i));
                dataParm.setData("ITEM_OLD",
                                 userInfoChange.getData("ITEM_OLD", i));
                dataParm.setData("ITEM_NEW",
                                 userInfoChange.getData("ITEM_NEW", i));
                dataParm.setData("DESCRIPTION",
                                 userInfoChange.getData("DESCRIPTION", i));
                dataParm.setData("USER_ID", userInfoChange.getData("USER_ID"));
                dataParm.setData("OPT_DATE", userInfoChange.getData("OPT_DATE"));
                dataParm.setData("OPT_USER", userInfoChange.getData("OPT_USER"));
                dataParm.setData("OPT_TERM", userInfoChange.getData("OPT_TERM"));
                //执行,
                result = this.update("userInfoChange", dataParm, conn);
                if (result.getErrCode() < 0) {
                    return result;
                }else{
                    continue;
                }
            }
        }
       
        if(parm.existData("emrRuleUser")){
        	
        	TParm emrRuleUser = new TParm((Map)parm.getData("emrRuleUser"));
        		
                TParm dataParm = new TParm();
                dataParm.setData("ID",
                                 emrRuleUser.getData("ID1"));
                dataParm.setData("EMR_RULE_CODE",
                                 emrRuleUser.getData("EMR_RULE_CODE1"));
                dataParm.setData("USER_EMR_ID",
                                 emrRuleUser.getData("USER_ID1"));
                dataParm.setData("START_DATE",
                                 emrRuleUser.getData("START_DATE1"));
                dataParm.setData("END_EMR_DATE",
                                 emrRuleUser.getData("END_DATE1"));
               
                //执行,
                result = this.update("emrRuleUser", dataParm, conn);
                
                if (result.getErrCode() < 0) {
                    return result;
                }
             
        }
        

        return result;
    }

    /**
     * 得到门急诊收费员
     * @return TParm
     * ======pangben modify 20110421 添加参数
     */
    public TParm getCasherCode(TParm parm) {
        TParm result = result = query("getCasherCode",parm);
        //if (result.getErrCode() < 0)
        //System.out.println("取得收费员" + result.getErrText());
        return result;
    }
    
    /**
     * 取得用用户自己的权限  add by huangtt 20150513
     * add by zhanglei 20171201 同步个人权限控制
     * @param userId
     * @param code
     * @return
     */
    public static TParm getUserPopedem(String userId,String code){
    	String sql = " SELECT AUTH_CODE FROM SYS_USER_AUTH WHERE USER_ID ='"+userId+"' AND " +
    			" GROUP_CODE = '"+code+"'" ;
    	TParm TP = new TParm( TJDODBTool.getInstance().select( sql ) );
    	return TP;
    }

    /**
     * 根据用户ID查询该用户是否有毒麻药证照
     * @param user_id String
     * @return boolean
     */
    public boolean getOperatorCtrlFlg(String user_id) {
        TParm parm = getLcsCode(user_id);
        if (parm == null || parm.getCount() == 0) {
            return false;
        }
        for (int i = 0; i < parm.getCount(); i++) {
            if ("1".equals(parm.getValue("LCS_CLASS_CODE", i))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean onInsertEmr(TParm parm){
    	TParm result = this.update("emrRuleUser", parm);
        if(result.getErrCode() < 0){
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }
}
