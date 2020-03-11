package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BrandApi {

    @GetMapping("brand/{id}")
    public Brand queryBrandById(@PathVariable("id") Long id);

    @GetMapping("brand/list")
    public List<Brand> queryBrandByIds(@RequestParam("ids") List<Long> ids);
}
