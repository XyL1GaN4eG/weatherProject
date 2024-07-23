package weatherproject.tgbotservice.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import weatherproject.tgbotservice.dto.UserDTO;

import java.util.List;

import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        //TODO: убрать хардкод и вынести в application.yml
        this.baseUrl = "http://localhost:8080/users";
    }

    public List<UserDTO> getAllUsers() {
        ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(baseUrl, UserDTO[].class);
        return List.of(response.getBody());
    }

    public UserDTO getUserById(Long id) {
        String url = baseUrl + "/id?id=" + id;
        return restTemplate.getForObject(url, UserDTO.class);
    }

    public List<UserDTO> getUserByCity(String city) {
        String url = baseUrl + "/city?city=" + city;
        ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(url, UserDTO[].class);
        return List.of(response.getBody());
    }

    public void createUser(UserDTO userDTO) {
        restTemplate.postForObject(baseUrl, userDTO, Void.class);
    }

    public void deleteUser(Long id) {
        String url = baseUrl + "/" + id;
        restTemplate.delete(url);
    }
}
