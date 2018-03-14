 #
  # Title: CDR综合查询菜单
  #
  # Description:
  #
  # Copyright: Bluecore (c) 2015
  #
  # @author wangb 2015.5.17
 # @version 1.0
<Type=TMenuBar>
UI.Item=File
UI.button=query;|;close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif