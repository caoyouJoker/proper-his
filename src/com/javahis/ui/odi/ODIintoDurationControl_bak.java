package com.javahis.ui.odi;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.odi.ODIintoDurationTool;
import com.dongyang.manager.TIOM_AppServer;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.dongyang.ui.TButton;

/**
 * <p>Title: ����ʱ��</p>
 *
 * <p>Description: ����ʱ��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class ODIintoDurationControl_bak extends TControl {
    public ODIintoDurationControl_bak() {

    }
    private TParm sendParm;
    private String case_no="";
    private String currentDuration;
    private String nextDuration;
    private String clncPathCode;
    /**
     * ҳ���ʼ������
     */
    public void onInit() {
        super.onInit();
        initPage();
    }
    /**
     * ��ʼ��ҳ��
     */
    private void initPage(){
        //System.out.println("ҳ���ʼ��");
        sendParm = (TParm)this.getParameter();
        case_no=sendParm.getValue("CASE_NO");
        //��ջ�������begin
        currentDuration=null;
        nextDuration=null;
        this.setValue("currentDuration","");
        this.setValue("nextDuration", "");
        //��ջ�������end
        getClncPathCode();
        getCurrentDuration();
        getNextDuration();
        initPageData();
        //�������ʱ�̰�ť��ʾ����
        changeDurationButnValue();
    }
    /**
     * ���û����һʱ������ʾ����ʱ��
     */
    private void changeDurationButnValue(){
        if(nextDuration==null){
            TButton intoDurationBtn = (TButton)this.getComponent("btnIntoDuration");
            intoDurationBtn.setText("����ʱ��");
        }
    }
    /**
     * ��ʼ��ҳ��ֵ
     */
    private void initPageData(){
        if(clncPathCode!=null){
            this.setValue("CLNC_PATHCODE",clncPathCode);
        }
        if(currentDuration!=null){
             this.setValue("currentDuration",currentDuration);
        }
        if (nextDuration != null) {
            this.setValue("nextDuration", nextDuration);
        }
    }
    /**
     * �õ���ǰʱ��
     */
    private void getCurrentDuration(){
//        if(case_no==null){
//            return;
//        }
//        StringBuffer sqlbf = new StringBuffer();
//        sqlbf.append("SELECT * FROM (");
//        sqlbf.append("SELECT SCHD_CODE FROM CLP_THRPYSCHDM_REAL WHERE CLNCPATH_CODE= ");
//        sqlbf.append("(SELECT CLNCPATH_CODE  FROM  ADM_INP WHERE CASE_NO ='"+this.case_no+"' AND DS_DATE IS NULL AND CANCEL_FLG = 'N' AND ROWNUM<2 )");
//        sqlbf.append(" AND REGION_CODE='"+Operator.getRegion()+"' ");
//        sqlbf.append(" AND CASE_NO='"+this.case_no+"' ");
//        sqlbf.append("ORDER BY SEQ DESC");
//        sqlbf.append(")A WHERE ROWNUM<2");
//        System.out.println("�õ���ǰʱ��sql:"+sqlbf.toString());
//        TParm result = new TParm(TJDODBTool.getInstance().select(
//                sqlbf.toString()));
//        //�õ���ǰʱ��
//        if (result.getCount() > 0) {
//            currentDuration = result.getValue("SCHD_CODE", 0);
//        }
//
//        if(case_no==null){
//    return;
//}
        //20110905
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(
                "SELECT SCHD_CODE FROM CLP_THRPYSCHDM_REAL WHERE CLNCPATH_CODE= '");
        sqlbf.append(clncPathCode);
        sqlbf.append("' AND REGION_CODE='" + Operator.getRegion() + "' ");
        sqlbf.append(" AND CASE_NO='" + this.case_no + "' ");
        sqlbf.append(" AND SYSDATE BETWEEN START_DATE AND END_DATE");
        sqlbf.append(" ORDER BY SEQ ");
       // System.out.println("�õ���ǰʱ��sql:" + sqlbf.toString());
        TParm result = new TParm(TJDODBTool.getInstance().select(
                sqlbf.toString()));
        //�õ���ǰʱ��
        if (result.getCount("SCHD_CODE") > 0) {
            currentDuration = result.getValue("SCHD_CODE", 0);
            return;
        }
        sqlbf.delete(0,sqlbf.length());
        sqlbf.append("SELECT CASE WHEN  SYSDATE < MIN(START_DATE) THEN 1 ELSE 0 END AS FLAG ");
        sqlbf.append(" FROM CLP_THRPYSCHDM_REAL WHERE CLNCPATH_CODE= '");
        sqlbf.append(clncPathCode);
        sqlbf.append("' AND REGION_CODE='" + Operator.getRegion() + "' ");
        sqlbf.append(" AND CASE_NO='" + this.case_no + "' ");
        //System.out.println("sqlbf:::DDDD:"+sqlbf);
        result = new TParm(TJDODBTool.getInstance().select(
                sqlbf.toString()));
         //�õ���ǰʱ��
        if (result.getCount("FLAG") > 0) {
//            currentDuration = result.getValue("SCHD_CODE", 0);
            if("1".equals(result.getValue("FLAG",0))){
                sqlbf.delete(0, sqlbf.length());
                sqlbf.append(
                        "SELECT SCHD_CODE ");
                sqlbf.append(" FROM CLP_THRPYSCHDM_REAL WHERE CLNCPATH_CODE= '");
                sqlbf.append(clncPathCode);
                sqlbf.append("' AND REGION_CODE='" + Operator.getRegion() + "' ");
                sqlbf.append(" AND CASE_NO='" + this.case_no + "' ");
                sqlbf.append(" ORDER BY SEQ ");
                //System.out.println("sqlbf:::"+sqlbf.toString());
                result = new TParm(TJDODBTool.getInstance().select(
                sqlbf.toString()));
                //�õ���ǰʱ��
                if (result.getCount("SCHD_CODE") > 0) {
                    currentDuration = result.getValue("SCHD_CODE", 0);
                    return;
                }
            }
        }
        //System.out.println("111111111111111111");
        //�ж�ʱ�̽��������
        sqlbf.delete(0, sqlbf.length());
        sqlbf.append(
                "SELECT CASE WHEN  SYSDATE > MAX(END_DATE) THEN 1 ELSE 0 END AS FLAG ");
        sqlbf.append(" FROM CLP_THRPYSCHDM_REAL WHERE CLNCPATH_CODE= '");
        sqlbf.append(clncPathCode);
        sqlbf.append("' AND REGION_CODE='" + Operator.getRegion() + "' ");
        sqlbf.append(" AND CASE_NO='" + this.case_no + "' ");
        //System.out.println("sqlbfAAAAAAAAAAAA:"+sqlbf);
        result = new TParm(TJDODBTool.getInstance().select(
                sqlbf.toString()));
        //�õ���ǰʱ��
        if (result.getCount("FLAG") > 0) {
//            currentDuration = result.getValue("SCHD_CODE", 0);
        	//��������ʱ��
            if ("1".equals(result.getValue("FLAG", 0))) {
                sqlbf.delete(0, sqlbf.length());
                sqlbf.append(
                        "SELECT SCHD_CODE ");
                sqlbf.append(" FROM CLP_THRPYSCHDM_REAL WHERE CLNCPATH_CODE= '");
                sqlbf.append(clncPathCode);
                sqlbf.append("' AND REGION_CODE='" + Operator.getRegion() + "' ");
                sqlbf.append(" AND CASE_NO='" + this.case_no + "' ");
                sqlbf.append(" ORDER BY SEQ  DESC");
                result = new TParm(TJDODBTool.getInstance().select(
                        sqlbf.toString()));
                //�õ���ǰʱ��
                if (result.getCount() > 0) {
                    currentDuration = result.getValue("SCHD_CODE", 0);
                    //System.out.println("22222222222222");
                    return;
                }
            }
        }

    }
    /**
     * �õ���һʱ�̣���û����һʱ����һʱ�̸�ֵΪnull
     */
    private void getNextDuration(){
        //�õ���һʱ��
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(
                "SELECT SCHD_CODE FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE=");
        sqlbf.append("(SELECT CLNCPATH_CODE  FROM  ADM_INP WHERE CASE_NO ='"+this.case_no+"' AND DS_DATE IS NULL AND CANCEL_FLG = 'N' AND ROWNUM<2 )");
        sqlbf.append(" AND REGION_CODE='"+Operator.getRegion()+"' ");
        sqlbf.append(" ORDER BY SEQ");
        //System.out.println("�õ���һʱ��sql:"+sqlbf.toString());
       // System.out.println("currentDuration:::"+currentDuration);
        TParm nextDurationResult = new TParm(TJDODBTool.getInstance().select(
                sqlbf.toString()));
        //�״ν���ʱ�̵����
        if(currentDuration==null){
            if(nextDurationResult.getCount()>0){
                nextDuration=nextDurationResult.getRow(0).getValue("SCHD_CODE");
                return ;
            }
        }
        //��ʱ���е����
        for (int i = 0; i < nextDurationResult.getCount(); i++) {
            TParm rowParm = nextDurationResult.getRow(i);
            if (rowParm.getValue("SCHD_CODE").equals(currentDuration)) {
                    //���һ��ʱ�̵����
                if(i==(nextDurationResult.getCount()-1)){
                    nextDuration=null;
                }else{
                    //���ٴ�·��������һ��ʱ���е����
                    nextDuration=nextDurationResult.getRow(i+1).getValue("SCHD_CODE");
                }
               break;
            }
        }
    }
    /**
     * �õ��ٴ�·��
     */
    private void getClncPathCode(){
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("SELECT CLNCPATH_CODE  FROM  ADM_INP WHERE CASE_NO ='"+this.case_no+"' AND DS_DATE IS NULL AND CANCEL_FLG = 'N' AND ROWNUM<2 ");
        //System.out.println("�õ��ٴ�·��sql:"+sqlbf.toString());
        TParm result = new TParm(TJDODBTool.getInstance().select(
                sqlbf.toString()));
        if(result.getCount()>0){
            clncPathCode=result.getValue("CLNCPATH_CODE",0);
        }
    }
    /**
     * ����ʱ��
     */
    public void inDuration(){
        if(checkDurationIsEnd()){
            this.messageBox("�ò��˵�ʱ���Ѿ�����");
            return ;
        }
        boolean flag = true;
        //��һ�ν���ʱ��
       // System.out.println("currentDuration:::::"+currentDuration);
       // System.out.println("nextDuration:::"+nextDuration);
        if(currentDuration==null){
            flag=insertNewDuration();
        }else if(nextDuration==null){ //�����һ��ʱ�̵����
            flag=intoLastDuration();
        }else{//ʱ���е����
            flag=intoNextDuration();
        }
        if(flag){
            this.messageBox("����ɹ�!");
        }else{
            this.messageBox("����ʧ��!");
        }
       //ִ�к�ҳ���ʼ��
       initPage();

    }
    /**
     * ���ʱ���Ƿ����
     * @return boolean
     */
    private boolean  checkDurationIsEnd(){
       // System.out.println("�ж�ʱ���Ƿ����");
        if(this.nextDuration!=null){
            return false;
        }
        boolean flag = true;
        TParm checkParm = new TParm();
        checkParm.setData("CLNC_PATHCODE", clncPathCode);
        checkParm.setData("NEXT_DURATION", nextDuration);
        checkParm.setData("CASE_NO", case_no);
        checkParm.setData("CURRENT_DURATION", this.currentDuration);
        putBasicSysInfoIntoParm(checkParm);
        TParm result= ODIintoDurationTool.getInstance().isDurationEnd(checkParm);
        flag=result.getInt("ISEND",0)==2;
        return flag;
    }
    /**
     * ���һ��ʱ��
     * @return boolean
     */
    private boolean intoLastDuration(){
       // System.out.println("���һ��ʱ�̽���");
        boolean flag=true;
        TParm saveParm = new TParm();
        saveParm.setData("CLNC_PATHCODE", clncPathCode);
        saveParm.setData("NEXT_DURATION", nextDuration);
        saveParm.setData("CASE_NO", case_no);
        saveParm.setData("CURRENT_DURATION", this.currentDuration);
        putBasicSysInfoIntoParm(saveParm);
        //�������
        Map basicMap = this.getBasicOperatorMap();
        //��ǰʱ��
        saveParm.setData("CUR_DATE", basicMap.get("OPT_DATE"));
        saveParm.setData("END_DATE", basicMap.get("OPT_DATE"));
        TParm result=ODIintoDurationTool.getInstance().updateDurationReal(saveParm);
        if (result.getErrCode() < 0) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * ������һʱ��
     * @return boolean
     */
    private boolean intoNextDuration(){
        TParm sendParm=new TParm();
        TParm saveParm=new TParm();
        saveParm.setData("CLNC_PATHCODE",clncPathCode);
        saveParm.setData("NEXT_DURATION",nextDuration);
        saveParm.setData("CASE_NO",case_no);
        saveParm.setData("CURRENT_DURATION",this.currentDuration);
        putBasicSysInfoIntoParm(saveParm);
        sendParm.setData("saveData",saveParm.getData());
        sendParm.setData("basicMap",this.getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.odi.ODIintoDurationAction", "intoNextDuration", sendParm);
        if (result.getErrCode() < 0) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * ��һ�ν���ʱ�̷���
     * @return boolean
     */
    private boolean insertNewDuration(){
        TParm saveParm=new TParm();
        saveParm.setData("CLNC_PATHCODE",clncPathCode);
        saveParm.setData("NEXT_DURATION",nextDuration);
        saveParm.setData("CASE_NO",case_no);
        putBasicSysInfoIntoParm(saveParm);
        //System.out.println("savaParm:"+saveParm);
        TParm result = ODIintoDurationTool.getInstance().insertIntoDurationReal(saveParm);
        if (result.getErrCode() < 0) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * �رշ���
     */
    public void onWindowClose(){
        this.closeWindow();
    }

    /**
     * ����Operator�õ�map
     * @return Map
     */
    private Map getBasicOperatorMap() {
        Map map = new HashMap();
        map.put("REGION_CODE", Operator.getRegion());
        map.put("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMddHHmmss");
        map.put("OPT_DATE", datestr);
        map.put("OPT_TERM", Operator.getIP());
        return map;
    }

    /**
     * ��TParm�м���ϵͳĬ����Ϣ
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
        //System.out.println("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
//        Timestamp today = SystemTool.getInstance().getDate();
//        String datestr = StringTool.getString(today, "yyyyMMddHHmmSS");
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        parm.setData("OPT_DATE", format.format(today));
        parm.setData("OPT_TERM", Operator.getIP());
    }



}
