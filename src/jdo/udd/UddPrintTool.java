package jdo.udd;

import java.sql.Timestamp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
/**
*
* <p>Title: 住院药房打印工具类</p>
*
* <p>Description: 住院药房打印工具类</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author ehui
* @version 1.0
*/
public class UddPrintTool extends TJDOTool {
	/**
	 * 取得护士站名称
	 */
	private static final String GET_DEPT_SQL="SELECT DEPT_CHN_DESC FROM SYS_DEPT ";
	/**
     * 实例
     */
    public static UddPrintTool instanceObject;
    /**
     * 得到实例
     * @return UddPrintTool
     */
    public static UddPrintTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new UddPrintTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public UddPrintTool()
    {
        onInit();
    }
    /**
     * 返回补打显示的时间，形如：“Send On 20090514 At 112400”
     * @return
     */
    public String getPrintDate(){
    	Timestamp now=TJDODBTool.getInstance().getDBTime();
    	String time=StringTool.getString(now,"yyyyMMdd HHmmss");
    	String text="Send On "+time.substring(0,time.indexOf(" "))+" At "+time.substring(time.indexOf(" "));
    	return text;
    }
    /**
     * 根据给入的station_code返回护士站名称
     * @param stationCode
     * @return
     */
    public String getStationName(String stationCode){
    	TParm parm=new TParm(TJDODBTool.getInstance().select(GET_DEPT_SQL+" WHERE DEPT_CODE='" +stationCode+"'"));
    	StringTool d;
    	String stationName=parm.getValue("DEPT_CHN_DESC",0);
    	return stationName;
    }
}
