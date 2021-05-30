package com.example.dogInformation;

import android.graphics.Bitmap;

public class PublicItem {
    // 공고종료일, 성별, 중성화여부, 이미지, 특징, 보호소 전화번호
    // 접수일, 발견장소, 품종, 색상,  체중, 공고시작일, 공고번호, 상태
    String age = "";    //나이
    String careNm = ""; //보호소 이름
    String careTel = "";    //보호소 전화번호
    String colorCd = "";    //색상
    String desertionNm = "";    //유기번호
    String happenDt = "";    //접수일
    String happenPlace = "";    //발견장소
    String kindCd = "";    //품종
    String neuterYn = "";    //중성화여부
    String noticeEdt = "";    //공고종료일
    String noticeNo = "";    //공고번호
    String noticeSdt = "";    //공고시작일
    Bitmap image = null;    //이미지
    String sexCd = "";    //성별
    String feature = "";    //특징
    String weight = "";    //체중

    public void setAge(String age) {
        this.age = age;
    }

    public void setCareNm(String careNm) {
        this.careNm = careNm;
    }

    public void setCareTel(String careTel) {
        this.careTel = careTel;
    }

    public void setColorCd(String colorCd) {
        this.colorCd = colorCd;
    }

    public void setDesertionNm(String desertionNm) {
        this.desertionNm = desertionNm;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public void setHappenDt(String happenDt) {
        this.happenDt = happenDt;
    }

    public void setHappenPlace(String happenPlace) {
        this.happenPlace = happenPlace;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setKindCd(String kindCd) {
        this.kindCd = kindCd;
    }

    public void setNeuterYn(String neuterYn) {
        this.neuterYn = neuterYn;
    }

    public void setNoticeEdt(String noticeEdt) {
        this.noticeEdt = noticeEdt;
    }

    public void setNoticeNo(String noticeNo) {
        this.noticeNo = noticeNo;
    }

    public void setNoticeSdt(String noticeSdt) {
        this.noticeSdt = noticeSdt;
    }

    public void setSexCd(String sexCd) {
        this.sexCd = sexCd;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getAge() {
        return age;
    }

    public String getCareNm() {
        return careNm;
    }

    public String getCareTel() {
        return careTel;
    }

    public String getColorCd() {
        return colorCd;
    }

    public String getDesertionNm() {
        return desertionNm;
    }

    public String getFeature() {
        return feature;
    }

    public String getHappenDt() {
        return happenDt;
    }

    public String getHappenPlace() {
        return happenPlace;
    }

    public String getKindCd() {
        return kindCd;
    }

    public String getNeuterYn() {
        return neuterYn;
    }

    public String getNoticeEdt() {
        return noticeEdt;
    }

    public String getNoticeNo() {
        return noticeNo;
    }

    public String getNoticeSdt() {
        return noticeSdt;
    }

    public String getSexCd() {
        return sexCd;
    }

    public String getWeight() {
        return weight;
    }
}
