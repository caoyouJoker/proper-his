package com.javahis.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ClipboardTool {
    public ClipboardTool() {
    }

    public static void run(String gifName) throws Exception {
        ClipboardTool tool = new ClipboardTool();
        Image image = tool.getImageFromClipboard();
        tool.compressPic(image, gifName);
    }

    // 图片处理
    public void compressPic(Image img, String gifName) throws Exception {

        // 判断图片格式是否正确
        if (img == null || img.getWidth(null) == -1) {
            throw new Exception("当前剪帖板中没有图片片段。或者片段生成方式不能复制某个图片文件而是截取屏幕等。");

        }
        int newWidth;
        int newHeight;
        newWidth = img.getWidth(null); // 输出的图片宽度
        newHeight = img.getHeight(null); // 输出的图片高度
        BufferedImage tag = new BufferedImage( (int) newWidth, (int) newHeight,
                                              BufferedImage.TYPE_INT_RGB);

        /*
         * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
         */
        tag.getGraphics().drawImage(
            img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH),
            0, 0, null);

        jpgToGif(tag, gifName);

    }

    /**
     * 剪帖板获取图片
     *
     * @return
     * @throws Exception
     */
    public Image getImageFromClipboard() throws Exception {
        Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable cc = sysc.getContents(null);

        if (cc == null) {
            return null;
        }
        else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            return (Image) cc.getTransferData(DataFlavor.imageFlavor);
        }
        return null;
    }

    /**
     * jpg格式转gif
     *
     * @param image
     *            BufferedImage 对象
     * @param newPic
     *            新图片存储路径
     */
    public synchronized void jpgToGif(BufferedImage image, String newPic) {
        try {
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.setRepeat(0);
            e.start(newPic);

            e.setDelay(200); // 设置播放的延迟时间
            e.addFrame(image); // 添加到帧中
            e.finish(); // 刷新任何未决的数据，并关闭输出文件
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
