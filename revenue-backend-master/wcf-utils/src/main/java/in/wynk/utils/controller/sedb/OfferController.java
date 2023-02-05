package in.wynk.utils.controller.sedb;

import in.wynk.utils.domain.OfferConfig;
import in.wynk.utils.request.SwitchOfferRequest;
import in.wynk.utils.response.BaseResponse;
import in.wynk.utils.service.sedb.IOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offers")
@Deprecated
public class OfferController {

    @Autowired
    private IOfferService offerService;

    @PostMapping()
    public ResponseEntity<OfferConfig> save(@RequestBody OfferConfig offer) {
        offerService.save(offer);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/id/{offerId}")
    public ResponseEntity<OfferConfig> update(@RequestBody OfferConfig offer) {
        offerService.update(offer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/id/{offerId}")
    public ResponseEntity<OfferConfig> getOfferPeriodicElementById(@PathVariable int offerId) {
        return new ResponseEntity<>(offerService.getOfferPeriodicElementById(offerId), HttpStatus.OK);
    }

    @GetMapping("/service/{service}")
    public ResponseEntity<List<OfferConfig>> getOfferPeriodicElements(@PathVariable String service) {
        return new ResponseEntity<>(offerService.getOfferPeriodicElements(service), HttpStatus.OK);
    }

    @PatchMapping("/id/{offerId}")
    public ResponseEntity<BaseResponse<Object>> switchOfferPeriodicElementById(@PathVariable int offerId, @RequestBody SwitchOfferRequest request) {
        return new ResponseEntity<>(BaseResponse.build(null, offerService.switchOfferPeriodicElementById(offerId, request.isSystemOffer())), HttpStatus.OK);
    }

    @PatchMapping("/bulk")
    public ResponseEntity<BaseResponse<Object>> switchOfferPeriodicElements(@RequestBody SwitchOfferRequest request) {
        return new ResponseEntity<>(BaseResponse.build(null, offerService.switchOfferPeriodicElements(request.getOfferIds(), request.isSystemOffer())), HttpStatus.OK);
    }

}
