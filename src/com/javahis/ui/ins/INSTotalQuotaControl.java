package com.javahis.ui.ins;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Vector;

import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;

import jdo.sys.SystemTool;

/**
 * Title: ҽ���ܶ�ָ���¼
 * Description:ҽ���ܶ�ָ���¼
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2014
 * @version 1.0
 */
public class INSTotalQuotaControl extends TControl {
    TParm data;
    int selectRow = -1;
	private TTable tableTotal;
	// ����
	private Compare compare = new Compare();
	private int sortColumn = -1;
	private boolean ascending = false;
    public void onInit() {
        super.onInit();
        ( (TTable) getComponent("TABLE")).addEventListener("TABLE->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        tableTotal = (TTable) this.getComponent("TABLE");
        onClear();
        addListener(tableTotal);//��������
    }
    /**
     * ��ʼ������
     */
    public void getData() {
     	//���
    	String sysdate =StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
    	String year = sysdate.substring(0,4);  
    	this.setValue("YEAR",year);   	
    	//ָ�꿪ʼ����
    	String startdate =year+"-04-01 00:00:00";
    	Timestamp date = StringTool.getTimestamp(startdate, "yyyy-MM-dd HH:mm:ss");
    	this.setValue("START_DATE",date);
    }
    /**
     * ���Ӷ�Table�ļ���
     *
     * @param row
     */
    public void onTableClicked(int row) {
        // ѡ����
        if (row < 0)
            return;
        setValueForParm(
            "YEAR;YEAR_QUOTA_AMT;YEAR_PAT_COUNT;MON_QUOTA_AMT;" +
            "MON_PAT_COUNT;AVERAGE_PAY_AMT;OWN_PAY_PERCENT",data, row);
        //��ʾ���
        if(data.getValue("INS_TYPE_DESC", row).equals("��ְ����")){
            this.setValue("INS_TYPE","01");
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //�ξ�ͳ��֧�����ɱ༭
        }
        if(data.getValue("INS_TYPE_DESC", row).equals("��ְ����")){
            this.setValue("INS_TYPE","02");
            callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //�ξ�ͳ��֧�����ɱ༭
        }
        if(data.getValue("INS_TYPE_DESC", row).equals("��������")){
            this.setValue("INS_TYPE","03");
            callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //�ξ�ͳ��֧�����ɱ༭
        }
        if(data.getValue("INS_TYPE_DESC", row).equals("��ְסԺ")){
            this.setValue("INS_TYPE","04");
            callFunction("UI|AVERAGE_PAY_AMT|setEnabled", true); //�ξ�ͳ��֧���ɱ༭
        }
        if(data.getValue("INS_TYPE_DESC", row).equals("����סԺ")){
            this.setValue("INS_TYPE","05");
            callFunction("UI|AVERAGE_PAY_AMT|setEnabled", true); //�ξ�ͳ��֧���ɱ༭
        }
        //��ʾָ�꿪ʼ����
//        String sysdate =StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
    	String year = data.getValue("YEAR", row);
    	String startdate = data.getValue("START_DATE", row);
    	String date =year+"-"+startdate.substring(0,2)+"-"+startdate.substring(2,4)+" 00:00:00";
    	Timestamp datejm = StringTool.getTimestamp(date, "yyyy-MM-dd HH:mm:ss");
    	this.setValue("START_DATE",datejm);
        selectRow = row;
        callFunction("UI|YEAR|setEnabled", false); //��Ȳ��ɱ༭
        callFunction("UI|INS_TYPE|setEnabled", false); //��𲻿ɱ༭
    }
    /**
     * ��ѯ
     */
    public void onQuery() {
    	String sql =" SELECT YEAR,CASE  WHEN INS_TYPE='01' THEN '��ְ����'  " +
    			    " WHEN INS_TYPE='02' THEN '��ְ����' " +   			    
    			    " WHEN INS_TYPE='03' THEN '��������' " +
    			    " WHEN INS_TYPE='04' THEN '��ְסԺ' " +
    			    " WHEN INS_TYPE='05' THEN '����סԺ' END AS INS_TYPE_DESC," +
    			    " YEAR_QUOTA_AMT,YEAR_PAT_COUNT,MON_QUOTA_AMT,"+
                    " MON_PAT_COUNT,AVERAGE_PAY_AMT,OWN_PAY_PERCENT," +
                    " START_DATE,OPT_USER,OPT_TERM,OPT_DATE,INS_TYPE"+
                    " FROM INS_TOTAL_QUOTA" +
                    " WHERE YEAR ='"+this.getValue("YEAR")+"'" +
                    " ORDER BY INS_TYPE";
    	data = new TParm(TJDODBTool.getInstance().select(sql));
		if (data.getErrCode() < 0) {
			this.messageBox("E0116");//û������
			return;
		}
        ((TTable) getComponent("TABLE")).setParmValue(data);
    }
    /**
     * ��ѯ����(��ʼ��)
     */
    public void onQuerydata() {
    	//���
    	String sysdate =StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
    	String year = sysdate.substring(0,4); 
    	String sql =" SELECT YEAR,CASE  WHEN INS_TYPE='01' THEN '��ְ����'  " +
    	            " WHEN INS_TYPE='02' THEN '��ְ����' " +   			    
		            " WHEN INS_TYPE='03' THEN '��������' " +
		            " WHEN INS_TYPE='04' THEN '��ְסԺ' " +
		            " WHEN INS_TYPE='05' THEN '����סԺ' END AS INS_TYPE_DESC," +
    			    " YEAR_QUOTA_AMT,YEAR_PAT_COUNT,MON_QUOTA_AMT,"+
                    " MON_PAT_COUNT,AVERAGE_PAY_AMT,OWN_PAY_PERCENT," +
                    " START_DATE,OPT_USER,OPT_TERM,OPT_DATE,INS_TYPE"+
                    " FROM INS_TOTAL_QUOTA" +
                    " WHERE YEAR ='"+year+"'" +
                    " ORDER BY INS_TYPE";
    	data = new TParm(TJDODBTool.getInstance().select(sql));
		if (data.getErrCode() < 0) {
			this.messageBox("E0116");//û������
			return;
		}
        ((TTable) getComponent("TABLE")).setParmValue(data);
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
     * ����
     */
    public void onInsert() {
        if (this.getValue("YEAR").equals("")) {
        	this.messageBox("��Ȳ���Ϊ��");
            return;
        }
        if (this.getValue("INS_TYPE").equals("")) {
        	this.messageBox("�����Ϊ��");
            return;
        }
        //�ж��Ƿ������������
        TParm parmQ = new TParm();
        parmQ.setData("YEAR", this.getValue("YEAR"));
        parmQ.setData("INS_TYPE", this.getValue("INS_TYPE"));
        String sqlQ= " SELECT * FROM INS_TOTAL_QUOTA B"+
        " WHERE  B.YEAR ='"+ parmQ.getValue("YEAR")+ "'"+
        " AND B.INS_TYPE ='"+ parmQ.getValue("INS_TYPE")+ "'";
        TParm resultQ = new TParm(TJDODBTool.getInstance().select(sqlQ)); 
//        System.out.println("resultQ=========="+resultQ.getData("YEAR"));
        if(resultQ.getData("YEAR")!=null){
            this.messageBox("�����Ѵ���,���ܱ���");
        	return;   
        }        	
        TParm parm = getParmForTag("YEAR;INS_TYPE;YEAR_QUOTA_AMT;YEAR_PAT_COUNT;MON_QUOTA_AMT;" +
                                   "MON_PAT_COUNT;AVERAGE_PAY_AMT;OWN_PAY_PERCENT");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "START_DATE")), "yyyyMMdd"); //�õ�����Ĳ�ѯʱ��
        String startdate = date.substring(4,8);
        String sql= " INSERT INTO INS_TOTAL_QUOTA"+
		            " (YEAR,INS_TYPE,YEAR_QUOTA_AMT,YEAR_PAT_COUNT,MON_QUOTA_AMT,"+
		            " MON_PAT_COUNT,AVERAGE_PAY_AMT,OWN_PAY_PERCENT," +
		            " START_DATE,OPT_USER,OPT_TERM,OPT_DATE)"+ 
	                " VALUES ('"+ parm.getValue("YEAR")+ "','"+ parm.getValue("INS_TYPE")+ "'," +
	                 parm.getValue("YEAR_QUOTA_AMT")+ ","+ parm.getDouble("YEAR_PAT_COUNT")+ "," +
	                 parm.getValue("MON_QUOTA_AMT")+ ","+ parm.getDouble("MON_PAT_COUNT")+ "," +
	                 parm.getDouble("AVERAGE_PAY_AMT")+ ","+ parm.getDouble("OWN_PAY_PERCENT")+ ",'" +
	                 startdate + "','"+ parm.getValue("OPT_USER")+ "','" +
	                 parm.getValue("OPT_TERM")+ "',SYSDATE)";
//     System.out.println("sql=========="+sql);   
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//        System.out.println("result=========="+result);  
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        onQuery();
        this.messageBox("����ɹ�");
    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = getParmForTag("YEAR;INS_TYPE;YEAR_QUOTA_AMT;YEAR_PAT_COUNT;MON_QUOTA_AMT;" +
                                   "MON_PAT_COUNT;AVERAGE_PAY_AMT;OWN_PAY_PERCENT");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
                      "START_DATE")), "yyyyMMdd"); //�õ�����Ĳ�ѯʱ��
        String startdate = date.substring(4,8);
        String sql = "UPDATE INS_TOTAL_QUOTA SET YEAR ='"
		+ parm.getValue("YEAR") + "',INS_TYPE ='"
		+ parm.getValue("INS_TYPE") + "',YEAR_QUOTA_AMT ="
		+ parm.getValue("YEAR_QUOTA_AMT") + ",YEAR_PAT_COUNT ="
		+ parm.getDouble("YEAR_PAT_COUNT") + ",MON_QUOTA_AMT ="
		+ parm.getValue("MON_QUOTA_AMT") + ",MON_PAT_COUNT ="
		+ parm.getDouble("MON_PAT_COUNT") + ",AVERAGE_PAY_AMT="
		+ parm.getDouble("AVERAGE_PAY_AMT") + ",OWN_PAY_PERCENT="
		+ parm.getDouble("OWN_PAY_PERCENT") + ",START_DATE ='"
		+ startdate + "',OPT_USER ='"
		+ parm.getValue("OPT_USER") + "',OPT_TERM ='"
		+ parm.getValue("OPT_TERM") + "',OPT_DATE = SYSDATE" +
		" WHERE YEAR = '"
		+ parm.getValue("YEAR") + "'" +
		" AND INS_TYPE='"
		+ parm.getValue("INS_TYPE") + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        onQuery();
        this.messageBox("�޸ĳɹ�");
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("INS_TYPE;YEAR_QUOTA_AMT;YEAR_PAT_COUNT;MON_QUOTA_AMT;"+
                   "MON_PAT_COUNT;AVERAGE_PAY_AMT;OWN_PAY_PERCENT;START_DATE");
        ((TTable) getComponent("TABLE")).removeRowAll();
        selectRow = -1;
        callFunction("UI|YEAR|setEnabled", true); //��ȿɱ༭
        callFunction("UI|INS_TYPE|setEnabled", true); //���ɱ༭
        callFunction("UI|AVERAGE_PAY_AMT|setEnabled", true); //�ξ�ͳ��֧���ɱ༭
		//��ʼ������
		onQuerydata();
		//���,����
        getData();
    }
    /**
     * ���ܶ�ָ��س��¼�
     */
    public void yearAmt(){
    	//��ʾ���ܶ�ָ��   	
        double amt=this.getValueDouble("YEAR_QUOTA_AMT");
        double monthamt = (amt*10000)/12;
        this.setValue("MON_QUOTA_AMT", monthamt);
        this.grabFocus("YEAR_PAT_COUNT");
    }
    /**
     * ���˴�ָ��س��¼�
     */
    public void yearCount(){
        //��ʾ���˴�ָ��
        double count=this.getValueDouble("YEAR_PAT_COUNT");
        this.setValue("MON_PAT_COUNT", count/12);
        if(this.getValue("INS_TYPE").equals("01"))//��ְ����
        this.grabFocus("OWN_PAY_PERCENT");
        if(this.getValue("INS_TYPE").equals("02"))//��ְ����
        this.grabFocus("OWN_PAY_PERCENT");
        if(this.getValue("INS_TYPE").equals("03"))//��������
        this.grabFocus("OWN_PAY_PERCENT");
        if(this.getValue("INS_TYPE").equals("04"))//��ְסԺ
        this.grabFocus("AVERAGE_PAY_AMT");
        if(this.getValue("INS_TYPE").equals("05"))//����סԺ
        this.grabFocus("AVERAGE_PAY_AMT");
    }   
    /**
     * �ξ�ͳ��֧���س��¼�
     */
    public void averagepayAmt(){
    	this.grabFocus("OWN_PAY_PERCENT");
    }
    /**
     * ���ѡ���¼�
     */
    public void instype(){
    	if(this.getValue("INS_TYPE").equals("01")){//��ְ����
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //�ξ�ͳ��֧�����ɱ༭
    		this.grabFocus("YEAR_QUOTA_AMT");
    	}else if(this.getValue("INS_TYPE").equals("02")){//��ְ����
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //�ξ�ͳ��֧�����ɱ༭
    		this.grabFocus("YEAR_QUOTA_AMT");
    	}else if(this.getValue("INS_TYPE").equals("03")){//��������
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", false); //�ξ�ͳ��֧�����ɱ༭
    		this.grabFocus("YEAR_QUOTA_AMT");
    	}else if(this.getValue("INS_TYPE").equals("04")){//��ְסԺ
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", true); //�ξ�ͳ��֧���ɱ༭
    		this.grabFocus("YEAR_QUOTA_AMT");
    	}else if(this.getValue("INS_TYPE").equals("05")){//����סԺ
    		callFunction("UI|AVERAGE_PAY_AMT|setEnabled", true); //�ξ�ͳ��֧���ɱ༭
    		this.grabFocus("YEAR_QUOTA_AMT");
    	}
    	clearValue("YEAR_QUOTA_AMT;YEAR_PAT_COUNT;MON_QUOTA_AMT;MON_PAT_COUNT;"+
                   "AVERAGE_PAY_AMT;OWN_PAY_PERCENT");
    }
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}

				// �����parmֵһ��
				// 1.ȡparamwֵ;
				TParm tableData = tableTotal.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// 3.���ݵ������,��vector����
				// ������������;
				String tblColumnName = tableTotal.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);
			}
		});
	}
	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
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
		tableTotal.setParmValue(parmTable);
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
				return index;
			}
			index++;
		}

		return index;
	}
	
}
