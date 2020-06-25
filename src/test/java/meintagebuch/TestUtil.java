package meintagebuch;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.*;
import com.amazon.ask.response.ResponseBuilder;
import meintagebuch.model.DiaryEntry;
import meintagebuch.model.DiarySubject;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TestUtil {

  public static Response testForHandle(RequestHandler handler,
                                       String entry,
                                       String slotName,
                                       Map<String, Object> sessionAttributes,
                                       Map<String, Object> persistentAttributes,
                                       Map<String, Object> requestAttributes,
                                       String confirmation) {

    if (sessionAttributes == null)
      sessionAttributes = new HashMap<>();
    if (persistentAttributes == null)
      persistentAttributes = new HashMap<>();
    if (requestAttributes == null)
      requestAttributes = new HashMap<>();

    final AttributesManager attributesManagerMock = Mockito.mock(AttributesManager.class);
    when(attributesManagerMock.getSessionAttributes()).thenReturn(sessionAttributes);
    when(attributesManagerMock.getPersistentAttributes()).thenReturn(persistentAttributes);
    when(attributesManagerMock.getRequestAttributes()).thenReturn(requestAttributes);

    // Mock Slots
    final RequestEnvelope requestEnvelopeMock = RequestEnvelope.builder()
        .withRequest(IntentRequest.builder()
            .withIntent(Intent.builder()
                .putSlotsItem(slotName, Slot.builder()
                    .withName(slotName)
                    .withValue(entry)
                    .build()).withConfirmationStatus(IntentConfirmationStatus.fromValue(confirmation))
                .build())
            .build())
        .build();


    // Mock Handler input attributes
    final HandlerInput input = Mockito.mock(HandlerInput.class);
    when(input.getAttributesManager()).thenReturn(attributesManagerMock);
    when(input.getResponseBuilder()).thenReturn(new ResponseBuilder());
    when(input.getRequestEnvelope()).thenReturn(requestEnvelopeMock);
    when(input.getRequest()).thenReturn(requestEnvelopeMock.getRequest());

    final Optional<Response> res = handler.handle(input);

    assertTrue(res.isPresent());
    final Response response = res.get();
    assertNotEquals("Test", response.getReprompt());
    assertNotNull(response.getOutputSpeech());
    return response;
  }

  public static Response testForHandle(RequestHandler handler,
                                       String entry1,
                                       String slotName1, String entry2,
                                       String slotName2,
                                       Map<String, Object> sessionAttributes,
                                       Map<String, Object> persistentAttributes,
                                       Map<String, Object> requestAttributes,
                                       String confirmation) {

    if (sessionAttributes == null)
      sessionAttributes = new HashMap<>();
    if (persistentAttributes == null)
      persistentAttributes = new HashMap<>();
    if (requestAttributes == null)
      requestAttributes = new HashMap<>();

    final AttributesManager attributesManagerMock = Mockito.mock(AttributesManager.class);
    when(attributesManagerMock.getSessionAttributes()).thenReturn(sessionAttributes);
    when(attributesManagerMock.getPersistentAttributes()).thenReturn(persistentAttributes);
    when(attributesManagerMock.getRequestAttributes()).thenReturn(requestAttributes);

    // Mock Slots
    final RequestEnvelope requestEnvelopeMock = RequestEnvelope.builder()
        .withRequest(IntentRequest.builder()
            .withIntent(Intent.builder()
                .putSlotsItem(slotName1, Slot.builder()
                    .withName(slotName1)
                    .withValue(entry1)
                    .build()).withConfirmationStatus(IntentConfirmationStatus.fromValue(confirmation))
                .putSlotsItem(slotName2, Slot.builder()
                    .withName(slotName2)
                    .withValue(entry2)
                    .build()).withConfirmationStatus(IntentConfirmationStatus.fromValue(confirmation))
                .build())
            .build())
        .build();


    // Mock Handler input attributes
    final HandlerInput input = Mockito.mock(HandlerInput.class);
    when(input.getAttributesManager()).thenReturn(attributesManagerMock);
    when(input.getResponseBuilder()).thenReturn(new ResponseBuilder());
    when(input.getRequestEnvelope()).thenReturn(requestEnvelopeMock);
    when(input.getRequest()).thenReturn(requestEnvelopeMock.getRequest());

    final Optional<Response> res = handler.handle(input);

    assertTrue(res.isPresent());
    final Response response = res.get();
    assertNotEquals("Test", response.getReprompt());
    assertNotNull(response.getOutputSpeech());
    return response;
  }

  public static HandlerInput mockHandlerInput(String entry,
                                              String slotName,
                                              Map<String, Object> sessionAttributes,
                                              Map<String, Object> persistentAttributes,
                                              Map<String, Object> requestAttributes,
                                              String confirmation) {
    final AttributesManager attributesManagerMock = Mockito.mock(AttributesManager.class);
    when(attributesManagerMock.getSessionAttributes()).thenReturn(sessionAttributes);
    when(attributesManagerMock.getPersistentAttributes()).thenReturn(persistentAttributes);
    when(attributesManagerMock.getRequestAttributes()).thenReturn(requestAttributes);

    // Mock Slots
    final RequestEnvelope requestEnvelopeMock = RequestEnvelope.builder()
        .withRequest(IntentRequest.builder()
            .withIntent(Intent.builder()
                .putSlotsItem(slotName, Slot.builder()
                    .withName(slotName)
                    .withValue(entry)
                    .withConfirmationStatus(SlotConfirmationStatus.fromValue(confirmation))
                    .build())
                .build())
            .build())
        .build();


    // Mock Handler input attributes
    final HandlerInput input = Mockito.mock(HandlerInput.class);
    when(input.getAttributesManager()).thenReturn(attributesManagerMock);
    when(input.getResponseBuilder()).thenReturn(new ResponseBuilder());
    when(input.getRequestEnvelope()).thenReturn(requestEnvelopeMock);

    return input;
  }

  public static Response standardTestForHandle(RequestHandler handler) {
    final Map<String, Object> sessionAttributes = new HashMap<>();
    final Map<String, Object> persistentAttributes = new HashMap<>();
    sessionAttributes.put(PhrasesAndConstants.ENTRYTEXT_KEY_SESSION, "Test");
    persistentAttributes.put(PhrasesAndConstants.ENTRY_KEY_PERSISTANT, "Test");
    final HandlerInput inputMock = TestUtil.mockHandlerInput(null, PhrasesAndConstants.ENTRY_SLOT, sessionAttributes, persistentAttributes, null, null);
    final Optional<Response> res = handler.handle(inputMock);

    assertTrue(res.isPresent());
    final Response response = res.get();

    //assertFalse(response.getShouldEndSession());
    assertNotEquals("Test", response.getReprompt());
    assertNotNull(response.getOutputSpeech());
    return response;
  }

  public static Response entryTestForHandle(RequestHandler handler, String testEntry) {
    final Map<String, Object> sessionAttributes = new HashMap<>();
    final Map<String, Object> persistentAttributes = new HashMap<>();
    sessionAttributes.put(PhrasesAndConstants.ENTRYTEXT_KEY_SESSION, "Test");
    persistentAttributes.put(PhrasesAndConstants.ENTRY_KEY_PERSISTANT, "Test");
    final HandlerInput inputMock = TestUtil.mockHandlerInput(testEntry, PhrasesAndConstants.ENTRY_SLOT, sessionAttributes, persistentAttributes, null, null);
    final Optional<Response> res = handler.handle(inputMock);

    assertTrue(res.isPresent());
    final Response response = res.get();

    //assertFalse(response.getShouldEndSession());
    assertNotEquals("Test", response.getReprompt());
    assertNotNull(response.getOutputSpeech());
    return response;
  }

  public static Response GetEntryFromSubjectTestForHandle(RequestHandler handler, String testSubject, List<DiaryEntry> testEntries) {
    final Map<String, Object> requestAttributes = new HashMap<>();
    requestAttributes.put(PhrasesAndConstants.TAG_SLOT, testSubject);
    final Map<String, Object> persistentAttributes = new HashMap<>();
    List<String> entryIds = new ArrayList<>();
    for (DiaryEntry entry : testEntries) {
      entryIds.add(String.valueOf(entry.getDiaryEntryId()));
      persistentAttributes.put(PhrasesAndConstants.ENTRY_KEY_PERSISTANT + entry.getDiaryEntryId(), entry.getEntryText());
    }
    for (DiaryEntry entry : testEntries) {
      for (DiarySubject subject : entry.getSubjects()) {
        List<String> ids = new ArrayList<>();
        ids.add(entry.getDiaryEntryIdAsString());
        persistentAttributes.put(subject.toString(), ids);
      }
    }
    final HandlerInput inputMock = TestUtil.mockHandlerInput(testSubject, PhrasesAndConstants.TAG_SLOT, persistentAttributes, persistentAttributes, persistentAttributes, null);
    final Optional<Response> res = handler.handle(inputMock);

    assertTrue(res.isPresent());
    final Response response = res.get();

    assertNotNull(response.getOutputSpeech());
    return response;
  }

  public static Response sessionAttributesTestForHandle(RequestHandler handler, String conf) {
    final Map<String, Object> sessionAttributes = new HashMap<>();
    sessionAttributes.put(PhrasesAndConstants.ENTRYTEXT_KEY_SESSION, "schwimmen");
    final HandlerInput inputMock = TestUtil.mockHandlerInput(null, PhrasesAndConstants.ENTRY_SLOT, sessionAttributes, null, null, conf);
    final Optional<Response> res = handler.handle(inputMock);

    assertTrue(res.isPresent());
    final Response response = res.get();

    //in the WhatsMyColorIntentHandler
    //assertTrue(response.getShouldEndSession());
    assertNotNull(response.getOutputSpeech());
    return response;
  }

  public static Response persistentAttributesTestForHandle(RequestHandler handler) {
    final Map<String, Object> persistentAttributes = new HashMap<>();
    persistentAttributes.put(PhrasesAndConstants.ENTRY_KEY_PERSISTANT, "schwimmen");
    final HandlerInput inputMock = TestUtil.mockHandlerInput(null, PhrasesAndConstants.ENTRY_SLOT, null, persistentAttributes, null, null);
    final Optional<Response> res = handler.handle(inputMock);

    assertTrue(res.isPresent());
    final Response response = res.get();

    //in the WhatsMyColorIntentHandler
    //assertTrue(response.getShouldEndSession());
    assertNotNull(response.getOutputSpeech());
    return response;
  }

  public static Response noAttributesTestForHandle(RequestHandler handler) {
    final HandlerInput inputMock = TestUtil.mockHandlerInput(null, PhrasesAndConstants.ENTRY_SLOT, null, new HashMap<>(), null, null);
    final Optional<Response> res = handler.handle(inputMock);

    assertTrue(res.isPresent());
    final Response response = res.get();

    assertFalse(response.getShouldEndSession());
    assertNotNull(response.getOutputSpeech());
    return response;
  }

  public static Response sessionEndedTestForHandle(RequestHandler handler) {
    final HandlerInput inputMock = TestUtil.mockHandlerInput(null, PhrasesAndConstants.ENTRY_SLOT, null, null, null, null);
    final Optional<Response> res = handler.handle(inputMock);

    assertTrue(res.isPresent());
    final Response response = res.get();
    assertTrue(response.getShouldEndSession());
    return response;
  }
}