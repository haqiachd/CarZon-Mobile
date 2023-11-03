package com.c2.arenafinder.data.response;

import com.c2.arenafinder.data.model.FasilitasModel;
import com.c2.arenafinder.data.model.JamOperasionalModel;
import com.c2.arenafinder.data.model.VenueCommentModel;
import com.c2.arenafinder.data.model.VenueContactModel;
import com.c2.arenafinder.data.model.VenueDetailedModel;
import com.c2.arenafinder.data.model.VenuePhotos;
import com.c2.arenafinder.data.model.VenueRatingModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VenueDetailedResponse {

    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("data")
    private Data data;

    public VenueDetailedResponse(String status, String message, Data data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public static class Data {

        @Expose
        @SerializedName("venue_data")
        private VenueDetailedModel venue;

        @Expose
        @SerializedName("operasional")
        private JamOperasionalModel jamOperasional;

        @Expose
        @SerializedName("contact")
        private VenueContactModel contact;

        @Expose
        @SerializedName("fasilitas")
        private FasilitasModel fasilitas;

        @Expose
        @SerializedName("rating")
        private VenueRatingModel rating;

        @Expose
        @SerializedName("comment")
        private VenueCommentModel comment;

        @Expose
        @SerializedName("photos")
        private VenuePhotos photo;

        public Data(VenueDetailedModel venue, JamOperasionalModel jamOperasional, VenueContactModel contact, FasilitasModel fasilitas, VenueRatingModel rating, VenueCommentModel comment, VenuePhotos photo) {
            this.venue = venue;
            this.jamOperasional = jamOperasional;
            this.contact = contact;
            this.fasilitas = fasilitas;
            this.rating = rating;
            this.comment = comment;
            this.photo = photo;
        }

        public VenueDetailedModel getVenue() {
            return venue;
        }

        public void setVenue(VenueDetailedModel venue) {
            this.venue = venue;
        }

        public JamOperasionalModel getJamOperasional() {
            return jamOperasional;
        }

        public void setJamOperasional(JamOperasionalModel jamOperasional) {
            this.jamOperasional = jamOperasional;
        }

        public VenueContactModel getContact() {
            return contact;
        }

        public void setContact(VenueContactModel contact) {
            this.contact = contact;
        }

        public FasilitasModel getFasilitas() {
            return fasilitas;
        }

        public void setFasilitas(FasilitasModel fasilitas) {
            this.fasilitas = fasilitas;
        }

        public VenueRatingModel getRating() {
            return rating;
        }

        public void setRating(VenueRatingModel rating) {
            this.rating = rating;
        }

        public VenueCommentModel getComment() {
            return comment;
        }

        public void setComment(VenueCommentModel comment) {
            this.comment = comment;
        }

        public VenuePhotos getPhoto() {
            return photo;
        }

        public void setPhoto(VenuePhotos photo) {
            this.photo = photo;
        }
    }

}
