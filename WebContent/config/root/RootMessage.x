#
# TBuilder Config File 
#
# Title:在线通讯消息窗口
#
# Company:JavaHis
#
# Author:lzk 2009.04.22
#
# version 1.0
#

<Type=TFrame>
UI.Title=
UI.MenuConfig=
UI.Width=392
UI.Height=343
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.root.RootMessageControl
UI.item=ROOT
UI.layout=null
UI.FocusList=SENDM_ESSAGE;SEND
ROOT.Type=TRootPanel
ROOT.X=0
ROOT.Y=0
ROOT.Width=392
ROOT.Height=343
ROOT.AutoSize=0
ROOT.AutoX=Y
ROOT.AutoY=Y
ROOT.AutoWidth=Y
ROOT.AutoHeight=Y
ROOT.Item=MESSAGE;SENDM_ESSAGE;SEND;tButton_1
tButton_1.Type=TButton
tButton_1.X=303
tButton_1.Y=305
tButton_1.Width=81
tButton_1.Height=23
tButton_1.Text=关闭
tButton_1.Action=onClose
SEND.Type=TButton
SEND.X=216
SEND.Y=305
SEND.Width=81
SEND.Height=23
SEND.Text=发送(S)
SEND.Action=onSend
SENDM_ESSAGE.Type=TTextArea
SENDM_ESSAGE.X=5
SENDM_ESSAGE.Y=216
SENDM_ESSAGE.Width=380
SENDM_ESSAGE.Height=81
SENDM_ESSAGE.SpacingRow=1
SENDM_ESSAGE.RowHeight=20
MESSAGE.Type=TTextArea
MESSAGE.X=5
MESSAGE.Y=24
MESSAGE.Width=380
MESSAGE.Height=190
MESSAGE.SpacingRow=1
MESSAGE.RowHeight=20