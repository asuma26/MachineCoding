package in.wynk.utils.controller.testingConfig;

import in.wynk.data.enums.State;
import in.wynk.targeting.core.dao.entity.mongo.AdsConfigTestUser;
import in.wynk.utils.service.testingConfig.IAdConfigTestUserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/ad/config/test/user")
public class AdConfigTestUserController {

    private final IAdConfigTestUserService iAdConfigTestUserService;

    public AdConfigTestUserController(IAdConfigTestUserService iAdConfigTestUserService) {
        this.iAdConfigTestUserService = iAdConfigTestUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<AdsConfigTestUser> create(@RequestBody AdsConfigTestUser adsConfigTestUser) {
        return new ResponseEntity<>(iAdConfigTestUserService.save(adsConfigTestUser), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<AdsConfigTestUser> update(@RequestBody AdsConfigTestUser adsConfigTestUser) {
        return new ResponseEntity<>(iAdConfigTestUserService.update(adsConfigTestUser), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchState(@PathVariable String id, @RequestBody Map<String, String> payload) {
        iAdConfigTestUserService.switchState(id, State.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<AdsConfigTestUser> find(@PathVariable String id) {
        return new ResponseEntity<>(iAdConfigTestUserService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdsConfigTestUser>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iAdConfigTestUserService.findAll(pageable), HttpStatus.OK);
    }

}
