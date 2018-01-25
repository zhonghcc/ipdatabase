package com.ggstar.util.ip;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.*;

/**
 * Created by Wang Zhe on 2015/8/11.
 */
public class IpHelper {

    private static IpTree ipTree = IpTree.getInstance();

    private static final String ipFile = "ipDatabase.csv";

    private static final Map<String,List<IpRelation>> regionIpCache = new HashMap<String, List<IpRelation>>();


    static{
        buildTrain();
        System.out.println("buildTrain Finished");
    }

    private static void buildTrain() {
        System.out.println("buildTrain");
        List<IpRelation> ipRelationList;
        try {
            ipRelationList = IpHelper.getIpRelation();
            buildRegionIpMap(ipRelationList);
//            int count = 0;
//            for (IpRelation ipRelation : ipRelationList) {
////                ipTree.train(ipRelation.getIpStart(), ipRelation.getIpEnd(), ipRelation.getProvince());
//                if(count > 10){
//                    break;
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 静态方法，传入ip地址，返回ip地址所在城市或地区
     * @param ip    IP地址，例：58.30.15.255
     * @return  返回IP地址所在城市或地区，例：北京市
     */
    public static String findRegionByIp(String ip){
        return ipTree.findIp(ip);
    }


    public static String getRadomIpByCity(String city){
        String key = "北京市";
        for(String dic: regionIpCache.keySet()){
            if(dic.startsWith(city)){
                key = dic;
                break;
            }
        }
        List<IpRelation> preparedList = regionIpCache.get(key);
        Random rdm = new Random();
        int index = rdm.nextInt(preparedList.size());
        IpRelation selected = preparedList.get(index);
        String [] startIps = selected.getIpStart().split("\\.");
        String [] endIps = selected.getIpEnd().split("\\.");
        String ip0 = startIps[0];
        String ip1 = startIps[1];
        String ip2 = startIps[2];
        String ip3 = startIps[3];
        int minius = Integer.parseInt(endIps[2])-Integer.parseInt(startIps[2]);
        if(minius>0) {
            ip2 = String.valueOf(Integer.parseInt(ip2) + rdm.nextInt(minius));
        }
        ip3 = String.valueOf(2+ rdm.nextInt(250));
        return ip0 + "." + ip1 + "." + ip2 + "." + ip3;
    }
    public static List<IpRelation> getIpRelation() throws Exception {

        Map<Integer, IpRegion> regionRelationMap = getRegionMap();
        String file =  IpHelper.class.getClassLoader().getResource(ipFile).getFile();
        BufferedReader ipRelationReader = new BufferedReader(new FileReader(new File(file)));

        String line;
        List<IpRelation> list = new ArrayList<IpRelation>();
        while((line = ipRelationReader.readLine()) != null){
            String[] split = line.split(",");
            String ipStart = split[0];
            String ipEnd = split[1];
            Integer ipCode = Integer.valueOf(split[2]);

            IpRegion ipRegion = regionRelationMap.get(ipCode);
            IpRelation ipRelation = new IpRelation();
            ipRelation.setIpStart(ipStart);
            ipRelation.setIpEnd(ipEnd);
            ipRelation.setIpCode(ipCode);
            ipRelation.setProvince(ipRegion.getProvince());
            ipRelation.setCity(ipRegion.getCity());
            list.add(ipRelation);
        }
        return list;

    }

    /**
     * @return Map<ipCode, IpRegion>
     * @throws Exception
     */
    public static Map<Integer, IpRegion> getRegionMap() throws Exception {
        Map<Integer, IpRegion> map = new HashMap<Integer, IpRegion>();
        String line;
        BufferedReader reader = new BufferedReader(new StringReader(IpRegionFile.content));
        while((line = reader.readLine()) != null){
            String []row = line.split(",");
            String province = row[0];
            String city = row[1];
            Integer ipCode = Integer.parseInt(row[2]);
            IpRegion ipRegion = new IpRegion();
            ipRegion.setCity(city);
            ipRegion.setProvince(province);
            ipRegion.setCode(ipCode);
            map.put(ipCode, ipRegion);
        }

        return map;
    }

    private static void buildRegionIpMap(List<IpRelation> ipRelationList) {
        for(IpRelation ipRelation: ipRelationList){
            String key = ipRelation.getCity();
            if(regionIpCache.containsKey(key)){
                List<IpRelation> list = regionIpCache.get(key);
                list.add(ipRelation);
            }else{
                List<IpRelation> list = new ArrayList<IpRelation>();
                list.add(ipRelation);
                regionIpCache.put(key,list);
            }
        }
    }

}
