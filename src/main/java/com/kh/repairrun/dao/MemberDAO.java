package com.kh.repairrun.dao;

import com.kh.repairrun.common.Common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;

    // 로그인 시 회원 여부 체크
    public boolean memberLoginCheck(String userId, String userPw) {
        boolean isMember = false;
        try {
            conn = Common.getConnection();
            String sql = "SELECT COUNT(*) FROM MEMBER_TB WHERE USER_ID_PK = ? AND USER_PW = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,userId);
            pstmt.setString(2,userPw);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                if(rs.getInt("Count(*)") == 1) {
                    isMember = true;
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(pstmt);
        Common.close(conn);
        return isMember;
    }

    // 중복체크
    public boolean checkUnique(int type, String inputVal) {
        boolean isUnique = false;
        String[] sqlList = {
                "SELECT COUNT(*) FROM MEMBER_TB WHERE USER_ID_PK = ?",
                "SELECT COUNT(*) FROM MEMBER_TB WHERE USER_PHONE = ?",
                "SELECT COUNT(*) FROM MEMBER_TB WHERE USER_EMAIL = ?"
        };
        try {
            conn = Common.getConnection();
            String sql = sqlList[type];
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,inputVal);
            rs = pstmt.executeQuery();
            if(rs.next()) {
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
}
