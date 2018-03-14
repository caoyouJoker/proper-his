 #
  # Title: 电生理叫号
  #
  # Description: 电生理叫号
  #
  # Copyright: JavaHis (c) 
  #
  # @author luhai 2012.01.25
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;read;|;regist;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;close


query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

read.Type=TMenuItem
read.Text=读卡
read.Tip=读卡
read.M=P
read.Action=onRead
read.pic=042.gif

regist.Type=TMenuItem
regist.Text=报到
regist.Tip=报到
regist.M=E
regist.Action=onRegist
regist.pic=operation.gif