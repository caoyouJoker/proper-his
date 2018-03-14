 #
  # Title: 临床路径展开
  #
  # Description: 临床路径展开
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author luhai 2010.05.17
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;deployExecute;|;deployPractice;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;deployExecute;|;deployPractice;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

deployPractice.Type=TMenuItem
deployPractice.Text=路径展开
deployPractice.Tip=路径展开
deployPractice.M=A
deployPractice.key=
deployPractice.Action=deployPractice
deployPractice.pic=spreadout.gif

revertPractice.Type=TMenuItem
revertPractice.Text=还原实际
revertPractice.Tip=还原实际
revertPractice.M=A
revertPractice.key=
revertPractice.Action=returnPractice
revertPractice.pic=Redo.gif

deployStandard.Type=TMenuItem
deployStandard.Text=展开标准
deployStandard.Tip=展开标准
deployStandard.M=A
deployStandard.key=
deployStandard.Action=deployStandard
deployStandard.pic=search-2.gif

deployExecute.Type=TMenuItem
deployExecute.Text=展开执行护嘱
deployExecute.Tip=展开执行护嘱
deployExecute.M=A
deployExecute.key=
deployExecute.Action=nurseWorkListOpen
deployExecute.pic=023.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
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


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif