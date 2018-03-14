package com.javahis.ui.ins;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import jdo.ins.INSTJTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

public class INS_ItemRegItemControl extends TControl {
	private TTextField NHI_CODE_Q;
	private TRadioButton ADM_TYPE_O;
	private TRadioButton ADM_TYPE_E;
	private TRadioButton ADM_TYPE_I;

	private TComboBox INS_TYPE;
	private TTextField NHI_CODE;
	private TTextField NHI_ORDER_DESC;
	private TTextField ORDER_CODE;
	private TTextField ORDER_DESC;
	private TTextFormat START_DATE;
	private TTextFormat CHANGE_DATE;
	private TComboBox REG_TYPE;
	private TNumberTextField PRICE;
	private TComboBox ORDER_TYPE;
	///////////////////////////////////
	private TNumberTextField OWN_PRICE;
	private TNumberTextField TOT_QTY;
	private TTextFormat REG_CLASS;
	private TComboBox DUL_FLG;
	private TTextFormat DISEASE_CODE;


	private boolean updateFlg = false;
	private TParm regionParm; // ҽ���������

	public void onInit() {
		super.onInit();
		onComponentInit();// ���������ʼ��
		Timestamp date = StringTool.getTimestamp(new Date());
		 this.setValue("START_DATE", date.toString()
				 .substring(0, 10).replace('-', '/'));
//		 this.setValue("CHANGE_DATE", date.toString()
//					.substring(0, 10).replace('-', '/'));
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // ���ҽ���������
		INS_TYPE.setSelectedIndex(0);
		REG_TYPE.setSelectedIndex(0);
		/////////////////////////////////////////////
		DUL_FLG.setSelectedIndex(0);
		REG_CLASS.setValue("1");
		// �õ�ǰ̨���������ݲ���ʾ�ڽ�����
		TParm recptype = this.getInputParm();
		if (recptype == null) {
			updateFlg = false;
//			this.setValue("APPROVE_TYPE", "2");
		} else {
			this.setValue("NHI_CODE", recptype.getValue("NHI_CODE"));
			this.setValue("INS_TYPE", recptype.getValue("INS_TYPE"));
			this.setValue("START_DATE", recptype.getValue("START_DATE"));
			this.setValue("APPROVE_TYPE", recptype.getValue("APPROVE_TYPE"));
			TParm data = this.onQuery(recptype.getValue("NHI_CODE"),recptype.getValue("INS_TYPE"),recptype.getValue("START_DATE"));
			updateFlg = true;
			this.onSetUI(data);
		}
	}

	public void onComponentInit() {
		NHI_CODE_Q = (TTextField) getComponent("NHI_CODE_Q");
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "");
		// ���õ����˵�
		NHI_CODE_Q.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		NHI_CODE_Q.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		
		ADM_TYPE_O = (TRadioButton) getComponent("ADM_TYPE_O");
		ADM_TYPE_E = (TRadioButton) getComponent("ADM_TYPE_E");
		ADM_TYPE_I = (TRadioButton) getComponent("ADM_TYPE_I");
		
		INS_TYPE = (TComboBox) getComponent("INS_TYPE");
		NHI_CODE = (TTextField) getComponent("NHI_CODE");
		NHI_ORDER_DESC = (TTextField) getComponent("NHI_ORDER_DESC");
		ORDER_CODE = (TTextField) getComponent("ORDER_CODE");
		ORDER_DESC = (TTextField) getComponent("ORDER_DESC");
		
		START_DATE = (TTextFormat) getComponent("START_DATE");
		CHANGE_DATE = (TTextFormat) getComponent("CHANGE_DATE");
		
		REG_TYPE = (TComboBox) getComponent("REG_TYPE");
		PRICE = (TNumberTextField) getComponent("PRICE");
		ORDER_TYPE = (TComboBox) getComponent("ORDER_TYPE");
		//////////////////////////////////////////////////
		OWN_PRICE = (TNumberTextField) getComponent("OWN_PRICE");
		TOT_QTY = (TNumberTextField) getComponent("TOT_QTY");
		REG_CLASS = (TTextFormat) getComponent("REG_CLASS");
	    DUL_FLG = (TComboBox) getComponent("DUL_FLG");
	    DISEASE_CODE = (TTextFormat) getComponent("DISEASE_CODE");
	}

	/**
	 * ��ҽ�����ѯҽ����Ϣ
	 * 
	 * @return TParm
	 */
	public void onQueryNhiCode() {
//		System.out.println("onQueryNhiCode()");
		String NHI_CODE_Q = this.getValueString("NHI_CODE_Q").trim();
		if (NHI_CODE_Q.equals("")) {
			return;
		}
		String Sql = " SELECT A.ORDER_CODE,A.ORDER_DESC,B.SFXMBM AS NHI_CODE,B.XMMC AS NHI_ORDER_DESC,A.OWN_PRICE AS PRICE, "+
		             " CASE WHEN A.CAT1_TYPE='PHA' THEN  A.CAT1_TYPE ELSE 'OTH' END AS ORDER_TYPE "+
		             " FROM SYS_FEE A,INS_RULE B "+
		             " WHERE A.ORDER_CODE ='" + NHI_CODE_Q + "' ";
		
		if (ADM_TYPE_I.isSelected()) {
			Sql = Sql + " AND B.SFXMBM(+)=A.NHI_CODE_I ";
		} else if (ADM_TYPE_E.isSelected()) {
			Sql = Sql + " AND B.SFXMBM(+)=A.NHI_CODE_E ";
		} else if (ADM_TYPE_O.isSelected()) {
			Sql = Sql + " AND B.SFXMBM(+)=A.NHI_CODE_O ";
		}
		Sql = Sql + " AND A.ACTIVE_FLG='Y' ";
		// System.out.println("regSql==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("ORDER_CODE") < 0) {
			this.messageBox("û�в�ѯ����Ӧҽ��");
			return;
		}
		onSetUI(tabParm);
	}
	/**
	 * ��ѯ������Ϣ
	 * 
	 * @return TParm
	 */
	private TParm onQuery(String nhi_code,String ins_type,String change_date) {
		String Sql = " SELECT NHI_CODE,NHI_ORDER_DESC,ORDER_CODE,ORDER_DESC, "+ 
	    " INS_TYPE,CHANGE_DATE,REG_TYPE,APPROVE_TYPE, "+
	    " PRICE,START_DATE,END_DATE,ORDER_TYPE,OWN_PRICE,TOT_QTY,REG_CLASS,DISEASE_CODE "+ 
	    " FROM INS_ITEM_REG "+
	    " WHERE NHI_CODE='"+ nhi_code + "' " + 
		" AND INS_TYPE='"+ins_type+"' "+
		" AND START_DATE='"+change_date+"' ";
//		System.out.println("onQuery==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("NHI_CODE") < 0) {
			this.messageBox("û�в�ѯ����Ӧ��¼");
			return null;
		}
		return tabParm;
	}
	/**
	 * ���ý�������
	 * 
	 * @return void
	 */
	private void onSetUI(TParm tabParm) {
		if(tabParm==null){
			return;
		}
		
		this.setValue("NHI_CODE", tabParm.getValue("NHI_CODE",0));
		this.setValue("NHI_ORDER_DESC", tabParm.getValue("NHI_ORDER_DESC",0));
		this.setValue("ORDER_CODE", tabParm.getValue("ORDER_CODE",0));
		this.setValue("ORDER_DESC", tabParm.getValue("ORDER_DESC",0));
		this.setValue("PRICE", tabParm.getValue("PRICE",0));
		this.setValue("ORDER_TYPE", tabParm.getValue("ORDER_TYPE",0));
	    if (this.updateFlg) {
		////////////////////////////////////////////////////////////////
		    this.setValue("OWN_PRICE", tabParm.getValue("OWN_PRICE",0));
		    this.setValue("TOT_QTY", tabParm.getValue("TOT_QTY",0));
		    this.setValue("REG_CLASS", tabParm.getValue("REG_CLASS",0));
		    this.setValue("DISEASE_CODE", tabParm.getValue("DISEASE_CODE",0));
		//////////////////////////////////////////////////////////
			this.setValue("INS_TYPE", tabParm.getValue("INS_TYPE",0));
			this.setValue("START_DATE", this.getUpDateFromat(tabParm.getValue("START_DATE", 0)));
			this.setValue("CHANGE_DATE", this.getUpDateFromat(tabParm.getValue("CHANGE_DATE",0)));
			this.setValue("REG_TYPE", tabParm.getValue("REG_TYPE",0));
	    }
	}

	private TParm onGetSaveDate() {
		TParm parm = new TParm();
		parm.setData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // ҽԺ����
		parm.setData("INS_TYPE", this.getValueString("INS_TYPE")); // ҽ������
		parm.setData("NHI_CODE", NHI_CODE.getValue()); // �շ���Ŀ����
		parm.setData("NHI_ORDER_DESC", NHI_ORDER_DESC.getValue()); // �շ���Ŀ����
		parm.setData("ORDER_CODE", ORDER_CODE.getValue()); // Ժ��ҽ������
		parm.setData("ORDER_DESC", ORDER_DESC.getValue()); // Ժ��ҽ������
		parm.setData("START_DATE", START_DATE.getValue().toString().substring(0, 10).replace("-", "")); // ��ʼʱ��
//		System.out.println("REG_TYPE:"+this.getValueString("REG_TYPE"));
		if(this.updateFlg&&(!this.getValueString("REG_TYPE").equals("1"))&&
				(!this.getValueString("REG_TYPE").equals("4"))){
			parm.setData("CHANGE_DATE", CHANGE_DATE.getValue().toString().substring(0, 10).replace("-", "")); // ���ʱ��
		}else{
			parm.setData("CHANGE_DATE", ""); // ���ʱ��
		}
		parm.setData("REG_TYPE", this.getValueString("REG_TYPE")); //����״̬
		parm.setData("PRICE", PRICE.getValue()); // ʵ�ʼ۸�
		/////////////////////////////////////////////////////////
		parm.setData("OWN_PRICE", String.valueOf(OWN_PRICE.getValue()).equals("null")?0:OWN_PRICE.getValue()); // �ɹ��۸�
		parm.setData("TOT_QTY", String.valueOf(TOT_QTY.getValue()).equals("null")?0:TOT_QTY.getValue()); // �ɹ�����
		parm.setData("REG_CLASS", this.getValueString("REG_CLASS"));
		parm.setData("DUL_FLG", this.getValueString("DUL_FLG"));
		parm.setData("DISEASE_CODE", this.getValueString("DISEASE_CODE"));
		///////////////////////////////////////////////////////////
		parm.setData("ORDER_TYPE", this.getValueString("ORDER_TYPE")); //ҽ�����
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("APPROVE_TYPE", "8");
		return parm;
	}

	public void onSaveAddUp() {
		if (!onIsNull()) {
			return;
		}
		TParm saveData = this.onGetSaveDate();
		// System.out.println("onSave");
		if (this.updateFlg) {

			// saveData.setData("HOSP_NHI_NO", regionP arm.getValue("NHI_NO",
			// 0)); // ҽԺ����
			// if(onInsItemRegItemUp(saveData)){
			// ����ϴ��ɹ����Ӽ�¼
			if (this.getValueString("REG_TYPE").equals("2")
					&& (!this.getValueString("APPROVE_TYPE").equals("8"))) {
				if (onInsItemRegItemUp(saveData)) {
					saveData.setData("START_DATE", CHANGE_DATE.getValue()
							.toString().substring(0, 10).replace("-", ""));
					saveData.setData("APPROVE_TYPE", "9");
					if (this.insert(saveData)) {
						this.messageBox("������Ϣ����ɹ�");
					} else {
						this.messageBox("������Ϣ����ʧ��");
					}
				}
			} else {
				if (this.update(saveData)) {
					onInsItemRegItemUp(saveData);
				}

			}
			// }

		} else {
			// Timestamp date = StringTool.getTimestamp(new Date());
			// this.setValue("START_DATE", date.toString()
			// .substring(0, 10).replace('-', '/'));
			// this.setValue("CHANGE_DATE", date.toString()
			// .substring(0, 10).replace('-', '/'));
			// saveData.setData("START_DATE",
			// START_DATE.getValue().toString().substring(0, 10).replace("-",
			// ""));
			// saveData.setData("CHANGE_DATE",
			// CHANGE_DATE.getValue().toString().substring(0, 10).replace("-",
			// ""));
			if (this.insert(saveData)) {
				// saveData.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO",
				// 0)); // ҽԺ����
				onInsItemRegItemUp(saveData);
			}
		}

	}

	public void onSave() {
//		System.out.println("onSave:"+this.updateFlg);
		if(!onIsNull()){
			return;
		}
//		System.out.println("onSave:"+this.updateFlg);
		TParm saveData = this.onGetSaveDate();
//		System.out.println("onSave:"+this.updateFlg);
		if (this.updateFlg) {
			if(this.update(saveData)){
				this.messageBox("������Ϣ����ɹ�");
			}else{
				this.messageBox("������Ϣ����ʧ��");
			}
		} else {
//			System.out.println("onSave:"+this.updateFlg);
//			Timestamp date = StringTool.getTimestamp(new Date());
//			 this.setValue("START_DATE", date.toString()
//					 .substring(0, 10).replace('-', '/'));
//			 this.setValue("CHANGE_DATE", date.toString()
//						.substring(0, 10).replace('-', '/'));
//			saveData.setData("START_DATE", START_DATE.getValue().toString().substring(0, 10).replace("-", ""));
//			saveData.setData("CHANGE_DATE", CHANGE_DATE.getValue().toString().substring(0, 10).replace("-", ""));
			if(this.insert(saveData)){
				this.messageBox("������Ϣ����ɹ�");
			}else{
				this.messageBox("������Ϣ����ʧ��");
			}
		}

	}

	private boolean insert(TParm saveData) {
		System.out.println("insertsaveData:"+saveData);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_Item_RegAction", "onInsertInsItemReg",
				saveData);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return false;
		}
		return true;
	}

	private boolean update(TParm saveData) {
//		System.out.println("updatesaveData:"+saveData);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_Item_RegAction", "onUpdateInsItemReg",
				saveData);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return false;
		}
		return true;
	}

	/**
	 * ������Ϣ�ϴ�
	 */
	public boolean onInsItemRegItemUp(TParm parm) {
		TParm tmpParm=onGetUpload(parm);
//		System.out.println("onInsItemRegItemUp:"+tmpParm.getValue("CHANGE_DATE",0));
//		System.out.println("onInsItemRegItemUp:"+tmpParm.getValue("CHANGE_DATE",0).equals(""));
		TParm splitParm = INSTJTool.getInstance().DataDown_zjks_X(tmpParm);
//		System.out.println("onInsItemRegItemUp:"+splitParm);
		
		if (!INSTJTool.getInstance().getErrParm(splitParm)) {
			this.messageBox("������Ϣ�ϴ�ʧ��\n"+splitParm.getErrText());
			return false;
		} else {
			String sql=" UPDATE INS_ITEM_REG SET "+
			           " APPROVE_TYPE='9', ";
			if(!tmpParm.getValue("CHANGE_DATE",0).equals("")){
				sql=sql+" CHANGE_DATE='"+tmpParm.getValue("CHANGE_DATE",0)+"', ";
			}
			sql=sql+ " OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' "+
		    " WHERE INS_TYPE='"+tmpParm.getValue("INS_TYPE",0)+
		    "' AND NHI_CODE='"+tmpParm.getValue("NHI_CODE",0)+
		    "' AND START_DATE='"+tmpParm.getValue("START_DATE",0)+"' ";
//			System.out.println("onInsItemRegDown_sql:"+sql);
			TParm result = new TParm(TJDODBTool.getInstance().update(sql));

			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
				return false;
			}else{
				this.messageBox("������Ϣ�ϴ��ɹ�");
				return true;
			}
		}
	}

	/**
	 * ���
	 */
	public void onClear(){
		NHI_CODE_Q.setValue("");
		ADM_TYPE_I.setSelected(true);
		INS_TYPE.setSelectedIndex(0);
		REG_TYPE.setSelectedIndex(0);
		NHI_CODE.setValue("");
		NHI_ORDER_DESC.setValue("");
		ORDER_CODE.setValue("");
		ORDER_DESC.setValue("");
		START_DATE.setValue("");
		CHANGE_DATE.setValue("");
		PRICE.setValue("0.0000");
		OWN_PRICE.setValue("0.0000");
		TOT_QTY.setValue("0.00");
		REG_CLASS.setValue("1");
		DISEASE_CODE.setValue("");
		DUL_FLG.setSelectedIndex(0);
	}
	
	/**
	 * ��ֵ���
	 */
	public boolean onIsNull() {
		
		if(getValueString("INS_TYPE").equals("")){
			this.messageBox("ҽ�����Ͳ���Ϊ��");
			return false;
		}
		if(getValueString("NHI_CODE").equals("")){
			this.messageBox("��Ŀ���벻��Ϊ��");
			return false;
		}
		if(getValueString("REG_TYPE").equals("2")&&
				(CHANGE_DATE.getValue()==null||
						CHANGE_DATE.getValue().equals(""))){
			this.messageBox("���ʱ�䲻��Ϊ��");
			return false;
		}
		if(getValueString("REG_TYPE").equals("3")&&
				(CHANGE_DATE.getValue()==null||
				CHANGE_DATE.getValue().equals(""))){
			this.messageBox("���ʱ�䲻��Ϊ��");
			return false;
		}
		if(getValueString("REG_CLASS").equals("3")&&
				(StringUtil.isNullString((String)DISEASE_CODE.getValue()))){
			this.messageBox("���ֱ��벻��Ϊ��");
			return false;
		}
		
		return true;
	}
	private TParm onGetUpload(TParm tabParm) {
//		System.out.println("tabParm:"+tabParm);
		TParm parm = new TParm();
		parm.addData("INS_TYPE", tabParm.getValue("INS_TYPE")); // ����
		parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // ҽԺ����
		parm.addData("NHI_CODE", tabParm.getValue("NHI_CODE")); // �շ���Ŀ����
		parm.addData("START_DATE", tabParm.getValue("START_DATE")); // ��ʼʱ��
		parm.addData("REG_TYPE", tabParm.getValue("REG_TYPE")); // ����״̬
		parm.addData("CHANGE_DATE", tabParm.getValue("CHANGE_DATE")); //���/��ֹʱ��
		parm.addData("PRICE", tabParm.getValue("PRICE")); // ʵ�ʼ۸�
		/////////////////////////////////////////////////////////////
		parm.addData("OWN_PRICE", tabParm.getValue("OWN_PRICE"));//�ɹ��۸�
		parm.addData("TOT_QTY", tabParm.getValue("TOT_QTY"));//�ɹ�����
		/////////////////////////////////////////////////////////
		parm.addData("OPT_USER", Operator.getName()); // ����Ա
		///////////////////////////////////////////////////////////////////
		parm.addData("REG_CLASS", tabParm.getValue("REG_CLASS"));//�������
		parm.addData("DISEASE_CODE", tabParm.getValue("DISEASE_CODE"));//���ֱ���
		parm.addData("DUL_FLG", "0");//˫���Ʊ�־
		parm.addData("PARM_COUNT", 13);
//		System.out.println("parm:"+parm);
		return parm;
	}
	private String getUpDateFromat(String str){
		if(str.length()<=0){
			return "";
		}
		return str.substring(0, 4)+"/"+str.substring(4, 6)+"/"+str.substring(6, 8);
	}
//	private boolean isUpdate(TParm parm){
//		 return (this.updateFlg&&
//		         parm.getValue("REG_TYPE", 0).equals("1"))||
//		         (this.updateFlg&&
//		         (!parm.getValue("REG_TYPE", 0).equals("1"))&&
//		         this.getValue("APPROVE_TYPE").equals("2"));
//	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
//		System.out.println("popReturn:"+parm);
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			NHI_CODE_Q.setValue(order_code);

		// ��ղ�ѯ�ؼ�
		onQueryNhiCode();
		NHI_CODE_Q.setValue("");
	}
	public void reg_typeSelect(){
		Timestamp date = StringTool.getTimestamp(new Date());
		 this.setValue("CHANGE_DATE", date.toString()
		.substring(0, 10).replace('-', '/'));
	}
}
