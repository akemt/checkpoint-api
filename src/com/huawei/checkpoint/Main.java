package com.huawei.checkpoint;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

public class Main {
	
	private static Logger log = Logger.getLogger(Main.class);
	
	public static void main( String[] args )
    {

		//NativeLibrary.addSearchPath("", "/root/workspace/checkpoint/bin/linux-x86-64");
		//long tmp = 100;
//		int tmp = Long.SIZE;
		log.info("start checkpoinst");
		SystemManager sm = SystemManager.getIns();
        /* 注册KILL信号 */
        sm.install(sm,"TERM");
        
        sm.init();
    }

}
