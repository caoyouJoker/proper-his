###################################################
# <p>Title:自动计费档 </p>
#
# <p>Description:自动计费档 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY 2009.04.23
# @version 4.0
###################################################
<Type=TFrame>
UI.Title=自动计费项目窗口
UI.MenuConfig=%ROOT%\config\adm\admAutoBill_Menu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.adm.ADMAutoBillControl
UI.item=tPanel_0;Table
UI.layout=null
UI.FocusList=REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG
UI.TopMenu=Y
UI.TopToolBar=Y
UI.ShowTitle=N
UI.ShowMenu=N
Table.Type=TTable
Table.X=9
Table.Y=100
Table.Width=638
Table.Height=638
Table.SpacingRow=1
Table.RowHeight=20
Table.AutoX=Y
Table.AutoWidth=Y
Table.AutoHeight=Y
Table.AutoSize=10
Table.LockColumns=all
Table.StringData=string,string,string,string,string,string,string,string
Table.Header=医嘱名称,150,ORDER_LIST;数量,50;单位,70,UNIT_CODE;单价,80;总价,70;包床计费,80,boolean;婴儿床计费,80,boolean;起始日期,80;结束日期,80;操作人员,80;操作日期,120;操作终端,120
Table.ColumnHorizontalAlignmentData=0,left;1,right;2,left;3,right;4,right;7,left;8,left;9,left;10,left;11,left
Table.ParmMap=ORDER_CODE;DOSEAGE_QTY;UNIT_CODE;PRICE;SUM_PRICE;OCCUFEE_FLG;BABY_FLG;START_DATE;END_DATE;OPT_USER;OPT_DATE;OPT_TERM
Table.Item=UNIT_CODE
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=1014
tPanel_0.Height=90
tPanel_0.Item=tLabel_9;tLabel_11;ORDER_CODE;tLabel_2;ORDER_DESC;DOSEAGE_QTY;OCCUFEE_FLG;BABY_FLG;tLabel_0;tLabel_1;tLabel_3;UNIT_CODE;tLabel_4;SUM_PRICE;tLabel_5;tLabel_6;END;tLabel_45;START_MONTH;START_DAY;END_MONTH;END_DAY
tPanel_0.Enabled=Y
tPanel_0.Border=凸
tPanel_0.AutoWidth=Y
tPanel_0.TopMenu=Y
tPanel_0.TopToolBar=Y
tPanel_0.FocusList=ORDER_CODE;DOSEAGE_QTY;SUM_PRICE;OCCUFEE_FLG;BABY_FLG;START_MONTH;START_DAY;END_MONTH;END_DAY
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
END_DAY.Type=TNumberTextField
END_DAY.X=824
END_DAY.Y=55
END_DAY.Width=37
END_DAY.Height=20
END_DAY.Text=
END_DAY.Format=#0
END_MONTH.Type=TNumberTextField
END_MONTH.X=762
END_MONTH.Y=55
END_MONTH.Width=37
END_MONTH.Height=20
END_MONTH.Text=
END_MONTH.Format=#0
START_DAY.Type=TNumberTextField
START_DAY.X=576
START_DAY.Y=55
START_DAY.Width=37
START_DAY.Height=20
START_DAY.Text=
START_DAY.Format=#0
START_MONTH.Type=TNumberTextField
START_MONTH.X=518
START_MONTH.Y=55
START_MONTH.Width=37
START_MONTH.Height=20
START_MONTH.Text=
START_MONTH.Format=#0
tLabel_45.Type=TLabel
tLabel_45.X=867
tLabel_45.Y=59
tLabel_45.Width=25
tLabel_45.Height=15
tLabel_45.Text=日
END.Type=TLabel
END.X=805
END.Y=59
END.Width=36
END.Height=15
END.Text=月
tLabel_6.Type=TLabel
tLabel_6.X=619
tLabel_6.Y=59
tLabel_6.Width=22
tLabel_6.Height=15
tLabel_6.Text=日
tLabel_5.Type=TLabel
tLabel_5.X=557
tLabel_5.Y=59
tLabel_5.Width=22
tLabel_5.Height=15
tLabel_5.Text=月
SUM_PRICE.Type=TNumberTextField
SUM_PRICE.X=850
SUM_PRICE.Y=20
SUM_PRICE.Width=77
SUM_PRICE.Height=20
SUM_PRICE.Text=0
SUM_PRICE.Format=#########0.00
SUM_PRICE.Enabled=N
tLabel_4.Type=TLabel
tLabel_4.X=797
tLabel_4.Y=23
tLabel_4.Width=50
tLabel_4.Height=15
tLabel_4.Text=总价：
UNIT_CODE.Type=计量单位下拉列表
UNIT_CODE.X=691
UNIT_CODE.Y=19
UNIT_CODE.Width=81
UNIT_CODE.Height=23
UNIT_CODE.Text=TButton
UNIT_CODE.showID=Y
UNIT_CODE.showName=Y
UNIT_CODE.showText=N
UNIT_CODE.showValue=N
UNIT_CODE.showPy1=N
UNIT_CODE.showPy2=N
UNIT_CODE.Editable=Y
UNIT_CODE.Tip=计量单位
UNIT_CODE.TableShowList=name
UNIT_CODE.ExpandWidth=60
UNIT_CODE.Enabled=N
tLabel_3.Type=TLabel
tLabel_3.X=643
tLabel_3.Y=23
tLabel_3.Width=45
tLabel_3.Height=15
tLabel_3.Text=单位
tLabel_1.Type=TLabel
tLabel_1.X=697
tLabel_1.Y=59
tLabel_1.Width=68
tLabel_1.Height=15
tLabel_1.Text=截止日期
tLabel_0.Type=TLabel
tLabel_0.X=442
tLabel_0.Y=59
tLabel_0.Width=72
tLabel_0.Height=15
tLabel_0.Text=起始日期
BABY_FLG.Type=TCheckBox
BABY_FLG.X=251
BABY_FLG.Y=55
BABY_FLG.Width=130
BABY_FLG.Height=23
BABY_FLG.Text=婴儿床计费注记
OCCUFEE_FLG.Type=TCheckBox
OCCUFEE_FLG.X=101
OCCUFEE_FLG.Y=55
OCCUFEE_FLG.Width=127
OCCUFEE_FLG.Height=23
OCCUFEE_FLG.Text=包床计费注记
DOSEAGE_QTY.Type=TNumberTextField
DOSEAGE_QTY.X=560
DOSEAGE_QTY.Y=20
DOSEAGE_QTY.Width=51
DOSEAGE_QTY.Height=20
DOSEAGE_QTY.Text=0
DOSEAGE_QTY.Format=#########0
DOSEAGE_QTY.Action=onQty
ORDER_DESC.Type=TTextField
ORDER_DESC.X=223
ORDER_DESC.Y=19
ORDER_DESC.Width=227
ORDER_DESC.Height=20
ORDER_DESC.Text=
ORDER_DESC.Enabled=N
tLabel_2.Type=TLabel
tLabel_2.X=458
tLabel_2.Y=22
tLabel_2.Width=15
tLabel_2.Height=15
tLabel_2.Text= *
tLabel_2.Color=red
ORDER_CODE.Type=TTextField
ORDER_CODE.X=111
ORDER_CODE.Y=19
ORDER_CODE.Width=104
ORDER_CODE.Height=20
ORDER_CODE.Text=
ORDER_CODE.Tip=医嘱代码
ORDER_CODE.InputLength=0
tLabel_11.Type=TLabel
tLabel_11.X=20
tLabel_11.Y=22
tLabel_11.Width=91
tLabel_11.Height=15
tLabel_11.Text=医嘱代码:
tLabel_11.Color=蓝
tLabel_11.HorizontalAlignment=2
tLabel_9.Type=TLabel
tLabel_9.X=517
tLabel_9.Y=23
tLabel_9.Width=41
tLabel_9.Height=15
tLabel_9.Text=数量:
tLabel_9.HorizontalAlignment=2
tLabel_9.Color=黑