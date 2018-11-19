package uk.gov.hmcts.probate.health;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import uk.gov.hmcts.probate.health.BusinessHealthIndicator;

@RunWith(MockitoJUnitRunner.class)
public class BusinessHealthIndicatorTest {

    private static final String URL = "http://url.com";

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private ResponseEntity<String> mockResponseEntity;

    private BusinessHealthIndicator businessHealthIndicator;

    @Before
    public void setUp() {
    	businessHealthIndicator = new BusinessHealthIndicator(URL, mockRestTemplate);
    }

	@Test
    public void shouldReturnStatusOfUpWhenHttpStatusIsOK() {
        when(mockRestTemplate.getForEntity(URL + "/health", String.class)).thenReturn(mockResponseEntity);
        when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        Health health = businessHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.UP));
        assertThat(health.getDetails().get("url"), is(URL));
    }

	@Test
    public void shouldReturnStatusOfDownWhenHttpStatusIsNotOK() {
        when(mockRestTemplate.getForEntity(URL + "/health", String.class)).thenReturn(mockResponseEntity);
        when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        when(mockResponseEntity.getStatusCodeValue()).thenReturn(HttpStatus.NO_CONTENT.value());
        Health health = businessHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is("HTTP Status code not 200"));
        assertThat(health.getDetails().get("exception"), is("HTTP Status: 204"));
    }

    @Test
    public void shouldReturnStatusOfDownWhenResourceAccessExceptionIsThrown() {
        final String message = "EXCEPTION MESSAGE";
        when(mockRestTemplate.getForEntity(URL + "/health", String.class)).thenThrow(new ResourceAccessException(message));

        Health health = businessHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is(message));
        assertThat(health.getDetails().get("exception"), is("ResourceAccessException"));
    }

    @Test
    public void shouldReturnStatusOfDownWhenHttpStatusCodeExceptionIsThrown() {
        when(mockRestTemplate.getForEntity(URL + "/health", String.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        Health health = businessHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is("400 BAD_REQUEST"));
        assertThat(health.getDetails().get("exception"), is("HttpStatusCodeException - HTTP Status: 400"));
    }

    @Test
    public void shouldReturnStatusOfDownWhenUnknownHttpStatusCodeExceptionIsThrown() {
        final String statusText = "status text";
        when(mockRestTemplate.getForEntity(URL + "/health", String.class))
                .thenThrow(new UnknownHttpStatusCodeException(1000, statusText, null, null, null));

        Health health = businessHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is("Unknown status code [1000] status text"));
        assertThat(health.getDetails().get("exception"), is("UnknownHttpStatusCodeException - " + statusText));
    }
}
