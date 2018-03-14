package jdo.odi;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: HIS医疗系统</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author WangM
 * @version 1.0
 */
public class ODIPACKORDERDataStore extends TDataStore {
    private String packCode;
    public String getPackCode() {
        return packCode;
    }
    public void setPackCode(String packCode) {
        this.packCode = packCode;
    }
    /**
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT PACK_CODE,SEQ_NO,DESCRIPTION,SPECIFICATION,ORDER_DESC,TRADE_ENG_DESC,LINKMAIN_FLG,LINK_NO,ORDER_CODE,MEDI_QTY,MEDI_UNIT,DEPT_OR_DR,DEPTORDR_CODE,ROUTE_CODE,FREQ_CODE,DCTEXCEP_CODE,CAT1_TYPE,ORDER_CAT1_CODE,TAKE_DAYS,OPT_USER,OPT_TERM,OPT_DATE,PHASE_I_CLINICAL_FLG,BC_INTERVAL_TIME "+
                "FROM ODI_PACK_ORDER WHERE PACK_CODE='"+this.getPackCode()+"'"+
                " ORDER BY PACK_CODE,SEQ_NO";
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
        //System.out.println("数据:"+parm+"行:"+row+"列名:"+column);
        return null;
    }
    /**
     * 设置未知列值
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setOtherColumnValue(TParm parm, int row, String column, Object value) {
        //System.out.println("数据:"+parm+"行:"+row+"列名:"+column+"值:"+value);
        return true;
    }



}
