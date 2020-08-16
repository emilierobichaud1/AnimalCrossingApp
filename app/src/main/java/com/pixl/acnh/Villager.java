package com.pixl.acnh;

public class Villager {

    private String name;
    private String personality;
    private String species;
    private String birthday;
    private String catchphrase;
    private String pic_link;
    private Boolean my_villager;
    private Boolean favourite;

    public Villager(String name, String personality, String species,
                    String birthday, String catchphrase, String pic_link) {
        this.name = name;
        this.personality = personality;
        this.species = species;
        this.birthday = birthday;
        this.catchphrase = catchphrase;
        this.pic_link = pic_link;
    }

    public Boolean getMy_villager() {
        return my_villager;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public void setMy_villager(Boolean my_villager) {
        this.my_villager = my_villager;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setCatchphrase(String catchphrase) {
        this.catchphrase = catchphrase;
    }

    public void setPic_link(String pic_link) { this.pic_link = pic_link; }

    public String getName() {
        return name;
    }

    public String getPersonality() {
        return personality;
    }

    public String getSpecies() {
        return species;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getCatchphrase() {
        return catchphrase;
    }

    public String getPic_link() { return pic_link; }
}
