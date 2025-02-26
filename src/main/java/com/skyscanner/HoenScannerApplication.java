package com.skyscanner;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class HoenScannerApplication extends Application<HoenScannerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new HoenScannerApplication().run(args);
    }

    @Override
    public String getName() {
        return "hoen-scanner";
    }

    @Override
    public void initialize(final Bootstrap<HoenScannerConfiguration> bootstrap) {
        // No initialization required
    }

    @Override
    public void run(final HoenScannerConfiguration configuration, final Environment environment) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        List<SearchResult> carResults = Arrays.asList(
                mapper.readValue(
                        getClass().getClassLoader().getResource("rental_cars.json"),
                        SearchResult[].class
                )
        );

        List<SearchResult> hotelResults = Arrays.asList(
                mapper.readValue(
                        getClass().getClassLoader().getResource("hotels.json"),
                        SearchResult[].class
                )
        );

        List<SearchResult> searchResults = new ArrayList<>();
        searchResults.addAll(carResults);
        searchResults.addAll(hotelResults);

        environment.jersey().register(new SearchResource(searchResults));
    }

}


class Search {
    @JsonProperty
    private String city;

    public Search() { }

    public Search(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }
}


class SearchResult {
    @JsonProperty
    private String city;

    @JsonProperty
    private String title;

    @JsonProperty
    private String kind;

    public SearchResult() { }

    public SearchResult(String city, String title, String kind) {
        this.city = city;
        this.title = title;
        this.kind = kind;
    }

    public String getCity() {
        return city;
    }

    public String getTitle() {
        return title;
    }

    public String getKind() {
        return kind;
    }
}


@Path("/search")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class SearchResource {
    private List<SearchResult> searchResults;


    public SearchResource() {
        this.searchResults = new ArrayList<>();
    }

    public SearchResource(List<SearchResult> searchResults) {
        this.searchResults = searchResults != null ? searchResults : new ArrayList<>();
    }

    @POST
    public List<SearchResult> search(@NotNull @Valid Search search) {
        List<SearchResult> response = new ArrayList<>();
        for (SearchResult result : searchResults) {
            if (result.getCity().equalsIgnoreCase(search.getCity())) {
                response.add(result);
            }
        }
        return response;
    }
}
