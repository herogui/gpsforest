package com.example.xiaohai.gpsforest.MyEvent;



        import java.util.EventObject;


public class CusEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private Object source;//�¼�Դ

    public CusEvent(Object source){
        super(source);
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
}
