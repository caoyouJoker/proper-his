<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;photo;|;dbfConvert;|;upload;|;preview;|;delete;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;photo;|;dbfConvert;|;upload;|;preview;|;delete;|;clear;|;close

query.Type=TMenuItem
query.Text=病患查询
query.Tip=病患查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=search-1.gif

photo.Type=TMenuItem
photo.Text=拍照
photo.Tip=拍照
photo.M=Q
photo.key=Ctrl+F
photo.Action=onPhoto
photo.pic=print-1.gif

dbfConvert.Type=TMenuItem
dbfConvert.Text=上传
dbfConvert.Tip=上传
dbfConvert.M=P
dbfConvert.Action=onCombination
dbfConvert.pic=046.gif

upload.Type=TMenuItem
upload.Text=提交
upload.Tip=提交
upload.M=I
upload.key=Ctrl+D
upload.Action=onUpload
upload.pic=Commit.gif

preview.Type=TMenuItem
preview.Text=浏览完整病历
preview.Tip=浏览完整病历
preview.M=I
preview.key=Ctrl+D
preview.Action=onReadSubmit
preview.pic=012.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=D
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

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

