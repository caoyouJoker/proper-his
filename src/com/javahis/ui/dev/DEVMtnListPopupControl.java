package com.javahis.ui.dev;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;



/**
 *
 * <p>Title: ά���������ѡ���</p>
 * 
 * DEV_MAINTENANCEM
 * DEV_MEASURE
 *
 * <p>Description: </p>
 *
 * <p>Copyright: ProperSoft 2015</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangjc
 * @version 1.0
 */
public class DEVMtnListPopupControl extends TControl {
	
	private String sql = "";
	private TTable table;
	private TParm dataParm;
	private String oldText = "";
	private TParm tableData;
    
	public void onInit(){
		super.onInit();
        table = (TTable) this.getComponent("TABLE");
        callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                     "onKeyReleased");
        callFunction("UI|EDIT|addEventListener",
                     "EDIT->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                               "onDoubleClicked");
        this.tableData = this.table.getParmValue();
        initParamenter();
        onResetDW();
	}
	
    /**
     * ���¼���
     */
    public void onInitReset() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        String oldText = (String) callFunction("UI|EDIT|getText");
        if (oldText.equals(text)) {
            return;
        }
        setEditText(text);
    }
    
    /**
     * ��ʼ������
     */
    public void initParamenter() {
        Object obj = getParameter();
//        if (obj == null)
//            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        setEditText(text);
        if(parm.getValue("MTN_KIND").equals("0")){//����
        	this.sql = "SELECT A.DEV_CODE,A.MTN_TYPE_CODE,'����' AS MTN_KIND,A.MTN_TYPE_DESC,B.DEV_CHN_DESC "
        			+ " FROM DEV_MAINTENANCEM A,DEV_BASE B "
        			+ " WHERE A.DEV_CODE = B.DEV_CODE "
        			+ " AND A.MTN_KIND='0' "
        			+ " AND A.ACTIVE_FLG='Y' ";
        }else if(parm.getValue("MTN_KIND").equals("1")){//�ʿ�
        	this.sql = "SELECT A.DEV_CODE,A.MTN_TYPE_CODE,'�ʿ�' AS MTN_KIND,A.MTN_TYPE_DESC,B.DEV_CHN_DESC "
        			+ " FROM DEV_MAINTENANCEM A,DEV_BASE B "
        			+ " WHERE A.DEV_CODE = B.DEV_CODE "
        			+ " AND A.MTN_KIND='1' "
        			+ " AND A.ACTIVE_FLG='Y' ";
        }else if(parm.getValue("MTN_KIND").equals("2")){//����
        	this.sql = "SELECT A.DEV_CODE,A.MEASUREM_CODE AS MTN_TYPE_CODE,'����' AS MTN_KIND,A.MEASUREM_DESC AS MTN_TYPE_DESC,B.DEV_CHN_DESC "
        			+ " FROM DEV_MEASURE A,DEV_BASE B "
        			+ " WHERE A.DEV_CODE = B.DEV_CODE "
        			+ " AND A.ACTIVE_FLG='Y' ";
        }
        String dev_code = parm.getValue("DEV_CODE",0);
        if(!dev_code.equals("")){
        	sql += " AND A.DEV_CODE='"+dev_code+"' ";
        }
//        if(!sup_code.equals("")){
//        	sql += " AND A.SUP_CODE='"+sup_code+"' ";
//        }
        
    }

    /**
     * ������������
     * @param s String
     */
    public void setEditText(String s) {
        callFunction("UI|EDIT|setText", s);
        int x = s.length();
        callFunction("UI|EDIT|select", x, x);
        onKeyReleased(s);
    }

    /**
     * �����¼�
     * @param s String
     */
    public void onKeyReleased(String s) {
        s = s.toUpperCase();
        if (oldText.equals(s))
            return;
        oldText = s;
        int count = dataParm.getCount("DEV_CODE");
        String names[] = dataParm.getNames();
        TParm temp = new TParm();
        for(int i=0;i<count;i++){
            TParm rowParm = dataParm.getRow(i);
            if(this.filter(rowParm)){
                for(String tempData:names){
                    temp.addData(tempData,rowParm.getData(tempData));
                }
            }
        }
        table.setParmValue(temp);
        int rowConunt = temp.getCount("DEV_CODE");
        if (rowConunt > 0)
            table.setSelectedRow(0);
    }

    /**
     * ���˷���
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter(TParm parm) {
//        boolean falg = parm.getValue("MTN_TYPE_CODE").toUpperCase().
//            startsWith(oldText) ||
//            parm.getValue("PY1").toUpperCase().startsWith(oldText);
    	boolean falg = parm.getValue("MTN_TYPE_CODE").toUpperCase().indexOf(oldText) != -1 ||
                parm.getValue("MTN_TYPE_DESC").toUpperCase().indexOf(oldText) != -1;
        return falg;
    }

    /**
     * �����¼�
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            callFunction("UI|setVisible", false);
            return;
        }
        int count = table.getRowCount();
        if (count <= 0)
            return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                int row = table.getSelectedRow() - 1;
                if (row < 0)
                    row = 0;
                table.getTable().grabFocus();
                table.setSelectedRow(row);
                break;
            case KeyEvent.VK_DOWN:
                row = table.getSelectedRow() + 1;
                if (row >= count)
                    row = count - 1;
                table.getTable().grabFocus();
                table.setSelectedRow(row);
                break;
            case KeyEvent.VK_ENTER:
                callFunction("UI|setVisible", false);
                onSelected();
                break;
        }
    }

    /**
     * ��˫���¼�
     * @param row int
     */
    public void onDoubleClicked(int row) {
        if (row < 0)
            return;
        callFunction("UI|setVisible", false);
        onSelected();
    }

    /**
     * ѡ��
     */
    public void onSelected() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        setReturnValue(table.getParmValue().getRow(row));
    }
    
    /**
     * ���±���
     */
    public void onResetDW() {
//        System.out.println("��ѯSQL��"+sql);
        dataParm = new TParm(TJDODBTool.getInstance().select(sql));
        //��������
//        if(tableData!=null){
//            int rowCount = tableData.getCount("DEV_CODE");
////            System.out.println("����:" + rowCount);
//            for (int i = 0; i < rowCount; i++) {
//                TParm temp = tableData.getRow(i);
//                int rowMainCount = dataParm.getCount("DEV_CODE");
//                for (int j = rowMainCount-1; j >= 0; j--) {
////                    System.out.println(rowMainCount + " -----  " + j);
//                    TParm tempMain = dataParm.getRow(j);
//                    if (temp.getValue("DEV_CODE").equals(tempMain.getValue("DEV_CODE"))) {
//                        dataParm.removeRow(j);
//                    }
//                }
//            }
//        }
//        System.out.println("--------"+dataParm);
        table.setParmValue(dataParm);
    }
    
    /**
     * ��������ȫ��
     */
    public void onResetFile() {
        TParm parm = new TParm(this.getDBTool().select(sql));
        table.setParmValue(parm);
    }
    
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
