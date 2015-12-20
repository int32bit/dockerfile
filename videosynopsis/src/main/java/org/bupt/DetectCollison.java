package org.bupt;


import org.bytedeco.javacpp.opencv_core.CvRect;

class DetectCollison {
	private Hash hash =new Hash();
	public CvRect[] imagedone = new CvRect[100];//用于存放已经可以显示图元区域
	public String[] path_name = new String[100];//用于存放已经可以显示图元区域的路径
	public String[] videoname=new String[100];
	public int[]    frame = new int[100];
	public Obj_img[] objdone = new Obj_img[100];//用于存放已经可以显示活动对象
	public int[] flag = new int[100];
	
	private   int count_oi;//用于记录已经经过碰撞检测的活动对象的个数
	private  int count  ; 
	
	public DetectCollison(int count,Hash hash)
	{
		this.hash=hash;
		this.count=0 ; 
		count_oi=0;
		for(int i=0; i<100;i++)//初始化标记为-1
		{
			flag[i]=-1;
		}
	}
	
	//将当前图元区域同所以可以显示的图元区域进行碰撞检测,true表示没有碰撞
	public boolean detcol(Obj_img oi,double cfate)
	{
		if(count_oi!=0)	
		{	
			for(int i=0; i<100;i++)//遍历每一个已经显示的活动对象
			{
				if(flag[i]==-1)
				{
					continue;
				}
				
				int j=0;
				int k=0;
			    for(j=0,k=objdone[i].nowimgframe;j<oi.allimgframe&&k<objdone[i].allimgframe;j=j+3,k=k+3)//当前活动对象的每一帧和已存在的活动对象的当前帧-最后一帧比较
				{
			    		if(iscollisionWithRect(oi.imfo[j].x,oi.imfo[j].y,oi.imfo[j].width*cfate,oi.imfo[j].height*cfate,objdone[i].imfo[k].x,objdone[i].imfo[k].y,objdone[i].imfo[k].width*cfate,objdone[i].imfo[k].height))
			    		{
			    			return false;
			    		} 		
				}
			    
			    
			
			}
		}
		
		return true ;
		
	}
	
	//将已经通过碰撞检测的图元放入objdone中
	public boolean addObj_img(Obj_img oi,int obj_num)
	{
		if(count_oi<100)
		{	
			for(int i=0;i<100;i++)
			{
			    if(flag[i]==-1)
			    {
			    	objdone[i]=oi;
					flag[i]=obj_num;
					count_oi++;
					System.out.println("----------count_oi---------"+count_oi);
					return true ;
			    }
			}
		}
			return false;
	}
	
	public boolean addimgarea(CvRect imgarea,String path_name,int frame,String videoname)
	{
		if(count<100)
		{	
			imagedone[count]=imgarea ;
			this.path_name[count]=path_name;
			this.frame[count]=frame;
			this.videoname[count]=videoname;
			count++;
			
			return true ;
		}
		else
			return false;
	}
	//
	public int maxIndex()
	{
		int[] fd= new int[100];
		for(int i=0;i<count;i++)
		{
			fd[i]=0;
		}
		
		for(int i=0;i<count;i++)
		{
			fd[hash.findIndex(videoname[i])]++;
		}
		
		int num=0;
		int index =0;
		for(int i=0;i<count;i++)
		{
			if(num<fd[i])
			{
				num=fd[i];
				index=i;
			}
		}
		
		System.out.println("index= "+index);
		return index ;
		
	}
    //碰撞检测的辅助算法
    public  boolean iscollisionWithRect(int x1, int y1, double w1, double h1,int x2,int y2, double w2, double h2) 
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
    
    
	//清空DetectCollison
    public void clearimgdone()
    {
    	count=0;  	
    }
    //获取图元区域个数
    public int getCount()
    {
    	return count ;
    }
	
    public void delete_obj(int obj_i)
    {
    	for(int i=0;i<100;i++)
		{
			if(flag[i]==obj_i)
			{
				flag[i]=-1;
				count_oi--;
				break;
			}
		}	
    }
    	
}
