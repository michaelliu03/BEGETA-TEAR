package com.kxw.drools.sample;

public class User {
    private long user_id;
    private long user_score;
    private int vip_level;
    public User(){}
    public long getUserId(){
        return user_id;
    }

    public void setUserId(long id){
        this.user_id=id;
    }

    public long getUserScore(){
        return user_score;
    }

    public void setUserScore(long score){
        this.user_score=score;
    }

    public int getVipLevel(){
        return this.vip_level;
    }

    public void setVipLevel(int level){
        this.vip_level=level;
    }
}
