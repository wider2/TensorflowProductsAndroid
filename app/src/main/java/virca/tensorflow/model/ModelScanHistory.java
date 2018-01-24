package virca.tensorflow.model;

public class ModelScanHistory {
 
    public final String cat_id;
    public final String barcode;
    public final String text;
    public String hexColor;
    public Long timestamp;

    public ModelScanHistory(String cat_id, String barcode, String text, String hexColor, Long timestamp) {
        this.cat_id = cat_id;
        this.barcode = barcode;
        this.text = text;
        this.hexColor = hexColor;
        this.timestamp = timestamp;
    }
}