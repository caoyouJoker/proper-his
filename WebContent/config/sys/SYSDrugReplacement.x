<Type=TFrame>
UI.Title=替代药
UI.MenuConfig=%ROOT%\config\sys\SYSDrugReplacementMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=
UI.item=tPanel_0;TABLE
UI.layout=null
UI.Y=10
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=1024
tPanel_0.Height=108
tPanel_0.Item=tLabel_18;ORDER_CODE;ORDER_DESC;tLabel_0;REPLACE_ORDER_CODE;REPLACING_ORDER_DESC;tLabel_23;DESCRIPTION;s;SEQ
tPanel_0.ControlClassName=
tPanel_0.MenuConfig=%ROOT%\config\sys\SYS_RegionMenu.x
tPanel_0.Border=凸
SEQ.Type=TNumberTextField
SEQ.X=580
SEQ.Y=15
SEQ.Width=77
SEQ.Height=20
SEQ.Text=0
SEQ.Format=#########0
s.Type=TLabel
s.X=528
s.Y=18
s.Width=44
s.Height=15
s.Text=序号:
DESCRIPTION.Type=TTextField
DESCRIPTION.X=128
DESCRIPTION.Y=74
DESCRIPTION.Width=388
DESCRIPTION.Height=20
DESCRIPTION.Text=
DESCRIPTION.Enabled=Y
tLabel_23.Type=TLabel
tLabel_23.X=17
tLabel_23.Y=77
tLabel_23.Width=43
tLabel_23.Height=15
tLabel_23.Text=备注：
REPLACING_ORDER_DESC.Type=TTextField
REPLACING_ORDER_DESC.X=256
REPLACING_ORDER_DESC.Y=45
REPLACING_ORDER_DESC.Width=260
REPLACING_ORDER_DESC.Height=20
REPLACING_ORDER_DESC.Text=
REPLACING_ORDER_DESC.Enabled=N
REPLACE_ORDER_CODE.Type=TTextField
REPLACE_ORDER_CODE.X=128
REPLACE_ORDER_CODE.Y=45
REPLACE_ORDER_CODE.Width=117
REPLACE_ORDER_CODE.Height=20
REPLACE_ORDER_CODE.Text=
tLabel_0.Type=TLabel
tLabel_0.X=18
tLabel_0.Y=48
tLabel_0.Width=113
tLabel_0.Height=15
tLabel_0.Text=替代医嘱:
TABLE.Type=TTable
TABLE.X=5
TABLE.Y=115
TABLE.Width=1024
TABLE.Height=206
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.Header=序号,60;医嘱代码,160;医嘱名称,160;替代医嘱,160;替代医嘱名称,160;备注,160;操作人员,160;操作日期,160
TABLE.StringData=string,string,string,string,string,string,string,string,string,string
TABLE.LockColumns=0,1,2,3,4,5,6,7,8,9
TABLE.AutoX=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.AutoSize=5
ORDER_DESC.Type=TTextField
ORDER_DESC.X=256
ORDER_DESC.Y=15
ORDER_DESC.Width=260
ORDER_DESC.Height=20
ORDER_DESC.Text=
ORDER_DESC.Enabled=N
ORDER_CODE.Type=TTextField
ORDER_CODE.X=128
ORDER_CODE.Y=15
ORDER_CODE.Width=117
ORDER_CODE.Height=20
ORDER_CODE.Text=
tLabel_18.Type=TLabel
tLabel_18.X=18
tLabel_18.Y=17
tLabel_18.Width=84
tLabel_18.Height=17
tLabel_18.Text=医嘱代码: