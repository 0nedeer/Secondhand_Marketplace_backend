package com.secondhand.marketplace.backend.modules.product.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.modules.product.entity.Product;
import com.secondhand.marketplace.backend.modules.product.entity.Category;
import com.secondhand.marketplace.backend.modules.product.service.ProductService;
import com.secondhand.marketplace.backend.modules.product.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/categorylist")
    public CommonResult<List<Category>> categoryList() {
        return CommonResult.success(categoryMapper.selectList(null));
    }

    @PostMapping("/create")
    public CommonResult<String> createProduct(@RequestBody Product product) {
        product.setPublishStatus("pending_review");
        productService.save(product);
        return CommonResult.success("Success");
    }

    @GetMapping("/list")
    public CommonResult<List<Product>> listProducts() {
        return CommonResult.success(productService.list());
    }

    @GetMapping("/{id}")
    public CommonResult<Product> getProduct(@PathVariable("id") Long id) {
        return CommonResult.success(productService.getById(id));
    }

    @PutMapping("/{id}")
    public CommonResult<String> updateProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        product.setId(id);
        productService.updateById(product);
        return CommonResult.success("Success");
    }

    @DeleteMapping("/{id}")
    public CommonResult<String> deleteProduct(@PathVariable("id") Long id) {
        Product p = new Product();
        p.setId(id);
        p.setPublishStatus("off_shelf");
        productService.updateById(p);
        return CommonResult.success("Success");
    }

    @GetMapping("/status")
    public CommonResult<String> getProductStatus(@RequestParam("id") Long id) {
        Product p = productService.getById(id);
        if (p != null) {
            return CommonResult.success(p.getPublishStatus());
        }
        return CommonResult.success(null);
    }

    @PostMapping("/draft")
    public CommonResult<String> saveDraft(@RequestBody Product product) {
        product.setPublishStatus("draft");
        productService.save(product);
        return CommonResult.success("Success");
    }

    @PutMapping("/{id}/view")
    public CommonResult<String> addViewCount(@PathVariable("id") Long id) {
        productService.addViewCount(id);
        return CommonResult.success("Success");
    }

    @GetMapping("/{id}/stats")
    public CommonResult<Product> getProductStats(@PathVariable("id") Long id) {
        return CommonResult.success(productService.getById(id));
    }
}