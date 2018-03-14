 #
  # Title: 张建国 关键诊疗项目
  #
  # Description: 张建国 关键诊疗项目
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author Zhangjg 2011.04.11
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;clear;preview;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;query;clear;preview;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

preview.Type=TMenuItem
preview.Text=临床路径模板
preview.Tip=临床路径模板(Ctrl+P)
preview.M=P
preview.key=Ctrl+P
preview.Action=onPreview
preview.pic=025.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif