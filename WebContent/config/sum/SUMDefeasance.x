#
# TBuilder Config File 
#
# Title:体温单作废提示界面
#
# Company:JavaHis
#
# Author:zjh 2009.08.20
#
# version 1.0
#

<Type=TFrame>
UI.Title=
UI.MenuConfig=
UI.Width=300
UI.Height=200
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sum.SUMDefeasanceControl
UI.item=tLabel_2;defReason;tPanel_2;tLabel_3;OK;CANCLE
UI.layout=null
CANCLE.Type=TButton
CANCLE.X=158
CANCLE.Y=126
CANCLE.Width=81
CANCLE.Height=23
CANCLE.Text=取消
CANCLE.Action=onCancle
OK.Type=TButton
OK.X=50
OK.Y=126
OK.Width=81
OK.Height=23
OK.Text=确定
OK.Action=onOK
tLabel_3.Type=TLabel
tLabel_3.X=7
tLabel_3.Y=99
tLabel_3.Width=284
tLabel_3.Height=4
tLabel_3.Text=
tLabel_3.Border=凸
tLabel_3.AutoCenter=N
tLabel_3.AutoY=N
tLabel_3.AutoX=Y
tLabel_3.AutoWidth=Y
tLabel_3.AutoHeight=N
tPanel_2.Type=TPanel
tPanel_2.X=5
tPanel_2.Y=17
tPanel_2.Width=290
tPanel_2.Height=4
tPanel_2.Border=凸
tPanel_2.AutoWidth=Y
tPanel_2.AutoX=Y
defReason.Type=TTextField
defReason.X=30
defReason.Y=60
defReason.Width=240
defReason.Height=23
defReason.Text=
tLabel_2.Type=TLabel
tLabel_2.X=30
tLabel_2.Y=32
tLabel_2.Width=72
tLabel_2.Height=15
tLabel_2.Text=作废理由: