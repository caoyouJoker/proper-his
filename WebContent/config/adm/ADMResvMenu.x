#####################################################
# <p>Title:预约住院Menu </p>
#
# <p>Description: 预约住院Menu</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK
# @version 1.0
#####################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;manage;|;notify;|;patinfo;|;child;|;print;|;stop;|;linkHrm;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.enTip=Window
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.enText=File
File.enTip=File
File.M=F
File.Item=save;|;query;|;manage;|;notify;|;patinfo;|;child;|;print;|;stop;|;linkHrm;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.enTip=save
save.enText=save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.enTip=query
query.enText=query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

manage.Type=TMenuItem
manage.Text=预约管理
manage.Tip=预约管理
manage.enText=Reservation
manage.enTip=Reservation 
manage.M=
manage.key=
manage.Action=onManage
manage.pic=time.gif

notify.Type=TMenuItem
notify.Text=预约通知
notify.Tip=预约通知
notify.enText=Appointment
notify.enTip=Appointment
notify.M=
notify.key=
notify.Action=onNotify
notify.pic=044.gif

print.Type=TMenuItem
print.Text=住院证
print.Tip=住院证
print.enText=Admission Note
print.enTip=Admission Note
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

stop.Type=TMenuItem
stop.Text=取消预约
stop.Tip=取消预约
stop.enText=Cancel
stop.enTip=Cancel
stop.M=
stop.key=
stop.Action=onCanResv
stop.pic=closebill.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.enTip=Empty
clear.enText=Empty
clear.M=X
clear.key=
clear.Action=onClear
clear.pic=clear.gif


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.enText=Refresh
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.enTip=Log out
close.enText=Log out
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

child.Type=TMenuItem
child.Text=新生儿登记
child.Tip=新生儿登记
child.M=
child.key=
child.Action=onChild
child.pic=035.gif

immunity.Type=TMenuItem
immunity.Text=新生儿免疫
immunity.Tip=新生儿免疫
immunity.Action=onImmunity
immunity.pic=013.gif

patinfo.Type=TMenuItem
patinfo.Text=病患资料
patinfo.Tip=病患资料
patinfo.enTip=Pat Profile
patinfo.enText=Pat Profile
patinfo.M=
patinfo.key=
patinfo.Action=onPatInfo
patinfo.pic=038.gif

linkHrm.Type=TMenuItem
linkHrm.Text=关联体检信息
linkHrm.Tip=关联体检信息
linkHrm.M=
linkHrm.key=
linkHrm.Action=linkHrmInfo
linkHrm.pic=emr.gif