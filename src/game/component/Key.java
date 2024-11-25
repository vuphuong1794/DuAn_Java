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

    private boolean key_right;
    private boolean key_left;
    private boolean key_up;
    private boolean key_down;
    private boolean mouseLeftClick;
    private boolean key_enter;


}
