package org.bupt;


import static org.bytedeco.javacpp.opencv_core.CV_FONT_HERSHEY_SIMPLEX;
import static org.bytedeco.javacpp.opencv_core.CV_FONT_ITALIC;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvInitFont;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvPutText;
import static org.bytedeco.javacpp.opencv_core.cvRect;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvResetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_highgui.cvConvertImage;
import static org.bytedeco.javacpp.opencv_highgui.cvCreateVideoWriter;
import static org.bytedeco.javacpp.opencv_highgui.cvLoadImage;
import static org.bytedeco.javacpp.opencv_highgui.cvReleaseVideoWriter;
import static org.bytedeco.javacpp.opencv_highgui.cvWriteFrame;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;

import java.io.File;

import org.bytedeco.javacpp.opencv_core.CvFont;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_highgui.CvVideoWriter;

class Concentrate  {
	private String objectpath;//活动对象的地址
	private String videoaddress;//合成视频的存放地址
	private String videoname;//合成视屏的名字,"*.avi"
	private String ffmpegpath ; //压缩程序的地址
	
	private int ysb=15 ;//每一帧显示的活动对象的个数
	private double cfate=1.0;//避免碰撞率  为1时完全避免碰撞 
	private Hash hash =new Hash();
	
	private int r=20;
	private int g=20;
	private int b=30;

	
	public Concentrate(IplImage[] bg,String objectpath,String videoaddress,String videoname,String ffmpegpath ,int ysb ,double cfate)
	{
		this.objectpath=objectpath;
		this.videoaddress=videoaddress;
		this.videoname=videoname;
		this.ffmpegpath=ffmpegpath;
		this.ysb=ysb;
		this.cfate=cfate;
		//cvLoadImage();
	}
	
	
	public  void start(IplImage[] bankgd)
	{
		IplImage bg = null;
		File object = new File(objectpath);
		File[] objectfile = object.listFiles();
	
		//初始化视频格式
		CvSize size =  cvSize(640,360); 
        double fps = 25;
        CvVideoWriter writer = cvCreateVideoWriter(videoaddress+"temp.avi",5,fps,size); //创建视频文件
        IplImage src_resize = cvCreateImage(size,8,3);//创建视频文件格式大小的图片

		Obj_img[] obj_img = new  Obj_img[objectfile.length];//申请obj_img对象
		
		//获取所有图元信息 保存在Obj_img类中	
    	for(int i=0;i<objectfile.length;i++)
		{
			File[] imgfile= objectfile[i].listFiles();
			int count1=imgfile.length-1;
			if(count1>2)
		    {
				obj_img[i] = new Obj_img(count1);//初试话图元的帧数，《《《《《《【【【【【【可优化
				obj_img[i].imfo=Image_Info(objectfile[i]);//获取第i个图元的所有帧数 
				obj_img[i].nowimgframe=0;//设置第i个图元的当前播放帧数,既该图元的第一帧
				//初试化开始结束帧
				obj_img[i].startframe=obj_img[i].imfo[0].frame;
				obj_img[i].endframe=obj_img[i].startframe+count1-1;
				
				Obj_img.countall=Obj_img.countall+count1;
				obj_img[i].iseffective=true;
				
				
				Obj_img.obj_count=Obj_img.obj_count+1;//图元个数加一	
	        }
			else
			{
				obj_img[i] = new Obj_img(0);
				obj_img[i].iseffective=false;
			}
			
			
		}
    	
	   
	    
    	
    	//碰撞检测，确定输入的是那几个object的帧
		DetectCollison dc= new DetectCollison(0,hash);
		//调度算法 
		int ty_count=0;//用于记录已经显示的活动对象的个数
		
		while(Obj_img.obj_count>=1)//Obj_img里面图元个数大于0
		{
		   //适配一帧上所有图元
			int obj_i=0; 
	    	while(obj_i<objectfile.length)//遍历所有图元的当前帧，查找何时的图元
	    	{	
	    		if(obj_img[obj_i].iseffective)//首先判断该图元所有帧是否应经被显示完毕
				{
	    			CvRect imgrect = cvRect(obj_img[obj_i].imfo[obj_img[obj_i].nowimgframe].x,
					obj_img[obj_i].imfo[obj_img[obj_i].nowimgframe].y,
					obj_img[obj_i].imfo[obj_img[obj_i].nowimgframe].width,
					obj_img[obj_i].imfo[obj_img[obj_i].nowimgframe].height);
	    			
	    			if(obj_img[obj_i].nowimgframe!=0)
					{
						dc.addimgarea(imgrect,obj_img[obj_i].imfo[obj_img[obj_i].nowimgframe].path_name,obj_img[obj_i].imfo[obj_img[obj_i].nowimgframe].frame,obj_img[obj_i].imfo[obj_img[obj_i].nowimgframe].videoname);//
						obj_img[obj_i].nowimgframe++;
						if(obj_img[obj_i].nowimgframe>=obj_img[obj_i].allimgframe)//判断该图元是否显示完毕
						{
							obj_img[obj_i].iseffective=false;	//显示完毕的话，将其有效值设为false
							Obj_img.obj_count--;     //图元个数减一
							ty_count--;
							dc.delete_obj(obj_i);
						}
						
						
					}
	    			else if(dc.detcol(obj_img[obj_i],cfate)&&ty_count<=ysb)//判断是否和已有图元帧发生碰撞
					{
						if(obj_img[obj_i].nowimgframe==0)
							ty_count++;//每个活动对象第一帧被载入到背景时，ty_count加一
						
						dc.addimgarea(imgrect,obj_img[obj_i].imfo[obj_img[obj_i].nowimgframe].path_name,obj_img[obj_i].imfo[obj_img[obj_i].nowimgframe].frame,obj_img[obj_i].imfo[obj_img[obj_i].nowimgframe].videoname);//
						dc.addObj_img(obj_img[obj_i], obj_i);
						
						obj_img[obj_i].nowimgframe++;
						if(obj_img[obj_i].nowimgframe>=obj_img[obj_i].allimgframe)//判断该图元是否显示完毕
						{
							obj_img[obj_i].iseffective=false;	//显示完毕的话，将其有效值设为false
							Obj_img.obj_count--;     //图元个数减一
							ty_count--;//每个活动对象最后一帧被载入到背景时，ty_count减一
						    dc.delete_obj(obj_i);
							
						}
						
					}
					
	    		}
	    		
	    		obj_i++;
	    	}
	    	//---------------------------------------
	    	System.out.println("-------------------"+ty_count);
	    	
	    	 bg= cvCreateImage(size, IPL_DEPTH_8U, 3);
	    	 bg= bankgd[dc.maxIndex()];
	    
	    	//生成临时的背景图
	    	IplImage bgrd_1 = cvCreateImage(size, IPL_DEPTH_8U, 3);
	    	cvConvertImage(bg, bgrd_1, 0); 
			//生成多图元的背景帧
			
	    	 for(int i =0 ; i<dc.getCount();i++)
	    	 {
	    		 Insert_Iamge(bgrd_1,i,dc);
	    	 }
	         
	    	 //背景帧处理
	    	 Bgrdprocess(bgrd_1 ,bg,r,g,b,dc );//和背景比对将，小于sim值的，像素点更新为背景像素点
	    	 
	    	 
	    	//创建视频文件格式大小的图片 
	 	    cvResize(bgrd_1,src_resize);
	 	    
	 	  //  cvShowImage("bankground[dc.maxIndex()]",bankground[dc.maxIndex()]);
	 	    cvWriteFrame(writer,src_resize); 
	 	   
	 	  //  System.out.println(""+Obj_img.obj_count);      //释放临时资源
	 		
	 	    cvReleaseImage(bgrd_1);
	 	   //cvReleaseImage(bg);
	 	    dc.clearimgdone();//清空全背景图元信息
	   
		}
        
		//释放资源
	 	//cvReleaseImage(bg);
        cvReleaseVideoWriter(writer);   
        
        //压缩文件
        FFMpegUtil ffm = new FFMpegUtil(ffmpegpath,videoaddress+"temp.avi");
        ffm.videoTransfer(videoaddress+videoname);
        File f = new File(videoaddress+"temp.avi");
        f.delete();
        
	}
	/*
	 * 
	 * 
	 * 
	 * 
	 * 辅助函数区域
	 * 
	 * 
	 * 
	 * 
	 * */
//    返回一个保存有图片信息的对象数组 
	public static ImageInfo[]  Image_Info(File res)
	{
		        ImageInfo[] imageinfo = null;
	        	File[] f3=res.listFiles();//
				//视频文件信息存放进Object里面
				if(f3.length!=0){
				imageinfo =new ImageInfo[f3.length-1] ;//去掉info.txt
				for(int k = 0 ;k<f3.length-1;k++)
				{
					//466_1_318,48,27,10_roi_random119
					String img_name=new String(f3[k].getName());
					System.out.println(img_name);
					
					String[] a=img_name.split(",");
					if(a.length!=4)
					{    
						System.out.println("前景物体文件的文件名不符合规则！具体参考TopK.java--第27行代码");
						System.exit(0);
			     	}
					String[] b=a[3].split("_");//这里需进行一次判断 增加健壮性
					if(b.length!=3)
					{
						System.out.println("前景物体文件的文件名不符合规则！具体参考TopK.java--第33行代码");
						System.exit(0);
					}
					String [] c = a[0].split("_");
					
					int frame=Integer.parseInt(c[0]);
				    String videoname=c[1];
					int x=Integer.parseInt(c[2]);
					int y= Integer.parseInt(a[1]);
					int w=Integer.parseInt(a[2]);
					int h=Integer.parseInt(b[0]);
					
					imageinfo[k]=new ImageInfo(frame,videoname,x,y,w,h,res+"\\"+f3[k].getName());
					
					if(k==0)
					{
						imageinfo[0].set_ff(frame);
					}
				}
				//这里需要判断一下Obj的长度 ，暂时先不考虑 《在存储前景物体的过程中，不足5帧的会被删除，所以一定会大于5帧》
						//==================
			
			
		}
				
				return imageinfo ;
	}	
//    生成多图元背景帧
	public static void Insert_Iamge(IplImage bgrd,int l,DetectCollison dc1)
	{
		//读取前景图元
		IplImage img = cvLoadImage(dc1.path_name[l]);
		
		//生成时间
		String id =dc1.videoname[l];
		int m=dc1.frame[l]/(1500);
		int s=dc1.frame[l]%(1500);
		s=s/25;
		String sec;
		if(s<10)
			sec="0"+s;
		else
			sec=s+"";
		cvText(img,id+"."+m+":"+sec,0,dc1.imagedone[l].height()-10);
		//将图元的ROI设置成整个图元
		CvRect roi =cvRect(0, 0, dc1.imagedone[l].width(),dc1.imagedone[l].height()); 
        
		//设置背景ROI 相关图层
		CvRect roi1 = dc1.imagedone[l]; 
		cvSetImageROI(img, roi);
		cvSetImageROI(bgrd, roi1);
		
		//将图元复制到背景上面
		cvCopy(img,bgrd);
		cvResetImageROI(img);
		cvResetImageROI(bgrd);
		
		//释放临时资源
 	    cvReleaseImage(img);
	   
	}
//显示时间
	
	public static void cvText(IplImage img, String text, int x, int y)
	{
	    CvFont font=new CvFont() ;
	   

	    double hscale = 0.25;
	    double vscale = 0.25;
	    cvInitFont(font,CV_FONT_HERSHEY_SIMPLEX |CV_FONT_ITALIC,hscale,vscale);
	    CvScalar textColor = cvScalar(0,255,255,0);
	    CvPoint textPos =cvPoint(x, y);
	    cvPutText(img,text,textPos,font,textColor);
	}


	
	
	
//图像平滑处理
	public static void Bgrdprocess(IplImage bgrd ,IplImage bg, int r,int g ,int b ,DetectCollison dc )
	{
		CvMat bgrd_mat = bgrd.asCvMat();
		CvMat bg_mat=bg.asCvMat();
		
		for(int d=0;d<dc.getCount();d++)
		{	
			
			for(int i =dc.imagedone[d].y(); i <dc.imagedone[d].y()+ dc.imagedone[d].height(); i++)   
			 {  
				 for(int j = dc.imagedone[d].x(); j <dc.imagedone[d].x()+dc.imagedone[d].width(); j++)  
			     {  
					 if(Math.abs(bgrd_mat.get(i, j,0)-bg_mat.get(i, j,0))<=r&&Math.abs(bgrd_mat.get(i, j,1)-bg_mat.get(i, j,1))<=g&&Math.abs(bgrd_mat.get(i, j,2)-bg_mat.get(i, j,2))<=b)
					 {
						for(int k=0;k<=2;k++)
						 bgrd_mat.put(i, j,k,bg_mat.get(i, j, k));	 
					 }
			     }
				 
			 }
		}
		
		 bgrd=bgrd_mat.asIplImage();
		
	}	
	
	public void setr(int r)
	{
		this.r=r;
	}
	
	public void setg(int g)
	{
		this.g=g;
	}
	
	public void setb(int b)
	{
		this.b=b;
	}
	
	
	public void sethash(Hash hash)
	{
		this.hash=hash;
	}
	
	  public static boolean iscolWithRect(int x1, int y1, double w1, double h1,int x2,int y2, double w2, double h2) 
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
	
	
	
	
	
	
	
	
	
}
