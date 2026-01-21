class TestInvoke {
  private static int calls=0;
  public static void main(String[] args) {
			
	for (int i=0; i<10; i++) {
	  foo();
	}
	
	System.out.println("I made "+calls+" static calls");
  }

  private static void foo(){
	calls++;
	int k=0;
	k=1;
	k=1;
	System.out.print(k);
	Yang yang=new Yang();
	yang.test_whileloop(3);
	bar();
  }

  private static void bar(){
	calls++;
	
	Yang yang=new Yang();
	
	yang.test_switch(3);
  }
}
