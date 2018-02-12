package org.saiku.web.rest.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Octavian Ciubotaru
 */
@Component
@Path("/saiku/validated-countries")
public class ValidatedCountriesResource {

    private JdbcTemplate template;

    @GET
    @Produces({"application/json"})
    public Response getMondrianStats() {

        List<Country> allCountries = getTemplate().query(
                "SELECT bc.ISO2, bc.NAME\n"
                + "FROM BASE_COUNTRY bc\n"
                + "  JOIN CATEGORY c ON bc.ISO2 = c.COUNTRY AND c.DTYPE = 'CountryRegion'\n"
                + "WHERE c.REGION != 'Uncategorized'\n"
                + "ORDER BY bc.NAME", new BeanPropertyRowMapper<Country>(Country.class));

        List<ValidatedDesignation> validatedDesignations = getTemplate().query(
                "SELECT ENTITY_YEAR AS \"YEAR\", DESIGNATION\n"
                + "FROM V_CP_VALIDATED_DESIGNATIONS",
                new BeanPropertyRowMapper<ValidatedDesignation>(ValidatedDesignation.class));

        List<String> countryNames = new ArrayList<String>();
        Map<String, String> nameByIso = new HashMap<String, String>();
        for (Country c : allCountries) {
            countryNames.add(c.name);
            nameByIso.put(c.iso2, c.name);
        }

        Multimap<Integer, String> map = HashMultimap.create();
        for (ValidatedDesignation vd : validatedDesignations) {
            map.put(vd.getYear(), nameByIso.get(vd.getDesignation()));
        }

        ArrayList<Integer> allYears = new ArrayList<Integer>(map.keySet());
        Collections.sort(allYears);
        Collections.reverse(allYears);

        return new Response(countryNames, allYears, map.asMap());
    }

    public static class Country {

        private String iso2;
        private String name;

        public String getIso2() {
            return iso2;
        }

        public void setIso2(String iso2) {
            this.iso2 = iso2;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ValidatedDesignation {

        private Integer year;
        private String designation;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }
    }

    private JdbcTemplate getTemplate() {
        if (template == null) {
            try {
                InitialContext ic = new InitialContext();
                DataSource ds = (DataSource) ic.lookup("mondrianDataSource");
                template = new JdbcTemplate(ds);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
        return template;
    }

    public static class Response {

        private List<String> allCountries;
        private List<Integer> allYears;
        private Map<Integer, Collection<String>> validated;

        public Response(List<String> allCountries, List<Integer> allYears,
                Map<Integer, Collection<String>> validated) {
            this.allCountries = allCountries;
            this.allYears = allYears;
            this.validated = validated;
        }

        public List<String> getAllCountries() {
            return allCountries;
        }

        public List<Integer> getAllYears() {
            return allYears;
        }

        public Map<Integer, Collection<String>> getValidated() {
            return validated;
        }
    }
}
