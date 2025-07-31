package com.example.datnspct.Controller.client;

import com.example.datnspct.Service.VoucherService;
import com.example.datnspct.dto.VoucherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client/api/voucher")
public class VoucherClientController {

    @Autowired
    private VoucherService voucherService;

    // API lấy danh sách voucher áp dụng được cho danh sách IdSPCT
    @PostMapping("/applicable")
    public ResponseEntity<List<VoucherDTO>> getApplicableVouchers(@RequestBody List<Integer> idSPCTs) {
        try {
            List<VoucherDTO> vouchers = voucherService.getApplicableVouchersForProducts(idSPCTs);
            return ResponseEntity.ok(vouchers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}