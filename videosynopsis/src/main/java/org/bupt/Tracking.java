package org.bupt;


import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRectangle;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvResetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_highgui.CV_CAP_PROP_FRAME_COUNT;
import static org.bytedeco.javacpp.opencv_highgui.CV_CAP_PROP_POS_FRAMES;
import static org.bytedeco.javacpp.opencv_highgui.cvCreateFileCapture;
import static org.bytedeco.javacpp.opencv_highgui.cvGetCaptureProperty;
import static org.bytedeco.javacpp.opencv_highgui.cvQueryFrame;
import static org.bytedeco.javacpp.opencv_highgui.cvReleaseCapture;
import static org.bytedeco.javacpp.opencv_highgui.cvSetCaptureProperty;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_highgui.CvCapture;



class Tracking {

	private  double threshold=0.5; // 重合比率
	private IplImage[] bankground ;
	private Hash hash= new Hash();
	private  String fileRoot = "D:\\objects";
	private  boolean done=true;
	private  int startFrame=30;
	private  String videoPath = "D:\\selected";
	private ArrayList<Point> list_point;
	

	public Tracking(String fileRoot ,String videoPath,ArrayList<Point> list_point)
	{
		this.fileRoot=fileRoot;
		this.videoPath=videoPath;
		this.list_point=list_point;
		
	}
	
	public IplImage[] getBG()
	{
		return bankground;
	}
	
	public Hash getHash()
	{
		return hash;
	}
	
	
	
	public  int proccess(MOGMotionDetector mog ,String video, String name,String videoname,boolean isfirstv) {
		
		CvCapture cap = cvCreateFileCapture(video);
		int frameTotal = (int)cvGetCaptureProperty(cap, CV_CAP_PROP_FRAME_COUNT);
		IplImage frame = cvQueryFrame(cap);
		
		//CanvasFrame canvasFrame = new CanvasFrame("Test");
		CvSize frameSize =cvSize(frame.width(), frame.height());
		IplImage foreground = cvCreateImage(frameSize, IPL_DEPTH_8U, 1);
		IplImage inputFrame = cvCreateImage(frameSize, IPL_DEPTH_8U, 3);
		
		
		
		int frameCount = 0;
		IplImage frameROI_1=null;
		if(isfirstv)
		{	while(frameCount<300)
			{
				frame = cvQueryFrame(cap);
				frameROI_1 = TrackUtils.getROI(frame,list_point);
				mog.getMotionRect(frameROI_1,foreground,inputFrame);
				frameCount++;
			}
		}
		frameROI_1=null;
		
		
		System.gc();
		
		
		cvSetCaptureProperty(cap,CV_CAP_PROP_POS_FRAMES,1);
		frameCount = 0;
		
		List<TrackObject> objects = new ArrayList<TrackObject>();//活动对象数组
		
		boolean isFirstFrame = true;
		
		while (null != frame)//遍历
		{
			IplImage frameROI = TrackUtils.getROI(frame,list_point);//处理图像
			 
			List<CvRect> rectList = mog.getMotionRect(frameROI,foreground,inputFrame);
			 
			if (done) {
				if (0 < rectList.size() && frameCount > startFrame) 
				{
					if (isFirstFrame)
					{
						isFirstFrame = false;
						for (CvRect objectRect : rectList) 
						{
							TrackObject object = new TrackObject();
							
							object.setLastRect(objectRect);//
							object.setEnterFrame(frameCount);//framecout = 0 ;framecount ++ ;
							object.setEndFrame(frameCount);
							List<CvRect> objectTrace = new ArrayList<CvRect>();

							objectTrace.add(objectRect);
							object.setTrajectory(objectTrace);

							List<IplImage> trajectoryRoi = new ArrayList<IplImage>();
							cvSetImageROI(frame, objectRect);
							IplImage copy = cvCreateImage(cvGetSize(frame), IPL_DEPTH_8U, 3);
							cvCopy(frame, copy);
							trajectoryRoi.add(copy);
							cvResetImageROI(frame);
							//cvReleaseImage(copy);
							object.setTrajectoryRoi(trajectoryRoi);
							trajectoryRoi=null;
							objects.add(object);

						}
					} 
					else 
					{
						for (CvRect currentRect : rectList) 
						{
							boolean isNew = true;
							for (TrackObject object : objects) 
							{
								CvRect lastRect = object.getLastRect();
							//	CvRect slastRect=object.getsecond_lastRect();
								if (isSurround(lastRect,currentRect))
								{
									/**
									 * update object
									 */
									object.setLastRect(currentRect);
									
									object.setEndFrame(frameCount);
									List<CvRect> trajectory = object
											.getTrajectory();
									trajectory.add(currentRect);
									object.setTrajectory(trajectory);

									List<IplImage> trajectoryRoi = object
											.getTrajectoryRoi();
									cvSetImageROI(frame, currentRect);
									IplImage copy = cvCreateImage(cvGetSize(frame), IPL_DEPTH_8U, 3);
									cvCopy(frame, copy);
 									trajectoryRoi.add(copy);
									cvResetImageROI(frame);
									//cvReleaseImage(copy);
									object.setTrajectoryRoi(trajectoryRoi);
									trajectoryRoi=null;
									isNew = false;
								}
							}

							if (isNew) {
								TrackObject newObject = new TrackObject();
								newObject.setEnterFrame(frameCount);
								newObject.setEndFrame(frameCount);
								newObject.setLastRect(currentRect);
								List<CvRect> objectTrace = new ArrayList<CvRect>();
								objectTrace.add(currentRect);
								newObject.setTrajectory(objectTrace);

								List<IplImage> trajectoryRoi = new ArrayList<IplImage>();
								cvSetImageROI(frame, currentRect);
								
								IplImage copy = cvCreateImage(cvGetSize(frame), IPL_DEPTH_8U, 3);
								cvCopy(frame, copy);
								
								trajectoryRoi.add(copy);
								cvResetImageROI(frame);
								//cvReleaseImage(copy);
								newObject.setTrajectoryRoi(trajectoryRoi);
								
								trajectoryRoi=null;

								objects.add(newObject);
							}
						}

						Iterator<TrackObject> itTrackObject = objects.iterator();
						while (itTrackObject.hasNext()) 
						{
							TrackObject object = itTrackObject.next();
							int objectLastFrame = object.getEndFrame();
							
							if ((frameCount - objectLastFrame) > 10) 
							{
								if (object.getTrajectory().size() > 20) 
								{
									TrackUtils.saveRectAndImage(object, frameCount, fileRoot, name,videoname);
									itTrackObject.remove();
								}
								else
								{
									itTrackObject.remove();
								}
							}
							
						}
					}
				}
			}
			
			for (CvRect boundbox : rectList) {
				cvRectangle(frame, cvPoint(boundbox.x(), boundbox.y()), cvPoint(boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()), CvScalar.RED, 2, 8, 0);
			}
			System.out.println("frameCount:" + frameCount);
			
			//canvasFrame.showImage(frame);
			
			cvReleaseImage(frameROI);
			
			frame = cvQueryFrame(cap);
			frameCount++;
			
			if (frameCount == frameTotal) 
			{
				break;
			}
		}
		
		Iterator<TrackObject> itObject = objects.iterator();
		System.out.println(objects.size());
		while (itObject.hasNext()) {
			TrackObject object = itObject.next();
			if (object.getTrajectory().size() > 20) {
				TrackUtils.saveRectAndImage(object, frameCount, fileRoot, name,videoname);
				itObject.remove();
			} else {
				itObject.remove();
			}
			
		}
		
			
		objects.clear();
		cvReleaseCapture(cap);
		frame=null;
		//canvasFrame.dispose();
		System.gc();
		
		return frameTotal;
		
	}
	
	public  void begin() {

		File videoRoot = new File(videoPath);
		File[] videos = videoRoot.listFiles();
		bankground = new IplImage[videos.length];
		float[] times = new float[videos.length];
		boolean isfirstv = true ;
		//int total=0;
		MOGMotionDetector mog =new MOGMotionDetector();

		for (int i = 0; i < videos.length; i++) {
			System.out.println(videos[i].getName());
			long start = System.currentTimeMillis();
			String tempName = videoRoot.getName();
			System.out.println(tempName);// videos[i].getName().substring(0, videos[i].getName().length() - 4);
			proccess(mog,videoPath + "\\" + videos[i].getName(), tempName,videos[i].getName().substring(0, videos[i].getName().length() - 4),isfirstv);
			isfirstv=false;
			System.gc();
			long end = System.currentTimeMillis();
			times[i] = (end - start) / 1000;
			Mat bmat=new Mat();
			mog.mog2.getBackgroundImage(bmat);
			bankground[i]=bmat.asIplImage();
			hash.setHash(i,videos[i].getName().substring(0, videos[i].getName().length() - 4));
			
		}
		for (int j = 0; j < times.length; j++) {
			System.out.println("run time for each " + times[j]);
		}
		
		
	}


	private  boolean isSurround(CvRect lastRect, CvRect currentRect) {

		/*		&& currentArea <= scale * lastArea
				&& currentArea >= lastArea / scale*/
		
		//int lastArea = lastRect.width() * lastRect.height();
		//int currentArea = currentRect.width() * currentRect.height();
		if (iscollisionWithRect(lastRect.x(),lastRect.y(),lastRect.width(),lastRect.height(),
				                currentRect.x(),currentRect.y(),currentRect.width(),currentRect.height())
				                &&isthesameone(lastRect,currentRect))
		{
		
			return true;
		}
		return false;
	}
	
	//
    //碰撞检测的辅助算法,true表示碰撞
	 private  boolean iscollisionWithRect(int x1, int y1, double w1, double h1,int x2,int y2, double w2, double h2) 
	{  
        if (x1 >= x2 && x1 >= x2 + w2) {  
            return false;  
        } else if (x1 <= x2 && x1 + w1 <= x2) {  
            return false;  
        } else if (y1 >= y2 && y1 >= y2 + h2) {  
            return false;  
        } else if (y1 <= y2 && y1 + h1 <= y2) {  
            return false;  
        }  
        return true;  
    }  
	 
	 private double calcoverlap(CvRect lastRect,CvRect currentRect)
	 {
		/* if (lastRect.x() >= currentRect.x() && lastRect.x() >= currentRect.x() + currentRect.width()) {  
	            return 0;  
	        } else if (lastRect.x() <= currentRect.x() && lastRect.x() + lastRect.width() <= currentRect.x()) {  
	            return 0;  
	        } else if (lastRect.y() >= currentRect.y() && lastRect.y() >= currentRect.y() + currentRect.height()) {  
	            return 0;  
	        } else if (lastRect.y() <= currentRect.y() && lastRect.y() + lastRect.height() <= currentRect.y()) {  
	            return 0;  
	        }  */
		 int lx=0;
		 int ly=0;
		 lx=lastRect.x()<currentRect.x()?lastRect.x():currentRect.x();
		 ly=lastRect.y()<currentRect.y()?lastRect.y():currentRect.y();
		 int rx=0;
		 int ry=0;
		 rx=(lastRect.x()+lastRect.width())<(currentRect.x()+currentRect.width())?(lastRect.x()+lastRect.width()):(currentRect.x()+currentRect.width());
		 ry=(lastRect.y()+lastRect.height())<(currentRect.y()+currentRect.height())?(lastRect.y()+lastRect.height()):(currentRect.y()+currentRect.height());
		 
	     double  count=0.0;
	     
		 for(int jy=ly;jy<=ry;jy++)
		 {
			 for(int ix=lx;ix<=rx;ix++)
			 {
				 if(isinRect(ix,jy,lastRect)&&isinRect(ix,jy,currentRect))
					 count++;
			 }
		 }
		  double  fate=0.0 ;
		 fate=count/(lastRect.width()*lastRect.height())<count/(currentRect.width()*currentRect.height())?count/(lastRect.width()*lastRect.height()):count/(currentRect.width()*currentRect.height());
		
		  //fate= count/(lastRect.width()*lastRect.height());
		//  System.out.println("重合率： "+fate);
		
		  return fate;
	 }
	 
	 private boolean isinRect(int x,int y,CvRect rect)
	 {
		 if(rect.x()<=x&&x<=rect.x()+rect.width()&&rect.y()<=y&&y<=rect.y()+rect.height())
			 return true;
		 else
			 return false;
	 }
	 
	 
	
	 private  boolean isthesameone(CvRect lastRect, CvRect currentRect)
	 {
		/* if(Math.abs(lastRect.width()-currentRect.width())<=threshold||Math.abs(lastRect.height()-currentRect.height())<=threshold)
			 return true;
		 else
			 return false; */
		 if(calcoverlap(lastRect,currentRect)>threshold)
			 return true ;
		 else
			 return false;	 
		 
		 
	 }
	
	
	
	public String getVideopath()
	{
		return videoPath ;
	}
	
	public void setvideoPath( String videoPath)
	{
		this.videoPath=videoPath;
	}
	

	
	
	
}
