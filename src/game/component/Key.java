package game.component;

public class Key {

    public boolean isKey_right(){
        return key_right;
    }

    public void setKey_right(boolean key_right){
        this.key_right = key_right;
    }

    public boolean isKey_left(){
        return key_left;
    }

    public void setKey_left(boolean key_left){
        this.key_left = key_left;
    }

    public boolean isKey_up() {
        return key_up;
    }
    
    public void setKey_up(boolean key_up) {
        this.key_up = key_up;
    }
    
    public boolean isKey_down() {
        return key_down;
    }
    
    public void setKey_down(boolean key_down) {
        this.key_down = key_down;
    }

    public boolean isMouseLeftClick() {
        return mouseLeftClick;
    }

    public void setMouseLeftClick(boolean mouseLeftClick) {
        this.mouseLeftClick = mouseLeftClick;
    }

    public boolean isKey_enter(){
        return key_enter;
    }

    public void setKey_enter(boolean key_enter){
        this.key_enter = key_enter;
    }

    public boolean isKey_1(){
        return key_1;
    }
    public void setKey_1(boolean key_1){
        setKeyFalse();
        this.key_1 = key_1;
    }
    public boolean isKey_2(){
        return key_2;
    }
    public void setKey_2(boolean key_2){
        setKeyFalse();
        this.key_2 = key_2;
    }
    public boolean isKey_3(){
        return key_3;
    }
    public void setKey_3(boolean key_3){
        setKeyFalse();
        this.key_3 = key_3;
    }
    public boolean isKey_4(){
        return key_4;
    }
    public void setKey_4(boolean key_4){
        setKeyFalse();
        this.key_4 = key_4;
    }

    public void setKeyFalse(){
        key_1=false;
        key_2=false;
        key_3=false;
        key_4=false;
    }
    private boolean key_right;
    private boolean key_left;
    private boolean key_up;
    private boolean key_down;
    private boolean mouseLeftClick;
    private boolean key_enter;
    private boolean key_1;
    private boolean key_2;
    private boolean key_3;
    private boolean key_4;


}
