package app2;


/*yangmodify*/
public class Yang {
	String[] zhi1;
	int[] zhi2 = new int[30];
	public int test_loop2(int mynum,int var222,String var333,int var444)
	{
		int a=0,b=0,c=0;
		int x=(int)(Math.random()*100);
		x=x/3;
		for(int i = 0;i<0;i++){ 
			c=b;
			b=a;
			a=mynum;
			} 
		
		//c=0;

		return c;
	}
	public int test_loop3(int myn_um)
	{
		return 1;
	}
	
	
	public int test(int mynum)
	{
		int rongrong=0;
        int rongrong2=1;
			rongrong=mynum;
			rongrong2=test_in(rongrong);


		return rongrong2;
	}
	public int test_in(int mynum_in)
	{
		int rongrong_in=0;
		rongrong_in=mynum_in;


		return rongrong_in;
	}
	
	public int test8(int mynum)
	{
		/*
		int rongrong=0;
		int x=(int)(Math.random()*100);
		if (x/2==0)
		{
		   // rongrong=mynum+1;
			rongrong+=38;
			//rongrong+=mynum+1;
			}
		else
		{
			rongrong=12+x;
			rongrong+=mynum;
		;
		}
		
		return rongrong;
		*/
		return mynum;
	}
	
	public int test2(int mynum)
	{
		
		int rongrong=0;
		int x=(int)(Math.random()*100);
		if (x/2==0)
		{
		   // rongrong=mynum+1;
			rongrong+=38;
			//rongrong+=mynum+1;
			}
		/*
		else
		{
			rongrong=12+x;
			rongrong+=mynum;
		;
		}
		
		return rongrong;
		*/
		return mynum;
	}
	public int test_switch(int mynum)
	{
		int rongrong=0;
		int x=(int)(Math.random()*100);
	
        switch(x/3){
        case 0:
            System.out.println("0");break;
        case 1:
            System.out.println("1");break;
        default:
            System.out.println("default");break;
        }
		
		return rongrong;
	}
	public int test_forloop(int mynum)
	{
		int rongrong=0;
		int x=(int)(Math.random()*100);
	
		for(int i = 0;i<x;i++){   
			System.out.println("forloop");
			}   
		
		return rongrong;
	}
	public int test_whileloop(int mynum)
	{
		int rongrong=0;
		int x=(int)(Math.random()*100);
		int i=0;
		while(i<x){
			
			rongrong+=mynum;
			i++;
		}
		
		return rongrong;
	}
	public int test_one(int mynum)
	{
		int rongrong=0;
		int x=(int)(Math.random()*100);
		if (x>50)
		{
			rongrong=test_two(mynum);
		}
		else
			rongrong=1;
		
		return rongrong;
	}
	public int test_two(int mynum222)
	{
		int rong=0;
		int y=(int)(Math.random()*100);
		if (y<50)
		{
			rong=test_one(mynum222);
		}
		else
			rong=2;
		
		return rong;
	}
	
	

}

