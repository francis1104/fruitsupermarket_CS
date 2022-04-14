package com.itheima;

import com.itheima.controller.MarketController;

import java.util.Scanner;

public class ClientApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        MarketController controller = new MarketController();
        //一级菜单
        while (true) {
            System.out.println("==============欢迎来到水果超市管理系统==============");
            System.out.println("请输入您的角色:(1:顾客角色     2:管理员角色     3:退出)");

            String option = sc.next();

            switch (option) {
                case "1":
                    //顾客角色
                    controller.customerStart();
                    break;
                case "2":
                    controller.adminiStart();
                    break;
                case "3":
                    System.out.println("欢迎下次使用~~");
                    return;
                default:
                    System.out.println("您的输入有误");
                    break;

            }
        }
    }

}
