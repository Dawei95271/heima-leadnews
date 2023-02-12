package com.heima.wemedia.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.vos.WmNewsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WmNewsMapper extends BaseMapper<WmNews> {

    /**
     * 文章审核列表查询
     * @param myPage
     * @param qw
     */
    IPage<WmNewsVo> getNewsAuthList(@Param("myPage") Page<WmNewsVo> myPage, @Param(Constants.WRAPPER) QueryWrapper<WmNews> qw);


    WmNewsVo getOneNews(@Param("id") Integer id);
}
