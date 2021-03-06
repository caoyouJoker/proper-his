<Type=TFrame>
UI.Title=药品剂型管理
UI.MenuConfig=%ROOT%\config\sys\SYSPhaDosMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SYSPhaDoseControl
UI.Item=tPanel_1;TABLE
UI.FocusList=DOSE_CODE;DOSE_CHN_DESC;ENG_DESC;PY1;PY2;DOSE_ENG_DESC;DESCRIPTION;DESCRIPTION;DOSE_TYPE;
UI.TopMenu=Y
UI.TopToolBar=Y
TABLE.Type=TTable
TABLE.X=6
TABLE.Y=215
TABLE.Width=974
TABLE.Height=528
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoHeight=Y
TABLE.Header=排序顺序,65;剂型代码,80;使用方式,80,DOSE_TYPE;中文说明,150;英文,150;备注,100;操作人员,100;操作日期,150;操作端末,100
TABLE.Item=DOSE_TYPE
TABLE.LockRows=
TABLE.LockColumns=all
TABLE.ColumnHorizontalAlignmentData=0,right;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left
TABLE.AutoWidth=Y
tPanel_1.Type=TPanel
tPanel_1.X=5
tPanel_1.Y=5
tPanel_1.Width=979
tPanel_1.Height=205
tPanel_1.Border=凸
tPanel_1.Item=tLabel_0;DOSE_CODE;tLabel_1;DOSE_CHN_DESC;tLabel_11;PY1;tLabel_12;PY2;tLabel_13;tLabel_14;tLabel_15;DESCRIPTION;tLabel_16;SEQ;DOSE_TYPE;tLabel_2;ENG_DESC
tPanel_1.AutoWidth=Y
tPanel_1.AutoX=Y
tPanel_1.AutoY=Y
ENG_DESC.Type=TTextField
ENG_DESC.X=79
ENG_DESC.Y=61
ENG_DESC.Width=209
ENG_DESC.Height=20
ENG_DESC.Text=
tLabel_2.Type=TLabel
tLabel_2.X=11
tLabel_2.Y=63
tLabel_2.Width=66
tLabel_2.Height=15
tLabel_2.Text=英文
DOSE_TYPE.Type=剂型大分类下拉列表
DOSE_TYPE.X=79
DOSE_TYPE.Y=171
DOSE_TYPE.Width=100
DOSE_TYPE.Height=23
DOSE_TYPE.Text=TButton
DOSE_TYPE.showID=Y
DOSE_TYPE.showName=Y
DOSE_TYPE.showText=N
DOSE_TYPE.showValue=N
DOSE_TYPE.showPy1=N
DOSE_TYPE.showPy2=N
DOSE_TYPE.Editable=Y
DOSE_TYPE.Tip=剂型大分类
DOSE_TYPE.TableShowList=name
DOSE_TYPE.ModuleParmString=GROUP_ID:SYS_DOSETYPE
DOSE_TYPE.ModuleParmTag=
SEQ.Type=TNumberTextField
SEQ.X=79
SEQ.Y=117
SEQ.Width=77
SEQ.Height=20
SEQ.Text=0
SEQ.Format=#########0
tLabel_16.Type=TLabel
tLabel_16.X=130
tLabel_16.Y=10
tLabel_16.Width=14
tLabel_16.Height=15
tLabel_16.Text=*
tLabel_16.Color=red
DESCRIPTION.Type=TTextField
DESCRIPTION.X=79
DESCRIPTION.Y=144
DESCRIPTION.Width=210
DESCRIPTION.Height=20
DESCRIPTION.Text=
tLabel_15.Type=TLabel
tLabel_15.X=11
tLabel_15.Y=148
tLabel_15.Width=58
tLabel_15.Height=15
tLabel_15.Text=备注
tLabel_14.Type=TLabel
tLabel_14.X=11
tLabel_14.Y=175
tLabel_14.Width=72
tLabel_14.Height=15
tLabel_14.Text=使用方式
tLabel_14.Color=blue
tLabel_13.Type=TLabel
tLabel_13.X=11
tLabel_13.Y=118
tLabel_13.Width=56
tLabel_13.Height=15
tLabel_13.Text=排序顺序
PY2.Type=TTextField
PY2.X=241
PY2.Y=88
PY2.Width=47
PY2.Height=20
PY2.Text=
tLabel_12.Type=TLabel
tLabel_12.X=178
tLabel_12.Y=91
tLabel_12.Width=47
tLabel_12.Height=15
tLabel_12.Text=注记码
PY1.Type=TTextField
PY1.X=79
PY1.Y=89
PY1.Width=77
PY1.Height=20
PY1.Text=
PY1.Action=onCode
tLabel_11.Type=TLabel
tLabel_11.X=11
tLabel_11.Y=92
tLabel_11.Width=49
tLabel_11.Height=15
tLabel_11.Text=简拼
DOSE_CHN_DESC.Type=TTextField
DOSE_CHN_DESC.X=79
DOSE_CHN_DESC.Y=35
DOSE_CHN_DESC.Width=210
DOSE_CHN_DESC.Height=20
DOSE_CHN_DESC.Text=
DOSE_CHN_DESC.Action=onCode
tLabel_1.Type=TLabel
tLabel_1.X=11
tLabel_1.Y=38
tLabel_1.Width=59
tLabel_1.Height=15
tLabel_1.Text=中文说明
DOSE_CODE.Type=TTextField
DOSE_CODE.X=79
DOSE_CODE.Y=8
DOSE_CODE.Width=45
DOSE_CODE.Height=20
DOSE_CODE.Text=
DOSE_CODE.Enabled=Y
DOSE_CODE.Tip=剂型代码
tLabel_0.Type=TLabel
tLabel_0.X=11
tLabel_0.Y=12
tLabel_0.Width=59
tLabel_0.Height=15
tLabel_0.Text=剂型代码
tLabel_0.Color=蓝