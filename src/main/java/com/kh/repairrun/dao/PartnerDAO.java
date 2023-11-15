package com.kh.repairrun.dao;

import com.kh.repairrun.common.Common;
import com.kh.repairrun.vo.PartnerVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartnerDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;

    // 로그인 시 회원 여부 체크
    public boolean partnerLoginCheck(String partnerId, String partnerPw) {
        boolean isPartner = false;
        try {
            conn = Common.getConnection();
            String sql = "SELECT COUNT(*) FROM PARTNER_TB WHERE PTN_ID_PK = ? AND PTN_PW = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,partnerId);
            pstmt.setString(2,partnerPw);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                if(rs.getInt("Count(*)") == 1) {
                    isPartner = true;
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(pstmt);
        Common.close(conn);
        return isPartner;
    }
    // 파트너 정보 조회
    public Map<String, String> partnerInfo(String ptnId) {
        Map<String, String> ptnInfo = new HashMap<>();
        String sql = "SELECT PTN_NAME, PTN_LOGO, PTN_EMAIL, PTN_PHONE, PTN_ADDR, PTN_DESC FROM PARTNER_TB WHERE PTN_ID_PK = ?";
        try{
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ptnId);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                String ptnName = rs.getString("PTN_NAME");
                String ptnLogo = rs.getString("PTN_LOGO");
                String ptnEmail = rs.getString("PTN_EMAIL");
                String ptnPhone = rs.getString("PTN_PHONE");
                String ptnAddr = rs.getString("PTN_ADDR");
                String ptnDesc = rs.getString("PTN_DESC");

                ptnInfo.put("userName", ptnName);
                ptnInfo.put("userImg", ptnLogo);
                ptnInfo.put("userEmail", ptnEmail);
                ptnInfo.put("userPhone", ptnPhone);
                ptnInfo.put("userAddr", ptnAddr);
                ptnInfo.put("ptnDesc", ptnDesc);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(pstmt);
        Common.close(conn);
        return ptnInfo;
    }


    // 파트너 로고 리스트화
    public List<String> partnerLogos()   {
        List<String> partnerLogos = new ArrayList<>();
        try {
            conn = Common.getConnection();
            String sql = "SELECT PTN_LOGO FROM PARTNER_TB";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                partnerLogos.add(rs.getString("PTN_LOGO"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(pstmt);
        Common.close(conn);
        return partnerLogos;
    }

    // 파트너의 수선 정보를 불러옴 (이중 맵)
    public List<Map<String,Object>> partnerItemList (String ptnId) {
        List<Map<String,Object>> list = new ArrayList<>();
        try{
            conn = Common.getConnection();
            String sql = "SELECT REPAIR_ITEM AS ITEM, REPAIR_DETAIL_FK AS DETAIL, REPAIR_DAYS AS DAYS, REPAIR_PRICE AS PRICE " +
                    "FROM PARTNER_ITEM_TB " +
                    "WHERE PTN_ID_FK = ? " +
                    "ORDER BY REPAIR_ITEM, REPAIR_DETAIL_FK";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,ptnId);
            rs = pstmt.executeQuery();

            // 품목 : [상세항목 맵 리스트]
            Map<String,List<Map<String, Object>>> itemMap = new HashMap<>();

            while(rs.next()) {
                String item = rs.getString("ITEM");
                String detailName = rs.getString("DETAIL");
                int days = rs.getInt("DAYS");
                int price = rs.getInt("PRICE");

                // 상세항목 맵
                Map<String, Object> detailMap = new HashMap<>();
                detailMap.put("name",detailName);
                detailMap.put("days",days);
                detailMap.put("price",price);

                // item을 키값으로 가지는 itemMap 여부 확인
                List<Map<String,Object>> detailMapList = itemMap.get(item);
                if(detailMapList == null) {
                    // item을 키값을 가지는 itemMap이 없으면 새 리스트 객체 생성
                    detailMapList = new ArrayList<>();
                    // itemMap에 키(item) : 값(detailMapList)
                    itemMap.put(item, detailMapList);
                    System.out.println("itemMap :" + itemMap);

                }
                // 리스트에 상세항목 map 추가
                detailMapList.add(detailMap);
                System.out.println("detailMapLsit :" + detailMapList);
            }
            System.out.println("itemMap ;" + itemMap);

            // 키(품목명1) : [상세항목Map1, 2, 3 ..], 키(품목명2) :[상세항목Map1, 2, 3...] 이므로
            // 키(품목명)을 기준으로 향상된 포문을 이용해서 리스트에  품목: 품목명 , 상세항목: [상세항목map1, 2, 3...]
            // 으로 만들어 list에 담기
            for (String item: itemMap.keySet()) {
                Map<String, Object> itemMapEntry = new HashMap<>();
                itemMapEntry.put("item",item);
                itemMapEntry.put("detail", itemMap.get(item));
                list.add(itemMapEntry);
                System.out.println("list:" +list);
            }


        }catch(Exception e) {
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(pstmt);
        Common.close(conn);
        return list;
    }

    // 중복체크
    public boolean checkUnique(int type, String inputVal) {
        boolean isUnique = false;
        String[] sqlList = {
                "SELECT COUNT(*) FROM PARTNER_TB WHERE PTN_ID_PK = ?",
                "SELECT COUNT(*) FROM PARTNER_TB WHERE PTN_PHONE = ?",
                "SELECT COUNT(*) FROM PARTNER_TB WHERE PTN_EMAIL = ?"
        };
        try {
            conn = Common.getConnection();
            String sql = sqlList[type];
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,inputVal);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                System.out.println("중복 : " + rs.getInt("Count(*)"));
                if (rs.getInt("Count(*)") == 1) {
                    isUnique = true; // 이미 존재하는 값
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(pstmt);
        Common.close(conn);
        return isUnique;
    }

    //회원가입
    public boolean newPartnerInsert(String id, String pw, String name, String ptnEmail, String ptnPhone, String ptnAddr, String ptnDesc ,String ptnLogo) {
        int result = 0;
        String sql = "INSERT INTO PARTNER_TB (PTN_ID_PK, PTN_PW, PTN_NAME, PTN_EMAIL, PTN_PHONE, PTN_ADDR, PTN_DESC, PTN_LOGO, PTN_UNIQUE_NUM) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, PARTNER_NUM_SEQ.NEXTVAL)";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,id);
            pstmt.setString(2, pw);
            pstmt.setString(3, name);
            pstmt.setString(4, ptnEmail);
            pstmt.setString(5, ptnPhone);
            pstmt.setString(6, ptnAddr);
            pstmt.setString(7, ptnDesc);
            pstmt.setString(8, ptnLogo);
            result = pstmt.executeUpdate();
            System.out.println("가입 결과 : " + result);

        }catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(pstmt);
        Common.close(conn);

        if(result == 1) return true;
        else return false;
    }

    // 파트너 정보 수정
    public boolean partnerUpdate(String ptnId, String ptnPw, String ptnEmail, String ptnPhone, String ptnAddr, String ptnImg, String ptnDesc) {
        int result = 0;
        String sql = "UPDATE PARTNER_TB SET PTN_PW = ?, PTN_EMAIL = ?, PTN_PHONE = ?, PTN_ADDR = ?, PTN_LOGO = ?, PTN_DESC = ? WHERE PTN_ID_PK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ptnPw);
            pstmt.setString(2, ptnEmail);
            pstmt.setString(3, ptnPhone);
            pstmt.setString(4, ptnAddr);
            pstmt.setString(5, ptnImg);
            pstmt.setString(6, ptnDesc);
            pstmt.setString(7, ptnId);
            result = pstmt.executeUpdate();
            System.out.println("회원수정 결과 : " + result);
        }catch(Exception e) {
            e.printStackTrace();
        }
        Common.close(pstmt);
        Common.close(conn);

        if(result == 1) return true;
        else return false;
    }



    // 수선항목 수정
    public boolean updateItemInfo (String ptnId, String repairDetail, int days, int price) {
        boolean isUpdate = false;
        String sql = "UPDATE PARTNER_ITEM_TB SET REPAIR_PRICE =?, REPAIR_DAYS =? WHERE PTN_ID_FK = ? AND REPAIR_DETAIL_FK = ?";
        try{
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, price);
            pstmt.setInt(2, days);
            pstmt.setString(3, ptnId);
            pstmt.setString(4, repairDetail);
            int result = pstmt.executeUpdate();
            if(result == 1) isUpdate = true;

        }catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(pstmt);
        Common.close(conn);
        return isUpdate;
    }

    public List<Map<String, Object>> ptnList() {
        List<Map<String, Object>> partnerList = new ArrayList<>();
        try {
            conn = Common.getConnection();
            String sql = "SELECT DISTINCT PT.PTN_NAME, PT.PTN_LOGO, PI.REPAIR_ITEM, FLOOR(AVG(RT.RATING)) AS AVG_RATING " +
                    "FROM PARTNER_TB PT " +
                    "JOIN PARTNER_ITEM_TB PI ON PT.PTN_ID_PK = PI.PTN_ID_FK " +
                    "JOIN ORDER_TB OT ON PT.PTN_ID_PK = OT.PTN_ID_FK " +
                    "JOIN REVIEW_TB RT ON OT.ORDER_NUM_PK = RT.ORDER_NUM_FK " +
                    "WHERE PI.REPAIR_PRICE <> 0 " +
                    "GROUP BY PT.PTN_NAME, PT.PTN_LOGO, PI.REPAIR_ITEM " +
                    "ORDER BY AVG_RATING DESC";  // 변경: PTN_UNIQUE_NUM이 없어졌으므로 ORDER BY 절 수정
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> ptnMap = new HashMap<>();
                String ptnName = rs.getString("PTN_NAME");
                String ptnLogo = rs.getString("PTN_LOGO");
                int avgRating = rs.getInt("AVG_RATING");
                String repairItem = rs.getString("REPAIR_ITEM");

                String rating = "";
                if(avgRating > 0) rating = "★".repeat(avgRating);
                else if (avgRating == 0) rating = "☆".repeat(5);

                ptnMap.put("ptnName", ptnName);
                ptnMap.put("ptnLogo", ptnLogo);
                ptnMap.put("rating", rating);


                ptnMap.put("repairItem", repairItem);

                partnerList.add(ptnMap);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        }
        System.out.println(partnerList);
        return partnerList;
    }
};
