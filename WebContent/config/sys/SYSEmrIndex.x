#
# TBuilder Config File 
#
# Title:����������
#
# Company:JavaHis
#
# Author:zhangy 2009.06.12
#
# version 1.0
#

<Type=TFrame>
UI.Title=����������
UI.MenuConfig=%ROOT%\config\sys\SYSEmrIndexMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SYSEmrIndexControl
UI.item=tPanel_0;tPanel_1
UI.layout=null
UI.Tip=����������
UI.Text=����������
UI.TopMenu=Y
UI.TopToolBar=Y
UI.FocusList=CASE_NO;ADM_TYPE;REGION_CODE;IPD_NO;MR_NO;ADM_DATE;DEPT_CODE;DR_CODE;DS_DATE
tPanel_1.Type=TPanel
tPanel_1.X=5
tPanel_1.Y=109
tPanel_1.Width=1014
tPanel_1.Height=634
tPanel_1.AutoX=Y
tPanel_1.AutoWidth=Y
tPanel_1.AutoHeight=Y
tPanel_1.Border=��
tPanel_1.Item=TABLE
TABLE.Type=TTable
TABLE.X=72
TABLE.Y=51
TABLE.Width=81
TABLE.Height=81
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoY=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.AutoSize=0
TABLE.Header=�������,120;�ż�ס��,80,ADM_TYPE;����,120,REGION_CODE;סԺ��,120;������,120;������(סԺ��),170,Timestamp,yyyy/MM/dd HH:mm:ss;�Ʊ�,120,DEPT_CODE;ҽʦ,120,OPT;��Ժ����,120;������,80,OPT;����ʱ��,120;����IP,120
TABLE.LockColumns=all
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left
TABLE.ParmMap=CASE_NO;ADM_TYPE;REGION_CODE;IPD_NO;MR_NO;ADM_DATE;DEPT_CODE;DR_CODE;DS_DATE;OPT_USER;OPT_DATE;OPT_TERM
TABLE.AutoModifyDataStore=Y
TABLE.ClickedAction=onTableClicked
TABLE.Item=ADM_TYPE;REGION_CODE;DEPT_CODE;OPT
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=1014
tPanel_0.Height=101
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoWidth=Y
tPanel_0.Border=��
tPanel_0.Item=tLabel_0;tLabel_1;tLabel_2;tLabel_3;tLabel_4;tLabel_5;tLabel_6;tLabel_7;tLabel_8;CASE_NO;ADM_TYPE;REGION_CODE;IPD_NO;MR_NO;ADM_DATE;DS_DATE;DR_CODE;DEPT_CODE;OPT
OPT.Type=��Ա�����б�
OPT.X=911
OPT.Y=109
OPT.Width=81
OPT.Height=23
OPT.Text=TButton
OPT.showID=Y
OPT.showName=Y
OPT.showText=N
OPT.showValue=N
OPT.showPy1=N
OPT.showPy2=N
OPT.Editable=Y
OPT.Tip=��Ա
OPT.TableShowList=name
OPT.ModuleParmString=
OPT.ModuleParmTag=
DEPT_CODE.Type=���������б�
DEPT_CODE.X=97
DEPT_CODE.Y=69
DEPT_CODE.Width=160
DEPT_CODE.Height=23
DEPT_CODE.Text=TButton
DEPT_CODE.showID=Y
DEPT_CODE.showName=Y
DEPT_CODE.showText=N
DEPT_CODE.showValue=N
DEPT_CODE.showPy1=N
DEPT_CODE.showPy2=N
DEPT_CODE.Editable=Y
DEPT_CODE.Tip=����
DEPT_CODE.TableShowList=name
DEPT_CODE.FinalFlg=Y
DEPT_CODE.Classify=0
DEPT_CODE.IpdFitFlg=
DEPT_CODE.Action=onDeptAction
DR_CODE.Type=��Ա�����б�
DR_CODE.X=382
DR_CODE.Y=69
DR_CODE.Width=160
DR_CODE.Height=23
DR_CODE.Text=TButton
DR_CODE.showID=Y
DR_CODE.showName=Y
DR_CODE.showText=N
DR_CODE.showValue=N
DR_CODE.showPy1=N
DR_CODE.showPy2=N
DR_CODE.Editable=Y
DR_CODE.Tip=��Ա
DR_CODE.TableShowList=name
DR_CODE.ModuleParmString=
DR_CODE.ModuleParmTag=
DR_CODE.PosType=2
DS_DATE.Type=TTextFormat
DS_DATE.X=715
DS_DATE.Y=70
DS_DATE.Width=160
DS_DATE.Height=20
DS_DATE.Text=
DS_DATE.showDownButton=Y
DS_DATE.FormatType=date
DS_DATE.Format=yyyy/MM/dd HH:mm:ss
ADM_DATE.Type=TTextFormat
ADM_DATE.X=715
ADM_DATE.Y=40
ADM_DATE.Width=160
ADM_DATE.Height=20
ADM_DATE.Text=
ADM_DATE.showDownButton=Y
ADM_DATE.FormatType=date
ADM_DATE.Format=yyyy/MM/dd HH:mm:ss
MR_NO.Type=TTextField
MR_NO.X=382
MR_NO.Y=40
MR_NO.Width=160
MR_NO.Height=20
MR_NO.Text=
IPD_NO.Type=TTextField
IPD_NO.X=97
IPD_NO.Y=40
IPD_NO.Width=160
IPD_NO.Height=20
IPD_NO.Text=
REGION_CODE.Type=���������б�
REGION_CODE.X=715
REGION_CODE.Y=9
REGION_CODE.Width=160
REGION_CODE.Height=23
REGION_CODE.Text=TButton
REGION_CODE.showID=Y
REGION_CODE.showName=Y
REGION_CODE.showText=N
REGION_CODE.showValue=N
REGION_CODE.showPy1=N
REGION_CODE.showPy2=N
REGION_CODE.Editable=Y
REGION_CODE.Tip=����
REGION_CODE.TableShowList=name
REGION_CODE.ModuleParmString=
REGION_CODE.ModuleParmTag=
REGION_CODE.ExpandWidth=30
ADM_TYPE.Type=�ż�ס�������б�
ADM_TYPE.X=382
ADM_TYPE.Y=9
ADM_TYPE.Width=120
ADM_TYPE.Height=23
ADM_TYPE.Text=TButton
ADM_TYPE.showID=Y
ADM_TYPE.showName=Y
ADM_TYPE.showText=N
ADM_TYPE.showValue=N
ADM_TYPE.showPy1=N
ADM_TYPE.showPy2=N
ADM_TYPE.Editable=Y
ADM_TYPE.Tip=�ż�ס��
ADM_TYPE.TableShowList=name
ADM_TYPE.ModuleParmString=GROUP_ID:SYS_ADMTYPE
ADM_TYPE.ModuleParmTag=
ADM_TYPE.Action=onADMTypeAction
CASE_NO.Type=TTextField
CASE_NO.X=97
CASE_NO.Y=10
CASE_NO.Width=160
CASE_NO.Height=20
CASE_NO.Text=
tLabel_8.Type=TLabel
tLabel_8.X=588
tLabel_8.Y=73
tLabel_8.Width=72
tLabel_8.Height=15
tLabel_8.Text=��Ժ����:
tLabel_7.Type=TLabel
tLabel_7.X=301
tLabel_7.Y=73
tLabel_7.Width=72
tLabel_7.Height=15
tLabel_7.Text=ҽʦ����:
tLabel_7.Color=blue
tLabel_6.Type=TLabel
tLabel_6.X=16
tLabel_6.Y=73
tLabel_6.Width=72
tLabel_6.Height=15
tLabel_6.Text=�Ʊ����:
tLabel_6.Color=blue
tLabel_5.Type=TLabel
tLabel_5.X=588
tLabel_5.Y=43
tLabel_5.Width=120
tLabel_5.Height=15
tLabel_5.Text=������(סԺ��):
tLabel_4.Type=TLabel
tLabel_4.X=301
tLabel_4.Y=43
tLabel_4.Width=72
tLabel_4.Height=15
tLabel_4.Text=�� �� ��:
tLabel_4.Color=blue
tLabel_3.Type=TLabel
tLabel_3.X=16
tLabel_3.Y=43
tLabel_3.Width=72
tLabel_3.Height=15
tLabel_3.Text=ס Ժ ��:
tLabel_2.Type=TLabel
tLabel_2.X=588
tLabel_2.Y=14
tLabel_2.Width=72
tLabel_2.Height=15
tLabel_2.Text=�������:
tLabel_1.Type=TLabel
tLabel_1.X=301
tLabel_1.Y=14
tLabel_1.Width=72
tLabel_1.Height=15
tLabel_1.Text=�ż�ס��:
tLabel_1.Color=blue
tLabel_0.Type=TLabel
tLabel_0.X=16
tLabel_0.Y=14
tLabel_0.Width=72
tLabel_0.Height=15
tLabel_0.Text=�������:
tLabel_0.Color=blue