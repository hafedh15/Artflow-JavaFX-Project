package tn.artflow.entities;


import java.util.Date;

public class Reservation {
    private int id;
    private User user;
    private Workshop workshop;
    private int seatsReserved;
    private String notes;
    private String dateReservation;
    private String uniqueCode;

    public Reservation() {
    }

    public Reservation(User user, Workshop workshop, int seatsReserved, String notes, String dateReservation, String uniqueCode) {
        this.user = user;
        this.workshop = workshop;
        this.seatsReserved = seatsReserved;
        this.notes = notes;
        this.dateReservation = dateReservation;
        this.uniqueCode = uniqueCode;
    }

    public Reservation(int id, User user, Workshop workshop, int seatsReserved, String notes, String dateReservation, String uniqueCode) {
        this.id = id;
        this.user = user;
        this.workshop = workshop;
        this.seatsReserved = seatsReserved;
        this.notes = notes;
        this.dateReservation = dateReservation;
        this.uniqueCode = uniqueCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public int getSeatsReserved() {
        return seatsReserved;
    }

    public void setSeatsReserved(int seatsReserved) {
        this.seatsReserved = seatsReserved;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(String dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    @Override
    public String toString() {
        return "Reservation {\n" +
                "  id=" + id + ",\n" +
                "  seatsReserved=" + seatsReserved + ",\n" +
                "  notes='" + notes + "',\n" +
                "  dateReservation=" + dateReservation + ",\n" +
                "  uniqueCode='" + uniqueCode + "'\n" +
                '}';
    }
}
