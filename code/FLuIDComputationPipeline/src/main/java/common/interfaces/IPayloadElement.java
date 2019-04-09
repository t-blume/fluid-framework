package common.interfaces;

import common.ILocatable;

/**
 * Created by Blume Till on 27.04.2017.
 */
public interface IPayloadElement extends ILocatable {


    void merge(IPayloadElement payloadElement);
}
