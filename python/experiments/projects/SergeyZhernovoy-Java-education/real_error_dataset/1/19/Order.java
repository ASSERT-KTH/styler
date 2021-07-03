package ru.szhernovoy.carstore.model;


import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 05.01.2017.
 */
public class Order {

    /**
     * Created by admin on 10.01.2017.
     */
    private List<Image> imageCarList = new CopyOnWriteArrayList<>();
    /**
     * Created by admin on 10.01.2017.
     */
    private Car car;
    /**
     * Created by admin on 10.01.2017.
     */
    private int price;
    /**
     * Created by admin on 10.01.2017.
     */
    private Timestamp release;
    /**
     * Created by admin on 10.01.2017.
     */
    private int milesage;
    /**
     * Created by admin on 10.01.2017.
     */
    private int id;
    /**
     * Created by admin on 10.01.2017.
     */
    private User user;

    /**
     * Created by admin on 10.01.2017.
     */
    private boolean sold;


    /**
     * Created by admin on 10.01.2017.
     */
    public Order() {

    }

    /**
     * Created by admin on 10.01.2017.
     * @return boolean
     */
    public boolean getSold() {
        return sold;
    }

    /**
     * Created by admin on 10.01.2017.
     * @param sold .
     */
    public void setSold(boolean sold) {
        this.sold = sold;
    }

    /**
     * Created by admin on 10.01.2017.
     * @return List<Image>
     */
    public List<Image> getImageCarList() {
        return imageCarList;
    }

    /**
     * Created by admin on 10.01.2017.
     * @param imageCarList .
     */
    public void setImageCarList(List<Image> imageCarList) {
        this.imageCarList = imageCarList;
    }

    /**
     * Created by admin on 14.01.2017.
     * @param image
     */
   public void addImage(Image image){
        this.imageCarList.add(image);
   }


    /**
     * Created by admin on 10.01.2017.
     * @return Car
     */
    public Car getCar() {
        return car;
    }

    /**
     * Created by admin on 10.01.2017.
     * @param car .
     */
    public void setCar(Car car) {
        this.car = car;
    }

    /**
     * Created by admin on 10.01.2017.
     * @return int
     */
    public int getPrice() {
        return price;
    }

    /**
     * Created by admin on 10.01.2017.
     * @param price .
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Created by admin on 10.01.2017.
     * @return Timestamp
     */
    public Timestamp getRelease() {
        return release;
    }

    /**
     * Created by admin on 10.01.2017.
     * @param release .
     */
    public void setRelease(Timestamp release) {
        this.release = release;
    }

    /**
     * Created by admin on 10.01.2017.
     * @return int
     */
    public int getMilesage() {
        return milesage;
    }

    /**
     * Created by admin on 10.01.2017.
     * @param milesage .
     */
    public void setMilesage(int milesage) {
        this.milesage = milesage;
    }

    /**
     * Created by admin on 10.01.2017.
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * Created by admin on 10.01.2017.
     * @param id .
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Created by admin on 10.01.2017.
     * @return User
     */
    public User getUser() {
        return user;
    }

    /**
     * Created by admin on 10.01.2017.
     * @param user .
     */
    public void setUser(User user) {
        this.user = user;
    }
}
