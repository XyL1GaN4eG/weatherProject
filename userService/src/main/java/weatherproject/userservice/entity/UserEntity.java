package weatherproject.userservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//строчка чтобы гитхаб дескстоп увидел изменения
@Entity
@Table(name = "tguser")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @Column(name = "chat_id")
    private Long chatId;
    private String city;
    private String state;
}