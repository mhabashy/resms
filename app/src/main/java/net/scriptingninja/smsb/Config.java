package net.scriptingninja.smsb;

/**
 * Created by michaelhabashy on 2/2/17.
 */

public class Config {
    private String basicHttp = "bGF3cHJhY3RpY2U6aGFiYXNoeWhhYmFzaHk=";

    public String getBasicHttp(){
        return basicHttp;
    }

    public interface OnTaskCompleted{
        void onTaskCompleted();
    }

    private String androidKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArNEaGqvCT4RujyHFItQCwAN45q2BacYYCcBs3xjmNoC21s9oBj1zHHPPZWn0Ya3ldcPrxVJUlXa6zKggvkUY9VrREJ9ZvfWd7a723V4YF+gBL227kxkFKV+u9+nntwSyVnQjNiLj4G5k0ZJ3wdOxdSanKmqrj/rxBw+HoidzCEsFuSPbJHR8LsU2R/h68OXDFEr/2EEzW8Y1VMaRYc5NOGlslhzY3CyXb4CLjo4qTvSFoVh43V/WnysnKCrlPZ9QhCDAdJ5pm70/yjYFT7DiOkCuGNAnPimjBtGP7WV8zkMQs3kG3UFfeIWhpKElZMvyZ5hZAAXeCVRvV/mJqlzfSQIDAQAB";
}
