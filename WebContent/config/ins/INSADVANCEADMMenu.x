<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;upload;|;download;|;resultload;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;upload;|;download;|;resultload;|;clear;|;close


upload.Type=TMenuItem
upload.Text=申请上传
upload.Tip=申请上传
upload.M=I
upload.key=Ctrl+D
upload.Action=onUpload
upload.pic=Save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

download.Type=TMenuItem
download.Text=申请下载
download.Tip=申请下载
download.M=I
download.key=Ctrl+D
download.Action=onDownload
download.pic=Commit.gif

resultload.Type=TMenuItem
resultload.Text=结果上传
resultload.Tip=结果上传
resultload.M=S
resultload.key=Ctrl+S
resultload.Action=onResultload
resultload.pic=046.gif

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

