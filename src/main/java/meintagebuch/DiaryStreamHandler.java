package meintagebuch;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import meintagebuch.handlers.*;

public class DiaryStreamHandler extends SkillStreamHandler {
  /**
   * Streamhandler uses super.
   */
  public DiaryStreamHandler() {
    super(getSkill());
  }

  private static Skill getSkill() {
    return Skills.standard()
        .addRequestHandlers(
            new LaunchRequestHandler(),
            new CreateNewEntryIntentHandler(),
            new SaveEntryIntentHandler(),
            new CancelNewEntryIntentHandler(),
            new GetEntryFromSubjectIntentHandler(),
            new DeleteEntryIntentHandler(),
            new AddTagsToEntryIntentHandler(),
            new SessionEndedRequestHandler(),
            new StopIntentHandler(),
            new FallbackIntentHandler(),
            new CancelIntentHandler(),
            new NavigateHomeIntentHandler(),
            new HelpIntentHandler())
        .withTableName("meintagebuchData")
        .withAutoCreateTable(true)
        // Add your skill id below
        //.withSkillId("")
        .build();
  }

  /**
   * For testing.
   *
   * @return if skill is valid.
   */
  public boolean isValid() {
    return (getSkill() != null && !getSkill().toString().isEmpty());
  }
}
