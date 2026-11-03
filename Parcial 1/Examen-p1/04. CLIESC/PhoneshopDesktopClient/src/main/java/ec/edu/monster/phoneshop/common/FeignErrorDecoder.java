package ec.edu.monster.phoneshop.common;

import com.google.gson.Gson;
import ec.edu.monster.phoneshop.dto.ExceptionMessageDto;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, feign.Response response) {
        ExceptionMessageDto message;

        try {
            message = new Gson().fromJson(response.body().asReader(), ExceptionMessageDto.class);
            System.out.println("\nError: " + message.getDetail() + "\n");
            return new ApiServerException(message);
        } catch (Exception e) {
            return new Exception("Error Interno del Servidor");
        }
    }
}
