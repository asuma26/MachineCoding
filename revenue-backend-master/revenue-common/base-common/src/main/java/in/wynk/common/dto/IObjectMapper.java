package in.wynk.common.dto;

import in.wynk.exception.WynkRuntimeException;

public interface IObjectMapper {

     static <R, T> R from(T item) {
          throw new WynkRuntimeException("Method is not implemented!");
     }

}
