package com.fsx.learn.service;

import com.fsx.learn.pojo.Item;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ItemService {

    String IMAGE_DIR = "./images";

    void save(Item items);

    List<Item> findAll(Item items);

    default void saveImage(String picName, byte[] image) {

        OutputStream out = null;
        try {
            out = new FileOutputStream(IMAGE_DIR + "/" + picName);
            out.write(image, 0, image.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
