#
# TBuilder Config File 
#
# Title:盘点报表
#
# Company:JavaHis
#
# Author:zhangy 2009.10.05
#
# version 1.0
#

<Type=TFrame>
UI.Title=盘点报表
UI.MenuConfig=%ROOT%\config\ind\INDQtyCheckPrtMenu.x
UI.Width=400
UI.Height=400
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ind.INDQtyCheckPrtControl
UI.item=tLabel_10;RadioButton1;RadioButton2;RadioButton3;FROZEN_DATE;tLabel_25
UI.layout=null
UI.Name=盘点报表
UI.Text=盘点报表
UI.Tip=盘点报表
UI.TopMenu=Y
UI.TopToolBar=Y
tLabel_25.Type=TLabel
tLabel_25.X=71
tLabel_25.Y=104
tLabel_25.Width=72
tLabel_25.Height=15
tLabel_25.Text=冻结时间
FROZEN_DATE.Type=TComboBox
FROZEN_DATE.X=145
FROZEN_DATE.Y=100
FROZEN_DATE.Width=160
FROZEN_DATE.Height=23
FROZEN_DATE.Text=TButton
FROZEN_DATE.showID=N
FROZEN_DATE.Editable=Y
FROZEN_DATE.ParmMap=id:FROZEN_DATE;name:F_DATE
FROZEN_DATE.TableShowList=name
FROZEN_DATE.ShowText=N
FROZEN_DATE.ShowName=Y
FROZEN_DATE.ExpandWidth=0
RadioButton3.Type=TRadioButton
RadioButton3.X=262
RadioButton3.Y=56
RadioButton3.Width=81
RadioButton3.Height=23
RadioButton3.Text=盈亏表
RadioButton3.Group=group1
RadioButton2.Type=TRadioButton
RadioButton2.X=145
RadioButton2.Y=56
RadioButton2.Width=97
RadioButton2.Height=23
RadioButton2.Text=盘点明细表
RadioButton2.Group=group1
RadioButton1.Type=TRadioButton
RadioButton1.X=44
RadioButton1.Y=56
RadioButton1.Width=81
RadioButton1.Height=23
RadioButton1.Text=盘点表
RadioButton1.Group=group1
RadioButton1.Selected=Y
tLabel_10.Type=TLabel
tLabel_10.X=154
tLabel_10.Y=18
tLabel_10.Width=86
tLabel_10.Height=30
tLabel_10.Text=盘点报表
tLabel_10.Color=blue
tLabel_10.FontSize=20