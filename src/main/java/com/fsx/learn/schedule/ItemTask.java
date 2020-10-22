package com.fsx.learn.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsx.learn.pojo.Item;
import com.fsx.learn.service.ItemService;
import com.fsx.learn.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class ItemTask {

    @Autowired
    private HttpUtils httpUtils;

    @Autowired
    @Qualifier("fileItemService")
    private ItemService itemService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static Logger logger = LoggerFactory.getLogger(ItemTask.class);

    //  调度注解，fixedDelay为当下载任务完成后，间隔多长时间进行下一次的任务
    @Scheduled(fixedDelay =1000 * 3600 * 24 * 5)
    public void itemTask() throws Exception{

        //  声明需要解析的初始地址
        String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8" +
                "&qrst=1&rt=1&stop=1&vt=2&wq=%E6%89%8B%E6%9C%BA&s=1&click=0&page=";

        //  遍历页面对手机的搜索进行遍历结果
        for (int i = 1; i < 10; i=i+3) {
            String html = null;
            try {
                html = httpUtils.doGetHtml(url+i);
                //  解析页面，获取商品数据并存储
                parse(html);
            } catch (Exception e) {
                logger.error("解析页面出错, 跳过", e);
            }
        }
        logger.info("手机数据抓取完成！！！");
    }

    /**
     * 解析页面，获取商品数据并存储
     * @param html
     */
    private void parse(String html) throws Exception {
        //  解析HTML获取Document
        Document doc = Jsoup.parse(html);
        //  获取spu
        Elements spuEles = doc.select("div#J_goodsList > ul > li");
        //  遍历获取spu数据
        for (Element spuEle : spuEles) {
            //  获取spu
            String spuAttr = spuEle.attr("data-spu");
            Long spu = StringUtils.isBlank(spuAttr) ? null : Long.parseLong(spuAttr);
            //  获取sku信息
            Elements skuEles = spuEle.select("li.ps-item");
            for (Element skuEle : skuEles) {
                try {
                    saveSku(spu, skuEle);
                } catch (Exception e) {
                    logger.error("保存信息出错, 跳过", e);
                }
            }
        }
    }

    private void saveSku(long spuId, Element skuEle) throws Exception {
        //  获取sku
        long sku = Long.parseLong(skuEle.select("[data-sku]").attr("data-sku"));
        //  根据sku查询商品数据
        Item item = new Item();
        item.setSku(sku);
        List<Item> list = this.itemService.findAll(item);
        if (list.size()>0){
            //如果商品存在，就进行下一个循环，该商品不保存，因为已存在
            return;
        }
        //  设置商品的spu
        item.setSpu(spuId);

        //  获取商品的详情信息
        String itemUrl = "https://item.jd.com/"+sku+".html";
        item.setUrl(itemUrl);

        //  商品图片
        String picUrl = skuEle.select("img[data-sku]").first().attr("data-lazy-img");
        picUrl ="https:"+picUrl.replace("/n9/","/n1/");	//	替换图片格式
        byte[] image = httpUtils.doGetImage(picUrl);
        String picName = UUID.randomUUID().toString() + ".jpg";
        item.setPic(picName);

        //  商品价格，页面源码中无法获得商品价格，使用下方URl获得
        String priceJson = httpUtils.doGetHtml("https://p.3.cn/prices/mgets?skuIds=J_" + sku);
        double price = MAPPER.readTree(priceJson).get(0).get("p").asDouble();
        item.setPrice(price);

        //  商品标题
        String itemInfo = this.httpUtils.doGetHtml(item.getUrl());
        String title = Jsoup.parse(itemInfo).getElementsByClass("sku-name").get(0).text();
        item.setTitle(title);

        //  商品创建时间
        item.setCreated(new Date());
        //  商品修改时间
        item.setUpdated(item.getCreated());

        //  保存商品数据到数据库中
        itemService.save(item);
        itemService.saveImage(picName, image);
    }
}
