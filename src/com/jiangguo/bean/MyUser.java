package com.jiangguo.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

public class MyUser extends BmobChatUser {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // private String headPicture;// ͷ���ļ�
    private Integer phoneNumber;// �绰����
    private BmobRelation goods;// �������Ʒ
    private long QQ;
    private String msg;// ǩ��
    // IM���
    private BmobRelation blogs;// �����Ĳ����б�
    private String sortLetters;// ��ʾ����ƴ��������ĸ
    private Boolean sex = true; // �Ա�-true-�У�Ĭ��ΪŮ
    private Blog blog;
    private BmobGeoPoint location;// ��������
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
