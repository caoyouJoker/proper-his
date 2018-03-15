package com.javahis.ui.med;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import jdo.ekt.EKTIO;
import jdo.med.MedToLedTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TKeyListener;
import com.javahis.util.StringUtil;

/**
 * 
 * <strong>Title : ExmToLedUIControl<br></strong>
 * <strong>Description : </strong>������к�<br> 
 * <strong>Create on : 2012-2-15<br></strong>
 * <p>
 * <strong>Copyright (C) <br></strong>
 * <p>
 * @author XXXXX XXXXX@126.com<br>
 * @version <strong>ProperSoft</strong><br>
 * <br>
 * <strong>�޸���ʷ:</strong><br>
 * �޸���		�޸�����		�޸�����<br>
 * -------------------------------------------<br>
 * <br>
 * <br>
 */
public class MedToLedUIControl extends TControl{
	private TTextField mrNo;
	private TTextField name;
	private TTextField sex;
	private TTextField age;
	private TTextField birth;
	private TComboBox type;
	private TComboBox caseNoCombo;
	private TTable table;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		initComponent();
        //�󶨿ؼ��¼�
        callFunction("UI|MR_NO|addEventListener",
                     "MR_NO->" + TKeyListener.KEY_RELEASED, this,
                     "onKeyReleased");
        this.type.setValue("O");
        //���ò����б��ֵ begin
        
        //���ò����б��ֵ end
	}
	private void initComponent(){
		mrNo=(TTextField)this.getComponent("MR_NO");
		name=(TTextField)this.getComponent("NAME");
		sex=(TTextField)this.getComponent("SEX");
		age=(TTextField)this.getComponent("AGE");
		birth=(TTextField)this.getComponent("BIRTH");
		type=(TComboBox)this.getComponent("TYPE");
		table=(TTable)this.getComponent("TABLE");
		caseNoCombo=(TComboBox)this.getComponent("CASE_COMBO");
	}
    public void onKeyReleased(KeyEvent e) {
        if (e.getKeyCode() != 10) {
            return;
        }
        this.onQuery();
    }
	/**
	 * 
	 * ��ѯ
	 */
	 public void onQuery(){
		 if("".equals(this.mrNo.getValue())){
			 this.messageBox("�����벡���ţ�");
			 return;
		 }
		 if("".equals(this.type.getValue())){
			 this.messageBox("��ѡ���ż�ס��");
			 return;
		 }
		 this.mrNo.setValue(PatTool.getInstance().checkMrno(this.mrNo.getValue()));
		// modify by huangtt 20160929 EMPI���߲�����ʾ start
		String mr_no = mrNo.getValue();
		Pat pat = Pat.onQueryByMrNo(mr_no);
		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			this.messageBox("������" + mr_no + " �Ѻϲ��� " + "" + pat.getMrNo());
			this.setValue("MR_NO", pat.getMrNo());// ������
			this.mrNo.setValue(pat.getMrNo());
		}
		// modify by huangtt 20160929 EMPI���߲�����ʾ end
		 TParm queryPatInfo = MedToLedTool.getInstance().queryPatInfo(this.mrNo.getValue());
		 if(queryPatInfo.getCount("MR_NO")<=0){
			 this.messageBox("�������ݣ�");
			 return;
		 }
		 String mrNo=queryPatInfo.getValue("MR_NO",0);
		 String patName=queryPatInfo.getValue("PAT_NAME",0);
		 String sexDesc=queryPatInfo.getValue("SEX_DESC",0);
		 String age =queryPatInfo.getValue("AGE",0);
		 String birthDate =queryPatInfo.getValue("BIRTH_DATE",0);
		 this.mrNo.setValue(mrNo);
		 this.name.setValue(patName);
		 this.sex.setValue(sexDesc);
		 this.age.setValue(age);
		 this.birth.setValue(birthDate);
		 //��ʼ�������б�begin
		 TParm allCaseNoOEForCombo = MedToLedTool.getInstance().getALLCaseNoOEForCombo(mrNo);
		 this.caseNoCombo.setParmValue(allCaseNoOEForCombo);
		 //��ʼ�������б�end
		 //������ϸ��Ϣ
		 TParm orderDetail = MedToLedTool.getInstance().queryOrderDetail(this.mrNo.getValue(),this.type.getValue(),this.caseNoCombo.getValue());
		 // System.out.println(orderDetail);
		 this.table.setParmValue(orderDetail);
	 }
	 /**
	  * 
	  * ����
	  */
	 public void onRead(){
		 TParm readEkt = EKTIO.getInstance().readEkt();
		 String mr_no=readEkt.getValue("MR_NO");
		 this.mrNo.setValue(mr_no);
		// modify by huangtt 20160929 EMPI���߲�����ʾ start
		mr_no = mrNo.getValue();
		Pat pat = Pat.onQueryByMrNo(mr_no);
		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			this.messageBox("������" + mr_no + " �Ѻϲ��� " + "" + pat.getMrNo());
			this.setValue("MR_NO", pat.getMrNo());// ������
			this.mrNo.setValue(pat.getMrNo());
		}
		// modify by huangtt 20160929 EMPI���߲�����ʾ end
		 this.onQuery();
	 }
	 /**
	  * 
	  * ����
	  */
	 public void  onRegist(){
		 TParm tableParm = this.table.getParmValue();
		 List orderList = new ArrayList<String>();
		 for(int i=0;i<tableParm.getCount("ORDER_CODE");i++){
			 if("N".equals(tableParm.getValue("BILL_FLG",i))&&"Y".equals(tableParm.getValue("CHK",i))){
				 this.messageBox(tableParm.getValue("ORDER_DESC",i)+"δ�Ʒѣ�");
				 return;
			 }
			 if("Y".equals(tableParm.getValue("CHK",i))){
				 orderList.add(tableParm.getValue("ORDER_DESC",i));
			 }
			 
		 }
		 //�к�
		 if(orderList.size()>0){
			 call(orderList);
			 this.messageBox("�кųɹ���");
		 }else{
			 this.messageBox("�к�ʧ�ܣ�");
		 }
		 
	 }
	 
	 /**
	  * 
	  * �к�
	  */
	 public void call(List<String> orderList){
		 
		// 000000001001|������|��|1961-07-12|����|192.168.1.124|*�ർ�ĵ�ͼ����Զ�����;*�����Զ�����
		String massages = this.mrNo.getValue() + "|" + this.name.getValue()
				+ "|" + this.sex.getValue() + "|"
				+ this.birth.getValue().replaceAll("/", "-") + "|"
				+ this.type.getSelectedName() + "|" + Operator.getIP() + "|";
		// ����ҽ����Ϣ
		for (String orderDesc : orderList) {
			massages += orderDesc + ";";
		}		
		// ��ҽ���е����һ����ɾ��
		massages = massages.substring(0, (massages.length() - 1));
		//System.out.println("========massages=========" + massages);
		// ���ýк� begin
		// this.messageBox(massages);
		TParm inParm = new TParm();
		inParm.setData("msg", massages);
		TIOM_AppServer.executeAction("action.device.CallNoAction",
				"doExmCallNo", inParm);
		 
		 //���ýк� end
	 }
	 
		/**
	 * �һ�MENU�����¼�
	 * 
	 * @param tableName
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("TABLE");
		table.setPopupMenuSyntax("��ʾ����ҽ��ϸ�� \n Display collection details with your doctor,openRigthPopMenu;�鿴���� \n Report,showRept");
	}
	/**
	 * �򿪼���ҽ��ϸ���ѯ
	 */
	public void openRigthPopMenu() {
		int groupNo = Integer.parseInt(this.table.getParmValue().getValue("ORDERSET_GROUP_NO",this.table.getSelectedRow()));
		String orderCode = this.table.getParmValue().getValue("ORDER_CODE",this.table.getSelectedRow());
		String caseNo = this.table.getParmValue().getValue("CASE_NO",this.table.getSelectedRow());
		TParm parm = getOrderSetDetails(groupNo, orderCode,caseNo);
		// this.messageBox_("����ҽ��ϸ��"+parm);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}
	/**
	 * ���ؼ���ҽ��ϸ���TParm��ʽ
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode,String caseNo) {
		TParm result = new TParm();
		if (groupNo < 0) {
			System.out
					.println("OpdOrder->getOrderSetDetails->groupNo is invalie");
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			System.out
					.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return result;
		}
		TParm parm = MedToLedTool.getInstance().getOrderListForOPD(caseNo);
		int count = parm.getCount("CASE_NO");
		// System.out.println(parm);
//		this.messageBox(count+"");
		if (count < 0) {
			// // System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// // System.out.println("groupNo=-============" + groupNo);
		// // System.out.println("orderSetCode===========" + orderSetCode);
		String tempCode;
		int tempNo;
		// // System.out.println("count===============" + count);
		// temperrϸ��۸�
		for (int i = 0; i < count; i++) {
			tempCode = parm.getValue("ORDERSET_CODE", i);
			tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
//			 // System.out.println("tempCode==========" + tempCode);
//			 // System.out.println("tempNO============" + tempNo);
//			 // System.out.println("setmain_flg========" + parm.getBoolean("SETMAIN_FLG", i));
			if (tempCode.equalsIgnoreCase(orderSetCode) && tempNo == groupNo
					&& !parm.getBoolean("SETMAIN_FLG", i)) {
//				this.messageBox("11");
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
				result.addData("SPECIFICATION",
						parm.getValue("SPECIFICATION", i));
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// ��ѯ����
				TParm ownPriceParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				// this.messageBox_(ownPriceParm);
				// �����ܼ۸�
				double ownPrice = ownPriceParm.getDouble("OWN_PRICE", 0)
						* parm.getDouble("DOSAGE_QTY", i);
				result.addData("OWN_PRICE",
						ownPriceParm.getDouble("OWN_PRICE", 0));
				result.addData("OWN_AMT", ownPrice);
				result.addData("EXEC_DEPT_CODE",
						parm.getValue("EXEC_DEPT_CODE", i));
				result.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
				result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
			}
		}
		return result;
	}
	/**
	 * �鿴����
	 */
	public void showRept() {
		TParm action = this.table.getParmValue().getRow(this.table.getSelectedRow());
//		// LIS����
//		if ("LIS".equals(action.getValue("CAT1_TYPE"))) {
//			String labNo = action.getValue("MED_APPLY_NO");
//			if (labNo.length() == 0) {
//				this.messageBox("E0188");
//				return;
//			}
//			SystemTool.getInstance().OpenLisWeb(this.getMrNo(), null, "", "",
//					"", "");
//		}
		// RIS����
//		if ("RIS".equals(action.getValue("CAT1_TYPE"))) {
			SystemTool.getInstance().OpenRisWeb(this.mrNo.getValue());
//		}
	}
	/**
	 * 
	 */
	public void onClear(){
		this.mrNo.setValue("");
		this.name.setValue("");
		this.sex.setValue("");
		this.age.setValue("");
		this.birth.setValue("");
		//this.type.setValue("");
		this.caseNoCombo.setValue("");
		this.caseNoCombo.setValue("");
		this.table.setParmValue(new TParm());
	}
}
