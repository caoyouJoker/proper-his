#############################################
# <p>Title:手术申请Menu </p>
#
# <p>Description:手术申请Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.23
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;sendRe;|;cancel;|;cancelRe;|;clear;|;blood;|;patInfo;|;Detail;|;close

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=save;|;sendRe;|;cancel;|;cancelRe;|;clear;|;blood;|;patInfo;|;Detail;|;close

save.Type=TMenuItem
save.Text=保存
save.zhText=保存
save.enText=Save
save.Tip=保存(Ctrl+S)
save.zhTip=保存(Ctrl+S)
save.enTip=Save(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

sendRe.Type=TMenuItem
sendRe.Text=保存重送
sendRe.Tip=保存重送
sendRe.M=
sendRe.key=
sendRe.Action=onSendRe
sendRe.pic=008.gif

cancel.Type=TMenuItem
cancel.Text=取消申请
cancel.zhText=取消申请
cancel.enText=Cancel
cancel.Tip=取消申请
cancel.zhTip=取消申请
cancel.enTip=Cancel
cancel.M=
cancel.key=
cancel.Action=onCancel
cancel.pic=004.gif

cancelRe.Type=TMenuItem
cancelRe.Text=取消重送
cancelRe.Tip=取消重送
cancelRe.M=
cancelRe.key=
cancelRe.Action=onSendReCancel
cancelRe.pic=008.gif

ana.Type=TMenuItem
ana.Text=麻醉申请
ana.zhText=麻醉申请
ana.enText=Anesthetize
ana.Tip=麻醉申请
ana.zhTip=麻醉申请
ana.enTip=Anesthetize
ana.M=
ana.key=
ana.Action=onANA
ana.pic=PHL.gif

blood.Type=TMenuItem
blood.Text=备血通知
blood.zhText=备血通知
blood.enText=Blood Notice
blood.Tip=备血通知
blood.zhTip=备血通知
blood.enTip=Blood Notice
blood.Action=onBlood
blood.pic=blood.gif

patInfo.Type=TMenuItem
patInfo.Text=患者资料
patInfo.zhText=患者资料
patInfo.enText=Pat Profile
patInfo.Tip=患者资料
patInfo.zhTip=患者资料
patInfo.enTip=Pat Profile
patInfo.Action=onPatInfo
patInfo.pic=038.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.zhText=刷新
Refresh.enText=Refresh
Refresh.Tip=刷新(F5)
Refresh.zhTip=刷新
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Empty
clear.Tip=清空(Ctrl+Z)
clear.zhTip=清空
clear.enTip=Empty
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出(Alt+F4)
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Detail.Type=TMenuItem
Detail.Text=手术记录
Detail.zhText=手术记录
Detail.enText=Detail
Detail.Tip=手术记录
Detail.zhTip=手术记录
Detail.enTip=Detail
Detail.M=
Detail.key=
Detail.Action=onDetail
Detail.pic=046.gif