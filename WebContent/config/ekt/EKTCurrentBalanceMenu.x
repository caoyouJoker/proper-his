 #
  # Title: Ò½ÁÆ¿¨Óà¶î²éÑ¯
  #
  # Description: Ò½ÁÆ¿¨Óà¶î²éÑ¯
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangp 2011.12.26
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;card;|;clear;|;close

Window.Type=TMenu
Window.Text=´°¿Ú
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=ÎÄ¼þ
File.M=F
File.Item=query;|;card;|;clear;|;close

EKTprint.Type=TMenuItem
EKTprint.Text=´òÓ¡
EKTprint.Tip=´òÓ¡(Ctrl+P)
EKTprint.M=P
EKTprint.key=Ctrl+P
EKTprint.Action=onPrint
EKTprint.pic=print.gif

close.Type=TMenuItem
close.Text=ÍË³ö
close.Tip=ÍË³ö(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

query.Type=TMenuItem
query.Text=²éÑ¯
query.Tip=²éÑ¯
query.M=Y
query.key=
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=Çå¿Õ
clear.Tip=Çå¿Õ(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

card.Type=TMenuItem
card.Text=¶Á¿¨
card.Tip=¶Á¿¨
card.M=D
card.key=
card.Action=onEKTcard
card.pic=042.gif