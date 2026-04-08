package com.secondhand.marketplace.backend.modules.forum.service;

import com.secondhand.marketplace.backend.modules.forum.dto.PostCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.PostSearchDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.PostUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
import com.secondhand.marketplace.backend.modules.forum.vo.PostListVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PostVO;
import org.springframework.transaction.annotation.Transactional;

public interface PostService {
    
    /**
     * 创建帖子（提交后待审核）
     * @param userId 当前登录用户ID
     * @param dto 帖子信息
     * @return 帖子ID
     */
    @Transactional(rollbackFor = Exception.class)
    Long createPost(Long userId, PostCreateDTO dto);
    
    /**
     * 编辑帖子
     * @param userId 当前登录用户ID
     * @param dto 更新信息
     * @return 是否成功（false表示帖子不存在）
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updatePost(Long userId, PostUpdateDTO dto);
    
    /**
     * 删除帖子（软删除）
     * @param userId 当前登录用户ID
     * @param postId 帖子ID
     * @return 是否成功（false表示帖子不存在）
     */
    @Transactional(rollbackFor = Exception.class)
    boolean deletePost(Long userId, Long postId);
    
    /**
     * 获取帖子详情
     * @param userId 当前登录用户ID（可为null）
     * @param postId 帖子ID
     * @return 帖子详情VO
     */
    PostVO getPostDetail(Long userId, Long postId);
    
    /**
     * 分页查询帖子列表
     * @param userId 当前登录用户ID（可为null）
     * @param searchDTO 查询条件
     * @return 分页结果
     */
    PageResult<PostListVO> listPosts(Long userId, PostSearchDTO searchDTO);
    
    /**
     * 查询用户发布的帖子
     * @param currentUserId 当前登录用户ID
     * @param authorId 作者ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<PostListVO> listUserPosts(Long currentUserId, Long authorId, Integer pageNum, Integer pageSize);
    
    /**
     * 审核帖子（管理员）
     * @param adminId 管理员ID
     * @param postId 帖子ID
     * @param approved 是否通过（true-通过，false-驳回）
     * @param rejectReason 驳回原因
     * @return 是否成功（false表示帖子不存在）
     */
    @Transactional(rollbackFor = Exception.class)
    boolean auditPost(Long adminId, Long postId, Boolean approved, String rejectReason);
    
    /**
     * 置顶/取消置顶帖子（管理员）
     * @param adminId 管理员ID
     * @param postId 帖子ID
     * @param top 是否置顶
     * @return 是否成功（false表示帖子不存在）
     */
    @Transactional(rollbackFor = Exception.class)
    boolean topPost(Long adminId, Long postId, Boolean top);
    
    /**
     * 设为/取消精华帖（管理员）
     * @param adminId 管理员ID
     * @param postId 帖子ID
     * @param featured 是否精华
     * @return 是否成功（false表示帖子不存在）
     */
    @Transactional(rollbackFor = Exception.class)
    boolean featurePost(Long adminId, Long postId, Boolean featured);
    
    /**
     * 点赞/取消点赞帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 当前点赞数（null表示帖子不存在）
     */
    @Transactional(rollbackFor = Exception.class)
    Integer likePost(Long userId, Long postId);
    
    /**
     * 收藏/取消收藏帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 当前收藏数（null表示帖子不存在）
     */
    @Transactional(rollbackFor = Exception.class)
    Integer collectPost(Long userId, Long postId);
    
    /**
     * 记录帖子浏览
     * @param userId 用户ID（未登录为null）
     * @param postId 帖子ID
     */
    @Transactional(rollbackFor = Exception.class)
    void recordView(Long userId, Long postId);
    
    /**
     * 转发帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @param channel 转发渠道
     * @return 是否成功（false表示帖子不存在）
     */
    @Transactional(rollbackFor = Exception.class)
    boolean sharePost(Long userId, Long postId, String channel);
}