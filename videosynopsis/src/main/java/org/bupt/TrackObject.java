package org.bupt;


import java.util.List;

import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.IplImage;

class TrackObject {

	private int id; //活动对象ID
	private List<CvRect> trajectory;//存放活动对象帧的坐标和大小
	private CvRect lastRect;
	private CvRect second_lastRect;
	private List<IplImage> trajectoryRoi;//存放活动对象帧的jpg
	
	private int enterFrame; //起始帧
	private int endFrame;//结束帧

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<CvRect> getTrajectory() {
		return trajectory;
	}

	public void setTrajectory(List<CvRect> trajectory) {
		this.trajectory = trajectory;
	}

	public CvRect getLastRect() {
		return lastRect;
	}

	public void setLastRect(CvRect lastRect) {
		//if(flag)
	//	{
			//this.second_lastRect=this.lastRect;
	//	}
		//else
	//	{
			//this.second_lastRect=cvRect(-1,-1,-1,-1);
			//flag=true;
		//}
		
		this.lastRect = lastRect;
	}
	
	public CvRect getsecond_lastRect() {
		return second_lastRect;
	}

/*	public void setsecond_lastRect(CvRect second_lastRect) {
		this.second_lastRect = second_lastRect;
	}
	*/

	public int getEnterFrame() {
		return enterFrame;
	}

	public void setEnterFrame(int enterFrame) {
		this.enterFrame = enterFrame;
	}

	public int getEndFrame() {
		return endFrame;
	}

	public void setEndFrame(int endFrame) {
		this.endFrame = endFrame;
	}

	public List<IplImage> getTrajectoryRoi() {
		return trajectoryRoi;
	}

	public void setTrajectoryRoi(List<IplImage> trajectoryRoi) {
		this.trajectoryRoi = trajectoryRoi;
	}

}
