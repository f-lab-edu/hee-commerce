package com.hcommerce.heecommerce.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {
    private static Logger log = LoggerFactory.getLogger(RedisController.class);

    @Autowired
    Publisher publisher;

    @PostMapping("/publisher")
    public void publish(@RequestBody String message){
        log.info(">> Publishing : {} " + message);
        publisher.publisher(message);
    }
}
