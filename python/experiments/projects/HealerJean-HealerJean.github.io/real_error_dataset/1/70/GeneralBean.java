package com.hlj.proj.dto;

import com.hlj.proj.utils.JsonUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HealerJean
 * @version 1.0v
 * @ClassName GeneralBean
 * @date 2019-08-04  15:35.
 * @Description
 */
@Data
@Accessors(chain = true)
public class GeneralBean {

    private String username;

    private String sex;

    private Company company;

    private List<String> cars;

    private List<Friend> friends;


    public static void main(String[] args) {
        GeneralBean generalBean = new GeneralBean();
        generalBean.setUsername("HealerJean");
        generalBean.setSex("男");
        List<String> cars = new ArrayList<>();
        cars.add("奔驰");
        cars.add("宝马");
        generalBean.setCars(cars);

        Company company = new Company();
        company.setCompanyName("梦想之都公司");
        company.setAddress("北京海淀");
        generalBean.setCompany(company);


        Friend friendOne = new Friend();
        friendOne.setFriendName("小明");
        friendOne.setFriendSex("男");
        Friend friendTwo = new Friend();
        friendTwo.setFriendName("小花");
        friendTwo.setFriendSex("女");
        List<Friend> friends = new ArrayList<>();
        friends.add(friendOne);
        friends.add(friendTwo);
        generalBean.setFriends(friends) ;

        System.out.println(JsonUtils.toJsonString(generalBean));



    }

}

@Data
class Company {
    private String companyName;
    private String address;
}

@Data
class Friend {
    private String friendName;
    private String friendSex;
}


