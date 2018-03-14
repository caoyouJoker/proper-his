#
# TBuilder Config File 
#
# Title:出生地下拉
#
# Company:JavaHis
#
# Author:fudw 2009.04.30
#
# version 1.0
#

<Type=TFrame>
UI.Title=
UI.MenuConfig=
UI.Width=400
UI.Height=300
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SYSHomePlacePopupControl
UI.item=HOMEPLACE_CODE;TABLE
UI.layout=null
UI.FocusList=HOMEPLACE_CODE
TABLE.Type=TTable
TABLE.X=2
TABLE.Y=26
TABLE.Width=397
TABLE.Height=271
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.AutoSize=0
TABLE.Header=出生地代码,80;出生地,140;拼音,70;备注,80
TABLE.ParmMap=HOMEPLACE_CODE;HOMEPLACE_DESC;PY1;DESCRIPTION
TABLE.AutoModifyDataStore=Y
TABLE.SQL=SELECT * FROM SYS_HOMEPLACE ORDER BY HOMEPLACE_CODE
TABLE.LockColumns=0,1,2,3,4
HOMEPLACE_CODE.Type=TTextField
HOMEPLACE_CODE.X=1
HOMEPLACE_CODE.Y=1
HOMEPLACE_CODE.Width=397
HOMEPLACE_CODE.Height=25
HOMEPLACE_CODE.Text=