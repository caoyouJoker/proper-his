package com.javahis.ui.opd;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.bcel.generic.NEWARRAY;

import jdo.clp.intoPathStatisticsTool;
import jdo.odo.OpdComPackQuoteTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.sun.org.apache.bcel.internal.generic.NEW;
/**
 * 
 * <p> Title: ����ҽ������վ�������׵��� </p>
 * 
 * <p> Description:����ҽ������վ���׵��ÿ����� </p>
 * 
 * <p> Copyright: Copyright (c) Liu dongyang 2008 </p>
 * 
 * 
 * <p> Company:JavaHis </p>
 * 
 * @author ehui 2009.05.03
 * 
 * 
 * @version 1.0
 */
public class OdoComPackQuoteControl extends TControl {
	//�ż�ס��
	private String admtype;//yanjing 20130614
	//�Ʊ�ҽʦ����
	private String deptOrDr;
	//�Ʊ��ҽʦ����
	private String code,value="",DEPT="1",DR="2";
	//��ʼ��SQL
	private String INIT_SQL,INIT_DIAG_SQL,INIT_ORDER_SQL;
	//��DATASTORE
	private TDataStore dsMain,dsDiag,dsOrder,dsChn;
	//TABLE
	private TTable tableDiag,tableExa,tableMed,tableChn;
	//��ҽ����ǩcombo
	private TComboBox rxNo;
	//ģ�����combo
	private TComboBox combo;
	//�������Ĳ���
	private TParm parm,chnParm;
	
	
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		//��ʼ������
		initParameter();
		//��ʼ���¼�
		initEvent();
		//��ʼ��TABLE
		initData();
		//��ʼ��combo
		initCombo();
	}
	/**
	 * ��ʼ������
	 */
	public void initParameter(){
		parm=(TParm)this.getParameter();
		deptOrDr=parm.getValue("DEPT_OR_DR");
		code=parm.getValue("DEPTORDR_CODE");
		admtype = parm.getValue("ADM_TYPE");
		Vector v=new Vector();
		Vector title=new Vector();
		title.add("id,name");
		v.add(title);
		Vector data=new Vector();
		v.add(data);
		TComboBox deptCom=(TComboBox)this.getComponent("INITDEPT");
		TComboBox operatorCom=(TComboBox)this.getComponent("INITOPERATOR");
		TLabel label=(TLabel)this.getComponent("LABEL");
		
		if(DEPT.equalsIgnoreCase(deptOrDr)){
			label.setEnText("Dept.");
			label.setZhText("����");
			deptCom.setSelectedID(code);
			deptCom.setVisible(true);
			operatorCom.setVisible(false);
		}else{
			label.setEnText("Dr.");
			label.setZhText("ҽʦ");
			operatorCom.setSelectedID(code);
			operatorCom.setVisible(true);
			deptCom.setVisible(false);
		}
		//ȡ�ó�ʼ��SQL
		INIT_SQL=OpdComPackQuoteTool.getInstance().initQuote(parm);
	}
	/**
	 * ��ʼ���ؼ�
	 */
	public void initEvent(){
		//ģ��combo
		combo=(TComboBox)this.getComponent("PACK_CODE");
		//���TABLE
		tableDiag=(TTable)this.getComponent("TABLE_DIAG");
		//������TABLE
		tableExa=(TTable)this.getComponent("TABLE_EXA");
		//ҩƷTABLE
		tableMed=(TTable)this.getComponent("TABLE_MED");
		//��ҩTABLE
		tableChn=(TTable)this.getComponent("TABLE_CHN");
		//��ҽ����ǩcombo
		rxNo=(TComboBox)this.getComponent("RX_NO");
	}
	/**
	 * ��ʼ��������
	 */
	public void initData(){
		dsMain=new TDataStore();
		dsMain.setSQL(INIT_SQL);
		dsMain.retrieve();
	}
	/**
	 * ��ʼ����Combo
	 */
	public void initCombo(){
		//ȡ��combo������
		TParm result = OpdComPackQuoteTool.getInstance().initCombo(parm);
		if(result.getErrCode()!=0){
//			this.messageBox_(result.getErrText());
			this.messageBox_("�Ҳ�������");
			return;
		}
		combo.setParmValue(result);
		combo.onInit();
	}
	/**
	 * ��combo��ѡ�¼�
	 */
	public void onComboSelect(){
		String packCode=combo.getSelectedID();
		filtDs(packCode);
		onClear();
		this.setValue("SUBJ_TEXT", dsMain.getItemString(0, "SUBJ_TEXT"));
		this.setValue("OBJ_TEXT", dsMain.getItemString(0, "OBJ_TEXT"));
		this.setValue("PHYSEXAM_REC", dsMain.getItemString(0, "PHYSEXAM_REC"));
		TParm diagParm=new TParm();
		diagParm.setData("DEPT_OR_DR",deptOrDr);
		diagParm.setData("DEPTORDR_CODE",code);
		diagParm.setData("PACK_CODE",packCode);
		//��ʼ�����
		TParm diagResult=OpdComPackQuoteTool.getInstance().initQuoteDiag(diagParm);
		tableDiag.setParmValue(diagResult);
		//��ʼ��ҽ��
		TParm orderResult=OpdComPackQuoteTool.getInstance().initOrderForPackQuote(diagParm);
		// System.out.println("orderResult="+orderResult);
		tableMed.setParmValue(orderResult);
		//��ʼ����ҽ
		chnParm=OpdComPackQuoteTool.getInstance().initChnForPackQuote(diagParm);
		initChn(chnParm);
		//��ʼ������ҽ��
		TParm exaResult=OpdComPackQuoteTool.getInstance().initExaForPackQuote(diagParm);
		tableExa.setParmValue(exaResult);
	}
	/**
	 * ��ʼ����ҽ�����Ϣ
	 * @param parm
	 * @return
	 */
	private boolean initChn(TParm parm){
		if(!initChnComobo(parm)){
//			this.messageBox_("��ʼ������ǩʧ��");
			return false;
		}
		if(!initChnTable(parm)){
//			this.messageBox_("��ʼ����ҽ����ʧ��");
			return false;
		}
		return true;
	}
	/**
	 * ��ʼ����ҽ����ǩ���ݣ���ѡ�����һ��
	 * @param parm
	 * @return
	 */
	private boolean initChnComobo(TParm parm){
		TParm comboParm=OpdComPackQuoteTool.getInstance().initChnCombo(parm);
		if(comboParm==null){
//			this.messageBox_("��ʼ������ǩʧ��");
			return false;
		}
		
		rxNo.setParmValue(comboParm);
		String id=comboParm.getValue("ID",comboParm.getCount("ID")-1);
		rxNo.setValue(id);
		return true;
	}
	/**
	 * ��ʼ����ҽ����
	 * @param parm
	 * @return
	 */
	private boolean initChnTable(TParm parm){
		String rxNo=this.getValueString("RX_NO");
		if(StringUtil.isNullString(rxNo)){
//			this.messageBox_("ȡ�ô���ǩ��ʧ��");
			return false;
		}
		TParm tableParm=OpdComPackQuoteTool.getInstance().getChnTableParm(parm, rxNo);
		tableChn.setParmValue(tableParm);
		this.setValue("TAKE_DAYS", tableParm.getValue("TAKE_DAYS",0));
		this.setValue("DCT_TAKE_QTY", tableParm.getValue("DCT_TAKE_QTY",0));
		this.setValue("CHN_FREQ_CODE", tableParm.getValue("FREQ_CODE",0));
		this.setValue("CHN_ROUTE_CODE", tableParm.getValue("ROUTE_CODE",0));
		this.setValue("DCTAGENT_CODE", tableParm.getValue("DCTAGENT_CODE",0));
		this.setValue("DESCRIPTION", tableParm.getValue("DESCRIPTION",0));
		return true;
	}
	/**
	 * ds����code�����¼�
	 * @param code String :PACK_CODE
	 */
	public void filtDs(String code){
		String filterString="PACK_CODE='" +code+"'";
		dsMain.setFilter(filterString);
		dsMain.filter();
	}
	/**
	 * ����ǩ�ı��¼�
	 */
	public void onRxChange(){
		initChnTable(chnParm);
	}
	/**
	 * ���
	 */
	public void onClear(){
		this.setValue("SUBJ_TEXT",value);
		this.setValue("OBJ_TEX", value);
		this.setValue("PHYSEXAM_REC", value);
		this.setValue("SUB_CHECK", true);
		this.setValue("OBJ_CHECK", true);
		this.setValue("PSY_CHECK", true);
		this.setValue("ORDER_CHECK", true);
		this.setValue("DIAG_CHECK", true);
		this.setValue("EXA_CHECK", true);
		this.tableMed.removeRowAll();
		this.tableExa.removeRowAll();
		this.tableChn.removeRowAll();
		this.tableDiag.removeRowAll();
	}
	/**
	 * ���� �����Ƿ񴫻صĽ���ѡ�񣬴��ظÿؼ���ֵ
	 */
	public void onFetch(){
		TParm result=new TParm();
		//yanjing 2014-04-16 ȡ��ϱ��е�����
		tableDiag.acceptText();
		tableExa.acceptText();
		TParm diag = tableDiag.getParmValue();
		TParm exa1 = tableExa.getParmValue();
		//yanjing 20130614 ȡҩ����������
		tableMed.acceptText();
		TParm med = tableMed.getParmValue();
		//����
		if(TypeTool.getBoolean(this.getValue("SUB_CHECK"))){
			result.setData("SUBJ_TEXT",this.getValue("SUBJ_TEXT"));
		}
		//����
		if(TypeTool.getBoolean(this.getValue("OBJ_CHECK"))){
			result.setData("OBJ_TEXT",this.getValue("OBJ_TEXT"));
		}
		//����
		if(TypeTool.getBoolean(this.getValue("PSY_CHECK"))){
			result.setData("PHYSEXAM_REC",this.getValue("PHYSEXAM_REC"));
		}
		//ҽ��
		//yanjing2013-04-16 ע
//		if(TypeTool.getBoolean(this.getValue("ORDER_CHECK"))){
			TParm order=new TParm();
			TParm ctrl=new TParm();
//			TParm exa=new TParm();//yanjingע2013��04-16
			TParm op=new TParm();
//			TParm chn=new TParm();//yanjingע2013��04-16
			tableMed.acceptText();
			TParm data=tableMed.getParmValue();
			String[] column=data.getNames();
			int count=column.length;
			int index=data.getCount("RX_TYPE");
		for (int i = 0; i < index; i++) {
			// yanjing2013-04-16�����Ƿ�ѡ��У��
			if (data.getValue("CHOOSE", i).equals("Y")) {
				//ҩ�����ֵ䵵�Ƿ����У�飬yanjing��2013/7/1
				if(med.getValue("ORDER_CODE",i).equals("")){
					this.messageBox(med.getValue("ORDER_DESC", i)+",���ֵ䵵�����ڣ�");
					return;
				}
				//ҩ���Ƿ�ͣ��У��
				if(med.getValue("ACTIVE_FLG",i).equals("N")){
					this.messageBox((med.getValue("ORDER_DESC", i)+",��ͣ�ã�"));
					return;
				}
				// add by yanj 2013/06/14�ż�������У��
				// ����
				if ("O".equalsIgnoreCase(admtype)) {
					// �ж��Ƿ��ż�������ҽ��
					if (!("Y".equals(med.getValue("OPD_FIT_FLG",i)))) {
						// ������������ҽ����
						this.messageBox(med.getValue("ORDER_DESC", i)+",������������ҽ����");
						return;
					}
				}
				// ����
				if ("E".equalsIgnoreCase(admtype)) {
					if (!("Y".equals(med.getValue("EMG_FIT_FLG",i)))) {
						// ������������ҽ����
						this.messageBox(med.getValue("ORDER_DESC", i)+",���Ǽ�������ҽ����");
						return;
					}
				}
				// $$===========add by yanj 2013/06/14�ż�������У��
				if ("1".equalsIgnoreCase(data.getValue("RX_TYPE", i))) {
					for (int j = 0; j < count; j++) {
						order.addData(column[j], data.getValue(column[j], i));
					}
				} else if ("2".equalsIgnoreCase(data.getValue("RX_TYPE", i))) {
					for (int j = 0; j < count; j++) {
						ctrl.addData(column[j], data.getValue(column[j], i));
					}
				} else if ("4".equalsIgnoreCase(data.getValue("RX_TYPE", i))) {
					for (int j = 0; j < count; j++) {
						op.addData(column[j], data.getValue(column[j], i));
					}
				}
			}
		}
			result.setData("ORDER",order);
			result.setData("CTRL",ctrl);
			result.setData("OP",op);
			
			result.setData("CHN",getChnMap());
//		}
		//���
//		if(TypeTool.getBoolean(this.getValue("DIAG_CHECK"))){
//			result.setData("DIAG",tableDiag.getParmValue());
//		}
		//=====yanj 2014-04-16
		TParm medParm=new TParm();
		int medCount=0;//�ۼƸ���
			//=====2013-04-16 yanj ����Ҫ������ϵ�У��
			for (int i = 0; i < diag.getCount(); i++) {
				if (diag.getValue("CHOOSE",i).equals("Y")){						
					medParm.setRowData(medCount, diag,i);
					medCount++;
					
					}
			}
			medParm.setCount(medCount);
				result.setData("DIAG",medParm);
		//���ü�����
//		if(TypeTool.getBoolean(this.getValue("EXA_CHECK"))){
//			result.setData("EXA",tableExa.getParmValue());
//		}
				//=====yanj 2014-04-16
				TParm exaParm=new TParm();
				int exaCount=0;//�ۼƸ���
					//=====2013-04-16 yanj ����Ҫ���ؼ������У��
//				System.out.println("������ exa1 is������"+exa1);
					for (int i = 0; i < exa1.getCount(); i++) {
						if (exa1.getValue("CHOOSE",i).equals("Y")){	
							//ҩ�����ֵ䵵�Ƿ����У�飬yanjing��2013/7/1
							if(exa1.getValue("ORDER_CODE",i).equals("")){
								this.messageBox(exa1.getValue("ORDER_DESC", i)+",���ֵ䵵�����ڣ�");
								return;
							}
							//ҩ���Ƿ�ͣ��У��
							if(exa1.getValue("ACTIVE_FLG",i).equals("N")){
								this.messageBox((exa1.getValue("ORDER_DESC", i)+",��ͣ�ã�"));
								return;
							}
							
							// add by yanj 2013/07/17�ż�������У��
							// ����
							if ("O".equalsIgnoreCase(admtype)) {
								// �ж��Ƿ��ż�������ҽ��
								if (!("Y".equals(exa1.getValue("OPD_FIT_FLG",i)))) {
									// ������������ҽ����
									this.messageBox(exa1.getValue("ORDER_DESC", i)+",�����������ü����顣");
									return;
								}
							}
							// ����
							if ("E".equalsIgnoreCase(admtype)) {
								if (!("Y".equals(exa1.getValue("EMG_FIT_FLG",i)))) {
									// ������������ҽ����
									this.messageBox(exa1.getValue("ORDER_DESC", i)+",���Ǽ������ü����顣");
									return;
								}
							}
							// $$===========add by yanj 2013/06/14�ż�������У��
							exaParm.setRowData(exaCount, exa1,i);
							exaCount++;
							
							}
					}
					exaParm.setCount(exaCount);
						result.setData("EXA",exaParm);
		this.setReturnValue(result);
		this.closeWindow();
		}	
	/**
	 * ����ҽ�������������Ǵ����ţ�ֵ��TParm��ͬ���þ����¼��ʽ��ͬ�Ļش������Ա�ҽ��վ����ס����
	 * @return
	 */
	private Map getChnMap(){
		Map result=new HashMap();
		if(chnParm==null){
			return null;
		}
		int count=chnParm.getCount();
		if(count<1){
			return null;
		}
		Vector prspNo=chnParm.getVectorValue("PRESRT_NO");
		if(prspNo==null){
			return null;
		}
		if(prspNo.size()<1){
			return null;
		}
		TParm parm=new TParm();
		String[] names=chnParm.getNames();
		StringBuffer nameSb=new StringBuffer();
		for(String temp:names){
			nameSb.append(temp).append(";");
		}
		String name=nameSb.deleteCharAt(nameSb.length()-1).toString();
		// System.out.println("names=========="+name);
		int nameLength=names.length;
		for(int i=0;i<count;i++){
			String presrtNo=chnParm.getValue("PRESRT_NO",i);
			if(result.get(presrtNo)==null){
				parm=new TParm();
				result.put(presrtNo, parm);
			}
			parm.addRowData(chnParm, i, name);
		}
		return result;
	}
	//yabjing 20130416����ȫѡ����
			/**
			 * ȫѡ�¼�
			 * @param parm
			 */
			public void onSelAll(int tag) {
				TParm parm = new TParm();
				TParm tableParm = new TParm();
				switch (tag) {
				case 0:
					tableParm = tableDiag.getParmValue();
					parm.setData("CHECK", getValue("DIAG_CHECK"));
					break;
				case 1:
					tableParm = tableExa.getParmValue();
					parm.setData("CHECK", getValue("EXA_CHECK"));
					break;
				case 2:	
					tableParm = tableMed.getParmValue();
					parm.setData("CHECK", getValue("ORDER_CHECK"));
					break;
				default:
					break;
				}
		        for (int i = 0; i < tableParm.getCount(); i++) {
		            tableParm.setData("CHOOSE", i, parm.getValue("CHECK"));
		        }
		        if (tag == 0) {
					tableDiag.setParmValue(tableParm);
				}else if (tag == 1) {
					tableExa.setParmValue(tableParm);
				}else if (tag == 2) {
					tableMed.setParmValue(tableParm);
				}
		        return ;
			}
			
}