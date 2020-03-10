package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecIficationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


//    //根据组id查询参数
//    @GetMapping("params")
//    public ResponseEntity<List<SpecParam>> queryParamsByGid(@RequestParam("gid") Long gid){
//        List<SpecParam> list = specIficationService.queryParamsByGid(gid);
//        return ResponseEntity.ok(list);
//    }

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParam(
            @RequestParam(value="gid", required = false) Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic
    ){
        List<SpecParam> list = specIficationService.querySpecParams(gid,cid,searching,generic);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }


    //根据分类查询规格组及组内参数
    @GetMapping("group/parem/{cid}")
    public  ResponseEntity<List<SpecGroup>> queryGroupByCid(@RequestParam("cid") Long cid){
        return ResponseEntity.ok(specIficationService.queryAllByCid(cid));
    };
}
