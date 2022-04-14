package com.itheima.controller;

import com.itheima.domain.Fruit;
import com.itheima.domain.Option;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class MarketController {

    private Scanner sc = new Scanner(System.in);

    private ArrayList<Integer> fruitNum = new ArrayList<>();//购买的水果个数

    private int totalPrice = 0;//结算总价

    private boolean flag = true;//切换排序开关

    //客户二级菜单
    public void customerStart() {

        while (true) {
            System.out.println("请输入您的选择:(1:查看水果     2:开始购买       3:结账      4:切换排序    5:退出)");

            String option = sc.next();

            switch (option) {
                case "1":
                    findAllFruit();
                    break;
                case "2":
                    findAllFruit();
                    shopping();
                    break;
                case "3":
                    buy();
                    break;
                case "4":
                    flag = !flag;
                    System.out.println("切换成功,请重新查看~");
                    break;
                case "5":
                    System.out.println("欢迎您下次光临~");
                    return;
                default:
                    System.out.println("您的输入有误");
                    break;
            }
        }
    }

    //结账
    private void buy() {

        if (totalPrice == 0 && fruitNum.size() == 0) {
            System.out.println("您还没有任何账单");
            return;
        }

        //优惠金额
        double finalPrice;

        if (totalPrice > 100 && totalPrice <= 200) {
            //大于100,超出100的部分打九折
            finalPrice = (totalPrice - 100) * 0.9 + 100;
            System.out.println("您购买的水果原始总金额是:" + totalPrice + "元.优惠之后的总金额是:" + finalPrice + "元");

        } else if (totalPrice > 200 && totalPrice <= 500) {
            //大于100,不超出200的部分九折
            double p1 = 100 * 0.9;
            //超出200,500以内的8折
            double p2 = totalPrice - 200.0;
            double p3 = p2 * 0.8;
            finalPrice = p1 + p3 + 100;
            System.out.println("您购买的水果原始总金额是:" + totalPrice + "元.优惠之后的总金额是:" + finalPrice + "元");

        } else if (totalPrice > 500) {
            //大于100,不超出200的部分九折
            double p1 = 100 * 0.9;
            //超出200,500以内的8折
            double p2 = 300 * 0.8;
            //超出500的7折
            double p3 = totalPrice - 500.0;
            double p4 = p3 * 0.7;
            finalPrice = p1 + p2 + p4 + 100;

            System.out.println("您购买的水果原始总金额是:" + totalPrice + "元.优惠之后的总金额是:" + finalPrice + "元");
        } else {
            System.out.println("您购买的水果总金额是:" + totalPrice);
        }

        StringBuilder msg = new StringBuilder();//被买走水果个数信息
        for (Integer integer : fruitNum) {
            msg.append(integer).append(",");
        }

        try {
            Socket socket = getSocket();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(Option.FRUIT_BUY + "&" + msg);
            bw.newLine();
            bw.flush();
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //结账完成,把维护被买走的水果个数集合清空
        //把总金额清空
        fruitNum.clear();
        totalPrice = 0;

    }

    //顾客角色购买水果
    private void shopping() {
        fruitNum.clear();
        totalPrice = 0;

        //调用查看请求获取整个集合
        ArrayList<Fruit> allFruits = null;
        try {
            Socket socket = getSocket();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(Option.FRUIT_FINDALL + "&find");
            bw.newLine();
            bw.flush();

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            allFruits = (ArrayList<Fruit>) ois.readObject();

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //如果没东西就结束
        if (allFruits == null || allFruits.size() == 0) {
            return;
        }

        System.out.println("如果您购买的商品总金额超过100元,100-200元部分将有一个9折的优惠~");
        System.out.println("超过200元,200-500元部分将有一个8折的优惠~");
        System.out.println("超过500元,超出500元部分将有一个7折的优惠~");

        //遍历集合,获取每一个水果
        for (Fruit fruit : allFruits) {
            //死循环确保数据合法
            while (true) {
                System.out.println("请输入购买" + fruit.getName() + "的数量:");
                String num = sc.next();

                String s = fruit.toString();

                //id+","+name+","+price+","+unit+","+number;
                String[] split = s.split(",");
                //价格
                String price = split[2];
                //数量
                String number = split[4];

                try {
                    int inputNum = Integer.parseInt(num);
                    if (inputNum < 0) {
                        System.out.println("数量不能为负");
                        continue;
                    } else if (inputNum > Integer.parseInt(number)) {
                        System.out.println("库存不足");
                        continue;
                    }

                    //到这会得到一个大于等于0,小于库存的数字
                    //每买一个水果记录一下购买的个数
                    fruitNum.add(inputNum);
                    //购买个数*价格
                    totalPrice += inputNum * Integer.parseInt(price);
                    break;
                } catch (Exception e) {
                    System.out.println("您输入的数量有误");
                }
            }

        }

        System.out.println("是否要开始结账?  Y(是)/N(否)");
        String option = sc.next();

        if ("y".equalsIgnoreCase(option)) {
            System.out.print("开始结账,计算中");
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print(".");
            }
            System.out.println();
            buy();
        } else {
            System.out.println("tips:重新开始购买会失去记录哦~");
        }
    }

    //管理员登录
    public void adminiStart() {
        System.out.println("请输入用户名:");
        String username = sc.next();
        System.out.println("请输入密码:");
        String password = sc.next();

        try {
            Socket socket = getSocket();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(Option.ADMIN_CHECK + "&" + username + "=" + password);
            bw.newLine();
            bw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String res = br.readLine();

            socket.close();
            if ("true".equals(res)) {
                Date date = new Date();
                String date1 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm E").format(date);
                System.out.println("欢迎您:" + username + "  " + date1);
                //管理员菜单
                managerMarket();
            } else {
                System.out.println("您不是管理员");
                System.exit(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //管理员二级菜单
    private void managerMarket() {
        while (true) {
            System.out.println("请输入您的选择:(1:查看水果     2:添加水果      3:修改水果    4:删除水果    5:切换排序    6.退出)");
            String option = sc.next();

            switch (option) {
                case "1":
                    findAllFruit();
                    break;
                case "2":
                    addFruit();
                    break;
                case "3":
                    updateFruit();
                    break;
                case "4":
                    deleteFruit();
                    break;
                case "5":
                    flag = !flag;
                    System.out.println("切换成功,请重新查看~");
                    break;
                case "6":
                    System.out.println("感谢您的使用~");
                    return;
                default:
                    System.out.println("您的输入有误");
            }
        }
    }

    //controller中查询id是否存在方法
    private boolean idIsExist(String id) {

        boolean res1 = false;
        try {
            Socket socket = getSocket();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(Option.FRUIT_CHECK_ID + "&" + id);
            bw.newLine();
            bw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String res = br.readLine();

            res1 = "true".equals(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res1;
    }

    //controller中更新水果方法
    private void updateFruit() {
        System.out.println("请输入要修改的水果编号:");
        String updateId = sc.next();

        //看一下id是否已经存在
        boolean idres = idIsExist(updateId);

        if (!idres) {
            System.out.println("您输入的编号:" + updateId + "不存在,修改失败.请您重新选择!");
            return;
        }

        //id存在,就用改id重新封装一个水果对象
        Fruit fruit = getFruit(updateId);

        try {
            Socket socket = getSocket();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(Option.FRUIT_UPDATE + "&" + fruit.toString());
            bw.newLine();
            bw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String res = br.readLine();

            System.out.println("true".equals(res) ? "成功修改ID为:" + fruit.getId() + "的水果[" + fruit.getName() +
                    "]每" + fruit.getUnit() + fruit.getPrice() + "元,库存还有--->" + fruit.getNumber() + fruit.getUnit() : "修改失败");

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //controller中删除水果方法
    private void deleteFruit() {
        System.out.println("请输入要删除的水果编号:");
        String delId = sc.next();

        //二次确认
        System.out.println("确定要删除id为:" + delId + "的水果信息吗?  Y(确定)/N(取消)");
        String option = sc.next();
        if ("y".equalsIgnoreCase(option)) {

            try {
                Socket socket = getSocket();

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bw.write(Option.FRUIT_DELETE + "&" + delId);
                bw.newLine();
                bw.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String res = br.readLine();

                System.out.println("true".equals(res) ? "删除成功!" : "您删除的编号不存在,删除失败.请重新你的的选择!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("操作已取消~");
        }
    }

    //controller中查看水果方法
    private void findAllFruit() {
        try {
            Socket socket = getSocket();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(Option.FRUIT_FINDALL + "&find");
            bw.newLine();
            bw.flush();

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            ArrayList<Fruit> allFruits = (ArrayList<Fruit>) ois.readObject();

            if (allFruits.size() != 0) {

                if (flag) {
                    allFruits.sort((o1, o2) -> {
                        //库存降序
                        return o2.getNumber() - o1.getNumber();
                    });
                    System.out.println("<当前为库存降序显示>");
                } else {
                    allFruits.sort((o1, o2) -> {
                        //价格降序
                        return o2.getPrice() - o1.getPrice();
                    });
                    System.out.println("<当前为价格降序显示>");
                }

                System.out.println("编号  名称  价格  单位  库存");

                for (Fruit fruit : allFruits) {
                    System.out.println(fruit.getId() + "  " + fruit.getName() + "  " + fruit.getPrice() + "    " + fruit.getUnit() + "   " + fruit.getNumber());

                }
            } else {
                System.out.println("暂无数据,请添加后再试~");
            }

            ois.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //controller中添加水果的方法
    private void addFruit() {
        System.out.println("请输入要添加的水果编号:");
        String id = sc.next();

        //看一下id是否存在
        boolean idres = idIsExist(id);

        if (idres) {
            System.out.println("不好意思,您输入的水果编号已经存在了.请您重新选择");
            return;
        }

        //不存在的话就封装一个水果对象
        Fruit fruit = getFruit(id);

        try {
            Socket socket = getSocket();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(Option.FRUIT_ADD + "&" + fruit.toString());
            bw.newLine();
            bw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String res = br.readLine();

            System.out.println("true".equals(res) ? "添加成功" : "添加失败");

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //接收一个id,封装一个水果并返回
    private Fruit getFruit(String id) {
        System.out.println("请输入要添加的水果名称:");
        String name = sc.next();

        String price;
        while (true) {
            System.out.println("请输入要添加的水果单价:");
            price = sc.next();
            try {
                int p = Integer.parseInt(price);

                if (p <= 0 || p > 1000) {
                    System.out.println("单价超出范围");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.println("您输入的单价有误");
            }
        }

        System.out.println("请输入要添加的水果单位:");
        String unit = sc.next();

        String number;
        while (true) {
            System.out.println("请输入要添加的水果数量:");
            number = sc.next();
            try {
                int n = Integer.parseInt(number);

                if (n <= 0 || n > 500) {
                    System.out.println("数量超出范围");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("您输入的数量有误");
            }
        }

        int priceInt = Integer.parseInt(price);
        int numberInt = Integer.parseInt(number);

        return new Fruit(id, name, priceInt, unit, numberInt);
    }

    //获取socket方法
    private Socket getSocket() throws IOException {

        return new Socket("127.0.0.1", 10000);
    }
}
