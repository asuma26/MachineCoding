package in.wynk.utils.controller.ads;

import in.wynk.targeting.core.dao.entity.mongo.AdProperties;
import in.wynk.utils.service.ads.IAdPropertiesService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("wynk/s2s/v1/ad/properties")
public class AdPropertiesController {

    private final IAdPropertiesService adPropertiesService;

    public AdPropertiesController(IAdPropertiesService adPropertiesService) {
        this.adPropertiesService = adPropertiesService;
    }

    @PostMapping("/create")
    public ResponseEntity<AdProperties> create(@RequestBody AdProperties adProperties) {
        return new ResponseEntity<>(adPropertiesService.save(adProperties), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<AdProperties> update(@RequestBody AdProperties adProperties) {
        return new ResponseEntity<>(adPropertiesService.update(adProperties), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<AdProperties> findAd(@PathVariable String id) {
        return new ResponseEntity<>(adPropertiesService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdProperties>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(adPropertiesService.findAll(pageable), HttpStatus.OK);
    }

}
