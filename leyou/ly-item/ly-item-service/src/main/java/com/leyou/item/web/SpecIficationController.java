package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecIficationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecIficationController {

    @Autowired
    private SpecIficationService specIficationService;

    //根据分类id查询规格组
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> list = specIficationService.queryGruopsByCid(cid);
        return ResponseEntity.ok(list);
    }


    //根据组id查询参数
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamsByGid(@RequestParam("gid") Long gid){
        List<SpecParam> list = specIficationService.queryParamsByGid(gid);
        return ResponseEntity.ok(list);
    }
}
