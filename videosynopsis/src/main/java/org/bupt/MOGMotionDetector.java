package org.bupt;


import static org.bytedeco.javacpp.helper.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_core.CV_WHOLE_SEQ;
import static org.bytedeco.javacpp.opencv_highgui.cvConvertImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_EXTERNAL;
import static org.bytedeco.javacpp.opencv_imgproc.CV_SHAPE_RECT;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvBoundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.cvContourArea;
import static org.bytedeco.javacpp.opencv_imgproc.cvCreateStructuringElementEx;
import static org.bytedeco.javacpp.opencv_imgproc.cvDilate;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplConvKernel;
import org.bytedeco.javacpp.opencv_core.IplImage;
//import org.bytedeco.javacv.Blobs;
//import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_video.BackgroundSubtractorMOG2;
class MOGMotionDetector {

	//private BackgroundSubtractorMOG mog = new BackgroundSubtractorMOG();
	public BackgroundSubtractorMOG2 mog2 = new BackgroundSubtractorMOG2();
	private final int MIN_RECT_AREA = 200;
	public List<CvRect> getMotionRect(IplImage frame,IplImage foreground , IplImage inputFrame) {
        List<CvRect> rectList = new ArrayList<CvRect>();
	
		cvConvertImage(frame, inputFrame, 0);
		
		CvMemStorage storage = CvMemStorage.create();
		CvRect boundbox;
		Mat inputFrame_mat= new Mat(inputFrame) ;
		Mat foreground_mat= new Mat(foreground) ;
		 mog2.apply(inputFrame_mat,foreground_mat,0.001);
		 inputFrame= inputFrame_mat.asIplImage();
		 foreground=foreground_mat.asIplImage();
		//mog2.getBackgroundImage(arg0);
		cvThreshold(foreground, foreground, 128, 255, CV_THRESH_BINARY);

		
		int kernelSize =6;
		int KernelAnchorOffset =3;
		IplConvKernel kernel = cvCreateStructuringElementEx(kernelSize,
				kernelSize, KernelAnchorOffset, KernelAnchorOffset,
				CV_SHAPE_RECT);
		cvDilate(foreground, foreground, kernel, 1);// 5*5缁撴瀯鑶ㄨ儉

		CvSeq contour = new CvSeq(null);
		cvFindContours(foreground, storage, contour,
				Loader.sizeof(CvContour.class), CV_RETR_EXTERNAL,
				CV_CHAIN_APPROX_SIMPLE);

		//contour = cvApproxPoly( contour, Loader.sizeof(CvContour.class), storage, CV_POLY_APPROX_DP, 3, 1 );
		
		while (contour != null && !contour.isNull()) {
			if (contour.elem_size() > 0) {
				if (cvContourArea(contour, CV_WHOLE_SEQ, 0) > MIN_RECT_AREA) {
					boundbox = cvBoundingRect(contour, 0);
					rectList.add(boundbox);
				}
			}
			contour = contour.h_next();
		}
	
	
		
		return rectList;
	}
	
	
}