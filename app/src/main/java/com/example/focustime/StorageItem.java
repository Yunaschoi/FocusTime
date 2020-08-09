package com.example.focustime;

import android.widget.ImageView;

import java.util.Comparator;
import java.util.Date;

public class StorageItem {
    String startTime;   // 시작한 시간을 저장
    String saveTime;   // 타이머 끝난 시간을 저장
    String tryTitle; // 타이머 설정 시간 성공하면 사용자가 임의로 제목 설정 / 임의로 종료하면 실패로 제목 설정
    String createAt;  // 오늘 날짜를 저장
    Date dateTime; // 생성일자 Date 로 저장
  //  ImageView tryTitleRevise;  // 제목 수정


    public StorageItem(String startTime, String saveTime, String tryTitle, String createAt) {
        this.startTime = startTime;
        this.saveTime = saveTime;
        this.tryTitle = tryTitle;
        this.createAt = createAt;

    }


    /*
    public StorageItem(String startTime, String saveTime, String tryTitle) {
        this.startTime = startTime;
        this.saveTime = saveTime;
        this.tryTitle = tryTitle;
    }



    public StorageItem() {

    }

     */


    /*
    public StorageItem(String startTime, String saveTime, String tryTitle) {
        this.startTime = startTime;
        this.saveTime = saveTime;
        this.tryTitle = tryTitle;
      //  this.tryTitleRevise = tryTitleRevise;
    }

     */



    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public StorageItem(String tryTitle) {
        this.tryTitle = tryTitle;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public String getTryTitle() {
        return tryTitle;
    }

    public void setTryTitle(String tryTitle) {
        this.tryTitle = tryTitle;
    }

    /*
    public ImageView getTryTitleRevise() {
        return tryTitleRevise;
    }

    public void setTryTitleRevise(ImageView tryTitleRevise) {
        this.tryTitleRevise = tryTitleRevise;
    }

     */
    // 정렬할 때 사용하는 코드 시작부분
    public static final Comparator<StorageItem> By_TITLE_ASCENDING = new Comparator<StorageItem>() {
        @Override
        public int compare(StorageItem o1, StorageItem o2) {
            return o1.getTryTitle().compareTo(o2.getTryTitle());
        }
    };

    public static final Comparator<StorageItem> By_TITLE_DESCENDING= new Comparator<StorageItem>() {
        @Override
        public int compare(StorageItem o1, StorageItem o2) {
            return o2.getTryTitle().compareTo(o1.getTryTitle());
        }
    };

    /*

    public static final Comparator<StorageItem> By_DATE_ASCENDING = new Comparator<StorageItem>() {  // 날짜시간 순
        @Override
        public int compare(StorageItem t1, StorageItem t2) {
            if (t1.getDateTime() == null || t2.getDateTime() == null)
                return 0;
            return t1.getDateTime().compareTo(t2.getDateTime());
        }
    };

    public static final Comparator<StorageItem> By_DATE_DESCENDING = new Comparator<StorageItem>() {  // 날짜시간 역순
        @Override
        public int compare(StorageItem t1, StorageItem t2) {
            if (t2.getDateTime() == null || t1.getDateTime() == null)
                return 0;

            return t2.getDateTime().compareTo(t1.getDateTime());

        }
    };

     */


}
