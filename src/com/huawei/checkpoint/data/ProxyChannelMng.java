package com.huawei.checkpoint.data;

import io.netty.channel.ChannelHandlerContext;

import java.util.Hashtable;

import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2016/3/6.
 */
public class ProxyChannelMng {
	
	private static Logger log = Logger.getLogger(ProxyChannelMng.class);
	
    private Hashtable<String, ChannelHandlerContext> chc = null;
    private Hashtable<String, ManagerUtils> manHT = null;
    private ProxyChannelMng(){
        chc = new Hashtable<String, ChannelHandlerContext>();
        manHT = new Hashtable<String, ManagerUtils>();
    }
    private static volatile  ProxyChannelMng spcm = null;
    public static ProxyChannelMng getInstance(){

        if(spcm == null){
            synchronized(ProxyChannelMng.class){
                if (spcm == null){
                    spcm = new ProxyChannelMng();
                }
            }
        }
        return spcm;
    }
    public void addProxyChannel(String ip,ChannelHandlerContext ctx){
        chc.put(ip, ctx);
    }
    public ChannelHandlerContext getProxyChannelByIP(String ip){
        return chc.get(ip);
    }
    public void rmProxyChannel(String ip) {
    	chc.remove(ip);
    	
    }
    
    public synchronized void rmProxyChannelManager(String ip) {
    	if(manHT.containsKey(ip)){
        	manHT.remove(ip);
        	log.debug("HashTable manHT removed:"+ip);
    	}
    	
    }
    /**
     * 把IP,Man放到HashTable内
     * @param ip
     * @param man
     */
    public synchronized void addProxyManager(String ip,ManagerUtils man){
    	manHT.put(ip, man);
    	log.debug("HashTable:"+manHT.size());
    }
    /**
     * 根据IP查找
     * 
     * @param ip
     * @return
     */
    public ManagerUtils getProxyManagerByIP(String ip){
        return manHT.get(ip);
    }
}
