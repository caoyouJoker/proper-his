##############################################
# <p>Title:非常态门诊 </p>
#
# <p>Description: 非常态门诊</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangk 2010-10-26
# @version 4.0
##############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;ekt;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;ekt;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.zhText=保存
save.enText=Save
save.Tip=保存
save.zhTip=保存
save.enTip=Save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Empty
clear.Tip=清空
clear.zhTip=清空
clear.enTip=Empty
clear.M=C
clear.Action=onClear
clear.pic=clear.gif


close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

ekt.Type=TMenuItem
ekt.Text=医疗卡读卡
ekt.zhText=医疗卡读卡
ekt.enText=
ekt.Tip=医疗卡读卡
ekt.zhTip=医疗卡读卡
ekt.enTip=
ekt.Action=onEKT
ekt.pic=042.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.enText=Refresh
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif