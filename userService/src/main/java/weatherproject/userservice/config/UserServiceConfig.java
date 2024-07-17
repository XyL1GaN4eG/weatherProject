package weatherproject.userservice.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import weatherproject.userservice.repository.UserRepository;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@EnableTransactionManagement
@PropertySource("classpath:application.yml") // Подключаем файл с настройками
public class UserServiceConfig {

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username}")
    private String dataSourceUsername;

    @Value("${spring.datasource.password}")
    private String dataSourcePassword;

    @Value("${spring.jpa.database-platform}")
    private String databasePlatform;

    @Value("${spring.datasource.hikari.allow-pool-suspension}")
    private boolean allowPoolSuspension;

    @Value("${spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation}")
    private boolean nonContextualLobCreation;

    @Value("${spring.jpa.properties.hibernate.format_sql}")
    private boolean formatSql;

    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dataSourceUrl);
        hikariConfig.setUsername(dataSourceUsername);
        hikariConfig.setPassword(dataSourcePassword);
        hikariConfig.setAllowPoolSuspension(allowPoolSuspension);

        return new HikariDataSource(hikariConfig);
    }

    // Другие настройки, бины и конфигурации могут быть добавлены сюда

}
