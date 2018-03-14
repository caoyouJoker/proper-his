package jdo.dev;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DevReceiptDDataStore extends TDataStore {
    /**
     * 验收单号
     */
    private String receiptNo;
    /**
     * 得到请购单号
     * @return String
     */
    public String getReceiptNo() {
        return receiptNo;
    }
    /**
     * 设置请购单号
     * @param requestNo String
     */
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }
    /**
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM DEV_RECEIPTD WHERE RECEIPT_NO='"+this.getReceiptNo()+"' ORDER BY SEQ_NO";
    }
    /**
    * 查询
    * @return boolean
    */
   public boolean onQuery(){
       if(!setSQL(getQuerySQL()))
           return false;
       if(retrieve()==-1)
           return false;
       return true;
   }  
    /**
     * 得到未知列数据
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm, int row, String column) {
        System.out.println("parm:"+parm+"行:"+row+"column:"+column);
        if("XJ".equals(column)){
            return parm.getDouble("UNIT_PRICE",row)*parm.getDouble("RECEIPT_QTY",row);
        }
        return null;
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
