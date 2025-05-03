package edu.eci.cvds.prometeo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;
import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        
        String host = getValue(dotenv, "NEON_HOST", "localhost:5432");
        String database = getValue(dotenv, "NEON_DATABASE", "postgres");
        String username = getValue(dotenv, "NEON_USERNAME", "postgres");
        String password = getValue(dotenv, "NEON_PASSWORD", "postgres");
        
        String url = "jdbc:postgresql://" + host + "/" + database + "?sslmode=require";
        
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
    
    private String getValue(Dotenv dotenv, String key, String defaultValue) {
        String value = dotenv.get(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}