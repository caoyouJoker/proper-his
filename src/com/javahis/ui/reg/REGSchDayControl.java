package com.javahis.ui.reg;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;

import jdo.sys.Operator;
import jdo.reg.SchDayTool;
import jdo.reg.PanelGroupTool;
import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;

import jdo.reg.PanelRoomTool;

import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.system.textFormat.TextFormatREGClinicType;
import com.dongyang.ui.TComboBox;

import jdo.sys.SYSRegionTool;

/**
 *
 * <p>Title:�հ������� </p>
 *
 * <p>Description:�հ������� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class REGSchDayControl
    extends TControl {
    TParm tableParm;
    int selectRow = -1;
	//===zhangp 20120813 start
	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;
	//===zhangp 20120813 end
	
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        //��ʼ��REGION��½Ĭ�ϵ�¼����
         //======pangben modify 20110410
         callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
         //========pangben modify 20110421 start Ȩ�����
         TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
         cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                 getValueString("REGION_CODE")));
         //===========pangben modify 20110421 stop

        setValue("ADM_DATE", SystemTool.getInstance().getDate());
        onQuery();
        callFunction("UI|DEPT_CODE|onQuery");
        //===zhangp 20120329 start
        onSelectTime();
        //===zhangp 20120329 end
        //===zhangp 20120816 start
        TTable table = (TTable) getComponent("Table");
        addListener(table);
        //===zhangp 20120816 end
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {

        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|Table|getParmValue");
        setValueForParm("REGION_CODE;ADM_TYPE;ADM_DATE;SESSION_CODE;CLINICROOM_NO;"+
                        "WEST_MEDI_FLG;DEPT_CODE;DR_CODE;REALDEPT_CODE;REALDR_CODE;"+
                        "CLINICTYPE_CODE;QUEGROUP_CODE;QUE_NO;MAX_QUE;VIP_FLG;CLINICTMP_FLG;STOP_SESSION;REFRSN_CODE;REG_SPECIAL_NUMBER",
                        data, row);
        setValue("ADM_DATE",
                 StringTool.getTimestamp(data.getValue("ADM_DATE", row),
                                         "yyyyMMdd"));
        tableParm =new TParm();
        tableParm = data.getRow(row);
        selectRow = row;
        TextFormatSYSOperator operatorText = (TextFormatSYSOperator)this.getComponent("DR_CODE");
        operatorText.onQuery();
        setValue("DR_CODE",data.getValue("DR_CODE",row));
        TextFormatREGClinicType clinicTypeText = (TextFormatREGClinicType)this.getComponent("CLINICTYPE_CODE");
        clinicTypeText.onQuery();
        setValue("CLINICTYPE_CODE",data.getValue("CLINICTYPE_CODE",row));
        //add by huangtt 20140106
        TextFormatDept realdept = (TextFormatDept) this.getComponent("REALDEPT_CODE");
        realdept.onQuery();
        
        //===zhangp 20120317 start
//        if(this.getValueInt("QUE_NO")>1){
//        	this.callFunction("UI|delete|setEnabled", false);
////        	this.callFunction("UI|save|setEnabled", false);
//        }
//        this.callFunction("UI|ADM_TYPE|setEnabled", false);
//        this.callFunction("UI|ADM_DATE|setEnabled", false);
//        this.callFunction("UI|SESSION_CODE|setEnabled", false);
//        this.callFunction("UI|CLINICROOM_NO|setEnabled", false);
//        this.callFunction("UI|WEST_MEDI_FLG|setEnabled", false);
//        this.callFunction("UI|DEPT_CODE|setEnabled", false);
//        this.callFunction("UI|DR_CODE|setEnabled", false);
//        this.callFunction("UI|QUEGROUP_CODE|setEnabled", false);
//        this.callFunction("UI|CLINICTMP_FLG|setEnabled", false);
//        this.callFunction("UI|STOP_SESSION|setEnabled", false);
        //===zhangp 20120317 end
    }

    /**
     * ����
     */
    public void onInsert() {
        //У���Ƿ������ֵ,��������ʾ��Ϣ
        if (getValue("DEPT_CODE") == null) {
            this.messageBox("���Ҳ���Ϊ��!");
            return;
        }
        if (getValue("CLINICTYPE_CODE") == null) {
            this.messageBox("�ű���Ϊ��!");
            return;
        }
        if (getValue("QUEGROUP_CODE") == null) {
            this.messageBox("���������Ϊ��!");
            return;
        }
        if (getValue("WEST_MEDI_FLG") == null ||
            this.getValueString("WEST_MEDI_FLG").length() == 0) {
            this.messageBox("����ҽע�ǲ���Ϊ��!");
            return;
        }
        //=====zhangp 20120302 modify start
        if (getValue("ADM_TYPE") == null ||
                this.getValueString("ADM_TYPE").length() == 0) {
                this.messageBox("�ż�����Ϊ��!");
                return;
            }
        //======zhangp 20120302 modify end
//        if (!this.emptyTextCheck("DEPT_CODE,CLINICTYPE_CODE,QUEGROUP_CODE"))
//            return;
        TParm parm = getParmForTag("REGION_CODE;ADM_TYPE;ADM_DATE:Timestamp;SESSION_CODE;CLINICROOM_NO;"+
                                   "WEST_MEDI_FLG;DEPT_CODE;DR_CODE;REALDEPT_CODE;REALDR_CODE;"+
                                   "CLINICTYPE_CODE;QUEGROUP_CODE;QUE_NO:int;MAX_QUE:int;VIP_FLG;"+
                                   "CLINICTMP_FLG;STOP_SESSION;REFRSN_CODE;REG_SPECIAL_NUMBER");
        //add by huangtt 20131224===========  || getValue("REALDEPT_CODE").equals("")
        if(getValue("REALDEPT_CODE")==null || getValue("REALDEPT_CODE").equals(""))
            parm.setData("REALDEPT_CODE",getValue("DEPT_CODE"));
        if(getValue("REALDR_CODE")==null || getValue("REALDR_CODE").equals(""))
            parm.setData("REALDR_CODE",getValue("DR_CODE"));

        //���ҷ���������Ϣ
        TParm roomParm = PanelRoomTool.getInstance().getAreaByRoom(getValue(
            "CLINICROOM_NO").toString());
        String areaCode = roomParm.getValue("CLINICAREA_CODE",0);
        parm.setData("REG_CLINICAREA",areaCode);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
//        //��ȡ�����ַ���
//        String temptime=StringTool .getString((Timestamp)this.getValue("ADM_DATE"),"yyMMddhhmmss");
//        parm.setData("ADM_DATE",temptime);

        TParm result = SchDayTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
        	//===zhangp 20120315 start
//            messageBox(result.getErrText());
//            return;
            messageBox("����,�ż���,����ʱ��,ʱ��,��� �����ظ�");
            messageBox("����ʧ��");
            return;
            //===zhangp 20120315
        }
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        //��ʾ��������
        callFunction("UI|Table|addRow", parm,
                     "REGION_CODE;ADM_TYPE;ADM_DATE;SESSION_CODE;CLINICROOM_NO;" +
                     "WEST_MEDI_FLG;DEPT_CODE;DR_CODE;REALDEPT_CODE;REALDR_CODE;" +
                     "CLINICTYPE_CODE;QUEGROUP_CODE;QUE_NO;MAX_QUE;VIP_FLG;" +
                     "CLINICTMP_FLG;STOP_SESSION;REFRSN_CODE;OPT_USER;OPT_DATE;OPT_TERM;REG_SPECIAL_NUMBER");
        this.messageBox("P0002");
        onClear();
    }

    /**
     * ����
     */
	public void onUpdate() {
		TTable table = (TTable) this.getComponent("Table");
		int selRow = table.getSelectedRow();
		// ===zhangp 20120620 start
		String west_medi_flg = getValueString("WEST_MEDI_FLG");
		String dept_code = getValueString("DEPT_CODE");
		String dr_code = getValueString("DR_CODE");
		String realdept_code=getValueString("REALDEPT_CODE");
		String realdr_code=getValueString("REALDR_CODE");
		String clinictype_code = getValueString("CLINICTYPE_CODE");
		String quegroup_code = getValueString("QUEGROUP_CODE");
		String clinictmp_flg = getValueString("CLINICTMP_FLG");
		String stop_session = getValueString("STOP_SESSION");
		String reg_specialNumber = getValueString("REG_SPECIAL_NUMBER");
		// ===zhangp 20120428 start
		String region = getValue("REGION_CODE").toString();
		String admType = getValue("ADM_TYPE").toString();
		String admDate = getValue("ADM_DATE").toString();
		admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
				+ admDate.substring(8, 10);
		String sessionCode = getValue("SESSION_CODE").toString();
		String clinicroomNo = getValue("CLINICROOM_NO").toString();
		TParm parm = getParmForTag("REGION_CODE;ADM_TYPE;ADM_DATE:Timestamp;SESSION_CODE;CLINICROOM_NO;"
				+ "WEST_MEDI_FLG;DEPT_CODE;DR_CODE;REALDEPT_CODE;REALDR_CODE;"
				+ "CLINICTYPE_CODE;QUEGROUP_CODE;QUE_NO:int;MAX_QUE:int;VIP_FLG;"
				+ "CLINICTMP_FLG;STOP_SESSION;REFRSN_CODE;REG_SPECIAL_NUMBER");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		
//	    old value �õ�����ǰ�ľ�ֵ
	    String oldRegionCode = table.getItemString(selRow,"REGION_CODE");
	    String oldAdmType = table.getItemString(selRow,"ADM_TYPE");
	    String oldAdmDate = table.getItemString(selRow,"ADM_DATE");
	    String oldSessionCode = table.getItemString(selRow,"SESSION_CODE");
	    String oldClinicRoomNo = table.getItemString(selRow,"CLINICROOM_NO");
		// ===zhangp 20120428 end
        parm.setData("OLD_REGION_CODE",oldRegionCode);
        parm.setData("OLD_ADM_TYPE",oldAdmType);
        parm.setData("OLD_ADM_DATE",oldAdmDate);
        parm.setData("OLD_SESSION_CODE",oldSessionCode);
        parm.setData("OLD_CLINICROOM_NO",oldClinicRoomNo);
        
        //add by huangtt 20131224 start
        if(getValue("REALDEPT_CODE")==null || getValue("REALDEPT_CODE").equals(""))
            parm.setData("REALDEPT_CODE",getValue("DEPT_CODE"));
        if(getValue("REALDR_CODE")==null || getValue("REALDR_CODE").equals(""))
            parm.setData("REALDR_CODE",getValue("DR_CODE"));
        //add by huangtt 20131224 end
        
        if(selRow<0){
            return;
        }
        TParm result = new TParm();

		if ((west_medi_flg.equals(table.getItemString(selRow, "WEST_MEDI_FLG"))
				&& dept_code.equals(table.getItemString(selRow, "DEPT_CODE"))
				&& dr_code.equals(table.getItemString(selRow, "DR_CODE"))
				&& clinictype_code.equals(table.getItemString(selRow,
						"CLINICTYPE_CODE"))
				&& quegroup_code.equals(table.getItemString(selRow,
						"QUEGROUP_CODE"))
				&& clinictmp_flg.equals(table.getItemString(selRow,
						"CLINICTMP_FLG"))
				&& reg_specialNumber.equals(table.getItemString(selRow,"REG_SPECIAL_NUMBER"))
						
				&& region.equals(table.getItemString(selRow, "REGION_CODE"))
				&& admType.equals(table.getItemString(selRow, "ADM_TYPE"))
				&& admDate.equals(table.getItemString(selRow, "ADM_DATE"))
				&& sessionCode.equals(table.getItemString(selRow,
						"SESSION_CODE")) && clinicroomNo.equals(table
				.getItemString(selRow, "CLINICROOM_NO")))
				) {
			
			if(!realdept_code.equals(table.getItemString(selRow,
					"REALDEPT_CODE")) || !realdr_code.equals(table
					.getItemString(selRow, "REALDR_CODE"))){
				messageBox("OK");
				result = TIOM_AppServer.executeAction("action.reg.REGAction",
						"updateRegSchDay", parm);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					this.messageBox("P0005");
					return;
				}
			}
			
			if(!stop_session.equals(table.getItemString(selRow,"STOP_SESSION"))){
				 result = SchDayTool.getInstance().updateRegSchDayStop(parm);
			        if (result.getErrCode() < 0) {
			            messageBox(result.getErrText());
			            return;
			        }
			}
			
			
		} else {
			//===huangtt 20131111 start
			if (checkRegpatadm(oldRegionCode, oldAdmType, oldAdmDate, oldSessionCode,
					oldClinicRoomNo)) { 
				messageBox("�йҺ���Ϣ,�����޸�");
				return;
			}
			//==huangtt 20131111 end
			if (checkRegpatadm(region, admType, admDate, sessionCode,
					clinicroomNo)) {  
				messageBox("�йҺ���Ϣ,�����޸�");
				return;
			}
			 result = SchDayTool.getInstance().updatedata(parm);
		        if (result.getErrCode() < 0) {
		            messageBox(result.getErrText());
		            return;
		        }
		}


       
        //ˢ�£�����ĩ��ĳ�е�ֵ
        int row = (Integer) callFunction("UI|Table|getSelectedRow");
        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|Table|getParmValue");
        data.setRowData(row, parm);
        callFunction("UI|Table|setRowParmValue", row, data);
        this.messageBox("P0001");

    }

    /**
     * ����
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }


    /**
     * ɾ��
     */
    public void onDelete() {
        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
            if (selectRow == -1)
                return;
            String region = getValue("REGION_CODE").toString();
            String admType = getValue("ADM_TYPE").toString();
            String admDate = getValue("ADM_DATE").toString();
            admDate = admDate.substring(0,4)+admDate.substring(5,7)+admDate.substring(8,10);
            String sessionCode = getValue("SESSION_CODE").toString();
            String clinicroomNo = getValue("CLINICROOM_NO").toString();
            if(checkRegpatadm(region,admType,admDate,sessionCode,clinicroomNo)){
            	messageBox("�йҺ���Ϣ,����ɾ��");
            	return;
            }
            TParm parm = getParmForTag(
                "REGION_CODE;ADM_TYPE;ADM_DATE:Timestamp;SESSION_CODE;CLINICROOM_NO;DEPT_CODE;DR_CODE", true);
            TParm result = SchDayTool.getInstance().deletedata(parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //ɾ��������ʾ
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);

            this.messageBox("P0003");
        }
        else {
            return;
        }
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
    	//===zhangp 201203029 start
//        TParm parm = getParmForTag(
//            "REGION_CODE;ADM_TYPE;ADM_DATE:Timestamp;SESSION_CODE;CLINICROOM_NO", true);
//        TParm data = SchDayTool.getInstance().selectdata(parm);
    	String admType = getValueString("ADM_DATE");
    	if(!admType.equals("")){
    		admType = admType.substring(0, 4) + admType.substring(5, 7) + admType.substring(8, 10);
    	}
    	TCheckBox selectTime = (TCheckBox) getComponent("SETECT_TIME");
    	String startDate = getValueString("START_DATE");
    	String endDate = getValueString("END_DATE");
    	if(selectTime.isSelected()){
    		if(startDate.equals("")||endDate.equals("")){
    			messageBox("��ѡ���ѯ����");
    			return;
    		}else{
    			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) + startDate.substring(8, 10);
    			endDate = endDate.substring(0, 4) + endDate.substring(5, 7) + endDate.substring(8, 10);
    		}
    	}
        String sql = 
        	" SELECT REGION_CODE,ADM_TYPE,ADM_DATE,SESSION_CODE,CLINICROOM_NO,WEST_MEDI_FLG,DEPT_CODE,REG_CLINICAREA,DR_CODE,REALDEPT_CODE,REALDR_CODE,CLINICTYPE_CODE," +
        	" QUEGROUP_CODE,QUE_NO,MAX_QUE,VIP_FLG,CLINICTMP_FLG,STOP_SESSION,REFRSN_CODE, OPT_USER,OPT_DATE,OPT_TERM,REG_SPECIAL_NUMBER " +
        	" FROM REG_SCHDAY WHERE 1=1 ";
        if(!getValueString("REGION_CODE").equals("")){
        	sql += " AND REGION_CODE = '"+getValueString("REGION_CODE")+"' ";
        }
        if(!getValueString("ADM_TYPE").equals("")){
        	sql += " AND ADM_TYPE = '"+getValueString("ADM_TYPE")+"' ";
        }
        if(!getValueString("ADM_DATE").equals("")){
        	sql += " AND ADM_DATE = '"+admType+"' ";
        }
        if(!getValueString("SESSION_CODE").equals("")){
        	sql += " AND SESSION_CODE = '"+getValueString("SESSION_CODE")+"' ";
        }
        if(!getValueString("CLINICROOM_NO").equals("")){
        	sql += " AND CLINICROOM_NO = '"+getValueString("CLINICROOM_NO")+"' ";
        }
        if(!getValueString("DR_CODE").equals("")){
        	sql += " AND DR_CODE = '"+getValueString("DR_CODE")+"' ";
        }
        if(selectTime.isSelected()){
        	sql += " AND ADM_DATE BETWEEN '"+startDate+"' AND '"+endDate+"' ";
        }
	 	TParm data = new TParm(TJDODBTool.getInstance().select(sql));
	 	//===zhangp 201203029 end
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|Table|setParmValue", data);
        //===zhangp 20120317 start
        this.callFunction("UI|delete|setEnabled", true);
    	this.callFunction("UI|save|setEnabled", true);
        this.callFunction("UI|ADM_TYPE|setEnabled", true);
        this.callFunction("UI|ADM_DATE|setEnabled", true);
        this.callFunction("UI|SESSION_CODE|setEnabled", true);
        this.callFunction("UI|CLINICROOM_NO|setEnabled", true);
        this.callFunction("UI|WEST_MEDI_FLG|setEnabled", true);
        this.callFunction("UI|DEPT_CODE|setEnabled", true);
        this.callFunction("UI|DR_CODE|setEnabled", true);
        this.callFunction("UI|QUEGROUP_CODE|setEnabled", true);
        this.callFunction("UI|CLINICTMP_FLG|setEnabled", true);
        this.callFunction("UI|STOP_SESSION|setEnabled", true);
    	//===end
    }
    /**
     * �ѹҺ�����
     */
    public void onInscon(){
        if(tableParm==null){
            this.messageBox("���ѡҽ��");
            return;
        }
//        this.messageBox_(tableParm);
        //=============pangben modify 20110602 �鳤Ȩ��
        tableParm.setData("LEADER",getPopedem("LEADER"));
        this.openDialog("%ROOT%\\config\\reg\\REGSchDayRegList.x",tableParm);
    }
    /**
     *���
     */
    public void onClear() {
        clearValue("REGION_CODE;ADM_TYPE;ADM_DATE;SESSION_CODE;CLINICROOM_NO;"+
                   "WEST_MEDI_FLG;DEPT_CODE;DR_CODE;REALDEPT_CODE;REALDR_CODE;"+
                   "CLINICTYPE_CODE;QUEGROUP_CODE;QUE_NO;MAX_QUE;VIP_FLG;CLINICTMP_FLG;STOP_SESSION;REFRSN_CODE" +
                   ";START_DATE;END_DATE;REG_SPECIAL_NUMBER");
        this.callFunction("UI|Table|clearSelection");
        selectRow = -1;
        setValue("ADM_DATE", SystemTool.getInstance().getDate());
        //=======pangben modify 20110421
        callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
        //===zhangp 20120317 start
        this.callFunction("UI|delete|setEnabled", true);
    	this.callFunction("UI|save|setEnabled", true);
        this.callFunction("UI|ADM_TYPE|setEnabled", true);
        this.callFunction("UI|ADM_DATE|setEnabled", true);
        this.callFunction("UI|SESSION_CODE|setEnabled", true);
        this.callFunction("UI|CLINICROOM_NO|setEnabled", true);
        this.callFunction("UI|WEST_MEDI_FLG|setEnabled", true);
        this.callFunction("UI|DEPT_CODE|setEnabled", true);
        this.callFunction("UI|DR_CODE|setEnabled", true);
        this.callFunction("UI|QUEGROUP_CODE|setEnabled", true);
        this.callFunction("UI|CLINICTMP_FLG|setEnabled", true);
        this.callFunction("UI|STOP_SESSION|setEnabled", true);
    	//===end
    	//zhangp 20120329 start
    	TCheckBox selectTime = (TCheckBox) getComponent("SETECT_TIME");
    	selectTime.setSelected(false);
    	onSelectTime();
    	//zhangp 20120329 end
    
    }

    /**
     * �ű�ı��¼�
     */
    public void onClinicTypeCombo() {
        TParm data = new TParm();
        if(getValueString("QUEGROUP_CODE").length()!=0){
            data = PanelGroupTool.getInstance().getInfobyClinicType(this.
                getValueString("QUEGROUP_CODE"));
            if (data.getErrCode() < 0) {
                messageBox(data.getErrText());
                return;
            }
            setValue("QUE_NO", 1);
            setValue("MAX_QUE", data.getInt("MAX_QUE", 0));
            setValue("VIP_FLG", data.getValue("VIP_FLG", 0));
        }
    }

    /**
     * ҽʦ�Ӻ�
     * ====add by huangtt 20131206
     */
    public void onAdd(){
    	this.openWindow("%ROOT%\\config\\reg\\REGQueUnfold.x");
    }   
    
    
    /**
     * ͣ���Ǹı��¼�
     */
    public void onStopSessionChange() {
        if ("Y".equalsIgnoreCase(this.getValueString("STOP_SESSION"))) {
            callFunction("UI|REFRSN_CODE|setEnabled", true);
        }
        if ("N".equalsIgnoreCase(this.getValueString("STOP_SESSION"))) {
            clearValue("REFRSN_CODE");

            callFunction("UI|REFRSN_CODE|setEnabled", false);
        }
    }
    //$$=======================add by lx 02/11=============================$$//
    /**
     * �ͽкŽӿ�
     */
    public void onCallNo(){
    	StringBuffer sb = new StringBuffer();
        /**
         �ű�����
         �������
        ҽʦ����
        ҽʦ����
        ���
        �ű�
        ʱ���
        �޹�����**/
   //�ѹ�����
    	//1.ȡ�հ��
    	TTable table = (TTable)this.getComponent("Table");
    	//
    	String startDate=((TTextFormat)this.getComponent("ADM_DATE")).getText();
    	//System.out.println("===startDate==="+startDate);
    	
    	 int sendNum = 0;
    	int rowCount=table.getRowCount();
    	for(int i=0;i<rowCount;i++){
    		 if(sb.length() > 0)
    	        //sb.append((char)13);
    		sb.append("\r\n");
    		//�ű�����
    		 sb.append(getDataString(startDate));
    		 sb.append("|");
    		//�������
             sb.append(this.getDesc("SYS_DEPT","DEPT_CODE",(String)table.getValueAt(i, 6),"DEPT_CHN_DESC"));//????????
             sb.append("|");
    		//ҽʦ����
             sb.append((String)table.getValueAt(i, 7));
             sb.append("|");
             //ҽʦ���� DR_CODE
             sb.append(this.getDesc("SYS_OPERATOR","USER_ID",(String)table.getValueAt(i, 7),"USER_NAME"));
             sb.append("|");
             //���
             sb.append(this.getDesc("REG_CLINICROOM","CLINICROOM_NO",(String)table.getValueAt(i, 4),"CLINICROOM_DESC"));            
             sb.append("|");
             //�ű�    
             sb.append(this.getDesc("REG_CLINICTYPE","CLINICTYPE_CODE",(String)table.getValueAt(i, 10),"CLINICTYPE_DESC"));
             sb.append("|");
             //ʱ��� ����
             sb.append(this.getDesc("REG_SESSION","SESSION_CODE",(String)table.getValueAt(i, 3),"SESSION_DESC"));         
             sb.append("|");
             //�޹�����           
             sb.append(String.valueOf(table.getValueAt(i, 13)));          
            //2.���������Ϣ;
         	
             
    	}
    	if(sb.length() > 0){
            //System.out.println("send msg======="+sb.toString());        	
         	TParm inParm = new TParm();
         	inParm.setData("msg", sb.toString());
			TIOM_AppServer.executeAction(
					"action.device.CallNoAction", "doRegSchDay", inParm);
			 sendNum++;   		
    	}
    	
    	if(sendNum > 0){
    		this.messageBox("�ͽкŽӿڳɹ�");
    	}else{
    		this.messageBox("�޷�����Ϣ");
    	}
    	
    	 	
    	
    	
    }
    
    /**
     * ��ʽ������
     * @param startDate String
     * @return String
     */
    public String getDataString(String startDate)
    {
    	
      StringBuffer sb = new StringBuffer();
      sb.append(startDate.substring(0, 4));
      sb.append("-");
      sb.append(startDate.substring(5, 7));
      sb.append("-");
      sb.append(startDate.substring(8, 10));
      return sb.toString();
    }
    

    /**
     * ������ʾ���
     * @param code
     * @return
     */
    private String getDesc(String tableName,String codeColumn,String code,String rtnColumn){
      String sql="SELECT ";
      sql+=rtnColumn+" AS rtnDesc";
      sql+=" FROM "+tableName;
      sql+=" WHERE "+codeColumn+"='"+code+"'";
      //System.out.println("SQL===="+sql);
      TParm rtn = new TParm(TJDODBTool.getInstance().select(sql)); 
      String rtnDesc=rtn.getValue("RTNDESC", 0);  
      //System.out.println("===rtnDesc==="+rtnDesc);
      return rtnDesc;
    }
    
    /**
     * ��ʱ��β�ѯ����
     * ===zhangp 20120329
     */
    public void onSelectTime(){
    	TCheckBox selectTime = (TCheckBox) getComponent("SETECT_TIME");
    	if(selectTime.isSelected()){
    		this.callFunction("UI|ADM_DATE|setEnabled", false);
    		this.callFunction("UI|START_DATE|setEnabled", true);
    		this.callFunction("UI|END_DATE|setEnabled", true);
    		setValue("START_DATE", SystemTool.getInstance().getDate());
    		setValue("END_DATE", SystemTool.getInstance().getDate());
    		clearValue("ADM_DATE");
    	}else{
    		this.callFunction("UI|ADM_DATE|setEnabled", true);
    		this.callFunction("UI|START_DATE|setEnabled", false);
    		this.callFunction("UI|END_DATE|setEnabled", false);
    		setValue("ADM_DATE", SystemTool.getInstance().getDate());
    		clearValue("START_DATE;END_DATE");
    	}
    }
    /**
     * ��ѯ�Ƿ��Ѿ����Һ�
     * ====zhangp 20120424
     * @param region
     * @param admType
     * @param admDate
     * @param sessionCode
     * @param clinicroomNo
     * @return
     */
    public boolean checkRegpatadm(String region, String admType, String admDate, String sessionCode, String clinicroomNo){
//    	Timestamp admDate1 = TCM_Transform.getTimestamp(admDate);
//    	admDate = StringTool.getString(admDate1, "yyyyMMdd");
    	admDate = admDate.replaceAll("-", "").replaceAll("/", "").substring(0, 8);
    	System.out.println("regschday--admDate---"+admDate);
    	String sql = 
    		"SELECT CASE_NO" +
    		" FROM REG_PATADM" +
    		" WHERE REGION_CODE = '"+region+"'" +
    		" AND ADM_TYPE = '"+admType+"'" +
    		" AND ADM_DATE = TO_DATE ('"+admDate+"', 'YYYYMMDD')" +
    		" AND SESSION_CODE = '"+sessionCode+"'" +
    		" AND CLINICROOM_NO = '"+clinicroomNo+"'" +
    		" AND REGCAN_USER IS NULL";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount()<0){
    		return false;
    	}
    	return true;
    }
    
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		TTable table = (TTable) getComponent("Table");
		table.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
	
	
	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
}
