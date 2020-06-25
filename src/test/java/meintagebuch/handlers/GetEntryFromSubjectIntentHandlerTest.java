package meintagebuch.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import meintagebuch.PhrasesAndConstants;
import meintagebuch.TestUtil;
import meintagebuch.model.DiaryEntry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GetEntryFromSubjectIntentHandlerTest {

  private GetEntryFromSubjectIntentHandler handler;

  @Before
  public void setup() {
    handler = new GetEntryFromSubjectIntentHandler();
  }

  @Test
  public void testCanHandle() {
    final HandlerInput inputMock = Mockito.mock(HandlerInput.class);
    when(inputMock.matches(any())).thenReturn(true);
    assertTrue(handler.canHandle(inputMock));
  }

  @Test
  public void testHandle() {
    Clock clockYesterday = Clock.fixed(Instant.parse(LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE) + "T" + "12:12:30.00Z"), ZoneId.systemDefault());
    Clock clockBefYesterday = Clock.fixed(Instant.parse(LocalDate.now().minusDays(2).format(DateTimeFormatter.ISO_DATE) + "T" + "12:12:30.00Z"), ZoneId.systemDefault());
    List<DiaryEntry> entries = new ArrayList<>();
    entries.add(new DiaryEntry("Ich war mit Peter plantschen"));
    entries.add(new DiaryEntry("Ich war mit Klaus schwimmen", clockYesterday));
    entries.add(new DiaryEntry("Ich war mit Franz baden", clockBefYesterday));
    System.out.println(entries.get(0).getDate());
    System.out.println(entries.get(1).getDate());
    System.out.println(entries.get(2).getDate());
    final Response response = TestUtil.GetEntryFromSubjectTestForHandle(handler, "gestern", entries);
    String outputSpeak = response.getOutputSpeech().toString();
    System.out.println(response.toString());
    assertTrue(outputSpeak.contains(PhrasesAndConstants.FOUND_MULTIPLE_ENTRIES)
        || outputSpeak.contains(PhrasesAndConstants.THE_ENTRY_IS)
        || outputSpeak.contains(PhrasesAndConstants.NO_ENTRY_WITH_THIS_TAGS));
    assertTrue(response.getOutputSpeech().toString().contains(entries.get(1).getEntryText()));
  }
}