package com.karrecy.order.service.impl;

import com.karrecy.order.domain.po.Tags;
import com.karrecy.order.mapper.TagsMapper;
import com.karrecy.order.service.ITagsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * tag表 服务实现类
 * </p>
 */
@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags> implements ITagsService {

}
