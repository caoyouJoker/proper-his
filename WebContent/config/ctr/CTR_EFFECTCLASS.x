#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:ʯ���� 2011.06.25
#
# version 1.0
#

<Type=TFrame>
UI.Title=ҩƷ��������Ӱ��ȼ�
UI.MenuConfig=%ROOT%\config\ctr\CTR_EFFECTCLASSMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ctr.CTREFFECTCLASSControl
UI.item=tPanel_0;TABLE
UI.layout=null
TABLE.Type=TTable
TABLE.X=6
TABLE.Y=124
TABLE.Width=81
TABLE.Height=81
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.Header=�ȼ�����,140;����˵��,100;PY1,70;PY2,70;Ӣ��˵��,150;˳���,50;�ܿط�ʽ,140,CTRL_TYPE;��ʾ����,200;��ע,200
TABLE.ParmMap=EFFECTCLAS_CODE;CHN_DESC;PY1;PY2;ENG_DESC;SEQ;CTRL_TYPE;MESSAGE_TEXT;DESCRIPTION
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left
TABLE.LockColumns=ALL
TABLE.Item=CTRL_TYPE
TABLE.ClickedAction=onTableClicked
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=1014
tPanel_0.Height=109
tPanel_0.Border=��
tPanel_0.AutoWidth=Y
tPanel_0.Item=tLabel_0;tLabel_1;CHN_DESC;tLabel_2;ENG_DESC;tLabel_4;PY1;tLabel_5;PY2;tLabel_6;tLabel_7;MESSAGE_TEXT;tLabel_8;DESCRIPTION;CTRL_TYPE;EFFECTCLAS_CODE
EFFECTCLAS_CODE.Type=TTextField
EFFECTCLAS_CODE.X=90
EFFECTCLAS_CODE.Y=19
EFFECTCLAS_CODE.Width=88
EFFECTCLAS_CODE.Height=20
EFFECTCLAS_CODE.Text=
CTRL_TYPE.Type=TComboBox
CTRL_TYPE.X=91
CTRL_TYPE.Y=60
CTRL_TYPE.Width=92
CTRL_TYPE.Height=23
CTRL_TYPE.Text=TButton
CTRL_TYPE.showID=N
CTRL_TYPE.Editable=Y
CTRL_TYPE.StringData=[[ID,TEXT],[1,ǿ��],[2,��ǿ��],[3,���ܿ�]]
CTRL_TYPE.TableShowList=TEXT
DESCRIPTION.Type=TTextField
DESCRIPTION.X=609
DESCRIPTION.Y=58
DESCRIPTION.Width=334
DESCRIPTION.Height=20
DESCRIPTION.Text=
tLabel_8.Type=TLabel
tLabel_8.X=555
tLabel_8.Y=61
tLabel_8.Width=56
tLabel_8.Height=15
tLabel_8.Text=��ע:
MESSAGE_TEXT.Type=TTextField
MESSAGE_TEXT.X=256
MESSAGE_TEXT.Y=60
MESSAGE_TEXT.Width=284
MESSAGE_TEXT.Height=20
MESSAGE_TEXT.Text=
tLabel_7.Type=TLabel
tLabel_7.X=190
tLabel_7.Y=64
tLabel_7.Width=72
tLabel_7.Height=15
tLabel_7.Text=��ʾ����:
tLabel_6.Type=TLabel
tLabel_6.X=8
tLabel_6.Y=63
tLabel_6.Width=81
tLabel_6.Height=15
tLabel_6.Text=�ܿط�ʽ*
tLabel_6.Color=blue
tLabel_6.FontSize=16
PY2.Type=TTextField
PY2.X=607
PY2.Y=21
PY2.Width=106
PY2.Height=20
PY2.Text=
tLabel_5.Type=TLabel
tLabel_5.X=560
tLabel_5.Y=21
tLabel_5.Width=49
tLabel_5.Height=15
tLabel_5.Text=PY2:
PY1.Type=TTextField
PY1.X=438
PY1.Y=20
PY1.Width=101
PY1.Height=20
PY1.Text=
tLabel_4.Type=TLabel
tLabel_4.X=401
tLabel_4.Y=20
tLabel_4.Width=37
tLabel_4.Height=15
tLabel_4.Text=PY1:
ENG_DESC.Type=TTextField
ENG_DESC.X=802
ENG_DESC.Y=22
ENG_DESC.Width=138
ENG_DESC.Height=20
ENG_DESC.Text=
tLabel_2.Type=TLabel
tLabel_2.X=726
tLabel_2.Y=23
tLabel_2.Width=72
tLabel_2.Height=15
tLabel_2.Text=Ӣ��˵��:
CHN_DESC.Type=TTextField
CHN_DESC.X=251
CHN_DESC.Y=19
CHN_DESC.Width=133
CHN_DESC.Height=20
CHN_DESC.Text=
tLabel_1.Type=TLabel
tLabel_1.X=181
tLabel_1.Y=20
tLabel_1.Width=72
tLabel_1.Height=13
tLabel_1.Text=����˵��:
tLabel_1.Color=blue
tLabel_0.Type=TLabel
tLabel_0.X=10
tLabel_0.Y=19
tLabel_0.Width=73
tLabel_0.Height=15
tLabel_0.Text=Ӱ��ȼ�:
tLabel_0.Color=blue
tLabel_0.FontSize=16