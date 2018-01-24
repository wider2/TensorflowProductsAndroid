package virca.tensorflow.model;

public class TensorGroup {
 
    public final String group_id;
    public final String group_eng;
    public final String group_rus;

    public TensorGroup(String group_id, String group_eng, String group_rus) {
        this.group_id = group_id;
        this.group_eng = group_eng;
        this.group_rus = group_rus;
    }
}