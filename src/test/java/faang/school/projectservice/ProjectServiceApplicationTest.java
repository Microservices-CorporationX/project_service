package faang.school.projectservice;

import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(properties = "spring.profiles.active=test")
public class ProjectServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
    }

    @Test
    public void checkSpecificBean() {
        boolean beanExists = applicationContext.containsBean("vacancyRepository");
        System.out.println("Bean found: " + beanExists);
        VacancyRepository myRepository = applicationContext.getBean(VacancyRepository.class);
        System.out.println(myRepository);

        Object vacancyRepositoryBean = applicationContext.getBean("vacancyRepository");
        System.out.println(vacancyRepositoryBean.getClass().getName());

        TeamMemberRepository teamMemberRepositoryBean = applicationContext.getBean(TeamMemberRepository.class);
        System.out.println(teamMemberRepositoryBean);
    }
}
