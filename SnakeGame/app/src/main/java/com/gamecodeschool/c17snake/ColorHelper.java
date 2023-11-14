public class ColorHelper {

    public static int getColor(String colorName) {
        switch (colorName) {
            case "BackgroundColor":
                return Color.argb(255, 26, 128, 182);
            case "White":
                return Color.argb(255, 255, 255, 255);
        }
        return 0;
    }
}
