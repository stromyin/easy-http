package demo.codec;

import com.github.firelcw.codec.Decoder;
import com.github.firelcw.model.HttpResponse;

import java.lang.reflect.Type;

/**
 * @author liaochongwei
 * @date 2020/8/4 13:50
 */
    public class CustomDecoder implements Decoder {
        @Override
        public Object decode(HttpResponse response, Type type) {
            return null;
        }
    }