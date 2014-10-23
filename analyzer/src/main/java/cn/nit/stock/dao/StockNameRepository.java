package cn.nit.stock.dao;

import cn.nit.stock.model.StockName;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by vicky on 2014/10/23.
 */
public interface StockNameRepository extends MongoRepository<StockName, String> {

}
