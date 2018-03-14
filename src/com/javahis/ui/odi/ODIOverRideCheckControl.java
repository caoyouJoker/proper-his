package com.javahis.ui.odi;

import java.util.List;
import java.util.Map;

import jdo.adm.ADMInpTool;
import jdo.opd.TotQtyTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ����ҩ��ҽ��Խ�����
 * </p>
 * 
 * <p>
 * Description: ����ҩ��ҽ��Խ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c) 2014��2��
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author Miracle
 * @version JavaHis 1.0
 */
public class ODIOverRideCheckControl extends TControl {
	private String caseNo = "";
	private String mrNo = "";
	private String ipdNo = "";
	private String bedNo = "";
	private String stationNo = "";
	private String deptCode = "";
	private String vsDcCode = "";
	private TTable table;
	private String orgCode = "";

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		table = getTable("TABLE1");
		// //���ܴ���Ĳ���
		TParm outsideParm = (TParm) this.getParameter();
		mrNo = outsideParm.getValue("INW", "MR_NO");// ��������
		caseNo = outsideParm.getValue("INW", "CASE_NO");// �����
		this.onQuery();
		ipdNo = outsideParm.getValue("INW", "IPD_NO");// סԺ��
		bedNo = outsideParm.getValue("INW", "BED_NO");// ����
		stationNo = outsideParm.getValue("INW", "STATION_CODE");// ����
		deptCode = outsideParm.getValue("INW", "DEPT_CODE");// ����
		vsDcCode = outsideParm.getValue("INW", "VS_DR_CODE");// ����ҽ��
		orgCode = (((TParm) this.getParameter()).getData("ODI", "ORG_CODE")
				.toString());
		addEventListener("TABLE1" + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValue");
		// ����ҽ��CHECK_BOX�����¼�
		getTable("TABLE1").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
				this, "onCheckBoxValue");

	}

	/**
	 * �޸��¼�
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onChangeTableValue(Object obj) {
		TTableNode tNode = (TTableNode) obj;
		if (tNode == null)
			return true;
		TParm tableParm = table.getParmValue();
		// �жϵ�ǰ���Ƿ���ҽ��
		int row = tNode.getRow();
		// �õ�table�ϵ�parmmap������
		String columnName = tNode.getTable().getDataStoreColumnName(
				tNode.getColumn());
		if (columnName.equals("CHOOSE_FLG")) {
			int rowCount = tableParm.getCount("ORDER_DESC");
			String linkNo = tableParm.getValue("LINK_NO",row);
			for (int i = 0; i < rowCount; i++) {
				if (!"".equals(linkNo)&&!linkNo.equals(null)&&
						linkNo.equals(tableParm.getValue("LINK_NO", i))&&i!= row) {
					tableParm.setData("CHOOSE_FLG", i, tableParm.getBoolean("CHOOSE_FLG", row) ? "N"
							: "Y");
				}
			}
			tableParm.setData("CHOOSE_FLG", row, tableParm.getBoolean("CHOOSE_FLG", row) ? "N"
					: "Y");
			table.setParmValue(tableParm);
			String flg = tableParm.getValue(columnName, row);
			int newRow = row;
			if (flg.equals("Y")) {
				if(!tableParm.getValue("LCS_CLASS_CODE", row).equals(null)&&
						!"".equals(tableParm.getValue("LCS_CLASS_CODE", row))){//����ҩ��ʱ
					newRow = row;
				}else{
					//�ǿ���ҩ��ʱȡ�����ҩ���ͣ��ʱ��
					//ȡ��link_no�ҳ���Ӧ������
					String fLinkNo = tableParm.getValue("LINK_NO", row);
					for(int m = 0;m<tableParm.getCount();m++){
						String kLinkNo = tableParm.getValue("LINK_NO", m);
						if(fLinkNo.equals(kLinkNo)){
						String lcsClassCode  = tableParm.getValue("LCS_CLASS_CODE", m);
						if(!"".equals(lcsClassCode)&&!lcsClassCode.equals(null)){//�ҳ����ȡ��ͣ��ʱ��
							newRow = m;
						}
							 
						}
						
					}
					
				}
					TParm lcsParm = new TParm(
							getDBTool()
									.select(
											"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
													+ Operator.getID()
													+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
					String lcsClassCode = table.getParmValue().getValue(
							"LCS_CLASS_CODE", newRow);
					if (!OrderUtil.getInstance().checkLcsClassCode(lcsParm,
							Operator.getID(), "" + lcsClassCode)) {
						lcsParm.setErrCode(-1);
						// ��û�д�ҽ����֤�գ�
						this.messageBox("E0166");
						for (int i = 0; i < rowCount; i++) {
							if (!"".equals(linkNo)&&!linkNo.equals(null)&&
									linkNo.equals(tableParm.getValue("LINK_NO", i))&&i!=row) {
								tableParm.setData("CHOOSE_FLG", i, tableParm.getBoolean("CHOOSE_FLG", row) ? "N"
										: "Y");
							}
						}
						tableParm.setData("CHOOSE_FLG", row, "N");
						table.setParmValue(tableParm);
						return true;
					}	
				tableParm.setData("DC_DATE", row, StringTool.rollDate(
						SystemTool.getInstance().getDate(), tableParm.getInt(
								"ANTI_TAKE_DAYS", newRow)));// ͣ��ʱ��
				tableParm.setData("DC_DR_CODE", row, Operator.getID());// ͣ��ҽ��
				tableParm.setData("ANTIBIOTIC_WAY", row, "02");// ������ʶ
				tableParm.setData("ORDER_DR_CODE", row, Operator.getID());
//				tableParm.setData("CHOOSE_FLG", newRow, "Y");
				for (int i = 0; i < rowCount; i++) {
					if (!"".equals(linkNo)&&!linkNo.equals(null)&&
							linkNo.equals(tableParm.getValue("LINK_NO", i))) {
//						tableParm.setData("CHOOSE_FLG", row, "Y");
						tableParm.setData("DC_DATE", i, StringTool.rollDate(
								SystemTool.getInstance().getDate(), tableParm.getInt(
										"ANTI_TAKE_DAYS", newRow)));// ͣ��ʱ��
						tableParm.setData("DC_DR_CODE", i, Operator.getID());// ͣ��ҽ��
						tableParm.setData("ANTIBIOTIC_WAY", i, "02");// ������ʶ
						tableParm.setData("ORDER_DR_CODE", i, Operator.getID());
						tableParm.setData("CHOOSE_FLG", i, "Y");
					}
				}
			} else {
				tableParm.setData("DC_DATE", row, "");// ͣ��ʱ��
				tableParm.setData("DC_DR_CODE", row, "");// ͣ��ҽ��
				tableParm.setData("ANTIBIOTIC_WAY", row, "");// ������ʶ
				tableParm.setData("ORDER_DR_CODE", row, vsDcCode);
				tableParm.setData("CHOOSE_FLG", newRow, "N");
				for (int i = 0; i < rowCount; i++) {
					if (!"".equals(linkNo)&&!linkNo.equals(null)&&
							linkNo.equals(tableParm.getValue("LINK_NO", i))) {
						tableParm.setData("DC_DATE", i, "");// ͣ��ʱ��
						tableParm.setData("DC_DR_CODE", i, "");// ͣ��ҽ��
						tableParm.setData("ANTIBIOTIC_WAY", i, "");// ������ʶ
						tableParm.setData("ORDER_DR_CODE", i, vsDcCode);
						tableParm.setData("CHOOSE_FLG", i, "N");
					}
				}
			}
			table.setParmValue(tableParm);
		}
		if (columnName.equals("MEDI_QTY") || columnName.equals("FREQ_CODE")) {
			if(setValueTableParm(tableParm, row, columnName, tNode))
				return true;
			table.setParmValue(tableParm);
		}
		// this.messageBox("===============888888888===================");
		// EXEC_DEPT_CODE
		return false;
	}
	
	
	private boolean setValueTableParm(TParm tableParm,int row,String columnName,TTableNode tNode){
		if (tableParm.getValue("ORDER_CODE", row).length() == 0) {
			// ��¼��ҽ����
			// ============xueyf modify 20120217 start
			if (Float.valueOf(tableParm.getValue("MEDI_QTY", row)) > 0) {
				this.messageBox("E0157");
			}
			// ============xueyf modify 20120217 stop
			this.messageBox("000");
			return true;
		}
		if (tableParm.getValue("CAT1_TYPE", row).equals("PHA")) {
			// ����������
			if (columnName.equals("MEDI_QTY")) {
				tableParm.setData("MEDI_QTY", row, tNode.getValue());
			}
			if (columnName.equals("FREQ_CODE")) {
				// �ж�Ƶ���Ƿ��������ʱʹ��
				tableParm.setData("FREQ_CODE", row, tNode.getValue());
			}
			TParm action = this.getTempStartQty(tableParm.getRow(row));
			if (action.getErrCode() < 0) {
				// ����������
				if (action.getErrCode() == -2) {
					if (messageBox("��ʾ��Ϣ Tips",
							"����ҩƷ����������׼�Ƿ��մ���������? \n Qty Overproof",
							this.YES_NO_OPTION) != 0)
						this.messageBox("111");
						return true;
				} else {// shibl 20130123 modify ������δ�ش�
					this.messageBox(action.getErrText());
					this.messageBox("222");
					return true;
				}
			}
			tableParm.setData("MEDI_UNIT",row,action.getValue("MEDI_UNIT"));
			tableParm.setData("DISPENSE_QTY",row,action.getDouble("DISPENSE_QTY"));
			tableParm.setData("DOSAGE_UNIT",row,action.getValue("DOSAGE_UNIT"));
			tableParm.setData("DISPENSE_UNIT",row,action.getValue("DISPENSE_UNIT"));
			tableParm.setData("ACUMMEDI_QTY",row,action.getDouble("ACUMMEDI_QTY"));
			tableParm.setData("ACUMDSPN_QTY",row,action.getDouble("ACUMDSPN_QTY"));
			tableParm.setData("DOSAGE_QTY",row,action.getDouble("DOSAGE_QTY"));
			tableParm.setData("LASTDSPN_QTY",row,action.getDouble("LASTDSPN_QTY"));
			tableParm.setData("START_DTTM",row,action.getTimestamp("START_DTTM"));
		}
		this.messageBox("3333");
		return false;
	}
	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		
		String selSql = "SELECT 'N' AS MODIFY_FLG,'N' AS CHOOSE_FLG,A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,"
				+ "A.FREQ_CODE,A.ROUTE_CODE,'N' AS URGENT_FLG,'N' AS DISPENSE_FLG,"
				+ "'' AS DR_NOTE,SYSDATE AS EFF_DATEDAY,'' AS DC_DATE,'' AS NS_NOTE,"
				+ "B.INSPAY_TYPE,A.ORDER_DATE,C.DOSAGE_QTY AS DISPENSE_QTY,C.DOSAGE_UNIT,"
				+ "C.DOSAGE_UNIT AS DISPENSE_UNIT,"
				+ "ANTI_MAX_DAYS AS ANTI_TAKE_DAYS,ANTI_MAX_DAYS AS TAKE_DAYS,A.PHA_SEQ," 
				+ "A.SEQ_NO,A.ORDER_CODE,B.LCS_CLASS_CODE,C.DOSAGE_QTY,'PHA' CAT1_TYPE,'' START_DTTM "
				+ " FROM PHA_ANTI A ,SYS_FEE B ,PHA_TRANSUNIT C " 
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDER_CODE=C.ORDER_CODE "
				+ "AND A.CASE_NO = '"
				+ caseNo
				+ "' AND A.OVERRIDE_FLG = 'Y' AND A.CHECK_FLG = 'N' ORDER BY LINK_NO,LINKMAIN_FLG DESC ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(selSql));

		for (int i = 0; i < parm.getCount(); i++) {
			// ȡ������ҩ���ʹ������
			parm.setData("ANTIBIOTIC_WAY", i, "");// ������ʶ
			parm.setData("EXEC_DEPT_CODE", i, orgCode);//ִ�п���
			parm.setData("ORDER_DR_CODE", i, Operator.getID());// ����ҽ��
			parm.setData("NS_CHECK_CODE", i, "");//ȷ�ϻ�ʿ
			parm.setData("NS_CHECK_DATE", i, "");// ȷ��ʱ��
			parm.setData("DC_RSN_CODE", i, "");// ͣ��ԭ��
			parm.setData("DC_NS_CHECK_CODE", i, "");// ͣ��ȷ�ϻ�ʿ
			parm.setData("DC_NS_CHECK_DATE", i, "");// ͣ��ȷ��ʱ��
			parm.setData("ORDER_STATE", i, "N");// ҽ��״̬��N��
			parm.setData("ACUMMEDI_QTY", i, 0);// �ۻ�����
			parm.setData("ACUMDSPN_QTY", i, 0);// �ۻ���ҩ
			parm.setData("DC_DATE", i, "");// ͣ��ʱ��
			parm.setData("DC_DR_CODE", i, "");// ͣ��ҽ��
			parm.setData("ANTIBIOTIC_WAY", i, "");// ������ʶ
		}
		// Ϊ���ֵ
		table.setParmValue(parm);
	}

	/**
	 * �õ�TABLE����
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		// ȡ�ñ���ֵ
		TTable table1 = (TTable) getComponent("TABLE1");
		table1.acceptText();
		TParm tableParm = table1.getParmValue();
		TParm saveParm = new TParm();
		boolean flg = false;
		// ���ô洢�����õ�ORDER_NO
		String orderNo = SystemTool.getInstance().getNo("ALL", "ODI",
				"ORDER_NO", "ORDER_NO");
		int orderSeq = 0;
		//=====�����ŵ�����   yanjing 20140619
		String linkNo = "9999";
		String linkNoSql = "SELECT LINK_NO FROM ODI_ORDER "
			+ "WHERE CASE_NO = '" +caseNo + "' AND RX_KIND = 'UD' AND LINK_NO IS NOT NULL "
			+ " ORDER BY LINK_NO DESC  ";
//		System.out.println("action sql is :"+linkNoSql);
	    TParm linkNoResult = new TParm(TJDODBTool.getInstance().select(linkNoSql));
	    if (linkNoResult.getErrCode() < 0) {
		    return ;
	    }
	    if(linkNoResult.getCount()<=0){
	    	linkNo = "0";
	    	
	    }else{
//	    	 linkNo =  String.valueOf(Integer.parseInt(linkNoResult.getValue("LINK_NO",0))+1);
	    	linkNo = linkNoResult.getValue("LINK_NO",0);
	    }
	    String tempLinkNo = linkNo;
	    //=====�����ŵ�����   yanjing 20140619  END
	    for(int m = 0;m < tableParm.getCount();m++){//������Ĳ���
	    	String rLinkNo = tableParm.getValue("LINK_NO", m);
	    	if("".equals(rLinkNo)||rLinkNo.equals(null)){
				tempLinkNo = linkNo;
				linkNo = "";
			}else if(!tableParm.getValue("MODIFY_FLG",m).equals("N")){
				linkNo = tempLinkNo;
			}
	    	if(tableParm.getValue("CHOOSE_FLG", m).equals("Y")&&
	    			tableParm.getValue("LINKMAIN_FLG", m).equals("Y")){//�����������޸��估��ϸ���������
				//=====ȡ����������
	    		
				
				linkNo =  String.valueOf(Integer.parseInt(linkNo)+1);
				tempLinkNo = linkNo;
				//=====ѭ�������ҳ���������ͬ��ҽ�������޸�������
				for(int j = 0;j<tableParm.getCount();j++){
					String nLinkNo = tableParm.getValue("LINK_NO", j);
					if(rLinkNo.equals(nLinkNo)&&tableParm.getValue("MODIFY_FLG", j).equals("N")&&j!=m){//�޸�������
						tableParm.setData("LINK_NO", j,linkNo);
						tableParm.setData("MODIFY_FLG", j,"Y");//�޸Ĺ��ı��
					}
				}
				tableParm.setData("LINK_NO", m,linkNo);
				tableParm.setData("MODIFY_FLG", m,"Y");//�޸Ĺ��ı��
				
			}
	    	
	    }
	    
		for (int i = 0; i < tableParm.getCount(); i++) {
			// ȡ���Ƿ�ѡ��״̬
			String isSave = tableParm.getValue("CHOOSE_FLG", i);
			if (isSave.equals("Y")) {
				String bedSql = "SELECT BED_NO FROM SYS_BED WHERE BED_NO_DESC = '"
						+ bedNo + "'";
				TParm bedNoParm = new TParm(TJDODBTool.getInstance().select(
						bedSql));
				String catSql = "SELECT A.CAT1_TYPE,A.ORDER_CAT1_CODE,B.ANTIBIOTIC_CODE " +
						"FROM SYS_FEE A ,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.ORDER_CODE = '"
						+ tableParm.getValue("ORDER_CODE", i) + "'";
				TParm catTypeParm = new TParm(TJDODBTool.getInstance().select(
						catSql));
				flg = true;
				// ȡ�����е�����
				saveParm = tableParm.getRow(i);
				saveParm.setData("REGION_CODE", Operator.getRegion());
				saveParm.setData("ORDER_NO", orderNo);
				saveParm.setData("ANTIBIOTIC_CODE", catTypeParm.getValue(
						"ANTIBIOTIC_CODE", 0));
				saveParm.setData("ORDER_SEQ", ++orderSeq);
				saveParm.setData("CASE_NO", caseNo);//
				saveParm.setData("MR_NO", mrNo);//
				saveParm.setData("IPD_NO", ipdNo);// סԺ��
				saveParm.setData("BED_NO", bedNoParm.getValue("BED_NO", 0));// ����
				saveParm.setData("STATION_CODE", stationNo);// ����
				saveParm.setData("RX_KIND", "UD");//
				saveParm.setData("DEPT_CODE", deptCode);//
				saveParm.setData("VS_DR_CODE", vsDcCode);//
				saveParm.setData("ORDER_DEPT_CODE", deptCode);//
				saveParm.setData("INDV_FLG", "Y");//
				saveParm.setData("HIDE_FLG", "N");//
				saveParm.setData("ORDER_CAT1_CODE", catTypeParm.getValue(
						"ORDER_CAT1_CODE", 0));//
				saveParm.setData("CAT1_TYPE", catTypeParm.getValue("CAT1_TYPE",
						0));//
				
				saveParm.setData("EXEC_DEPT_CODE", orgCode);// ִ�п���
				saveParm.setData("SETMAIN_FLG", "N");//
				saveParm.setData("CHECK_FLG", "Y");//
				saveParm.setData("USE_FLG", "Y");//
				saveParm.setData("OPT_DATE", SystemTool.getInstance().getDate());//
				saveParm.setData("OPT_TERM", Operator.getIP());//
				saveParm.setData("OPT_USER", Operator.getID());

				// ��odi_order����д���ݣ��޸�pha_anti���е�״̬λaction��
//				System.out.println("��� ���saveParm saveParm  is����"+saveParm);
				TParm result = TIOM_AppServer.executeAction(
						"action.odi.ODIAction", "onSaveToOdi", saveParm);
				if (result.getErrCode() < 0) {
					this.messageBox("����ʧ��");
					return;
				}
			}
		}
		if (!flg) {
			this.messageBox("��ѡ��Ҫ��������ݡ�");
			return;
		} else {
			this.messageBox("����ɹ���");
			onQuery() ;
			return;
		}

	}

	/**
	 * checkBoxֵ�ı��¼�
	 * 
	 * @param obj
	 */
	public boolean onCheckBoxValue(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		return true;
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		this.onQuery();
	}

	/**
	 * ������ʱ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getTempStartQty(TParm parm) {

		TParm result = new TParm();
		// ������
		parm.setData("ACUMDSPN_QTY", 0);
		// �ۼƿ�ҩ��
		parm.setData("ACUMMEDI_QTY", 0);
		String effDate = StringTool.getString(parm.getTimestamp("ORDER_DATE"),
				"yyyyMMddHHmmss");
		// �õ���ҩ���պ�����
		// List dispenseDttm =
		// TotQtyTool.getInstance().getDispenseDttmArrange(effDate);
		List dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(
				parm.getTimestamp("ORDER_DATE"));
		// System.out.println("===dispenseDttm==="+dispenseDttm);
		if (StringUtil.isNullList(dispenseDttm)) {
			result.setErrCode(-1);
			// �����д���
			result.setErrText("E0024");
			return result;
		}
		// this.messageBox("��ҩʱ������:"+dispenseDttm.get(0)+"��ҩʱ������:"+dispenseDttm.get(1));
		// ����������
		// this.messageBox_(parm);
		TParm selLevelParm = new TParm();
		selLevelParm.setData("CASE_NO", this.caseNo);
		// System.out.println(""+this.caseNo);
		// =============pangben modify 20110512 start ��Ӳ���
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			selLevelParm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop

		// System.out.println("==selLevelParm=="+selLevelParm);
		TParm selLevel = ADMInpTool.getInstance().selectall(selLevelParm);
		// System.out.println("selLevel==="+selLevel+"====="+selLevel.getValue("SERVICE_LEVEL",
		// 0));
		String level = selLevel.getValue("SERVICE_LEVEL", 0);
		parm.setData("RX_KIND", "UD");
		List startQty = TotQtyTool.getInstance().getOdiStQty(effDate,
				parm.getValue("DC_DATE"), dispenseDttm.get(0).toString(),
				dispenseDttm.get(1).toString(), parm, level);
		// this.messageBox_(startQty);
		// System.out.println(""+startQty);
		// �ײ�ʱ�� START_DTTM
		List startDate = (List) startQty.get(0);
		// System.out.println("======startDate====="+startDate);
		// ������Ҫ����//order���LASTDSPN_QTY ORDER_LASTDSPN_QTY
		// order���ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
		// order���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
		// M���dispenseQty M_DISPENSE_QTY
		// M���dispenseUnit M_DISPENSE_UNIT
		// M���dosageQty M_DOSAGE_QTY
		// M���dosageUnit M_DOSAGE_UNIT
		// D���MediQty D_MEDI_QTY
		// D���MediUnit D_MEDI_UNIT
		// D���dosageQty D_DOSAGE_QTY
		// D���dosageUnit D_DOSAGE_UNIT
		Map otherData = (Map) startQty.get(1);
		// System.out.println("===otherData==="+otherData);
		if (StringUtil.isNullList(startDate)
				&& (otherData == null || otherData.isEmpty())) {
			result.setErrCode(-1);
			result.setErrText("E0024");
			return result;
		}

		// �ײ�ʱ���
		result.setData("START_DTTM_LIST", startDate);
		// �ײ�ʱ��
		// this.messageBox_(startDate.get(0).toString()+":"+startDate.get(0).getClass());
		result.setData("START_DTTM", StringTool.getTimestamp(startDate.get(0)
				.toString(), "yyyyMMddHHmm"));
		// ������
		result.setData("FRST_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
		// �����ҩ��
		result.setData("LASTDSPN_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
		// ������
		result.setData("ACUMDSPN_QTY", otherData.get("ORDER_ACUMDSPN_QTY"));
		// �ۼƿ�ҩ��
		result.setData("ACUMMEDI_QTY", otherData.get("ORDER_ACUMMEDI_QTY"));
		// ��ҩ���� / ʵ����ҩ��������л���Ƭ��
		result.setData("DISPENSE_QTY", otherData.get("M_DISPENSE_QTY"));
		// ������λ
		result.setData("DISPENSE_UNIT", otherData.get("M_DISPENSE_UNIT"));
		// ��ҩ������ʵ�ʿۿ�����
		result.setData("DOSAGE_QTY", otherData.get("M_DOSAGE_QTY"));
		// ��ҩ��λ < ʵ�ʿۿ��� >
		result.setData("DOSAGE_UNIT", otherData.get("M_DOSAGE_UNIT"));
		// ��ҩ����
		result.setData("MEDI_QTY", otherData.get("D_MEDI_QTY"));
		// ��ҩ��λ
		result.setData("MEDI_UNIT", otherData.get("D_MEDI_UNIT"));
		// ��ҩ����
		result.setData("DOSAGE_QTY", otherData.get("D_DOSAGE_QTY"));
		// ��ҩ��λ�����䵥λ
		result.setData("DOSAGE_UNIT", otherData.get("D_DOSAGE_UNIT"));
		// ����ҩ�Ƿ���
		if (!OrderUtil.getInstance().checkKssPhaQty(parm)) { // shibl 20130123
			// modify ������δ�ش�
			result.setErrCode(-2);
			return result;
		}
		return result;
	}
}
