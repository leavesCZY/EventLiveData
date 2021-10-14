package github.leavesc.eventlivedata;

/**
 * @Author: leavesC
 * @Date: 2021/10/14 13:27
 * @Desc:
 * @Github：https://github.com/leavesC
 */
public class EventLiveData<T> extends AbstractLiveData<T> {

    public EventLiveData(T value) {
        super(value);
    }

    public EventLiveData() {
        super();
    }

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }

}
