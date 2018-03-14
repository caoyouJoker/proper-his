package jdo.device;

import com.dongyang.config.TConfig;
import com.javahis.util.JavaHisDebug;

public class CallNo {
    /**
     * 主机名
     */
    private String host;
    /**
     * 端口
     */
    private int port;
    /**
     * 路径
     */
    private String path;
    /**
     * 名称空间
     */
    private String namespace;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 正确的返回
     */
    private String success;
    /**
     * 初始化
     */
    public boolean init()
    {
        if("N".equals(TConfig.getSystemValue("ServerIsReady")))
            return false;
        setHost(TConfig.getSystemValue("CallHost"));
        int port = 80;
        try{
            port = Integer.parseInt(TConfig.getSystemValue("CallPort"));
        }catch(Exception e)
        {
            return false;
        }
        setPort(port);
        setPath(TConfig.getSystemValue("CallPath"));
        setNamespace(TConfig.getSystemValue("CallNamespace"));
        return true;
    }

    /**
     * 设置主机名
     * @param host String
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 得到主机名
     * @return String
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置端口
     * @param port String
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 得到端口
     * @return int
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置路径
     * @param path String
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 得到路径
     * @return String
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置名称空间
     * @param namespace String
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * 得到名称空间
     * @return String
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * 设置用户名
     * @param userName String
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 得到用户名
     * @return String
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置密码
     * @param password String
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 得到密码
     * @return String
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置成功
     * @param success String
     */
    public void setSuccess(String success) {
        this.success = success;
    }

    /**
     * 得到成功
     * @return String
     */
    public String getSuccess() {
        return success;
    }
    /**
     * 挂号同步
     * @param VISIT_DATE String 挂号日期 NOT NULL
     * @param VISIT_NO String 挂号号 NOT NULL CASENO
     * @param CLINIC_LABEL String 专家 普通等
     * @param SERIAL_NO String 小号
     * @param PATIENT_ID String 病人卡号
     * @param PNAME String 病人姓名
     * @param PSEX String 性别
     * @param PAGE String 年龄
     * @param IDENTITY String 身份
     * @param DEPTID String 科室ID
     * @param REGISTERING_DATE String 挂号时间（2010-2-8 11:11:11）
     * @param DOCTOR String 医生编号 普通不分为空
     * @param CTYPE String 0 挂号 1 退号  2就诊完成
     * @param OPTYPE String 操作类型 0 清空 1 删除  2 插入或者更新 3 退号
     * @param TIME_DESC String 班次
     * 挂号 CTYPE 0 OPTYPE 2
     * 退号 CTYPE 1 OPTYPE 3
     * @return String 成功返回 "true" 失败返回 "false"
     */
    public String SyncClinicMaster(String VISIT_DATE,
                                   String VISIT_NO,
                                   String CLINIC_LABEL,
                                   String SERIAL_NO,
                                   String PATIENT_ID,
                                   String PNAME,
                                   String PSEX,
                                   String PAGE,
                                   String IDENTITY,
                                   String DEPTID,
                                   String REGISTERING_DATE,
                                   String DOCTOR,
                                   String CTYPE ,
                                   String OPTYPE,
                                   String TIME_DESC)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"VISIT_DATE",VISIT_DATE);
        addXml(xml,"VISIT_NO",VISIT_NO);
        addXml(xml,"CLINIC_LABEL",CLINIC_LABEL);
        addXml(xml,"SERIAL_NO",SERIAL_NO);
        addXml(xml,"PATIENT_ID",PATIENT_ID);
        addXml(xml,"PNAME",PNAME);
        addXml(xml,"PSEX",PSEX);
        addXml(xml,"PAGE",PAGE);
        addXml(xml,"IDENTITY",IDENTITY);
        addXml(xml,"DEPTID",DEPTID);
        addXml(xml,"REGISTERING_DATE",REGISTERING_DATE);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"CTYPE",CTYPE);
        addXml(xml,"OPTYPE",OPTYPE);
        addXml(xml,"TIME_DESC",TIME_DESC);
        String a = TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SyncClinicMaster",xml.toString());
        return a;
    }
    public void addXml(StringBuffer xml,String name,String value)
    {
        xml.append("<");
        xml.append(name);
        xml.append(">");
        xml.append(value);
        xml.append("</");
        xml.append(name);
        xml.append(">");
    }
    /**
     * 叫号同步
     * @param VISIT_DATE String 挂号日期
     * @param VISIT_NO String 挂号号 CASENO
     * @param CLINIC_LABEL String 专家 普通等
     * @param SERIAL_NO String 小号
     * @param PNAME String 病人姓名
     * @param PSEX String 性别
     * @param IDENTITY String 身份
     * @param DOCTOR String 医生姓名 普通不分为空
     * @param COMPUTERIP String 诊室名称(诊室IP)
     * @return String
     */
    public String CallClinicMaster(String VISIT_DATE,
                                   String VISIT_NO,
                                   String CLINIC_LABEL,
                                   String SERIAL_NO,
                                   String PNAME,
                                   String PSEX,
                                   String IDENTITY,
                                   String DOCTOR,
                                   String COMPUTERIP)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"VISIT_DATE",VISIT_DATE);
        addXml(xml,"VISIT_NO",VISIT_NO);
        addXml(xml,"CLINIC_LABEL",CLINIC_LABEL);
        addXml(xml,"SERIAL_NO",SERIAL_NO);
        addXml(xml,"PNAME",PNAME);
        addXml(xml,"PSEX",PSEX);
        addXml(xml,"IDENTITY",IDENTITY);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"COMPUTERIP",COMPUTERIP);
        String a = TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"CallClinicMaster",xml.toString());
        return a;
    }
    /**
     * 取药列表
     * @param VISIT_DATE String  就诊日期           NOT NULL,
     * @param VISIT_NO String 就诊号
     * @param SERIAL_NO String 小号(空)
     * @param PATIENT_ID String 病人卡号 MRNO
     * @param PNAME String 病人姓名
     * @param PSEX String 性别
     * @param IDENTITY String 身份(空)
     * @param DEPTID String 药房名称
     * @param REGISTERING_DATE String 开药时间（2010-2-8 11:11:11）
     * @param DOCTOR String 窗口名称
     * @param CTYPE String 0 配药 1 发药完成
     * @param OPTYPE String 操作类型 2 插入或者更新
     * 医生开药保存后 CTYPE 0
     * 发完药回调 CTYPE 1
     * @return String
     */
    public String SyncDrug(String VISIT_DATE,
                           String VISIT_NO,
                           String SERIAL_NO,
                           String PATIENT_ID,
                           String PNAME,
                           String PSEX,
                           String IDENTITY,
                           String DEPTID,
                           String REGISTERING_DATE,
                           String DOCTOR,
                           String CTYPE,
                           String OPTYPE)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"VISIT_DATE",VISIT_DATE);
        addXml(xml,"VISIT_NO",VISIT_NO);
        addXml(xml,"SERIAL_NO",SERIAL_NO);
        addXml(xml,"PATIENT_ID",PATIENT_ID);
        addXml(xml,"PNAME",PNAME);
        addXml(xml,"PSEX",PSEX);
        addXml(xml,"IDENTITY",IDENTITY);
        addXml(xml,"REGISTERING_DATE",REGISTERING_DATE);
        addXml(xml,"DEPTID",DEPTID);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"CTYPE",CTYPE);
        addXml(xml,"OPTYPE",OPTYPE);
        return TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SyncDrug",xml.toString());
    }
    /**
     * 取药叫号
     * @param COUNTER_NO String 窗口号
     * @param SERIAL_NO String 领药号
     * @param PSEX String 性别, (空)
     * @param DRUGNMAE String 药品名称如下 白加黑~2合~3.2元&止痛片~3科~2元, (空)
     * @param DOCTOR String 药师姓名,(空)
     * @param COMPUTERIP String 本地计算机IP
     * @param ALLPAY String 总价钱(空)
     * @return String
     */
    public String CallDrug(String COUNTER_NO,
                           String SERIAL_NO,
                           String PSEX,
                           String DRUGNMAE,
                           String DOCTOR,
                           String COMPUTERIP,
                           String ALLPAY)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"COUNTER_NO",COUNTER_NO);
        addXml(xml,"SERIAL_NO",SERIAL_NO);
        addXml(xml,"PSEX",PSEX);
        addXml(xml,"DRUGNMAE",DRUGNMAE);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"COMPUTERIP",COMPUTERIP);
        addXml(xml,"ALLPAY",ALLPAY);
        return TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"CallDrug",xml.toString());
    }
    /**
     * Lis同步
     * @param EXAM_ITEM String 检查号 唯一
     * @param PATIENT_ID String 病人卡号
     * @param LISITEM String 检查项目
     * @param PNAME String 病人姓名
     * @param PSEX String 性别
     * @param PAGE String 年龄
     * @param IDENTITY String 急
     * @param REGISTERING_DATE String 挂号时间（2010-2-8 11:11:11）
     * @param DOCTOR String 空
     * @param CTYPE String 0 刷卡   2检查完成
     * @param OPTYPE String 2 插入或者更新
     * @return String
     */
    public String SyncLisMaster(String EXAM_ITEM,
                                String PATIENT_ID,
                                String LISITEM,
                                String PNAME,
                                String PSEX,
                                String PAGE,
                                String IDENTITY,
                                String REGISTERING_DATE,
                                String DOCTOR,
                                String CTYPE,
                                String OPTYPE)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"EXAM_ITEM",EXAM_ITEM);
        addXml(xml,"PATIENT_ID",PATIENT_ID);
        addXml(xml,"LISITEM",LISITEM);
        addXml(xml,"PNAME",PNAME);
        addXml(xml,"PSEX",PSEX);
        addXml(xml,"PAGE",PAGE);
        addXml(xml,"IDENTITY",IDENTITY);
        addXml(xml,"REGISTERING_DATE",REGISTERING_DATE);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"CTYPE",CTYPE);
        addXml(xml,"OPTYPE",OPTYPE);
        return TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SyncLisMaster",xml.toString());
    }

    /**
     * 科室同步  成功返回 "true" 失败返回 "false"
     * @param DEPTID String  科室代码唯一  NOT NULL
     * @param DEPTNAME String  科室名称
     * @param READNAME String 科室读法
     * @param OPTYPE String   操作类型 0 清空 1 删除  2 插入或者更新
     * @return String
     */
    public String SyncDept(String DEPTID,
                           String DEPTNAME,
                           String READNAME,
                           String OPTYPE)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"DEPTID",DEPTID);
        addXml(xml,"DEPTNAME",DEPTNAME);
        addXml(xml,"READNAME",READNAME);
        addXml(xml,"OPTYPE",OPTYPE);
        return TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SyncDept",xml.toString());
    }

    /**
     * 医生同步  成功返回 "true" 失败返回 "false"
     * @param USERID String  医生工号唯一 NOT NULL
     * @param USERNAME String 医生名字
     * @param READNAME String 医生名字读音
     * @param DEPTID String 科室代码
     * @param TITLE String 职称
     * @param OPTYPE String  操作类型 0 清空 1 删除  2 插入或者更新
     * @return String
     */
    public String SyncDoctors(String USERID,
                              String USERNAME,
                              String READNAME,
                              String DEPTID,
                              String TITLE,
                              String OPTYPE)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"USERID",USERID);
        addXml(xml,"USERNAME",USERNAME);
        addXml(xml,"READNAME",READNAME);
        addXml(xml,"DEPTID",DEPTID);
        addXml(xml,"TITLE",TITLE);
        addXml(xml,"OPTYPE",OPTYPE);
        return TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SyncDoctors",xml.toString());
    }

    /**
     * 登陆
     * @param COMPUTERIP String 机器ip
     * @param DOCTOR String 医生工号
     * @param fDate String 时间
     * @param OPTYPE String 1登陆,0退出
     * @return String
     */
    public String SynLogin(String COMPUTERIP,
                           String DOCTOR,
                           String fDate,
                           String OPTYPE)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"COMPUTERIP",COMPUTERIP);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"FDATE",fDate);
        addXml(xml,"OPTYPE",OPTYPE);
        String a = TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SynLogin",xml.toString());
        return a;
    }


    public static void main(String args[])
    {
        JavaHisDebug.initClient();
        CallNo call = new CallNo();
        /*call.setHost("192.168.1.4");
               call.setPort(80);
               call.setPath("/service.asmx");
               call.setNamespace("http://tempuri.org/");
               //call.setUserName("aaa");
               //call.setPassword("bbb");*/
        call.init();
        String s = call.SyncClinicMaster("2010-04-06 14:10:10","20100406001CASE_NO","专家","10","MR_NO","姓名","男","30","身份","科室ID","2010-04-06 14:10:10","医生编号","0","2","上午");
        System.out.println(s);
    }
}
