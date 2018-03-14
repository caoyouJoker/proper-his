# 
#  Title:µøµ¹¡¢ÌÛÍ´ÆÀ¹À
# 
#  Description:µøµ¹¡¢ÌÛÍ´ÆÀ¹À
# 
#  Copyright: Copyright (c) Javahis 2015
# 
#  author wangqing 20180119
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;close

Window.Type=TMenu
Window.Text=´°¿Ú
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=ÎÄ¼þ
File.M=F
File.Item=save;close

save.Type=TMenuItem
save.Text=±£´æ
save.Tip=±£´æ
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=Çå¿Õ
clear.Tip=Çå¿Õ
clear.M=E
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=¹Ø±Õ
close.Tip=¹Ø±Õ
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

test.Type=TMenuItem
test.Text=²âÊÔ
test.Tip=²âÊÔ
test.M=E
test.key=Ctrl+E
test.Action=onTest
test.pic=clear.gif



