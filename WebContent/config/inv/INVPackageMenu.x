 #
  # Title: 供应室入库
  #
  # Description:供应室入库
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;new;|;query;|;print;|;barcode;|;clear;|;picture;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;new;|;query;|;print;|;barcode;|;clear;|;picture;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onUpdate
save.pic=save.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=打印入库单
print.Tip=打印入库单
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

barcode.Type=TMenuItem
barcode.Text=条码
barcode.Tip=打印条码
barcode.M=T
barcode.key=Ctrl+T
barcode.Action=onBarcode
barcode.pic=barcode.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

picture.Type=TMenuItem
picture.Text=查看图片
picture.Tip=查看图片
picture.M=J
picture.key=Ctrl+J
picture.Action=onPicture
picture.pic=Picture.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif