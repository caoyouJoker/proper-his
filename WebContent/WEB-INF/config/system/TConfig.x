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
EmrLocalTempFileName=ģ���ļ�

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
//ҩ���ۿ�Ʒѵ�
//DOSAGE:��ҩ��DISPENSE:��ҩ
CHARGE_POINT=DOSAGE
//סԺҩ�������Ƿ����������
IS_CHECK=Y
//סԺҩ�������Ƿ�����ҩ����
IS_DOSAGE=Y
//������ҩ�����ѭ������
CYCLE_TIMES_FOR_ERR=3

//         ��������
//         �����ֲ�ʷ
ODOEmrTempletZSCLASSCODE=EMR020001
ODOEmrTempletZSSUBCLASSCODE=EMR02000103
ODOEmrTempletZSPath=JHW\�ţ������ﲡ��\��(��)�ﲡ��
ODOEmrTempletZSFileName=�����ֲ�ʷ
//ODOEmrTempletZSCLASSCODE=02
//ODOEmrTempletZSSUBCLASSCODE=0205
//ODOEmrTempletZSPath=�����ֲ�ʷ
//ODOEmrTempletZSFileName=�����ֲ�ʷ

//ODOEmrTempletZSFileName=Sub��Obj��Phy Template

//         ����ʷ
ODOEmrTempletFAMILYHISTORY_CLASSCODE=18
ODOEmrTempletFAMILYHISTORY_SUBCLASSCODE=1801
ODOEmrTempletFAMILYHISTORY_Path=����ʷ
ODOEmrTempletFAMILYHISTORY_FileName=�������ʷ


//סԺ֤
ADMEmrINHOSPPATH=JHW\�ţ������ﲡ��\��(��)�ﲡ��
ADMEmrINHOSPFILENAME=סԺ֤
ADMEmrINHOSPSUBCLASSCODE=EMR02000109
ADMEmrINHOSPCLASSCODE=EMR020001

//�ż��ﻤʿվ���ýṹ������
ONWEmrMRCODE=EMR02000104
//������¼���ýṹ������
OPEEmrMRCODE=EMR05010214
//���鱨��
LABIP=172.20.109.241

//��鱨��
RISIP=172.20.40.50

//��鱨����IP
RISIPNEW=172.20.40.40:8080

//Ѫ�Ǳ���
TNBIP=172.20.1.24

//        JavaHis Language
Language=zh,����;en,English


//        EKT Config
ekt.switch=true
ekt.opd.EKTDialogSwitch=true
ekt.port=COM1

//�������֤ͼƬ����·��
sid.path=C:\\IdCard

//סԺ��ҩ��XML�ļ����·��
ATCPATH.I=C:\JavaHis\ATC_I\
//�����ҩ��XML�ļ����·��
ATCPATH.O=C:\JavaHis\ATC_O\
//�����ҩ���ļ�������IP
FILE_SERVER_IP=172.20.10.49

//�����ҩ���ļ��������˿ں�
PORT=8103

//�����ҩ�����������ݿ�
ACTDBO.Type=oracle
ACTDBO.Address=172.20.107.87
ACTDBO.Port=1521
ACTDBO.DBName=ORCL
ACTDBO.UserName=fds_user
ACTDBO.Password=yuyama

//סԺ��ҩ�����������ݿ�
ACTDBI.Type=oracle
ACTDBI.Address=172.20.107.88
ACTDBI.Port=1521
ACTDBI.DBName=ORCL
ACTDBI.UserName=fds_user
ACTDBI.Password=yuyama


//�кŽӿ�
//CallHost=127.0.0.1
//CallPort=80
//CallPath=/fzservice/service.asmx
//CallNamespace=http://tempuri.org/
//ServerIsReady=Y
IsCallNO=N


//����ӿ�
//����
CallLabHostO=127.0.0.1
//�������
CallLabHostH=127.0.0.1
//����
CallLabHostE=127.0.0.1
//�˿ں�
CallLabPort=9100
//�Ƿ�����
ServerLabIsReady=Y

//��Ϣ����XML���·��
MONITOR.PATH=D:\FTP\MOBILE\
//��Ϣ���������IP
MONITOR.SERVER=172.20.10.49
//��Ϣ����������˿ں�
MONITOR.PORT=8103



//�����������ű���
ZhFontSizeProportion=1.0

//Ӣ���������ű���
EnFontSizeProportion=0.8

//������ҩ
PassIsReadyOE=Y
EnforcementFlg=N
WarnFlg=2

//�ʼ�����������
//SMTP��������ַ
mail.smtp.host=smtp.gmail.com
//SMTP�������˿ں�
mail.smtp.port=465
//SMTP��������Ҫ�����֤
mail.smtp.auth=Yes
//�������Ƿ�Ҫ��ȫ����(SSL)
mail.smtp.smtpSSL=Yes
//��������
mail.admin.account=li.xiang790130@gmail.com
//������������
mail.admin.password=eu000319
//�о�������ȥ��ǩ�����б�
EMR.TEST_TAG=HR02.01.002|HR01.01.002.02|HR03.00.004.01|HR03.00.004.02|HR03.00.004.03|HR03.00.004.04|HR03.00.004.05|HR03.00.004.06

//ҽ������
InsInPath=D:\log\INS_IN\
InsOutPath=D:\log\INS_OUT\
InsLogPath=c:/INS/log.txt
InsDebug=1
InsType=0
InsHost=172.20.10.99
InsPort=8002

//���η���
BatchServer=N

sms.path=C:\sms\
FTP.Server=172.20.1.253
FTP.Port=1022
FTP.Username=bluecore
FTP.Password=bluecore


//ҽ��ͨ��Ŀftp��������ַ
CHECKACCOUNTFTP.PATH=ftp://javahis:javahis@172.20.107.166:21/download/
REGSCHDAY.PATH=172.20.107.166
REGSCHDAY.COUNT=5

//ҽ�Ƽ��ָ���ϴ��ļ�����
//����·��
LocalStaGenFilePath=C:\JavaHis\StaGenData\
LocalStaSendFilePath=C:\JavaHis\StaSendData\
ServerStaSendFilePath=D:\JavaHis\StaSendData\
//log��־
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

//δִ��ҽ��log
NsExeclog=c:\JavaHisFile\NsExeclog\

//���Ӳ���ƽ̨
EMRIP=172.20.10.82:8080/emr/emr.html


//����ĻXML���·��
SCREEN.PATH=D:\FTP\SCREEN\



//��֢CIS����������ݽӿ�_ICU��ͼ��
CIS.ICU_VIEW_NAME=dbo.V_ICU_Vitalsigns
//��֢CIS����������ݽӿ�_CCU��ͼ��
CIS.CCU_VIEW_NAME=dbo.V_CCU_Vitalsigns
//��֢CIS����������ݽӿ�_סԺ������ͼ��
CIS.WARD_VIEW_NAME=dbo.V_Ward_Vitalsigns
//��֢CIS����������ݽӿ�_����������ͼ��
CIS.EMD_VIEW_NAME=dbo.V_EMD_Vitalsigns

//����CIS����������ݲɼ�����������ʱ����λ������
CIS.ODIDelayTime=180000
//����CIS����������ݲɼ��ظ����ü��ʱ�䣬��λ������
CIS.ODIPeriodTime=1800000

//����CIS����������ݲɼ�����������ʱ����λ������
CIS.ERDDelayTime=180000
//����CIS����������ݲɼ��ظ����ü��ʱ�䣬��λ������
CIS.ERDPeriodTime=300000



CDSS_SERVICES_IP=172.20.10.82:8080/drools


//��װ��ҩ��webservices ·��
bsm.path=http://172.20.10.88:1080/web_consis/ServiceConsis.asmx?WSDL

//��װ��ҩ�������
BOXDEPT_CODE=040110

//����webservice��IP��ַ
WEB_SERVICES_SYS_DIC_IP=172.20.10.43:8080/web

//����ͳһ������ע��ĳ����˺ź�����
UserName=SPC
Password=123

//����ͳһ������ִ�м��ʱ�䣬��λ������
Time=60000

WEB_SERVICES_LABELIP=http://172.20.10.63:8081/

OpeInfPIC.LocalPath=C:\JavaHis\Temp
OpeInfPIC.ServerPath=OpePic

//�����������
ERDLevelSUBCLASSCODE=EMR06020501
ERDLevelCLASSCODE=EMR060205

// add by wangqing 20180123 ������ʹ������
REG_FALL_PAIN_SUBCLASSCODE=EMR02000621
REG_FALL_PAIN_CLASSCODE=EMR020006

//NIS��ַ
NISIP=

//Ԥ������ſ���
sms.lock=Y
