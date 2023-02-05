package in.wynk.utils.controller.payments;

import in.wynk.data.enums.State;
import in.wynk.payment.core.dao.entity.UserPreferredPayment;
import in.wynk.utils.service.payments.IUserPreferredPaymentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/user/preferred/payment")
public class UserPreferredPaymentController {

    private final IUserPreferredPaymentService iUserPreferredPaymentService;

    public UserPreferredPaymentController(IUserPreferredPaymentService iUserPreferredPaymentService) {
        this.iUserPreferredPaymentService = iUserPreferredPaymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserPreferredPayment> create(@RequestBody UserPreferredPayment userPreferredPayment) {
        return new ResponseEntity<>(iUserPreferredPaymentService.save(userPreferredPayment), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<UserPreferredPayment> update(@RequestBody UserPreferredPayment userPreferredPayment) {
        return new ResponseEntity<>(iUserPreferredPaymentService.update(userPreferredPayment), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchState(@PathVariable String id, @RequestBody Map<String, String> payload) {
        iUserPreferredPaymentService.switchState(id, State.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<UserPreferredPayment> find(@PathVariable String id) {
        return new ResponseEntity<>(iUserPreferredPaymentService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserPreferredPayment>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iUserPreferredPaymentService.findAll(pageable), HttpStatus.OK);
    }

}
