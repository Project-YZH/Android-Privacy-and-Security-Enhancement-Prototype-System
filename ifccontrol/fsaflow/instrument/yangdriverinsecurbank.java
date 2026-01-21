package instrument;
/* Usage: java MainDriver appClass
 */

/* import necessary soot packages */

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import soot.*;
import soot.options.Options;

public class yangdriverinsecurbank {
  public static void main(String[] args) {

    /* check the arguments */
   
    
  //  G tmptg=soot.G.v();
 //   soot.G.reset();
  //  soot.G.setInstance(tmptg);
  
  //  Options.v().set_soot_classpath("I:\\soot\\eclipse-chao-droidbench\\eclipse\\eclipse\\workspace\\yanginstrument\\bin;C:\\Program Files\\Java\\jre7\\lib\\rt.jar;C:\\Program Files\\Java\\jre7\\lib\\jce.jar");

    
	  
    /* add a phase to transformer pack by call Pack.add */
   
    String[] yangarg=new String[1];
   
    yangarg[0]="com.android.insecurebank.PostLogin";
    //    “com.android.insecurebank.DataHelper”
    test(yangarg);
    
   /*
    yangarg[0]="com.android.insecurebank.LoginScreen";
    test(yangarg);
  
    yangarg[0]="com.android.insecurebank.LoginScreen$1";
    test(yangarg);
    
    yangarg[0]="com.android.insecurebank.InsecureBankActivity";
    //    “com.android.insecurebank.DataHelper”
    test(yangarg);

    yangarg[0]="com.android.insecurebank.InsecureBankActivity$1";
    test(yangarg);
    
    yangarg[0]="com.android.insecurebank.PostLogin$1";
    //    “com.android.insecurebank.DataHelper”
    test(yangarg);
    
    yangarg[0]="com.android.insecurebank.PostLogin";
    //    “com.android.insecurebank.DataHelper”
    test(yangarg);

    yangarg[0]="com.android.insecurebank.RestClient";
    test(yangarg);
    
    */
    //test(args);
    
  
    
	
  }
  public static void test(String[] args)
  {
	  
	  
	 soot.G.reset();
	 initializeSoot();
	  Options.v().set_soot_classpath("D:\\soot\\eclipse\\workspace\\counter2\\bin;C:\\Program Files\\Java\\jdk1.8.0_131\\jre\\lib\\jce.jar;C:\\Program Files\\Java\\jdk1.8.0_131\\jre\\lib\\rt.jar");

	  Pack jtp = PackManager.v().getPack("jtp");
	    jtp.add(new Transform("jtp.instrumenter", 
				  new InvokeStaticInstrumenter()));
	    
	   soot.Main.main(args);
	    
	   
	   jtp.remove("jtp.instrumenter");
	   
	//   soot.G.setInstance(tmptg);
	  // initializeSoot();
	   //System.gc();
	 
		 
		    
		 // Scene.v().loadNecessaryClasses();
		 // Scene.v().addBasicClass("app.branch",SIGNATURES);
	    
		
  }
  public static void initializeSoot() {
	  
	  Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_output_format(Options.output_format_class);
		Options.v().set_whole_program(true);
		
	  
	/*  List<String> excludeList = new LinkedList<String>();
		excludeList.add("java.");
		excludeList.add("sun.misc.");
		excludeList.add("android.");
		excludeList.add("org.apache.");
		excludeList.add("soot.");
		excludeList.add("javax.servlet.");
		Options.v().set_exclude(excludeList);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_output_format(Options.output_format_class);
		*/
		
	//	Options.v().set_no_bodies_for_excluded(true);
		//Options.v().set_allow_phantom_refs(true);
		
	//	Options.v().set_output_format(Options.output_format_none);
		//设置输出格式
		//Options.v().set_output_format(Options.output_format_dava);
		//Options.v().set_output_format(Options.output_format_class);
		//Options.v().set_output_format(0);
		
	//	Options.v().set_whole_program(true);
		
		
	//	Options.v().set_process_dir(Collections.singletonList(apkFileLocation));
	//	Options.v().set_soot_classpath(forceAndroidJar ? androidJar
	//			: Scene.v().getAndroidJarPath(androidJar, apkFileLocation));
		
	//	Options.v().set_src_prec(Options.src_prec_apk);
	//	Main.v().autoSetOptions();
		
	

		// Load whetever we need
	//	Scene.v().loadNecessaryClasses();
	}
  
  
  
}


