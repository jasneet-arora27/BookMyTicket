package ticket.booking.services;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.User;
import ticket.booking.entities.Train;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserBookingService {

    private User user;
    private List<User> userList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_PATH = "data/users.json"; // Relative to resources

    public List<User> loadUsers() throws IOException {
        File file = new File(USERS_PATH);
        if (!file.exists()) {
            return new ArrayList<>(); // Return empty list if file doesn't exist
        }
        return objectMapper.readValue(file, new TypeReference<List<User>>() {
        });
    }

    public UserBookingService() throws IOException {
        userList = loadUsers();
    }

    public UserBookingService(User user1) throws IOException {
        this.user = user1;
        userList = loadUsers();
    }

    public Boolean loginUser() {
        Optional<User> foundUser = userList.stream()
                .filter(user1 -> user1.getName().equals(user.getName())
                        && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword()))
                .findFirst();

        if (foundUser.isPresent()) {
            // Replace internal user object with fully populated one
            this.user = foundUser.get();
            return true;
        }

        return false;
    }

    public Boolean signUp(User user1) {
        try {
            // Check for existing user
            boolean userExists = userList.stream()
                    .anyMatch(u -> u.getName().equals(user1.getName()));
            if (userExists) {
                System.out.println("User already exists!");
                return false;
            }

            // Hash password
            String hashed = UserServiceUtil.hashPassword(user1.getPassword());
            user1.setHashedPassword(hashed);

            // Assign user ID (e.g. auto-incrementing)
            user1.setUserId("U" + (1000 + userList.size() + 1));

            userList.add(user1);
            saveUserListToFile();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void fetchBookings() {
        if (user != null) {
            user.printTickets();
        } else {
            System.out.println("Sorry! You don't have any bookings!");
        }
    }

    public Boolean cancelBooking(String ticketId) {
        if (user == null) {
            System.out.println("No user is logged in.");
            return Boolean.FALSE;
        }

        Optional<Ticket> ticketToCancel = user.getTicketsBooked().stream()
                .filter(ticket -> ticket.getTicketId().equals(ticketId))
                .findFirst();

        if (ticketToCancel.isPresent()) {
            user.getTicketsBooked().remove(ticketToCancel.get());

            try {
                saveUserListToFile();
                return Boolean.TRUE;
            } catch (IOException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }

        return Boolean.FALSE;
    }

    public List<Train> getTrains(String source, String destination) {
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        } catch (IOException ex) {
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train) {
        return train.getSeats();
    }

    private void saveUserListToFile() throws IOException {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs(); // Create data folder if it doesn't exist
        }
        objectMapper.writeValue(new File(USERS_PATH), userList);
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) {
        try {
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();

            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    // Step 1: Mark the seat as booked
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);

                    // Step 2: Generate a ticket
                    String ticketId = "T" + System.currentTimeMillis();
                    Ticket ticket = new Ticket(
                            ticketId,
                            user.getUserId(),
                            train.getStations().get(0), // assuming source is first
                            train.getStations().get(train.getStations().size() - 1), // assuming destination is last
                            java.time.LocalDate.now().toString(),
                            train);

                    // Step 3: Add to user's ticketsBooked
                    user.getTicketsBooked().add(ticket);

                    // Step 4: Save user list to file
                    saveUserListToFile();

                    System.out.println("Ticket Booked! Ticket ID: " + ticketId);
                    return true;
                } else {
                    System.out.println("Seat already booked.");
                    return false;
                }
            } else {
                System.out.println("Invalid row or seat number.");
                return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public User getUser() {
        return user;
    }

}
