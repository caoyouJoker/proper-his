#
# TBuilder Config File 
#
# Title:����δ������ϸ��
#
# Company:JavaHis
#
# Author:zhangy 2009.05.06
#
# version 1.0
#

<Type=TFrame>
UI.Title=����δ������ϸ��
UI.MenuConfig=%ROOT%\config\spc\INDUnVerifyinMenu.x
UI.Width=600
UI.Height=500
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.spc.INDUnVerifyinControl
UI.item=tPanel_6;TABLE
UI.layout=null
UI.ShowMenu=N
UI.ShowTitle=N
UI.TopToolBar=Y  
UI.TopMenu=Y
UI.Text=����δ������ϸ��
UI.Tip=����δ������ϸ��
UI.FocusList=SUP_CODE;PURORDER_NO;ORDER_CODE;SELECT_ALL
TABLE.Type=TTable
TABLE.X=106
TABLE.Y=100
TABLE.Width=81
TABLE.Height=400
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoY=N
TABLE.AutoX=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.AutoSize=5
TABLE.Header=ѡ,30,boolean;��������,100;��������,100;ҩƷ����,180;������,60,double,#####0.000;������,60,double,#####0.000;��λ,40,UNIT;��������,80,double,#####0.0000;�ۼ�����,100,double,#####0.000
TABLE.ParmMap=SELECT_FLG;PURORDER_NO;PURORDER_DATE;ORDER_DESC;PURORDER_QTY;GIFT_QTY;BILL_UNIT;PURORDER_PRICE;ACTUAL_QTY
TABLE.LockColumns=1,2,3,4,5,6,7,8
TABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,right;5,right;6,left;7,right;8,right
TABLE.Item=UNIT
tPanel_6.Type=TPanel
tPanel_6.X=5
tPanel_6.Y=5
tPanel_6.Width=590
tPanel_6.Height=91
tPanel_6.Border=��
tPanel_6.AutoX=Y
tPanel_6.AutoY=Y
tPanel_6.AutoWidth=Y
tPanel_6.Item=tLabel_2;tLabel_3;tLabel_4;SELECT_ALL;ORDER_CODE;ORDER_DESC;PURORDER_NO;UNIT;SUP_CODE
SUP_CODE.Type=��Ӧ����
SUP_CODE.X=95
SUP_CODE.Y=9
SUP_CODE.Width=200
SUP_CODE.Height=23
SUP_CODE.Text=
SUP_CODE.HorizontalAlignment=2
SUP_CODE.PopupMenuHeader=ID,100;NAME,100
SUP_CODE.PopupMenuWidth=300
SUP_CODE.PopupMenuHeight=300
SUP_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
SUP_CODE.FormatType=combo
SUP_CODE.ShowDownButton=Y
SUP_CODE.Tip=��Ӧ����
SUP_CODE.ShowColumnList=NAME
SUP_CODE.Action=onSupCodeChange
SUP_CODE.PhaFlg=Y
UNIT.Type=������λ�����б�
UNIT.X=112
UNIT.Y=105
UNIT.Width=10
UNIT.Height=23
UNIT.Text=TButton
UNIT.showID=Y
UNIT.showName=Y
UNIT.showText=N
UNIT.showValue=N
UNIT.showPy1=N
UNIT.showPy2=N
UNIT.Editable=Y
UNIT.Tip=������λ
UNIT.TableShowList=name
PURORDER_NO.Type=TComboBox
PURORDER_NO.X=422
PURORDER_NO.Y=9
PURORDER_NO.Width=150
PURORDER_NO.Height=23
PURORDER_NO.Text=TButton
PURORDER_NO.showID=Y
PURORDER_NO.Editable=Y
PURORDER_NO.ShowText=N
PURORDER_NO.ShowName=N
PURORDER_NO.TableShowList=id
PURORDER_NO.ParmMap=id:PURORDER_NO
PURORDER_NO.ModuleParmString=
PURORDER_NO.Action=onPurOrderChange
PURORDER_NO.SelectedAction=onPurOrderChange
ORDER_DESC.Type=TTextField
ORDER_DESC.X=202
ORDER_DESC.Y=38
ORDER_DESC.Width=150
ORDER_DESC.Height=20
ORDER_DESC.Text=
ORDER_DESC.Enabled=N
ORDER_CODE.Type=TTextField
ORDER_CODE.X=95
ORDER_CODE.Y=38
ORDER_CODE.Width=100
ORDER_CODE.Height=20
ORDER_CODE.Text=
SELECT_ALL.Type=TCheckBox
SELECT_ALL.X=13
SELECT_ALL.Y=64
SELECT_ALL.Width=81
SELECT_ALL.Height=23
SELECT_ALL.Text=ȫ����ѡ
SELECT_ALL.Action=onCheckSelectAll
tLabel_4.Type=TLabel
tLabel_4.X=18
tLabel_4.Y=41
tLabel_4.Width=72
tLabel_4.Height=15
tLabel_4.Text=ҩƷ����:
tLabel_4.Color=blue
tLabel_3.Type=TLabel
tLabel_3.X=358
tLabel_3.Y=12
tLabel_3.Width=60
tLabel_3.Height=15
tLabel_3.Text=������:
tLabel_3.Color=blue
tLabel_2.Type=TLabel
tLabel_2.X=18
tLabel_2.Y=12
tLabel_2.Width=72
tLabel_2.Height=15
tLabel_2.Text=��Ӧ����:
tLabel_2.Color=blue