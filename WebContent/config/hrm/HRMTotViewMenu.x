 #
  # Title: HRM总检
  #
  # Description:HRM总检
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Edit;Window;Report
UI.button=save;query;unfold;|;crawl;|;InsertLCSJ;InsertPY;deleteRow;|;print;|;PrintClear;|;ClearMenu;|;ModifyFontCombo;ModifyFontSizeCombo;AlignmentLeft;AlignmentCenter;AlignmentRight;FontBMenu;FontIMenu;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh;EditWord;|;Preview

Edit.Type=TMenu
Edit.Text=编辑
Edit.M=E
Edit.Item=InsertTableBase;DelTable;|;InsertPanel;deleteRow;|;TableProperty;TRProperty;UniteTD;|;ParagraphEdit;DIVProperty;InsertPic;DeletePic;PicDIVProperty;openFixedProperty;RemoveFixed

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;query;unfold;|;crawl;crawls;|;insertResult;|;InsertLCSJ;InsertPY;|;PrintClear;|;print;|;ClearMenu;|;clear;|;close

Report.Type=TMenu
Report.Text=报告/结果
Report.zhText=报告/结果
Report.enText=Report
Report.M=R
Report.Item=labReport;imageReport;eccReport

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=N
query.key=F5
query.Action=onQuery
query.pic=query.gif

unfold.Type=TMenuItem
unfold.Text=总检展开
unfold.Tip=总检展开
unfold.M=S
unfold.key=Ctrl+U
unfold.Action=onUnfold
unfold.pic=048.gif

crawl.Type=TMenuItem
crawl.Text=总检抓取
crawl.Tip=总检抓取
crawl.M=S
crawl.key=Ctrl+W
crawl.Action=onCrawl
crawl.pic=008.gif

crawls.Type=TMenuItem
crawls.Text=总检部分抓取
crawls.Tip=总检部分抓取
crawls.M=S
crawls.key=Ctrl+W
crawls.Action=onCrawls
crawls.pic=convert.gif

InsertLCSJ.Type=TMenuItem
InsertLCSJ.Text=临床数据
InsertLCSJ.Tip=临床数据
InsertLCSJ.M=S
InsertLCSJ.key=Ctrl+J
InsertLCSJ.Action=onInsertLCSJ
InsertLCSJ.pic=053.gif

InsertPY.Type=TMenuItem
InsertPY.Text=片语
InsertPY.Tip=片语
InsertPY.M=S
InsertPY.key=Ctrl+Y
InsertPY.Action=onInsertPY
InsertPY.pic=Line.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=C
print.Action=onPrint
print.pic=print.gif

ClearMenu.Type=TMenuItem
ClearMenu.Text=清空剪贴板
ClearMenu.Tip=清空剪贴板
ClearMenu.M=v
ClearMenu.Action=onClearMenu
ClearMenu.Key=
ClearMenu.pic=001.gif

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

deleteRow.type=TMenuItem
deleteRow.Text=删除行
deleteRow.Tip=删除行
deleteRow.M=I
deleteRow.key=
deleteRow.Action=onDelTableRow
deleteRow.pic=cancle.gif

DelTable.type=TMenuItem

DelTable.Text=删除表格

DelTable.Tip=删除表格

DelTable.M=I

DelTable.key=
DelTable.Action=onDelTable
DelTable.pic=delete.gif

insertResult.Type=TMenuItem
insertResult.Text=检验检查结果传回
insertResult.Tip=检验检查结果传回
insertResult.M=
insertResult.key=
insertResult.Action=onInsertLisRisResult
insertResult.pic=LIS.gif

exportword.Type=TMenuItem
exportword.Text=子模板
exportword.Tip=子模板
exportword.M=Z
exportword.key=Ctrl+Z
exportword.Action=onSubTemplate
exportword.pic=exportword.gif

ModifyFontCombo.type=TFontCombo
ModifyFontSizeCombo.type=TFontSizeCombo

AlignmentLeft.type=TMenuItem
AlignmentLeft.text=居左
AlignmentLeft.tip=居左
AlignmentLeft.Action=onAlignmentLeft
AlignmentLeft.pic=Left.gif

AlignmentCenter.type=TMenuItem
AlignmentCenter.text=居中
AlignmentCenter.tip=居中
AlignmentCenter.Action=onAlignmentCenter
AlignmentCenter.pic=Center.gif

AlignmentRight.type=TMenuItem
AlignmentRight.text=居右
AlignmentRight.tip=居右
AlignmentRight.Action=onAlignmentRight
AlignmentRight.pic=Right.gif

FontBMenu.type=TMenuItem
FontBMenu.text=粗体
FontBMenu.tip=粗体
FontBMenu.pic=B.gif

FontIMenu.type=TMenuItem
FontIMenu.text=斜体
FontIMenu.tip=斜体
FontIMenu.pic=I.gif

EditWord.Type=TMenuItem
EditWord.Text=编辑
EditWord.M=T
EditWord.Action=onEditWord

Preview.Type=TMenuItem
Preview.Text=预览
Preview.M=T
Preview.Action=onPreviewWord

InsertTableBase.Type=TMenuItem
InsertTableBase.Text=插入表格
InsertTableBase.M=T
InsertTableBase.Action=onInsertTableBase

TableProperty.Type=TMenuItem
TableProperty.Text=表格属性
TableProperty.M=T
TableProperty.Action=onTableProperty

TRProperty.Type=TMenuItem
TRProperty.Text=行属性
TRProperty.M=T
TRProperty.Action=onTRProperty

UniteTD.Type=TMenuItem
UniteTD.Text=合并单元格
UniteTD.M=T
UniteTD.Action=onUniteTD

InsertPanel.Type=TMenuItem
InsertPanel.Text=表格前插入空行
InsertPanel.M=T
InsertPanel.Action=onTableInsertPanel

ParagraphEdit.Type=TMenuItem
ParagraphEdit.Text=段落
ParagraphEdit.M=X
ParagraphEdit.Action=onParagraphEdit

DIVProperty.Type=TMenuItem
DIVProperty.Text=图层控制
DIVProperty.M=T
DIVProperty.Action=onDIVProperty

InsertPic.Type=TMenuItem
InsertPic.Text=插入图区
InsertPic.M=X
InsertPic.Action=onInsertPic

DeletePic.Type=TMenuItem
DeletePic.Text=删除图区
DeletePic.M=X
DeletePic.Action=onDeletePic

PicDIVProperty.Type=TMenuItem
PicDIVProperty.Text=图区图层控制
PicDIVProperty.M=T
PicDIVProperty.Action=onPicDIVProperty

openFixedProperty.Type=TMenuItem
openFixedProperty.Text=属性
openFixedProperty.M=R
openFixedProperty.Action=onOpenFixedProperty

RemoveFixed.Type=TMenuItem
RemoveFixed.Text=删除元素
RemoveFixed.M=R
RemoveFixed.Action=onRemoveFixed

labReport.Type=TMenuItem
labReport.Text=检验报告
labReport.zhText=检验报告
labReport.enText=labReport
labReport.Tip=检验报告
labReport.zhTip=检验报告
labReport.enTip=labReport
labReport.M=L
labReport.Action=onLis
labReport.pic=LIS.gif

imageReport.Type=TMenuItem
imageReport.Text=检查报告
imageReport.zhText=检查报告
imageReport.enText=imageReport
imageReport.Tip=检查报告
imageReport.zhTip=检查报告
imageReport.enTip=imageReport
imageReport.M=I
imageReport.Action=onRis
imageReport.pic=RIS.gif

eccReport.Type=TMenuItem
eccReport.Text=心电报告
eccReport.zhText=心电报告
eccReport.enText=eccReport
eccReport.Tip=心电报告
eccReport.zhTip=心电报告
eccReport.enTip=eccReport
eccReport.M=C
eccReport.Action=getPdfReport
eccReport.pic=PicData01.jpg

PrintClear.Type=TMenuItem
PrintClear.Text=整洁预览
PrintClear.zhText=整洁预览
PrintClear.enText=preview
PrintClear.Tip=整洁预览(Ctrl+Q)
PrintClear.zhTip=整洁预览(Ctrl+Q)
PrintClear.enTip=preview
PrintClear.M=N
PrintClear.key=Ctrl+Q
PrintClear.Action=onPrintClear
PrintClear.pic=Retrieve.gif