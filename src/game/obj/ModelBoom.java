package game.obj;

public class ModelBoom {
    double size;
    float angle;

    public double getSize(){
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getAngle(){
        return angle;
    }

    public ModelBoom(double size, float angle) {
        this.size = size;
        this.angle = angle;
    }

    public ModelBoom() {    }

}
