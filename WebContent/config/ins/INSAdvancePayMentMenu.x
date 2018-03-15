#
  # Title: 住院垫付费用明细上传
  # Description:住院垫付费用明细上传
  # Copyright: ProperSoft(c) 2017
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;download;|;sumdetail;|;upload;|;cancel;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;download;|;sumdetail;|;upload;|;cancel;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

download.Type=TMenuItem
download.Text=信息下载
download.Tip=信息下载
download.M=U
download.Action=onDownload
download.pic=Commit.gif

sumdetail.Type=TMenuItem
sumdetail.Text=明细汇总
sumdetail.Tip=明细汇总
sumdetail.M=A
sumdetail.Action=onSumdetail
sumdetail.pic=sta-1.gif


upload.Type=TMenuItem
upload.Text=费用上传
upload.Tip=费用上传
upload.M=U
upload.Action=onUpload
upload.pic=016.gif


cancel.Type=TMenuItem
cancel.Text=费用撤销
cancel.Tip=费用撤销
cancel.M=U
cancel.Action=onCancel
cancel.pic=030.gif

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

