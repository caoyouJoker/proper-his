package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class IndVerifyinDTool   
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndVerifyinDTool instanceObject;

    /**
     * ������
     */    
    public IndVerifyinDTool() {              
        setModuleName("spc\\INDVerifyinDModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static IndVerifyinDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndVerifyinDTool();
        return instanceObject;
    }

    /**
     * ��ѯ������ϸ
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryVerifyinD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��ѯ������ϸ
     *
     * @param parm
     * @return
     */
    public TParm onQueryAcnt(TParm parm) {//wanglong add 20150202
        TParm result = this.query("querySPCVerifyinD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ����δ�˻���ϸ��
     *
     * @param parm
     * @return
     */
    public TParm onQueryVerifyinDone(TParm parm) {
        TParm result = this.query("queryVerifyinDoneD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����δ�˻���ϸ��
     *
     * @param parm
     * @return
     */
    public TParm onQuerySPCVerifyinDone(TParm parm) {//wanglong add 20150202
        TParm result = this.query("querySPCVerifyinDoneD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * �ۼ��˻�������
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateReg(TParm parm, TConnection conn) {
        TParm result = this.update("updateVerifyinDReg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �ۼ��˻�������
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateRegAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("updateSPCVerifyinDReg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ҩƷ�������ͳ��(�������)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinBuyMaster(TParm parm) {
        TParm result = this.query("getQueryVerifyinBuyMaster", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ҩƷ�������ͳ��(�������)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinBuyMasterAcnt(TParm parm) {//wanglong add 20150202
        TParm result = this.query("getQueryVerifyinBuyMasterAcnt", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ҩƷ�������ͳ��(������ϸ)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinBuyDetail(TParm parm) {
        TParm result = this.query("getQueryVerifyinBuyDetail", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ҩƷ�������ͳ��(������ϸ)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinBuyDetailAcnt(TParm parm) {//wanglong add 20150202
        TParm result = this.query("getQueryVerifyinBuyDetailAcnt", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ҩƷ�������ͳ��(��ҩ����)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinGiftMaster(TParm parm) {
        TParm result = this.query("getQueryVerifyinGiftMaster", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ҩƷ�������ͳ��(��ҩ����)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinGiftMasterAcnt(TParm parm) {//wanglong add 20150202
        TParm result = this.query("getQueryVerifyinGiftMasterAcnt", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ҩƷ�������ͳ��(��ҩ��ϸ)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinGiftDetail(TParm parm) {
        TParm result = this.query("getQueryVerifyinGiftDetail", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ҩƷ�������ͳ��(��ҩ��ϸ)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinGiftDetailAcnt(TParm parm) {//wanglong add 20150202
        TParm result = this.query("getQueryVerifyinGiftDetailAcnt", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
}