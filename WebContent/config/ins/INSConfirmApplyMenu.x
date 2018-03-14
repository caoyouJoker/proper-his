#
  # Title: 资格确认书下载/开立
  #
  # Description:资格确认书下载/开立
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2011-11-30
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;resvNClose;|;admNClose;|;clear;|;delete;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;resvNClose;|;admNClose;|;clear;|;delete;|;close

save.Type=TMenuItem
save.Text=资格确认书下载/开立
save.Tip=资格确认书下载/开立
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=Commit.gif

resvNClose.Type=TMenuItem
resvNClose.Text=预约未结案
resvNClose.Tip=预约未结案
resvNClose.M=N
resvNClose.Action=onResvNClose
resvNClose.pic=046.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

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

admNClose.Type=TMenuItem
admNClose.Text=住院未结案
admNClose.Tip=住院未结案
admNClose.M=
admNClose.Action=onAdmNClose
admNClose.pic=046.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif