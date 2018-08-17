package com.huawei.checkpoint;

/**
 * 版本控制
 * 
 * @author Xialf
 *
 */
public class VersionControl {
	
	/** 卡口分类：洛阳   -   0:关闭、1：启动*/
	public static final int LUOYANG_KAKOU =0x00000001; 
	/** 卡口分类：宇视过车   -   0:关闭、1：启动*/
	public static final int YUSHI_CAMERA = 0x00000002;
	
	public static final int HENAN_SHENG = 0x00000004;
	
	    
	public static final int KAKOU_LIST = 0x00000008;
	//for dadong version
	public static final int VERSION_RELEASE = YUSHI_CAMERA;
	
	//for luoyang version 
	//public static final int VERSION_RELEASE = LUOYANG_KAKOU | HENAN_SHENG;
	//public static final int VERSION_RELEASE = LUOYANG_KAKOU | HENAN_SHENG | KAKOU_LIST;
	//public static final int VERSION_RELEASE = LUOYANG_KAKOU | HENAN_SHENG | KAKOU_LIST;
	
	
	 
}
