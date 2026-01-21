package instrument;
/* Usage: java MainDriver appClass
 */

/* import necessary soot packages */

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import soot.*;
import soot.options.Options;

public class yangdriver {
  public static void main(String[] args) {

	  //袁占慧：
	  /*
	   *
	  策略文件举例参考目录源码分析文章下的“路径示例”文件
	  
	  循环读目录下的策略文件，每个文件是一个信息流泄露策略，文件名0.policy,1.policy,..., x.policy，
	  对于每个文件策略哈希，顺序找出每一个类
	    对于每一个类，传给soot的main函数，即soot.Main.main(args);
	    等待该类遍历（internalTransform（）函数中等待即可）时出现的每个函数，
	    根据该策略哈希表，确定是否需要插桩，如果需要插桩，则开始插桩，先从策略中读出该函数的插桩策略
	   
	   对于source类型的插桩，是在该函数后插桩开始跟踪的函数security.monitorer()，可参考counter的例子
	  对于branch分支的插桩，是对所有的非目标路径插桩security.reject()函数
	                                                                 对目标路径插桩security.contine(参数：当前位置)函数
	 对于sink类型的插桩，是在该函数前插桩开始跟踪的函数security.act()，该函数主要完成对敏感变量同长度的复位
	 act函数的插桩例子回头我再给你讲一下,我再说明3文件最后粗略说了一下过
	 
	 
	 
	 

	                                                                 
	 
	     
	  
	   * */
    /* check the arguments */
    if (args.length == 0) {
      System.err.println("Usage: java MainDriver [options] classname");
      System.exit(0);
    }
    
  //  G tmptg=soot.G.v();
 //   soot.G.reset();
  //  soot.G.setInstance(tmptg);
  
  //  Options.v().set_soot_classpath("I:\\soot\\eclipse-chao-droidbench\\eclipse\\eclipse\\workspace\\yanginstrument\\bin;C:\\Program Files\\Java\\jre7\\lib\\rt.jar;C:\\Program Files\\Java\\jre7\\lib\\jce.jar");

    
	  
    /* add a phase to transformer pack by call Pack.add */
   
    String[] yangarg=new String[1];
   
          
   
    yangarg[0]="app2.ArrayTestCode";
    test(yangarg);
  
    
   // yangarg[0]="app.branch";
    //    “com.android.insecurebank.DataHelper”
   // test(yangarg);
   // test(yangarg);
   // yangarg[0]="app2.yangtest";
    //test(yangarg);
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
		Options.v().set_output_format(Options.output_format_none);
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


