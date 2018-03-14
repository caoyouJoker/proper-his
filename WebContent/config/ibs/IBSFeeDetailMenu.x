<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出(Ctrl+E)
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

