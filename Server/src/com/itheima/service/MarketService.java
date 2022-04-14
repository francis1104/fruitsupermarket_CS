package com.itheima.service;

import com.itheima.dao.MarketDao;
import com.itheima.domain.Fruit;
import com.itheima.domain.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MarketService implements Runnable{

    private MarketDao dao = new MarketDao();

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketService.class);

    private Socket socket;

    public MarketService(Socket socket) {
        this.socket=socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String s = br.readLine();

            String[] split = s.split("&");

            String option = split[0];
            String msg = split[1];

            Option option1 = Option.valueOf(option);

            LOGGER.info("接收到报文:"+option);
            LOGGER.info("接收到信息:"+msg);


            switch (option1){
                case ADMIN_CHECK:
                    //检查登录用户名和密码是否正确
                    checkUser(msg);
                    break;
                case FRUIT_ADD:
                    addFruit(msg);
                    break;
                case FRUIT_FINDALL:
                    findAllFruit();
                    break;
                case FRUIT_DELETE:
                    deleteFruit(msg);
                    break;
                case FRUIT_CHECK_ID:
                    idIsExist(msg);
                    break;
                case FRUIT_UPDATE:
                    updateFruit(msg);
                    break;
                case FRUIT_BUY:
                    buy(msg);
                    break;
            }


        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

    }

    //Service中结账后维护库存方法
    private void buy(String msg) {
        LOGGER.info("Service中接受到更新库存请求,被买走的水果数量(依次):" + msg);
        dao.buy(msg);


    }

    //Service中更新水果的方法
    private void updateFruit(String msg) {
        boolean res =dao.updateFruit(msg);
        LOGGER.info("Service中更新,水果方法接收到:"+msg);
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(res+"");
            bw.newLine();
            bw.flush();
            bw.close();
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    //Service中检查id是否存在
    private void idIsExist(String id) {
        int index = dao.getIndex(id);

        LOGGER.info("Service中查询id是否存在," + id + "号的索引为" + index);

        //true id存在 false id不存在
        boolean res = index != -1;

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(res+"");
            bw.newLine();
            bw.flush();
            bw.close();

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    //Service中删除水果方法
    private void deleteFruit(String delId) {
        boolean res = dao.deleteFruit(delId);
        LOGGER.info("Service中删除,id为"+delId+"的水果,结果为:"+res);

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(res+"");
            bw.newLine();
            bw.flush();
            bw.close();

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    //Service中查看水果方法
    private void findAllFruit() {
        ArrayList<Fruit> allFruit = dao.findAllFruit();
        LOGGER.info("Service中查看,查看集合元素个数为:"+allFruit.size());

        try{
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            oos.writeObject(allFruit);
            oos.flush();
            oos.close();

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    //Service中添加水果方法
    private void addFruit(String msg) {
        boolean res =dao.addFruit(msg);
        LOGGER.info("Service中添加:"+msg);
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(res+"");
            bw.newLine();
            bw.flush();
            bw.close();
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    //Service中检测用户名密码的方法
    private void checkUser(String msg) {
        boolean res = dao.checkUser(msg);

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(res+"");
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
