package weatherproject.tgbotservice.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.telegram.UserState;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public UserServiceClient(RestTemplate restTemplate, @Value("${user-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<UserDTO> getAllUsers() {
        try {
            ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(baseUrl, UserDTO[].class);
            return List.of(response.getBody());
        } catch (NullPointerException | RestClientException e) {
            log.error("Произошла ошибка при обращении к UserService: {}", e.getMessage());
            return Collections.emptyList();
        } catch (RuntimeException e) {
            log.error("Ошибка при выполнении: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public UserDTO getUserById(Long id) {
        String url = baseUrl + "/id/" + id;
        try {
            return restTemplate.getForObject(url, UserDTO.class);
        } catch (RestClientException e) {
            log.error("Произошла ошибка при обращении к UserService: сервер недоступен.");
            return null;
        }
    }

    public List<UserDTO> getUsersByCity(String city) {
        String url = baseUrl + "/city/" + city.replace(" ", "-");
        try {
            ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(url, UserDTO[].class);
            return List.of(Objects.requireNonNull(response.getBody()));
        } catch (RestClientException | NullPointerException e) {
            log.error("Вернулся пустой список: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void createOrUpdateUser(UserDTO userDTO) {
        restTemplate.postForObject(baseUrl, userDTO, Void.class);
    }

    public void createNewUser(Long chatId) {
        restTemplate.postForObject(baseUrl, new UserDTO(chatId, "null", UserState.START.toString()), Void.class);

    }

    public void deleteUser(Long id) {
        String url = baseUrl + "/" + id;
        try {
            restTemplate.delete(url);

        } catch (RestClientException e) {
            log.error(e.getMessage());
        }
    }
}
