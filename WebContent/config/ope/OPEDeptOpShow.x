#############################################
# <p>Title:科常用手术码选择界面 </p>
#
# <p>Description:科常用手术码选择界面 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.26
# @version 4.0
#############################################
<Type=TFrame>
UI.Title=科常用手术码选择
UI.MenuConfig=%ROOT%\config\ope\OPEDeptOpShowMenu.x
UI.Width=400
UI.Height=400
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ope.OPEDeptOpShowControl
UI.Item=tPanel_0;Table
UI.TopMenu=Y
UI.ShowTitle=N
UI.ShowMenu=N
UI.TopToolBar=Y
UI.zhTitle=科常用手术码选择
UI.enTitle=Dept common Op_ICD
Table.Type=TTable
Table.X=55
Table.Y=60
Table.Width=81
Table.Height=335
Table.SpacingRow=1
Table.RowHeight=20
Table.AutoX=Y
Table.AutoWidth=Y
Table.AutoHeight=Y
Table.Header=手术ICD,100;手术名称,280
Table.Visible=Y
Table.LockColumns=all
Table.ParmMap=OP_CODE;OPT_CHN_DESC
Table.ColumnHorizontalAlignmentData=0,left;1,left
Table.DoubleClickedAction=onDoubleTableClicked
Table.enHeader=Op ICD;Op ICD Desc
Table.LanguageMap=OPT_CHN_DESC|OPT_ENG_DESC
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=390
tPanel_0.Height=50
tPanel_0.Border=凸
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoWidth=Y
tPanel_0.Item=tLabel_0;DEPT
DEPT.Type=科室下拉列表
DEPT.X=86
DEPT.Y=13
DEPT.Width=120
DEPT.Height=23
DEPT.Text=TButton
DEPT.showID=Y
DEPT.showName=Y
DEPT.showText=N
DEPT.showValue=N
DEPT.showPy1=Y
DEPT.showPy2=Y
DEPT.Editable=Y
DEPT.Tip=科室
DEPT.TableShowList=name
DEPT.Enabled=N
tLabel_0.Type=TLabel
tLabel_0.X=27
tLabel_0.Y=18
tLabel_0.Width=49
tLabel_0.Height=15
tLabel_0.Text=科室
tLabel_0.enText=Dept
tLabel_0.zhText=科室