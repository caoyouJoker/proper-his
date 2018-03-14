package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 病案借阅登记</p>
 *
 * <p>Description: 病案借阅登记</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-11
 * @version 1.0
 */
public class MROLendRegTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static MROLendRegTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROLendRegTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROLendRegTool();
        return instanceObject;
    }

    public MROLendRegTool() {
        this.setModuleName("mro\\MROLendRegModule.x");
        onInit();
    }
    /**
     * 获取指定“借阅原因”的借阅天数
     * @param code String
     * @return TParm
     */
    public TParm queryLendDays(String code){
        TParm parm = new TParm();
        parm.setData("LEND_CODE",code);
        TParm result = this.query("selectLendDay",parm);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 插入病历待出库表
     * @param parm TParm
     * @return TParm
     */
    public TParm insertQueue(TParm parm){
        TParm result =this.update("insertQueue",parm);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询病历在出入库流水表中每日是否是唯一记录
     * @param parm TParm
     * @return TParm
     */
	public TParm selectQueue(TParm parm) {
		TParm result = this.query("selectQueue", parm);
		if(result.getErrCode() < 0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			return result;
		}
		return result;
	}
}
