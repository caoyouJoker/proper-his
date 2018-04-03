package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ������������</p>
 *
 * <p>Description: ������������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-12
 * @version 1.0
 */
public class MROQueueTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static MROQueueTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MROQueueTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROQueueTool();
        return instanceObject;
    }

    public MROQueueTool() {
        this.setModuleName("mro\\MROQueueModule.x");
        this.onInit();
    }

    /**
     * ��ѯ�����ⲡ��
     * @return TParm
     */
    public TParm queryQueue(TParm parm) {
        TParm result = this.query("selectOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸�Queue�� ���������״̬
     * @param parm TParm
     * @return TParm
     */
    public TParm updateISSUE(TParm parm, TConnection conn) {
        TParm result = this.update("updateOut", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸Ĳ������� �����ڿ�״̬
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateIn_flg(TParm parm, TConnection conn) {
        TParm result = this.update("updateIn_flg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��������� ����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOUT(TParm parm, TConnection conn) {
        TParm p;
        TParm result = new TParm();
        if (parm.getData("MRV") != null) {
            p = parm.getParm("MRV");
            result = this.updateIn_flg(p, conn);

            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        else{
            result.setErr(-1,"ȱ��MRV�����");
            return result;
        }
        if (parm.getData("Queue") != null) {
            p = parm.getParm("Queue");
            result = this.updateISSUE(p, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }else{
            result.setErr(-1,"ȱ��Queue�����");
            return result;
        }
        if (parm.getData("Tranhis") != null) {
            p = parm.getParm("Tranhis");
            result = this.insertTRANHIS(p, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            return result;
        }else{
            result.setErr(-1,"ȱ��Tranhis�����");
            return result;
        }

    }

    /**
     * ���벡��������ʷ��¼��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertTRANHIS(TParm parm, TConnection conn) {
        TParm result = this.update("insertTRANHIS", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����������ʷ��¼�� ��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectTRANHIS(TParm parm){
        TParm result = this.query("selectTRANHIS",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �����������Ϣ��ѯ
     * @return TParm
     */
    public TParm selectIn(TParm parm){
        TParm result = this.query("selectIn",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��Ժ�����鵵��ҳǩ3ʹ�ã�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOutHp(TParm parm){
        TParm result = this.query("selectOutHp",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ���ⲡ����Ϣ��ҳǩ3ʹ�ã�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOutQueue(TParm parm){
        TParm result = this.query("selectOutQueue",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���벡������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertMRO_MRV(TParm parm){
        TParm result = this.update("insertMRO_MRV",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����CASE_NOȡ�������ⲡ��(סԺ�Ǽ�ȡ��ʱʹ��)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm cancelQueueByCASE_NO(TParm parm,TConnection conn){
        TParm result = this.update("cancelQueueByCASE_NO",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectMRO_MRV(TParm parm){
        TParm result = this.query("selectMRO_MRV",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����Ƿ��Ѿ����ڲ�������
     * @param MR_NO String
     * @return boolean ���ؽ����true���ò����ŵĲ��������Ѵ��ڴ���  false���ò����ŵĲ�������������
     */
    public boolean checkHasMRO_MRV(String MR_NO){
        TParm parm = new TParm();
        parm.setData("MR_NO",MR_NO);
        TParm result = this.selectMRO_MRV(parm);
        boolean flg = false;
        if(result.getCount()>0){
            flg = true;
        }
        return flg;
    }

    /**
     * ��ѯ�����Ƿ�鵵
     * @param parm TParm
     * @return TParm
     */
	public TParm selectMRO_MRV_TECH(TParm parm) {
		TParm result = this.query("selectMRO_MRV_TECH", parm);
		if(result.getErrCode() < 0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * �������������סԺ�����������ѳ������ȡ��סԺ�����ݽ��й黹�����趨
	 * Ǩ��Ŀ�ģ����ת�ƹ���
	 * �����ˣ�zhutong   
	 * ����ʱ�䣺2018��3��30�� ����1:09:07    
	 */
	public TParm updateRtnDateByCaseNo(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("UPDATE MRO_QUEUE SET RTN_DATE = TO_DATE(TO_CHAR(SYSDATE, 'YYYY/MM/DD'), 'YYYY/MM/DD')");
		sbSql.append(" WHERE ISSUE_CODE = '1' AND CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("'");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sbSql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
}
