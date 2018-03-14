package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title:病案审核总Tool </p>
 *
 * <p>Description:病案审核总Tool </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-4
 * @version 1.0
 */
public class MROChrActionTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static MROChrActionTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROChrActionTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROChrActionTool();
        return instanceObject;
    }

    public MROChrActionTool() {
    }

    /**
     * 1，插入数据
     * 2，修改首页 mro_record表的MRO_CHAT_FLG字段
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm,TConnection conn) {
        TParm chatParm = new TParm();
        chatParm.setData("CASE_NO",parm.getValue("CASE_NO"));
        chatParm.setData("MRO_CHAT_FLG","1");//修改审核状态字段为1  审核中
        chatParm.setData("TYPERESULT","0");//修改完成状态字段为0
        TParm result = new TParm();
        result = MROChrtvetrecTool.getInstance().insertdata(parm,conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = MRORecordTool.getInstance().updateMRO_CHAT_FLG(chatParm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新数据
     * @param regMethod String
     * @return TParm
     */
    public TParm updatedata(TParm parm,TConnection conn) {
        TParm result = MROChrtvetrecTool.getInstance().updatedata(parm,conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除数据
     * @param parm TParm  三个必须参数  CASE_NO，EXAMINE_DATE，EXAMINE_CODE
     * @return TParm
     */
    public TParm deletedata(TParm parm,TConnection conn){
        TParm result = MROChrtvetrecTool.getInstance().deletedata(parm,conn);
        // 判断错误值
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
