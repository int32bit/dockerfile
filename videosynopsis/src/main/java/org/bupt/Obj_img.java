package org.bupt;



class Obj_img {
    
	public static int countall;
	public ImageInfo[] imfo;//用于记录每个图元 所有的帧
	public int  nowimgframe ;     //用于记录每个图元 当前需要播放的的帧数
	public int  allimgframe ;     //用于记录每个图元一共多少帧
	
	public int startframe ; //活动对象在原来视频中的开始帧
	public int endframe ;//活动对象在原来视频中的结束帧
	
	public boolean iseffective ;  //当播放完所有帧数后ImageInfo标记为无效

	
	
	public static int obj_count=0;//用于记录图院的个数
	
	public Obj_img(int allimgframe)
	{
		this.allimgframe=allimgframe;
	}
	
	
	
	
	
}
