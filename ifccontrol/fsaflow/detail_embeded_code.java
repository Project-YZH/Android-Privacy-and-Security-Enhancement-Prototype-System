

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

public class detail_embeded_code {
 
  //最大信息流路径个数
  public static int max_paths_num=100;
  //被插桩程序的全局信息流路径id数据
  public static detail_embeded_object[] paths=new detail_embeded_object[max_paths_num]; 
 

  //敏感源的插桩
  public static void source_logic(int pathid,int curpos,String sourcename) {
	  
	  paths[pathid]=new detail_embeded_object();
	  //记录敏感点
	  paths[pathid].source=new String(sourcename);
	  //激活该信息流路径，初始为当前位置
	  paths[pathid].curpos=0;
	
	  
	  
  }
  //分支点的插桩
  public static void branch_logic(int pathid,int curpos,String policy) {
	  
	  if (paths[pathid]==null)
		  return; //如果分支涉及的该信息流路径根本就没有启动，稳妥的方法是不处理，继续执行，sink点拒绝即可
	  //判断是否前结点和后续结点是邻居关系
	  if (paths[pathid].curpos!=curpos-1)
	  {
		  //不是邻居关系则直接放弃跟踪,返回
		  paths[pathid].curpos=-2;
		  return;
	  }
	  //是邻居关系，则继续
	  if ("accept".equals(policy))
	  {
		  //该分岔允许，则更新新的安全状态，向前走一步，继续跟踪
		  paths[pathid].curpos++;
	  }
	  if ("reject".equals(policy))
	  {
		  //该分岔拒绝，则禁用该信息流，不跟踪了
		  paths[pathid].curpos=-2;
	  }
  }
//释放点的插桩，插桩到当前语句之前
  public static boolean sink_logic(int pathid,int curpos,String sink) {
	  
	  if (paths[pathid]==null)
		  return true; //如果分支涉及的该信息流路径根本就没有启动，稳妥的方法是不处理，继续执行，sink点拒绝即可
	  
	  paths[pathid].sink=new String(sink);
	  //判断是否前结点和后续结点是邻居关系
	  if (paths[pathid].curpos!=curpos-1)
	  {
		  //不是邻居关系则放弃跟踪,放行
		  paths[pathid].curpos=-2;
		  return true;
	  }
	 
	  //最后审计一下
	  audit_sink_disclose(pathid,paths[pathid]);
	  
	  //能走到这里，说明该信息流泄露路径完全出现，即将信息泄露，必须禁止！！
	  return false;
	  
  }
  public static void audit_sink_disclose(int pathid,detail_embeded_object yangobject)
  {
	  //根据实际情况，可以将该次事件的源、释放点语句，发生时间，路径编号写到日志文件
	  //或者发消息/邮件出去
	  //未完成...
  }

}


