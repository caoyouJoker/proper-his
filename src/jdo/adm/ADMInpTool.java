package jdo.adm;

import java.util.Vector;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SYSBedTool;
import com.dongyang.ui.TLabel;
import com.dongyang.util.StringTool;
import jdo.ibs.IBSTool;
import jdo.sys.SYSDiagnosisTool;
import jdo.mro.MRORecordTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.javahis.ui.odi.ODIClnDiagControl.OrderList;
import jdo.sys.Operator;
import jdo.sys.SYSEmrIndexTool;
import jdo.sys.PatTool;

/**
 * <p>Title:סԺ�Ǽ� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMInpTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static ADMInpTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMInpTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMInpTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ADMInpTool() {
        setModuleName("adm\\ADMInpModule.x");
        onInit();
    }

    /**
     * insert ADM_INP
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("insertForInp", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * insert ADM_INP
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        result = update("insertForInp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯȫ�ֶ�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectall(TParm parm) {
        TParm result = new TParm();
        result = query("selectall", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    //============================== add  by chenxi 20130228
    /**
     * ��ѯԤԼ����
     * @param parm TParm    
     * @return TParm
     */
    public TParm selectBedNo(TParm parm) {
        TParm result = new TParm();
        result = query("selectBedNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��鲡���Ƿ�סԺ
     * @param parm TParm
     * @return TParm
     */
    public TParm checkAdmInp(TParm parm) {
        TParm result = query("checkAdmInp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ��Ժ����
     * @param parm TParm
     * @return TParm
     */
    public TParm queryInStation(TParm parm) {
        TParm result = query("queryInStation", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * ��ѯ��һ�γ�Ժʱ��
     * @param parm TParm
     * @return TParm
     */
    public TParm queryLastDsdate(TParm parm) {
        TParm result = query("SelectLastDsDate", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * ������Ժ������λ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForWait(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("updateForWait", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���²�������������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm updateForWaitPat(TParm parm) {
        TParm result = new TParm();
        result = update("updateForWaitPat", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���²�������������Ϣ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForWaitPat(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("updateForWaitPat", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������Ժ������λ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForOutDept(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("updateForOutDept", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��Ժ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm outAdmInp(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("outAdmInp", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��λ����
     * @param parm TParm
     * @return TParm
     */
    public TParm QueryBed(TParm parm) {
        TParm result = new TParm();
        result = query("queryBed", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * ��ѯ��Ժ������case_no
     * ��ѯ��Ժ�����Ļ�����Ϣ
     * �������,סԺ��,������,����,����/��ʿվ
     * ����,���һ,��Դ��O�����E�����I��סԺ��,��Ժ����,�ż���ҽ��,
     * ����ҽ��,����ҽ��,ĿǰסԺ����,��ɫ����,��ɫ����,Ԥ����
     * ������ȫ��Ϊ��Σ���MR_NO��IPD_NO��CASE_NO
     * @param parm TParm
     * @return TParm
     */
    public TParm queryCaseNo(TParm parm) {
        TParm result = new TParm();
        result = query("queryCaseNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * �޸Ļ�ɫ���䣬��ɫ����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateYellowRed(TParm parm) {
        TParm result = new TParm();
        result = update("updateYellowRed", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * �޸Ļ�ɫ���䣬��ɫ����,��Ժ���ڣ�סԺ�������ȱ�ܹ��޸ģ�
     * @param parm TParm
     * @return TParm
     */
    public TParm updateForAdmInp(TParm parm) {
        TParm result = new TParm();
        result = update("updateForAdmInp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * �޸Ļ�ɫ���䣬��ɫ����,��Ժ���ڣ�סԺ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForAdmInp(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("updateForAdmInp", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }


    /**
     * ����Ԥ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateForBillPay(TParm parm, TConnection connection) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "ADMInpTool.updateForBillPay()�����쳣!");
            return result;
        }
        result = update("updateForBillPay", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;

    }

    /**
     * ȡ��סԺ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForCancel(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "�����쳣!");
            return result;
        }
        result = update("updateForCancel", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;

    }

    /**
     * �޸Ĳ����������
     * ����CASE_NO
     * @param parm TParm
     * @return TParm
     */
    public TParm upDateWeightHigh(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "�����쳣!");
            return result;
        }
        result = update("upDateWeightHigh", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }

    /**
     * �޸Ĳ����������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm upDateWeightHigh(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "�����쳣!");
            return result;
        }
        result = update("upDateWeightHigh", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }

    /**
     * ��˲����Ƿ��ڴ�
     * @param parm TParm
     * @return TParm
     */
    public TParm checkInBed(TParm parm) {
        TParm result = new TParm();
        result = query("queryCaseNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��Ժ�Ǽ�
     * 1������סԺ������ADM_INP��
     * 2�������ת������
     * 3�����벡����̬��
     * 4�����´�λ��
     * 5������ԤԼ��ϢADM_RESV
     * 6�����벡��������
     * 7���޸Ĳ���������Ϣ�е����סԺ���Ҽ�ʱ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertADMData(TParm parm, TConnection conn) {
        String MR_NO = parm.getValue("MR_NO");
        String CASE_NO = parm.getValue("CASE_NO");
        String OPT_USER = parm.getValue("OPT_USER");
        String OPT_TERM = parm.getValue("OPT_TERM");
        String IPD_NO = parm.getValue("IPD_NO");
        //���벡����̬������
        TParm admChg = loadAdmChg(parm);
        //ɾ����ת������
        TParm waitDelParm = new TParm();
        waitDelParm.setData("CASE_NO", CASE_NO);
        //�����ת������ ����
        TParm waitParm = new TParm();
        waitParm.setData("CASE_NO", CASE_NO);
        waitParm.setData("MR_NO", MR_NO);
        waitParm.setData("IPD_NO", IPD_NO);
        waitParm.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
        waitParm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
        waitParm.setData("OPT_USER", OPT_USER);
        waitParm.setData("OPT_TERM", OPT_TERM);
        //���´�λ�� ����
        TParm bedParm = new TParm();
        bedParm.setData("MR_NO", MR_NO);
        bedParm.setData("CASE_NO", CASE_NO);
        bedParm.setData("IPD_NO", IPD_NO);
        bedParm.setData("OPT_USER", OPT_USER);
        bedParm.setData("OPT_TERM", OPT_TERM);
        bedParm.setData("BED_NO", parm.getValue("BED_NO"));
        bedParm.setData("APPT_FLG", "N"); //ԤԼ���
        bedParm.setData("ALLO_FLG", "Y"); //��ס���
        bedParm.setData("BED_STATUS", "0");
        //ԤԼ��ѯ ����
        TParm selectResv = new TParm();
        selectResv.setData("MR_NO", MR_NO);
        //�޸�ԤԼ��Ϣ ����
        TParm resvParm = new TParm();
        resvParm.setData("CASE_NO", CASE_NO);
        resvParm.setData("IN_DATE",
                         StringTool.getString(parm.getTimestamp("IN_DATE"),
                                              "yyyyMMddHHmmss"));
        resvParm.setData("BED_NO", parm.getValue("BED_NO"));
        resvParm.setData("OPT_USER", OPT_USER);
        resvParm.setData("OPT_TERM", OPT_TERM);
        resvParm.setData("MR_NO", MR_NO);
        TParm result = new TParm();
        //1������סԺ����
        if(parm.getData("DAY_OPE_CODE") == null){
        	parm.setData("DAY_OPE_CODE", "");
        }
        result = ADMInpTool.getInstance().inHosptal(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
//        System.out.println("1");
        //2�������ת������
        //��ɾ��������ת��
        result = ADMWaitTransTool.getInstance().deleteIn(waitDelParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
//        System.out.println("2");
        //�����ת������
        result = ADMWaitTransTool.getInstance().saveForInp(waitParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
//        System.out.println("3");
        //���벡����̬��
        result = ADMChgTool.getInstance().insertAdmChg(admChg, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
//        System.out.println("4");
        // ���´�λ��        
        //=========  chenxi  modify  סԺ�Ǽ�ʱ�����´�λ��
//        if (!"".equals(bedParm.getValue("BED_NO"))) {
//            // ���´�λ��
//            result = SYSBedTool.getInstance().upDate(bedParm, conn);
//            if (result.getErrCode() < 0) {
//                err("ERR:" + result.getErrCode() + result.getErrText() +
//                    result.getErrName());
//                return result;
//            }
//        }
        //=========   סԺ�Ǽ�ʱ����ԤԼ��λʱ������appt_flg Ϊy   chenxi  modify 20130301
        if (!"".equals(bedParm.getValue("BED_NO"))) {
            TParm upBedAttp = new TParm();
            upBedAttp.setData("BED_NO", bedParm.getValue("BED_NO"));
            upBedAttp.setData("APPT_FLG", "Y");   
            upBedAttp.setData("OPT_USER", OPT_USER);
            upBedAttp.setData("OPT_TERM", OPT_TERM);
            result = SYSBedTool.getInstance().upDateForResv(upBedAttp, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
//        System.out.println("5");
        //��ѯ�Ƿ���ԤԼ��Ϣ
        TParm resvNo = ADMResvTool.getInstance().selectAll(selectResv);
        //=========================  chenxi modify 20130311 
        if(resvNo.getValue("BED_NO", 0).length()>0 && !resvNo.getValue("BED_NO", 0).equals(bedParm.getValue("BED_NO"))){
        	TParm  bedResult = new TParm() ;
        	bedResult.setData("BED_NO", resvNo.getData("BED_NO", 0)) ;
          result = ADMResvTool.getInstance().upDateSysBed(bedResult, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        //========================   chenxi modify  20130311
        //���β�����ԤԼ��Ϣ ������ADM_RESV ͬʱҪ��ԤԼ�Ĵ�λ��ԤԼ���ȡ��
        if (resvNo.getCount() > 0) {
            resvParm.setData("RESV_NO", resvNo.getData("RESV_NO", 0));
            result = ADMResvTool.getInstance().upDateForInp(resvParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
//            System.out.println("8");
//            System.out.println("9");
            //�ж��Ƿ����ż������ ����������Ҫ���뵽ADM_INPDIAG��
            if (resvNo.getValue("DIAG_CODE", 0).length() > 0) {
                //��ȡ�����Ϣ
                TParm diag = SYSDiagnosisTool.getInstance().selectDataWithCode(
                        resvNo.getValue("DIAG_CODE", 0));
                TParm DiagParm = new TParm();
                DiagParm.setData("CASE_NO", CASE_NO);
                DiagParm.setData("IO_TYPE", "I"); //'I':�ż������  ,'M'����Ժ��� 'O':��Ժ���
                DiagParm.setData("ICD_CODE", resvNo.getValue("DIAG_CODE", 0));
                DiagParm.setData("MAINDIAG_FLG", "Y");
                DiagParm.setData("ICD_TYPE", diag.getValue("ICD_TYPE", 0)); //��Ҫ�������CODEȥ��ѯ
                DiagParm.setData("SEQ_NO", "1");
                DiagParm.setData("MR_NO", MR_NO);
                DiagParm.setData("IPD_NO", IPD_NO);
                DiagParm.setData("DESCRIPTION",
                                 resvNo.getValue("DIAG_REMARK", 0));
                DiagParm.setData("OPT_USER", OPT_USER);
                DiagParm.setData("OPT_TERM", OPT_TERM);
                result = ADMDiagTool.getInstance().insertDiag(DiagParm, conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
//                System.out.println("10");

				 //�Ѹ��ż�����ϲ��뵽MRO_RECORD_DIAG���� duzhw add 20131213(����ԤԼסԺҳ����д���ż���ϻ���Ҫͬ����������ϱ�)
                OrderList orderDesc = new OrderList();
                TParm mroDiag = new TParm();
                mroDiag.setData("CASE_NO", CASE_NO);// CASE_NO�������
                mroDiag.setData("MR_NO", MR_NO);// MR_NO�������
                mroDiag.setData("IPD_NO", IPD_NO);// IPD_NO�������
                mroDiag.setData("IO_TYPE", "I");// ����
                mroDiag.setData("ICD_KIND", "W");// ���� W��ҽ���C��ҽ���
                mroDiag.setData("MAIN_FLG", "Y");// �����
                mroDiag.setData("ICD_CODE", resvNo.getValue("DIAG_CODE", 0));// ��ϱ���
                mroDiag.setData("SEQ_NO", "1");// ��ϱ���
                mroDiag.setData("ICD_DESC", orderDesc.getTableShowValue(resvNo.getValue("DIAG_CODE", 0)));// �������
                mroDiag.setData("ICD_REMARK", resvNo.getValue("DIAG_REMARK", 0));// ��ע
                mroDiag.setData("ICD_STATUS", "");// ת��
                mroDiag.setData("ADDITIONAL_CODE", "");// ������
                mroDiag.setData("ADDITIONAL_DESC", "");// �����������
                mroDiag.setData("IN_PAT_CONDITION", "");// ��Ժ����
                mroDiag.setData("OPT_USER", OPT_USER);
                mroDiag.setData("OPT_TERM", OPT_TERM);
                result = this.update("insertMRODiag", mroDiag, conn);
    			if (result.getErrCode() < 0) {
    				err("ERR:" + result.getErrCode() + result.getErrText()
    						+ result.getErrName());
    				conn.rollback();
    				conn.close();
    				return result;
    			}
                //�޸�ADM_INP����������ֶ�MAINDIAG
                TParm daigNew = new TParm();
                daigNew.setData("MAINDIAG", resvNo.getValue("DIAG_CODE", 0));
                daigNew.setData("OPT_USER", OPT_USER);
                daigNew.setData("OPT_TERM", OPT_TERM);
                daigNew.setData("CASE_NO", CASE_NO);
                result = this.updateNewDaily(daigNew, conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
//                System.out.println("11");
            }
        }
//        System.out.println("6");
        //���벡��������
        if (!SYSEmrIndexTool.getInstance().onInsertIpd(CASE_NO, "I",
                parm.getValue("REGION_CODE"), IPD_NO,
                MR_NO,
                parm.getTimestamp("DATE"),
                parm.getValue("IN_DEPT_CODE"),
                parm.getValue("VS_DR_CODE"),
                OPT_USER, OPT_TERM, conn)) {
            result.setErr( -1, "�����������������");
            return result;
        }
        //�޸Ĳ���������Ϣ �� �����Ժ���Һ���Ժʱ��
        TParm patParm = new TParm();
        patParm.setData("ADM_TYPE", "I"); //�ż�ס��
        patParm.setData("VISIT_CODE", ""); //����ʹ������ סԺ����д
        patParm.setData("REALDEPT_CODE", parm.getValue("IN_DEPT_CODE")); //��ס����
        patParm.setData("MR_NO", MR_NO); //������
        result = PatTool.getInstance().upLatestDeptDate(patParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
//        System.out.println("7");
        return result;
    }
	

	/**
	 * ģ����ѯ���ڲ��ࣩ
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}
    /**
     * Ϊadm_chg׼������
     * @param parm TParm
     * @return TParm
     */
    public TParm loadAdmChg(TParm parm) {
        TParm result = new TParm();
        TParm seqParm = ADMChgTool.getInstance().ADMQuerySeq(parm);
        String seq = "";
        if (seqParm.getData("SEQ_NO", 0) == null ||
            "".equals(seqParm.getData("SEQ_NO", 0)))
            seq = "0";
        else
            seq = seqParm.getData("SEQ_NO", 0).toString();

        result.setData("CASE_NO", parm.getData("CASE_NO"));
        result.setData("SEQ_NO", seq);
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
        //=======pangben modify 20110617 start
        result.setData("REGION_CODE", parm.getData("REGION_CODE"));
        return result;
    }

    /**
     * �޸�adm_inp������� ����Ժ�����>��Ժ�����>�ż�������ϣ�
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateNewDaily(TParm parm, TConnection conn) {
        TParm result = this.update("updateNewDaily", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * סԺ�Ǽ�ʱ������Ϣ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm inHosptal(TParm parm, TConnection conn) {
        TParm result = this.update("inHosptal", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����˵�
     * @param parm TParm
     * @param FLG String   "Y":��Ժ�˵�;"N":�н��˵�
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertIBSBiilData(TParm parm, String FLG, TConnection conn) {
        //������Ժ�˵�
        TParm result = new TParm();
        TParm ibsParm = new TParm();
        ibsParm.setData("IPD_NO", parm.getValue("IPD_NO"));
        TParm orderParm = new TParm();
        orderParm.setData("BILL_DATE", parm.getData("OUT_DATE"));
        orderParm.setData("OPT_USER", parm.getData("OPT_USER"));
        orderParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
        orderParm.setData("FLG", FLG); //��Ժ��Y  ת����N
        orderParm.addData("CASE_NO", parm.getValue("CASE_NO"));
        orderParm.setData("TYPE", "ADM");
        orderParm.setCount(1);
//        System.out.println("orderParm:"+orderParm);
        //�ж�������ڷ��� ��ô�����˵� ���򲻲���
        result = IBSTool.getInstance().insertIBSBillData(orderParm, conn);
//        System.out.println("���ûزΣ�"+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * סԺ�ٻ� ���ԭ�г�Ժ����
     * @param CASE_NO String
     * @param conn TConnection
     * @return TParm
     */
    public TParm clearDsDate(String CASE_NO, TConnection conn) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm result = this.update("clearDS_DATE", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��Ժ�ٻ� �޸��ٻغ�Ĵ�λ
     * @param parm TParm   ������BED_NO����λ�ţ�   CASE_NO
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateBedNoForReturn(TParm parm, TConnection conn) {
        TParm result = this.update("updateBedNoForReturn", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * סԺ�ٻ� ���ԭ�в����Ƿ��Ѿ���ռ��
     * @param CASE_NO String
     * @return boolean ��ռ�÷��� true�� �մ����� false
     */
    public boolean checkBedForReturn(String CASE_NO) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm result = this.selectall(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        String bed_no = result.getValue("BED_NO", 0);
        TParm bed = new TParm();
        bed.setData("BED_NO", bed_no);
        TParm bedRe = SYSBedTool.getInstance().queryAll(bed);
        return bedRe.getBoolean("ALLO_FLG", 0);
    }

    /**
     * ��Ժ�����ٻ� ��Ҫ������CASE_NO;OPT_USER;OPT_TERM;
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm returnAdm(TParm parm, TConnection conn) {
        TParm result = new TParm();
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        TParm admInfo = this.selectall(admParm);
        if (admInfo.getCount() <= 0) {
            result.setErr( -1, "���޴˲�����סԺ��Ϣ��");
            return result;
        }
        String caseNo = parm.getValue("CASE_NO");
        //��¼�ϴγ�Ժʱ��
        result = this.updateLastDsDate(caseNo, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //��ճ�Ժ�����ֶ� DS_DATE
        result = this.clearDsDate(caseNo, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //��������Ժ���� CHARGE_DATE
        result = this.clearChargeDate(caseNo, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //��ղ�����ҳ�в����ĳ�Ժʱ��  OUT_DATE
        result = MRORecordTool.getInstance().clearOUT_DATE(caseNo, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //д���ת�뵵
        TParm waitParm = new TParm();
        waitParm.setData("CASE_NO", caseNo);
        waitParm.setData("MR_NO", admInfo.getValue("MR_NO", 0));
        waitParm.setData("IPD_NO", admInfo.getValue("IPD_NO", 0));
        waitParm.setData("DEPT_CODE", admInfo.getValue("DS_DEPT_CODE", 0));
        waitParm.setData("STATION_CODE", admInfo.getValue("DS_STATION_CODE", 0));
        waitParm.setData("OPT_USER", parm.getValue("OPT_USER"));
        waitParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        result = ADMWaitTransTool.getInstance().saveForInp(waitParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        //�޸Ĵ�λ��Ϣ(Ŀǰֻд���ת���ٻص���λ��)
        //���´�λ�� ����
//        TParm bedParm = new TParm();
//        bedParm.setData("MR_NO",parm.getValue("MR_NO"));
//        bedParm.setData("CASE_NO",CASE_NO);
//        bedParm.setData("IPD_NO",admInfo.getValue("IPD_NO",0));
//        bedParm.setData("OPT_USER",parm.getValue("OPT_USER"));
//        bedParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
//        bedParm.setData("BED_NO",parm.getValue("BED_NO"));
//        bedParm.setData("APPT_FLG", "N");//ԤԼ���
//        bedParm.setData("ALLO_FLG", "Y");//��ס���
//        bedParm.setData("BED_STATUS", "0");
//        if (!"".equals(bedParm.getValue("BED_NO"))) {
//            // ���´�λ��
//            result = SYSBedTool.getInstance().upDate(bedParm, conn);
//            if (result.getErrCode() < 0) {
//                err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//                return result;
//            }
//        }
        //�޸�ADM_INP�Ĵ�λ�ֶ�
//        TParm inp_bed = new TParm();
//        inp_bed.setData("CASE_NO",CASE_NO);
//        inp_bed.setData("BED_NO",parm.getValue("BED_NO"));
//        result = this.updateBedNoForReturn(inp_bed,conn);
//        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//            return result;
//        }
        //����ADM_CHG������̬��
        TParm adm_chg = new TParm();
        //��ѯ���SEQ
        TParm seqQuery = new TParm();
        seqQuery.setData("CASE_NO", caseNo);
        TParm seqParm = ADMChgTool.getInstance().ADMQuerySeq(seqQuery);
        String seq = seqParm.getData("SEQ_NO", 0).toString();
        adm_chg.setData("CASE_NO", caseNo);
        adm_chg.setData("SEQ_NO", seq);
        adm_chg.setData("IPD_NO", admInfo.getValue("IPD_NO", 0));
        adm_chg.setData("MR_NO", admInfo.getValue("MR_NO", 0));
        adm_chg.setData("PSF_KIND", "DSCC"); //סԺ������Ժ�ٻ�
        adm_chg.setData("PSF_HOSP", "");
        adm_chg.setData("CANCEL_FLG", "N");
        adm_chg.setData("CANCEL_DATE", "");
        adm_chg.setData("CANCEL_USER", "");
        adm_chg.setData("DEPT_CODE", admInfo.getValue("DS_DEPT_CODE", 0));
        adm_chg.setData("STATION_CODE", admInfo.getValue("DS_STATION_CODE", 0));
        adm_chg.setData("BED_NO", ""); //�ٻص��� ���봲
        adm_chg.setData("VS_CODE_CODE", "");
        adm_chg.setData("ATTEND_DR_CODE", "");
        adm_chg.setData("DIRECTOR_DR_CODE", "");
        adm_chg.setData("OPT_USER", parm.getValue("OPT_USER"));
        adm_chg.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        //===========pangben modify 20110617
        adm_chg.setData("REGION_CODE", parm.getValue("REGION_CODE"));
        result = ADMChgTool.getInstance().insertAdmChg(adm_chg, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ʿִ�и�����������  ���¡�����ȼ����ֶ�(סԺ��ʿվʹ��)
     * @param parm TParm  ������NURSING_CLASS:����ȼ�  CASE_NO:�������
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateNURSING_CLASS(TParm parm, TConnection conn) {
        TParm result = this.update("updateNURSING_CLASS", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ʿִ�и�����������  ���¡�����״̬���ֶ�(סԺ��ʿվʹ��)
     * @param parm TParm  ������PATIENT_STATUS:����ȼ�  CASE_NO:�������
     * @param conn TConnection
     * @return TParm
     */
    public TParm updatePATIENT_STATUS(TParm parm, TConnection conn) {
        TParm result = this.update("updatePATIENT_STATUS", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��Ժ֪ͨ  ���¡�ת�����롱�͡�ҽ�Ƴ�Ժ���ڡ��ֶ�
     * @param parm TParm  ������DISCH_CODE��ת������    MEDDISCH_DATE��ҽ�Ƴ�Ժ����
     * @return TParm
     */
    public TParm updateMEDDISCH_DATE(TParm parm) {
        TParm result = this.update("updateMEDDISCH_DATE", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸���ɫͨ��ֵ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateGREENPATH_VALUE(TParm parm, TConnection conn) {
        TParm result = this.update("updateGREENPATH_VALUE", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸�ADM_INP���еĲ������ע��
     * @param parm TParm  ���������CASE_NO��MRO_CHAT_FLG��0��δ���   1�������   2�����
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateMRO_CHAT_FLG(TParm parm, TConnection conn) {
        TParm result = this.update("updateMRO_CHAT_FLG", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��������Ժ����(�����ٻ�)
     * @param CASE_NO String
     * @param conn TConnection
     * @return TParm
     */
    public TParm clearChargeDate(String CASE_NO, TConnection conn) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm result = this.update("clearCHARGE_DATE", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ������Ϣ����XML����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectForXML(TParm parm) {
        TParm result = this.query("selectForXML", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ��Ժ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInHosp(TParm parm) {
        TParm result = this.query("selectInHosp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݴ�λ�Ų�ѯ������Ϣ
     * @param bed_no String
     * @return TParm
     */
    public TParm selectRoomInfo(String bed_no) {
        String sql = "SELECT A.ROOM_CODE, A.ROOM_DESC, A.PY1, " +
                     " A.PY2, A.SEQ, A.DESCRIPT, " +
                     " A.STATION_CODE, A.REGION_CODE, A.SEX_LIMIT_FLG, " +
                     " A.RED_SIGN, A.YELLOW_SIGN, A.ENG_DESC " +
                     " FROM SYS_ROOM A,SYS_BED B " +
                     " WHERE A.ROOM_CODE=B.ROOM_CODE " +
                     " AND B.BED_NO='" + bed_no + "' ";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * ��Ժ���������ٻ� ��Ҫ������CASE_NO;OPT_USER;OPT_TERM;
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm returnAdmBill(TParm parm, TConnection conn) {
        TParm result = new TParm();
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        TParm admInfo = this.selectall(admParm);
        if (admInfo.getCount() <= 0) {
            result.setErr( -1, "���޴˲�����סԺ��Ϣ��");
            return result;
        }
        String CASE_NO = parm.getValue("CASE_NO");
        //��¼�ϴγ�Ժʱ��LAST_DS_DATE,���ǲ���ճ�Ժ�����ֶ� DS_DATE
        result = this.updateLastDsDate(CASE_NO, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //��������Ժ���� CHARGE_DATE
        result = this.clearChargeDate(CASE_NO, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //��ղ�����ҳ�в����ĳ�Ժʱ��  OUT_DATE
        result = MRORecordTool.getInstance().clearOUT_DATE(CASE_NO, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��¼�ϴγ�Ժʱ��
     * @param CASE_NO String
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateLastDsDate(String CASE_NO, TConnection conn) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm result = this.update("updateLAST_DS_DATE", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸��ʸ�ȷ������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateINPConfirmNo(TParm parm, TConnection conn) {
        TParm result = this.update("updateINPConfirmNo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯδ�᰸����
     * @param parm TParm
     * @return TParm
     */
    public TParm queryAdmNClose(TParm parm) {
        TParm result = query("queryAdmNClose", parm);
        return result;
    }
    /**
     * ���÷ָ��ѯ������Ϣȷ��Ψһ����
     * @param parm TParm
     * @return TParm
     * ========pangben 2012-6-18
     */
    public TParm queryAdmNCloseInsBalance(TParm parm) {
        TParm result = query("queryAdmNCloseInsBalance", parm);
        return result;
    }
    
    /**
     * ���»����ٴ�������Ϣ
     * @param parm
     * =====wukai 20160524
     */
    public boolean updatePatPro(TParm parm) {
    	if(this.update("updatePatPro", parm).getErrCode() < 0) {
    		return false;
    	}
    	return true;
    }
}
