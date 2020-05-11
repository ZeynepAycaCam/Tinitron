package cf.infinitus.tinitron.users.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Primary
    @Bean
    public void firebaseInit() throws IOException {
        Resource resource = new ClassPathResource("tinitron-fa0b0-firebase-adminsdk-asrvc-3772cfa7ce.json");
        InputStream serviceAccount = resource.getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://tinitron-fa0b0.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
    }
}
