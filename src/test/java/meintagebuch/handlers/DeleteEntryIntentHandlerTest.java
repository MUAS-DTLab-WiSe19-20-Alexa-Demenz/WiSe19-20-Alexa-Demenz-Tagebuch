package meintagebuch.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import meintagebuch.PhrasesAndConstants;
import meintagebuch.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DeleteEntryIntentHandlerTest {

  private DeleteEntryIntentHandler handler;

  @Before
  public void setup() {
    handler = new DeleteEntryIntentHandler();
  }

  @Test
  public void testCanHandle() {
    final HandlerInput inputMock = Mockito.mock(HandlerInput.class);
    when(inputMock.matches(any())).thenReturn(true);
    assertTrue(handler.canHandle(inputMock));
  }


  @Test
  public void testHandle() {
    Map<String, Object> sessionAttributes = new HashMap();
    List<String> tags = new ArrayList();
    tags.add("42");
    sessionAttributes.put(PhrasesAndConstants.ENTRY_TO_EDIT_KEY, tags);
    Map<String, Object> persistentAttributes = new HashMap();
    persistentAttributes.put(PhrasesAndConstants.ENTRY_KEY_PERSISTANT, "Test");
    Map<String, Object> requestAttributes = new HashMap();
    requestAttributes.put(PhrasesAndConstants.ENTRY_TO_EDIT_KEY, "42");

    Response response = TestUtil.testForHandle(handler, "1", PhrasesAndConstants.ENTRY_NR_SLOT, sessionAttributes, persistentAttributes, requestAttributes,
        "CONFIRMED");
    assertTrue(response.getOutputSpeech().toString().contains(PhrasesAndConstants.DELETED_ENTRY));
    assertTrue(response.getShouldEndSession());

    response = TestUtil.testForHandle(handler, "1", PhrasesAndConstants.ENTRY_NR_SLOT, null, persistentAttributes, requestAttributes,
        "CONFIRMED");
    assertTrue(response.getOutputSpeech().toString().contains(PhrasesAndConstants.ASK_FOR_ENTRIES_FIRST));
    assertFalse(response.getShouldEndSession());


    response = TestUtil.testForHandle(handler, "1", PhrasesAndConstants.SUBJECT_SLOT, sessionAttributes, persistentAttributes, requestAttributes,
        "DENIED");
    assertTrue(response.getOutputSpeech().toString().contains(PhrasesAndConstants.NOT_DELETED));
    assertFalse(response.getShouldEndSession());
  }

}