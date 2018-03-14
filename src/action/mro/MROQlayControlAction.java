package action.mro;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import jdo.mro.MROQlayControlMTool;
import jdo.mro.MROQlayControlDTool;
import jdo.mro.MROChrtvetrecTool;
import com.dongyang.data.TNull;
import java.sql.Timestamp;
import jdo.emr.GetWordValue;
import jdo.sys.MailVO;
import jdo.sys.MailUtil;
import jdo.sys.SYSPublishBoardTool;
import jdo.mro.MRORecordTool;

/**
 * <p>Title: 自动质控</p>
 *
 * <p>Description: 自动质控</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2011.5.3
 * @version 1.0
 */
public class MROQlayControlAction extends TAction {

    /**
     * 查询所有使用自动质控的审核项目
     */
    private String MRO_CHRTVETSTD_CHECK_SQL =
        " SELECT A.METHOD_CODE, A.METHOD_DESC, C.METHOD_TYPE_CODE, "
        + " C.METHOD_TYPE_DESC, B.EXAMINE_CODE, B.TYPE_CODE, B.EXAMINE_DESC, "
        + " B.ENNAME, B.DESCRIPTION, B.SCORE, B.URG_FLG, B.SPCFY_DEPT, "
        + " B.METHOD_PARM, B.CHECK_RANGE, B.CHECK_FLG, B.CHECK_SQL "
        + "FROM MRO_METHOD A, MRO_CHRTVETSTD B, MRO_METHOD_TYPE C "
        + " WHERE A.METHOD_CODE = B.METHOD_CODE "
        + " AND A.METHOD_TYPE_CODE = C.METHOD_TYPE_CODE "
        + " AND B.CHECK_FLG = 'Y' " //筛选自动质控项（有自动质控项目时，此SQL才有结果）
        + " ORDER BY B.EXAMINE_CODE, B.TYPE_CODE ";

    /**
     * 查询对应的病患列表
     */
    private String ADM_INP_PAT_QUERY = "SELECT A.CASE_NO, A.MR_NO, A.IPD_NO, "
                                     + " A.VS_DR_CODE , A.IN_DATE, A.DS_DATE "
                                     + " FROM ADM_INP A, MRO_RECORD B "
                                     + " WHERE A.CASE_NO =  B.CASE_NO "
                                     //+ " AND B.MRO_CHAT_FLG <> '2' "
                                     + " AND # ";

    /**
     * 查询SQL
     */
    private String QUERY_SQL = "SELECT # FROM # WHERE CASE_NO = '#'";

    /**
     * 查询电子病历SQL
     */
    private String QUERY_EMR_SQL =
        "SELECT # FROM EMR_FILE_INDEX WHERE CASE_NO = '#' AND CLASS_CODE LIKE '#%' ";

    /**
     * 查询质控记录主档
     */
    private String MRO_QLAYCONTROLM =
        "SELECT STATUS,CASE_NO,EXAMINE_CODE,QUERYSTATUS FROM MRO_QLAYCONTROLM ";

    /**
     * 查询所有未发的公布栏信息
     * 查询条件是未通过已查状态的数据
     */
    private String QUERY_BOARD_MESSAGE =
        "SELECT B.USER_NAME, B.E_MAIL, A.CASE_NO, A.EXAMINE_CODE, "
        + " A.EXAMINE_DATE, D.MR_NO, D.PAT_NAME, C.EXAMINE_DESC, "
        + " C.SCORE, B.ROLE_ID, B.USER_ID, C.URG_FLG "
        + " FROM MRO_CHRTVETREC A, SYS_OPERATOR B, "
        + " MRO_CHRTVETSTD C, SYS_PATINFO D,MRO_QLAYCONTROLM E "
        + " WHERE A.VS_CODE = B.USER_ID(+) "
        + " AND A.EXAMINE_CODE = C.EXAMINE_CODE "
        + " AND A.EXAMINE_CODE = E.EXAMINE_CODE "
        + " AND A.CASE_NO = E.CASE_NO "
        + " AND A.MR_NO = D.MR_NO "
        + " AND E.STATUS='0' AND E.QUERYSTATUS='1' "
        + " AND A.BOARD_STATUS = 'N'";

    /**
     * 查询所有未发邮件
     * 查询条件是未通过已查状态的数据
     */
    private String QUERY_SEND_MESSAGE =
        "SELECT B.USER_NAME, B.E_MAIL, A.CASE_NO, A.EXAMINE_CODE, "
        + " A.EXAMINE_DATE, D.MR_NO, D.PAT_NAME, C.EXAMINE_DESC, "
        + " C.SCORE, B.ROLE_ID, B.USER_ID, C.URG_FLG "
        + " FROM MRO_CHRTVETREC A, SYS_OPERATOR B, "
        + " MRO_CHRTVETSTD C, SYS_PATINFO D,MRO_QLAYCONTROLM E "
        + " WHERE A.VS_CODE = B.USER_ID(+) "
        + " AND A.EXAMINE_CODE = C.EXAMINE_CODE "
        + " AND A.EXAMINE_CODE = E.EXAMINE_CODE "
        + " AND A.CASE_NO = E.CASE_NO "
        + " AND A.MR_NO = D.MR_NO "
        + " AND E.STATUS='0' AND E.QUERYSTATUS='1' "
        + " AND A.EMAIL_STATUS = 'N'";

    private TParm methodParm;//得到所有的质控方法
    private TParm parmValueM;//病患信息列表查看是否已完成质控
    private StringBuffer personName=new StringBuffer();//已经提交的病患显示病患名称
    private TParm qlayMParm;//MRO_QLAYCONTROLM 表中的所有数据
    private TParm checkParm;//查询是否有启用的电子病历内容缺失检核的checkParm
    
    public MROQlayControlAction() {
        // 1.查询是否有启用的电子病历内容缺失检核
        checkParm = new TParm(TJDODBTool.getInstance().select(MRO_CHRTVETSTD_CHECK_SQL));

    }
    
    /**
     * 自动质控:执行所有的质控方法自动指控统计医生分数
     * @param parm TParm
     * @return TParm
     * ===========pangben 20110801
     */
    public TParm onQlayControlMethod(TParm parm) {
        TConnection conn = getConnection();
        qlayMParm = new TParm(TJDODBTool.getInstance().select(
                MRO_QLAYCONTROLM + " WHERE CASE_NO IN (" + parm.getValue("CASE_NO") + ")"));//MRO_QLAYCONTROLM 表中的所有检核数据
        //查看所有病患信息，判断是否已经完成质控
        TParm parmTEMP = parm.getParm("parmTEMP");
        parmTEMP.setData("CASE_LIST", parm.getValue("CASE_NO"));
        parmValueM = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmTEMP);//在MRO_RECORD中查询病患的TYPERESULT和SUMSCODE
        //根据条件查询在院出院患者信息
        methodParm = new TParm(TJDODBTool.getInstance().select(
                "SELECT METHOD_CODE, METHOD_DESC FROM MRO_METHOD ORDER BY METHOD_CODE, SEQ"));
        TParm result = null;
        for (int i = 0; i < methodParm.getCount(); i++) {//循环每种质控类型
            String method_code = methodParm.getValue("METHOD_CODE", i);
            result = onQlayControlMethodbyCode(method_code, parm, conn);//执行自动指控方法
        }
        if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
            conn.rollback();//add by wanglong 20130819
            conn.close();
            result.setErrText("自动质控错误！"+result.getErrText());
            return result;
        }
        conn.commit();
        conn.close();
        //=================以下为统计每个病患的总分=======================
        //查询所有执行自动质控和人工质控的患者总分数
        conn=getConnection();
        TParm caseList = new TParm();// add by wanglong 20130819
        caseList.setData("CASE_LIST", parm.getValue("CASE_NO"));
        //分组查询当前所有患者的MR_NO,IPD_NO,CASE_NO
        TParm SumScode = MROChrtvetrecTool.getInstance().selectSumScode(caseList);//modify by wanglong 20130819
        //分组查询当前所有患者的总分（如果存在检核记录，将显示分值；不存在则显示总分为100）
        TParm SumScode1 = MROChrtvetrecTool.getInstance().selectSumScode1(caseList);
        TParm updateScode = null;
        for (int i = 0; i < parmValueM.getCount(); i++) {
            //TYPERESULT:Y 已完成的状态不可以修改 N 未完成的状态累计分数修改总分（在病案审核档MRO_CHRTVETREC 表中累计分数）
            if (parmValueM.getValue("TYPERESULT", i).equals("0")) {
                for (int j = 0; j < SumScode.getCount(); j++) {
                    //查找相同的病患
                    if (SumScode.getValue("CASE_NO", j).equals(parmValueM.getValue("CASE_NO", i))
                     && SumScode.getValue("MR_NO", j).equals(parmValueM.getValue("MR_NO", i))) {
                        TParm parmRecore = new TParm();
                        parmRecore.setData("MR_NO", SumScode.getValue("MR_NO", j));
                        parmRecore.setData("CASE_NO", SumScode.getValue("CASE_NO", j));
                        parmRecore.setData("IPD_NO", SumScode.getValue("IPD_NO", j));
                        boolean isDeductScore = false;//是否有扣分
                        // 计算分数查询条件是 已查 未通过的数据累计
                        for (int z = 0; z < SumScode1.getCount(); z++) {
                            if (SumScode.getValue("CASE_NO", j).equals(SumScode1.getValue("CASE_NO", z))
                             && SumScode.getValue("MR_NO", j).equals(SumScode1.getValue("MR_NO", z))) {
                                parmRecore.setData("SUMSCODE", SumScode1.getDouble("SCODE", z));
                                isDeductScore = true;//有扣分
                                break;
                            }
                        }
                        if(!isDeductScore) 
                            parmRecore.setData("SUMSCODE", 100);//没有扣分项，总分为100
                        updateScode = MRORecordTool.getInstance().updateSCODE(parmRecore, conn); //MRO_RECORD中修改总分
                        if (updateScode.getErrCode() < 0 || !"".equals(updateScode.getErrText())) {
                            conn.close();
                            result.setErrText("修改分数时错误！");
                            return result;
                        }
                        break;
                    }
                }
            }
        }
        conn.commit();
        conn.close();
        result.setData("personName",personName);//显示完成质控的病患的病案号
        return result;
    }
    
    /**
     * 自动质控：执行每一个质控方法
     * @param method_code String
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * ===========pangben 20110801
     *
     */
    public TParm onQlayControlMethodbyCode(String method_code,TParm parm,TConnection conn){
        //返回消息
        TParm result = new TParm();
        // 1.取得质控方法的信息
        String sql = "SELECT A.METHOD_TYPE_CODE, B.METHOD_TYPE_DESC, "
                     + " B.METHOD_TYPE_ENG_DESC, B.EMR_CHECK_NULL, B.EMR_CHECK_TIME, "
                     + " B.TABLE_CHECK_NULL, B.TABLE_CHECK_TIME, B.TIME_VALUE "
                     + " FROM MRO_METHOD A, MRO_METHOD_TYPE B "
                     + " WHERE A.METHOD_TYPE_CODE = B.METHOD_TYPE_CODE "
                     + " AND A.METHOD_CODE = '" + method_code + "'";
        TParm methodTypeParm = new TParm(TJDODBTool.getInstance().select(sql));//查询自动质控的类型，总的来说，目前归为4种
        // 2.根据不同方法执行自动质控
        TParm inparm = new TParm();
        inparm.setData("METHOD_CODE", method_code);
        inparm.setData("METHOD_TYPE_CODE", methodTypeParm.getValue("METHOD_TYPE_CODE", 0));
        inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
        inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        // 判断是否为指定病患的自动质控(如果指定择根据指定人员进行指控)
        if (parm.existData("CASE_NO")) {
            inparm.setData("CASE_NO", parm.getValue("CASE_NO"));
        }
        if ("Y".equals(methodTypeParm.getValue("EMR_CHECK_NULL", 0))) {
            inparm.setData("EMR_CHECK_NULL", "Y");
            result = getEmrCheckNull(inparm, conn);// 2.1.电子病历内容缺失检核
        } else if ("Y".equals(methodTypeParm.getValue("TABLE_CHECK_NULL", 0))) {
            inparm.setData("TABLE_CHECK_NULL", "Y");
            result = getTableCheckNull(inparm, conn);// 2.2.数据库表内容缺失检核
        } else if ("Y".equals(methodTypeParm.getValue("EMR_CHECK_TIME", 0))) {
            inparm.setData("EMR_CHECK_TIME", "Y");
            inparm.setData("TIME_VALUE", methodTypeParm.getDouble("TIME_VALUE", 0));
            result = getEmrCheckTime(inparm, conn);// 2.3.电子病历时效检核
        } else if ("Y".equals(methodTypeParm.getValue("TABLE_CHECK_TIME", 0))) {
            inparm.setData("TABLE_CHECK_TIME", "Y");
            inparm.setData("TIME_VALUE", methodTypeParm.getDouble("TIME_VALUE", 0));
            result = getTableCheckTime(inparm, conn);// 2.4.数据库表时效检核
        }
        return result;
    }
    
    /**
     * 电子病历内容缺失检核
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    private TParm getEmrCheckNull(TParm parm, TConnection conn) {
        TParm result = new TParm();
//        // 1.查询是否有启用的电子病历内容缺失检核
//        String sql = MRO_CHRTVETSTD_CHECK_SQL.replaceFirst("#", parm.getValue("METHOD_CODE"));
//        TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
//        if (checkParm == null || checkParm.getCount() < 0) {
//              result.setErrText("没有启用的电子病历内容缺失检核！");
//              return result;
//        }
        String where = "";
      //============以下为自动质控的调用参数===============
        String method_parm = "";// 取得方法参数
        String file_name = "";// 电子病历模板编号
        String tag_name = "";// 字段名称
        String query_condition = null;// 可选条件1（从EMR_FILE_INDEX表中筛选病历文件的where条件，前不必加and）
        String query_condition1 = null;// 可选条件2（字段名称对应的宏名）
        String check_sql = "";// 审核SQL
        
        // 查询数据列表
        String CASE_NO = "";
        String MR_NO = "";
        String EXAMINE_DATE = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
        String STATUS = "0"; //通过状态：0:未通过   1:通过
        for (int i = 0; i < checkParm.getCount(); i++) {
            query_condition = "";
            query_condition1 = "";
            if (checkParm.getValue("METHOD_CODE",
                i).equals(parm.getValue("METHOD_CODE"))) {
                // 2.根据不同的检核项目查询对应的病患列表
                if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                    where = " A.IN_DATE IS NOT NULL AND (A.DS_DATE IS NULL OR A.DS_DATE > SYSDATE) ";
                } else {
                    where = " A.DS_DATE IS NOT NULL AND A.DS_DATE <= SYSDATE ";
                }
                // 审核SQL： 如果不为空，取得其值；如果为空，取的默认值
                String sql = null;
                check_sql = checkParm.getValue("CHECK_SQL", i);//审核SQL
                if ("".equals(check_sql)) {
                    sql = ADM_INP_PAT_QUERY.replaceFirst("#", where);
                } else {
                    sql = check_sql;
                }
                //指定病患的自动质控(如果指定则根据指定人员进行指控)
                if (parm.existData("CASE_NO")) {
                    //由此可以看出，审核SQL必须给表起别名,必须有CASE_NO字段
                    sql += " AND A.CASE_NO IN (" + parm.getValue("CASE_NO") + ")";
                }
                TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));// 查询病患信息
                method_parm = checkParm.getValue("METHOD_PARM", i);//调用参数
                String[] tag = method_parm.split(";");
                try {
                    file_name = tag[0];
                    tag_name = tag[1];
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    TParm errParm = new TParm();
                    errParm.setErr(-1, "自动质控参数设置错误");
                    return errParm;
                }
                if (tag.length > 2) {
                    query_condition = tag[2];
                }
                if (tag.length > 3)
                    query_condition1 = tag[3]; //第四个参数得到宏的名称
                // 3.循环病患列表
                for (int j = 0; j < patParm.getCount(); j++) {
                    //填充数据列表
                    CASE_NO = patParm.getValue("CASE_NO", j);
                    MR_NO = patParm.getValue("MR_NO", j);
                    //已经完成的质控病患不可以在操作=======pangben modify 20110801
                    if (isCreate(MR_NO, CASE_NO)) {
                        if (!personName.toString().contains(MR_NO))
                            personName.append(MR_NO + ",\n");
                        continue;
                    }
                    //审核是否通过公共方法
                    TParm tempStatus = tempStatusShow(CASE_NO, checkParm.getValue("EXAMINE_CODE", i));
                    if (null != tempStatus.getValue("status") &&
                        "1".equals(tempStatus.getValue("status"))) {// 4.2 检核通过
                        continue;
                    }
                    boolean M_FLG = tempStatus.getBoolean("M_FLG");//记录新建还是更新数据（true新建，false更新）
                    // 5.检核电子病历内容缺失
                    sql = QUERY_EMR_SQL.replaceFirst("#", " FILE_PATH , FILE_NAME ")
                                       .replaceFirst("#", CASE_NO)
                                       .replaceFirst("#", file_name);
                    if (!"".equals(query_condition)) {
                        sql = sql + " AND " + query_condition;
                    }
                    TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));//查找电子病历文件的存放信息
                    if (queryParm.getErrCode() < 0) {
                        result.setErrText("查询电子病历文件出错！");
                        return result;
                    }
                    if (queryParm == null || queryParm.getCount("FILE_PATH") <= 0) {
                        STATUS = "0";// 审核未通过
                    } else {
                        TParm tagParm = new TParm();
                        //===============循环便利集合中的数据如果有一个病历不能通过审核将返回不通过
                        for(int index=0;index<queryParm.getCount();index++){
                            tagParm.setData("FILE_PATH", queryParm.getValue("FILE_PATH", index));
                            tagParm.setData("FILE_NAME", queryParm.getValue("FILE_NAME", index));
                            //判断电子病历TAG值是否存在
                            STATUS = getEMRTag(tagParm, tag_name, query_condition1); //===pangben modify 20110901 添加第三个参数
                            if("0".equals(STATUS)){
                                break;
                            }
                        }
                    }
                    //插入自动质控检核信息与扣分信息（分别插入MRO_QLAYCONTROLM表，之后插入MRO_CHRTVETREC表）
                    result = onInsertQlayTParm(patParm.getRow(j), checkParm.getRow(i),parm,
                                               EXAMINE_DATE,STATUS, M_FLG,result, conn);
                }
            }
        }
        return result;
    }

    /**
     * 数据库表内容缺失检核
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    private TParm getTableCheckNull(TParm parm, TConnection conn) {
        TParm result = new TParm();
//        // 1.查询是否有启用的数据库表内容缺失检核
//        String sql = MRO_CHRTVETSTD_CHECK_SQL.replaceFirst("#", parm.getValue("METHOD_CODE"));
//        TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
//        if (checkParm == null || checkParm.getCount() < 0) {
//            result.setErrText("没有启用的数据库表内容缺失检核！");
//            return result;
//        }
        String where = "";
        //============以下为自动质控的调用参数===============
        String method_parm = "";// 取得方法参数
        String table_name = "";// 表名称
        String tag_name = "";// 字段名称
        String query_condition = "";// 可选条件（筛选字段的where条件，前不必加and）
        String check_sql = "";// 审核SQL
        
        // 查询数据列表
        String CASE_NO = "";
        String MR_NO = "";
        String EXAMINE_DATE = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
        String STATUS = "0";
        for (int i = 0; i < checkParm.getCount(); i++) {
            // 2.根据不同的检核项目查询对应的病患列表
            query_condition = "";
            if (checkParm.getValue("METHOD_CODE",
                i).equals(parm.getValue("METHOD_CODE"))) {
                if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                    where = " A.IN_DATE IS NOT NULL AND (A.DS_DATE IS NULL OR A.DS_DATE > SYSDATE) ";
                } else {
                    where = " A.DS_DATE IS NOT NULL AND A.DS_DATE <= SYSDATE ";
                }
                // 审核SQL： 如果不为空，取得其值；如果为空，取的默认值
                String sql = null;
                check_sql = checkParm.getValue("CHECK_SQL", i);//审核SQL
                if ("".equals(check_sql)) {
                    sql = ADM_INP_PAT_QUERY.replaceFirst("#", where);
                } else {
                    sql = check_sql;
                }
                //指定病患的自动质控(如果指定择根据指定人员进行指控)
                if (parm.existData("CASE_NO")) {
                    sql += " AND A.CASE_NO IN (" + parm.getValue("CASE_NO") + ")";
                }
                TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
                method_parm = checkParm.getValue("METHOD_PARM", i);//调用参数
                String[] tag = method_parm.split(";");
                try {
                    table_name = tag[0];
                    tag_name = tag[1];
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    TParm errParm = new TParm();
                    errParm.setErr(-1, "自动质控参数设置错误");
                    return errParm;
                }
                if (tag.length > 2) {
                    query_condition = tag[2];
                }
                // 3.循环病患列表
                for (int j = 0; j < patParm.getCount(); j++) {
                    //填充数据列表
                    CASE_NO = patParm.getValue("CASE_NO", j);
                    MR_NO = patParm.getValue("MR_NO", j);
                    //已经完成的质控病患不可以在操作=======pangben modify 20110801
                    if (isCreate(MR_NO, CASE_NO)) {
                        if (!personName.toString().contains(MR_NO)) personName.append(MR_NO + ",\n");
                        continue;
                    }
                    //审核是否通过公共方法
                    TParm tempStatus = tempStatusShow(CASE_NO, checkParm.getValue("EXAMINE_CODE", i));
                    if (null != tempStatus.getValue("status") &&
                        "1".equals(tempStatus.getValue("status"))) {// 4.2 检核通过
                        continue;
                    }
                    boolean M_FLG = tempStatus.getBoolean("M_FLG");//记录新建还是更新数据（true新建，false更新）
                    // 5.检核数据库表内容缺失
                    sql = QUERY_SQL.replaceFirst("#", tag_name)
                                   .replaceFirst("#", table_name)
                                   .replaceFirst("#", CASE_NO);
                    if (!"".equals(query_condition)) {
                        sql = sql + " AND " + query_condition;
                    }
                    TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));
                    if (queryParm.getErrCode() < 0) {
                        result.setErrText("查询语句错误！");
                        return result;
                    }
                    if (queryParm == null || queryParm.getCount(tag_name) <= 0) {
                        STATUS = "0";// 审核未通过
                    } else if ("".equals(queryParm.getValue(tag_name, 0))) {
                        STATUS = "0";// 审核未通过
                    } else {
                        STATUS = "1";// 审核通过
                    }
                    //插入自动质控检核信息与扣分信息（分别插入MRO_QLAYCONTROLM表，之后插入MRO_CHRTVETREC表）
                    result = onInsertQlayTParm(patParm.getRow(j), checkParm.getRow(i),parm, 
                                               EXAMINE_DATE,STATUS, M_FLG,result, conn);
                }
            }
        }
        return result;
    }

    /**
     * 电子病历时效检核
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    private TParm getEmrCheckTime(TParm parm, TConnection conn) {
        TParm result = new TParm();
//        // 1.查询是否有启用的电子病历时效检核
//        String sql = MRO_CHRTVETSTD_CHECK_SQL.replaceFirst("#", parm.getValue("METHOD_CODE"));
//        TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
//        if (checkParm == null || checkParm.getCount() < 0) {
//            result.setErrText("没有启用的电子病历时效检核！");
//            return result;
//        }

        String where = "";
      //============以下为自动质控的调用参数===============
        double TIME_VALUE = parm.getDouble("TIME_VALUE");// 时效
        String method_parm = "";// 取得方法参数
        String file_name = "";// 电子病历模板编号
        String tag_name = "";// 字段名称
        String query_condition = "";//可选条件1（从EMR_FILE_INDEX表中筛选病历文件的where条件，前不必加and）
        String query_condition1="";//可选条件2（此项目前可任意填值。没有此项时，时效之内算质控成功）
        String check_sql = "";// 审核SQL
        
        // 查询数据列表
        String CASE_NO = "";
        String MR_NO = "";
        String EXAMINE_DATE = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
        String STATUS = "0";
        String IN_DS_DATE = "";// 在院时间还是出院时间
        // 当前日期(通过计算后的时间：减去方法控制的时间)
        Timestamp date = StringTool.rollDate(SystemTool.getInstance().getDate(), (long) ( -1 * TIME_VALUE / 24));
        String SYS_DATE = StringTool.getString(date, "yyyyMMdd");
        for (int i = 0; i < checkParm.getCount(); i++) {
            query_condition = "";
            query_condition1 = "";
            if (checkParm.getValue("METHOD_CODE",
                i).equals(parm.getValue("METHOD_CODE"))) {
                // 2.根据不同的检核项目查询对应的病患列表
                if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                    where = " A.IN_DATE IS NOT NULL AND (A.DS_DATE IS NULL OR A.DS_DATE > SYSDATE) ";
                    IN_DS_DATE = "IN_DATE";
                } else {
                    where = " A.DS_DATE IS NOT NULL AND A.DS_DATE <= SYSDATE ";
                    IN_DS_DATE = "DS_DATE";
                }
                // 审核SQL： 如果不为空，取得其值；如果为空，取的默认值
                String sql = null;
                check_sql = checkParm.getValue("CHECK_SQL", i);//审核SQL
                if ("".equals(check_sql)) {
                    sql = ADM_INP_PAT_QUERY.replaceFirst("#", where);
                } else {
                    sql = check_sql;
                    if (check_sql.indexOf("<START_DATE>") > 0) {
                        sql = sql.replaceFirst("<START_DATE>", SYS_DATE + "000000");
                    }
                    IN_DS_DATE = "CHECK_DATE";
                }
                //指定病患的自动质控(如果指定择根据指定人员进行指控)
                if (parm.existData("CASE_NO")) {
                    sql += " AND A.CASE_NO IN (" + parm.getValue("CASE_NO") + ")";
                }
                TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
                method_parm = checkParm.getValue("METHOD_PARM", i);//调用参数
                String[] tag = method_parm.split(";");
                try {
                    file_name = tag[0];
                    tag_name = tag[1];
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    TParm errParm = new TParm();
                    errParm.setErr(-1, "自动质控参数设置错误");
                    return errParm;
                }
                if (tag.length > 2) {
                    query_condition = tag[2];
                }
                if (tag.length > 3) {
                    query_condition1 = tag[3]; //用来控制比较的时间
                }
                // 3.循环病患列表
                for (int j = 0; j < patParm.getCount(); j++) {
                    
                    //填充数据列表
                    CASE_NO = patParm.getValue("CASE_NO", j);
                    MR_NO = patParm.getValue("MR_NO", j);
                    //已经完成的质控病患不可以在操作
                    if (isCreate(MR_NO, CASE_NO)) {
                        if (!personName.toString().contains(MR_NO))
                            personName.append(MR_NO + ",\n");
                        continue;
                    }
                    //审核是否通过公共方法
                    TParm tempStatus = tempStatusShow(CASE_NO, checkParm.getValue("EXAMINE_CODE", i));
                    if (null != tempStatus.getValue("status") &&
                        "1".equals(tempStatus.getValue("status"))) {// 4.2 检核通过
                        continue;
                    }
                    boolean M_FLG = tempStatus.getBoolean("M_FLG");//记录新建还是更新数据（true新建，false更新）
                    // 5.检核电子病历时效
                    sql = QUERY_EMR_SQL.replaceFirst("#", tag_name).
                          replaceFirst("#", CASE_NO).
                          replaceFirst("#", file_name);
                    if (!"".equals(query_condition)) {
                        sql = sql + " AND " + query_condition;
                    }
                    TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));
                    if (queryParm.getErrCode() < 0) {
                        result.setErrText("查询语句错误！");
                        return result;
                    }
                    if (queryParm == null || queryParm.getCount(tag_name) <= 0) {
                        //病人住院的时长小于时效，这段期间内要检查的项目可能还没做，这种情况下不应该扣分
                        if (M_FLG == true) {// add by wanglong 20130909
                            if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                                Timestamp offset = rollHour(patParm.getTimestamp("IN_DATE", j), TIME_VALUE);
                                Timestamp now = SystemTool.getInstance().getDate();
                                if (offset.getTime() > now.getTime()) {
                                    continue;
                                }
                            } else {
                                Timestamp offset = rollHour(patParm.getTimestamp("DS_DATE", j), TIME_VALUE);
                                Timestamp now = SystemTool.getInstance().getDate();
                                if (offset.getTime() > now.getTime()) {
                                    continue;
                                }
                            }
                        }
                        STATUS = "0";// 审核未通过
                    } else if ("".equals(queryParm.getValue(tag_name, 0))) {
                        STATUS = "0";// 审核未通过
                    } else {
                        for (int index = 0; index < queryParm.getCount(); index++) {
                            Timestamp START_DATE = patParm.getTimestamp(IN_DS_DATE, j);// 开始时间
                            Timestamp END_DATE = queryParm.getTimestamp(tag_name, index);// 结束时间
                            START_DATE = rollHour(START_DATE, TIME_VALUE);// 计算新的开始时间
                            //===============pangben 20110829 添加代码 查询条件第四个参数 ：前一天查找的操作
                            if (!"".equals(query_condition1.trim())) {
                                if (START_DATE.getTime() <= END_DATE.getTime()) {
                                    STATUS = "1";// 审核通过
                                } else {
                                    STATUS = "0";// 审核未通过
                                }
                                //===============pangben 20110829 stop
                            } else if ("".equals(query_condition1.trim())) {
                                if (START_DATE.getTime() >= END_DATE.getTime()) {
                                    STATUS = "1";// 审核通过
                                } else {
                                    STATUS = "0";// 审核未通过
                                }
                            }
                            if("1".equals(STATUS)) break;//如果通过就结束
                        }
                    }
                    //插入自动质控检核信息与扣分信息（分别插入MRO_QLAYCONTROLM表，之后插入MRO_CHRTVETREC表）
                    result = onInsertQlayTParm(patParm.getRow(j), checkParm.getRow(i),parm,
                                               EXAMINE_DATE,STATUS, M_FLG,result, conn);
                }
            }
        }
        return result;
    }
    

    
    /**
     * 数据库表时效检核
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    private TParm getTableCheckTime(TParm parm, TConnection conn) {
        TParm result = new TParm();
//        // 1.查询是否有启用的数据库表时效检核
//        String sql = MRO_CHRTVETSTD_CHECK_SQL.replaceFirst("#", parm.getValue("METHOD_CODE"));
//        TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
//        if (checkParm == null || checkParm.getCount() < 0) {
//            result.setErrText("没有启用的数据库表时效检核！");
//            return result;
//        }

        String where = "";
        //============以下为自动质控的调用参数===============
        double TIME_VALUE = parm.getDouble("TIME_VALUE");// 时效
        String method_parm = "";// 取得方法参数
        String table_name = "";// 表名称
        String tag_name = "";// 字段名称
        String query_condition = "";// 可选条件（筛选字段的where条件，前不必加and）
        String query_condition1="";// 可选条件2（此项目前可任意填值。没有此项时，时效之内算质控成功）
        String check_sql = "";// 审核SQL
        
        // 查询数据列表
        String CASE_NO = "";
        String MR_NO = "";
        String EXAMINE_DATE = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
        String STATUS = "0";
        String IN_DS_DATE = "";// 在院时间还是出院时间
        // 当前日期(通过计算后的时间：减去方法控制的时间)
        Timestamp date = StringTool.rollDate(SystemTool.getInstance().getDate(), (long) ( -1 * TIME_VALUE / 24));
        String SYS_DATE = StringTool.getString(date, "yyyyMMdd");
        for (int i = 0; i < checkParm.getCount(); i++) {
            query_condition = "";
            query_condition1 = "";
            if (checkParm.getValue("METHOD_CODE",
                i).equals(parm.getValue("METHOD_CODE"))) {
                // 2.根据不同的检核项目查询对应的病患列表
                if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                    where = " A.IN_DATE IS NOT NULL AND (A.DS_DATE IS NULL OR A.DS_DATE > SYSDATE) ";
                    IN_DS_DATE = "IN_DATE";
                } else {
                    where = " A.DS_DATE IS NOT NULL AND A.DS_DATE <= SYSDATE ";
                    IN_DS_DATE = "DS_DATE";
                }
                // 审核SQL： 如果不为空，取得其值；如果为空，取的默认值
                check_sql = checkParm.getValue("CHECK_SQL", i);//审核SQL
                String sql = null;
                if ("".equals(check_sql)) {
                    sql = ADM_INP_PAT_QUERY.replaceFirst("#", where);
                } else {
                    sql = check_sql;
                    if (check_sql.indexOf("<START_DATE>") > 0) {
                        sql = sql.replaceFirst("<START_DATE>", SYS_DATE + "000000");
                    }
                    IN_DS_DATE = "CHECK_DATE";
                }
                //指定病患的自动质控(如果指定择根据指定人员进行指控)
                if (parm.existData("CASE_NO")) {
                    sql += " AND A.CASE_NO IN (" + parm.getValue("CASE_NO") + ")";
                }
                TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
                method_parm = checkParm.getValue("METHOD_PARM", i);//调用参数
                String[] tag = method_parm.split(";");
                try {
                    table_name = tag[0];
                    tag_name = tag[1];
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    TParm errParm = new TParm();
                    errParm.setErr(-1, "自动质控参数设置错误");
                    return errParm;
                }
                if (tag.length > 2) {
                    query_condition = tag[2];
                }
                if (tag.length > 3) {
                    query_condition1 = tag[3]; //用来控制比较的时间
                }
                // 3.循环病患列表
                for (int j = 0; j < patParm.getCount(); j++) {
                    //填充数据列表
                    CASE_NO = patParm.getValue("CASE_NO", j);
                    MR_NO = patParm.getValue("MR_NO", j);
                    //已经完成的质控病患不可以在操作=======pangben modify 20110801
                    if (isCreate(MR_NO, CASE_NO)) {
                        if (!personName.toString().contains(MR_NO))
                            personName.append(MR_NO + ",\n");
                        continue;
                    }
                    //审核是否通过公共方法
                    TParm tempStatus = tempStatusShow(CASE_NO, checkParm.getValue("EXAMINE_CODE", i));
                    if (null != tempStatus.getValue("status") &&
                        "1".equals(tempStatus.getValue("status"))) {// 4.2 检核通过
                        continue;
                    }
                    boolean M_FLG = tempStatus.getBoolean("M_FLG");//记录新建还是更新数据（true新建，false更新）
                    // 5.检核数据库表时效
                    sql = QUERY_SQL.replaceFirst("#", tag_name)
                                   .replaceFirst("#", table_name)
                                   .replaceFirst("#", CASE_NO);
                    if (!"".equals(query_condition)) {
                        sql = sql + " AND " + query_condition;
                    }
                    TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));
                    if (queryParm.getErrCode() < 0) {
                        result.setErrText("查询语句错误！");
                        return result;
                    }
                    if (queryParm == null || queryParm.getCount(tag_name) <= 0) {
                        //病人住院的时长小于时效，这段期间内要检查的项目可能还没做，这种情况下不应该扣分
                        if (M_FLG == true) {// add by wanglong 20130909
                            if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                                Timestamp offset = rollHour(patParm.getTimestamp("IN_DATE", j), TIME_VALUE);
                                Timestamp now = SystemTool.getInstance().getDate();
                                if (offset.getTime() > now.getTime()) {
                                    continue;
                                }
                            } else {
                                Timestamp offset = rollHour(patParm.getTimestamp("DS_DATE", j), TIME_VALUE);
                                Timestamp now = SystemTool.getInstance().getDate();
                                if (offset.getTime() > now.getTime()) {
                                    continue;
                                }
                            }
                        }
                        STATUS = "0";// 审核未通过
                    } else if ("".equals(queryParm.getValue(tag_name, 0))) {
                        STATUS = "0";// 审核未通过
                    } else {
                        //便利调用参数的sql语句返回的集合
                        for (int index = 0; index < queryParm.getCount(); index++) {
                            Timestamp START_DATE = patParm.getTimestamp(IN_DS_DATE, j);// 开始时间
                            Timestamp END_DATE = queryParm.getTimestamp(tag_name, index);// 结束时间
                            START_DATE = rollHour(START_DATE, TIME_VALUE);// 计算新的开始时间
                            //===============pangben 20110829 添加代码 查询条件第四个参数 ：前一天查找的操作
                            if (query_condition1.trim().length() > 0) {
                                if (START_DATE.getTime() <= END_DATE.getTime()) {
                                    STATUS = "1";// 审核通过
                                } else {
                                    STATUS = "0";// 审核未通过
                                }
                                //===============pangben 20110829 stop
                            } else {
                                if (START_DATE.getTime() >= END_DATE.getTime()) {
                                    STATUS = "1";// 审核通过
                                } else {
                                    STATUS = "0";// 审核未通过
                                }
                            }
                            if("1".equals(STATUS)){
                                break; //如果通过就结束操作
                            }
                        }
                    }
                    //插入自动质控检核信息与扣分信息（分别插入MRO_QLAYCONTROLM表，之后插入MRO_CHRTVETREC表）
                    result = onInsertQlayTParm(patParm.getRow(j), checkParm.getRow(i),parm,
                                               EXAMINE_DATE,STATUS, M_FLG,result, conn);
                }
            }
        }
        return result;
    }

    /**
     * 判断此病患是否已经提交质控
     * @param mr_no String
     * @param case_no String
     * @return boolean
     */
    private boolean isCreate(String mr_no,String case_no){// 查看MRO_RECORD表的TYPERESULT，看是否已完成
        boolean isTrue=false;
        for (int i = 0; i < parmValueM.getCount(); i++) {
            if (mr_no.equals(parmValueM.getValue("MR_NO", i)) &&
                case_no.equals(parmValueM.getValue("CASE_NO", i))) {
                isTrue="1".equals(parmValueM.getValue("TYPERESULT", i)) ? true : false;
                break;
            }
        }
        return isTrue;
    }
    
    /**
     * 发送公布栏
     * @param parm TParm
     * @return TParm
     */
    public TParm onBoardMessage(TParm parm) {
        TParm result = new TParm();
        TParm caseParm = parm.getParm("CASE_NO");
        String caseSql = MROQlayControlMTool.getInstance().getInStatement("A.CASE_NO", caseParm);//add by wanglong 20130813
        // 1.查询未发送的公布栏信息
        TParm sendMessage = new TParm(TJDODBTool.getInstance().select(QUERY_BOARD_MESSAGE + " AND (" + caseSql + ")"));//modify by wanglong 20130819
        if (sendMessage == null || sendMessage.getCount() <= 0) {
            result.setErrText("没有未发送的消息！");
        }
        // 2.发送公布栏信息并且更新公布栏状态
        TParm resultMessage = sendBoard(sendMessage);
        String message = resultMessage.getValue("MESSAGE");
        TConnection conn = getConnection();
        for (int i = 0; i < resultMessage.getCount("CASE_NO"); i++) {
            TParm inparm = new TParm();
            inparm.setData("MESSAGE_NO", getMessageNo());
            inparm.setData("POST_TYPE", "P");
            inparm.setData("POST_GROUP", resultMessage.getValue("ROLE_ID", i));
            inparm.setData("USER_ID", resultMessage.getValue("USER_ID", i));
            inparm.setData("READ_FLG", "N");
            inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
            inparm.setData("OPT_DATE", SystemTool.getInstance().getDate());
            inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            inparm.setData("POST_SUBJECT", resultMessage.getValue("TITLE", i));
            inparm.setData("URG_FLG", resultMessage.getValue("URG_FLG", i));
            inparm.setData("POST_INFOMATION", resultMessage.getValue("BODY", i));
            inparm.setData("RESPONSE_NO", 0);
            inparm.setData("POST_ID", parm.getValue("OPT_USER"));
            inparm.setData("POST_TIME", SystemTool.getInstance().getDate());
            inparm.setData("CASE_NO", resultMessage.getValue("CASE_NO", i));
            inparm.setData("EXAMINE_CODE", resultMessage.getValue("EXAMINE_CODE", i));
            inparm.setData("EXAMINE_DATE", resultMessage.getValue("EXAMINE_DATE", i));
            inparm.setData("BOARD_STATUS", "Y");
            result = SYSPublishBoardTool.getInstance().insertPostRCV(inparm, conn);
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                return result;
            }
            result = SYSPublishBoardTool.getInstance().insertBoard(inparm, conn);
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                return result;
            }
            result = MROChrtvetrecTool.getInstance().updateBoard(inparm, conn);
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        result.setData("MESSAGE", message);
        return result;
    }

    /**
     * 发送邮件
     * @param parm TParm
     * @return TParm
     */
    public TParm onSendMessage(TParm parm) {
        TParm result = new TParm();
        TParm caseParm = parm.getParm("CASE_NO");
        String caseSql = MROQlayControlMTool.getInstance().getInStatement("A.CASE_NO", caseParm);//add by wanglong 20130813
        // 1.查询未发送的邮件
        TParm sendMessage = new TParm(TJDODBTool.getInstance().select(QUERY_SEND_MESSAGE + " AND (" + caseSql + ")"));//modify by wanglong 20130819
        if (sendMessage == null || sendMessage.getCount() <= 0) {
            result.setErrText("没有未发送的邮件！");
        }
        // 1.发送邮件
        TParm resultMessage = sendEMail(sendMessage);
        String message = resultMessage.getValue("MESSAGE");
        // 2.更新邮件状态
        TConnection conn = getConnection();
        for (int i = 0; i < resultMessage.getCount("CASE_NO"); i++) {
            TParm inparm = new TParm();
            inparm.setData("CASE_NO", resultMessage.getValue("CASE_NO", i));
            inparm.setData("EXAMINE_CODE", resultMessage.getValue("EXAMINE_CODE", i));
            inparm.setData("EXAMINE_DATE", resultMessage.getValue("EXAMINE_DATE", i));
            inparm.setData("EMAIL_STATUS", "Y");
            inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
            inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            result = MROChrtvetrecTool.getInstance().updateEMail(inparm, conn);
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        result.setData("MESSAGE", message);
        return result;
    }

    /**
     * 进行审核记录
     * @param parmM TParm
     * @param parmC TParm
     * @param M_FLG boolean
     * @param STATUS String
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsertQlay(TParm parmM, TParm parmC, boolean M_FLG,
                              String STATUS, TConnection conn) {
        TParm result = new TParm();
        // 6.1 记录质控记录主档
        if (M_FLG) {
            result = MROQlayControlMTool.getInstance().onInsert(parmM, conn);// 新增质控记录主档
        } else {
            result = MROQlayControlMTool.getInstance().onUpdate(parmM, conn);// 更新质控记录主档
        }
        if (result.getErrCode() < 0) {
            result.setErrText("质控记录主档记录错误！");
            return result;
        }

//      6.2 记录质控记录明细
//      TParm parmD = parmM;

//      result = MROQlayControlDTool.getInstance().onInsert(parmD, conn);
//      if (result.getErrCode() < 0) {
//          result.setErrText("质控记录明细记录错误！");
//          return result;
//      }
//      6.3 记录病案审核档:审核不通过并且未扣过分的记录扣分
        TParm inParm = new TParm();
        inParm.setData("CASE_NO", parmC.getValue("CASE_NO"));
        inParm.setData("EXAMINE_CODE", parmC.getValue("EXAMINE_CODE"));
        TParm queryParm = MROChrtvetrecTool.getInstance().selectChrData(inParm);//在扣分表中查询扣分情况
        if (queryParm == null || queryParm.getCount("CASE_NO") <= 0) {
            STATUS = "0";//审核未通过
        }
        else {
            STATUS = "1";//审核通过
        }
        if (!"1".equals(STATUS)) {
            result = MROChrtvetrecTool.getInstance().insertdata(parmC, conn);
            if (result.getErrCode() < 0) {
                result.setErrText("记录病案审核档记录错误！");
                return result;
            }
        }
        return result;
    }

    /**
     * 日期偏移小时
     * @param t Timestamp
     * @param hour double
     * @return Timestamp
     */
    public Timestamp rollHour(Timestamp t, double hour) {
        return new Timestamp(t.getTime() + (long) hour * 60 * 60 * 1000);
    }

    /**
     * 判断电子病历TAG值是否存在
     * @param parm TParm
     * @param tag String 名称
     * @param great String 宏名
     * @return String
     */
    public String getEMRTag(TParm parm, String tag,String macroName) {
        TParm result = new TParm();
        result = GetWordValue.getInstance().getWordValueByName(parm, tag, macroName);
        //System.out.println("RESULT::::"+result);
        if (result == null || result.getErrCode() < 0) {
            return "0";
        }
        //===========pangben modify 20110710 start
        int count =0;
        //如果macroName 宏名称存在数据将通过宏名称查找数据
        if(null!=macroName && macroName.trim().length()>0)
            count=result.getCount(macroName);
        else
            count = result.getCount(tag);
        //System.out.println("count::::::"+count);
        if( count <= 0)
            return "0";
        for (int i = 0; i < count; i++) {
            if (null != macroName && macroName.trim().length() > 0){
                if (result.getValue(macroName + "_VALUE", i) == null ||
                    result.getValue(macroName + "_VALUE", i).length() == 0) {
                    return "0";
                }
            } else{
                if (result.getValue(tag + "_VALUE", i) == null ||
                    result.getValue(tag + "_VALUE", i).length() == 0) {
                    return "0";
                }
            }
        }
        //===========pangben modify 20110710 stop
        return "1";
    }

    /**
     * 发送公布栏信息
     * @param parm TParm
     * @return TParm
     */
    public TParm sendBoard(TParm parm) {
        String message = "";
        boolean flg = true;
        TParm result = new TParm();
        String title = "";
        String body = "";
        String dr_name = "";
        for (int i = 0; i < parm.getCount("E_MAIL"); i++) {
            title = "自动质控检查";
            dr_name = parm.getValue("USER_NAME", i);
            body = dr_name + "医生， 病案号：" + parm.getValue("MR_NO", i) + "(" +
                parm.getValue("PAT_NAME", i) + ") " +
                parm.getValue("EXAMINE_DESC", i) + "， 扣" +
                parm.getDouble("SCORE", i) + "分";
            result.addData("CASE_NO", parm.getValue("CASE_NO", i));
            result.addData("EXAMINE_CODE", parm.getValue("EXAMINE_CODE", i));
            result.addData("EXAMINE_DATE", parm.getValue("EXAMINE_DATE", i));
            result.addData("ROLE_ID", parm.getValue("ROLE_ID", i));
            result.addData("USER_ID", parm.getValue("USER_ID", i));
            result.addData("TITLE", title);
            result.addData("BODY", body);
            result.addData("URG_FLG", parm.getValue("URG_FLG", i));
            message += dr_name + (flg ? "公布栏信息发送成功" : "公布栏信息发送失败") + "\n";
        }
        result.setData("MESSAGE", message);
        return result;
    }


    /**
     * 创建邮件并且发送
     * @param parm TParm
     * @return TParm
     */
    public TParm sendEMail(TParm parm) {
        String message = "";
        boolean flg = true;
        TParm result = new TParm();
        String e_mail = "";
        String title = "";
        String body = "";
        String dr_name = "";
        for (int i = 0; i < parm.getCount("E_MAIL"); i++) {
            e_mail = parm.getValue("E_MAIL", i);
            title = "自动质控检查";
            dr_name = parm.getValue("USER_NAME", i);
            body = dr_name + "医生， 病案号：" + parm.getValue("MR_NO", i) + "(" +
                parm.getValue("PAT_NAME", i) + ") " +
                parm.getValue("EXAMINE_DESC", i) + "， 扣" +
                parm.getDouble("SCORE", i) + "分";

            flg = sendEMail(e_mail, title, body);
            if (flg) {
                result.addData("CASE_NO", parm.getValue("CASE_NO", i));
                result.addData("EXAMINE_CODE", parm.getValue("EXAMINE_CODE", i));
                result.addData("EXAMINE_DATE", parm.getValue("EXAMINE_DATE", i));
                result.addData("ROLE_ID", parm.getValue("ROLE_ID", i));
                result.addData("USER_ID", parm.getValue("USER_ID", i));
                result.addData("TITLE", title);
                result.addData("BODY", body);
                result.addData("URG_FLG", parm.getValue("URG_FLG", i));
            }
            message += dr_name + (flg ? "邮件发送成功" : "邮件发送失败") + "\n";
        }

        result.setData("MESSAGE", message);
        return result;
    }

    /**
     * 创建邮件并且发送
     * @param email_address String
     * @param title String
     * @param body String
     * @return TParm
     */
    public boolean sendEMail(String email_address, String title, String body) {
        MailVO mail = new MailVO();
        mail.getToAddress().add(email_address);
        mail.setSubject(title);
        mail.setContent(body);
        TParm MailSendResult = MailUtil.getInstance().sendMail(mail);
        //System.out.println("MailSendResult----"+MailSendResult);
        if (MailSendResult.getErrCode() < 0) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 取得流水号
     * @return String
     */
    private synchronized String getMessageNo() {
        String messageNo = "";
        messageNo = SystemTool.getInstance().getNo("ALL", "PUB", "MESSAGE_NO", "MESSAGE_NO");
        return messageNo;
    }

    /**
     * 审核是否通过公共方法
     * @return boolean
     */
    private TParm tempStatusShow(String CASE_NO, String EXAMINE_CODE) {
        // 4.查询判断该检核是否已经通过: 通过则continue;没通过则进行检核
        TParm parm = new TParm();
        String status = null;
        String queryStatus=null;//已查未查状态
        // 判断主项是否进行新增
        boolean M_FLG = true;
        if (null==qlayMParm || qlayMParm.getCount("STATUS") <= 0) {
            M_FLG = true;// 4.1 第一次进行检核
        } else {
            //遍历MRO_QLAYCONTROLM 表中数据是否存在status值
            for (int i = 0; i < qlayMParm.getCount(); i++) {
                if (CASE_NO.equals(qlayMParm.getValue("CASE_NO", i)) &&
                    EXAMINE_CODE.equals(qlayMParm.getValue("EXAMINE_CODE", i))) {
                    status = qlayMParm.getValue("STATUS", i);
                    queryStatus=qlayMParm.getValue("QUERYSTATUS", i);
                    break;
                }
            }
            if ((queryStatus == null) && (status == null)) {
                M_FLG = true;// 首次进行检核  modify by wanglong 20121029
            } else
            //不执行操作条件：1.已经通过的项目 2.未检查的项目
           // if ((null!=status && "1".equals(status)) || (null!=queryStatus && "1".equals(queryStatus))) {
            if ((null != status && "1".equals(status)) ) {//modify by wanglong 20121029
                parm.setData("status", "1");// 4.2 检核通过
            } else {
                M_FLG = false;// 4.3 检核没通过再一次进行检核
            }
        }
        parm.setData("M_FLG",M_FLG);
        return parm;
    }
    
    /**
     * 执行自动指控方法的参数
     * @param patParm TParm
     * @param parm TParm 主档parm
     * @param tmpParm TParm 共用parm
     * @param EXAMINE_DATE String 时间
     * @param STATUS String 是否扣分状态
     * @param M_FLG Boolean 审查状态
     * @param result TParm 返回值
     * @param conn TConnection
     * @return TParm
     */
    private TParm onInsertQlayTParm(TParm patParm,TParm parm,TParm tmpParm,
                                    String EXAMINE_DATE, String STATUS,
                                    Boolean M_FLG, TParm result,
                                    TConnection conn) {

        // 6.进行审核记录
        TParm parmM = new TParm();
        parmM.setData("CASE_NO", patParm.getValue("CASE_NO"));
        parmM.setData("EXAMINE_CODE", parm.getValue("EXAMINE_CODE"));
        parmM.setData("EXAMINE_DATE", EXAMINE_DATE);
        parmM.setData("MR_NO", patParm.getValue("MR_NO"));
        parmM.setData("IPD_NO",patParm.getValue("IPD_NO") );
        parmM.setData("STATUS", STATUS);//是否通过检核
        parmM.setData("QUERYSTATUS", "1"); //=============pangben modify 20110801 当前状态：1.已查、0.未查
        parmM.setData("CHECK_RANGE", parm.getValue("CHECK_RANGE"));
        parmM.setData("CHECK_USER",tmpParm.getValue("OPT_USER"));
        parmM.setData("CHECK_DATE", "");
        parmM.setData("OPT_USER", tmpParm.getValue("OPT_USER"));
        parmM.setData("OPT_DATE", "");
        parmM.setData("OPT_TERM", tmpParm.getValue("OPT_TERM"));
        TParm parmC = new TParm();
        parmC.setData("CASE_NO", patParm.getValue("CASE_NO"));
        parmC.setData("EXAMINE_CODE",parm.getValue("EXAMINE_CODE"));
        parmC.setData("EXAMINE_DATE", EXAMINE_DATE.substring(0, 8));
        parmC.setData("IPD_NO", patParm.getValue("IPD_NO"));
        parmC.setData("MR_NO", patParm.getValue("MR_NO"));
        parmC.setData("VS_CODE",patParm.getValue("VS_DR_CODE") );
        parmC.setData("DEDUCT_SCORE", parm.getDouble("SCORE"));
        parmC.setData("DEDUCT_NOTE", "");
        parmC.setData("URG_FLG",null==parm.getValue("URG_FLG")?"N":parm.getValue("URG_FLG") );
        parmC.setData("REPLY_DTTM", new TNull(Timestamp.class));
        parmC.setData("REPLY_DR_CODE", "");
        parmC.setData("REPLY_REMK", "");
        parmC.setData("OPT_USER",tmpParm.getValue("OPT_USER"));
        parmC.setData("OPT_DATE", "");
        parmC.setData("OPT_TERM",tmpParm.getValue("OPT_TERM"));
        parmC.setData("DEDUCT_SCORECOUNT", 1); //========pangben modify 20110801  添加每项个数
        // 进行审核记录
        result = onInsertQlay(parmM, parmC, M_FLG, STATUS, conn);
        if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
            result.setErrText("添加数据错误！");
            conn.rollback();//add by wanglong 20130819
            conn.close();
            return result;
        }
        conn.commit();//add by wanglong 20130819
        return result;
    }
    
    /**
     * 更改病案的质控得分和完成状态为“已完成”
     * @param parm TParm（传入分数与完成状态）
     * @return TParm
     */
    public TParm saveScoreAndState(TParm parm) {//add by wanglong 20121129
        TConnection conn = getConnection();
        TParm result = new TParm();
        TParm parms = new TParm();
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            parms = parm.getRow(i);
            result = MRORecordTool.getInstance().updateTYPERESULT(parms, conn);
            conn.commit();
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    
    /**
     * 更新质控分数
     * @param parm
     * @return
     */
    public TParm updateScore(TParm parm) {//add by wanglong 20130819
        TConnection conn = getConnection();
        TParm result = new TParm();
        TParm parms = new TParm();
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            parms = parm.getRow(i);
            TParm SumScode1 = MROChrtvetrecTool.getInstance().selectSumScode1(parms);//查询分数
            if (SumScode1.getErrCode() < 0) {
                err("ERR:" + SumScode1.getErrCode() + SumScode1.getErrText()
                    + SumScode1.getErrName());
                conn.close();
                return SumScode1;
            }
            TParm parmRecore= parm.getRow(i);
            parmRecore.setData("SUMSCODE", SumScode1.getDouble("SCODE", 0));
            result = MRORecordTool.getInstance().updateSCODE(parmRecore, conn); //MRO_RECORD中修改总分
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                conn.close();
                return result;
            }
            conn.commit();
        }
        conn.commit();
        conn.close();
        return result;
    }
}
