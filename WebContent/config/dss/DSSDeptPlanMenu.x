 #
 # Title: 科室绩效
 #
 # Description: 科室绩效
 #
 # Copyright: JavaHis (c) 2009
 #
 # @author sundx 2009.10.15
 # @version 1.0
 
 <Type=TMenuBar>
 UI.Item=File;Window
 UI.button=save;new;pinyin;delete;|;clear;|;close
 
 Window.Type=TMenu
 Window.Text=窗口
 Window.M=W
 Window.Item=Refresh

 File.Type=TMenu
 File.Text=文件
 File.M=F
 File.Item=save;new;delete;|;clear;|;close

 save.Type=TMenuItem
 save.Text=保存
 save.Tip=保存(Ctrl+S)
 save.M=S
 save.key=Ctrl+S
 save.Action=onSave
 save.pic=save.gif

 new.Type=TMenuItem
 new.Text=新增
 new.Tip=新增(Ctrl+N)
 new.M=N
 new.key=Ctrl+N
 new.Action=onNew
 new.pic=039.gif

 delete.Type=TMenuItem
 delete.Text=删除
 delete.Tip=删除(Delete)
 delete.M=N
 delete.key=Delete
 delete.Action=onDelete
 delete.pic=delete.gif

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
 
 pinyin.Type=TMenuItem
 pinyin.Text=保存
 pinyin.Tip=保存(Ctrl+P)
 pinyin.M=S
 pinyin.key=Ctrl+P
 pinyin.Action=onPinyin
 pinyin.pic=save.gif

 Refresh.Type=TMenuItem
 Refresh.Text=刷新
 Refresh.Tip=刷新
 Refresh.M=R
 Refresh.key=F5
 Refresh.Action=onReset
 Refresh.pic=Refresh.gif