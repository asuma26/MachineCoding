package in.wynk.utils.controller.coupons;

import in.wynk.coupon.core.dao.entity.Coupon;
import in.wynk.data.enums.State;
import in.wynk.utils.service.coupons.ICouponsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/coupons")
public class CouponsController {

    private final ICouponsService iCouponsService;

    public CouponsController(ICouponsService iCouponsService) {
        this.iCouponsService = iCouponsService;
    }

    @PostMapping("/create")
    public ResponseEntity<Coupon> create(@RequestBody Coupon coupon) {
        return new ResponseEntity<>(iCouponsService.save(coupon), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Coupon> update(@RequestBody Coupon coupon) {
        return new ResponseEntity<>(iCouponsService.update(coupon), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchState(@PathVariable String id, @RequestBody Map<String, String> payload) {
        iCouponsService.switchState(id, State.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Coupon> find(@PathVariable String id) {
        return new ResponseEntity<>(iCouponsService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Coupon>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iCouponsService.findAll(pageable), HttpStatus.OK);
    }

}
