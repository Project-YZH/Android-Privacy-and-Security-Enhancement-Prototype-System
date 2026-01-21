/* Usage: java MainDriver appClass
 */

/* import necessary soot packages */
import soot.*;
import soot.options.Options;

public class MainDriver {
  public static void main(String[] args) {

    /* check the arguments */
  
    Options.v().set_soot_classpath("D:\\soot\\eclipse\\workspace\\counter2\\bin;C:\\Program Files\\Java\\jdk1.8.0_131\\jre\\lib\\jce.jar;C:\\Program Files\\Java\\jdk1.8.0_131\\jre\\lib\\rt.jar");

    
	  
    /* add a phase to transformer pack by call Pack.add */
    Pack jtp = PackManager.v().getPack("jtp");
    jtp.add(new Transform("jtp.instrumenter", 
			  new InvokeStaticInstrumenter()));
    String[] arg=new String[1];
    arg[0]="app.branch";
    //    ¡°com.android.insecurebank.DataHelper¡±
    //test(arg);
    test(arg);
  test(arg);
    
	
  }
  public static void test(String[] args)
  {
	  
	 

	    /* Give control to Soot to process all options, 
	     * InvokeStaticInstrumenter.internalTransform will get called.
	     */
	    /*
	     yangmodify
	     */
	  
	    soot.Main.main(args);
	    soot.G.reset();
	    Options.v().set_soot_classpath("D:\\soot\\eclipse\\workspace\\counter2\\bin;C:\\Program Files\\Java\\jdk1.8.0_131\\jre\\lib\\jce.jar;C:\\Program Files\\Java\\jdk1.8.0_131\\jre\\lib\\rt.jar");

		//initializeSoot();
  }
  
  
}


