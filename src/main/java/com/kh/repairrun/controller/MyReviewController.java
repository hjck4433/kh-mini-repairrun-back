package com.kh.repairrun.controller;

import com.kh.repairrun.dao.MemReviewDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.kh.repairrun.common.Common.CORS_ORIGIN;


@CrossOrigin(origins = CORS_ORIGIN)
@RestController
@RequestMapping("/mypage")
public class MyReviewController {
    // Service 페이지
    @PostMapping("/myreview") //FAQ(리액트에서 들어오는값을 쓰는것)
    public ResponseEntity<List<Map<String, String>>> myReviewAll(@RequestBody Map<String,String> userIdData) {
        String userIdBox = userIdData.get("userId");
        MemReviewDAO dao = new MemReviewDAO();
        List<Map<String, String>> myReviewList = dao.userMyreview(userIdBox);
        System.out.println(userIdBox);
        return new ResponseEntity<>(myReviewList, HttpStatus.OK);
    }
}

