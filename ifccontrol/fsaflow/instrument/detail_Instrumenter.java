 package instrument;
/*
 * InvokeStaticInstrumenter inserts count instructions before
 * INVOKESTATIC bytecode in a program. The instrumented program will
 * report how many static invocations happen in a run.
 * 
 * Goal:
 *   Insert counter instruction before static invocation instruction.
 *   Report counters before program's normal exit point.
 *
 * Approach:
 *   1. Create a counter class which has a counter field, and 
 *      a reporting method.
 *   2. Take each method body, go through each instruction, and
 *      insert count instructions before INVOKESTATIC.
 *   3. Make a call of reporting method of the counter class.
 *
 * Things to learn from this example:
 *   1. How to use Soot to examine a Java class.
 *   2. How to insert profiling instructions in a class.
 */

/* InvokeStaticInstrumenter extends the abstract class BodyTransformer,
 * and implements <pre>internalTransform</pre> method.
 */ 
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.Units;
import soot.options.Options;
import soot.tagkit.Tag;
import soot.util.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.ObjectInputStream.GetField;
import java.nio.file.Path;
import java.security.PublicKey;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//完成对相关函数的插桩

  public class detail_Instrumenter extends BodyTransformer{

/* some internal fields */
	/* internalTransform goes through a method body and inserts 
	   * counter instructions before an INVOKESTATIC instruction
	   */
  protected void internalTransform(Body body, String phase, Map options) {
	    
	    
		// body's method
		SootMethod method = body.getMethod();
		Body[] body_in=new Body[1];
	    // debugging
				
		System.out.println("instrumenting method : " + method.getSignature());
		
		//查看当前方法（函数）是否是当前信息流路径上的结点
	    //此时依次枚举method  Transform过来的是某个.class文件    method来自于.class文件中
	    //policy是在文件夹中依次读取的  此时读取了一个policy  policy早已生成，是一个hashmap 
	    //此时用例的policy 
	    //{java.lang.String postHttpContent(java.lang.String,java.util.Map)=sourcestart￥<com.android.insecurebank.RestClient: java.lang.String postHttpContent(java.lang.String,java.util.Map)>￥$r8 = virtualinvoke $r7.<java.net.URL: java.net.URLConnection openConnection()>()##1￥2￥1##2￥2￥2##sinkend￥<com.android.insecurebank.RestClient: java.lang.String postHttpContent(java.lang.String,java.util.Map)>￥staticinvoke <android.util.Log: int e(java.lang.String,java.lang.String)>("RestClient.java", $r16)￥$r16(java.lang.String) * | >>}
		//用method名 去匹配 key值
	    String currentmethodpolicy = detail_global_policy.currentclasspolicy.get(method.getSubSignature());
		
	    if (currentmethodpolicy == null) return;
	    //【解决了】这个currentmethodpolicy一直是null
	    //currentmethodpolicy=null表示该条policy里没有这个method 需要循环 继续查找这个类的下一个函数
	    //以下工程按照某一实例进行测试即可
	    //选取第一个读取出的实例如下：
	    //method.SubSignature = java.lang.String postHttpContent(java.lang.String,java.util.Map)
	    //currentmethodpolicy = sourcestart￥<com.android.insecurebank.RestClient: java.lang.String postHttpContent(java.lang.String,java.util.Map)>￥$r8 = virtualinvoke $r7.<java.net.URL: java.net.URLConnection openConnection()>()##1￥2￥1##2￥2￥2##sinkend￥<com.android.insecurebank.RestClient: java.lang.String postHttpContent(java.lang.String,java.util.Map)>￥staticinvoke <android.util.Log: int e(java.lang.String,java.lang.String)>("RestClient.java", $r16)￥$r16(java.lang.String) * | >>
	    //此时 method和policy第一次对应上 当前方法是信息流路径上的结点 需要进行处理
	    
	      //如果该函数需要插桩（函数method和currentclasspolicy对应上了），则继续往下处理（即判断当前方法是信息流路径上的结点）
	      //currentmethodpolicy示例1：
	   	  //sourcestart￥<soot.jimple.infoflow.test.ArrayTestCode: int concreteWriteReadSamePosIntArrayTest()>￥yangvar1 = staticinvoke <soot.jimple.infoflow.test.android.TelephonyManager: int getIMEI()>()##sinkend￥<soot.jimple.infoflow.test.ArrayTestCode: int concreteWriteReadSamePosIntArrayTest()>￥virtualinvoke cm.<soot.jimple.infoflow.test.android.ConnectionManager: void publish(int)>(yangvar22) ￥yangoutputvar#2(int) * | >>
	      //currentmethodpolicy示例2：
	      //##newinvoke##branchmid￥1￥2￥1##sinkend￥<soot.jimple.infoflow.test.ArrayTestCode: int concreteWriteReadSamePosIntArrayTest()>￥virtualinvoke cm.<soot.jimple.infoflow.test.android.ConnectionManager: void publish(int)>(yangoutputvar#2)￥yangoutputvar#2(int) * | >>
	    
	    System.out.println("找到了相关函数");
	    
	    System.out.println("当前policy为 " +detail_global_policy.current_pathid + ".policy:  " +detail_global_policy.currentclasspolicy);
	    //处理该方法的插桩策略，先变成数组,数组中每一项对应于当前信息流路径上的一个结点，即具体的结点信息
	   
	    String[] methodpolicyitem=currentmethodpolicy.split(detail_global_policy.yangtopseparator);
	    //这个函数需要插桩，并且这个函数可能同时存在source branch sink，所以 把policy按节点分开，从[0]节点开始遍历 依次找到source branch sink
	    //System.out.println(methodpolicyitem);
	    
	    //每个结点 即 methodpolicyitem的每一项类型包括如下几个：
	  	//public static String yanginvokenew = "newinvoke"; //该结点表明后续结点分析从该函数的起始位置开始（可能重新）分析
		//public static String yangbranchmid="|branchmid|"; //该结点从该函数的当前位置找下一个分支语句
	 	//public static String yangsourcestart = "sourcestart";//该结点是该函数的当前位置找的敏感源语句
		//public static String yangsinkend = "sinkend";  //该结点是从该函数的当前位置的释放点语句
			   
	    //针对不同情况开始插桩
	    // get body's unit as a chain
	    //考虑对象作为函数参数时是值参，这里想作为引用参数，所以用上了对象数组
	    Chain[] units_array=new Chain[1];
	    units_array[0]= body.getUnits(); //units_array[0]是函数体
	    
	    Iterator[] stmtIt_array = new Iterator[1]; //stmtIt[0]是函数的当前分析位置
	    stmtIt_array[0]=units_array[0].snapshotIterator();
	   
	    String[] detailitem=null;
	    		
	    for (int i=0;i<methodpolicyitem.length;i++)
	    	//此时i循环 是对methodpolicyitem的遍历 找到的source branch sink都会对应于detailitem[0]，所以逻辑成立。
	    {
	    	detailitem=methodpolicyitem[i].split(detail_global_policy.yangsecondoryseparator);
	    	stmtIt_array[0]=units_array[0].snapshotIterator();
	    	if (detailitem==null)
	    	{
				System.out.println("路径中的结点错误，程序退出！！");
				 System.exit(0);
			}
	    	
	    	if (detail_global_policy.yanginvokenew.equals(detailitem[0])) {
	    		//初始化当前位置到函数开始位置
	    		stmtIt_array[0]=units_array[0].snapshotIterator();
	    		//Options.v().set_output_format(Options.output_format_none);
				continue;
			}
	    	if (detail_global_policy.yangsourcestart.equals(detailitem[0])) {
	    		System.out.println("***********************发现了source点 *****************************************");
	    		
	    		if (deal_sourcestart(units_array,stmtIt_array,detailitem,detail_global_policy.current_pathid))
	    		{
	    			System.out.println("路径中的处理源点错误，程序退出！！");
	   			    System.exit(0);
	    		}
				continue;
			}
	    	if (detail_global_policy.yangbranchmid.equals(detailitem[0])) {
	    		System.out.println("***********************发现了branch点***************************************** ");
	    		
	    		//##newinvoke##|branchmid|￥3￥2￥1##sinkend￥4￥<com.android.insecurebank.PostLogin: void dotransfer()>￥staticinvoke <android.util.Log: int d(java.lang.String,java.lang.String)>("EXAMPLE", $r14)￥$r14(java.lang.String) * | >>
	    		if (deal_branchmid(units_array,stmtIt_array,detailitem,detail_global_policy.current_pathid))
	    		
	    		{
	    			System.out.println("路径中的处理分支点错误，程序退出！！");
	   			    System.exit(0);
	    		}
				continue;
			}
	    	if (detail_global_policy.yangsinkend.equals(detailitem[0])) {
	    		System.out.println("***********************发现了sink点 *****************************************");//有一个问题，是如果有source点就一定有sink点么
	    		body_in[0]=body;
	    		//检查此时的detailitem[0]
	    		
	    		 if (deal_sinkend(body_in,units_array,stmtIt_array,detailitem,detail_global_policy.current_pathid))
	    		{
	    			System.out.println("路径中的处理释放点错误，程序退出！！");
	   			    System.exit(0);
	    		}
	    			    		  		
	     		//System.out.println("完成了 "+ detail_global_policy.current_pathid +".policy 的插桩");
				continue;
			}
	    }
    
	   //System.out.println("处理完成 或 不需要处理");
	  }

  static SootClass embededClass;
  static SootMethod source_logic , branch_logic , sink_logic ;
  static { 
    embededClass = Scene.v().loadClassAndSupport("security.detail_embeded_code");
    source_logic = embededClass.getMethod("void source_logic(int,int,java.lang.String)");
    branch_logic = embededClass.getMethod("void branch_logic(int,int,java.lang.String)");
    sink_logic   = embededClass.getMethod("boolean sink_logic(int,int,java.lang.String)");
  }
 
  
public void insert_source_logic(Chain[] units,Stmt stmt,String source,int pathid)
  {
	  
	  System.out.println("开始对source进行插桩");
	  
	  //插入的目标函数
	  //branch_logic(int pathid,int curpos,String policy)；
	  	  
	  //具体做法如下，1声明函数
	  InvokeExpr incExpr= Jimple.v().newStaticInvokeExpr(source_logic.makeRef(),
						  IntConstant.v(pathid),IntConstant.v(0),StringConstant.v(source));
	 //	  
     // 2. then, make a invoke statement
     Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);

     // 3. insert new statement into the chain 
     //    (we are mutating the unit chain).
    units[0].insertAfter(incStmt,(Stmt)stmt);
    
    System.out.println("***********************source点插桩完成！！*****************************************\n\n");
	  return;
  }
 
  public void insert_branch_logic(Chain[] units,Stmt stmt,int pathid,int curpos,String policy)
  {
	  System.out.println("开始对branch进行插桩");
	  //插入的目标函数
	
	  //branch_logic(int pathid,int curpos,String policy)
	  
	  //和insert_source_logic没有大的不同，也都是带两个int参数，1个string参数，可模仿实现  
	  
	  //1声明函数
	 InvokeExpr incExpr= Jimple.v().newStaticInvokeExpr(branch_logic.makeRef(),
						  IntConstant.v(pathid),IntConstant.v(curpos),StringConstant.v(policy));
	  //	  
      // 2. then, make a invoke statement
	  Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);

      // 3. insert new statement into the chain 
      //    (we are mutating the unit chain).
      units[0].insertBefore(incStmt,(Stmt)stmt);
      System.out.println("***********************branch点插桩完成！！*****************************************\n\n");
	  return;
  }
 
  
  
  public static String control_sink(String variables) {
	
		  //判断传入变量类型
		  String variablesstr = variables.substring(variables.indexOf("(")+1,variables.indexOf(")"));
		 
		  if (variablesstr.equals("java.lang.String")) {
			//System.out.println("string");
			return "java.lang.String";
		}
		  else if (variablesstr.equals("int")){
			//System.out.println("int");
			return "int";
		}
		  else {
				//System.out.println("none");
				return "none";
			}
  }
 
  
  public void insert_sink_logic(Body[] body_in,Chain[] units,Stmt stmt,String sink,int pathid,int curpos,String variables)

  {
	  System.out.println("开始对sink进行插桩");
      InvokeExpr incExpr= Jimple.v().newStaticInvokeExpr(sink_logic.makeRef(),IntConstant.v(pathid),IntConstant.v(curpos),StringConstant.v(sink));
      
	  Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);
	  
      units[0].insertBefore(incStmt,(Stmt)stmt);
      
      System.out.println("***********************已插入sink_logic！！*****************************************\n\n");
      
      
      //控制
//      String stmtString =stmt.toString();
//	  String variable1=stmtString.substring(stmtString.lastIndexOf("("));
//	  String variable;
//	  if (variable1.contains("$")) {
//		  variable=variable1.substring(variable1.indexOf("$"),variable1.indexOf(")"));
//		  
//	}else {
//		variable=variable1.substring(variable1.indexOf("r"),variable1.indexOf(")"));
//		
//	}
//	  
//	  String reftypeString=control_sink(variables);
//	  if (reftypeString!="none") {
//		  
//		  Local  tmpRef;
//	      tmpRef = Jimple.v().newLocal(variable, RefType.v(reftypeString));
//	      body_in[0].getLocals().add(tmpRef);
//		  
//		  if (reftypeString.equals("java.lang.String")) {
//			  units[0].insertBefore(Jimple.v().newAssignStmt(tmpRef, StringConstant.v("yuanzhanhui")),stmt);
//		}
////		  if (reftypeString.equals("int")) {
////			  units[0].insertBefore(Jimple.v().newAssignStmt(tmpRef, IntConstant.v(0)),stmt);
////		}
//		  
//	      
//	}
//      
      
	  //第一阶段：
	  //插入的目标函数1:
	  // boolean yangzhi = sink_logic(int pathid,int curpos,String sink,String variable)
	  //该目标函数分为 两小步
	  
	  //1）插入：  boolean yangzhi; 
	  
	  /*Local arg;
	  arg = Jimple.v().newLocal("l0", ArrayType.v(RefType.v("java.lang.String"), 1));
	  body_in[0].getLocals().add(arg);
	  */
	
	  //2）插入：yangzhi=sink_logic(pathid,curpos,sink,variable;
      //这里如何将yangzhi=sink_logic还未实现，请查阅本eclipose所有工程中的如下赋值示例
    
      //第二阶段：
      //假设  从variables中得到的敏感变量是yang(String类型)和zhi(int类型）
  	  //下面根据第一阶段yangzhi的赋值决定是否清空变量yang和zhi
    
      //1)插入语句
      //if (!yangzhi) 
      
      //2)插入语句
      //then yang="";
	  //下面是清空变量yang的代码
      /*
       Chain locals=body_in[0].getLocals();
	  if (!locals.isEmpty())
	  {

		  Local localitem=(Local)locals.getFirst();
		  while (localitem!=null)
		  {
			  //以下这些代码均为仿真，函数意思表达了，不见得是正确的函数，真实语义需要调试确认！！
			  if ("yang".equals(localitem.getName()))
			  {
				  //分析变量yang的类型是不是string，
				  //还应判断，是不是和跟踪模块传来的类型一致，这里还没有检查
				  String yangtype=localitem.getType().toString();				  
				  if (yangtype.equals("java.lang.String"))
				  {  
				  //插入语句 "yang = """ ,后续考虑 将yang用和原来yang等长的空格代替
			        units[0].insertBefore(Jimple.v().newAssignStmt(localitem, 
			        		StringConstant.v("")), (Stmt)stmt);			      
			      //关于int、数组等其他类型可以仿照string类型的处理，，然后对所有相关敏感变量清空的语句
			      
				  }
			  }
			  localitem=(Local)locals.getSuccOf(localitem);
		  }
	  }
       */	 
  }
  
  public static  boolean dealsplitsource1;
  //public static  boolean dealsplitsource2;
  
  
  public String splitsource1(String content) {
	  //specialinvoke $r2.<android.content.Intent: void <init>(android.content.Context,java.lang.Class)>($r0, class "com/android/insecurebankv2/LoginActivity")
	  //specialinvoke r5.<android.content.Intent: void <init>(android.content.Context,java.lang.Class)>(r6, class "com/android/insecurebankv2/LoginActivity")
	  String[] str = content.split(".>.");
	  String contentSplit =null;
	  dealsplitsource1=false;
	  
	  if (str.length > 1) {
		
	  if (str[str.length - 1 ]!= null) {
		  contentSplit =  str[str.length - 1 ]; 
	}
	  else {
		  contentSplit =  str[0];
	}
	  dealsplitsource1=true;
	  return contentSplit;
	  }
	  
	  return content;
	  
	}
  public String splitsource2(String content) {
		Pattern pattern = Pattern.compile("[0-9]");
		//Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(content);
		
		while (matcher.find()) {
			
				return matcher.replaceAll("");
		}
		  return content;  
	}
  public String splitsource3(String content) {
	   
		Pattern pattern = Pattern.compile("[$]");
		//Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(content);
		
		while (matcher.find()) {
				return matcher.replaceAll("");
		}
		  return content;
	}
  

  public boolean deal_sourcestart(Chain[] units,Iterator[] stmtIt,String[] detailitem,int pathid)
  {
	  System.out.println("处理source点");
	  //detailitem=
	  //0号项：标志sourcestart
	  //1号项：源所在类及函数名：<soot.jimple.infoflow.test.Yang: int testA(int)>
	  //<com.android.insecurebank.RestClient: java.lang.String postHttpContent(java.lang.String,java.util.Map)>
	  //2号项：源这条语句：yangvar1 = staticinvoke <soot.jimple.infoflow.test.android.TelephonyManager: int getIMEI()>()
	  
	  //$r8 = virtualinvoke $r7.<java.net.URL: java.net.URLConnection openConnection()>()  从policy来
	  //$r9 = virtualinvoke $r8.<java.net.URL: java.net.URLConnection openConnection()>()  从body.units来
	  
	  while (stmtIt[0].hasNext()) {
		  
		  Stmt stmt = (Stmt)stmtIt[0].next();
		
		  //一条一条语句串匹配，顺序去找吧，你简单实现下...，
		  
		  //detailitem[2]="$r9 = virtualinvoke $r8.<java.net.URL: java.net.URLConnection openConnection()>()";
		  
		  //String stmtString=stmt.toString();
		  //String stmtstr =splitsource1(stmt.toString()) ;
		  //String detailitemstr =splitsource1(detailitem[2]);
		  
		  String stmtstr =stmt.toString() ;
		  String detailitemstr =detailitem[2];
		  
		  String stmtstr2 =splitsource2(stmtstr);
		  String detailitemstr2 =splitsource2(detailitemstr);
		  String stmtstr3 =splitsource3(stmtstr2) ;
		  String detailitemstr3 =splitsource3(detailitemstr2);
		  if (stmtstr.equals(detailitemstr)) 
		  {
			  System.out.println("找到了 source点对应的语句");
			  System.out.println(stmt.toString());
			  //找到后，插入语句，通过下面函数实现
			  insert_source_logic(units,stmt,detailitem[1]+detailitem[2],pathid);
			  dealsplitsource1=true;
		  }
		  if (!dealsplitsource1) {
			  if (stmtstr2.equals(detailitemstr2)) {
			    	 System.out.println("找到了 source点对应的语句");
					  System.out.println(stmt.toString());
					  //找到后，插入语句，通过下面函数实现
					  insert_source_logic(units,stmt,detailitem[1]+detailitem[2],pathid);
			       }else if (stmtstr3.equals(detailitemstr3)) {
			    	   System.out.println("找到了 source点对应的语句");
			 		  System.out.println(stmt.toString());
			 		  //找到后，插入语句，通过下面函数实现
			 		  insert_source_logic(units,stmt,detailitem[1]+detailitem[2],pathid);
			       }
		  }
		  }
	  
	  return false;
  }
  
  public boolean deal_branchmid(Chain[] units,Iterator[] stmtIt,String[] detailitem,int pathid)
  {
	  System.out.println("处理branch点");
	  int branchnumber=0;
	 // detailitem=
	  //0号项：标志branchimid
	  //1号项：该结点全局编号 如2  (就这个全局编号是啥意思？)
	  //2号项：该结点的分支有几个岔路 如2
	  //3号项：第几个岔路（索引起始0号岔路）如1

	  while (stmtIt[0].hasNext()) {
		  Stmt stmt = (Stmt)stmtIt[0].next();
		   
		
		  if (stmt.branches())
			  //发现问题，23.policy指向的method没有分支
			  //23.policy 指向的第二个类策略内容为 {void <init>(com.android.insecurebank.PostLogin)=##newinvoke##newinvoke, void onClick(android.view.View)=##newinvoke##|branchmid|￥1￥2￥1}
			  // void onClick(android.view.View)这个函数没有分支
	      {	  
			  branchnumber=branchnumber+1;
			  if ( branchnumber == Integer.parseInt(detailitem[1]))
			  {

			  //第一条分叉 if $i3 <= 0 goto r99 = (android.content.Context) r0
			  //第二条分叉 if r2 == null goto (branch)
			  //第三条分叉 if $z0 != 0 goto r97 = new java.lang.StringBuilder
			  //现在找到了分叉，分叉的结点全局编号为detailitem[1]
			  //分叉的第detailitem[3]个岔路是允许accept，其他是拒绝reject
	    	  
	    	  //下列语句得到所有分岔
	    	  List<UnitBox> yangunitbox=stmt.getUnitBoxes();
	    	  System.out.println(yangunitbox.getClass().toString());
	    	 int a=yangunitbox.size();
	    	  if (yangunitbox.size()+1 != Integer.parseInt(detailitem[2]))
	    	  {
	    			System.out.println("当前分叉语句和跟踪分析模块得到的分叉个数不同错误，退出！！");
	   			    //System.exit(0);	
	    			continue;
	    	  }
	    	 System.out.println("分叉数已对应");
	    	 System.out.println("找到了branch点"+branchnumber+"对应的语句");
	    	 System.out.println(stmt.toString());
	    	  int sn=1;
	    	  for (UnitBox ubox : stmt.getUnitBoxes())
	    	  {
	    		  if (sn==Integer.parseInt(detailitem[3]))
	    		  {
	    			  insert_branch_logic(units,stmt,pathid,Integer.parseInt(detailitem[1]),"accept");
	    			  
	    		  }
	    		  else
	    			  insert_branch_logic(units,stmt,pathid,Integer.parseInt(detailitem[1]),"reject"); 
	    		      sn=sn+1;
	    	  }
	      }
	      }
		  
	  }
	  return false;
  }
  
  public String splitsink1(String content) {
	Pattern pattern = Pattern.compile("[0-9]");
	//Pattern pattern = Pattern.compile("\\d+");
	Matcher matcher = pattern.matcher(content);
	
	while (matcher.find()) {
		
		return matcher.replaceAll("");
		
	}
	  return "";  
}
  
   public String splitsink2(String content) {
   
		Pattern pattern = Pattern.compile("[$]");
		//Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(content);
		
		while (matcher.find()) {
			
			return matcher.replaceAll("");
			
		}
		  return "";
	}
	
	 
  public boolean deal_sinkend(Body[] body_in,Chain[] units,Iterator[] stmtIt,String[] detailitem,int pathid)
  {
	  System.out.println("处理sink点");
	  // detailitem=
	  //0号项：标志sinkend
	  //1号项：当前位置如88
	  //2号项：释放点所在类及函数名：<soot.jimple.infoflow.test.ArrayTestCode: int concreteWriteReadSamePosIntArrayTest()>
	  //3号项：释放点这条语句：virtualinvoke cm.<soot.jimple.infoflow.test.android.ConnectionManager: void publish(int)>(yangoutputvar#2)
	  //4号项：释放点这条语句中哪个变量是敏感信息yangoutputvar#2(int) * | >>
	  
	  //sinkend
	  //￥<com.android.insecurebank.RestClient: java.lang.String postHttpContent(java.lang.String,java.util.Map)>
	  //￥staticinvoke <android.util.Log: int e(java.lang.String,java.lang.String)>("RestClient.java", $r16)
	  //￥$r16(java.lang.String) * | >>}}
	  
	  while (stmtIt[0].hasNext()) {
		  Stmt stmt = (Stmt)stmtIt[0].next();
		  
		  //一条一条语句顺序去找.......
		  //找到后，插入几条语句！！，通过下面函数实现
		  String stmtstr =splitsink1(stmt.toString()) ;
		  String detailitemstr =splitsink1(detailitem[3]);
		  String detailitemstr2 =splitsink2(detailitemstr);
		  String stmtstr2 =splitsink2(stmtstr);
		  //String stmtstr2 =splitsink2(stmtstr);
		  if (stmtstr.equals(detailitemstr2)||stmtstr.equals(detailitemstr)||stmtstr2.equals(detailitemstr)||stmtstr2.equals(detailitemstr2)) 
			  //staticinvoke <android.util.Log: int e(java.lang.String,java.lang.String)>("RestClient.java", $r16)
		  {
		  System.out.println("找到了sink点对应的语句");
		  System.out.println(stmt.toString());
		  //找到后，插入语句，通过下面函数实现
		  
		 
		 
		  insert_sink_logic(body_in,units,stmt,detailitem[2]+detailitem[3],pathid,Integer.parseInt(detailitem[1]),detailitem[4]);
		  }
			  }
	  return false;
  }
}
