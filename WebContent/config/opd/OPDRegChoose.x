#############################################
# <p>Title:�Һ���Ϣѡ�� </p>
#
# <p>Description:�Һ���Ϣѡ�� </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangk 2010-10-28
# @version 4.0
#############################################
<Type=TFrame>
UI.Title=�Һ���Ϣѡ��
UI.MenuConfig=
UI.Width=600
UI.Height=300
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.opd.OPDRegChooseControl
UI.Item=TABLE;ADM_STATUS;REPORT_STATUS
REPORT_STATUS.Type=����״̬�����б�
REPORT_STATUS.X=186
REPORT_STATUS.Y=8
REPORT_STATUS.Width=81
REPORT_STATUS.Height=23
REPORT_STATUS.Text=TButton
REPORT_STATUS.showID=Y
REPORT_STATUS.showName=Y
REPORT_STATUS.showText=N
REPORT_STATUS.showValue=N
REPORT_STATUS.showPy1=Y
REPORT_STATUS.showPy2=Y
REPORT_STATUS.Editable=Y
REPORT_STATUS.Tip=����״̬
REPORT_STATUS.TableShowList=name
REPORT_STATUS.ModuleParmString=GROUP_ID:SYS_RPT_STATUS
REPORT_STATUS.ModuleParmTag=
ADM_STATUS.Type=����״̬�����б�
ADM_STATUS.X=70
ADM_STATUS.Y=7
ADM_STATUS.Width=81
ADM_STATUS.Height=23
ADM_STATUS.Text=TButton
ADM_STATUS.showID=Y
ADM_STATUS.showName=Y
ADM_STATUS.showText=N
ADM_STATUS.showValue=N
ADM_STATUS.showPy1=Y
ADM_STATUS.showPy2=Y
ADM_STATUS.Editable=Y
ADM_STATUS.Tip=����״̬�����б�
ADM_STATUS.TableShowList=name
ADM_STATUS.ModuleParmString=GROUP_ID:SYS_ADM_STATUS
ADM_STATUS.ModuleParmTag=
TABLE.Type=TTable
TABLE.X=66
TABLE.Y=34
TABLE.Width=81
TABLE.Height=261
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoY=Y
TABLE.AutoX=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.Header=������,100;�������,100;�Һ�����,100,timestamp,yyyy/MM/dd;����,80;����״̬,100,ADM_STATUS;����״̬,100,REPORT_STATUS
TABLE.Item=ADM_STATUS;REPORT_STATUS
TABLE.ParmMap=MR_NO;CASE_NO;ADM_DATE;PAT_NAME;ADM_STATUS;REPORT_STATUS
TABLE.DoubleClickedAction=onDoubleClick
TABLE.LockColumns=all