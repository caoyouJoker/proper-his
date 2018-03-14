#
# TBuilder Config File 
#
# Title:操作员检核
#
# Company:JavaHis
#
# Author:zhangy 2010.03.30
#
# version 1.0
#

<Type=TFrame>
UI.Title=使用者确认
UI.MenuConfig=
UI.Width=300
UI.Height=186
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.phl.PHLOPTCheckControl
UI.item=tLabel_0;tLabel_1;USER_ID;USER_PASSWORD;BTN_OK;BTN_CANCEL
UI.layout=null
UI.FocusList=USER_ID;USER_PASSWORD
BTN_CANCEL.Type=TButton
BTN_CANCEL.X=156
BTN_CANCEL.Y=112
BTN_CANCEL.Width=81
BTN_CANCEL.Height=28
BTN_CANCEL.Text=取消
BTN_CANCEL.Action=onBtnCancel
BTN_OK.Type=TButton
BTN_OK.X=43
BTN_OK.Y=112
BTN_OK.Width=81
BTN_OK.Height=27
BTN_OK.Text=确定
BTN_OK.Action=onBtnOK
USER_PASSWORD.Type=TPasswordField
USER_PASSWORD.X=116
USER_PASSWORD.Y=59
USER_PASSWORD.Width=150
USER_PASSWORD.Height=20
USER_PASSWORD.Text=
USER_ID.Type=TTextField
USER_ID.X=116
USER_ID.Y=22
USER_ID.Width=150
USER_ID.Height=20
USER_ID.Text=
tLabel_1.Type=TLabel
tLabel_1.X=27
tLabel_1.Y=62
tLabel_1.Width=71
tLabel_1.Height=15
tLabel_1.Text=密    码:
tLabel_0.Type=TLabel
tLabel_0.X=27
tLabel_0.Y=25
tLabel_0.Width=69
tLabel_0.Height=15
tLabel_0.Text=用    户: