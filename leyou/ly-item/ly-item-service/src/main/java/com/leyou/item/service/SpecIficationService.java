package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecIficationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;


    public List<SpecGroup> queryGruopsByCid(Long cid){
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(group);
        return list;
    };

    public List<SpecParam> queryParamsByGid(Long gid) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        List<SpecParam> params = specParamMapper.select(param);
        return params;
    }


    //根据条件查询规格参数
    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching, Boolean generic) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setSearching(searching);
        param.setGeneric(generic);
        return specParamMapper.select(param);
    }

    public List<SpecGroup> queryAllByCid(Long cid) {
        //查询规格组
        List<SpecGroup> specGroups = queryGruopsByCid(cid);
       specGroups.forEach(gruop -> {
           List<SpecParam> params = querySpecParams(gruop.getId(), null, null, null);
            gruop.setParams(params);
       });

        return specGroups;
    }
}
