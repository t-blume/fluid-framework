package common.implemenation;

import common.IResource;
import common.ITimestamp;
import common.implementation.Quad;


public class Quint extends Quad {

    private ITimestamp timestamp;

    public Quint(IResource subject, IResource predicate, IResource object) {
        this(subject, predicate, object, null);
    }

    public Quint(IResource subject, IResource predicate, IResource object, IResource context) {
        super(subject, predicate, object, context);
        this.timestamp = new DateTimestamp(System.currentTimeMillis());
    }

    public Quint(IResource subject, IResource predicate, IResource object, IResource context, ITimestamp timestamp) {
        super(subject, predicate, object, context);
        this.timestamp = timestamp;
    }

    @Override
    public ITimestamp getTimestamp() {
        return timestamp;
    }
}
