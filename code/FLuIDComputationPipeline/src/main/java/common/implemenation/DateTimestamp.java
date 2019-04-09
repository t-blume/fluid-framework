package common.implemenation;

import common.ITimestamp;

public class DateTimestamp implements ITimestamp {
    long millicesonds;
    public DateTimestamp(long millicesonds){
        this.millicesonds = millicesonds;
    }
}
