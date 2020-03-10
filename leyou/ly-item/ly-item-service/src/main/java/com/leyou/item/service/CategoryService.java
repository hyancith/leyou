package com.leyou.item.service;


import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    //查询全部分类
    public List<Category> queryAllByCid3(Long id) {
        Category c3 = this.categoryMapper.selectByPrimaryKey(id);
        Category c2 = this.categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = this.categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1,c2,c3);
    }

    public List<String> queryNamesByIds(List<Long> ids) {
        List<Category> categories = categoryMapper.selectByIdList(ids);
        return categories.stream().map(Category::getName).collect(Collectors.toList());
    }
}
