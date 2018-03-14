#
# TBuilder Config File 
#
# Title:
#
# Company:javahis
#
# Author:ZangJH 2009.04.09
#
# version 1.0
#

<Type=TFrame>
UI.Title=
UI.MenuConfig=
UI.Width=627
UI.Height=321
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SYSFee_HistoryControl
UI.item=mainTable
UI.layout=null
UI.TopMenu=Y
UI.TopToolBar=Y
mainTable.Type=TTable
mainTable.X=2
mainTable.Y=2
mainTable.Width=623
mainTable.Height=317
mainTable.SpacingRow=1
mainTable.RowHeight=20
mainTable.AutoX=Y
mainTable.AutoY=Y
mainTable.AutoWidth=Y
mainTable.AutoHeight=Y
mainTable.AutoSize=2
mainTable.Header=编码,100;名称,150;生效日期,150;失效日期,150;价格,70
mainTable.AutoModifyDataStore=Y
mainTable.ParmMap=ORDER_CODE;ORDER_DESC;START_DATE;END_DATE;OWN_PRICE
mainTable.DoubleClickedAction=
mainTable.RightClickedAction=
mainTable.ClickedAction=onDClick
mainTable.LockColumns=0,1,2,3,4
mainTable.HorizontalAlignmentData=
mainTable.ColumnHorizontalAlignmentData=0,left;1,left;4,right