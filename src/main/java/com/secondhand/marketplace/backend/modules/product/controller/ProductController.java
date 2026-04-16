package com.secondhand.marketplace.backend.modules.product.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.modules.product.dto.ProductCreateDTO;
import com.secondhand.marketplace.backend.modules.product.dto.ProductPageQueryDTO;
import com.secondhand.marketplace.backend.modules.product.dto.ProductUpdateDTO;
import com.secondhand.marketplace.backend.modules.product.entity.Category;
import com.secondhand.marketplace.backend.modules.product.entity.Product;
import com.secondhand.marketplace.backend.modules.product.mapper.CategoryMapper;
import com.secondhand.marketplace.backend.modules.product.service.ProductService;
import com.secondhand.marketplace.backend.modules.product.vo.PageResult;
import com.secondhand.marketplace.backend.modules.product.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Tag(name = "商品管理模块", description = "二手商品创建、查询、状态控制等相关接口")
public class ProductController {

    private final ProductService productService;
    private final CategoryMapper categoryMapper;

    // TODO: 目前没有接真正的登录授权上下文，暂时用模拟的1作为卖家ID
    private final Long MOCK_SELLER_ID = 1L;

    @Operation(summary = "获取商品分类列表", description = "返回所有启用的单层商品分类")
    @GetMapping("/categorylist")
    public CommonResult<List<Category>> categoryList() {
        return CommonResult.success(categoryMapper.selectList(null));
    }

    @Operation(summary = "发布新商品", description = "提交商品主信息+图片，进入待审核(或草稿)状态")
    @PostMapping("/create")
    public CommonResult<ProductVO> createProduct(@RequestBody @Valid ProductCreateDTO dto) {
        ProductVO vo = productService.createProduct(dto, MOCK_SELLER_ID);
        return CommonResult.success(vo);
    }

    @Operation(summary = "保存商品至草稿箱", description = "仅保存信息不提交审核")
    @PostMapping("/draft")
    public CommonResult<ProductVO> saveDraft(@RequestBody @Valid ProductCreateDTO dto) {
        dto.setIsDraft(true);
        ProductVO vo = productService.createProduct(dto, MOCK_SELLER_ID);
        return CommonResult.success(vo);
    }

    @Operation(summary = "分页条件查询商品", description = "支持按分类、状态、关键词检索")
    @PostMapping("/list")
    public CommonResult<PageResult<ProductVO>> listProducts(@RequestBody ProductPageQueryDTO queryDTO) {
        return CommonResult.success(productService.getProductPage(queryDTO));
    }

    @Operation(summary = "获取商品详情", description = "根据ID获取商品所有核心属性与关联图片")
    @GetMapping("/{id}")
    public CommonResult<ProductVO> getProduct(@PathVariable("id") @NotNull Long id) {
        return CommonResult.success(productService.getProductDetail(id));
    }

    @Operation(summary = "修改商品信息", description = "允许修改草稿或重新提交被驳回的商品信息")
    @PutMapping("/{id}")
    public CommonResult<ProductVO> updateProduct(@PathVariable("id") @NotNull Long id, 
                                                 @RequestBody @Valid ProductUpdateDTO dto) {
        dto.setId(id);
        ProductVO vo = productService.updateProduct(dto, MOCK_SELLER_ID);
        return CommonResult.success(vo);
    }

    @Operation(summary = "下架商品", description = "根据ID将发布的商品置为下架状态，非真删除")
    @DeleteMapping("/{id}")
    public CommonResult<String> deleteProduct(@PathVariable("id") @NotNull Long id) {
        productService.deleteProduct(id, MOCK_SELLER_ID);
        return CommonResult.success("下架成功");
    }

    @Operation(summary = "查询商品单独状态", description = "仅返回当前所处的业务阶段字符串")
    @GetMapping("/status")
    public CommonResult<String> getProductStatus(@RequestParam("id") @NotNull Long id) {
        Product p = productService.getById(id);
        return CommonResult.success(p != null ? p.getPublishStatus() : null);
    }

    @Operation(summary = "增加商品浏览量", description = "前端进入详情时自动触发")
    @PutMapping("/{id}/view")
    public CommonResult<String> addViewCount(@PathVariable("id") @NotNull Long id) {
        productService.addViewCount(id);
        return CommonResult.success("浏览量增加成功");
    }

    @Operation(summary = "获取商品简要统计", description = "目前返回商品基本信息(包含浏览/收藏)")
    @GetMapping("/{id}/stats")
    public CommonResult<ProductVO> getProductStats(@PathVariable("id") @NotNull Long id) {
        return CommonResult.success(productService.getProductDetail(id));
    }
}