package instrument;
/* Usage: java MainDriver appClass
 */

/* import necessary soot packages */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import soot.Pack;
import soot.PackManager;
import soot.Transform;
import soot.options.Options;


//完成策略加载和启动soot

public class detail_driver {
  
	

	public static void main(String[] args) throws IOException {
	  
	
		//准备工作需要读入policy策略文件
		//把要进行插桩的根目录文件夹放到工程目录bin文件夹下
	
		//工程没有传入其他参数
	
	  
	 //读策略文件目录
	 String path = "D:\\soot\\eclipse\\workspace\\Policys";
	 //
	 
	 // File对象 可以是文件或者目录
     File file = new File(path);
     File[] array = file.listFiles();//把policy文件列表放到array中
     
     for (int i = 0; i < array.length; i++) {
         if (array[i].isFile()) //判断如果是文件
         {
            String yangtmpt2 =array[i].getPath();//把路径读下来
           
            //打开当前策略文件，读取策略
            HashMap<String, HashMap<String, String>>  currentpolicy=read_policy(yangtmpt2);
            
            //此时policy内容已经存放到currentpolicy中   【此时获得的是某一个policy文件的内容】
            //{com.android.insecurebank.RestClient={java.lang.String postHttpContent(java.lang.String,java.util.Map)=sourcestart￥<com.android.insecurebank.RestClient: java.lang.String postHttpContent(java.lang.String,java.util.Map)>￥$r8 = virtualinvoke $r7.<java.net.URL: java.net.URLConnection openConnection()>()##1￥2￥1##2￥2￥2##sinkend￥<com.android.insecurebank.RestClient: java.lang.String postHttpContent(java.lang.String,java.util.Map)>￥staticinvoke <android.util.Log: int e(java.lang.String,java.lang.String)>("RestClient.java", $r16)￥$r16(java.lang.String) * | >>}}
            //首先指向了某一个类  
            /*当前的策略currentpolicy对应一条信息流泄露路径，参考“路径示例.docx”，该策略的三级映射如下：
             
             * 示例：
					一级：类名：
					soot.jimple.infoflow.test.ArrayTestCode
					二级：方法名：
					int concreteWriteReadSamePosIntArrayTest()
					三级：该方法的插桩逻辑：
					sourcestart￥<soot.jimple.infoflow.test.ArrayTestCode: int concreteWriteReadSamePosIntArrayTest()>￥yangvar1 = staticinvoke <soot.jimple.infoflow.test.android.TelephonyManager: int getIMEI()>()##sinkend￥<soot.jimple.infoflow.test.ArrayTestCode: int concreteWriteReadSamePosIntArrayTest()>￥virtualinvoke cm.<soot.jimple.infoflow.test.android.ConnectionManager: void publish(int)>(yangvar22) ￥yangoutputvar#2(int) * | >>
             * 
             */
            
            //首先更新全局的信息流路径名称，等于x.policy这条策略的文件名的数组部分
            String filename = array[i].getName();
            String onlynameString=filename.substring(0,filename.lastIndexOf("."));
            
            detail_global_policy.current_pathid=Integer.parseInt(onlynameString);//获取文件名
            
            
            if (detail_global_policy.current_pathid>security.detail_embeded_code.max_paths_num)//安全判定
            {
    			System.out.println("插桩的信息流路径id超过预期值，该路径被忽略不插桩！！");
    			continue;
    		}
           //这一个policy文件
          //下面开始加载【该条信息流相关的类集合】进行分析。
  
            String current_classname="";
            
            for (Map.Entry<String, HashMap<String, String>> item : currentpolicy.entrySet()) {
                //获取当前类名
            	current_classname=item.getKey();
            	//调用soot分析当前关联类
            	
            	
            	String[] yangarg=new String[1];
            	
                yangarg[0]=current_classname;//"app2.ArrayTestCode"
                		//"com.android.insecurebank.PostLogin";
                		//"app2.ArrayTestCode";
                		//current_classname;//"app2.ArrayTestCode";
                
                //更新全局的面向每个类分析的函数
                detail_global_policy.update_currentpolicy(item.getValue());
                
                //开始分析和插桩
                analyze_target_class(yangarg);
                
                outputclass(args);
                deletejimple();
           	  
               }
         }
     }
	
  }
	
	public static void deletejimple(){
		
		    String  filePath="D:\\soot\\eclipse\\workspace\\yanginstrument\\sootOutput\\jimpleoutput";
	        File scFileDir = new File(filePath);
	        File TrxFiles[] = scFileDir.listFiles();
	        for(File curFile:TrxFiles ){
	            curFile.delete();  
	        }
	    
	}
	public static void outputclass(String[] args) throws MalformedURLException, IOException {
	   
     //复位soot
   	 soot.G.reset();
   	 //初始化环境
   	 initializeSoot();
   	  
    //D:\\soot\\eclipse\\workspace\\yanginstrument\\src\\instrument\\ClassConversion.java
    String javaAbsolutePath = "D:\\soot\\eclipse\\workspace\\yanginstrument\\src\\ClassConversion.java";    
    String jarAbsolutePath = "D:\\soot\\eclipse\\workspace\\yanginstrument\\ClassConversion\\soot-2.5.0.jar";

    //String javaAbsolutePath = "D:\\soot\\eclipse\\workspace\\yanginstrument\\sootOutput\\javaoutput\\dava\\src\\com\\android\\insecurebank\\"+current_classname.substring(current_classname.lastIndexOf(".")+1)+".java";
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		//compiler.run(null, null, null, "-encoding", "UTF-8", "-classpath", javaAbsolutePath.toString(), javaAbsolutePath);
 	try {
 		compiler.run(null, null, null, "-encoding", "UTF-8", "-classpath", jarAbsolutePath.toString(), javaAbsolutePath);

	} catch (Exception e) {
		// TODO: handle exception
	}
		//此时只是编译了 并没有运行
 	try (URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { new URL("file:/D:/soot/eclipse/workspace/yanginstrument/src/") })) {
		Class<?> clazz = urlClassLoader.loadClass("ClassConversion");
		Method mainMethod = clazz.getMethod("main", String[].class);
		/**
		 * 把参数强制转换为Object的原因
		 * 可变参数是jdk5之后的东西
		 * 如果说不把这个String[]转换为Object的话
		 * String[]里面的每个参数,都会被当作一个新的String[]被main方法加载,而main方法的参数只有一个
		 */
		
		mainMethod.invoke(clazz, (Object) args);
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchMethodException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 	
 	//String javaAbsolutePath = "D:\\soot\\eclipse\\workspace\\yanginstrument\\src\\instrument\\ClassConversion.java";    
	
	//String jarAbsolutePath = "D:\\soot\\eclipse\\workspace\\yanginstrument\\ClassConversion\\soot-2.5.0.jar";
 
 /*
 Process process = Runtime.getRuntime().exec("javac -classpath "+ jarAbsolutePath+ " " + javaAbsolutePath);
 try {
     InputStream errorStream = process.getErrorStream();
     InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
     BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
     String line = null;
     while ((line=bufferedReader.readLine()) != null){
         System.out.println(line);
     }
     int exitVal = process.waitFor();
     System.out.println("Process exitValue: " + exitVal);
 } catch (InterruptedException e) {
     e.printStackTrace();
 }
 */
 	
      	  
     //思路一： 主函数执行完毕后输出java（出现问题 有输出Java文件输出不出来的 例 1.policy）
  	 //对java文件进行编译生成class文件   
     //!!!无法编译 宣告失败
   
   	 //思路二： 主函数执行完毕后输出none
     //直接写文件生成class文件
       
	    /*
	     * Chain<SootClass> appClasses = Scene.v().getApplicationClasses(); 
		Iterator<SootClass> classIt = appClasses.iterator();
        while (classIt.hasNext()) {
            SootClass sClass = (SootClass) classIt.next();
            String fileName = SourceLocator.v().getFileNameFor(sClass, Options.output_format_class);
            OutputStream streamOut = null;
			try {
				streamOut = new JasminOutputStream(
				                            new FileOutputStream(fileName));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            PrintWriter writerOut = new PrintWriter(
                                        new OutputStreamWriter(streamOut));
            JasminClass jasminClass = new soot.jimple.JasminClass(sClass);
            jasminClass.print(writerOut);
            writerOut.flush();
            try {
				streamOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			}
            
	     */
        //！！！仍然出现堆栈为负高度的问题 宣告失败
     
	}
  
  public static HashMap<String, HashMap<String, String>> read_policy(String yangtmpt2file)
  {
	  // 读
      FileInputStream fis = null;
      ObjectInputStream ois = null;
      try {
			fis = new FileInputStream(yangtmpt2file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      try {
			ois = new ObjectInputStream(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     // HashMap<Integer, Student> stuRead = new HashMap<Integer, Student>();
      HashMap<String, HashMap<String, String>> currentpolicy = new HashMap<String, HashMap<String, String>>();

      try {
    	  currentpolicy = (HashMap<String, HashMap<String, String>> ) ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      try {
			ois.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      return currentpolicy;
  }

  
  public static void analyze_target_class(String[] args)
  {
	  //复位soot
	 soot.G.reset();
	 //初始化环境
	 initializeSoot();
	 //设置参数

	 Options.v().set_soot_classpath("D:\\soot\\eclipse\\workspace\\yanginstrument\\bin;D:\\soot\\eclipse\\workspace\\yanginstrument\\jre\\jce.jar;D:\\soot\\eclipse\\workspace\\yanginstrument\\jre\\rt.jar");
     
	 Pack jtp = PackManager.v().getPack("jtp");
	 
	 jtp.add(new Transform("jtp.instrumenter", new detail_Instrumenter()));
	  
	 //开始分析目标java类   
	 soot.Main.main(args);
	 //复位插桩参数
	 jtp.remove("jtp.instrumenter"); 
	 
  }
  
  
  public static void initializeSoot() {
	//初始化环境
	    Options.v().set_src_prec(Options.src_prec_class);
	    
	  
	    Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
		
		Options.v().set_android_jars("D:\\soot\\eclipse\\workspace");
		Options.v().set_output_dir("D:\\soot\\eclipse\\workspace\\yanginstrument\\sootOutput\\jimpleoutput");

		Options.v().set_output_format(Options.output_format_jimple);
		Options.v().set_whole_program(true);
		
		//以下为设置soot参数测试数据，可先不管 
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


