<Type=TFrame>
UI.Width=400
UI.Height=300
UI.layout=null
UI.bkcolor=160,220,230
UI.Item=tPanel_0
UI.ControlClassName=com.javahis.ui.sys.DevStockDDPopupControl
UI.FocusList=EDIT
tPanel_0.Type=TPanel
tPanel_0.X=0
tPanel_0.Y=0
tPanel_0.Width=400
tPanel_0.Height=300
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoHeight=Y
tPanel_0.AutoWidth=Y
tPanel_0.Border=凸
tPanel_0.AutoSize=0
tPanel_0.Item=EDIT;TABLE;tLabel_0;tLabel_1
tPanel_0.ControlClassName=
DEPT_CODE.Type=科室
DEPT_CODE.X=2
DEPT_CODE.Y=25
DEPT_CODE.Width=81
DEPT_CODE.Height=23
DEPT_CODE.Text=
DEPT_CODE.HorizontalAlignment=2
DEPT_CODE.PopupMenuHeader=ID,100;NAME,100
DEPT_CODE.PopupMenuWidth=300
DEPT_CODE.PopupMenuHeight=300
DEPT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
DEPT_CODE.FormatType=combo
DEPT_CODE.ShowDownButton=Y
DEPT_CODE.Tip=科室
DEPT_CODE.ShowColumnList=NAME
tLabel_1.Type=TLabel
tLabel_1.X=378
tLabel_1.Y=4
tLabel_1.Width=16
tLabel_1.Height=14
tLabel_1.Text=
tLabel_1.PictureName=sys.gif
tLabel_1.CursorType=12
tLabel_1.Action=onResetFile
tLabel_0.Type=TLabel
tLabel_0.X=359
tLabel_0.Y=4
tLabel_0.Width=16
tLabel_0.Height=14
tLabel_0.Text=
tLabel_0.PictureName=table.gif
tLabel_0.CursorType=12
tLabel_0.Action=onResetDW
TABLE.Type=TTable
TABLE.X=2
TABLE.Y=23
TABLE.Width=200
TABLE.Height=273
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoSize=2
TABLE.AutoX=Y
TABLE.AutoY=N
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.LocalTableName=
TABLE.LockColumns=all
TABLE.ShowCount=20
TABLE.SQL=
TABLE.Header=出库科室,100,DEPT_CODE;设备编码,100;设备名称,200;批号,100;设备序号,70;库存量,80
TABLE.ParmMap=DEPT_CODE;DEV_CODE;DEV_CHN_DESC;BATCH_SEQ;DEVSEQ_NO;QTY
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,left
TABLE.AutoModifyDataStore=N
TABLE.Item=DEPT_CODE
EDIT.Type=TTextField
EDIT.X=2
EDIT.Y=2
EDIT.Width=353
EDIT.Height=20
EDIT.Text=
EDIT.FocusLostAction=grabFocus