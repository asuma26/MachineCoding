package in.wynk.utils.controller.coupons;

import in.wynk.coupon.core.dao.entity.UserCouponWhiteListRecord;
import in.wynk.data.enums.State;
import in.wynk.utils.service.coupons.IUserCouponRecordsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/user/coupon/records")
public class UserCouponRecordsController {

    private final IUserCouponRecordsService iUserCouponRecordsService;

    public UserCouponRecordsController(IUserCouponRecordsService iUserCouponRecordsService) {
        this.iUserCouponRecordsService = iUserCouponRecordsService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserCouponWhiteListRecord> create(@RequestBody UserCouponWhiteListRecord UserCouponWhiteListRecord) {
        return new ResponseEntity<>(iUserCouponRecordsService.save(UserCouponWhiteListRecord), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<UserCouponWhiteListRecord> update(@RequestBody UserCouponWhiteListRecord userCouponWhiteListRecord) {
        return new ResponseEntity<>(iUserCouponRecordsService.update(userCouponWhiteListRecord), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchState(@PathVariable String id, @RequestBody Map<String, String> payload) {
        iUserCouponRecordsService.switchState(id, State.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<UserCouponWhiteListRecord> find(@PathVariable String id) {
        return new ResponseEntity<>(iUserCouponRecordsService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserCouponWhiteListRecord>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iUserCouponRecordsService.findAll(pageable), HttpStatus.OK);
    }

}
