package com.javahis.ui.odi;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.mro.MROTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.bluecore.ca.pdf.CaPdfUtil;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;


public class DocQueryControlMain extends TControl {

	/**
	 * �����
	 */
	private static int PDF_FLG_YSH = 2;
	/**
	 * ���ύ
	 */
	private static int PDF_FLG_YTJ = 1;     
	/**
	 * δ�ύ
	 */
	private static int PDF_FLG_WTJ = -1;       
	/**
	 * ����˻�
	 */
	private static int PDF_FLG_SHTH = -2;
	/**
	 * �鵵�˻�
	 */
	private static int PDF_FLG_GDTH = -3;
	/**
	 * �ѹ鵵
	 */
	private static int PDF_FLG_YGD = 3;
	/**
	 * ���ͨ��UPDATASQL
	 */
	private String UPDATE_EXAMINE = "";
	/**
	 * ����˻�UPDATASQL
	 */
	private String UPDATE_EXAMINECANCEL = "";
	/**
	 * �ѹ鵵UPDATASQL
	 */
	private String UPDATE_FILEOK = "";
	/**
	 * �鵵�˻�UPDATASQL
	 */
	private String UPDATE_FILECANCEL = "";
	/**
	 * ��ʱĿ¼
	 */
	String tempPath = "C:\\JavaHisFile\\temp\\pdf";
	String CAPath = "C:\\JavaHisFile\\temp\\CA";
	private String KEYPATH = "C:\\CA\\"+Operator.getID()+"\\Key" ;
	private String IMAGEPATH = "C:\\CA\\"+Operator.getID()+"\\Image" ;
	/**String tempPath = TConfig.getSystemValue("FileServer.Main.Root")
			+ "\\temp\\pdf";**/
	
//	String tempPath ="C:\\JavaHisFile\\temp\\pdf";

	// =================������==============add by wanglong 20120921
	private BILComparator compare = new BILComparator();
	private int sortColumn = -1;
	private boolean ascending = false;
	private String caSwitch;// ����ǩ�¿���
	
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		 callFunction("UI|PRINT|setEnabled", false);
		 callFunction("UI|PRINT_DR_CODE|setEnabled", false);
		 callFunction("UI|PRINT_DATE|setEnabled", false);
		 callFunction("UI|PrintSave|setEnabled", false);  
		if(this.getPopedem("MEDICALRECORD")){//������
//			((TRadioButton)this.getComponent("radioStatusWTJ")).setVisible(true);
//			((TRadioButton) this.getComponent("radioStatusYTJ")).setVisible(true);
//			((TRadioButton) this.getComponent("radioStatusSHTH")).setVisible(true);
//			((TRadioButton) this.getComponent("radioStatusYSH")).setVisible(true);
//			((TRadioButton) this.getComponent("radioStatusGDTH")).setVisible(true);
//			((TRadioButton) this.getComponent("radioStatusYGD")).setVisible(true);
			((TMenuItem)this.getComponent("examine")).setVisible(false);
			((TMenuItem)this.getComponent("examineCancel")).setVisible(false);
			((TRadioButton) this.getComponent("radioStatusYSH")).setSelected(true);
//			((TLabel) this.getComponent("CA_SIGN_FLG_LABEL")).setVisible(true);
//			((TComboBox) this.getComponent("CA_SIGN_FLG_COMBO")).setVisible(true);
		}
		if(this.getPopedem("DR")){//ҽʦ
			((TRadioButton) this.getComponent("radioStatusYTJ")).setSelected(true);
//			((TRadioButton) this.getComponent("radioStatusYSH")).setVisible(false);
//			((TRadioButton) this.getComponent("radioStatusGDTH")).setVisible(fFalse);
			((TRadioButton) this.getComponent("radioStatusYGD")).setVisible(false);
			((TMenuItem)this.getComponent("fileOK")).setVisible(false);
			((TMenuItem)this.getComponent("fileCancel")).setVisible(false);
			((TMenuItem)this.getComponent("addFile")).setVisible(false);
			((TLabel) this.getComponent("CA_SIGN_FLG_LABEL")).setVisible(false);
			((TComboBox) this.getComponent("CA_SIGN_FLG_COMBO")).setVisible(false);
			((TLabel) this.getComponent("HP_MERGE_STATUS_LABEL")).setVisible(false);
			((TComboBox) this.getComponent("HP_MERGE_STATUS_COMBO")).setVisible(false);
			
			((TMenuItem)this.getComponent("emr")).setVisible(false);
			
		}
		              
		// ��ʼ��ҳ��
		UPDATE_EXAMINE = "UPDATE MRO_MRV_TECH SET CHECK_FLG=" + PDF_FLG_YSH
				+ ", CHECK_CODE='" + Operator.getID() + "' ,CHECK_DATE=SYSDATE";
		UPDATE_EXAMINECANCEL = "UPDATE MRO_MRV_TECH SET CHECK_FLG="
				+ PDF_FLG_SHTH + ", CHECK_CODE='" + Operator.getID()
				+ "' ,CHECK_DATE=SYSDATE";
		UPDATE_FILEOK = "UPDATE MRO_MRV_TECH SET CHECK_FLG=" + PDF_FLG_YGD
				+ ", ARCHIVE_CODE='" + Operator.getID()
				+ "' ,ARCHIVE_DATE=SYSDATE";
		// modify by wangbin 20150424 �鵵�˻�ʱ�����������Ա���ݣ�ֻ���²�������Ϣ
		UPDATE_FILECANCEL = "UPDATE MRO_MRV_TECH SET CHECK_FLG=" + PDF_FLG_GDTH
				+ ", OPT_USER='" + Operator.getID() + "' ,OPT_DATE=SYSDATE,OPT_TERM='"+ Operator.getIP() +"'";
		TTable table = (TTable) this.getComponent("TABLE");//add by wanglong 20120921 ������
		addSortListener(table);//add by wanglong 20120921 ������
//		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
//		"onCheckBox");    //    add  by  chenx  ��Ӹ�ѡ��ѡ���¼�
		caSwitch = TConfig.getSystemValue("CA.SWITCH");
	}

	/**     
	 * ��ѯ
	 */
	public void onQuery() {
		String mrNo = getValueString("MR_NO");
		if (mrNo.length() > 0) {
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			setValue("MR_NO", mrNo);
			// modify by huangtt 20160929 EMPI���߲�����ʾ start
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
				mrNo= pat.getMrNo();
				setValue("MR_NO", mrNo);
			}	
			// modify by huangtt 20160929 EMPI���߲�����ʾ start
			
		}
		TTable table = (TTable) this.getComponent("TABLE");
		// ���ò���
		//MRO_RECORD B,MRO_MRV_TECH C
		String SQL = "select 'N' AS FLG,A.MR_NO,B.PAT_NAME,B.SEX SEX_CODE,B.BIRTH_DATE,B.IN_DATE,B.OUT_DATE AS DS_DATE," + //modify by wanglong 20120921 �޸����ڴ�mro_record���в�ѯ
				"A.DS_DEPT_CODE IN_DEPT_CODE,A.VS_DR_CODE,A.IPD_NO,"
				+ "A.CASE_NO,C.CHECK_FLG,C.MERGE_CODE,C.MERGE_DATE,C.SUBMIT_CODE,C.SUBMIT_DATE,C.CHECK_CODE,C.CHECK_DATE,C.ARCHIVE_CODE,"
				+ "C.ARCHIVE_DATE ,TRUNC(C.CHECK_DATE, 'DD')   -   TRUNC(A.DS_DATE, 'DD') AS DAYS,C.PRINT_USER AS OPT_USER,C.PRINT_DATE  AS OPT_DATE,C.CA_SIGN_FLG,"
				+ "C.HP_MERGE_CODE,C.HP_MERGE_DATE,C.COPYING_RESERVATION_FLG "
				+ " FROM ADM_INP A,MRO_RECORD B,MRO_MRV_TECH C where A.MR_NO=B.MR_NO " +
				  " AND  A.MR_NO=C.MR_NO(+) AND A.CASE_NO=B.CASE_NO AND A.CASE_NO=C.CASE_NO(+) AND CANCEL_FLG ='N' ";
		String MR_NO = ((TTextField) this.getComponent("MR_NO")).getValue();
		String sreachType = ((TComboBox) this.getComponent("sreachType"))
				.getValue();     
		String inStartDate = ((TTextFormat) this.getComponent("inStartDate"))
				.getText();
		String inEndDate = ((TTextFormat) this.getComponent("inEndDate")).getText();
		String outStartDate = ((TTextFormat) this.getComponent("outStartDate"))
		.getText();
		String outEndDate = ((TTextFormat) this.getComponent("outEndDate")).getText();
		String DEPT_CODE = ((TTextFormat) this.getComponent("DEPT_CODE"))
				.getComboValue();
		String filterSQL = "  ";
		if (!StringUtil.isNullString(MR_NO)) {
			filterSQL += " AND A.MR_NO='" + MR_NO + "'";
		}

		if (!StringUtil.isNullString(inStartDate)) {
			filterSQL += "  AND B.IN_DATE>=TO_DATE('" + inStartDate //modify by wanglong 20120921 �޸����ڴ�mro_record���в�ѯ
					+ " 00:00:00', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(inEndDate)) {
			filterSQL += "  AND B.IN_DATE<=TO_DATE('" + inEndDate //modify by wanglong 20120921 �޸����ڴ�mro_record���в�ѯ
					+ " 23:59:59', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(outStartDate)) {
			filterSQL += "  AND B.OUT_DATE>=TO_DATE('" + outStartDate //modify by wanglong 20120921 �޸����ڴ�mro_record���в�ѯ
			+ " 00:00:00', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(outEndDate)) {
			filterSQL += "  AND B.OUT_DATE<=TO_DATE('" + outEndDate //modify by wanglong 20120921 �޸����ڴ�mro_record���в�ѯ
			+ " 23:59:59', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(DEPT_CODE)) {
			filterSQL += "  AND A.DS_DEPT_CODE='" + DEPT_CODE + "'";
		}

		if (sreachType.equals("0")) {// δ��Ժ
			filterSQL += " AND B.OUT_DATE IS NULL ";
		} else if (sreachType.equals("1")) {// ��Ժ/δ���
			filterSQL += " AND B.OUT_DATE IS NOT NULL "
					+ " AND (B.ADMCHK_FLG<>'Y' OR B.DIAGCHK_FLG<>'Y' OR B.BILCHK_FLG<>'Y' OR B.QTYCHK_FLG<>'Y')";
		} else if (sreachType.equals("2")) {// ��Ժ/�����
			filterSQL += " AND B.OUT_DATE IS NOT NULL "
					+ " AND B.ADMCHK_FLG='Y' " + " AND B.DIAGCHK_FLG='Y' "
					+ " AND B.BILCHK_FLG='Y' " + " AND B.QTYCHK_FLG='Y' ";
		}
         
		int status = getStatusFlg();
		if (status != 0) {
			if(status==PDF_FLG_WTJ){
				filterSQL += "  AND C.CHECK_FLG IS NULL ";
			}else{
				filterSQL += "  AND C.CHECK_FLG=" + status + "";
			}
		}
		
		// add by wangb 2016/5/17 ����ǩ�¹��� START
		String caSignFlg = ((TComboBox)this.getComponent("CA_SIGN_FLG_COMBO")).getSelectedID();
		if (StringUtils.isNotEmpty(caSignFlg)) {
			filterSQL += "  AND C.CA_SIGN_FLG = '" + caSignFlg + "' ";
		}
		// add by wangb 2016/5/17 ����ǩ�¹��� END
		
		// add by wangb 2017/7/10 ������ҳ״̬ START
		String hpMergeStatus = ((TComboBox)this.getComponent("HP_MERGE_STATUS_COMBO")).getSelectedID();
		if ("Y".equals(hpMergeStatus)) {
			filterSQL += "  AND C.HP_MERGE_CODE IS NOT NULL ";
		} else if ("N".equals(hpMergeStatus)) {
			filterSQL += "  AND C.HP_MERGE_CODE IS NULL ";
		}
		// add by wangb 2017/7/10 ������ҳ״̬ END
		
//		System.out.println("===sql==="+SQL + filterSQL);
		TParm result = new TParm(TJDODBTool.getInstance().select(
				SQL + filterSQL));
		int count=result.getCount("CASE_NO");
        if (count < 0) {
            ((TTextField) this.getComponent("diseaseCount")).setValue(0 + "");
        } else
		((TTextField) this.getComponent("diseaseCount")).setValue(count	+ "");
		for (int i = 0; i < count; i++) {
			String indatestr = result.getValue("ARCHIVE_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
						"yyyy-MM-dd");
				result.setData("ARCHIVE_DATE", i, date);
			}
			indatestr = result.getValue("CHECK_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
						"yyyy-MM-dd");
				result.setData("CHECK_DATE", i, date);
			}
			indatestr = result.getValue("MERGE_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
				"yyyy-MM-dd");
				result.setData("MERGE_DATE", i, date);
			}
			indatestr = result.getValue("SUBMIT_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
				"yyyy-MM-dd");
				result.setData("SUBMIT_DATE", i, date);
			}	
			indatestr = result.getValue("OPT_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
				"yyyy-MM-dd");
				result.setData("OPT_DATE", i, date);
			}	
		}
		table.setParmValue(result);
		
		TCheckBox checkAll = (TCheckBox)this.getComponent("FLG_ALL");
		checkAll.setSelected(false);
	}
	
	/**       
	 * ��ӡ��ť����¼�  
	 * 
	 * @return  void 
	 */    
	public void PrintClick() {
		if (Selected("PRINT")) {
			// �õ���ǰʱ��
			Timestamp date = SystemTool.getInstance().getDate();
			// ��ʼ����ѯ����
			setValue("PRINT_DATE", date.toString().substring(0, 10).replace(
					'-', '/'));
			setValue("PRINT_DR_CODE", Operator.getID());
		} else {
			setValue("PRINT_DATE", "");
			setValue("PRINT_DR_CODE", "");
		}

	}
	
	/**
	 * �����Żس���ѯ
	 */
	public void onPush(){
		this.onQuery() ;
	}
	
     //==================  add  by  chenxi 
	/**    
	 * ��ӡ�����¼�  
	 * 
	 * @return  void 
	 */            
	public void onPrintSave() {
		TTable table = (TTable) this.getComponent("TABLE");
		if (table == null || table.getRowCount() <= 0) {
			this.messageBox("û�б��������");
			return;
		}
		table.acceptText();
		TParm tableParm = table.getParmValue();
		
		// add by wangb 2017/12/14
		if (!tableParm.getValue("FLG").contains("Y")) {
			this.messageBox("�빴ѡҪ����������");
			return;
		}
		
		String optUser = this.getValueString("PRINT_DR_CODE");
		String optDate = this.getValueString("PRINT_DATE").substring(0,
				10);
		String caseNo = "";
		String updateSql = "";
		
		for (int i = 0; i < tableParm.getCount("MR_NO"); i++) {
			if (tableParm.getBoolean("FLG", i)) {
				caseNo = tableParm.getValue("CASE_NO", i);
				updateSql = " UPDATE MRO_MRV_TECH " + " SET  PRINT_USER='"
						+ optUser + "', " + " PRINT_DATE=TO_DATE('" + optDate
						+ "','YYYY-MM-DD HH24MISS') " + " WHERE CASE_NO = '"
						+ caseNo + "'" + " AND CHECK_FLG = " + PDF_FLG_YGD
						+ " ";
				TParm result = new TParm(TJDODBTool.getInstance().update(
						updateSql));
				if (result.getErrCode() < 0) {
					this.messageBox("E0005");
					return;
				}
			}
		}

		this.messageBox("P0005");
		this.onQuery();

	}
	/**
	 * ��ѡ��ѡ���¼�
	 * 
	 * @param obj
	 * @return
	 */
	public void onCheckBox(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int row =table.getSelectedRow() ;
		TParm parm =table.getParmValue() ;
		parm.setData("FLG", row, "Y");
	}
	//===========================  add  by  chenx  end 
	/**
	 * ��ѡ��ȫѡ�¼�
	 * 
	 * 2017.3.27
	 */
	public void onSelectAll() {
		TTable table = (TTable) this.getComponent("TABLE");
		String select=getValueString("FLG_ALL");
		TParm parm=table.getParmValue();
		int count =parm.getCount();
		for(int i=0;i<count ;i++){
			parm.setData("FLG",i,select);
		}
		table.setParmValue(parm);
	}
	//===========================  add  by  chenhj  end 	
	
	
	/**
	 * ���ز�ѯ״̬
	 * 
	 * @return
	 */
	private int getStatusFlg() {
		if (isSelected("radioStatusWTJ")) {
			return PDF_FLG_WTJ;
		} else if (isSelected("radioStatusYTJ")) {
			return PDF_FLG_YTJ;
		} else if (isSelected("radioStatusYSH")) {
			return PDF_FLG_YSH;
		} else if (isSelected("radioStatusSHTH")) {
			return PDF_FLG_SHTH;
		} else if (isSelected("radioStatusYGD")) {
			return PDF_FLG_YGD;
		} else if (isSelected("radioStatusGDTH")) {
			return PDF_FLG_GDTH;
		}
		return 0;
	}

	/**
	 * TRadioButton�ж��Ƿ�ѡ��
	 * 
	 * @param tagName
	 * @return
	 */
	private boolean isSelected(String tagName) {
		return ((TRadioButton) this.getComponent(tagName)).isSelected();
	}
	/**
	 * CheckBox�ж��Ƿ�ѡ��
	 * 
	 * @param tagName
	 * @return
	 */
	private boolean Selected(String tagName) {
		return ((TCheckBox) this.getComponent(tagName)).isSelected();
	}

	/**
	 * ���ͨ��
	 * 
	 * @return
	 */
	public void onExamine() {
		update(UPDATE_EXAMINE, PDF_FLG_YSH);
	}

	/**
	 * ����˻�
	 * 
	 * @return
	 */
	public void onexamineCancel() {
		update(UPDATE_EXAMINECANCEL, PDF_FLG_SHTH);
	}

	/**
	 * �鵵ͨ��
	 * 
	 * @return
	 */
	public void onFileOK() {
		TTable table = (TTable) this.getComponent("TABLE");
		table.acceptText();
		TParm parm = table.getParmValue();
		for (int i = 0; i < parm.getCount(); i++) {
			if (StringUtils.equals("Y", parm.getValue("FLG", i))) {
				// �Ѿ���ɼ�ǩ���ļ������ظ���ǩ
				if (PDF_FLG_YGD == parm.getInt("CHECK_FLG", i)) {
					this.messageBox("������:��" + parm.getValue("MR_NO", i)
							+ "�������Ĳ����ѹ鵵�������ظ��鵵");
					return;
				}
			}
		}
		
		update(UPDATE_FILEOK, PDF_FLG_YGD);
		
		// ��������ǩ�¿�������м�ǩ����
		if ("Y".equalsIgnoreCase(caSwitch)) {
			// �鵵ͨ��ʱ��ǩ���Ӳ���
			TParm result = this.signPdf("Y");
			
			if (result.getErrCode() < 0) {
				return;
			}
			
			String caseNoStr = "";
			int count = result.getCount("CASE_NO");
			for (int i = 0; i < count; i++) {
				caseNoStr = caseNoStr + "'" + result.getValue("CASE_NO", i) + "'";
				if (i < count - 1) {
					caseNoStr = caseNoStr + ",";
				}
			}
			
			if (StringUtils.isNotEmpty(caseNoStr)) {
				String sql = "UPDATE MRO_MRV_TECH SET CA_SIGN_FLG = 'Y' WHERE CASE_NO IN ("
						+ caseNoStr + ")";
				parm = new TParm(TJDODBTool.getInstance().update(sql));
				if (parm.getErrCode() < 0) {
					this.messageBox("����ǩ��״̬ʧ��");
					return;
				}
			}
		}
		
		this.messageBox("�����ɹ�");
		this.onQuery();
	}

	/**
	 * �鵵�˻�
	 * 
	 * @return
	 */
	public void onFileCancel() {
		update(UPDATE_FILECANCEL, PDF_FLG_GDTH);
	}

	/**
	 * �ϲ�������ҳ
	 * 
	 * @return
	 */
	public void onAddFile() {
		TTable table = (TTable) this.getComponent("TABLE");
		// ǿ��ʧȥ���༭����
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		TParm parm = table.getParmValue();
		if (table.getRowCount() < 1 || parm == null
				|| !parm.getValue("FLG").contains("Y")) {
			this.messageBox("�빴ѡ��Ҫ�ϲ���������");
			return;
		}

		int selCount = 0;
		int count = parm.getCount();
		int selRow = 0;

		TParm selParm = new TParm();
		String[] parmNames = parm.getNames();
		for (int i = 0; i < count; i++) {
			if ("Y".equals(parm.getValue("FLG", i))) {
				selRow = i;
				selCount++;
				
				for (int j = 0; j < parmNames.length; j++) {
					selParm.addData(parmNames[j], parm.getValue(parmNames[j], i));
				}
			}
		}

		selParm.setCount(selCount);
		if (selCount == 1) {
			TParm queryParm = new TParm();
			queryParm.setData("CASE_NO", selParm.getValue("CASE_NO", 0));
			// ��ѯ������ҳ�ϲ���Ϣ
			TParm result = MROTool.getInstance().queryMergeHomePageInfo(queryParm);
			if (result.getErrCode() < 0) {
				this.messageBox("��ѯ������ҳ�ϲ���Ϣ����");
				return;
			} else if (result.getCount() > 0) {
				this.messageBox("������ҳ�Ѻϲ��������ظ��ϲ�");
				return;
			}
			
			// ��һ�����Ĳ�����ҳ�ϲ�ά��ԭ��
			result = (TParm)this.openDialog("%ROOT%\\config\\odi\\ODIDocMergeHome.x", table
					.getParmValue().getRow(selRow), false);
			if (null != result && "SUCCESS".equals(result.getValue("FLG"))) {
				this.onQuery();
			}
		} else {
			String caseNo = "";
			StringBuffer errMsg = new StringBuffer();

			for (int i = 0; i < selCount; i++) {
				caseNo = caseNo + "'" + selParm.getValue("CASE_NO", i) + "'";
				if (i < selCount - 1) {
					caseNo = caseNo + ",";
				}
			}

			TParm queryParm = new TParm();
			queryParm.setData("CASE_NO_LIST", caseNo);

			// ��ѯ������ҳ����
			TParm result = MROTool.getInstance().queryHomePageInfo(queryParm);
			if (result.getErrCode() < 0) {
				this.messageBox("��ѯ������ҳ���ݴ���");
				return;
			}
			
			boolean flg = false;
			for (int i = selCount - 1; i > -1; i--) {
				flg = false;
				for (int j = 0; j < result.getCount(); j++) {
					if (StringUtils.equals(selParm.getValue("CASE_NO", i),
							result.getValue("CASE_NO", j))) {
						flg = true;
						break;
					}
				}

				if (!flg) {
					errMsg.append("�����š�" + selParm.getValue("MR_NO", i)
							+ "����δ��ӡ������ҳ�����β��账��\r\n");
					selParm.removeRow(i);
				}
			}

			// ��ѯ������ҳ�ϲ���Ϣ,����ɲ�����ҳ�ϲ��Ĳ��������ٴκϲ�
			result = MROTool.getInstance().queryMergeHomePageInfo(queryParm);
			if (result.getErrCode() < 0) {
				this.messageBox("��ѯ������ҳ�ϲ���Ϣ����");
				return;
			} else if (result.getCount() > 0) {
				// ���δ���ɸ�����Ѿ���ɺϲ�������
				for (int i = 0; i < result.getCount(); i++) {
					errMsg.append("�����š�" + result.getValue("MR_NO", i)
							+ "������ɲ�����ҳ�ϲ������β��账��\r\n");
					for (int j = selCount - 1; j > -1; j--) {
						if (StringUtils.equals(selParm.getValue("CASE_NO", j),
								result.getValue("CASE_NO", i))) {
							selParm.removeRow(j);
							break;
						}
					}
				}
			}

			if (errMsg.length() > 0) {
				this.messageBox(errMsg.toString());
			}

			if (selParm.getCount() > 0) {
				result = (TParm)this.openDialog("%ROOT%\\config\\mro\\MROHomePageBatchMerge.x",
						selParm, false);
				if (null != result && "SUCCESS".equals(result.getValue("FLG"))) {
					this.onQuery();
				}
			}
		}
	}

	/**
	 * ����״̬
	 * 
	 * @return
	 */
	public void update(String updateType, int flg) {
		TTable table = (TTable) this.getComponent("TABLE");
		table.acceptText();
		String filterSQL = "";
		TParm parm = table.getParmValue();
		int count = parm.getCount("FLG");
		String caseNo = "";
		
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("FLG", i)) {
				if (filterSQL.equals("")) {
					filterSQL += " WHERE (MR_NO='" + parm.getValue("MR_NO", i)
							+ "' AND CASE_NO='" + parm.getValue("CASE_NO", i)
							+ "')";
				} else {
					filterSQL += " OR (MR_NO='" + parm.getValue("MR_NO", i)
							+ "' AND CASE_NO='" + parm.getValue("CASE_NO", i)
							+ "')";
				}
				
				caseNo = caseNo + "'" + parm.getValue("CASE_NO", i) + "',";
			}
		}
		if (filterSQL.equals("")) {
			this.messageBox("����ѡ����Ҫ�����Ĳ�����");
			return;
		}
		parm = new TParm(TJDODBTool.getInstance()
				.update(updateType + filterSQL));
		if (parm.getErrCode() < 0) {
			this.messageBox_("����״̬ʧ�ܡ�");
			return;
		}
		
		if (caseNo.length() > 0) {
			caseNo = caseNo.substring(0, caseNo.length() - 1);
		}
		// add by wangb 2017/07/10 �鵵�˻�ʱ����ϲ���Ϣ
		if (PDF_FLG_GDTH == flg) {
			String optId = Operator.getID();
			String optIp = Operator.getIP();
			TParm updateParm = new TParm();
			updateParm.setData("CASE_NO_LIST", caseNo);
			updateParm.setData("HP_MERGE_CODE", "");
			updateParm.setData("HP_MERGE_DATE", null);
			updateParm.setData("OPT_USER", optId);
			updateParm.setData("OPT_TERM", optIp);
			// ���������ҳ�ϲ���Ϣ
			updateParm = MROTool.getInstance()
					.updateMergeHomePageInfo(updateParm);

			if (updateParm.getErrCode() < 0) {
				this.messageBox("���²�����ҳ�ϲ���Ϣʧ��");
			}
		}
		
		// �鵵ͨ����ˢ��
		if (PDF_FLG_YGD == flg) {
			return;
		}
		
		this.messageBox_("�����ɹ���");
		onQuery();
	}

	/**
	 * ���
	 */
	public void onReaderSubmitPDF() {
		TTable table = (TTable) this.getComponent("TABLE");
		int col = table.getSelectedColumn();
		if (col == 0) {
			return;
		}
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		String MR_NO = parm.getValue("MR_NO");
		String caseNo = parm.getValue("CASE_NO");
		String bigFilePath = TConfig
		.getSystemValue("FileServer.Main.Root")
				+ "\\��ʽ����\\" + MR_NO.substring(0, 7) + "\\" + MR_NO + "\\"
				+ caseNo + ".pdf";
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
				.getSocket(),bigFilePath);
		if (data == null) {
			messageBox_("��δ�ύPDF");
			return;
		}
		try {
			FileTool.setByte(tempPath + "\\" + caseNo + ".pdf", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runtime runtime = Runtime.getRuntime();
		try {
			// ���ļ�
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + caseNo + ".pdf");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	/**
	 * ����EXECL
	 */
	public void onExecl() {
		ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE"),
				"�����鵵");
	}/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {    
		return (TTable) this.getComponent(tag);
	}
	
	// ====================������begin======================add by wanglong 20120921
	/**
	 * �����������������
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// �����ͬ�У���ת����
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// ȡ�ñ��е�����
				String columnName[] = tableData.getNames("Data");// �������
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // ������������;
				int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * �����������ݣ���TParmתΪVector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
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
	 * ����ָ���������������е�index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * �����������ݣ���Vectorת��Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */

	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================������end======================
//=====================================================   chenxi modify  20130410  CA����ǩ��
	//�ϴ�
	public void onUpLoad()
	{
		String caseNo;
		String MR_NO;
		TTable table = (TTable)getComponent("TABLE");
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		if (table.getSelectedRow() < 0 || table == null)
		{
			messageBox("û��ѡ�в���");
			return;
		}
		String sreachType = ((TComboBox)getComponent("sreachType")).getValue();
		caseNo = parm.getValue("CASE_NO");
		MR_NO = parm.getValue("MR_NO");
		int status = getStatusFlg(); 
		if (sreachType.equals("2") || status == 3){	
		String dir;
		String localPath = CAPath + "\\" + caseNo + ".pdf" ;   //ǩ��֮�󱣴�ı���·��
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), localPath);
		String dirpath = TConfig.getSystemValue("FileServer.Main.Root")
		                   + "\\��ʽ����\\" + MR_NO.substring(0, 7) + "\\" + MR_NO ;  //�ϴ�����������·��
			  dir = dirpath + "\\"+caseNo + ".pdf" ;
		File filepath = new File(dirpath);
		filepath = new File(localPath);
		if (!filepath.exists())
		{
			messageBox("�ò���pdf�ļ���δǩ��,�����ϴ�");
			return;
		}
		if (!filepath.exists())
			filepath.mkdirs();
		try
		{
			TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir, data);
			messageBox("�ϴ��ɹ�");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		}
		else {
			messageBox("�����,�ѹ鵵��pdf�ļ������ϴ�");
			return;
		}
		
	}
   //  ����ǩ����֤
	@SuppressWarnings("static-access")
	public void onInPassword()
	{	
		TTable table = (TTable)getComponent("TABLE");
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		if (table.getSelectedRow() < 0 || table == null)
		{
			messageBox("û��ѡ�в���");
			return;
		}
		String caseNo = parm.getValue("CASE_NO");
		String MR_NO = parm.getValue("MR_NO");
		CaPdfUtil caPdf = new CaPdfUtil();
		String keyPath = KEYPATH+"\\"+"key.pfx";//-=====��Կ·��
		String  sourcePdfPath = tempPath + "\\" + caseNo + ".pdf" ; ;//��ǩ��PDF·��
		String signPdfPath = CAPath + "\\" + caseNo + ".pdf" ; ;//��ǩ����PDF·��
		String signImagePath = IMAGEPATH+"\\"+"Image.gif";//ǩ��ʹ�õ�ͼƬ·��
		String reason = Operator.getName();//��������1
		String location = SystemTool.getInstance().getDate().toString().substring(0, 19);  //��������2
		String bigFilePath = TConfig
		.getSystemValue("FileServer.Main.Root")
				+ "\\��ʽ����\\" + MR_NO.substring(0, 7) + "\\" + MR_NO + "\\"
				+ caseNo + ".pdf";
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
				.getSocket(),bigFilePath);
		if (data == null) {
			messageBox_("��δ�ύPDF,����ǩ��");
			return;
		}
		String password = (String)openDialog("%ROOT%\\config\\mro\\MROInPassword.x");//	����    
		//  =====�����ļ���
		File file = new File(KEYPATH) ;
		if(!file.exists()){
			file.mkdirs() ;
		}
		file = new File(IMAGEPATH) ;
		if(!file.exists()){
			file.mkdirs() ;
		}
		file = new File(CAPath) ;
		if(!file.exists()){
			file.mkdirs() ;
		}
		//�ж�ͼƬ����Կ�Ƿ���ڣ���������Ӧ����ʾ
		file =  new File(keyPath) ;
		if(!file.exists()){
			this.messageBox("��Կ�����ڣ��뽫�俽����"+KEYPATH+"������Ϊkey.pfx") ;
			return ;
		}
		file =  new File(signImagePath) ;
		if(!file.exists()){
			this.messageBox("ͼƬ�����ڣ��뽫�俽����"+KEYPATH+"������ΪImage.pfx") ;
			return ;
		}  
		if (password == null)
			return;
		try
		{
			caPdf.doPdfSign(keyPath, password, sourcePdfPath, signPdfPath, signImagePath, reason, location);
			messageBox("ǩ���ɹ�");
		}
		catch (Exception e)
		{
			System.out.println(e.toString());   
		}
		return;
	}
//	public static void main(String[] args) {
//		String keyPath = "C:\\test20121015.pfx";//-=====��Կ·��
//		String  sourcePdfPath = "C:\\1.pdf" ; ;//��ǩ��PDF·��
//		String signPdfPath = "C:\\2.pdf" ; ;//��ǩ����PDF·��
//		String signImagePath = "C:\\1.gif";//ǩ��ʹ�õ�ͼƬ·��
//		String reason = "1";//��������1
//		String location = "2";  //��������2           
//		String password = "111111";//	����
//		System.out.println("1111111111====222222=��Կ·��====="+keyPath);
//		System.out.println("1111111112=====��ǩ��PDF·��====="+sourcePdfPath);     
//		System.out.println("1111111113=====��ǩ����PDF·��====="+signPdfPath);     
//		System.out.println("1111111114===ǩ��ʹ�õ�ͼ====Ƭ·��======="+signImagePath);
//		System.out.println("1111111115====��������1==5ewewe==="+reason);   
//		System.out.println("1111111116====��������2======"+location);  
//		System.out.println("1111111116===3232==����==rerer==="+password);  
//		System.out.println("1111111111=====��Կ·��====="+keyPath);
//		System.out.println("1111111112=====��ǩ��PDF·��====="+sourcePdfPath);
//		System.out.println("1111111113=====��ǩ����PDF·��====="+signPdfPath);
//		System.out.println("1111111114===ǩ��ʹ�õ�ͼƬ·��======="+signImagePath);
//		System.out.println("1111111115====��������1====="+reason);    
//		System.out.println("1111111116====��������2======"+location);
//		System.out.println("1111111116=====����====="+password);      
//		CaPdfUtil.doPdfSign(keyPath, password, sourcePdfPath, signPdfPath, signImagePath, reason, location);
//	}
	
	/**
	 * ������ǩCA����ǩ��
	 * 
	 * @author wangbin
	 */
	public TParm signPdf(String flag) {
		TParm result = new TParm();
		if (!"Y".equalsIgnoreCase(caSwitch)) {
			result.setErr(-1, "����ǩ�¿���δ����");
			this.messageBox(result.getErrText());
			return result;
		}
		
		TTable table = (TTable) this.getComponent("TABLE");
		table.acceptText();
		TParm parm = table.getParmValue();
		if (!parm.getValue("FLG").contains("Y")) {
			this.messageBox("�빴ѡ��Ӧ��������");
			return result;
		}
		
		if (StringUtils.isEmpty(flag)) {
			for (int i = 0; i < parm.getCount(); i++) {
				if (StringUtils.equals("Y", parm.getValue("FLG", i))) {
					// �Ѿ���ɼ�ǩ���ļ������ظ���ǩ
					if ("Y".equals(parm.getValue("CA_SIGN_FLG", i))) {
						this.messageBox("������:��" + parm.getValue("MR_NO", i)
								+ "�������Ĳ����Ѽ�ǩ�������ظ���ǩ");
						return result;
					}
					
					// ֻ���ѹ鵵�Ĳ����ſ��Ե�����ǩ
					if (StringUtils.isEmpty(parm.getValue("ARCHIVE_CODE", i))
							|| (PDF_FLG_YGD != parm.getInt("CHECK_FLG", i))) {
						this.messageBox("������:��" + parm.getValue("MR_NO", i)
								+ "�������Ĳ���δ�鵵ͨ�����ɼ�ǩ");
						return result;
					}
				}
			}
		}
		
		parm.setData("X", 5);
		parm.setData("Y", 760);
		parm.setData("W", 85);
		parm.setData("H", 840);
		
		// ����CAʱ����ӿ�
		result = TIOM_AppServer.executeAction(
				"action.emr.EMRSignPdfAction", "signPdf", parm);
		
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		} else {
			// ���ǩ�°�ťʱ������ʾ
			if (StringUtils.isEmpty(flag)) {
				this.messageBox("ǩ�³ɹ�");
			}
		}
		
		return result;
	}
	
	public void onSealed() {
		String MR_NO = "";
		TTable table = (TTable) getComponent("TABLE");
		// System.out.println("selectrow====="+table.getSelectedRow());
		table.acceptText();
		if (table.getRowCount() > 0) {
			TParm parm = table.getParmValue().getRow(table.getSelectedRow());

			if (table.getSelectedRow() < 0) {
				MR_NO = "";
			} else {
				MR_NO = parm.getValue("MR_NO");
			}
		}

		TParm re = new TParm();
		re.setData("MR_NO", MR_NO);
		// System.out.println(re);
		this.openDialog("%ROOT%\\config\\mro\\MROSealedMain.x", re);
	}
	
	/**
	 * ǩ�²���չ��
	 */
	public void showSignPdf() {
		TTable table = (TTable) this.getComponent("TABLE");
		
		// ǿ��ʧȥ�༭����
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		
		TParm parm = table.getParmValue();
		if (parm == null || parm.getCount() < 1) {
			this.messageBox("�빴ѡһ������");
			return;
		}
		
		if (!parm.getValue("FLG").contains("Y")) {
			this.messageBox("�빴ѡһ������");
			return;
		}

		int count = parm.getCount();
		// CAǩ����ϴ�ŷ�����IP
		String serverIp = TConfig.getSystemValue("CA.STORE.SERVER_IP");
		if (StringUtils.isEmpty(serverIp)) {
			this.messageBox("CA����ǩ�����PDF��ŷ�������ַ���ô���");
			return;
		}
		// CAǩ����ϴ�ŷ���������·��
		String serverPath = TConfig.getSystemValue("CA.STORE.SIGN_PDF_PATH");
		if (StringUtils.isEmpty(serverPath)) {
			this.messageBox("CA����ǩ�����PDF����ļ�λ�����ô���");
			return;
		}
		// CAǩ����ϴ�ŷ������˿�
		String port = TConfig.getSystemValue("CA.STORE.FILE_SERVER_PORT");
		int serverPort = 8103;
		if (StringUtils.isNotEmpty(port)) {
			serverPort = StringTool.getInt(port);
		}
		String mrNo = "";
		String caseNo = "";
		String filePath = "";
		TSocket socket = new TSocket(serverIp, serverPort);
		byte data[] = null;
		Runtime runtime = null;

		for (int i = 0; i < count; i++) {
			if ("Y".equals(parm.getValue("FLG", i))) {
				mrNo = parm.getValue("MR_NO", i);
				caseNo = parm.getValue("CASE_NO", i);
				filePath = serverPath + File.separator + mrNo.substring(0, 7)
						+ "\\" + mrNo + "\\" + caseNo + ".pdf";
				data = TIOM_FileServer.readFile(socket, filePath);

				if (data == null) {
					messageBox("ǩ�²���������");
					return;
				}

				try {
					FileTool.setByte(tempPath + "\\" + caseNo + ".pdf", data);
				} catch (Exception e) {
					e.printStackTrace();
				}
				runtime = Runtime.getRuntime();
				try {
					// ���ļ�
					runtime.exec("rundll32 url.dll FileProtocolHandler "
							+ tempPath + "\\" + caseNo + ".pdf");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**    
	 * ��ӡԤԼ�����¼�  
	 */            
	public void onCopingReservationSave() {
		TTable table = (TTable) this.getComponent("TABLE");
		if (table == null || table.getRowCount() <= 0) {
			this.messageBox("û�б��������");
			return;
		}
		table.acceptText();
		TParm tableParm = table.getParmValue();

		if (!tableParm.getValue("FLG").contains("Y")) {
			this.messageBox("�빴ѡҪ����������");
			return;
		}

		String caseNolist = "";
		for (int i = 0; i < tableParm.getCount("CASE_NO"); i++) {
			if (tableParm.getBoolean("FLG", i)) {
				caseNolist += tableParm.getValue("CASE_NO", i) + "','";
			}
		}

		if (caseNolist.length() > 0) {
			caseNolist = caseNolist.substring(0, caseNolist.length() - 3);
		}

		String checkValue = ((TCheckBox) this
				.getComponent("COPYING_RESERVATION_CHECKBOX")).getValue();
		String sql = "UPDATE MRO_MRV_TECH SET COPYING_RESERVATION_FLG = '"
				+ checkValue + "' WHERE CASE_NO IN ('" + caseNolist + "')";

		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}

		this.messageBox("P0005");
		this.onQuery();
	}
	
	/**
	 * ����״̬�л�
	 * 
	 * @param flg ����״̬
	 */
	public void onStatusRadioBtnChange(int flg) {
		// �����鵵
		if (flg == 3) {
			 callFunction("UI|PRINT|setEnabled", true);
			 callFunction("UI|PRINT_DR_CODE|setEnabled", true);
			 callFunction("UI|PRINT_DATE|setEnabled", true);
			 callFunction("UI|PrintSave|setEnabled", true);
			 callFunction("UI|COPYING_RESERVATION_CHECKBOX|setEnabled", true);
			 callFunction("UI|COPYING_RESERVATION_BUTTON|setEnabled", true);
		} else {
			 callFunction("UI|PRINT|setEnabled", false);
			 callFunction("UI|PRINT_DR_CODE|setEnabled", false);
			 callFunction("UI|PRINT_DATE|setEnabled", false);
			 callFunction("UI|PrintSave|setEnabled", false);
			 callFunction("UI|COPYING_RESERVATION_CHECKBOX|setEnabled", false);
			 callFunction("UI|COPYING_RESERVATION_BUTTON|setEnabled", false);
		}
	}
}
