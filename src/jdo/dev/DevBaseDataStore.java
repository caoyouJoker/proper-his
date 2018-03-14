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
public class DevBaseDataStore extends TDataStore {
    /**
     * 请购单号
     */
    private String requestNo;
    /**
     * 得到请购单号
     * @return String
     */
    public String getRequestNo() {
        return requestNo;
    }
    /**
     * 设置请购单号
     * @param requestNo String
     */
    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }
    /**
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){    
        return "SELECT * FROM DEV_PURCHASED WHERE REQUEST_NO='"+this.getRequestNo()+"' ORDER BY DEV_CODE,SEQ_NO";
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
        if("#ACTIVE#".equals(column)){
            if(parm.getBoolean("#ACTIVE#",row))
                return "false";
            else
                return "true";
        }
        if("XJ".equals(column)){
            return parm.getDouble("UNIT_PRICE",row)*parm.getDouble("QTY",row);
        }
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
//        System.out.println("设置值parm"+parm+"row:"+row+"column:"+column+"value:"+value);
        if("#ACTIVE#".equals(column)){
            parm.setData("#ACTIVE#",row,value.equals("Y")?"false":"true");
            return true;
        }
        return false;
    }
}
