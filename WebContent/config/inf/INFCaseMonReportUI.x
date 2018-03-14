#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sundx 2009.12.07
#
# version 1.0
#

<Type=TFrame>
UI.Title=感染病例月报表
UI.MenuConfig=%ROOT%\config\inf\INFCaseMonReportMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.inf.INFCaseMonReportControl
UI.item=DATE;tLabel_70;TABLE
UI.layout=null
UI.Name=
UI.TopMenu=Y
UI.TopToolBar=Y
UI.ShowMenu=Y
TABLE.Type=TTable
TABLE.X=18
TABLE.Y=86
TABLE.Width=996
TABLE.Height=652
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.Header=科别,120;病历总数,120;感染例数,120;感染率(%),120;构成比(%),120;实报感染例数,120;实报感染率,120;漏报数,120;漏报率(%),120;送检例数,120;送检率(%),120
TABLE.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11
TABLE.ParmMap=DEPT_CHN_DESC;COUNT;INF_COUNT;INF_RATE;CONSTRUCT_RATE;REPORT_COUNT;REPORT_RATE;NO_REPORT_COUNT;NO_REPORT_RATE;ETIOLGEXM_COUNT;ETIOLGEXM_RATE
TABLE.ColumnHorizontalAlignmentData=0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right
tLabel_70.Type=TLabel
tLabel_70.X=22
tLabel_70.Y=38
tLabel_70.Width=63
tLabel_70.Height=15
tLabel_70.Text=检测年月:
DATE.Type=TTextFormat
DATE.X=86
DATE.Y=36
DATE.Width=141
DATE.Height=20
DATE.Text=TTextFormat
DATE.FormatType=date
DATE.Format=yyyy/MM
DATE.showDownButton=Y
DATE.Name=