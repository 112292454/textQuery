package com.example.demo.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IpUtil {
    public int[][] intervalIntersection(int[][] firstList, int[][] secondList) {
        int up=0,dp=0;
        //最大的左端，最小的右端，构成一个交集
        HashMap<Integer,Integer> hash=new HashMap();
        while(up<firstList.length&&dp<secondList.length){
            int l=Math.max(firstList[up][0],secondList[dp][0]);
            int r=Math.min(firstList[up][1],secondList[dp][1]);
            if(r>=l) hash.put(l,r);
            if(firstList[up][1]<secondList[dp][1]) up++;
            else dp++;
        }
        int[][] res=new int[hash.size()][2];
        final int[] i = {0};
        hash.forEach((k,v)->res[i[0]++]=new int[]{k,v});
        Arrays.sort(res);
        return res;
    }
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()=15————但“8.8.8.8”呢？
                if (ipAddress.contains(",")) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
        }
        // ipAddress = this.getRequest().getRemoteAddr();

        return ipAddress;
    }
}