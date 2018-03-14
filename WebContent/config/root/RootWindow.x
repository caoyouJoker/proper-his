#
# TBuilder Config File 
#
# Title:通讯主窗口
#
# Company:JavaHis
#
# Author:lzk 2009.04.21
#
# version 1.0
#

<Type=TFrame>
UI.Title=
UI.MenuConfig=
UI.Width=261
UI.Height=461
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.root.RootWindowControl
UI.item=tRootPanel_0
UI.layout=null
tRootPanel_0.Type=TRootPanel
tRootPanel_0.X=0
tRootPanel_0.Y=0
tRootPanel_0.Width=261
tRootPanel_0.Height=461
tRootPanel_0.AutoX=Y
tRootPanel_0.AutoY=Y
tRootPanel_0.AutoHeight=Y
tRootPanel_0.AutoWidth=Y
tRootPanel_0.AutoSize=0
tRootPanel_0.Title=在线通讯
tRootPanel_0.Item=NAME;TREE;tLabel_0
tLabel_0.Type=TLabel
tLabel_0.X=223
tLabel_0.Y=437
tLabel_0.Width=31
tLabel_0.Height=15
tLabel_0.Text=查找
tLabel_0.CursorType=12
tLabel_0.Color=蓝
tLabel_0.HorizontalAlignment=0
tLabel_0.Action=onFind
TREE.Type=TTree
TREE.X=5
TREE.Y=56
TREE.Width=252
TREE.Height=375
TREE.SpacingRow=1
TREE.RowHeight=20
TREE.pics=Group:dir1.gif:dir1.gif;User:013.gif
TREE.DoubleClickedItemAction=onDoubleItemTree
NAME.Type=TLabel
NAME.X=7
NAME.Y=24
NAME.Width=248
NAME.Height=30
NAME.Text=
NAME.IconName=%ROOT%\image\ImageIcon\Link.gif