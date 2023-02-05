package in.wynk.utils.controller.sedb;

import in.wynk.utils.domain.SubscriptionPack;
import in.wynk.utils.dto.PackPeriodicElement;
import in.wynk.utils.request.SwitchPackRequest;
import in.wynk.utils.response.BaseResponse;
import in.wynk.utils.service.sedb.ISubscriptionPackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packs")
@Deprecated
public class PackController {

    @Autowired
    private ISubscriptionPackService packService;


    @PostMapping()
    public ResponseEntity<SubscriptionPack> save(@RequestBody SubscriptionPack pack) {
        return new ResponseEntity<>(packService.save(pack), HttpStatus.CREATED);
    }

    @PutMapping("/id/{packId}")
    public ResponseEntity<SubscriptionPack> update(@RequestBody SubscriptionPack pack) {
        return new ResponseEntity<>(packService.update(pack), HttpStatus.OK);
    }

    @GetMapping("/service/{service}")
    public ResponseEntity<List<PackPeriodicElement>> getSubscriptionPacks(@PathVariable String service) {
        return new ResponseEntity<>(this.packService.getPackPeriodicElements(service), HttpStatus.OK);
    }

    @GetMapping("/id/{productId}")
    public ResponseEntity<SubscriptionPack> getSubscriptionPackById(@PathVariable int productId) {
        return new ResponseEntity<>(this.packService.getSubscriptionPackById(productId), HttpStatus.OK);
    }

    @PatchMapping("/id/{packId}")
    public ResponseEntity<BaseResponse<Object>> switchPackPeriodicElementById(@PathVariable int packId, @RequestBody SwitchPackRequest request) {
        return new ResponseEntity<>(BaseResponse.build(null, packService.switchPackPeriodicElementById(packId, request.isDeprecated())), HttpStatus.OK);
    }

    @PatchMapping("/bulk")
    public ResponseEntity<BaseResponse<Object>> switchPackPeriodicElements(@RequestBody SwitchPackRequest request) {
        return new ResponseEntity<>(BaseResponse.build(null, packService.switchPackPeriodicElements(request.getPackIds(), request.isDeprecated())), HttpStatus.OK);
    }


} 
 