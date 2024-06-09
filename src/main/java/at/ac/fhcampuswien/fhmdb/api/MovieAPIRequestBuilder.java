package at.ac.fhcampuswien.fhmdb.api;

import at.ac.fhcampuswien.fhmdb.models.Genre;

public class MovieAPIRequestBuilder {
    private String base;
    private String query;
    private Genre genre;
    private String releaseYear;
    private String ratingFrom;

    // initializing base url
    public MovieAPIRequestBuilder(String base) {
        this.base = base;
    }

    // setting methods
    public MovieAPIRequestBuilder query (String query) {
        this.query = query;
        return this;
    }

    public MovieAPIRequestBuilder genre (Genre genre) {
        this.genre = genre;
        return this;
    }

    public MovieAPIRequestBuilder releaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
        return this;
    }

    public MovieAPIRequestBuilder ratingFrom (String ratingFrom) {
        this.ratingFrom = ratingFrom;
        return this;
    }

    // build method to combine set parameters to complete URL
    public String build() {
        StringBuilder url = new StringBuilder(base);

        if (query != null || genre != null || releaseYear != null || ratingFrom != null) {
            url.append("?");
        }

        if (query != null) {
            url.append("query=").append(query).append("&");
        }

        if (genre != null) {
            url.append("genre=").append(genre).append("&");
        }

        if (releaseYear != null) {
            url.append("releaseYear=").append(releaseYear).append("&");
        }

        if (ratingFrom != null) {
            url.append("ratingFrom=").append(ratingFrom).append("&");
        }

        // remove last & and ? if no parameters were set
        if (url.charAt(url.length() - 1) == '&' || url.charAt(url.length() - 1) == '?') {
            url.deleteCharAt(url.length() - 1);
        }

        return url.toString();
    }
}
