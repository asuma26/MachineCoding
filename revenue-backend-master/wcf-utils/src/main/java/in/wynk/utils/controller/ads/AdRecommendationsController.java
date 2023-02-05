package in.wynk.utils.controller.ads;

import in.wynk.targeting.core.dao.entity.mongo.AdRecommendations;
import in.wynk.utils.service.ads.IAdRecommendationsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("wynk/s2s/v1/ad/recommendations")
public class AdRecommendationsController {

    private final IAdRecommendationsService adRecommendationsService;

    public AdRecommendationsController(IAdRecommendationsService adRecommendationsService) {
        this.adRecommendationsService = adRecommendationsService;
    }

    @PostMapping("/create")
    public ResponseEntity<AdRecommendations> create(@RequestBody AdRecommendations adRecommendations) {
        return new ResponseEntity<>(adRecommendationsService.save(adRecommendations), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<AdRecommendations> update(@RequestBody AdRecommendations adRecommendations) {
        return new ResponseEntity<>(adRecommendationsService.update(adRecommendations), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<AdRecommendations> findAd(@PathVariable String id) {
        return new ResponseEntity<>(adRecommendationsService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdRecommendations>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(adRecommendationsService.findAll(pageable), HttpStatus.OK);
    }

}
