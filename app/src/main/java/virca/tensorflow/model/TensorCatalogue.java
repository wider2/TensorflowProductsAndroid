package virca.tensorflow.model;

public class TensorCatalogue {
 
    public final String id;
    public final String group_id;
    public final String category_eng;
    public final String category_rus;
 
    public TensorCatalogue(String id, String group_id, String category_eng, String category_rus) {
        this.id = id;
        this.group_id = group_id;
        this.category_eng = category_eng;
        this.category_rus = category_rus;
    }
}