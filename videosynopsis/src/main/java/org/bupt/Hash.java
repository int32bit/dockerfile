package org.bupt;


class Hash {
	private int id[]= new int[100];
	private String name[] = new String[100];
	private int count=0;
	
	public Hash()
	{
		count=0;
	}
	
	
	public  boolean setHash(int id ,String name)
	{
		if(count<100)
		{
			this.id[count]=id;
			this.name[count]=name;
			count++;
			return true ;
		}
		else
		{
			return false ;
		}
		
	}
	
	public int findIndex(String name)
	{
		System.out.println("要查找的视频名字"+name);
		for(int i=0;i<count;i++)
		{
			System.out.println("hash中的名字"+this.name[i]+"  i="+i);
			if(this.name[i].equals(name))
			{
				System.out.println("hash返回的值"+id[i]);
				return id[i];
				
			}
		}
		
		return -1;
	}
	

}
