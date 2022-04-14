package com.itheima.dao;

import com.itheima.domain.Fruit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class MarketDao {

    private static ArrayList<Fruit> fruitList = new ArrayList<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDao.class);

    static {
        LOGGER.info("================loading==============");
        reLoad();
        LOGGER.info("================加载完成==============");
    }


    //Dao中检测用户名密码的方法
    public boolean checkUser(String msg) {
        Properties prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream("Server/src/user.properties");
            prop.load(fis);
            fis.close();

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        String[] split = msg.split("=");

        String username = split[0];
        String password = split[1];

        //假设登录失败
        boolean flag = false;

        //集合中存在该用户名,并且与之对应的密码也相同
        if (prop.containsKey(username) && prop.getProperty(username).equals(password)) {
            flag = true;
        }

        return flag;
    }

    //Dao中添加水果方法
    public boolean addFruit(String msg) {
        //id+","+name+","+price+","+unit+","+number;
        String[] split = msg.split(",");
        String id = split[0];
        String name = split[1];
        String price = split[2];
        String unit = split[3];
        String number = split[4];

        int priceInt = Integer.parseInt(price);
        int numberInt = Integer.parseInt(number);

        Fruit fruit = new Fruit(id, name, priceInt, unit, numberInt);

        fruitList.add(fruit);
        reSave();

        LOGGER.info("dao中添加完成,集合中元素个数为:" + fruitList.size());
        return true;

    }

    //Dao中查看水果方法
    public ArrayList<Fruit> findAllFruit() {
        return fruitList;
    }

    //Dao中删除水果方法
    public boolean deleteFruit(String delId) {
        int index = getIndex(delId);

        if (index == -1) {
            return false;
        } else {
            fruitList.remove(index);
            reSave();
            LOGGER.info("dao中成功删除水果,Id:"+delId+" 索引为:"+index);
            return true;
        }
    }

    //Dao根据id查索引方法
    public int getIndex(String id) {
        int index = -1;
        for (Fruit fruit : fruitList) {
            //id在集合中存在
            if (id.equals(fruit.getId())) {
                //记录索引位置
                index = fruitList.indexOf(fruit);
                break;
            }
        }
        return index;
    }

    //Dao中更新水果方法
    public boolean updateFruit(String msg) {
        //id+","+name+","+price+","+unit+","+number;
        String[] split = msg.split(",");
        String id = split[0];
        String name = split[1];
        String price = split[2];
        String unit = split[3];
        String number = split[4];

        int priceInt = Integer.parseInt(price);
        int numberInt = Integer.parseInt(number);

        Fruit fruit = new Fruit(id, name, priceInt, unit, numberInt);

        int index = getIndex(id);

        fruitList.set(index, fruit);
        reSave();
        LOGGER.info("dao中成功更新水果:"+fruit.toString());

        return true;
    }

    //Dao购买完成维护库存个数
    public void buy(String msg) {
        //客户买走的水果个数
        // 0,0,2,0,3,
        String[] split = msg.split(",");

        //遍历水果集合
        for (int i = 0; i < fruitList.size(); i++) {
            //获取到每一个水果
            Fruit fruit = fruitList.get(i);
            //获取该水果的库存
            int oldNum = fruit.getNumber();
            //用库存减去客户购买的个数
            int newNum = oldNum - Integer.parseInt(split[i]);
            //更新库存
            fruit.setNumber(newNum);

        }
        //把库存为0的水果信息剔除
        fruitList.removeIf(fruit -> fruit.getNumber() == 0);
        reSave();
        LOGGER.info("dao中水果库存更新完毕,水果个数为:"+fruitList.size());
    }

    //存档
    private void reSave(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Server/Fruit.txt"));

            oos.writeObject(fruitList);
            oos.flush();
            oos.close();

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //读档
    private static void reLoad(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Server/Fruit.txt"));

            //读取对象复制内存中的集合
            fruitList = (ArrayList<Fruit>) ois.readObject();

            ois.close();

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
