package in.wynk.utils.controller.subscription;

import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.Offer;
import in.wynk.utils.service.subscription.IOffersService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/offers")
public class OffersController {

    private final IOffersService iOffersService;

    public OffersController(IOffersService iOffersService) {
        this.iOffersService = iOffersService;
    }

    @PostMapping("/create")
    public ResponseEntity<Offer> create(@RequestBody Offer offer) {
        return new ResponseEntity<>(iOffersService.save(offer), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Offer> update(@RequestBody Offer offer) {
        return new ResponseEntity<>(iOffersService.update(offer), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchState(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        iOffersService.switchState(id, State.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Offer> find(@PathVariable Integer id) {
        return new ResponseEntity<>(iOffersService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Offer>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iOffersService.findAll(pageable), HttpStatus.OK);
    }

}
