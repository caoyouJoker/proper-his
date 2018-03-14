 #
 # Title: 病患历次医保数据
 #
 # Description: 病患历次医保数据
 #
 # Copyright: BlueCore (c) 2012
 #
 # @author WangLong 2012.09.21
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;card;|;clear;|;close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;card;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

card.Type=TMenuItem
card.Text=读卡
card.Tip=读卡
card.M=D
card.key=
card.Action=onReadEKT
card.pic=042.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.Action=onQuery
query.pic=query.gif

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

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif