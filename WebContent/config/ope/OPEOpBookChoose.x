#############################################
# <p>Title:手术预约及相关手术记录列表 </p>
#
# <p>Description:手术预约及相关手术记录列表 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.28
# @version 4.0
#############################################
<Type=TFrame>
UI.Title=手术预约及相关手术记录列表
UI.MenuConfig=%ROOT%\config\ope\OPEOpBookChooseMenu.x
UI.Width=400
UI.Height=360
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ope.OPEOpBookChooseControl
UI.Item=tPanel_0;tPanel_1
UI.TopMenu=Y
UI.TopToolBar=Y
UI.ShowTitle=N
UI.ShowMenu=N
UI.Opaque=Y
UI.zhTitle=手术预约及相关手术记录列表
UI.enTitle=Op Appoint & Op Record List
tPanel_1.Type=TPanel
tPanel_1.X=5
tPanel_1.Y=130
tPanel_1.Width=390
tPanel_1.Height=120
tPanel_1.Border=组|手术记录|Op Record
tPanel_1.AutoX=Y
tPanel_1.AutoWidth=Y
tPanel_1.Item=Table2
Table2.Type=TTable
Table2.X=60
Table2.Y=28
Table2.Width=81
Table2.Height=81
Table2.SpacingRow=1
Table2.RowHeight=20
Table2.AutoY=Y
Table2.AutoHeight=Y
Table2.AutoWidth=Y
Table2.AutoX=Y
Table2.Header=手术记录单号,120;手术日期,160
Table2.ParmMap=OP_RECORD_NO;OP_DATE
Table2.LockColumns=ALL
Table2.ColumnHorizontalAlignmentData=0,left;1,left
Table2.enHeader=Op Record No;Op Date
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=390
tPanel_0.Height=120
tPanel_0.Border=组|手术申请|Op Apply
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoWidth=Y
tPanel_0.Item=Table1
Table1.Type=TTable
Table1.X=21
Table1.Y=23
Table1.Width=81
Table1.Height=81
Table1.SpacingRow=1
Table1.RowHeight=20
Table1.AutoX=Y
Table1.AutoY=Y
Table1.AutoWidth=Y
Table1.AutoHeight=Y
Table1.Header=手术申请单号,120;手术日期,160,timestamp
Table1.ParmMap=OPBOOK_SEQ;OP_DATE
Table1.ColumnHorizontalAlignmentData=0,left;1,left
Table1.ClickedAction=onTable1Selected
Table1.LockColumns=ALL
Table1.enHeader=App No;Op Date