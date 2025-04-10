package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();

    public TrainService() throws IOException {
        // Load trains.json from resources folder
        URL resourceUrl = getClass().getClassLoader().getResource("trains.json");

        if (resourceUrl == null) {
            throw new IOException("trains.json not found in resources folder!");
        }

        File trainsFile = new File(URLDecoder.decode(resourceUrl.getFile(), StandardCharsets.UTF_8));
        trainList = objectMapper.readValue(trainsFile, new TypeReference<List<Train>>() {
        });

        // Print number of trains and some basic info
        System.out.println("Loaded Trains: " + trainList.size());
        for (Train train : trainList) {
            System.out.println(train.getTrainInfo());
            System.out.println("Stations: " + train.getStations());
        }
    }

    public List<Train> searchTrains(String source, String destination) {
        List<Train> matchingTrains = new ArrayList<>();

        for (Train train : trainList) {
            List<String> stations = train.getStations();

            int sourceIndex = -1;
            int destinationIndex = -1;

            for (int i = 0; i < stations.size(); i++) {
                if (stations.get(i).equalsIgnoreCase(source)) {
                    sourceIndex = i;
                }
                if (stations.get(i).equalsIgnoreCase(destination)) {
                    destinationIndex = i;
                }
            }

            if (sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex) {
                matchingTrains.add(train);
            }
        }

        return matchingTrains;
    }

    public void addTrain(Train newTrain) {
        Optional<Train> existingTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();

        if (existingTrain.isPresent()) {
            updateTrain(newTrain);
        } else {
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }

    public void updateTrain(Train updatedTrain) {
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
        } else {
            addTrain(updatedTrain);
        }
    }

    private void saveTrainListToFile() {
        try {
            // Save using same path again (optional; can customize)
            URL resourceUrl = getClass().getClassLoader().getResource("trains.json");
            if (resourceUrl == null) {
                throw new IOException("trains.json not found while saving.");
            }
            File trainsFile = new File(URLDecoder.decode(resourceUrl.getFile(), StandardCharsets.UTF_8));
            objectMapper.writeValue(trainsFile, trainList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validTrain(Train train, String source, String destination) {
        List<String> stationOrder = train.getStations().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        source = source.toLowerCase();
        destination = destination.toLowerCase();

        int sourceIndex = stationOrder.indexOf(source);
        int destinationIndex = stationOrder.indexOf(destination);

        return sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;
    }
}
