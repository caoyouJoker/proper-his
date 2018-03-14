package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.mro.MROInfectTool;
import jdo.sys.PatTool;
import jdo.sys.SYSDiagnosisTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

import java.sql.Timestamp;

/**
 * <p>Title: ��Ⱦ�����濨</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-10-12
 * @version 1.0
 */
public class MROInfectControl
    extends TControl {
    private String MR_NO = "";
    private String CASE_NO = "";
    private String CARD_SEQ_NO = "";
    private String IPD_NO = "";
    private String CARD_NO = "";
    private String SAVE_FLG = "NEW";//NEW:�½�   UPDATE:�޸�
    private String OPEN_TYPE = "";//�������� ����Ǳ�ҽ��վ���õ� OPEN_TYPE="DR"
    private String ADM_TYPE = "";//�ż�ס��
    public void onInit(){
        //ֻ��text���������������ICD10������
        callFunction("UI|ICD_CODE|setPopupMenuParameter", "aaa",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        //textfield���ܻش�ֵ
        callFunction("UI|ICD_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        pageInit();
    }
    /**
     * ҳ���ʼ��
     */
    private void pageInit(){
        /**
         * ���ò���
         * MR_NO
         * CASE_NO
         * ICD_CODE ���CODE
         * DEPT_CODE �����
         * USER_NAME �ҽʦ
         */
//        TParm can = new TParm();
//        can.setData("MR_NO","000000000091");
//        can.setData("CASE_NO","123456789012");
//        can.setData("ICD_CODE","A00");
//        can.setData("DEPT_CODE","10101");
//        can.setData("USER_NAME","admin");
//        can.setData("ADM_TYPE","O");
//        Object obj = can;
        Object obj = this.getParameter();
        if(obj==null){
            return;
        }
        TParm parm = new TParm();
        if(obj instanceof TParm){
            parm = (TParm)obj;
            MR_NO = parm.getValue("MR_NO");
            CASE_NO = parm.getValue("CASE_NO");
            IPD_NO = parm.getValue("IPD_NO");
            ADM_TYPE = parm.getValue("ADM_TYPE");
            OPEN_TYPE = "DR";//��ʾ�Ǳ�����ҽ��վ���õ�
            //�����ҽ��վ���� ���ز�ѯ��ť
            this.callFunction("UI|query|setVisible",false);
            hasInfectData("M");//�жϲ����Ƿ���ڴ�Ⱦ������¼
            if(MR_NO.length()<=0){
                this.closeWindow();
                return;
            }
            if("NEW".equals(SAVE_FLG))//�½����ݵ�ʱ��Ż���ʾҽ��վ���������CODE
            {
                this.setValue("FIRST_FLG","0");
                //ȡ���������
                CARD_SEQ_NO = MROInfectTool.getInstance().getMaxSEQ(MR_NO,CASE_NO)+"";
                this.setValue("CARD_SEQ_NO",CARD_SEQ_NO);
                String ICD_CODE = parm.getValue("ICD_CODE");
                this.setValue("ICD_CODE", ICD_CODE);
                TParm icd = SYSDiagnosisTool.getInstance().selectDataWithCode(ICD_CODE);
                this.setValue("DISEASETYPE_CODE",icd.getValue("DISEASETYPE_CODE",0));
                this.setValue("ICD_DESC",icd.getValue("ICD_CHN_DESC",0));
                //��ʼ��������Ϣ
                Pat pat = Pat.onQueryByMrNo(MR_NO);
                this.setValue("MR_NO",pat.getMrNo());
                this.callFunction("UI|MR_NO|setEnabled",false);
                this.setValue("PAT_NAME",pat.getName());//����
                this.setValue("IDNO",pat.getIdNo());//���֤
                this.setValue("BIRTH_DATE",pat.getBirthday());
                if(pat.getBirthday()!=null){
                    String age[] = StringTool.CountAgeByTimestamp(pat.
                        getBirthday(), SystemTool.getInstance().getDate());
                    this.setValue("AGE",
                                  age[0] + "��" + age[1] + "��" + age[2] + "��");
                }
                else {
                    this.setValue("AGE", "");
                }
                this.setValue("SEX",pat.getSexCode());
                this.setValue("CONT_TEL",pat.getCellPhone());
                this.setValue("OFFICE",pat.getCompanyDesc());
                this.setValue("PAD_DEPT",parm.getValue("DEPT_CODE"));//ҽ��վ������ �����
                this.setValue("SPEAKER",parm.getValue("USER_NAME"));//ҽ��վ������ �ҽʦ
                this.setValue("PAD_DATE",SystemTool.getInstance().getDate());
            }
        }
    }
    /**
     * �жϴ˲��� �ڱ��ο����ڼ��Ƿ��Ѿ��д�Ⱦ�����濨������
     * ����� ������ѯ���� ����ѡ��ԭ�еı��濨���ݽ����޸�
     * û�� ���½���Ⱦ�����濨
     * @param TYPE String �������� H:����ʷ��ѯ����ť����  M:��ָ���������ڶ�����Ⱦ����¼ʱ�Զ�����
     */
    private void hasInfectData(String TYPE){
        boolean flg;
        //����Ǹ���ҽ��վ���õ� ��ô��ѯָ�����ߵ���ʷ��Ϣ
        //if("DR".equals(OPEN_TYPE)){ �ִ�Ⱦ�����濨����סԺҽ����д����ʱ��ע�͵�����
            TParm check = new TParm();
            check.setData("MR_NO", MR_NO);
            if (CASE_NO.trim().length() > 0) {
                check.setData("CASE_NO", CASE_NO);
            }
            flg = MROInfectTool.getInstance().checkHasInfect(check);
//        }else//����ҽ��վ���� ���Բ�ѯ���в��������д�Ⱦ�����濨����Ϣ
//            flg = true;
        this.setValue("FIRST_FLG","0");//�������Ĭ��Ϊ���α���
        if(flg){//������ʷ��Ϣ
            //������ʷ��ѯ����
            TParm parm = new TParm();
            parm.setData("MR_NO",MR_NO);
            if(CASE_NO.trim().length()>0)
                parm.setData("CASE_NO",CASE_NO);
            parm.setData("TYPE",TYPE);//��ʶ����״̬  �����߱��ξ����Ѿ�������д���������
            Object obj = this.openDialog("%ROOT%/config/mro/MROInfectQuery.x",parm);
            if(obj==null){
                //������ؿ�ֵ  ��ô��ս���
                this.onClear();
                return;
            }
            TParm reParm = (TParm)obj;
            SAVE_FLG = reParm.getValue("SAVE_FLG");
            if("UPDATE".equals(SAVE_FLG)){//�޸�ԭ������
                CARD_SEQ_NO = reParm.getValue("CARD_SEQ_NO");
                MR_NO = reParm.getValue("MR_NO");
                CASE_NO = reParm.getValue("CASE_NO");

                TParm selectParm = new TParm();
                selectParm.setData("MR_NO", MR_NO);
                selectParm.setData("CASE_NO", CASE_NO);
                selectParm.setData("CARD_SEQ_NO", CARD_SEQ_NO);
                TParm result = MROInfectTool.getInstance().selectInfect(
                    selectParm);
                this.setDataValue(result);
                this.callFunction("UI|MR_NO|setEnabled",false);
                this.setValue("FIRST_FLG","1");//������޸� ��ô�������Ĭ��Ϊ ����
            }
        }
    }
    /**
     * ���
     */
    public void onClear() {
        this.clearValue("MR_NO;PAT_NAME;GENEARCH_NAME;FIRST_FLG;CARD_NO;CARD_SEQ_NO;IDNO;BIRTH_DATE");
        this.clearValue("AGE;SEX;CONT_TEL;PAD_DATE;OFFICE;ADDRESS_PROVICE;ADDRESS_COUNTRY;ADDRESS_ROAD");
        this.clearValue("ADDRESS_THORP;DOORPLATE;ILLNESS_DATE;DEAD_DATE;COMFIRM_DATE;ICD_CODE;ICD_DESC;DISEASETYPE_CODE");
        this.clearValue("REST_PROF;REST_INFECTION;REMARK;REVISALILLNESS_NAME;COUNTERMAND_REAS;REPORT_UNIT;CONT_TEL2;SPEAKER;PAD_DEPT");
        this.clearValue("DOUBT_CASE;CLINIC_DIAGNOSE;LAB_DIAGNOSE;PATHOGENY_SCHLEP;PLAGUE_SPOT;CHOLERA_FLG;");
        this.clearValue("SARS_FLG;AIDS_FLG;POLIOMYELITIS_FLG;HIGH_FLU;EPIDEMIC_CEPHALITIS;DENGUE;LYSSA;EPIDEMIC_HEPATITIS");
        this.clearValue("HIVES_FLG;EPIDEMIC_BLOOD;SCARLATINA;BRUCE_DISEASE;GONORRHEA;CHINCOUGH;DIPHTHERIA;NEW_LOCKJAW;CATCH_LEPTOSPIRA;SCHISTOSOMIASIS_FLG");
        this.clearValue("GRIPPE_FLG;MUMPS;MEASLES;ACUTE_CONJUNCTIVITIS;LEPRA;SHIP_FEVER;KALA_AZAR;ECHINOCOCCOSIS;FILARIASIS;EXPECT_CHOLERA");
        this.setValue("SICK_ZONE_0","Y");
        this.setValue("INVALID_PROF_16","Y");
        this.setValue("VIRUS_TYPE_2","Y");
        this.setValue("VIRUS_HEPATITIS_5","Y");
        this.setValue("TYPHOID_2","Y");
        this.setValue("CHARCOAL_3","Y");
        this.setValue("DIARRHEA_2","Y");
        this.setValue("PHTHISIC_4","Y");
        this.setValue("LUES_5","Y");
        this.setValue("AGUE_3","Y");
        SAVE_FLG = "NEW";
        CASE_NO = "";
        MR_NO = "";
        CARD_SEQ_NO = "";
        CARD_NO="";
        IPD_NO = "";
        OPEN_TYPE = "";
        ADM_TYPE = "";
        this.callFunction("UI|MR_NO|setEnabled",true);
    }
    /**
     * ����
     */
    public void onSave(){
        if(!checkData()){
            return;
        }
        String type = this.getValueString("FIRST_FLG");
        //����ǳ��α��� ��ô��������
        if("0".equals(type)){
            //�ж��Ƿ���ڴ�������,��������Ҫ���������� ��ֹ�����ظ�
            if(checkNew()){
                CARD_SEQ_NO = MROInfectTool.getInstance().getMaxSEQ(MR_NO,CASE_NO)+"";
                this.setValue("CARD_SEQ_NO",CARD_SEQ_NO);
            }
            insertData();
        }
        else{//����Ƕ������� ��ô���޸�����
            //�ж��Ƿ���ڴ�������,����������޸�
            if(checkNew())
                updateData();
            else
                this.messageBox_("�뽫�񱨿����ѡ��Ϊ�����α�����");
        }
    }
    /**
     * �����µı��濨��Ϣ
     */
    private void insertData(){
        //���CASE_NO�����ڲ�������  ֻ�е�����ҽ��վ���õ�ʱ��Żᴫ��CASE_NO ֱ�ӵ��ñ��濨ҳ��ʱ���ܹ�����
        if("".equals(CASE_NO)){
            return;
        }
        TParm parm = this.getDataValue();//��ȡҳ�������
        TParm result = MROInfectTool.getInstance().insertInfect(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        SAVE_FLG = "UPDATE";
        this.messageBox("P0005");
    }
    /**
     * �޸ı��濨��Ϣ
     */
    private void updateData(){
        TParm parm = this.getDataValue();//��ȡҳ�������
        TParm result = MROInfectTool.getInstance().updateInfect(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        this.messageBox("P0005");
    }
    /**
     * ���Ҫ�޸ĵĴ�Ⱦ��������Ϣ�Ƿ����
     * @return boolean
     */
    private boolean checkNew(){
        TParm parm = new TParm();
        parm.setData("MR_NO",this.getValue("MR_NO"));
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("CARD_SEQ_NO",this.getValue("CARD_SEQ_NO"));
        TParm result = MROInfectTool.getInstance().selectInfect(parm);
        if(result.getCount()>0){
            return true;
        }else
            return false;
    }
    /**
     * �����Żس��¼�
     */
    public void onMRNO(){
        SAVE_FLG = "NEW";
        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        if(pat==null){
            this.messageBox_("���޴˲���!");
            return;
        }
     // modify by huangtt 20160929 EMPI���߲�����ʾ start
        MR_NO = PatTool.getInstance().checkMrno(getValueString("MR_NO")); 
        this.setValue("MR_NO",MR_NO);
        if (!StringUtil.isNullString(MR_NO) && !MR_NO.equals(pat.getMrNo())) {
			this.messageBox("������" + MR_NO + " �Ѻϲ��� " + "" + pat.getMrNo());
			MR_NO= pat.getMrNo();
			setValue("MR_NO", MR_NO);
		}	
     // modify by huangtt 20160929 EMPI���߲�����ʾ end 
        
        hasInfectData("H");
        if("NEW".equals(SAVE_FLG)){
            this.onClear();
        }
        //2013-04-09 zhangh ������벡���Żس����Զ�������Ϣ����
        this.setValue("MR_NO",pat.getMrNo());
        this.setValue("PAT_NAME",pat.getName());//����
        this.setValue("IDNO",pat.getIdNo());//���֤
        this.setValue("BIRTH_DATE",pat.getBirthday());
        if(pat.getBirthday()!=null){
            String age[] = StringTool.CountAgeByTimestamp(pat.
                getBirthday(), SystemTool.getInstance().getDate());
            this.setValue("AGE",
                          age[0] + "��" + age[1] + "��" + age[2] + "��");
        }
        else {
            this.setValue("AGE", "");
        }
        this.setValue("SEX",pat.getSexCode());
        if(null == pat.getCellPhone())
        	this.setValue("CONT_TEL",pat.getCellPhone());
        else
        	this.setValue("CONT_TEL",pat.getTelHome());
        this.setValue("OFFICE",pat.getCompanyDesc());
        this.setValue("PAD_DATE",SystemTool.getInstance().getDate());
        this.setValue("ADDRESS_PROVICE",pat.getPostCode().substring(0, 2));
        this.setValue("ADDRESS_COUNTRY",pat.getPostCode());
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        onMRNO();
    }
    /**
     * ҳ��ؼ���ֵ
     */
    private void setDataValue(TParm parm){
        ADM_TYPE = parm.getValue("ADM_TYPE",0);
        this.setValue("MR_NO",parm.getValue("MR_NO",0));
        this.setValue("PAT_NAME",parm.getValue("PAT_NAME",0));
        this.setValue("GENEARCH_NAME",parm.getValue("GENEARCH_NAME",0));
        this.setValue("FIRST_FLG",parm.getValue("FIRST_FLG",0));
        this.setValue("CARD_NO",parm.getValue("CARD_NO",0));
        this.setValue("CARD_SEQ_NO",parm.getValue("CARD_SEQ_NO",0));
        this.setValue("IDNO",parm.getValue("IDNO",0));
        this.setValue("BIRTH_DATE",parm.getTimestamp("BIRTH_DATE",0));
        if(parm.getTimestamp("BIRTH_DATE",0)!=null){
            String age[] = StringTool.CountAgeByTimestamp(parm.getTimestamp(
                "BIRTH_DATE", 0), SystemTool.getInstance().getDate());
            this.setValue("AGE", age[0] + "��" + age[1] + "��" + age[2] + "��");
        }else{
            this.setValue("AGE","");
        }
        this.setValue("SEX",parm.getValue("SEX",0));
        this.setValue("CONT_TEL",parm.getValue("CONT_TEL",0));
        this.setValue("PAD_DATE",parm.getTimestamp("PAD_DATE",0));
        this.setValue("OFFICE",parm.getValue("OFFICE",0));
        this.setValue("SICK_ZONE_"+parm.getValue("SICK_ZONE",0),"Y");
        this.setValue("ADDRESS_PROVICE",parm.getValue("ADDRESS_PROVICE",0));
        this.setValue("ADDRESS_COUNTRY",parm.getValue("ADDRESS_COUNTRY",0));
        this.setValue("ADDRESS_ROAD",parm.getValue("ADDRESS_ROAD",0));
        this.setValue("ADDRESS_THORP",parm.getValue("ADDRESS_THORP",0));
        this.setValue("DOORPLATE",parm.getValue("DOORPLATE",0));
        this.setValue("ILLNESS_DATE",parm.getTimestamp("ILLNESS_DATE",0));
        this.setValue("DEAD_DATE",parm.getTimestamp("DEAD_DATE",0));
        this.setValue("COMFIRM_DATE",parm.getTimestamp("COMFIRM_DATE",0));
        this.setValue("INVALID_PROF_"+parm.getValue("INVALID_PROF",0),"Y");
        this.setValue("REST_PROF",parm.getValue("REST_PROF",0));
        this.setValue("DOUBT_CASE",parm.getValue("DOUBT_CASE",0));
        this.setValue("CLINIC_DIAGNOSE",parm.getValue("CLINIC_DIAGNOSE",0));
        this.setValue("LAB_DIAGNOSE",parm.getValue("LAB_DIAGNOSE",0));
        this.setValue("PATHOGENY_SCHLEP",parm.getValue("PATHOGENY_SCHLEP",0));
        this.setValue("PLAGUE_SPOT",parm.getValue("PLAGUE_SPOT",0));
        this.setValue("CHOLERA_FLG",parm.getValue("CHOLERA_FLG",0));
        this.setValue("VIRUS_TYPE_"+parm.getValue("",0),"Y");
        this.setValue("SARS_FLG",parm.getValue("SARS_FLG",0));
        this.setValue("AIDS_FLG",parm.getValue("AIDS_FLG",0));
        this.setValue("VIRUS_HEPATITIS_"+parm.getValue("",0),"Y");
        this.setValue("POLIOMYELITIS_FLG",parm.getValue("POLIOMYELITIS_FLG",0));
        this.setValue("HIGH_FLU",parm.getValue("HIGH_FLU",0));
        this.setValue("TYPHOID_"+parm.getValue("",0),"Y");
        this.setValue("EPIDEMIC_CEPHALITIS",parm.getValue("EPIDEMIC_CEPHALITIS",0));
        this.setValue("DENGUE",parm.getValue("DENGUE",0));
        this.setValue("CHARCOAL_"+parm.getValue("CHARCOAL",0),"Y");
        this.setValue("DIARRHEA_"+parm.getValue("DIARRHEA",0),"Y");
        this.setValue("PHTHISIC_"+parm.getValue("PHTHISIC",0),"Y");
        this.setValue("LYSSA",parm.getValue("LYSSA",0));
        this.setValue("EPIDEMIC_HEPATITIS",parm.getValue("EPIDEMIC_HEPATITIS",0));
        this.setValue("HIVES_FLG",parm.getValue("HIVES_FLG",0));
        this.setValue("EPIDEMIC_BLOOD",parm.getValue("EPIDEMIC_BLOOD",0));
        this.setValue("SCARLATINA",parm.getValue("SCARLATINA",0));
        this.setValue("BRUCE_DISEASE",parm.getValue("BRUCE_DISEASE",0));
        this.setValue("GONORRHEA",parm.getValue("GONORRHEA",0));
        this.setValue("LUES_"+parm.getValue("LUES",0),"Y");
        this.setValue("CHINCOUGH",parm.getValue("CHINCOUGH",0));
        this.setValue("DIPHTHERIA",parm.getValue("DIPHTHERIA",0));
        this.setValue("NEW_LOCKJAW",parm.getValue("NEW_LOCKJAW",0));
        this.setValue("CATCH_LEPTOSPIRA",parm.getValue("CATCH_LEPTOSPIRA",0));
        this.setValue("SCHISTOSOMIASIS_FLG",parm.getValue("SCHISTOSOMIASIS_FLG",0));
        this.setValue("AGUE_"+parm.getValue("",0),"Y");
        this.setValue("GRIPPE_FLG",parm.getValue("GRIPPE_FLG",0));
        this.setValue("MUMPS",parm.getValue("MUMPS",0));
        this.setValue("MEASLES",parm.getValue("MEASLES",0));
        this.setValue("ACUTE_CONJUNCTIVITIS",parm.getValue("ACUTE_CONJUNCTIVITIS",0));
        this.setValue("LEPRA",parm.getValue("LEPRA",0));
        this.setValue("SHIP_FEVER",parm.getValue("SHIP_FEVER",0));
        this.setValue("KALA_AZAR",parm.getValue("KALA_AZAR",0));
        this.setValue("ECHINOCOCCOSIS",parm.getValue("ECHINOCOCCOSIS",0));
        this.setValue("FILARIASIS",parm.getValue("FILARIASIS",0));
        this.setValue("EXPECT_CHOLERA",parm.getValue("EXPECT_CHOLERA",0));
        this.setValue("REST_INFECTION",parm.getValue("REST_INFECTION",0));
        this.setValue("REMARK", parm.getValue("REMARK", 0));
        this.setValue("REVISALILLNESS_NAME", parm.getValue("REVISALILLNESS_NAME", 0));
        this.setValue("COUNTERMAND_REAS", parm.getValue("COUNTERMAND_REAS", 0));
        this.setValue("REPORT_UNIT", parm.getValue("REPORT_UNIT", 0));
        this.setValue("CONT_TEL2", parm.getValue("CONT_TEL2", 0));
        this.setValue("SPEAKER", parm.getValue("SPEAKER", 0));
        this.setValue("PAD_DEPT", parm.getValue("PAD_DEPT", 0));
        String ICD_CODE = parm.getValue("ICD_CODE",0);
        this.setValue("ICD_CODE", ICD_CODE);
        TParm icd = SYSDiagnosisTool.getInstance().selectDataWithCode(ICD_CODE);
        this.setValue("DISEASETYPE_CODE", icd.getValue("DISEASETYPE_CODE", 0));
        this.setValue("ICD_DESC", icd.getValue("ICD_CHN_DESC", 0));
        this.setValue("DISEASETYPE_CODE",parm.getValue("DISEASETYPE_CODE",0));
    }
    /**
     * ��ȡҳ������
     */
    private TParm getDataValue(){
        TParm parm = new TParm();
        parm.setData("MR_NO",this.getValue("MR_NO"));
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("CARD_SEQ_NO",this.getValue("CARD_SEQ_NO"));
        parm.setData("IPD_NO",IPD_NO);
        parm.setData("CARD_NO",this.getValue("CARD_NO"));
        parm.setData("FIRST_FLG",this.getValue("FIRST_FLG"));
        parm.setData("ADM_TYPE",ADM_TYPE);
        parm.setData("PAT_NAME",this.getValue("PAT_NAME"));
        parm.setData("GENEARCH_NAME",this.getValue("GENEARCH_NAME"));
        parm.setData("IDNO",this.getValue("IDNO"));
        parm.setData("SEX",this.getValue("SEX"));
        parm.setData("BIRTH_DATE",this.getValue("BIRTH_DATE")==null?"":this.getValue("BIRTH_DATE"));
        parm.setData("OFFICE",this.getValue("OFFICE"));
        parm.setData("CONT_TEL",this.getValue("CONT_TEL"));
        if(this.getRadioSelected("SICK_ZONE_0"))
            parm.setData("SICK_ZONE","0");
        else if(this.getRadioSelected("SICK_ZONE_1"))
            parm.setData("SICK_ZONE","1");
        else if(this.getRadioSelected("SICK_ZONE_2"))
            parm.setData("SICK_ZONE","2");
        else if(this.getRadioSelected("SICK_ZONE_3"))
            parm.setData("SICK_ZONE","3");
        else if(this.getRadioSelected("SICK_ZONE_4"))
            parm.setData("SICK_ZONE","4");
        parm.setData("ADDRESS_PROVICE",this.getValueString("ADDRESS_PROVICE"));
        parm.setData("ADDRESS_COUNTRY",this.getValueString("ADDRESS_COUNTRY"));
        parm.setData("ADDRESS_ROAD",this.getValue("ADDRESS_ROAD"));
        parm.setData("ADDRESS_THORP",this.getValue("ADDRESS_THORP"));
        parm.setData("DOORPLATE",this.getValue("DOORPLATE"));
        if(this.getRadioSelected("INVALID_PROF_0"))
            parm.setData("INVALID_PROF","0");
        else if(this.getRadioSelected("INVALID_PROF_1"))
            parm.setData("INVALID_PROF","1");
        else if(this.getRadioSelected("INVALID_PROF_2"))
            parm.setData("INVALID_PROF","2");
        else if(this.getRadioSelected("INVALID_PROF_3"))
            parm.setData("INVALID_PROF","3");
        else if(this.getRadioSelected("INVALID_PROF_4"))
            parm.setData("INVALID_PROF","4");
        else if(this.getRadioSelected("INVALID_PROF_5"))
            parm.setData("INVALID_PROF","5");
        else if(this.getRadioSelected("INVALID_PROF_6"))
            parm.setData("INVALID_PROF","6");
        else if(this.getRadioSelected("INVALID_PROF_7"))
            parm.setData("INVALID_PROF","7");
        else if(this.getRadioSelected("INVALID_PROF_8"))
            parm.setData("INVALID_PROF","8");
        else if(this.getRadioSelected("INVALID_PROF_9"))
            parm.setData("INVALID_PROF","9");
        else if(this.getRadioSelected("INVALID_PROF_10"))
            parm.setData("INVALID_PROF","10");
        else if(this.getRadioSelected("INVALID_PROF_11"))
            parm.setData("INVALID_PROF","11");
        else if(this.getRadioSelected("INVALID_PROF_12"))
            parm.setData("INVALID_PROF","12");
        else if(this.getRadioSelected("INVALID_PROF_13"))
            parm.setData("INVALID_PROF","13");
        else if(this.getRadioSelected("INVALID_PROF_14"))
            parm.setData("INVALID_PROF","14");
        else if(this.getRadioSelected("INVALID_PROF_15"))
            parm.setData("INVALID_PROF","15");
        else if(this.getRadioSelected("INVALID_PROF_16"))
            parm.setData("INVALID_PROF","16");
        else if(this.getRadioSelected("INVALID_PROF_17"))
            parm.setData("INVALID_PROF","17");
        parm.setData("REST_PROF",this.getValue("REST_PROF"));
        parm.setData("DOUBT_CASE",this.getCheckBoxSelected("DOUBT_CASE"));
        parm.setData("CLINIC_DIAGNOSE",this.getCheckBoxSelected("CLINIC_DIAGNOSE"));
        parm.setData("LAB_DIAGNOSE",this.getCheckBoxSelected("LAB_DIAGNOSE"));
        parm.setData("PATHOGENY_SCHLEP",this.getCheckBoxSelected("PATHOGENY_SCHLEP"));
        parm.setData("ILLNESS_DATE",this.getValue("ILLNESS_DATE")==null?"":this.getValue("ILLNESS_DATE"));
        parm.setData("COMFIRM_DATE",this.getValue("COMFIRM_DATE")==null?"":this.getValue("COMFIRM_DATE"));
        parm.setData("DEAD_DATE",this.getValue("DEAD_DATE")==null?"":this.getValue("DEAD_DATE"));
        parm.setData("PLAGUE_SPOT",this.getCheckBoxSelected("PLAGUE_SPOT"));
        parm.setData("CHOLERA_FLG",this.getCheckBoxSelected("CHOLERA_FLG"));
        parm.setData("SARS_FLG",this.getCheckBoxSelected("SARS_FLG"));
        parm.setData("AIDS_FLG",this.getCheckBoxSelected("AIDS_FLG"));
        if(this.getRadioSelected("VIRUS_HEPATITIS_0"))
            parm.setData("VIRUS_HEPATITIS","0");
        else if(this.getRadioSelected("VIRUS_HEPATITIS_1"))
            parm.setData("VIRUS_HEPATITIS","1");
        else if(this.getRadioSelected("VIRUS_HEPATITIS_2"))
            parm.setData("VIRUS_HEPATITIS","2");
        else if(this.getRadioSelected("VIRUS_HEPATITIS_3"))
            parm.setData("VIRUS_HEPATITIS","3");
        else if(this.getRadioSelected("VIRUS_HEPATITIS_4"))
            parm.setData("VIRUS_HEPATITIS","4");
        else if(this.getRadioSelected("VIRUS_HEPATITIS_5"))
            parm.setData("VIRUS_HEPATITIS","5");
        if(this.getRadioSelected("VIRUS_TYPE_0"))
            parm.setData("VIRUS_TYPE","0");
        else if(this.getRadioSelected("VIRUS_TYPE_1"))
            parm.setData("VIRUS_TYPE","1");
        else if(this.getRadioSelected("VIRUS_TYPE_2"))
            parm.setData("VIRUS_TYPE","2");
        parm.setData("POLIOMYELITIS_FLG",this.getCheckBoxSelected("POLIOMYELITIS_FLG"));
        parm.setData("HIGH_FLU",this.getCheckBoxSelected("HIGH_FLU"));
        parm.setData("HIVES_FLG",this.getCheckBoxSelected("HIVES_FLG"));
        parm.setData("EPIDEMIC_BLOOD",this.getCheckBoxSelected("EPIDEMIC_BLOOD"));
        parm.setData("LYSSA",this.getCheckBoxSelected("LYSSA"));
        parm.setData("EPIDEMIC_HEPATITIS",this.getCheckBoxSelected("EPIDEMIC_HEPATITIS"));
        parm.setData("DENGUE",this.getCheckBoxSelected("DENGUE"));
        if(this.getRadioSelected("CHARCOAL_0"))
            parm.setData("CHARCOAL","0");
        else if(this.getRadioSelected("CHARCOAL_1"))
            parm.setData("CHARCOAL","1");
        else if(this.getRadioSelected("CHARCOAL_2"))
            parm.setData("CHARCOAL","2");
        else if(this.getRadioSelected("CHARCOAL_3"))
            parm.setData("CHARCOAL","3");
        if(this.getRadioSelected("DIARRHEA_0"))
            parm.setData("DIARRHEA","0");
        else if(this.getRadioSelected("DIARRHEA_1"))
            parm.setData("DIARRHEA","1");
        else if(this.getRadioSelected("DIARRHEA_2"))
            parm.setData("DIARRHEA","2");
        if(this.getRadioSelected("PHTHISIC_0"))
            parm.setData("PHTHISIC","0");
        else if(this.getRadioSelected("PHTHISIC_1"))
            parm.setData("PHTHISIC","1");
        else if(this.getRadioSelected("PHTHISIC_2"))
            parm.setData("PHTHISIC","2");
        else if(this.getRadioSelected("PHTHISIC_3"))
            parm.setData("PHTHISIC","3");
        else if(this.getRadioSelected("PHTHISIC_4"))
            parm.setData("PHTHISIC","4");
        if(this.getRadioSelected("TYPHOID_0"))
            parm.setData("TYPHOID","0");
        else if(this.getRadioSelected("TYPHOID_1"))
            parm.setData("TYPHOID","1");
        else if(this.getRadioSelected("TYPHOID_2"))
            parm.setData("TYPHOID","2");
        parm.setData("EPIDEMIC_CEPHALITIS",this.getCheckBoxSelected("EPIDEMIC_CEPHALITIS"));
        parm.setData("CHINCOUGH",this.getCheckBoxSelected("CHINCOUGH"));
        parm.setData("DIPHTHERIA",this.getCheckBoxSelected("DIPHTHERIA"));
        parm.setData("NEW_LOCKJAW",this.getCheckBoxSelected("NEW_LOCKJAW"));
        parm.setData("SCARLATINA",this.getCheckBoxSelected("SCARLATINA"));
        parm.setData("BRUCE_DISEASE",this.getCheckBoxSelected("BRUCE_DISEASE"));
        parm.setData("GONORRHEA",this.getCheckBoxSelected("GONORRHEA"));
        if(this.getRadioSelected("LUES_0"))
            parm.setData("LUES","0");
        else if(this.getRadioSelected("LUES_1"))
            parm.setData("LUES","1");
        else if(this.getRadioSelected("LUES_2"))
            parm.setData("LUES","2");
        else if(this.getRadioSelected("LUES_3"))
            parm.setData("LUES","3");
        else if(this.getRadioSelected("LUES_4"))
            parm.setData("LUES","4");
        else if(this.getRadioSelected("LUES_5"))
            parm.setData("LUES","5");
        parm.setData("CATCH_LEPTOSPIRA",this.getCheckBoxSelected("CATCH_LEPTOSPIRA"));
        parm.setData("SCHISTOSOMIASIS_FLG",this.getCheckBoxSelected("SCHISTOSOMIASIS_FLG"));
        if(this.getRadioSelected("AGUE_0"))
            parm.setData("AGUE","0");
        else if(this.getRadioSelected("AGUE_1"))
            parm.setData("AGUE","1");
        else if(this.getRadioSelected("AGUE_2"))
            parm.setData("AGUE","2");
        else if(this.getRadioSelected("AGUE_3"))
            parm.setData("AGUE","3");
        parm.setData("GRIPPE_FLG",this.getCheckBoxSelected("GRIPPE_FLG"));
        parm.setData("MUMPS",this.getCheckBoxSelected("MUMPS"));
        parm.setData("MEASLES",this.getCheckBoxSelected("MEASLES"));
        parm.setData("ACUTE_CONJUNCTIVITIS",this.getCheckBoxSelected("ACUTE_CONJUNCTIVITIS"));
        parm.setData("LEPRA",this.getCheckBoxSelected("LEPRA"));
        parm.setData("SHIP_FEVER",this.getCheckBoxSelected("SHIP_FEVER"));
        parm.setData("KALA_AZAR",this.getCheckBoxSelected("KALA_AZAR"));
        parm.setData("ECHINOCOCCOSIS",this.getCheckBoxSelected("ECHINOCOCCOSIS"));
        parm.setData("FILARIASIS",this.getCheckBoxSelected("FILARIASIS"));
        parm.setData("EXPECT_CHOLERA",this.getCheckBoxSelected("EXPECT_CHOLERA"));
        parm.setData("REST_INFECTION",this.getValue("REST_INFECTION"));
        parm.setData("REVISALILLNESS_NAME",this.getValue("REVISALILLNESS_NAME"));
        parm.setData("COUNTERMAND_REAS",this.getValue("COUNTERMAND_REAS"));
        parm.setData("REPORT_UNIT",this.getValue("REPORT_UNIT"));
        parm.setData("CONT_TEL2",this.getValue("CONT_TEL2"));
        parm.setData("SPEAKER",this.getValue("SPEAKER"));
        parm.setData("PAD_DATE",this.getValue("PAD_DATE")==null?"":this.getValue("PAD_DATE"));
        parm.setData("REMARK",this.getValue("REMARK"));
        parm.setData("PAD_DEPT", this.getValueString("PAD_DEPT"));
        parm.setData("ICD_CODE",this.getValue("ICD_CODE"));
        parm.setData("DISEASETYPE_CODE",this.getValue("DISEASETYPE_CODE"));
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        return parm;
    }
    /**
     * ��鱣����Ϣ
     */
    private boolean checkData(){
        if(this.getValue("PAD_DATE")==null){
            this.messageBox_("��ѡ�񱨿�ʱ��");
            this.grabFocus("PAD_DATE");
            return false;
        }
        if(this.getValueString("FIRST_FLG").length()<=0){
            this.messageBox_("��ѡ�񱨿����");
            this.grabFocus("FIRST_FLG");
            return false;
        }
        if(this.getValue("DEAD_DATE")!=null&&this.getValue("ILLNESS_DATE")!=null){
            if(StringTool.getDateDiffer((Timestamp)this.getValue("DEAD_DATE"),(Timestamp)this.getValue("ILLNESS_DATE"))<0){
                this.messageBox_("�������ڲ������ڷ�������");
                this.grabFocus("DEAD_DATE");
                return false;
            }
        }
        if(this.getValue("COMFIRM_DATE")!=null&&this.getValue("ILLNESS_DATE")!=null){
            if(StringTool.getDateDiffer((Timestamp)this.getValue("COMFIRM_DATE"),(Timestamp)this.getValue("ILLNESS_DATE"))<0){
                this.messageBox_("������ڲ������ڷ�������");
                this.grabFocus("COMFIRM_DATE");
                return false;
            }
        }
        if(this.getValueString("REPORT_UNIT").length()<=0){
            this.messageBox_("����д���浥λ");
            this.grabFocus("REPORT_UNIT");
            return false;
        }
        return true;
    }
    /**
     * ��ȡ��ѡ��ť��ѡ��״̬
     * @param tag String
     * @return boolean
     */
    private boolean getRadioSelected(String tag){
        TRadioButton a = (TRadioButton)this.getComponent(tag);
        return a.isSelected();
    }
    /**
     * ��ȡ��ѡ���ѡ��״̬
     * @param tag String
     * @return String
     */
    private String getCheckBoxSelected(String tag){
        TCheckBox a = (TCheckBox)this.getComponent(tag);
        if(a.isSelected())
            return "Y";
        else
            return "N";
    }
    /**
     * ����¼�
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        this.setValue("ICD_CODE", parm.getValue("ICD_CODE"));
        this.setValue("ICD_DESC", parm.getValue("ICD_CHN_DESC"));
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
        //��ȡ��ӡ����
        TParm parm = MROInfectTool.getInstance().getPrintData(MR_NO,CASE_NO,CARD_SEQ_NO);
        if(parm.getErrCode()<0){
            return;
        }
        this.openPrintDialog("%ROOT%\\config\\prt\\MRO\\MROInfect.jhw",parm);
    }
    /**
     * ɾ��
     */
    public void onDelete(){
        if("UPDATE".equals(SAVE_FLG)){
            int re = this.messageBox("��ʾ","ȷ��Ҫɾ��������Ϣ��",0);
            if(re==0){
                TParm result = MROInfectTool.getInstance().delInfect(MR_NO,
                    CASE_NO, CARD_SEQ_NO);
                if (result.getErrCode() < 0) {
                    this.messageBox("E0005");
                    return;
                }
                this.messageBox("P0005");
                this.onClear();
            }
        }
    }
    /**
     * ��ʷ��ѯ
     */
    public void onHistory(){
        if("DR".equals(OPEN_TYPE)){
            hasInfectData("M");
        }
        else
            hasInfectData("H");
    }
    /**
     * ʡѡ���¼�
     */
    public void onADDRESS_PROVICE(){
        this.clearValue("ADDRESS_COUNTRY");
        this.callFunction("UI|ADDRESS_COUNTRY|onQuery");
    }
}
