#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sundx 2010.04.06
#
# version 1.0
#

<Type=TFrame>
UI.Title=叫号窗口
UI.MenuConfig=
UI.Width=254
UI.Height=175
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.pha.PHACallNoControl
UI.item=tLabel_15;tButton_0;tButton_1;SERIAL_NO
UI.layout=null
SERIAL_NO.Type=TNumberTextField
SERIAL_NO.X=84
SERIAL_NO.Y=38
SERIAL_NO.Width=138
SERIAL_NO.Height=20
SERIAL_NO.Text=0
SERIAL_NO.Format=#########0
tButton_1.Type=TButton
tButton_1.X=153
tButton_1.Y=89
tButton_1.Width=81
tButton_1.Height=23
tButton_1.Text=取消
tButton_1.Action=onCancel
tButton_0.Type=TButton
tButton_0.X=23
tButton_0.Y=89
tButton_0.Width=81
tButton_0.Height=23
tButton_0.Text=确定
tButton_0.Action=onConfirm
tLabel_15.Type=TLabel
tLabel_15.X=30
tLabel_15.Y=40
tLabel_15.Width=53
tLabel_15.Height=15
tLabel_15.Text=领药号