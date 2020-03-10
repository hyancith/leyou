package com.leyou.item.web;

import com.leyou.common.utils.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    //根据条件查询品牌
    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",defaultValue = "false") boolean desc,
            @RequestParam(value = "key",required = false) String key
    ){
        PageResult<Brand> result = brandService.queryBrandByPage(page, rows, sortBy, desc, key);
        return ResponseEntity.ok(result);
    }

    //新增品牌
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids){

        brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //根据品牌id查询品牌
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id){
        Brand brand = brandService.queryById(id);
        return ResponseEntity.ok(brand);
    }

    //根据分类id查询品牌列表
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandBtCid(@PathVariable("cid")Long cid){
        List<Brand> brands = brandService.queryBrandBtCid(cid);
        return ResponseEntity.ok(brands);
    }


    /**
     * 根据多个id查询品牌
     * @param ids
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids){
        List<Brand> list = brandService.queryBrandByIds(ids);
        if(list == null){
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
}
