#############################################
# <p>Title:入出转管理Menu </p>
#
# <p>Description:入出转管理Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=outDept;|;inDept;|;insureInfo;|;bed;|;cancelBed;|;cancelInDP;|;reload;|;cancelTrans;|;nurse;|;selTWD;|;close


Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=outDept;|;inDept;|;reload;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=checkSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

bed.Type=TMenuItem
bed.Text=包床管理
bed.Tip=包床管理
bed.M=
bed.key=
bed.Action=onBed
bed.pic=048.gif

cancelBed.Type=TMenuItem
cancelBed.Text=取消包床
cancelBed.Tip=取消包床
cancelBed.M=
cancelBed.key=
cancelBed.Action=onCancelBed
cancelBed.pic=Undo.gif

outDept.Type=TMenuItem
outDept.Text=转科管理
outDept.Tip=转科管理
outDept.M=Q
outDept.key=Ctrl+F
outDept.Action=onOutDept
outDept.pic=tempsave.gif

inDept.Type=TMenuItem
inDept.Text=病患信息
inDept.Tip=病患信息
inDept.M=Q
inDept.key=Ctrl+F
inDept.Action=onInStation
inDept.pic=013.gif

insureInfo.Type=TMenuItem
insureInfo.Text=病患保险信息
insureInfo.Tip=病患保险信息
insureInfo.M=
insureInfo.key=Ctrl+F
insureInfo.Action=onInsureInfo
insureInfo.pic=013.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

reload.Type=TMenuItem
reload.Text=更新信息
reload.Tip=更新信息
reload.M=
reload.key=Ctrl+R
reload.Action=onReload
reload.pic=008.gif

cancelTrans.Type=TMenuItem
cancelTrans.Text=取消转科
cancelTrans.Tip=取消转科
cancelTrans.M=Q
cancelTrans.key=Ctrl+F
cancelTrans.Action=onCancelTrans
cancelTrans.pic=002.gif

cancelInHospital.Type=TMenuItem
cancelInHospital.Text=取消住院
cancelInHospital.Tip=取消住院
cancelInHospital.M=Q
cancelInHospital.key=Ctrl+F
cancelInHospital.Action=onCancelInHospital
cancelInHospital.pic=030.gif

cancelInDP.Type=TMenuItem
cancelInDP.Text=取消入科
cancelInDP.Tip=取消入科
cancelInDP.M=Q
cancelInDP.key=Ctrl+F
cancelInDP.Action=onCancleInDP
cancelInDP.pic=030.gif

nurse.Type=TMenuItem
nurse.Text=护理记录
nurse.Tip=护理记录
nurse.M=
nurse.key=
nurse.Action=onNursingRec
nurse.pic=inwimg.gif

selTWD.Type=TMenuItem
selTWD.Text=体温表
selTWD.Tip=体温表
selTWD.M=
selTWD.key=
selTWD.Action=onSelTWD
selTWD.pic=037.gif


