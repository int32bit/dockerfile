package org.bupt;


import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvAnd;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvFillConvexPoly;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvZero;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
class TrackUtils {

	public static IplImage getROI(IplImage src,ArrayList<Point> list_point) {
		//System.out.println("11111111111111111111111111111111111111");
		IplImage res = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 3);
		IplImage roi = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 1);
		cvZero(roi);
		cvZero(res);

		
	
	/*	CvPoint a = new CvPoint(5);
		a.position(0).x(186).y(59);
		a.position(1).x(407).y(61);
		a.position(2).x(550).y(332);
		a.position(3).x(2).y(334);
		a.position(4).x(0).y(192);*/
	//	ArrayList<Point> list_point ;
		//list_point = new ArrayList<Point>();
		//list_point.size();
		
		
		CvPoint a = new CvPoint(list_point.size());
		for(int i=0;i<list_point.size();i++)
		{
			a.position(i).x(list_point.get(i).x).y(list_point.get(i).y);

		}
	
		
	/*
	    a.position(0).x(205).y(44);
		a.position(1).x(387).y(43);
		a.position(2).x(596).y(322);
		a.position(3).x(3).y(317);
	    */
		
		cvFillConvexPoly(roi, a.position(0), 4, CvScalar.WHITE, 8, 0);
		// Extract Sub Image
		cvAnd(src, src, res, roi);
		cvReleaseImage(roi);
		return res;
	}
	
	public static void saveRectAndImage(TrackObject object, int frameCount, String fileRoot, String videoName,String vn) {

		List<CvRect> trajectory = object.getTrajectory();

		List<IplImage> trajectoryRoi = object.getTrajectoryRoi();
		
		String videoRoot = fileRoot + "\\" + videoName;
		File videoFile = new File(videoRoot);
		videoFile.mkdir();
		String filename = "object_" + object.getEnterFrame() + "_"
				+ object.getEndFrame() + "_random" + random();
		File file = new File(videoRoot + "/" + filename);
		file.mkdir();
		File info = new File(videoRoot + "/" + filename + "/info.txt");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(info));
			bw.write("startFrame:" + object.getEnterFrame() + "\r\n");
			bw.write("endFrame:" + object.getEndFrame() + "\r\n");
			bw.write("RectSize:" + trajectory.size() + "\r\n");
			bw.write("ImageSize:" + trajectoryRoi.size() + "\r\n");
			String traceStr = "";
			for (int i = 0; i < trajectory.size(); i++) {
				CvRect trace = trajectory.get(i);
				String traceStrTemp = trace.x() + "," + trace.y() + ","
						+ trace.width() + "," + trace.height();
				if (i == trajectory.size() - 1) {
					traceStr = traceStr + traceStrTemp;
				} else {
					traceStr = traceStr + traceStrTemp + "|";
				}

			}
			bw.write(traceStr);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int j = 0; j < trajectoryRoi.size(); j++) {
			int count = j + object.getEnterFrame();
			CvRect trace = trajectory.get(j);
			// ground truth
			String traceStrTemp = trace.x() + "," + trace.y() + ","
					+ trace.width() + "," + trace.height();
			String roiName = "/" + count+"_"+vn + "_" + traceStrTemp + "_roi_random" + random() + ".jpg";
			IplImage roiImg = trajectoryRoi.get(j);
			cvSaveImage(videoRoot + "\\" + filename + roiName, roiImg);
			//cvSaveImage("D:\\selected\\11.jpg", roiImg);
			cvReleaseImage(roiImg);
		}
		

	}
	
	private static int random() {
		int x = (int)(Math.random()*10000);
		return x;
	}
}
