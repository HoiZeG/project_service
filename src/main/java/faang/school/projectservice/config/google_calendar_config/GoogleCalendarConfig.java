package faang.school.projectservice.config.google_calendar_config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import faang.school.projectservice.exceptions.google_calendar.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class GoogleCalendarConfig {
    private static final String APPLICATION_NAME = "CorpX";
    private static final String ACCESS_TYPE_OFFLINE = "offline";
    private static final String USER_KEY = "user";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    private final DataStoreFactory dataStoreFactory;

    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow() throws GeneralSecurityException, IOException {
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets()
                .setWeb(new GoogleClientSecrets.Details()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret));

        return new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                Arrays.asList(CalendarScopes.CALENDAR, CalendarScopes.CALENDAR_EVENTS))
                .setAccessType(ACCESS_TYPE_OFFLINE)
                .setDataStoreFactory(dataStoreFactory)
                .build();
    }

    @Bean
    @Lazy
    public Calendar googleCalendarClient(GoogleAuthorizationCodeFlow flow) throws IOException, GeneralSecurityException {
        log.info("Configuring Google Calendar client");

        Credential credential = flow.loadCredential(USER_KEY);

        if (credential == null) {
            String errorMsg = "OAuth tokens are missing. Please authorize the application.";
            log.error(errorMsg);
            throw new UnauthorizedException(errorMsg);
        }

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}