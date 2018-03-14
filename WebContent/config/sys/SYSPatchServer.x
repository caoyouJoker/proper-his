#
# TBuilder Config File 
#
# Title:批次程序服务器设置
#
# Company:JavaHis
#
# Author:zhangy 2009.07.22
#
# version 1.0
#

<Type=TFrame>
UI.Title=参数设定
UI.MenuConfig=%ROOT%\config\sys\SYSPatchServerMenu.x
UI.Width=250
UI.Height=300
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SYSPatchServerControl
UI.item=tLabel_0;tLabel_1;SleepTime;ExpiredTime;tLabel_2;tLabel_3
UI.layout=null
tLabel_3.Type=TLabel
tLabel_3.X=191
tLabel_3.Y=99
tLabel_3.Width=30
tLabel_3.Height=15
tLabel_3.Text=毫秒
tLabel_2.Type=TLabel
tLabel_2.X=191
tLabel_2.Y=43
tLabel_2.Width=30
tLabel_2.Height=15
tLabel_2.Text=毫秒
ExpiredTime.Type=TNumberTextField
ExpiredTime.X=108
ExpiredTime.Y=96
ExpiredTime.Width=77
ExpiredTime.Height=20
ExpiredTime.Text=0
ExpiredTime.Format=#########0
SleepTime.Type=TNumberTextField
SleepTime.X=108
SleepTime.Y=40
SleepTime.Width=77
SleepTime.Height=20
SleepTime.Text=0
SleepTime.Format=#########0
tLabel_1.Type=TLabel
tLabel_1.X=9
tLabel_1.Y=99
tLabel_1.Width=100
tLabel_1.Height=15
tLabel_1.Text=过期扫描时间:
tLabel_0.Type=TLabel
tLabel_0.X=9
tLabel_0.Y=43
tLabel_0.Width=72
tLabel_0.Height=15
tLabel_0.Text=休眠时间: