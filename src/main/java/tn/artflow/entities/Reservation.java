package tn.artflow.entities;


import java.util.Date;

public class Reservation {
    private int id;
    private int userId;
    private int workshopId;
    private int seatsReserved;
    private String notes;
    private Date dateReservation;
    private String uniqueCode;

    public Reservation() {
    }

    public Reservation(int userId, int workshopId, int seatsReserved, String notes, Date dateReservation, String uniqueCode) {
        this.userId = userId;
        this.workshopId = workshopId;
        this.seatsReserved = seatsReserved;
        this.notes = notes;
        this.dateReservation = dateReservation;
        this.uniqueCode = uniqueCode;
    }

    public Reservation(int id, int userId, int workshopId, int seatsReserved, String notes, Date dateReservation, String uniqueCode) {
        this.id = id;
        this.userId = userId;
        this.workshopId = workshopId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWorkshopId() {
        return workshopId;
    }

    public void setWorkshopId(int workshopId) {
        this.workshopId = workshopId;
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

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
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
                "  userId=" + userId + ",\n" +
                "  workshopId=" + workshopId + ",\n" +
                "  seatsReserved=" + seatsReserved + ",\n" +
                "  notes='" + notes + "',\n" +
                "  dateReservation=" + dateReservation + ",\n" +
                "  uniqueCode='" + uniqueCode + "'\n" +
                '}';
    }
}
