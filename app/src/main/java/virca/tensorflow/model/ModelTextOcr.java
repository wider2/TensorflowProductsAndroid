package virca.tensorflow.model;

public class ModelTextOcr {
 
    public final String barcode;
    public final String text;
 
    public ModelTextOcr(String barcode, String text) {
        this.barcode = barcode;
        this.text = text;
    }
}