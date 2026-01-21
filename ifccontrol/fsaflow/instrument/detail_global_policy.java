package instrument;
/* Usage: java MainDriver appClass
 */

/* import necessary soot packages */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.*;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.util.Chain;

//用于driver和Instrumenter在策略方面的互相引用

public class detail_global_policy {
 
  //全局可访问的当前分析的信息流路径id
  public static int current_pathid=-1;
  //全局可访问的当前分析的类的策略
  public static HashMap<String, String> currentclasspolicy;
  
  
  /********************以下和分析模块是共用一致的******************************/
	public static String yangtopseparator = "##";
	public static String yangsecondoryseparator = "￥";
    //public static String yangcalltag = "|call|";
    //public static String yangcallreturntag = "|callreturn|";
    //public static String yangsourcetag = "|source|";
	//public static String yangbranch = "|branch|";
    //public static String yangsinktag = "|sink|";
	//public static int currentpathid = 0;
	public static String yangsourcestart = "sourcestart";
	public static String yangsinkend = "sinkend";
	public static String yanginvokenew = "newinvoke";
	public static String yangbranchmid="|branchmid|";
	
	/********************以上和分析模块是共用一致的******************************/
	
	
  public static void update_currentpolicy(HashMap<String, String> policy) {
	  
	  currentclasspolicy=new HashMap<String, String>(policy);
	  
	  /*当前的策略对应一条信息流泄露路径在当前分析的class类上的路径部分，参考“路径示例.docx”，该策略的二级映射如下：
       * 示例：

				一级：方法名：
				int concreteWriteReadSamePosIntArrayTest()
				二级：该方法的插桩逻辑：
				sourcestart￥<soot.jimple.infoflow.test.ArrayTestCode: int concreteWriteReadSamePosIntArrayTest()>￥yangvar1 = staticinvoke <soot.jimple.infoflow.test.android.TelephonyManager: int getIMEI()>()##sinkend￥<soot.jimple.infoflow.test.ArrayTestCode: int concreteWriteReadSamePosIntArrayTest()>￥virtualinvoke cm.<soot.jimple.infoflow.test.android.ConnectionManager: void publish(int)>(yangvar22) ￥yangoutputvar#2(int) * | >>

       */
	  /*
	   * 方法的策略示例1：
	   * sourcestart￥<soot.jimple.infoflow.test.ArrayTestCode: int concreteWriteReadSamePosIntArrayTest()>￥yangvar1 = staticinvoke <soot.jimple.infoflow.test.android.TelephonyManager: int getIMEI()>()##sinkend￥<soot.jimple.infoflow.test.ArrayTestCode: int concreteWriteReadSamePosIntArrayTest()>￥virtualinvoke cm.<soot.jimple.infoflow.test.android.ConnectionManager: void publish(int)>(yangvar22) ￥yangoutputvar#2(int) * | >>
	   */
	  
  }
 
}


