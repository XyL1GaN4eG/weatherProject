package weatherproject.tgbotservice.telegram.commandsTest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.telegram.commands.HelpCommand;
import weatherproject.tgbotservice.utils.Constants;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest {

    @Mock
    private Update update;

    @Mock
    private Message message;

    @Mock
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(123L);
    }

    @Test
    void testHelpCommand() {
        HelpCommand helpCommand = new HelpCommand();
        when(message.getChatId()).thenReturn(123L);
        assertEquals(Constants.HELP_MESSAGE, helpCommand.apply(userDTO, update).getText());
    }
}
