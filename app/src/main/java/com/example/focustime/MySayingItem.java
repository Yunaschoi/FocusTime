package com.example.focustime;

import java.util.Comparator;
import java.util.Date;

public class MySayingItem {


    private String title;
    private String memo;
    private String publisher;   // 작성자
    private Date createAt;  // 날짜

    private String memoUri;


    public MySayingItem(String title, String memo, String publisher, Date createAt, String memoUri) {
        this.title = title;
        this.memo = memo;
        this.publisher = publisher;
        this.createAt = createAt;
        this.memoUri = memoUri;
    }


    public MySayingItem(String title, String memo, String publisher, Date createAt) {
        this.title = title;
        this.memo = memo;
        this.publisher = publisher;
        this.createAt = createAt;
    }





    public String getMemoUri() {
        return memoUri;
    }

    public void setMemoUri(String memoUri) {
        this.memoUri = memoUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    /*
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

     */

    // 정렬할 때 사용하는 코드 시작부분
    public static final Comparator<MySayingItem> By_TITLE_ASCENDING = new Comparator<MySayingItem>() {   // 가나다 순
        @Override
        public int compare(MySayingItem o1, MySayingItem o2) {
            return o1.getTitle().compareTo(o2.getTitle());
        }
    };

    public static final Comparator<MySayingItem> By_TITLE_DESCENDING= new Comparator<MySayingItem>() {   // 가나다 역순
        @Override
        public int compare(MySayingItem o1, MySayingItem o2) {
            return o2.getTitle().compareTo(o1.getTitle());
        }
    };

    public static final Comparator<MySayingItem> By_DATE_ASCENDING = new Comparator<MySayingItem>() {  // 날짜 와 시간순
        @Override
        public int compare(MySayingItem t1, MySayingItem t2) {
            if (t1.getCreateAt() == null || t2.getCreateAt() == null)
                return 0;
            return t1.getCreateAt().compareTo(t2.getCreateAt());
        }
    };

    public static final Comparator<MySayingItem> By_DATE_DESCENDING = new Comparator<MySayingItem>() {  // 날짜 시간 역순
        @Override
        public int compare(MySayingItem t1, MySayingItem t2) {
            if (t2.getCreateAt() == null || t1.getCreateAt() == null)
                return 0;

            return t2.getCreateAt().compareTo(t1.getCreateAt());

        }
    };

}
