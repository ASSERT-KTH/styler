package com.hlj.java9.api.impl;

import com.hlj.java9.api.MyServiceInter;

/**
 * @Desc:
 * @Author HealerJean
 * @Date 2018/10/10  上午10:17.
 */
public class MyServiceInterImpl  implements MyServiceInter {

    @Override
    public void method() {
        System.out.println("接口实现类");
    }


    public static  void staticImpl(){
        System.out.println("接口实现类中自己定义的静态方法");
    }
}
