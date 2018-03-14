 #
  # Title: 采购计划
  #
  # Description:采购计划
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009-4-28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;query;|;clear;|;export;|;make;|;plan;|;analyse;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;delete;|;query;|;clear;|;export;|;make;|;plan;|;analyse;|;close

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

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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

export.Type=TMenuItem
export.Text=引用
export.Tip=引用
execrpt.M=E
export.Action=onExport
export.pic=export.gif

make.Type=TMenuItem
make.Text=生成
make.Tip=生成
make.M=M
make.Action=onMake
make.pic=045.gif

plan.Type=TMenuItem
plan.Text=计划单
plan.Tip=计划单
plan.M=P
plan.Action=onPlan
plan.pic=047.gif

analyse.Type=TMenuItem
analyse.Text=差异分析
analyse.Tip=差异分析
analyse.M=A
analyse.Action=onAnalyse
analyse.pic=039.gif