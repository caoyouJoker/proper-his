#
# TBuilder Config File 
#
# Title: 批次解锁
#
# Company:JavaHis
#
# Author:zhangy 2009.04.20
#
# version 1.0
#

<Type=TFrame>
UI.Title=批次解锁
UI.MenuConfig=%ROOT%\config\ind\INDBatchLockMenu.x
UI.Width=322
UI.Height=453
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ind.IndBatchLockControl
UI.item=tPanel_1
UI.layout=null
UI.ShowMenu=N
UI.ShowTitle=N
UI.TopToolBar=Y
UI.TopMenu=Y
UI.Text=批次解锁
UI.Tip=批次解锁档
UI.X=5
UI.AutoX=N
tPanel_1.Type=TPanel
tPanel_1.X=5
tPanel_1.Y=5
tPanel_1.Width=312
tPanel_1.Height=443
tPanel_1.Border=组
tPanel_1.AutoX=Y
tPanel_1.AutoY=Y
tPanel_1.AutoSize=5
tPanel_1.Item=SELECT_ALL;TABLE
tPanel_1.AutoWidth=Y
tPanel_1.AutoHeight=Y
TABLE.Type=TTable
TABLE.X=70
TABLE.Y=40
TABLE.Width=280
TABLE.Height=397
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoY=N
TABLE.AutoWidth=Y
TABLE.AutoHeight=N
TABLE.AutoSize=0
TABLE.StringData=
TABLE.Header=执行,60,boolean;药库部门,150
TABLE.LockColumns=1
TABLE.AutoModifyDataStore=Y
TABLE.Item=
TABLE.ColumnHorizontalAlignmentData=1,left
TABLE.ParmMap=BATCH_FLG;ORG_CHN_DESC
SELECT_ALL.Type=TCheckBox
SELECT_ALL.X=15
SELECT_ALL.Y=15
SELECT_ALL.Width=81
SELECT_ALL.Height=23
SELECT_ALL.Text=全选
SELECT_ALL.Action=onSelectAll
SELECT_ALL.Selected=Y