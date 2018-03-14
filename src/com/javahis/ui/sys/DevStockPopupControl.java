package com.javahis.ui.sys;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;

/**
 * <p>Title: ���죨���⣩�豸ѡ��</p>
 *
 * <p>Description: ���죨���⣩�豸ѡ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>  
 *
 * <p>Company: </p>  
 *
 * @author not attributable
 * @version 1.0
 */
public class DevStockPopupControl extends TControl {
    private String oldText = "";
    private TTable table;
    private TParm dataParm;
    /**
     * ��ѯSQL
     */
//    STOCKM 
//    DEPT_CODE         
//    DEV_CODE         
//    MAN_DATE        
//    SCRAP_VALUE       
//    GUAREP_DATE     
//    DEP_DATE      
//    FILES_WAY        
//    CARE_USER        
//    USE_USER          
//    LOC_CODE       
//    INWAREHOUSE_DATE 
//    STOCK_FLG         
//    QTY                        
//    UNIT_PRICE       
//    private String SQL=" SELECT A.DEPT_CODE,A.DEV_CODE,A.MAN_DATE,A.SCRAP_VALUE,A.GUAREP_DATE," +
//    		           " A.DEP_DATE,A.FILES_WAY,A.CARE_USER,A.USE_USER,A.LOC_CODE," +
//    		           " A.INWAREHOUSE_DATE,A.STOCK_FLG,A.QTY,A.UNIT_PRICE,B.ACTIVE_FLG," +
//    		           " B.DEVKIND_CODE,B.DEVTYPE_CODE,B.DEVPRO_CODE,B.DEV_CHN_DESC,B.DESCRIPTION," +
//    		           " B.SPECIFICATION,B.UNIT_CODE,B.SEQMAN_FLG,B.DEV_CLASS,B.BUYWAY_CODE" +
//    		           " FROM DEV_STOCKM A ,DEV_BASE B" +
//    		           " WHERE A.DEV_CODE = B.DEV_CODE";
    
     
    private String SQL=" SELECT A.DEPT_CODE,A.DEV_CODE,A.STOCK_STATUS,A.QTY,A.UNIT_PRICE,B.ACTIVE_FLG,"+
    	" B.DEVKIND_CODE,B.DEVTYPE_CODE,B.DEVPRO_CODE,B.DEV_CHN_DESC,B.DESCRIPTION,"+
    	" B.SPECIFICATION,B.UNIT_CODE,B.SEQMAN_FLG,B.DEV_CLASS,B.BUYWAY_CODE, "+
    	" A.MAN_NATION,A.MAN_CODE,A.MAN_DATE,A.MANSEQ_NO,A.DEPR_METHOD,"+
    	" A.SCRAP_VALUE,A.CARE_USER,A.USE_USER,A.LOC_CODE,A.INWAREHOUSE_DATE "+
//    	" B.SPECIFICATION,B.UNIT_CODE,B.SEQMAN_FLG,B.DEV_CLASS,B.BUYWAY_CODE,A.UNIT_PRICE, "+
//    	" A.MAN_NATION,A.MAN_CODE,A.MAN_DATE,A.MANSEQ_NO,A.GUAREP_DATE,A.DEPR_METHOD,"+
//    	" A.DEP_DATE,A.SCRAP_VALUE,A.CARE_USER,A.USE_USER,A.LOC_CODE,A.INWAREHOUSE_DATE "+
    	" FROM DEV_STOCKD A ,DEV_BASE B"+  
    	" WHERE A.DEV_CODE = B.DEV_CODE ";
    //DEV_STOCKM A
    //DEV_STOCKD B       
    //DEV_BASE C
    //DEV_STOCKM A��DEV_STOCKD B����ΪE 
    /**
     * ����ע��
     */
    private String activeFlg;
    /**
     * �豸����
     */
    private String devKindCode;
    /**
     * �豸���
     */
    private String devTypeCode;
    /**
     * �豸����
     */
    private String devProCode;
    /**
     * ����;��
     */
    private String buyWayCode;
    /**
     * ����
     */
    private String deptCode;
    /**
     * ǰ̨��������
     */
    private TParm tableData;
    /**
     * ��ʼ��
     */
    public void onInit() { 
        super.onInit();
        table = (TTable) callFunction("UI|TABLE|getThis");
        callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                     "onKeyReleased");
        callFunction("UI|EDIT|addEventListener",
                     "EDIT->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                               "onDoubleClicked");
        initParamenter();
        //��ʼ������
        onResetDW();
    }

    /**
     * ��ʼ������
     */
    public void initParamenter() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        setEditText(text);
        String sql = " AND ";
        int andCount = 0;  
        //����ע��
        activeFlg = parm.getValue("ACTIVE_FLG");
        if(activeFlg.length()!=0){
            sql+="B.ACTIVE_FLG='"+activeFlg+"'";
            andCount++;
        }
        //�豸����
        devKindCode = parm.getValue("DEVKIND_CODE");
        if(devKindCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="B.DEVKIND_CODE='"+devKindCode+"'";
            andCount++;
        }
        //�豸���
        devTypeCode = parm.getValue("DEVTYPE_CODE");
        if(devTypeCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="B.DEVTYPE_CODE='"+devTypeCode+"'";
            andCount++;
        }
        //�豸����
        devProCode = parm.getValue("DEVPRO_CODE");
        if(devProCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="B.DEVPRO_CODE='"+devProCode+"'";
            andCount++;
        }
        //����;��
        buyWayCode = parm.getValue("BUYWAY_CODE");
        if(buyWayCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }   
            sql+="B.BUYWAY_CODE='"+buyWayCode+"'";
            andCount++;
        }
        //����
        deptCode = parm.getValue("DEPT_CODE");
        if(deptCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="A.DEPT_CODE='"+deptCode+"'";
            andCount++;
        }
        if(parm.getData("TABLEDATA")!=null){
            tableData = (TParm)parm.getData("TABLEDATA");
        }else{
            tableData = new TParm();
        }
        this.SQL+=sql;
//        System.out.println(this.SQL);
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
        boolean falg = parm.getValue("DEV_CODE").toUpperCase().
            startsWith(oldText) ||
            parm.getValue("DEV_CHN_DESC").toUpperCase().indexOf(oldText) > 0 ||
            parm.getValue("DEV_ENG_DESC").toUpperCase().startsWith(oldText) ||
            parm.getValue("DEV_ABS_DESC").toUpperCase().startsWith(oldText) ||
            parm.getValue("PY1").toUpperCase().startsWith(oldText) ||
            parm.getValue("PY2").toUpperCase().startsWith(oldText);
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
//        System.out.println(">>>>>>>>>>>>"+table.getParmValue().getRow(row));
        setReturnValue(table.getParmValue().getRow(row));
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
     * ���±���
     */
    public void onResetDW() {  
//        System.out.println("��ѯSQL��"+SQL);
        dataParm = new TParm(this.getDBTool().select(SQL));
        //��������
        if(tableData!=null){
//        	System.out.println("dataParm<<<<<<<<<<<<"+dataParm);
            int rowCount = tableData.getCount("DEV_CODE");
//            System.out.println("����:" + rowCount);
            for (int i = 0; i < rowCount; i++) {
                TParm temp = tableData.getRow(i);
                int rowMainCount = dataParm.getCount("DEV_CODE");
                for (int j = rowMainCount-1; j >= 0; j--) {
//                    System.out.println(rowMainCount + " -----  " + j);
                    TParm tempMain = dataParm.getRow(j);
                    if (temp.getValue("DEV_CODE").equals(tempMain.getValue(
                        "DEV_CODE"))) { 
                        dataParm.removeRow(j);
                    }
                }
            }  
        }
//        System.out.println("dataParm>>>>>"+dataParm);
        table.setParmValue(dataParm);  
    }
  
    /**
     * ��������ȫ��
     */
    public void onResetFile() {
        TParm parm = new TParm(this.getDBTool().select(SQL));
        table.setParmValue(parm);
    }

}
