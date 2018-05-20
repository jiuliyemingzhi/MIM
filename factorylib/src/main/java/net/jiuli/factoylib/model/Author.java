package net.jiuli.factoylib.model;

/**
 * Created by jiuli on 17-9-24.
 */

public interface Author extends Db {


    void setId(String id);

    String getName();

    void setName(String name);

    String getPortrait();

    void setPortrait(String portrait);
}
