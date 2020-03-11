package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface CategoryApi {

    @GetMapping("category/list/ids")
    List<Category> queryCategoryListByids(@RequestParam("ids") List<Long> ids);

    @GetMapping("category/all/level")
    public List<Category> queryAllByCid3(@RequestParam("id") Long id);

    @GetMapping("category")
    public List<String> queryNameByIds(@RequestParam("ids")List<Long> ids);
}
