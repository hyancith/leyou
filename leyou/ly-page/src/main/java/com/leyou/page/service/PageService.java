package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpeclicationClient;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;




@Service
public class PageService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpeclicationClient specificationClient;

    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long spuId) {


        Map<String,Object> model = new HashMap<>();

        //根据spuId查询spu
        Spu spu = goodsClient.querySpuById(spuId);

        //查询spuDetail
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spuId);

        //查询分类： Map<String,Object>
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = categoryClient.queryNameByIds(cids);

        //初始化一个分类的Map
        List<Map<String,Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",cids.get(i));
            map.put("name",names.get(i));
            categories.add(map);
        }

        //查询品牌brand
        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        //查询skus
        List<Sku> skus = goodsClient.querySkuBySpuId(spuId);

        //查询规格参数组
        List<SpecGroup> groups = specificationClient.queryGroupByCid(spu.getCid3());

        //查询特殊的规格参数
        List<SpecParam> params = specificationClient.querySpecParam(null, spu.getCid3(), null, false);
        //初始化特殊规格参数的map
        Map<Long,String> paramMap = new HashMap<>();
        params.forEach(param ->{
            paramMap.put(param.getId(),param.getName());
        });

        model.put("spu",spu);
        model.put("spuDetail",spuDetail);
        model.put("categories",categories);
        model.put("brand",brand);
        model.put("skus",skus);
        model.put("groups",groups);
        model.put("paramMap",paramMap);

        return model;

    }


    public void  cteateHtml(Long spuId){
        Map<String, Object> model = loadModel(spuId);
        try { //创建thymeleaf上下文对象
        Context context = new Context();
        //把数据放入上下文
         context.setVariables(model);

        File dest = new File("E:\\nginx-1.17.8\\html\\item\\" + spuId + ".html");
            PrintWriter writer = new PrintWriter(dest,"utf-8");
            // 执行页面静态化方法
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            LOGGER.error("页面静态化出错：{}，" + e, model);
        }

    }

//    public void asyncExcuteCreateHtml(Long spuId)) {
//        ThreadUtils.execute(() -> cteateHtml(Long spuId));
//    }



//    public void deleteHtml(Long id) {
//        File file = new File("E:\\nginx-1.17.8\\html\\images\\item", id + ".html");
//        file.deleteOnExit();
//    }
}
