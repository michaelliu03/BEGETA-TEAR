package com.kxw.elasticsearch.springboot;

import java.io.IOException;

import com.kxw.elasticsearch.service.ElasticsearchCRUDExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kingsonwu on 18/1/4.
 */
@RestController
@RequestMapping("/springboot/elasticsearch")
public class ElasticsearchController {

    @Autowired
    private ElasticsearchCRUDExampleService elasticsearchCRUDExampleService;

    @RequestMapping(value = "/crud/test", method = RequestMethod.GET)
    public String crudTest() throws IOException {

        //elasticsearchCRUDExampleService.putMapping();
        //elasticsearchCRUDExampleService.savaIndex();
        return elasticsearchCRUDExampleService.query();
    }

}
