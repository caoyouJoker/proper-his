 #
  # Title: 药库库存主档
  #
  # Description:药库库存主档
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.04.29
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;clear;|;close

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

toExcel.Type=TMenuItem
toExcel.Text=引入电子标签货位关系
toExcel.zhText=引入电子标签货位关系
toExcel.enText=引入电子标签货位关系
toExcel.Tip=引入电子标签货位关系
toExcel.zhTip=引入电子标签货位关系
toExcel.enTip=引入电子标签货位关系
toExcel.M=S
toExcel.key=Ctrl+E
toExcel.Action=onImpXML
toExcel.pic=export.gif