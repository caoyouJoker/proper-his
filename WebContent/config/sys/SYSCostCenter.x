#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:�Ӡ� 2011.06.01
#
# version 1.0
#

<Type=TFrame>
UI.Title=�ɱ����Ĺ���
UI.MenuConfig=%ROOT%\config\sys\SYSCostCenter_Menu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SYSCostCenterControl
UI.item=COST_CENTER_CODE;COST_CENTER_CHN_DESC;tLabel_3;TREE;TABLE;tMovePane_0;DEPT_CODE1;REGION_CODE;tLabel_2
UI.layout=null
UI.TopMenu=Y
UI.TopToolBar=Y
tLabel_2.Type=TLabel
tLabel_2.X=26
tLabel_2.Y=18
tLabel_2.Width=102
tLabel_2.Height=15
tLabel_2.Text=�ɱ����Ĵ���:
tLabel_2.Color=��
REGION_CODE.Type=���������б�
REGION_CODE.X=415
REGION_CODE.Y=85
REGION_CODE.Width=81
REGION_CODE.Height=23
REGION_CODE.Text=TButton
REGION_CODE.showID=Y
REGION_CODE.showName=Y
REGION_CODE.showText=N
REGION_CODE.showValue=N
REGION_CODE.showPy1=N
REGION_CODE.showPy2=N
REGION_CODE.Editable=Y
REGION_CODE.Tip=����
REGION_CODE.TableShowList=name
REGION_CODE.ModuleParmString=
REGION_CODE.ModuleParmTag=
DEPT_CODE1.Type=��ͳ���������б�
DEPT_CODE1.X=279
DEPT_CODE1.Y=90
DEPT_CODE1.Width=81
DEPT_CODE1.Height=23
DEPT_CODE1.Text=TButton
DEPT_CODE1.showID=Y
DEPT_CODE1.showName=Y
DEPT_CODE1.showText=N
DEPT_CODE1.showValue=N
DEPT_CODE1.showPy1=N
DEPT_CODE1.showPy2=N
DEPT_CODE1.Editable=Y
DEPT_CODE1.Tip=��ͳ����
DEPT_CODE1.TableShowList=name
DEPT_CODE1.ModuleParmString=GROUP_ID:SYS_DEPTCAT1
DEPT_CODE1.ModuleParmTag=
tMovePane_0.Type=TMovePane
tMovePane_0.X=147
tMovePane_0.Y=68
tMovePane_0.Width=27
tMovePane_0.Height=697
tMovePane_0.Text=TMovePane
tMovePane_0.MoveType=1
tMovePane_0.AutoHeight=Y
tMovePane_0.EntityData=TREE,4;TABLE,3
TABLE.Type=TTable
TABLE.X=151
TABLE.Y=50
TABLE.Width=863
TABLE.Height=693
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.Header=�ɱ����Ĵ���,105;�ɱ���������,120;��ƴ1,60;��ƴ2,60;Ӣ������,75;�ɱ����ļ�ƴ,100;���,40;�������,120,REGION_CODE;��������,70;��С����,70,boolean;���ҵȼ�,70,combo|1:һ������|2:��������|3:��������;���ҹ���,100,combo|0:�ٴ�����|1:ҽ������|2:ҩ�����|3:��������|4:��ҩ��;���Ҵ����,80,DEPT_CODE1;����,40,boolean;����,40,boolean;סԺ,40,boolean;����,40,boolean;������ĩ��,80;Ԥ��ӡ����,80;ͳ��,40,boolean;����,40,boolean
TABLE.AutoHeight=Y
TABLE.AutoWidth=Y
TABLE.Item=REGION_CODE;DEPT_CODE1
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,left;8,left;10,left;11,left
TABLE.ParmMap=COST_CENTER_CODE;COST_CENTER_CHN_DESC;PY1;PY2;COST_CENTER_ENG_DESC;COST_CENTER_ABS_DESC;SEQ;REGION_CODE;DESCRIPTION;FINAL_FLG;DEPT_GRADE;CLASSIFY;DEPT_CAT1;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;HRM_FIT_FLG;DEFAULT_TERM_NO;DEFAULT_PRINTER_NO;STATISTICS_FLG;ACTIVE_FLG
TABLE.SQL=SELECT * FROM SYS_COST_CENTER
TABLE.AutoModifyDataStore=Y
TABLE.FocusType=2
TREE.Type=TTree
TREE.X=6
TREE.Y=50
TREE.Width=143
TREE.Height=693
TREE.SpacingRow=1
TREE.RowHeight=20
TREE.Pics=Path:dir1.gif
TREE.AutoHeight=Y
tLabel_3.Type=TLabel
tLabel_3.X=225
tLabel_3.Y=17
tLabel_3.Width=97
tLabel_3.Height=15
tLabel_3.Text=�ɱ���������:
tLabel_3.Color=��
COST_CENTER_CHN_DESC.Type=TTextField
COST_CENTER_CHN_DESC.X=327
COST_CENTER_CHN_DESC.Y=14
COST_CENTER_CHN_DESC.Width=149
COST_CENTER_CHN_DESC.Height=20
COST_CENTER_CHN_DESC.Text=
COST_CENTER_CHN_DESC.Action=onQuery
COST_CENTER_CODE.Type=TTextField
COST_CENTER_CODE.X=125
COST_CENTER_CODE.Y=15
COST_CENTER_CODE.Width=77
COST_CENTER_CODE.Height=20
COST_CENTER_CODE.Text=
COST_CENTER_CODE.Action=onQuery