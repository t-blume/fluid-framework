package utils.implementation;


import common.interfaces.ILocatable;
import common.interfaces.IResource;

/**
 * Created by Blume Till on 08.08.2016.
 */
public class StatEntry implements ILocatable {
    private String key;
    private Integer value;

    public StatEntry(String key, Integer value) {
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public IResource getLocator() {
        return new IResource() {
            @Override
            public String toN3() {
                return key;
            }
        };
    }
}
