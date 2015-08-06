package com.jiangguo.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

public class MyUser extends BmobChatUser {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // private String headPicture;// 头像文件
    private Integer phoneNumber;// 电话号码
    private BmobRelation goods;// 发表的物品
    private long QQ;
    private String msg;// 签名
    // IM相关
    private BmobRelation blogs;// 发布的博客列表
    private String sortLetters;// 显示数据拼音的首字母
    private Boolean sex = true; // 性别-true-男，默认为女
    private Blog blog;
    private BmobGeoPoint location;// 地理坐标
    private Integer hight;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getQQ() {
        return QQ;
    }

    public void setQQ(long qQ) {
        QQ = qQ;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BmobRelation getGoods() {
        return goods;
    }

    public void setGoods(BmobRelation goods) {
        this.goods = goods;
    }

    public BmobRelation getBlogs() {
        return blogs;
    }

    public void setBlogs(BmobRelation blogs) {
        this.blogs = blogs;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public Integer getHight() {
        return hight;
    }

    public void setHight(Integer hight) {
        this.hight = hight;
    }

}
