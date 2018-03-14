package action.mro;

import com.dongyang.action.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import jdo.mro.MROChrActionTool;
import jdo.mro.MROChrtvetrecTool;
import jdo.mro.MROQlayControlMTool;

/**
 * <p>Title: 病案审核Action</p>
 *
 * <p>Description: 病案审核Action</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-4
 * @version 1.0
 */
public class MROChrtvetrecAction
    extends TAction {
	// modified by WangQing at 20170209 -start
	// 删除connection成员变量,每个方法中各自使用自己的connection
//    TConnection connection;
	// modified by WangQing at 20170209 -end
    public MROChrtvetrecAction() {
    }
    /**
     * 插入审核信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"参数不存在！");
            return result;
        }
        // modified by WangQing at 20170209 -start
        // 新建connection变量
        TConnection connection = this.getConnection();
        // modified by WangQing at 20170209 -end
        //===========add by wanglong 20130819
        result = MROQlayControlMTool.getInstance().onInsert(parm, connection);// 新增质控记录主档
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //===========add end
        result = MROChrActionTool.getInstance().insertdata(parm,connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.rollback();//add by wanglong 20130819
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 更新数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"参数不存在！");
            return result;
        }
        // modified by WangQing at 20170209 -start
        // 新建connection变量
        TConnection connection = this.getConnection();
        // modified by WangQing at 20170209 -end
        result = MROChrActionTool.getInstance().updatedata(parm,connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 删除数据
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(TParm parm){
    	// modified by WangQing at 20170209 -start
        // 新建connection变量
        TConnection connection = this.getConnection();
        // modified by WangQing at 20170209 -end
        TParm result = new TParm();
        result = MROChrActionTool.getInstance().deletedata(parm,connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //add by wanglong 20130819
        result = MROQlayControlMTool.getInstance().onDelete(parm,connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.rollback();
            connection.close();
            return result;
        }
        //add end
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * 修改病案审核注记（包括MR_RECORD和ADM_INP两张表的）
     * @param parm TParm 必须参数：CASE_NO；MRO_CHAT_FLG：0，未审核   1，审核中   2，完成
     * @return TParm
     */
    public TParm updateMRO_CHAT_FLG(TParm parm){
    	// modified by WangQing at 20170209 -start
        // 新建connection变量
        TConnection connection = this.getConnection();
        // modified by WangQing at 20170209 -end
        TParm result = new TParm();
        result = MROChrtvetrecTool.getInstance().updateMRO_CHAT_FLG(parm,connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
}
