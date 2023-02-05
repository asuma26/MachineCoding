package in.wynk.utils.controller.ads;

import in.wynk.targeting.core.constant.AdState;
import in.wynk.targeting.core.dao.entity.mongo.AdConfig;
import in.wynk.utils.service.ads.IAdConfigService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("wynk/s2s/v1/ad/config")
public class AdConfigController {

    private final IAdConfigService adConfigService;

    public AdConfigController(IAdConfigService adConfigService) {
        this.adConfigService = adConfigService;
    }

    @PostMapping("/create")
    public ResponseEntity<AdConfig> create(@RequestBody AdConfig adConfig) {
        return new ResponseEntity<>(adConfigService.save(adConfig), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<AdConfig> update(@RequestBody AdConfig adConfig) {
        return new ResponseEntity<>(adConfigService.update(adConfig), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchAd(@PathVariable String id, @RequestBody Map<String, String> payload) {
        adConfigService.switchState(id, AdState.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/switch-all")
    public ResponseEntity switchAll(@RequestBody Map<String, String> payload) {
        adConfigService.switchAll(payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<AdConfig> findAd(@PathVariable String id) {
        return new ResponseEntity<>(adConfigService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdConfig>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(adConfigService.findAll(pageable), HttpStatus.OK);
    }

}
