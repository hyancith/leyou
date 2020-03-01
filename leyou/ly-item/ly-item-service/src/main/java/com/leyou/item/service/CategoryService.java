package com.leyou.item.service;


import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    public List<Category> queryCategoryListByPid(Long pid) {
        //查询条件，mapper会把对象中的非空属性作为查询条件
        Category t = new Category();
        t.setParentId(pid);
        List<Category>  list = categoryMapper.select(t);
        if (list==null ||list.isEmpty()){

        }
        return list;
    }

    //
    public List<Category> queryByIds (List<Long> ids){
        List<Category> categories = categoryMapper.selectByIdList(ids);

        return categories;
    }
}
