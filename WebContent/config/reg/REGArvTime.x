<Type=TFrame>
UI.Title=��Ժʱ�䴰��
UI.MenuConfig=%ROOT%\config\reg\REGArvTimeMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.reg.REGArvTimeControl
UI.item=tPanel_0;Table
UI.layout=null
UI.BKColor=161,220,230
UI.FocusList=QUE_GROUP;START_TIME;INTERAL_TIME
Table.Type=TTable
Table.X=6
Table.Y=140
Table.Width=562
Table.Height=481
Table.SpacingRow=1
Table.RowHeight=20
Table.AutoX=Y
Table.AutoWidth=Y
Table.AutoHeight=Y
Table.AutoSize=10
Table.LockColumns=2,3,4,5,6,7
Table.StringData=
Table.Header=�������,200,QUE_GROUP;��ʼʱ��,100;���ʱ��,100;������Ա,100;��������,100;�����ն�,100
Table.ParmMap=QUE_GROUP;START_TIME;INTERAL_TIME;OPT_USER;OPT_DATE;OPT_TERM
Table.Item=QUE_GROUP
Table.ColumnHorizontalAlignmentData=0,left;1,right;2,right;3,left;4,right;5,right
Table.SQL=
Table.AutoModifyDataStore=Y
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=949
tPanel_0.Height=129
tPanel_0.Item=tLabel_8;tLabel_9;tLabel_12;QUE_GROUP;tLabel_7;tLabel_3;tLabel_10;START_TIME;INTERAL_TIME;add;save;delete;sel
tPanel_0.Enabled=Y
tPanel_0.Border=͹
tPanel_0.BKColor=161,220,230
tPanel_0.AutoWidth=Y
sel.Type=TButton
sel.X=326
sel.Y=12
sel.Width=81
sel.Height=23
sel.Text=sel
sel.Action=TABLE|retrieve
delete.Type=TButton
delete.X=455
delete.Y=96
delete.Width=81
delete.Height=23
delete.Text=delete
delete.Action=TABLE|removeRow
save.Type=TButton
save.X=440
save.Y=60
save.Width=81
save.Height=23
save.Text=save
save.Action=TABLE|update
add.Type=TButton
add.X=428
add.Y=21
add.Width=81
add.Height=23
add.Text=add
add.Action=TABLE|addrow
INTERAL_TIME.Type=TNumberTextField
INTERAL_TIME.X=233
INTERAL_TIME.Y=66
INTERAL_TIME.Width=77
INTERAL_TIME.Height=20
INTERAL_TIME.Text=
INTERAL_TIME.Format=#0
INTERAL_TIME.Tip=���ʱ��
START_TIME.Type=TTextField
START_TIME.X=90
START_TIME.Y=66
START_TIME.Width=66
START_TIME.Height=20
START_TIME.Text=
tLabel_10.Type=TLabel
tLabel_10.X=157
tLabel_10.Y=69
tLabel_10.Width=12
tLabel_10.Height=15
tLabel_10.Text=*
tLabel_10.Color=��
tLabel_3.Type=TLabel
tLabel_3.X=248
tLabel_3.Y=25
tLabel_3.Width=72
tLabel_3.Height=15
tLabel_3.Text=*
tLabel_3.Color=red
tLabel_7.Type=TLabel
tLabel_7.X=314
tLabel_7.Y=69
tLabel_7.Width=37
tLabel_7.Height=15
tLabel_7.Text=����
QUE_GROUP.Type=TComboBox
QUE_GROUP.X=90
QUE_GROUP.Y=22
QUE_GROUP.Width=151
QUE_GROUP.Height=22
QUE_GROUP.Text=TButton
QUE_GROUP.showID=Y
QUE_GROUP.Editable=Y
QUE_GROUP.ParmMap=id:ID;text:NAME
QUE_GROUP.TableShowList=text
QUE_GROUP.ModuleMethodName=getqueGropCombo
QUE_GROUP.ModuleName=reg\REGQueMethodModule.x
tLabel_12.Type=TLabel
tLabel_12.X=171
tLabel_12.Y=69
tLabel_12.Width=71
tLabel_12.Height=15
tLabel_12.Text=���ʱ��:
tLabel_9.Type=TLabel
tLabel_9.X=20
tLabel_9.Y=69
tLabel_9.Width=71
tLabel_9.Height=15
tLabel_9.Text=��ʼʱ��:
tLabel_9.Color=��
tLabel_8.Type=TLabel
tLabel_8.X=20
tLabel_8.Y=26
tLabel_8.Width=71
tLabel_8.Height=15
tLabel_8.Text=�������:
tLabel_8.Color=��