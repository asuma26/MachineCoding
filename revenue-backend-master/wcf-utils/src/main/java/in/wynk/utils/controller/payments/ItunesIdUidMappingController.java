package in.wynk.utils.controller.payments;

import in.wynk.payment.core.dao.entity.ItunesIdUidMapping;
import in.wynk.utils.service.payments.IItunesIdUidMappingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wynk/s2s/v1/itunes")
public class ItunesIdUidMappingController {

    private final IItunesIdUidMappingService iItunesIdUidMappingService;

    public ItunesIdUidMappingController(IItunesIdUidMappingService iItunesIdUidMappingService) {
        this.iItunesIdUidMappingService = iItunesIdUidMappingService;
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<ItunesIdUidMapping> find(@PathVariable String id) {
        return new ResponseEntity<>(iItunesIdUidMappingService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItunesIdUidMapping>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iItunesIdUidMappingService.findAll(pageable), HttpStatus.OK);
    }

}
