package com.javahis.ui.mro;

import java.sql.Timestamp;

import jdo.mro.MROLendRegTool;
import jdo.mro.MROQueueTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;


/**
 * <p>Title: ����ע��</p>
 *
 * <p>Description: ����ע��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-11
 * @version 1.0
 */
public class MROLendRegControl
    extends TControl {
    private TParm Parameter;//��Ų���
    private String CASE_NO;//��¼�����е�CASE_NO
    //��ʼ��
    public void onInit() {
        super.onInit();
        //���ղ���
        Object obj = this.getParameter();
        if (obj != null) {
            if (obj instanceof TParm) { //�ж��Ƿ���TParm
                Parameter = (TParm) obj;
            }
            else {
                this.closeWindow();
            }
        }
        else {
            this.closeWindow();
        }
        pageInit();
    }

    //ҳ���ʼ��
    private void pageInit() {
        this.setValue("IPD_NO", Parameter.getValue("IPD_NO"));
        this.setValue("MR_NO",Parameter.getValue("MR_NO"));
        this.setValue("PAT_NAME",Parameter.getValue("PAT_NAME"));
        this.setValue("VS_CODE",Parameter.getValue("VS_CODE"));
        CASE_NO = Parameter.getValue("CASE_NO");
        this.setValue("QUE_DATE",SystemTool.getInstance().getDate());
        this.setValue("REQ_DEPT",Operator.getDept());//������Ա����
        this.setValue("MR_PERSON",Operator.getID());//������Ա
    }
    /**
     * ����
     */
    public void onSave(){
//        Map taskVariables = new HashMap();
//        taskVariables.put("CASE NO", CASE_NO);
//        taskVariables.put("IF_FORMER", "��ʽ");
//        taskVariables.put("APPLIER", Operator.getName());
//        JbpmTool.createStartTaskInstance("BORROW CASE",Operator.getID(),taskVariables);
//        this.messageBox("����ɹ���");
        TParm mrv = new TParm();
        mrv.setData("MR_NO",this.getValue("MR_NO"));
        TParm mrvResult = MROQueueTool.getInstance().selectMRO_MRV_TECH(mrv);
        if(mrvResult.getErrCode()<0){
        	this.messageBox_("��ѯ�鵵��Ϣʧ�ܣ�" + mrvResult.getErrName() + mrvResult.getErrCode() + mrvResult.getErrText());
            return;
        }
        if(mrvResult.getCount()<=0){
        	this.messageBox_("�˲����޹鵵��Ϣ�����ɽ���");
            return;
        }
        if(!"3".equalsIgnoreCase(mrvResult.getValue("CHECK_FLG",0))){
            this.messageBox_("�˲�����δ�鵵�����ɽ���");
            return;
        }
        if(((TTextFormat)this.getComponent("QUE_DATE")).getValue() == null){
        	this.messageBox("����д�������ڣ�");
        	this.grabFocus("QUE_DATE");
        	return;
        }
        if(this.getValue("LEND_CODE") == null || "".equals(this.getValue("LEND_CODE").toString().trim())){
        	this.messageBox("����д����ԭ��");
        	this.grabFocus("LEND_CODE");
        	return;
        }
        
        TParm selParm = new TParm();
        selParm.setData("MR_NO", this.getValue("MR_NO"));
        TParm selResultParm = MROLendRegTool.getInstance().selectQueue(selParm);
        if(selResultParm.getCount() > 0 && !selResultParm.getData("ISSUE_CODE", 0).toString().equals("2")){
        	this.messageBox("�ò��������δ�黹�������ظ������");
        	return;
        }
        TNull tn = new TNull(Timestamp.class);
        TParm parm = this.getParmForTag("IPD_NO;MR_NO;LEND_CODE;MR_PERSON;REQ_DEPT");
        String QUE_SEQ = SystemTool.getInstance().getNo("ALL", "MRO", "QUE_SEQ",
        "QUE_SEQ"); //����ȡ��ԭ��
        parm.setData("QUE_SEQ",QUE_SEQ);//���ĺ�
        parm.setData("ADM_HOSP","");//����Ժ�����ż����ã�
        parm.setData("SESSION_CODE","");//ʱ�Σ��ż����ã�
        parm.setData("ISSUE_CODE","0");//����״̬��0 �Ǽ�δ����
        parm.setData("RTN_DATE",((TTextFormat)this.getComponent("RTN_DATE")).getValue()==null?tn:((TTextFormat)this.getComponent("RTN_DATE")).getValue());//Ӧ�黹����
        parm.setData("QUE_DATE",((TTextFormat)this.getComponent("QUE_DATE")).getValue());//��������
        parm.setData("CAN_FLG","N");//��ǣ�ȡ����
        parm.setData("ADM_TYPE","");//�ż�ס�� ��������ʹ�� Ŀǰ��ʹ��
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("QUE_HOSP",Operator.getRegion());//Ŀǰ������������
        parm.setData("DUE_DATE",tn);//��Ժ�󣬲���Ӧ�ù鵵ʱ����ʱ����
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("IN_PERSON", "");
        parm.setData("IN_DATE", tn);
        parm.setData("OPT_TERM",Operator.getIP());
        TParm result = MROLendRegTool.getInstance().insertQueue(parm);
        if(result.getErrCode()<0){
            this.messageBox("�Ǽ�ʧ�ܣ�"+ result.getErrName() + result.getErrText());
            return;
        }
        this.messageBox("�Ǽ���ϣ�");
    }
    /**
     * ���
     */
    public void onClear(){
        this.clearValue("LEND_CODE;QUE_DATE;RTN_DATE");
    }
    /**
     * ѡ�񡰽���ԭ�򡱺��Զ���д ����
     */
    public void getLendDays(){
        TParm result = MROLendRegTool.getInstance().queryLendDays(this.getValueString("LEND_CODE"));
        int days = result.getInt("LEND_DAY",0);
        Timestamp rueDate = StringTool.rollDate((Timestamp)((TTextFormat)this.getComponent("QUE_DATE")).getValue(),(long)days);
        this.setValue("RTN_DATE",rueDate);
    }
}
