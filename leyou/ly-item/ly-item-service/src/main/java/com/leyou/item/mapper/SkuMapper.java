package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.Sku;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> , IdListMapper<Sku,Long> {
}