 #
  # Title: 抗菌药物临床应用监测
  #
  # Description:抗菌药物临床应用监测
  #
  # Copyright: JavaHis (c) 2012
  #
  # @author yuanxm
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;export;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;clear;|;export;|;close;

save.Type=TMenuItem
save.Text=报到
save.Tip=报到
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=N
query.key=Query
query.Action=onQuery
query.pic=query.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=
export.Action=onExport
export.pic=exportexcel.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
