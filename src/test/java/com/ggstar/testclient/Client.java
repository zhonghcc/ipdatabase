package com.ggstar.testclient;

import com.ggstar.util.ip.IpHelper;
import org.junit.Test;

/**
 * Created by Wang Zhe on 2015/8/11.
 */
public class Client {

    @Test
    public void example() throws Exception {
        String ip = "59.48.32.0";
        String region = IpHelper.findRegionByIp(ip);
        System.out.println(region);
    }

    @Test
    public void getIpFromRegion() throws Exception {
        String city = "杭州";
        String result = IpHelper.getRadomIpByCity(city);
        System.out.println(result);
        for (int i=0;i<20;i++){
            result = IpHelper.getRadomIpByCity(city);
            System.out.println(result);
        }
    }
}
