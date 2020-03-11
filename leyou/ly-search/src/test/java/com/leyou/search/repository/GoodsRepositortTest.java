package com.leyou.search.repository;

import com.leyou.common.utils.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.service.SerarchService;

import org.intellij.lang.annotations.JdkConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositortTest {

    @Autowired
    private GoodsRepositort goodsgRepositor;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SerarchService searchService;
    @Test
    public void Test(){
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }

    @Test
    public void LoaeDara(){
        int page = 1;
        int rows = 200;
        int siza = 0;
        //查询spu信息
        PageResult<Spu> result = goodsClient.querySpuByPage(page, rows, true, null);
        List<Spu> items = result.getItems();
        //构建goods
        do {
            List<Goods> goodsList = items.stream().map(spu -> {
                try {
                    return searchService.buildGoods(spu);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            goodsgRepositor.saveAll(goodsList);

            // 获取当前页的数据条数，如果是最后一页，没有100条
            siza = items.size();
            //翻页
            page++;
        }while (siza == 100);

    }
}