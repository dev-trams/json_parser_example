package com.sample.myapplication;

public class PersonalData {
    private String karaoke_id;
    private String karaoke_num;
    private String karaoke_title;

    //MainActivity -ArrayList 에 데이터를 저장하기 위해 사용
    //UsersAdapter - ArrayList 에 있는 데이터를 RecyclerView 에 보여줄 때 사용

    public String getKaraoke_id() {
        return karaoke_id;
    }

    public void setKaraoke_id(String karaoke_id) {
        this.karaoke_id = karaoke_id;
    }

    public String getKaraoke_num() {
        return karaoke_num;
    }

    public void setKaraoke_num(String karaoke_num) {
        this.karaoke_num = karaoke_num;
    }

    public String getKaraoke_title() {
        return karaoke_title;
    }

    public void setKaraoke_title(String karaoke_title) {
        this.karaoke_title = karaoke_title;
    }
}
