#
# TBuilder Config File 
#
# Title:ҩ��ҩ���趨
#
# Company:JavaHis
#
# Author:zhangy 2009.04.21
#
# version 1.0
#

<Type=TFrame>
UI.Title=ҩ��ҩ��
UI.MenuConfig=%ROOT%\config\ind\INDOrgMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ind.INDOrgControl
UI.item=tPanel_0;tPanel_1;tMovePane_0
UI.layout=null
UI.TopMenu=Y
UI.TopToolBar=Y
UI.ShowTitle=N
UI.ShowMenu=N
UI.Text=ҩ��ҩ��
UI.Tip=ҩ��ҩ��
UI.FocusList=REGION_CODE;ORG_TYPE_A;ORG_TYPE_B;ORG_TYPE_C;STATION_FLG;ORG_CODE;SUP_ORG_CODE;DECOCT_CODE;EXINV_FLG;INJ_ORG_FLG;ATC_FLG;SEQ;DESCRIPTION
tMovePane_0.Type=TMovePane
tMovePane_0.X=6
tMovePane_0.Y=95
tMovePane_0.Width=1014
tMovePane_0.Height=5
tMovePane_0.Text=
tMovePane_0.MoveType=2
tMovePane_0.Border=͹
tMovePane_0.AutoX=Y
tMovePane_0.AutoWidth=Y
tMovePane_0.Style=3
tMovePane_0.EntityData=tPanel_0,2;tPanel_1,1
tPanel_1.Type=TPanel
tPanel_1.X=5
tPanel_1.Y=101
tPanel_1.Width=1014
tPanel_1.Height=642
tPanel_1.AutoX=Y
tPanel_1.AutoY=N
tPanel_1.AutoWidth=Y
tPanel_1.AutoHeight=Y
tPanel_1.AutoSize=5
tPanel_1.Border=��
tPanel_1.Item=TABLE
TABLE.Type=TTable
TABLE.X=107
TABLE.Y=2
TABLE.Width=1010
TABLE.Height=638
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoY=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.AutoSize=0
TABLE.Header=���Ŵ���,80;ҩ������,120;���,60;�ⷿ����,80,TYPE;�����ⷿ,120,SUP_ORG_CODE;����,80,REGION_CODE;��ҩ��,120,DECOCT_CODE;��ʿվ,80,boolean;�������쵥,80,boolean;������Һ,80,boolean;��ҩ��,80,boolean;��ע,150;���� ��,80;��������,120;����ĩ��,100
TABLE.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
TABLE.ParmMap=ORG_CODE;ORG_CHN_DESC;SEQ;ORG_TYPE;SUP_ORG_CODE;REGION_CODE;DECOCT_CODE;STATION_FLG;EXINV_FLG;INJ_ORG_FLG;ATC_FLG;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM
TABLE.AutoModifyDataStore=Y
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,left;4,left;5,left;6,left;11,left;12,left;13,left;14,left
TABLE.ClickedAction=onTableClicked
TABLE.Item=SUP_ORG_CODE;REGION_CODE;TYPE;DECOCT_CODE
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=1014
tPanel_0.Height=89
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoWidth=Y
tPanel_0.AutoSize=5
tPanel_0.Border=��
tPanel_0.Item=tLabel_1;ORG_TYPE_A;ORG_TYPE_B;ORG_TYPE_C;tLabel_3;STATION_FLG;SUP_ORG_CODE;tLabel_4;REGION_CODE;EXINV_FLG;INJ_ORG_FLG;ATC_FLG;tLabel_5;DESCRIPTION;tLabel_2;SEQ;tLabel_6;TYPE;tLabel_7;ORG_CODE;DECOCT_CODE
DECOCT_CODE.Type=����
DECOCT_CODE.X=88
DECOCT_CODE.Y=50
DECOCT_CODE.Width=120
DECOCT_CODE.Height=23
DECOCT_CODE.Text=
DECOCT_CODE.HorizontalAlignment=2
DECOCT_CODE.PopupMenuHeader=����,100;����,100
DECOCT_CODE.PopupMenuWidth=300
DECOCT_CODE.PopupMenuHeight=300
DECOCT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
DECOCT_CODE.FormatType=combo
DECOCT_CODE.ShowDownButton=Y
DECOCT_CODE.Tip=����
DECOCT_CODE.ShowColumnList=NAME
DECOCT_CODE.ClassIfy=4
DECOCT_CODE.Enabled=N
DECOCT_CODE.HisOneNullRow=Y
ORG_CODE.Type=TTextFormat
ORG_CODE.X=635
ORG_CODE.Y=12
ORG_CODE.Width=131
ORG_CODE.Height=20
ORG_CODE.Text=
ORG_CODE.showDownButton=Y
ORG_CODE.FormatType=combo
ORG_CODE.PopupMenuWidth=250
ORG_CODE.PopupMenuHeight=200
ORG_CODE.HisOneNullRow=Y
ORG_CODE.PopupMenuHeader=ID,100;NAME,150
ORG_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
ORG_CODE.ShowColumnList=NAME
ORG_CODE.ValueColumn=ID
ORG_CODE.HorizontalAlignment=2
tLabel_7.Type=TLabel
tLabel_7.X=565
tLabel_7.Y=15
tLabel_7.Width=72
tLabel_7.Height=15
tLabel_7.Text=ҩ������:
tLabel_7.Color=blue
TYPE.Type=TComboBox
TYPE.X=958
TYPE.Y=37
TYPE.Width=39
TYPE.Height=23
TYPE.Text=TButton
TYPE.showID=Y
TYPE.Editable=Y
TYPE.StringData=[[id,name],[A,����],[B,�п�],[C,С��]]
TYPE.ShowText=N
TYPE.ShowName=Y
TYPE.TableShowList=name
TYPE.Visible=N
tLabel_6.Type=TLabel
tLabel_6.X=15
tLabel_6.Y=55
tLabel_6.Width=69
tLabel_6.Height=15
tLabel_6.Text=��ҩ��:
SEQ.Type=TNumberTextField
SEQ.X=639
SEQ.Y=52
SEQ.Width=77
SEQ.Height=20
SEQ.Text=0
SEQ.Format=#########0
tLabel_2.Type=TLabel
tLabel_2.X=565
tLabel_2.Y=55
tLabel_2.Width=44
tLabel_2.Height=15
tLabel_2.Text=���:
DESCRIPTION.Type=TTextField
DESCRIPTION.X=817
DESCRIPTION.Y=52
DESCRIPTION.Width=153
DESCRIPTION.Height=20
DESCRIPTION.Text=
tLabel_5.Type=TLabel
tLabel_5.X=751
tLabel_5.Y=55
tLabel_5.Width=56
tLabel_5.Height=15
tLabel_5.Text=��  ע:
ATC_FLG.Type=TCheckBox
ATC_FLG.X=484
ATC_FLG.Y=51
ATC_FLG.Width=70
ATC_FLG.Height=23
ATC_FLG.Text=��ҩ��
ATC_FLG.Enabled=N
INJ_ORG_FLG.Type=TCheckBox
INJ_ORG_FLG.X=352
INJ_ORG_FLG.Y=51
INJ_ORG_FLG.Width=111
INJ_ORG_FLG.Height=23
INJ_ORG_FLG.Text=������Һ����
INJ_ORG_FLG.Enabled=N
EXINV_FLG.Type=TCheckBox
EXINV_FLG.X=225
EXINV_FLG.Y=51
EXINV_FLG.Width=97
EXINV_FLG.Height=23
EXINV_FLG.Text=�������쵥
REGION_CODE.Type=���������б�
REGION_CODE.X=88
REGION_CODE.Y=11
REGION_CODE.Width=120
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
REGION_CODE.Enabled=N
tLabel_4.Type=TLabel
tLabel_4.X=16
tLabel_4.Y=15
tLabel_4.Width=52
tLabel_4.Height=15
tLabel_4.Text=����:
tLabel_4.Color=blue
SUP_ORG_CODE.Type=ҩ�������б�
SUP_ORG_CODE.X=850
SUP_ORG_CODE.Y=11
SUP_ORG_CODE.Width=120
SUP_ORG_CODE.Height=23
SUP_ORG_CODE.Text=TButton
SUP_ORG_CODE.showID=Y
SUP_ORG_CODE.showName=Y
SUP_ORG_CODE.showText=N
SUP_ORG_CODE.showValue=N
SUP_ORG_CODE.showPy1=N
SUP_ORG_CODE.showPy2=N
SUP_ORG_CODE.Editable=Y
SUP_ORG_CODE.Tip=ҩ��
SUP_ORG_CODE.TableShowList=name
SUP_ORG_CODE.ModuleParmTag=
SUP_ORG_CODE.OrgType=
SUP_ORG_CODE.ExpandWidth=30
STATION_FLG.Type=TCheckBox
STATION_FLG.X=484
STATION_FLG.Y=11
STATION_FLG.Width=72
STATION_FLG.Height=23
STATION_FLG.Text=��ʿվ
STATION_FLG.Action=onSelectStation
tLabel_3.Type=TLabel
tLabel_3.X=778
tLabel_3.Y=15
tLabel_3.Width=69
tLabel_3.Height=15
tLabel_3.Text=�����ⷿ:
ORG_TYPE_C.Type=TRadioButton
ORG_TYPE_C.X=422
ORG_TYPE_C.Y=11
ORG_TYPE_C.Width=55
ORG_TYPE_C.Height=23
ORG_TYPE_C.Text=С��
ORG_TYPE_C.Group=group1
ORG_TYPE_C.Selected=Y
ORG_TYPE_C.Action=onChangeOrgType
ORG_TYPE_B.Type=TRadioButton
ORG_TYPE_B.X=362
ORG_TYPE_B.Y=11
ORG_TYPE_B.Width=57
ORG_TYPE_B.Height=23
ORG_TYPE_B.Text=�п�
ORG_TYPE_B.Group=group1
ORG_TYPE_B.Action=onChangeOrgType
ORG_TYPE_A.Type=TRadioButton
ORG_TYPE_A.X=302
ORG_TYPE_A.Y=11
ORG_TYPE_A.Width=58
ORG_TYPE_A.Height=23
ORG_TYPE_A.Text=����
ORG_TYPE_A.Group=group1
ORG_TYPE_A.Action=onChangeOrgType
tLabel_1.Type=TLabel
tLabel_1.X=227
tLabel_1.Y=15
tLabel_1.Width=72
tLabel_1.Height=15
tLabel_1.Text=�ⷿ����:
tLabel_1.Color=blue