#
# TBuilder Config File 
#
# Title:设备盘点
#
# Company:JavaHis
#
# Author:WangM 2010.12.18
#
# version 1.0
#

<Type=TFrame>
UI.Title=设备盘点
UI.MenuConfig=%ROOT%\config\dev\DevInventoryMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=
UI.item=tPanel_0;tPanel_1
UI.layout=null
tPanel_1.Type=TPanel
tPanel_1.X=5
tPanel_1.Y=227
tPanel_1.Width=1014
tPanel_1.Height=516
tPanel_1.AutoWidth=Y
tPanel_1.AutoHeight=Y
tPanel_1.Border=组|盘点明细
tPanel_1.AutoY=N
tPanel_1.Item=tTable_0
tTable_0.Type=TTable
tTable_0.X=60
tTable_0.Y=50
tTable_0.Width=992
tTable_0.Height=81
tTable_0.SpacingRow=1
tTable_0.RowHeight=20
tTable_0.AutoX=Y
tTable_0.AutoY=Y
tTable_0.AutoWidth=Y
tTable_0.AutoHeight=Y
tTable_0.Header=删,40;属性,100;编号,100;设备名称,120;设备规格,120;序号,80;账面数量,100;盘点数量,100;依附主设备,100;使用科室,100;盘点结果,100;备注,180
tPanel_0.Type=TPanel
tPanel_0.X=8
tPanel_0.Y=5
tPanel_0.Width=1011
tPanel_0.Height=226
tPanel_0.Border=组|基本信息
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoWidth=Y