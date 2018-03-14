#
  # Title: 票据管理
  #
  # Description:票据开关账
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;Closedata;|;returnback;|;Adjustment;|;clear;|;close;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;Closedata;|;clear;|;close;|;returnback;|;Adjustment

save.Type=TMenuItem
save.Text=开账
save.Tip=开账
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=openbill.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Closedata.Type=TMenuItem
Closedata.Text=关帐
Closedata.Tip=关帐
Closedata.M=L
Closedata.key=F11
Closedata.Action=onClosedata
Closedata.pic=closebill.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

Recipients.Type=TMenuItem
Recipients.Text=领票
Recipients.Tip=领票
Recipients.M=T
Recipients.key=F9
Recipients.Action=Recipients
Recipients.pic=openbill-2.gif

returnback.Type=TMenuItem
returnback.Text=交回
returnback.Tip=交回
returnback.M=O
returnback.key=F10
returnback.Action=returnback
returnback.pic=closebill-2.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Adjustment.Type=TMenuItem
Adjustment.Text=调整票号
Adjustment.Tip=调整票号
Adjustment.M=M
Adjustment.key=F11
Adjustment.Action=onAdjustment
Adjustment.pic=correct.gif