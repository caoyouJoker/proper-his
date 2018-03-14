package jdo.inp;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: 会诊申请</p>
*
* <p>Description: 会诊申请报表</p>
*
* <p>Copyright: Copyright (c) caoyong 20139011</p>
*
* <p>Company: JavaHis</p>
*
* @author caoyong
* @version 1.0
*/
public class ConsApplicationTool extends TJDOTool{
	
	public static ConsApplicationTool instanceObject;
	
	public static ConsApplicationTool getInstance(){
		if(instanceObject==null){
			instanceObject=new ConsApplicationTool();
			
		}
		return instanceObject;
	}
	
	public ConsApplicationTool() {
		setModuleName("inp\\INPConsApplicationModule.x");
		onInit();
	}
	/**
	 * 插入公告栏
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertBoard(TParm parm) {
		TParm result = new TParm();
		result = update("inserBoard", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 插入接收档
	 * 
	 * @param parm
	 * @return
	 */
	
	public TParm insertPostRCV(TParm parm) {
		TParm result = new TParm();
		result = update("inserPostRCV", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询操作
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdata(TParm parm) {
		TParm result = new TParm();
		result = query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询操作被会科室值班医生
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdacc(TParm parm) {
		TParm result = new TParm();
		result = query("selectdatacc", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询操作被会科室指定医生
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdass(TParm parm) {
		TParm result = new TParm();
		result = query("selectdatass", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询操作一个患者所有的会诊申请记录
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdataall(TParm parm) {
		TParm result = new TParm();
		result = query("selectall", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
		
	}
	
	/**
     * 删除
     * @param admType String
     * @param clinicTypeCode String
     * @param orderCode String
     * @return TParm
     */
    public TParm deletedata(TParm parm) {
       
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 新增
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 发送邮件
     */
    
    public TParm updateEMail(TParm parm){
        TParm result = update("updateEMail",parm);
        // 判断错误值
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
	 * 查询发送短信息的手机号码
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdMess(TParm parm) {
		TParm result = new TParm();
		result = query("selectdMess", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询会诊是否已完成
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdCons(TParm parm) {
		TParm result = new TParm();
		result = query("selectdCons", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	

}
