package com.javahis.ui.hrm;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import jdo.bil.BilInvoice;
import jdo.hrm.HRMBill;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMInvRcp;
import jdo.hrm.HRMOpbReceipt;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPackageD;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.hrm.HRMPrePay;
import jdo.opb.OPBReceiptTool;
import jdo.reg.REGSysParmTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatHRMCompany;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: �ɷѲ�ѯ </p>
 *
 * <p> Description: �ɷѲ�ѯ </p>
 *
 * <p> Copyright: javahis 20161213 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author wuxy
 * @version 1.0
 */
public class HRMQueryPaymentControl extends TControl {
	
	public TTable getTTable(String tag) {
		return (TTable)this.getComponent(tag);
	}

    // �շ�TABLE������TABLE��ҽ��TABLE
    private TTable feeTable, billTable, billDetailTable;
    // ��ͬ������TEXTFORMAT
    private TTextFormat contract, company;
    // ��ͬϸ�����
    private HRMContractD contractD;
    // Ԥ�������
    private HRMPrePay prePay;
    // ҽ������
    private HRMOrder order;
    // ��������
    private HRMPatInfo pat;
    // ��������
    private HRMPatAdm adm;
    // Ʊ�ݶ���
    private HRMOpbReceipt receipt;
    // �ײ�ϸ�����
    private HRMPackageD packageD;
    // ��Ʊ����
    private HRMInvRcp invRcp;
    // Ʊ�ݹ������
    private BilInvoice bilInvoice;
    // �˵�����
    private HRMBill bill;
    // ��һƱ��
    private String updateNo;
    // Ĭ��֧����ʽ
    private String payType;
    //�˵��б�
    private String billNoList;//add by wanglong 20130510
    // ��Ʊ��
    private String receiptNo;
    // �˵��б�
    private TParm billParm;// add by wanglong 20130324
    
    private String start_Date;
    private String end_Date;
    /**
     * ��ʼ���¼�
     */
    public void onInit() {
    	// �õ���ǰʱ��
    	Timestamp date = SystemTool.getInstance().getDate();
    	// ��ʼ����ѯ����
    	this.setValue("end_Date",
    			date.toString().substring(0, 10).replace('-', '/'));
    	this.setValue("start_Date",
    			StringTool.rollDate(date, -7).toString().substring(0, 10).
    			replace('-', '/'));
    	
        super.onInit();
        // ��ʼ���ؼ�
        initComponent();
        // ��ս���
        //onClear();
    }
    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        billTable = (TTable) this.getComponent("BILL_TABLE");
        billTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onPatCheckBox");// ����˵�����ʾ����Ա�˵���ϸ add by wanglong 20130510
    }

    /**
     * ����¼�
     */
    public void onClear() {
        billTable.removeRowAll();
        
        this.setValue("UNFEE", "Y");
        onInit();
        
    }
   /**
    * ��ѯ����
    */
    public void onQuery(){
    	start_Date = this.getValue("start_Date").toString().substring(0,10).replace('-', '/') + " 00:00:00";
    	end_Date = this.getValue("end_Date").toString().substring(0,10).replace('-','/')+"23:59:59";
    	 String sql =
                 "WITH AA AS (SELECT DISTINCT CASE WHEN A.REXP_FLG='Y' THEN 'Y' ELSE 'N' END REXP_FLG,A.RECEIPT_NO,A.COMPANY_CODE,"
                         + "         A.CONTRACT_CODE,A.BILL_NO,A.OWN_AMT,A.AR_AMT,A.DISCOUNT_AMT,A.CUT_AMT,C.PAT_NAME,C.MR_NO,"//modify by wanglong 20131111
                         + "         DENSE_RANK() OVER (PARTITION BY A.BILL_NO ORDER BY C.MR_NO) NUM "
                         + "    FROM HRM_BILL A, HRM_ORDER B, HRM_CONTRACTD C  "
                         + "   WHERE  A.BILL_NO = B.BILL_NO "
                         + "     AND B.MR_NO = C.MR_NO "
                         + "      @ "
                         + " AND B.BILL_DATE BETWEEN TO_DATE('"+start_Date+"','YYYY-MM-DD HH24:MI:SS')"
                         + " AND TO_DATE('"+end_Date+"','YYYY-MM-DD HH24:MI:SS')) "
                         + "SELECT '' FLG,CASE WHEN AA.REXP_FLG='Y' THEN 'Y' ELSE 'N' END REXP_FLG,AA.RECEIPT_NO,AA.COMPANY_CODE,AA.CONTRACT_CODE,"
                         + "        WM_CONCAT(AA.PAT_NAME) PAT_NAME,WM_CONCAT(AA.MR_NO) MR_NO,AA.BILL_NO,AA.OWN_AMT,AA.DISCOUNT_AMT,"
                         + "        ROUND(AA.OWN_AMT-AA.DISCOUNT_AMT,2) AR_AMT,AA.CUT_AMT,AA.AR_AMT AS RECV_AMT "
                         + "  FROM AA "
                         + " WHERE 1 = 1 "
                         + "   AND NUM < 50 "
                         + "GROUP BY AA.REXP_FLG,AA.RECEIPT_NO,AA.COMPANY_CODE,AA.CONTRACT_CODE,AA.BILL_NO,AA.OWN_AMT,AA.AR_AMT,AA.DISCOUNT_AMT,AA.CUT_AMT "
                         + "ORDER BY AA.RECEIPT_NO";
    	 if (this.getValueBoolean("UNFEE")) {
             sql = sql.replaceFirst("@", " AND (A.REXP_FLG <> 'Y' OR A.REXP_FLG IS NULL) ");
         } else if (this.getValueBoolean("FEE")) {
             sql = sql.replaceFirst("@", " AND A.REXP_FLG = 'Y' ");
         } 
    	
    	System.out.println("--------:"+sql);
    	 billParm = new TParm(TJDODBTool.getInstance().select(sql));
         if (billParm.getErrCode() != 0) {
             this.messageBox("��ѯ�˵��б�ʧ��");
             return;
         }
         billTable.setParmValue(billParm);
         
    }
    /**
	 * ���Excel
	 */
	public void onExport() {
		//�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table_d = getTTable("BILL_TABLE");
		ExportExcelUtil.getInstance().exportExcel(table_d, "�ɷѼ�¼");
	}
    
    /**
     * ��δ��Ʊ�������Ѵ�Ʊ������ȫ������ѡ��ť�¼�
     */
    public void onStateChoose() {//modify by wanglong 20130510
        String contractCode = this.getValueString("CONTRACT_CODE");
        if (contractCode.equals("")) {
            return;
        }
        if (this.getValueBoolean("FEE")) {
            this.callFunction("UI|ALL_CHOOSE|setEnabled", false);
        } else {
            this.callFunction("UI|ALL_CHOOSE|setEnabled", true);
        }
        // }
    }
    
   
    /**
     * ���ݲ����Ų�ѯ
     */
    public void onMrNO() {// ��ʱ����û��ʹ��
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        // ȡ��MR_NO�ĳ���
        TParm mrParm =
                new TParm(TJDODBTool.getInstance()
                        .select("SELECT MAX(MR_NO) MR_NO FROM SYS_PATINFO"));
        if (mrParm.getErrCode() != 0) {
            this.messageBox_("ȡ�ò����ų���ʧ��");
            return;
        }
        int mrLength = mrParm.getValue("MR_NO", 0).length();
        if (mrLength < 1) {
            this.messageBox_("ȡ�ò����ų��ȴ���");
            return;
        }
        // MR_NO�Զ����㣬��ֵ��������
        mrNo = StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); // ========= chenxi
        this.setValue("MR_NO", mrNo);
//        int result = bill.onQueryByMrNo(mrNo);
        billTable.setDataStore(bill);
        billTable.setDSValue();
    }
    

}
