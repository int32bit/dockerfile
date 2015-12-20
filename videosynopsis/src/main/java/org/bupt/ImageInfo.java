package org.bupt;


class ImageInfo {

	public int first_frame;//用于记录此图元的第一帧位置
	public String videoname;
	public int frame;
	public int x ;//用于记录该图元在背景的中的x坐标
	public int y ;//用于记录该图元在背景的中的y坐标
	public int width ;//用于记录图元的宽度
	public int height ;//用于记录图院的高度
	public String path_name ;//用于记录文件名

	
	public ImageInfo(int frame,String videoname,int x ,int y , int width ,int height ,String path_name )
	{ 
		this.frame=frame;
		this.videoname=videoname;
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.path_name=path_name;		
	}
	
	public void set_ff(int first_frame)
	{
		this.first_frame=first_frame;
	}

}