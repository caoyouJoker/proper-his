 #
  # Title: Ò½ÁÆ¿¨¹ÒÊ§/²¹¿¨
  #
  # Description: Ò½ÁÆ¿¨¹ÒÊ§/²¹¿¨
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author pangben 20111007
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;cardprint;|;updateEKTpwd;|;clear;|;close

Window.Type=TMenu
Window.Text=´°¿Ú
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=ÎÄ¼þ
File.M=F
File.Item=save;|;query;|;renew;|;cardprint;|;updateEKTpwd;|;clear;|;close

save.Type=TMenuItem
save.Text=±£´æ
save.Tip=±£´æ(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onRenew
save.pic=save.gif

query.Type=TMenuItem
query.Text=²¡»¼²éÑ¯
query.Tip=²¡»¼²éÑ¯(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

cardprint.Type=TMenuItem
cardprint.Text=¿¨Æ¬´òÓ¡
cardprint.Tip=¿¨Æ¬´òÓ¡
cardprint.M=D
cardprint.key=Ctrl+D
cardprint.Action=onPrint
cardprint.pic=print.gif

clear.Type=TMenuItem
clear.Text=Çå¿Õ
clear.Tip=Çå¿Õ(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=ÍË³ö
close.Tip=ÍË³ö(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

updateEKTpwd.Type=TMenuItem
updateEKTpwd.Text=Ò½ÁÆ¿¨ÐÞ¸ÄÃÜÂë
updateEKTpwd.Tip=Ò½ÁÆ¿¨ÐÞ¸ÄÃÜÂë
updateEKTpwd.M=U
updateEKTpwd.Action=updateEKTPwd
updateEKTpwd.pic=007.gif