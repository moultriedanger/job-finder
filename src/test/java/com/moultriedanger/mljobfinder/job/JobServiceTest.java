package com.moultriedanger.mljobfinder.job;

import com.moultriedanger.mljobfinder.company.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class JobServiceTest {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void testListAllJobs(){


    }


}
