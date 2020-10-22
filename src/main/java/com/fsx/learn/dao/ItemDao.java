package com.fsx.learn.dao;

import com.fsx.learn.pojo.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDao extends JpaRepository<Item,Long> {

}
