package in.wynk.utils.controller.payments;

import in.wynk.data.enums.State;
import in.wynk.payment.core.dao.entity.PaymentMethod;
import in.wynk.utils.service.payments.IPaymentsMethodService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/payments/method")
public class PaymentsMethodController {

    private final IPaymentsMethodService iPaymentsMethodService;

    public PaymentsMethodController(IPaymentsMethodService iPaymentsMethodService) {
        this.iPaymentsMethodService = iPaymentsMethodService;
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentMethod> create(@RequestBody PaymentMethod paymentMethod) {
        return new ResponseEntity<>(iPaymentsMethodService.save(paymentMethod), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<PaymentMethod> update(@RequestBody PaymentMethod paymentMethod) {
        return new ResponseEntity<>(iPaymentsMethodService.update(paymentMethod), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchState(@PathVariable String id, @RequestBody Map<String, String> payload) {
        iPaymentsMethodService.switchState(id, State.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<PaymentMethod> find(@PathVariable String id) {
        return new ResponseEntity<>(iPaymentsMethodService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PaymentMethod>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iPaymentsMethodService.findAll(pageable), HttpStatus.OK);
    }

}
