<Type=TFrame>
UI.Title=�������
UI.MenuConfig=%ROOT%\config\sys\SYSCategoryUI_Menu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SYSCategoryControl
UI.item=TREE;TABLE;RULE_TYPE;tLabel_0;tMovePane_0
UI.layout=null
UI.TopMenu=Y
UI.TopToolBar=Y
tMovePane_0.Type=TMovePane
tMovePane_0.X=133
tMovePane_0.Y=46
tMovePane_0.Width=6
tMovePane_0.Height=697
tMovePane_0.Text=
tMovePane_0.MoveType=1
tMovePane_0.Style=3
tMovePane_0.DoubleClickType=1
tMovePane_0.AutoHeight=Y
tMovePane_0.EntityData=TREE,4;TABLE,3
tLabel_0.Type=TLabel
tLabel_0.X=54
tLabel_0.Y=17
tLabel_0.Width=72
tLabel_0.Height=15
tLabel_0.Text=ѡ�����:
RULE_TYPE.Type=TComboBox
RULE_TYPE.X=135
RULE_TYPE.Y=13
RULE_TYPE.Width=308
RULE_TYPE.Height=23
RULE_TYPE.Text=
RULE_TYPE.showID=Y
RULE_TYPE.Editable=Y
RULE_TYPE.SQL=SELECT RULE_TYPE,RULE_DESC FROM SYS_RULE
RULE_TYPE.TableShowList=TEXT
RULE_TYPE.Action=
RULE_TYPE.SelectedAction=onSelectType
TABLE.Type=TTable
TABLE.X=139
TABLE.Y=46
TABLE.Width=880
TABLE.Height=697
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.AutoSize=5
TABLE.AutoModifyDataStore=Y
TABLE.SQL=SELECT * FROM SYS_CATEGORY ORDER BY RULE_TYPE,CATEGORY_CODE
TABLE.LockColumns=7,8,9,10
TABLE.Header=�������,100;��������,100;����Ӣ������,100;��ƴ,80;ע�Ƿ�,80;����˳��,80,int;��ע,100;��С����ע��,100,boolean;������Ա,80;��������,80;������ĩ,100
TABLE.ParmMap=CATEGORY_CODE;CATEGORY_CHN_DESC;CATEGORY_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;DETAIL_FLG;OPT_USER;OPT_DATE;OPT_TERM
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,right;6,left
TABLE.FocusIndexList=0,1,2,4,5
TREE.Type=TTree
TREE.X=7
TREE.Y=46
TREE.Width=128
TREE.Height=697
TREE.SpacingRow=1
TREE.RowHeight=20
TREE.AutoX=Y
TREE.AutoY=N
TREE.AutoHeight=Y
TREE.AutoSize=5
TREE.Pics=Path:dir1.gif