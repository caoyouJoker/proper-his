 ######################################################
 # <p>Title:包床管理 </p>
 #
 # <p>Description: </p>
 #
 # <p>Copyright: Copyright (c) 2008</p>
 #
 # <p>Company: </p>
 #
 # @author JiaoY
 # @version 1.0
 ######################################################
<Type=TFrame>
UI.Title=包床管理
UI.MenuConfig=
UI.Width=400
UI.Height=400
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.adm.ADMSysBedAlloControl
UI.Item=tLabel_0;DEPT_CODE;tLabel_1;STATION_CODE;tLabel_2;ROOM_CODE;tLabel_3;tButton_0;tButton_2;BED_NO
UI.TopMenu=Y
UI.TopToolBar=Y
UI.ShowTitle=N
UI.ShowMenu=N
BED_NO.Type=床位
BED_NO.X=129
BED_NO.Y=190
BED_NO.Width=125
BED_NO.Height=23
BED_NO.Text=TButton
BED_NO.showID=Y
BED_NO.showName=Y
BED_NO.showText=N
BED_NO.showValue=N
BED_NO.showPy1=N
BED_NO.showPy2=N
BED_NO.Editable=Y
BED_NO.Tip=床位
BED_NO.TableShowList=name
BED_NO.ModuleParmString=
BED_NO.ModuleParmTag=
BED_NO.ExpandWidth=80
BED_NO.ActiveFlg=Y
BED_NO.ApptFlg=
BED_NO.AlloFlg=N
BED_NO.RoomCode=<ROOM_CODE>
BED_NO.StationCode=<STATION_CODE>
BED_NO.BedOccuFlg=N
tButton_2.Type=TButton
tButton_2.X=202
tButton_2.Y=250
tButton_2.Width=81
tButton_2.Height=23
tButton_2.Text=关闭
tButton_2.Action=onCan
tButton_0.Type=TButton
tButton_0.X=70
tButton_0.Y=250
tButton_0.Width=81
tButton_0.Height=23
tButton_0.Text=包床
tButton_0.Action=onBed
tLabel_3.Type=TLabel
tLabel_3.X=71
tLabel_3.Y=193
tLabel_3.Width=72
tLabel_3.Height=15
tLabel_3.Text=病床：
ROOM_CODE.Type=病房下拉列表
ROOM_CODE.X=129
ROOM_CODE.Y=132
ROOM_CODE.Width=125
ROOM_CODE.Height=23
ROOM_CODE.Text=TButton
ROOM_CODE.showID=Y
ROOM_CODE.showName=Y
ROOM_CODE.showText=N
ROOM_CODE.showValue=N
ROOM_CODE.showPy1=N
ROOM_CODE.showPy2=N
ROOM_CODE.Editable=Y
ROOM_CODE.Tip=病房
ROOM_CODE.TableShowList=name
ROOM_CODE.Enabled=N
ROOM_CODE.SelectedAction=BED_NO|onQuery
tLabel_2.Type=TLabel
tLabel_2.X=71
tLabel_2.Y=135
tLabel_2.Width=72
tLabel_2.Height=15
tLabel_2.Text=病房：
STATION_CODE.Type=病区下拉列表
STATION_CODE.X=129
STATION_CODE.Y=79
STATION_CODE.Width=125
STATION_CODE.Height=23
STATION_CODE.Text=TButton
STATION_CODE.showID=Y
STATION_CODE.showName=Y
STATION_CODE.showText=N
STATION_CODE.showValue=N
STATION_CODE.showPy1=N
STATION_CODE.showPy2=N
STATION_CODE.Editable=Y
STATION_CODE.Tip=病区
STATION_CODE.TableShowList=name
STATION_CODE.Enabled=N
tLabel_1.Type=TLabel
tLabel_1.X=71
tLabel_1.Y=85
tLabel_1.Width=57
tLabel_1.Height=15
tLabel_1.Text=病区：
DEPT_CODE.Type=科室下拉列表
DEPT_CODE.X=129
DEPT_CODE.Y=28
DEPT_CODE.Width=125
DEPT_CODE.Height=23
DEPT_CODE.Text=TButton
DEPT_CODE.showID=Y
DEPT_CODE.showName=Y
DEPT_CODE.showText=N
DEPT_CODE.showValue=N
DEPT_CODE.showPy1=N
DEPT_CODE.showPy2=N
DEPT_CODE.Editable=Y
DEPT_CODE.Tip=科室
DEPT_CODE.TableShowList=name
DEPT_CODE.Enabled=N
tLabel_0.Type=TLabel
tLabel_0.X=71
tLabel_0.Y=32
tLabel_0.Width=50
tLabel_0.Height=15
tLabel_0.Text=科室：