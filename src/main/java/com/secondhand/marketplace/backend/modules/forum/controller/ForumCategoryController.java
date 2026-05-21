package com.secondhand.marketplace.backend.modules.forum.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.forum.service.CategoryService;
import com.secondhand.marketplace.backend.modules.forum.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/forum/category")
public class ForumCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public CommonResult<List<CategoryVO>> listCategories() {
        if (UserContext.getCurrentUserId() == null) {
            throw new BusinessException(401, "请先登录");
        }
        return CommonResult.success(categoryService.listCategories());
    }
}
