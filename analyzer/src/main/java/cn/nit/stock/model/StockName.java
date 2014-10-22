package cn.nit.stock.model;

import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by vicky on 2014/10/22.
 */
@Entity
public class StockName {

    @Id
    private ObjectId id;

    private String name,code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "StockName{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
