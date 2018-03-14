package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SYSDictionaryTool
    extends TJDOTool {
    /**
     * 实例
     */
    private static SYSDictionaryTool instanceObject;
    /**
     * 得到实例
     * @return PatTool
     */
    public static SYSDictionaryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSDictionaryTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SYSDictionaryTool() {
        setModuleName("sys\\SYSDictionaryModule.x");
        onInit();
    }

    /**
     * 初始化界面，查询所有的数据
     * @return TParm
     */
    public TParm select(TParm parm) {
        TParm result = query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm insert(TParm parm) {
        TParm result = new TParm();
        result = update("insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insert(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm update(TParm parm) {
        TParm result = new TParm();
        result = update("update", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm update(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("update", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
    * 修改默认注记
    * ======pangben 2011-12-30
    * @param parm TParm
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateFlg(TParm parm, TConnection conn) {
       TParm result = new TParm();
       result = update("updateFlg", parm, conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }

    /**
     *
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm delete(TParm parm) {
        TParm result = new TParm();
        result = update("delete", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     *
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm delete(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("delete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     *
     * @return TParm
     */
    public TParm onUpdatePopedom(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        TParm result = new TParm();

        Object parmM = parm.getData("PARM_M");
        if (parmM != null) {
            TParm inParm = parm.getParm("PARM_M");
            result = this.update(inParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }

        Object parmD = parm.getData("PARM_D");
        if (parmD != null) {
            TParm inParm = parm.getParm("PARM_D");
            result = this.insert(inParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * 删除目录分类
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeletePath(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        TParm result = new TParm();
        return result;
    }

    /**
     * 删除程序
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeletePrg(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        TParm result = new TParm();
        // 删除挂接程序
        Object prgParm = parm.getData("PRG_PARM");
        if (prgParm != null) {
            TParm inParm = parm.getParm("PRG_PARM");
            result = this.delete(inParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }

        // 删除程序所有权限
        Object popedomParm = parm.getData("POPEDOM_PARM");
        if (popedomParm != null) {
            TParm inParm = parm.getParm("POPEDOM_PARM");
            for (int i = 0; i < inParm.getCount(); i++) {
                result = this.delete(inParm.getRow(i), conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }
            }
        }

        // 删除程序所有用户的权限,删除程序所有的使用用户
        Object roleParm = parm.getData("ROLE_PARM");
        if (roleParm != null) {
            TParm inParm = parm.getParm("ROLE_PARM");
            result = SYSRolePopedomTool.getInstance().deleteUserRolePopedom(
                inParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * 删除权限
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeletePopedom(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        TParm result = new TParm();
        // 删除程序权限
        Object popedomParm = parm.getData("POPEDOM_PARM");
        if (popedomParm != null) {
            TParm inParm = parm.getParm("POPEDOM_PARM");
            result = this.delete(inParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }

        // 删除用户权限
        Object roleParm = parm.getData("ROLE_PARM");
        if (roleParm != null) {
            TParm inParm = parm.getParm("ROLE_PARM");
            result = SYSRolePopedomTool.getInstance().deleteRolePopedom(inParm,
                conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

}
