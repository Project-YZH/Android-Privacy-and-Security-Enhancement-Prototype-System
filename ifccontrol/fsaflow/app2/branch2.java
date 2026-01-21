package app2;

public class branch2 {

	private static int calls=0;
	  public static void main(String[] args) {
		
		  /**/
		  char grade = 'C';
		  
	      switch(grade)
	      {
	         case 'A' :
	            System.out.println("优秀"); 
	            break;
	         case 'B' :
	         case 'C' :
	            System.out.println("良好");
	            break;
	         case 'D' :
	            System.out.println("及格");
	            break;
	          default :
	            System.out.println("未知等级");
	      }
	      
		  for (int i=0; i<10; i++) 
		  foo();
		
		
		System.out.println("I made "+calls+" static calls");
	  }

	  private static void foo(){
		calls++;
		if (calls==0)
		
			System.out.println("yangmade made "+calls+" static calls");
		
			else
		{
				calls--;
		}
		bar();
	  }

	  private static void bar(){
		calls++;
		
		
	  }
	  
}
