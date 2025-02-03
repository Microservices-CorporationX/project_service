package faang.school.projectservice.service;

import faang.school.projectservice.adapter.VacancyRepositoryAdapter;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceImplTest {

    @InjectMocks
    private VacancyServiceImpl vacancyService;

    @Mock
    private VacancyRepositoryAdapter vacancyRepositoryAdapter;
    @Mock
    private S3Service s3Service;
//    @Mock
//    private MultipartFile multipartFile;

    private static final Long VACANCY_ID = 1L;
    private static final int NUMBER_INVOCATION = 1;

    private Vacancy vacancy;
    private MultipartFile file;
    @BeforeEach
    void setUp(){
        vacancy = new Vacancy();
        vacancy.setId(VACANCY_ID);
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "file.png",
                MediaType.IMAGE_PNG_VALUE,
                "Image".getBytes()
        );

    }

    @Test
    public void testAddCover() {
        when(vacancyRepositoryAdapter.findById(VACANCY_ID)).thenReturn(vacancy);
        vacancyService.addCover(VACANCY_ID, file);
        Mockito.verify(vacancyRepositoryAdapter,times(NUMBER_INVOCATION)).save(vacancy);
    }

    @Test
    public void testGetVacancyCover() {

    }

    @Test
    public void testDeleteVacancyCover() {

    }
}
