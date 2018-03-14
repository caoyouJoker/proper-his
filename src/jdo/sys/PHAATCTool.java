package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.config.TConfig;
import com.javahis.util.JavaHisDebug;
import java.sql.Timestamp;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.data.TSocket;

/**
 * <p>Title: 生成包药机/摆药机XML文件</p>
 *
 * <p>Description: 生成包药机/摆药机XML文件</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company:javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class PHAATCTool {

    //流水号静态变量
    static int seqNo = 0;

    /**
     * 构造器
     */
    public PHAATCTool() {
    }

    /**
     * 取得入库单号
     * @return String
     */
    static public String getATCNo(){
        return SystemTool.getInstance().getNo("ALL", "PHA",
                "ATC_NO", "ATC_NO");
    }
    /**
     * 产生送包药机XML文件(门诊)
     * @return boolean
     */
    static public boolean generateATCXMLO(TParm parm){
        TParm  drugListParm = parm.getParm("DRUG_LIST_PARM");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
        stringBuffer.append("<JAVAHIS-DRUG-IO>");
        //患者姓名
        stringBuffer.append("<NAME>");
        stringBuffer.append(parm.getValue("NAME"));
        stringBuffer.append("</NAME>");
        //病案号
        stringBuffer.append("<MRNO>");
        stringBuffer.append(parm.getValue("MRNO"));
        stringBuffer.append("</MRNO>");
        //住院号
        stringBuffer.append("<IPDNO>");
        stringBuffer.append(parm.getValue("IPDNO"));
        stringBuffer.append("</IPDNO>");
        //包药日期
        stringBuffer.append("<DATE>");
        stringBuffer.append(parm.getValue("DATE"));
        stringBuffer.append("</DATE>");
        //科室
        stringBuffer.append("<DEPT>");
        stringBuffer.append(parm.getValue("DEPT"));
        stringBuffer.append("</DEPT>");
        //科室名称
        stringBuffer.append("<DEPT_DESC>");
        stringBuffer.append(parm.getValue("DEPT_DESC"));
        stringBuffer.append("</DEPT_DESC>");
        //领药号
        stringBuffer.append("<PRESCRIPT_NO>");
        stringBuffer.append(parm.getValue("PRESCRIPT_NO"));
        stringBuffer.append("</PRESCRIPT_NO>");
        //处方号
        stringBuffer.append("<RX_NO>");
        stringBuffer.append(parm.getValue("RX_NO"));
        stringBuffer.append("</RX_NO>");
        //领药品窗口
        stringBuffer.append("<WINDOW_NO>");
        stringBuffer.append(parm.getValue("WINDOW_NO"));
        stringBuffer.append("</WINDOW_NO>");
        //门急别
        stringBuffer.append("<ADM_TYPE>");
        stringBuffer.append(parm.getValue("ADM_TYPE"));
        stringBuffer.append("</ADM_TYPE>");
        //门诊住院注记
        stringBuffer.append("<TYPE>");
        stringBuffer.append("1");
        stringBuffer.append("</TYPE>");
        //药品列表
        stringBuffer.append("<DRUGS>");
        for(int i = 0;i < drugListParm.getCount("SEQ");i++){
            stringBuffer.append("<DRUG>");
            //序号
            stringBuffer.append("<SEQ>");
            stringBuffer.append(drugListParm.getValue("SEQ",i));
            stringBuffer.append("</SEQ>");
            //药品代码
            stringBuffer.append("<ORDER_CODE>");
            stringBuffer.append(drugListParm.getValue("ORDER_CODE",i));
            stringBuffer.append("</ORDER_CODE>");
            //药品商品名
            stringBuffer.append("<ORDER_GOODS_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_GOODS_DESC",i));
            stringBuffer.append("</ORDER_GOODS_DESC>");
            //药品化学名
            stringBuffer.append("<ORDER_CHEMICAL_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_CHEMICAL_DESC",i));
            stringBuffer.append("</ORDER_CHEMICAL_DESC>");
            //药品英文名
            stringBuffer.append("<ORDER_ENG_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_ENG_DESC",i));
            stringBuffer.append("</ORDER_ENG_DESC>");
            //剂量
            stringBuffer.append("<QTY>");
            stringBuffer.append(drugListParm.getValue("QTY",i));
            stringBuffer.append("</QTY>");
            //给药频次
            stringBuffer.append("<FREQ>");
            stringBuffer.append(drugListParm.getValue("FREQ",i));
            stringBuffer.append("</FREQ>");
            //规格
            stringBuffer.append("<SPECIFICATION>");
            stringBuffer.append(drugListParm.getValue("SPECIFICATION",i));
            stringBuffer.append("</SPECIFICATION>");
            //发药单位库存单位转换率
            stringBuffer.append("<TRANS_RATE>");
            stringBuffer.append(drugListParm.getValue("TRANS_RATE",i));
            stringBuffer.append("</TRANS_RATE>");
            //给药日数
            stringBuffer.append("<DAY>");
            stringBuffer.append(drugListParm.getValue("DAY",i));
            stringBuffer.append("</DAY>");
            //首餐时间
            stringBuffer.append("<START_DTTM>");
            stringBuffer.append(drugListParm.getValue("START_DTTM",i));
            stringBuffer.append("</START_DTTM>");
            //餐包注记
            stringBuffer.append("<FLG>");
            stringBuffer.append(drugListParm.getValue("FLG",i));
            stringBuffer.append("</FLG>");
            stringBuffer.append("</DRUG>");
        }
        stringBuffer.append("</DRUGS>");
        stringBuffer.append("</JAVAHIS-DRUG-IO>");
        String path = TConfig.getSystemValue("ATCPATH.O");
        String fileServerIP = TConfig.getSystemValue("FILE_SERVER_IP");
        String port = TConfig.getSystemValue("PORT");
        Timestamp timestamp = SystemTool.getInstance().getDate();
        String timestampStr = timestamp.toString().replaceAll("-","");
        timestampStr = timestampStr.replaceAll(" ","");
        timestampStr = timestampStr.replaceAll(":","");
        timestampStr = timestampStr.substring(0,14);
        TSocket socket = new TSocket(fileServerIP,Integer.parseInt(port));
        return TIOM_FileServer.writeFile(socket,path + getATCNo() + ".xml",stringBuffer.toString().getBytes());
    }

    /**
     * 产生送包药机XML文件(住院)
     * @return boolean
     */
    static public boolean generateATCXMLI(TParm parm){
        TParm  drugListParm = parm.getParm("DRUG_LIST_PARM");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\"  encoding=\"GB2312\"?>");
        stringBuffer.append("<JAVAHIS-DRUG-IO>");
        //患者姓名
        stringBuffer.append("<NAME>");
        stringBuffer.append(parm.getValue("NAME"));
        stringBuffer.append("</NAME>");
        //病案号
        stringBuffer.append("<MRNO>");
        stringBuffer.append(parm.getValue("MRNO"));
        stringBuffer.append("</MRNO>");
        //住院号
        stringBuffer.append("<IPDNO>");
        stringBuffer.append(parm.getValue("IPDNO"));
        stringBuffer.append("</IPDNO>");
        //病床号
        stringBuffer.append("<BED_NO>");
        stringBuffer.append(parm.getValue("BED_NO"));
        stringBuffer.append("</BED_NO>");
        //科室
        stringBuffer.append("<DEPT>");
        stringBuffer.append(parm.getValue("DEPT"));
        stringBuffer.append("</DEPT>");
        //科室名称
        stringBuffer.append("<DEPT_DESC>");
        stringBuffer.append(parm.getValue("DEPT_DESC"));
        stringBuffer.append("</DEPT_DESC>");
        //病区
        stringBuffer.append("<STATION_CODE>");
        stringBuffer.append(parm.getValue("STATION_CODE"));
        stringBuffer.append("</STATION_CODE>");
        //病区名称
        stringBuffer.append("<STATION_DESC>");
        stringBuffer.append(parm.getValue("STATION_DESC"));
        stringBuffer.append("</STATION_DESC>");
        //包药日期
        stringBuffer.append("<DATE>");
        stringBuffer.append(parm.getValue("DATE"));
        stringBuffer.append("</DATE>");
        //门诊住院注记
        stringBuffer.append("<TYPE>");
        stringBuffer.append("2");
        stringBuffer.append("</TYPE>");
        //药品列表
        stringBuffer.append("<DRUGS>");
        for(int i = 0;i < drugListParm.getCount("SEQ");i++){
            stringBuffer.append("<DRUG>");
            //序号
            stringBuffer.append("<SEQ>");
            stringBuffer.append(drugListParm.getValue("SEQ",i));
            stringBuffer.append("</SEQ>");
            //药品代码
            stringBuffer.append("<ORDER_CODE>");
            stringBuffer.append(drugListParm.getValue("ORDER_CODE",i));
            stringBuffer.append("</ORDER_CODE>");
            //药品商品名
            stringBuffer.append("<ORDER_GOODS_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_GOODS_DESC",i));
            stringBuffer.append("</ORDER_GOODS_DESC>");
            //药品化学名
            stringBuffer.append("<ORDER_CHEMICAL_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_CHEMICAL_DESC",i));
            stringBuffer.append("</ORDER_CHEMICAL_DESC>");
            //药品英文名
            stringBuffer.append("<ORDER_ENG_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_ENG_DESC",i));
            stringBuffer.append("</ORDER_ENG_DESC>");
            //剂量
            stringBuffer.append("<QTY>");
            stringBuffer.append(drugListParm.getValue("QTY",i));
            stringBuffer.append("</QTY>");
            //给药频次
            stringBuffer.append("<FREQ>");
            stringBuffer.append(drugListParm.getValue("FREQ",i));
            stringBuffer.append("</FREQ>");
            //投药日期时间
            stringBuffer.append("<DAY>");
            stringBuffer.append(drugListParm.getValue("DAY",i));
            stringBuffer.append("</DAY>");
            //用药时间
            stringBuffer.append("<DRUG_DATETIME>");
            stringBuffer.append(drugListParm.getValue("DRUG_DATETIME",i));
            stringBuffer.append("</DRUG_DATETIME>");
            //用药途径(口服/含服)
            stringBuffer.append("<ROUTE>");
            stringBuffer.append(drugListParm.getValue("ROUTE",i));
            stringBuffer.append("</ROUTE>");
            //饭前饭后(0:无;1:饭前;2:饭后)
            stringBuffer.append("<MEAL_FLG>");
            stringBuffer.append(drugListParm.getValue("MEAL_FLG",i));
            stringBuffer.append("</MEAL_FLG>");
            //首餐时间
            stringBuffer.append("<START_DTTM>");
            stringBuffer.append(drugListParm.getValue("START_DTTM",i));
            stringBuffer.append("</START_DTTM>");
            //餐包注记
            stringBuffer.append("<FLG>");
            stringBuffer.append(drugListParm.getValue("FLG",i));
            stringBuffer.append("</FLG>");
            //长期医嘱与临时医嘱的区分
            stringBuffer.append("<OrderType>");
            stringBuffer.append(drugListParm.getValue("OrderType",i));
            stringBuffer.append("</OrderType>");
            stringBuffer.append("</DRUG>");
        }
        stringBuffer.append("</DRUGS>");
        stringBuffer.append("</JAVAHIS-DRUG-IO>");
        String path = TConfig.getSystemValue("ATCPATH.I");
        String fileServerIP = TConfig.getSystemValue("FILE_SERVER_IP");
        String port = TConfig.getSystemValue("PORT");
        Timestamp timestamp = SystemTool.getInstance().getDate();
        String timestampStr = timestamp.toString().replaceAll("-","");
        timestampStr = timestampStr.replaceAll(" ","");
        timestampStr = timestampStr.replaceAll(":","");
        timestampStr = timestampStr.substring(0,14);
        TSocket socket = new TSocket(fileServerIP,Integer.parseInt(port));
        return TIOM_FileServer.writeFile(socket,path + parm.getValue("STATION_CODE")+"_" + getATCNo() + ".xml",stringBuffer.toString().getBytes());
    }

    public static void main(String[] args) {
        JavaHisDebug.initClient();

        TParm parm = new TParm();
        parm.setData("NAME","测试");
        parm.setData("MRNO","000000000744");
        parm.setData("IPDNO","000000000130");
        parm.setData("DATE",("" + SystemTool.getInstance().getDate()).substring(0,19));
        parm.setData("DEPT","10101");
        parm.setData("DEPT_DESC","呼吸内科");
        parm.setData("PRESCRIPT_NO","0");
        parm.setData("RX_NO","100316000027");
        parm.setData("WINDOW_NO","30");
        TParm drugListParm = new TParm();
            drugListParm.addData("SEQ",0);
            drugListParm.addData("ORDER_CODE","1CCA0001");
            drugListParm.addData("ORDER_GOODS_DESC","阿司匹林肠溶片");
            drugListParm.addData("ORDER_CHEMICAL_DESC","阿司匹林肠溶片化学名");
            drugListParm.addData("ORDER_ENG_DESC","Aspirin");
            drugListParm.addData("QTY",1);
            drugListParm.addData("FREQ","BID");
            drugListParm.addData("SPECIFICATION","阿司匹林肠溶片规格");
            drugListParm.addData("TRANS_RATE","12");
            drugListParm.addData("DAY",1);
            drugListParm.addData("START_DTTM","");
            drugListParm.addData("FLG","");

            drugListParm.addData("SEQ",1);
            drugListParm.addData("ORDER_CODE","1CHB0001");
            drugListParm.addData("ORDER_GOODS_DESC","氟哌啶醇片");
            drugListParm.addData("ORDER_CHEMICAL_DESC","氟哌啶醇片化学名");
            drugListParm.addData("ORDER_ENG_DESC","氟哌啶醇片英文名");
            drugListParm.addData("QTY",1);
            drugListParm.addData("FREQ","BID");
            drugListParm.addData("SPECIFICATION","氟哌啶醇片规格");
            drugListParm.addData("TRANS_RATE","12");
            drugListParm.addData("DAY",1);
            drugListParm.addData("START_DTTM","");
            drugListParm.addData("FLG","");

            drugListParm.addData("SEQ",2);
            drugListParm.addData("ORDER_CODE","2AJ00001");
            drugListParm.addData("ORDER_GOODS_DESC","血宁胶囊");
            drugListParm.addData("ORDER_CHEMICAL_DESC","血宁胶囊化学名");
            drugListParm.addData("ORDER_ENG_DESC","血宁胶囊英文名");
            drugListParm.addData("QTY",1);
            drugListParm.addData("FREQ","BID");
            drugListParm.addData("SPECIFICATION","血宁胶囊规格");
            drugListParm.addData("TRANS_RATE","12");
            drugListParm.addData("DAY",1);
            drugListParm.addData("START_DTTM","");
            drugListParm.addData("FLG","");
        parm.setData("DRUG_LIST_PARM",drugListParm.getData());
        generateATCXMLO(parm);

//        TParm parm = new TParm();
//        parm.setData("NAME","测试");
//        parm.setData("MRNO","000000000744");
//        parm.setData("IPDNO","000000000130");
//        parm.setData("BED_NO","0010102");
//        parm.setData("DEPT","10101");
//        parm.setData("DEPT_DESC","呼吸内科");
//        parm.setData("STATION_CODE","001");
//        parm.setData("STATION_DESC","呼吸科");
//        parm.setData("DATE","20100318092828");
//        TParm drugListParm = new TParm();
//            drugListParm.addData("SEQ",1);
//            drugListParm.addData("ORDER_CODE","1CCA0001");
//            drugListParm.addData("ORDER_GOODS_DESC","阿司匹林肠溶片");
//            drugListParm.addData("ORDER_CHEMICAL_DESC","阿司匹林肠溶片化学名");
//            drugListParm.addData("ORDER_ENG_DESC","Aspirin");
//            drugListParm.addData("QTY",1);
//            drugListParm.addData("FREQ","BID");
//            drugListParm.addData("DAY",1);
//            drugListParm.addData("DRUG_DATETIME","20100319092828");
//            drugListParm.addData("ROUTE","口服");
//            drugListParm.addData("MEAL_FLG","0");
//            drugListParm.addData("START_DTTM","20100319092828");
//            drugListParm.addData("FLG","");
//
//            drugListParm.addData("SEQ",2);
//            drugListParm.addData("ORDER_CODE","1CHB0001");
//            drugListParm.addData("ORDER_GOODS_DESC","氟哌啶醇片");
//            drugListParm.addData("ORDER_CHEMICAL_DESC","氟哌啶醇片化学名");
//            drugListParm.addData("ORDER_ENG_DESC","氟哌啶醇片英文名");
//            drugListParm.addData("QTY",1);
//            drugListParm.addData("FREQ","BID");
//            drugListParm.addData("DAY",1);
//            drugListParm.addData("DRUG_DATETIME","20100319092828");
//            drugListParm.addData("ROUTE","口服");
//            drugListParm.addData("MEAL_FLG","0");
//            drugListParm.addData("START_DTTM","20100319092828");
//            drugListParm.addData("FLG","");
//
//            drugListParm.addData("SEQ",3);
//            drugListParm.addData("ORDER_CODE","2AJ00001");
//            drugListParm.addData("ORDER_GOODS_DESC","血宁胶囊");
//            drugListParm.addData("ORDER_CHEMICAL_DESC","血宁胶囊化学名");
//            drugListParm.addData("ORDER_ENG_DESC","血宁胶囊英文名");
//            drugListParm.addData("QTY",1);
//            drugListParm.addData("FREQ","BID");
//            drugListParm.addData("DAY",1);
//            drugListParm.addData("DRUG_DATETIME","20100319092828");
//            drugListParm.addData("ROUTE","口服");
//            drugListParm.addData("MEAL_FLG","0");
//            drugListParm.addData("START_DTTM","20100319092828");
//            drugListParm.addData("FLG","");
//        parm.setData("DRUG_LIST_PARM",drugListParm.getData());
//        generateATCXMLI(parm);

    }

}
