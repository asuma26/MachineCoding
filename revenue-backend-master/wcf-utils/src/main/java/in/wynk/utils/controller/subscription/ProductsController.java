package in.wynk.utils.controller.subscription;

import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.Product;
import in.wynk.utils.service.subscription.IProductsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/products")
public class ProductsController {

    private final IProductsService iProductsService;

    public ProductsController(IProductsService iProductsService) {
        this.iProductsService = iProductsService;
    }

    @PostMapping("/create")
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return new ResponseEntity<>(iProductsService.save(product), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Product> update(@RequestBody Product product) {
        return new ResponseEntity<>(iProductsService.update(product), HttpStatus.OK);
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity switchState(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        iProductsService.switchState(id, State.valueOf(payload.get("state")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Product> find(@PathVariable Integer id) {
        return new ResponseEntity<>(iProductsService.find(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(iProductsService.findAll(pageable), HttpStatus.OK);
    }

}
