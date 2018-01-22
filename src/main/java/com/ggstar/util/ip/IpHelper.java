package com.ggstar.util.ip;

import com.ggstar.util.file.FileUtil;
import com.ggstar.util.file.PoiUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Wang Zhe on 2015/8/11.
 */
public class IpHelper {

    private static IpTree ipTree = IpTree.getInstance();

    private static final String ipFile = "ipDatabase.csv";

    private static final String regionFile = "ipRegion.xlsx";

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
            int count = 0;
            for (IpRelation ipRelation : ipRelationList) {
//                ipTree.train(ipRelation.getIpStart(), ipRelation.getIpEnd(), ipRelation.getProvince());
                buildRegionIpMap(ipRelationList);
                if(count > 10){
                    break;
                }
            }
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
        return preparedList.toString();
    }
    public static List<IpRelation> getIpRelation() throws Exception {

        Map<Integer, IpRegion> regionRelationMap = getRegionMap();
        String file =  IpHelper.class.getClassLoader().getResource(ipFile).getFile();
        BufferedReader ipRelationReader = FileUtil.readFile(file);

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
        String file =  IpHelper.class.getClassLoader().getResource(regionFile).getFile();

        Workbook workbook = PoiUtil.getWorkbook(file);

        Sheet sheet = workbook.getSheetAt(0);
        Map<Integer, IpRegion> map = new HashMap<Integer, IpRegion>();
        int rowLen = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < rowLen; i++) {
            Row row = sheet.getRow(i);
            String province = row.getCell(0).getStringCellValue();
            String city = row.getCell(1).getStringCellValue();
            Double a = row.getCell(2).getNumericCellValue();
            Integer ipCode = a.intValue();
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
