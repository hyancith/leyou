package com.leyou.item.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.utils.PageResult;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMpapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.Spu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMpapper spuMpapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    public PageResult querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {

        //分页
        PageHelper.startPage(page,rows);
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //过滤
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if (saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");

        //查询
        List<Spu> spus = spuMpapper.selectByExample(example);

        //解析分类和品牌的匹配
        loadCategoeryAndBradName(spus);

        //解析分页的结果
        PageInfo<Spu> info = new PageInfo<>(spus);
        return new PageResult<>(info.getTotal(),spus);
    }

    private void loadCategoeryAndBradName(List<Spu> spus) {
        for (Spu spu : spus){
            //处理分类名称
            List<String> names = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())).stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names,"/"));
            //处理品牌名称
           spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }
}
