package objects;

import android.graphics.Color;

public class SettingsObject {

    private int nightMode ;
    private String font ;
    private String bgColor ;
    private String textColor ;
    private int fontSize ;

    private AvatarObject AvatarObject ;



    public int getNightMode() {
        return nightMode;
    }

    public String getFont() {
        return font;
    }


    public int getBgColor() {
        try {
            if(!bgColor.contains("#"))
                    bgColor = "#"+bgColor;
            return  Color.parseColor(bgColor);
        }catch (Exception e) {
            return Color.WHITE;
        }

    }

      public int getTextColor() {
        try {
            if(!textColor.contains("#"))
                textColor = "#"+textColor;
            return  Color.parseColor(textColor);
        }catch (Exception e) {
            return Color.BLACK;
        }

    }

    public int getFontSize() {
        return fontSize;
    }

    public objects.AvatarObject getAvatarObject() {
        return AvatarObject;
    }
}
