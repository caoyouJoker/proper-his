 #
  # Title: 麻精备药入智能柜Menu
  #
  # Description:麻精备药入智能柜Menu
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author shendr 2013.08.07
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;open;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;clear;|;open;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


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
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onCloseCabinet
close.pic=close.gif


open.Type=TMenuItem
open.Text=智能柜查询
open.Tip=智能柜查询
open.M=C
open.Action=onSearch
open.pic=search-2.gif