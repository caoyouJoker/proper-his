#
# TBuilder Config File 
#
# Title:����������
#
# Company:JavaHis
#
# Author:zhangy 2009.10.17
#
# version 1.0
#

<Type=TFrame>
UI.Title=����������
UI.MenuConfig=%ROOT%\config\phl\PHLRegionMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.phl.PHLRegionControl
UI.item=tPanel_10;tPanel_11
UI.layout=null
UI.TopMenu=Y
UI.TopToolBar=Y
UI.FocusList=REGION_CODE;REGION_DESC;PY1;PY2;START_IP;END_IP;DESCRIPTION
tPanel_11.Type=TPanel
tPanel_11.X=5
tPanel_11.Y=74
tPanel_11.Width=1014
tPanel_11.Height=669
tPanel_11.Border=��
tPanel_11.AutoX=Y
tPanel_11.AutoWidth=Y
tPanel_11.AutoHeight=Y
tPanel_11.Item=TABLE
TABLE.Type=TTable
TABLE.X=54
TABLE.Y=2
TABLE.Width=1010
TABLE.Height=665
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoSize=0
TABLE.AutoX=Y
TABLE.AutoY=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.Header=�������,120;��������,150;ƴ����,80;������,80;��ʼIP,120;����IP,120;��ע,200
TABLE.LockColumns=all
TABLE.ClickedAction=onTableClicked
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left
TABLE.ParmMap=REGION_CODE;REGION_DESC;PY1;PY2;START_IP;END_IP;DESCRIPTION
tPanel_10.Type=TPanel
tPanel_10.X=5
tPanel_10.Y=5
tPanel_10.Width=1014
tPanel_10.Height=67
tPanel_10.AutoY=Y
tPanel_10.AutoX=Y
tPanel_10.AutoWidth=Y
tPanel_10.Border=��
tPanel_10.Item=tLabel_18;tLabel_19;tLabel_20;tLabel_21;tLabel_22;tLabel_23;tLabel_24;REGION_CODE;START_IP;REGION_DESC;END_IP;PY1;DESCRIPTION;PY2
PY2.Type=TTextField
PY2.X=727
PY2.Y=8
PY2.Width=120
PY2.Height=20
PY2.Text=
DESCRIPTION.Type=TTextField
DESCRIPTION.X=516
DESCRIPTION.Y=38
DESCRIPTION.Width=200
DESCRIPTION.Height=20
DESCRIPTION.Text=
PY1.Type=TTextField
PY1.X=516
PY1.Y=8
PY1.Width=120
PY1.Height=20
PY1.Text=
END_IP.Type=TTextField
END_IP.X=301
END_IP.Y=38
END_IP.Width=120
END_IP.Height=20
END_IP.Text=
REGION_DESC.Type=TTextField
REGION_DESC.X=301
REGION_DESC.Y=8
REGION_DESC.Width=120
REGION_DESC.Height=20
REGION_DESC.Text=
REGION_DESC.Action=onRegionDescAction
START_IP.Type=TTextField
START_IP.X=83
START_IP.Y=38
START_IP.Width=120
START_IP.Height=20
START_IP.Text=
REGION_CODE.Type=TTextField
REGION_CODE.X=83
REGION_CODE.Y=8
REGION_CODE.Width=120
REGION_CODE.Height=20
REGION_CODE.Text=
tLabel_24.Type=TLabel
tLabel_24.X=455
tLabel_24.Y=40
tLabel_24.Width=54
tLabel_24.Height=15
tLabel_24.Text=��ע
tLabel_23.Type=TLabel
tLabel_23.X=229
tLabel_23.Y=40
tLabel_23.Width=72
tLabel_23.Height=15
tLabel_23.Text=����IP
tLabel_22.Type=TLabel
tLabel_22.X=12
tLabel_22.Y=40
tLabel_22.Width=72
tLabel_22.Height=15
tLabel_22.Text=��ʼIP
tLabel_21.Type=TLabel
tLabel_21.X=667
tLabel_21.Y=12
tLabel_21.Width=60
tLabel_21.Height=15
tLabel_21.Text=������
tLabel_20.Type=TLabel
tLabel_20.X=455
tLabel_20.Y=12
tLabel_20.Width=62
tLabel_20.Height=15
tLabel_20.Text=ƴ����
tLabel_19.Type=TLabel
tLabel_19.X=229
tLabel_19.Y=12
tLabel_19.Width=72
tLabel_19.Height=15
tLabel_19.Text=��������
tLabel_18.Type=TLabel
tLabel_18.X=12
tLabel_18.Y=12
tLabel_18.Width=72
tLabel_18.Height=15
tLabel_18.Text=�������
tLabel_18.Color=blue