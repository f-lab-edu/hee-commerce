package com.hcommerce.heecommerce.order;

public class RedisLockTryInterruptedException extends RuntimeException {

    public RedisLockTryInterruptedException(Throwable cause) {
        super("현재 접속자가 몰려 서비스 이용이 불가능합니다. 잠시 후 다시 시도해주세요.", cause);
    }
}
