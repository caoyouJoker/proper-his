#
# TBuilder Config File 
#
# Title:医嘱套餐选择界面，供外部调用
#
# Company:JAVAHIS
#
# Author:ZangJH 2009.04.24
#
# version 1.0
#

<Type=TFrame>
UI.Title=
UI.MenuConfig=
UI.Width=770
UI.Height=600
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.sysFeeOrdPackOptionControl
UI.item=TABLE
UI.layout=null
TABLE.Type=TTable
TABLE.X=3
TABLE.Y=6
TABLE.Width=766
TABLE.Height=592
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoY=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.AutoSize=2
TABLE.Header=选,30,boolean;项目名称,200;用量,70;单位,70,UNIT;,10;选,30,boolean;项目名称,200;用量,70;单位,70,UNIT
TABLE.ParmMap=N_SEL;N_ORDER_DESC;N_DOSAGE_QTY;N_DOSAGE_UNIT;BLANK;S_SEL;S_ORDER_DESC;S_DOSAGE_QTY;S_DOSAGE_UNIT
TABLE.Item=UNIT
TABLE.LockColumns=1,3,4,6,8