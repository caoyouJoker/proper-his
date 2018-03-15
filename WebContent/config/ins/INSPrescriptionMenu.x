#
# TBuilder Config File 
#
# Title:外购处方
#
# Company: ProperSoft
#
# Author:yufh 2014.04.08
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;upload;|;delete;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;upload;|;delete;|;clear;|;close


query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

upload.Type=TMenuItem
upload.Text=上传
upload.Tip=上传
upload.M=S
upload.key=Ctrl+S
upload.Action=onUpload
upload.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
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
