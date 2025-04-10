package ticket.booking.entities;

import java.util.List;
import java.util.Map;

public class Train {
    private String trainId;
    private String trainNo;
    private List<List<Integer>> seats;
    private Map<String, String> stationTimes;
    private List<String> stations;
    private String trainInfo;

    public Train() {
    }

    public Train(String trainId, String trainNo, List<List<Integer>> seats, Map<String, String> stationTimes,
            List<String> stations, String trainInfo) {
        this.trainId = trainId;
        this.trainNo = trainNo;
        this.seats = seats;
        this.stationTimes = stationTimes;
        this.stations = stations;
        this.trainInfo = trainInfo;
    }

    public List<String> getStations() {
        return this.stations;
    }

    public List<List<Integer>> getSeats() {
        return this.seats;
    }

    public void setSeats(List<List<Integer>> seats) {
        this.seats = seats;
    }

    public String getTrainId() {
        return this.trainId;
    }

    public Map<String, String> getStationTimes() {
        return this.stationTimes;
    }

    public String getTrainNo() {
        return this.trainNo;
    }

    public String getTrainInfo() {
        return String.format("Train ID: %s Train No: %s", trainId, trainNo);
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public void setStationTimes(Map<String, String> stationTimes) {
        this.stationTimes = stationTimes;
    }

    public void setStations(List<String> stations) {
        this.stations = stations;
    }

    public void setTrainInfo(String trainId, String trainNo) {
        this.trainId = trainId;
        this.trainNo = trainNo;
    }
}
