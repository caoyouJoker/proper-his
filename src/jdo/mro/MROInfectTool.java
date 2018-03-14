package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import jdo.sys.SYSPostTool;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ��Ⱦ�����濨</p>
 *
 * <p>Description: ��Ⱦ�����濨</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-10-10
 * @version 1.0
 */
public class MROInfectTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static MROInfectTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MROInfectTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROInfectTool();
        return instanceObject;
    }

    public MROInfectTool() {
        this.setModuleName("mro\\MROInfectModule.x");
        this.onInit();
    }
    /**
     * ��ѯ��Ⱦ�����濨��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfect(TParm parm){
        TParm result = this.query("selectInfect",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���봫Ⱦ�����濨��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertInfect(TParm parm){
        TParm result = this.update("insertInfect",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸Ĵ�Ⱦ�����濨��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm updateInfect(TParm parm){
        TParm result = this.update("updateInfect",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���ò����Ƿ��Ѿ��д�Ⱦ�����濨��¼ (���ݲ����ź�CASE_NO��ѯ)
     * @param MR_NO String
     * @param CASE_NO String
     * @return TParm
     */
    public TParm checkInfectCount(TParm parm){
        TParm result = this.query("checkInfectCount",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��鲡���˴ο����Ƿ���ڴ�Ⱦ�����濨��¼
     * @param MR_NO String
     * @param CASE_NO String
     * @return boolean  true:����   false:������
     */
    public boolean checkHasInfect(TParm parm){
        TParm result = this.checkInfectCount(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        if(result.getInt("NUM",0)>0){
            return true;
        }
        return false;
    }
    /**
     * ��ȡĳһ����ĳһ�ο�����Ĵ�Ⱦ�����濨��������
     * @param MR_NO String
     * @param CASE_NO String
     * @return String
     */
    public int getMaxSEQ(String MR_NO,String CASE_NO){
        TParm parm = new TParm();
        parm.setData("MR_NO",MR_NO);
        parm.setData("CASE_NO",CASE_NO);
        TParm result = this.query("getMaxSEQ",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return 1;
        }
        int MAXSEQ = result.getInt("CARD_SEQ_NO",0) + 1;
        return MAXSEQ;
    }
    /**
     * ��ȡ��Ⱦ�����濨��ӡ����
     * @param MR_NO String  ������
     * @param CASE_NO String  �������
     * @param CARD_NO_SEQ String  ���濨���
     * @return TParm
     */
    public TParm getPrintData(String MR_NO,String CASE_NO,String CARD_SEQ_NO){
        TParm parm = new TParm();
        parm.setData("MR_NO",MR_NO);
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("CARD_SEQ_NO",CARD_SEQ_NO);
        TParm data = this.query("selectInfect",parm);
        if(data.getErrCode()<0){
            err("ERR:" + data.getErrCode() + data.getErrText() +
                data.getErrName());
            return data;
        }
        TParm result = new TParm();
        result.setData("CARD_NO","TEXT",data.getValue("CARD_NO",0));//��Ƭ���
        result.setData("FIRST_FLG","TEXT","0".equals(data.getValue("FIRST_FLG",0))?"���α���":"��������");//�������
        result.setData("PAT_NAME","TEXT",data.getValue("PAT_NAME",0));
        result.setData("GENEARCH_NAME","TEXT",data.getValue("GENEARCH_NAME",0));
        result.setData("IDNO", "TEXT", data.getValue("IDNO", 0));
        result.setData("SEX", "TEXT",
                       MROPrintTool.getInstance().getDesc("SYS_DICTIONARY",
            "SYS_SEX", "CHN_DESC", "ID", data.getValue("SEX", 0)));//�Ա�
        result.setData("BIRTH_DATE", "TEXT",StringTool.getString(data.getTimestamp("BIRTH_DATE", 0),"yyyy��MM��dd��"));
        //����ʵ������
        if(data.getTimestamp("BIRTH_DATE",0)!=null){
            result.setData("AGE","TEXT", StringUtil.showAge(data.getTimestamp("BIRTH_DATE", 0), data.getTimestamp("PAD_DATE",0)));
        }else{
            result.setData("AGE","TEXT","");
        }
        result.setData("OFFICE","TEXT",data.getValue("OFFICE",0));
        result.setData("CONT_TEL","TEXT",data.getValue("CONT_TEL",0));
        result.setData("SICK_ZONE_"+data.getValue("SICK_ZONE",0),"TEXT","��");//�������ڵ���
        //�����ʱ��ȡʡ����Ϣ
        TParm post = SYSPostTool.getInstance().getProvinceCity(data.getValue("ADDRESS_COUNTRY",0));
        result.setData("ADDRESS_PROVICE","TEXT",post.getValue("STATE",0));
        result.setData("ADDRESS_COUNTRY","TEXT",post.getValue("CITY",0));
        result.setData("ADDRESS_ROAD","TEXT",data.getValue("ADDRESS_ROAD",0));
        result.setData("ADDRESS_THORP","TEXT",data.getValue("ADDRESS_THORP",0));
        result.setData("DOORPLATE","TEXT",data.getValue("DOORPLATE",0));
        result.setData("INVALID_PROF_" + data.getValue("INVALID_PROF", 0),
                       "TEXT", "��"); //ְҵ
        result.setData("REST_PROF", "TEXT",
                       "17".equals(data.getValue("INVALID_PROF", 0)) ?
                       data.getValue("REST_PROF", 0) : "");//����ְҵ
        result.setData("DOUBT_CASE", "TEXT", "Y".equals(data.getValue("DOUBT_CASE", 0))?"��":"");
        result.setData("CLINIC_DIAGNOSE","TEXT","Y".equals(data.getValue("CLINIC_DIAGNOSE", 0))?"��":"");
        result.setData("LAB_DIAGNOSE","TEXT","Y".equals(data.getValue("LAB_DIAGNOSE", 0))?"��":"");
        result.setData("PATHOGENY_SCHLEP","TEXT","Y".equals(data.getValue("PATHOGENY_SCHLEP", 0))?"��":"");
        result.setData("VIRUS_TYPE_"+data.getValue("VIRUS_TYPE",0),"TEXT","��");//�ҸΡ�Ѫ���没����
        result.setData("ILLNESS_DATE","TEXT",StringTool.getString(data.getTimestamp("ILLNESS_DATE",0),"yyyy��MM��dd��"));//��������
        result.setData("COMFIRM_DATE","TEXT",StringTool.getString(data.getTimestamp("COMFIRM_DATE",0),"yyyy��MM��dd��"));//�������
        result.setData("DEAD_DATE","TEXT",StringTool.getString(data.getTimestamp("DEAD_DATE",0),"yyyy��MM��dd��"));//��������
        result.setData("PLAGUE_SPOT","TEXT","Y".equals(data.getValue("PLAGUE_SPOT",0))?"��":"");//����
        result.setData("CHOLERA_FLG","TEXT","Y".equals(data.getValue("CHOLERA_FLG",0))?"��":"");//����
        result.setData("SARS_FLG","TEXT","Y".equals(data.getValue("SARS_FLG",0))?"��":"");//��Ⱦ�Էǵ��ͷ���
        result.setData("AIDS_FLG","TEXT","Y".equals(data.getValue("AIDS_FLG",0))?"��":"");//���̲�
        result.setData("VIRUS_HEPATITIS_"+data.getValue("VIRUS_HEPATITIS",0),"TEXT","��");//�����Ը���
        result.setData("POLIOMYELITIS_FLG","TEXT","Y".equals(data.getValue("POLIOMYELITIS_FLG",0))?"��":"");//���������
        result.setData("HIGH_FLU","TEXT","Y".equals(data.getValue("HIGH_FLU",0))?"��":"");//�˸�Ⱦ���²���������
        result.setData("HIVES_FLG","TEXT","Y".equals(data.getValue("HIVES_FLG",0))?"��":"");//����
        result.setData("EPIDEMIC_BLOOD","TEXT","Y".equals(data.getValue("EPIDEMIC_BLOOD",0))?"��":"");//�����Գ�Ѫ��
        result.setData("LYSSA","TEXT","Y".equals(data.getValue("LYSSA",0))?"��":"");//��Ȯ��
        result.setData("EPIDEMIC_HEPATITIS","TEXT","Y".equals(data.getValue("EPIDEMIC_HEPATITIS",0))?"��":"");//��������������
        result.setData("DENGUE","TEXT","Y".equals(data.getValue("DENGUE",0))?"��":"");//�Ǹ���
        result.setData("CHARCOAL_"+data.getValue("CHARCOAL",0),"TEXT","��");//̿��
        result.setData("DIARRHEA_"+data.getValue("DIARRHEA",0),"TEXT","��");//����
        result.setData("PHTHISIC_"+data.getValue("PHTHISIC",0),"TEXT","��");//�ν��
        result.setData("TYPHOID_"+data.getValue("TYPHOID",0),"TEXT","��");//�˺�
        result.setData("EPIDEMIC_CEPHALITIS","TEXT","Y".equals(data.getValue("EPIDEMIC_CEPHALITIS",0))?"��":"");//�������Լ���Ĥ��
        result.setData("CHINCOUGH","TEXT","Y".equals(data.getValue("CHINCOUGH",0))?"��":"");//���տ�
        result.setData("DIPHTHERIA","TEXT","Y".equals(data.getValue("DIPHTHERIA",0))?"��":"");//�׺�
        result.setData("NEW_LOCKJAW","TEXT","Y".equals(data.getValue("NEW_LOCKJAW",0))?"��":"");//���������˷�
        result.setData("SCARLATINA","TEXT","Y".equals(data.getValue("SCARLATINA",0))?"��":"");//�ɺ���
        result.setData("BRUCE_DISEASE","TEXT","Y".equals(data.getValue("BRUCE_DISEASE",0))?"��":"");//��³�Ͼ���
        result.setData("GONORRHEA","TEXT","Y".equals(data.getValue("GONORRHEA",0))?"��":"");//�ܲ�
        result.setData("LUES_"+data.getValue("LUES",0),"TEXT","��");//÷��
        result.setData("CATCH_LEPTOSPIRA","TEXT","Y".equals(data.getValue("CATCH_LEPTOSPIRA",0))?"��":"");//���������岡
        result.setData("SCHISTOSOMIASIS_FLG","TEXT","Y".equals(data.getValue("SCHISTOSOMIASIS_FLG",0))?"��":"");//Ѫ���没
        result.setData("AGUE_"+data.getValue("AGUE",0),"TEXT","��");//ű��
        result.setData("GRIPPE_FLG","TEXT","Y".equals(data.getValue("GRIPPE_FLG",0))?"��":"");//�����Ը�ð
        result.setData("MUMPS","TEXT","Y".equals(data.getValue("MUMPS",0))?"��":"");//������������
        result.setData("MEASLES","TEXT","Y".equals(data.getValue("MEASLES",0))?"��":"");//����
        result.setData("ACUTE_CONJUNCTIVITIS","TEXT","Y".equals(data.getValue("ACUTE_CONJUNCTIVITIS",0))?"��":"");//���Գ�Ѫ�Խ�Ĥ��
        result.setData("LEPRA","TEXT","Y".equals(data.getValue("LEPRA",0))?"��":"");//��粡
        result.setData("SHIP_FEVER","TEXT","Y".equals(data.getValue("SHIP_FEVER",0))?"��":"");//�����Ժ͵ط��԰����˺�
        result.setData("KALA_AZAR","TEXT","Y".equals(data.getValue("KALA_AZAR",0))?"��":"");//���Ȳ�
        result.setData("ECHINOCOCCOSIS","TEXT","Y".equals(data.getValue("ECHINOCOCCOSIS",0))?"��":"");//���没
        result.setData("FILARIASIS","TEXT","Y".equals(data.getValue("FILARIASIS",0))?"��":"");//˿�没
        result.setData("EXPECT_CHOLERA","TEXT","Y".equals(data.getValue("EXPECT_CHOLERA",0))?"��":"");//�����ң�ϸ���ԺͰ��װ����������˺��͸��˺�����ĸ�Ⱦ�Ը�к
        result.setData("REST_INFECTION","TEXT",data.getValue("REST_INFECTION",0));//�������������Լ��ص��⴫Ⱦ��
        result.setData("REMARK","TEXT",data.getValue("REMARK",0));//��ע
        result.setData("REVISALILLNESS_NAME","TEXT",data.getValue("REVISALILLNESS_NAME",0));//��������
        result.setData("COUNTERMAND_REAS","TEXT",data.getValue("COUNTERMAND_REAS",0));//�˿�ԭ��
        result.setData("REPORT_UNIT","TEXT",data.getValue("REPORT_UNIT",0));//���浥λ
        result.setData("CONT_TEL2","TEXT",data.getValue("CONT_TEL2",0));//��ϵ�绰2
        result.setData("SPEAKER","TEXT",data.getValue("SPEAKER",0));//����ҽ��
        result.setData("PAD_DEPT","TEXT",MROPrintTool.getInstance().getDesc("SYS_DEPT","","DEPT_CHN_DESC","DEPT_CODE",data.getValue("PAD_DEPT",0)));//�������
        result.setData("ICD_DESC","TEXT",getICD_DESC(data.getValue("ICD_CODE",0)));//ԭ���
        result.setData("PAD_DATE","TEXT",StringTool.getString(data.getTimestamp("PAD_DATE",0),"yyyy��MM��dd��"));//�ʱ��
        return result;
    }
    /**
     * ICD_CODE��ȡ����
     * @param s String
     * @return String
     */
    public String getICD_DESC(String s) {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
        if (dataStore == null)
            return s;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
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
    /**
     * ɾ����Ⱦ�����濨����
     * @return TParm
     */
    public TParm delInfect(String MR_NO,String CASE_NO,String CARD_SEQ_NO){
        TParm parm = new TParm();
        parm.setData("MR_NO",MR_NO);
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("CARD_SEQ_NO",CARD_SEQ_NO);
        TParm result = this.update("delInfect",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
