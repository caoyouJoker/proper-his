#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sundx 2009.10.15
#
# version 1.0
#

<Type=TFrame>
UI.Title=ָ��ȼ��趨
UI.MenuConfig=%ROOT%\config\dss\DSSKPIUIMenu.x
UI.Width=1282
UI.Height=683
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.dss.DSSKPIControl
UI.item=tPanel_1;tMovePane_0;tPanel_2
UI.layout=null
UI.X=10
UI.Y=14
UI.Name=
UI.Text=
UI.ShowMenu=N
UI.FocusList=KPI_CODE
tPanel_2.Type=TPanel
tPanel_2.X=206
tPanel_2.Y=5
tPanel_2.Width=1071
tPanel_2.Height=673
tPanel_2.item=tPanel_0;tLabel_0;KPI_CODE;tLabel_1;KPI_DESC;tLabel_2;PY1;tLabel_3;SEQ;tLabel_4;DESCRIPTION;tLabel_5;PY2;tLabel_6;KPI_VALUE;tLabel_7;KPI_GOAL;tLabel_8;tLabel_9;tLabel_10;tLabel_12;LEAF;KPI_ATTRIBUTE;KPI_KIND;PARENT_CODE;tLabel_13;KPI_STATUS;
tPanel_2.Border=��
tPanel_2.AutoY=Y
tPanel_2.AutoWidth=Y
tPanel_2.AutoHeight=Y
tMovePane_0.Type=TMovePane
tMovePane_0.X=192
tMovePane_0.Y=121
tMovePane_0.Width=5
tMovePane_0.Height=253
tMovePane_0.Text=
tMovePane_0.MoveType=1
tMovePane_0.Style=3
tMovePane_0.EntityData=tPanel_1,4;tPanel_2,3
tMovePane_0.AutoY=Y
tMovePane_0.AutoHeight=Y
KPI_STATUS.Type=TTextField
KPI_STATUS.X=140
KPI_STATUS.Y=270
KPI_STATUS.Width=766
KPI_STATUS.Height=20
KPI_STATUS.Text=
KPI_STATUS.Action=onKPIStatus
KPI_STATUS.InputLength=1000
tLabel_13.Type=TLabel
tLabel_13.X=316
tLabel_13.Y=115
tLabel_13.Width=72
tLabel_13.Height=15
tLabel_13.Text=��KPI����
PARENT_CODE.Type=TTextField
PARENT_CODE.X=414
PARENT_CODE.Y=111
PARENT_CODE.Width=164
PARENT_CODE.Height=20
PARENT_CODE.Text=
PARENT_CODE.Action=onParentCode
PARENT_CODE.InputLength=20
KPI_KIND.Type=TComboBox
KPI_KIND.X=140
KPI_KIND.Y=117
KPI_KIND.Width=154
KPI_KIND.Height=23
KPI_KIND.Text=TButton
KPI_KIND.showID=Y
KPI_KIND.Editable=Y
KPI_KIND.StringData=[[id,text],["",""],[0,ȫԺ����KPI],[1,���ҿ���KPI],[2,ҽ������KPI]]
KPI_KIND.TableShowList=text
KPI_KIND.Action=onKPIKind
KPI_ATTRIBUTE.Type=TComboBox
KPI_ATTRIBUTE.X=415
KPI_ATTRIBUTE.Y=77
KPI_ATTRIBUTE.Width=163
KPI_ATTRIBUTE.Height=23
KPI_ATTRIBUTE.Text=TButton
KPI_ATTRIBUTE.showID=Y
KPI_ATTRIBUTE.Editable=Y
KPI_ATTRIBUTE.StringData=[[id,text],["",""],[0,ȡƽ��ֵ],[1,�ۼ�]]
KPI_ATTRIBUTE.TableShowList=text
KPI_ATTRIBUTE.Action=onKPIAttribute
LEAF.Type=TComboBox
LEAF.X=140
LEAF.Y=159
LEAF.Width=154
LEAF.Height=23
LEAF.Text=TButton
LEAF.showID=Y
LEAF.Editable=Y
LEAF.StringData=[[id,text],["",""],[Y,��],[N,��]]
LEAF.TableShowList=text
LEAF.Action=onLeaf
tLabel_12.Type=TLabel
tLabel_12.X=32
tLabel_12.Y=164
tLabel_12.Width=72
tLabel_12.Height=15
tLabel_12.Text=�Ƿ�ΪҶ��
tLabel_10.Type=TLabel
tLabel_10.X=36
tLabel_10.Y=122
tLabel_10.Width=72
tLabel_10.Height=15
tLabel_10.Text=KPI ����
tLabel_9.Type=TLabel
tLabel_9.X=318
tLabel_9.Y=83
tLabel_9.Width=72
tLabel_9.Height=15
tLabel_9.Text=KPI ����
tLabel_8.Type=TLabel
tLabel_8.X=34
tLabel_8.Y=274
tLabel_8.Width=107
tLabel_8.Height=15
tLabel_8.Text=KPI ״̬����ʽ 
KPI_GOAL.Type=TTextField
KPI_GOAL.X=140
KPI_GOAL.Y=236
KPI_GOAL.Width=766
KPI_GOAL.Height=20
KPI_GOAL.Text=
KPI_GOAL.Action=onKPIGoal
KPI_GOAL.InputLength=1000
tLabel_7.Type=TLabel
tLabel_7.X=34
tLabel_7.Y=239
tLabel_7.Width=98
tLabel_7.Height=15
tLabel_7.Text=KPI Ŀ�����ʽ
KPI_VALUE.Type=TTextField
KPI_VALUE.X=141
KPI_VALUE.Y=200
KPI_VALUE.Width=765
KPI_VALUE.Height=20
KPI_VALUE.Text=
KPI_VALUE.Action=onKPIValue
KPI_VALUE.InputLength=1000
tLabel_6.Type=TLabel
tLabel_6.X=34
tLabel_6.Y=203
tLabel_6.Width=72
tLabel_6.Height=15
tLabel_6.Text=KPI ����ʽ
PY2.Type=TTextField
PY2.X=414
PY2.Y=44
PY2.Width=166
PY2.Height=20
PY2.Text=
PY2.Action=onPY2
PY2.InputLength=50
PY2.Enabled=N
tLabel_5.Type=TLabel
tLabel_5.X=317
tLabel_5.Y=47
tLabel_5.Width=72
tLabel_5.Height=15
tLabel_5.Text=�� �� ��
DESCRIPTION.Type=TTextField
DESCRIPTION.X=140
DESCRIPTION.Y=304
DESCRIPTION.Width=766
DESCRIPTION.Height=20
DESCRIPTION.Text=
DESCRIPTION.InputLength=200
tLabel_4.Type=TLabel
tLabel_4.X=29
tLabel_4.Y=308
tLabel_4.Width=75
tLabel_4.Height=15
tLabel_4.Text=��     ע
tLabel_4.Color=��
tLabel_4.HorizontalAlignment=0
SEQ.Type=TTextField
SEQ.X=140
SEQ.Y=79
SEQ.Width=155
SEQ.Height=20
SEQ.Text=
SEQ.Action=onSeq
SEQ.Enabled=Y
tLabel_3.Type=TLabel
tLabel_3.X=37
tLabel_3.Y=80
tLabel_3.Width=86
tLabel_3.Height=16
tLabel_3.Text=˳ �� ��
tLabel_3.Color=��
PY1.Type=TTextField
PY1.X=140
PY1.Y=45
PY1.Width=155
PY1.Height=20
PY1.Text=
PY1.Action=onPY1
PY1.InputLength=50
PY1.Enabled=N
tLabel_2.Type=TLabel
tLabel_2.X=36
tLabel_2.Y=50
tLabel_2.Width=84
tLabel_2.Height=15
tLabel_2.Text=ƴ    ��
tLabel_2.Color=��
KPI_DESC.Type=TTextField
KPI_DESC.X=414
KPI_DESC.Y=14
KPI_DESC.Width=167
KPI_DESC.Height=20
KPI_DESC.Text=
KPI_DESC.Action=onKPIDesc
KPI_DESC.InputLength=20
tLabel_1.Type=TLabel
tLabel_1.X=317
tLabel_1.Y=18
tLabel_1.Width=79
tLabel_1.Height=15
tLabel_1.Text=KPI ����
tLabel_1.Color=��
KPI_CODE.Type=TTextField
KPI_CODE.X=140
KPI_CODE.Y=12
KPI_CODE.Width=155
KPI_CODE.Height=20
KPI_CODE.Text=
KPI_CODE.Action=onQuery
KPI_CODE.InputLength=20
tLabel_0.Type=TLabel
tLabel_0.X=36
tLabel_0.Y=16
tLabel_0.Width=89
tLabel_0.Height=15
tLabel_0.Text=KPI ����
tLabel_0.Color=��
tPanel_1.Type=TPanel
tPanel_1.X=5
tPanel_1.Y=5
tPanel_1.Width=184
tPanel_1.Height=673
tPanel_1.Border=��
tPanel_1.Item=TREE
tPanel_1.AutoX=Y
tPanel_1.AutoY=Y
tPanel_1.AutoWidth=N
tPanel_1.AutoHeight=Y
tPanel_1.AutoW=N
TREE.Type=TTree
TREE.X=2
TREE.Y=5
TREE.Width=162
TREE.Height=655
TREE.SpacingRow=1
TREE.RowHeight=20
TREE.AutoY=Y
TREE.AutoX=Y
TREE.AutoWidth=Y
TREE.AutoW=N
TREE.AutoHeight=Y
TREE.AutoH=N
TREE.pics=Root:sys.gif;Path:dir1.gif:dir1.gif;UI:refurbish.gif;Module:table.gif
tPanel_0.Type=TPanel
tPanel_0.X=10
tPanel_0.Y=348
tPanel_0.Width=1050
tPanel_0.Height=317
tPanel_0.Border=��
tPanel_0.Item=TABLE
tPanel_0.AutoW=N
tPanel_0.AutoH=N
tPanel_0.AutoWidth=Y
TABLE.Type=TTable
TABLE.X=7
TABLE.Y=11
TABLE.Width=916
TABLE.Height=295
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoHeight=Y
TABLE.AutoWidth=Y
TABLE.AutoY=Y
TABLE.Header=KPI����,100;KPI����,100;��KPI����,100;�Ƿ�ΪҶ��,100,boolean;ƴ��,100;������,100;˳���,100;��ע,100;KPI����ʽ,100;KPIĿ�����ʽ,100;KPI״̬����ʽ,100,KPI_STATUS;KPI����,100,KPI_ATTRIBUTE;KPI����,100,KPI_KIND;������Ա,100;����ʱ��,100;������ĩ,100
TABLE.StringData=string,string,string,string,string,string,string,string,string,string,string,string,string,string,string��string
TABLE.ParmMap=KPI_CODE;KPI_DESC;PARENT_CODE;LEAF;PY1;PY2;SEQ;DESCRIPTION;KPI_VALUE;KPI_GOAL;KPI_STATUS;KPI_ATTRIBUTE;KPI_KIND;OPT_USER;OPT_DATE;OPT_TERM
TABLE.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,center;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left
TABLE.Item=KPI_ATTRIBUTE;KPI_KIND
TABLE.DoubleClickedAction=onTable