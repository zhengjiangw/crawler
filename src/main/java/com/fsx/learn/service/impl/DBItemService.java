package com.fsx.learn.service.impl;

import com.fsx.learn.dao.ItemDao;
import com.fsx.learn.pojo.Item;
import com.fsx.learn.service.ItemService;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service("dBItemService")
public class DBItemService implements ItemService {

    @Resource
    private ItemDao itemDao;

    @Override
    public void save(Item item) {
        itemDao.save(item);
    }

    @Override
    public List<Item> findAll(Item item) {
        Example<Item> example = Example.of(item);
        return itemDao.findAll(example);
    }
}
