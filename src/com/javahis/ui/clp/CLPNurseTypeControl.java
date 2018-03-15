package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import java.util.regex.Pattern;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import java.util.Map;
import com.dongyang.data.TNull;
import java.util.HashMap;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import jdo.clp.CLPCHKItemTool;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import jdo.clp.CLPNurseTypeTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.TMessage;

/**
 * <p>Title: �������</p>
 *
 * <p>Description: �������</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPNurseTypeControl extends TControl {
    //��¼ѡ������
    int selectRow = -1;
    public CLPNurseTypeControl() {

    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        initPage();
        //SYSTEM.OUT.PRINTln("�������ҳ�����");
        this.onQuery();
        callFunction("UI|Table|addEventListener",
                 "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
    }
    /**
     * ��ʼ��ҳ��
     */
    private void initPage(){

    }
    /**
     * ҳ���ѯ����
     */
    public void onQuery(){
        setPrimaryKeyEnabled(true);
        //��˳��Ŵ���ʱ��֤����
        if (this.checkNullAndEmpty(this.getValueString("SEQ")) &&
            !this.validNumber(this.getValueString("SEQ"))) {
            this.messageBox("˳�������������");
            return;
        }
        TParm selectParm =getSelectedCondition();
        TParm result = CLPNurseTypeTool.getInstance().selectData(selectParm);
        this.callFunction("UI|Table|setParmValue", result);
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        selectRow = this.getSelectedRow("Table");
        if (selectRow == -1) {
            this.messageBox("��ѡ����Ҫɾ��������");
            return;
        }
        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
            TTable table = (TTable)this.getComponent("Table");
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String REGION_CODE = tableParm.getValue("REGION_CODE", selRow);
            String TYPE_CODE = tableParm.getValue("TYPE_CODE", selRow);
            TParm parm = new TParm();
            parm.setData("REGION_CODE", REGION_CODE);
            parm.setData("TYPE_CODE", TYPE_CODE);
            TParm result = CLPNurseTypeTool.getInstance().deleteData(parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //ɾ��������ʾ
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0) {
                return;
            }
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);
            this.messageBox("P0003");
        } else {
            return;
        }
    }

    /**
     * ��շ���
     */
    public void onClear(){
        this.setValue("TYPE_CODE","");
        this.setValue("PY2","");
        this.setValue("TYPE_CHN_DESC","");
        this.setValue("PY1","");
        this.setValue("TYPE_ENG_DESC","");
        this.setValue("CLASS_FLG","");
        this.setValue("DESCRIPTION","");
        this.setValue("SEQ","");
        setPrimaryKeyEnabled(true);
    }
    /**
     * ���淽��
     */
    public void onSave() {
        //SYSTEM.OUT.PRINTln("���水ťִ��");
        //��֤����
        if(!validBasicData()){
            return;
        }
        //�õ���������
        //��������ƴ������
        //����ƴ�������Զ���������
        //����ƴ��1
        setValue("PY1", TMessage.getPy(getValueString("TYPE_CHN_DESC")));
        TParm parm = new TParm();
        this.putParamWithObjName("TYPE_CODE",parm);
        this.putParamWithObjName("PY2",parm);
        this.putParamWithObjName("TYPE_CHN_DESC",parm);
        this.putParamWithObjName("PY1",parm);
        this.putParamWithObjName("TYPE_ENG_DESC",parm);
        this.putParamWithObjName("CLASS_FLG",parm);
        this.putParamWithObjName("DESCRIPTION",parm);
        this.putParamWithObjName("SEQ",parm);
        this.putBasicSysInfoIntoParm(parm);
        //�ж������Ƿ����
        TParm result = CLPNurseTypeTool.getInstance().checkDataExist(parm);
        boolean isdataExist=Integer.parseInt(result.getValue("DATACOUNT",0))>0?true:false;
        TParm resultopt=null;
        //����seq
        if ("".equals(parm.getValue("SEQ"))) {
            TNull tnull = new TNull(String.class);
            //SYSTEM.OUT.PRINTln("����----------------");
            parm.setData("SEQ", tnull);
        }
        if(isdataExist){
            //SYSTEM.OUT.PRINTln("���ݴ��ڣ�����----------");
            resultopt=CLPNurseTypeTool.getInstance().updateData(parm);
            if (resultopt.getErrCode() >= 0) {
                this.messageBox("P0001");
            } else {
                this.messageBox("E0001");
            }
        }else{
            //SYSTEM.OUT.PRINTln("���ݲ����ڣ�����--------");
            resultopt=CLPNurseTypeTool.getInstance().insertData(parm);
            if (resultopt.getErrCode() >= 0) {
                this.messageBox("P0002");
            } else {
                this.messageBox("E0002");
            }
        }
        this.onQuery();
    }
    /**
     * ��֤��Ϣ
     * @return TParm
     */
    private boolean validBasicData(){
        if(!this.emptyTextCheck("TYPE_CODE,TYPE_CHN_DESC")){
            return false;
        }
        String seqstr = this.getValueString("SEQ");
        if(this.checkNullAndEmpty(seqstr)){
            if(!this.validNumber(seqstr)){
                this.messageBox("˳�������������");
                return false;
            }
        }
        //��֤����
        TComboBox classFlgCb= (TComboBox)this.getComponent("CLASS_FLG");
        if(classFlgCb.getValue().equalsIgnoreCase("")){
            this.messageBox("��ѡ�����");
            return false;
        }
        return true;
    }
    /**
     * ���ÿؼ��Ƿ���÷���
     * @param flag boolean
     */
    private void setPrimaryKeyEnabled(boolean flag ){
        TTextField tTextField = (TTextField)this.getComponent("TYPE_CODE");
        tTextField.setEnabled(flag);
    }
    /**
     * ��ҳ��õ���ѯ��������
     */
    private TParm getSelectedCondition(){
        TParm selectedCondition=new TParm();
        putParamLikeWithObjName("TYPE_CODE",selectedCondition);
//        putParamLikeWithObjName("PY2",selectedCondition);
        putParamLikeWithObjName("TYPE_CHN_DESC",selectedCondition);
//        putParamLikeWithObjName("TYPE_ENG_DESC",selectedCondition);
//        putParamWithObjNameForQuery("CLASS_FLG",selectedCondition);
//        putParamLikeWithObjName("DESCRIPTION",selectedCondition);
//        putParamLikeWithObjName("SEQ",selectedCondition);
        selectedCondition.setData("REGION_CODE",Operator.getRegion());
        return selectedCondition;
    }
    /**
     * ������Ķ�Ӧ��Ԫ�����óɿ�д�����������óɲ���д
     * @param tableName String
     * @param rowNum int
     * @param columnNum int
     */
    private void setTableEnabled(String tableName, int rowNum, int columnNum) {
        TTable table = (TTable)this.getComponent(tableName);
        int totalColumnMaxLength = table.getColumnCount();
        int totalRowMaxLength = table.getRowCount();
        //SYSTEM.OUT.PRINTln("��������" + totalColumnMaxLength + "������:" +
                          // totalRowMaxLength);
        //����
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
        //SYSTEM.OUT.PRINTln("���д���" + lockColumnStr);
        table.setLockColumns(lockColumnStr);
        //����
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
        //SYSTEM.OUT.PRINTln("���д�ǰ��" + lockRowStr + "����" + totalRowMaxLength);
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
        //SYSTEM.OUT.PRINTln("���д���" + lockRowStr);
        if (lockRowStr.length() > 0) {
            table.setLockRows(lockRowStr);
        }

    }

    /**
     * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
     * @param objName String
     */
    private void putParamWithObjName(String objName, TParm parm,
                                     String paramName) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        parm.setData(paramName, objstr);
    }


    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        setValueForParm(
                "TYPE_CODE;TYPE_CHN_DESC;PY1;PY2;TYPE_ENG_DESC;CLASS_FLG;DESCRIPTION;SEQ",
                data, row);
        String str = data.getValue("SEQ", row);
        this.setValue("SEQ", str);
        setPrimaryKeyEnabled(false);
        selectRow = row;
    }
    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        //SYSTEM.OUT.PRINTln(objstr);
        objstr = objstr;
        //����ֵ��ؼ�����ͬ
        parm.setData(objName, objstr);
    }

    /**
     * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
     * @param objName String
     */
    private void putParamLikeWithObjName(String objName, TParm parm,
                                         String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr + "%";
            parm.setData(paramName, objstr);
        }

    }

    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamLikeWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr.trim() + "%";
            //����ֵ��ؼ�����ͬ
            parm.setData(objName, objstr);
        }
    }

    /**
     * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm) {
        putParamWithObjNameForQuery(objName, parm, objName);
    }

    /**
     * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
     * @param objName String
     * @param parm TParm
     * @param paramName String
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm,
                                             String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            //����ֵ��ؼ�����ͬ
            parm.setData(paramName, objstr.trim());
        }
    }

    /**
     * ���ؼ��Ƿ�Ϊ��
     * @param componentName String
     * @return boolean
     */
    private boolean checkComponentNullOrEmpty(String componentName) {
        if (componentName == null || "".equals(componentName)) {
            return false;
        }
        String valueStr = this.getValueString(componentName);
        if (valueStr == null || "".equals(valueStr)) {
            return false;
        }
        return true;
    }

    /**
     * �õ�ָ��table��ѡ����
     * @param tableName String
     * @return int
     */
    private int getSelectedRow(String tableName) {
        int selectedIndex = -1;
        if (tableName == null || tableName.length() <= 0) {
            return -1;
        }
        Object componentObj = this.getComponent(tableName);
        if (!(componentObj instanceof TTable)) {
            return -1;
        }
        TTable table = (TTable) componentObj;
        selectedIndex = table.getSelectedRow();
        return selectedIndex;
    }

    /**
     * ������֤����
     * @param validData String
     * @return boolean
     */
    private boolean validNumber(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,}");
        Matcher match = p.matcher(validData);
        if (!match.matches()) {
            return false;
        }
        return true;
    }

    /**
     * ��TParm�м���ϵͳĬ����Ϣ
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
        //SYSTEM.OUT.PRINTln("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM", Operator.getIP());
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
        String datestr = StringTool.getString(today, "yyyyMMdd");
        map.put("OPT_DATE", datestr);
        map.put("OPT_TERM",Operator.getIP());
        return map;
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @param dataFormatStr String
     * @return String
     */
    private String getCurrentDateStr(String dataFormatStr) {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, dataFormatStr);
        return datestr;
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @return String
     */
    private String getCurrentDateStr() {
        return getCurrentDateStr("yyyyMMdd");
    }

    /**
     * ����Ƿ�Ϊ�ջ�մ�
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }

    /**
     * ����TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from, TParm to, int row) {
        for (int i = 0; i < from.getNames().length; i++) {
            to.addData(from.getNames()[i],
                       from.getValue(from.getNames()[i], row));
        }
    }

    /**
     * ��¡����
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getValue(from.getNames()[i]));
        }
        return returnTParm;
    }

    /**
     * ����TParm ���null�ķ���
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNullVector(TParm parm, String keyStr, Class type) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData(keyStr, i) == null) {
                //SYSTEM.OUT.PRINTln("����Ϊ�����");
                TNull tnull = new TNull(type);
                parm.setData(keyStr, i, tnull);
            }
        }
    }

    /**
     * ����TParm ��nullֵ����
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNull(TParm parm, String keyStr, Class type) {
        if (parm.getData(keyStr) == null) {
            //SYSTEM.OUT.PRINTln("����Ϊ�����");
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }


}