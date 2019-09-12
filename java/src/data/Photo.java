package data;

public class Photo extends Media {
    public int id;
    public int album_id;
    public int owner_id;
    public int user_id;
    public String text;
    public int date;
    public PhotoSize[] sizes;
    public int width;
    public int height;
}
