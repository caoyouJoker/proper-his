# 
#  Title:中间档173病种维护单档
# 
#  Description:中间档173病种维护单档
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.6.2
#  version 1.0
#
<Type=TFrame>
UI.Title=173病种
UI.MenuConfig=%ROOT%\config\sta\STA173ListMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sta.STA173ListControl
UI.Item=tPanel_0;TABLE
UI.TopMenu=Y
UI.TopToolBar=Y
UI.ShowTitle=N
UI.ShowMenu=N
TABLE.Type=TTable
TABLE.X=110
TABLE.Y=110
TABLE.Width=81
TABLE.Height=633
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.Header=病种序号,100;病种分类名称,200;条件,300;操作人员,100;操作时间,120;操作终端,120
TABLE.ParmMap=SEQ;ICD_DESC;CONDITION;OPT_USER;OPT_DATE;OPT_TERM
TABLE.LockColumns=all
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,left
TABLE.ClickedAction=
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=1014
tPanel_0.Height=100
tPanel_0.Border=凸
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoWidth=Y
tPanel_0.Item=tLabel_0;SEQ;tLabel_1;ICD_DESC;tLabel_2;CONDITION
CONDITION.Type=TTextField
CONDITION.X=116
CONDITION.Y=66
CONDITION.Width=600
CONDITION.Height=20
CONDITION.Text=
tLabel_2.Type=TLabel
tLabel_2.X=42
tLabel_2.Y=70
tLabel_2.Width=60
tLabel_2.Height=15
tLabel_2.Text=条    件
ICD_DESC.Type=TTextField
ICD_DESC.X=116
ICD_DESC.Y=39
ICD_DESC.Width=300
ICD_DESC.Height=20
ICD_DESC.Text=
tLabel_1.Type=TLabel
tLabel_1.X=42
tLabel_1.Y=43
tLabel_1.Width=64
tLabel_1.Height=15
tLabel_1.Text=病种名称
SEQ.Type=TTextField
SEQ.X=116
SEQ.Y=12
SEQ.Width=100
SEQ.Height=20
SEQ.Text=
tLabel_0.Type=TLabel
tLabel_0.X=42
tLabel_0.Y=16
tLabel_0.Width=64
tLabel_0.Height=15
tLabel_0.Text=病种序号
tLabel_0.Color=蓝