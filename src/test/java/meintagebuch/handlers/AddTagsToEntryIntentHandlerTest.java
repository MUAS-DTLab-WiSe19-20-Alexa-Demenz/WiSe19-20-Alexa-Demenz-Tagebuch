package meintagebuch.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import meintagebuch.PhrasesAndConstants;
import meintagebuch.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AddTagsToEntryIntentHandlerTest {
  private AddTagsToEntryIntentHandler handler;

  @Before
  public void setup() {
    handler = new AddTagsToEntryIntentHandler();
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
    Response response = TestUtil.testForHandle(handler, "1", PhrasesAndConstants.ENTRY_NR_SLOT, "42", PhrasesAndConstants.SUBJECT_SLOT, sessionAttributes, persistentAttributes, requestAttributes,
        null);
    assertTrue(response.getOutputSpeech().toString().contains(PhrasesAndConstants.ADDED_NEW_SUBJECTS));
    assertTrue(response.getShouldEndSession());
    response = TestUtil.noAttributesTestForHandle(handler);
    assertTrue(response.getOutputSpeech().toString().contains(PhrasesAndConstants.ASK_FOR_ENTRIES_FIRST));
    assertFalse(response.getShouldEndSession());

    persistentAttributes = new HashMap();
    tags.add("Test");
    persistentAttributes.put("42", tags);
    response = TestUtil.testForHandle(handler, "1", PhrasesAndConstants.ENTRY_NR_SLOT, "42", PhrasesAndConstants.SUBJECT_SLOT, sessionAttributes, persistentAttributes, requestAttributes,
        null);
    assertTrue(response.getOutputSpeech().toString().contains(PhrasesAndConstants.ADDED_NEW_SUBJECTS));
  }
}
