package mis.web.rest;

import com.codahale.metrics.annotation.Timed;
import mis.domain.Flat;

import mis.repository.FlatRepository;
import mis.repository.ProfileRepository;
import mis.repository.UserRepository;
import mis.web.rest.errors.BadRequestAlertException;
import mis.web.rest.util.HeaderUtil;
import mis.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Flat.
 */
@RestController
@RequestMapping("/api")
public class FlatResource {
    @Inject
    private UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(FlatResource.class);

    private static final String ENTITY_NAME = "flat";

    private final FlatRepository flatRepository;

    @Inject
    private ProfileRepository profileRepository;

    public FlatResource(FlatRepository flatRepository) {
        this.flatRepository = flatRepository;
    }

    /**
     * POST  /flats : Create a new flat.
     *
     * @param flat the flat to create
     * @return the ResponseEntity with status 201 (Created) and with body the new flat, or with status 400 (Bad Request) if the flat has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/flats")
    @Timed
    public ResponseEntity<Flat> createFlat(@Valid @RequestBody Flat flat) throws URISyntaxException {
        log.debug("REST request to save Flat : {}", flat);
        if (flat.getId() != null) {
            throw new BadRequestAlertException("A new flat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        flat.setCreateBy(userRepository.getCurrentUserId());
        flat.createdate(LocalDate.now());
        flat.setUser(userRepository.getCurrentUser());
        flat.setProfile(profileRepository.getCurrentUserProfile());

        Flat result = flatRepository.save(flat);
        return ResponseEntity.created(new URI("/api/flats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /flats : Updates an existing flat.
     *
     * @param flat the flat to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated flat,
     * or with status 400 (Bad Request) if the flat is not valid,
     * or with status 500 (Internal Server Error) if the flat couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/flats")
    @Timed
    public ResponseEntity<Flat> updateFlat(@Valid @RequestBody Flat flat) throws URISyntaxException {
        log.debug("REST request to update Flat : {}", flat);
        if (flat.getId() == null) {
            return createFlat(flat);
        }
        flat.setUpdateBy(userRepository.getCurrentUserId());
        flat.setUpdateDate(LocalDate.now());
        Flat result = flatRepository.save(flat);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, flat.getId().toString()))
            .body(result);
    }

    /**
     * GET  /flats : get all the flats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of flats in body
     */
    @GetMapping("/flats")
    @Timed
    public ResponseEntity<List<Flat>> getAllFlats(Pageable pageable) {
        log.debug("REST request to get a page of Flats");
        Page<Flat> page = flatRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/flats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /flats/:id : get the "id" flat.
     *
     * @param id the id of the flat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the flat, or with status 404 (Not Found)
     */
    @GetMapping("/flats/{id}")
    @Timed
    public ResponseEntity<Flat> getFlat(@PathVariable Long id) {
        log.debug("REST request to get Flat : {}", id);
        Flat flat = flatRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(flat));
    }

    /**
     * DELETE  /flats/:id : delete the "id" flat.
     *
     * @param id the id of the flat to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/flats/{id}")
    @Timed
    public ResponseEntity<Void> deleteFlat(@PathVariable Long id) {
        log.debug("REST request to delete Flat : {}", id);
        flatRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
