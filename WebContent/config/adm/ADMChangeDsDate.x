###############################################
# <p>Title:出院日期选择 </p>
#
# <p>Description: 出院日期选择</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:JavaHis </p>
#
# @author zhangk 2009-11-18
# @version 1.0
###############################################
<Type=TFrame>
UI.Title=出院日期选择
UI.MenuConfig=%ROOT%\config\adm\ADMChangeDsDateMenu.x
UI.Width=400
UI.Height=200
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.adm.ADMChangeDsDateControl
UI.Item=R1;R2;T_Last;T_Now
UI.TopToolBar=Y
UI.TopMenu=Y
UI.Opaque=Y
UI.ButtonGroupFlg=Y
T_Now.Type=TTextFormat
T_Now.X=147
T_Now.Y=68
T_Now.Width=160
T_Now.Height=22
T_Now.Text=TTextFormat
T_Now.FormatType=date
T_Now.Format=yyyy/MM/dd HH:mm:ss
T_Now.showDownButton=Y
T_Now.Enabled=N
T_Last.Type=TTextFormat
T_Last.X=147
T_Last.Y=22
T_Last.Width=160
T_Last.Height=22
T_Last.Text=TTextFormat
T_Last.FormatType=date
T_Last.Format=yyyy/MM/dd HH:mm:ss
T_Last.showDownButton=Y
T_Last.Enabled=N
R2.Type=TRadioButton
R2.X=24
R2.Y=68
R2.Width=105
R2.Height=23
R2.Text=当前时间
R2.Group=
R1.Type=TRadioButton
R1.X=24
R1.Y=22
R1.Width=115
R1.Height=23
R1.Text=上次出院时间
R1.Group=
R1.Selected=Y