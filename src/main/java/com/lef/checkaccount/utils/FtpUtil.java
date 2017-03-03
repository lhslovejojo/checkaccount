package com.lef.checkaccount.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.oro.text.perl.Perl5Util;
/**
 * 
 * @author lihongsong
 *
 */
public class FtpUtil {
	
	private static Log log = LogFactory.getLog(FtpUtil.class);
	

	/** 
	 * Description: 从FTP服务器下载文件 
	 * @param url FTP服务器hostname 
	 * @param port FTP服务器端口 
	 * @param username FTP登录账号 
	 * @param password FTP登录密码 
	 * @param remotePath FTP服务器上的相对路径 
	 * @param fileName 要下载的文件名 
	 * @param localPath 下载后保存到本地的路径 
	 * @return 
	 * @throws IOException 
	 */  
	public static boolean downFile(String ip, int port,String username, String password, String remotePath,String fileName,String localPath) throws IOException {  
	    boolean success = false;  
	    log.info("ip="+ip+",port="+port+",username="+username+",psw="+password);
	    FTPClient ftp = new FTPClient();  
	    try {  
	        int reply;  
	        ftp.connect(ip, port);
	    log.info("--------------------ftp.connect ok--------");    
	        //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器  
	        ftp.login(username, password);//登录
	        ftp.enterLocalPassiveMode();
	    log.info("------------------- ftp.login ok--------");       
	        ftp.setControlEncoding("GB2312");
	        ftp.setFileType(FTPClient.ASCII_FILE_TYPE);  
	        ftp.setDefaultTimeout(1000*300);
	        ftp.setDataTimeout(1000*300);
	        ftp.setBufferSize(1024);
	        reply = ftp.getReplyCode();
	   log.info("---------------------reply----"+reply);          
	        if (!FTPReply.isPositiveCompletion(reply)) {
	       log.info("-------------if-FTPReply.isPositiveCompletion(reply)=false-----beofore-disconnect()----"); 
	            ftp.disconnect();  
	            return success;  
	        } 
	        ftp.changeWorkingDirectory(remotePath);
	        File localDir = new File(localPath);
	        if(!localDir.exists()){
	        	localDir.mkdirs();
	        }
	        File localFile = new File(localDir, fileName); 
	  /*    FTPFile[] fs = ftp.listFiles();//ftp.retrieveFileStream(remote);
	    log.info("----fs----"+(fs==null?"null":fs.length));
	        for(FTPFile ff:fs){
	            if(ff.getName().equals(fileName)){
	                OutputStream is = null;   
	                try {
						is = new FileOutputStream(localFile);
						ftp.retrieveFile(fileName, is);
						is.flush();
					} catch (Exception e) {
						log.info("文件接受异常 ");
					}finally{
						if(is!=null){
							is.close();
						}
					}
	                log.info(fileName+" 文件下载成功");
	                break;
	            }
	        }*/
//-----------------------------------------------------------------------------------	        
		    /*OutputStream ous = null;
		    try{
		    	ous = new FileOutputStream(localFile);
		    	ous.flush();
		   log.info("-----------------------before:retrieveFile----------------------------");    	
		        boolean b = ftp.retrieveFile(remotePath+fileName,ous);
		         
		        if(b){
			    	log.info("---文件下载生成成功--- ");
			    }else{
			    	log.info("---文件下载生成失败--- ");
			    }
		        success = b;
		 log.info("-----------------------success:----------------------------"+b);      
		    }catch(Exception ee){
		    	log.info("文件接受异常 ");
		    	log.info(ee);
		    }finally{
		    	if(ous!=null){
		    		ous.close();
				}
		    }
		    log.info("-----------------------before:logout----------------------------"); */
//------------------------------------------------------------------------------------------------	
		    log.info("-----------before:ftp.listNames(fileName)-------------"); 
		    String[] ts =  ftp.listNames(fileName);
		    log.info("----------after:ftp.listNames(fileName)---------------"+(null==ts?"ts is null":ts.length)); 
		    if(null!=ts && ts.length>0){
		    	log.info("----------ts.length>0-----------"); 	
		    	OutputStream os = null;
		    	try{
		    		os = new FileOutputStream(localFile);
		    		boolean b = ftp.retrieveFile(remotePath+fileName, os);
					os.flush();
				    if(b){
				    	log.info("---文件下载生成成功--- ");
				    }else{
				    	log.info("---文件下载生成失败--- ");
				    }
			        success = b;
		    	}catch(Exception e){
		    		log.info("-----------------------文件接受异常------------- ");
		    		log.error(e);
		    	}finally {
		            if (os != null) {
		            	os.close();
		            }
		        }
		    }
//-----------------------------------------------------------------------------------			    
		    ftp.logout();
	        /*if(!localFile.exists()){
	        	log.info("---文件不存在--- ");
	        	log.error(fileName+" 文件不存在");
	        	throw new FileNotFoundException("文件不存在");
	        }
	        success = true;*/   
	    } catch (IOException e) {  
	    	e.printStackTrace();
	    	log.error(e);
	        throw e;
	    }finally {  
	        if (ftp.isConnected()) {  
	            try {  
	            	log.info("-----------------------before:disconnect----------------------------"); 
	                ftp.disconnect();  
	            } catch (IOException ioe) {  
	            	
	            }  
	        }  
	    }  
	    return success;  
	}
	
	
	/** 
	 * Description: 从FTP服务器下载文件 
	 * @param url FTP服务器hostname 
	 * @param port FTP服务器端口 
	 * @param username FTP登录账号 
	 * @param password FTP登录密码 
	 * @param remotePath FTP服务器上的相对路径 
	 * @param fileName 要下载的文件名 
	 * @param localPath 下载后保存到本地的路径 
	 * @return 
	 * @throws IOException 
	 */  
	public static boolean downTarFile(String ip, int port,String username, String password, String remotePath,String fileName,String localPath) throws IOException {  
	    boolean success = false;  
	    log.info("ip="+ip+",port="+port+",username="+username+",psw="+password);
	    FTPClient ftp = new FTPClient();  
	    try {  
	        int reply;  
	        ftp.connect(ip, port);
	    log.info("--------------------ftp.connect ok--------");    
	        //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器  
	        ftp.login(username, password);//登录
	        ftp.enterLocalPassiveMode();
	    log.info("------------------- ftp.login ok--------");       
	        ftp.setControlEncoding("GB2312");
	        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);  
	        ftp.setDefaultTimeout(1000*300);
	        ftp.setDataTimeout(1000*300);
	        ftp.setBufferSize(1024);
	        reply = ftp.getReplyCode();
	   log.info("---------------------reply----"+reply);          
	        if (!FTPReply.isPositiveCompletion(reply)) {
	       log.info("-------------if-FTPReply.isPositiveCompletion(reply)=false-----beofore-disconnect()----"); 
	            ftp.disconnect();  
	            return success;  
	        } 
	        ftp.changeWorkingDirectory(remotePath);
	        File localDir = new File(localPath);
	        if(!localDir.exists()){
	        	localDir.mkdirs();
	        }
	        File localFile = new File(localDir, fileName); 
	  /*    FTPFile[] fs = ftp.listFiles();//ftp.retrieveFileStream(remote);
	    log.info("----fs----"+(fs==null?"null":fs.length));
	        for(FTPFile ff:fs){
	            if(ff.getName().equals(fileName)){
	                OutputStream is = null;   
	                try {
						is = new FileOutputStream(localFile);
						ftp.retrieveFile(fileName, is);
						is.flush();
					} catch (Exception e) {
						log.info("文件接受异常 ");
					}finally{
						if(is!=null){
							is.close();
						}
					}
	                log.info(fileName+" 文件下载成功");
	                break;
	            }
	        }*/
//-----------------------------------------------------------------------------------	        
		    /*OutputStream ous = null;
		    try{
		    	ous = new FileOutputStream(localFile);
		    	ous.flush();
		   log.info("-----------------------before:retrieveFile----------------------------");    	
		        boolean b = ftp.retrieveFile(remotePath+fileName,ous);
		         
		        if(b){
			    	log.info("---文件下载生成成功--- ");
			    }else{
			    	log.info("---文件下载生成失败--- ");
			    }
		        success = b;
		 log.info("-----------------------success:----------------------------"+b);      
		    }catch(Exception ee){
		    	log.info("文件接受异常 ");
		    	log.info(ee);
		    }finally{
		    	if(ous!=null){
		    		ous.close();
				}
		    }
		    log.info("-----------------------before:logout----------------------------"); */
//------------------------------------------------------------------------------------------------	
		    log.info("-----------before:ftp.listNames(fileName)-------------"); 
		    String[] ts =  ftp.listNames(fileName);
		    log.info("----------after:ftp.listNames(fileName)---------------"+(null==ts?"ts is null":ts.length)); 
		    if(null!=ts && ts.length>0){
		    	log.info("----------ts.length>0-----------"); 	
		    	OutputStream os = null;
		    	try{
		    		os = new FileOutputStream(localFile);
		    		boolean b = ftp.retrieveFile(remotePath+fileName, os);
					os.flush();
				    if(b){
				    	log.info("---文件下载生成成功--- ");
				    }else{
				    	log.info("---文件下载生成失败--- ");
				    }
			        success = b;
		    	}catch(Exception e){
		    		log.info("-----------------------文件接受异常------------- ");
		    		log.error(e);
		    	}finally {
		            if (os != null) {
		            	os.close();
		            }
		        }
		    }
//-----------------------------------------------------------------------------------			    
		    ftp.logout();
	        /*if(!localFile.exists()){
	        	log.info("---文件不存在--- ");
	        	log.error(fileName+" 文件不存在");
	        	throw new FileNotFoundException("文件不存在");
	        }
	        success = true;*/   
	    } catch (IOException e) {  
	    	e.printStackTrace();
	    	log.error(e);
	        throw e;
	    }finally {  
	        if (ftp.isConnected()) {  
	            try {  
	            	log.info("-----------------------before:disconnect----------------------------"); 
	                ftp.disconnect();  
	            } catch (IOException ioe) {  
	            	
	            }  
	        }  
	    }  
	    return success;  
	}
	
	public static boolean downFileAfterDate(String ip, int port,String username, String password, String remotePath,String fileName,String localPath,Date date) throws IOException {  
	    boolean success = false;  
	    FTPClient ftp = new FTPClient();  
	    try {  
	        int reply;  
	        ftp.connect(ip, port);  
	        //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器  
	        ftp.login(username, password);//登录  
	        ftp.setControlEncoding("GB2312");
	        ftp.setFileType(FTPClient.ASCII_FILE_TYPE);
	        ftp.setDefaultTimeout(1000*90);
	        ftp.setDataTimeout(1000*90);
	        ftp.setBufferSize(1024);
	        ftp.enterLocalPassiveMode();
	        reply = ftp.getReplyCode();  
	        if (!FTPReply.isPositiveCompletion(reply)) {  
	            ftp.disconnect();  
	            return success;  
	        } 
	        ftp.changeWorkingDirectory(remotePath);
	        File localDir = new File(localPath);
	        if(!localDir.exists()) localDir.mkdirs();
	        File localFile = new File(localDir, fileName); 
	        FTPFile[] fs = ftp.listFiles();  
	        for(FTPFile ff:fs){ 
	            if(ff.getName().equals(fileName)){
	            	Calendar cal = ff.getTimestamp();//格林威治时间
	            	cal.add(Calendar.MILLISECOND, TimeZone.getDefault().getRawOffset());//偏差时区 
	            	if(!cal.getTime().before(date)){
	            		 OutputStream is = null;   
	 	                try {
	 						is = new FileOutputStream(localFile);
	 						ftp.retrieveFile(fileName, is);
	 						is.flush();
	 					} catch (Exception e) {
	 						log.info("文件接受异常 ");
	 					}finally{
	 						if(is!=null){
	 							is.close();
	 						}
	 					}
	 	                log.info(fileName+" 文件下载成功");
	            	}else{
//	            		throw new FileExpireException("文件"+fileName+"已过期");
	            	}
	            	break;
	            }
	        }  
	        ftp.logout();
	        if(!localFile.exists()){
	        	log.error(fileName+" 文件不存在");
	        	throw new FileNotFoundException("文件不存在");
	        }
	        success = true;  
	    } catch (IOException e) {  
	    	e.printStackTrace();
	    	log.error(e);
	        throw e;
	    } finally {  
	        if (ftp.isConnected()) {  
	            try {  
	                ftp.disconnect();  
	            } catch (IOException ioe) {  
	            	
	            }  
	        }  
	    }  
	    return success;  
	}
	
	
	
	/**
	 * @param host ftp所在的主机IP
	 * @param port 端口号
	 * @param userName 用户名
	 * @param password 密码
	 * @param changeDir change文件加，可为空，为空则不改变
	 * @param newDir 是否创建文件夹，可为空，为空则不创建
	 * @param srcFile 上传文件路径
	 */
	public static void ftpUploadFile(String host,int port,String userName,String password,String changeDir,String newDir,File srcFile){
		FTPClient ftpClient = new FTPClient();
        FileInputStream fis = null;
        try {
            ftpClient.connect(host,port);
            ftpClient.login(userName, password);
            fis = new FileInputStream(srcFile);
            int  reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
            	log.error("ftp can't connect");
            	ftpClient.disconnect();
                return;
            }
            ftpClient.setControlEncoding("GB2312");
            ftpClient.enterLocalPassiveMode();
            //设置上传目录
            if(changeDir!=null&&changeDir.trim().length()>0){
            	ftpClient.changeWorkingDirectory(changeDir);
            }
            if(newDir!=null&&newDir.trim().length()>0){
            	ftpClient.makeDirectory(new String(newDir.getBytes("GB2312"),"iso-8859-1"));
            	ftpClient.changeWorkingDirectory(new String(newDir.getBytes("GB2312"),"iso-8859-1"));
            }
            ftpClient.setBufferSize(1024);

            //设置文件类型（二进制）
            ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
            ftpClient.storeFile(new String(srcFile.getName().getBytes("GB2312"),"iso-8859-1"), fis);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("FTP客户端出错！", e);
        } finally {
            if(fis!=null){
            	try {
					fis.close();
					fis = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            try {
            	if(ftpClient.isConnected()){
            		ftpClient.logout();
	                ftpClient.disconnect();
            	}
            	ftpClient = null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
	}
	
	
	public static void ftpBatchUploadFile(String host,int port,String userName,String password,String changeDir,String newDir,File[] files){
		FTPClient ftpClient = null;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(host, port);
			ftpClient.login(userName, password);
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				log.error("ftp can't connect");
				ftpClient.disconnect();
				return;
			}
			ftpClient.setControlEncoding("GB2312");
			ftpClient.enterLocalPassiveMode();
			//设置上传目录
			if (changeDir != null && changeDir.trim().length() > 0) {
				ftpClient.changeWorkingDirectory(changeDir);
			}
			if (newDir != null && newDir.trim().length() > 0) {
				ftpClient.makeDirectory(new String(newDir.getBytes("GB2312"),
						"iso-8859-1"));
				ftpClient.changeWorkingDirectory(new String(newDir
						.getBytes("GB2312"), "iso-8859-1"));
			}
			ftpClient.setBufferSize(1024);
			//设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
			
			for(File srcFile:files){
				FileInputStream fis = null;
		        try {
		            fis = new FileInputStream(srcFile);
		            ftpClient.storeFile(new String(srcFile.getName().getBytes("GB2312"),"iso-8859-1"), fis);
		        } catch (IOException e) {
		            e.printStackTrace();
		            throw new RuntimeException("FTP客户端出错！", e);
		        } finally {
		            if(fis!=null){
		            	try {
							fis.close();
							fis = null;
						} catch (IOException e) {
							e.printStackTrace();
						}
		            }
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
            throw new RuntimeException("FTP客户端出错！", e);
		}finally{
			try {
            	if(ftpClient.isConnected()){
            		ftpClient.logout();
	                ftpClient.disconnect();
            	}
            	ftpClient = null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
		}
	}
/**
 * 
 * @param ip
 * @param port
 * @param username
 * @param password
 * @param remotePath
 * @param fileNameRegexp 文件名，正则表达式
 * @param localPath
 * @return
 * @throws IOException
 */
	public static boolean downFileByRegexp(String ip, int port,String username, String password, String remotePath,String fileNameRegexp,String localPath) throws IOException {  
	    boolean success = false;  
	    log.info("ip="+ip+",port="+port+",username="+username+",psw="+password);
	    FTPClient ftp = new FTPClient();  
	    try {  
	        int reply;  
	        ftp.connect(ip, port);
	    log.info("--------------------ftp.connect ok--------");    
	        //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器  
	        ftp.login(username, password);//登录
	        ftp.enterLocalPassiveMode();
	    log.info("------------------- ftp.login ok--------");       
	        ftp.setControlEncoding("GB2312");
	        ftp.setFileType(FTPClient.ASCII_FILE_TYPE);  
	        ftp.setDefaultTimeout(1000*300);
	        ftp.setDataTimeout(1000*300);
	        ftp.setBufferSize(1024 * 1024 * 10);
	        reply = ftp.getReplyCode();
	   log.info("---------------------reply----"+reply);          
	        if (!FTPReply.isPositiveCompletion(reply)) {
	       log.info("-------------if-FTPReply.isPositiveCompletion(reply)=false-----beofore-disconnect()----"); 
	            ftp.disconnect();  
	            return success;  
	        } 
	        if (remotePath!=null)
	        {
	        ftp.changeWorkingDirectory(remotePath);
	        }
	        File localDir = new File(localPath);
	        if(!localDir.exists()){
	        	localDir.mkdirs();
	        }
	        FTPFile [] fs= ftp.listFiles();
	        Perl5Util util=new Perl5Util();
	        for(FTPFile ff:fs){
	            if(fileNameRegexp==null || util.match(fileNameRegexp, ff.getName())){
	                OutputStream is = null;   
	                try {
	                	File localFile = new File(localDir,  ff.getName()); 
						is = new FileOutputStream(localFile);
						ftp.retrieveFile(ff.getName(), is);
						is.flush();
					} catch (Exception e) {
						log.info("文件接受异常 ");
					}finally{
						if(is!=null){
							is.close();
						}
					}
	                log.info(ff.getName()+" 文件下载成功");
	            }
	        }
	         
		    ftp.logout();
	    } catch (IOException e) {  
	    	e.printStackTrace();
	    	log.error(e);
	        throw e;
	    }finally {  
	        if (ftp.isConnected()) {  
	            try {  
	            	log.info("-----------------------before:disconnect----------------------------"); 
	                ftp.disconnect();  
	            } catch (IOException ioe) {  
	            	
	            }  
	        }  
	    }  
	    return success;  
	}
}
