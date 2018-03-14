package action.adm;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.adm.ADMResvTool;
import com.dongyang.db.TConnection;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;

/**
 * <p>Title: 预约住院Action</p>
 *
 * <p>Description: 预约住院Action</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk
 * @version 1.0
 */
public class ADMResvAction
    extends TAction {
    public ADMResvAction() {
    }

    /**
     * 新增预约住院表
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm bedParm = new TParm();
        bedParm.setData("APPT_FLG","Y");
        bedParm.setData("OPT_USER",parm.getValue("OPT_USER"));
        bedParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
        bedParm.setData("BED_NO",parm.getValue("BED_NO"));
        TConnection conn = getConnection();
        TParm result = ADMResvTool.getInstance().insertdata(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            conn.close();
            return result;
        }
        if (bedParm.getData("BED_NO") != null) {
            result = SYSBedTool.getInstance().upDateForResv(bedParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                conn.close();
                return result;
            }

        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 修改病患信息
     * @param parm TParm
     * @return TParm
     */
    public TParm upDate(TParm parm) {
        TParm resvParm = new TParm();
        resvParm.setRowData(parm);
        TConnection conn = getConnection();
        TParm result = ADMResvTool.getInstance().upDate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            conn.close();
            return result;
        }
        TParm bedParm = new TParm();
        bedParm.setRowData(parm);
        if (bedParm.getData("BED_NO") != null) {
            bedParm.setData("APPT_FLG", "Y");
            result = SYSBedTool.getInstance().upDateForResv(bedParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 取消预约
     * @param parm TParm
     * @return TParm
     */
    public TParm cancelResv(TParm parm) {
        TConnection conn = this.getConnection();
        TParm result = new TParm();
        result = ADMResvTool.getInstance().cancelResv(parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 添加预登记表的数据
     * @param parm
     * @return
     */
    public TParm insertPretreat(TParm parm){
    	TConnection conn = getConnection();
    	TParm result = new TParm();
    	TParm bedParm=parm.getParm("BEDPARM");
    	TParm param=parm.getParm("PARM");
        result = ADMResvTool.getInstance().insertPretreat(param,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            conn.close();
            return result;
        }
        if(!bedParm.getValue("BED_NO").equals("") && bedParm.getValue("BED_NO")!=null){
        	bedParm.setData("PRE_FLG","N");
        	bedParm.setData("PRETREAT_DATE",param.getValue("PRETREAT_DATE"));
        	bedParm.setData("PRE_MRNO",param.getValue("MR_NO"));
        	bedParm.setData("PRETREAT_TYPE",param.getValue("PRETREAT_TYPE"));
        	bedParm.setData("PRETREAT_NO",param.getValue("PRETREAT_NO"));
        	result = ADMResvTool.getInstance().updateBedInfo(bedParm,conn);
        	if (result.getErrCode() < 0) {
        		err("ERR:" + result.getErrCode() + result.getErrText() +
        				result.getErrName());
        		conn.close();
        		return result;
        	}
        }
        
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 修改预登记表的数据
     * @param parm
     * @return
     */
    public TParm updatePretreat(TParm parm){
    	TConnection conn = getConnection();
    	TParm result = new TParm();
        result = ADMResvTool.getInstance().updatePretreat(parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
   
}
