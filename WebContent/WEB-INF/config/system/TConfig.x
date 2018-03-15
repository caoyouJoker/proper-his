FileServer.Main.IP=127.0.0.1
FileServer.Main.Root=D:\JavaHisFile

//         PIC Config
PatInfPIC.LocalPath=C:\JavaHis\Temp
PatInfPIC.ServerPath=PatPic

//         DrugPic Config
PHAInfoPic.LocalPath=C:\JavaHis\Temp
PHAInfoPic.ServerPath=DrugPic

DBTableModifyLog.Root=DBTableModifyLog
DBTableLocalLog.Root=C:\JavaHis\DBTableLocalLogs

RootServer.IP=127.0.0.1

EmrTemplet=EmrFileData\EmrTemplet
EmrData=EmrFileData\EmrData
EmrLocalTempFileName=模版文件

UDD_DISBATCH_LocalPath=C:\JavaHis\logs

//         JavaCTool Config
JavaCTool.JavaHome=C:\Program Files (x86)\Java\jdk1.6.0_10
JavaCTool.SourcePath=C:\Tomcat 8.0\webapps\web\common\src
JavaCTool.OutPath=C:\Tomcat 8.0\webapps\web\common\classes
JavaCTool.ClassPath=C:\Tomcat 8.0\webapps\web\common\lib\T40-api.jar;C:\Tomcat 8.0\webapps\web\common\classes

//         JavaHis Word
JavaHisWord.localTempPath=C:\JavaHis\JavaHisWord

//         Locale Table Columns
Locale.Version=20140901_1
Locale.SYS_FEE.Columns=ORDER_CODE,ORDER_DESC,PY1,PY2,TRADE_ENG_DESC,GOODS_DESC,GOODS_PYCODE,ALIAS_DESC,ALIAS_PYCODE,NHI_FEE_DESC,ORDER_CAT1_CODE,CAT1_TYPE,SPECIFICATION,ORDERSET_FLG,OWN_PRICE,CHARGE_HOSP_CODE,UNIT_CODE,CTRL_FLG,OWN_PRICE2,OWN_PRICE3,ACTIVE_FLG,MAN_CODE,REGION_CODE,SEQ
Locale.SYS_DIAGNOSIS.Columns=ICD_CODE,ICD_CHN_DESC,ICD_ENG_DESC,PY1,PY2,DESCRIPTION,ICD_TYPE,STA1_CODE,STA1_DESC
//药房扣库计费点
//DOSAGE:配药；DISPENSE:发药
CHARGE_POINT=DOSAGE
//住院药房流程是否有审核流程
IS_CHECK=Y
//住院药房流程是否有配药流程
IS_DOSAGE=Y
//病区配药出错后循环次数
CYCLE_TIMES_FOR_ERR=3

//         调用门诊
//         主诉现病史
ODOEmrTempletZSCLASSCODE=EMR020001
ODOEmrTempletZSSUBCLASSCODE=EMR02000103
ODOEmrTempletZSPath=JHW\门（急）诊病历\门(急)诊病历
ODOEmrTempletZSFileName=主诉现病史
//ODOEmrTempletZSCLASSCODE=02
//ODOEmrTempletZSSUBCLASSCODE=0205
//ODOEmrTempletZSPath=主诉现病史
//ODOEmrTempletZSFileName=主诉现病史

//ODOEmrTempletZSFileName=Sub、Obj、Phy Template

//         家族史
ODOEmrTempletFAMILYHISTORY_CLASSCODE=18
ODOEmrTempletFAMILYHISTORY_SUBCLASSCODE=1801
ODOEmrTempletFAMILYHISTORY_Path=家族史
ODOEmrTempletFAMILYHISTORY_FileName=门诊家族史


//住院证
ADMEmrINHOSPPATH=JHW\门（急）诊病历\门(急)诊病历
ADMEmrINHOSPFILENAME=住院证
ADMEmrINHOSPSUBCLASSCODE=EMR02000109
ADMEmrINHOSPCLASSCODE=EMR020001

//门急诊护士站调用结构化病历
ONWEmrMRCODE=EMR02000104
//手术记录调用结构化病历
OPEEmrMRCODE=EMR05010214
//检验报告
LABIP=172.20.109.241

//检查报告
RISIP=172.20.40.50

//检查报告新IP
RISIPNEW=172.20.40.40:8080

//血糖报告
TNBIP=172.20.1.24

//        JavaHis Language
Language=zh,中文;en,English


//        EKT Config
ekt.switch=true
ekt.opd.EKTDialogSwitch=true
ekt.port=COM1

//二代身份证图片保存路径
sid.path=C:\\IdCard

//住院包药机XML文件存放路径
ATCPATH.I=C:\JavaHis\ATC_I\
//门诊包药机XML文件存放路径
ATCPATH.O=C:\JavaHis\ATC_O\
//门诊包药机文件服务器IP
FILE_SERVER_IP=172.20.10.49

//门诊包药机文件服务器端口号
PORT=8103

//门诊包药机服务器数据库
ACTDBO.Type=oracle
ACTDBO.Address=172.20.107.87
ACTDBO.Port=1521
ACTDBO.DBName=ORCL
ACTDBO.UserName=fds_user
ACTDBO.Password=yuyama

//住院包药机服务器数据库
ACTDBI.Type=oracle
ACTDBI.Address=172.20.107.88
ACTDBI.Port=1521
ACTDBI.DBName=ORCL
ACTDBI.UserName=fds_user
ACTDBI.Password=yuyama


//叫号接口
//CallHost=127.0.0.1
//CallPort=80
//CallPath=/fzservice/service.asmx
//CallNamespace=http://tempuri.org/
//ServerIsReady=Y
IsCallNO=N


//条码接口
//门诊
CallLabHostO=127.0.0.1
//健康体检
CallLabHostH=127.0.0.1
//急诊
CallLabHostE=127.0.0.1
//端口号
CallLabPort=9100
//是否启用
ServerLabIsReady=Y

//信息看板XML存放路径
MONITOR.PATH=D:\FTP\MOBILE\
//信息看板服务器IP
MONITOR.SERVER=172.20.10.49
//信息看板服务器端口号
MONITOR.PORT=8103



//中文字体缩放比例
ZhFontSizeProportion=1.0

//英文字体缩放比例
EnFontSizeProportion=0.8

//合理用药
PassIsReadyOE=Y
EnforcementFlg=N
WarnFlg=2

//邮件服务器配置
//SMTP服和器地址
mail.smtp.host=smtp.gmail.com
//SMTP服务器端口号
mail.smtp.port=465
//SMTP服务器需要身份认证
mail.smtp.auth=Yes
//服务器是否要求安全连接(SSL)
mail.smtp.smtpSSL=Yes
//发送邮箱
mail.admin.account=li.xiang790130@gmail.com
//发送邮箱密码
mail.admin.password=eu000319
//研究病例隐去标签名称列表
EMR.TEST_TAG=HR02.01.002|HR01.01.002.02|HR03.00.004.01|HR03.00.004.02|HR03.00.004.03|HR03.00.004.04|HR03.00.004.05|HR03.00.004.06

//医保设置
InsInPath=D:\log\INS_IN\
InsOutPath=D:\log\INS_OUT\
InsLogPath=c:/INS/log.txt
InsDebug=1
InsType=0
InsHost=172.20.10.99
InsPort=8002

//批次服务
BatchServer=N

sms.path=C:\sms\
FTP.Server=172.20.1.253
FTP.Port=1022
FTP.Username=bluecore
FTP.Password=bluecore


//医建通项目ftp服务器地址
CHECKACCOUNTFTP.PATH=ftp://javahis:javahis@172.20.107.166:21/download/
REGSCHDAY.PATH=172.20.107.166
REGSCHDAY.COUNT=5

//医疗监管指标上传文件参数
//生成路径
LocalStaGenFilePath=C:\JavaHis\StaGenData\
LocalStaSendFilePath=C:\JavaHis\StaSendData\
ServerStaSendFilePath=D:\JavaHis\StaSendData\
//log日志
StaGenFileLocalPath=C:\JavaHis\logs
Servcie_Url=https://ts.hqms.org.cn
User=u13247
Pwd=tedaich8491
DataType=json
DEBUG=0

WEB_SERVICES_IP=172.20.10.89:8080/webgy

inp.name=EMR100009
inp.nameparent=EMR10
inp.nameseq=9

//未执行医嘱log
NsExeclog=c:\JavaHisFile\NsExeclog\

//电子病历平台
EMRIP=172.20.10.82:8080/emr/emr.html


//大屏幕XML存放路径
SCREEN.PATH=D:\FTP\SCREEN\



//重症CIS体征监测数据接口_ICU视图名
CIS.ICU_VIEW_NAME=dbo.V_ICU_Vitalsigns
//重症CIS体征监测数据接口_CCU视图名
CIS.CCU_VIEW_NAME=dbo.V_CCU_Vitalsigns
//重症CIS体征监测数据接口_住院抢救视图名
CIS.WARD_VIEW_NAME=dbo.V_Ward_Vitalsigns
//重症CIS体征监测数据接口_急诊抢救视图名
CIS.EMD_VIEW_NAME=dbo.V_EMD_Vitalsigns

//病区CIS体征监测数据采集服务启动延时，单位：毫秒
CIS.ODIDelayTime=180000
//病区CIS体征监测数据采集重复调用间隔时间，单位：毫秒
CIS.ODIPeriodTime=1800000

//急诊CIS体征监测数据采集服务启动延时，单位：毫秒
CIS.ERDDelayTime=180000
//急诊CIS体征监测数据采集重复调用间隔时间，单位：毫秒
CIS.ERDPeriodTime=300000



CDSS_SERVICES_IP=172.20.10.82:8080/drools


//盒装包药机webservices 路径
bsm.path=http://172.20.10.88:1080/web_consis/ServiceConsis.asmx?WSDL

//盒装包药机虚拟库
BOXDEPT_CODE=040110

//配置webservice的IP地址
WEB_SERVICES_SYS_DIC_IP=172.20.10.43:8080/web

//配置统一编码中注册的厂商账号和密码
UserName=SPC
Password=123

//配置统一编码中执行间隔时间，单位：毫秒
Time=60000

WEB_SERVICES_LABELIP=http://172.20.10.63:8081/

OpeInfPIC.LocalPath=C:\JavaHis\Temp
OpeInfPIC.ServerPath=OpePic

//急诊检伤评分
ERDLevelSUBCLASSCODE=EMR06020501
ERDLevelCLASSCODE=EMR060205

// add by wangqing 20180123 跌倒疼痛评估表
REG_FALL_PAIN_SUBCLASSCODE=EMR02000621
REG_FALL_PAIN_CLASSCODE=EMR020006

//NIS地址
NISIP=

//预交金短信开关
sms.lock=Y
