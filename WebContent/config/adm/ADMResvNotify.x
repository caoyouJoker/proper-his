#####################################################
# <p>Title:住院通知 </p>
#
# <p>Description: 住院通知</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK
# @version 1.0
#####################################################
<Type=TFrame>
UI.Title=住院通知
UI.MenuConfig=
UI.Width=400
UI.Height=300
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.adm.ADMResvNotifyControl
UI.item=tLabel_26;NOTIFY_DATE;tLabel_27;NOTIFY_DATE;tButton_0;tButton_2;NOTIFY_TIMES
UI.layout=null
UI.TopMenu=Y
UI.ShowTitle=N
UI.ShowMenu=N
UI.TopToolBar=Y
NOTIFY_TIMES.Type=TNumberTextField
NOTIFY_TIMES.X=135
NOTIFY_TIMES.Y=44
NOTIFY_TIMES.Width=77
NOTIFY_TIMES.Height=20
NOTIFY_TIMES.Text=0
NOTIFY_TIMES.Format=#########0
NOTIFY_TIMES.Enabled=N
tButton_2.Type=TButton
tButton_2.X=202
tButton_2.Y=155
tButton_2.Width=81
tButton_2.Height=23
tButton_2.Text=取消
tButton_2.Action=onCancel
tButton_0.Type=TButton
tButton_0.X=74
tButton_0.Y=154
tButton_0.Width=81
tButton_0.Height=23
tButton_0.Text=通知
tButton_0.Action=onNotify
NOTIFY_DATE.Type=TTextFormat
NOTIFY_DATE.X=135
NOTIFY_DATE.Y=88
NOTIFY_DATE.Width=168
NOTIFY_DATE.Height=20
NOTIFY_DATE.Text=TTextField
NOTIFY_DATE.FormatType=date
NOTIFY_DATE.Format=yyyy/MM/dd HH:mm:ss
NOTIFY_DATE.showDownButton=Y
NOTIFY_DATE.Enabled=N
tLabel_27.Type=TLabel
tLabel_27.X=49
tLabel_27.Y=90
tLabel_27.Width=72
tLabel_27.Height=15
tLabel_27.Text=通知日期：
tLabel_26.Type=TLabel
tLabel_26.X=48
tLabel_26.Y=47
tLabel_26.Width=72
tLabel_26.Height=15
tLabel_26.Text=通知次数：