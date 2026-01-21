package security;
/* Usage: java MainDriver appClass
 */

/* import necessary soot packages */


import java.io.File;

import java.io.IOException;

import java.io.RandomAccessFile;

import java.util.Date;


//import android.widget.Toast;


public class detail_embeded_code {
 
  //最大信息流路径个数
  public static int max_paths_num=200;
  //被插桩程序的全局信息流路径id数据
  public static detail_embeded_object[] paths=new detail_embeded_object[max_paths_num]; 
 

  //敏感源的插桩
  public static void source_logic(int pathid,int curpos,String sourcename) {
	//获取当前时间
	  //Date date = new Date(System.currentTimeMillis());
	
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
  
  
  public static boolean sink_logic(int pathid,int curpos,String sink) throws IOException {
	  
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
	  
	  writeData2(pathid);

	  //能走到这里，说明该信息流泄露路径完全出现，即将信息泄露，必须禁止！！\
	 
	  return false;
	  
  }

  private static void writeData2(int pathid) {
	  
	  String source = paths[pathid].source;
	  String sink = paths[pathid].sink;
	  Date date = new Date(System.currentTimeMillis());
      
      
	  
	  
	  String writestr1 = "source: \n" + source + "\n"  + "sink: \n"+ sink + "\n"  + "time: \n"+ date.toString() + "\n"  +"pathID:"+ pathid + "\n" ;
	  String writestr2 ="1";
	  //String writestr = "1";
      //String filePath = "/sdcard/Insecurebank-Output";
      //String fileName = "Data.txt";
      String filePath = "/sdcard/Insecurebank-output/";
      String fileName = "Auditdata.txt";
      writeTxtToFile2(writestr1, filePath, fileName);
  }

//将字符串写入到文本文件中
  private static void writeTxtToFile2(String strcontent, String filePath, String fileName) {
      //生成文件夹之后，再生成文件，不然会出错
      makeFilePath2(filePath, fileName);

      String strFilePath = filePath + fileName;
      // 每次写入时，都换行写
      String strContent = strcontent + "\r\n";
      try {
          File file = new File(strFilePath);
          if (!file.exists()) {
              //Log.d("TestFile", "Create the file:" + strFilePath);
              file.getParentFile().mkdirs();
              file.createNewFile();
          }
          RandomAccessFile raf = new RandomAccessFile(file, "rwd");
          raf.seek(file.length());
          raf.write(strContent.getBytes());
          raf.close();
      } catch (Exception e) {
          //Log.e("TestFile", "Error on write File:" + e);
      }
  }

//生成文件

private static File makeFilePath2(String filePath, String fileName) {
      File file = null;
      makeRootDirectory2(filePath);
      try {
          file = new File(filePath + fileName);
          if (!file.exists()) {
              file.createNewFile();
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
      return file;
  }

//生成文件夹

  private static void makeRootDirectory2(String filePath) {
      File file = null;
      try {
          file = new File(filePath);
          if (!file.exists()) {
              file.mkdir();
          }
      } catch (Exception e) {
          //Log.i("error:", e + "");
      }
  }
  }
  
  



