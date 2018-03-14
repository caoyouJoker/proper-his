#
# TBuilder Config File 
#
# Title:厂商同步表基本档
#
# Company:JavaHis
#
# Author:WangM 2010.03.09
#
# version 1.0
#

<Type=TFrame>
UI.Title=厂商同步表信息
UI.MenuConfig=%ROOT%\config\sys\SYSIOTABLEUI_Menu.x
UI.Width=450
UI.Height=400
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SysIOTableControl
UI.item=TABLE1;IO_CODE;TABLE_NAME
UI.layout=null
TABLE_NAME.Type=编码同步字典下拉区域
TABLE_NAME.X=108
TABLE_NAME.Y=19
TABLE_NAME.Width=94
TABLE_NAME.Height=23
TABLE_NAME.Text=
TABLE_NAME.HorizontalAlignment=2
TABLE_NAME.PopupMenuHeader=代码,100;名称,100
TABLE_NAME.PopupMenuWidth=300
TABLE_NAME.PopupMenuHeight=300
TABLE_NAME.PopupMenuFilter=ID,1;NAME,1;PY1,1
TABLE_NAME.FormatType=combo
TABLE_NAME.ShowDownButton=Y
TABLE_NAME.Tip=编码同步字典
TABLE_NAME.ShowColumnList=NAME
IO_CODE.Type=统一编码厂商下拉区域
IO_CODE.X=8
IO_CODE.Y=19
IO_CODE.Width=95
IO_CODE.Height=23
IO_CODE.Text=
IO_CODE.HorizontalAlignment=2
IO_CODE.PopupMenuHeader=代码,100;名称,100
IO_CODE.PopupMenuWidth=300
IO_CODE.PopupMenuHeight=300
IO_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
IO_CODE.FormatType=combo
IO_CODE.ShowDownButton=Y
IO_CODE.Tip=统一编码厂商
IO_CODE.ShowColumnList=NAME
TABLE1.Type=TTable
TABLE1.X=59
TABLE1.Y=5
TABLE1.Width=440
TABLE1.Height=390
TABLE1.SpacingRow=1
TABLE1.RowHeight=20
TABLE1.AutoY=Y
TABLE1.AutoX=Y
TABLE1.AutoWidth=Y
TABLE1.AutoHeight=Y
TABLE1.Header=厂商代码,100,IO_CODE;表名,180,TABLE_NAME;读,40;写,40;监,40
TABLE1.Item=IO_CODE;TABLE_NAME
TABLE1.ParmMap=IO_CODE;TABLE_NAME;READ;WRITE;LISTEN
TABLE1.ColumnHorizontalAlignmentData=0,Left;1,Left
TABLE1.LockColumns=0
TABLE1.AutoModifyDataStore=Y