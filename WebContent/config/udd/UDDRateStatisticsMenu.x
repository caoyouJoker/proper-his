<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;export;|;close
Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;export;|;close



query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
execrpt.M=E
export.Action=onExport
export.pic=exportexcel.gif