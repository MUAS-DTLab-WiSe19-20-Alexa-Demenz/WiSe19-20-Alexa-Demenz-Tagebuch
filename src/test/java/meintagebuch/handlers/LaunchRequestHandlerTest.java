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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class LaunchRequestHandlerTest {
  private LaunchRequestHandler handler;

  @Before
  public void setup() {
    handler = new LaunchRequestHandler();
  }

  @Test
  public void testCanHandle() {
    final HandlerInput inputMock = Mockito.mock(HandlerInput.class);
    when(inputMock.matches(any())).thenReturn(true);
    assertTrue(handler.canHandle(inputMock));
  }

  @Test
  public void testHandle() {
    Response response = TestUtil.noAttributesTestForHandle(handler);
    assertTrue(response.getOutputSpeech().toString().contains(PhrasesAndConstants.WELCOME));
    Map<String, Object> persistentAttributes = new HashMap();
    List<String> tags = new ArrayList();
    tags.add("42");
    tags.add("Test");
    persistentAttributes.put("22.10.1999", tags);
    persistentAttributes.put("13.01.2020", tags);
    response = TestUtil.testForHandle(handler, null, null, null, persistentAttributes, null, null);
    assertTrue(response.getOutputSpeech().toString().contains(PhrasesAndConstants.WELCOME));
  }
}

