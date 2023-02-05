package in.wynk.utils.controller.coupons;

import in.wynk.coupon.core.dao.entity.UserCouponAvailedRecord;
import in.wynk.data.enums.State;
import in.wynk.utils.service.coupons.IUserCouponAvailedRecordsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/user/coupon/availed/records")
public class UserCouponAvailedRecordsController {

    private final IUserCouponAvailedRecordsService iUserCouponAvailedRecordsService;

    public UserCouponAvailedRecordsController(IUserCouponAvailedRecordsService iUserCouponAvailedRecordsService) {
        this.iUserCouponAvailedRecordsService = iUserCouponAvailedRecordsService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserCouponAvailedRecord> create(@RequestBody UserCouponAvailedRecord userCouponAvailedRecords) {
        return new ResponseEntity<>(iUserCouponAvailedRecordsService.save(userCouponAvailedRecords), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<UserCouponAvailedRecord> update(@RequestBody UserCouponAvailedRecord userCouponAvailedRecords) {
        return new ResponseEntity<>(iUserCouponAvailedRecordsService.update(userCouponAvailedRecords), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchState(@PathVariable String id, @RequestBody Map<String, String> payload) {
        iUserCouponAvailedRecordsService.switchState(id, State.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<UserCouponAvailedRecord> find(@PathVariable String id) {
        return new ResponseEntity<>(iUserCouponAvailedRecordsService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserCouponAvailedRecord>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iUserCouponAvailedRecordsService.findAll(pageable), HttpStatus.OK);
    }

}
