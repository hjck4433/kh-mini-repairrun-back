package com.kh.repairrun.dao;

import com.kh.repairrun.common.Common;
import com.kh.repairrun.vo.OrderVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class OrderDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;


    // BasicRepair, Request.jsx 부분
    public List<String> orderSelect (String selItem) {//리액트에서 전달 될 값. 매개변수 자리에 원하는 이름 지정. 외부에서 값이 들어오는 경우는 매개변수라고 생각
        List<String> list = new ArrayList<>();
        try{
            conn = Common.getConnection();
            String sql = "SELECT DISTINCT REPAIR_DETAIL_FK FROM PARTNER_ITEM_TB WHERE REPAIR_ITEM = ?";


            pstmt = conn.prepareStatement(sql);// 보낼 쿼리문을 준비
            //?의 값을 지정
            pstmt.setString(1,selItem); // 1번 물음표에 selItem이 올 것임

            rs = pstmt.executeQuery();// 보냄. rs의 값 반환을 위한 것. set 형식으로 리스트 받겠다.


            while (rs.next()) { // 값이 있는 동안 한줄씩 반환
                String repairDetail = rs.getString("REPAIR_DETAIL_FK");
                list.add(repairDetail);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }




    //ddd
    public String itemInfo (String selDetail) {//리액트에서 전달 될 값. 매개변수 자리에 원하는 이름 지정. 외부에서 값이 들어오는 경우는 매개변수라고 생각
        String item = "";
        try{
            conn = Common.getConnection();
            String sql = "SELECT REPAIR_ITEM FROM REPAIR_ITEM_TB WHERE REPAIR_DETAIL_PK = ?";


            pstmt = conn.prepareStatement(sql);// 보낼 쿼리문을 준비
            //?의 값을 지정
            pstmt.setString(1,selDetail); // 1번 물음표에 selItem이 올 것임

            rs = pstmt.executeQuery();// 보냄. rs의 값 반환을 위한 것. set 형식으로 리스트 받겠다.


            if (rs.next()) { // 값이 있는 동안 한줄씩 반환
                String repairItem = rs.getString("REPAIR_ITEM");
                item = repairItem;
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

// PartnerSelect.jsx 부분
    public List<Map<String,Object>> partnerSelect (String selDetail) {
        List<Map<String,Object>> list = new ArrayList<>();
        try{
            conn = Common.getConnection();
            String sql = "SELECT " +
                    "PT.PTN_LOGO, " +
                    "NVL(AVG(RV.RATING), 0) AS RATING, " +
                    "PT.PTN_NAME, " +
                    "PI.REPAIR_DETAIL_FK, " +
                    "PI.REPAIR_PRICE, " +
                    "PT.PTN_ID_PK, " +
                    "PI.REPAIR_DAYS, PT.PTN_UNIQUE_NUM " +
                    "FROM " +
                    "PARTNER_ITEM_TB PI " +
                    "JOIN PARTNER_TB PT ON " +
                    "PI.PTN_ID_FK = PT.PTN_ID_PK " +
                    "LEFT JOIN ORDER_TB O ON " +
                    "PT.PTN_ID_PK = O.PTN_ID_FK " +
                    "LEFT JOIN REVIEW_TB RV ON " +
                    "O.ORDER_NUM_PK = RV.ORDER_NUM_FK " +
                    "WHERE " +
                    "PI.REPAIR_DETAIL_FK = ? AND PI.REPAIR_PRICE <> 0 " +
                    "GROUP BY " +
                    "PT.PTN_LOGO, PT.PTN_NAME, PI.REPAIR_DETAIL_FK, PI.REPAIR_PRICE, PT.PTN_ID_PK, PI.REPAIR_DAYS, PT.PTN_UNIQUE_NUM " +
                    "ORDER BY " +
                    "PI.REPAIR_PRICE ASC";
            pstmt = conn.prepareStatement(sql);// 받을 준비
            pstmt.setString(1, selDetail);
            rs = pstmt.executeQuery();

            while (rs.next()) { // 값이 있는 동안 한줄씩 반환
                String ptnId = rs.getString("PTN_ID_PK");
                int repairDays = rs.getInt("REPAIR_DAYS");
                String ptnLogo = rs.getString("PTN_LOGO");
                double rating = rs.getDouble("RATING");
                String ptnName = rs.getString("PTN_NAME");
                String repairDetail = rs.getString("REPAIR_DETAIL_FK");
                int repairPrice = rs.getInt("REPAIR_PRICE");
                int ptnNum = rs.getInt("PTN_UNIQUE_NUM");

                // 리액트에서 선언한 이름을 ""사이에
                Map<String, Object> ptnMap = new HashMap<>();
                ptnMap.put("ptnId",ptnId);
                ptnMap.put("repairDays",repairDays);
                ptnMap.put("ptnLogo",ptnLogo);
                ptnMap.put("rating",rating);
                ptnMap.put("ptnName",ptnName);
                ptnMap.put("repairItem",repairDetail);
                ptnMap.put("repairPrice",repairPrice);
                ptnMap.put("ptnNum", ptnNum);

                list.add(ptnMap);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);


        } catch (Exception e) {
            e.printStackTrace();
        }
    return list;
    }


// Payment.jsx 부분

    public List<Map<String,Object>> payment (String paySum) {
        List<Map<String,Object>> list = new ArrayList<>();
        try{
            conn = Common.getConnection();
            String sql = "SELECT " +
                    "CU.COUPON_TYPE_FK, " +
                    "CT.DISCOUNT_AMOUNT, " +
                    "CASE " +
                    "WHEN CT.MIN_PRICE IS NOT NULL THEN CT.MIN_PRICE " +
                    "ELSE 0 " +
                    "END AS MIN_PRICE " +
                    "FROM " +
                    "COUPON_USER_TB CU " +
                    "JOIN " +
                    "COUPON_TYPE_TB CT ON CU.COUPON_TYPE_FK = CT.COUPON_TYPE_PK " +
                    "WHERE " +
                    "CU.END_DATE > SYSDATE " +
                    "AND CU.USER_ID_FK = ?";

            pstmt = conn.prepareStatement(sql);// 보낼 쿼리문을 준비
            //?의 값을 지정
            pstmt.setString(1,paySum); //
            rs = pstmt.executeQuery();//
            while (rs.next()) { // 값이 있는 동안 한줄씩 반환
                String couponType = rs.getString("COUPON_TYPE_FK");
                String discountAmount = rs.getString("DISCOUNT_AMOUNT");
                String minPrice = rs.getString("MIN_PRICE");
                // 각 정보를 리스트에 추가
                Map<String,Object> couponMap = new HashMap<>();
                couponMap.put("coupon", couponType);
                couponMap.put("discount", discountAmount);
                couponMap.put("minprice", minPrice);
                list.add(couponMap);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 새 주문 정보 인서트
    public boolean newOrder(String orderNum, String userId, String ptnId,String brand, String repairDetail, String request, int priceTotal, int days, String imgFull, String imgDet01, String imgDet02, String imgDet03) {
        int result = 0;
        String sql = "INSERT INTO ORDER_TB(ORDER_NUM_PK,USER_ID_FK,PTN_ID_FK,BRAND,REPAIR_DETAIL_FK,ORDER_REQUEST,PRICE_TOTAL,ORDER_DATE,COMPLETE_DATE,ORDER_PRG,IMG_FULL,IMG_COMP,IMG_DETAIL_01,IMG_DETAIL_02,IMG_DETAIL_03) VALUES(TO_NUMBER(CONCAT(?,ORDER_NUM_SEQ.NEXTVAL)), ? , ? , ? , ? , ? , ? , SYSDATE, SYSDATE+?, '주문접수', ?,'',?,?,? )";
        try{
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,orderNum);
            pstmt.setString(2,userId);
            pstmt.setString(3,ptnId);
            pstmt.setString(4,brand);
            pstmt.setString(5,repairDetail);
            pstmt.setString(6,request);
            pstmt.setInt(7,priceTotal);
            pstmt.setInt(8, days);
            pstmt.setString(9, imgFull);
            pstmt.setString(10, imgDet01);
            pstmt.setString(11, imgDet02);
            pstmt.setString(12, imgDet03);
            result = pstmt.executeUpdate();
            System.out.println("회원 수정 결과 : " + result);
        }catch(Exception e) {
            e.printStackTrace();
        }
        Common.close(pstmt);
        Common.close(conn);
        if(result == 1) return true;
        else return false;
    }

}







