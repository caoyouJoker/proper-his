package jdo.ibs;

import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
/**
 *
 * <p>Title: 住院账务大对象</p>
 *
 * <p>Description: 住院账务大对象</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class IBS extends TDataStore{
    IBSOrderD orderD;
    IBSOrderM orderM;
    public IBSOrderD getOrderD(){
        return orderD;
    }
    public IBSOrderM getOrderM(){
        return orderM;
    }
    public void setOrderD(IBSOrderD orderD){
        this.orderD=orderD;
    }
    public void setOrderM(IBSOrderM orderM){
        this.orderM=orderM;
    }
    /**
     * 保存动作
     * @return boolean true:保存成功，false:保存失败
     */
    public boolean onSave() {

        String[] sql = getOrderM().getUpdateSQL();
        String[] tempSql = getOrderD().getUpdateSQL();
        sql = StringTool.copyArray(sql, tempSql);
        TJDODBTool dfd;
        TParm result=new TParm(TJDODBTool.getInstance().update(sql));
        //update adm_inp
        //todo
        //update ....
        //todo
        if(result.getErrCode()!=0){
            return false;
        }
        return true;
    }
}
