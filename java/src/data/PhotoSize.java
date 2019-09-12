package data;

public class PhotoSize {
    public String src;
    public int width;
    public int height;
    public char type;
    private PhotoSizeType typeEnum;

    public PhotoSizeType getTypeEnum(){
        return PhotoSizeType.valueOf((type + ""));
    }
}
