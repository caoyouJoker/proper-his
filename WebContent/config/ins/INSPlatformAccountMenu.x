#
  # Title: 异地联网与部平台对账下载
  #
  # Description:异地联网与部平台对账下载
  # Copyright: ProperSoft(c) 2017
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;account;|;download;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;account;|;download;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

account.Type=TMenuItem
account.Text=对账
account.Tip=对账
account.M=A
account.Action=onAccount
account.pic=sta-1.gif

download.Type=TMenuItem
download.Text=下载
download.Tip=下载
download.M=U
download.Action=onDownload
download.pic=Commit.gif

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

