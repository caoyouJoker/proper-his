 #
  # Title: 住院腕带打印
  #
  # Description: 住院腕带打印操作
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author huangjw 20150304
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=close

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
