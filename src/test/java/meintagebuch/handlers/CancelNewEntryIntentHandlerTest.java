package meintagebuch.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import meintagebuch.PhrasesAndConstants;
import meintagebuch.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class CancelNewEntryIntentHandlerTest {
  private CancelNewEntryIntentHandler handler;

  @Before
  public void setup() {
    handler = new CancelNewEntryIntentHandler();
  }

  @Test
  public void testCanHandle() {
    final HandlerInput inputMock = Mockito.mock(HandlerInput.class);
    when(inputMock.matches(any())).thenReturn(true);
    assertTrue(handler.canHandle(inputMock));
  }

  @Test
  public void testHandle() {
    final Response response = TestUtil.entryTestForHandle(handler, "schwimmen");
    assertTrue(response.getOutputSpeech().toString().contains(PhrasesAndConstants.SAY_CANCEL_ENTRY));
  }
}
