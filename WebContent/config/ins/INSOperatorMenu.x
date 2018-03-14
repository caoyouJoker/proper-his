#
  # Title: 单病种费用分割中病案首页之手术资料
  #
  # Description:单病种费用分割中病案首页之手术资料
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2012-2-12
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=close


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
