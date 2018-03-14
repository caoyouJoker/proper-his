package com.javahis.ui.opb;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.reg.PatAdmTool;
import jdo.opb.OPB;
import java.sql.Timestamp;
import jdo.sys.Operator;
/**
 * <p>Title:�����ѡ�� </p>
 *
 * <p>Description:�����ѡ�� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class OPBChooseVisitControl
    extends TControl {
    int selectrow = -1;
    OPB opb=new OPB();

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //�õ�ǰ̨���������ݲ���ʾ�ڽ�����
        TParm recptype = (TParm) getParameter();
        if (!recptype.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", recptype.getParm("PARM"),
                            -1);
            callFunction("UI|TABLE|setParmValue", recptype.getParm("RESULT"));
        }
        if (recptype.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", recptype, -1);

        }
        //Ԥ�����ʱ���
        this.callFunction("UI|STARTTIME|setValue",
                          SystemTool.getInstance().getDate());
        this.callFunction("UI|ENDTIME|setValue",
                          SystemTool.getInstance().getDate());
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        //Ĭ��Table����ʾ����Һż�¼
    onQuery();
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        //���������¼�
        this.callFunction("UI|TABLE|acceptText");
//   TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        selectrow = row;
    }

    public void onTableDoubleClicked(int row) {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue( (String) data.getData("CASE_NO", row));
        this.callFunction("UI|onClose");
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        //==========pangben modify 20110421 start ��Ӳ�����ΪselDateByMrNo��������������������
        String regionCode = Operator.getRegion();
        //modify by huangtt 20161013  start �кϲ����������ʱ����֮ǰ�����ŵ�����ҲҪ��ѯ���� 
      
        String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("STARTTIME")), "yyyyMMdd");
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("ENDTIME")), "yyyyMMdd");
		String mrNo = getValueString("MR_NO");
		
		String sql = "  SELECT CASE_NO, MR_NO, ADM_TYPE, ADM_DATE, SESSION_CODE," +
				" QUE_NO, REALDEPT_CODE AS DEPT_CODE, REALDR_CODE AS DR_CODE" +
				" FROM REG_PATADM" +
				" WHERE  @ " +
				" AND REGCAN_USER IS NULL" +
				" AND ADM_DATE BETWEEN TO_DATE ('"+startTime+"','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+endTime+"', 'YYYYMMDDHH24MISS')" +
				" AND REGION_CODE = '"+regionCode+"'" +
				" ORDER BY ADM_DATE, SESSION_CODE";
		
		mrNo = PatTool.getInstance().getMrRegMrNos(mrNo);
		
		sql = sql.replaceFirst("@", "MR_NO IN ("+mrNo+")");
		
		TParm parm =new TParm(TJDODBTool.getInstance().select(sql));
        
		  //modify by huangtt 20161013  end �кϲ����������ʱ����֮ǰ�����ŵ�����ҲҪ��ѯ���� 
        
//        TParm parm = PatAdmTool.getInstance().selDateByMrNo(getValueString("MR_NO"),
//	            (Timestamp) getValue("STARTTIME"), (Timestamp) getValue("ENDTIME"),regionCode);
        
        if (parm.getCount() < 0)
	            return;
	        if (parm.getCount() == 0)
	            this.messageBox("�޹Һ���Ϣ!");
	        this.callFunction("UI|TABLE|setParmValue", parm);
        //==========pangben modify 20110421 start
        
        
       
    }

    /**
     *
     */
    public void onOK() {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue( (String) data.getData("CASE_NO", selectrow));
        this.callFunction("UI|onClose");
    }
}
