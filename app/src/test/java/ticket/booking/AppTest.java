package ticket.booking;

import org.junit.Test;
import java.io.*;

public class AppTest {

    @Test
    public void testAppRuns() {
        // Simulate user input: here "7" to trigger "Exit the App" immediately
        String simulatedInput = "7\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        // Run the main method
        String[] args = {};
        App.main(args);

        // Simple assertion to confirm the method runs (can be extended for actual output tests)
        assert true;
    }
}
