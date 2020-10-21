package com.fsx.learn.service.impl;

import com.alibaba.fastjson.JSON;
import com.fsx.learn.pojo.Item;
import com.fsx.learn.service.ItemService;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@Service("fileItemService")
public class FileItemService implements ItemService {

    private static final String file_path = "./data.json";

    @Override
    public void save(Item item) {

        Writer writer = null;
        try{
            writer = new BufferedWriter(new FileWriter(file_path, true));
            writer.write(JSON.toJSONString(item));
            writer.write("\n");
            System.out.println("保存数据 " + JSON.toJSONString(item));
        } catch (Exception e) {
            throw new RuntimeException("保存失败", e);
        } finally {

            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Item> findAll(Item item) {
        return new ArrayList<>();
    }
}
