package in.wynk.utils.controller.subscription;

import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.Plan;
import in.wynk.utils.service.subscription.IPlansService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/plans")
public class PlansController {

    private final IPlansService iPlansService;

    public PlansController(IPlansService iPlansService) {
        this.iPlansService = iPlansService;
    }

    @PostMapping("/create")
    public ResponseEntity<Plan> create(@RequestBody Plan plan) {
        return new ResponseEntity<>(iPlansService.save(plan), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Plan> update(@RequestBody Plan plan) {
        return new ResponseEntity<>(iPlansService.update(plan), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchState(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        iPlansService.switchState(id, State.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Plan> find(@PathVariable Integer id) {
        return new ResponseEntity<>(iPlansService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Plan>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iPlansService.findAll(pageable), HttpStatus.OK);
    }

}
