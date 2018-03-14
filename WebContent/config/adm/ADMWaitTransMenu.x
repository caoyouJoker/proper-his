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
UI.button=outDept;|;inDept;|;bed;|;cancelBed;|;reload;|;cancelTrans;|;cancelInHospital;|;create;|;transfer;|;close


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
cancelInHospital.Text=取消入科
cancelInHospital.Tip=取消入科
cancelInHospital.M=Q
cancelInHospital.key=Ctrl+F
cancelInHospital.Action=onCancleInDP
cancelInHospital.pic=030.gif

create.Type=TMenuItem
create.Text=生成交接单
create.Tip=生成交接单
create.M=X
create.key=Alt+F4
create.Action=onCreate
create.pic=save.gif

transfer.Type=TMenuItem
transfer.Text=交接一览表
transfer.Tip=交接一览表
transfer.M=X
transfer.key=Alt+F4
transfer.Action=onTransfer
transfer.pic=correct.gif