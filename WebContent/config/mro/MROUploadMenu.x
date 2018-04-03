<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;upload;|;cancle;|;close

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=query;|;clear;|;upload;|;cancle;|;close

query.Type=TMenuItem
query.Text=查询
query.zhText=查询
query.enText=Query
query.Tip=查询
query.zhTip=查询
query.enTip=Query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Clear
clear.Tip=清空
clear.zhTip=清空
clear.enTip=Clear
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

upload.Type=TMenuItem
upload.Text=生成XML文件
upload.zhText=生成XML文件
upload.enText=CreatXML
upload.Tip=生成XML文件
upload.zhTip=生成XML文件
upload.enTip=CreatXML
upload.M=U
upload.Action=onUpload
upload.pic=046.gif

cancle.Type=TMenuItem
cancle.Text=取消生成
cancle.zhText=取消生成
cancle.enText=cancel
cancle.Tip=取消生成
cancle.zhTip=取消生成
cancle.enTip=cancel
cancle.M=Z
cancle.Action=onCancle
cancle.pic=Undo.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif