package com.leyou.item.web;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

   //根据父类id查询商品分类
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryListByPid (@RequestParam ("pid") Long pid){
        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }

    //根据id查询商品分类
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryListByids(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }


    /**
     * 根据3级分类id，查询1~3级的分类
     * @param id
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long id){
        List<Category> list = categoryService.queryAllByCid3(id);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }


    /**
     * 根据id查询分类的名称(集合)
     *
     * @param ids
     * @return
     */
    @GetMapping
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids")List<Long> ids){
        List<String> names = categoryService.queryNamesByIds(ids);
        if (CollectionUtils.isEmpty(names)){
            //404:资源服务器未找到
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.notFound().build();
        }
        //200:查询成功
        return ResponseEntity.ok(names);
    }
}
