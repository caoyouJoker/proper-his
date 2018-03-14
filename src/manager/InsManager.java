package manager;

import java.util.Properties;
import javax.swing.JOptionPane;

import jdo.ins.InsManagerTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2011.12.07
 * @version 1.0
 */
public class InsManager {
    /**
     * Debug标记<BR>
     * true Debug状态<BR>
     * false 正常状态
     * @alias Debug标记
     */
    public static boolean DEBUG;


    /**
     * 实例化对象
     * @alias 实例
     */
    private static InsManager instanceObject;


    /**
     * 控制类名
     * @alias 控制类名
     */
    public final static String ACTION_CLASS_NAME = "action.ins.INSActionTJ";

    /**
     * 控制类名(动态Action)
     * @alias 控制类名
     */
    public final static String ACTION_ENGINE_CLASS_NAME =
        "action.ins.InsUpdateEngine";

    /**
     * 动作对象
     * @alias 动作对象
     */
    private TAction actionObject;

    /**
     * 动作对象(动态Action)
     * @alias 动作对象
     */
    private TAction actionEngineObject;
    /**
     * 获得唯一实例
     * @alias 获得唯一实例
     * @return InsManager
     */
    public static synchronized InsManager getInstance() {
        if (instanceObject == null) {
            instanceObject = new InsManager();
        }
        return instanceObject;
    }

    /**
     * 构造函数
     * @alias 构造函数
     */
    public InsManager() {
        actionObject = new TAction();
        actionEngineObject = new TAction();
    }

    //========================================//
    // 医保标记位开关
    //========================================//
    /**
     * 门诊医保运行状态
     * @return TParm NHI_DEBUG,REG_NHI_ONLINE,OPB_NHI_ONLINE<BR>
     * NHI_DEBUG<BR>
     * 0 标准：正常运行<BR>
     * 1 调试：输出各个医保动态函数调用的参数和结果到日志文件<BR>
     * REG_NHI_ONLINE<BR>
     * 0 分时链接：在挂号保存同时，只是将挂号数据写入门诊医保明细表，离院收据业务中处理医保业务<BR>
     * 1 实时链接：在挂号保存同时，不仅将挂号数据写入门诊医保明细表，还要调用门诊实时函数<BR>
     * OPB_NHI_ONLINE<BR>
     * 0 分时链接：在收费保存同时，只是将费用数据写入门诊医保明细表<BR>
     * 1 实时链接：在收费保存同时，不仅将费用数据写入门诊医保明细表，还要调用门诊实时函数<BR>
     */
    private TParm getNhiFlg() {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "HIS");
        TParm result = (TParm) actionObject.executeAction(parm, "INSActionTJ", "getNhiFlg");
        if (result.getErrCode() < 0) {
            messageBox(result.getErrName());
            return null;
        }
        return result;
    }

    /**
     * 是否是调试状态
     * @return boolean<BR>
     *  true 调试：输出各个医保动态函数调用的参数和结果到日志文件<BR>
     *  false 标准：正常运行<BR>
     */
    public boolean isDebug() {
        TParm result = getNhiFlg();
        if (result == null || result.getErrCode() != 0)
            return false;
        return result.getInt("NHI_DEBUG", 0) == 1;
    }

    /**
     * REG 是否实时链接
     * @return boolean<BR>
     *   true 实时链接：在挂号保存同时，不仅将挂号数据写入门诊医保明细表，还要调用门诊实时函数<BR>
     *   false 分时链接：在挂号保存同时，只是将挂号数据写入门诊医保明细表，离院收据业务中处理医保业务<BR>
     */
    public boolean isRegOnline() {
        TParm result = getNhiFlg();
        if (result == null || result.getErrCode() != 0)
            return false;
        return result.getInt("REG_NHI_ONLINE", 0) == 1;
    }

    /**
     * REG 是否实时链接
     * @return boolean<BR>
     *  true 实时链接：在收费保存同时，不仅将费用数据写入门诊医保明细表，还要调用门诊实时函数<BR>
     *  false 分时链接：在收费保存同时，只是将费用数据写入门诊医保明细表<BR>
     */
    public boolean isOpbOnline() {
        TParm result = getNhiFlg();

        if (result == null || result.getErrCode() != 0)
            return false;
        return result.getInt("OPB_NHI_ONLINE", 0) == 1;
    }

    //========================================//
    // 医保接口方法
    //========================================//
    /**
     * 接口方法
     * @param parm TParm 传入参数 PIPELINE,PLOT_TYPE,和相关数据<BR>
     * 具体内容参见数据库字典INS_IO表<BR>
     * 范例<BR>
     * parm.setCommitData("HOSP_AREA","HIS");<BR>
     * parm.setCommitData("PIPELINE","DataDown_rs");<BR>
     * parm.setCommitData("PLOT_TYPE","A");<BR>
     * parm.addReturnData("SID","120105800618251");<BR>
     * parm.addReturnData("NAME","张三丰");<BR>
     * parm.addReturnData("SID","120105800618251");<BR>
     * parm.addReturnData("NAME","张三丰");<BR>
     * parm.addReturnData("SID","120105800618251");<BR>
     * parm.addReturnData("NAME","张三丰");<BR>
     * TParm result = safe(parm);<BR>
     * @return TParm 传出参数
     */
    public TParm safe(TParm parm) {
        TParm result = new TParm();
        //基本数据检测
        if (parm.checkEmpty("PIPELINE,PLOT_TYPE", result)) {
            messageBox(result.getErrName());
            return null;
        }
        parm.setData("HOSP_AREA", "HIS");
//        INSInterface insInterface =new INSInterface();
//        Object obj= (Object)insInterface.safe(parm);
        //=====pangben 2012-01-29  start修改
//        Object obj = (Object)TIOM_AppServer.executeAction(
//                "action.ins.INSActionTJ",
//                "safe", parm);
        //=====pangben 2012-01-29  stop修改
        Object obj = InsManagerTool.getInstance().safe(parm);
//        System.out.println("action obj"+obj);
        if (obj instanceof String) {
//            System.out.println(obj);
            messageBox( (String) obj);
            return null;
        }
        result = (TParm) obj;
        //System.out.println("PIPELINE = " +parm.getCommitData("PIPELINE"));
        //System.out.println("PLOT_TYPE = " +parm.getCommitData("PLOT_TYPE"));
        //System.out.println("obj = "+result);
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrName());
            messageBox(result.getErrName());
            return null;
        }
        return result.getRow(0);
    }

    /**
     * 医疗卡读卡
     * @param type int
     * "1" 城职 门诊
     * "2" 城职 门特
     * "3" 城居 门诊 (无)
     * "4" 城居 门特
     * @return TParm
     */
//  public TParm readCard(String type)
//  {
//    //System.out.println("----------a");
//    ReadCard readCard = new ReadCard(type);
//    //System.out.println("----------b");
//    readCard.setVisible(true);
//    return (TParm)readCard.getReturnValue();
//  }
    /**
     * 动态Action
     * @param parm TParm
     * @return TParm
     */
    public TParm doAction(TParm parm) {
        return (TParm) actionEngineObject.executeAction(parm, "InsUpdateEngine", "doAction");
    }

    /**
     * 是否是医保窗口
     * @return boolean<BR>
     * true 是医保窗口<BR>
     * false 不是医保窗口<BR>
     */
    public boolean isInsWindow() {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "HIS");
        parm.setData("TERM_IP", publics.Comm_Operator.user_IP);
        parm.setData("ACTION_ID", "INS_GET_YB_ONLINE");
        TParm result = doAction(parm);
        if (result.getErrCode() != 0)
            return false;
        return result.getBoolean("YB_ONLINE_FLG", 0);
    }

    /**
     * 是否是医保身份
     * @param ctzCode String 身份代码
     * @return boolean<BR>
     * true 是医保身份<BR>
     * false 不是医保身份
     */
    public boolean isInsCtz(String ctzCode) {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "HIS");
        parm.setData("CTZ_CODE", ctzCode);
        parm.setData("ACTION_ID", "INS_GET_CTZ_IS_INS");
        TParm result = doAction(parm);
        if (result.getErrCode() != 0)
            return false;
        return result.getBoolean("NHI_CTZ_FLG", 0);
    }

    public TParm getNhiRegCode(String admType, String clinicTypeCode) {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "HIS");
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        parm.setData("ACTION_ID", "INS_GET_NHI_CODE");
        parm.setData("ACTION_ID", "INS_GET_NHI_CODE");
        return doAction(parm);
    }

    /**
     * 最高床位费
     * @return double
     */
    public double getTipBedprice() {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "HIS");
        parm.setData("REGION_CODE","HIS");
        parm.setData("ACTION_ID", "INS_GETTIP_BEDPRICE");
        TParm result = doAction(parm);
        if (result.getErrCode() != 0) {
            messageBox("最高床位费读取失败!" + result.getErrName());
            return -1;
        }
        return result.getDouble("TIP_BEDPRICE", 0);
    }

    /**
     * 费用分割
     * @param admType String
     * @param clinicTypeCode String
     * @param ctz1Code String
     * @return TParm
     */
    public TParm regFeePartition(String admType, String clinicTypeCode,
                                 String ctz1Code) {
        TParm parm = new TParm();
        //区域
        parm.setData("REGION_CODE", "HIS");
        //人员类别
        parm.setData("CTZ1_CODE", ctz1Code);
        //门急住别
        parm.setData("ADM_TYPE", admType);
        //号别
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        //医院编码
        parm.setData("HOSP_NHI_NO", "000551");

        return (TParm) actionObject.executeAction(parm, "INSActionTJ", "regFeePartition");
    }

    /**
     * 提示消息窗口
     * @param text 提示信息
     * @alias 消息对话框(窗口)
     */
    public void messageBox(String text) {
        if (DEBUG) {
            return;
        }
        JOptionPane.showMessageDialog(null, text);
    }

    //========================================//
    // 测试区 用于调试程序,不参与流程
    //========================================//
    public Properties test_Data() {
        Properties prop = new Properties();
        prop.setProperty("HOSP_AREA", "HIS");
        return prop;
    }

    public boolean test_getNhiFlg() {
//        System.out.println("========>test_getNhiFlg()");
        TParm result = getNhiFlg();
//        System.out.println("outPut:" + result);
        boolean state = result.getErrCode() == 0;
//        System.out.println("state:" + state);
        return state;
    }

    public boolean test_NhiFlg1() {
//        System.out.println("isDebug()=" + isDebug());
//        System.out.println("isRegOnline()=" + isRegOnline());
//        System.out.println("isOpbOnline()=" + isOpbOnline());
        return true;
    }
//  public static void main(String args[])
//  {
//    publics.Comm_System.hosp_Host = "127.0.0.1:8090";
//    publics.Comm_System.nhi_Hosp_Code = "000551";
//    SysManager.getInstance().setOperator(testserver.data.IData.OPERATE);
//    InsManager manager = InsManager.getInstance();
//    //manager.test_getNhiFlg();//OK
//    //System.out.println(manager.isInsWindow());//OK
//    //System.out.println(manager.isInsCtz("99"));//OK
//    //System.out.println(manager.getNhiRegCode("O","03"));//ok
//    //System.out.println(manager.getTipBedprice());//ok
//    //System.out.println(manager.regFeePartition("O","03","51"));
//    System.out.println(manager.readCard("1"));
//
//
//    /*TParm parm = new ActionParm();
//    parm.setCommitData("PIPELINE", "DataDown_rs");
//    parm.setCommitData("PLOT_TYPE", "A");
//    parm.addReturnData("SID",  "120105800618251");
//    parm.addReturnData("NAME", "张三丰");
//    parm.addReturnData("SID",  "120105800618251");
//    parm.addReturnData("NAME", "张三丰");
//    parm.addReturnData("SID",  "120105800618251");
//    parm.addReturnData("NAME", "张三丰");
//    System.out.println(manager.safe(parm));//OK*/
//
//    //System.out.println("manager.readCard()=" + manager.readCard());
//
//
//  }
}
