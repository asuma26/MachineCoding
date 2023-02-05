package in.wynk.utils.controller.ads;

import in.wynk.targeting.core.dao.entity.mongo.AdClient;
import in.wynk.utils.service.ads.IAdClientService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/ad/client")
public class AdClientController {

    private final IAdClientService adClientService;

    public AdClientController(IAdClientService adClientService) {
        this.adClientService = adClientService;
    }

    @PostMapping("/create")
    public ResponseEntity<AdClient> create(@RequestBody AdClient adClient) {
        return new ResponseEntity<>(adClientService.save(adClient), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<AdClient> update(@RequestBody AdClient adClient) {
        return new ResponseEntity<>(adClientService.update(adClient), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchAd(@PathVariable String id, @RequestBody Map<String, String> payload) {
        adClientService.switchClient(id, Boolean.valueOf(payload.get("state")));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<AdClient> findAd(@PathVariable String id) {
        return new ResponseEntity<>(adClientService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdClient>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(adClientService.findAll(pageable), HttpStatus.OK);
    }

}
