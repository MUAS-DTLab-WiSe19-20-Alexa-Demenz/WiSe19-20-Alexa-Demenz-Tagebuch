package meintagebuch.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import meintagebuch.PhrasesAndConstants;
import meintagebuch.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class SaveEntryIntentHandlerTest {
  private SaveEntryIntentHandler handler;

  @Before
  public void setup() {
    handler = new SaveEntryIntentHandler();
  }

  @Test
  public void testCanHandle() {
    final HandlerInput inputMock = Mockito.mock(HandlerInput.class);
    when(inputMock.matches(any())).thenReturn(true);
    assertTrue(handler.canHandle(inputMock));
  }

  @Test
  public void testHandle() {
    Response response = TestUtil.testForHandle(handler, null, null, null, null, null, null);
    assertTrue(response.getOutputSpeech().toString().contains(PhrasesAndConstants.SAY_MAKE_ENTRY_FIRST));
    Map<String, Object> sessionAttributes = new HashMap();
    sessionAttributes.put(PhrasesAndConstants.ENTRYTEXT_KEY_SESSION, "Test123");
    handler = new SaveEntryIntentHandler();
    response = TestUtil.testForHandle(handler, "Test1 Test2 Test3", PhrasesAndConstants.SUBJECT_SLOT, sessionAttributes, null, null, PhrasesAndConstants.CONFIRMATION_STATUS_SLOT_CONFIRMED);
    assertTrue(response.getOutputSpeech().toString().contains("Test1"));
    assertTrue(response.getOutputSpeech().toString().contains("Test2"));
    assertTrue(response.getOutputSpeech().toString().contains("Test3"));
    assertTrue(response.getOutputSpeech().toString().contains("Test123"));
    response = TestUtil.testForHandle(handler, "Test1 Test2 Test3", PhrasesAndConstants.SUBJECT_SLOT, sessionAttributes, null, null, PhrasesAndConstants.CONFIRMATION_STATUS_SLOT_DENIED);
    assertTrue(response.getOutputSpeech().toString().contains(PhrasesAndConstants.SAY_CANCEL_SUBJECTS));
    assertTrue(response.getOutputSpeech().toString().contains("Test123"));
  }
}
