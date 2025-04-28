package tn.artflow.entities;

import java.util.Date;

public class Workshop {
    private int id;
    private String title,description,image,type,location;
    private String date;
    private float latitude,longitude;

    public Workshop() {
    }

   /* public Workshop(String title, String description, String image, String date, String type, String location, float latitude, float longitude) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.date = date;
        this.type = type;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }
*/


    public Workshop(String title, String description, String image, String date, String type, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.date = date;
        this.type = type;
        this.location = location;
    }
    public Workshop(int id, String title, String description, String image, String date, String type, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.date = date;
        this.type = type;
        this.location = location;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override

    public String toString() {
        return "Workshop {\n" +
                "  id=" + id + ",\n" +
                "  title='" + title + "',\n" +
                "  description='" + description + "',\n" +
                "  image='" + image + "',\n" +
                "  date=" + date + ",\n" +
                "  type='" + type + "',\n" +
                "  location='" + location + "',\n" +
                '}';
    }

}