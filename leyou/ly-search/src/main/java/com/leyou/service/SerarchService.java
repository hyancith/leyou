package com.leyou.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpeclicationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepositort;



import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SerarchService {

    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpeclicationClient speclicationClient;
    @Autowired
    private GoodsRepositort goodsRepositort;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    //jack工具转换为json数据
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws Exception {
        //查询分类
        List<Category> categories = categoryClient.queryCategoryListByids(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());

        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //搜索字段
        String all = spu.getSubTitle() + StringUtils.join(names," ") +brand.getName();
        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        //对sku进行处理，只获取想要的字段
        List<Map<String ,Object>> skus = new ArrayList<>();
        //对价格的处理
        List<Long> prices = new ArrayList<>();
        for (Sku sku : skuList) {
            Map<String ,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("images",StringUtils.substringBefore(sku.getImages(),","));

            prices.add(sku.getPrice());

            skus.add(map);
        }
        //原始的对价格的处理
        // List<Long> prices = skuList.stream().map(Sku::getPrice).collect(Collectors.toList());

        //查询规格参数
        List<SpecParam> params = speclicationClient.querySpecParam(null, spu.getCid3(), true, null);
        //查询详情
        SpuDetail spuDetail =  goodsClient.querySpuDetailById(spu.getId());
        //获取通用的规格参数
        String genericSpec = spuDetail.getGenericSpec();
        Map<Long ,Object> genericMap= MAPPER.readValue(genericSpec, new TypeReference<Map<Long, Object>>() {});
        //获取特有的规格参数
        String specialSpec = spuDetail.getSpecialSpec();
         Map<Long,List<Object>> specialMap = MAPPER.readValue(specialSpec,new TypeReference<Map<Long,Object>>(){});
        //规格参数   key规格参数名    value规格参数值
        HashMap<String, Object> specs = new HashMap<>();
        for (SpecParam param : params) {
            //规格名称
            String Key = param.getName();
            Object value ="";
            //判断是否是通用的属性
            if (param.getGeneric()){
                value = genericMap.get(param.getId());
                //判断是数值类型的
                if (param.getNumeric()){
                    //处理成段
                   value = chooseSegment(value.toString(),param);
                }

            }else {
                value = specialMap.get(param.getId());
            }

            //存入map
            specs.put(Key,value);


        }

        //构建goods
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setAll(all);//搜索字段
        goods.setPrice(prices);//价格
        goods.setSkus(MAPPER.writeValueAsString(skus));//skus
        goods.setSpecs(specs);//规格参数
        goods.setSubTitle(spu.getSubTitle());

        return goods;
    }

    public PageResult<Goods> search(SearchRequest request){
        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if (StringUtils.isBlank(request.getKey())) {
            return null;
        }
        Integer page = request.getPage() - 1;
        Integer size = request.getSize();
        //创建查询构建器
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
         // 通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        searchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));
        //分页
        searchQueryBuilder.withPageable(PageRequest.of(page, size));

        //搜索条件
        QueryBuilder basicQuery = buildBasicQuery(request);
        searchQueryBuilder.withQuery(basicQuery);


        //过滤
        //searchQueryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()));
        searchQueryBuilder.withSourceFilter(new FetchSourceFilter(
                new String[]{"id","skus","subTitle"}, null));

        //聚合分类和品牌
            //聚合分类
            String categoryAggName ="category_agg";
            searchQueryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
            //聚合品牌
            String brandAggName ="brand_agg";
            searchQueryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //查询
//        Page<Goods> result = goodsRepositort.search(searchQueryBuilder.build());
        AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(searchQueryBuilder.build(), Goods.class);
        //解析结果
        Long total = result.getTotalElements();
        int totalpage = result.getTotalPages();
        List<Goods> goods = result.getContent();

        //聚合的解析
       // Aggregations aggs = result.getAggregations();
        // 商品分类的聚合结果
        List<Category> categories = getCategoryAggResult(result.getAggregation(categoryAggName));
        // 品牌的聚合结果
        List<Brand> brands = getBrandAggResult(result.getAggregation(brandAggName));

        // 根据商品分类判断是否需要聚合
        List<Map<String, Object>> specs =null;
        if (categories!=null && categories.size() == 1) {
            // 如果商品分类只有一个才进行聚合，并根据分类与基本查询条件聚合
            specs = getSpec(categories.get(0).getId(), basicQuery);
        }

        return new SearchResult(total, totalpage, goods,categories,brands,specs);
    }

    private List<Map<String, Object>> getSpec(Long cid, QueryBuilder basicQuery) {
          try{

            List<Map<String, Object>> specs = new ArrayList<>();
             // 不管是全局参数还是sku参数，只要是搜索参数，都根据分类id查询出来
            List<SpecParam> params = speclicationClient.querySpecParam(null, cid, true,null );
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(basicQuery);

            // 聚合规格参数
            for (SpecParam param : params) {
                String name = param.getName();
                queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." +name+ ".keyword"));
            }

            // 查询
            AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(queryBuilder.build(), Goods.class);
             // Map<String, Aggregation> aggs = this.elasticsearchTemplate.query(queryBuilder.build(), SearchResponse::getAggregations).asMap();
            // 解析聚合结果
            Aggregations aggs = result.getAggregations();
            for (SpecParam param : params) {
                String name = param.getName();
                StringTerms terms=(StringTerms) aggs.get(name);
                List<String> options = terms.getBuckets().stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());
                HashMap<String, Object> map = new HashMap<>();
                map.put("k",name);
                map.put("options",options);
                specs.add(map);
            }
            return specs;
          } catch (Exception e){
            System.out.println("规格参数出现异常：" + e);
            return null;
        }
    }

    private QueryBuilder buildBasicQuery(SearchRequest request) {
        //创建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()));
        //过滤条件
        Map<String,Object> map = request.getFilter();
        for (Map.Entry<String,Object> entry : map.entrySet()){
            String key = entry.getKey();
            //处理key
            if ( !"cid3".equals(key) && !"brandId".equals(key)){
                key = "specs" + key  +".keyword";
            }
            queryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));

        }
        return queryBuilder;
    }


    // 解析品牌聚合结果
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        try {
            LongTerms brandAgg = (LongTerms) aggregation;
            List<Long> bids = new ArrayList<>();
            for (LongTerms.Bucket bucket : brandAgg.getBuckets()) {
                bids.add(bucket.getKeyAsNumber().longValue());
            }
            // 根据id查询品牌
            return brandClient.queryBrandByIds(bids);
        } catch (Exception e){
            System.out.println("品牌聚合出现异常：" + e);
            return null;
        }
    }

    // 解析商品分类聚合结果
    private List<Category> getCategoryAggResult(Aggregation aggregation) {
        try{
            List<Category> categories = new ArrayList<>();
            LongTerms categoryAgg = (LongTerms) aggregation;
            List<Long> cids = new ArrayList<>();
            for (LongTerms.Bucket bucket : categoryAgg.getBuckets()) {
                cids.add(bucket.getKeyAsNumber().longValue());
            }
            // 根据id查询分类名称
            List<Category> names = categoryClient.queryCategoryListByids(cids);
            return names;
        } catch (Exception e){
            System.out.println("分类聚合出现异常："+ e);
            return null;
        }
    }




    /**
     * 获取可选区间
     *
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
}
