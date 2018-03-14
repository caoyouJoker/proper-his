package action.ins;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import jdo.ins.INSTool;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.db.TConnection;
import com.dongyang.config.TConfig;
import java.util.Map;
import java.io.IOException;
import com.dongyang.util.FileTool;
import com.dongyang.jdo.TJDODBTool;
import java.io.FileWriter;
import java.io.FileOutputStream;

/**
 *
 * <p>Title: 天津医保接口类(外部交互)</p>
 *
 * <p>Description: 天津医保接口类(外部交互)</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2011.12.07
 * @version 1.0
 */
public class INSInterface {
    public static boolean DEBUG = true;
    public INSInterface() {
    }

    /**
     * 获得数据模型INSTool
     * @alias 获得数据模型INSTool
     * @return INSTool
     */
    public INSTool getTool() {
        return INSTool.getInstance();
    }

    /**
     * 得到数据库连接
     * @alias 得到数据库连接
     * @return Connection 返回一个数据库连接
     */
    public Connection getConnect() {
        Connection connection = null;
        TConnection conn;
        conn = null;
        try {
            connection = conn.getConn();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    //========================================//
    // 医保标记位开关
    //========================================//

    /**
     * 门诊医保运行状态(Client 接口)
     * @param parm Object TParm HOSP_AREA
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
    public TParm getNhiFlg(Object parm) {
        TParm result = new TParm();
        //检核参数是否为空
        if (parm == null) {
            result.setErr( -1,
                          "action.ins.InsInterface.getNhiFlg->Err:参数为空");
            return result;
        }
        //检核参数类型是否正确
        if (! (parm instanceof TParm)) {
            result.setErr( -1,
                          "action.ins.InsInterface.getNhiFlg->Err:参数类型错误");
            return result;
        }
        TParm acParm = (TParm) parm;
        return getNhiFlg(acParm);
    }

    /**
     * 门诊医保运行状态(Server 接口)
     * @param parm TParm HOSP_AREA
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
    public TParm getNhiFlg(TParm parm) {
        TParm result = new TParm();
        //基本数据检测
        if (parm.checkEmpty("HOSP_AREA", result)) {
            return result;
        }
        result = getTool().getNhiFlg(parm);
        if (result.getErrCode() < 0)
            return result;
        if (result.getErrCode() > 0) {
            result.setErr( -1, "SYS_HOSP_AREA 表中没有找到 [HOSP_AREA=" +
                          parm.getValue("HOSP_AREA") + "]医保的相关状态数据!");
            return result;
        }
        return result;
    }

    /**
     * 是否是调试状态
     * @param HospArea String HospArea
     * @return boolean<BR>
     *  true 调试：输出各个医保动态函数调用的参数和结果到日志文件<BR>
     *  false 标准：正常运行<BR>
     */
    public boolean isDebug(String HospArea) {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", HospArea);

        TParm result = getNhiFlg(parm);

        if (result.getErrCode() != 0)
            return false;
        return result.getInt("NHI_DEBUG", 0) == 1;
    }

    /**
     * REG 是否实时链接
     * @param HospArea String HOSP_AREA
     * @return boolean<BR>
     *   true 实时链接：在挂号保存同时，不仅将挂号数据写入门诊医保明细表，还要调用门诊实时函数<BR>
     *   false 分时链接：在挂号保存同时，只是将挂号数据写入门诊医保明细表，离院收据业务中处理医保业务<BR>
     */
    public boolean isRegOnline(String HospArea) {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", HospArea);

        TParm result = getNhiFlg(parm);

        if (result.getErrCode() != 0)
            return false;
        return result.getInt("REG_NHI_ONLINE", 0) == 1;
    }

    /**
     * OPB 是否实时链接
     * @param HospArea String HOSP_AREA
     * @return boolean<BR>
     *  true 实时链接：在收费保存同时，不仅将费用数据写入门诊医保明细表，还要调用门诊实时函数<BR>
     *  false 分时链接：在收费保存同时，只是将费用数据写入门诊医保明细表<BR>
     */
    public boolean isOpbOnline(String HospArea) {
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", HospArea);

        TParm result = getNhiFlg(parm);

        if (result.getErrCode() != 0)
            return false;
        return result.getInt("OPB_NHI_ONLINE", 0) == 1;
    }

    //========================================//
    // 医保接口方法
    //========================================//
    /**
     * 接口方法(Client 接口)
     * @param parm Object TParm 传入参数 HOSP_AREA,PIPELINE,PLOT_TYPE,和相关数据<BR>
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
    public TParm safe(Object parm) {
        log("safe()", "start input object");
        TParm result = new TParm();
        //检核参数是否为空
        if (parm == null) {
            result.setErr( -1,
                          "action.ins.InsInterface.safe->Err:参数为空");
            return result;
        }
        //检核参数类型是否正确
        if (! (parm instanceof TParm)) {
            result.setErr( -1,
                          "action.ins.InsInterface.safe->Err:参数类型错误");
            return result;
        }
        TParm acParm = (TParm) parm;
        return safe(acParm);
    }

    /**
     * 接口方法
     * @param parm TParm 传入参数 HOSP_AREA,PIPELINE,PLOT_TYPE,和相关数据<BR>
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
        log("safe()", "start input TParm");
        TParm result = new TParm();

//        if (!IsinsIodata(parm.getValue("PIPELINE"), parm.getValue("PLOT_TYPE"),
//                         "IN", parm.getInt("PARM_COUNT",0)))
//            return result;
        if (!isInit())
            if (!init()) {
                result.setErr( -1, "医保服务器没有准备好!");
                return result;
            }
        log("safe()", "Init ok");
        result.setData("PIPELINE", parm.getData("PIPELINE"));
        result.setData("PLOT_TYPE", parm.getData("PLOT_TYPE"));
        result.setData("HOSP_AREA", parm.getData("HOSP_AREA"));
        //解析传入数据
        if (!parseIn(parm, result))
            return result;
        log("safe()", "parseIn ok");

        //向接口传送数据
        result = linkFunction(parm);
        if (result.getErrCode() < 0)
            return result;
        log("safe()", "linkFunction ok");

       //处理返回值
//       String returnValue = result.getReturnValue("_RETURN_");

//    if(!"0".equals(returnValue))
//    {
//      result.setErr(-1,"医保函数返回结果异常=" + returnValue);
//      return result;
//    }
        log("safe()", "returnValue=!0 ok");
        //解析传出数据
        result = parseOut(parm, result);
        String returnValue = result.getValue("PROGRAM_STATE", 0);
        if (returnValue == null || returnValue.length() == 0)
            return result;
        try {
            if (Integer.parseInt(returnValue) < 0) {
                result.setErr( -1, result.getValue("PROGRAM_MESSAGE", 0));
                return result;
            }
        }
        catch (Exception e) {
            result.setErr( -1, e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 传入参数解析
     * @param parm TParm
     * @param result TParm
     * @return boolean
     */
    private boolean parseIn(TParm parm, TParm result) {
        log("parseIn()", "start");
        //基本数据检测
        if (parm.checkEmpty("PIPELINE,PLOT_TYPE,HOSP_AREA", result)) {
            return false;
        }
        log("parseIn()",
            parm.getValue("PIPELINE") + " " + parm.getValue("PLOT_TYPE"));
        //得到医保基本参数
        TParm sysParm = getTool().getSysParm(parm);
        if (sysParm.getErrCode() < 0) {
            result.setErr( -1, sysParm.getErrText(), sysParm.getErrName());
            return false;
        }
        if (sysParm.getErrCode() > 0) {
            result.setErr( -1, "INS_SYSPARM 表中没有找到默认的医保参数!");
            return false;
        }
        log("parseIn()", "getSysParm ok");

        parm.setData("sysParm", sysParm);
        parm.setData("CITY", sysParm.getData("CITY", 0));
        //读取医保接口函数信息
        TParm ioParm = getTool().getIOParm(parm);
        if (ioParm.getErrCode() < 0) {
            result.setErr( -1, ioParm.getErrText(), ioParm.getErrName());
            return false;
        }
        if (ioParm.getErrCode() > 0) {
            result.setErr( -1, "INS_IO_PARM 表中没有找到[CITY=" +
                          parm.getValue("CITY") + " PIPELINE=" +
                          parm.getValue("PIPELINE") + " PLOT_TYPE=" +
                          parm.getValue("PLOT_TYPE") + "]的相关数据!");
            return false;
        }
        log("parseIn()", "getIOParm ok");
        parm.setData("ioParm", ioParm);

        //得到传入接口数据类型
        parm.setData("IN_OUT", "IN");
        TParm ioData = getIOData(parm);
        if (ioData.getErrCode() < 0) {
            result.setErr( -1, ioData.getErrText(), ioData.getErrName());
            return false;
        }
        log("parseIn()", "getIOData IN ok");
        int count = ioData.getCount("ID");
        StringBuffer sb = new StringBuffer();
        //默认的分割符
        String separator = sysParm.getValue("SEPARATOR", 0);
        //默认的行结束符
        String newline = initNewline(sysParm.getValue("NEWLINE", 0));
        //默认的结束符
        String finish = initNewline(sysParm.getValue("FINISH", 0));
        String cn = ioData.getValue("COLUMN_NAME", 0);
        if (parm.getData(cn) == null) {
            result.setErr( -1, "InsInterface.parse()parm 缺少 "
                          + cn + " " + ioData.getData("COLUMN_DESC", 0) +
                          " 必要参数");
            return false;
        }

        int rowCount = parm.getCount(cn);
        for (int row = 0; row < rowCount; row++) {
            for (int i = 0; i < count; i++) {
                if (i > 0)
                    sb.append(separator);

                String columnName = ioData.getValue("COLUMN_NAME", i);
                boolean need = ioData.getBoolean("NEED", i);
                String dataType = ioData.getValue("DATA_TYPE", i);
                String columnDesc = ioData.getValue("COLUMN_DESC", i);
                String format = ioData.getValue("FORMAT", i);
                String defaultValue = ioData.getValue("DEFAULT_VALUE", i);
                int precision = 0;
                int scale = 0;
                int length = 0;
                try {
                    precision = ioData.getInt("PRECISION", i);
                    scale = ioData.getInt("SCALE", i);
                    length = ioData.getInt("LENGTH", i);
                }
                catch (Exception e) {
                    result.setErr( -1, "INS_IO_表中数据非法[CITY=" +
                                  parm.getValue("CITY") + " PIPELINE=" +
                                  parm.getValue("PIPELINE") + " PLOT_TYPE=" +
                                  parm.getValue("PLOT_TYPE") +
                                  " IN_OUT=IN] 在 PRECISION,SCALE,LENGTH 列中存在null!");
                    return false;
                }
                String data = parm.getValue(columnName, row);
                //检测必要数据是否存在
                if (need && (data.length() == 0)) {
                    result.setErr( -1, "InsInterface.parse()parm 缺少 "
                                  + columnName + " " + columnDesc + " 必要参数在第 " +
                                  row + " 行!");
                    return false;
                }
                //扩展其他检核

                if (data.length() == 0) {
                    if (defaultValue != null && defaultValue.length() > 0)
                        data = defaultValue;
                    else
                        data = " ";
                }
                if (format != null && format.length() > 0) {
                    if ("BOOLEAN".equals(format)) {
                        if ("Y".equals(data))
                            data = "1";
                        if ("N".equals(data))
                            data = "0";
                        if ("".equals(data))
                            data = "0";
                    }
                }
                //拼接数据
                sb.append(data);
            }
            if (rowCount > 1)
                sb.append(newline);
        }
        sb.append(finish);

        parm.setData("PARAM", sb.toString());
        //测试start
//        try {
//            String fileName = parm.getValue("PIPELINE") + "_" +
//                parm.getValue("PLOT_TYPE");
//            String subValue = sb.toString();
//            FileTool fileA = new FileTool();
//            fileA.setString("D:\\log\\INS_IN\\" + fileName + ".txt",
//                               subValue,false);
////            FileWriter file =new FileWriter("D:\\log\\INS_IN\\" + fileName + ".txt");
////            file.write(subValue);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
        //测试end
        //得到传出接口数据类型
        parm.setData("IN_OUT", "OUT");
        ioData = getIOData(parm);
        if (ioData.getErrCode() < 0) {
            result.setErr( -1, ioData.getErrText(), ioData.getErrName());
            return false;
        }
        log("parseIn()", "getIOData OUT ok");
        //计算返回数据长度
        //数据库设定最大缓冲行数
        int row = ioParm.getInt("ROW_COUNT", 0);
        int dataSize = 0;
        //输出列个数
        count = ioData.getCount("ID");
        //累加总输出数据字节
        for (int i = 0; i < count; i++)
            dataSize += ioData.getInt("LENGTH", i);
        //累加分割符和换行符
        dataSize += count;
        //乘预先设定行数
        dataSize *= row;
        //加结束符
        dataSize += 1;

        parm.setData("ioOutData", ioData);
        parm.setData("RETURN_DATA_COUNT", dataSize);
        return true;
    }

    /**
     * 传出参数解析
     * @param parm TParm
     * @param resultParm TParm
     * @return TParm
     */
    public TParm parseOut(TParm parm, TParm resultParm) {
        TParm result = new TParm();
        //得到医保基本参数
        TParm sysParm = (TParm) parm.getData("sysParm");
//        TParm ioParm = (TParm) parm.getData("ioParm");
        TParm ioData = (TParm) parm.getData("ioOutData");
        //默认的分割符
        String separator = sysParm.getValue("SEPARATOR", 0);
        //默认的行结束符
        String newline = initNewline(sysParm.getValue("NEWLINE", 0));
        //默认的结束符
        String finish = initNewline(sysParm.getValue("FINISH", 0));
        byte[] byteData = (byte[]) resultParm.getData("RETURN_DATA");
        String data = new String(byteData);
        if (data.length() == 0) {
            result.setErr( -1, resultParm.getValue("_RETURN_"));
            return result;
        }
        /*System.out.println(" finish = " + finish);
             int index = data.lastIndexOf(finish);
             System.out.println("# index = " + index);
             if(index >= 0)
          data = data.substring(0,index);
         if(!data.substring(data.length() - newline.length()).equals(newline))
          data += newline;
         */

        data = data.trim();
        if (data.endsWith(finish)) {
            data = data.substring(0, data.length() - finish.length());
        }
        if (!data.endsWith(newline))
            data += newline;
        int count = ioData.getCount("ID");
        String[] sData = parseNewLine(data, newline, false);
        int head = -1;
        for (int i = 0; i < sData.length; i++) {
            String[] cData = parseNewLine(sData[i], separator, true);
            if (count == cData.length) {
                for (int j = 0; j < count; j++) {
                    String columnName = ioData.getValue("COLUMN_NAME", j);
                    String format = ioData.getValue("FORMAT", j);
                    String dataType = ioData.getValue("DATA_TYPE", j);
                    String value = cData[j];
                    if ("DATE".equals(dataType))
                        value = dateFormat(value, format);
                    result.addData(columnName, value);
                }
                continue;
            }
            else if (cData.length != 2 && count != cData.length + 2) {
                //cData.length = 2为首行返回参数
                //count != cData.length + 2 返回参数后面是char(6)分隔符
                if (cData.length == 3) {
                    result.setErr( -1,
                                  trim(cData[0]) + "," + trim(cData[1]) + "," +
                                  trim(cData[2]));
                    return result;
                }
                result.setErr( -1, "InsInterface.parseOut()返回参数与预先设定参数个数不符 "
                              + " [row=" + i + "][Data=" + count + " DB=" +
                              cData.length + "] " + trim(data));
                return result;
            }

            if (i == 0) {
                for (int j = 0; j < cData.length; j++) {
                    String columnName = ioData.getValue("COLUMN_NAME", j);
                    String format = ioData.getValue("FORMAT", j);
                    String dataType = ioData.getValue("DATA_TYPE", j);
                    String value = cData[j];
                    if ("DATE".equals(dataType))
                        value = dateFormat(value, format);
                    result.addData(columnName, value);
                    head = j + 1;
                }
                continue;
            }
            if (head > 0) {
                for (int j = 0; j < cData.length; j++) {
                    String columnName = ioData.getValue("COLUMN_NAME", j + head);
                    String format = ioData.getValue("FORMAT", j + head);
                    String dataType = ioData.getValue("DATA_TYPE", j + head);
                    String value = cData[j];
                    if ("DATE".equals(dataType))
                        value = dateFormat(value, format);
                    result.addData(columnName, value);
                }
                continue;
            }
        }
        return result;
    }

    public String dateFormat(String s, String format) {
        if (format == null)
            format = "";
        StringBuffer v = new StringBuffer();
        for (int i = 0; i < s.length(); i++)
            switch (s.charAt(i)) {
                case ' ':
                case '/':
                case '-':
                case ':':
                    break;
                default:
                    v.append(s.charAt(i));
            }
        String value = v.toString();
        if ("DATE".equals(format))
            if (value.length() > 8)
                value = value.substring(0, 8);
        return value;
    }

    /**
     * 将数据拆分成多行
     * @param s String
     * @param newline String
     * @param b boolean
     * @return String[]
     */
    private String[] parseNewLine(String s, String newline, boolean b) {
        List list = new ArrayList();
        if (s.startsWith(newline))
            s = s.substring(newline.length(), s.length());
        int index = s.indexOf(newline);
        while (index >= 0) {
            list.add(s.substring(0, index));
            s = s.substring(index + newline.length(), s.length());
            index = s.indexOf(newline);
        }
        if (b)
            list.add(s);
        return (String[]) list.toArray(new String[] {});
    }

    /**
     * 初始化换行符
     * @param s String
     * @return String
     */
    public String initNewline(String s) {
        if (s.startsWith("char(") && s.endsWith(")"))
            return "" + ( (char) Integer.parseInt(s.substring(5, s.length() - 1)));
        return s;
    }

    /**
     * 得到接口参数数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getIOData(TParm parm) {
        TParm ioData = getTool().getIOData(parm);
        if (ioData.getErrCode() < 0)
            return ioData;
        if (ioData.getErrCode() > 0) {
            ioData.setErr( -1, "INS_IO 表中没有找到 [CITY=" +
                          parm.getValue("CITY") + " PIPELINE=" +
                          parm.getValue("PIPELINE") + " PLOT_TYPE=" +
                          parm.getValue("PLOT_TYPE") + " IN_OUT=" +
                          parm.getValue("IN_OUT") + "]的相关数据!");
            return ioData;
        }
        return ioData;
    }

    /**
     * 初始化医保接口
     * @return boolean<BR>
     *  true 初始化成功<BR>
     *  false 初始化失败
     */
    private boolean init() {
        TParm parm = new TParm();
        //得到 TConfig.x
        TConfig prop = getProp();
        if (prop == null) {
            return false;
        }
        //接口日志输出目录文件
        String insLogPath = prop.getString("", "InsLogPath");
        //医保类型 0 正式医保接口 1 测试医保接口
        String insType = prop.getString("", "InsType");

        //是否输出接口日志
        String insDebug = prop.getString("", "InsDebug");

        if (insLogPath == null || insLogPath.length() == 0) {
            System.out.println("TConfig.x insLogPath 参数没有配好");
            insDebug = "0";
        }
        if (insType == null || insType.length() == 0)
            System.out.println("TConfig.x insType 参数没有配好");
        if (insDebug == null || insDebug.length() == 0)
            System.out.println("TConfig.x insDebug 参数没有配好");

        int type = 0;
        int debug = 0;

        try {
            type = Integer.parseInt(insType);
        }
        catch (Exception e) {
            System.out.println("TConfig.x insType 参数没有配好");
        }
        try {
            debug = Integer.parseInt(insDebug);
        }
        catch (Exception e) {
            System.out.println("TConfig.x insDebug 参数没有配好");
        }

        //配置 InsServer 调用参数
        parm.setData("_ACTION_", 0x2);
        parm.setData("_TYPE_", type);
        parm.setData("_PATH_", insLogPath);
        parm.setData("_ISDEBUG_", debug);
        TParm result = doSocket(parm);
        if (result.getErrCode() == -1)
            return false;

        return result.getBoolean("_IS_INIT_");
    }

    /**
     * InsServer 是否成功初始化医保接口的DLL
     * @return boolean true 已经初始化 false 没有初始化
     */
    private boolean isInit() {
        TParm parm = new TParm();
        parm.setData("_ACTION_", 0x1);
        //System.out.println("isInit>>parm" + parm);
        TParm result = doSocket(parm);
       // System.out.println("isInit>>result" + result);
        if (result.getErrCode() == -1)
            return false;
        return result.getBoolean("_IS_INIT_");
    }

    public TParm linkFunction(Object parm) {
        log("linkFunction()", "linkFunction input object");
        TParm result = new TParm();
        //检核参数是否为空
        if (parm == null) {
            result.setErr( -1,
                          "action.ins.InsInterface.linkFunction->Err:参数为空");
            return result;
        }
        //检核参数类型是否正确
        if (! (parm instanceof TParm)) {
            result.setErr( -1,
                          "action.ins.InsInterface.linkFunction->Err:参数类型错误");
            return result;
        }
        TParm actionParm = (TParm) parm;
        return linkFunction(actionParm);
    }

    /**
     * 调用 InsServer 的接口函数
     * @param parm TParm
     * @return TParm
     */
    public TParm linkFunction(TParm parm) {
        log("linkFunction()", "start");
        parm.setData("_ACTION_", 0x3);
        log("linkFunction()", "doSocket");
       // System.out.println("doSocket3333333333   " + parm);
//        parm.setData("ROW_COUNT",parm.getParm("ioParm").getData("ROW_COUNT",0));
        TParm parmm = new TParm();
        parmm.setData("sysParm", ( (TParm) parm.getData("sysParm")).getData());
        parmm.setData("ioOutData", ( (TParm) parm.getData("ioOutData")).getData());
        parmm.setData("ioParm", ( (TParm) parm.getData("ioParm")).getData());
        parmm.setData("_ACTION_", 0x3);
        parmm.setData("RETURN_DATA_COUNT", parm.getData("RETURN_DATA_COUNT"));
        parmm.setData("PIPELINE", parm.getData("PIPELINE"));
        parmm.setData("PLOT_TYPE", parm.getData("PLOT_TYPE"));
        parmm.setData("IN_OUT", parm.getData("IN_OUT"));
        parmm.setData("CITY", parm.getData("CITY"));
        parmm.setData("HOSP_AREA", parm.getData("HOSP_AREA"));
        parmm.setData("TEXT", parm.getData("TEXT"));
        parmm.setData("PARAM", parm.getData("PARAM"));

        TParm result = doSocket(parmm);
        if (result.getErrCode() == -1)
            return result;
        log("linkFunction()", "result ok");

        return result;
    }

    /**
     * 读取 TConfig.x
     * @return TConfig
     */
    private TConfig getProp() {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
//        TConfig config = new TConfig(
//            "%ROOT%\\WEB-INF\\config\\system\\TConfig.x",
//            TIOM_AppServer.SOCKET);
        return config;
    }

    /**
     * Socket 呼叫 InsServer
     * @param parm TParm
     * @return TParm
     */
    private TParm doSocket(TParm parm) {
        //System.out.println("Socket 呼叫 InsServer");
        TParm result = new TParm();

        //得到 TConfig.x
        TConfig prop = getProp();
        if (prop == null) {
            result.setErr( -1, "TConfig.x 文件没有找到:");
            return result;
        }

        //InsServer IP
        String insHost = prop.getString("", "InsHost");
        //InsServer Port
        String insPort = prop.getString("", "InsPort");

        //check Ins Parm from TConfig.x
        if (insHost == null || insHost.length() == 0) {
            result.setErr( -1, "TConfig.x InsHost参数没有配好");
            return result;
        }
        if (insPort == null || insPort.length() == 0) {
            result.setErr( -1, "TConfig.x InsPort参数没有配好");
            return result;
        }
        int port = 0;
        try {
            port = Integer.parseInt(insPort);
        }
        catch (Exception e) {
            result.setErr( -1, "TConfig.x InsPort不是数字");
            return result;
        }
        //call socket
//    Object obj = DtAction.doSocket(insHost,port,parm);
        TSocket socket;
        socket = new TSocket(insHost, port);
        Object obj = socket.doSocket(parm.getData());
        if (obj == null) {
            result.setErr( -1, "医保服务器没有启动!");
            return result;
        }
        result = new TParm( (Map) obj);
        return result;
    }

    public String trim(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ( (int) c != 0)
                sb.append(c);
        }
        return sb.toString();
    }

    public void log(String function, String text) {
        if (DEBUG)
            System.out.println(getClass().getName() + "." + function + "->" +
                               text);
    }

    //========================================//
    // 测试区 用于调试程序,不参与流程
    //========================================//
    /*public Properties test_Data()
       {
      Properties prop = new Properties();
      prop.setProperty("HOSP_AREA","HIS");
      return prop;
       }
       public boolean test_getNhiFlg()
       {
      ActionParm parm = new ActionParm();
      parm.setCommitData("HOSP_AREA",test_Data().getProperty("HOSP_AREA"));
      System.out.println("========>test_getNhiFlg()");
      System.out.println("inPut:" + parm);
      ActionParm result = getNhiFlg(parm);
      System.out.println("outPut:" + result);
      boolean state = result.getErrCode() == 0;
      System.out.println("state:" + state);
      return state;
       }
       public boolean test_NhiFlg1()
       {
      System.out.println("isDebug()=" + isDebug(test_Data().getProperty("HOSP_AREA")));
      System.out.println("isRegOnline()=" + isRegOnline(test_Data().getProperty("HOSP_AREA")));
      System.out.println("isOpbOnline()=" + isOpbOnline(test_Data().getProperty("HOSP_AREA")));
      return true;
       }*/
    public static void main(String args[]) {
        INSInterface driver = new INSInterface();
//        System.out.println(driver.dateFormat("2007-12-10", ""));
//        System.out.println(driver.dateFormat("2007/12/10", ""));
//        System.out.println(driver.dateFormat("2007/12/10 23:59:59", "DATE"));
//        System.out.println(driver.isInit()); //OK
        //System.out.println(driver.init());//OK

        //driver.test_getNhiFlg();//OK
        //driver.test_NhiFlg1();//OK

        //driver.init(1,"D:\\工作\\NEW\\log\\aaa.txt",1);
        //ActionParm parm = new ActionParm();

        //parm.setCommitData("HOSP_AREA","HIS");
        //parm.setCommitData("PIPELINE","DataDown_mtd");
        //parm.setCommitData("PLOT_TYPE","H");
        //parm.addReturnData("CARD_NO","120105800618251");
        /*parm.addReturnData("NAME","张三丰");
             parm.addReturnData("SID","120105800618251");
             parm.addReturnData("NAME","张三丰");
             parm.addReturnData("SID","120105800618251");
             parm.addReturnData("NAME","张三丰");
         */
        //System.out.println(driver.safe(parm));


        //byte[] b = new byte[14];
        //System.out.println(driver.DataDown_rs("AAA你好","BBB",b));
        //System.out.println(driver.DataDown_rs("AAA你好","BBB",b));
        //System.out.println(driver.DataDown_rs("AAA你好","BBB",b));
        //System.out.println(driver.DataDown_sp("aaa","BBB",b));
        //System.out.println(driver.DataUpload("AAA","BBB",b));
        //System.out.println(driver.DataDown_sp1("AAA","BBB",b));
        //System.out.println(driver.DataDown_yb("AAA","BBB",b));
        //System.out.println(new String(b));
        //for(int i = 0;i < b.length;i++)
        //  System.out.print(b[i] + " ");
    }


    /**
     * 检验方法中参数正确性（方法共用）
     * @param interName String   函数名称
     * @param method String      接口方法
     * @param in_outType String  函数类型
     * @param count int          函数个数
     * @return boolean           返回值类型
     */
    private boolean IsinsIodata(String interName, String method,
                                String in_outType, int count) {
        boolean flg = true;
        StringBuffer Str = new StringBuffer();
        if (!interName.equals("")) {
            Str.append(" PIPELINE='" + interName + "'");
            Str.append(" AND ");
        }
        if (!method.equals("")) {
            Str.append(" PLOT_TYPE='" + method + "'");
            Str.append(" AND ");
        }
        if (!in_outType.equals("")) {
            Str.append(" IN_OUT='" + in_outType + "'");
        }
        String sql = " SELECT COLUMN_NAME,COLUMN_DESC,DATA_TYPE,LENGTH,PRECISION,SCALE,NEED,DEFAULT_VALUE," +
            " FORMAT,REMARK " +
            " FROM INS_IO" +
            " WHERE " + Str.toString();
        TParm parm = new TParm(this.getDBTool().select(sql));
        int Parmcount = parm.getCount();
        if (Parmcount < 0) {
            flg = false;
        }
        if (Parmcount != count) {
            flg = false;
        }
        return flg;
    }

    /**
     * getDBTool
     * 数据库工具实例
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }


}
