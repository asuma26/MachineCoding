package in.wynk.utils.controller.subscription;

import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.Partner;
import in.wynk.utils.service.subscription.IPartnersService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/partners")
public class PartnersController {

    private final IPartnersService iPartnersService;

    public PartnersController(IPartnersService iPartnersService) {
        this.iPartnersService = iPartnersService;
    }

    @PostMapping("/create")
    public ResponseEntity<Partner> create(@RequestBody Partner partner) {
        return new ResponseEntity<>(iPartnersService.save(partner), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Partner> update(@RequestBody Partner partner) {
        return new ResponseEntity<>(iPartnersService.update(partner), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchState(@PathVariable String id, @RequestBody Map<String, String> payload) {
        iPartnersService.switchState(id, State.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Partner> find(@PathVariable String id) {
        return new ResponseEntity<>(iPartnersService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Partner>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iPartnersService.findAll(pageable), HttpStatus.OK);
    }

}
