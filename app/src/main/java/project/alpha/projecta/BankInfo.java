package project.alpha.projecta;

import android.graphics.drawable.Drawable;

/**
 * Created by Christian on 2015-05-31.
 */

public class BankInfo {
    private String title;
    String thumbnailUrl;
    private String year;
    private String ownerInfo;
    private Drawable theDraw;


    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


    public String getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(String ownerInfo) {
        this.ownerInfo = ownerInfo;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Drawable getTheDraw() {
        return theDraw;
    }

    public void setTheDraw(Drawable theDraw) {
        this.theDraw = theDraw;
    }

}



