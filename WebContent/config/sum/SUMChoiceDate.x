#
# TBuilder Config File 
#
# Title:选择日期区间
#
# Company:JavaHis
#
# Author:zjh 2009.07.15
#
# version 1.0
#

<Type=TFrame>
UI.Title=
UI.MenuConfig=
UI.Width=300
UI.Height=200
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sum.GetDateSectionControl
UI.item=tLabel_2;tLabel_3;START_DATE;END_DATE;tPanel_2;tPanel_3;OK;CANCLE
UI.layout=null
CANCLE.Type=TButton
CANCLE.X=169
CANCLE.Y=128
CANCLE.Width=81
CANCLE.Height=23
CANCLE.Text=取 消
CANCLE.Action=onCANCLE
OK.Type=TButton
OK.X=35
OK.Y=128
OK.Width=81
OK.Height=23
OK.Text=打 印
OK.Action=onOK
tPanel_3.Type=TPanel
tPanel_3.X=5
tPanel_3.Y=20
tPanel_3.Width=290
tPanel_3.Height=3
tPanel_3.MoveType=
tPanel_3.Border=凸
tPanel_3.AutoW=Y
tPanel_3.AutoX=Y
tPanel_3.AutoWidth=Y
tPanel_2.Type=TPanel
tPanel_2.X=5
tPanel_2.Y=104
tPanel_2.Width=290
tPanel_2.Height=3
tPanel_2.Border=凸
tPanel_2.AutoWidth=Y
tPanel_2.AutoX=Y
tPanel_2.AutoW=Y
END_DATE.Type=TTextFormat
END_DATE.X=106
END_DATE.Y=65
END_DATE.Width=146
END_DATE.Height=20
END_DATE.Text=TTextFormat
END_DATE.Format=yyyy年MM月dd日
END_DATE.showDownButton=Y
END_DATE.FormatType=date
START_DATE.Type=TTextFormat
START_DATE.X=106
START_DATE.Y=35
START_DATE.Width=146
START_DATE.Height=20
START_DATE.Text=TTextFormat
START_DATE.FormatType=date
START_DATE.Format=yyyy年MM月dd日
START_DATE.showDownButton=Y
tLabel_3.Type=TLabel
tLabel_3.X=34
tLabel_3.Y=69
tLabel_3.Width=72
tLabel_3.Height=15
tLabel_3.Text=结束日期:
tLabel_2.Type=TLabel
tLabel_2.X=34
tLabel_2.Y=37
tLabel_2.Width=72
tLabel_2.Height=15
tLabel_2.Text=起始日期: