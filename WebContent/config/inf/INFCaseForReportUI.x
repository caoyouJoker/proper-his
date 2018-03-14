#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sundx 2009.12.11
#
# version 1.0
#

<Type=TFrame>
UI.Title=感染情况分析表
UI.MenuConfig=%ROOT%\config\inf\INFCaseForReportMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.inf.INFCaseForReportControl
UI.item=tLabel_0;INF_DATE;TABLE
UI.layout=null
UI.TopToolBar=Y
UI.TopMenu=Y
UI.ShowMenu=Y
TABLE.Type=TTable
TABLE.X=16
TABLE.Y=85
TABLE.Width=995
TABLE.Height=650
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.Header=科室,120;出院人数,100;感染人数,100;感染次数,100;上呼吸道,100;下呼吸道,100;泌尿道,100;手术切口感染(深部、浅部),100;胃肠道,100;血液,100;皮肤和软组织,100;产褥,100;颅内、器官和腔隙,100;胸腔,100;腹腔,100;血管相关性,100;其他(请注明部位),100;直接,100;间接,100;无关,100
TABLE.LockRows=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19
TABLE.ColumnHorizontalAlignmentData=0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right
TABLE.ParmMap=DEPT_DESC;DS_COUNT;INF_PAT_COUNT;INF_COUNT;POSITION_01;POSITION_02;POSITION_03;POSITION_04;POSITION_05;POSITION_06;POSITION_07;POSITION_08;POSITION_09;POSITION_10;POSITION_11;POSITION_12;POSITION_13;DIE_04;DIE_02;DIE_01
INF_DATE.Type=TTextFormat
INF_DATE.X=86
INF_DATE.Y=36
INF_DATE.Width=126
INF_DATE.Height=20
INF_DATE.Text=
INF_DATE.FormatType=date
INF_DATE.Format=yyyy/MM
INF_DATE.showDownButton=Y
tLabel_0.Type=TLabel
tLabel_0.X=20
tLabel_0.Y=39
tLabel_0.Width=64
tLabel_0.Height=15
tLabel_0.Text=检测年月: