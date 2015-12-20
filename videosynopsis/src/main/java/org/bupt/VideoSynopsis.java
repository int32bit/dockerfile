package org.bupt;

import java.awt.Point;
import java.util.ArrayList;

public class VideoSynopsis {
	public static void main(String[] args) {
		if (args.length != 7) {
			System.out.println("请输入参数");
		}
		ArrayList<Point> list_point = new ArrayList<Point>();
		String videolocatedfile, tempfile = null, CVlocatedfile, CVname, ConcentrateEXEfile, tempfile2;
		int ysb, chd;
		videolocatedfile = args[0];// 原始监控视频存放文件夹，暂时先存放一个 "E:\\Test\\12"
		tempfile = args[1];// 差分完的中间数据存放的文件夹，浓缩完成后可以删除 "E:\\Test\\objects"
		CVlocatedfile = args[2]; // /*浓缩后的视频存放地址*/"E:\\Test\\concentrate\\"
		CVname = args[3];// /*浓缩后视频的名字*/"12TTTTTT.avi"
		ConcentrateEXEfile = args[4];// /*压缩程序的所在目录和名字*/
		ysb = Integer.parseInt(args[5]);// /*压缩比，建议10-20*/
		chd = Integer.parseInt(args[6]);/* 避免碰撞的概率，为1时完全避免 */

		String[] name = tempfile.split("\\");
		tempfile2 = tempfile + name[name.length - 1];
		Point p0 = new Point(205, 44);
		Point p1 = new Point(387, 43);
		Point p2 = new Point(596, 322);
		Point p3 = new Point(3, 317);
		list_point.add(p0);
		list_point.add(p1);
		list_point.add(p2);
		list_point.add(p3);
		// 视频差分过程
		Tracking tc = new Tracking(videolocatedfile, tempfile, list_point);
		tc.begin();
		// 视频浓缩过程
		Concentrate con = new Concentrate(tc.getBG(), tempfile2,CVlocatedfile, CVname,ConcentrateEXEfile, ysb, chd);
		// System.out.println("背景的长度 "+ tc.getBG().length);
		con.sethash(tc.getHash());
		con.start(tc.getBG());
	}

}
