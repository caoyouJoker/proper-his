package jdo.inw;

import com.dongyang.jdo.TDataStore;
import java.sql.Timestamp;
import java.util.Date;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

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
public class InwOrder extends TDataStore{

    //需要执行的SQL
    private String exeSQL = "";
    //当前时间
    Date nowTime;

    public InwOrder() {
    }

    /**
     * 查询
     * @param CaseNo String
     * @return boolean
     */
    public boolean onQuery() {
        //设置要执行的SQL
        if (!setSQL(getExeSQL()))
            return false;
        //执行
        if (retrieve() == -1)
            return false;
        return true;
    }

    public boolean onSave(){
        //得到当前时间
        nowTime=SystemTool.getInstance().getDate();
        setCheck();
        setOperator();

        if(!this.update()){
            return false;
        }
        return true;
    }

    /**
     * 设置审核护士和审核时间
     * @return boolean
     */
    public boolean setCheck() {

        String storeName = isFilter() ? FILTER : PRIMARY;
        int rows[] = getModifiedRows(storeName);
        for (int i = 0; i < rows.length; i++) {
            setItem(rows[i], "NS_CHECK_CODE", Operator.getID(), storeName);
            setItem(rows[i], "NS_CHECK_DATE", nowTime, storeName);
        }
        return true;
    }


    /**
     * 设置操作用户
     * @param optUser String 操作用户
     * @param optDate Timestamp 操作时间
     * @param optTerm String 操作IP
     * @return boolean true 成功 false 失败
     */
    public boolean setOperator() {

        String storeName = isFilter() ? FILTER : PRIMARY;
        int rows[] = getModifiedRows(storeName);
        for (int i = 0; i < rows.length; i++) {
            setItem(rows[i], "OPT_USER", Operator.getID(), storeName);
            setItem(rows[i], "OPT_DATE", nowTime, storeName);
            setItem(rows[i], "OPT_TERM", Operator.getIP(), storeName);
        }
        return true;
    }



    public void setExeSQL(String sql){
        this.exeSQL=sql;
    }
    public String getExeSQL(){
        return this.exeSQL;
    }
    /**
     * 得到其他列数据
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm,int row,String column)
    {
        //如果这列是数据库中本身没有的实际列
        if("NS_CHECK_DATE_DAY".equals(column))
        {
            Timestamp date = parm.getTimestamp("NS_CHECK_DATE",row);
            if(date == null)
                return "";
            //给这个列一个值
            return StringTool.getString(date,"yyyy/MM/dd");
        }
        if("NS_CHECK_DATE_TIME".equals(column))
        {
            Timestamp date = parm.getTimestamp("NS_CHECK_DATE",row);
            if(date == null)
                return "";
            return StringTool.getString(date,"HH:mm:ss");
        }
        return "";
    }
    /**
     * 设置其他列数据
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setOtherColumnValue(TParm parm,int row,String column,Object value)
    {
        if("NS_CHECK_DATE_DAY".equals(column))
        {
            Timestamp date = parm.getTimestamp("NS_CHECK_DATE",row);
            if(date == null)
                setItem(row,"NS_CHECK_DATE",StringTool.getTimestamp(value.toString(),"yyyy/MM/dd"));
            return true;
        }
        if("NS_CHECK_DATE_TIME".equals(column))
        {
            /*Timestamp date = parm.getTimestamp("NS_CHECK_DATE",row);
            if(date == null)
                return "";
            return StringTool.getString(date,"HH:mm:ss");*/
        }
//        System.out.println(row + " " + column + " " + value);
        return true;
    }

}
