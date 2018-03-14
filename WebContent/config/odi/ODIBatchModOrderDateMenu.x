#
# Title: 批量修改一期临床医嘱时间
# Description:批量修改一期临床医嘱时间
# Copyright: JavaHis (c) 2016
# @author wangb
# @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;modDate;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;modDate;|;close

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

modDate.Type=TMenuItem
modDate.Text=自动修改时间
modDate.Tip=自动修改时间(Ctrl+M)
modDate.M=M
modDate.key=Ctrl+M
modDate.Action=onModifyDate
modDate.pic=convert.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif