 #
  # Title: 临床路径设定
  #
  # Description: 临床路径设定
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author Zhangjg 2011.04.11
 # @version 1.0
 #20140822 liling add  release发布   revise修订
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;print;revise;release;|;export;|;allExport;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;query;print;export;|;allExport;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

use.Type=TMenuItem
use.Text=启用
use.Tip=启用(Ctrl+U)
use.M=U
use.key=Ctrl+U
use.Action=onUse
use.pic=save-1.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

revise.Type=TMenuItem
revise.Text=修订
revise.Tip=修订
revise.Action=onRevise
revise.pic=026.gif

allExport.Type=TMenuItem
allExport.Text=导出全部
allExport.Tip=导出全部
allExport.Action=onAllExport
allExport.pic=045.gif

release.Type=TMenuItem
release.Text=发布
release.Tip=发布
release.Action=onRelease
release.pic=007.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=(Ctrl+P)
print.Action=onPrint
print.pic=print.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出
execrpt.M=E
export.Action=onExport
export.pic=exportexcel.gif
