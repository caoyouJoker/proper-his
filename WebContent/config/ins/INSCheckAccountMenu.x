#
  # Title: 门诊对账
  #
  # Description:门诊对账：自动对账 总对账 明细对账 对账确认
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2012-1-8
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=autoSave;|;selectMrNo;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=autoSave;|;selectMrNo;|;clear;|;close

autoSave.Type=TMenuItem
autoSave.Text=自动对账
autoSave.Tip=自动对账
autoSave.M=S
autoSave.key=Ctrl+S
autoSave.Action=onAutoSave
autoSave.pic=046.gif

selectMrNo.Type=TMenuItem
selectMrNo.Text=查询病患信息
selectMrNo.Tip=查询病患信息
selectMrNo.M=N
selectMrNo.Action=onQueryMrNo
selectMrNo.pic=detail-1.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif