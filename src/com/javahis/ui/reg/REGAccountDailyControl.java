package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.ui.TTable;
import com.dongyang.manager.TIOM_AppServer;
import jdo.bil.BILSysParmTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.bil.BILAccountTool;
import com.javahis.util.StringUtil;
import java.sql.Timestamp;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;
import jdo.sys.SYSOperatorTool;
import com.dongyang.ui.TTextFormat;

/**
 *
 * <p>Title: 挂号日结控制类</p>
 *
 * <p>Description: 挂号日结控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class REGAccountDailyControl
    extends TControl {
    String accountSeq = "";
    String admType = "O";
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        //table监听单击事件
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        TTable table = (TTable)this.getComponent("Table");
        //table监听checkBox事件
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                               "onTableComponent");
        initPage();
        //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop
        setValue("ADM_TYPE", "O");
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        //初始化院区
        setValue("REGION_CODE", Operator.getRegion());
        //初始化查询起时,迄时
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("S_DATE", yesterday);
        setValue("E_DATE", today);
        String todayTime = StringTool.getString(today, "HH:mm:ss");
        String accountTime = todayTime;
        if (getAccountDate().length() != 0) {
            accountTime = getAccountDate();
            accountTime =getStartTime(accountTime);//=========pangben modify 20110411
        }
        //==========pangben modify 20110411 start 查询条件起始日期设成零点
        String[] s_time=  accountTime.split(":");
        //将数据库查询的起始日期添加一秒方法
        s_time=  startTimeTemp(s_time,s_time.length-1);
        //转换页面显示的格式
        setValue("S_TIME", getStartTime(startTime(s_time)));
        //==========pangben modify 20110411 stop
        setValue("E_TIME", accountTime);
        setValue("CASHIER_CODE", Operator.getID());

        //置日结按钮为灰
        callFunction("UI|unreg|setEnabled", false);
        callFunction("UI|arrive|setEnabled", false);
        //==zhang 20120514 start
        setValue("DEPT", Operator.getDept());
        //==zhang 20120514 end

    }
    /**
     * 开始日期添加一秒的递归方法
     * @param time String[]
     * @param i int
     * @return String[]
     * =========pangben modify 20110411
     */
    public String[] startTimeTemp(String[] time,int i){
        if(i<0)
             return time;//返回得到的数组
        else {
            //判断是否是可以进位的时间数字
            if (Integer.parseInt(time[i]) == 59 ||
                Integer.parseInt(time[i]) == 23) {
                time[i] = "00";
            } else {
                //不能进位将添加一个数
                if((Integer.parseInt(time[i]) + 1)<10)
                    time[i] =  "0"+(Integer.parseInt(time[i]) + 1) ;
                else
                    time[i] =  ""+(Integer.parseInt(time[i]) + 1) ;
                i=-1;//退出递归循环
            }
           return startTimeTemp(time,i-1);
        }
      }
      /**
       * 将数组转换获得开始时间字符串方法
       * @param startTime String[]
       * @return String
       * =======pangben modify 20110411
       */
      public String startTime(String[] startTime){
          String s_time="";
          for(int i=0;i<startTime.length;i++)
              s_time+=startTime[i];
          return s_time;
      }
      /**
       * 获得时间格式的开始日期
       * @param accountTime String
       * @return String
       * ==========pangben modify 20110411
       */
      public String getStartTime(String accountTime){
          return accountTime.substring(0, 2) + ":" +
                accountTime.substring(2, 4) + ":" + accountTime.substring(4, 6);
      }
    /**
     * 查询
     */
    public void onQuery() {
    	//===zhangp 20120418 start
//    	String admType = getValueString("ADM_TYPE");
//    	if(admType.equals("")){
//    		messageBox("请选择门级别");
//    		return;
//    	}
    	//===zhangp 20120418 end
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_DATE")), "yyyyMMdd");
        Timestamp today = SystemTool.getInstance().getDate();
        String todayTime = StringTool.getString(today, "HHmmss");
        String accountTime = todayTime;
        if (getAccountDate().length() != 0) {
            accountTime = getAccountDate();
            accountTime = accountTime.substring(0, 2) +
                accountTime.substring(2, 4) + accountTime.substring(4, 6);
        }
        TParm result = new TParm();
        //===zhangp 20120418 start
//        TParm selAccountData = new TParm();
//        selAccountData.setData("ACCOUNT_TYPE", "REG");
//        if (this.getValueString("CASHIER_CODE") == null ||
//            this.getValueString("CASHIER_CODE").length() == 0) {}
//        else
//            selAccountData.setData("ACCOUNT_USER", this.getValueString("CASHIER_CODE"));
//        selAccountData.setData("S_TIME", startTime + accountTime);
//        selAccountData.setData("E_TIME", endTime + accountTime);
        //======pangben modify 20110620 start
//        if(this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals(""))
//            selAccountData.setData("REGION_CODE",this.getValue("REGION_CODE"));
//         if(this.getValue("ADM_TYPE")!=null&&!this.getValue("ADM_TYPE").equals(""))
//            selAccountData.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
        //======pangben modify 20110620 stop
//        result = BILAccountTool.getInstance().accountQuery(selAccountData);
        //===zhangp 20120514 start
        String sql = 
        	"SELECT 'N' AS FLG,B.REGION_CHN_DESC, A.ACCOUNT_SEQ,TO_CHAR (A.ACCOUNT_DATE, 'YYYY/MM/DD HH24:MI:SS')  AS ACCOUNT_DATE, A.ACCOUNT_USER, A.AR_AMT " +
        	" FROM BIL_ACCOUNT A ,SYS_REGION B ,SYS_OPERATOR_DEPT C" +
        	" WHERE ACCOUNT_DATE BETWEEN TO_DATE ('"+startTime + accountTime+"','YYYYMMDDHH24MISS') " +
        	" AND TO_DATE ('"+endTime + accountTime+"','YYYYMMDDHH24MISS') AND A.REGION_CODE=B.REGION_CODE " +
        	" AND ACCOUNT_TYPE = 'REG' " +
        	" AND A.ACCOUNT_USER = C.USER_ID(+) AND C.MAIN_FLG = 'Y' ";
        if(!this.getValue("ADM_TYPE").equals("")){
        	sql += " AND ADM_TYPE='"+getValue("ADM_TYPE")+"'";
        }
        if(!this.getValue("REGION_CODE").equals("")){
        	sql += " AND A.REGION_CODE='"+getValue("REGION_CODE")+"'";
        }
        if(!this.getValue("CASHIER_CODE").equals("")){
        	sql += " AND A.ACCOUNT_USER='"+getValue("CASHIER_CODE")+"'";
        }
        if(!this.getValue("DEPT").equals("")){
        	sql += " AND C.DEPT_CODE='"+getValue("DEPT")+"'";
        }
        sql += " ORDER BY B.REGION_CHN_DESC,A.ACCOUNT_SEQ ";
        System.out.println(sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        //===zhangp 20120418 end
        if(result.getCount()==0)
            this.messageBox("没有要查询的数据");
        this.callFunction("UI|Table|setParmValue", result);

    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.setValue("SELECT","N");
        setValue("TOGEDER_FLG","N");
        accountSeq = new String();
    }

    /**
     * 打印
     */
    public void onPrint() {
        print();
    }
    
    public void onSave(){
    	String admType = getValueString("ADM_TYPE");
    	if(!admType.equals("")){
    		save(admType);
    	}else{
    		save("O");
    		save("E");
    	}
    }

    /**
     * 日结
     */
    public void save(String admType) {
        TParm parm = new TParm();
        String today = StringTool.getString(TypeTool.getTimestamp(SystemTool.
            getInstance().getDate()), "yyyyMMdd");
        String todayTime = StringTool.getString(SystemTool.getInstance().
                                                getDate(), "HHmmss");
        String accountTime = todayTime;
        if (getAccountDate().length() != 0) {
            accountTime = getAccountDate();
        }
        //调用取号原则
        String accountNo = SystemTool.getInstance().getNo("ALL", "REG",
            "ACCOUNT_SEQ",
            "ACCOUNT_SEQ");
        parm.setData("BILL_DATE", today + accountTime);
        //==============pangben modify 20110620 start
        //如果没选日结人员表示全部一起日结
        //全部门诊日结人员
        TParm casherParm;
        String casherUser=   this.getValueString("CASHIER_CODE");
        if (casherUser == null || casherUser.length() == 0) {
            //得到全部门诊日结人员
            casherParm = getAccountUser(Operator.getRegion());
            //取出人员的个数
            int row = casherParm.getCount();
            //循环所有的收费员
            //======zhangp 20120302 modify start
            List<String> successCashier = new ArrayList<String>();
            List<String>  faileCashier = new ArrayList<String>();
            for (int i = 0; i < row; i++) {
                //取出一个收费人员
                casherUser = casherParm.getValue("USER_ID", i);
                //调用一个人的日结程序
                if (!accountOneCasher(parm,accountNo,casherUser,admType)) {
//                    messageBox("" + casherParm.getValue("USER_NAME", i) +
//                               "日结失败!");
//                    this.messageBox(casherParm.getValue("USER_NAME", i) + "无日结数据!");
                	faileCashier.add(casherParm.getValue("USER_NAME", i));
                    continue;
                }
//                messageBox(casherParm.getValue("USER_NAME", i) + "日结成功!");
                successCashier.add(casherParm.getValue("USER_NAME", i));
            }
            String successCashiers = "";
            String faileCashiers = "";
            if(successCashier.size()>0){
            	for (int i = 0; i < successCashier.size(); i++) {
            		if(i%10==0){
            			successCashiers = successCashiers+"," + successCashier.get(i) + "\n";
            		}else{
            			successCashiers = successCashiers+"," + successCashier.get(i);
            		}
				}
            }
            if(faileCashier.size()>0){
            	for (int i = 0; i < faileCashier.size(); i++) {
            		if(i%10==0){
            			faileCashiers = faileCashiers+"," + faileCashier.get(i) + "\n";
            		}else{
            			faileCashiers = faileCashiers+"," + faileCashier.get(i);
            		}
				}
            }
            if(!faileCashiers.equals("")){
            	messageBox(faileCashiers+"\n无日结数据!");
            }
            if(!successCashiers.equals("")){
            	messageBox(successCashiers+"\n日结成功!");
            }
            //====zhangp 20120302 modify end
            return ;
        }else{
            if (!accountOneCasher(parm,accountNo,casherUser,admType)) {
                    messageBox("无日结数据!");
                    return ;
                }
                messageBox("日结成功!");
            return ;
        }
       //==============pangben modify 20110620 stop
    }

    /**
     * 调用报表打印预览界面
     */
    public void print() {

        String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyy/MM/dd") + " " + this.getValue("S_TIME");
        String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyy/MM/dd") + " " + this.getValue("E_TIME");
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd hh:mm:ss");
        //        TParm printData = this.getPrintReturnTableDate(accountSeq);
        TParm printCancleDate = this.getPrintCancelTableDate(accountSeq);
        TParm printReturnDate = this.getPrintReturnTableDate(accountSeq);
        TParm printChangeDate = this.getChangeTableDate(accountSeq);
        //打印每一个日结数据
        if ("N".equals(getValue("TOGEDER_FLG"))) {
           String [] accoutSeqs= accountSeq.split(",");
            for(int i=0;i<accoutSeqs.length;i++){
             getPrintValue(accoutSeqs[i],eDate,sDate,sysDate,printCancleDate,printReturnDate,printChangeDate);
            }
        } else {
            //将日结数据汇总
           getPrintValue(accountSeq,eDate,sDate,sysDate,printCancleDate,printReturnDate,printChangeDate);
        }


    }
    /**
     * 整理作废表打印数据
     * @param accountSeq String
     * @return TParm
     */
    private TParm getPrintCancelTableDate(String accountSeq) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm parmData = new TParm();
        String selMrNo =
            " SELECT INV_NO,AR_AMT FROM BIL_INVRCP " +
            "  WHERE RECP_TYPE = 'REG' "+
            "    AND ACCOUNT_SEQ IN (" +accountSeq + ") "+
            "    AND CANCEL_FLG = '3' " +
            "    AND CANCEL_DATE < ACCOUNT_DATE";
        selMrNo += " AND LENGTH (INV_NO) < 12";//add by wanglong 20121112 过滤掉12位的建行机器的票据号
        parmData = new TParm(TJDODBTool.getInstance().select(
            selMrNo));
        int count = parmData.getCount("INV_NO");
        TParm aparm = new TParm();
        // 分两列显示算法
        int row = 0;
        int column = 0;
        for (int i = 0; i < count; i++) {

            aparm.addData("INV_NO_" + column,
                          parmData.getData("INV_NO", i));
            aparm.addData("AR_AMT_" + column,
                          df.format(parmData.getDouble("AR_AMT", i)));
            column++;
            if (column == 2) {
                column = 0;
            }
        }
        if(count % 2 == 1){
            row = count / 2 + 1;
            aparm.addData("INV_NO_1", "");
            aparm.addData("AR_AMT_1", "");
        }else
            row = count/2;
        aparm.setCount(row);
        TParm printData = new TParm(); //打印数据
        printData.setCount(row);
        printData = aparm;
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
        return printData;
    }
    /**
     * 整理退费打印数据
     * @param accountSeq String
     * @return TParm
     */
    private TParm getPrintReturnTableDate(String accountSeq) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm parmData = new TParm();
        String selMrNo =
            " SELECT INV_NO,AR_AMT FROM BIL_INVRCP " +
            "  WHERE RECP_TYPE = 'REG' "+
            "    AND ACCOUNT_SEQ IN (" +accountSeq + ") "+
            "    AND CANCEL_FLG = '1' " ;
//            "  AND CANCEL_DATE < ACCOUNT_DATE";
        selMrNo += " AND LENGTH (INV_NO) < 12";//add by wanglong 20121112 过滤掉12位的建行机器的票据号
        parmData = new TParm(TJDODBTool.getInstance().select(
            selMrNo));
        int count = parmData.getCount("INV_NO");
        TParm aparm = new TParm();
        // 分两列显示算法
        int row = 0;
        int column = 0;
        for (int i = 0; i < count; i++) {

            aparm.addData("INV_NO_" + column,
                          parmData.getData("INV_NO", i));
            aparm.addData("AR_AMT_" + column,
                          df.format(parmData.getDouble("AR_AMT", i)));
            column++;
            if (column == 2) {
                column = 0;
            }
        }
        if(count % 2 == 1){
            row = count / 2 + 1;
            aparm.addData("INV_NO_1", "");
            aparm.addData("AR_AMT_1", "");
        }else
            row = count/2;
        aparm.setCount(row);
        TParm printData = new TParm(); //打印数据
        printData.setCount(row);
        printData = aparm;
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
        return printData;
    }
    /**
     * 整理调整票号打印数据
     * @param accountSeq String
     * @return TParm
     */
    private TParm getChangeTableDate(String accountSeq) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm parmData = new TParm();
        String selMrNo =
            " SELECT INV_NO,AR_AMT FROM BIL_INVRCP " +
            "  WHERE RECP_TYPE = 'REG' "+
            "    AND ACCOUNT_SEQ IN (" +accountSeq + ") "+
            "    AND STATUS = '2' ";
        selMrNo += " AND LENGTH (INV_NO) < 12";//add by wanglong 20121112 过滤掉12位的建行机器的票据号
        parmData = new TParm(TJDODBTool.getInstance().select(
            selMrNo));
        int count = parmData.getCount("INV_NO");
        TParm aparm = new TParm();
        // 分两列显示算法
        int row = 0;
        int column = 0;
        for (int i = 0; i < count; i++) {

            aparm.addData("INV_NO_" + column,
                          parmData.getData("INV_NO", i));
            aparm.addData("AR_AMT_" + column,
                          df.format(parmData.getDouble("AR_AMT", i)));
            column++;
            if (column == 2) {
                column = 0;
            }
        }
        if(count % 2 == 1){
            row = count / 2 + 1;
            aparm.addData("INV_NO_1", "");
            aparm.addData("AR_AMT_1", "");
        }else
            row = count/2;
        aparm.setCount(row);
        TParm printData = new TParm(); //打印数据
        printData.setCount(row);
        printData = aparm;
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
        return printData;
    }

    /**
     * 得到结账时间点
     * @return String
     */
    public String getAccountDate() {
        String accountDate = "";
        TParm accountDateParm = new TParm();
        accountDateParm = BILSysParmTool.getInstance().getDayCycle("O");
        accountDate = accountDateParm.getValue("DAY_CYCLE", 0);
        return accountDate;
    }


    /**
     * table监听checkBox事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        accountSeq = new String();
        TTable table = (TTable) obj;
        table.acceptText();
        TParm tableParm = table.getParmValue();
        int allRow = table.getRowCount();
        StringBuffer allSeq = new StringBuffer();
        for (int i = 0; i < allRow; i++) {
            String seq = "";
            if ("Y".equals(tableParm.getValue("FLG", i))) {
                seq = tableParm.getValue("ACCOUNT_SEQ", i);
                if (allSeq.length() > 0)
                    allSeq.append(",");
                allSeq.append(seq);
            }
        }
        accountSeq = allSeq.toString();
        return true;
    }
    /**
    * 全选事件
    * ================pangben modify 20110618
    */
   public void onSelectAll() {
       accountSeq = new String();
       StringBuffer allSeq = new StringBuffer();
       String select = getValueString("SELECT");
       TTable table = (TTable)this.getComponent("Table");
       table.acceptText();
       TParm parm = table.getParmValue();
       int count = parm.getCount();
       for (int i = 0; i < count; i++) {
           String seq = "";
           parm.setData("FLG", i, select);
           seq = parm.getValue("ACCOUNT_SEQ", i);
              if (allSeq.length() > 0)
                  allSeq.append(",");
              allSeq.append(seq);
       }
       accountSeq = allSeq.toString();
       table.setParmValue(parm);
   }
   /**
    * pangben modify 20110620  打印数据可以实现合并功能
    * @param accountSeq String SQL 语句查询
    * @param eDate String 结束时间
    * @param sDate String 开始时间
    * @param sysDate String 系统时间
    * @param printCancleDate TParm
    * @param printReturnDate TParm
    * @param printChangeDate TParm
    */
   public void getPrintValue(String accountSeq,String eDate,String sDate,String sysDate,TParm printCancleDate,TParm printReturnDate,TParm printChangeDate) {
       // 打印数据参数
   TParm selPayTypeFeeY = null;
   TParm selPayTypeFeeT = null;
   //===zhangp 20120423 start
       String selFee_Y =
    	   //===zhangp 20120510 start
//               " SELECT SUM (ABS(REG_FEE)) REG_FEE, SUM (ABS(CLINIC_FEE)) CLINIC_FEE," +
               " SELECT SUM (ABS(REG_FEE_REAL)) REG_FEE, SUM (ABS(CLINIC_FEE_REAL)) CLINIC_FEE," +
               //===zhangp 20120510 end
               "        SUM (ABS(PAY_CASH)) PAY_CASH, SUM (ABS(PAY_BANK_CARD)) PAY_BANK_CARD," +
               "        SUM (ABS(PAY_CHECK)) PAY_CHECK, SUM (ABS(PAY_MEDICAL_CARD)) PAY_MEDICAL_CARD," +
               "        SUM (ABS(PAY_INS_CARD)) PAY_INS_CARD, SUM (ABS(PAY_DEBIT)) PAY_DEBIT," +
               "        SUM (ABS(PAY_INS)) PAY_INS," +
               " SUM(OTHER_FEE1) OTHER_FEE1," +
               " SUM(OTHER_FEE2) OTHER_FEE2," +//add by wanglong 20120911 关于“建行收入”
               " SUM(ALIPAY) ALIPAY," +//add by huangtt 20160607  支付宝
               " SUM (ABS(AR_AMT)) AR_AMT,COUNT(CASE_NO) COUNT " +
               "   FROM BIL_REG_RECP " +
               "  WHERE ACCOUNT_SEQ IN (" + accountSeq + ") " +
//               "    AND RESET_RECEIPT_NO IS NULL " +
               //===zhangp 20120412 start
//               " AND AR_AMT > 0";
       			" AND AR_AMT >= 0";
       		   //===zhangp 20120412 end
       //查询不同支付方式付款金额(日结收费)
//       System.out.println(selFee_Y);
       selPayTypeFeeY = new TParm(TJDODBTool.getInstance().select(
               selFee_Y));
       String selFee_T =
               " SELECT SUM (REG_FEE) REG_FEE, SUM (CLINIC_FEE) CLINIC_FEE," +
               "        SUM (PAY_CASH) PAY_CASH, SUM (PAY_BANK_CARD) PAY_BANK_CARD," +
               "        SUM (PAY_CHECK) PAY_CHECK, SUM (PAY_MEDICAL_CARD) PAY_MEDICAL_CARD," +
               "        SUM (PAY_INS_CARD) PAY_INS_CARD, SUM (PAY_DEBIT) PAY_DEBIT," +
               "        SUM (PAY_INS) PAY_INS, SUM (A.AR_AMT) AR_AMT," +
               "SUM(OTHER_FEE1) OTHER_FEE1," +
               "SUM(OTHER_FEE2) OTHER_FEE2," +//add by wanglong 20120911 关于“建行收入”
               "SUM(ALIPAY) ALIPAY," +//add by huangtt 20160607  支付宝
               "COUNT(CASE_NO) COUNT " +
               "   FROM BIL_REG_RECP A,BIL_INVRCP B " +
               "  WHERE A.PRINT_NO=B.INV_NO " +
               " AND B.RECP_TYPE = 'REG'" +
               " AND A.ACCOUNT_SEQ IN (" + accountSeq + ") " +
               "  AND B.CANCEL_FLG='1'  AND RESET_RECEIPT_NO IS NULL  ";//======pangben 20121121
     //===zhangp 20120423 end
       //查询不同支付方式付款金额(日结退费)
       selPayTypeFeeT = new TParm(TJDODBTool.getInstance().select(
               selFee_T));
       if (selPayTypeFeeT.getCount() <= 0) {
           this.messageBox("请选择要打印的数据");
           return;
       }
       double payCashY = Math.abs(selPayTypeFeeY.getDouble("PAY_CASH", 0));
        double payBankCardY = Math.abs(selPayTypeFeeY.getDouble("PAY_BANK_CARD", 0));
        double payCheckY = Math.abs(selPayTypeFeeY.getDouble("PAY_CHECK", 0));
        double payMedicalCardY = Math.abs(selPayTypeFeeY.getDouble(
                "PAY_MEDICAL_CARD", 0));
        double payInsCardY = Math.abs(selPayTypeFeeY.getDouble("PAY_INS_CARD", 0));
        double payDebitY = Math.abs(selPayTypeFeeY.getDouble("PAY_DEBIT", 0));
        double payInsY = Math.abs(selPayTypeFeeY.getDouble("PAY_INS", 0));
        double arAmtY = Math.abs(selPayTypeFeeY.getDouble("AR_AMT", 0));
        double regFeeY = Math.abs(selPayTypeFeeY.getDouble("REG_FEE", 0));
        double clinicFeeY = Math.abs(selPayTypeFeeY.getDouble("CLINIC_FEE", 0));
        //===zhangp 20120423 start
        double otherFeeY = Math.abs(selPayTypeFeeY.getDouble("OTHER_FEE1", 0));
        double otherFeeY2 = Math.abs(selPayTypeFeeY.getDouble("OTHER_FEE2", 0));//add by wanglong 20120911 关于“建行收入”
        double alipayY = Math.abs(selPayTypeFeeY.getDouble("ALIPAY", 0));//add by huangtt 20160607  支付宝
        //===zhangp 20120423 end
        int countY = TypeTool.getInt(selPayTypeFeeY.getData("COUNT", 0));

        double payCashT = Math.abs(selPayTypeFeeT.getDouble("PAY_CASH", 0));
        double payBankCardT = Math.abs(selPayTypeFeeT.getDouble("PAY_BANK_CARD", 0));
        double payCheckT = Math.abs(selPayTypeFeeT.getDouble("PAY_CHECK", 0));
        double payMedicalCardT = Math.abs(selPayTypeFeeT.getDouble(
                "PAY_MEDICAL_CARD", 0));
        double payInsCardT = Math.abs(selPayTypeFeeT.getDouble("PAY_INS_CARD", 0));
        double payDebitT = Math.abs(selPayTypeFeeT.getDouble("PAY_DEBIT", 0));
        double payInsT = Math.abs(selPayTypeFeeT.getDouble("PAY_INS", 0));
        double arAmtT = Math.abs(selPayTypeFeeT.getDouble("AR_AMT", 0));
        double regFeeT = Math.abs(selPayTypeFeeT.getDouble("REG_FEE", 0));
        double clinicFeeT = Math.abs(selPayTypeFeeT.getDouble("CLINIC_FEE", 0));
        //===zhangp 20120423 start
        double otherFeeT = Math.abs(selPayTypeFeeT.getDouble("OTHER_FEE1", 0));
        double otherFeeT2 = Math.abs(selPayTypeFeeT.getDouble("OTHER_FEE2", 0));//add by wanglong 20120911 关于“建行收入”
        double alipayT = Math.abs(selPayTypeFeeT.getDouble("ALIPAY", 0));//add by huangtt 20160607  支付宝
        //===zhangp 20120423 end
        int countT = TypeTool.getInt(selPayTypeFeeT.getData("COUNT", 0));

        double payCashS = payCashY - payCashT;
        double payBankCardS = payBankCardY - payBankCardT;
        double payCheckS = payCheckY - payCheckT;
        double payMedicalCardS = payMedicalCardY - payMedicalCardT;
        double payInsCardS = payInsCardY - payInsCardT;
        double payDebitS = payDebitY - payDebitT;
        double payInsS = payInsY - payInsT;
        double arAmtS = arAmtY - arAmtT;
        double regFeeS = regFeeY - regFeeT;
        double clinicFeeS = clinicFeeY - clinicFeeT;
        //===zhangp 20120423 start
        double otherFeeS = otherFeeY - otherFeeT;
        double otherFeeS2 = otherFeeY2 - otherFeeT2;//add by wanglong 20120911 关于“建行收入”
        double alipayS = alipayY - alipayT;//add by huangtt 20160607  支付宝
        int countS = countY - countT;
        //===zhangp 20120312 start
//        String nuberToWord = StringUtil.getInstance().numberToWord(payCashS);
        String nuberToWord = StringUtil.getInstance().numberToWord(arAmtS);
        String recp_no = "";
        TParm recParm = getREGparm(accountSeq);
        for (int i = 0; i < recParm.getCount("PRINT_USER"); i++) {
        	recp_no += recParm.getValue("INV_NOS",i) + ";";
		}
        String dateSql = 
        	"SELECT BILL_DATE FROM BIL_REG_RECP WHERE ACCOUNT_SEQ in ("+accountSeq+") ORDER BY BILL_DATE";
        TParm dateparm = new TParm(TJDODBTool.getInstance().select(dateSql));
        String date = ""+dateparm.getData("BILL_DATE", 0).toString().substring(0,19)+" 至 "+dateparm.getData("BILL_DATE", dateparm.getCount()-1).toString().substring(0,19);
        //=====20120217 zhangpeng modify end
        TParm parm = new TParm();
        //========pangben modify 20110328 start
        TTable table = ((TTable)this.getComponent("Table"));
        String region = table.getParmValue().getRow(0).getValue("REGION_CHN_DESC");
        parm.setData("TITLE", "TEXT",
                     (region != null && !region.equals("") ? region :
                      Operator.getHospitalCHNShortName()) + "挂号日结报表");
        //========pangben modify 20110328 stop
        //========20120217 zhangp modify start
//        parm.setData("START_DATE", sDate);
        parm.setData("START_DATE", date);
//        parm.setData("END_DATE", eDate);
        //============20120217 zhangp modify end
        parm.setData("ACCOUNT_SEQ", accountSeq);
        parm.setData("NUMBER_TO_WORD", nuberToWord);
        //收据开始号码
        //============zhangp 20120218 modify start
        parm.setData("START_RECP_NO", recp_no);
        //收据终止号码
//        parm.setData("END_RECP_NO", recp_no);
      //============zhangp 20120218 modify end
        //收款项目
        parm.setData("PAY_CASH_Y", payCashY);
        parm.setData("PAY_BANK_CARD_Y", payBankCardY);
        parm.setData("PAY_CHECK_Y", payCheckY);
        parm.setData("PAY_MEDICAL_CARD_Y", payMedicalCardY);
        parm.setData("PAY_INS_CARD_Y", payInsCardY);
        parm.setData("PAY_DEBIT_Y", payDebitY);
        parm.setData("PAY_INS_Y", "");
        parm.setData("AR_AMT_Y", arAmtY);
        parm.setData("REG_FEE_Y", regFeeY);
        parm.setData("CLINIC_FEE_Y", clinicFeeY);
        //====zhangp 20120423 start
        DecimalFormat df = new DecimalFormat("##########0.00");
        parm.setData("OTHER_FEE_Y", "TEXT",df.format(StringTool.round(otherFeeY,2)));
        parm.setData("OTHER_FEE_Y2",df.format(StringTool.round(otherFeeY2,2)));//add by wanglong 20120911 关于“建行收入”
        parm.setData("ALIPAY_Y",df.format(StringTool.round(alipayY,2)));//add by huangtt 20160607  支付宝
        //====zhangp 20120423 end
        parm.setData("COUNT_Y", countY);
        //退款项目
        parm.setData("PAY_CASH_T", payCashT);
        parm.setData("PAY_BANK_CARD_T", payBankCardT);
        parm.setData("PAY_CHECK_T", payCheckT);
        parm.setData("PAY_MEDICAL_CARD_T", payMedicalCardT);
        parm.setData("PAY_INS_CARD_T", payInsCardT);
        parm.setData("PAY_DEBIT_T", payDebitT);
        parm.setData("PAY_INS_T", "");
        parm.setData("AR_AMT_T", arAmtT);
        parm.setData("REG_FEE_T", regFeeT);
        parm.setData("CLINIC_FEE_T", clinicFeeT);
        //====zhangp 20120423 start
        parm.setData("OTHER_FEE_T", "TEXT",df.format(StringTool.round(otherFeeT,2)));
        parm.setData("OTHER_FEE_T2",df.format(StringTool.round(otherFeeT2,2)));//add by wanglong 20120911 关于“建行收入”
        parm.setData("ALIPAY_T",df.format(StringTool.round(alipayT,2)));//add by huangtt 20160607  支付宝
        //====zhangp 20120423 end
        parm.setData("COUNT_T", countT);
        //实收款项目
        //===zhangp 20120312 start
//        parm.setData("PAY_CASH_S", payCashS);
        parm.setData("PAY_CASH_S", payCashS);
        //===zhangp 20120312 end
        parm.setData("PAY_BANK_CARD_S", payBankCardS);
        parm.setData("PAY_CHECK_S", payCheckS);
        parm.setData("PAY_MEDICAL_CARD_S", payMedicalCardS);
        parm.setData("PAY_INS_CARD_S", payInsCardS);
        parm.setData("PAY_DEBIT_S", payDebitS);
        parm.setData("PAY_INS_S", "");
        parm.setData("AR_AMT_S", arAmtS);
        parm.setData("REG_FEE_S", regFeeS);
        parm.setData("CLINIC_FEE_S", clinicFeeS);
        //====zhangp 20120423 start
        parm.setData("OTHER_FEE_S", "TEXT",df.format(StringTool.round(otherFeeS,2)));
        parm.setData("OTHER_FEE_S2",df.format(StringTool.round(otherFeeS2,2)));//add by wanglong 20120911 关于“建行收入”
        //====zhangp 20120423 end
        parm.setData("ALIPAY_S",df.format(StringTool.round(alipayS,2)));//add by huangtt 20160607  支付宝
        parm.setData("COUNT_S", countS);
        parm.setData("OPT_USER", Operator.getName());
        parm.setData("OPT_DATE", sysDate);
        parm.setData("cancelTable", printCancleDate.getData());
        parm.setData("returnFeeTable", printReturnDate.getData());
        parm.setData("changeTable", printChangeDate.getData());
        //打印报表
        //===zhangp 20120313 start
        //====zhangp 20120324 start
        String sql = 
        	"SELECT " +
        	" A.INS_CROWD_TYPE," +
        	" SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT," +
        	" SUM(A.OTOT_AMT)        AS OTOT_AMT," +
        	" SUM(A.ARMY_AI_AMT)     AS ARMY_AI_AMT," +
        	" SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT," +
        	" SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT," +
        	" SUM(A.SERVANT_AMT)     AS SERVANT_AMT," +
        	" SUM(A.UNREIM_AMT)      AS UNREIM_AMT," +
        	" SUM(A.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT " +
        	" FROM " +
        	" INS_OPD A,BIL_REG_RECP B " +
        	"WHERE " +
        	" A.REGION_CODE = '"+Operator.getRegion()+"' " +
        	" AND A.REGION_CODE  = B.REGION_CODE " +
        	" AND A.CASE_NO = B.CASE_NO " +
        	" AND A.CONFIRM_NO NOT LIKE '*%' " +
        	" AND A.INV_NO = B.PRINT_NO " +
        	" AND B.ACCOUNT_SEQ IN ("+accountSeq+") " +
        	" AND B.AR_AMT>0 " +
        	"GROUP BY  " +
        	" INS_CROWD_TYPE " +
        	"ORDER BY " +
        	" INS_CROWD_TYPE";
        TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
        double payInsNhiS = 0;
        double payInsHelpS = 0;
        double unreimAmtY = 0;
        double unreimAmtT = 0;
        double unreimAmtS = 0;
        payInsCardS = 0;
        payInsS = 0;
        payInsCardT = 0;
        payInsT = 0;
        payInsCardY = 0;
        payInsY = 0;
        double payInsNhiT = 0;
        double payInsHelpT = 0;
		double payInsNhiY = 0;
		double payInsHelpY = 0;
		double payInsTotY = 0;
		double payInsTotT = 0;
		double payInsTotS = 0;
		if(insParm.getCount()>0){
			for (int i = 0; i < insParm.getCount(); i++) {
				if(insParm.getData("INS_CROWD_TYPE", i).equals("1")){
					//城职INS_CROWD_TYPE = ‘1’
					//个人账户=ACCOUNT_PAY_AMT
					//社保基金支付=OTOT_AMT+ ARMY_AI_AMT+TOTAL_AGENT_AMT+FLG_AGENT_AMT+SERVANT_AMT
					payInsCardY = insParm.getDouble("ACCOUNT_PAY_AMT", i);
					payInsNhiY = insParm.getDouble("OTOT_AMT", i) + insParm.getDouble("ARMY_AI_AMT", i) + 
										insParm.getDouble("TOTAL_AGENT_AMT", i) + insParm.getDouble("FLG_AGENT_AMT", i) + 
										insParm.getDouble("SERVANT_AMT", i);
					parm.setData("PAY_INS_CARD_Y", "TEXT", StringTool.round(payInsCardY,2));
					parm.setData("PAY_INS_NHI_Y", "TEXT", StringTool.round(payInsNhiY,2));
				}
				if(insParm.getData("INS_CROWD_TYPE", i).equals("2")){
//					城居INS_CROWD_TYPE = ‘2’
//					救助金额=FLG_AGENT_AMT+ ARMY_AI_AMT+ SERVANT_AMT
//					统筹=TOTAL_AGENT_AMT
					payInsHelpY = insParm.getDouble("FLG_AGENT_AMT", i) + insParm.getDouble("ARMY_AI_AMT", i) + 
										insParm.getDouble("SERVANT_AMT", i) + insParm.getDouble("ILLNESS_SUBSIDY_AMT", i);
					payInsY = insParm.getDouble("TOTAL_AGENT_AMT", i);
					parm.setData("PAY_INS_HELP_Y", "TEXT", StringTool.round(payInsHelpY,2));
					parm.setData("PAY_INS_Y", "TEXT", StringTool.round(payInsY,2));
				}
				unreimAmtY += insParm.getDouble("UNREIM_AMT", i);
			}
		}
        sql = 
        	"SELECT " +
        	" A.INS_CROWD_TYPE," +
        	" SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT," +
        	" SUM(A.OTOT_AMT)        AS OTOT_AMT," +
        	" SUM(A.ARMY_AI_AMT) ARMY_AI_AMT," +
        	" SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT," +
        	" SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT," +
        	" SUM(A.SERVANT_AMT)     AS SERVANT_AMT," +
        	" SUM(A.UNREIM_AMT)      AS UNREIM_AMT," +
        	" SUM(A.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT " +
        	" FROM " +
        	" INS_OPD A,BIL_REG_RECP B " +
        	"WHERE A.REGION_CODE = '"+Operator.getRegion()+"' " +
        	" AND A.REGION_CODE  = B.REGION_CODE " +
        	" AND A.CASE_NO = B.CASE_NO " +
        	" AND A.INV_NO = B.PRINT_NO " +
        	" AND A.CONFIRM_NO  LIKE '*%' " +
        	" AND B.ACCOUNT_SEQ IN ("+accountSeq+") " +
        	" AND B.AR_AMT<0 " +
        	"GROUP BY " +
        	" INS_CROWD_TYPE " +
        	"ORDER BY " +
        	" INS_CROWD_TYPE";
        TParm insParmT = new TParm(TJDODBTool.getInstance().select(sql));
        if(insParmT.getCount()>0){
        	for (int i = 0; i < insParmT.getCount(); i++) {
            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("1")){
            		//城职INS_CROWD_TYPE = ‘1’
            		//个人账户=ACCOUNT_PAY_AMT
            		//社保基金支付=OTOT_AMT+ ARMY_AI_AMT+TOTAL_AGENT_AMT+FLG_AGENT_AMT+SERVANT_AMT
            		payInsCardT = insParmT.getDouble("ACCOUNT_PAY_AMT", i);
            		payInsNhiT = insParmT.getDouble("OTOT_AMT", i) + insParmT.getDouble("ARMY_AI_AMT", i) + 
            		insParmT.getDouble("TOTAL_AGENT_AMT", i) + insParmT.getDouble("FLG_AGENT_AMT", i) + 
            		insParmT.getDouble("SERVANT_AMT", i);
            		parm.setData("PAY_INS_CARD_T", "TEXT", Math.abs(StringTool.round(payInsCardT,2)));
            		parm.setData("PAY_INS_NHI_T", "TEXT", Math.abs(StringTool.round(payInsNhiT,2)));
            	}
            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("2")){
//    				城居INS_CROWD_TYPE = ‘2’
//    				救助金额=FLG_AGENT_AMT+ ARMY_AI_AMT+ SERVANT_AMT
//    				统筹=TOTAL_AGENT_AMT
            		payInsHelpT = insParmT.getDouble("FLG_AGENT_AMT", i) + insParmT.getDouble("ARMY_AI_AMT", i) + 
            		insParmT.getDouble("SERVANT_AMT", i) + insParmT.getDouble("ILLNESS_SUBSIDY_AMT", i);
            		payInsT = insParmT.getDouble("TOTAL_AGENT_AMT", i);
            		parm.setData("PAY_INS_HELP_T", "TEXT", Math.abs(StringTool.round(payInsHelpT,2)));
            		parm.setData("PAY_INS_T", "TEXT", Math.abs(StringTool.round(payInsT,2)));
            	}
            	unreimAmtT += insParmT.getDouble("UNREIM_AMT", i);
            }
        }
        //===zhangp 20120613 start
        unreimAmtS = unreimAmtY + unreimAmtT;
//        unreimAmtS = unreimAmtY - unreimAmtT;
        //===zhangp 20120613 end
        payInsCardS = payInsCardY + payInsCardT ;
        payInsS = payInsY + payInsT;
        payInsHelpS = payInsHelpY + payInsHelpT;
        payInsNhiS = payInsNhiY + payInsNhiT;
//		医保金额小计= 个人账户+社保基金支付+救助金额+统筹-基金未报销金额
        payInsTotY = payInsCardY + payInsY + payInsHelpY + payInsNhiY;
        payInsTotT = payInsCardT + payInsT + payInsHelpT + payInsNhiT;
        payInsTotS = payInsTotY + payInsTotT;
        parm.setData("PAY_INS_NHI_S", "TEXT", StringTool.round(payInsNhiS,2));
		parm.setData("PAY_INS_CARD_S", "TEXT", StringTool.round(payInsCardS,2));
		parm.setData("PAY_INS_HELP_S", "TEXT", StringTool.round(payInsHelpS,2));
        parm.setData("PAY_INS_S", "TEXT", StringTool.round(payInsS,2));
        parm.setData("PAY_UNREIM_AMT_Y", "TEXT", StringTool.round(unreimAmtY,2));
		parm.setData("PAY_UNREIM_AMT_T", "TEXT", Math.abs(StringTool.round(unreimAmtT,2)));
		parm.setData("PAY_UNREIM_AMT_S", "TEXT", StringTool.round(unreimAmtS,2));
		parm.setData("PAY_INS_TOT_Y", "TEXT", StringTool.round(payInsTotY,2));
		parm.setData("PAY_INS_TOT_T", "TEXT", Math.abs(StringTool.round(payInsTotT,2)));
		parm.setData("PAY_INS_TOT_S", "TEXT", StringTool.round(payInsTotS,2));
		
		
        this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGAccountDaily.jhw",
                             parm);

   }
   

   

   /**
    * 得到日结人员组
    * @return String[]
    * ======pangben modify 20110620 添加区域参数
    */
   public TParm getAccountUser(String regionCode) {
       //=============pangben modify 20110620 start
//        if(!"".equals(regionCode)&&null!=regionCode)
//            parm.setData("REGION_CODE",regionCode);
//       TParm accountUser = SYSOperatorTool.getInstance().getCasherCode(parm);
       String region = "";
       if (!"".equals(regionCode) && null != regionCode)
           region = " AND region_code = '" + regionCode + "' ";
       String sql =
               "SELECT user_id AS USER_ID, user_name AS USER_NAME, user_eng_name AS enname, py1, py2" +
               "  FROM sys_operator "
               + " WHERE pos_code IN (SELECT pos_code"
               + " FROM sys_position"
               + " WHERE pos_type = '5') " + region +
               "ORDER BY user_id";
       TParm accountUser = new TParm(TJDODBTool.getInstance().select(sql));
       //=============pangben modify 20110620 stop
       if (accountUser.getErrCode() < 0)
           System.out.println(" 取得收费员 " + accountUser.getErrText());
       return accountUser;
   }
   /**
    * pangben modify 20110620 日结实现所用用户日结方法
    * @param parm TParm
    * @param accountNo String
    * @return boolean
    */
   public boolean  accountOneCasher(TParm parm,String accountNo,String casherUser,String admType){
       parm.setData("ACCOUNT_USER", casherUser);
       parm.setData("ACCOUNT_DATE", SystemTool.getInstance().getDate());
       parm.setData("ACCOUNT_SEQ", accountNo);
       parm.setData("REGION_CODE", Operator.getRegion());
       parm.setData("OPT_USER", Operator.getID());
       parm.setData("OPT_TERM", Operator.getIP());
       parm.setData("CASH_CODE", casherUser);
       parm.setData("ADM_TYPE", admType);
       TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
               "onREGAccount", parm);
       if (result.getErrCode() < 0) {
           err(result.getErrName() + " " + result.getErrText());
           return false;
       } else {
           //日结成功
           return true;
       }
   }
   /**
    * ADM_TYPE监听器
    * =====zhangp 20120306
    */
   public void onAdmTypeClick(){
   	admType = getValueString("ADM_TYPE");
   }
   
	/**
	 * 比较票号
	 * ===========zhangp 20130312
	 * @param inv_no
	 * @param latestInv_no
	 * @return
	 */
	private boolean compareInvno(String inv_no, String latestInv_no) {
		String inv_no_num = inv_no.replaceAll("[^0-9]", "");// 去非数字
		String inv_no_word = inv_no.replaceAll("[0-9]", "");// 去数字
		String latestInv_no_num = latestInv_no.replaceAll("[^0-9]", "");
		String latestInv_no_word = latestInv_no.replaceAll("[0-9]", "");
		if (inv_no_word.equals(latestInv_no_word)
				&& Long.valueOf(inv_no_num)
						- Long.valueOf(latestInv_no_num) == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 取得收费票号
	 * =======zhangp 20130312
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	private TParm getREGparm(String account_seq) {
		String sql = " SELECT   '收费' RECP_TYPE, A.INV_NO, B.USER_NAME PRINT_USER " +
						" FROM BIL_INVRCP A , SYS_OPERATOR B " +
						" WHERE A.ACCOUNT_SEQ IN (" + account_seq + ")" +
						" AND A.PRINT_USER = B.USER_ID " +
						" AND A.RECP_TYPE = 'REG'";
		sql += " AND LENGTH (A.INV_NO) < 12";//add by wanglong 20121112 过滤掉12位的建行机器的票据号
		sql += " ORDER BY A.RECP_TYPE, A.PRINT_USER, A.INV_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() < 0) {
			return result;
		}
		String print_user = result.getValue("PRINT_USER", 0);
		String inv_no = result.getValue("INV_NO", 0);
		TParm regParm = new TParm();
		List<String> reglist = new ArrayList<String>();
		reglist.add(result.getValue("INV_NO", 0));
		int regcount = 0;
		String inv_nos = "";
		for (int i = 1; i < result.getCount(); i++) {
			if (result.getValue("PRINT_USER", i).equals(print_user)) {
				if (!compareInvno(result.getValue("INV_NO", i), inv_no)) {
					inv_nos += reglist.get(0) + "~"
							+ reglist.get(reglist.size() - 1) + ",";
					regcount += reglist.size();
					reglist = new ArrayList<String>();
				}
			} else {
				inv_nos += reglist.get(0) + "~"
						+ reglist.get(reglist.size() - 1) + ",";
				regcount += reglist.size();
				reglist = new ArrayList<String>();
				inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
				regParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", i));
				regParm.addData("PRINT_USER", print_user);
				regParm.addData("INV_NOS", inv_nos);
				regParm.addData("INV_COUNT", regcount);
				inv_nos = "";
				regcount = 0;
			}
			inv_no = result.getValue("INV_NO", i);
			print_user = result.getValue("PRINT_USER", i);
			reglist.add(result.getValue("INV_NO", i));
		}
		if (reglist.size() > 0) {
			inv_nos += reglist.get(0) + "~" + reglist.get(reglist.size() - 1)
					+ ",";
		}
		regcount += reglist.size();
		inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
		regParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", 0));
		regParm.addData("PRINT_USER", print_user);
		regParm.addData("INV_NOS", inv_nos);
		regParm.addData("INV_COUNT", regcount);
		return regParm;
	}
	 /**
     * 医保明细打印
     */
    public void onDetailPrint() {
    TParm printData = new TParm();
     //获得医保明细
     printData = getInsDetailPrint(accountSeq);
     //表头
     printData.setData("TITLE", "TEXT","门诊挂号医保明细表");
    
     //打印日期
     String printDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                          "yyyy-MM-dd HH:mm:ss");
     printData.setData("PRINTDATE","TEXT",printDate);
     
     //收费员
     printData.setData("USER","TEXT",Operator.getName());
     if (printData == null)
         return;
     this.openPrintWindow(
         "%ROOT%\\config\\prt\\opb\\INSDetailPrint.jhw", printData); 
    }
    
    /**
     *得到医保明细
     * @param tableParm TParm
     * @return TParm
     */
    public TParm getInsDetailPrint(String accountSeq) {
   	 TParm returnParm = new TParm();
   	 String sql = 
        	"SELECT " +
        	" A.INS_CROWD_TYPE," +
        	" SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT," +
        	" SUM(A.OTOT_AMT)        AS OTOT_AMT," +
        	" SUM(A.ARMY_AI_AMT)     AS ARMY_AI_AMT," +
        	" SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT," +
        	" SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT," +
        	" SUM(A.SERVANT_AMT)     AS SERVANT_AMT," +
        	" SUM(A.UNREIM_AMT)      AS UNREIM_AMT," +
        	" SUM(A.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT,C.SPECIAL_PAT" +
        	" FROM " +
        	" INS_OPD A,BIL_REG_RECP B,INS_MZ_CONFIRM C " +
        	"WHERE " +
        	" A.REGION_CODE = '"+Operator.getRegion()+"' " +
        	" AND A.REGION_CODE  = B.REGION_CODE " +
        	" AND A.CASE_NO = B.CASE_NO " +
        	" AND A.CONFIRM_NO NOT LIKE '*%' " +
        	" AND A.CASE_NO = C.CASE_NO"+
        	" AND A.CONFIRM_NO =C.CONFIRM_NO"+
        	" AND A.INV_NO = B.PRINT_NO " +
        	" AND B.ACCOUNT_SEQ IN ("+accountSeq+") " +
        	" AND B.AR_AMT>0 " +
        	"GROUP BY  " +
        	" A.INS_CROWD_TYPE,C.SPECIAL_PAT " +
        	"ORDER BY " +
        	" A.INS_CROWD_TYPE,C.SPECIAL_PAT";
//   	  System.out.println("tttttttttttttt======"+sql);	 
        TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        double CZototamtS = 0;
        double CZototamtY = 0;
        double CZototamtT = 0;
        
        double CXototamtS = 0;
        double CXototamtY = 0;
        double CXototamtT = 0;
        
        double totalagentamtS = 0; 
        double totalagentamtY = 0; 
        double totalagentamtT = 0; 
        
        double CZaccountpayamtS = 0;
        double CZaccountpayamtY = 0;
        double CZaccountpayamtT = 0;
        
        double CXaccountpayamtS = 0;
        double CXaccountpayamtY = 0;
        double CXaccountpayamtT = 0;
        
        double illnesssubsidyamtS = 0;
        double illnesssubsidyamtY = 0;
        double illnesssubsidyamtT = 0;
        
        double flgagentamtS = 0;
        double flgagentamtY = 0;
        double flgagentamtT = 0; 
        
        double servantarmyaiamtS = 0;
        double servantarmyaiamtY = 0;
        double servantarmyaiamtT = 0;
        
        double soldierarmyaiamtS = 0;
        double soldierarmyaiamtY = 0;
        double soldierarmyaiamtT = 0;  
        
        double civilarmyaiamtS = 0;
        double civilarmyaiamtY = 0;
        double civilarmyaiamtT = 0;
    
        double specialarmyaiamtS = 0;
        double specialarmyaiamtY = 0;
        double specialarmyaiamtT = 0;
              
        double insallamtS = 0;
           
        double unreimAmtS = 0;
        double unreimAmtY = 0;
        double unreimAmtT = 0;        
		if(insParm.getCount()>0){
			for (int i = 0; i < insParm.getCount(); i++) {
				//公务员补助
				if(insParm.getData("SPECIAL_PAT", i).equals("06"))
				servantarmyaiamtY +=insParm.getDouble("ARMY_AI_AMT", i);
				//军残补助
				else if(insParm.getData("SPECIAL_PAT", i).equals("04"))
				soldierarmyaiamtY +=insParm.getDouble("ARMY_AI_AMT", i);
				//民政补助
				else if(insParm.getData("SPECIAL_PAT", i).equals("07"))
				civilarmyaiamtY += insParm.getDouble("ARMY_AI_AMT", i);
				//民政优抚
				else if(insParm.getData("SPECIAL_PAT", i).equals("08"))
				specialarmyaiamtY += insParm.getDouble("ARMY_AI_AMT", i);

				if(insParm.getData("INS_CROWD_TYPE", i).equals("1")){
				//城职专项基金
				CZototamtY += insParm.getDouble("OTOT_AMT", i);
				//城职个人账户
				CZaccountpayamtY += insParm.getDouble("ACCOUNT_PAY_AMT", i);
				}
				if(insParm.getData("INS_CROWD_TYPE", i).equals("2")){
				//城乡专项基金
				CXototamtY += insParm.getDouble("OTOT_AMT", i);
				//城乡个人账户
				CXaccountpayamtY += insParm.getDouble("ACCOUNT_PAY_AMT", i);
				}
				//统筹支付
				totalagentamtY += insParm.getDouble("TOTAL_AGENT_AMT", i);
				//大额救助
				flgagentamtY += insParm.getDouble("FLG_AGENT_AMT", i);
				//城乡大病
				illnesssubsidyamtY += insParm.getDouble("ILLNESS_SUBSIDY_AMT", i);
				//基金未报销
				unreimAmtY += insParm.getDouble("UNREIM_AMT", i);
			}
		}
		String sql1 = 
        	"SELECT " +
        	" A.INS_CROWD_TYPE," +
        	" SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT," +
        	" SUM(A.OTOT_AMT)        AS OTOT_AMT," +
        	" SUM(A.ARMY_AI_AMT) ARMY_AI_AMT," +
        	" SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT," +
        	" SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT," +
        	" SUM(A.SERVANT_AMT)     AS SERVANT_AMT," +
        	" SUM(A.UNREIM_AMT)      AS UNREIM_AMT," +
        	" SUM(A.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT,C.SPECIAL_PAT " +
        	" FROM " +
        	" INS_OPD A,BIL_REG_RECP B,INS_MZ_CONFIRM C " +
        	"WHERE A.REGION_CODE = '"+Operator.getRegion()+"' " +
        	" AND A.REGION_CODE  = B.REGION_CODE " +
        	" AND A.CASE_NO = B.CASE_NO " +
        	" AND A.INV_NO = B.PRINT_NO " +
        	" AND A.CASE_NO =C.CASE_NO"+
        	" AND SUBSTR(A.CONFIRM_NO,2,LENGTH(A.CONFIRM_NO)) = C.CONFIRM_NO"+  
        	" AND A.CONFIRM_NO  LIKE '*%' " +
        	" AND B.ACCOUNT_SEQ IN ("+accountSeq+") " +
        	" AND B.AR_AMT<0 " +
        	"GROUP BY " +
        	" A.INS_CROWD_TYPE ,C.SPECIAL_PAT " +
        	"ORDER BY " +
        	" A.INS_CROWD_TYPE ,C.SPECIAL_PAT";
//        System.out.println("退==="+sql1);
        TParm insParmT = new TParm(TJDODBTool.getInstance().select(sql1));
        if(insParmT.getCount()>0){
        	for (int i = 0; i < insParmT.getCount(); i++) {      
				//公务员补助
				if(insParmT.getData("SPECIAL_PAT", i).equals("06"))
				servantarmyaiamtT +=insParmT.getDouble("ARMY_AI_AMT", i);
				//军残补助
				else if(insParmT.getData("SPECIAL_PAT", i).equals("04"))
				soldierarmyaiamtT +=insParmT.getDouble("ARMY_AI_AMT", i);
				//民政补助
				else if(insParmT.getData("SPECIAL_PAT", i).equals("07"))
				civilarmyaiamtT += insParmT.getDouble("ARMY_AI_AMT", i);
				//民政优抚
				else if(insParmT.getData("SPECIAL_PAT", i).equals("08"))
				specialarmyaiamtT += insParmT.getDouble("ARMY_AI_AMT", i);
            		
            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("1")){
            	//城职专项基金
				CZototamtT += insParmT.getDouble("OTOT_AMT", i);
				//城职个人账户
				CZaccountpayamtT += insParmT.getDouble("ACCOUNT_PAY_AMT", i);
            	}
            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("2")){
            	//城乡专项基金
    			CXototamtT += insParmT.getDouble("OTOT_AMT", i);
    			//城乡个人账户
    			CXaccountpayamtT += insParmT.getDouble("ACCOUNT_PAY_AMT", i);	
            	}
            	//统筹支付
				totalagentamtT += insParmT.getDouble("TOTAL_AGENT_AMT", i);
				//大额救助
				flgagentamtT += insParmT.getDouble("FLG_AGENT_AMT", i);
				//城乡大病
				illnesssubsidyamtT += insParmT.getDouble("ILLNESS_SUBSIDY_AMT", i);
				//基金未报销
            	unreimAmtT += insParmT.getDouble("UNREIM_AMT", i);
            }
        }
        
        CZototamtS = CZototamtY+CZototamtT;
        CXototamtS = CXototamtY+CXototamtT;
        totalagentamtS= totalagentamtY+totalagentamtT;
        CZaccountpayamtS= CZaccountpayamtY+CZaccountpayamtT;
        CXaccountpayamtS= CXaccountpayamtY+CXaccountpayamtT;
        illnesssubsidyamtS = illnesssubsidyamtY+illnesssubsidyamtT;
        flgagentamtS = flgagentamtY+flgagentamtT;
        servantarmyaiamtS= servantarmyaiamtY+servantarmyaiamtT;
        soldierarmyaiamtS = soldierarmyaiamtY+soldierarmyaiamtT;
        civilarmyaiamtS =civilarmyaiamtY+civilarmyaiamtT;
        specialarmyaiamtS = specialarmyaiamtY+specialarmyaiamtT;

       //基金未报销
        unreimAmtS = unreimAmtY +unreimAmtT;
        //医保合计
        insallamtS = CZototamtS+CXototamtS+totalagentamtS+CZaccountpayamtS+
        CXaccountpayamtS+illnesssubsidyamtS+flgagentamtS+servantarmyaiamtS+
        soldierarmyaiamtS+civilarmyaiamtS+specialarmyaiamtS;
        
        returnParm.setData("CZ_OTOT_AMT", "TEXT", StringTool.round(CZototamtS,2)); 
		returnParm.setData("TOTAL_AGENT_AMT", "TEXT", StringTool.round(totalagentamtS,2));
		returnParm.setData("CZ_ACCOUNT_PAY_AMT", "TEXT", StringTool.round(CZaccountpayamtS,2));
        returnParm.setData("ILLNESS_SUBSIDY_AMT", "TEXT", StringTool.round(illnesssubsidyamtS,2));
        returnParm.setData("FLG_AGENT_AMT", "TEXT", StringTool.round(flgagentamtS,2));
		returnParm.setData("SERVANT_ARMY_AI_AMT", "TEXT", StringTool.round(servantarmyaiamtS,2));
		returnParm.setData("SOLDIER_ARMY_AI_AMT", "TEXT", StringTool.round(soldierarmyaiamtS,2));
		returnParm.setData("CIVIL_ARMY_AI_AMT", "TEXT", StringTool.round(civilarmyaiamtS,2));
		returnParm.setData("SPECIAL_ARMY_AI_AMT", "TEXT", StringTool.round(specialarmyaiamtS,2));
		returnParm.setData("CX_OTOT_AMT", "TEXT", StringTool.round(CXototamtS,2));
		returnParm.setData("CX_ACCOUNT_PAY_AMT", "TEXT", StringTool.round(CXaccountpayamtS,2));
		returnParm.setData("UNREIM_AMT", "TEXT", StringTool.round(unreimAmtS,2));
		returnParm.setData("INS_ALL_AMT", "TEXT", StringTool.round(insallamtS,2));
		//结算日期
		String sqlBil = " SELECT PRINT_NO,BILL_DATE,ACCOUNT_SEQ " +
                        " FROM BIL_REG_RECP WHERE ACCOUNT_SEQ in ("+accountSeq+") " +
                        " ORDER BY BILL_DATE";
       TParm bilParm = new TParm(TJDODBTool.getInstance().select(sqlBil));
       String stardate = bilParm.getData("BILL_DATE", 0).toString();
       String enddate = bilParm.getData("BILL_DATE", bilParm.getCount()-1).toString();
       stardate = stardate.substring(0, 19);
       enddate = enddate.substring(0, 19);
       returnParm.setData("ACCOUNTDATE", "TEXT",stardate+" 至 "+enddate);
   	 return returnParm; 
   }
    /**
     * 第三方打印
     */
    public void onThirdpartPrint() {
    	//打印每一个日结数据
        if ("N".equals(getValue("TOGEDER_FLG"))) {
           String [] accoutSeqs = accountSeq.split(",");
            for(int i = 0;i < accoutSeqs.length;i++){
            	getThirdpartValue(accoutSeqs[i]);
            }
        } else {
            //将日结数据汇总
        	getThirdpartValue(accountSeq);
        }   	
    }
    
    /**
     * 第三方打印数据
     */
    public void getThirdpartValue(String accountSeq){
    	DecimalFormat df = new DecimalFormat("##########0.00");
    	TParm printData = new TParm();
    	String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd hh:mm:ss");
    	//支付宝收款
    	String sql = "SELECT SUM(ALIPAY) ALIPAY"
    			+ " FROM BIL_REG_RECP "
    			+ " WHERE ACCOUNT_SEQ IN (" + accountSeq + ") AND AR_AMT>=0 AND QE_PAY_TYPE = '2'";
    	TParm data1 = new TParm(TJDODBTool.getInstance().select(sql));
    	double alipay = 0.00;
    	if(data1.getCount()>0){
    		for (int i = 0; i < data1.getCount(); i++) { 
    			alipay += data1.getDouble("ALIPAY", i);
    		}
    	}
    	alipay = Math.abs(alipay);
    	printData.setData("ZFB","TEXT",df.format(StringTool.round(alipay,2)));
    	
    	//支付宝退医疗卡
    	String sql1 = "SELECT SUM(ALIPAY) ALIPAY"
    			+ " FROM BIL_REG_RECP A,BIL_INVRCP B"
    			+ " WHERE A.PRINT_NO = B.INV_NO AND B.RECP_TYPE = 'REG' AND"
    			+ " A.ACCOUNT_SEQ IN (" + accountSeq + ")"
    			+ " AND B.CANCEL_FLG = '1' AND A.RESET_RECEIPT_NO IS NULL AND A.QE_PAY_TYPE = '2'";
    	//System.out.println("sql1 = "+sql1);
    	TParm data2 = new TParm(TJDODBTool.getInstance().select(sql1));
    	double alipay_c = 0.00;
    	if(data2.getCount()>0){
    		for (int i = 0; i < data2.getCount(); i++) { 
    			alipay_c += data2.getDouble("ALIPAY", i);
    		}
    	}
    	alipay_c = Math.abs(alipay_c);
    	if(alipay_c > 0){
    		alipay_c = -alipay_c;
    	}
    	else{
    		alipay_c = 0;
    	}
    	printData.setData("ZFB_C","TEXT",df.format(StringTool.round(alipay_c,2)));
    	
    	//微信收款
    	String sql_wx = "SELECT SUM(ALIPAY) ALIPAY"
			+ " FROM BIL_REG_RECP "
			+ " WHERE ACCOUNT_SEQ IN (" + accountSeq + ") AND AR_AMT>=0 AND QE_PAY_TYPE = '6'";
    	TParm data1_wx = new TParm(TJDODBTool.getInstance().select(sql_wx));
    	double wechat = 0.00;
    	if(data1_wx.getCount()>0){
    		for (int i = 0; i < data1_wx.getCount(); i++) { 
    			wechat += data1_wx.getDouble("ALIPAY", i);
    		}
    	}
    	wechat = Math.abs(wechat);
    	printData.setData("WX","TEXT",df.format(StringTool.round(wechat,2)));
    	
    	//微信退医疗卡
    	String sql_wx1 = "SELECT SUM(ALIPAY) ALIPAY"
    			+ " FROM BIL_REG_RECP A,BIL_INVRCP B"
    			+ " WHERE A.PRINT_NO = B.INV_NO AND B.RECP_TYPE = 'REG' AND"
    			+ " A.ACCOUNT_SEQ IN (" + accountSeq + ")"
    			+ " AND B.CANCEL_FLG = '1' AND A.RESET_RECEIPT_NO IS NULL AND A.QE_PAY_TYPE = '6'";
    	TParm data2_wx = new TParm(TJDODBTool.getInstance().select(sql_wx1));
    	double wechat_c = 0.00;
    	if(data2_wx.getCount()>0){
    		for (int i = 0; i < data2_wx.getCount(); i++) { 
    			wechat_c += data2_wx.getDouble("ALIPAY", i);
    		}
    	}
    	wechat_c = Math.abs(wechat_c);
    	if(wechat_c > 0){
    		wechat_c = -wechat_c;
    	}
    	else{
    		wechat_c = 0;
    	}
    	printData.setData("WX_C","TEXT",df.format(StringTool.round(wechat_c,2)));
    	//合计收入
    	printData.setData("PAY_ALL", "TEXT", df.format(StringTool.round((alipay+wechat),2)));
    	//合计退医疗卡
    	printData.setData("PAY_ALL_C", "TEXT", df.format(StringTool.round((alipay_c+wechat_c),2)));	
    	//当前时间
    	printData.setData("PRINTDATE","TEXT",sysDate);
    	
    	//收费日期
    	String sqlBil = " SELECT PRINT_NO,BILL_DATE,ACCOUNT_SEQ " +
        			" FROM BIL_REG_RECP WHERE ACCOUNT_SEQ in ("+accountSeq+") " +
        			" ORDER BY BILL_DATE";
    	TParm bilParm = new TParm(TJDODBTool.getInstance().select(sqlBil));
    	String stardate = bilParm.getData("BILL_DATE", 0).toString();
    	String enddate = bilParm.getData("BILL_DATE", bilParm.getCount()-1).toString();
    	stardate = stardate.substring(0, 19);
    	enddate = enddate.substring(0, 19);
    	printData.setData("ACCOUNTDATE", "TEXT",stardate+" 至 "+enddate);
        
        //收费员
        printData.setData("USER","TEXT",Operator.getName());
        
        //报表号
        printData.setData("ACCOUNTSEQ","TEXT",accountSeq);
        
        
        
        if (printData == null)
            return;
    	this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGThirdpartPrint.jhw", printData); 
    }  
}
