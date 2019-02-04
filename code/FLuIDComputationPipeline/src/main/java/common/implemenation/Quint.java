package common.implemenation;

import common.interfaces.IResource;

import java.sql.Timestamp;

public class Quint extends Quad {

    private Timestamp timestamp;

    public Quint(IResource subject, IResource predicate, IResource object) {
        this(subject, predicate, object, null);
    }

    public Quint(IResource subject, IResource predicate, IResource object, IResource context) {
        super(subject, predicate, object, context);
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Quint(IResource subject, IResource predicate, IResource object, IResource context, Timestamp timestamp) {
        super(subject, predicate, object, context);
        this.timestamp = timestamp;
    }

    @Override
    public Timestamp getTimestamp() {
        return timestamp;
    }
}
