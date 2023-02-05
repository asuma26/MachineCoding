package in.wynk.utils.controller.coupons;

import in.wynk.coupon.core.dao.entity.CouponCodeLink;
import in.wynk.data.enums.State;
import in.wynk.utils.service.coupons.ICouponsCodeLinkService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/coupon/code/link")
public class CouponCodeLinkController {

    private final ICouponsCodeLinkService iCouponsCodeLinkService;

    public CouponCodeLinkController(ICouponsCodeLinkService iCouponsCodeLinkService) {
        this.iCouponsCodeLinkService = iCouponsCodeLinkService;
    }

    @PostMapping("/generate/{n}")
    public ResponseEntity<List<CouponCodeLink>> generate(@PathVariable Integer n, @RequestBody Map<String, String> payload) {
        return new ResponseEntity<>(iCouponsCodeLinkService.generate(n, payload.get("couponId"), payload.get("totalCount")), HttpStatus.CREATED);
    }

    @PostMapping("/create")
    public ResponseEntity<CouponCodeLink> create(@RequestBody CouponCodeLink couponCodeLink) {
        return new ResponseEntity<>(iCouponsCodeLinkService.save(couponCodeLink), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<CouponCodeLink> update(@RequestBody CouponCodeLink couponCodeLink) {
        return new ResponseEntity<>(iCouponsCodeLinkService.update(couponCodeLink), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchState(@PathVariable String id, @RequestBody Map<String, String> payload) {
        iCouponsCodeLinkService.switchState(id, State.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<CouponCodeLink> find(@PathVariable String id) {
        return new ResponseEntity<>(iCouponsCodeLinkService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CouponCodeLink>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iCouponsCodeLinkService.findAll(pageable), HttpStatus.OK);
    }

}
