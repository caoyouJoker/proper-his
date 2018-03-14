package com.javahis.ui.ins;

import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTable;
import com.javahis.ui.opd.CDSSStationDosntWork;
import com.javahis.ui.opd.CDSSStationDrools;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.ins.INSSingleTransferTool;

/**
 * <p>Title: ������ת�������</p>
 *
 * <p>Description: ������ת�������</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.02.13
 * @version 1.0
 */
public class INSSingleTransferControl
    extends TControl {
	private TParm SingleParm = new TParm();// ����������
    private CDSSStationDrools insSingleTransferDrools = new CDSSStationDosntWork();//ɸѡ������

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        initPage();
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                getDate(), -1);
       //��Ժ��ʼʱ��
        setValue("S_DATE", yesterday);
       //��Ժ����ʱ��
        setValue("E_DATE", SystemTool.getInstance().getDate());
       //Ĭ��ֵ ��ְ
       setValue("CARD_TYPE", "1");
       //ɸѡ������
		if (CDSSStationDrools.isCdssOn(Operator.getRegion())) {
			insSingleTransferDrools = new INSSingleTransferDrools(this);
		}
    }

    /**
     * ��ʼ������
     */
    public void initPage() {
        //Ĭ��ֵ δת��
        setValue("TRANS_TYPE", "2");
        //tableר�õļ���
        getTTable("Table").addEventListener(TTableEvent.CLICKED, this,
                                                "onTable1Clicked");
    }

    /**
     * ����
     */
    public void onSave() {
        int row = getTTable("Table").getSelectedRow();
        if (row < 0) {
            messageBox("���ѡһ������");
            return;
        }
        TParm parm = new TParm();
        parm = getTTable("Table").getParmValue().getRow(row);
//        parm.setData("TRANS_TYPE",getValueString("TRANS_TYPE"));
        //System.out.println("table"+parm);
        TParm result = new TParm();
        //�����ʸ�ȷ����״̬
        result = INSSingleTransferTool.getInstance().onUpINSIbsUpload(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;

        }else{
            messageBox("P0005");
//            onQuery();      
              onFilter();
        }
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("CARD_TYPE", this.getValueString("CARD_TYPE"));
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("S_DATE",StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd"));
        parm.setData("E_DATE",
                     StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd"));
        
        String mrNo = getValueString("MR_NO").length()==0?"":PatTool.getInstance().checkMrno(getValueString("MR_NO"));
        
        // modify by huangtt 20160930 EMPI���߲�����ʾ start
        Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
        if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
		}
     // modify by huangtt 20160930 EMPI���߲�����ʾ end
        
        
        parm.setData("MR_NO", mrNo);
        parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));//����
        parm.setData("SINGLE_PARM", null);//�����־����
        if (this.getValueString("TRANS_TYPE").equals("1"))
            result = INSSingleTransferTool.getInstance().getSingleTransData(
                parm);
        else
            result = INSSingleTransferTool.getInstance().getSingleNoTransData(
                parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount("CONFIRM_NO") < 1) {
            //��������
            this.messageBox("E0008");
            TTable table = (TTable)this.getComponent("Table");
            table.removeRowAll();
            return;
        }
        this.callFunction("UI|Table|setParmValue", result);
        return;
    }
    /**
     * ɸѡ������
     */
    public void onFilter() {
    	 //ɸѡ������
    	insSingleTransferDrools.fireRules();
//    	System.out.println("SingleParm======"+SingleParm);
    	onSingleQuery();
    }
    /**
     * ��ѯ������
     */
    public void onSingleQuery() {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("CARD_TYPE", this.getValueString("CARD_TYPE"));
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("S_DATE",StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd"));
        parm.setData("E_DATE",
                     StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd"));
        
        String mrNo = getValueString("MR_NO").length()==0?"":PatTool.getInstance().checkMrno(getValueString("MR_NO"));
        
        // modify by huangtt 20160930 EMPI���߲�����ʾ start
        Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
        if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
		}
     // modify by huangtt 20160930 EMPI���߲�����ʾ end       
        parm.setData("MR_NO", mrNo);
        parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));//����
        //��õ�����CASENO   
        int count = SingleParm.getCount("CASENO");
        String caseNoparm = "";
        for (int i = 0; i < count; i++) {
                if (caseNoparm.length() > 0)
                	caseNoparm += ",";
                caseNoparm += "'" + SingleParm.getValue("CASENO", i) + "'";
            
        }
//        System.out.println("caseNoparm======"+caseNoparm);
        parm.setData("SINGLE_PARM", caseNoparm);//�����־����
        if (this.getValueString("TRANS_TYPE").equals("1"))
            result = INSSingleTransferTool.getInstance().getSingleTransData(
                parm);
        else
            result = INSSingleTransferTool.getInstance().getSingleNoTransData(
                parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount("CONFIRM_NO") < 1) {
            //��������
            this.messageBox("E0008");
            TTable table = (TTable)this.getComponent("Table");
            table.removeRowAll();
            return;
        }
        this.callFunction("UI|Table|setParmValue", result);
        return;	
    }
    
    /**
     * ���
     */
    public void onExport() {
    	String title ="��������ϸ��";
    	TTable table = (TTable)this.getComponent("Table");
    	if (table.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table,title);	   	
    }
    /**
     * ���
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.clearValue("MR_NO");
        SingleParm = null;
    }
    /**
     * table�����¼�
     */
    public void onTable1Clicked() {
        callFunction("UI|Table1|getClickedRow");
    }
    /**
     * �õ�TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
    /**
     * �����������ݸ�ֵ
     */
    public void setDesc(){
      TTextFormat singleDisease = (TTextFormat) this.getComponent("SYS_SINGLEDISEASE");
      String text=singleDisease.getText();
      getTTable("Table").setValueAt(text,getTTable("Table").getSelectedRow(),10);
    }
    /**
	 * �õ������־����
	 * 
	 * @return String
	 */
	public TParm getSingleParm() {
		return this.SingleParm;
	}


}

