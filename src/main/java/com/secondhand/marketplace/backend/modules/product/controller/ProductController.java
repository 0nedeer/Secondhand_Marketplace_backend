package com.secondhand.marketplace.backend.modules.product.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
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
import com.secondhand.marketplace.backend.common.util.MinioUtil;
import org.springframework.web.multipart.MultipartFile;

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
    private final MinioUtil minioUtil;


    

    private Long getCurrentUserId() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return null;
        }
        return userId;
    }

    @Operation(summary = "获取商品分类列表", description = "返回所有启用的单层商品分类")
    @GetMapping("/categorylist")
    public CommonResult<List<Category>> categoryList() {
        return CommonResult.success(categoryMapper.selectList(null));
    }

    @Operation(summary = "发布新商品", description = "提交商品主信息+图片，进入待审核(或草稿)状态")
    @PostMapping("/create")
    public CommonResult<ProductVO> createProduct(@RequestBody @Valid ProductCreateDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) return CommonResult.error(401, "请先登录");
        ProductVO vo = productService.createProduct(dto, userId);
        return CommonResult.success(vo);
    }

    @Operation(summary = "保存商品至草稿箱", description = "仅保存信息不提交审核")
    @PostMapping("/draft")
    public CommonResult<ProductVO> saveDraft(@RequestBody @Valid ProductCreateDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) return CommonResult.error(401, "请先登录");
        dto.setIsDraft(true);
        ProductVO vo = productService.createProduct(dto, userId);
        return CommonResult.success(vo);
    }

    @Operation(summary = "分页条件查询商品", description = "支持按分类、状态、关键词检索")
    @PostMapping("/list")
    public CommonResult<PageResult<ProductVO>> listProducts(@RequestBody ProductPageQueryDTO queryDTO) {
        return CommonResult.success(productService.getProductPage(queryDTO, UserContext.getCurrentUserId()));
    }

    @Operation(summary = "获取商品详情", description = "根据ID获取商品所有核心属性与关联图片")
    @GetMapping("/{id}")
    public CommonResult<ProductVO> getProduct(@PathVariable("id") @NotNull Long id) {
        ProductVO vo = productService.getProductDetail(id);
        if (vo == null) {
            return CommonResult.error(404, "商品不存在");
        }
        return CommonResult.success(vo);
    }

    @Operation(summary = "修改商品信息", description = "允许修改草稿或重新提交被驳回的商品信息")
    @PutMapping("/{id}")
    public CommonResult<ProductVO> updateProduct(@PathVariable("id") @NotNull Long id,
                                                 @RequestBody @Valid ProductUpdateDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) return CommonResult.error(401, "请先登录");
        
        dto.setId(id);
        ProductVO vo = productService.updateProduct(dto, userId);
        if (vo == null) {
            return CommonResult.error(404, "商品不存在");
        }
        return CommonResult.success(vo);
    }

    @Operation(summary = "下架商品", description = "根据ID将发布的商品置为下架状态，非真删除")
    @DeleteMapping("/{id}")
    public CommonResult<String> deleteProduct(@PathVariable("id") @NotNull Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) return CommonResult.error(401, "请先登录");

        boolean success = productService.deleteProduct(id, userId);
        if (!success) {
            return CommonResult.error(404, "商品不存在");
        }
        return CommonResult.success("下架成功");
    }

    @Operation(summary = "查询商品单独状态", description = "仅返回当前所处的业务阶段字符串")
    @GetMapping("/status")
    public CommonResult<String> getProductStatus(@RequestParam("id") @NotNull Long id) {
        Product p = productService.getById(id);
        if (p == null) {
            return CommonResult.error(404, "商品不存在");
        }
        return CommonResult.success(p.getPublishStatus());
    }

    @Operation(summary = "增加商品浏览量", description = "前端进入详情时自动触发")
    @PutMapping("/{id}/view")
    public CommonResult<String> addViewCount(@PathVariable("id") @NotNull Long id) {
        boolean success = productService.addViewCount(id);
        if (!success) {
            return CommonResult.error(404, "商品不存在");
        }
        return CommonResult.success("浏览量增加成功");
    }

    @Operation(summary = "获取商品简要统计", description = "目前返回商品基本信息(包含浏览/收藏)")
    @GetMapping("/{id}/stats")
    public CommonResult<ProductVO> getProductStats(@PathVariable("id") @NotNull Long id) {
        ProductVO vo = productService.getProductDetail(id);
        if (vo == null) {
            return CommonResult.error(404, "商品不存在");
        }
        return CommonResult.success(vo);
    }

    @Operation(summary = "上传商品图片", description = "支持上传单张商品图片，内部通过上传用户ID进行文件夹隔离管理")
    @PostMapping("/upload-image")
    public CommonResult<String> uploadImage(@RequestParam("file") MultipartFile file) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return CommonResult.error(401, "请先登录");
        }

        try {
            // 为商品图片划分独立的文件夹：product / 用户ID，便于后续查找孤儿文件或者统计用户存储用量
            String folder = "product/" + userId;
            String objectName = minioUtil.uploadFile(file, folder);
            
            // 返回在 OSS 上的对象路径，前端通过这个路径拼接域名或者再次请求鉴权链接来访问图片
            return CommonResult.success(objectName);
        } catch (Exception e) {
            log.error("上传商品图片失败", e);
            return CommonResult.error(500, "图片上传失败，请稍后重试");
        }
    }
}

