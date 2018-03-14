<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;upload;|;download;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;upload;|;download;|;clear;|;close

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
upload.key=Ctrl+S
upload.Action=onUpload
upload.pic=Save.gif

download.Type=TMenuItem
download.Text=下载
download.Tip=下载
download.M=I
download.key=Ctrl+D
download.Action=onDownload
download.pic=Commit.gif


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

