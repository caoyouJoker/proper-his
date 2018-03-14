package action.adm;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.adm.ADMAutoBillTool;

/**
 * <p>Title:סԺ�Ǽ�Action </p>
 *
 * <p>Description:סԺ�Ǽ�Action </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMInpAction
    extends TAction {
    /**
     * ��Ժ�ǼǱ��淽��
     * @param parm TParm
     * @return TParm
     */
    public TParm insertADMData(TParm parm){
        TParm result = new TParm();
        TConnection conn = this.getConnection();
        result = ADMInpTool.getInstance().insertADMData(parm,conn);
        if(result.getErrCode()<0){
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    /**
     * ȡ��סԺ
     * @param parm TParm
     * @return TParm
     */
    public TParm ADMCanInp(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        result = ADMTool.getInstance().ADM_CANCEL_INP(parm, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    /**
     *��ȡ����
     * @param parm TParm
     * @return TParm
     */
    public TParm loadSysBed(TParm parm) {
        TParm result = new TParm();
        result.setData("CASE_NO", parm.getData("CASE_NO"));
        result.setData("SEQ_NO", 1);
        result.setData("IPD_NO", parm.getData("IPD_NO"));
        result.setData("MR_NO", parm.getData("MR_NO"));
        result.setData("CHG_DATE", parm.getData("DATE"));
        result.setData("PSF_KIND", "IN");
        result.setData("PSF_HOSP", "");
        result.setData("CANCEL_FLG", "N");
        result.setData("CANCEL_DATE", "");
        result.setData("CANCEL_USER", "");
        result.setData("DEPT_CODE", parm.getData("DEPT_CODE"));
        result.setData("STATION_CODE", parm.getData("STATION_CODE"));
        result.setData("BED_NO", parm.getData("BED_NO"));
        result.setData("VS_CODE_CODE", parm.getData("VS_DR_CODE"));
        result.setData("ATTEND_DR_CODE", "");
        result.setData("DIRECTOR_DR_CODE", "");
        result.setData("OPT_USER", parm.getData("OPT_USER"));
        result.setData("OPT_TERM", parm.getData("OPT_TERM"));
        return result;
    }

    /**
     * סԺ�Ǽ��޸�
     * @param parm TParm
     * @return TParm
     */
    public TParm upDataAdmInp(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = ADMTool.getInstance().updataAdmInp(parm, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();

        return result;
    }
    /**
     * �̶������Զ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm postAutoBill(TParm parm){
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = ADMAutoBillTool.getInstance().postAutoBill(parm,conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    /**
     * �̶������Զ����ˣ��Ը���Ϊ��λ���ʣ�
     * @param parm TParm
     * @return TParm
     */
    public TParm postAutoBillOfMen(TParm parm){
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = ADMAutoBillTool.getInstance().postAutoBillOfMen(parm,conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ��Ժ�����ٻ�
     * @param parm TParm  ��Ҫ������CASE_NO;MR_NO;BED_NO;OPT_USER;OPT_TERM;
     * @return TParm
     */
    public TParm returnAdm(TParm parm){
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = ADMInpTool.getInstance().returnAdm(parm,conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
}
