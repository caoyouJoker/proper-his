<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;upload;print;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=print;query;upload;|;clear;|;close

print.Type=TMenuItem
print.Text=打印
print.Tip=打印(Ctrl+P)
print.M=S
print.key=Ctrl+S
print.Action=onPrint
print.pic=print.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

upload.Type=TMenuItem
upload.Text=上传
upload.Tip=上传
upload.M=I
upload.key=Ctrl+U
upload.Action=onUpload
upload.pic=CommitOne.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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

