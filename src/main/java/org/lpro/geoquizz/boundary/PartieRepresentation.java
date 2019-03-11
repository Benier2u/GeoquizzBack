package org.lpro.geoquizz.boundary;

import org.lpro.geoquizz.Exception.NotFound;
import org.lpro.geoquizz.entity.Partie;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/parties", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Partie.class)
public class PartieRepresentation {

    private final PartieResource pr;

    public PartieRepresentation(PartieResource pr) {
        this.pr = pr;
    }

    @GetMapping
    public ResponseEntity<?> getAllParties() {
        return new ResponseEntity<>(pr.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> postPartie(@RequestBody Partie partie) {
        partie.setId(UUID.randomUUID().toString());
        Partie saved = pr.save(partie);
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/{ID}")
    public ResponseEntity<?> getPartieById(@PathVariable("ID") String id) {
        return Optional.ofNullable(pr.findById(id))
                .filter(Optional::isPresent)
                .map(partie -> new ResponseEntity<>(partie.get(),HttpStatus.OK))
                .orElseThrow( () -> new NotFound("Partie inexistante"));
    }

    @GetMapping("/{ID}/photos")
    public ResponseEntity<?> getPhotosByPartieId(@PathVariable("ID") String id) {
        return Optional.ofNullable(pr.findById(id))
                .filter(Optional::isPresent)
                .map(partie -> new ResponseEntity<>(partie.get(),HttpStatus.OK))
                .orElseThrow( () -> new NotFound("Partie inexistante"));
    }
}