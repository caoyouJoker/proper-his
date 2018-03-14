#
  # Title: 预约挂号月统计表
  #
  # Description:预约挂号月统计表
  #
  # Copyright: Bluecore (c) 2013
  #
  # @author huangtt
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;clear;|;close;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;export;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=O
print.key=F10
print.Action=onPrint
print.pic=print.gif

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

SavePESResult.Type=TMenuItem
SavePESResult.Text=点评结论
SavePESResult.Tip=点评结论
SavePESResult.M=M
SavePESResult.key=F11
SavePESResult.Action=onSavePESResult
SavePESResult.pic=correct.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif