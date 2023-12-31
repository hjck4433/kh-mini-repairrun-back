package com.kh.repairrun.controller;

import com.kh.repairrun.dao.PartnerDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kh.repairrun.common.Common.CORS_ORIGIN;

@CrossOrigin(origins = CORS_ORIGIN)
@RestController
@RequestMapping("/partners")
public class PartnerController {
    @PostMapping("/login")
    public ResponseEntity<Boolean> partnerLogin(@RequestBody Map<String, String> loginData) {
        String partnerId = loginData.get("id");
        String partnerPw = loginData.get("pw");
        System.out.println("partnerId : " + partnerId);
        System.out.println("partnerPw : " + partnerPw);
        PartnerDAO dao = new PartnerDAO();
        boolean result = dao.partnerLoginCheck(partnerId, partnerPw);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/ptninfo")
    public ResponseEntity<Map<String, String>> partnerInfo(@RequestBody Map<String, String> ptnData){
        String ptnId = ptnData.get("ptnId");
        PartnerDAO dao = new PartnerDAO();
        Map<String,String> result = dao.partnerInfo(ptnId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/logolist")
    public ResponseEntity<List<String>> partnerLogo() {
        PartnerDAO dao = new PartnerDAO();
        List<String> partnerLogo = new ArrayList<>();
        partnerLogo = dao.partnerLogos();
        return new ResponseEntity<> (partnerLogo, HttpStatus.OK);

    }

    @PostMapping("/itemlist")
    public ResponseEntity<List<Map<String,Object>>> ptnItemList(@RequestBody Map<String, String> ptnId) {
        String id = ptnId.get("id");
        System.out.println("ptnid :" + id);
        PartnerDAO dao = new PartnerDAO();
        List<Map<String,Object>> result = new ArrayList<>();
        result = dao.partnerItemList(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //중복 체크
    @PostMapping("/uniquecheck")
    public ResponseEntity<Boolean> uniqueCheck(@RequestBody Map<String, String> checkData) {
        Integer type = Integer.parseInt(checkData.get("type"));
        String inputVal = checkData.get("inputVal");
        System.out.println("type : " + type);
        System.out.println("inputVal : " + inputVal);
        PartnerDAO dao = new PartnerDAO();
        boolean result = dao.checkUnique(type, inputVal);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Boolean> newPartner(@RequestBody Map<String, String> newPtnData) {
        String id = newPtnData.get("id");
        String pw = newPtnData.get("pw");
        String name = newPtnData.get("name");
        String ptnEmail = newPtnData.get("ptnEmail");
        String ptnPhone = newPtnData.get("ptnPhone");
        String ptnAddr = newPtnData.get("ptnAddr");
        String ptnDesc = newPtnData.get("ptnDesc");
        String ptnLogo = newPtnData.get("ptnLogo");
        PartnerDAO dao = new PartnerDAO();
        boolean result = dao.newPartnerInsert(id, pw, name, ptnEmail, ptnPhone, ptnAddr, ptnDesc, ptnLogo);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/insertitems")
    public ResponseEntity<Boolean> insertPartnerItems(@RequestBody Map<String, String> newPtn) {
        String ptnId = newPtn.get("ptnId");
        PartnerDAO dao = new PartnerDAO();
        boolean result = dao.batchInputPartnerItems(ptnId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/updateinfo")
    public ResponseEntity<Boolean> updatePtnInfo(@RequestBody Map<String, String> updateList) {
        String ptnId = updateList.get("ptnId");
        String ptnPw = updateList.get("ptnPw");
        String ptnEmail = updateList.get("ptnEmail");
        String ptnPhone = updateList.get("ptnPhone");
        String ptnAddr = updateList.get("ptnAddr");
        String ptnImg = updateList.get("ptnImg");
        String ptnDesc = updateList.get("ptnDesc");
        PartnerDAO dao = new PartnerDAO();
        boolean result = dao.partnerUpdate(ptnId, ptnPw, ptnEmail, ptnPhone, ptnAddr, ptnImg, ptnDesc);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/updatedetail")
    public ResponseEntity<Boolean> updateDetail(@RequestBody Map<String,String> updateInfo) {
        String ptnId = updateInfo.get("ptnId");
        String repairDetail = updateInfo.get("repairDetail");
        Integer days = Integer.parseInt(updateInfo.get("days"));
        Integer price = Integer.parseInt(updateInfo.get("price"));
        PartnerDAO dao = new PartnerDAO();
        boolean result = dao.updateItemInfo(ptnId, repairDetail, days, price);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/ptnlist")
    public ResponseEntity <List<Map<String, Object>>>partnerlist(){
        PartnerDAO dao = new PartnerDAO();
        List<Map<String,Object>> partnerList = new ArrayList<>();
        partnerList = dao.ptnList();
        System.out.println(partnerList);
        return new ResponseEntity<>(partnerList,HttpStatus.OK);
    }
}
