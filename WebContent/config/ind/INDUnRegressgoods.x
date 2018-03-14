#
# TBuilder Config File 
#
# Title:验收未退货明细表
#
# Company:JavaHis
#
# Author:zhangy 2009.05.06
#
# version 1.0
#

<Type=TFrame>
UI.Title=验收未退货明细表
UI.MenuConfig=%ROOT%\config\ind\INDUnRegressgoodsMenu.x
UI.Width=600
UI.Height=500
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ind.INDUnRegressgoodsControl
UI.item=tPanel_6;TABLE
UI.layout=null
UI.ShowMenu=N
UI.ShowTitle=N
UI.TopToolBar=Y
UI.TopMenu=Y
UI.Text=验收未退货明细表
UI.Tip=验收未退货明细表
UI.FocusList=SUP_CODE;VERIFYIN_NO;START_CHECK_DATE;END_CHECK_DATE;ORDER_CODE
TABLE.Type=TTable
TABLE.X=106
TABLE.Y=126
TABLE.Width=81
TABLE.Height=369
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoY=N
TABLE.AutoX=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.AutoSize=5
TABLE.Header=选,30,boolean;验收单号,120;验收日期,100;药品名称,120;规格,120;批号,60;效期,100;验收数,60,double,#####0.000;赠送数,60,double,#####0.000;单位,40,UNIT;验收单价,80,double,#####0.0000;累计验收,100,double,#####0.000
TABLE.Item=UNIT
TABLE.ParmMap=SELECT_FLG;VERIFYIN_NO;VERIFYIN_DATE;ORDER_DESC;SPECIFICATION;BATCH_NO;VALID_DATE;VERIFYIN_QTY;GIFT_QTY;BILL_UNIT;VERIFYIN_PRICE;ACTUAL_QTY
TABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,right;9,right;10,right;11,right
TABLE.LockRows=
TABLE.LockColumns=1,2,3,4,5,6,7,8,9,10,11
tPanel_6.Type=TPanel
tPanel_6.X=5
tPanel_6.Y=5
tPanel_6.Width=590
tPanel_6.Height=117
tPanel_6.Border=组
tPanel_6.AutoX=Y
tPanel_6.AutoY=Y
tPanel_6.AutoWidth=Y
tPanel_6.Item=tLabel_2;tLabel_3;tLabel_4;SELECT_ALL;ORDER_CODE;ORDER_DESC;SUP_CODE;VERIFYIN_NO;tLabel_0;START_CHECK_DATE;END_CHECK_DATE;tLabel_1;UNIT
UNIT.Type=计量单位下拉列表
UNIT.X=120
UNIT.Y=141
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
UNIT.Tip=计量单位
UNIT.TableShowList=name
tLabel_1.Type=TLabel
tLabel_1.X=265
tLabel_1.Y=40
tLabel_1.Width=20
tLabel_1.Height=15
tLabel_1.Text=～
END_CHECK_DATE.Type=TTextFormat
END_CHECK_DATE.X=292
END_CHECK_DATE.Y=37
END_CHECK_DATE.Width=160
END_CHECK_DATE.Height=20
END_CHECK_DATE.Text=
END_CHECK_DATE.Format=yyyy/MM/dd HH:mm:ss
END_CHECK_DATE.showDownButton=Y
END_CHECK_DATE.FormatType=date
END_CHECK_DATE.HorizontalAlignment=2
START_CHECK_DATE.Type=TTextFormat
START_CHECK_DATE.X=95
START_CHECK_DATE.Y=37
START_CHECK_DATE.Width=160
START_CHECK_DATE.Height=20
START_CHECK_DATE.Text=
START_CHECK_DATE.showDownButton=Y
START_CHECK_DATE.Format=yyyy/MM/dd HH:mm:ss
START_CHECK_DATE.FormatType=date
START_CHECK_DATE.HorizontalAlignment=2
tLabel_0.Type=TLabel
tLabel_0.X=18
tLabel_0.Y=39
tLabel_0.Width=72
tLabel_0.Height=15
tLabel_0.Text=查询区间:
tLabel_0.Color=blue
VERIFYIN_NO.Type=TComboBox
VERIFYIN_NO.X=395
VERIFYIN_NO.Y=8
VERIFYIN_NO.Width=150
VERIFYIN_NO.Height=23
VERIFYIN_NO.Text=TButton
VERIFYIN_NO.showID=Y
VERIFYIN_NO.Editable=Y
VERIFYIN_NO.ShowText=N
VERIFYIN_NO.ShowName=Y
VERIFYIN_NO.TableShowList=id
VERIFYIN_NO.ParmMap=id:VERIFYIN_NO
SUP_CODE.Type=供应厂商下拉列表
SUP_CODE.X=95
SUP_CODE.Y=8
SUP_CODE.Width=200
SUP_CODE.Height=23
SUP_CODE.Text=TButton
SUP_CODE.showID=Y
SUP_CODE.showName=Y
SUP_CODE.showText=N
SUP_CODE.showValue=N
SUP_CODE.showPy1=N
SUP_CODE.showPy2=N
SUP_CODE.Editable=Y
SUP_CODE.Tip=供应厂商
SUP_CODE.TableShowList=name
SUP_CODE.ModuleParmString=
SUP_CODE.ModuleParmTag=
SUP_CODE.ExpandWidth=30
SUP_CODE.Enabled=N
SUP_CODE.PhaFlg=Y
ORDER_DESC.Type=TTextField
ORDER_DESC.X=202
ORDER_DESC.Y=63
ORDER_DESC.Width=150
ORDER_DESC.Height=20
ORDER_DESC.Text=
ORDER_DESC.Enabled=N
ORDER_CODE.Type=TTextField
ORDER_CODE.X=95
ORDER_CODE.Y=63
ORDER_CODE.Width=100
ORDER_CODE.Height=20
ORDER_CODE.Text=
SELECT_ALL.Type=TCheckBox
SELECT_ALL.X=13
SELECT_ALL.Y=90
SELECT_ALL.Width=81
SELECT_ALL.Height=23
SELECT_ALL.Text=全部勾选
SELECT_ALL.Action=onCheckSelectAll
tLabel_4.Type=TLabel
tLabel_4.X=18
tLabel_4.Y=66
tLabel_4.Width=72
tLabel_4.Height=15
tLabel_4.Text=药品代码:
tLabel_4.Color=blue
tLabel_3.Type=TLabel
tLabel_3.X=324
tLabel_3.Y=11
tLabel_3.Width=70
tLabel_3.Height=15
tLabel_3.Text=验收单号:
tLabel_3.Color=blue
tLabel_2.Type=TLabel
tLabel_2.X=18
tLabel_2.Y=11
tLabel_2.Width=72
tLabel_2.Height=15
tLabel_2.Text=供应厂商:
tLabel_2.Color=blue