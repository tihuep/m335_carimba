package ch.timonhueppi.m335.carimba.model;

public class Mod {
    String modId;
    String carId;
    ModCategory category;
    String title;
    String details;
    String photo;

    public Mod(String modId, String carId, ModCategory category, String title, String details, String photo) {
        this.modId = modId;
        this.carId = carId;
        this.category = category;
        this.title = title;
        this.details = details;
        this.photo = photo;
    }

    public Mod(String carId, ModCategory category, String title, String details, String photo) {
        this.carId = carId;
        this.category = category;
        this.title = title;
        this.details = details;
        this.photo = photo;
    }

    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public ModCategory getCategory() {
        return category;
    }

    public void setCategory(ModCategory category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
