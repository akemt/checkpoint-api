package com.huawei.checkpoint.utils;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class StructUtil {
	
	
		public static class IVS_IP extends Structure{ 
			public int uiIPType;
			public byte[] cIP=new byte[64];
			public static class ByValue     extends IVS_IP implements Structure.ByValue { }  
		    public static class ByReference extends IVS_IP implements Structure.ByReference { }
			@Override
			protected List<String> getFieldOrder() {
				// TODO Auto-generated method stub
				return Arrays.asList(new String[] {"uiIPType", "cIP"});
			}
			
		}
		public static class IVS_LOGIN_INFO extends Structure{  
		    
		    public byte[] cUserName=new byte[128];  
		    public byte[] pPWD=new byte[64]; 
		    public IVS_IP.ByValue stIP = new IVS_IP.ByValue();
		    public int uiPort;
		    public int uiLoginType;
		    public byte[] cDomainName=new byte[64]; 
		    public byte[] cMachineName=new byte[128]; 
		    public int uiClientType;
		    public byte[] cReserve = new byte[32];
		   
		    public static class ByValue     extends IVS_LOGIN_INFO implements Structure.ByValue { }  
		    public static class ByReference extends IVS_LOGIN_INFO implements Structure.ByReference { }
			@Override
			protected List<String> getFieldOrder() {
				// TODO Auto-generated method stub
				return Arrays.asList(new String[] {"cUserName", "pPWD","stIP", "uiPort","uiLoginType","cDomainName"
						,"cMachineName", "uiClientType", "cReserve"});
			}  
		    
		
		}  
		/**
		 * 图片平台存储信息
		 * 
		 * @author root
		 *
		 */
		public static class IVS_IMG_STORE_INFO extends Structure{ 
			
			public int ulPicBufLen;
			public int ulPicLen;
			public long  ullPictureID=0;
			public long  ullSnapTime;
			public byte[] cNVRCode=new byte[32+1];
			//public byte [] pPictureBuf= new byte[1024*1024*2];
			
			public Pointer pPictureBuf=Pointer.NULL;
			
			public static class ByValue     extends IVS_IMG_STORE_INFO implements Structure.ByValue {
				public ByValue(){
					super();
					setAlignType( ALIGN_NONE);
				}
			}  
			public static class ByReference extends IVS_IMG_STORE_INFO implements Structure.ByReference { 
				public ByReference(){
					super();
					setAlignType( ALIGN_NONE);
				}
			}
			
			@Override
			protected List<String> getFieldOrder() {
				// TODO Auto-generated method stub
				return Arrays.asList(new String[] {"ulPicBufLen", "ulPicLen","ullPictureID", "ullSnapTime","cNVRCode","pPictureBuf"});
			}  
			public byte[] getData() { 
	            Pointer p = pPictureBuf.getPointer(0);
	            if (p == null) return null;
	            return p.getByteArray(0, ulPicLen);  
	        }
	        public void setData(byte[] data) {
	        	
	        	Pointer p=Pointer.NULL;
	            if (pPictureBuf == Pointer.NULL) {
	            	  pPictureBuf = new Memory(1024*1024*2); 
	            	  p = pPictureBuf.getPointer(0);
	                  pPictureBuf.setPointer(0, p);
	            }
	            pPictureBuf.write(0, data, 0, data.length);
	           
	            write();
	        }
		
		}
		/**
		 * 单张图片信息
		 * 
		 * @author root
		 *
		 */
		public static class IVS_IMG_STORE_NVR_INFO extends Structure{ 
			
			public byte [] cDomainCode = new byte[32+1];//域编码
			public byte [] cClusterCode = new byte[32+1];//集群编码
			public byte[] cNVRCode=new byte[32+1];//NVR编码
			
			public static class ByValue     extends IVS_IMG_STORE_NVR_INFO implements Structure.ByValue { }  
			public static class ByReference extends IVS_IMG_STORE_NVR_INFO implements Structure.ByReference { }
			
			@Override
			protected List<String> getFieldOrder() {
				// TODO Auto-generated method stub
				return Arrays.asList(new String[] {"cDomainCode", "cClusterCode","cNVRCode"});
			}  
		
		}
		public static class IVS_PTZ_PRESET extends Structure{ 
			public static final int IVS_MAX_PRESET_NUM=128;                 
			public static final int IVS_PRESET_NAME_LEN=84;                 
		 
		    public int  	  uiPresetIndex;                      
		    public byte []    cPresetName = new byte[IVS_PRESET_NAME_LEN];   
		    public byte []    cReserve= new byte[32];       
		    
		    public static class ByValue     extends IVS_PTZ_PRESET implements Structure.ByValue { }  
			public static class ByReference extends IVS_PTZ_PRESET implements Structure.ByReference { }
			
		    @Override
			protected List<String> getFieldOrder() {
				// TODO Auto-generated method stub
				return Arrays.asList(new String[] {"uiPresetIndex", "cPresetName","cReserve"});
			}  
		 
		}
		public static class IVS_PTZ_CONTROL_INFO extends Structure{ 
			public static final int IVS_NAME_LEN=128; 
			public int  uiLockStatus;              
			public int  uiUserID;                    
			public byte  []  cUserName = new byte[IVS_NAME_LEN];    
			public IVS_IP.ByValue   stIP;                        
			public int  uiReleaseTimeRemain;        
			public byte []   cReserve = new byte[32]; 
			public static class ByValue     extends IVS_PTZ_CONTROL_INFO implements Structure.ByValue { }  
			public static class ByReference extends IVS_PTZ_CONTROL_INFO implements Structure.ByReference { }
			 @Override
				protected List<String> getFieldOrder() {
					// TODO Auto-generated method stub
					return Arrays.asList(new String[] {"uiLockStatus", "uiUserID","cUserName","stIP","uiReleaseTimeRemain","cReserve"});
				}  
		 
		}
		public static class IVS_INDEX_RANGE extends Structure{ 
	
		
		    public int uiFromIndex;   
		    public int uiToIndex;    
		    
		    public static class ByValue     extends IVS_INDEX_RANGE implements Structure.ByValue { }  
			public static class ByReference extends IVS_INDEX_RANGE implements Structure.ByReference { }
		    @Override
			protected List<String> getFieldOrder() {
				// TODO Auto-generated method stub
				return Arrays.asList(new String[] {"uiFromIndex", "uiToIndex"});
			}  
		}
		public static class IVS_ORDER_COND extends Structure{ 
		
		     public int    bEnableOrder;  
		     public int  	eFieID;         
		     public int    bUp;     
		     public static class ByValue     extends IVS_ORDER_COND implements Structure.ByValue { }  
			 public static class ByReference extends IVS_ORDER_COND implements Structure.ByReference { }
		     @Override
				protected List<String> getFieldOrder() {
					// TODO Auto-generated method stub
					return Arrays.asList(new String[] {"bEnableOrder", "eFieID","bUp"});
				}  
		 }
		public static class IVS_QUERY_FIELD extends Structure{ 
			public static final int  IVS_QUERY_VALUE_LEN =2048;                
		
		     public int  eFieID;                         
		     public byte   [] cValue = new byte[IVS_QUERY_VALUE_LEN];    
		     public int    bExactQuery; 
		     
		     public static class ByValue     extends IVS_QUERY_FIELD implements Structure.ByValue { }  
			 public static class ByReference extends IVS_QUERY_FIELD implements Structure.ByReference { }
			 
		     @Override
				protected List<String> getFieldOrder() {
					// TODO Auto-generated method stub
					return Arrays.asList(new String[] {"eFieID", "cValue","bExactQuery"});
				}  
		 }
		public static class IVS_QUERY_UNIFIED_FORMAT extends Structure{
		
		     public IVS_INDEX_RANGE.ByValue      stIndex;            
		     public IVS_ORDER_COND.ByValue       stOrderCond;         
		     public int            iFieldNum;           
		     public IVS_QUERY_FIELD.ByValue     [] stQueryField; 
		     public static class ByValue     extends IVS_QUERY_UNIFIED_FORMAT implements Structure.ByValue {
		    	 public ByValue(int size){
		    		 super(size);
		    	 }
		     }  
			 public static class ByReference extends IVS_QUERY_UNIFIED_FORMAT implements Structure.ByReference { 
				 public ByReference(int size){
		    		 super(size);
		    	 }
			 }
			 
			 public IVS_QUERY_UNIFIED_FORMAT(int size){
				 stQueryField = new IVS_QUERY_FIELD.ByValue[size];
			 }
			 
		     @Override
				protected List<String> getFieldOrder() {
					// TODO Auto-generated method stub
					return Arrays.asList(new String[] {"stIndex", "stOrderCond","iFieldNum", "stQueryField"});
				}  
		 }
		public static class IVS_DEVICE_BASIC_INFO extends Structure{
			public static final int IVS_MAX_VENDOR_TYPE_NAME_LEN =   32   ;                          
			public static final int IVS_DEVICE_NAME_LEN  =            128;                             
			
			
			public static final int IVS_DEV_SERIALNO_LEN =   64   ;                          
			public static final int IVS_DEV_CODE_LEN  =            64;  
			public static final int IVS_DOMAIN_CODE_LEN =   32   ;                          
			public static final int IVS_DEV_MODEL_LEN  =            32;  
			
			
		    public int  uiType;  
		     public byte  []  cSerialNumber = new byte[IVS_DEV_SERIALNO_LEN];          
		     public byte  []    cCode = new byte[IVS_DEV_CODE_LEN];  
		     public byte  []    cName = new byte[IVS_DEVICE_NAME_LEN]; 
		     public byte  []    cDomainCode = new byte[IVS_DOMAIN_CODE_LEN];  
		     public byte  []    cVendorType = new byte[IVS_MAX_VENDOR_TYPE_NAME_LEN];              
		     public byte  []    cModel = new byte[IVS_DEV_MODEL_LEN];      
		     public IVS_IP.ByValue      stIP;    
		     public int  uiPort;    
		     public byte  []    cReserve = new byte[32]; 
		     public static class ByValue     extends IVS_DEVICE_BASIC_INFO implements Structure.ByValue { }  
			 public static class ByReference extends IVS_DEVICE_BASIC_INFO implements Structure.ByReference { }
			 
		     @Override
				protected List<String> getFieldOrder() {
					// TODO Auto-generated method stub
					return Arrays.asList(new String[] {"uiType", "cSerialNumber","cCode", "cName",
							"cDomainCode","cVendorType","cModel","stIP","uiPort", "cReserve"});
				}  
		 }
		public static class IVS_DEVICE_BRIEF_INFO extends Structure{
			public static final int  IVS_MAX_PROTOCOLTYPE_NAME_LEN = 64; 
			
		    public IVS_DEVICE_BASIC_INFO.ByValue   stDeviceInfo;    
		    public byte []   cProtocolType = new byte[IVS_MAX_PROTOCOLTYPE_NAME_LEN];
		    public int       iLoginType;               
		    public int       bEnableSchedule;           
		    public int       uiStatus;              
		    public byte []   cReserve = new byte[32];   
		    public static class ByValue     extends IVS_DEVICE_BRIEF_INFO implements Structure.ByValue { }  
			 public static class ByReference extends IVS_DEVICE_BRIEF_INFO implements Structure.ByReference { }
			 
		    @Override
			protected List<String> getFieldOrder() {
				// TODO Auto-generated method stub
				return Arrays.asList(new String[] {"stDeviceInfo", "cProtocolType","iLoginType", "bEnableSchedule",
						"uiStatus", "cReserve"});
			}  
		 }
		public static class IVS_DEVICE_BRIEF_INFO_LIST extends Structure{
		
		     int              					uiTotal;                 
		     IVS_INDEX_RANGE.ByValue         	stIndexRange;             
		     byte []                			cRes = new byte[32];                 
		     IVS_DEVICE_BRIEF_INFO.ByValue  [] 	stDeviceBriefInfo;    
		     
		     public static class ByValue     extends IVS_DEVICE_BRIEF_INFO_LIST implements Structure.ByValue { 
		    	 public ByValue(int size){
		    		 super(size);
		    	 }
		     }  
			 public static class ByReference extends IVS_DEVICE_BRIEF_INFO_LIST implements Structure.ByReference {
				 public ByReference(int size){
		    		 super(size);
		    	 }
			 }
			 public IVS_DEVICE_BRIEF_INFO_LIST(int size){
				 stDeviceBriefInfo = new IVS_DEVICE_BRIEF_INFO.ByValue[size];
			 }
		     @Override
				protected List<String> getFieldOrder() {
					// TODO Auto-generated method stub
					return Arrays.asList(new String[] {"uiTotal", "stIndexRange","cRes", "stDeviceBriefInfo"});
				}  
		 }
		
		public static class IVS_CAMERA_BRIEF_INFO extends Structure{
			public static final int  IVS_DEV_CODE_LEN = 64; 
			public static final int  IVS_CAMERA_NAME_LEN = 192; 
			public static final int  IVS_DEVICE_GROUP_LEN = 128;
			public static final int IVS_DOMAIN_CODE_LEN =   32   ;  
			public static final int IVS_DEV_MODEL_LEN  =            32; 
			public static final int IVS_MAX_VENDOR_TYPE_NAME_LEN =   32   ;
			public static final int IVS_DESCRIBE_LEN =   256   ;
			public static final int IVS_NVR_CODE_LEN =   32   ;
			public static final int IVS_TIME_LEN =   20   ;
			public static final int IVS_IP_LEN =   64  ;
			
			
			public byte []     cCode = new byte[IVS_DEV_CODE_LEN];  
			public byte []    cName = new byte[IVS_CAMERA_NAME_LEN];  //#define IVS_CAMERA_NAME_LEN     192  
			public byte []    cDevGroupCode = new byte[IVS_DEVICE_GROUP_LEN]; //128
			public byte []    cParentCode = new byte[IVS_DEV_CODE_LEN];   
			public byte []    cDomainCode = new byte[IVS_DOMAIN_CODE_LEN];   
			public byte []    cDevModelType = new byte[IVS_DEV_MODEL_LEN];   
			public byte []    cVendorType = new byte[IVS_MAX_VENDOR_TYPE_NAME_LEN];   
			public int  uiDevFormType;     
			public int uiType;    
			public byte []    cCameraLocation = new byte[IVS_DESCRIBE_LEN];  //256
		    public int  uiCameraStatus;     
		    public int  uiStatus;     
		    public int  uiNetType;   
		    public int    bSupportIntelligent; 
		    public int    bEnableVoice;  
		    public byte []    cNvrCode = new byte[IVS_NVR_CODE_LEN];  //32
		    public byte []    cDevCreateTime = new byte[IVS_TIME_LEN];  //20
		    public int    bIsExDomain;  
		    public byte []    cDevIp = new byte[IVS_IP_LEN];  //64
		    public byte []    cReserve = new byte[32];    
		    public static class ByValue     extends IVS_CAMERA_BRIEF_INFO implements Structure.ByValue {
		     }  
			 public static class ByReference extends IVS_CAMERA_BRIEF_INFO implements Structure.ByReference {
			 }
		    
		    @Override
			protected List<String> getFieldOrder() {
				// TODO Auto-generated method stub
				return Arrays.asList(new String[] {"cCode", "cName","cDevGroupCode", "cParentCode",
						"cDomainCode", "cDevModelType", "cVendorType", "uiDevFormType", "uiType"
						, "cCameraLocation", "uiCameraStatus", "uiStatus", "uiNetType", "bSupportIntelligent"
						, "bEnableVoice", "cNvrCode", "cDevCreateTime", "bIsExDomain", "cDevIp"
						, "cReserve"});
			}  
		    
		 }
		public static class IVS_CAMERA_BRIEF_INFO_LIST extends Structure{
	
		     public int              uiTotal;                
		     public IVS_INDEX_RANGE.ByValue         stIndexRange;          
		     public IVS_CAMERA_BRIEF_INFO.ByValue   []stCameraBriefInfo;
		     public static class ByValue     extends IVS_CAMERA_BRIEF_INFO_LIST implements Structure.ByValue { 
		    	 public ByValue(int size){
		    		 super(size);
		    	 }
		     }  
			 public static class ByReference extends IVS_CAMERA_BRIEF_INFO_LIST implements Structure.ByReference {
				 public ByReference(int size){
		    		 super(size);
		    	 }
			 }
			 public IVS_CAMERA_BRIEF_INFO_LIST(int size){
				 stCameraBriefInfo = new IVS_CAMERA_BRIEF_INFO.ByValue[size];
			 }
			 
		     @Override
				protected List<String> getFieldOrder() {
					// TODO Auto-generated method stub
					return Arrays.asList(new String[] {"uiTotal", "stIndexRange","stCameraBriefInfo"});
				}  
		 }

		


	
}