package instrument;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;





import com.google.common.io.Files;


public class CopyClassFromBinToNewClass {

	
	public static void main(String[] args) throws Exception {

		
		
		File path=new File("C:\\Users\\YZH\\Desktop\\1");
		
		//列出该目录下所有文件和文件夹
				
		File[] files = path.listFiles();
		
		File[] filesDirectory = null;		
				
		for (int i = 0; i < files.length; i++ ){
			
			if (files[i].isDirectory())
			{
				filesDirectory = files[i].listFiles();
			}
			
		}
		
		File[] filesall = concat(files, filesDirectory);
		
		for (int i = 0; i < filesall.length; i++) {
			if (filesall[i].isDirectory()) {
				filesDirectory = filesall[i].listFiles();
				filesall[i]=filesall[i+1];
			}
		}
		filesall = concat(files, filesDirectory);
		
		//按照目录中文件最后修改日期实现倒序排序
		Arrays.sort(filesall, new Comparator<File>() {
		   @Override
		   public int compare(File file1, File file2) {
		      return (int)(file2.lastModified()-file1.lastModified());
		   }
		});
		//取最新修改的文件，get文件名
		String files0Name = filesall[0].getName();
		
		System.out.println("最新的文件是" + files0Name);

		
//        //Scanner sc =new Scanner(System.in);
//        // 指定数据源
//		//System.out.println("请输入数据源");
//		//String str1 = sc.nextLine();
//		String str1 = "C:\\Users\\YZH\\Desktop\\";
//		File source = new File(str1);
//		
//      // 指定目的地
//		//System.out.println("请输入目的地");
//		//String dest = sc.nextLine(); 
//		String dest = "C:\\Users\\YZH\\Desktop\\2";
//		copyFile(source,dest);
//		System.out.println("同步输出到New_class");
		
		
		
    }
	
	static File[] concat(File[] a, File[] b) {
		File[] c= new File[a.length+b.length];
		   System.arraycopy(a, 0, c, 0, a.length);
		   System.arraycopy(b, 0, c, a.length, b.length);
		   return c;
		}
	

     @SafeVarargs
	public static <T> T[] concatAll(T[] first, T[]... rest) {
         int totalLength = first.length;
         for (T[] array : rest) {
              totalLength += array.length;
             }
         T[] result = Arrays.copyOf(first, totalLength);
         int offset = first.length;
         for (T[] array : rest) {
             System.arraycopy(array, 0, result, offset, array.length);
             offset += array.length;
             }
         return result;
        }
 
	

	public static void copyFile(File source,String dest )throws IOException{
		//创建目的地文件夹
		File destfile = new File(dest);
		if(!destfile.exists()){
			destfile.mkdir();
		}
		//如果source是文件夹，则在目的地址中创建新的文件夹
		if(source.isDirectory()){
			File file = new File(dest+"\\"+source.getName());//用目的地址加上source的文件夹名称，创建新的文件夹
			file.mkdir();
			//得到source文件夹的所有文件及目录
			File[] files = source.listFiles();
			if(files.length==0){
				return;
			}else{
				for(int i = 0 ;i<files.length;i++){
					copyFile(files[i],file.getPath());
				}
			}
			
		}
		//source是文件，则用字节输入输出流复制文件
		else if(source.isFile()){
			FileInputStream fis = new FileInputStream(source);
			//创建新的文件，保存复制内容，文件名称与源文件名称一致
			File dfile = new File(dest+"\\"+source.getName());
			if(!dfile.exists()){
				dfile.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(dfile);
				// 读写数据
				// 定义数组
				byte[] b = new byte[1024];
				// 定义长度
				int len;
				// 循环读取
				while ((len = fis.read(b))!=-1) {
					// 写出数据
					fos.write(b, 0 , len);
				}
 
				//关闭资源
				fos.close();
				fis.close();
			
		}
	}			
}
