package org.bupt;


import static org.bytedeco.javacpp.helper.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_core.CV_WHOLE_SEQ;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvAnd;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvRectangleR;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_core.cvSub;
import static org.bytedeco.javacpp.opencv_highgui.cvConvertImage;
import static org.bytedeco.javacpp.opencv_highgui.cvCreateFileCapture;
import static org.bytedeco.javacpp.opencv_highgui.cvDestroyWindow;
import static org.bytedeco.javacpp.opencv_highgui.cvQueryFrame;
import static org.bytedeco.javacpp.opencv_highgui.cvReleaseCapture;
import static org.bytedeco.javacpp.opencv_highgui.cvShowImage;
import static org.bytedeco.javacpp.opencv_highgui.cvWaitKey;
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
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.IplConvKernel;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_highgui.CvCapture;

class Subtract {
	
	
	private final int MIN_RECT_AREA = 200;

	public List<CvRect> getMotionRect(IplImage input1,IplImage input2,IplImage input3) 
	{
        List<CvRect> rectList = new ArrayList<CvRect>();
	
		CvMemStorage storage = CvMemStorage.create();
		
		CvRect boundbox;
		IplImage dist = cvCreateImage(cvGetSize(input1),IPL_DEPTH_8U,1);
		IplImage dist1 = cvCreateImage(cvGetSize(input1),IPL_DEPTH_8U,1);
		IplImage dist2 = cvCreateImage(cvGetSize(input1),IPL_DEPTH_8U,1);
		
		cvSub(input1,input2,dist1);
		cvSub(input2,input3,dist2);
		
		cvAnd(dist1,dist2,dist);
		
		cvThreshold(dist, dist, 20, 255, CV_THRESH_BINARY);

		
	
		
		int kernelSize = 16;
		int KernelAnchorOffset =8;
		IplConvKernel kernel = cvCreateStructuringElementEx(kernelSize,
				kernelSize, KernelAnchorOffset, KernelAnchorOffset,
				CV_SHAPE_RECT);
		cvDilate(dist, dist, kernel, 1);// 5*5缁撴瀯鑶ㄨ儉
	
		
		 cvShowImage("000", dist); 
		
		

		CvSeq contour = new CvSeq(null);
		cvFindContours(dist, storage, contour,
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
	
	
	public static void main(String[] args) {
		IplImage frame = null;
		CvCapture capture = null;
		
		String path = "D:\\selected\\0.avi";
		capture = cvCreateFileCapture(path);
		
		
		frame = cvQueryFrame(capture);
		CvSize frameSize =cvSize(frame.width(), frame.height());
		IplImage input_gray = cvCreateImage(frameSize,IPL_DEPTH_8U,1);
	//	cvCvtColor(TrackUtils.getROI(frame), input_gray, CV_RGB2GRAY);  获取ROI时注释掉的
		
		frame = cvQueryFrame(capture);
		IplImage input1_gray = cvCreateImage(frameSize,IPL_DEPTH_8U,1);
		//cvCvtColor(TrackUtils.getROI(frame), input1_gray, CV_RGB2GRAY);
		
		////////////////////////////////////////////
		Subtract mog =new Subtract();
		IplImage temp_gray =  cvCreateImage(frameSize,IPL_DEPTH_8U,1);
		
		int i=0 ;	
		while (null!=frame) {
		
			frame = cvQueryFrame(capture);
		
		//	cvCvtColor(TrackUtils.getROI(frame), temp_gray, CV_RGB2GRAY);
			
			//cvShowImage("frame", input_gray); 
	
			List<CvRect> rects = mog.getMotionRect(input_gray,input1_gray,temp_gray);
			
	        for (CvRect rect : rects) {
				cvRectangleR(frame, rect, cvScalar(0, 255, 0, 0), 0, 0, 0);
			
			}
	        
	     //   cvShowImage("frame", frame); 
	      
	        cvConvertImage(input1_gray, input_gray, 0);
	        cvConvertImage(temp_gray, input1_gray, 0);
	        
	  
	        
	        
	        System.out.println(i++);
	        
	        if ( cvWaitKey(10) == 'q' )  
	            break;  
			
	    }  
		
		
		cvReleaseCapture(capture);
		cvDestroyWindow("frame");
			
		}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
